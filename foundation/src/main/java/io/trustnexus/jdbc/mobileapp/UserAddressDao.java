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

import io.trustnexus.model.mobileapp.UserAddress;
import io.trustnexus.util.C3P0DataSource;

public class UserAddressDao {

  private final static Logger logger = LogManager.getLogger(UserAddressDao.class);

  public static final String INSERT 
                             = "INSERT UserAddress (created, updated, updatedById, dataSourceTypeValue, " 
                             + "userId, addressType, addressLineOne, addressLineTwo, city, state, postalCode, country) " 
                             + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

  public static final String UPDATE 
                             = "UPDATE UserAddress SET updated = ?, updatedById = ?, dataSourceTypeValue = ?, " 
                             + "addressType = ?, addressLineOne = ?, addressLineTwo = ?, city = ?, state = ?, postalCode = ?, country = ? " 
                             + "WHERE userId = ?";

  private static final String RETRIEVE_BY_USER_ID 
                              = "SELECT * FROM UserAddress "
                              + "WHERE userId = ?";

  private static final String DELETE_BY_USER_ID = "DELETE FROM UserAddress " 
                                                  + "WHERE userId = ?";

  // ------------------------------------------------------------------------------------------------------------------
  
  public UserAddressDao() {    
  }

  // ------------------------------------------------------------------------------------------------------------------

  public static void createOrUpdate(UserAddress userAddress) {

    logger.info("###Entering");
    
    UserAddress existingAddress = retrieveByUserId(userAddress.getUserId());
    
    if (existingAddress == null) {
      create(userAddress);
    } else {
      update(userAddress);
    }
  }

  // ------------------------------------------------------------------------------------------------------------------

  public static void create(UserAddress userAddress) {

    logger.info("###Entering");
  
    Connection connection = null;
    PreparedStatement insertStatement = null;
  
    try {
      Timestamp timestamp = new Timestamp(System.currentTimeMillis());
      userAddress.setCreated(timestamp);
      userAddress.setUpdated(timestamp);
      
      connection = C3P0DataSource.getInstance().getConnection(); 
      
      insertStatement = connection.prepareStatement(INSERT);
      
      insertStatement.setTimestamp(1, userAddress.getCreated());
      insertStatement.setTimestamp(2, userAddress.getUpdated());
      insertStatement.setInt(3, userAddress.getUpdatedById());
      insertStatement.setInt(4, userAddress.getDataSourceTypeValue());

      insertStatement.setInt(5, userAddress.getUserId());
      insertStatement.setInt(6, userAddress.getAddressType());
      insertStatement.setString(7, userAddress.getAddressLineOne());
      insertStatement.setString(8, userAddress.getAddressLineTwo());
      insertStatement.setString(9, userAddress.getCity());
      insertStatement.setString(10, userAddress.getState());
      insertStatement.setString(11, userAddress.getPostalCode());
      insertStatement.setString(12, userAddress.getCountry());
      
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

  public static void update(UserAddress userAddress) {

    logger.info("###Entering");
  
    Connection connection = null;
    PreparedStatement insertStatement = null;
  
    try {
      Timestamp timestamp = new Timestamp(System.currentTimeMillis());
      userAddress.setUpdated(timestamp);
      
      connection = C3P0DataSource.getInstance().getConnection(); 
      
      insertStatement = connection.prepareStatement(UPDATE);
      
      insertStatement.setTimestamp(1, userAddress.getUpdated());
      insertStatement.setInt(2, userAddress.getUpdatedById());
      insertStatement.setInt(3, userAddress.getDataSourceTypeValue());

      insertStatement.setInt(4, userAddress.getAddressType());
      insertStatement.setString(5, userAddress.getAddressLineOne());
      insertStatement.setString(6, userAddress.getAddressLineTwo());
      insertStatement.setString(7, userAddress.getCity());
      insertStatement.setString(8, userAddress.getState());
      insertStatement.setString(9, userAddress.getPostalCode());
      insertStatement.setString(10, userAddress.getCountry());

      insertStatement.setInt(11, userAddress.getUserId());
      
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

  public static UserAddress retrieveByUserId(Integer userId) {

    logger.info("###Entering");
    
    UserAddress userAddress = null;
  
    Connection connection = null;
    PreparedStatement retrieveStatement = null;
    ResultSet resultSet = null;
  
    try {
      connection = C3P0DataSource.getInstance().getConnection(); 
      
      retrieveStatement = connection.prepareStatement(RETRIEVE_BY_USER_ID);
      
      retrieveStatement.setInt(1, userId);
      
      resultSet = retrieveStatement.executeQuery(); 
      
      if (resultSet != null && resultSet.next()) {
        userAddress = loadFromResultSet(resultSet);
      }
      
      return userAddress;
  
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
  
  private static UserAddress loadFromResultSet(ResultSet resultSet) {
    
    try {
      UserAddress userAddress = new UserAddress();
      
      userAddress.setUserAddressId(resultSet.getInt("userAddressId")); 
      userAddress.setCreated(resultSet.getTimestamp("created"));
      userAddress.setUpdated(resultSet.getTimestamp("updated"));
      userAddress.setUpdatedById(resultSet.getInt("updatedById"));
      userAddress.setDataSourceTypeValue(resultSet.getInt("dataSourceTypeValue"));
      
      userAddress.setUserId(resultSet.getInt("userId"));
      userAddress.setAddressType(resultSet.getInt("addressType"));
      userAddress.setAddressLineOne(resultSet.getString("addressLineOne"));
      userAddress.setAddressLineTwo(resultSet.getString("addressLineTwo"));
      userAddress.setCity(resultSet.getString("city"));
      userAddress.setState(resultSet.getString("state"));
      userAddress.setPostalCode(resultSet.getString("postalCode"));
      userAddress.setCountry(resultSet.getString("country"));
      
      return userAddress;
      
    } catch (Exception e) {
      logger.error("", e);
      throw new RuntimeException(e);
    }
  }

  // ------------------------------------------------------------------------------------------------------------------

  public static void deleteByUserId(int userId) {

    logger.info("###Entering");

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







