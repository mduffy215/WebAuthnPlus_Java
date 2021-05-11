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

package io.trustnexus.struts.actions.createcredential;

import io.trustnexus.jdbc.AddressTypeDao;
import io.trustnexus.jdbc.CredentialAddressDao;
import io.trustnexus.jdbc.OrganizationDao;
import io.trustnexus.jdbc.UserSessionTrackingDao;
import io.trustnexus.jdbc.CredentialDao;
import io.trustnexus.jdbc.mobileapp.PublicPrivateKeyDao;
import io.trustnexus.model.CredentialAddress;
import io.trustnexus.model.Organization;
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
 * This action is called from the WebAuthn+ mobile app when a user receives a create credential message and 
 * authentication code and then touches the "Create" button.  All communication between the WebAuthn+ mobile app 
 * and the TNX Secure web application is encrypted based on the Transport Layer Security protocol with no digital 
 * certificates being used:
 * https://en.wikipedia.org/wiki/Transport_Layer_Security
 * 
 * Digital certificates are a notoriously weak method for securing keys and establishing identity.  No matter who 
 * you really are, you can always find a certificate "authority" to say you are someone else.
 * 
 * Order of precedence within io.trustnexus.struts.actions.createcredential:  44
 */
public class CreateCredentialAction extends ActionSupport implements ServletRequestAware, ServletResponseAware {

  private static final long serialVersionUID = -8992335522646657699L;

  private static Logger logger = LogManager.getLogger(CreateCredentialAction.class);

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
       * The HTTP request contains four packages (key/value pairs). The first package is the publicKeyUuid.  
       * This UUID is used to lookup the CREDENTIAL_PROVIDER private key.
       */
      logger.debug("publicKeyUuid: " + publicKeyUuid);
      
      PublicPrivateKey publicPrivateKey = PublicPrivateKeyDao.retrieveByUuid(publicKeyUuid);
      String privateKeyHex = publicPrivateKey.getPrivateKeyHex();

      /*
       * The second HTTP request package contains the transferKeyEncryptedHex which was encrypted with the credential 
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
       * the credential creation process; else return a message:  PropertyManager.getInstance().getProperty(Constants.MESSAGE_INTEGRITY_COMPROMISED).
       */
      if (transferDataEncryptedHashedHex.equals(transferDataEncryptedHashedHexTest)) {

        Cipher cipher = Cipher.getInstance(CryptoUtilities.CIPHER_ALGORITHM);

        /*
         * The first sixteen bytes, thirty-two HEX characters, of the encrypted data represents the initialization vector.
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

        String userUuid = Utilities.parseNameValuePairs(transferDataDecrypted, Constants.USER_UUID);
        logger.debug("userUuid: " + userUuid);
        
        UserSessionTrackingDao.updateUserIdentifier(userUuid, userSessionTrackingId); 

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

            String sessionUuid = Utilities.parseNameValuePairs(transferDataDecrypted, Constants.SESSION_UUID); 
            logger.debug("sessionUuid: " + sessionUuid);

            Credential credential = CredentialDao.retrieveByUserUuidAndSessionUuid(userUuid, sessionUuid);  
 
            if (credential != null) {

              String credentialUuid = Utilities.parseNameValuePairs(transferDataDecrypted, Constants.CREDENTIAL_UUID);
              String verificationCode = Utilities.parseNameValuePairs(transferDataDecrypted, Constants.VERIFICATION_CODE);
              String screenName = Utilities.parseNameValuePairs(transferDataDecrypted, Constants.SCREEN_NAME);
              String email = Utilities.parseNameValuePairs(transferDataDecrypted, Constants.EMAIL);

              String publicKeyHex = Utilities.parseNameValuePairs(transferDataDecrypted, Constants.PUBLIC_KEY_HEX);
              logger.debug("\npublicKeyHex: " + publicKeyHex);

              String firstName = Utilities.parseNameValuePairs(transferDataDecrypted, Constants.FIRST_NAME);
              String lastName = Utilities.parseNameValuePairs(transferDataDecrypted, Constants.LAST_NAME);
              String phone = Utilities.parseNameValuePairs(transferDataDecrypted, Constants.PHONE);

              credential.setUpdatedById(Constants.MASTER_USER);
              credential.setDataSourceTypeValue(Constants.DATA_SOURCE_APPLICATION);

              credential.setCredentialUuid(credentialUuid);
              credential.setVerificationCode(verificationCode);
              credential.setScreenName(screenName);
              credential.setEmail(email);
              credential.setUserUuid(userUuid);
              credential.setPublicKeyHex(publicKeyHex);

              credential.setFirstName(firstName);
              credential.setLastName(lastName);
              credential.setMobilePhone(phone); 

              // ------------------------------------------------------------------------------------------------------

              int addressType = 0;

              String addressTypeString = Utilities.parseNameValuePairs(transferDataDecrypted, Constants.ADDRESS_TYPE);
              logger.debug("addressTypeString: " + addressTypeString);

              if (addressTypeString != null && addressTypeString.length() > 0) {

                try {
                  addressType = Integer.parseInt(addressTypeString);
                } catch (Exception e) {
                	logger.error("", e);
                }
              }

              if (addressType == Constants.ADDRESS_TYPE_LEGAL_AND_MAILING || addressType==Constants.ADDRESS_TYPE_LEGAL) {

		            String legalAddressLineOne = Utilities.parseNameValuePairs(transferDataDecrypted, Constants.LEGAL_ADDRESS_LINE_ONE);
		            String legalAddressLineTwo = Utilities.parseNameValuePairs(transferDataDecrypted, Constants.LEGAL_ADDRESS_LINE_TWO);
		            String legalAddressCity = Utilities.parseNameValuePairs(transferDataDecrypted, Constants.LEGAL_ADDRESS_CITY);
		            String legalAddressState = Utilities.parseNameValuePairs(transferDataDecrypted, Constants.LEGAL_ADDRESS_STATE);
		            String legalAddressPostalCode = Utilities.parseNameValuePairs(transferDataDecrypted, Constants.LEGAL_ADDRESS_POSTAL_CODE);
		            String legalAddressCountry = Utilities.parseNameValuePairs(transferDataDecrypted, Constants.LEGAL_ADDRESS_COUNTRY);
		
		            if (legalAddressLineOne != null && legalAddressCity != null & legalAddressState != null && legalAddressPostalCode != null
		                    && legalAddressLineOne.length() > 1 && legalAddressCity.length() > 1 & legalAddressState.length() > 1
		                    && legalAddressPostalCode.length() > 1) {
		
		              CredentialAddress legalAddress = new CredentialAddress();
		
		              legalAddress.setUpdatedById(Constants.MASTER_USER);
		              legalAddress.setDataSourceTypeValue(Constants.DATA_SOURCE_APPLICATION);
		              legalAddress.setCredentialId(credential.getCredentialId());
		
		              legalAddress.setAddressType(addressType);
		              legalAddress.setAddressLineOne(legalAddressLineOne);
		              legalAddress.setAddressLineTwo(legalAddressLineTwo);
		              legalAddress.setCity(legalAddressCity);
		              legalAddress.setStateOrProvince(legalAddressState);
		              legalAddress.setPostalCode(legalAddressPostalCode);
		              legalAddress.setCountry(legalAddressCountry);
		              
		              legalAddress.setAddressTypeObject(AddressTypeDao.retrieveByAddressType(addressType));  
		
		              CredentialAddressDao.create(legalAddress);
		              credential.adddCredentialAddress(legalAddress); 
		            }
              }

              // ------------------------------------------------------------------------------------------------------

              if (addressType == Constants.ADDRESS_TYPE_MAILING) {

                String mailingAddressLineOne = Utilities.parseNameValuePairs(transferDataDecrypted, Constants.MAILING_ADDRESS_LINE_ONE);
                String mailingAddressLineTwo = Utilities.parseNameValuePairs(transferDataDecrypted, Constants.MAILING_ADDRESS_LINE_TWO);
                String mailingAddressCity = Utilities.parseNameValuePairs(transferDataDecrypted, Constants.MAILING_ADDRESS_CITY);
                String mailingAddressState = Utilities.parseNameValuePairs(transferDataDecrypted, Constants.MAILING_ADDRESS_STATE);
                String mailingAddressPostalCode = Utilities.parseNameValuePairs(transferDataDecrypted, Constants.MAILING_ADDRESS_POSTAL_CODE);
                String mailingAddressCountry = Utilities.parseNameValuePairs(transferDataDecrypted, Constants.MAILING_ADDRESS_COUNTRY);

                if (mailingAddressLineOne != null && mailingAddressCity != null & mailingAddressState != null && mailingAddressPostalCode != null
                        && mailingAddressLineOne.length() > 1 && mailingAddressCity.length() > 1 && mailingAddressState.length() > 1
                        && mailingAddressPostalCode.length() > 1) {

                  CredentialAddress mailingAddress = new CredentialAddress();

                  mailingAddress.setUpdatedById(Constants.MASTER_USER);
                  mailingAddress.setDataSourceTypeValue(Constants.DATA_SOURCE_APPLICATION);
                  mailingAddress.setCredentialId(credential.getCredentialId());

                  mailingAddress.setAddressType(Constants.ADDRESS_TYPE_MAILING);
                  mailingAddress.setAddressLineOne(mailingAddressLineOne);
                  mailingAddress.setAddressLineTwo(mailingAddressLineTwo);
                  mailingAddress.setCity(mailingAddressCity);
                  mailingAddress.setStateOrProvince(mailingAddressState);
                  mailingAddress.setPostalCode(mailingAddressPostalCode);
                  mailingAddress.setCountry(mailingAddressCountry);
		              
                  mailingAddress.setAddressTypeObject(AddressTypeDao.retrieveByAddressType(addressType));

                  CredentialAddressDao.create(mailingAddress);
		              credential.adddCredentialAddress(mailingAddress); 
                }
              }

              // ------------------------------------------------------------------------------------------------------

              String organizationName = Utilities.parseNameValuePairs(transferDataDecrypted, Constants.ORGANIZATION_NAME);
              String organizationUrl = Utilities.parseNameValuePairs(transferDataDecrypted, Constants.ORGANIZATION_URL);
              String organizationTitle = Utilities.parseNameValuePairs(transferDataDecrypted, Constants.ORGANIZATION_TITLE);
              String organizationPhone = Utilities.parseNameValuePairs(transferDataDecrypted, Constants.ORGANIZATION_PHONE);

              if (organizationName != null && organizationName.length() > 1) {

                Organization organization = new Organization();

                organization.setUpdatedById(Constants.MASTER_USER);
                organization.setDataSourceTypeValue(Constants.DATA_SOURCE_APPLICATION);
                organization.setCredentialId(credential.getCredentialId());

                organization.setOrganizationName(organizationName);
                organization.setOrganizationUrl(organizationUrl);
                organization.setTitle(organizationTitle);
                organization.setPhone(organizationPhone);

                OrganizationDao.create(organization);
                credential.setOrganization(organization);

                // ----------------------------------------------------------------------------------------------------

                String organizationAddressLineOne = Utilities.parseNameValuePairs(transferDataDecrypted, Constants.ORGANIZATION_ADDRESS_LINE_ONE);
                String organizationAddressLineTwo = Utilities.parseNameValuePairs(transferDataDecrypted, Constants.ORGANIZATION_ADDRESS_LINE_TWO);
                String organizationAddressCity = Utilities.parseNameValuePairs(transferDataDecrypted, Constants.ORGANIZATION_ADDRESS_CITY);
                String organizationAddressState = Utilities.parseNameValuePairs(transferDataDecrypted, Constants.ORGANIZATION_ADDRESS_STATE);
                String organizationAddressPostalCode = Utilities.parseNameValuePairs(transferDataDecrypted, Constants.ORGANIZATION_ADDRESS_POSTAL_CODE);
                String organizationAddressCountry = Utilities.parseNameValuePairs(transferDataDecrypted, Constants.ORGANIZATION_ADDRESS_COUNTRY);

                if (organizationAddressLineOne != null && organizationAddressCity != null && organizationAddressState != null
                        && organizationAddressPostalCode != null && organizationAddressLineOne.length() > 1 && organizationAddressCity.length() > 1
                        && organizationAddressState.length() > 1 && organizationAddressPostalCode.length() > 1) {

                  CredentialAddress organizationAddress = new CredentialAddress();

                  organizationAddress.setUpdatedById(Constants.MASTER_USER);
                  organizationAddress.setDataSourceTypeValue(Constants.DATA_SOURCE_APPLICATION);
                  organizationAddress.setCredentialId(credential.getCredentialId());

                  organizationAddress.setAddressType(Constants.ADDRESS_TYPE_ORGANIZATION);
                  organizationAddress.setAddressLineOne(organizationAddressLineOne);
                  organizationAddress.setAddressLineTwo(organizationAddressLineTwo);
                  organizationAddress.setCity(organizationAddressCity);
                  organizationAddress.setStateOrProvince(organizationAddressState);
                  organizationAddress.setPostalCode(organizationAddressPostalCode);
                  organizationAddress.setCountry(organizationAddressCountry);
		              
                  organizationAddress.setAddressTypeObject(AddressTypeDao.retrieveByAddressType(Constants.ADDRESS_TYPE_ORGANIZATION));

                  CredentialAddressDao.create(organizationAddress);
                  organization.setOrganizationAddress(organizationAddress); 
                }
              }
              
              // ------------------------------------------------------------------------------------------------------
              
              String jsonCredential = credential.generateJsonCredntial();
            	logger.debug("jsonCredential: " + jsonCredential);
            	
            	String credentialProviderHash = CryptoUtilities.digest(jsonCredential);
            	logger.debug("credentialProviderHash: " + credentialProviderHash); 

    	        jsonCredential += "\n\"credentialProviderHash\":\"" + credentialProviderHash + "\",";
            	
              String credentialProviderSignedHash = CryptoUtilities.generateSignedHex(credentialProviderHash, privateKey);          			
          		logger.debug("credentialProviderSignedHash: " + credentialProviderSignedHash);

    	        jsonCredential += "\n\"credentialProviderSignedHash\":\"" + credentialProviderSignedHash + "\",";    	      	
              
     	        jsonCredential += "\n}],";  //end credential               
              
              credential.setJson(jsonCredential);
              logger.debug("credential.getJson(): " + credential.getJson());

              CredentialDao.update(credential);
              logger.debug("CREDENTIAL_UPDATED: " + credential.toString());

              responseString = PropertyManager.getInstance().getProperty(Constants.CREDENTIAL_CREATED) + jsonCredential;              

            } else {
              responseString = PropertyManager.getInstance().getProperty(Constants.CREDENTIAL_CREATION_PROCESS_NOT_INITIALIZED);
            }

          } else {
            responseString = verifySignatureMessage;
          }

        } else {
          responseString = verifyTransactionUuidMessage;
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







