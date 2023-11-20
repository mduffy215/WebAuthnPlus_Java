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

package io.trustnexus.jdbc; 

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.trustnexus.model.TrustedSystem;
import io.trustnexus.util.C3P0DataSource;

public class TrustedSystemDao {

  private final static Logger logger = LogManager.getLogger(TrustedSystemDao.class);

  public static final String INSERT 
                             = "INSERT TrustedSystem (created, updated, updatedById, dataSourceTypeValue, " 
                             + "email, userUuid, trustedSystemUuid, passwordHash, credentialProviderUuid, credentialType, credentialUuid) " 
                             + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

  public static final String DELETE_BY_TRUSTED_SYSTEM_UUID = "DELETE FROM TrustedSystem " 
                                                           + "WHERE trustedSystemUuid = ?";

  public static final String RETRIEVE_BY_EMAIL_AND_TRUSTED_SYSTEM_UUID 
                             = "SELECT * FROM TrustedSystem "
                             + "WHERE email = ? AND trustedSystemUuid = ?";

  // ------------------------------------------------------------------------------------------------------------------

  public TrustedSystemDao() {
  }
  
  // ------------------------------------------------------------------------------------------------------------------

  public static void create(TrustedSystem trustedSystem) {

    logger.info("###Entering");
    
    Connection connection = C3P0DataSource.getInstance().getConnection();
  
    PreparedStatement insertStatement = null;
  
    try {
      Timestamp timestamp = new Timestamp(System.currentTimeMillis());
      trustedSystem.setCreated(timestamp);
      trustedSystem.setUpdated(timestamp);
      
      insertStatement = connection.prepareStatement(INSERT);
      
      insertStatement.setTimestamp(1, trustedSystem.getCreated());
      insertStatement.setTimestamp(2, trustedSystem.getUpdated());
      insertStatement.setInt(3, trustedSystem.getUpdatedById());
      insertStatement.setInt(4, trustedSystem.getDataSourceTypeValue());

      insertStatement.setString(5, trustedSystem.getEmail());
      insertStatement.setString(6, trustedSystem.getUserUuid());
      insertStatement.setString(7, trustedSystem.getTrustedSystemUuid());
      insertStatement.setString(8, trustedSystem.getPasswordHash());
      insertStatement.setString(9, trustedSystem.getCredentialProviderUuid());
      insertStatement.setString(10, trustedSystem.getCredentialType());
      insertStatement.setString(11, trustedSystem.getCredentialUuid());
      
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

  public static void deleteByTrustedSystemUuid(String trustedSystemUuid) {

    logger.info("###Entering");

    Connection connection = null;
    PreparedStatement deleteStatement = null;

    try {
      connection = C3P0DataSource.getInstance().getConnection();

      deleteStatement = connection.prepareStatement(DELETE_BY_TRUSTED_SYSTEM_UUID);
      deleteStatement.setString(1, trustedSystemUuid);
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

  public static TrustedSystem retrieveByEmailAndTrustedSystemUuid(String email, String trustedSystemUuid) {

    logger.info("###Entering");
    
    TrustedSystem trustedSystemObject = null;
  
    Connection connection = null;
    PreparedStatement retrieveStatement = null;
    ResultSet resultSet = null;
  
    try {
      connection = C3P0DataSource.getInstance().getConnection(); 
      
      retrieveStatement = connection.prepareStatement(RETRIEVE_BY_EMAIL_AND_TRUSTED_SYSTEM_UUID);
      
      retrieveStatement.setString(1, email);
      retrieveStatement.setString(2, trustedSystemUuid);
       
      resultSet = retrieveStatement.executeQuery(); 
      
      if (resultSet != null && resultSet.next()) {
        trustedSystemObject = loadFromResultSet(resultSet);
      }
      
      return trustedSystemObject;
  
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
  
  public static TrustedSystem loadFromResultSet(ResultSet resultSet) {
    
    try {
      TrustedSystem trustedSystem = new TrustedSystem();
      
      trustedSystem.setTrustedSystemId(resultSet.getInt("trustedSystemId")); 
      trustedSystem.setCreated(resultSet.getTimestamp("created"));
      trustedSystem.setUpdated(resultSet.getTimestamp("updated"));
      trustedSystem.setUpdatedById(resultSet.getInt("updatedById"));
      trustedSystem.setDataSourceTypeValue(resultSet.getInt("dataSourceTypeValue"));
      
      trustedSystem.setEmail(resultSet.getString("email"));
      trustedSystem.setUserUuid(resultSet.getString("userUuid"));
      trustedSystem.setTrustedSystemUuid(resultSet.getString("trustedSystemUuid"));
      trustedSystem.setPasswordHash(resultSet.getString("passwordHash"));
      trustedSystem.setCredentialProviderUuid(resultSet.getString("credentialProviderUuid"));
      trustedSystem.setCredentialType(resultSet.getString("credentialType"));
      trustedSystem.setCredentialUuid(resultSet.getString("credentialUuid"));
      
      return trustedSystem;
      
    } catch (Exception e) {
      logger.error("", e);
      throw new RuntimeException(e);
    }
  }
}







