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

import io.trustnexus.jdbc.UserSessionTrackingDao;
import io.trustnexus.jdbc.mobileapp.PublicPrivateKeyDao;
import io.trustnexus.jdbc.mobileapp.UserAddressDao;
import io.trustnexus.jdbc.mobileapp.UserDao;
import io.trustnexus.model.mobileapp.PublicPrivateKey;
import io.trustnexus.model.mobileapp.UserAddress;
import io.trustnexus.model.mobileapp.User;
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
 * This action is called from the WebAuthn+ mobile app during the create user process.  All communication between 
 * the WebAuthn+ mobile app and the TNX Secure web application is encrypted based on the Transport Layer Security 
 * protocol with no digital certificates being used:
 * https://en.wikipedia.org/wiki/Transport_Layer_Security
 * 
 * Digital certificates are a notoriously weak method for securing keys and establishing identity.  No matter who 
 * you really are, you can always find a certificate "authority" to say you are someone else.
 */
public class CreateUserAction extends ActionSupport implements ServletRequestAware, ServletResponseAware {

  private static final long serialVersionUID = -7439378916563388176L;

  private static Logger logger = LogManager.getLogger(CreateUserAction.class);

  private String publicKeyUuid;
  private String transferKeyEncryptedHex;
  private String transferDataEncryptedHex;
  private String transferDataEncryptedHashedHex;

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
      PrivateKey privateKey = KeyFactory.getInstance(CryptoUtilities.KEY_FACTORY_ALGORITHM)
      		                              .generatePrivate(new PKCS8EncodedKeySpec(CryptoUtilities.hexStringToByteArray(privateKeyHex)));

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
       * the process; else return a message:  PropertyManager.getInstance().getProperty(Constants.MESSAGE_INTEGRITY_COMPROMISED).
       */
      if (transferDataEncryptedHashedHex.equals(transferDataEncryptedHashedHexTest)) {

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

        String email = Utilities.parseNameValuePairs(transferDataDecrypted, Constants.EMAIL);
        logger.debug("email: " + email);

        User testUser = UserDao.retrieveByEmail(email);

        /*
         * If the user does not already exist, create the user; else, return a message: Constants.USER_ALREADY_EXISTS.
         */
        if (testUser == null) {

          String screenName = Utilities.parseNameValuePairs(transferDataDecrypted, Constants.SCREEN_NAME);
          logger.debug("screenName: " + screenName);

          String phone = Utilities.parseNameValuePairs(transferDataDecrypted, Constants.PHONE);
          logger.debug("phone: " + phone);

          String firstName = Utilities.parseNameValuePairs(transferDataDecrypted, Constants.FIRST_NAME);
          logger.debug("firstName: " + firstName);

          String lastName = Utilities.parseNameValuePairs(transferDataDecrypted, Constants.LAST_NAME);
          logger.debug("lastName: " + lastName);

          userUuid = Utilities.parseNameValuePairs(transferDataDecrypted, Constants.USER_UUID);
          logger.debug("userUuid: " + userUuid);

          String firebaseDeviceId = Utilities.parseNameValuePairs(transferDataDecrypted, Constants.FIRE_BASE_DEVICE_ID);
          logger.debug("firebaseDeviceId: " + firebaseDeviceId);
          
          UserSessionTrackingDao.updateUserIdentifier(userUuid, userSessionTrackingId);
          
          if (email.length() < 1 || screenName.length() < 1 || userUuid.length() < 1) {
						throw new RuntimeException("Invalid user data.");
					}

          // ----------------------------------------------------------------------------------------------------------

          User user = new User();

          user.setUpdatedById(Constants.MASTER_USER);
          user.setDataSourceTypeValue(Constants.DATA_SOURCE_APPLICATION);

          user.setScreenName(screenName);
          user.setEmail(email);
          user.setPhone(phone);
          user.setFirstName(firstName);
          user.setLastName(lastName);
          user.setUserUuid(userUuid);
          user.setInactiveFlag(true);
          user.setRefCode(CryptoUtilities.generateUuid());
          user.setFirebaseDeviceId(firebaseDeviceId); 

          UserDao.create(user);

          User newUser = UserDao.retrieveByUserUuid(userUuid);

          // ----------------------------------------------------------------------------------------------------------

          String addressLineOne = Utilities.parseNameValuePairs(transferDataDecrypted, Constants.LEGAL_ADDRESS_LINE_ONE);
          logger.debug("addressLineOne: " + addressLineOne);

          String addressLineTwo = Utilities.parseNameValuePairs(transferDataDecrypted, Constants.LEGAL_ADDRESS_LINE_TWO);
          logger.debug("addressLineTwo: " + addressLineTwo);

          String city = Utilities.parseNameValuePairs(transferDataDecrypted, Constants.LEGAL_ADDRESS_CITY);
          logger.debug("city: " + city);

          String state = Utilities.parseNameValuePairs(transferDataDecrypted, Constants.LEGAL_ADDRESS_STATE);
          logger.debug("state: " + state);

          String postalCode = Utilities.parseNameValuePairs(transferDataDecrypted, Constants.LEGAL_ADDRESS_POSTAL_CODE);
          logger.debug("postalCode: " + postalCode);

          String country = Utilities.parseNameValuePairs(transferDataDecrypted, Constants.LEGAL_ADDRESS_COUNTRY);
          logger.debug("country: " + country);

          // ----------------------------------------------------------------------------------------------------------

          UserAddress userAddress = new UserAddress();

          userAddress.setUpdatedById(Constants.MASTER_USER);
          userAddress.setDataSourceTypeValue(Constants.DATA_SOURCE_APPLICATION);
          userAddress.setUserId(newUser.getUserId());

          userAddress.setAddressType(Constants.ADDRESS_TYPE_LEGAL);
          userAddress.setAddressLineOne(addressLineOne);
          userAddress.setAddressLineTwo(addressLineTwo);
          userAddress.setCity(city);
          userAddress.setState(state);
          userAddress.setPostalCode(postalCode);
          userAddress.setCountry(country);

          UserAddressDao.create(userAddress);

          // ----------------------------------------------------------------------------------------------------------

          responseString = PropertyManager.getInstance().getProperty(Constants.USER_SUCCESSFULLY_CREATED);

        } else {
          responseString = PropertyManager.getInstance().getProperty(Constants.USER_ALREADY_EXISTS);
        }
      } else {
        responseString = PropertyManager.getInstance().getProperty(Constants.MESSAGE_INTEGRITY_COMPROMISED);
      }

    } catch (Exception e) {
      responseString = PropertyManager.getInstance().getProperty(Constants.PROBLEM_WITH_AUTHENTICATION_SERVER);
      logger.error("", e);
    } finally {
    	try {
				UserDao.deleteByUserUuid(userUuid);
			} catch (Exception e2) {
	      logger.error("", e2); 
			}
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







