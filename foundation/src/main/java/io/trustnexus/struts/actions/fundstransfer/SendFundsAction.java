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

package io.trustnexus.struts.actions.fundstransfer;

import io.trustnexus.jdbc.UserSessionTrackingDao;
import io.trustnexus.jdbc.fundstransfer.SendFundsDao;
import io.trustnexus.jdbc.CredentialDao;
import io.trustnexus.jdbc.mobileapp.PublicPrivateKeyDao;
import io.trustnexus.model.Credential;
import io.trustnexus.model.fundstransfer.FundsTransfer;
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

public class SendFundsAction extends ActionSupport implements ServletRequestAware, ServletResponseAware {

  private static final long serialVersionUID = -3489163381348414378L;

  private static Logger logger = LogManager.getLogger(SendFundsAction.class);

  private String publicKeyUuid;
  private String transferKeyEncryptedHex;
  private String transferDataEncryptedHex;
  private String transferDataEncryptedHashedHex;

  private HttpServletRequest servletRequest;
  private HttpServletResponse servletResponse;
  private InputStream inputStream;
  
  /*
   * This action is called from the WebAuthn+ mobile app when the user touches the "Send" option for 
   * funds transfer.  
   * 
   * All communication between the WebAuthn+ mobile app and the TNX Secure web application is encrypted 
   * based on the Transport Layer Security protocol with no digital certificates being used:
   * https://en.wikipedia.org/wiki/Transport_Layer_Security
   * 
   * Digital certificates are a notoriously weak method for securing keys and establishing identity.  
   * No matter who you really are, you can always find a certificate "authority" to say you are someone else.
   * 
   * Order of precedence within io.trustnexus.struts.actions.fundstransfer:  11
   */
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
       * The HTTP request contains four packages (key/value pairs). The first package is the publicKeyUuid. 
       * This UUID is used to lookup the CREDENTIAL_PROVIDER private key.
       */
      logger.debug("publicKeyUuid: " + publicKeyUuid);

      PublicPrivateKey publicPrivateKey = PublicPrivateKeyDao.retrieveByUuid(publicKeyUuid);
      String privateKeyHex = publicPrivateKey.getPrivateKeyHex();

      /*
       * The second HTTP request package contains the transferKeyEncryptedHex which was encrypted with the 
       * publicKey.
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
       * Test the hashed values to determine the integrity of the message. 
       * If the hashed values are equal continue with the process; else return a message:  
       * PropertyManager.getInstance().getProperty(Constants.MESSAGE_INTEGRITY_COMPROMISED).
       */
      if (transferDataEncryptedHashedHex.equals(transferDataEncryptedHashedHexTest)) {

        Cipher cipher = Cipher.getInstance(CryptoUtilities.CIPHER_ALGORITHM);

        /*
         * The first sixteen bytes, thirty-two HEX characters, of the encrypted data represents the 
         * initialization vector.
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

        String credentialType = Utilities.parseNameValuePairs(transferDataDecrypted, Constants.CREDENTIAL_TYPE);
        logger.debug("credentialType: " + credentialType);

        String userUuid = Utilities.parseNameValuePairs(transferDataDecrypted, Constants.USER_UUID);
        logger.debug("userUuid: " + userUuid);
        
        UserSessionTrackingDao.updateUserIdentifier(userUuid, userSessionTrackingId); 

        Credential credential = CredentialDao.retrieveByCredentialTypeAndUserUuid(credentialType, userUuid);

        if (credential != null) {

          String transactionUuid = Utilities.parseNameValuePairs(transferDataDecrypted, Constants.TRANSACTION_UUID);
          logger.debug("transactionUuid: " + transactionUuid);

          String verifyTransactionUuidMessage = CryptoUtilities.verifyTransactionUuid(userUuid, transactionUuid);
          logger.debug("verifyTransactionUuidMessage: " + verifyTransactionUuidMessage);

          if (verifyTransactionUuidMessage.equals(PropertyManager.getInstance().getProperty(Constants.TRANSACTION_UUID_VERIFIED))) {

            String transactionUuidSigned = Utilities.parseNameValuePairs(transferDataDecrypted, Constants.TRANSACTION_UUID_SIGNED);
            logger.debug("transactionUuidSigned: " + transactionUuidSigned);

            String verifySignatureMessage = CryptoUtilities.verifySignatureByUserUuid(userUuid, transactionUuid, transactionUuidSigned);
            logger.debug("verifySignatureMessage: " + verifySignatureMessage);

            if (verifySignatureMessage.trim().equals(PropertyManager.getInstance().getProperty(Constants.SIGNATURE_VERIFIED))) {

              String jsonFundsTransfer = Utilities.parseNameValuePairs(transferDataDecrypted, Constants.JSON_FUNDS_TRANSFER);
              logger.debug("jsonFundsTransfer: " + jsonFundsTransfer);
              
              String senderData = jsonFundsTransfer.substring(0, jsonFundsTransfer.indexOf("\n\"" + Constants.SENDER_HASH));
              logger.debug("senderData: " + senderData);

              String senderHash = Utilities.parseJsonNameValuePairs(jsonFundsTransfer, Constants.SENDER_HASH);
              logger.debug("senderHash: " + senderHash);
              
              String calculatedSenderHash = CryptoUtilities.digest(senderData);
              logger.debug("calculatedSenderHash: " + calculatedSenderHash);
              
              if (senderHash.equals(calculatedSenderHash)) {
              	
              	String senderCredential = jsonFundsTransfer.substring(jsonFundsTransfer.indexOf("\"" + Constants.CREDENTIAL), jsonFundsTransfer.indexOf("\n\n\"" + Constants.RECIPIENT_DATA));
                logger.debug("senderCredential: " + senderCredential);
                
                String storedCredential = credential.getJson();
                logger.debug("storedCredential: " + storedCredential);
                
                if (storedCredential.equals(senderCredential)) {

                  String publicKeyHex = Utilities.parseJsonNameValuePairs(senderCredential, Constants.PUBLIC_KEY_HEX);
                  logger.debug("publicKeyHex: " + publicKeyHex);

                  String senderSignedHash = Utilities.parseJsonNameValuePairs(jsonFundsTransfer, Constants.SENDER_SIGNED_HASH);
                  logger.debug("senderSignedHash: " + senderSignedHash);
                	
                  /*
                   * Usually the signature is verified based on the user in the data structure.  However, as an 
                   * additional security check, in this case we will verify the signature based on the public key 
                   * in the user's credential that will appear in the distributed ledger.
                   */
                  String verifyDistributedLedgerSignatureMessage = CryptoUtilities.verifySignature(publicKeyHex, senderHash, senderSignedHash);
                  logger.debug("verifySignatureMessage: " + verifySignatureMessage);

                  if (verifyDistributedLedgerSignatureMessage.trim().equals(PropertyManager.getInstance().getProperty(Constants.SIGNATURE_VERIFIED))) {

                    String fundsTransferUuid = Utilities.parseJsonNameValuePairs(jsonFundsTransfer, Constants.FUNDS_TRANSFER_UUID);
                    logger.debug("fundsTransferUuid: " + fundsTransferUuid);

                    String recipientName = Utilities.parseJsonNameValuePairs(jsonFundsTransfer, Constants.RECIPIENT_NAME);
                    logger.debug("recipientName: " + recipientName);

                    String recipientEmail = Utilities.parseJsonNameValuePairs(jsonFundsTransfer, Constants.RECIPIENT_EMAIL);
                    logger.debug("recipientEmail: " + recipientEmail);

                    String recipientPhoneNumber = Utilities.parseJsonNameValuePairs(jsonFundsTransfer, Constants.RECIPIENT_PHONE_NUMBER);
                    logger.debug("recipientPhoneNumber: " + recipientPhoneNumber);

                    String transferAmount = Utilities.parseJsonNameValuePairs(jsonFundsTransfer, Constants.TRANSFER_AMOUNT);
                    logger.debug("transferAmount: " + transferAmount);
                    
                    // ------------------------------------------------------------------------------------------------
                		
                		jsonFundsTransfer = jsonFundsTransfer.substring(0, jsonFundsTransfer.lastIndexOf(",")); 
                  	
                  	jsonFundsTransfer += "\n\n\"" + Constants.TIMESTAMP + "\":\"" + Utilities.generateIsoTimestamp(System.currentTimeMillis()) + "\",";
                  	jsonFundsTransfer += "\n\"" + Constants.TRANSACTION_UUID + "\":\"" + transactionUuid + "\",";  
                		jsonFundsTransfer += "\n\n\"" + Constants.SENDING_BANK_NAME + "\":\"" + credential.getCredentialType().getCredentialProvider().getCredentialProviderName() + "\",";
                		jsonFundsTransfer += "\n\"" + Constants.SENDING_BANK_UUID + "\":\"" + credential.getCredentialType().getCredentialProvider().getCredentialProviderUuid() + "\",";
                 		jsonFundsTransfer += "\n\"" + Constants.SENDING_BANK_SECURE_HASH_ALGORITHM + "\":\"" + CryptoUtilities.SIGNATURE_ALGORITHM + "\",";
                		jsonFundsTransfer += "\n\"" + Constants.SENDING_BANK_SIGNATURE_ALGORITHM + "\":\"" + CryptoUtilities.SECURE_HASH_ALGORITHM + "\",";
                  	
                  	String sendingBankHash = CryptoUtilities.digest(jsonFundsTransfer);          			
                		logger.debug("sendingBankHash: " + sendingBankHash);
                  	
                    String sendingBankSignedHash = CryptoUtilities.generateSignedHex(sendingBankHash, privateKey);          			
                		logger.debug("sendingBankSignedHash: " + sendingBankSignedHash);

                		jsonFundsTransfer += "\n\"" + Constants.SENDING_BANK_HASH + "\":\"" + sendingBankHash + "\",";
                		jsonFundsTransfer += "\n\"" + Constants.SENDING_BANK_SIGNED_HASH + "\":\"" + sendingBankSignedHash + "\",\n}]";
                    
                    // ------------------------------------------------------------------------------------------------                    
                    
                    /*
                     * At this point in a fully functioning system a check would be made to the user's account to see if 
                     * funds were available.
                     */
                    
                    // ------------------------------------------------------------------------------------------------
                    
                    FundsTransfer fundsTransfer = new FundsTransfer();

                    fundsTransfer.setUpdatedById(Constants.MASTER_USER);
                    fundsTransfer.setDataSourceTypeValue(Constants.DATA_SOURCE_APPLICATION);
                    
                    fundsTransfer.setFundsTransferUuid(fundsTransferUuid);
                    fundsTransfer.setUserUuid(userUuid);
                    fundsTransfer.setCredentialUuid(credential.getCredentialUuid()); 

                    fundsTransfer.setRecipientName(recipientName);
                    fundsTransfer.setRecipientEmail(recipientEmail);
                    fundsTransfer.setRecipientPhoneNumber(recipientPhoneNumber);
       
                    fundsTransfer.setTransferAmount(Utilities.parseCurrency(transferAmount));
                    fundsTransfer.setJson(jsonFundsTransfer);
                    
                    SendFundsDao.create(fundsTransfer); 
                    
                    FundsTransfer fundsTransferTest = SendFundsDao.retrieveByFundsTransferUuid(fundsTransferUuid); 
                    logger.debug(fundsTransferTest.toString());
                  	
                    responseString = PropertyManager.getInstance().getProperty(Constants.FUNDS_TRANSFER_INITIALIZED);
                  	
                  } else {
                    responseString = verifyDistributedLedgerSignatureMessage;
                  }									
								} else {
									responseString =  PropertyManager.getInstance().getProperty(Constants.INVALID_CREDENTIAL);
								}              	
                
							} else {
								responseString = PropertyManager.getInstance().getProperty(Constants.DISTRIBUTED_LEDGER_COMPROMISED);										
							}

            } else {
              responseString = verifySignatureMessage;
            }

          } else {
            responseString = verifyTransactionUuidMessage;
          }

        } else {
          responseString = PropertyManager.getInstance().getProperty(Constants.CREDENTIAL_DOES_NOT_EXIST);
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







