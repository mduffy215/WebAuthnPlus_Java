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

public class CredentialProvider extends EntityBase {

  private Integer credentialProviderId;
  
  private String credentialProviderName;
  private String domainName;
  private String adminFirstName;
  private String adminLastName;
  private String adminEmail;
  private String retrieveCredentialMetaDataUrl;
  private String createCredentialUrl;
  private String deleteCredentialUrl;
  private String signOnUrl;
  private String cancelSignOnUrl;
  private String retrieveUnsignedDistributedLedgerUrl;
  private String returnSignedDistributedLedgerUrl;
  private String sendFundsUrl;
  private String acceptFundsUrl;
  private String confirmFundsUrl;
  private String receiveFundsUrl;
  private String retrieveTransactionUuidUrl;
  private String credentialProviderUuid;

  // ------------------------------------------------------------------------------------------------------------------

  public CredentialProvider() {}

  // ------------------------------------------------------------------------------------------------------------------

  public Integer getCredentialProviderId() {
    return credentialProviderId;
  }

  public void setCredentialProviderId(Integer credentialProviderId) {
    this.credentialProviderId = credentialProviderId;
  }

  // ------------------------------------------------------------------------------------------------------------------

  public String getCredentialProviderName() {
    return credentialProviderName;
  }

  public void setCredentialProviderName(String credentialProviderName) {
    this.credentialProviderName = credentialProviderName;
  }

  // ------------------------------------------------------------------------------------------------------------------

  public String getDomainName() {
    return domainName;
  }

  public void setDomainName(String domainName) {
    this.domainName = domainName;
  }

  // ------------------------------------------------------------------------------------------------------------------

  public String getAdminFirstName() {
    return adminFirstName;
  }

  public void setAdminFirstName(String adminFirstName) {
    this.adminFirstName = adminFirstName;
  }

  // ------------------------------------------------------------------------------------------------------------------

  public String getAdminLastName() {
    return adminLastName;
  }

  public void setAdminLastName(String adminLastName) {
    this.adminLastName = adminLastName;
  }

  // ------------------------------------------------------------------------------------------------------------------

  public String getAdminEmail() {
    return adminEmail;
  }

  public void setAdminEmail(String adminEmail) {
    this.adminEmail = adminEmail;
  }

  // ------------------------------------------------------------------------------------------------------------------

  public String getRetrieveCredentialMetaDataUrl() {
		return retrieveCredentialMetaDataUrl;
	}

	public void setRetrieveCredentialMetaDataUrl(
			String retrieveCredentialMetaDataUrl) {
		this.retrieveCredentialMetaDataUrl = retrieveCredentialMetaDataUrl;
	}

  // ------------------------------------------------------------------------------------------------------------------

	public String getCreateCredentialUrl() {
    return createCredentialUrl;
  }

  public void setCreateCredentialUrl(String createCredentialUrl) {
    this.createCredentialUrl = createCredentialUrl;
  }

  // ------------------------------------------------------------------------------------------------------------------

  public String getDeleteCredentialUrl() {
    return deleteCredentialUrl;
  }

  public void setDeleteCredentialUrl(String deleteCredentialUrl) {
    this.deleteCredentialUrl = deleteCredentialUrl;
  }

  // ------------------------------------------------------------------------------------------------------------------

  public String getSignOnUrl() {
    return signOnUrl;
  }

  public void setSignOnUrl(String signOnUrl) {
    this.signOnUrl = signOnUrl;
  }

  // ------------------------------------------------------------------------------------------------------------------

  public String getCancelSignOnUrl() {
    return cancelSignOnUrl;
  }

  public void setCancelSignOnUrl(String cancelSignOnUrl) {
    this.cancelSignOnUrl = cancelSignOnUrl;
  }

  // ------------------------------------------------------------------------------------------------------------------

  public String getRetrieveUnsignedDistributedLedgerUrl() {
    return retrieveUnsignedDistributedLedgerUrl;
  }

  public void setRetrieveUnsignedDistributedLedgerUrl(String retrieveUnsignedDistributedLedgerUrl) {
    this.retrieveUnsignedDistributedLedgerUrl = retrieveUnsignedDistributedLedgerUrl;
  }

  // ------------------------------------------------------------------------------------------------------------------

  public String getReturnSignedDistributedLedgerUrl() {
    return returnSignedDistributedLedgerUrl;
  }

  public void setReturnSignedDistributedLedgerUrl(String returnSignedDistributedLedgerUrl) {
    this.returnSignedDistributedLedgerUrl = returnSignedDistributedLedgerUrl;
  }

  // ------------------------------------------------------------------------------------------------------------------

  public String getSendFundsUrl() {
		return sendFundsUrl;
	}

  public void setSendFundsUrl(String sendFundsUrl) {
		this.sendFundsUrl = sendFundsUrl;
	}	
	
  // ------------------------------------------------------------------------------------------------------------------

	public String getReceiveFundsUrl() {
		return receiveFundsUrl;
	}

	public void setReceiveFundsUrl(String receiveFundsUrl) {
		this.receiveFundsUrl = receiveFundsUrl;
	}
	
  // ------------------------------------------------------------------------------------------------------------------

	public String getAcceptFundsUrl() {
		return acceptFundsUrl;
	}

	public void setAcceptFundsUrl(String acceptFundsUrl) {
		this.acceptFundsUrl = acceptFundsUrl;
	}
	
  // ------------------------------------------------------------------------------------------------------------------

	public String getConfirmFundsUrl() {
		return confirmFundsUrl;
	}

	public void setConfirmFundsUrl(String confirmFundsUrl) {
		this.confirmFundsUrl = confirmFundsUrl;
	}
	
  // ------------------------------------------------------------------------------------------------------------------

	public String getRetrieveTransactionUuidUrl() {
    return retrieveTransactionUuidUrl;
  }

  public void setRetrieveTransactionUuidUrl(String retrieveTransactionUuidUrl) {
    this.retrieveTransactionUuidUrl = retrieveTransactionUuidUrl;
  }

  // ------------------------------------------------------------------------------------------------------------------

	public String getCredentialProviderUuid() {
    return credentialProviderUuid;
  }

  public void setCredentialProviderUuid(String credentialProviderUuid) {
    this.credentialProviderUuid = credentialProviderUuid;
  }

  // ------------------------------------------------------------------------------------------------------------------ 

  @Override
  public String toString() {
    return "CredentialProvider [credentialProviderId=" + credentialProviderId 
            + ", \ncredentialProviderName=" + credentialProviderName
            + ", \ndomainName=" + domainName + ", \nadminFirstName=" + adminFirstName + ", \nadminLastName=" + adminLastName + ", \nadminEmail=" + adminEmail
            + ", \ncreateCredentialUrl=" + createCredentialUrl + ", \ndeleteCredentialUrl=" + deleteCredentialUrl
            + ", \nsignOnUrl=" + signOnUrl + ", \ncancelSignOnUrl=" + cancelSignOnUrl
            + ", \nretrieveTransactionUuidUrl=" + retrieveTransactionUuidUrl + ", \ncredentialProviderUuid=" + credentialProviderUuid + "]";
  }  
}







