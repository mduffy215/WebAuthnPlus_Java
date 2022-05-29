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

import java.util.ArrayList;
import java.util.Iterator;

import io.trustnexus.model.mobileapp.CredentialType;
import io.trustnexus.util.Constants;
import io.trustnexus.util.CryptoUtilities;

public class Credential extends EntityBase {

  private Integer credentialId;
  
  private int credentialTypeId;
  private CredentialType credentialType;
  
  private String credentialUuid;
  private String activationTimestamp;
  private String expirationTimestamp;
  private String userUuid;
  private String screenName;
  private String email;
  private String firstName;
  private String lastName;
  private String mobilePhone;
  private String authenticationCode;
  private String sessionUuid;
  private String remoteAddress;
  private String remoteHost;
  private String verificationCode;
  private String publicKeyHex;

	private Organization organization;
  private ArrayList<CredentialAddress> addressList = new ArrayList<CredentialAddress>();

  private String credentialProviderSignatureAlgorithm;
  private String credentialProviderSecureHashAlgorithm;

  private String json;
  private boolean inactiveFlag;

  // ------------------------------------------------------------------------------------------------------------------

	public Credential() {}

  // ------------------------------------------------------------------------------------------------------------------

  public Integer getCredentialId() {
    return credentialId;
  }

  public void setCredentialId(Integer credentialId) {
    this.credentialId = credentialId;
  }

  // ------------------------------------------------------------------------------------------------------------------ 

  public int getCredentialTypeId() {
		return credentialTypeId;
	} 

	public void setCredentialTypeId(int credentialTypeId) {
		this.credentialTypeId = credentialTypeId;
	}

  // ------------------------------------------------------------------------------------------------------------------

	public CredentialType getCredentialType() {
		return credentialType;
	} 

	public void setCredentialType(CredentialType credentialType) {
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

  public String getActivationTimestamp() {
		return activationTimestamp;
	}

	public void setActivationTimestamp(String activationTimestamp) {
		this.activationTimestamp = activationTimestamp;
	}

  // ------------------------------------------------------------------------------------------------------------------

	public String getExpirationTimestamp() {
		return expirationTimestamp;
	}

	public void setExpirationTimestamp(String expirationTimestamp) {
		this.expirationTimestamp = expirationTimestamp;
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
  
  public String getUserUuid() {
    return userUuid;
  }

  public void setUserUuid(String userUuid) {
    this.userUuid = userUuid;
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

  public String getMobilePhone() {
    return mobilePhone;
  }

  public void setMobilePhone(String phone) {
    this.mobilePhone = phone;
  }

  // ------------------------------------------------------------------------------------------------------------------

  public String getAuthenticationCode() {
    return authenticationCode;
  }

  public void setAuthenticationCode(String authenticationCode) {
    this.authenticationCode = authenticationCode;
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

  public String getVerificationCode() {
    return verificationCode;
  }

  public void setVerificationCode(String verificationCode) {
    this.verificationCode = verificationCode;
  }
  
  // ------------------------------------------------------------------------------------------------------------------
  
  public String getPublicKeyHex() {
    return publicKeyHex;
  }

  public void setPublicKeyHex(String publicKeyHex) {
    this.publicKeyHex = publicKeyHex;
  }
  
  // ------------------------------------------------------------------------------------------------------------------
  
  public Organization getOrganization() {
		return organization;
	}

	public void setOrganization(Organization organization) {
		this.organization = organization;
	}
  
  // ------------------------------------------------------------------------------------------------------------------

	public ArrayList<CredentialAddress> getAddressList() {
		return addressList;
	}

	public void setAddressList(ArrayList<CredentialAddress> addressList) {
		this.addressList = addressList;
	}
	
	public void adddCredentialAddress(CredentialAddress credentialAddress) {
		this.addressList.add(credentialAddress);
	}
  
  // ------------------------------------------------------------------------------------------------------------------

  public String getCredentialProviderSignatureAlgorithm() {
		return credentialProviderSignatureAlgorithm;
	}

	public void setCredentialProviderSignatureAlgorithm(String credentialProviderSignatureAlgorithm) {
		this.credentialProviderSignatureAlgorithm = credentialProviderSignatureAlgorithm;
	}
  
  // ------------------------------------------------------------------------------------------------------------------

	public String getCredentialProviderSecureHashAlgorithm() {
		return credentialProviderSecureHashAlgorithm;
	}

	public void setCredentialProviderSecureHashAlgorithm(String credentialProviderSecureHashAlgorithm) {
		this.credentialProviderSecureHashAlgorithm = credentialProviderSecureHashAlgorithm;
	}
  
  // ------------------------------------------------------------------------------------------------------------------

	public String getJson() {
		return json;
	}

	public void setJson(String json) {
		this.json = json;
	}

  // ------------------------------------------------------------------------------------------------------------------

  public boolean isInactiveFlag() {
    return inactiveFlag;
  }

  public void setInactiveFlag(boolean inactiveFlag) {
    this.inactiveFlag = inactiveFlag;
  }
  
  // ------------------------------------------------------------------------------------------------------------------

  public boolean hasCredentialBeenCreated() {
    /*
     * The verificationCode is generated within the user's mobile app and is only 
     * entered into the system if the user's public/private key signature is valid.
     */
    if (this.credentialUuid == null || this.screenName == null || this.email == null || this.verificationCode == null) {
      return false;
    } else {
      return true;  
    }
  }
  
  // ------------------------------------------------------------------------------------------------------------------
  
  public String generateJsonCredntial() {
  	 
  	String jsonCredential = "\"" + Constants.CREDENTIAL + "\":[{"
  			                  + "\n\"" + Constants.TYPE + "\":\"" + Constants.CREDENTIAL + "\","
  	  			              + "\n\"" + Constants.CREDENTIAL_PROVIDER_NAME + "\":\"" + this.credentialType.getCredentialProvider().getCredentialProviderName() + "\","
  	  	  			          + "\n\"" + Constants.CREDENTIAL_PROVIDER_UUID + "\":\"" + this.credentialType.getCredentialProvider().getCredentialProviderUuid() + "\","
  			                  + "\n\"" + Constants.CREDENTIAL_DISPLAY_NAME + "\":\"" + this.credentialType.getDisplayName() + "\","
  			                  + "\n\"" + Constants.CREDENTIAL_TYPE + "\":\"" + this.credentialType.getCredentialTypeName() + "\","
  			                  + "\n\"" + Constants.CREDENTIAL_UUID + "\":\"" + this.credentialUuid + "\","
  			                  + "\n\"" + Constants.ACTIVATION_TIMESTAMP + "\":\"" + this.activationTimestamp + "\","
  			                  + "\n\"" + Constants.EXPIRATION_TIMESTAMP + "\":\"" + this.expirationTimestamp + "\","
  			                  + "\n\"" + Constants.PUBLIC_KEY_ALGORITHM + "\":\"" + CryptoUtilities.KEY_FACTORY_ALGORITHM + "\","
  			                  + "\n\"" + Constants.PUBLIC_KEY_MODULUS + "\":\"" + CryptoUtilities.PUBLIC_KEY_MODULUS + "\","
  			                  + "\n\"" + Constants.PUBLIC_KEY_HEX + "\":\"" + this.publicKeyHex + "\","
  			                  + "\n\"" + Constants.CREDENTIAL_DATA + "\":[{"
  			                  + "\n\"" + Constants.USER_UUID + "\":\"" + this.userUuid + "\","
  			                  + "\n\"" + Constants.SCREEN_NAME + "\":\"" + this.screenName + "\","
  			                  + "\n\"" + Constants.EMAIL + "\":\"" + this.email + "\","
  			                  + "\n\"" + Constants.FIRST_NAME+ "\":\"" + this.firstName + "\","
  			                  + "\n\"" + Constants.LAST_NAME + "\":\"" + this.lastName + "\","
  			                  + "\n\"" + Constants.MOBILE_PHONE + "\":\"" + this.mobilePhone + "\",";
  			                  
  	for (Iterator<CredentialAddress> iterator = addressList.iterator(); iterator.hasNext();) {
			CredentialAddress credentialAddress = (CredentialAddress) iterator.next();	 		
 
			     jsonCredential += "\n\"" + Constants.ADDRESS + "\":[{"   
                          + "\n\"" + Constants.ADDRESS_TYPE + "\":\"" + credentialAddress.getAddressTypeObject().getAddressTypeLabel() + "\","    
                          + "\n\"" + Constants.ADDRESS_LINE_ONE + "\":\"" + credentialAddress.getAddressLineOne() + "\","   
                          + "\n\"" + Constants.ADDRESS_LINE_TWO + "\":\"" + credentialAddress.getAddressLineTwo() + "\","   
                          + "\n\"" + Constants.ADDRESS_CITY + "\":\"" + credentialAddress.getCity() + "\","   
                          + "\n\"" + Constants.ADDRESS_STATE_PROVINCE + "\":\"" + credentialAddress.getStateOrProvince() + "\","   
                          + "\n\"" + Constants.ADDRESS_POSTAL_CODE + "\":\"" + credentialAddress.getPostalCode() + "\","   
                          + "\n\"" + Constants.ADDRESS_COUNTRY + "\":\"" + credentialAddress.getCountry() + "\","           
                          + "\n}]";			
		}
  	
  	if (this.getOrganization() != null && this.getOrganization().getOrganizationName() != null) {		

	         jsonCredential += "\n\"" + Constants.ORGANIZATION + "\":[{"  
                          + "\n\"" + Constants.ORGANIZATION_NAME + "\":\"" + this.getOrganization().getOrganizationName() + "\","  
                          + "\n\"" + Constants.ORGANIZATION_URL + "\":\"" + this.getOrganization().getOrganizationUrl() + "\","  
                          + "\n\"" + Constants.ORGANIZATION_TITLE + "\":\"" + this.getOrganization().getTitle() + "\","  
                          + "\n\"" + Constants.ORGANIZATION_PHONE + "\":\"" + this.getOrganization().getPhone() + "\",";
                          
      if (this.getOrganization().getOrganizationAddress() != null) {      	
      	
           jsonCredential += "\n\"" + Constants.ADDRESS + "\":[{" 
                          + "\n\"" + Constants.ADDRESS_TYPE + "\":\"" + this.getOrganization().getOrganizationAddress().getAddressTypeObject().getAddressTypeLabel() + "\","    
                          + "\n\"" + Constants.ADDRESS_LINE_ONE + "\":\"" + this.getOrganization().getOrganizationAddress().getAddressLineOne() + "\","   
                          + "\n\"" + Constants.ADDRESS_LINE_TWO + "\":\"" + this.getOrganization().getOrganizationAddress().getAddressLineTwo() + "\","   
                          + "\n\"" + Constants.ADDRESS_CITY + "\":\"" + this.getOrganization().getOrganizationAddress().getCity() + "\","   
                          + "\n\"" + Constants.ADDRESS_STATE_PROVINCE + "\":\"" + this.getOrganization().getOrganizationAddress().getStateOrProvince() + "\","   
                          + "\n\"" + Constants.ADDRESS_POSTAL_CODE + "\":\"" + this.getOrganization().getOrganizationAddress().getPostalCode() + "\","   
                          + "\n\"" + Constants.ADDRESS_COUNTRY + "\":\"" + this.getOrganization().getOrganizationAddress().getCountry() + "\","           
                          + "\n}]";														
			}
                          
           jsonCredential += "\n}]"; // end organization			
		}
  	
           jsonCredential += "\n}]"; // end credentialData
 	 
 	         jsonCredential += "\n\"" + Constants.CREDENTIAL_PROVIDER_SECURE_HASH_ALGORITHM + "\":\"" + this.getCredentialProviderSecureHashAlgorithm() + "\","
 	                        + "\n\"" + Constants.CREDENTIAL_PROVIDER_SIGNATURE_ALGORITHM + "\":\"" + this.getCredentialProviderSignatureAlgorithm() + "\",";
  	
  	return jsonCredential;
  }
  
  
  
  // ------------------------------------------------------------------------------------------------------------------

  @Override
  public String toString() {

    return "\n             credentialId: " + credentialId + 
           "\n                  created: " + getCreated() + 
           "\n                  updated: " + getUpdated() + 
           "\n              updatedById: " + getUpdatedById() + 
           "\n      dataSourceTypeValue: " + getDataSourceTypeValue() + 
            
         "\n\n         credentialTypeId: " + credentialTypeId + 
           "\n           credentialUuid: " + credentialUuid + 
           "\n               screenName: " + screenName +  
           "\n                    email: " + email +  
           "\n                 userUuid: " + userUuid + 
           
         "\n\n                firstName: " + firstName +  
           "\n                 lastName: " + lastName +  
           "\n                    phone: " + mobilePhone +  
           "\n       authenticationCode: " + authenticationCode +  
           "\n              sessionUuid: " + sessionUuid +   
           "\n            remoteAddress: " + remoteAddress +   
           "\n               remoteHost: " + remoteHost + 
           "\n         verificationCode: " + verificationCode +   
           "\n             inactiveFlag: " + inactiveFlag +   
           
         "\n\n             publicKeyHex: " + publicKeyHex ;
  }
}







