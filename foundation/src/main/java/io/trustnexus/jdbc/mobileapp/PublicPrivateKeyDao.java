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

package io.trustnexus.jdbc.mobileapp;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.trustnexus.model.mobileapp.PublicPrivateKey;
import io.trustnexus.util.C3P0DataSource;

public class PublicPrivateKeyDao {

  private final static Logger logger = LogManager.getLogger(PublicPrivateKeyDao.class);

  public static final String INSERT 
                             = "INSERT PublicPrivateKey (created, updated, updatedById, dataSourceTypeValue, " 
                             + "keyOwner, publicKeyHex, privateKeyHex, inactiveFlag, uuid) " 
                             + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

  public static final String UPDATE
                             = "UPDATE User SET updated = ?, updatedById = ?, dataSourceTypeValue = ?, " 
                             + "keyOwner = ?, publicKeyHex = ?, privateKeyHex = ?, inactiveFlag = ?, uuid = ? " 
                             + "WHERE keyOwner = ?";

  private static final String RETRIEVE_BY_ID 
                              = "SELECT * FROM PublicPrivateKey "
                              + "WHERE publicPrivateKeyId = ?";

  public static final String RETRIEVE_BY_KEY_OWNER 
                              = "SELECT * FROM PublicPrivateKey "
                              + "WHERE keyOwner = ?";

  private static final String RETRIEVE_BY_UUID 
                              = "SELECT * FROM PublicPrivateKey "
                              + "WHERE uuid = ?";

  public static final String UPDATE_INACTIVE_FLAG
                             = "UPDATE PublicPrivateKey SET inactiveFlag = ? WHERE uuid = ?";


  // ------------------------------------------------------------------------------------------------------------------
  
  public PublicPrivateKeyDao() {    
  }

  // ------------------------------------------------------------------------------------------------------------------

  public static void create(PublicPrivateKey publicPrivateKey) {

    logger.info("###Entering");
  
    Connection connection = null;
    PreparedStatement insertStatement = null;
  
    try {
      Timestamp timestamp = new Timestamp(System.currentTimeMillis());
      publicPrivateKey.setCreated(timestamp);
      publicPrivateKey.setUpdated(timestamp);
      
      connection = C3P0DataSource.getInstance().getConnection(); 
      
      insertStatement = connection.prepareStatement(INSERT);
      
      insertStatement.setTimestamp(1, publicPrivateKey.getCreated());
      insertStatement.setTimestamp(2, publicPrivateKey.getUpdated());
      insertStatement.setInt(3, publicPrivateKey.getUpdatedById());
      insertStatement.setInt(4, publicPrivateKey.getDataSourceTypeValue());
      
      insertStatement.setString(5, publicPrivateKey.getKeyOwner());
      insertStatement.setString(6, publicPrivateKey.getPublicKeyHex());
      insertStatement.setString(7, publicPrivateKey.getPrivateKeyHex());
      insertStatement.setBoolean(8, publicPrivateKey.isInactiveFlag());
      insertStatement.setString(9, publicPrivateKey.getUuid());
      
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

  public static void update(PublicPrivateKey publicPrivateKey) {

    logger.info("###Entering");
  
    Connection connection = null;
    PreparedStatement insertStatement = null;
  
    try {
      Timestamp timestamp = new Timestamp(System.currentTimeMillis());
      publicPrivateKey.setUpdated(timestamp);
      
      connection = C3P0DataSource.getInstance().getConnection(); 
      
      insertStatement = connection.prepareStatement(UPDATE);
      
      insertStatement.setTimestamp(1, publicPrivateKey.getUpdated());
      insertStatement.setInt(2, publicPrivateKey.getUpdatedById());
      insertStatement.setInt(3, publicPrivateKey.getDataSourceTypeValue());
      
      insertStatement.setString(4, publicPrivateKey.getKeyOwner());
      insertStatement.setString(5, publicPrivateKey.getPublicKeyHex());
      insertStatement.setString(6, publicPrivateKey.getPrivateKeyHex());
      insertStatement.setBoolean(7, publicPrivateKey.isInactiveFlag());
      insertStatement.setString(8, publicPrivateKey.getUuid());

      insertStatement.setInt(9, publicPrivateKey.getPublicPrivateKeyId());
      
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

  public static PublicPrivateKey retrieveById(Integer publicPrivateKeyId) {

    logger.info("###Entering");
    
  	PublicPrivateKey publicPrivateKey = null;
  
    Connection connection = null;
    PreparedStatement retrieveStatement = null;
    ResultSet resultSet = null;
  
    try {
      connection = C3P0DataSource.getInstance().getConnection(); 
      
      retrieveStatement = connection.prepareStatement(RETRIEVE_BY_ID);
      
      retrieveStatement.setInt(1, publicPrivateKeyId);
      
      resultSet = retrieveStatement.executeQuery(); 
      
      if (resultSet != null && resultSet.next()) {
      	publicPrivateKey = loadFromResultSet(resultSet);
      }
      
      return publicPrivateKey;
  
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

  public static PublicPrivateKey retrieveByUuid(String uuid) {

    logger.info("###Entering");
    
  	PublicPrivateKey publicPrivateKey = null;
  
    Connection connection = null;
    PreparedStatement retrieveStatement = null;
    ResultSet resultSet = null;
  
    try {
      connection = C3P0DataSource.getInstance().getConnection(); 
      
      retrieveStatement = connection.prepareStatement(RETRIEVE_BY_UUID);
      
      retrieveStatement.setString(1, uuid);
      
      resultSet = retrieveStatement.executeQuery(); 
      
      if (resultSet != null && resultSet.next()) {
      	publicPrivateKey = loadFromResultSet(resultSet);
      }
      
      return publicPrivateKey;
  
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
  
  public static PublicPrivateKey loadFromResultSet(ResultSet resultSet) {
    
    try {
      PublicPrivateKey publicPrivateKey = new PublicPrivateKey();
      
      publicPrivateKey.setPublicPrivateKeyId(resultSet.getInt("publicPrivateKeyId")); 
      publicPrivateKey.setCreated(resultSet.getTimestamp("created"));
      publicPrivateKey.setUpdated(resultSet.getTimestamp("updated"));
      publicPrivateKey.setUpdatedById(resultSet.getInt("updatedById"));
      publicPrivateKey.setDataSourceTypeValue(resultSet.getInt("dataSourceTypeValue"));
      
      publicPrivateKey.setKeyOwner(resultSet.getString("keyOwner"));
      publicPrivateKey.setPublicKeyHex(resultSet.getString("publicKeyHex"));
      publicPrivateKey.setPrivateKeyHex(resultSet.getString("privateKeyHex"));
      publicPrivateKey.setInactiveFlag(resultSet.getBoolean("inactiveFlag"));
      publicPrivateKey.setUuid(resultSet.getString("uuid"));
      
      return publicPrivateKey;
      
    } catch (Exception e) {
      logger.error("", e);
      throw new RuntimeException(e);
    }
  }

  // ------------------------------------------------------------------------------------------------------------------

  public static void updateInactiveFlag(boolean inactiveFlag, String uuid) {

    logger.info("###Entering");
  
    Connection connection = null;
    PreparedStatement updateStatement = null;
  
    try {       
      connection = C3P0DataSource.getInstance().getConnection(); 
      
      updateStatement = connection.prepareStatement(UPDATE_INACTIVE_FLAG);
        
      updateStatement.setBoolean(1, inactiveFlag);
      updateStatement.setString(2, uuid);
        
      updateStatement.executeUpdate();
  
    } catch (Exception e) {
      logger.error("", e);
      throw new RuntimeException(e);
    } finally {
      
      C3P0DataSource.getInstance().closeConnection(connection);
      C3P0DataSource.getInstance().closeStatement(updateStatement);
    }    
  }
}







