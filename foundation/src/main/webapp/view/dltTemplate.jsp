<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="/struts-tags" prefix="s" %>
<%response.setDateHeader("expires",0);%>  
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">

  <!-- (c) Copyright 2021 ~ Trust Nexus, Inc.
       All technologies described here in are "Patent Pending". 
       License information:  http://www.trustnexus.io/license.htm
  
       AS LONG AS THIS NOTICE IS MAINTAINED THE LICENSE PERMITS REDISTRIBUTION OR RE-POSTING  
       OF THIS SOURCE CODE TO A PUBLIC REPOSITORY (WITH OR WITHOUT MODIFICATIONS)! 
  
       Report License Violations:  trustnexus.io@austin.rr.com
  
       This is a beta version:  0.0.1
       Suggestions for code improvements:  trustnexus.io@austin.rr.com -->

  <head>
    <link href="../../foundation/styles/styles.css" rel="stylesheet"/>
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/font-awesome/4.5.0/css/font-awesome.min.css"/>
    <meta http-equiv="content-type" content="text/html; charset=ISO-8859-1"/>
    <meta http-equiv="Pragma" content="no-cache"/>
    <meta http-equiv="Expires" content="no-cache"/>
    <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
    
    <title>JSON DLT Template</title> <!-- dltTemplate.jsp -->
    
    <script type="text/javascript" src="../../foundation/javascript/webAuthnPlus.js"></script>   
  </head>

  <body>

    <div class="content"> 

      <div class="header">
        <h1>WebAuthn+</h1>
      </div>
    
      <div class="row">
        <div class="col-deskTopA col-tabletA menu">
          <ul>
            <li onclick="location.href='http://www.trustnexus.io/index.htm';">Trust Nexus - Home</li>
            <li onclick="location.href='http://www.trustnexus.io/dev01.htm';">Dev Guide</li>
            <li onclick="location.href='../../foundation/displayTestOverview.action';">Test Overview</li>
            <li onclick="location.href='../../foundation/view/createCredential.jsp?credentialType=com.webauthnplus.credentialtype.TNX_Test_1';">Create Credential</li>
            <li onclick="location.href='../../foundation/view/signOn.jsp?credentialType=com.webauthnplus.credentialtype.TNX_Test_1';">Sign On</li>
            <li class="selected" onclick="location.href='../../foundation/displayDistributedLedgerSignatureRequest.action?credentialType=com.webauthnplus.credentialtype.TNX_Test_1';">JSON DLT</li>
            <li onclick="location.href='../../foundation/view/createCredential.jsp?credentialType=com.cwbank.credentialtype.Financial_Test_1';">Financial Credential</li>
          </ul>
        </div>
    
        <div class="col-deskTopB col-tabletB">
          <div class="mainText">
                   
            <h2><span class="font_emphasis">JSON DLT Template</span></h2>
                  
            <s:form action='initializeDistributedLedgerSignatureRequest.action' namespace="/" method='POST'>
            
	            <div class="textDivB">
	              <p>Assume the sections below are elements of an agreement.&nbsp; You can modify the sections and then click <span class="font_emphasis">Signature</span>.</p>
	              <p>A message will be sent to your <span class="font_emphasis">TNX WebAuthn+</span> mobile app; touch the <span class="font_emphasis">Signature</span> 
	                 button in your mobile app and the text below will become part of a cryptographically secure signed distributed ledger. </p> 
	            </div>
	            
	            <div class="textDivD">SectionOne:</div>	            
	            <div class="textDivB">
	              <s:textarea name="sectionOne" cols="64" rows="4" 
                   value="When in the Course of human events, it becomes necessary for one people to dissolve the political bands which have connected them with another..."
                   onkeypress="if (event.keyCode == 13) {this.form.submit();}"/>
	            </div>
	            
              <div class="textDivD">SectionTwo:</div>       
	            <div class="textDivB">
	              <s:textarea name="sectionTwo" cols="64" rows="4" 
                   value="With malice toward none, with charity for all, with firmness in the right as God gives us to see the right, let us strive on to finish the work we are in, to bind up the nation's wounds..."
                   onkeypress="if (event.keyCode == 13) {this.form.submit();}"/>
	            </div>
              
              <div class="textDivD">SectionThree:</div>          
              <div class="textDivB">
                <s:textarea name="sectionThree" cols="64" rows="4" 
                   value="With a good conscience our only sure reward, with history the final judge of our deeds, let us go forth to lead the land we love, asking His blessing and His help, but knowing that here on earth God's work must truly be our own."
                   onkeypress="if (event.keyCode == 13) {this.form.submit();}"/> 
              </div>
              
              <div class="textDivD"></div>              
              <div class="textDivB">
                <a href="javascript: processDistributedLedgerSignatureRequest()"><img style="width: 88px; height: 28px; border: 0;" 
                   alt="" src="../../foundation/images/button_signature.png"></img></a>
              </div>            
            
            </s:form>
            
          </div>
        </div>
    
        <div class="col-deskTopA col-tabletC">
          <div class="rightPanel">
            <p>There will be a smooth transition to this new technology, not a radical paradigm shift.</p>
          </div>
        </div>
      </div>
    
      <div class="footer">
        <p>The code for this page and all the test options is part of the, "WebAuthn+ Open Source Project" (click the "Dev Guide" on the upper left for more details).</p>
        <p>The CSS for this "responsive design" will cause the page layout to change as you resize your browser.</p>
      </div>  
      
      <div>
        <p><span class="font_copyright">&copy; Copyright 2021 ~ Trust Nexus, Inc.
           <br>All technologies described here in are "Patent Pending".</span></p>
      </div> 
    </div>  
    
  </body>
</html>







