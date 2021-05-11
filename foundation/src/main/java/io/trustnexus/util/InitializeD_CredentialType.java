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

package io.trustnexus.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import io.trustnexus.jdbc.mobileapp.CredentialTypeDao;
import io.trustnexus.model.mobileapp.CredentialType;

public class InitializeD_CredentialType {

  public static void main(String[] args) {
    
    /*
     * New credentials are created by initializing the credential type. 
     * 
     * If you are not distributing your own version of the TNX WebAuthn+ mobile app, you must register the credential 
     * types at http://www.webauthnplus.com/registerCredentialType.action [not yet available]
     * 
     * TODO:  Create RegisterCredentialTypeAction.java and registerCredentialType.jsp
     */    
    
    // ----------------------------------------------------------------------------------------------------------------

    /*
     *  TODO: Set the publicPrivateKeyUuid from the database (this value was generated by running InitializeB_Keys).
     */
    String publicPrivateKeyUuid = "1620483590224-C68FBF87-6447-41A4-9F5B-D7F725774224";
    //String publicPrivateKeyUuid = "1620483671514-46B8F6E0-6A1D-4817-B891-B481497CB5D0";
    
    /*
     *  TODO: Set the credentialProviderUuid from the database (this value was generated by running InitializeC_CredentialProvider). 
     */
    String credentialProviderUuid = "1620489409066-98A7A32E-43EC-4D97-9B0A-1F39758CC890";
    //String credentialProviderUuid = "1620489685915-833709F4-5B0E-42EA-83BE-F98199C33DAE";

    /*
     * TODO: Set the values for credentialType, credentialIconUrl and displayName.
     */
    
    String credentialType = "com.webauthnplus.credentialtype.TNX_Test_1";
    //String credentialType = "com.cwbank.credentialtype.Financial_Test_1";
    String credentialIconUrl = "https://www.webauthnplus.com/foundation/images/com_webauthnplus_credentialtype_TNX_Test_1.png";
    //String credentialIconUrl = "http://www.webauthnplus.com/foundation/images/com_cwbank_credentialtype_Financial_Test_1.png";
    String displayName = "WebAuthn Plus 1";
    //String displayName = "Financial Test 1";
    
    Connection connection = null;

    try { 
      Class.forName("com.mysql.cj.jdbc.Driver");
      
      connection = DriverManager.getConnection(Constants.SYSTEM_INIT_DATABASE_URL, 
          Constants.SYSTEM_INIT_DATABASE_USERNAME, 
          Constants.SYSTEM_INIT_DATABASE_PASSWORD);

      System.out.println("Connection established: " + connection.toString());
      
      CredentialType credentialTypeCreate = new CredentialType();
      
      credentialTypeCreate.setUpdatedById(Constants.MASTER_USER);
      credentialTypeCreate.setDataSourceTypeValue(Constants.DATA_SOURCE_APPLICATION);
  
      credentialTypeCreate.setCredentialProviderUuid(credentialProviderUuid); 
      credentialTypeCreate.setPublicPrivateKeyUuid(publicPrivateKeyUuid); 
      credentialTypeCreate.setCredentialTypeName(credentialType);
      credentialTypeCreate.setCredentialIconUrl(credentialIconUrl);
      credentialTypeCreate.setDisplayName(displayName);  
      credentialTypeCreate.setExpirationMonths(Constants.DEFAULT_CREDENTIAL_EXPIRATION_MONTHS); 
      
      CredentialTypeDao.create(credentialTypeCreate, connection); 
      
      System.out.println("\n\n\n\nStore the CredentialType data.");
      System.out.println(credentialTypeCreate.toString());
      
      // ----------------------------------------------------------------------------------------------------------------
      
      connection = DriverManager.getConnection(Constants.SYSTEM_INIT_DATABASE_URL, 
          Constants.SYSTEM_INIT_DATABASE_USERNAME, 
          Constants.SYSTEM_INIT_DATABASE_PASSWORD);

      System.out.println("Connection established: " + connection.toString());
      
      CredentialType credentialTypeRetrieve = CredentialTypeDao.retrieveByCredentialType(credentialType, connection); 
  
      System.out.println("\n\n\n\nRetrieve the CredentialType data.");
      System.out.println(credentialTypeRetrieve.toString()); 

    } catch (Exception e) {
      throw new RuntimeException(e);
    } finally {
      try {
        if (connection != null) {
          connection.close();         
        }
      } catch (SQLException e) {
        throw new RuntimeException(e);
      }
    }
  }
}







