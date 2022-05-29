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

public class SecurityKey extends EntityBase {

  private Integer securityKeyId;
  
  private String obfuscatedIdentifier;
  private String userSecurityKeyEncrypted;

  // ------------------------------------------------------------------------------------------------------------------

  public SecurityKey() {}

  // ------------------------------------------------------------------------------------------------------------------

  public Integer getSecurityKeyId() {
    return securityKeyId;
  }

  public void setSecurityKeyId(Integer securityKeyId) {
    this.securityKeyId = securityKeyId;
  }

  // ------------------------------------------------------------------------------------------------------------------

  public String getObfuscatedIdentifier() {
		return obfuscatedIdentifier;
	}

	public void setObfuscatedIdentifier(String obfuscatedIdentifier) {
		this.obfuscatedIdentifier = obfuscatedIdentifier;
	}

  // ------------------------------------------------------------------------------------------------------------------

  public String getUserSecurityKeyEncrypted() {
    return userSecurityKeyEncrypted;
  }

	public void setUserSecurityKeyEncrypted(String userSecurityKeyEncrypted) {
    this.userSecurityKeyEncrypted = userSecurityKeyEncrypted;
  }
}







