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

package io.trustnexus.jdbc.mobileapp;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.trustnexus.model.mobileapp.User;
import io.trustnexus.util.C3P0DataSource;

public class UserDao {

  private final static Logger logger = LogManager.getLogger(UserDao.class);

  public static final String INSERT 
                             = "INSERT User (created, updated, updatedById, dataSourceTypeValue, " 
                             + "screenName, email, phone, firstName, lastName, userUuid, publicKey, inactiveFlag, refCode, firebaseDeviceId) " 
                             + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

  public static final String UPDATE
                             = "UPDATE User SET updated = ?, updatedById = ?, dataSourceTypeValue = ?, screenName = ?, email = ?, phone = ?, " 
                             + "firstName = ?, lastName = ?, userUuid = ?, publicKey = ?, inactiveFlag = ?, refCode = ?, firebaseDeviceId = ? " 
                             + "WHERE userId = ?";

  private static final String RETRIEVE_BY_EMAIL 
                              = "SELECT * FROM User "
                              + "WHERE email = ?";

  private static final String RETRIEVE_BY_USER_UUID 
                              = "SELECT * FROM User "
                              + "WHERE userUuid = ?";

  private static final String RETRIEVE_BY_CREDENTIAL_UUID 
                              = "SELECT * FROM User U "
                              + "JOIN Credential CRED ON U.userUuid = CRED.userUuid "
                              + "WHERE CRED.credentialUuid = ?";

  private static final String RETRIEVE_BY_AUTHENTICATION_SESSION_UUID 
                              = "SELECT * FROM User U "
                              + "JOIN Authentication AUTH ON U.userUuid = AUTH.userUuid "
                              + "WHERE AUTH.sessionUuid = ?";

  private static final String RETRIEVE_BY_CREDENTIAL_SESSION_UUID 
                              = "SELECT * FROM User U "
                              + "JOIN Credential CRED ON U.userUuid = CRED.userUuid "
                              + "WHERE CRED.sessionUuid = ?";

  private static final String RETRIEVE_BY_DISTRIBUTED_LEDGER_SESSION_UUID 
                              = "SELECT * FROM User U "
                              + "JOIN DistributedLedger DL ON U.userUuid = DL.userUuid "
                              + "WHERE DL.sessionUuid = ?";

  private static final String RETRIEVE_BY_REF_CODE 
                              = "SELECT * FROM User "
                              + "WHERE refCode = ?";

  public static final String UPDATE_INACTIVE_FLAG
                             = "UPDATE User SET inactiveFlag = ? WHERE refCode = ?";

  private static final String DELETE_BY_USER_ID = "DELETE FROM User " 
                                                  + "WHERE userId = ?";


  // ------------------------------------------------------------------------------------------------------------------
  
  public UserDao() {    
  }

  // ------------------------------------------------------------------------------------------------------------------

  public static void create(User user) {

    logger.info("###Entering");
  
    Connection connection = null;
    PreparedStatement insertStatement = null;
  
    try {
      Timestamp timestamp = new Timestamp(System.currentTimeMillis());
      user.setCreated(timestamp);
      user.setUpdated(timestamp);
      
      connection = C3P0DataSource.getInstance().getConnection(); 
      
      insertStatement = connection.prepareStatement(INSERT);
      
      insertStatement.setTimestamp(1, user.getCreated());
      insertStatement.setTimestamp(2, user.getUpdated());
      insertStatement.setInt(3, user.getUpdatedById());
      insertStatement.setInt(4, user.getDataSourceTypeValue());
      
      insertStatement.setString(5, user.getScreenName());
      insertStatement.setString(6, user.getEmail());
      insertStatement.setString(7, user.getPhone());
      insertStatement.setString(8, user.getFirstName());
      insertStatement.setString(9, user.getLastName());
      insertStatement.setString(10, user.getUserUuid());
      insertStatement.setString(11, user.getPublicKey());
      insertStatement.setBoolean(12, user.isInactiveFlag());
      insertStatement.setString(13, user.getRefCode());
      insertStatement.setString(14, user.getFirebaseDeviceId());
      
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

  public static void update(User user) {

    logger.info("###Entering");
  
    Connection connection = null;
    PreparedStatement insertStatement = null;
  
    try {
      Timestamp timestamp = new Timestamp(System.currentTimeMillis());
      user.setUpdated(timestamp);
      
      connection = C3P0DataSource.getInstance().getConnection(); 
      
      insertStatement = connection.prepareStatement(UPDATE);
      
      insertStatement.setTimestamp(1, user.getUpdated());
      insertStatement.setInt(2, user.getUpdatedById());
      insertStatement.setInt(3, user.getDataSourceTypeValue());
      
      insertStatement.setString(4, user.getScreenName());
      insertStatement.setString(5, user.getEmail());
      insertStatement.setString(6, user.getPhone());
      insertStatement.setString(7, user.getFirstName());
      insertStatement.setString(8, user.getLastName());
      insertStatement.setString(9, user.getUserUuid());
      insertStatement.setString(10, user.getPublicKey());
      insertStatement.setBoolean(11, user.isInactiveFlag());
      insertStatement.setString(12, user.getRefCode());
      insertStatement.setString(13, user.getFirebaseDeviceId());

      insertStatement.setInt(14, user.getUserId());
      
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

  public static User retrieveByEmail(String email) {

    logger.info("###Entering");
    
    User user = null;
  
    Connection connection = null;
    PreparedStatement retrieveStatement = null;
    ResultSet resultSet = null;
  
    try {
      connection = C3P0DataSource.getInstance().getConnection(); 
      
      retrieveStatement = connection.prepareStatement(RETRIEVE_BY_EMAIL);
      
      retrieveStatement.setString(1, email);
      
      resultSet = retrieveStatement.executeQuery(); 
      
      if (resultSet != null && resultSet.next()) {
        user = loadFromResultSet(resultSet);
      }
      
      return user;
  
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

  public static User retrieveByUserUuid(String userUuid) {

    logger.info("###Entering");
    
    User user = null;
  
    Connection connection = null;
    PreparedStatement retrieveStatement = null;
    ResultSet resultSet = null;
  
    try {
      connection = C3P0DataSource.getInstance().getConnection(); 
      
      retrieveStatement = connection.prepareStatement(RETRIEVE_BY_USER_UUID);
      
      retrieveStatement.setString(1, userUuid);
      
      resultSet = retrieveStatement.executeQuery(); 
      
      if (resultSet != null && resultSet.next()) {
        user = loadFromResultSet(resultSet);
      }
      
      return user;
  
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

  public static User retrieveByCredentialUuid(String credentialUuid) {

    logger.info("###Entering");
    
    User user = null;
  
    Connection connection = null;
    PreparedStatement retrieveStatement = null;
    ResultSet resultSet = null;
  
    try {
      connection = C3P0DataSource.getInstance().getConnection(); 
      
      retrieveStatement = connection.prepareStatement(RETRIEVE_BY_CREDENTIAL_UUID);
      
      retrieveStatement.setString(1, credentialUuid);
      
      resultSet = retrieveStatement.executeQuery(); 
      
      if (resultSet != null && resultSet.next()) {
        user = loadFromResultSet(resultSet);
      }
      
      return user;
  
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

  public static User retrieveByAuthenticationSessionUuid(String sessionUuid) {

    logger.info("###Entering");
    
    User user = null;
  
    Connection connection = null;
    PreparedStatement retrieveStatement = null;
    ResultSet resultSet = null;
  
    try {
      connection = C3P0DataSource.getInstance().getConnection(); 
      
      retrieveStatement = connection.prepareStatement(RETRIEVE_BY_AUTHENTICATION_SESSION_UUID);
      
      retrieveStatement.setString(1, sessionUuid);
      
      resultSet = retrieveStatement.executeQuery(); 
      
      if (resultSet != null && resultSet.next()) {
        user = loadFromResultSet(resultSet);
      }
      
      return user;
  
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

  public static User retrieveByCredentialSessionUuid(String sessionUuid) {

    logger.info("###Entering");
    
    User user = null;
  
    Connection connection = null;
    PreparedStatement retrieveStatement = null;
    ResultSet resultSet = null;
  
    try {
      connection = C3P0DataSource.getInstance().getConnection(); 
      
      retrieveStatement = connection.prepareStatement(RETRIEVE_BY_CREDENTIAL_SESSION_UUID);
      
      retrieveStatement.setString(1, sessionUuid);
      
      resultSet = retrieveStatement.executeQuery(); 
      
      if (resultSet != null && resultSet.next()) {
        user = loadFromResultSet(resultSet);
      }
      
      return user;
  
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

  public static User retrieveByDistributedLedgerSessionUuid(String sessionUuid) {

    logger.info("###Entering");
    
    User user = null;
  
    Connection connection = null;
    PreparedStatement retrieveStatement = null;
    ResultSet resultSet = null;
  
    try {
      connection = C3P0DataSource.getInstance().getConnection(); 
      
      retrieveStatement = connection.prepareStatement(RETRIEVE_BY_DISTRIBUTED_LEDGER_SESSION_UUID);
      
      retrieveStatement.setString(1, sessionUuid);
      
      resultSet = retrieveStatement.executeQuery(); 
      
      if (resultSet != null && resultSet.next()) {
        user = loadFromResultSet(resultSet);
      }
      
      return user;
  
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

  public static User retrieveByRefCode(String refCode) {

    logger.info("###Entering");
    
    User user = null;
  
    Connection connection = null;
    PreparedStatement retrieveStatement = null;
    ResultSet resultSet = null;
  
    try {
      connection = C3P0DataSource.getInstance().getConnection(); 
      
      retrieveStatement = connection.prepareStatement(RETRIEVE_BY_REF_CODE);
      
      retrieveStatement.setString(1, refCode);
      
      resultSet = retrieveStatement.executeQuery(); 
      
      if (resultSet != null && resultSet.next()) {
        user = loadFromResultSet(resultSet);
      }
      
      return user;
  
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
  
  private static User loadFromResultSet(ResultSet resultSet) {
    
    try {
      User user = new User();
      
      user.setUserId(resultSet.getInt("userId")); 
      user.setCreated(resultSet.getTimestamp("created"));
      user.setUpdated(resultSet.getTimestamp("updated"));
      user.setUpdatedById(resultSet.getInt("updatedById"));
      user.setDataSourceTypeValue(resultSet.getInt("dataSourceTypeValue"));
      
      user.setScreenName(resultSet.getString("screenName"));
      user.setEmail(resultSet.getString("email"));
      user.setPhone(resultSet.getString("phone"));
      user.setFirstName(resultSet.getString("firstName"));
      user.setLastName(resultSet.getString("lastName"));
      user.setEmail(resultSet.getString("email"));
      user.setUserUuid(resultSet.getString("userUuid"));
      user.setPublicKey(resultSet.getString("publicKey"));
      user.setInactiveFlag(resultSet.getBoolean("inactiveFlag"));
      user.setRefCode(resultSet.getString("refCode"));
      user.setFirebaseDeviceId(resultSet.getString("firebaseDeviceId"));
      
      return user;
      
    } catch (Exception e) {
      logger.error("", e);
      throw new RuntimeException(e);
    }
  }

  // ------------------------------------------------------------------------------------------------------------------

  public static void updateInactiveFlag(boolean inactiveFlag, String refCode) {

    logger.info("###Entering");
  
    Connection connection = null;
    PreparedStatement updateStatement = null;
  
    try {       
      connection = C3P0DataSource.getInstance().getConnection(); 
      
      updateStatement = connection.prepareStatement(UPDATE_INACTIVE_FLAG);
        
      updateStatement.setBoolean(1, inactiveFlag);
      updateStatement.setString(2, refCode);
        
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

  public static void deleteByUserUuid(String userUuid) {

    logger.info("###Entering");
  	
  	User user = retrieveByUserUuid(userUuid);
  	if (user != null) {			
    	deleteByUserId(user.getUpdatedById());
		}
  }

  // ------------------------------------------------------------------------------------------------------------------

  public static void deleteByUserId(int userId) {

    logger.info("###Entering");
  	
  	UserAddressDao.deleteByUserId(userId); 

    Connection connection = null;
    PreparedStatement deleteStatement = null;

    try {
      connection = C3P0DataSource.getInstance().getConnection();

      deleteStatement = connection.prepareStatement(DELETE_BY_USER_ID);
      deleteStatement.setInt(1, userId);
      deleteStatement.executeUpdate();

    } catch (Exception e) {
      logger.error("", e);
      throw new RuntimeException(e);
    } finally {
      C3P0DataSource.getInstance().closeStatement(deleteStatement);
      C3P0DataSource.getInstance().closeConnection(connection);
    }
  }
}







