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
import io.trustnexus.jdbc.mobileapp.CredentialProviderDao;
import io.trustnexus.jdbc.mobileapp.PublicPrivateKeyDao;
import io.trustnexus.jdbc.mobileapp.UserDao;
import io.trustnexus.model.Credential;
import io.trustnexus.model.fundstransfer.FundsTransfer;
import io.trustnexus.model.mobileapp.CredentialProvider;
import io.trustnexus.model.mobileapp.PublicPrivateKey;
import io.trustnexus.model.mobileapp.User;
import io.trustnexus.util.Constants;
import io.trustnexus.util.CryptoUtilities;
import io.trustnexus.util.EmailFactory;
import io.trustnexus.util.Firebase;
import io.trustnexus.util.PropertyManager;
import io.trustnexus.util.Utilities;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.HashMap;

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
 * This action is called from the WebAuthn+ mobile app when the user touches the "Accept" button for 
 * funds transfer.  
 * 
 * All communication between the WebAuthn+ mobile app and the TNX Secure web application is encrypted 
 * based on the Transport Layer Security protocol with no digital certificates being used:
 * https://en.wikipedia.org/wiki/Transport_Layer_Security
 * 
 * Digital certificates are a notoriously weak method for securing keys and establishing identity.  
 * No matter who you really are, you can always find a certificate "authority" to say you are someone else.
 * 
 * Order of precedence within io.trustnexus.struts.actions.fundstransfer:  44
 */
public class AcceptFundsAction extends ActionSupport implements ServletRequestAware, ServletResponseAware {

  private static final long serialVersionUID = -3489163381348414378L;

  private static Logger logger = LogManager.getLogger(AcceptFundsAction.class);

  private String publicKeyUuid;
  private String transferKeyEncryptedHex;
  private String transferDataEncryptedHex;
  private String transferDataEncryptedHashedHex;

  private HttpServletRequest servletRequest;
  private HttpServletResponse servletResponse;
  private InputStream inputStream;

  public String execute() {

    logger.info("###Entering");
  	
  	HttpSession session = servletRequest.getSession();
  	String sessionIdentifier = session.getId();
  	ThreadContext.put("sessionId", "sessionId::" + sessionIdentifier + "::");
  	
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
              
              String recipientData = jsonFundsTransfer.substring(0, jsonFundsTransfer.indexOf("\n\"" + Constants.RECIPIENT_HASH));
              logger.debug("recipientData: " + recipientData);

              String recipientHash = Utilities.parseJsonNameValuePairs(jsonFundsTransfer, Constants.RECIPIENT_HASH);
              logger.debug("recipientHash: " + recipientHash);
              
              String calculatedRecipientHash = CryptoUtilities.digest(recipientData);
              logger.debug("calculatedRecipientHash: " + calculatedRecipientHash);
              
              if (recipientHash.equals(calculatedRecipientHash)) {
              	
              	int begIndex = jsonFundsTransfer.lastIndexOf(Constants.TRANSACTION_UUID);
              	begIndex = jsonFundsTransfer.indexOf("\"" + Constants.CREDENTIAL, begIndex);
              	
              	String recipientCredential = jsonFundsTransfer.substring(begIndex, jsonFundsTransfer.indexOf("\n\n\"" + Constants.RECIPIENT_SECURE_HASH_ALGORITHM));
                logger.debug("recipientCredential: " + recipientCredential);
                
                String storedCredential = credential.getJson();
                logger.debug("storedCredential: " + storedCredential);
                
                if (storedCredential.equals(recipientCredential)) {

                  String publicKeyHex = Utilities.parseJsonNameValuePairs(recipientCredential, Constants.PUBLIC_KEY_HEX);
                  logger.debug("publicKeyHex: " + publicKeyHex);

                  String recipientSignedHash = Utilities.parseJsonNameValuePairs(jsonFundsTransfer, Constants.RECIPIENT_SIGNED_HASH);
                  logger.debug("recipientSignedHash: " + recipientSignedHash);
                	
                  /*
                   * Usually the signature is verified based on the user in the data structure.  However, as an 
                   * additional security check, in this case we will verify the signature based on the public key 
                   * in the user's credential that will appear in the distributed ledger.
                   */
                  String verifyDistributedLedgerSignatureMessage = CryptoUtilities.verifySignature(publicKeyHex, recipientHash, recipientSignedHash);
                  logger.debug("verifySignatureMessage: " + verifySignatureMessage);

                  if (verifyDistributedLedgerSignatureMessage.trim().equals(PropertyManager.getInstance().getProperty(Constants.SIGNATURE_VERIFIED))) {

                    String fundsTransferUuid = Utilities.parseJsonNameValuePairs(jsonFundsTransfer, Constants.FUNDS_TRANSFER_UUID);
                    logger.debug("fundsTransferUuid: " + fundsTransferUuid);

                    String sendingBankUuid = Utilities.parseJsonNameValuePairs(jsonFundsTransfer, Constants.SENDING_BANK_UUID);
                    logger.debug("sendingBankUuid: " + sendingBankUuid);
                    
                    // ------------------------------------------------------------------------------------------------
                    
                    /*
                     * In the simplest use case the sender and recipient have accounts at the same bank;
                     * else,
                     * contact the sending bank to retrieve the funds transfer data.
                     */
                    
                    if (sendingBankUuid.equalsIgnoreCase(Constants.TEST_FINANCIAL_CREDENTIAL_PROVIDER_UUID)) {                      
                    	
                    	CredentialProvider credentialProvider = CredentialProviderDao.retrieveByCredentialProviderUuid(sendingBankUuid, null);
                    	
                    	jsonFundsTransfer += "\n\n\"" + Constants.TIMESTAMP + "\":\"" + Utilities.generateIsoTimestamp(System.currentTimeMillis()) + "\",";
                    	jsonFundsTransfer += "\n\"" + Constants.TRANSACTION_UUID + "\":\"" + transactionUuid + "\",";  
                  		jsonFundsTransfer += "\n\n\"" + Constants.RECEIVING_BANK_NAME + "\":\"" + credentialProvider.getCredentialProviderName() + "\",";
                  		jsonFundsTransfer += "\n\"" + Constants.RECEIVING_BANK_UUID + "\":\"" + sendingBankUuid + "\",";
                   		jsonFundsTransfer += "\n\"" + Constants.RECEIVING_BANK_SECURE_HASH_ALGORITHM + "\":\"" + CryptoUtilities.SIGNATURE_ALGORITHM + "\",";
                  		jsonFundsTransfer += "\n\"" + Constants.RECEIVING_BANK_SIGNATURE_ALGORITHM + "\":\"" + CryptoUtilities.SECURE_HASH_ALGORITHM + "\",";
                    	
                    	String receivingBankHash = CryptoUtilities.digest(jsonFundsTransfer);          			
                  		logger.debug("receivingBankHash: " + receivingBankHash);
                    	
                      String receivingBankSignedHash = CryptoUtilities.generateSignedHex(receivingBankHash, privateKey);          			
                  		logger.debug("receivingBankSignedHash: " + receivingBankSignedHash);          		
                    	
                  		jsonFundsTransfer += "\n\"" + Constants.RECEIVING_BANK_HASH + "\":\"" + receivingBankHash + ",";
                  		jsonFundsTransfer += "\n\"" + Constants.RECEIVING_BANK_SIGNED_HASH + "\":\"" + receivingBankSignedHash + "\",\n}]";
                    	
                    	FundsTransfer fundsTransfer = SendFundsDao.retrieveByFundsTransferUuid(fundsTransferUuid);

                  		fundsTransfer.setJson(jsonFundsTransfer);
                  		SendFundsDao.updateJson(fundsTransfer); 
                    	
                      responseString = PropertyManager.getInstance().getProperty(Constants.FUNDS_TRANSFER_ACCEPTED);
            	        
            	        // ----------------------------------------------------------------------------------------------
            	  			
            	  			HashMap<String, String> transferDataMap = new HashMap<String, String>();
            	  			
            	  			transferDataMap.put(Constants.FIREBASE_MSG_TYPE_KEY, Constants.FIREBASE_MSG_TYPE_CONFIRM_FUNDS_TRANSFER);
            	  			transferDataMap.put(Constants.CREDENTIAL_TYPE, Utilities.parseJsonNameValuePairs(jsonFundsTransfer, Constants.CREDENTIAL_TYPE));
            	  			transferDataMap.put(Constants.RECIPIENT_DATA, Utilities.parseJsonNameDataSet(jsonFundsTransfer, Constants.RECIPIENT_DATA));
            	  			transferDataMap.put(Constants.TRANSFER_AMOUNT, Utilities.parseJsonNameValuePairs(jsonFundsTransfer, Constants.TRANSFER_AMOUNT));
            	        
            	        HashMap<String, String> firebaseTransferDataMapSender =  
            	        		CryptoUtilities.generateFirebaseTransferDataMap(transferDataMap, Utilities.parseJsonNameValuePairs(jsonFundsTransfer, Constants.PUBLIC_KEY_HEX));
            	                    	        
            	        User sender = UserDao.retrieveByUserUuid(Utilities.parseJsonNameValuePairs(jsonFundsTransfer, Constants.USER_UUID));
            	  			Firebase.sendMessage(PropertyManager.getInstance().getProperty(Constants.NOTIFICATION_BODY_FUNDS_TRANSFER_CONFIRMED), firebaseTransferDataMapSender, sender.getFirebaseDeviceId());
                      
                      // ----------------------------------------------------------------------------------------------           	  			
                      
                      String emailMessage = "\nThank you for testing the funds transfer process.";
                      emailMessage += "\n";
                      emailMessage += "\nAn explanation of the distributed ledger below can be found at: http://www.trustnexus.io/finance.htm";
                      emailMessage += "\n";
                      emailMessage += "\n" + jsonFundsTransfer;
            	  	    
            	  	    EmailFactory.sendEmail(sender, PropertyManager.getInstance().getProperty(Constants.NOTIFICATION_BODY_FUNDS_TRANSFER_CONFIRMED), emailMessage);   
                      
                      // ----------------------------------------------------------------------------------------------    
            	  	                	  	    
            	  			
            	  			String recipientPublicKeyHex = Utilities.parseJsonNameValuePairs(jsonFundsTransfer.substring(jsonFundsTransfer.indexOf(Constants.PUBLIC_KEY_HEX) + Constants.PUBLIC_KEY_HEX.length()), Constants.PUBLIC_KEY_HEX);
            	  	    logger.debug("recipientPublicKeyHex: " + recipientPublicKeyHex);
            	        
            	        HashMap<String, String> firebaseTransferDataMapRecipient =   
            	        		CryptoUtilities.generateFirebaseTransferDataMap(transferDataMap, recipientPublicKeyHex);
            	        
            	        String recipientUserUuid = Utilities.parseJsonNameValuePairs(jsonFundsTransfer.substring(jsonFundsTransfer.indexOf(Constants.USER_UUID) + Constants.USER_UUID.length()), Constants.USER_UUID);
            	  	    logger.debug("recipientUserUuid: " + recipientUserUuid);            	        
            	  	    
											User recipient = UserDao.retrieveByUserUuid(recipientUserUuid);
											Firebase.sendMessage(PropertyManager.getInstance().getProperty(Constants.NOTIFICATION_BODY_FUNDS_TRANSFER_CONFIRMED), firebaseTransferDataMapRecipient, recipient.getFirebaseDeviceId()); 
            	  	    
            	  	    EmailFactory.sendEmail(recipient, PropertyManager.getInstance().getProperty(Constants.NOTIFICATION_BODY_FUNDS_TRANSFER_CONFIRMED), emailMessage);                                          
                    	
                    } else {
                    	
                    	// TODO:  Different banks.
                    	
                    }                  	
                  	
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







