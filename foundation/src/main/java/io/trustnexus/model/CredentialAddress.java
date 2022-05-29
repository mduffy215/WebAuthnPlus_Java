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

package io.trustnexus.model;

public class CredentialAddress extends EntityBase implements Comparable<CredentialAddress> {

  private Integer credentialAddressId;

  private Integer credentialId;  
  private Integer addressType;  
  private AddressType addressTypeObject;
  private String addressLineOne;
  private String addressLineTwo;
  private String city;
  private String stateOrProvince;
  private String postalCode;
  private String country;

  // ------------------------------------------------------------------------------------------------------------------

  public CredentialAddress() {}

  // ------------------------------------------------------------------------------------------------------------------

  public Integer getCredentialAddressId() {
    return credentialAddressId;
  }

  public void setCredentialAddressId(Integer credentialAddressId) {
    this.credentialAddressId = credentialAddressId;
  }

  // ------------------------------------------------------------------------------------------------------------------
  
  public Integer getCredentialId() {
    return credentialId;
  }

  public void setCredentialId(Integer credentialId) {
    this.credentialId = credentialId;
  }

  // ------------------------------------------------------------------------------------------------------------------

  public Integer getAddressType() {
    return addressType;
  }

  public void setAddressType(Integer addressType) {
    this.addressType = addressType;
  }

  // ------------------------------------------------------------------------------------------------------------------

  public AddressType getAddressTypeObject() {
		return addressTypeObject;
	}

	public void setAddressTypeObject(AddressType addressTypeObject) {
		this.addressTypeObject = addressTypeObject;
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

  public String getStateOrProvince() {
    return stateOrProvince;
  }

  public void setStateOrProvince(String stateOrProvince) {
    this.stateOrProvince = stateOrProvince;
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

  // ------------------------------------------------------------------------------------------------------------------

	@Override
	public int compareTo(CredentialAddress obj) {
    return this.addressType > obj.addressType ? 1 : this.addressType < obj.addressType ? -1 : 0; 
	}
}







