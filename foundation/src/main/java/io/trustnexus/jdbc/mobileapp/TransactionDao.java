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

import io.trustnexus.model.mobileapp.Transaction;
import io.trustnexus.util.C3P0DataSource;

public class TransactionDao {

  private final static Logger logger = LogManager.getLogger(TransactionDao.class);

  public static final String INSERT 
                             = "INSERT Transaction (created, updated, updatedById, dataSourceTypeValue, " 
                             + "userUuid, transactionUuid, inactiveFlag) " 
                             + "VALUES (?, ?, ?, ?, ?, ?, ?)";

  public static final String UPDATE 
                             = "UPDATE Transaction SET updated = ?, updatedById = ?, dataSourceTypeValue = ?, " 
                             + "userUuid = ?, transactionUuid = ?, inactiveFlag = ? " 
                             + "WHERE transactionId = ?";

  private static final String RETRIEVE_BY_USER_UUID_AND_TRANSACTION_UUID
                              = "SELECT * FROM Transaction "
                              + "WHERE userUuid = ? AND transactionUuid = ?"
                              + "AND created > DATE_SUB(NOW(), INTERVAL 3 MINUTE)";  // Transaction UUIDs become invalid after three minutes.

  // ------------------------------------------------------------------------------------------------------------------
  
  public TransactionDao() {    
  }

  // ------------------------------------------------------------------------------------------------------------------

  public static void create(Transaction transaction) {

    logger.info("###Entering");
  
    Connection connection = null;
    PreparedStatement insertStatement = null;
  
    try {
      Timestamp timestamp = new Timestamp(System.currentTimeMillis());
      transaction.setCreated(timestamp);
      transaction.setUpdated(timestamp);
      
      connection = C3P0DataSource.getInstance().getConnection(); 
      
      insertStatement = connection.prepareStatement(INSERT);
      
      insertStatement.setTimestamp(1, transaction.getCreated());
      insertStatement.setTimestamp(2, transaction.getUpdated());
      insertStatement.setInt(3, transaction.getUpdatedById());
      insertStatement.setInt(4, transaction.getDataSourceTypeValue());

      insertStatement.setString(5, transaction.getUserUuid());
      insertStatement.setString(6, transaction.getTransactionUuid());
      insertStatement.setBoolean(7, transaction.isInactiveFlag());
      
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

  public static void update(Transaction transaction) {

    logger.info("###Entering");
  
    Connection connection = null;
    PreparedStatement insertStatement = null;
  
    try {
      Timestamp timestamp = new Timestamp(System.currentTimeMillis());
      transaction.setUpdated(timestamp);
      
      connection = C3P0DataSource.getInstance().getConnection(); 
      
      insertStatement = connection.prepareStatement(UPDATE);
      
      insertStatement.setTimestamp(1, transaction.getUpdated());
      insertStatement.setInt(2, transaction.getUpdatedById());
      insertStatement.setInt(3, transaction.getDataSourceTypeValue());

      insertStatement.setString(4, transaction.getUserUuid());
      insertStatement.setString(5, transaction.getTransactionUuid());
      insertStatement.setBoolean(6, transaction.isInactiveFlag());
      
      insertStatement.setInt(7, transaction.getTransactionId());
      
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

  public static Transaction retrieveByUserUuidAndTransactionUuid(String userUuid, String transactionUuid) {

    logger.info("###Entering");
    
    Transaction transaction = null;
  
    Connection connection = null;
    PreparedStatement retrieveStatement = null;
    ResultSet resultSet = null;
  
    try {
      connection = C3P0DataSource.getInstance().getConnection(); 
      
      retrieveStatement = connection.prepareStatement(RETRIEVE_BY_USER_UUID_AND_TRANSACTION_UUID);
      
      retrieveStatement.setString(1, userUuid);
      retrieveStatement.setString(2, transactionUuid);
      
      resultSet = retrieveStatement.executeQuery(); 
      
      if (resultSet != null && resultSet.next()) {
        transaction = loadFromResultSet(resultSet);
      }
      
      return transaction;
  
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
  
  private static Transaction loadFromResultSet(ResultSet resultSet) {
    
    try {
      Transaction transaction = new Transaction();
      
      transaction.setTransactionId(resultSet.getInt("transactionId")); 
      transaction.setCreated(resultSet.getTimestamp("created"));
      transaction.setUpdated(resultSet.getTimestamp("updated"));
      transaction.setUpdatedById(resultSet.getInt("updatedById"));
      transaction.setDataSourceTypeValue(resultSet.getInt("dataSourceTypeValue"));
      
      transaction.setUserUuid(resultSet.getString("userUuid"));
      transaction.setTransactionUuid(resultSet.getString("transactionUuid"));
      transaction.setInactiveFlag(resultSet.getBoolean("inactiveFlag"));
      
      return transaction;
      
    } catch (Exception e) {
      logger.error("", e);
      throw new RuntimeException(e);
    }
  }
}







