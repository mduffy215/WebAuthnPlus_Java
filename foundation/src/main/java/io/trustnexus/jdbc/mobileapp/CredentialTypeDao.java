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

import io.trustnexus.model.mobileapp.CredentialProvider;
import io.trustnexus.model.mobileapp.CredentialType;
import io.trustnexus.util.C3P0DataSource;

public class CredentialTypeDao {

  private final static Logger logger = LogManager.getLogger(CredentialTypeDao.class);

  public static final String INSERT 
                             = "INSERT CredentialType (created, updated, updatedById, dataSourceTypeValue, " 
                             + "credentialProviderUuid, publicPrivateKeyUuid, credentialType, displayName, credentialIconUrl, expirationMonths) " 
                             + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

  public static final String RETRIEVE = "SELECT * FROM CredentialType "
                                      + "WHERE credentialTypeId = ?";

  public static final String RETRIEVE_BY_CREDENTIAL_TYPE 
                             = "SELECT * FROM CredentialType "
                             + "WHERE credentialType = ?";

  private static final String DELETE_BY_CREDENTIAL_PROVIDER_ID_AND_CREDENTIAL_TYPE  
                              = "DELETE FROM CredentialType "
                              + "WHERE credentialProviderId = ? AND credentialType = ?";

  // ------------------------------------------------------------------------------------------------------------------
  
  public CredentialTypeDao() {    
  }

  // ------------------------------------------------------------------------------------------------------------------

  public static void create(CredentialType credentialType, Connection connection) {

    logger.info("###Entering");
    
    if (connection == null) {
      connection = C3P0DataSource.getInstance().getConnection(); 				
		}
    
    PreparedStatement insertStatement = null;
  
    try {
      Timestamp timestamp = new Timestamp(System.currentTimeMillis());
      credentialType.setCreated(timestamp);
      credentialType.setUpdated(timestamp);
      
      insertStatement = connection.prepareStatement(INSERT);
      
      insertStatement.setTimestamp(1, credentialType.getCreated());
      insertStatement.setTimestamp(2, credentialType.getUpdated());
      insertStatement.setInt(3, credentialType.getUpdatedById());
      insertStatement.setInt(4, credentialType.getDataSourceTypeValue());

      insertStatement.setString(5, credentialType.getCredentialProviderUuid());
      insertStatement.setString(6, credentialType.getPublicPrivateKeyUuid());
      insertStatement.setString(7, credentialType.getCredentialTypeName());
      insertStatement.setString(8, credentialType.getDisplayName());
      insertStatement.setString(9, credentialType.getCredentialIconUrl());
      insertStatement.setInt(10, credentialType.getExpirationMonths());
      
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

  public static CredentialType retrieve(int credentialTypeId) {

    logger.info("###Entering");
    
    CredentialType credentialTypeObject = null;
  
    Connection connection = null;
    PreparedStatement retrieveStatement = null;
    ResultSet resultSet = null;
  
    try {      
      connection = C3P0DataSource.getInstance().getConnection(); 
      
      retrieveStatement = connection.prepareStatement(RETRIEVE);
      
      retrieveStatement.setInt(1, credentialTypeId);
      
      resultSet = retrieveStatement.executeQuery(); 
      
      if (resultSet != null && resultSet.next()) {
        credentialTypeObject = loadFromResultSet(resultSet, connection);
      }
      
      return credentialTypeObject;
  
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

  public static CredentialType retrieveByCredentialType(String credentialType) {

    logger.info("###Entering");
    
    Connection connection = C3P0DataSource.getInstance().getConnection();  
    
    CredentialType credentialTypeObject = null;
  
    PreparedStatement retrieveStatement = null;
    ResultSet resultSet = null;
  
    try {      
      retrieveStatement = connection.prepareStatement(RETRIEVE_BY_CREDENTIAL_TYPE);
      
      retrieveStatement.setString(1, credentialType);
      
      resultSet = retrieveStatement.executeQuery(); 
      
      if (resultSet != null && resultSet.next()) {
        credentialTypeObject = loadFromResultSet(resultSet, connection);
      }
      
      return credentialTypeObject;
  
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

  public static CredentialType retrieveByCredentialType(String credentialType, Connection connection) {

    logger.info("###Entering");
    
    CredentialType credentialTypeObject = null;
  
    PreparedStatement retrieveStatement = null;
    ResultSet resultSet = null;
  
    try {      
      retrieveStatement = connection.prepareStatement(RETRIEVE_BY_CREDENTIAL_TYPE);
      
      retrieveStatement.setString(1, credentialType);
      
      resultSet = retrieveStatement.executeQuery(); 
      
      if (resultSet != null && resultSet.next()) {
        credentialTypeObject = loadFromResultSet(resultSet, connection);
      }
      
      return credentialTypeObject;
  
    } catch (Exception e) {
      logger.error("", e);
      throw new RuntimeException(e);
    } finally {
      C3P0DataSource.getInstance().closeStatement(retrieveStatement);
      C3P0DataSource.getInstance().closeResultSet(resultSet);
    }    
  }
  
  // ------------------------------------------------------------------------------------------------------------------

  public static void deleteByCredentialProviderIdAndCredentialType(Integer credentialProviderId, String credentialType) {

    logger.info("###Entering");
  
    Connection connection = null;
    PreparedStatement deleteStatement = null;
  
    try {
      connection = C3P0DataSource.getInstance().getConnection(); 
      
      deleteStatement = connection.prepareStatement(DELETE_BY_CREDENTIAL_PROVIDER_ID_AND_CREDENTIAL_TYPE); 
      deleteStatement.setString(2, credentialType);
      deleteStatement.executeUpdate(); 
  
    } catch (Exception e) {
      logger.error("", e);
      throw new RuntimeException(e);
    } finally {
      C3P0DataSource.getInstance().closeStatement(deleteStatement);
      C3P0DataSource.getInstance().closeConnection(connection);
    }    
  }

  // ------------------------------------------------------------------------------------------------------------------
  
  public static CredentialType loadFromResultSet(ResultSet resultSet, Connection connection) {
    
    try {
      CredentialType credentialType = new CredentialType();
      
      credentialType.setCredentialTypeId(resultSet.getInt("credentialTypeId")); 
      credentialType.setCreated(resultSet.getTimestamp("created"));
      credentialType.setUpdated(resultSet.getTimestamp("updated"));
      credentialType.setUpdatedById(resultSet.getInt("updatedById"));
      credentialType.setDataSourceTypeValue(resultSet.getInt("dataSourceTypeValue"));
      
      credentialType.setCredentialProviderUuid(resultSet.getString("credentialProviderUuid"));
      credentialType.setPublicPrivateKeyUuid(resultSet.getString("publicPrivateKeyUuid"));
      credentialType.setCredentialTypeName(resultSet.getString("credentialType"));
      credentialType.setDisplayName(resultSet.getString("displayName"));
      credentialType.setCredentialIconUrl(resultSet.getString("credentialIconUrl"));
      credentialType.setExpirationMonths(resultSet.getInt("expirationMonths"));
      
      CredentialProvider credentialProvider = CredentialProviderDao.retrieveByCredentialProviderUuid(credentialType.getCredentialProviderUuid(), connection);
      credentialType.setCredentialProvider(credentialProvider); 
      
      return credentialType;
      
    } catch (Exception e) {
      logger.error("", e);
      throw new RuntimeException(e);
    }
  }
}







