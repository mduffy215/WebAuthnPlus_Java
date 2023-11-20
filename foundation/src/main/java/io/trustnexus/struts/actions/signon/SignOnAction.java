/*
 * (c) Copyright 2023 ~ Trust Nexus, Inc.
 * All technologies described here in are "Patent Pending". 
 * License information:  http://www.trustnexus.io/license.htm
 * 
 * AS LONG AS THIS NOTICE IS MAINTAINED THE LICENSE PERMITS REDISTRIBUTION OR RE-POSTING  
 * OF THIS SOURCE CODE TO A PUBLIC REPOSITORY (WITH OR WITHOUT MODIFICATIONS)! 
 * 
 * Report License Violations:  trustnexus.io@austin.rr.com 
 * 
 * This is a beta version:  0.0.1
 * Suggestions for code improvements:  trustnexus.io@austin.rr.com
 */

package io.trustnexus.struts.actions.signon;

import io.trustnexus.jdbc.AuthenticationDao; 
import io.trustnexus.jdbc.CredentialDao;
import io.trustnexus.jdbc.UserSessionTrackingDao;
import io.trustnexus.jdbc.mobileapp.PublicPrivateKeyDao;
import io.trustnexus.model.Authentication;
import io.trustnexus.model.Credential;
import io.trustnexus.model.mobileapp.PublicPrivateKey;
import io.trustnexus.util.Constants;
import io.trustnexus.util.CryptoUtilities;
import io.trustnexus.util.PropertyManager;
import io.trustnexus.util.Utilities;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;

import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;

import com.opensymphony.xwork2.ActionSupport;

/*
 * This action is called from the WebAuthn+ mobile app when the user touches the Sign On button.  All 
 * communication between the WebAuthn+ mobile app and the TNX Web Application is encrypted based on the Transport 
 * Layer Security protocol with no digital certificates being used:
 * https://en.wikipedia.org/wiki/Transport_Layer_Security
 * 
 * Digital certificates are a notoriously weak method for securing keys and establishing identity.  No matter who 
 * you really are, you can always find a certificate "authority" to say you are someone else.
 * 
 * Order of precedence within io.trustnexus.struts.actions.signon:  44
 */
public class SignOnAction extends ActionSupport implements ServletRequestAware, ServletResponseAware {

  private static final long serialVersionUID = -3642304116268465794L;

  private static Logger logger = LogManager.getLogger(SignOnAction.class);

  private HttpServletRequest servletRequest;
  private HttpServletResponse servletResponse;

  private String publicKeyUuid;
  private String transferKeyEncryptedHex;
  private String transferDataEncryptedHex;
  private String transferDataEncryptedHashedHex;
  private InputStream inputStream;
  
  public String execute() {
  	
  	HttpSession session = servletRequest.getSession();
  	String sessionIdentifier = session.getId();
  	ThreadContext.put("sessionId", "sessionId::" + sessionIdentifier + "::");

    logger.info("###Entering");
  	
  	int userSessionTrackingId = UserSessionTrackingDao.createForMdc(sessionIdentifier); 

    // ----------------------------------------------------------------------------------------------------------------

    String responseString = "";
    String transferKeyDecryptedHex = null;

    try {
      /*
       * The HTTP request contains four packages (key/value pairs). The first package is the publicKeyUuid. This UUID 
       * is used to lookup the CREDENTIAL_PROVIDER private key.
       */
      logger.debug("publicKeyUuid: " + publicKeyUuid);

      PublicPrivateKey publicPrivateKey = PublicPrivateKeyDao.retrieveByUuid(publicKeyUuid);
      String privateKeyHex = publicPrivateKey.getPrivateKeyHex();

      /*
       * The second HTTP request package contains the transferKeyEncryptedHex which was encrypted with the mobile app 
       * provider publicKey.
       */
      logger.debug("transferKeyEncryptedHex: " + transferKeyEncryptedHex);

      /*
       * Use the credential provider private key to decrypt the transfer key.
       */
      PrivateKey privateKey = KeyFactory.getInstance(CryptoUtilities.KEY_FACTORY_ALGORITHM).generatePrivate(new PKCS8EncodedKeySpec(CryptoUtilities.hexStringToByteArray(privateKeyHex)));

      Cipher rsaCipher = Cipher.getInstance(CryptoUtilities.RSA_CIPHER_ALGORITHM);
      rsaCipher.init(Cipher.DECRYPT_MODE, privateKey);

      byte[] transferKeyDecryptedBytes = rsaCipher.doFinal(CryptoUtilities.hexStringToByteArray(transferKeyEncryptedHex));
      Key transferKey = new SecretKeySpec(transferKeyDecryptedBytes, CryptoUtilities.SECRET_KEY_ALGORITHM);
      
      transferKeyDecryptedHex = CryptoUtilities.toHex(transferKeyDecryptedBytes);
      logger.debug("transferKeyDecryptedHex: " + transferKeyDecryptedHex);

      // --------------------------------------------------------------------------------------------------------------

      /*
       * The third HTTP request package contains the transfer data encrypted with the transferKey.
       */
      logger.debug("transferDataEncryptedHex: " + transferDataEncryptedHex);

      // --------------------------------------------------------------------------------------------------------------

      /*
       * Hash the transferDataEncryptedHex using the transferKey.
       */
      Mac macTransferData = Mac.getInstance(CryptoUtilities.MAC_ALGORITHM);
      macTransferData.init(transferKey);
      byte[] transferDataEncryptedHashedBytesTest = macTransferData.doFinal(transferDataEncryptedHex.getBytes());

      String transferDataEncryptedHashedHexTest = CryptoUtilities.toHex(transferDataEncryptedHashedBytesTest);
      logger.debug("transferDataEncryptedHashedHexTest: " + transferDataEncryptedHashedHexTest);

      /*
       * The fourth HTTP request package contains the transferDataEncryptedHashedHex.
       */
      logger.debug("transferDataEncryptedHashedHex: " + transferDataEncryptedHashedHex);

      /*
       * Test the hashed values to determine the integrity of the message. If the hashed values are equal continue with 
       * the process; else return a message: PropertyManager.getInstance().getProperty(Constants.MESSAGE_INTEGRITY_COMPROMISED).
       */
      if (transferDataEncryptedHashedHex.equals(transferDataEncryptedHashedHexTest)) {

        Cipher cipher = Cipher.getInstance(CryptoUtilities.CIPHER_ALGORITHM);

        /*
         * The first sixteen bytes, thirty-two HEX characters, of the encrypted
         * data represents the initialization vector.
         */
        cipher.init(Cipher.DECRYPT_MODE, transferKey, new IvParameterSpec(CryptoUtilities.hexStringToByteArray(transferDataEncryptedHex.substring(0, 32))));

        byte[] transferDataEncryptedBytes = CryptoUtilities.hexStringToByteArray(transferDataEncryptedHex.substring(32));
        byte[] transferDataDecryptedBytes = new byte[cipher.getOutputSize(transferDataEncryptedBytes.length)];

        int transferDataDecryptionPointer = cipher.update(transferDataEncryptedBytes, 0, transferDataEncryptedBytes.length, transferDataDecryptedBytes, 0);
        transferDataDecryptionPointer += cipher.doFinal(transferDataDecryptedBytes, transferDataDecryptionPointer);

        /*
         * Convert the decrypted byte array into a human readable string.
         */
        String transferDataDecrypted = new String(transferDataDecryptedBytes);
        logger.debug("transferDataDecrypted: " + transferDataDecrypted);

        // --------------------------------------------------------------------------------------------------------------

        String userUuid = Utilities.parseNameValuePairs(transferDataDecrypted, Constants.USER_UUID);
        logger.debug("userUuid: " + userUuid);
        
        UserSessionTrackingDao.updateUserIdentifier(userUuid, userSessionTrackingId); 

        String credentialUuid = Utilities.parseNameValuePairs(transferDataDecrypted, Constants.CREDENTIAL_UUID);
        logger.debug("credentialUuid: " + credentialUuid);
        
        String sessionUuid = Utilities.parseNameValuePairs(transferDataDecrypted, Constants.SESSION_UUID);
        logger.debug("sessionUuid: " + sessionUuid);

        String verificationCode = Utilities.parseNameValuePairs(transferDataDecrypted, Constants.VERIFICATION_CODE);
        logger.debug("verificationCode: " + verificationCode);

        String transactionUuid = Utilities.parseNameValuePairs(transferDataDecrypted, Constants.TRANSACTION_UUID);
        logger.debug("transactionUuid: " + transactionUuid);

        String transactionUuidSigned = Utilities.parseNameValuePairs(transferDataDecrypted, Constants.TRANSACTION_UUID_SIGNED);
        logger.debug("transactionUuidSigned: " + transactionUuidSigned);

        Authentication authenticationCheck = AuthenticationDao.retrieveBySessionUuid(sessionUuid);

        if (authenticationCheck == null || !authenticationCheck.isSessionValid()) {

          responseString = PropertyManager.getInstance().getProperty(Constants.PLEASE_REFRESH);

        } else if (authenticationCheck.getVerificationCode() == null) {

        	/*
        	 * Check to be sure this user has a valid credential of this type with this credential provider.
        	 */
          Credential credential = CredentialDao.retrieveByCredentialUuid(credentialUuid);

          if (credential != null) {

            logger.debug("credential: " + credential.toString());

            String verifyTransactionUuidMessage = CryptoUtilities.verifyTransactionUuid(userUuid, transactionUuid);
            logger.debug("verifyTransactionUuidMessage: " + verifyTransactionUuidMessage);

            if (verifyTransactionUuidMessage.equals(PropertyManager.getInstance().getProperty(Constants.TRANSACTION_UUID_VERIFIED))) {

              String verifyTransactionUuidSignatureMessage = CryptoUtilities.verifySignatureByUserUuid(userUuid, transactionUuid, transactionUuidSigned);
              logger.debug("verifyTransactionUuidSignatureMessage: " + verifyTransactionUuidSignatureMessage);

              if (verifyTransactionUuidSignatureMessage.trim().equals(PropertyManager.getInstance().getProperty(Constants.SIGNATURE_VERIFIED))) {
                
                /*
                 * This is the key step in the authentication process.  The sessionUuid was originally sent to the 
                 * TNX WebAuthn+ mobile app through an encrypted Firebase channel.  It was signed by the user's private
                 * key which is stored securely on his/her smart phone.
                 */
                String sessionUuidSigned = Utilities.parseNameValuePairs(transferDataDecrypted, Constants.SESSION_UUID_SIGNED);
                logger.debug("sessionUuidSigned: " + sessionUuidSigned);

                String verifySessionUuidSignatureMessage = CryptoUtilities.verifySignatureByUserUuid(userUuid, sessionUuid, sessionUuidSigned);
                logger.debug("verifyTransactionUuidSignatureMessage: " + verifySessionUuidSignatureMessage);

                if (verifySessionUuidSignatureMessage.trim().equals(PropertyManager.getInstance().getProperty(Constants.SIGNATURE_VERIFIED))) {

                  authenticationCheck.setVerificationCode(verificationCode);
                  
                  /*
                   * The signOnUuid is only generated if the sessionUuid signature is verified.
                   */
                  authenticationCheck.setSignOnUuid(CryptoUtilities.generateUuid());
                  authenticationCheck.setScreenName(credential.getScreenName());
                  authenticationCheck.setEmail(credential.getEmail());
                  AuthenticationDao.updateForSignOn(authenticationCheck);

                  responseString = PropertyManager.getInstance().getProperty(Constants.SIGN_ON_SUCCESSFUL);
                  
                } else {
                  responseString = verifySessionUuidSignatureMessage;
                }

              } else {
                responseString = verifyTransactionUuidSignatureMessage;
              }

            } else {
              responseString = verifyTransactionUuidMessage;
            }

          } else {

            /*
             * If someone tried to fake a credential we do not want to tell them specifically that their credential failed.
             */
            responseString = PropertyManager.getInstance().getProperty(Constants.PROBLEM_WITH_AUTHENTICATION_SERVER);
          }

        } else {

          /*
           * This condition is only met when the verification code is valid and the session timeout is null.
           */
          responseString = PropertyManager.getInstance().getProperty(Constants.ALREADY_SIGNED_ON); 
        }

      } else {
        responseString = PropertyManager.getInstance().getProperty(Constants.MESSAGE_INTEGRITY_COMPROMISED);
      }

    } catch (Exception e) {
      responseString = PropertyManager.getInstance().getProperty(Constants.PROBLEM_WITH_AUTHENTICATION_SERVER);
      logger.error("", e);
    }

    logger.debug("responseString: " + responseString);

    // ----------------------------------------------------------------------------------------------------------------

    try {
    	String encryptedResponseString = CryptoUtilities.generatedEncryptedResponse(transferKeyDecryptedHex, responseString);    	
      inputStream = new ByteArrayInputStream(encryptedResponseString.getBytes("UTF-8")); 
    } catch (UnsupportedEncodingException e) {
      logger.error("", e);
    }

    return (SUCCESS);
  }

  // ------------------------------------------------------------------------------------------------------------------

  public String getPublicKeyUuid() {
    return publicKeyUuid;
  }

  public void setPublicKeyUuid(String publicKeyUuid) {
    this.publicKeyUuid = publicKeyUuid;
  }

  // ------------------------------------------------------------------------------------------------------------------

  public String getTransferKeyEncryptedHex() {
    return transferKeyEncryptedHex;
  }

  public void setTransferKeyEncryptedHex(String transferKeyEncryptedHex) {
    this.transferKeyEncryptedHex = transferKeyEncryptedHex;
  }

  // ------------------------------------------------------------------------------------------------------------------

  public String getTransferDataEncryptedHex() {
    return transferDataEncryptedHex;
  }

  public void setTransferDataEncryptedHex(String transferDataEncryptedHex) {
    this.transferDataEncryptedHex = transferDataEncryptedHex;
  }

  // ------------------------------------------------------------------------------------------------------------------

  public String getTransferDataEncryptedHashedHex() {
    return transferDataEncryptedHashedHex;
  }

  public void setTransferDataEncryptedHashedHex(String transferDataEncryptedHashedHex) {
    this.transferDataEncryptedHashedHex = transferDataEncryptedHashedHex;
  }

  // ------------------------------------------------------------------------------------------------------------------

	public HttpServletRequest getServletRequest() {
    return servletRequest;
  }

  public void setServletRequest(HttpServletRequest servletRequest) {
    this.servletRequest = servletRequest;
  }

  // ------------------------------------------------------------------------------------------------------------------

  public HttpServletResponse getServletResponse() {
    return servletResponse;
  }

  public void setServletResponse(HttpServletResponse servletResponse) {
    this.servletResponse = servletResponse;
  }

  // ------------------------------------------------------------------------------------------------------------------

  public InputStream getInputStream() {
    return inputStream;
  }

  public void setInputStream(InputStream inputStream) {
    this.inputStream = inputStream;
  }
}











