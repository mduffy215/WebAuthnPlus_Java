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
    
    <title>WebAuthn+ Sign On Confirmation</title> <!-- signOnConfirmation.jsp -->
     
    <script type="text/javascript" src="../../foundation/javascript/sjcl.js"></script>   
    <script type="text/javascript" src="../../foundation/javascript/webAuthnPlus.js"></script>
  </head>

  <body onload="signOnConfirmation();">

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
            <li onclick="location.href='../../foundation/displayDistributedLedgerSignatureRequest.action?credentialType=com.webauthnplus.credentialtype.TNX_Test_1';">JSON DLT</li>
            <li onclick="location.href='../../foundation/view/createCredential.jsp?credentialType=com.cwbank.credentialtype.Financial_Test_1';">Financial Credential</li>
          </ul>
        </div>
    
        <div class="col-deskTopB col-tabletB">
          <div class="mainText">
                   
            <h2><span class="font_emphasis">WebAuthn+ Sign On Successful</span></h2>
            
            <div class="textDivB"><s:property escapeHtml="false" value="message.lineOne"/></div>
            <div class="textDivB"><s:property escapeHtml="false" value="message.lineTwo"/></div>
            <div class="textDivA"><s:property escapeHtml="false" value="message.lineThree"/></div>
            
            <div class="textDivB"><img src="../../foundation/images/pixel_brownze.png" height="2" width="100%"/></div>
                                   
            <div id="establishedSystem1">            
            
              <h2><span class="font_emphasis">Trusted System</span></h2> 
	            
	            <div class="textDivA">If you trust this system (e.g., a home system or a work system that you exclusively use), in the future you can
	              authenticate with a user name and password.</div> 
	            
	            <div class="textDivA">If a bad actor can steal your password <strong>and</strong> gain access to your system,
	              your account can be compromised; however, a bad actor <strong>cannot</strong> gain access to your account 
	              through a phishing scam, a MITM attack, by stealing user names and passwords or by any of the new 
	              <a href="https://www.forbes.com/sites/zakdoffman/2019/10/07/fbi-issues-surprise-cyber-attack-warningurges-new-precautions/#68a54ffa7efb" target="_blank">advanced attacks</a>.</div>           
	            
	            <div class="textDivA"><input type="checkbox" id="establishTrustedSystem">&nbsp; <span class="font_emphasis">Establish this system as a trusted system.</span></div>
	            <div class="textDivA" id="checkRequired" style="display:none"><span class="font_alert">Check required!</span></div>
	            
	            <div class="textDivA">Password:&nbsp; <input type="password" name="password" size="30"/></div>            
	            <div class="textDivA">Confirm:&nbsp; <input type="password" name="passwordConfirm" size="30" onkeypress="if (event.keyCode == 13) {establishTrustedSystem();}" /></div>
	            <div class="textDivA" id="passwordError" style="display:none"><span class="font_alert">Passwords do not match!</span></div>
	            
	            <!-- We are not using Struts2 to process the form because there is a lot happening in the JavaScript befroe the values are sent to the server. -->
	            
	            <div class="textDivA"><a href="javascript: establishTrustedSystem()"><img style="width: 74px; height: 28px; border: 0;" 
	                                      src="../../foundation/images/button_submit.png"></img></a></div> 
	            
	            <div class="textDivA"></div> 
	            
	            <div class="textDivA"><img src="../../foundation/images/pixel_brownze.png" height="2" width="100%"/></div>
            
            </div>           
            
            <div id="establishedSystem2">            
              <div class="textDivA"><span id="establishedSystemResponse"></span></div>            
            </div>
            
            <div id="serverLogOutput">            
            
	            <div class="textDivA"><span class="font_emphasis">Server Log Output</span>
	                                  <br>(DEV mode only; for signed on user; last five minutes)</div>
	            
	            <div class="user_log">   
	              <span class="font_user_log"><s:property escapeHtml="false" value="message.lineFour"/></span>
	            </div>
	            
	          </div>
            
            <div class="textDivA"></div>
            
          </div>
        </div>
    
        <div class="col-deskTopA col-tabletC">
          <div class="rightPanel">
            <p>The potential for distributed ledgers to become a 
                cryptographically secure shared source of truth is extraordinary.</p>
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







