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

package io.trustnexus.model.mobileapp;

import io.trustnexus.model.EntityBase;

public class User extends EntityBase {

  private Integer userId;
  
  private String screenName;
  private String email;
  private String phone;
  private String firstName;
  private String lastName;
  private String userUuid;
  private String publicKey;
  private boolean inactiveFlag;
  private String refCode;
  private String firebaseDeviceId;

  // ------------------------------------------------------------------------------------------------------------------

  public User() {}

  // ------------------------------------------------------------------------------------------------------------------

  public Integer getUserId() {
    return userId;
  }

  public void setUserId(Integer userId) {
    this.userId = userId;
  }

  // ------------------------------------------------------------------------------------------------------------------

  public String getScreenName() {
    return screenName;
  }

  public void setScreenName(String screenName) {
    this.screenName = screenName;
  }

  // ------------------------------------------------------------------------------------------------------------------

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  // ------------------------------------------------------------------------------------------------------------------
  
  public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

  // ------------------------------------------------------------------------------------------------------------------

	public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  // ------------------------------------------------------------------------------------------------------------------

  public String getLastName() {
    return lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  // ------------------------------------------------------------------------------------------------------------------

  public String getUserUuid() {
    return userUuid;
  }

  public void setUserUuid(String userUuid) {
    this.userUuid = userUuid;
  }

  // ------------------------------------------------------------------------------------------------------------------

  public String getPublicKey() {
    return publicKey;
  }

  public void setPublicKey(String publicKey) {
    this.publicKey = publicKey;
  }

  // ------------------------------------------------------------------------------------------------------------------

  public boolean isInactiveFlag() {
    return inactiveFlag;
  }

  public void setInactiveFlag(boolean inactiveFlag) {
    this.inactiveFlag = inactiveFlag;
  }

  // ------------------------------------------------------------------------------------------------------------------

  public String getRefCode() {
    return refCode;
  }

  public void setRefCode(String refCode) {
    this.refCode = refCode;
  }

  // ------------------------------------------------------------------------------------------------------------------

	public String getFirebaseDeviceId() {
		return firebaseDeviceId;
	}

	public void setFirebaseDeviceId(String firebaseDeviceId) {
		this.firebaseDeviceId = firebaseDeviceId;
	}
}







