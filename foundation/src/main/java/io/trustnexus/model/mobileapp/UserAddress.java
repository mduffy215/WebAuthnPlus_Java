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

package io.trustnexus.model.mobileapp;

import io.trustnexus.model.EntityBase;

public class UserAddress extends EntityBase {

  private Integer userAddressId;

  private Integer userId;
  private Integer addressType;
  private String addressLineOne;
  private String addressLineTwo;
  private String city;
  private String state;
  private String postalCode;
  private String country;

  // ------------------------------------------------------------------------------------------------------------------

  public UserAddress() {}

  // ------------------------------------------------------------------------------------------------------------------

  public Integer getUserAddressId() {
    return userAddressId;
  }

  public void setUserAddressId(Integer userAddressId) {
    this.userAddressId = userAddressId;
  }

  // ------------------------------------------------------------------------------------------------------------------
  
  public Integer getUserId() {
    return userId;
  }

  public void setUserId(Integer userId) {
    this.userId = userId;
  }

  // ------------------------------------------------------------------------------------------------------------------

  public Integer getAddressType() {
    return addressType;
  }

  public void setAddressType(Integer addressType) {
    this.addressType = addressType;
  }

  // ------------------------------------------------------------------------------------------------------------------

  public String getAddressLineOne() {
    return addressLineOne;
  }

  public void setAddressLineOne(String addressLineOne) {
    this.addressLineOne = addressLineOne;
  }

  // ------------------------------------------------------------------------------------------------------------------

  public String getAddressLineTwo() {
    return addressLineTwo;
  }

  public void setAddressLineTwo(String addressLineTwo) {
    this.addressLineTwo = addressLineTwo;
  }

  // ------------------------------------------------------------------------------------------------------------------

  public String getCity() {
    return city;
  }

  public void setCity(String city) {
    this.city = city;
  }

  // ------------------------------------------------------------------------------------------------------------------

  public String getState() {
    return state;
  }

  public void setState(String state) {
    this.state = state;
  }

  // ------------------------------------------------------------------------------------------------------------------

  public String getPostalCode() {
    return postalCode;
  }

  public void setPostalCode(String postalCode) {
    this.postalCode = postalCode;
  }

  // ------------------------------------------------------------------------------------------------------------------

  public String getCountry() {
    return country;
  }

  public void setCountry(String country) {
    this.country = country;
  }
}







