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

package io.trustnexus.util;

import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mchange.v2.c3p0.ComboPooledDataSource;

public class C3P0DataSource {

  private static C3P0DataSource dataSource;
  private ComboPooledDataSource comboPooledDataSource;
  
  public static Timestamp connTime;
  public static int minPoolSize;
  public static int maxPoolSize;
  public static int numConnections;
  public static int numBusyConnections;
  public static int numIdleConnections;

  public static int activeConnectionCount;

  private final static Logger logger = LogManager.getLogger(C3P0DataSource.class);

  private C3P0DataSource() {
    try {
      comboPooledDataSource = new ComboPooledDataSource();
      comboPooledDataSource.setDriverClass(PropertyManager.getInstance().getProperty(Constants.DRIVER_CLASS));
      comboPooledDataSource.setJdbcUrl(PropertyManager.getInstance().getProperty(Constants.JDBC_URL));
      comboPooledDataSource.setUser(PropertyManager.getInstance().getProperty(Constants.DATA_BASE_USER));
      comboPooledDataSource.setPassword(PropertyManager.getInstance().getProperty(Constants.DATA_BASE_PASSWORD));
      
      connTime= new Timestamp(System.currentTimeMillis());
    } catch (PropertyVetoException e) {
      throw new RuntimeException(e);
    }
  }
  
  // ------------------------------------------------------------------------------------------------------------------

  public static C3P0DataSource getInstance() { 
    if (dataSource == null)
      dataSource = new C3P0DataSource();
    return dataSource;
  }
  
  // ------------------------------------------------------------------------------------------------------------------

  public Connection getConnection() {
    Connection connection = null;
    try {
      
      connection = comboPooledDataSource.getConnection();
      activeConnectionCount++;    

      minPoolSize = comboPooledDataSource.getMinPoolSize();
      maxPoolSize = comboPooledDataSource.getMaxPoolSize();
      numConnections = comboPooledDataSource.getNumConnectionsDefaultUser();
      numBusyConnections = comboPooledDataSource.getNumBusyConnectionsDefaultUser();
      numIdleConnections = comboPooledDataSource.getNumIdleConnectionsDefaultUser();
      
//      logger.info("@@@@@@@@@@@@@@@@ minPoolSize: " + minPoolSize + "  maxPoolSize: " + maxPoolSize + "  numConnections: " + numConnections 
//          + "  numBusyConnections: " + numBusyConnections + "  numIdleeConnections: " + numIdleConnections + "  activeConnectionCount: " + activeConnectionCount);
      
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
    return connection;
  }
  
  // ------------------------------------------------------------------------------------------------------------------
  
  public void logStatus() {   

    try {
      minPoolSize = comboPooledDataSource.getMinPoolSize();
      maxPoolSize = comboPooledDataSource.getMaxPoolSize();
      numConnections = comboPooledDataSource.getNumConnectionsDefaultUser();
      numBusyConnections = comboPooledDataSource.getNumBusyConnectionsDefaultUser();
      numIdleConnections = comboPooledDataSource.getNumIdleConnectionsDefaultUser();
      
      logger.info("\n\n\n\n\n\n\n\n################ minPoolSize: " + minPoolSize + "  maxPoolSize: " + maxPoolSize + "  numConnections: " + numConnections 
          + "  numBusyConnections: " + numBusyConnections + "  numIdleeConnections: " + numIdleConnections + "  activeConnectionCount: " + activeConnectionCount);
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }   
  }

  // ------------------------------------------------------------------------------------------------------------------

  public void closeConnection(Connection connection) {

    if (connection != null) {

      try {
        connection.close();
        activeConnectionCount--;
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }
  }

  // ------------------------------------------------------------------------------------------------------------------

  public void closeResultSet(ResultSet resultSet) {

    if (resultSet != null) {

      try {
        resultSet.close();
      } catch (SQLException e) {
        throw new RuntimeException(e);
      }
    }
  }

  // ------------------------------------------------------------------------------------------------------------------

  public void closeStatement(Statement statement) {

    if (statement != null) {

      try {
        statement.close();
      } catch (SQLException e) {
        throw new RuntimeException(e);
      }
    }
  }
}







