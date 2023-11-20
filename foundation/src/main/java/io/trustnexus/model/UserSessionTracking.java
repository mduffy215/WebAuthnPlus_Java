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

package io.trustnexus.model;

import io.trustnexus.model.EntityBase;

public class UserSessionTracking extends EntityBase {

  private Integer userSessionTrackingId;
  
  private String userIdentifier;
  private String sessionIdentifier;

  // ------------------------------------------------------------------------------------------------------------------

  public UserSessionTracking() {}

  // ------------------------------------------------------------------------------------------------------------------

  public Integer getUserSessionTrackingId() {
		return userSessionTrackingId;
	}

	public void setUserSessionTrackingId(Integer userSessionTrackingId) {
		this.userSessionTrackingId = userSessionTrackingId;
	}

  // ------------------------------------------------------------------------------------------------------------------

  public String getUserIdentifier() {
    return userIdentifier;
  }

	public void setUserIdentifier(String userIdentifier) {
    this.userIdentifier = userIdentifier;
  }

  // ------------------------------------------------------------------------------------------------------------------

	public String getSessionIdentifier() {
		return sessionIdentifier;
	}

	public void setSessionIdentifier(String sessionIdentifier) {
		this.sessionIdentifier = sessionIdentifier;
	}
}







