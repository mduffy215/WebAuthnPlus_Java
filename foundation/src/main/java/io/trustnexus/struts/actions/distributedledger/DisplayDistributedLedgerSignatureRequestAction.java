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

package io.trustnexus.struts.actions.distributedledger;

import io.trustnexus.jdbc.UserSessionTrackingDao;
import io.trustnexus.jdbc.mobileapp.UserDao;
import io.trustnexus.model.mobileapp.User;
import io.trustnexus.util.Constants;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;

import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;

import com.opensymphony.xwork2.ActionSupport;

/*
 * If the user has signed in, this action displays the initial dltTemplate.jsp'
 * else, redirects to signOn.
 * 
 * Order of precedence within io.trustnexus.struts.actions.distributedledger:  11
 */
public class DisplayDistributedLedgerSignatureRequestAction extends ActionSupport implements ServletRequestAware, ServletResponseAware {

  private static final long serialVersionUID = 3395650935182301408L;

  private static Logger logger = LogManager.getLogger(DisplayDistributedLedgerSignatureRequestAction.class);

  private HttpServletRequest servletRequest;
  private HttpServletResponse servletResponse;
  
  public String credentialType;
  public boolean distributedLedgerInitialized;

  public String execute() {
    
    HttpSession session = servletRequest.getSession();
    String sessionIdentifier = session.getId();
    ThreadContext.put("sessionId", "sessionId::" + sessionIdentifier + "::");

    logger.info("###Entering");
    
    int userSessionTrackingId = UserSessionTrackingDao.createForMdc(sessionIdentifier);
    
    // ----------------------------------------------------------------------------------------------------------------
    
    logger.debug("credentialType: " + credentialType);
    
    boolean authenticationStatus = false;
    
    if (session.getAttribute(Constants.AUTHENTICATION_STATUS) != null) {
      authenticationStatus = (boolean)session.getAttribute(Constants.AUTHENTICATION_STATUS);
    }

    logger.debug("authenticationStatus: " + authenticationStatus);
    
    if (authenticationStatus) {

      String sessionUuid = (String)session.getAttribute(Constants.SESSION_UUID);  
      User user = UserDao.retrieveByAuthenticationSessionUuid(sessionUuid);      
      UserSessionTrackingDao.updateUserIdentifier(user.getUserUuid(), userSessionTrackingId);
      
      return SUCCESS;
           
    } else {  
      logger.debug("Not authenticated.");
      session.setAttribute(Constants.DISTRIBUTED_LEDGER_INITIALIZED, true); 
      distributedLedgerInitialized = true;
      return ("signOn");
    }
  }

  // ------------------------------------------------------------------------------------------------------------------

  public HttpServletRequest getServletRequest() {
    return servletRequest;
  }

  public void setServletRequest(HttpServletRequest servletRequest) {
    this.servletRequest = servletRequest;
  }

  // ------------------------------------------------------------------------------------------------------------------

  public HttpServletResponse getServletResponse() {
    return servletResponse;
  }

  public void setServletResponse(HttpServletResponse servletResponse) {
    this.servletResponse = servletResponse;
  }

  // ------------------------------------------------------------------------------------------------------------------

  public String getCredentialType() {
    return credentialType;
  }

  public void setCredentialType(String credentialType) {
    this.credentialType = credentialType;
  }

  // ------------------------------------------------------------------------------------------------------------------

  public boolean isDistributedLedgerInitialized() {
    return distributedLedgerInitialized;
  }

  public void setDistributedLedgerInitialized(boolean distributedLedgerInitialized) {
    this.distributedLedgerInitialized = distributedLedgerInitialized;
  }  
}







