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

package io.trustnexus.model;

public class Visitor extends EntityBase {

  private Integer visitorId;
  
  private String refCode;
  private String remoteAddress;
  private String remoteHost;

  // ------------------------------------------------------------------------------------------------------------------

  public Visitor() {}

  // ------------------------------------------------------------------------------------------------------------------

  public Integer getVisitorId() {
    return visitorId;
  }

  public void setVisitorId(Integer visitorId) {
    this.visitorId = visitorId;
  }

  // ------------------------------------------------------------------------------------------------------------------

  public String getRefCode() {
    return refCode;
  }

  public void setRefCode(String refCode) {
    this.refCode = refCode;
  }

  // ------------------------------------------------------------------------------------------------------------------

  public String getRemoteAddress() {
    return remoteAddress;
  }

  public void setRemoteAddress(String remoteAddress) {
    this.remoteAddress = remoteAddress;
  }

  // ------------------------------------------------------------------------------------------------------------------

  public String getRemoteHost() {
    return remoteHost;
  }

  public void setRemoteHost(String remoteHost) {
    this.remoteHost = remoteHost;
  }
}







