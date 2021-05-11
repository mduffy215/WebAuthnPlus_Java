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

import io.trustnexus.util.Constants;

public class Authentication extends EntityBase {

  private Integer authenticationId;
  
  private String serverSessionId;
  private String credentialProviderUuid;
  private String credentialType;
  private String credentialUuid;
  private String authenticationCode;
  private String verificationCode;
  private String userUuid;
  private String screenName;
  private String email;
  private String sessionUuid;
  private String remoteAddress;
  private String remoteHost;
  private String signOnUuid;

  // ------------------------------------------------------------------------------------------------------------------

  public Authentication() {}

  // ------------------------------------------------------------------------------------------------------------------

  public Integer getAuthenticationId() {
    return authenticationId;
  }

  public void setAuthenticationId(Integer authenticationId) {
    this.authenticationId = authenticationId;
  }

  // ------------------------------------------------------------------------------------------------------------------

  public String getServerSessionId() {
    return serverSessionId;
  }

  public void setServerSessionId(String serverSessionId) {
    this.serverSessionId = serverSessionId;
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

  // ------------------------------------------------------------------------------------------------------------------

	public String getAuthenticationCode() {
    return authenticationCode;
  }

  public void setAuthenticationCode(String authenticationCode) {
    this.authenticationCode = authenticationCode;
  }

  // ------------------------------------------------------------------------------------------------------------------

  public String getVerificationCode() {
    return verificationCode;
  }

  public void setVerificationCode(String verificationCode) {
    this.verificationCode = verificationCode;
  }

  // ------------------------------------------------------------------------------------------------------------------

  public String getUserUuid() {
    return userUuid;
  }

  public void setUserUuid(String userUuid) {
    this.userUuid = userUuid;
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

  public String getSessionUuid() {
    return sessionUuid;
  }

  public void setSessionUuid(String sessionUuid) {
    this.sessionUuid = sessionUuid;
  }
  
  // ------------------------------------------------------------------------------------------------------------------

  public boolean isCanceled() {
    if (this.verificationCode != null && this.verificationCode.equalsIgnoreCase(Constants.CANCELED)) {
      return true;
    } else {
      return false;
    }
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

  // ------------------------------------------------------------------------------------------------------------------

	public String getSignOnUuid() {
		return signOnUuid;
	}

	public void setSignOnUuid(String signOnUuid) {
		this.signOnUuid = signOnUuid;
	}

  // ------------------------------------------------------------------------------------------------------------------
	
	public boolean isSessionValid () {
		if (this.verificationCode == null) {
			return true; 
		} else if (this.verificationCode.equalsIgnoreCase(Constants.SESSION_INVALIDATED)
			      || this.verificationCode.equalsIgnoreCase(Constants.SESSION_TIMEOUT)
            || this.verificationCode.equalsIgnoreCase(Constants.CANCELED)) {
			return false;
		} else {
			return true; 
		}
	}

  // ------------------------------------------------------------------------------------------------------------------
	
	public boolean isUserSignedOn () {
		if (this.verificationCode != null && !this.verificationCode.equalsIgnoreCase(Constants.SESSION_INVALIDATED)
			      && !this.verificationCode.equalsIgnoreCase(Constants.SESSION_TIMEOUT)
			      && !this.verificationCode.equalsIgnoreCase(Constants.CANCELED)) {
			return true;
		} else {
			return false; 
		}
	}

  // ------------------------------------------------------------------------------------------------------------------

	@Override
	public String toString() {
		return "Authentication [authenticationId=" + authenticationId
				+ ", \ncredentialProviderUuid=" + credentialProviderUuid
				+ ", \ncredentialType=" + credentialType + ", \nauthenticationCode="
				+ authenticationCode + ", \nverificationCode=" + verificationCode
				+ ", \nuserUuid=" + userUuid + ", \nscreenName=" + screenName
				+ ", \nemail=" + email + ", \nsessionUuid=" + sessionUuid
				+  ", \nsignOnUuid=" + signOnUuid 
				+ "]";
	}
}







