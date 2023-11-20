/*
 * (c) Copyright 2023 ~ Trust Nexus, Inc.
 * All technologies described here in are "Patent Pending". 
 * License information:  https://www.trustnexus.io/license.htm
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

import io.trustnexus.jdbc.mobileapp.CredentialProviderDao;
import io.trustnexus.model.mobileapp.CredentialProvider;

public class InitializeC_CredentialProvider {

  public static void main(String[] args) {
    
    /*
     * If you are distributing your own version of the TNX WebAuthn+ mobile app, your credential provider values can be 
     * initialized by running this utility.
     * 
     * If you are not distributing your own version of the TNX WebAuthn+ mobile app, you will need to register the 
     * credential provider values at https://www.webauthnplus.com/registerCredentialProvider.action [not yet available]
     * 
     * TODO:  Create RegisterCredentialProvider.java and registerCredentialProvider.jsp
     */    
    
    // ----------------------------------------------------------------------------------------------------------------

    Connection connection = null;

    CredentialProvider credentialProviderCreate = null;
    CredentialProvider credentialProviderRetrieve = null;
    
    /*
     * Set your own values for the baseUrl, the credential provider name and for the administrator.
     */
    String baseUrl = "www.webauthnplus.com";
    //String baseUrl = "www.cwb_test.com";

    try { 
      Class.forName("com.mysql.cj.jdbc.Driver");
      connection = DriverManager.getConnection(Constants.SYSTEM_INIT_DATABASE_URL, 
                                               Constants.SYSTEM_INIT_DATABASE_USERNAME, 
                                               Constants.SYSTEM_INIT_DATABASE_PASSWORD);

      System.out.println("Connection established: " + connection.toString());
      
      String credentialProviderUuid = CryptoUtilities.generateUuid(); 
        
      credentialProviderCreate = new CredentialProvider();
      
      credentialProviderCreate.setUpdatedById(Constants.MASTER_USER);
      credentialProviderCreate.setDataSourceTypeValue(Constants.DATA_SOURCE_APPLICATION);
      
      credentialProviderCreate.setCredentialProviderName("WebAuthn Plus");
      credentialProviderCreate.setDomainName(baseUrl);
      credentialProviderCreate.setAdminFirstName("Michael");
      credentialProviderCreate.setAdminLastName("Duffy");
      credentialProviderCreate.setAdminEmail("admin@webauthnplus.com");
    
//      credentialProviderCreate.setCredentialProviderName("Community World Bank");
//      credentialProviderCreate.setDomainName(baseUrl);
//      credentialProviderCreate.setAdminFirstName("Michael");
//      credentialProviderCreate.setAdminLastName("Duffy");
//      credentialProviderCreate.setAdminEmail("admin@cwb_test.com");
      
      credentialProviderCreate.setRetrieveCredentialMetaDataUrl("https://" + baseUrl + "/foundation/retrieveCredentialMetaData.action");     
      credentialProviderCreate.setCreateCredentialUrl("https://" + baseUrl + "/foundation/createCredential.action");
      credentialProviderCreate.setSignOnUrl("https://" + baseUrl + "/foundation/signOn.action");
      credentialProviderCreate.setCancelSignOnUrl("https://" + baseUrl + "/foundation/cancelSignOn.action");
      credentialProviderCreate.setRetrieveUnsignedDistributedLedgerUrl("https://" + baseUrl + "/foundation/retrieveUnsignedDistributedLedger.action");
      credentialProviderCreate.setReturnSignedDistributedLedgerUrl("https://" + baseUrl + "/foundation/returnSignedDistributedLedger.action");
      
      credentialProviderCreate.setSendFundsUrl("https://" + baseUrl + "/foundation/sendFunds.action");
      credentialProviderCreate.setReceiveFundsUrl("https://" + baseUrl + "/foundation/receiveFunds.action");
      credentialProviderCreate.setAcceptFundsUrl("https://" + baseUrl + "/foundation/acceptFunds.action");
      credentialProviderCreate.setConfirmFundsUrl("https://" + baseUrl + "/foundation/confirmFunds.action");
      
      credentialProviderCreate.setDeleteCredentialUrl("https://" + baseUrl + "/foundation/deleteCredential.action");
      
      credentialProviderCreate.setRetrieveTransactionUuidUrl("https://" + baseUrl + "/foundation/retrieveTransactionUuid.action");
      credentialProviderCreate.setCredentialProviderUuid(credentialProviderUuid);
      
      CredentialProviderDao.create(credentialProviderCreate, connection); 
      
      System.out.println("\n\nStore the CredentialProvider data.\n");
      System.out.println(credentialProviderCreate.toString());
      
      // ----------------------------------------------------------------------------------------------------------------
      
      connection = DriverManager.getConnection(Constants.SYSTEM_INIT_DATABASE_URL,  
                                               Constants.SYSTEM_INIT_DATABASE_USERNAME, 
                                               Constants.SYSTEM_INIT_DATABASE_PASSWORD);

      System.out.println("Connection established: " + connection.toString());
      
      credentialProviderRetrieve = CredentialProviderDao.retrieveByCredentialProviderUuid(credentialProviderUuid, connection);  
  
      System.out.println("\n\nRetrieve the CredentialProvider data.\n");
      System.out.println(credentialProviderRetrieve.toString());

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







