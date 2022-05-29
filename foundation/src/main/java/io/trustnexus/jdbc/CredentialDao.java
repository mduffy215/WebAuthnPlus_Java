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

import io.trustnexus.jdbc.mobileapp.CredentialTypeDao;
import io.trustnexus.model.Credential;
import io.trustnexus.model.mobileapp.CredentialType;
import io.trustnexus.util.C3P0DataSource;
import io.trustnexus.util.Constants;

public class CredentialDao {

  private final static Logger logger = LogManager.getLogger(CredentialDao.class);

  public static final String INSERT = "INSERT Credential (created, updated, updatedById, dataSourceTypeValue, credentialTypeId, "
                                    + "credentialProviderSignatureAlgorithm, credentialProviderSecureHashAlgorithm, "
                                    + "credentialUuid, activationTimestamp, expirationTimestamp, screenName, email, "
                                    + "userUuid, firstName, lastName, mobilePhone, authenticationCode, sessionUuid, "
                                    + "remoteAddress, remoteHost, verificationCode, publicKeyHex, json, inactiveFlag) " 
                                    + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"; 

  private static final String RETRIEVE_BY_CREDENTIAL_UUID = "SELECT * FROM Credential " 
                                                          + "WHERE credentialUuid = ? "; 

  private static final String RETRIEVE_BY_CREDENTIAL_TYPE_AND_USER_UUID 
                              = "SELECT * FROM Credential CRED " 
                              + "JOIN CredentialType CT ON CRED.credentialTypeId = CT.credentialTypeId " 
                              + "WHERE CT.credentialType = ? AND userUuid = ? "
                              + "AND credentialUuid IS NOT NULL AND screenName IS NOT NULL "
                              + "AND email IS NOT NULL AND verificationCode IS NOT NULL "
                              + "AND inactiveFlag = FALSE";

  private static final String RETRIEVE_BY_USER_UUID_AND_SESSION_UUID = "SELECT * FROM Credential " 
                                                                     + "WHERE userUuid = ? AND sessionUuid = ? "
                                                                     + "AND created > DATE_SUB(NOW(), INTERVAL 4 MINUTE)";

  private static final String DELETE_BY_CREDENTIAL_UUID = "DELETE FROM Credential " 
                                                        + "WHERE credentialUuid = ?";

  private static final String DELETE_BY_CREDENTIAL_ID = "DELETE FROM Credential " + "WHERE credentialId = ?";

  private static final String DELETE_BY_SESSION_UUID 
                              = "DELETE FROM Credential "
                              + "WHERE sessionUuid = ?";

  public static final String CANCEL_CREATE = "UPDATE Credential SET updated = ?, verificationCode = ? "
                                           + "WHERE userUuid = ? AND sessionUuid = ?";

  public static final String UPDATE = "UPDATE Credential SET updated = ?, credentialUuid = ?, screenName = ?, "
                                    + "email = ?, userUuid = ?, firstName = ?, lastName = ?, mobilePhone = ?, "
                                    + "verificationCode = ?, publicKeyHex = ?, json = ? WHERE authenticationCode = ?";

  // ------------------------------------------------------------------------------------------------------------------

  public CredentialDao() {
  }

  // ------------------------------------------------------------------------------------------------------------------

  public static void create(Credential credential) {

    logger.info("###Entering");

    Connection connection = null;
    PreparedStatement insertStatement = null;

    try {
      Timestamp timestamp = new Timestamp(System.currentTimeMillis());
      credential.setCreated(timestamp);
      credential.setUpdated(timestamp);

      connection = C3P0DataSource.getInstance().getConnection();

      insertStatement = connection.prepareStatement(INSERT);

      insertStatement.setTimestamp(1, credential.getCreated());
      insertStatement.setTimestamp(2, credential.getUpdated());
      insertStatement.setInt(3, credential.getUpdatedById());
      insertStatement.setInt(4, credential.getDataSourceTypeValue());

      insertStatement.setInt(5, credential.getCredentialTypeId());
      insertStatement.setString(6, credential.getCredentialProviderSignatureAlgorithm());
      insertStatement.setString(7, credential.getCredentialProviderSecureHashAlgorithm());
      
      insertStatement.setString(8, credential.getCredentialUuid());
      insertStatement.setString(9, credential.getActivationTimestamp());
      insertStatement.setString(10, credential.getExpirationTimestamp());
      insertStatement.setString(11, credential.getScreenName());
      insertStatement.setString(12, credential.getEmail());
      insertStatement.setString(13, credential.getUserUuid());
      insertStatement.setString(14, credential.getFirstName());
      insertStatement.setString(15, credential.getLastName());
      insertStatement.setString(16, credential.getMobilePhone());
      insertStatement.setString(17, credential.getAuthenticationCode());
      insertStatement.setString(18, credential.getSessionUuid());
      insertStatement.setString(19, credential.getRemoteAddress());
      insertStatement.setString(20, credential.getRemoteHost());
      insertStatement.setString(21, credential.getVerificationCode());
      insertStatement.setString(22, credential.getPublicKeyHex());
      insertStatement.setString(23, credential.getJson());
      insertStatement.setBoolean(24, credential.isInactiveFlag());

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

  public static Credential retrieveByCredentialUuid(String credentialUuid) {

    logger.info("###Entering");

    Credential credential = null;

    Connection connection = null;
    PreparedStatement retrieveStatement = null;
    ResultSet resultSet = null;

    try {
      connection = C3P0DataSource.getInstance().getConnection();

      retrieveStatement = connection.prepareStatement(RETRIEVE_BY_CREDENTIAL_UUID);

      retrieveStatement.setString(1, credentialUuid);

      resultSet = retrieveStatement.executeQuery();

      if (resultSet != null && resultSet.next()) {
        credential = loadFromResultSet(resultSet);
      }

      return credential;

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

  public static Credential retrieveByCredentialTypeAndUserUuid(String credentialType, String userUuid) {

    logger.info("###Entering");

    Credential credential = null;

    Connection connection = null;
    PreparedStatement retrieveStatement = null;
    ResultSet resultSet = null;

    try {
      connection = C3P0DataSource.getInstance().getConnection();

      retrieveStatement = connection.prepareStatement(RETRIEVE_BY_CREDENTIAL_TYPE_AND_USER_UUID);

      retrieveStatement.setString(1, credentialType);
      retrieveStatement.setString(2, userUuid);

      resultSet = retrieveStatement.executeQuery();

      if (resultSet != null && resultSet.next()) {
        credential = loadFromResultSet(resultSet);
      }

      return credential;

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

  public static Credential retrieveByUserUuidAndSessionUuid(String userUuid, String sessionUuid) { 

    logger.info("###Entering");

    Credential credential = null;

    Connection connection = null;
    PreparedStatement retrieveStatement = null;
    ResultSet resultSet = null;

    try {
      connection = C3P0DataSource.getInstance().getConnection();

      retrieveStatement = connection.prepareStatement(RETRIEVE_BY_USER_UUID_AND_SESSION_UUID);

      retrieveStatement.setString(1, userUuid);
      retrieveStatement.setString(2, sessionUuid);

      resultSet = retrieveStatement.executeQuery();

      if (resultSet != null && resultSet.next()) {
        credential = loadFromResultSet(resultSet);
      }

      return credential;

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

  public static void deleteByCredentialUuid(String credentialUuid) {

    logger.info("###Entering");

    Connection connection = null;
    PreparedStatement deleteStatement = null;

    try {
      connection = C3P0DataSource.getInstance().getConnection();

      deleteStatement = connection.prepareStatement(DELETE_BY_CREDENTIAL_UUID);
      deleteStatement.setString(1, credentialUuid);
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

  public static void deleteBySessionUuid(String sessionUuid) {

    logger.info("###Entering");

    Connection connection = null;
    PreparedStatement deleteStatement = null;

    try {
      connection = C3P0DataSource.getInstance().getConnection();

      deleteStatement = connection.prepareStatement(DELETE_BY_SESSION_UUID);
      deleteStatement.setString(1, sessionUuid);
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

  public static void cancelCreate(String userUuid, String sessionUuid) {

    logger.info("###Entering");

    Connection connection = null;
    PreparedStatement updateStatement = null;

    try {
      connection = C3P0DataSource.getInstance().getConnection();

      updateStatement = connection.prepareStatement(CANCEL_CREATE);
      
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

  public static void update(Credential credential) {

    logger.info("###Entering");

    Connection connection = null;
    PreparedStatement updateStatement = null;

    try {
      connection = C3P0DataSource.getInstance().getConnection();

      updateStatement = connection.prepareStatement(UPDATE);

      updateStatement.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
      updateStatement.setString(2, credential.getCredentialUuid());
      updateStatement.setString(3, credential.getScreenName());
      updateStatement.setString(4, credential.getEmail());
      updateStatement.setString(5, credential.getUserUuid());
      updateStatement.setString(6, credential.getFirstName());
      updateStatement.setString(7, credential.getLastName());
      updateStatement.setString(8, credential.getMobilePhone());
      updateStatement.setString(9, credential.getVerificationCode());
      updateStatement.setString(10, credential.getPublicKeyHex());
      updateStatement.setString(11, credential.getJson());
      updateStatement.setString(12, credential.getAuthenticationCode());

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

  private static Credential loadFromResultSet(ResultSet resultSet) {

    try {
      Credential credential = new Credential();

      credential.setCredentialId(resultSet.getInt("credentialId"));
      credential.setCreated(resultSet.getTimestamp("created"));
      credential.setUpdated(resultSet.getTimestamp("updated"));
      credential.setUpdatedById(resultSet.getInt("updatedById"));
      credential.setDataSourceTypeValue(resultSet.getInt("dataSourceTypeValue"));

      credential.setCredentialProviderSignatureAlgorithm(resultSet.getString("credentialProviderSignatureAlgorithm"));
      credential.setCredentialProviderSecureHashAlgorithm(resultSet.getString("credentialProviderSecureHashAlgorithm"));
      
      credential.setCredentialTypeId(resultSet.getInt("credentialTypeId"));
      credential.setCredentialUuid(resultSet.getString("credentialUuid"));
      credential.setActivationTimestamp(resultSet.getString("activationTimestamp"));   
      credential.setExpirationTimestamp(resultSet.getString("expirationTimestamp"));
      
      credential.setScreenName(resultSet.getString("screenName"));
      credential.setEmail(resultSet.getString("email"));
      credential.setUserUuid(resultSet.getString("userUuid"));
      credential.setFirstName(resultSet.getString("firstName"));
      credential.setLastName(resultSet.getString("lastName"));
      credential.setMobilePhone(resultSet.getString("mobilePhone"));
      credential.setAuthenticationCode(resultSet.getString("authenticationCode"));
      credential.setSessionUuid(resultSet.getString("sessionUuid"));
      credential.setRemoteAddress(resultSet.getString("remoteAddress"));
      credential.setRemoteHost(resultSet.getString("remoteHost"));
      credential.setVerificationCode(resultSet.getString("verificationCode"));
      credential.setPublicKeyHex(resultSet.getString("publicKeyHex"));
      credential.setJson(resultSet.getString("json"));
      credential.setInactiveFlag(resultSet.getBoolean("inactiveFlag"));
      
      CredentialType credentialType = CredentialTypeDao.retrieve(credential.getCredentialTypeId());
      credential.setCredentialType(credentialType); 

      return credential;

    } catch (Exception e) {
      logger.error("", e);
      throw new RuntimeException(e);
    }
  }
}







