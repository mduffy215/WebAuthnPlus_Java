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
import java.util.HashMap;

import io.trustnexus.jdbc.AuthenticationDao;
import io.trustnexus.jdbc.CredentialDao;
import io.trustnexus.jdbc.UserSessionTrackingDao;
import io.trustnexus.jdbc.mobileapp.UserDao;
import io.trustnexus.model.Authentication;
import io.trustnexus.model.Credential;
import io.trustnexus.model.mobileapp.User;
import io.trustnexus.util.Constants;
import io.trustnexus.util.CryptoUtilities;
import io.trustnexus.util.Firebase;
import io.trustnexus.util.PropertyManager;
import io.trustnexus.util.Utilities;

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
 * This action is called from within the JavaScript when the user goes to  the WebAuthn+ sign on web page and 
 * contacts the TNX Secure web application from a web browser through HTTPS (secure).
 * 
 * Order of precedence within io.trustnexus.struts.actions.signon:  11
 */
public class InitializeSignOnAction extends ActionSupport implements ServletRequestAware, ServletResponseAware {

  private static final long serialVersionUID = 6540584622822119981L;

  private static Logger logger = LogManager.getLogger(InitializeSignOnAction.class);

  public HttpServletRequest servletRequest;
  public HttpServletResponse servletResponse;

  public String credentialType;
  public String email;
  public String sessionUuid;
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
      logger.debug("credentialType: " + credentialType);
      logger.debug("email: " + email);
      logger.debug("sessionUuid: " + sessionUuid);
      
      /*
       * Make sure the user exists in the system.
       */
      User user = UserDao.retrieveByEmail(email);
      
      if (user != null) {
        
        UserSessionTrackingDao.updateUserIdentifier(user.getUserUuid(), userSessionTrackingId);
        
        Credential credential = CredentialDao.retrieveByCredentialTypeAndUserUuid(credentialType, user.getUserUuid());
	      
	      if (credential != null) {

	        /*
	         * First invalidate all the previous WebAuthn+ Sign Ons for this user.
	         */
	        AuthenticationDao.invalidateVerificationCodesByUserUuidAndCredentialType(user.getUserUuid(), credential.getCredentialType().getCredentialTypeName());
	
	        /*
	         * By creating both an authenticationCode and a session UUID to designate the session, associating a 
	         * userUuid, encrypting the Firebase channel and sending everything over HTTPS, it is impossible for a bad 
	         * actor to hijack the authentication creation process.
	         */
	        String authenticationCode = Utilities.generateAuthenticationCode();
	        logger.debug("authenticationCode: " + authenticationCode);
	
	        Authentication authentication = new Authentication();
	
	        authentication.setUpdatedById(Constants.MASTER_USER);
	        authentication.setDataSourceTypeValue(Constants.DATA_SOURCE_APPLICATION);
	        authentication.setCredentialProviderUuid(credential.getCredentialType().getCredentialProvider().getCredentialProviderUuid());
	        authentication.setServerSessionId(sessionIdentifier); 
	        authentication.setCredentialType(credential.getCredentialType().getCredentialTypeName());
          authentication.setCredentialUuid(credential.getCredentialUuid());
	        authentication.setUserUuid(user.getUserUuid());
	        authentication.setAuthenticationCode(authenticationCode);
	        authentication.setSessionUuid(sessionUuid); 
	        authentication.setRemoteAddress(servletRequest.getRemoteAddr());
	        authentication.setRemoteHost(servletRequest.getRemoteHost());
	
	        AuthenticationDao.create(authentication);
	        
	        // ----------------------------------------------------------------------------------------------------------
	  			
	  			HashMap<String, String> transferDataMap = new HashMap<String, String>();
	  			
	  			transferDataMap.put(Constants.FIREBASE_MSG_TYPE_KEY, Constants.FIREBASE_MSG_TYPE_SIGN_ON);
	  			transferDataMap.put(Constants.CREDENTIAL_PROVIDER_NAME, credential.getCredentialType().getCredentialProvider().getCredentialProviderName());
	  			transferDataMap.put(Constants.CREDENTIAL_TYPE, credential.getCredentialType().getCredentialTypeName());
          transferDataMap.put(Constants.CREDENTIAL_UUID, credential.getCredentialUuid());
	  			transferDataMap.put(Constants.DOMAIN_NAME, credential.getCredentialType().getCredentialProvider().getDomainName());
	  			transferDataMap.put(Constants.AUTHENTICATION_CODE, authenticationCode);
	  			transferDataMap.put(Constants.SESSION_UUID, sessionUuid);
	        
	        HashMap<String, String> firebaseTransferDataMap =  CryptoUtilities.generateFirebaseTransferDataMap(transferDataMap, user.getPublicKey()); 
	  			
	        /*
	         * The Firebase message goes to the user's smart phone.
	         */
	  			Firebase.sendMessage(PropertyManager.getInstance().getProperty(Constants.NOTIFICATION_BODY_WEB_AUTHN_PLUS_SIGN_ON), firebaseTransferDataMap, user.getFirebaseDeviceId());
	        
	        // ----------------------------------------------------------------------------------------------------------
	        
	  			/*
	  			 * The response string goes back to the web browser and is processed by the JavaScript.
	  			 */
	        responseString = Constants.AUTHENTICATION_CODE + "=" + authenticationCode + "&"; 
					
				} else {
	        responseString = PropertyManager.getInstance().getProperty(Constants.CREDENTIAL_DOES_NOT_EXIST);
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

  public InputStream getInputStream() {
    return inputStream;
  }

  public void setInputStream(InputStream inputStream) {
    this.inputStream = inputStream;
  }
}







