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

package io.trustnexus.jdbc.distributedledger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.trustnexus.model.distributedledger.DistributedLedger;
import io.trustnexus.util.C3P0DataSource;
import io.trustnexus.util.Constants;

public class DistributedLedgerDao {

  private final static Logger logger = LogManager.getLogger(DistributedLedgerDao.class);

    public static final String INSERT 
                               = "INSERT DistributedLedger (created, updated, updatedById, dataSourceTypeValue, "
                               + "distributedLedgerUuid, jsonDistributedLedger, credentialProviderUuid, "
                               + "credentialType, credentialUuid, authenticationCode, verificationCode, "
                               + "userUuid, screenName, email, sessionUuid, signatureCompleteUuid) "
                               + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    public static final String UPDATE
                               = "UPDATE DistributedLedger SET updated = ?, verificationCode = ?, screenName = ?, email = ?, jsonDistributedLedger = ?  WHERE distributedLedgerId = ?";

    public static final String UPDATE_SIGNATURE_COMPLETED_UUID
                               = "UPDATE DistributedLedger SET updated = ?, signatureCompleteUuid = ? WHERE distributedLedgerId = ?";

    public static final String UPDATE_VERIFICATION_CODE
                               = "UPDATE DistributedLedger SET updated = ?, verificationCode = ? WHERE distributedLedgerId = ?";

    public static final String INVALIDATE_VERIFICATION_CODES_BY_USER_UUID_AND_CREDENTIAL_TYPE
                               = "UPDATE DistributedLedger SET updated = ?, verificationCode = '" + Constants.SESSION_INVALIDATED 
                               + "' WHERE verificationCode IS NULL AND authenticationCode IS NULL AND userUuid = ? AND credentialType = ?"; 

    private static final String RETRIEVE_DISTRIBUTED_LEDGER_BY_USER_UUID_AND_CREDENTIAL_TYPE 
                                = "SELECT * FROM DistributedLedger "
                                + "WHERE userUuid = ? AND credentialType = ? "
                                + "AND created = (select MAX(created) FROM DistributedLedger "
                                + "WHERE userUuid = ? AND credentialType = ?)";

    private static final String RETRIEVE_BY_SESSION_UUID 
                                = "SELECT * FROM DistributedLedger "
                                + "WHERE sessionUuid = ?";

    private static final String RETRIEVE_BY_USER_UUID_SESSION_UUID_AND_SIGN_ON_UUID 
                                = "SELECT * FROM DistributedLedger "
                                + "WHERE userUuid = ? AND sessionUuid = ? AND signOnUuid = ? "
                                + "AND UPDATED > DATE_SUB(NOW(), INTERVAL 20 MINUTE)";

    private static final String RETRIEVE_BY_AUTHENTICATION_CODE_USER_UUID_AND_SESSION_UUID 
                                = "SELECT * FROM DistributedLedger "
                                + "WHERE authenticationCode = ? AND userUuid = ? AND sessionUuid = ?";

    private static final String RETRIEVE_BY_AUTHENTICATION_CODE_AND_USER_UUID 
                                = "SELECT * FROM DistributedLedger "
                                + "WHERE authenticationCode = ? AND userUuid = ? "
                                + "AND created = (SELECT MAX(created) FROM DistributedLedger "
                                + "WHERE authenticationCode = ? AND userUuid = ? )";

    private static final String RETRIEVE_BY_USER_UUID_AND_CREDENTIAL_TYPE 
                                = "SELECT * FROM DistributedLedger "
                                + "WHERE userUuid = ?  AND credentialType = ? "
                                + "AND created = (SELECT MAX(created) FROM DistributedLedger "
                                + "WHERE userUuid = ? AND credentialType = ? )";

    // ------------------------------------------------------------------------------------------------------------------
    
    public DistributedLedgerDao() {    
    }

    // ------------------------------------------------------------------------------------------------------------------

    public static void create(DistributedLedger distributedLedger) {

      logger.info("###Entering");
    
      Connection connection = null;
      PreparedStatement insertStatement = null;
    
      try {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        distributedLedger.setCreated(timestamp);
        distributedLedger.setUpdated(timestamp);
        
        connection = C3P0DataSource.getInstance().getConnection(); 
        
        insertStatement = connection.prepareStatement(INSERT);
        
        insertStatement.setTimestamp(1, distributedLedger.getCreated());
        insertStatement.setTimestamp(2, distributedLedger.getUpdated());
        insertStatement.setInt(3, distributedLedger.getUpdatedById());
        insertStatement.setInt(4, distributedLedger.getDataSourceTypeValue());

        insertStatement.setString(5, distributedLedger.getDistributedLedgerUuid());
        insertStatement.setString(6, distributedLedger.getJsonDistributedLedger());
        insertStatement.setString(7, distributedLedger.getCredentialProviderUuid());
        insertStatement.setString(8, distributedLedger.getCredentialType());
        insertStatement.setString(9, distributedLedger.getCredentialUuid());
        insertStatement.setString(10, distributedLedger.getAuthenticationCode());
        insertStatement.setString(11, distributedLedger.getVerificationCode());
        insertStatement.setString(12, distributedLedger.getUserUuid());
        insertStatement.setString(13, distributedLedger.getScreenName());
        insertStatement.setString(14, distributedLedger.getEmail());
        insertStatement.setString(15, distributedLedger.getSessionUuid());
        insertStatement.setString(16, distributedLedger.getSignatureCompleteUuid());
        
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

    public static void update(DistributedLedger distributedLedger) {

      logger.info("###Entering");
    
      Connection connection = null;
      PreparedStatement updateStatement = null;
    
      try {       
        connection = C3P0DataSource.getInstance().getConnection(); 
          
        updateStatement = connection.prepareStatement(UPDATE);

        updateStatement.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
        updateStatement.setString(2, distributedLedger.getVerificationCode());
        updateStatement.setString(3, distributedLedger.getScreenName());
        updateStatement.setString(4, distributedLedger.getEmail());
        updateStatement.setString(5, distributedLedger.getJsonDistributedLedger());
        updateStatement.setInt(6, distributedLedger.getDistributedLedgerId());
          
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

    public static void updateSignatureCompleteUuid(DistributedLedger distributedLedger) {

      logger.info("###Entering");
    
      Connection connection = null;
      PreparedStatement updateStatement = null;
    
      try {       
        connection = C3P0DataSource.getInstance().getConnection(); 
          
        updateStatement = connection.prepareStatement(UPDATE_SIGNATURE_COMPLETED_UUID);

        updateStatement.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
        updateStatement.setString(2, distributedLedger.getSignatureCompleteUuid());
        updateStatement.setInt(3, distributedLedger.getDistributedLedgerId());
          
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

    public static void updateVerificationCode(DistributedLedger distributedLedger) {

      logger.info("###Entering");
    
      Connection connection = null;
      PreparedStatement updateStatement = null;
    
      try {       
        connection = C3P0DataSource.getInstance().getConnection(); 
          
        updateStatement = connection.prepareStatement(UPDATE_VERIFICATION_CODE);

        updateStatement.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
        updateStatement.setString(2, distributedLedger.getVerificationCode());
        updateStatement.setInt(3, distributedLedger.getDistributedLedgerId());
          
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

    public static DistributedLedger retrieveDistributedLedger(String userUuid, String credentialType) {

      logger.info("###Entering");
      
      DistributedLedger distributedLedger = null;
    
      Connection connection = null;
      PreparedStatement retrieveStatement = null;
      ResultSet resultSet = null;
    
      try {
        connection = C3P0DataSource.getInstance().getConnection(); 
        
        retrieveStatement = connection.prepareStatement(RETRIEVE_DISTRIBUTED_LEDGER_BY_USER_UUID_AND_CREDENTIAL_TYPE);
        
        retrieveStatement.setString(1, userUuid);
        retrieveStatement.setString(2, credentialType);
        retrieveStatement.setString(3, userUuid);
        retrieveStatement.setString(4, credentialType);
        
        resultSet = retrieveStatement.executeQuery(); 
        
        if (resultSet != null && resultSet.next()) {
          distributedLedger = loadFromResultSet(resultSet);
        }
        
        return distributedLedger;
    
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

    public static DistributedLedger retrieveBySessionUuid(String sessionUuid) {

      logger.info("###Entering");
      
      DistributedLedger distributedLedger = null;
    
      Connection connection = null;
      PreparedStatement retrieveStatement = null;
      ResultSet resultSet = null;
    
      try {
        connection = C3P0DataSource.getInstance().getConnection(); 
        
        retrieveStatement = connection.prepareStatement(RETRIEVE_BY_SESSION_UUID);
        
        retrieveStatement.setString(1, sessionUuid);
        
        resultSet = retrieveStatement.executeQuery(); 
        
        if (resultSet != null && resultSet.next()) {
          distributedLedger = loadFromResultSet(resultSet);
        }
        
        return distributedLedger;
    
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

    public static DistributedLedger retrieveByUserUuidSessionUuidAndSignOnUuid(String userUuid, String sessionUuid, String signOnUuid) {

      logger.info("###Entering");
      
      DistributedLedger distributedLedger = null;
    
      Connection connection = null;
      PreparedStatement retrieveStatement = null;
      ResultSet resultSet = null;
    
      try {
        connection = C3P0DataSource.getInstance().getConnection(); 
        
        retrieveStatement = connection.prepareStatement(RETRIEVE_BY_USER_UUID_SESSION_UUID_AND_SIGN_ON_UUID);
        
        retrieveStatement.setString(1, userUuid);
        retrieveStatement.setString(2, sessionUuid);
        retrieveStatement.setString(3, signOnUuid);
        
        resultSet = retrieveStatement.executeQuery();
        
        if (resultSet != null && resultSet.next()) {
          distributedLedger = loadFromResultSet(resultSet);
        }
        
        return distributedLedger;
    
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

    public static DistributedLedger retrieveByDistributedLedgerCodeUserUuidAndSessionUuid(String authenticationCode, 
                                                                                    String userUuid, String sessionUuid) {

      logger.info("###Entering");
      
      DistributedLedger distributedLedger = null;
    
      Connection connection = null;
      PreparedStatement retrieveStatement = null;
      ResultSet resultSet = null;
    
      try {
        connection = C3P0DataSource.getInstance().getConnection(); 
        
        retrieveStatement = connection.prepareStatement(RETRIEVE_BY_AUTHENTICATION_CODE_USER_UUID_AND_SESSION_UUID);

        retrieveStatement.setString(1, authenticationCode);
        retrieveStatement.setString(2, userUuid);
        retrieveStatement.setString(3, sessionUuid);
        
        resultSet = retrieveStatement.executeQuery(); 
        
        if (resultSet != null && resultSet.next()) {
          distributedLedger = loadFromResultSet(resultSet);
        }
        
        return distributedLedger;
    
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

    public static DistributedLedger retrieveByDistributedLedgerCodeAndUserUuid(String authenticationCode, String userUuid) {

      logger.info("###Entering");
      
      DistributedLedger distributedLedger = null;
    
      Connection connection = null;
      PreparedStatement retrieveStatement = null;
      ResultSet resultSet = null;
    
      try {
        connection = C3P0DataSource.getInstance().getConnection(); 
        
        retrieveStatement = connection.prepareStatement(RETRIEVE_BY_AUTHENTICATION_CODE_AND_USER_UUID);

        retrieveStatement.setString(1, authenticationCode);
        retrieveStatement.setString(2, userUuid);
        retrieveStatement.setString(3, authenticationCode);
        retrieveStatement.setString(4, userUuid);
        
        resultSet = retrieveStatement.executeQuery(); 
        
        if (resultSet != null && resultSet.next()) {
          distributedLedger = loadFromResultSet(resultSet);
        }
        
        return distributedLedger;
    
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

    public static DistributedLedger retrieveByUserUuidAndCredentialType(String userUuid, String credentialType) {

      logger.info("###Entering");
      
      DistributedLedger distributedLedger = null;
    
      Connection connection = null;
      PreparedStatement retrieveStatement = null;
      ResultSet resultSet = null;
    
      try {
        connection = C3P0DataSource.getInstance().getConnection(); 
        
        retrieveStatement = connection.prepareStatement(RETRIEVE_BY_USER_UUID_AND_CREDENTIAL_TYPE);

        retrieveStatement.setString(1, userUuid);
        retrieveStatement.setString(2, credentialType);
        retrieveStatement.setString(3, userUuid);
        retrieveStatement.setString(4, credentialType);
        
        resultSet = retrieveStatement.executeQuery(); 
        
        if (resultSet != null && resultSet.next()) {
          distributedLedger = loadFromResultSet(resultSet);
        }
        
        return distributedLedger;
    
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
    
    private static DistributedLedger loadFromResultSet(ResultSet resultSet) {
      
      try {
        DistributedLedger distributedLedger = new DistributedLedger();
        
        distributedLedger.setDistributedLedgerId(resultSet.getInt("distributedLedgerId")); 
        distributedLedger.setCreated(resultSet.getTimestamp("created"));
        distributedLedger.setUpdated(resultSet.getTimestamp("updated"));
        distributedLedger.setUpdatedById(resultSet.getInt("updatedById"));
        distributedLedger.setDataSourceTypeValue(resultSet.getInt("dataSourceTypeValue"));

        distributedLedger.setDistributedLedgerUuid(resultSet.getString("distributedLedgerUuid"));
        distributedLedger.setJsonDistributedLedger(resultSet.getString("jsonDistributedLedger"));
        distributedLedger.setCredentialProviderUuid(resultSet.getString("credentialProviderUuid"));
        distributedLedger.setCredentialType(resultSet.getString("credentialType"));
        distributedLedger.setCredentialUuid(resultSet.getString("credentialUuid"));
        distributedLedger.setAuthenticationCode(resultSet.getString("authenticationCode"));
        distributedLedger.setVerificationCode(resultSet.getString("verificationCode"));
        distributedLedger.setUserUuid(resultSet.getString("userUuid"));
        distributedLedger.setScreenName(resultSet.getString("screenName"));
        distributedLedger.setEmail(resultSet.getString("email"));
        distributedLedger.setSessionUuid(resultSet.getString("sessionUuid"));
        distributedLedger.setSignatureCompleteUuid(resultSet.getString("signatureCompleteUuid"));
        
        return distributedLedger;
        
      } catch (Exception e) {
        logger.error("", e);
        throw new RuntimeException(e);
      }
    }
  }







