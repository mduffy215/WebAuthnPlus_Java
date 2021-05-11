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

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;

import io.trustnexus.jdbc.distributedledger.DistributedLedgerDao;
import io.trustnexus.jdbc.mobileapp.UserDao;
import io.trustnexus.jdbc.UserSessionTrackingDao;
import io.trustnexus.model.distributedledger.DistributedLedger;
import io.trustnexus.model.mobileapp.User;
import io.trustnexus.util.Constants;
import io.trustnexus.util.CryptoUtilities;
import io.trustnexus.util.PropertyManager;
import com.opensymphony.xwork2.ActionSupport;

/*
 * This action is called from within the JavaScript on a timer function (default every 5 seconds) after the user 
 * goes to the "Distributed Ledger Signature" web page and contacts the TNX Web Application from a web browser 
 * through HTTPS (secure).
 * 
 * Order of precedence within io.trustnexus.struts.actions.distributedledger:  33
 */
public class DistributedLedgerSignatureCheckAction extends ActionSupport implements ServletRequestAware, ServletResponseAware {

  private static final long serialVersionUID = -8699098111461400041L;

  private static Logger logger = LogManager.getLogger(DistributedLedgerSignatureCheckAction.class); 
  
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

    logger.debug("sessionUuid: " + sessionUuid);
    
    String responseString = null;
    
    try {
      
      /*
       * Make sure the user exists in the system.
       */
      User user = UserDao.retrieveByDistributedLedgerSessionUuid(sessionUuid);
      
      if (user != null) {
        
        UserSessionTrackingDao.updateUserIdentifier(user.getUserUuid(), userSessionTrackingId);
      
        DistributedLedger distributedLedger = DistributedLedgerDao.retrieveBySessionUuid(sessionUuid);
  
        if (distributedLedger != null && distributedLedger.getVerificationCode() != null &&  distributedLedger.getVerificationCode().equals(Constants.SESSION_INVALIDATED)) {        
          responseString = Constants.SESSION_INVALIDATED;
        } else if (distributedLedger != null && distributedLedger.getVerificationCode() != null && distributedLedger.getScreenName() != null && distributedLedger.getEmail() != null) {
          
          /*
           * The verificationCode comes from the user's smart phone through a secure channel.  If the verificationCode 
           * is not null, it means that the user, through the WebAuthn+ mobile app, has been verified in a cryptographically 
           * secure process. @see WebAuthnPlusSignOnAction.
           */
          
          String signatureCompletedUuid = CryptoUtilities.generateUuid();  
          distributedLedger.setSignatureCompleteUuid(signatureCompletedUuid);
          DistributedLedgerDao.updateSignatureCompleteUuid(distributedLedger); 
  
          responseString = Constants.SCREEN_NAME + "=" + distributedLedger.getScreenName() + "&"
                         + Constants.SIGNATURE_COMPLETED_UUID + "=" + signatureCompletedUuid + "&"
                         + Constants.DISTRIBUTED_LEDGER + "=" + distributedLedger.generateHtmlJsonDistributedLedger() + "&"
                         + Constants.VERIFICATION_CODE + "=" + distributedLedger.getVerificationCode() + "&";
          
        } else  {        
          responseString = Constants.NOT_AUTHENTICATED;
        }
      } else {
        responseString = PropertyManager.getInstance().getProperty(Constants.USER_DOES_NOT_EXIST);
      }
      
    } catch (Exception e) {
      responseString = PropertyManager.getInstance().getProperty(Constants.PROBLEM_WITH_AUTHENTICATION_SERVER); 
      logger.error("", e);
    }

    // ----------------------------------------------------------------------------------------------------------------

    try {
      logger.debug("responseString: " + responseString);
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







