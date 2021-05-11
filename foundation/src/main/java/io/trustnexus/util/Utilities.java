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

package io.trustnexus.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.TimeZone;
import java.util.TreeMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.trustnexus.jdbc.UserSessionTrackingDao;

public class Utilities {

	private static Logger logger = LogManager.getLogger(Utilities.class); 
  
  private static HashSet<String> sensitiveWordsFour;
  private static HashSet<String> sensitiveWordsThree;
  
  static {
    
    sensitiveWordsFour = new HashSet<String>(); 
    
    for (int i = 0; i < Constants.SENSITIVE_WORDS_FOUR.length; i++) {
      sensitiveWordsFour.add(Constants.SENSITIVE_WORDS_FOUR[i]);
    }
    
    sensitiveWordsThree = new HashSet<String>(); 
    
    for (int i = 0; i < Constants.SENSITIVE_WORDS_THREE.length; i++) {
      sensitiveWordsThree.add(Constants.SENSITIVE_WORDS_THREE[i]);
    }
  }

  // ------------------------------------------------------------------------------------------------------------------ 

	public static void main(String[] args) {
		
		String isoTimeStamp = new Timestamp(System.currentTimeMillis()).toString();
		
		String test = generateIsoTimestamp(System.currentTimeMillis());
		System.out.println(isoTimeStamp + "\n" + test);
	}

  // ------------------------------------------------------------------------------------------------------------------  

  public static String generateRegistrationCode() {

    logger.info("###Entering");
    
    boolean sensitive = true;
    String registrationCode = null;
    
    while (sensitive) {

      int characterOne = (int) Math.floor(Constants.CHARACTER_ARRAY.length * Math.random());
      int characterTwo = (int) Math.floor(Constants.CHARACTER_ARRAY.length * Math.random());
      int characterThree = (int) Math.floor(Constants.CHARACTER_ARRAY.length * Math.random());
      int characterFour = (int) Math.floor(Constants.CHARACTER_ARRAY.length * Math.random());
      
      registrationCode = "" + Constants.CHARACTER_ARRAY[characterOne] + Constants.CHARACTER_ARRAY[characterTwo] + Constants.CHARACTER_ARRAY[characterThree] + Constants.CHARACTER_ARRAY[characterFour];
      
      sensitive = sensitiveWordsFour.contains(registrationCode); 
    }

    return registrationCode;
  }

  // ------------------------------------------------------------------------------------------------------------------  

  public static String generateVerificationCode() {

    logger.info("###Entering");
    
    boolean sensitive = true;
    String verificationCode = null;
    
    double mathRandom = 1000 * Math.random();

    while (mathRandom < 100) {
      mathRandom = mathRandom * 10;
    }
    
    while (sensitive) {

      int characterOne = (int) Math.floor(Constants.CHARACTER_ARRAY.length * Math.random());
      int characterTwo = (int) Math.floor(Constants.CHARACTER_ARRAY.length * Math.random());
      int characterThree = (int) Math.floor(Constants.CHARACTER_ARRAY.length * Math.random());
  
      verificationCode = "" + Constants.CHARACTER_ARRAY[characterOne] + Constants.CHARACTER_ARRAY[characterTwo] + Constants.CHARACTER_ARRAY[characterThree];
      
      sensitive = sensitiveWordsThree.contains(verificationCode);      
    }
    
    verificationCode += " " + (int) Math.floor(mathRandom);

    return verificationCode;
  }

  // ------------------------------------------------------------------------------------------------------------------

  public static String generateAuthenticationCode() {

    logger.info("###Entering");
    
    boolean sensitive = true;
    String authenticationCode = "";
    String authenticationCodeSegment = "";
    
    for (int i = 0; i < 3; i++) {
    	
      while (sensitive) {

        int characterOne = (int) Math.floor(Constants.CHARACTER_ARRAY.length * Math.random());
        int characterTwo = (int) Math.floor(Constants.CHARACTER_ARRAY.length * Math.random());
        int characterThree = (int) Math.floor(Constants.CHARACTER_ARRAY.length * Math.random());
        int characterFour = (int) Math.floor(Constants.CHARACTER_ARRAY.length * Math.random());
    
        authenticationCodeSegment = "" + Constants.CHARACTER_ARRAY[characterOne] + Constants.CHARACTER_ARRAY[characterTwo] + Constants.CHARACTER_ARRAY[characterThree] + Constants.CHARACTER_ARRAY[characterFour];
        
        sensitive = sensitiveWordsFour.contains(authenticationCodeSegment);       
      }
      
      sensitive = true;			
      authenticationCode += authenticationCodeSegment + " ";
		}

    return authenticationCode.trim();
  }

  // ------------------------------------------------------------------------------------------------------------------
  
  public static String parseNameValuePairs(String parseString, String name) {
    
    int begIndex = 0;
    int endIndex = 0;
    
    begIndex = parseString.indexOf(name);
    
    if (begIndex != -1) {
      
      begIndex = parseString.indexOf("=", begIndex);
      
      if (begIndex != -1) {     
        
        begIndex ++;
        endIndex = parseString.indexOf("&", begIndex);
        
        if (endIndex != -1) {
          return parseString.substring(begIndex, endIndex).trim();
        } else {
          return parseString.substring(begIndex).trim();
        }
        
      } else {
        return "";
      } 
      
    } else {
      return ""; 
    }
  }

  // ------------------------------------------------------------------------------------------------------------------
  
  public static String parseJsonNameValuePairs(String parseString, String name) {
    
    int begIndex = 0;
    int endIndex = 0;
    
    begIndex = parseString.indexOf(name);
    
    if (begIndex != -1) {
      
      begIndex = parseString.indexOf(":", begIndex);
      
      if (begIndex != -1) {     
        
        begIndex ++;
        begIndex ++;
        endIndex = parseString.indexOf("\",", begIndex);
        
        if (endIndex != -1) {
          return parseString.substring(begIndex, endIndex).trim();
        } else {
          return "";
        }
        
      } else {
        return "";
      } 
      
    } else {
      return ""; 
    }
  }

  // ------------------------------------------------------------------------------------------------------------------
  
  public static String parseJsonNameDataSet(String parseString, String name) {
    
    int begIndex = 0;
    int endIndex = 0;
    
    begIndex = parseString.indexOf(name);
    
    if (begIndex != -1) {
      
      begIndex = parseString.indexOf(":", begIndex);
      
      if (begIndex != -1) {     
        
        begIndex ++;
        endIndex = parseString.indexOf("]", begIndex);
        
        if (endIndex != -1) {
        	endIndex++;
          return parseString.substring(begIndex, endIndex).trim();
        } else {
          return "";
        }
        
      } else {
        return "";
      } 
      
    } else {
      return ""; 
    }
  }

  // ------------------------------------------------------------------------------------------------------------------

  public static String generateProductId() {

   logger.info("###Entering");

    String productId = "";
    long randomInt = Math.round((Math.random() * 10000));
    productId += randomInt +"-";
    randomInt = Math.round((Math.random() * 10000));
    productId += randomInt +"-";
    randomInt = Math.round((Math.random() * 10000));
    productId += randomInt;

    return productId;
  }

  // ------------------------------------------------------------------------------------------------------------------

	public static String buildUserLog(String userIdentifier) {
		
		StringBuilder userLog = new StringBuilder();
		
		Set<String> sessionIdentifierSet = UserSessionTrackingDao.retrieveSessionIdentifierSet(userIdentifier); 
		
		try (BufferedReader br = new BufferedReader(new FileReader(PropertyManager.getInstance().getProperty(Constants.LOG_FILE)))) {
			
	    String line = null;
	    boolean addLines = false;
	    
	    while ((line = br.readLine()) != null) {
	    	
	    	int begIndex = line.indexOf("::") + 2;
	    	int endIndex = line.lastIndexOf("::");
	    	
	    	if (begIndex != -1 && endIndex != -1) { 
		    	String sessionIdentifier = line.substring(begIndex, endIndex);
		      
		      if (sessionIdentifierSet.contains(sessionIdentifier)) {  
		      	addLines = true;
		      	 
		      	boolean continueProcessing = true;		      	

	      		int insertionPoint = line.indexOf("(com.");
		      	if (insertionPoint != -1 ) {
	      			line = line.substring(0, insertionPoint) + "<br/>" + line.substring(insertionPoint, line.length()); 
	      			insertionPoint += PropertyManager.getInstance().getInteger(Constants.LOG_FILE_LINE_LENGTH);  
						} else {
							insertionPoint = PropertyManager.getInstance().getInteger(Constants.LOG_FILE_LINE_LENGTH);
						}
		      	
		      	while (continueProcessing) {
			      	
		      		if (insertionPoint < line.length()) {
		      			line = line.substring(0, insertionPoint) + "<br/>" + line.substring(insertionPoint, line.length()); 
		      			insertionPoint += PropertyManager.getInstance().getInteger(Constants.LOG_FILE_LINE_LENGTH);
							} else {
								continueProcessing = false;
							}							
						}
		      	
		      	userLog.append(line + "<br/>");
		      	
					} else {
						addLines = false;
					}					
				} else if (addLines) {
	      	 
	      	boolean continueProcessing = true;		      	

     	  	int insertionPoint = line.indexOf("(com.");
	      	if (insertionPoint != -1 ) {
     			line = line.substring(0, insertionPoint) + "<br/>" + line.substring(insertionPoint, line.length()); 
     			insertionPoint += PropertyManager.getInstance().getInteger(Constants.LOG_FILE_LINE_LENGTH); 
					} else {
						insertionPoint = PropertyManager.getInstance().getInteger(Constants.LOG_FILE_LINE_LENGTH);
					}
	      	
	      	while (continueProcessing) {		      		
	      		if (insertionPoint < line.length()) {
	      			line = line.substring(0, insertionPoint) + "<br/>" + line.substring(insertionPoint, line.length());
	      			insertionPoint += PropertyManager.getInstance().getInteger(Constants.LOG_FILE_LINE_LENGTH);
						} else {
							continueProcessing = false;
						}							
					}
	      	
	      	userLog.append(line + "<br/>");					
				}
	    }
	  } catch (Exception e) {
      logger.error("", e);
      throw new RuntimeException(e);
	  }
		
		userLog.append("<br/>&copy; Copyright 2021 ~ Trust Nexus, Inc.");	
		
		return userLog.toString();
	}
	
	// ------------------------------------------------------------------------------------------------------------------
	
	public static String generateIsoTimestamp(long timeMillis) {
	  
	  /*
	   * Thx to Joachim Sauer. 
	   * https://stackoverflow.com/questions/3914404/how-to-get-current-moment-in-iso-8601-format-with-date-hour-and-minute
	   */

		TimeZone timeZone = TimeZone.getTimeZone("UTC");
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'"); 
		dateFormat.setTimeZone(timeZone);
		String isoTimeStamp = dateFormat.format(new Date(timeMillis));
		
		return isoTimeStamp;
	}
	
	// ------------------------------------------------------------------------------------------------------------------
	
	public static String generateRandomString(int length) {
		
		String randomString = "";
		
		for (int i = 0; i < length; i++) {
      int characterOne = (int) Math.floor(Constants.CHARACTER_ARRAY.length * Math.random());
      randomString += Constants.CHARACTER_ARRAY[characterOne];
		}
		
		return randomString;
	}
	
	// ------------------------------------------------------------------------------------------------------------------
	
	/*
	 * Thx to birryree
	 * https://stackoverflow.com/questions/10707238/locale-getdefault-returns-en-always
	 */
	public static BigDecimal parseCurrency(final String amount) throws ParseException {
    final NumberFormat format = NumberFormat.getNumberInstance(Locale.getDefault());
    if (format instanceof DecimalFormat) {
        ((DecimalFormat) format).setParseBigDecimal(true);
    }
    return (BigDecimal) format.parse(amount.replaceAll("[^\\d.,]",""));
  }	

  // ------------------------------------------------------------------------------------------------------------------

  public static String generateDistributedLedger(String distributedLedgerUuid, TreeMap<String, String> distributedLedgerSortedMap) { 

    logger.info("###Entering");
    
    String jsonDistributedLedger = "\"JSON DLT Test\":[{";
    
    jsonDistributedLedger += "\n\n\"timestamp\":\"" + Utilities.generateIsoTimestamp(System.currentTimeMillis()) + "\""; 
    jsonDistributedLedger += "\n\"distributedLedgerUuid\":\"" + distributedLedgerUuid + "\""; 
    
    for (String key : distributedLedgerSortedMap.keySet()) {      
      jsonDistributedLedger += "\n\n\"" + key + "\":\"" + distributedLedgerSortedMap.get(key) + "\""; 
    }
    
    jsonDistributedLedger += "\n}]"; 


    return jsonDistributedLedger;
  }
}







