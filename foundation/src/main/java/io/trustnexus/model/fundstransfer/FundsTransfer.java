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

package io.trustnexus.model.fundstransfer;

import java.math.BigDecimal;

import io.trustnexus.model.EntityBase;

public class FundsTransfer extends EntityBase {

  private Integer fundsTransferId;
  
  private String fundsTransferUuid;
  private String userUuid;
  private String credentialUuid;
  
  private String recipientName;
  private String recipientEmail;
  private String recipientPhoneNumber;

  private BigDecimal transferAmount;
  
  private String recipientCredentialProviderUuid;
  private String recipientCredentialUuid;
  private String recipientUuid;

  private String json;

  // ------------------------------------------------------------------------------------------------------------------

	public FundsTransfer() {}

  // ------------------------------------------------------------------------------------------------------------------

	public Integer getFundsTransferId() {
		return fundsTransferId;
	}

	public void setFundsTransferId(Integer fundsTransferId) {
		this.fundsTransferId = fundsTransferId;
	}

  // ------------------------------------------------------------------------------------------------------------------
	
	public String getFundsTransferUuid() {
		return fundsTransferUuid;
	}

	public void setFundsTransferUuid(String fundsTransferUuid) {
		this.fundsTransferUuid = fundsTransferUuid;
	}

  // ------------------------------------------------------------------------------------------------------------------

	public String getUserUuid() {
		return userUuid;
	}

	public void setUserUuid(String userUuid) {
		this.userUuid = userUuid;
	}

  // ------------------------------------------------------------------------------------------------------------------

	public String getCredentialUuid() {
		return credentialUuid;
	}

	public void setCredentialUuid(String credentialUuid) {
		this.credentialUuid = credentialUuid;
	}

  // ------------------------------------------------------------------------------------------------------------------

	public String getRecipientName() {
		return recipientName;
	}

	public void setRecipientName(String recipientName) {
		this.recipientName = recipientName;
	}

  // ------------------------------------------------------------------------------------------------------------------

	public String getRecipientEmail() {
		return recipientEmail;
	}

	public void setRecipientEmail(String recipientEmail) {
		this.recipientEmail = recipientEmail;
	}

  // ------------------------------------------------------------------------------------------------------------------

	public String getRecipientPhoneNumber() {
		return recipientPhoneNumber;
	}

	public void setRecipientPhoneNumber(String recipientPhoneNumber) {
		this.recipientPhoneNumber = recipientPhoneNumber;
	}

  // ------------------------------------------------------------------------------------------------------------------

	public BigDecimal getTransferAmount() {
		return transferAmount;
	}

	public void setTransferAmount(BigDecimal transferAmount) {
		this.transferAmount = transferAmount;
	}

  // ------------------------------------------------------------------------------------------------------------------


	public void setRecipientCredentialProviderUuid(
			String recipientCredentialProviderUuid) {
		this.recipientCredentialProviderUuid = recipientCredentialProviderUuid;
	}

	public void setRecipientCredentialUuid(String recipientCredentialUuid) {
		this.recipientCredentialUuid = recipientCredentialUuid;
	}

  // ------------------------------------------------------------------------------------------------------------------

	public String getRecipientCredentialUuid() {
		return recipientCredentialUuid;
	}

	public String getRecipientCredentialProviderUuid() {
		return recipientCredentialProviderUuid;
	}

  // ------------------------------------------------------------------------------------------------------------------

	public String getRecipientUuid() {
		return recipientUuid;
	}

	public void setRecipientUuid(String recipientUuid) {
		this.recipientUuid = recipientUuid;
	}

  // ------------------------------------------------------------------------------------------------------------------

	public String getJson() {
		return json;
	}

	public void setJson(String json) {
		this.json = json;
	}

  // ------------------------------------------------------------------------------------------------------------------

	@Override
	public String toString() {
		return "FundsTransfer [fundsTransferId=" + fundsTransferId
				+ ", \nfundsTransferUuid=" + fundsTransferUuid + ", \nuserUuid=" + userUuid
				+ ", \ncredentialUuid=" + credentialUuid + ", \nrecipientName=" + recipientName
				+ ", \nrecipientEmail=" + recipientEmail + ", \nrecipientPhoneNumber=" + recipientPhoneNumber 
				+ ", \ntransferAmount="	+ transferAmount + ", \nrecipientCredentialProviderUuid="
				+ recipientCredentialProviderUuid + ", \nrecipientCredentialUuid="
				+ recipientCredentialUuid + ", \nrecipientUuid=" + recipientUuid + ", \njson="
				+ json + "]";
	}	
}







