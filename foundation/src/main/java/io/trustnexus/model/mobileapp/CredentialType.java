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

public class CredentialType extends EntityBase {

  private Integer credentialTypeId;
  
  private String credentialProviderUuid;
  private CredentialProvider credentialProvider;
  
  private String publicPrivateKeyUuid;
  private String credentialTypeName;
  private String displayName;
  private String credentialIconUrl;
  private int expirationMonths;
  
  // ------------------------------------------------------------------------------------------------------------------

  public CredentialType() {}

  // ------------------------------------------------------------------------------------------------------------------

  public Integer getCredentialTypeId() {
    return credentialTypeId;
  }

  public void setCredentialTypeId(Integer credentialTypeId) {
    this.credentialTypeId = credentialTypeId;
  }

  // ------------------------------------------------------------------------------------------------------------------

  public String getCredentialProviderUuid() {
    return credentialProviderUuid;
  }

  public void setCredentialProviderUuid(String credentialProviderUuid) {
    this.credentialProviderUuid = credentialProviderUuid;
  }

  // ------------------------------------------------------------------------------------------------------------------

  public CredentialProvider getCredentialProvider() {
		return credentialProvider;
	}

	public void setCredentialProvider(CredentialProvider credentialProvider) {
		this.credentialProvider = credentialProvider;
	}

  // ------------------------------------------------------------------------------------------------------------------

	public String getPublicPrivateKeyUuid() {
		return publicPrivateKeyUuid;
	}

	public void setPublicPrivateKeyUuid(String publicPrivateKeyUuid) {
		this.publicPrivateKeyUuid = publicPrivateKeyUuid;
	}

  // ------------------------------------------------------------------------------------------------------------------

	public String getCredentialTypeName() {
    return credentialTypeName;
  }

  public void setCredentialTypeName(String credentialTypeName) {
    this.credentialTypeName = credentialTypeName;
  }

  // ------------------------------------------------------------------------------------------------------------------

  public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

  // ------------------------------------------------------------------------------------------------------------------

	public String getCredentialIconUrl() {
    return credentialIconUrl;
  }

  public void setCredentialIconUrl(String credentialIconUrl) {
    this.credentialIconUrl = credentialIconUrl;
  }

  // ------------------------------------------------------------------------------------------------------------------

	public int getExpirationMonths() {
		return expirationMonths;
	}

	public void setExpirationMonths(int expirationMonths) {
		this.expirationMonths = expirationMonths;
	}

  // ------------------------------------------------------------------------------------------------------------------

  @Override
  public String toString() {

    return "\n             credentialId: " + credentialTypeId + 
           "\n                  created: " + getCreated() + 
           "\n                  updated: " + getUpdated() + 
           "\n              updatedById: " + getUpdatedById() + 
           "\n      dataSourceTypeValue: " + getDataSourceTypeValue() +            

         "\n\n   credentialProviderUuid:" + credentialProviderUuid +           
           "\n     publicPrivateKeyUuid:" + publicPrivateKeyUuid + 
           "\n       credentialTypeName:" + credentialTypeName +
           "\n              displayName:" + displayName + 
           "\n        credentialIconUrl:" + credentialIconUrl +
           "\n         expirationMonths:" + expirationMonths; 
  }
}







