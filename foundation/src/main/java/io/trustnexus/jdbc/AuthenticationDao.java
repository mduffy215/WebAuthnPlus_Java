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

package io.trustnexus.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.trustnexus.model.Authentication;
import io.trustnexus.util.C3P0DataSource;
import io.trustnexus.util.Constants;

public class AuthenticationDao {

  private final static Logger logger = LogManager.getLogger(AuthenticationDao.class);

  public static final String INSERT 
                             = "INSERT Authentication (created, updated, updatedById, dataSourceTypeValue, "
                             + "serverSessionId, credentialProviderUuid, credentialType, credentialUuid, authenticationCode, "
                             + "verificationCode, userUuid, screenName, email, sessionUuid, "
                             + "remoteAddress, remoteHost, signOnUuid) "
                             + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

  public static final String UPDATE_FOR_SIGN_ON 
                             = "UPDATE Authentication SET updated = ?, "
                             + "verificationCode = ?, signOnUuid = ?, screenName = ?, email = ? WHERE authenticationId = ?";

  public static final String UPDATE_VERIFICATION_CODE 
                             = "UPDATE Authentication SET updated = ?, verificationCode = ? WHERE authenticationId = ?";

  public static final String CANCEL_AUTHENTICATION  
                             = "UPDATE Authentication SET updated = ?, verificationCode = ? " 
                             + "WHERE userUuid = ? AND sessionUuid = ?";

  public static final String INVALIDATE_VERIFICATION_CODES_BY_USER_UUID_AND_CREDENTIAL_TYPE //
                             = "UPDATE Authentication SET updated = ?, verificationCode = '" + Constants.SESSION_INVALIDATED 
                             + "' WHERE verificationCode IS NULL AND userUuid = ? AND credentialType = ?"; 

  private static final String RETRIEVE_BY_SESSION_UUID 
                              = "SELECT * FROM Authentication "
                              + "WHERE sessionUuid = ?";

  private static final String RETRIEVE_BY_SESSION_UUID_AND_SIGN_ON_UUID 
                              = "SELECT * FROM Authentication "
                              + "WHERE serverSessionId = ? AND sessionUuid = ? AND signOnUuid = ? "
                              + "AND UPDATED > DATE_SUB(NOW(), INTERVAL 20 MINUTE)";

  // ------------------------------------------------------------------------------------------------------------------
    
  public AuthenticationDao() {    
  }

  // ------------------------------------------------------------------------------------------------------------------

  public static void create(Authentication authentication) {

    logger.info("###Entering");
  
    Connection connection = null;
    PreparedStatement insertStatement = null;
  
    try {
      Timestamp timestamp = new Timestamp(System.currentTimeMillis());
      authentication.setCreated(timestamp);
      authentication.setUpdated(timestamp);
      
      connection = C3P0DataSource.getInstance().getConnection(); 
      
      insertStatement = connection.prepareStatement(INSERT);
      
      insertStatement.setTimestamp(1, authentication.getCreated());
      insertStatement.setTimestamp(2, authentication.getUpdated());
      insertStatement.setInt(3, authentication.getUpdatedById());
      insertStatement.setInt(4, authentication.getDataSourceTypeValue());

      insertStatement.setString(5, authentication.getServerSessionId());
      insertStatement.setString(6, authentication.getCredentialProviderUuid());
      insertStatement.setString(7, authentication.getCredentialType());
      insertStatement.setString(8, authentication.getCredentialUuid());
      insertStatement.setString(9, authentication.getAuthenticationCode());
      insertStatement.setString(10, authentication.getVerificationCode());
      insertStatement.setString(11, authentication.getUserUuid());
      insertStatement.setString(12, authentication.getScreenName());
      insertStatement.setString(13, authentication.getEmail());
      insertStatement.setString(14, authentication.getSessionUuid());
      insertStatement.setString(15, authentication.getRemoteAddress());
      insertStatement.setString(16, authentication.getRemoteHost());
      insertStatement.setString(17, authentication.getSignOnUuid());
      
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

  public static void updateForSignOn(Authentication authentication) {

    logger.info("###Entering");
  
    Connection connection = null;
    PreparedStatement updateStatement = null;
  
    try {       
      connection = C3P0DataSource.getInstance().getConnection(); 
        
      updateStatement = connection.prepareStatement(UPDATE_FOR_SIGN_ON);

      updateStatement.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
      updateStatement.setString(2, authentication.getVerificationCode());
      updateStatement.setString(3, authentication.getSignOnUuid());
      updateStatement.setString(4, authentication.getScreenName());
      updateStatement.setString(5, authentication.getEmail());
      updateStatement.setInt(6, authentication.getAuthenticationId());
        
      updateStatement.executeUpdate();
  
    } catch (Exception e) {
      logger.error("", e);
      throw new RuntimeException(e);
    } finally {
      
      C3P0DataSource.getInstance().closeConnection(connection);
      C3P0DataSource.getInstance().closeStatement(updateStatement);
    }  
  }

  // ------------------------------------------------------------------------------------------------------------------

  public static void updateVerificationCode(Authentication authentication) {

    logger.info("###Entering");
  
    Connection connection = null;
    PreparedStatement updateStatement = null;
  
    try {       
      connection = C3P0DataSource.getInstance().getConnection(); 
        
      updateStatement = connection.prepareStatement(UPDATE_VERIFICATION_CODE);

      updateStatement.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
      updateStatement.setString(2, authentication.getVerificationCode());
      updateStatement.setInt(3, authentication.getAuthenticationId());
        
      updateStatement.executeUpdate();
  
    } catch (Exception e) {
      logger.error("", e);
      throw new RuntimeException(e);
    } finally {
      
      C3P0DataSource.getInstance().closeConnection(connection);
      C3P0DataSource.getInstance().closeStatement(updateStatement);
    }  
  }

  // ------------------------------------------------------------------------------------------------------------------

  public static void cancelAuthentication(String userUuid, String sessionUuid) {

    logger.info("###Entering");

    Connection connection = null;
    PreparedStatement updateStatement = null;

    try {
      connection = C3P0DataSource.getInstance().getConnection();

      updateStatement = connection.prepareStatement(CANCEL_AUTHENTICATION);
      
      updateStatement.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
      updateStatement.setString(2, Constants.CANCELED);
      updateStatement.setString(3, userUuid);
      updateStatement.setString(4, sessionUuid);
      updateStatement.executeUpdate();

    } catch (Exception e) {
      logger.error("", e);
      throw new RuntimeException(e);
    } finally {
      C3P0DataSource.getInstance().closeStatement(updateStatement);
      C3P0DataSource.getInstance().closeConnection(connection);
    }
  }

  // ------------------------------------------------------------------------------------------------------------------

  public static void invalidateVerificationCodesByUserUuidAndCredentialType(String userUuid, String credentialType) {

    logger.info("###Entering");
  
    Connection connection = null;
    PreparedStatement updateStatement = null;
  
    try {       
      connection = C3P0DataSource.getInstance().getConnection(); 
        
      updateStatement = connection.prepareStatement(INVALIDATE_VERIFICATION_CODES_BY_USER_UUID_AND_CREDENTIAL_TYPE);

      updateStatement.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
      updateStatement.setString(2, userUuid);
      updateStatement.setString(3, credentialType);
        
      updateStatement.executeUpdate();
  
    } catch (Exception e) {
      logger.error("", e);
      throw new RuntimeException(e);
    } finally {
      
      C3P0DataSource.getInstance().closeConnection(connection);
      C3P0DataSource.getInstance().closeStatement(updateStatement);
    }  
  }
  
  // ------------------------------------------------------------------------------------------------------------------

  public static Authentication retrieveBySessionUuid(String sessionUuid) {

    logger.info("###Entering");
    
    Authentication authentication = null;
  
    Connection connection = null;
    PreparedStatement retrieveStatement = null;
    ResultSet resultSet = null;
  
    try {
      connection = C3P0DataSource.getInstance().getConnection(); 
      
      retrieveStatement = connection.prepareStatement(RETRIEVE_BY_SESSION_UUID);
      
      retrieveStatement.setString(1, sessionUuid);
      
      resultSet = retrieveStatement.executeQuery(); 
      
      if (resultSet != null && resultSet.next()) {
        authentication = loadFromResultSet(resultSet);
      }
      
      return authentication;
  
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

  public static Authentication retrieveBySessionUuidAndSignOnUuid(String serverSessionId, String sessionUuid, String signOnUuid) {

    logger.info("###Entering");
    
    Authentication authentication = null;
  
    Connection connection = null;
    PreparedStatement retrieveStatement = null;
    ResultSet resultSet = null;
  
    try {
      connection = C3P0DataSource.getInstance().getConnection(); 
      
      retrieveStatement = connection.prepareStatement(RETRIEVE_BY_SESSION_UUID_AND_SIGN_ON_UUID);

      retrieveStatement.setString(1, serverSessionId);
      retrieveStatement.setString(2, sessionUuid);
      retrieveStatement.setString(3, signOnUuid);
      
      resultSet = retrieveStatement.executeQuery();
      
      if (resultSet != null && resultSet.next()) {
        authentication = loadFromResultSet(resultSet);
      }
      
      return authentication;
  
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
  
  private static Authentication loadFromResultSet(ResultSet resultSet) {
    
    try {
      Authentication authentication = new Authentication();
      
      authentication.setAuthenticationId(resultSet.getInt("authenticationId")); 
      authentication.setCreated(resultSet.getTimestamp("created"));
      authentication.setUpdated(resultSet.getTimestamp("updated"));
      authentication.setUpdatedById(resultSet.getInt("updatedById"));
      authentication.setDataSourceTypeValue(resultSet.getInt("dataSourceTypeValue"));

      authentication.setCredentialProviderUuid(resultSet.getString("credentialProviderUuid"));
      authentication.setCredentialType(resultSet.getString("credentialType"));
      authentication.setCredentialUuid(resultSet.getString("credentialUuid"));
      authentication.setAuthenticationCode(resultSet.getString("authenticationCode"));
      authentication.setVerificationCode(resultSet.getString("verificationCode"));
      authentication.setUserUuid(resultSet.getString("userUuid"));
      authentication.setScreenName(resultSet.getString("screenName"));
      authentication.setEmail(resultSet.getString("email"));
      authentication.setSessionUuid(resultSet.getString("sessionUuid"));
      authentication.setRemoteAddress(resultSet.getString("remoteAddress"));
      authentication.setRemoteHost(resultSet.getString("remoteHost"));
      authentication.setSignOnUuid(resultSet.getString("signOnUuid"));
      
      return authentication;
      
    } catch (Exception e) {
      logger.error("", e);
      throw new RuntimeException(e);
    }
  }
}







