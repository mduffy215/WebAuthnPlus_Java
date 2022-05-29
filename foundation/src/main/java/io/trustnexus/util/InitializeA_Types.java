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

package io.trustnexus.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import io.trustnexus.jdbc.AddressTypeDao;
import io.trustnexus.model.AddressType;

public class InitializeA_Types {

	public InitializeA_Types() {
	}

	/*
	 * This main method will initialize type values in the data structures.
	 * Right now, the only type is ADDRESS_TYPE.
	 */
	public static void main(String[] args) {
    
    // ------------------------------------------------------------------------

    Connection connection = null;
    

    try {
      Class.forName("com.mysql.cj.jdbc.Driver");
      connection = DriverManager.getConnection(Constants.SYSTEM_INIT_DATABASE_URL, 
                                               Constants.SYSTEM_INIT_DATABASE_USERNAME, 
                                               Constants.SYSTEM_INIT_DATABASE_PASSWORD);

      System.out.println("Connection established: " + connection.toString());
      
      // ---------------------------------------------------------------------- 
      
      AddressType addressType = new AddressType();

      addressType.setUpdatedById(Constants.MASTER_USER);
      addressType.setDataSourceTypeValue(Constants.DATA_SOURCE_APPLICATION);
      addressType.setAddressType(Constants.ADDRESS_TYPE_LEGAL);
      addressType.setAddressTypeLabel(PropertyManager.getInstance().getProperty(Constants.ADDRESS_TYPE_LABEL_LEGAL));
      
      AddressTypeDao.create(addressType, connection); 
      
      addressType.setAddressType(Constants.ADDRESS_TYPE_MAILING);
      addressType.setAddressTypeLabel(PropertyManager.getInstance().getProperty(Constants.ADDRESS_TYPE_LABEL_MAILING));
      
      AddressTypeDao.create(addressType, connection); 
      
      addressType.setAddressType(Constants.ADDRESS_TYPE_LEGAL_AND_MAILING);
      addressType.setAddressTypeLabel(PropertyManager.getInstance().getProperty(Constants.ADDRESS_TYPE_LABEL_LEGAL_AND_MAILING));
      
      AddressTypeDao.create(addressType, connection);  
      
      addressType.setAddressType(Constants.ADDRESS_TYPE_ORGANIZATION);
      addressType.setAddressTypeLabel(PropertyManager.getInstance().getProperty(Constants.ADDRESS_TYPE_LABEL_ORGANIZATION));
      
      AddressTypeDao.create(addressType, connection);    

      System.out.println("\nAddress types created.");  
     
    } catch (Exception e) {
      throw new RuntimeException(e);
    } finally {    	    	
      try {      
        connection.close();
      } catch (SQLException e) {
        throw new RuntimeException(e);
      }
    }
  }
}







