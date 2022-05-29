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

package io.trustnexus.struts.actions.signon;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;

import com.opensymphony.xwork2.ActionSupport;

import io.trustnexus.jdbc.AuthenticationDao;
import io.trustnexus.jdbc.UserSessionTrackingDao;
import io.trustnexus.jdbc.mobileapp.UserDao;
import io.trustnexus.model.Authentication;
import io.trustnexus.model.mobileapp.User;
import io.trustnexus.util.Constants;
import io.trustnexus.util.Message;
import io.trustnexus.util.PropertyManager;
import io.trustnexus.util.Utilities;

/*
 * This action is called from within the JavaScript when the user received a valid signOnUuid from the SignOnCheckAction. 
 * 
 * Order of precedence within io.trustnexus.struts.actions.signon:  55
 */
public class SignOnConfirmationAction extends ActionSupport implements ServletRequestAware, ServletResponseAware {

  private static final long serialVersionUID = -6651349703967751935L;

  private static Logger logger = LogManager.getLogger(SignOnConfirmationAction.class);
  
  private HttpServletRequest servletRequest;
  private HttpServletResponse servletResponse;

  private String sessionUuid;
  private String signOnUuid;
  private Message message;
  private String userLog;

  public String execute() {
    
    HttpSession session = servletRequest.getSession();
    String sessionIdentifier = session.getId();
    ThreadContext.put("sessionId", "sessionId::" + sessionIdentifier + "::");

    logger.info("###Entering");
    
    int userSessionTrackingId = UserSessionTrackingDao.createForMdc(sessionIdentifier); 

    // ----------------------------------------------------------------------------------------------------------------

    logger.debug("sessionUuid: " + sessionUuid);
    logger.debug("signOnUuid: " + signOnUuid);
    
    /*
     * Make sure the user exists in the system.
     */
    User user = UserDao.retrieveByAuthenticationSessionUuid(sessionUuid);
    
    if (user != null) {
      
      UserSessionTrackingDao.updateUserIdentifier(user.getUserUuid(), userSessionTrackingId);
    
      message = new Message();
      
      /*
       * The authentication object, retrieved by the parameters sessionUuid and signOnUuid, will not be null if the user has been 
       * authenticated and the parameters are valid.
       */
      
      Authentication authentication = AuthenticationDao.retrieveBySessionUuidAndSignOnUuid(sessionIdentifier, sessionUuid, signOnUuid); 
      
      if (authentication != null) {
        
        session.setAttribute(Constants.SESSION_UUID, sessionUuid);
        session.setAttribute(Constants.SIGN_ON_UUID, signOnUuid);      
  
        session.setAttribute(Constants.AUTHENTICATION_STATUS, true);
        session.setAttribute(Constants.CREDENTIAL_TYPE, authentication.getCredentialType());
        
        logger.debug("attributes set");
        logger.debug("DISTRIBUTED_LEDGER_INITIALIZED: " + session.getAttribute(Constants.DISTRIBUTED_LEDGER_INITIALIZED));
        
        if (session.getAttribute(Constants.DISTRIBUTED_LEDGER_INITIALIZED) != null && (boolean)session.getAttribute(Constants.DISTRIBUTED_LEDGER_INITIALIZED)) {
          session.removeAttribute(Constants.DISTRIBUTED_LEDGER_INITIALIZED);
          return("dltTemplate");
        } else { 

          logger.debug("building user log");
          String userLog = Utilities.buildUserLog(user.getUserUuid());
          
          message.setLineOne("<strong>Welcome " + authentication.getScreenName() + "</strong>"); 
          message.setLineTwo("Sign on Successful.");      
          message.setLineThree("<strong><span class='font_emphasis'>Verification Code: </span><span class='font_alert'>" + authentication.getVerificationCode() + "</span></strong>"); 
          message.setLineFour(userLog);
        }
        
      } else {
        
        message.setLineOne("Sign on Not Successful");      
        message.setLineTwo("There was a problem in the sign on process.");     
        message.setLineThree("Please contact us.");      
        message.setLineFour("Contact email address: " + PropertyManager.getInstance().getProperty(Constants.CONTACT_EMAIL));
      }  
      
      return SUCCESS;      
      
    } else {
      return "forbidden";
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

  public String getSessionUuid() {
    return sessionUuid;
  }

  public void setSessionUuid(String sessionUuid) {
    this.sessionUuid = sessionUuid;
  }
  
  // ------------------------------------------------------------------------------------------------------------------

  public String getSignOnUuid() {
    return signOnUuid;
  }

  public void setSignOnUuid(String signOnUuid) {
    this.signOnUuid = signOnUuid;
  }
  
  // ------------------------------------------------------------------------------------------------------------------

  public Message getMessage() {
    return message;
  }

  public void setMessage(Message message) {
    this.message = message;
  }
  
  // ------------------------------------------------------------------------------------------------------------------

  public String getUserLog() {
    return userLog;
  }

  public void setUserLog(String userLog) {
    this.userLog = userLog;
  }
}







