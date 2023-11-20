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

package io.trustnexus.struts.actions.createcredential;

import io.trustnexus.jdbc.CredentialDao;

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
 * This action is called from within the JavaScript based on the expiration of a timer function after the user goes 
 * to the "Create Credential" web page and contacts the TNX Web Application from a web browser throughHTTPS (secure).
 * 
 * Order of precedence within io.trustnexus.struts.actions.createcredential:  55
 */
public class CreateCredentialSessionTimeoutAction extends ActionSupport implements ServletRequestAware, ServletResponseAware {

  private static final long serialVersionUID = 9069716771951499411L;

	private static Logger logger = LogManager.getLogger(CreateCredentialSessionTimeoutAction.class); 
  
  private HttpServletRequest servletRequest;
  private HttpServletResponse servletResponse;
	
	private String sessionUuid; 

  public String execute() {
  	
  	HttpSession session = servletRequest.getSession();
  	String sessionIdentifier = session.getId();
  	ThreadContext.put("sessionId", "sessionId::" + sessionIdentifier + "::");

    logger.info("###Entering"); 
    
    // ----------------------------------------------------------------------------------------------------------------

    logger.debug("sessionUuid: " + sessionUuid);
    
    CredentialDao.deleteBySessionUuid(sessionUuid); 
    
    return (null);
  }
  
  // ------------------------------------------------------------------------------------------------------------------

	public String getSessionUuid() {
		return sessionUuid;
	}

	public void setSessionUuid(String sessionUuid) {
		this.sessionUuid = sessionUuid;
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







