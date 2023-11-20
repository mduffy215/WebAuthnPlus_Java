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

package io.trustnexus.model.distributedledger;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.trustnexus.model.EntityBase;
import io.trustnexus.util.Constants;

public class DistributedLedger extends EntityBase {

  private static Logger logger = LogManager.getLogger(DistributedLedger.class); 

  private Integer distributedLedgerId;
  
  private String distributedLedgerUuid;
  private String jsonDistributedLedger;
  private String credentialProviderUuid;
  private String credentialType;
  private String credentialUuid;
  private String authenticationCode;
  private String verificationCode;
  private String userUuid;
  private String screenName;
  private String email;
  private String sessionUuid;
  private String signatureCompleteUuid;

  // ------------------------------------------------------------------------------------------------------------------

  public DistributedLedger() {}

  // ------------------------------------------------------------------------------------------------------------------

  public Integer getDistributedLedgerId() {
    return distributedLedgerId;
  }

  public void setDistributedLedgerId(Integer distributedLedgerId) {
    this.distributedLedgerId = distributedLedgerId;
  }

  // ------------------------------------------------------------------------------------------------------------------

  public String getDistributedLedgerUuid() {
    return distributedLedgerUuid;
  }

  public void setDistributedLedgerUuid(String distributedLedgerUuid) {
    this.distributedLedgerUuid = distributedLedgerUuid;
  }

  // ------------------------------------------------------------------------------------------------------------------

  public String getJsonDistributedLedger() {
    return jsonDistributedLedger;
  }

  public void setJsonDistributedLedger(String jsonDistributedLedger) {
    this.jsonDistributedLedger = jsonDistributedLedger;
  }

  // ------------------------------------------------------------------------------------------------------------------

  public String generateHtmlJsonDistributedLedger() {
    
    String htmlJsonDistributedLedger = jsonDistributedLedger.replaceAll("\n", "<br>");
    String formattedHtmlJsonDistributedLedger = "";
    
    boolean continueParsing = true;    
    int beginIndex = 0;
    
    while (continueParsing) {
      
      int breakIndex = htmlJsonDistributedLedger.indexOf("<br>", beginIndex) + 4;
      
      // -1 + 4 = 3
      if (breakIndex == 3) {
        continueParsing = false;
        breakIndex = htmlJsonDistributedLedger.length();
      }
      
      String distributedLedgerLine = htmlJsonDistributedLedger.substring(beginIndex, breakIndex);
      logger.debug("distributedLedgerLine: " + distributedLedgerLine);
      
      if (distributedLedgerLine.length() < 110) {
        formattedHtmlJsonDistributedLedger += distributedLedgerLine;
      } else {        
        
        boolean continueParsingLine = true;
        String lineSegmentA = "";
        String lineSegmentB = "";
        
        while (continueParsingLine) {
          
          lineSegmentA = distributedLedgerLine.substring(0, 110);
          logger.debug("lineSegmentA: " + lineSegmentA);
          
          formattedHtmlJsonDistributedLedger += lineSegmentA + "<br>";
          lineSegmentB = distributedLedgerLine.substring(110);
          logger.debug("lineSegmentB: " + lineSegmentB);

          logger.debug("lineSegmentB.length(): " + lineSegmentB.length());
          
          if (lineSegmentB.length() < 110) {
            formattedHtmlJsonDistributedLedger += lineSegmentB;
            continueParsingLine = false;
            break;            
          } else {
            distributedLedgerLine = lineSegmentB;
          }  
        }
      }
      
      if (!continueParsing) {
        break;
      }
      
      beginIndex = breakIndex; 
    }
    
    return formattedHtmlJsonDistributedLedger;
  }

  // ------------------------------------------------------------------------------------------------------------------

  public String getCredentialUuid() {
    return credentialUuid;
  }

  public void setCredentialUuid(String credentialUuid) {
    this.credentialUuid = credentialUuid;
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

  public String getSignatureCompleteUuid() {
    return signatureCompleteUuid;
  }

  public void setSignatureCompleteUuid(String signatureCompleteUuid) {
    this.signatureCompleteUuid = signatureCompleteUuid;
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
    return "DistributedLedger [distributedLedgerId=" + distributedLedgerId + ", distributedLedgerUuid="
        + distributedLedgerUuid + ", jsonDistributedLedger=" + jsonDistributedLedger + ", credentialProviderUuid="
        + credentialProviderUuid + ", credentialType=" + credentialType + ", credentialUuid=" + credentialUuid
        + ", authenticationCode=" + authenticationCode + ", verificationCode=" + verificationCode + ", userUuid="
        + userUuid + ", screenName=" + screenName + ", email=" + email + ", sessionUuid=" + sessionUuid
        + ", signatureCompleteUuid=" + signatureCompleteUuid + "]";
  }
}







