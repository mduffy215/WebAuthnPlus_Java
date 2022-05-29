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

public class Organization extends EntityBase {

  private Integer organizationId;

  private Integer credentialId;
  private String organizationName;
  private String organizationUrl;
  private String title;
  private String phone;
  
  private CredentialAddress organizationAddress;

  // ------------------------------------------------------------------------------------------------------------------

	public Organization() {    
  }

  // ------------------------------------------------------------------------------------------------------------------

  public Integer getOrganizationId() {
    return organizationId;
  }

  public void setOrganizationId(Integer organizationId) {
    this.organizationId = organizationId;
  }

  // ------------------------------------------------------------------------------------------------------------------

  public Integer getCredentialId() {
    return credentialId;
  }

  public void setCredentialId(Integer credentialId) {
    this.credentialId = credentialId;
  }

  // ------------------------------------------------------------------------------------------------------------------

  public String getOrganizationName() {
    return organizationName;
  }

  public void setOrganizationName(String organizationName) {
    this.organizationName = organizationName;
  }

  // ------------------------------------------------------------------------------------------------------------------

  public String getOrganizationUrl() {
    return organizationUrl;
  }

  public void setOrganizationUrl(String organizationUrl) {
    this.organizationUrl = organizationUrl;
  }

  // ------------------------------------------------------------------------------------------------------------------

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  // ------------------------------------------------------------------------------------------------------------------

  public String getPhone() {
    return phone;
  }

  public void setPhone(String phone) {
    this.phone = phone;
  }

  // ------------------------------------------------------------------------------------------------------------------

  public CredentialAddress getOrganizationAddress() {
		return organizationAddress;
	}

	public void setOrganizationAddress(CredentialAddress organizationAddress) {
		this.organizationAddress = organizationAddress;
	}
}







