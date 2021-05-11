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

package io.trustnexus.util;

import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.Resource;
import javax.enterprise.concurrent.ManagedExecutorService;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.naming.InitialContext;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.trustnexus.model.mobileapp.User;

/*
 * mail-1.4.7.jar to ../apache-tomcat-8.5.31/lib
 * https://haveacafe.wordpress.com/2008/09/26/113/
 */
public class EmailFactory {

	private static Logger logger = LogManager.getLogger(EmailFactory.class); 
	
  @Resource
  private static ManagedExecutorService managedExecutorService;

  public EmailFactory() {
  }

  // ------------------------------------------------------------------------------------------------------------------

  public static void sendActivationEmail(User user) {

    logger.info("###Entering");
    
    String subject = "Welcome to The Trust Nexus";
    
    StringBuilder message = new StringBuilder();
    
    message.append(user.getScreenName() + ",");
    message.append("\n\n");
    message.append("Thank you for signing up with the Trust Nexus.");
    message.append("\n\n");
    message.append("Clicking the link below will activate your account.");
    message.append("\n\n");
    message.append("http://www.webauthnplus.com/foundation/accountActivation.action?refCode=" + user.getRefCode());
    message.append("\n\n");
    message.append("We hope you will find the TNX WebAuthn+ mobile application to be highly useful. " +
                   "Please feel free to send us your feedback, comments, and suggestions.  These are the " +
                   "building blocks that allow us to continue to improve this application.");
    message.append("\n\n");
    message.append("Sincerely,");
    message.append("\n\n");
    message.append("The Trust Nexus Team");
      	
  	sendEmail(user, subject, message.toString());
  }

  // ------------------------------------------------------------------------------------------------------------------

  public static void sendEmail(User user, String subject, String message) {

    logger.info("###Entering");    
    
    /*
     * Many thx to hagrawal!
     * https://stackoverflow.com/questions/39353020/creating-a-background-thread-for-sending-email
     * 
     * TODO:  Implement the thread pool described at the link above.
     */
      
    ExecutorService emailExecutor = Executors.newSingleThreadExecutor();
    emailExecutor.execute(new Runnable() {
        @Override
        public void run() {
            try {
              
              InternetAddress[] toInternetAddresses = new InternetAddress[1];
              toInternetAddresses[0] = new InternetAddress(user.getEmail());

              EmailFactory.sendEMail(new InternetAddress(PropertyManager.getInstance().getProperty(Constants.SEND_EMAIL_ADDRESS)),
                                     toInternetAddresses, 
                                     null,
                                     subject, 
                                     message.toString());
              
            } catch (Exception e) {
              logger.error("", e);
              throw new RuntimeException(e);
            }
        }
    });
    emailExecutor.shutdown(); // it is very important to shutdown your non-singleton ExecutorService.
  }

  // ------------------------------------------------------------------------------------------------------------------

  /**
   * Sends an email message.
   * 
   * @param fromAddress A String object containing the from address.
   * 
   * @param internetAddresses An object array of InternetAddress objects 
   *                          containing the adresses of the recipients
   * 
   * @param subject A String object containing the subject.
   * 
   * @param messageText A String object containing the message text.
   */
  public static void sendEMail(InternetAddress fromAddress,
                               InternetAddress[] internetAddresses,
                               InternetAddress[] bccInternetAddresses,
                               String subject, 
                               String messageText) {

    logger.info("###Entering");

    try { 
    	
    	// TODO:  Message for no mail session configured.
    	
      InitialContext initialContext = new InitialContext();
      Session session = (Session) initialContext.lookup(PropertyManager.getInstance().getProperty(Constants.MAIL_SESSION));    	
    	
      Message message = new MimeMessage(session);

      message.setFrom(fromAddress);
      message.addRecipients(Message.RecipientType.TO, internetAddresses);
      
      if (bccInternetAddresses != null) {
        message.addRecipients(Message.RecipientType.BCC, bccInternetAddresses);
      }

      message.setSubject(subject);
      message.setText(messageText);

      message.setHeader("X-Mailer", "");
      message.setSentDate(new Date());

      Transport.send(message);

      logger.info("###Send mail complete.");

    } catch (Exception e) {
      logger.error("", e);
      throw new RuntimeException(e);
    }
  }
}







