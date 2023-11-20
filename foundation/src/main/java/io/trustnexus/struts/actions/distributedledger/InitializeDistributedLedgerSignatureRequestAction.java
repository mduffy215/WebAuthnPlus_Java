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

package io.trustnexus.struts.actions.distributedledger;

import java.util.HashMap;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;

import com.opensymphony.xwork2.ActionSupport;

import io.trustnexus.jdbc.CredentialDao;
import io.trustnexus.jdbc.UserSessionTrackingDao;
import io.trustnexus.jdbc.distributedledger.DistributedLedgerDao;
import io.trustnexus.jdbc.mobileapp.UserDao;
import io.trustnexus.model.Credential;
import io.trustnexus.model.distributedledger.DistributedLedger;
import io.trustnexus.model.mobileapp.User;
import io.trustnexus.util.Constants;
import io.trustnexus.util.CryptoUtilities;
import io.trustnexus.util.Firebase;
import io.trustnexus.util.PropertyManager;
import io.trustnexus.util.Utilities;

/*
 * This action is called from within the JavaScript when the user clicks the "Signature" button to process the
 * DLT form template in dltTemplate.jsp.  A FirebaseMessage is sent to the user and then the action forwards to 
 * dltSignatureRequest.jsp.
 * 
 * Order of precedence within io.trustnexus.struts.actions.distributedledger:  22
 */
public class InitializeDistributedLedgerSignatureRequestAction extends ActionSupport implements ServletRequestAware, ServletResponseAware {

  private static final long serialVersionUID = 3395650935182301408L; 

  private static Logger logger = LogManager.getLogger(InitializeDistributedLedgerSignatureRequestAction.class);

  private HttpServletRequest servletRequest;
  private HttpServletResponse servletResponse;

  private String credentialType;
  private String credentialProviderName;
  private String domainName;
  
  private String authenticationCode;
  private String sessionUuid;
  
  private String sectionOne;
  private String sectionTwo;
  private String sectionThree;
  
  private String htmlJsonDistributedLedger;

  public String execute() {
    
    HttpSession session = servletRequest.getSession();
    String sessionIdentifier = session.getId();
    ThreadContext.put("sessionId", "sessionId::" + sessionIdentifier + "::");

    logger.info("###Entering");
    
    int userSessionTrackingId = UserSessionTrackingDao.createForMdc(sessionIdentifier); 
    
    // ----------------------------------------------------------------------------------------------------------------
    
    logger.debug("sectionOne: " + sectionOne);
    logger.debug("sectionTwo: " + sectionTwo);
    logger.debug("sectionThree: " + sectionThree);
    
    // ----------------------------------------------------------------------------------------------------------------
    
    boolean authenticationStatus = false;
    
    if (session.getAttribute(Constants.AUTHENTICATION_STATUS) != null && (boolean)session.getAttribute(Constants.AUTHENTICATION_STATUS)) {
      authenticationStatus = true;
    }

    logger.debug("authenticationStatus: " + authenticationStatus);
    
    if (authenticationStatus) {

      String authenticationSessionUuid = (String)session.getAttribute(Constants.SESSION_UUID);  
      User user = UserDao.retrieveByAuthenticationSessionUuid(authenticationSessionUuid);
      
      if (user != null) {
        
        UserSessionTrackingDao.updateUserIdentifier(user.getUserUuid(), userSessionTrackingId);   
        
        credentialType = (String)session.getAttribute(Constants.CREDENTIAL_TYPE);
        logger.debug("credentialType: " + credentialType); 
        
        Credential credential = CredentialDao.retrieveByCredentialTypeAndUserUuid(credentialType, user.getUserUuid());
        
        if (credential != null) {
          
          credentialProviderName = credential.getCredentialType().getCredentialProvider().getCredentialProviderName();
          
          domainName = credential.getCredentialType().getCredentialProvider().getDomainName();
  
          /*
           * By creating both an authenticationCode and a session UUID to designate the session, associating a 
           * userUuid, encrypting the Firebase channel and sending everything over HTTPS, it is impossible for a bad 
           * actor to hijack the distributed ledger creation process.
           */
          authenticationCode = Utilities.generateAuthenticationCode();
          logger.debug("authenticationCode: " + authenticationCode);
    
          sessionUuid = CryptoUtilities.generateUuid();
          logger.debug("sessionUuid: " + sessionUuid); 
    
          String distributedLedgerUuid = CryptoUtilities.generateUuid();
          logger.debug("distributedLedgerUuid: " + distributedLedgerUuid); 
          
          TreeMap<String, String> distributedLedgerSortedMap = new TreeMap<String, String>(); 
          
          distributedLedgerSortedMap.put("01_sectionOne", sectionOne);
          distributedLedgerSortedMap.put("02_sectionTwo", sectionTwo);
          distributedLedgerSortedMap.put("03_sectionThree", sectionThree);
          distributedLedgerSortedMap.put("04_credential", credential.getJson());
          
          String jsonDistributedLedger = Utilities.generateDistributedLedger(distributedLedgerUuid, distributedLedgerSortedMap);
          logger.debug("jsonDistributedLedger: " + jsonDistributedLedger); 
  
          DistributedLedger distributedLedger = new DistributedLedger();
  
          distributedLedger.setUpdatedById(Constants.MASTER_USER);
          distributedLedger.setDataSourceTypeValue(Constants.DATA_SOURCE_APPLICATION);
          distributedLedger.setDistributedLedgerUuid(distributedLedgerUuid);
          distributedLedger.setJsonDistributedLedger(jsonDistributedLedger); 
          distributedLedger.setCredentialProviderUuid(credential.getCredentialType().getCredentialProvider().getCredentialProviderUuid());
          distributedLedger.setCredentialType(credential.getCredentialType().getCredentialTypeName());
          distributedLedger.setCredentialUuid(credential.getCredentialUuid());
          distributedLedger.setUserUuid(user.getUserUuid());
          distributedLedger.setAuthenticationCode(authenticationCode);
          distributedLedger.setSessionUuid(sessionUuid);            
  
          DistributedLedgerDao.create(distributedLedger); 
          
          htmlJsonDistributedLedger = distributedLedger.generateHtmlJsonDistributedLedger();
          
          // ----------------------------------------------------------------------------------------------------------
          
          HashMap<String, String> transferDataMap = new HashMap<String, String>();
          
          transferDataMap.put(Constants.FIREBASE_MSG_TYPE_KEY, Constants.FIREBASE_MSG_TYPE_SIGN_DISTRIBUTED_LEDGER);
          transferDataMap.put(Constants.CREDENTIAL_PROVIDER_NAME, credential.getCredentialType().getCredentialProvider().getCredentialProviderName());
          transferDataMap.put(Constants.CREDENTIAL_TYPE, credential.getCredentialType().getCredentialTypeName());
          transferDataMap.put(Constants.DOMAIN_NAME, credential.getCredentialType().getCredentialProvider().getDomainName());
          transferDataMap.put(Constants.AUTHENTICATION_CODE, authenticationCode);
          transferDataMap.put(Constants.SESSION_UUID, sessionUuid);
          transferDataMap.put(Constants.USER_UUID, user.getUserUuid());
          transferDataMap.put(Constants.CREDENTIAL_UUID, credential.getCredentialUuid());
          
          HashMap<String, String> firebaseTransferDataMap =  CryptoUtilities.generateFirebaseTransferDataMap(transferDataMap, user.getPublicKey()); 
          
          /*
           * The Firebase message goes to the user's smart phone.
           */
          Firebase.sendMessage(PropertyManager.getInstance().getProperty(Constants.NOTIFICATION_BODY_WEB_AUTHN_PLUS_SIGN_ON), firebaseTransferDataMap, user.getFirebaseDeviceId());
          
          return SUCCESS;
          
        } else {
          logger.debug("Credential not found.");    
          return ("signOn");
        }
        
      } else {
        logger.debug("User does not exist.");    
        return ("signOn");
      }
           
    } else {  
      logger.debug("Not authenticated.");    
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

  public String getCredentialProviderName() {
    return credentialProviderName;
  }

  public void setCredentialProviderName(String credentialProviderName) {
    this.credentialProviderName = credentialProviderName;
  }

  // ------------------------------------------------------------------------------------------------------------------

  public String getDomainName() {
    return domainName;
  }

  public void setDomainName(String domainName) {
    this.domainName = domainName;
  }

  // ------------------------------------------------------------------------------------------------------------------

  public String getAuthenticationCode() {
    return authenticationCode;
  }

  public void setAuthenticationCode(String authenticationCode) {
    this.authenticationCode = authenticationCode;
  }

  // ------------------------------------------------------------------------------------------------------------------

  public String getSectionOne() {
    return sectionOne;
  }

  public void setSectionOne(String sectionOne) {
    this.sectionOne = sectionOne;
  }

  // ------------------------------------------------------------------------------------------------------------------

  public String getSectionTwo() {
    return sectionTwo;
  }

  public void setSectionTwo(String sectionTwo) {
    this.sectionTwo = sectionTwo;
  }

  // ------------------------------------------------------------------------------------------------------------------

  public String getSectionThree() {
    return sectionThree;
  }

  public void setSectionThree(String sectionThree) {
    this.sectionThree = sectionThree;
  }

  // ------------------------------------------------------------------------------------------------------------------

  public String getHtmlJsonDistributedLedger() {
    return htmlJsonDistributedLedger;
  }

  public void setHtmlJsonDistributedLedger(String htmlJsonDistributedLedger) {
    this.htmlJsonDistributedLedger = htmlJsonDistributedLedger;
  }
  
  // ------------------------------------------------------------------------------------------------------------------

  public String getSessionUuid() {
    return sessionUuid;
  }

  public void setSessionUuid(String sessionUuid) {
    this.sessionUuid = sessionUuid;
  }
}







