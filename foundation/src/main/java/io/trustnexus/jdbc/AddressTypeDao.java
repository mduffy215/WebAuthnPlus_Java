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

package io.trustnexus.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.trustnexus.model.AddressType;
import io.trustnexus.util.C3P0DataSource;

public class AddressTypeDao {

    private final static Logger logger = LogManager.getLogger(AddressTypeDao.class);

    public static final String INSERT = "INSERT AddressType (created, updated, updatedById, dataSourceTypeValue, "
            + "addressType, addressTypeLabel) " + "VALUES (?, ?, ?, ?, ?, ?)";

    public static final String RETRIEVE_BY_ADDRESS_TYPE = "SELECT * FROM AddressType " + "WHERE addressType = ?";

    // ------------------------------------------------------------------------------------------------------------------

    public AddressTypeDao() {}
    
    // ------------------------------------------------------------------------------------------------------------------

    public static void create(AddressType addressType, Connection connection) {

        logger.info("###Entering");

        if (connection == null) {
            connection = C3P0DataSource.getInstance().getConnection();
        }

        PreparedStatement insertStatement = null;

        try {
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            addressType.setCreated(timestamp);
            addressType.setUpdated(timestamp);

            insertStatement = connection.prepareStatement(INSERT);

            insertStatement.setTimestamp(1, addressType.getCreated());
            insertStatement.setTimestamp(2, addressType.getUpdated());
            insertStatement.setInt(3, addressType.getUpdatedById());
            insertStatement.setInt(4, addressType.getDataSourceTypeValue());

            insertStatement.setInt(5, addressType.getAddressType());
            insertStatement.setString(6, addressType.getAddressTypeLabel());

            insertStatement.execute();

        } catch (Exception e) {
            logger.error("", e);
            throw new RuntimeException(e);
        } finally {
            C3P0DataSource.getInstance().closeStatement(insertStatement);
            // C3P0DataSource.getInstance().closeConnection(connection); Done in
            // InitializeTypes.main();
        }
    }

    // ------------------------------------------------------------------------------------------------------------------

    public static AddressType retrieveByAddressType(Integer addressType) {

        logger.info("###Entering");

        AddressType addressTypeObject = null;

        Connection connection = null;
        PreparedStatement retrieveStatement = null;
        ResultSet resultSet = null;

        try {
            connection = C3P0DataSource.getInstance().getConnection();

            retrieveStatement = connection.prepareStatement(RETRIEVE_BY_ADDRESS_TYPE);

            retrieveStatement.setInt(1, addressType);

            resultSet = retrieveStatement.executeQuery();

            if (resultSet != null && resultSet.next()) {
                addressTypeObject = loadFromResultSet(resultSet);
            }

            return addressTypeObject;

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

    public static AddressType loadFromResultSet(ResultSet resultSet) {

        try {
            AddressType addressType = new AddressType();

            addressType.setAddressTypeId(resultSet.getInt("addressTypeId"));
            addressType.setCreated(resultSet.getTimestamp("created"));
            addressType.setUpdated(resultSet.getTimestamp("updated"));
            addressType.setUpdatedById(resultSet.getInt("updatedById"));
            addressType.setDataSourceTypeValue(resultSet.getInt("dataSourceTypeValue"));

            addressType.setAddressType(resultSet.getInt("addressType"));
            addressType.setAddressTypeLabel(resultSet.getString("addressTypeLabel"));

            return addressType;

        } catch (Exception e) {
            logger.error("", e);
            throw new RuntimeException(e);
        }
    }
}
