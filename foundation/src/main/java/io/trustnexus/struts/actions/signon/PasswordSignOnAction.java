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

package io.trustnexus.struts.actions.signon;

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

import com.opensymphony.xwork2.ActionSupport;

import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;
import de.mkammerer.argon2.Argon2Factory.Argon2Types;
import io.trustnexus.jdbc.AuthenticationDao;
import io.trustnexus.jdbc.TrustedSystemDao;
import io.trustnexus.jdbc.UserSessionTrackingDao;
import io.trustnexus.jdbc.mobileapp.UserDao;
import io.trustnexus.model.Authentication;
import io.trustnexus.model.TrustedSystem;
import io.trustnexus.model.mobileapp.User;
import io.trustnexus.util.Constants;
import io.trustnexus.util.CryptoUtilities;
import io.trustnexus.util.PropertyManager;      

/*
 * This action is called from within the JavaScript when the user enters an email and password in the password sign on form.. 
 * 
 * Order of precedence within io.trustnexus.struts.actions.signon:  88
 */
public class PasswordSignOnAction extends ActionSupport implements ServletRequestAware, ServletResponseAware {

 private static final long serialVersionUID = -6324860840755152284L;

  private static Logger logger = LogManager.getLogger(PasswordSignOnAction.class);
  
  private HttpServletRequest servletRequest;
  private HttpServletResponse servletResponse;

  private String email;
  private String trustedSystemUuid;
  private String passwordHash;
  private String sessionUuid;
  public InputStream inputStream;

  public String execute() {
    
    HttpSession session = servletRequest.getSession();
    String sessionIdentifier = session.getId();
    ThreadContext.put("sessionId", "sessionId::" + sessionIdentifier + "::");

    logger.info("###Entering");
    
    int userSessionTrackingId = UserSessionTrackingDao.createForMdc(sessionIdentifier); 

    // ----------------------------------------------------------------------------------------------------------------

    String responseString = null;

    logger.debug("email: " + email);
    logger.debug("trustedSystemUuid: " + trustedSystemUuid);
    logger.debug("passwordHash: " + passwordHash);
    logger.debug("sessionUuid: " + sessionUuid);
    
    try {
    
      /*
       * Make sure the user exists in the system.
       */
      User user = UserDao.retrieveByEmail(email);
      
      if (user != null) {
        
        UserSessionTrackingDao.updateUserIdentifier(user.getUserUuid(), userSessionTrackingId);
        
        TrustedSystem trustedSystem = TrustedSystemDao.retrieveByEmailAndTrustedSystemUuid(email, trustedSystemUuid);

        Argon2 argon2 = Argon2Factory.create(Argon2Types.ARGON2id);
        boolean success = argon2.verify(trustedSystem.getPasswordHash(), passwordHash);
        
        if (success) {
          
          logger.debug("Argon2 success.");
          
          Authentication authentication = new Authentication();
  
          authentication.setUpdatedById(Constants.MASTER_USER);
          authentication.setDataSourceTypeValue(Constants.DATA_SOURCE_APPLICATION);
          authentication.setServerSessionId(sessionIdentifier); 
          authentication.setCredentialProviderUuid(trustedSystem.getCredentialProviderUuid());
          authentication.setCredentialType(trustedSystem.getCredentialType());
          authentication.setCredentialUuid(trustedSystem.getCredentialUuid()); 
          authentication.setVerificationCode(PropertyManager.getInstance().getProperty(Constants.TRUSTED_SYSTEM)); 
          authentication.setUserUuid(user.getUserUuid());
          authentication.setScreenName(user.getScreenName());
          authentication.setEmail(user.getEmail());
          authentication.setSessionUuid(sessionUuid); 
          
          /*
           * The signOnUuid is only generated if the passwordHash is verified.  
           */
          authentication.setSignOnUuid(CryptoUtilities.generateUuid());
          authentication.setRemoteAddress(servletRequest.getRemoteAddr());
          authentication.setRemoteHost(servletRequest.getRemoteHost());
  
          AuthenticationDao.create(authentication);
                    
          responseString = Constants.SIGN_ON_UUID + "=" + authentication.getSignOnUuid() + "&";
          
        } else {
          return "forbidden";  // TODO:  three times
        }
        
      } else {
        return "forbidden";
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

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }
  
  // ------------------------------------------------------------------------------------------------------------------

  public String getTrustedSystemUuid() {
    return trustedSystemUuid;
  }

  public void setTrustedSystemUuid(String trustedSystemUuid) {
    this.trustedSystemUuid = trustedSystemUuid;
  }
  
  // ------------------------------------------------------------------------------------------------------------------

  public String getPasswordHash() {
    return passwordHash;
  }

  public void setPasswordHash(String passwordHash) {
    this.passwordHash = passwordHash;
  }
  
  // ------------------------------------------------------------------------------------------------------------------

  public String getSessionUuid() {
    return sessionUuid;
  }

  public void setSessionUuid(String sessionUuid) {
    this.sessionUuid = sessionUuid;
  }
}







