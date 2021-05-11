/*
 * (c) Copyright 2021 ~ Trust Nexus, Inc.
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

package io.trustnexus.struts.actions.mobileapp;

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

import io.trustnexus.jdbc.UserSessionTrackingDao;
import io.trustnexus.jdbc.mobileapp.PublicPrivateKeyDao;
import io.trustnexus.jdbc.mobileapp.UserDao;
import io.trustnexus.model.mobileapp.PublicPrivateKey;
import io.trustnexus.model.mobileapp.User;
import io.trustnexus.util.Constants;
import io.trustnexus.util.CryptoUtilities;
import io.trustnexus.util.EmailFactory;
import io.trustnexus.util.PropertyManager;
import io.trustnexus.util.Utilities;
import com.opensymphony.xwork2.ActionSupport;

/*
 * This action is called from the WebAuthn+ mobile app during the create user process.  All communication between 
 * the WebAuthn+ mobile app and the TNX Secure web application is encrypted based on the Transport Layer Security 
 * protocol with no digital certificates being used:
 * https://en.wikipedia.org/wiki/Transport_Layer_Security
 * 
 * Digital certificates are a notoriously weak method for securing keys and establishing identity.  No matter who 
 * you really are, you can always find a certificate "authority" to say you are someone else.
 */
public class CreateUserPublicKeyAction extends ActionSupport implements ServletRequestAware, ServletResponseAware {

  private static final long serialVersionUID = 5408882849806783138L;

  private static Logger logger = LogManager.getLogger(CreateUserPublicKeyAction.class);

  private String publicKeyUuid;
  private String transferKeyEncryptedHex;
  private String transferDataEncryptedHex;
  private String TransferDataEncryptedHashedHex;

  private HttpServletRequest servletRequest;
  private HttpServletResponse servletResponse;
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
    String userUuid = null;

    try {

      /*
       * The HTTP request contains four packages (key/value pairs). The first package is the publicKeyUuid.  This UUID 
       * is used to lookup the MOBILE_APP_PROVIDER private key.
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
       * Use the mobile app provider's private key to decrypt the transfer key.
       */
      PrivateKey privateKey = KeyFactory.getInstance(CryptoUtilities.KEY_FACTORY_ALGORITHM).generatePrivate(new PKCS8EncodedKeySpec(CryptoUtilities.hexStringToByteArray(privateKeyHex)));

      Cipher rsaCipher = Cipher.getInstance(CryptoUtilities.RSA_CIPHER_ALGORITHM);
      rsaCipher.init(Cipher.DECRYPT_MODE, privateKey);

      byte[] transferKeyDecryptedBytes = rsaCipher.doFinal(CryptoUtilities.hexStringToByteArray(transferKeyEncryptedHex));
      Key transferKey = new SecretKeySpec(transferKeyDecryptedBytes, CryptoUtilities.SECRET_KEY_ALGORITHM);;
      
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

      String TransferDataEncryptedHashedHexTest = CryptoUtilities.toHex(transferDataEncryptedHashedBytesTest);
      logger.debug("TransferDataEncryptedHashedHexTest: " + TransferDataEncryptedHashedHexTest);

      /*
       * The fourth HTTP request package contains the TransferDataEncryptedHashedHex.
       */
      logger.debug("TransferDataEncryptedHashedHex: " + TransferDataEncryptedHashedHex);

      /*
       * Test the hashed values to determine the integrity of the message. If the hashed values are equal continue with 
       * the process; else return a message:  PropertyManager.getInstance().getProperty(Constants.MESSAGE_INTEGRITY_COMPROMISED).
       */
      if (TransferDataEncryptedHashedHex.equals(TransferDataEncryptedHashedHexTest)) {

        Cipher cipher = Cipher.getInstance(CryptoUtilities.CIPHER_ALGORITHM);

        /*
         * The first sixteen bytes, thirty-two HEX characters, of the encrypted data represents the initialization 
         * vector.
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

        // ------------------------------------------------------------------------------------------------------------

        userUuid = Utilities.parseNameValuePairs(transferDataDecrypted, Constants.USER_UUID);
        logger.debug("userUuid: " + userUuid);
        
        UserSessionTrackingDao.updateUserIdentifier(userUuid, userSessionTrackingId);

        User existingUser = UserDao.retrieveByUserUuid(userUuid);

        /*
         * If the user exists AND does not have a public key, create the public key; else, return a message: 
         * PropertyManager.getInstance().getProperty(Constants.USER_DOES_NOT_EXIST).
         * 
         * Checking for an existing public key means that this action can only be called once right after the user is 
         * created.
         */
        if (existingUser != null) {
        	
        	if (existingUser.getPublicKey() == null || existingUser.getPublicKey().length() < 16) {

            String publicKeyHex = Utilities.parseNameValuePairs(transferDataDecrypted, Constants.PUBLIC_KEY_HEX);
            logger.debug("publicKey: " + publicKeyHex);

            existingUser.setPublicKey(publicKeyHex);

            UserDao.update(existingUser);

            responseString = PropertyManager.getInstance().getProperty(Constants.PUBLIC_KEY_SAVED);

            // --------------------------------------------------------------------------------------------------------

            EmailFactory.sendActivationEmail(existingUser); 
						
					} else {
	          responseString = PropertyManager.getInstance().getProperty(Constants.PUBLIC_KEY_ALREADY_CREATED);						
					}

        } else {
          responseString = PropertyManager.getInstance().getProperty(Constants.USER_DOES_NOT_EXIST);
        }
      } else {
        responseString = PropertyManager.getInstance().getProperty(Constants.MESSAGE_INTEGRITY_COMPROMISED);
      }

    } catch (Exception e) {
    	UserDao.deleteByUserUuid(userUuid); 
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
    return TransferDataEncryptedHashedHex;
  }

  public void setTransferDataEncryptedHashedHex(String TransferDataEncryptedHashedHex) {
    this.TransferDataEncryptedHashedHex = TransferDataEncryptedHashedHex;
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







