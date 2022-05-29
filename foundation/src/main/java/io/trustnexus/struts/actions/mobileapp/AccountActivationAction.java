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

package io.trustnexus.struts.actions.mobileapp;

import io.trustnexus.jdbc.UserSessionTrackingDao;
import io.trustnexus.jdbc.mobileapp.UserDao;
import io.trustnexus.model.mobileapp.User;
import io.trustnexus.util.Constants;
import io.trustnexus.util.Message;
import io.trustnexus.util.PropertyManager;

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
 * This action is called from a link within an email requesting that the user activate his/her account.
 * 
 * The only security in this process is the uniqueness of the UUID refCode.
 * 
 * This process is validates that the user has created an account with an valid email address that he/she controls.
 */
public class AccountActivationAction extends ActionSupport implements ServletRequestAware, ServletResponseAware {

  private static final long serialVersionUID = -6651349703967751935L;

	private static Logger logger = LogManager.getLogger(AccountActivationAction.class);
  
	private String refCode;
	private Message message;
  private HttpServletRequest servletRequest;
  private HttpServletResponse servletResponse;

  public String execute() {
  	
  	HttpSession session = servletRequest.getSession();
  	String sessionIdentifier = session.getId();
  	ThreadContext.put("sessionId", "sessionId::" + sessionIdentifier + "::");

    logger.info("###Entering");
  	
  	int userSessionTrackingId = UserSessionTrackingDao.createForMdc(sessionIdentifier); 

    // ----------------------------------------------------------------------------------------------------------------
 
    logger.debug("refCode: " + refCode);
    
    User user = UserDao.retrieveByRefCode(refCode);
      
    message = new Message();
    
    if (user != null) {
      
      UserSessionTrackingDao.updateUserIdentifier(user.getUserUuid(), userSessionTrackingId);

      UserDao.updateInactiveFlag(false, refCode);
      
      message.setLineOne(PropertyManager.getInstance().getProperty(Constants.ACTIVATION_SUCCESSFUL));      
      message.setLineTwo(PropertyManager.getInstance().getProperty(Constants.THANK_YOU_FOR_ACTIVATING));      
      message.setLineThree(PropertyManager.getInstance().getProperty(Constants.CONTACT_EMAIL));
      
    } else {
      
      message.setLineOne(PropertyManager.getInstance().getProperty(Constants.ACTIVATION_NOT_SUCCESSFUL));      
      message.setLineTwo(PropertyManager.getInstance().getProperty(Constants.ACTIVATION_PROBLEM));     
      message.setLineThree(PropertyManager.getInstance().getProperty(Constants.CONTACT_US));      
      message.setLineFour(PropertyManager.getInstance().getProperty(Constants.CONTACT_EMAIL));
    }

    return (SUCCESS);
  }
  
  // ------------------------------------------------------------------------------------------------------------------

	public String getRefCode() {
		return refCode;
	}

	public void setRefCode(String refCode) {
		this.refCode = refCode;
	}
  
  // ------------------------------------------------------------------------------------------------------------------

	public Message getMessage() {
		return message;
	}

	public void setMessage(Message message) {
		this.message = message;
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
}







