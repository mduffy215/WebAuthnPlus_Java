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

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class PropertyManager {
	
	// TODO:  Update for internationalization.  getProperty(Locale locale, String key)
  
  // TODO:  Do an initial load of all properties into the ServletContect.

  private final static Logger logger = LogManager.getLogger(PropertyManager.class);

	private Properties configProperties = new Properties();
	private Properties applicationProperties = new Properties();

	private static Object lock = new Object();
	private static PropertyManager instance = null;
	
	// ------------------------------------------------------------------------------------------------------------------

	private PropertyManager() {
	}
	
	// ------------------------------------------------------------------------------------------------------------------

	public static PropertyManager getInstance()  {
		if (instance == null) {
			synchronized (lock) {
				if (instance == null) {
					instance = new PropertyManager();
					try {
						instance.loadProperties();
					} catch (IOException e) {
						logger.error(e);
					}
				}
			}
		}
		return (instance);
	}
	
	// ------------------------------------------------------------------------------------------------------------------

	private void loadProperties() throws IOException {

		ClassLoader loader = Thread.currentThread().getContextClassLoader();
  	InputStream inputStream = null;

  	try {

  		inputStream = loader.getResourceAsStream("config.properties");
  		configProperties.load(inputStream);

  		inputStream = loader.getResourceAsStream("application.properties");
  		applicationProperties.load(inputStream);
  		
  		logger.debug("Load complete.");

  	} catch (Exception e) {
  		logger.error(e);
  	} finally {
  		if (inputStream != null) {
  			try {
  				inputStream.close();
  			} catch (IOException e) {
      		logger.error(e);
  			}
  		}
  	}
	}
	
	// ------------------------------------------------------------------------------------------------------------------

	public Properties getConfigProperties() {
		return configProperties;
	}

	public void setConfigProperties(Properties configProperties) {
		this.configProperties = configProperties;
	}
	
	// ------------------------------------------------------------------------------------------------------------------

	public Properties getApplicationProperties() {
		return applicationProperties;
	}

	public void setApplicationProperties(Properties applicationProperties) {
		this.applicationProperties = applicationProperties;
	}
	
	// ------------------------------------------------------------------------------------------------------------------

	public String getProperty(String key) {
	  
	  // logging from this method caused an infinite loop from Utilities.buildUserLog(...)
		
		// logger.debug(key);
		
		String value = null;
		
		if (key != null) {
			value = configProperties.getProperty(key);
			// logger.debug(value);
			if (value == null) {
				value = applicationProperties.getProperty(key);
				// logger.debug(value);
			}
		}
		
		return value;
	}
  
  // ------------------------------------------------------------------------------------------------------------------

  public Integer getInteger(String key) {
    
    // logging from this method cause an infinite loop from Utilities.buildUserLog(...)
    
    // logger.debug(key);
    
    String value = null;
    
    if (key != null) {
      value = configProperties.getProperty(key);
      // logger.debug(value);
      if (value == null) {
        value = applicationProperties.getProperty(key);
        // logger.debug(value);
      }
    }
    
    if (value == null) {
      return null;
    } else {      
      return Integer.valueOf(value);
    }
  }
}







