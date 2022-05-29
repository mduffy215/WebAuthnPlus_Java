/*
 * (c) Copyright 2022 ~ Trust Nexus, Inc.
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

package io.trustnexus.util;

import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import javax.crypto.Cipher;

import io.trustnexus.jdbc.mobileapp.PublicPrivateKeyDao;
import io.trustnexus.model.mobileapp.PublicPrivateKey;

public class InitializeB_Keys {

  public static void main(String[] args) {

    /*
     * You only need to create keys for the MOBILE_APP_PROVIDER if you are distributing your own version of the 
     * TNX WebAuthn+ mobile app; if not, just create keys for "your organization" (e.g., COMMUNITY_WORLD_BANK).
     * 
     * IF YOU ARE THE MOBILE APP PROVIDER YOU MUST SET THE PUBLIC KEY GENERATED HERE IN YOUR TNX WEBAUTHN+ MOBILE APP.
     * 
     * Note:  Over time, an organization will create many keys, each with their own unique UUID.
     */
    
    String keyOwner = Constants.MOBILE_APP_PROVIDER_KEY_OWNER;
    //String keyOwner = Constants.COMMUNITY_WORLD_BANK;
    
    // ----------------------------------------------------------------------------------------------------------------

    Connection connection = null;
    
    PreparedStatement insertStatementPublicPrivateKey = null;
    PreparedStatement retrieveStatementPublicPrivateKey = null;
    ResultSet resultSetPublicPrivateKey = null;

    PublicPrivateKey publicPrivateKeyCreate = null;
    PublicPrivateKey publicPrivateKeyRetrieve = null;
    

    try {
      Class.forName("com.mysql.cj.jdbc.Driver");
      connection = DriverManager.getConnection(Constants.SYSTEM_INIT_DATABASE_URL, 
                                               Constants.SYSTEM_INIT_DATABASE_USERNAME, 
                                               Constants.SYSTEM_INIT_DATABASE_PASSWORD);

      System.out.println("Connection established: " + connection.toString());
      
      // --------------------------------------------------------------------------------------------------------------   
      
      publicPrivateKeyCreate = new PublicPrivateKey();
      
      Timestamp timestamp = new Timestamp(System.currentTimeMillis());
      publicPrivateKeyCreate.setCreated(timestamp);
      publicPrivateKeyCreate.setUpdated(timestamp);
      
      publicPrivateKeyCreate.setUpdatedById(Constants.MASTER_USER);
      publicPrivateKeyCreate.setDataSourceTypeValue(Constants.DATA_SOURCE_APPLICATION);
      
      publicPrivateKeyCreate.setKeyOwner(keyOwner);
      publicPrivateKeyCreate.setInactiveFlag(false);
      publicPrivateKeyCreate.setUuid(CryptoUtilities.generateUuid());
      
      // --------------------------------------------------------------------------------------------------------------

      long begTime = System.currentTimeMillis();
      System.out.println("\nBegin key pair generation.  This may take a few seconds...");

      Cipher rsaCipher = Cipher.getInstance("RSA/ECB/OAEPWITHSHA-512ANDMGF1PADDING ");

      KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(CryptoUtilities.KEY_FACTORY_ALGORITHM);

      keyPairGenerator.initialize(CryptoUtilities.PUBLIC_KEY_MODULUS);
      KeyPair pair = keyPairGenerator.generateKeyPair();

      long endTime = System.currentTimeMillis();

      System.out.println("\n            Key Gen Time: " + (endTime - begTime));

      Key publicKey = pair.getPublic();
      Key privateKey = pair.getPrivate();
      
      publicPrivateKeyCreate.setPublicKeyHex(CryptoUtilities.toHex(publicKey.getEncoded()));
      publicPrivateKeyCreate.setPrivateKeyHex(CryptoUtilities.toHex(privateKey.getEncoded())); 

      System.out.println("\n               publicKey: " + CryptoUtilities.toHex(publicKey.getEncoded()));
      System.out.println("\n              privateKey: " + CryptoUtilities.toHex(privateKey.getEncoded()));
      
      // --------------------------------------------------------------------------------------------------------------

      System.out.println("\n\n\n\nTest the keys.");

      String inputString = "When in the Course of human events, it becomes necessary for one people to...";

      byte[] inputBytes = inputString.getBytes();      
      String inputHex = CryptoUtilities.toHex(inputBytes);
      byte[] inputHexBytes = CryptoUtilities.hexStringToByteArray(inputHex); 
      
      System.out.println("\n             inputString: " + inputString);
      System.out.println("\n                inputHex: " + CryptoUtilities.toHex(inputHexBytes));

      // RSA Encryption

      begTime = System.currentTimeMillis();

      rsaCipher.init(Cipher.ENCRYPT_MODE, publicKey);
      byte[] rsaCipherText = rsaCipher.doFinal(inputHexBytes);

      endTime = System.currentTimeMillis();

      System.out.println("\n         Encryption Time: " + (endTime - begTime) + "ms");
      System.out.println("\n           rsaCipherText: " + CryptoUtilities.toHex(rsaCipherText));

      // RSA Decryption

      begTime = System.currentTimeMillis();

      rsaCipher.init(Cipher.DECRYPT_MODE, privateKey);
      byte[] rsaPlainText = rsaCipher.doFinal(rsaCipherText);
      
      String decryptedHex = CryptoUtilities.toHex(rsaPlainText);

      endTime = System.currentTimeMillis();
      
      String decryptedString = new String(CryptoUtilities.hexStringToByteArray(decryptedHex));

      System.out.println("\n         Decryption Time: " + (endTime - begTime) + "ms");
      System.out.println("\n            decryptedHex: " + decryptedHex);
      System.out.println("\n        Decrypted String: " + decryptedString);
      System.out.println("\n        Input String =? Decrypted String: " + inputString.equals(decryptedString));
      
      // --------------------------------------------------------------------------------------------------------------
      
      /*
       * In this prototype application we are going to store our private keys in the data base.
       * This is NOT a good idea for a production level application.
       * Some good suggestions can be found here:  
       * https://security.stackexchange.com/questions/12332/where-to-store-a-server-side-encryption-key
       * 
       * If the keys for the MOBILE_APP_PROVIDER_KEY_OWNER are ever compromised new keys will need to be issued by
       * creating a new version of the TNX Secure Mobile application and requiring all users to upgrade.
       * 
       * TODO:  Create process for upgrade notice.
       */
      
      System.out.println("\n\n\n\nStore the PublicPrivateKey data.");
      System.out.println(publicPrivateKeyCreate.toString());
      
      insertStatementPublicPrivateKey = connection.prepareStatement(PublicPrivateKeyDao.INSERT);
      
      insertStatementPublicPrivateKey.setTimestamp(1, publicPrivateKeyCreate.getCreated());
      insertStatementPublicPrivateKey.setTimestamp(2, publicPrivateKeyCreate.getUpdated());
      insertStatementPublicPrivateKey.setInt(3, publicPrivateKeyCreate.getUpdatedById());
      insertStatementPublicPrivateKey.setInt(4, publicPrivateKeyCreate.getDataSourceTypeValue());
      
      insertStatementPublicPrivateKey.setString(5, publicPrivateKeyCreate.getKeyOwner());
      insertStatementPublicPrivateKey.setString(6, publicPrivateKeyCreate.getPublicKeyHex());
      insertStatementPublicPrivateKey.setString(7, publicPrivateKeyCreate.getPrivateKeyHex());
      insertStatementPublicPrivateKey.setBoolean(8, publicPrivateKeyCreate.isInactiveFlag());
      insertStatementPublicPrivateKey.setString(9, publicPrivateKeyCreate.getUuid());
      
      insertStatementPublicPrivateKey.execute();
      
      // --------------------------------------------------------------------------------------------------------------
      
      retrieveStatementPublicPrivateKey = connection.prepareStatement(PublicPrivateKeyDao.RETRIEVE_BY_KEY_OWNER);
      retrieveStatementPublicPrivateKey.setString(1, keyOwner);

      resultSetPublicPrivateKey = retrieveStatementPublicPrivateKey.executeQuery();

      if (resultSetPublicPrivateKey != null && resultSetPublicPrivateKey.next()) {
        publicPrivateKeyRetrieve = PublicPrivateKeyDao.loadFromResultSet(resultSetPublicPrivateKey);
      }

      System.out.println("\n\n\n\nRetrieve the PublicPrivateKey data.");
      System.out.println(publicPrivateKeyRetrieve.toString()); 

      System.out.println("\n\n\n\npublicPrivateKeyCreate =? publicPrivateKeyRetrieve: " + publicPrivateKeyCreate.equals(publicPrivateKeyRetrieve));      
     
    } catch (Exception e) {
      throw new RuntimeException(e);
    } finally {    	    	
      try {
        insertStatementPublicPrivateKey.close();
        retrieveStatementPublicPrivateKey.close();
        resultSetPublicPrivateKey.close();        
        connection.close();
      } catch (SQLException e) {
        throw new RuntimeException(e);
      }
    }
  }
}







