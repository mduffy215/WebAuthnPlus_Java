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
import io.trustnexus.jdbc.UserSessionTrackingDao;
import io.trustnexus.jdbc.mobileapp.CredentialTypeDao;
import io.trustnexus.jdbc.mobileapp.UserDao;
import io.trustnexus.model.Credential;
import io.trustnexus.model.mobileapp.CredentialType;
import io.trustnexus.model.mobileapp.User;
import io.trustnexus.util.Constants;
import io.trustnexus.util.CryptoUtilities;
import io.trustnexus.util.Firebase;
import io.trustnexus.util.PropertyManager;
import io.trustnexus.util.Utilities;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Calendar;
import java.util.HashMap;

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
 * This action is called from within the JavaScript when the user goes to the "Create Credential" web page and 
 * contacts the TNX Secure web application from a web browser through HTTPS (secure).
 * 
 * Note:  In a production system, there would be some check to confirm that the user is authorized to create the 
 * credential.
 * 
 * Order of precedence within io.trustnexus.struts.actions.createcredential:  11
 */
public class InitializeCreateCredentialAction extends ActionSupport implements ServletRequestAware, ServletResponseAware {

    private static final long serialVersionUID = 2392617587925171112L;

    private static Logger logger = LogManager.getLogger(InitializeCreateCredentialAction.class);

    private HttpServletRequest servletRequest;
    private HttpServletResponse servletResponse;

    private String credentialType;
    public String email;
    public String serviceUuid;
    public String sessionUuid;
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
            logger.debug("credentialType: " + credentialType);
            logger.debug("email: " + email);
            logger.debug("serviceUuid: " + serviceUuid);
            logger.debug("sessionUuid: " + sessionUuid);

            /*
             * Make sure the user exists in the system.
             */
            User user = UserDao.retrieveByEmail(email);

            if (user != null) {

                UserSessionTrackingDao.updateUserIdentifier(user.getUserUuid(), userSessionTrackingId);

                /*
                 * TODO: In a production level system there will be some check to see if the
                 * user is authorized to create this credential.
                 */

                /*
                 * Make sure the user does not already have a credential of this type.
                 */
                Credential existingCredential = CredentialDao.retrieveByCredentialTypeAndUserUuid(credentialType, user.getUserUuid());

                if (existingCredential == null) {

                    /*
                     * By dynamically creating a serviceUuid, an authenticationCode and a session
                     * UUID to designate the session, associating a userUuid, encrypting the
                     * Firebase channel and sending everything over HTTPS, it is impossible for a
                     * bad actor to hijack the credential creation process.
                     */
                    String authenticationCode = Utilities.generateAuthenticationCode();
                    logger.debug("authenticationCode: " + authenticationCode);

                    CredentialType credentialTypeObject = CredentialTypeDao.retrieveByCredentialType(credentialType);

                    Credential credential = new Credential();

                    credential.setCredentialProviderSignatureAlgorithm(CryptoUtilities.SIGNATURE_ALGORITHM);
                    credential.setCredentialProviderSecureHashAlgorithm(CryptoUtilities.SECURE_HASH_ALGORITHM);

                    credential.setUpdatedById(Constants.MASTER_USER);
                    credential.setDataSourceTypeValue(Constants.DATA_SOURCE_APPLICATION);
                    credential.setCredentialTypeId(credentialTypeObject.getCredentialTypeId());
                    credential.setUserUuid(user.getUserUuid());
                    credential.setAuthenticationCode(authenticationCode);
                    credential.setSessionUuid(sessionUuid);
                    credential.setRemoteAddress(servletRequest.getRemoteAddr());
                    credential.setRemoteHost(servletRequest.getRemoteHost());
                    credential.setInactiveFlag(false);

                    credential.setActivationTimestamp(Utilities.generateIsoTimestamp(System.currentTimeMillis()));

                    Calendar calendar = Calendar.getInstance();
                    calendar.add(Calendar.MONTH, credentialTypeObject.getExpirationMonths());
                    credential.setExpirationTimestamp(Utilities.generateIsoTimestamp(calendar.getTimeInMillis()));

                    CredentialDao.create(credential);

                    // ----------------------------------------------------------------------------------------------------------

                    HashMap<String, String> transferDataMap = new HashMap<String, String>();

                    transferDataMap.put(Constants.FIREBASE_MSG_TYPE_KEY, Constants.FIREBASE_MSG_TYPE_CREATE_CREDENTIAL);
                    transferDataMap.put(Constants.CREDENTIAL_PROVIDER_NAME, credentialTypeObject.getCredentialProvider().getCredentialProviderName());
                    transferDataMap.put(Constants.CREDENTIAL_ICON_URL, credentialTypeObject.getCredentialIconUrl());
                    transferDataMap.put(Constants.DOMAIN_NAME, credentialTypeObject.getCredentialProvider().getDomainName());
                    transferDataMap.put(Constants.SERVICE_UUID, serviceUuid);
                    transferDataMap.put(Constants.AUTHENTICATION_CODE, authenticationCode);
                    transferDataMap.put(Constants.SESSION_UUID, sessionUuid);

                    HashMap<String, String> firebaseTransferDataMap = CryptoUtilities.generateFirebaseTransferDataMap(transferDataMap, user.getPublicKey());

                    /*
                     * The Firebase message goes to the user's smart phone.
                     */
                    Firebase.sendMessage(PropertyManager.getInstance().getProperty(Constants.NOTIFICATION_BODY_CREATE_CREDENTIAL), firebaseTransferDataMap, user.getFirebaseDeviceId());

                    // ----------------------------------------------------------------------------------------------------------

                    /*
                     * The response string goes back to the web browser and is processed by the
                     * JavaScript.
                     */
                    responseString = Constants.SERVICE_UUID + "=" + serviceUuid + "&" + Constants.AUTHENTICATION_CODE + "=" + authenticationCode + "&";

                } else {
                    responseString = PropertyManager.getInstance().getProperty(Constants.USER_ALREADY_HAS_CREDENTIAL) + existingCredential.getEmail();
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
   
    public String getServiceUuid() {
        return serviceUuid;
    }

    public void setServiceUuid(String serviceUuid) {
        this.serviceUuid = serviceUuid;
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
