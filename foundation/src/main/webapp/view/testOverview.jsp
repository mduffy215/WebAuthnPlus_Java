<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="/struts-tags" prefix="s" %>
<%response.setDateHeader("expires",0);%>  
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">

  <!-- (c) Copyright 2022 ~ Trust Nexus, Inc.
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
     
    <title>Test Overview</title> <!-- testOverview.jsp -->
    
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
            <li class="selected" onclick="location.href='../../foundation/displayTestOverview.action';">Test Overview</li>
	          <li onclick="location.href='../../foundation/view/createCredential.jsp?credentialType=com.webauthnplus.credentialtype.TNX_Test_1';">Create Credential</li>
	          <li onclick="location.href='../../foundation/view/signOn.jsp?credentialType=com.webauthnplus.credentialtype.TNX_Test_1';">Sign On</li>
            <li onclick="location.href='../../foundation/displayDistributedLedgerSignatureRequest.action?credentialType=com.webauthnplus.credentialtype.TNX_Test_1';">JSON DLT</li>
            <li onclick="location.href='../../foundation/view/createCredential.jsp?credentialType=com.cwbank.credentialtype.Financial_Test_1';">Financial Credential</li>
	        </ul>
	      </div>
		
	      <div class="col-deskTopB col-tabletB">
          <div class="mainText">
          
	          <h2><span class="font_emphasis">Test Overview</span></h2>
	          
	          <p>If you have already <a href="https://play.google.com/store/apps/details?id=io.trustnexus.webauthnplus" target="_blank">downloaded</a> 
               the <span class="font_emphasis">TNX WebAuthn+</span> mobile app, you can test the system (options on the left).</p>
            <p>Start with "Create Credential".</p>
               
	          <p>Currently, <span class="font_emphasis">WebAuthn+</span> is only functional on our prototype version of the 
		          <a href="https://drive.google.com/file/d/13Ki_eTSWhSBylI2TaBh4gmH_W-4bXIfS/view?usp=sharing" target="_blank">Nexus Chromium Browser</a>. </p>
		          
		      <p><span class="font_body_text"><span class="font_emphasis">Important note:</span>&nbsp; The  
		          <a href="https://drive.google.com/file/d/13Ki_eTSWhSBylI2TaBh4gmH_W-4bXIfS/view?usp=sharing" target="_blank">Nexus Chromium Browser</a>
		           is a <span class="font_emphasis">prototype</span>!&nbsp; It is meant for conceptual testing.</span></p>
		          
		      <p><span class="font_body_text">The download consists of a 504 MB zip file 
		           (which is too large for Google drive to scan for viruses; but you really can trust us); this zip file needs to be unzipped and then the browser 
		           is launched by double clicking on "...\Nexus_Chromium\chrome.exe".</span></p>
	          
            <p>You will need to make sure Bluetooth is turned on in Windows 10/11.&nbsp;
              <a href="https://support.microsoft.com/en-us/windows/turn-bluetooth-on-or-off-in-windows-9e92fddd-4e12-e32b-9132-5e36bdb2f75a" target="_blank">Click here</a> for more details.</p>
              
            <p>You will need to set the Windows 10/11, "Allow apps to control device radios", to "On" while Chrome is NOT running.&nbsp;
              <a href="https://www.thewindowsclub.com/let-windows-apps-control-radios-in-windows-10" target="_blank">Click here</a> for more details.</p>
            
            <p>Once you and a friend have each created the test <span class="font_emphasis">Financial Credential</span>
               you can test the funds transfer process (with simulated data).&nbsp; <a href="http://www.trustnexus.io/finance.htm" target="_blank">Click Here</a> for more details.</p>
	          
	        </div>
	      </div>
		
	      <div class="col-deskTopA col-tabletC">
	        <div class="rightPanel">
	          <p>A system is secure if the plans for the system are public, 
              and the bad actors can still not break in.</p>
	        </div>
	      </div>
	    </div>
		
	    <div class="footer">
        <p>The code for this page and all the test options is part of the, "WebAuthn+ Open Source Project" (click the "Dev Guide" on the upper left for more details).</p>
        <p>The CSS for this "responsive design" will cause the page layout to change as you resize your browser.</p>
	    </div> 
	    
	    <div>
	      <p><span class="font_copyright">&copy; Copyright 2022 ~ Trust Nexus, Inc.
           <br>All technologies described here in are "Patent Pending".</span></p>
	    </div> 
	  </div>  
    
  </body>
</html>







