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

public class AddressType extends EntityBase {

  private Integer addressTypeId;
  
  private Integer addressType;
  private String addressTypeLabel;
  
  // ------------------------------------------------------------------------------------------------------------------

	public AddressType() {
	}
  
  // ------------------------------------------------------------------------------------------------------------------

	public Integer getAddressTypeId() {
		return addressTypeId;
	}

	public void setAddressTypeId(Integer addressTypeId) {
		this.addressTypeId = addressTypeId;
	}
  
  // ------------------------------------------------------------------------------------------------------------------

	public Integer getAddressType() {
		return addressType;
	}

	public void setAddressType(Integer addressType) {
		this.addressType = addressType;
	}
  
  // ------------------------------------------------------------------------------------------------------------------

	public String getAddressTypeLabel() {
		return addressTypeLabel;
	}

	public void setAddressTypeLabel(String addressTypeLabel) {
		this.addressTypeLabel = addressTypeLabel;
	}
}







