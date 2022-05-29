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

package io.trustnexus.struts.actions.fundstransfer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;

import com.opensymphony.xwork2.ActionSupport;

import io.trustnexus.jdbc.mobileapp.CredentialProviderDao;
import io.trustnexus.model.mobileapp.CredentialProvider;
import io.trustnexus.util.Constants;
import io.trustnexus.util.Message;
import io.trustnexus.util.PropertyManager;

/*
 * This action is called from the WebAuthn+ mobile app when the user touches the SMS link for a
 * funds transfer AND the user does not have the TNX WebAuthn+ mobile app installed.  
 * 
 * All communication between the WebAuthn+ mobile app and the TNX Secure web application is encrypted 
 * based on the Transport Layer Security protocol with no digital certificates being used:
 * https://en.wikipedia.org/wiki/Transport_Layer_Security
 * 
 * Digital certificates are a notoriously weak method for securing keys and establishing identity.  
 * No matter who you really are, you can always find a certificate "authority" to say you are someone else.
 * 
 * Order of precedence within io.trustnexus.struts.actions.fundstransfer:  44
 */
public class FundsNotificationAction extends ActionSupport implements ServletRequestAware, ServletResponseAware {

	private static final long serialVersionUID = -8938977546884551938L;

	private static Logger logger = LogManager.getLogger(FundsNotificationAction.class);
	
  private HttpServletRequest servletRequest;
  private HttpServletResponse servletResponse;
  
  private String sender;
	private String ref;
	private Message message;

	public String execute() {

    logger.info("###Entering");

    // ----------------------------------------------------------------------------------------------------------------

    logger.debug("sender: " + sender);
    logger.debug("ref: " + ref);
    
    CredentialProvider credentialProvider = CredentialProviderDao.retrieveByCredentialProviderUuid(sender, null);
    
    message = new Message();
    
    message.setLineOne("Funds Transfer FAILURE!");
    
    if (credentialProvider != null) {         	
      message.setLineTwo(credentialProvider.getCredentialProviderName() + " is attempting to send funds to your account.");
      message.setLineThree("A simple resolution is to <a href=''>create and account</a> with "  + credentialProvider.getCredentialProviderName() + ".");      
    } else {            
      message.setLineTwo("Error in retrieving the sending bank.&nbsp; Please contact the person sending you the SMS text message.");
    }    
    
    message.setLineFour("Contact TNX: " + PropertyManager.getInstance().getProperty(Constants.CONTACT_EMAIL));

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

	public Message getMessage() {
		return message;
	}

	public void setMessage(Message message) {
		this.message = message;
	}
  
  // ------------------------------------------------------------------------------------------------------------------

	public String getSender() {
		return sender;
	}

	public void setSender(String sender) {
		this.sender = sender;
	}
  
  // ------------------------------------------------------------------------------------------------------------------

	public String getRef() {
		return ref;
	}

	public void setRef(String ref) {
		this.ref = ref;
	}	
}







