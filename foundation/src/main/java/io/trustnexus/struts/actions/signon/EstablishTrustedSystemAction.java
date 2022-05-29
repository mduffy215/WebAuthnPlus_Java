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

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.time.Duration;
import java.time.Instant;
import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;
import de.mkammerer.argon2.Argon2Factory.Argon2Types;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;

import com.opensymphony.xwork2.ActionSupport;

import io.trustnexus.jdbc.AuthenticationDao;
import io.trustnexus.jdbc.TrustedSystemDao;
import io.trustnexus.jdbc.UserSessionTrackingDao;
import io.trustnexus.jdbc.mobileapp.UserDao;
import io.trustnexus.model.Authentication;
import io.trustnexus.model.TrustedSystem;
import io.trustnexus.model.mobileapp.User;
import io.trustnexus.util.Constants;
import io.trustnexus.util.PropertyManager;

/*
 * If the user has signed in, this action enables the user to establish a "trusted system", meaning that in the future
 * the user can sign on with just a user name and password.
 * 
 * Order of precedence within io.trustnexus.struts.actions.signon:  77
 */
public class EstablishTrustedSystemAction extends ActionSupport implements ServletRequestAware, ServletResponseAware {

  private static final long serialVersionUID = 3395650935182301408L;

  private static Logger logger = LogManager.getLogger(EstablishTrustedSystemAction.class);

  public HttpServletRequest servletRequest;
  public HttpServletResponse servletResponse;

  public String sessionUuid;
  public String signOnUuid;
  public String trustedSystemUuid;
  public String passwordHash;
  public InputStream inputStream;

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
      logger.debug("signOnUuid: " + signOnUuid);
      logger.debug("trustedSystemUuid: " + trustedSystemUuid);
      logger.debug("passwordHash: " + passwordHash);
      
      /*
       * Make sure the user exists in the system.
       */
      User user = UserDao.retrieveByAuthenticationSessionUuid(sessionUuid);
      
      if (user != null) {
        
        UserSessionTrackingDao.updateUserIdentifier(user.getUserUuid(), userSessionTrackingId);
        
        Authentication authentication = AuthenticationDao.retrieveBySessionUuidAndSignOnUuid(sessionIdentifier, sessionUuid, signOnUuid); 
        
        if (authentication != null) {
          
          String sessionUuidSession = (String)session.getAttribute(Constants.SESSION_UUID);
          String signOnUuidSession = (String)session.getAttribute(Constants.SIGN_ON_UUID);
          
          if (sessionUuidSession.equals(authentication.getSessionUuid()) && signOnUuidSession.equals(authentication.getSignOnUuid())) {
            
            /*
             * Good info:
             * https://www.twelve21.io/how-to-use-argon2-for-password-hashing-in-java/
             * https://www.alexedwards.net/blog/how-to-hash-and-verify-passwords-with-argon2-in-go
             */
            
            long beginHash = System.currentTimeMillis();
            
            Argon2 argon2 = Argon2Factory.create(Argon2Types.ARGON2id);
            String hash = argon2.hash(4, 1024 * 1024, 8, passwordHash);  // 4 iterations results in a bout a 1000 ms hash time on a good workstation.
            
            long endHash = System.currentTimeMillis();
            
            boolean success = argon2.verify(hash, passwordHash);
            
            long endVerify = System.currentTimeMillis();
            
            logger.debug("Argon2 Hash: " + hash);
            logger.debug("Argon2 Verify: " + success + "  Hash time: " + (endHash - beginHash) + "  Verify time: " + (endVerify - endHash));
            
            TrustedSystem trustedSystem = new TrustedSystem();
            
            trustedSystem.setUpdatedById(Constants.MASTER_USER);
            trustedSystem.setDataSourceTypeValue(Constants.DATA_SOURCE_APPLICATION);
            trustedSystem.setEmail(user.getEmail());
            trustedSystem.setUserUuid(user.getUserUuid());
            trustedSystem.setTrustedSystemUuid(trustedSystemUuid);
            trustedSystem.setPasswordHash(hash);
            trustedSystem.setCredentialProviderUuid(authentication.getCredentialProviderUuid()); 
            trustedSystem.setCredentialType(authentication.getCredentialType());
            trustedSystem.setCredentialUuid(authentication.getCredentialUuid()); 
            
            TrustedSystemDao.create(trustedSystem);

            responseString = PropertyManager.getInstance().getProperty(Constants.TRUSTED_SYSTEM_ESTABLISHED);
            
          } else {
            responseString = PropertyManager.getInstance().getProperty(Constants.NOT_SIGNED_ON);
          }
          
        } else {
          responseString = PropertyManager.getInstance().getProperty(Constants.NOT_SIGNED_ON);
        }        
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

  public InputStream getInputStream() {
    return inputStream;
  }

  public void setInputStream(InputStream inputStream) {
    this.inputStream = inputStream;
  }
}







