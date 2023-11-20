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

package io.trustnexus.struts.actions;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;

import com.opensymphony.xwork2.ActionSupport;

import io.trustnexus.jdbc.VisitorDao;
import io.trustnexus.model.Visitor;
import io.trustnexus.util.Constants;

public class DisplayAdPageAction extends ActionSupport implements ServletRequestAware, ServletResponseAware {

  private static final long serialVersionUID = 1646074465998580303L;

  private final static Logger logger = LogManager.getLogger(DisplayAdPageAction.class);

  private String refCode;
  private HttpServletRequest servletRequest;
  private HttpServletResponse servletResponse;

  public String execute() {
    
    HttpSession session = servletRequest.getSession();
    String sessionIdentifier = session.getId();
    ThreadContext.put("sessionId", "sessionId::" + sessionIdentifier + "::");

    logger.info("###Entering");
    
    /*
     * This action displays the AdPage and registers the IP address of the visitor.  The web page which 
     * contacts the TNX Web Application from a web browser through HTTPS (secure).
     */
    
    // ----------------------------------------------------------------------------------------------------------------

    try {
      //refCode = "AD 1";  
      logger.debug("refCode: " + refCode);

      Visitor visitor = new Visitor();

      visitor.setUpdatedById(Constants.MASTER_USER);
      visitor.setDataSourceTypeValue(Constants.DATA_SOURCE_APPLICATION);
      visitor.setRemoteAddress(servletRequest.getRemoteAddr());
      visitor.setRemoteHost(servletRequest.getRemoteHost());
      visitor.setRefCode(refCode);

      VisitorDao.create(visitor);

    } catch (Exception e) {
      logger.error("", e);
    }

    // ----------------------------------------------------------------------------------------------------------------

    return (SUCCESS);
  }

  // ------------------------------------------------------------------------------------------------------------------

  public String getRefCode() {
    return refCode;
  }

  public void setRefCode(String refCode) {
    this.refCode = refCode;
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
}







