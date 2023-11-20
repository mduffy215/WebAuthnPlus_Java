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

public class TrustedSystem extends EntityBase {

  private Integer trustedSystemId;
  
  private String email;
  private String userUuid;
  private String trustedSystemUuid;
  private String passwordHash;
  private String credentialProviderUuid;
  private String credentialType;
  private String credentialUuid;
  
  // ------------------------------------------------------------------------------------------------------------------

  public TrustedSystem() {
  }
  
  // ------------------------------------------------------------------------------------------------------------------

  public Integer getTrustedSystemId() {
    return trustedSystemId;
  }

  public void setTrustedSystemId(Integer trustedSystemId) {
    this.trustedSystemId = trustedSystemId;
  }
  
  // ------------------------------------------------------------------------------------------------------------------

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }
  
  // ------------------------------------------------------------------------------------------------------------------

  public String getUserUuid() {
    return userUuid;
  }

  public void setUserUuid(String userUuid) {
    this.userUuid = userUuid;
  }
  
  // ------------------------------------------------------------------------------------------------------------------

  public String getTrustedSystemUuid() {
    return trustedSystemUuid;
  }

  public void setTrustedSystemUuid(String trustedSystemUuid) {
    this.trustedSystemUuid = trustedSystemUuid;
  }
  
  // ------------------------------------------------------------------------------------------------------------------

  public String getPasswordHash() {
    return passwordHash;
  }

  public void setPasswordHash(String passwordHash) {
    this.passwordHash = passwordHash;
  }
  
  // ------------------------------------------------------------------------------------------------------------------

  public String getCredentialProviderUuid() {
    return credentialProviderUuid;
  }

  public void setCredentialProviderUuid(String credentialProviderUuid) {
    this.credentialProviderUuid = credentialProviderUuid;
  }
  
  // ------------------------------------------------------------------------------------------------------------------

  public String getCredentialType() {
    return credentialType;
  }

  public void setCredentialType(String credentialType) {
    this.credentialType = credentialType;
  }
  
  // ------------------------------------------------------------------------------------------------------------------

  public String getCredentialUuid() {
    return credentialUuid;
  }

  public void setCredentialUuid(String credentialUuid) {
    this.credentialUuid = credentialUuid;
  }
}







