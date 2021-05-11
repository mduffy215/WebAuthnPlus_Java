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
import java.util.HashSet;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.trustnexus.model.UserSessionTracking;
import io.trustnexus.util.C3P0DataSource;
import io.trustnexus.util.Constants;

public class UserSessionTrackingDao {

  private final static Logger logger = LogManager.getLogger(UserSessionTrackingDao.class);

  public static final String INSERT 
                             = "INSERT UserSessionTracking (created, updated, updatedById, dataSourceTypeValue, " 
                             + "userIdentifier, sessionIdentifier) " 
                             + "VALUES (?, ?, ?, ?, ?, ?)";

  public static final String UPDATE 
                             = "UPDATE UserSessionTracking SET updated = ?, updatedById = ?, dataSourceTypeValue = ?, " 
                             + "userIdentifier = ? " 
                             + "WHERE userSessionTrackingId = ?";

  public static final String UPDATE_USER_IDENTIFER 
                             = "UPDATE UserSessionTracking SET userIdentifier = ? " 
                             + "WHERE userSessionTrackingId = ?";

  private static final String RETRIEVE_BY_SESSION_IDENTIFIER 
                              = "SELECT * FROM UserSessionTracking "
                              + "WHERE sessionIdentifier = ?";

  private static final String RETRIEVE_BY_USER_IDENTIFIER 
                              = "SELECT * FROM UserSessionTracking "
                              + "WHERE userIdentifier = ? "
                              + "AND UPDATED > DATE_SUB(NOW(), INTERVAL 5 MINUTE)";

  // ------------------------------------------------------------------------------------------------------------------
  
  public UserSessionTrackingDao() {    
  }

  // ------------------------------------------------------------------------------------------------------------------

  public static void create(UserSessionTracking userSessionTracking) {

    logger.info("###Entering");
  
    Connection connection = null;
    PreparedStatement insertStatement = null;
  
    try {
      Timestamp timestamp = new Timestamp(System.currentTimeMillis());
      userSessionTracking.setCreated(timestamp);
      userSessionTracking.setUpdated(timestamp);
      
      connection = C3P0DataSource.getInstance().getConnection(); 
      
      insertStatement = connection.prepareStatement(INSERT);
      
      insertStatement.setTimestamp(1, userSessionTracking.getCreated());
      insertStatement.setTimestamp(2, userSessionTracking.getUpdated());
      insertStatement.setInt(3, userSessionTracking.getUpdatedById());
      insertStatement.setInt(4, userSessionTracking.getDataSourceTypeValue());
      
      insertStatement.setString(5, userSessionTracking.getUserIdentifier());
      insertStatement.setString(6, userSessionTracking.getSessionIdentifier());
      
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

  public static int createForMdc(String sessionIdentifer) {

    logger.info("###Entering");
  	
  	UserSessionTracking userSessionTracking = retrieveBySessionIdentifier(sessionIdentifer);
  	
  	if (userSessionTracking != null) {
			return userSessionTracking.getUserSessionTrackingId();
		} else {
			
			userSessionTracking = new UserSessionTracking();
		  
	    Connection connection = null;
	    PreparedStatement insertStatement = null;
	  
	    try {
	      Timestamp timestamp = new Timestamp(System.currentTimeMillis());
	      
	      connection = C3P0DataSource.getInstance().getConnection(); 
	      
	      insertStatement = connection.prepareStatement(INSERT, PreparedStatement.RETURN_GENERATED_KEYS);
	      
	      insertStatement.setTimestamp(1, timestamp);
	      insertStatement.setTimestamp(2, timestamp);
	      insertStatement.setInt(3, Constants.MASTER_USER);
	      insertStatement.setInt(4, Constants.DATA_SOURCE_APPLICATION);
	      
	      insertStatement.setString(5, null);
	      insertStatement.setString(6, sessionIdentifer);
	      
	      insertStatement.execute();
	      ResultSet resultSet = insertStatement.getGeneratedKeys();
	      resultSet.next();
	      
	      return resultSet.getInt(1);
	  
	    } catch (Exception e) {
	      logger.error("", e);
	      throw new RuntimeException(e);
	    } finally {
	      C3P0DataSource.getInstance().closeStatement(insertStatement);
	      C3P0DataSource.getInstance().closeConnection(connection);
	    }
		}
  }

  // ------------------------------------------------------------------------------------------------------------------

  public static void update(UserSessionTracking userSessionTracking) {

    logger.info("###Entering");
  
    Connection connection = null;
    PreparedStatement insertStatement = null;
  
    try {
      Timestamp timestamp = new Timestamp(System.currentTimeMillis());
      userSessionTracking.setUpdated(timestamp);
      
      connection = C3P0DataSource.getInstance().getConnection(); 
      
      insertStatement = connection.prepareStatement(UPDATE);
      
      insertStatement.setTimestamp(1, userSessionTracking.getUpdated());
      insertStatement.setInt(2, userSessionTracking.getUpdatedById());
      insertStatement.setInt(3, userSessionTracking.getDataSourceTypeValue());
      
      insertStatement.setString(4, userSessionTracking.getUserIdentifier());

      insertStatement.setInt(5, userSessionTracking.getUserSessionTrackingId());
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

  public static void updateUserIdentifier(String userIdentifier, int userSessionTrackingId) {

    logger.info("###Entering");
  
    Connection connection = null;
    PreparedStatement insertStatement = null;
  
    try {
      
      connection = C3P0DataSource.getInstance().getConnection(); 
      
      insertStatement = connection.prepareStatement(UPDATE_USER_IDENTIFER);
      
      insertStatement.setString(1, userIdentifier);
      insertStatement.setInt(2, userSessionTrackingId);
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

  public static UserSessionTracking retrieveBySessionIdentifier(String sessionIdentifier) {

    logger.info("###Entering");
    
    UserSessionTracking userSessionTracking = null;
  
    Connection connection = null;
    PreparedStatement retrieveStatement = null;
    ResultSet resultSet = null;
  
    try {
      connection = C3P0DataSource.getInstance().getConnection(); 
      
      retrieveStatement = connection.prepareStatement(RETRIEVE_BY_SESSION_IDENTIFIER);
      
      retrieveStatement.setString(1, sessionIdentifier);
      
      resultSet = retrieveStatement.executeQuery(); 
      
      if (resultSet != null && resultSet.next()) {
        userSessionTracking = loadFromResultSet(resultSet);
      }
      
      return userSessionTracking;
  
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

  public static Set<String> retrieveSessionIdentifierSet(String userIdentifier) {

    logger.info("###Entering");
    logger.info("userIdentifier: " + userIdentifier);
    
    HashSet<String> sessionIdentifierSet = new HashSet<String>();
  
    Connection connection = null;
    PreparedStatement retrieveStatement = null;
    ResultSet resultSet = null;
  
    try {
      connection = C3P0DataSource.getInstance().getConnection(); 
      
      retrieveStatement = connection.prepareStatement(RETRIEVE_BY_USER_IDENTIFIER);
      
      retrieveStatement.setString(1, userIdentifier);
      
      resultSet = retrieveStatement.executeQuery(); 
      
      while (resultSet.next()) {
      	UserSessionTracking userSessionTracking = loadFromResultSet(resultSet);
      	sessionIdentifierSet.add(userSessionTracking.getSessionIdentifier());

        //logger.info("###userSessionTracking.getSessionIdentifier(): " + userSessionTracking.getSessionIdentifier() );
      }
      
      return sessionIdentifierSet;
  
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
  
  private static UserSessionTracking loadFromResultSet(ResultSet resultSet) {
    
    try {
      UserSessionTracking userSessionTracking = new UserSessionTracking();
      
      userSessionTracking.setUserSessionTrackingId(resultSet.getInt("userSessionTrackingId")); 
      userSessionTracking.setCreated(resultSet.getTimestamp("created"));
      userSessionTracking.setUpdated(resultSet.getTimestamp("updated"));
      userSessionTracking.setUpdatedById(resultSet.getInt("updatedById"));
      userSessionTracking.setDataSourceTypeValue(resultSet.getInt("dataSourceTypeValue"));
      
      userSessionTracking.setUserIdentifier(resultSet.getString("userIdentifier"));
      userSessionTracking.setSessionIdentifier(resultSet.getString("sessionIdentifier"));
      
      return userSessionTracking;
      
    } catch (Exception e) {
      logger.error("", e);
      throw new RuntimeException(e);
    }
  }
}







