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

public class Message {
  
  private String lineOne;
  private String lineTwo;
  private String lineThree;
  private String lineFour;
  private String lineFive;
  
  // ------------------------------------------------------------------------------------------------------------------

  public Message() {
  }
  
  // ------------------------------------------------------------------------------------------------------------------

  public String getLineOne() {
  	if (lineOne == null) {
	    return "";
    } else {
      return lineOne;
    }
  }

  public void setLineOne(String lineOne) {
    this.lineOne = lineOne;
  }
  
  // ------------------------------------------------------------------------------------------------------------------

  public String getLineTwo() {
  	if (lineTwo == null) {
	    return "";
    } else {
      return lineTwo;
    }
  }

  public void setLineTwo(String lineTwo) {
    this.lineTwo = lineTwo;
  }
  
  // ------------------------------------------------------------------------------------------------------------------

  public String getLineThree() {
  	if (lineThree == null) {
	    return "";
    } else {
      return lineThree;
    }
  }

  public void setLineThree(String lineThree) {
    this.lineThree = lineThree;
  }
  
  // ------------------------------------------------------------------------------------------------------------------

  public String getLineFour() {
  	if (lineFour == null) {
	    return "";
    } else {
      return lineFour;
    }
  }

  public void setLineFour(String lineFour) {
    this.lineFour = lineFour;
  }
  
  // ------------------------------------------------------------------------------------------------------------------

  public String getLineFive() {
  	if (lineFive == null) {
	    return "";
    } else {
      return lineFive;
    }
  }

  public void setLineFive(String lineFive) {
    this.lineFive = lineFive;
  }
}







