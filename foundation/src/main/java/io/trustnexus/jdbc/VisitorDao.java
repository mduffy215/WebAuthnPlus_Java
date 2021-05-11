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

import io.trustnexus.model.Visitor;
import io.trustnexus.util.C3P0DataSource;

public class VisitorDao {

  private final static Logger logger = LogManager.getLogger(VisitorDao.class);

  public static final String INSERT 
                             = "INSERT Visitor (created, updated, updatedById, dataSourceTypeValue, remoteAddress, remoteHost, refCode)"
                           	 + "VALUES (?, ?, ?, ?, ?, ?, ?)";

  // ------------------------------------------------------------------------------------------------------------------
  
  public VisitorDao() {    
  }

  // ------------------------------------------------------------------------------------------------------------------

  public static void create(Visitor visitor) {

    logger.info("###Entering");
  
    Connection connection = null;
    PreparedStatement insertStatement = null;
  
    try {
      Timestamp timestamp = new Timestamp(System.currentTimeMillis());
      visitor.setCreated(timestamp);
      visitor.setUpdated(timestamp);
      
      connection = C3P0DataSource.getInstance().getConnection(); 
      
      insertStatement = connection.prepareStatement(INSERT);
      
      insertStatement.setTimestamp(1, visitor.getCreated());
      insertStatement.setTimestamp(2, visitor.getUpdated());
      insertStatement.setInt(3, visitor.getUpdatedById());
      insertStatement.setInt(4, visitor.getDataSourceTypeValue());
      insertStatement.setString(5, visitor.getRemoteAddress());
      insertStatement.setString(6, visitor.getRemoteHost());
      insertStatement.setString(7, visitor.getRefCode());
      
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
  
  @SuppressWarnings("unused")
  private static Visitor loadFromResultSet(ResultSet resultSet) {
    
    try {
      Visitor visitor = new Visitor();
      
      visitor.setVisitorId(resultSet.getInt("visitorId")); 
      visitor.setCreated(resultSet.getTimestamp("created"));
      visitor.setUpdated(resultSet.getTimestamp("updated"));
      visitor.setUpdatedById(resultSet.getInt("updatedById"));
      visitor.setDataSourceTypeValue(resultSet.getInt("dataSourceTypeValue"));
      visitor.setRemoteAddress(resultSet.getString("remoteAddress"));
      visitor.setRemoteHost(resultSet.getString("remoteHost"));
      visitor.setRefCode(resultSet.getString("refCode"));
      
      return visitor;
    } catch (Exception e) {
      logger.error("", e);
      throw new RuntimeException(e);
    }
  }
}








