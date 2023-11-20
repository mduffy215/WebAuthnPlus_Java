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

package io.trustnexus.jdbc.mobileapp;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.trustnexus.model.Credential;
import io.trustnexus.model.mobileapp.CredentialProvider;
import io.trustnexus.model.mobileapp.PublicPrivateKey;
import io.trustnexus.util.C3P0DataSource;
import io.trustnexus.util.Constants;

public class CredentialProviderDao {

  private final static Logger logger = LogManager.getLogger(CredentialProviderDao.class);

  public static final String INSERT = "INSERT CredentialProvider (created, updated, updatedById, dataSourceTypeValue, credentialProviderName, "
                                    + "domainName, adminFirstName, adminLastName, adminEmail, retrieveCredentialMetaDataUrl, createCredentialUrl, "
                                    + "deleteCredentialUrl, signOnUrl, cancelSignOnUrl, retrieveUnsignedDistributedLedgerUrl, "
                                    + "returnSignedDistributedLedgerUrl, sendFundsUrl, receiveFundsUrl, acceptFundsUrl, confirmFundsUrl, "
                                    + "retrieveTransactionUuidUrl, credentialProviderUuid) " 
                                    + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

  private static final String RETRIEVE = "SELECT * FROM CredentialProvider " 
                                       + "WHERE credentialProviderId = ?";

  private static final String RETRIEVE_BY_CREDENTIAL_PROVIDER_UUID = "SELECT * FROM CredentialProvider " 
                                                                   + "WHERE credentialProviderUuid = ?";

  private static final String RETRIEVE_BY_CREDENTIAL_TYPE = "SELECT * FROM CredentialProvider CP "
  		                                                    + "JOIN CredentialType CT ON CP.credentialProviderUuid = CT.credentialProviderUuid " 
                                                          + "WHERE CT.credentialType = ?";

  // ------------------------------------------------------------------------------------------------------------------

  public CredentialProviderDao() {
  }

  // ------------------------------------------------------------------------------------------------------------------

  public static void create(CredentialProvider credentialProvider, Connection connection) {

    logger.info("###Entering");
    
    if (connection == null) {
      connection = C3P0DataSource.getInstance().getConnection(); 				
		}
    
    PreparedStatement insertStatement = null;

    try {
      Timestamp timestamp = new Timestamp(System.currentTimeMillis());
      credentialProvider.setCreated(timestamp);
      credentialProvider.setUpdated(timestamp);

      insertStatement = connection.prepareStatement(INSERT);

      insertStatement.setTimestamp(1, credentialProvider.getCreated());
      insertStatement.setTimestamp(2, credentialProvider.getUpdated());
      insertStatement.setInt(3, credentialProvider.getUpdatedById());
      insertStatement.setInt(4, credentialProvider.getDataSourceTypeValue());

      insertStatement.setString(5, credentialProvider.getCredentialProviderName());
      insertStatement.setString(6, credentialProvider.getDomainName());
      insertStatement.setString(7, credentialProvider.getAdminFirstName());
      insertStatement.setString(8, credentialProvider.getAdminLastName());
      insertStatement.setString(9, credentialProvider.getAdminEmail());

      insertStatement.setString(10, credentialProvider.getRetrieveCredentialMetaDataUrl());
      insertStatement.setString(11, credentialProvider.getCreateCredentialUrl());
      insertStatement.setString(12, credentialProvider.getDeleteCredentialUrl());
      insertStatement.setString(13, credentialProvider.getSignOnUrl());
      insertStatement.setString(14, credentialProvider.getCancelSignOnUrl());
      insertStatement.setString(15, credentialProvider.getRetrieveUnsignedDistributedLedgerUrl());
      insertStatement.setString(16, credentialProvider.getReturnSignedDistributedLedgerUrl());
      insertStatement.setString(17, credentialProvider.getSendFundsUrl());
      insertStatement.setString(18, credentialProvider.getReceiveFundsUrl());
      insertStatement.setString(19, credentialProvider.getAcceptFundsUrl());
      insertStatement.setString(20, credentialProvider.getConfirmFundsUrl());
      insertStatement.setString(21, credentialProvider.getRetrieveTransactionUuidUrl());
      insertStatement.setString(22, credentialProvider.getCredentialProviderUuid());

      insertStatement.execute();

    } catch (Exception e) {
      logger.error("", e);
      throw new RuntimeException(e);
    } finally {
      C3P0DataSource.getInstance().closeStatement(insertStatement);
      C3P0DataSource.getInstance().closeConnection(connection);
    }
  }

  // ------------------------------------------------------------------------------------------------------------------

  public static CredentialProvider retrieve(int credentialProviderId) {

    logger.info("###Entering");

    CredentialProvider credentialProvider = null;

    Connection connection = null;
    PreparedStatement retrieveStatement = null;
    ResultSet resultSet = null;

    try {
      connection = C3P0DataSource.getInstance().getConnection();

      retrieveStatement = connection.prepareStatement(RETRIEVE);

      retrieveStatement.setInt(1, credentialProviderId);

      resultSet = retrieveStatement.executeQuery();

      if (resultSet != null && resultSet.next()) {
        credentialProvider = loadFromResultSet(resultSet);
      }

      return credentialProvider;

    } catch (Exception e) {
      logger.error("", e);
      throw new RuntimeException(e);
    } finally {
      C3P0DataSource.getInstance().closeStatement(retrieveStatement);
      C3P0DataSource.getInstance().closeResultSet(resultSet);
      C3P0DataSource.getInstance().closeConnection(connection);
    }
  }

  // ------------------------------------------------------------------------------------------------------------------

  public static CredentialProvider retrieveByCredentialProviderUuid(String credentialProviderUuid, Connection connection) {

    logger.info("###Entering");
    
    if (connection == null) {
      connection = C3P0DataSource.getInstance().getConnection(); 				
		}

    CredentialProvider credentialProvider = null;

    PreparedStatement retrieveStatement = null;
    ResultSet resultSet = null;

    try {
      connection = C3P0DataSource.getInstance().getConnection();

      retrieveStatement = connection.prepareStatement(RETRIEVE_BY_CREDENTIAL_PROVIDER_UUID);

      retrieveStatement.setString(1, credentialProviderUuid);

      resultSet = retrieveStatement.executeQuery();

      if (resultSet != null && resultSet.next()) {
        credentialProvider = loadFromResultSet(resultSet);
      }

      return credentialProvider;

    } catch (Exception e) {
      logger.error("", e);
      throw new RuntimeException(e);
    } finally {
      C3P0DataSource.getInstance().closeStatement(retrieveStatement);
      C3P0DataSource.getInstance().closeResultSet(resultSet);
      C3P0DataSource.getInstance().closeConnection(connection);
    }
  }

  // ------------------------------------------------------------------------------------------------------------------

  public static CredentialProvider retrieveByCredentialType(String credentialType) {

    logger.info("###Entering");

    CredentialProvider credentialProvider = null;

    Connection connection = null;
    PreparedStatement retrieveStatement = null;
    ResultSet resultSet = null;

    try {
      connection = C3P0DataSource.getInstance().getConnection();

      retrieveStatement = connection.prepareStatement(RETRIEVE_BY_CREDENTIAL_TYPE);

      retrieveStatement.setString(1, credentialType);

      resultSet = retrieveStatement.executeQuery();

      if (resultSet != null && resultSet.next()) {
        credentialProvider = loadFromResultSet(resultSet);
      }

      return credentialProvider;

    } catch (Exception e) {
      logger.error("", e);
      throw new RuntimeException(e);
    } finally {
      C3P0DataSource.getInstance().closeStatement(retrieveStatement);
      C3P0DataSource.getInstance().closeResultSet(resultSet);
      C3P0DataSource.getInstance().closeConnection(connection);
    }
  }

  // ------------------------------------------------------------------------------------------------------------------

  public static String retrieveCredentialMetaDataNameValuePairs(Credential credential) {

    logger.info("###Entering");

    String credentialMetaDataNameValuePairs = null;  
    
    PublicPrivateKey publicPrivateKey = PublicPrivateKeyDao.retrieveByUuid(credential.getCredentialType().getPublicPrivateKeyUuid());

    if (publicPrivateKey != null) {

      credentialMetaDataNameValuePairs = Constants.CREDENTIAL_TYPE + "=" + credential.getCredentialType().getCredentialTypeName() + "&" + 
      		                               Constants.CREDENTIAL_DISPLAY_NAME + "=" + credential.getCredentialType().getDisplayName() + "&" +
                                         Constants.CREDENTIAL_PROVIDER_UUID + "=" + credential.getCredentialType().getCredentialProvider().getCredentialProviderUuid() + "&" + 
                                         Constants.CREDENTIAL_PROVIDER_NAME + "=" + credential.getCredentialType().getCredentialProvider().getCredentialProviderName() + "&" + 
                                         Constants.DOMAIN_NAME + "=" + credential.getCredentialType().getCredentialProvider().getDomainName() + "&" + 
                                         Constants.RETRIEVE_CREDENTIAL_META_DATA_URL + "=" + credential.getCredentialType().getCredentialProvider().getRetrieveCredentialMetaDataUrl() + "&" + 
                                         Constants.CREATE_CREDENTIAL_URL + "=" + credential.getCredentialType().getCredentialProvider().getCreateCredentialUrl() + "&" + 
                                         Constants.DELETE_CREDENTIAL_URL + "=" + credential.getCredentialType().getCredentialProvider().getDeleteCredentialUrl() + "&" + 
                                         Constants.SIGN_ON_URL + "=" + credential.getCredentialType().getCredentialProvider().getSignOnUrl() + "&" +  
                                         Constants.CANCEL_SIGN_ON_URL + "=" + credential.getCredentialType().getCredentialProvider().getCancelSignOnUrl() + "&" +  
                                         Constants.RETRIEVE_UNSIGNED_DISTRIBUTED_LEDGER_URL + "=" + credential.getCredentialType().getCredentialProvider().getRetrieveUnsignedDistributedLedgerUrl() + "&" +  
                                         Constants.RETURN_SIGNED_DISTRIBUTED_LEDGER_URL + "=" + credential.getCredentialType().getCredentialProvider().getReturnSignedDistributedLedgerUrl() + "&" + 
                                         Constants.SEND_FUNDS_URL + "=" + credential.getCredentialType().getCredentialProvider().getSendFundsUrl() + "&" + 
                                         Constants.RECEIVE_FUNDS_URL + "=" + credential.getCredentialType().getCredentialProvider().getReceiveFundsUrl() + "&" + 
                                         Constants.ACCEPT_FUNDS_URL + "=" + credential.getCredentialType().getCredentialProvider().getAcceptFundsUrl() + "&" + 
                                         Constants.CONFIRM_FUNDS_URL + "=" + credential.getCredentialType().getCredentialProvider().getConfirmFundsUrl() + "&" + 
                                         Constants.RETRIEVE_TRANSACTION_UUID_URL + "=" + credential.getCredentialType().getCredentialProvider().getRetrieveTransactionUuidUrl() + "&" +  

                                         Constants.PUBLIC_KEY_UUID + "=" + publicPrivateKey.getUuid() + "&" + 
                                         Constants.PUBLIC_KEY_HEX + "=" + publicPrivateKey.getPublicKeyHex() + "&" + 
                               
                                         Constants.CREDENTIAL_ICON_URL + "=" + credential.getCredentialType().getCredentialIconUrl() + "&";
    }

    return credentialMetaDataNameValuePairs;
  }

  // ------------------------------------------------------------------------------------------------------------------

  public static CredentialProvider loadFromResultSet(ResultSet resultSet) {

    try {
      CredentialProvider credentialProvider = new CredentialProvider();

      credentialProvider.setCredentialProviderId(resultSet.getInt("credentialProviderId"));
      credentialProvider.setCreated(resultSet.getTimestamp("created"));
      credentialProvider.setUpdated(resultSet.getTimestamp("updated"));
      credentialProvider.setUpdatedById(resultSet.getInt("updatedById"));
      credentialProvider.setDataSourceTypeValue(resultSet.getInt("dataSourceTypeValue"));

      credentialProvider.setCredentialProviderName(resultSet.getString("credentialProviderName"));
      credentialProvider.setDomainName(resultSet.getString("domainName"));
      credentialProvider.setAdminFirstName(resultSet.getString("adminFirstName"));
      credentialProvider.setAdminLastName(resultSet.getString("adminLastName"));
      credentialProvider.setAdminEmail(resultSet.getString("adminEmail"));

      credentialProvider.setRetrieveCredentialMetaDataUrl(resultSet.getString("retrieveCredentialMetaDataUrl"));
      credentialProvider.setCreateCredentialUrl(resultSet.getString("createCredentialUrl"));
      credentialProvider.setDeleteCredentialUrl(resultSet.getString("deleteCredentialUrl"));
      credentialProvider.setSignOnUrl(resultSet.getString("signOnUrl"));
      credentialProvider.setCancelSignOnUrl(resultSet.getString("cancelSignOnUrl"));
      credentialProvider.setRetrieveUnsignedDistributedLedgerUrl(resultSet.getString("retrieveUnsignedDistributedLedgerUrl"));
      credentialProvider.setReturnSignedDistributedLedgerUrl(resultSet.getString("returnSignedDistributedLedgerUrl"));
      credentialProvider.setSendFundsUrl(resultSet.getString("sendFundsUrl"));
      credentialProvider.setReceiveFundsUrl(resultSet.getString("receiveFundsUrl"));
      credentialProvider.setAcceptFundsUrl(resultSet.getString("acceptFundsUrl"));
      credentialProvider.setConfirmFundsUrl(resultSet.getString("confirmFundsUrl"));
      credentialProvider.setRetrieveTransactionUuidUrl(resultSet.getString("retrieveTransactionUuidUrl"));
      credentialProvider.setCredentialProviderUuid(resultSet.getString("credentialProviderUuid"));

      return credentialProvider;

    } catch (Exception e) {
      logger.error("", e);
      throw new RuntimeException(e);
    }
  }
}







