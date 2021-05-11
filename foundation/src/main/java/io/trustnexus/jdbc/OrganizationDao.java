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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.trustnexus.model.Organization;
import io.trustnexus.util.C3P0DataSource;

public class OrganizationDao {

  private final static Logger logger = LogManager.getLogger(OrganizationDao.class);

  public static final String INSERT 
                             = "INSERT Organization (created, updated, updatedById, dataSourceTypeValue, " 
                             + "credentialId, organizationName, organizationUrl, title, phone) " 
                             + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

  private static final String RETRIEVE_BY_CREDENTIAL_ID 
                              = "SELECT * FROM Organization "
                              + "WHERE credentialId = ?";

  private static final String DELETE_BY_CREDENTIAL_ID 
                              = "DELETE FROM Organization "
                              + "WHERE credentialId = ?";

  // ------------------------------------------------------------------------------------------------------------------
  
  public OrganizationDao() {    
  }

  // ------------------------------------------------------------------------------------------------------------------

  public static void create(Organization organization) {

    logger.info("###Entering");
  
    Connection connection = null;
    PreparedStatement insertStatement = null;
  
    try {
      Timestamp timestamp = new Timestamp(System.currentTimeMillis());
      organization.setCreated(timestamp);
      organization.setUpdated(timestamp);
      
      connection = C3P0DataSource.getInstance().getConnection(); 
      
      insertStatement = connection.prepareStatement(INSERT);
      
      insertStatement.setTimestamp(1, organization.getCreated());
      insertStatement.setTimestamp(2, organization.getUpdated());
      insertStatement.setInt(3, organization.getUpdatedById());
      insertStatement.setInt(4, organization.getDataSourceTypeValue());

      insertStatement.setInt(5, organization.getCredentialId());
      insertStatement.setString(6, organization.getOrganizationName());
      insertStatement.setString(7, organization.getOrganizationUrl());
      insertStatement.setString(8, organization.getTitle());
      insertStatement.setString(9, organization.getPhone());
      
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

  public static Organization retrieveByCredentialId(Integer credentialId) {

    logger.info("###Entering");
    
    Organization organization = null;
  
    Connection connection = null;
    PreparedStatement retrieveStatement = null;
    ResultSet resultSet = null;
  
    try {
      connection = C3P0DataSource.getInstance().getConnection(); 
      
      retrieveStatement = connection.prepareStatement(RETRIEVE_BY_CREDENTIAL_ID);
      
      retrieveStatement.setInt(1, credentialId);
      
      resultSet = retrieveStatement.executeQuery(); 
      
      if (resultSet != null && resultSet.next()) {
        organization = loadFromResultSet(resultSet);
      }
      
      return organization;
  
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
  
  private static Organization loadFromResultSet(ResultSet resultSet) {
    
    try {
      Organization organization = new Organization();
      
      organization.setOrganizationId(resultSet.getInt("organizationId")); 
      organization.setCreated(resultSet.getTimestamp("created"));
      organization.setUpdated(resultSet.getTimestamp("updated"));
      organization.setUpdatedById(resultSet.getInt("updatedById"));
      organization.setDataSourceTypeValue(resultSet.getInt("dataSourceTypeValue"));
      
      organization.setCredentialId(resultSet.getInt("credentialId"));
      organization.setOrganizationName(resultSet.getString("organizationName"));
      organization.setOrganizationUrl(resultSet.getString("organizationUrl"));
      organization.setTitle(resultSet.getString("title"));
      organization.setPhone(resultSet.getString("phone"));
      
      return organization;
      
    } catch (Exception e) {
      logger.error("", e);
      throw new RuntimeException(e);
    }
  }
}







