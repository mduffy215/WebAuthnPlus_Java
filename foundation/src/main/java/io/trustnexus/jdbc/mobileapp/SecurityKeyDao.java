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

import io.trustnexus.model.mobileapp.SecurityKey;
import io.trustnexus.util.C3P0DataSource;

public class SecurityKeyDao {

  private final static Logger logger = LogManager.getLogger(SecurityKeyDao.class);

  public static final String INSERT 
                             = "INSERT SecurityKey (created, updated, updatedById, dataSourceTypeValue, " 
                             + "obfuscatedIdentifier, userSecurityKeyEncrypted) " 
                             + "VALUES (?, ?, ?, ?, ?, ?)";

  public static final String UPDATE 
                             = "UPDATE SecurityKey SET updated = ?, updatedById = ?, dataSourceTypeValue = ?, " 
                             + "userSecurityKeyEncrypted = ? " 
                             + "WHERE obfuscatedIdentifier = ?";

  private static final String RETRIEVE_BY_OBFUSCATED_IDENTIFIER 
                              = "SELECT * FROM SecurityKey "
                              + "WHERE obfuscatedIdentifier = ?";

  // ------------------------------------------------------------------------------------------------------------------
  
  public SecurityKeyDao() {    
  }

  // ------------------------------------------------------------------------------------------------------------------

  public static void create(SecurityKey securityKey) {

    logger.info("###Entering");
  
    Connection connection = null;
    PreparedStatement insertStatement = null;
  
    try {
      Timestamp timestamp = new Timestamp(System.currentTimeMillis());
      securityKey.setCreated(timestamp);
      securityKey.setUpdated(timestamp);
      
      connection = C3P0DataSource.getInstance().getConnection(); 
      
      insertStatement = connection.prepareStatement(INSERT);
      
      insertStatement.setTimestamp(1, securityKey.getCreated());
      insertStatement.setTimestamp(2, securityKey.getUpdated());
      insertStatement.setInt(3, securityKey.getUpdatedById());
      insertStatement.setInt(4, securityKey.getDataSourceTypeValue());
      
      insertStatement.setString(5, securityKey.getObfuscatedIdentifier());
      insertStatement.setString(6, securityKey.getUserSecurityKeyEncrypted());
      
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

  public static void update(SecurityKey securityKey) {

    logger.info("###Entering");
  
    Connection connection = null;
    PreparedStatement insertStatement = null;
  
    try {
      Timestamp timestamp = new Timestamp(System.currentTimeMillis());
      securityKey.setUpdated(timestamp);
      
      connection = C3P0DataSource.getInstance().getConnection(); 
      
      insertStatement = connection.prepareStatement(UPDATE);
      
      insertStatement.setTimestamp(1, securityKey.getUpdated());
      insertStatement.setInt(2, securityKey.getUpdatedById());
      insertStatement.setInt(3, securityKey.getDataSourceTypeValue());
      
      insertStatement.setString(4, securityKey.getUserSecurityKeyEncrypted());

      insertStatement.setString(5, securityKey.getObfuscatedIdentifier());
      
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

  public static SecurityKey retrieveByObfuscatedIdentifier(String obfuscatedIdentifier) {

    logger.info("###Entering");
    
    SecurityKey securityKey = null;
  
    Connection connection = null;
    PreparedStatement retrieveStatement = null;
    ResultSet resultSet = null;
  
    try {
      connection = C3P0DataSource.getInstance().getConnection(); 
      
      retrieveStatement = connection.prepareStatement(RETRIEVE_BY_OBFUSCATED_IDENTIFIER);
      
      retrieveStatement.setString(1, obfuscatedIdentifier);
      
      resultSet = retrieveStatement.executeQuery(); 
      
      if (resultSet != null && resultSet.next()) {
        securityKey = loadFromResultSet(resultSet);
      }
      
      return securityKey;
  
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
  
  private static SecurityKey loadFromResultSet(ResultSet resultSet) {
    
    try {
      SecurityKey securityKey = new SecurityKey();
      
      securityKey.setSecurityKeyId(resultSet.getInt("securityKeyId")); 
      securityKey.setCreated(resultSet.getTimestamp("created"));
      securityKey.setUpdated(resultSet.getTimestamp("updated"));
      securityKey.setUpdatedById(resultSet.getInt("updatedById"));
      securityKey.setDataSourceTypeValue(resultSet.getInt("dataSourceTypeValue"));
      
      securityKey.setObfuscatedIdentifier(resultSet.getString("obfuscatedIdentifier"));
      securityKey.setUserSecurityKeyEncrypted(resultSet.getString("userSecurityKeyEncrypted"));
      
      return securityKey;
      
    } catch (Exception e) {
      logger.error("", e);
      throw new RuntimeException(e);
    }
  }
}







