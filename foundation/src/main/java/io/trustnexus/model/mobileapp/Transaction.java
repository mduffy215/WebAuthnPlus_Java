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

public class Transaction extends EntityBase {

  private Integer transactionId;
  
  private String userUuid;
  private String transactionUuid;
  private boolean inactiveFlag;

  // ------------------------------------------------------------------------------------------------------------------

  public Transaction() {}

  // ------------------------------------------------------------------------------------------------------------------

  public Integer getTransactionId() {
    return transactionId;
  }

  public void setTransactionId(Integer transactionId) {
    this.transactionId = transactionId;
  }

  // ------------------------------------------------------------------------------------------------------------------

  public String getUserUuid() {
    return userUuid;
  }

  public void setUserUuid(String userUuid) {
    this.userUuid = userUuid;
  }

  // ------------------------------------------------------------------------------------------------------------------

  public String getTransactionUuid() {
    return transactionUuid;
  }

  public void setTransactionUuid(String transactionUuid) {
    this.transactionUuid = transactionUuid;
  }

  // ------------------------------------------------------------------------------------------------------------------

  public boolean isInactiveFlag() {
    return inactiveFlag;
  }

  public void setInactiveFlag(boolean inactiveFlag) {
    this.inactiveFlag = inactiveFlag;
  }

  // ------------------------------------------------------------------------------------------------------------------

	@Override
	public String toString() {
		return "Transaction [transactionId=" + transactionId + ", \nuserUuid="
				+ userUuid + ", \ntransactionUuid=" + transactionUuid + ", \ninactiveFlag="
				+ inactiveFlag + "]";
	}
}







