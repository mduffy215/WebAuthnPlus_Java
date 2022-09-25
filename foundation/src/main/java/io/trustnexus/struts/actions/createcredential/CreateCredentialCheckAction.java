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

package io.trustnexus.struts.actions.createcredential;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;

import io.trustnexus.jdbc.CredentialDao;
import io.trustnexus.jdbc.UserSessionTrackingDao;
import io.trustnexus.jdbc.mobileapp.UserDao;
import io.trustnexus.model.Credential;
import io.trustnexus.model.mobileapp.User;
import io.trustnexus.util.Constants;
import io.trustnexus.util.PropertyManager;
import com.opensymphony.xwork2.ActionSupport;

/*
 * This action is called from within the JavaScript on a timer function (default every 5 seconds) after the user 
 * goes to the "Create Credential" web page and contacts the TNX Web Application from a web browser through HTTPS 
 * (secure).
 * 
 * Order of precedence within io.trustnexus.struts.actions.createcredential:  33
 */
public class CreateCredentialCheckAction extends ActionSupport implements ServletRequestAware, ServletResponseAware {

  private static final long serialVersionUID = 4587888215712787376L;

  private static Logger logger = LogManager.getLogger(CreateCredentialCheckAction.class);

  private HttpServletRequest servletRequest;
  private HttpServletResponse servletResponse;

  private String sessionUuid;
  private InputStream inputStream;

  public String execute() {
  	
  	HttpSession session = servletRequest.getSession();
  	String sessionIdentifier = session.getId();
  	ThreadContext.put("sessionId", "sessionId::" + sessionIdentifier + "::");

    logger.info("###Entering");
  	
  	int userSessionTrackingId = UserSessionTrackingDao.createForMdc(sessionIdentifier);
    
    // ----------------------------------------------------------------------------------------------------------------

    String responseString = null;

    try {    	
      logger.debug("sessionUuid: " + sessionUuid);
      
      /*
       * Make sure the user exists in the system.
       */
      User user = UserDao.retrieveByCredentialSessionUuid(sessionUuid);
      
      if (user != null) {
        
        UserSessionTrackingDao.updateUserIdentifier(user.getUserUuid(), userSessionTrackingId); 

        Credential credential = CredentialDao.retrieveByUserUuidAndSessionUuid(user.getUserUuid(), sessionUuid);
        
        if (credential != null) {
            
            logger.debug("credential: " + credential.toString());
      
            if (credential.hasCredentialBeenCreated()) {
      
              responseString = Constants.MESSAGE + "=" + credential.hasCredentialBeenCreated() + "&" 
                             + Constants.SCREEN_NAME + "=" + credential.getScreenName() + "&"
                             + Constants.VERIFICATION_CODE + "=" + credential.getVerificationCode() + "&"
                             + Constants.CREDENTIAL_TYPE + "=" + credential.getCredentialType().getCredentialTypeName() + "&";
              
            } else if (credential.isCanceled()) {
              responseString = Constants.CANCELED + "=" + Constants.CANCELED + "&"; 
            } else {
              responseString = "false";
            }			
		} else {
            responseString = "false";			
		}
        
      } else {
        responseString = PropertyManager.getInstance().getProperty(Constants.USER_DOES_NOT_EXIST);
      }      

    } catch (Exception e) {
      responseString = PropertyManager.getInstance().getProperty(Constants.PROBLEM_WITH_AUTHENTICATION_SERVER);
      logger.error("", e);
    }

    logger.debug("responseString: " + responseString);

    // ----------------------------------------------------------------------------------------------------------------

    try {
      inputStream = new ByteArrayInputStream(responseString.getBytes("UTF-8"));
    } catch (UnsupportedEncodingException e) {
      logger.error("", e);
    }

    return (SUCCESS);
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

  // ------------------------------------------------------------------------------------------------------------------

  public InputStream getInputStream() {
    return inputStream;
  }

  public void setInputStream(InputStream inputStream) {
    this.inputStream = inputStream;
  }
}







