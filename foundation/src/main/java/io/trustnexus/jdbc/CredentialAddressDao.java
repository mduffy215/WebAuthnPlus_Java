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

package io.trustnexus.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.trustnexus.model.CredentialAddress;
import io.trustnexus.util.C3P0DataSource;

public class CredentialAddressDao {

  private final static Logger logger = LogManager.getLogger(CredentialAddressDao.class);

  public static final String INSERT 
                             = "INSERT CredentialAddress (created, updated, updatedById, dataSourceTypeValue, " 
                             + "credentialId, addressType, addressLineOne, addressLineTwo, city, stateOrProvince, postalCode, country) " 
                             + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

  private static final String RETRIEVE_BY_CREDENTIAL_ID 
                              = "SELECT * FROM CredentialAddress "
                              + "WHERE credentialId = ?";

  private static final String DELETE_BY_CREDENTIAL_ID 
                              = "DELETE FROM CredentialAddress "
                              + "WHERE credentialId = ?";

  // ------------------------------------------------------------------------------------------------------------------
  
  public CredentialAddressDao() {    
  }

  // ------------------------------------------------------------------------------------------------------------------

  public static void create(CredentialAddress address) {

    logger.info("###Entering");
  
    Connection connection = null;
    PreparedStatement insertStatement = null;
  
    try {
      Timestamp timestamp = new Timestamp(System.currentTimeMillis());
      address.setCreated(timestamp);
      address.setUpdated(timestamp);
      
      connection = C3P0DataSource.getInstance().getConnection(); 
      
      insertStatement = connection.prepareStatement(INSERT);
      
      insertStatement.setTimestamp(1, address.getCreated());
      insertStatement.setTimestamp(2, address.getUpdated());
      insertStatement.setInt(3, address.getUpdatedById());
      insertStatement.setInt(4, address.getDataSourceTypeValue());

      insertStatement.setInt(5, address.getCredentialId());
      insertStatement.setInt(6, address.getAddressType());
      insertStatement.setString(7, address.getAddressLineOne());
      insertStatement.setString(8, address.getAddressLineTwo());
      insertStatement.setString(9, address.getCity());
      insertStatement.setString(10, address.getStateOrProvince());
      insertStatement.setString(11, address.getPostalCode());
      insertStatement.setString(12, address.getCountry());
      
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

  public static CredentialAddress retrieveByCredentialId(Integer credentialId) {

    logger.info("###Entering");
    
    CredentialAddress credentialAddress = null;
  
    Connection connection = null;
    PreparedStatement retrieveStatement = null;
    ResultSet resultSet = null;
  
    try {
      connection = C3P0DataSource.getInstance().getConnection(); 
      
      retrieveStatement = connection.prepareStatement(RETRIEVE_BY_CREDENTIAL_ID);
      
      retrieveStatement.setInt(1, credentialId);
      
      resultSet = retrieveStatement.executeQuery(); 
      
      if (resultSet != null && resultSet.next()) {
        credentialAddress = loadFromResultSet(resultSet);
      }
      
      return credentialAddress;
  
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

  public static void deleteByCredentialId(int credentialId) {

    logger.info("###Entering");
  
    Connection connection = null;
    PreparedStatement deleteStatement = null;
  
    try {
      connection = C3P0DataSource.getInstance().getConnection(); 
      
      deleteStatement = connection.prepareStatement(DELETE_BY_CREDENTIAL_ID);        
      deleteStatement.setInt(1, credentialId);        
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
  
  private static CredentialAddress loadFromResultSet(ResultSet resultSet) {
    
    try {
      CredentialAddress credentialAddress = new CredentialAddress();
      
      credentialAddress.setCredentialAddressId(resultSet.getInt("credentialAddressId")); 
      credentialAddress.setCreated(resultSet.getTimestamp("created"));
      credentialAddress.setUpdated(resultSet.getTimestamp("updated"));
      credentialAddress.setUpdatedById(resultSet.getInt("updatedById"));
      credentialAddress.setDataSourceTypeValue(resultSet.getInt("dataSourceTypeValue"));
      
      credentialAddress.setCredentialId(resultSet.getInt("credentialId"));
      credentialAddress.setAddressType(resultSet.getInt("addressType"));
      credentialAddress.setAddressLineOne(resultSet.getString("addressLineOne"));
      credentialAddress.setAddressLineTwo(resultSet.getString("addressLineTwo"));
      credentialAddress.setCity(resultSet.getString("city"));
      credentialAddress.setStateOrProvince(resultSet.getString("stateOrProvince"));
      credentialAddress.setPostalCode(resultSet.getString("postalCode"));
      credentialAddress.setCountry(resultSet.getString("country"));
      
      return credentialAddress;
      
    } catch (Exception e) {
      logger.error("", e);
      throw new RuntimeException(e);
    }
  }
}







