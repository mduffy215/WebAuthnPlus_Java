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

package io.trustnexus.jdbc.fundstransfer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.trustnexus.model.fundstransfer.FundsTransfer;
import io.trustnexus.util.C3P0DataSource;

public class SendFundsDao {

  private final static Logger logger = LogManager.getLogger(SendFundsDao.class);

  public static final String INSERT = "INSERT SendFunds (created, updated, updatedById, dataSourceTypeValue, "
                                    + "fundsTransferUuid, userUuid, credentialUuid, recipientName, recipientEmail, recipientPhoneNumber, "
                                    + "transferAmount, recipientCredentialProviderUuid, recipientCredentialUuid, recipientUuid, json) " 
                                    + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";  

  private static final String RETRIEVE_BY_USER_UUID_AND_CREDENTIAL_UUID 
                              = "SELECT * FROM SendFunds "  
                              + "WHERE userUuid = ? AND credentialUuid = ?";  

  private static final String RETRIEVE_BY_FUNDS_TRANSFER_UUID 
                              = "SELECT * FROM SendFunds "  
                              + "WHERE fundsTransferUuid = ?";

  public static final String UPDATE_JSON 
                             = "UPDATE SendFunds SET updated = ?, json = ? "  
                             + "WHERE fundsTransferUuid = ?";

  // ------------------------------------------------------------------------------------------------------------------

  public SendFundsDao() {
  }

  // ------------------------------------------------------------------------------------------------------------------

  public static void create(FundsTransfer fundsTransfer) {

    logger.info("###Entering");

    Connection connection = null;
    PreparedStatement insertStatement = null;

    try {
      Timestamp timestamp = new Timestamp(System.currentTimeMillis());
      fundsTransfer.setCreated(timestamp);
      fundsTransfer.setUpdated(timestamp);

      connection = C3P0DataSource.getInstance().getConnection();

      insertStatement = connection.prepareStatement(INSERT);

      insertStatement.setTimestamp(1, fundsTransfer.getCreated());
      insertStatement.setTimestamp(2, fundsTransfer.getUpdated());
      insertStatement.setInt(3, fundsTransfer.getUpdatedById());
      insertStatement.setInt(4, fundsTransfer.getDataSourceTypeValue());

      insertStatement.setString(5, fundsTransfer.getFundsTransferUuid());
      insertStatement.setString(6, fundsTransfer.getUserUuid());
      insertStatement.setString(7, fundsTransfer.getCredentialUuid());
      
      insertStatement.setString(8, fundsTransfer.getRecipientName());      
      insertStatement.setString(9, fundsTransfer.getRecipientEmail());
      insertStatement.setString(10, fundsTransfer.getRecipientPhoneNumber());
      
      insertStatement.setBigDecimal(11, fundsTransfer.getTransferAmount());
      
      insertStatement.setString(12, fundsTransfer.getRecipientCredentialProviderUuid());
      insertStatement.setString(13, fundsTransfer.getRecipientCredentialUuid());
      insertStatement.setString(14, fundsTransfer.getRecipientUuid());
      insertStatement.setString(15, fundsTransfer.getJson());

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

  public static FundsTransfer retrieveByFundsTransferUuid(String fundsTransferUuid) {

    logger.info("###Entering");

    FundsTransfer fundsTransfer = null;

    Connection connection = null;
    PreparedStatement retrieveStatement = null;
    ResultSet resultSet = null;

    try {
      connection = C3P0DataSource.getInstance().getConnection();

      retrieveStatement = connection.prepareStatement(RETRIEVE_BY_FUNDS_TRANSFER_UUID);

      retrieveStatement.setString(1, fundsTransferUuid);

      resultSet = retrieveStatement.executeQuery();

      if (resultSet != null && resultSet.next()) {
      	fundsTransfer = loadFromResultSet(resultSet);
      }

      return fundsTransfer;

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

  public static FundsTransfer retrieveByUserUuidAndCredentialUuid(String userUuid, String credentialUuid) {

    logger.info("###Entering");

    FundsTransfer fundsTransfer = null;

    Connection connection = null;
    PreparedStatement retrieveStatement = null;
    ResultSet resultSet = null;

    try {
      connection = C3P0DataSource.getInstance().getConnection();

      retrieveStatement = connection.prepareStatement(RETRIEVE_BY_USER_UUID_AND_CREDENTIAL_UUID);

      retrieveStatement.setString(1, userUuid);
      retrieveStatement.setString(2, credentialUuid);

      resultSet = retrieveStatement.executeQuery();

      if (resultSet != null && resultSet.next()) {
      	fundsTransfer = loadFromResultSet(resultSet);
      }

      return fundsTransfer;

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

  public static void updateJson(FundsTransfer fundsTransfer) {

    logger.info("###Entering");

    Connection connection = null;
    PreparedStatement updateStatement = null;

    try {
      connection = C3P0DataSource.getInstance().getConnection();

      updateStatement = connection.prepareStatement(UPDATE_JSON);

      updateStatement.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
      updateStatement.setString(2, fundsTransfer.getJson());
      updateStatement.setString(3, fundsTransfer.getFundsTransferUuid());

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

  private static FundsTransfer loadFromResultSet(ResultSet resultSet) {

    try {
    	FundsTransfer fundsTransfer = new FundsTransfer();

    	fundsTransfer.setFundsTransferId(resultSet.getInt("fundsTransferId"));
    	fundsTransfer.setCreated(resultSet.getTimestamp("created"));
    	fundsTransfer.setUpdated(resultSet.getTimestamp("updated"));
    	fundsTransfer.setUpdatedById(resultSet.getInt("updatedById"));
    	fundsTransfer.setDataSourceTypeValue(resultSet.getInt("dataSourceTypeValue"));

    	fundsTransfer.setFundsTransferUuid(resultSet.getString("fundsTransferUuid"));
    	fundsTransfer.setUserUuid(resultSet.getString("userUuid"));
    	fundsTransfer.setCredentialUuid(resultSet.getString("credentialUuid"));
      
    	fundsTransfer.setRecipientName(resultSet.getString("recipientName"));
    	fundsTransfer.setRecipientEmail(resultSet.getString("recipientEmail"));   
    	fundsTransfer.setRecipientPhoneNumber(resultSet.getString("recipientPhoneNumber"));
      
    	fundsTransfer.setTransferAmount(resultSet.getBigDecimal("transferAmount"));
      
    	fundsTransfer.setRecipientCredentialProviderUuid(resultSet.getString("recipientCredentialProviderUuid"));
    	fundsTransfer.setRecipientCredentialUuid(resultSet.getString("recipientCredentialUuid"));
    	fundsTransfer.setRecipientUuid(resultSet.getString("recipientUuid"));
      
    	fundsTransfer.setJson(resultSet.getString("json"));

      return fundsTransfer;

    } catch (Exception e) {
      logger.error("", e);
      throw new RuntimeException(e);
    }
  }
}







