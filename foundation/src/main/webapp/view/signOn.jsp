<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/struts-tags" prefix="s"%>
<%
response.setDateHeader("expires", 0);
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">

<!-- (c) Copyright 2023 ~ Trust Nexus, Inc.
       All technologies described here in are "Patent Pending". 
       License information:  http://www.trustnexus.io/license.htm
  
       AS LONG AS THIS NOTICE IS MAINTAINED THE LICENSE PERMITS REDISTRIBUTION OR RE-POSTING  
       OF THIS SOURCE CODE TO A PUBLIC REPOSITORY (WITH OR WITHOUT MODIFICATIONS)! 
  
       Report License Violations:  trustnexus.io@austin.rr.com
  
       This is a beta version:  0.0.1
       Suggestions for code improvements:  trustnexus.io@austin.rr.com -->

<head>
<link href="../../foundation/styles/styles.css" rel="stylesheet" />
<link rel="stylesheet"
	href="https://maxcdn.bootstrapcdn.com/font-awesome/4.5.0/css/font-awesome.min.css" />
<meta http-equiv="content-type" content="text/html; charset=ISO-8859-1" />
<meta http-equiv="Pragma" content="no-cache" />
<meta http-equiv="Expires" content="no-cache" />
<meta name="viewport" content="width=device-width, initial-scale=1.0" />

<title>WebAuthn+ Sign On</title>
<!-- signOn.jsp -->

<script type="text/javascript" src="../../foundation/javascript/sjcl.js"></script>
<script type="text/javascript"
	src="../../foundation/javascript/webAuthnPlus.js"></script>
</head>

<body onload="initializeSignOn();">

	<div class="content">

		<div class="header">
			<h1>WebAuthn+</h1>
		</div>

		<div class="row">
			<div class="col-deskTopA col-tabletA menu">
				<ul>
					<li onclick="location.href='http://www.trustnexus.io/index.htm';">Trust
						Nexus - Home</li>
					<li onclick="location.href='http://www.trustnexus.io/dev01.htm';">Dev
						Guide</li>
					<li
						onclick="location.href='../../foundation/displayTestOverview.action';">Test
						Overview</li>
					<li
						onclick="location.href='../../foundation/view/createCredential.jsp?credentialType=com.webauthnplus.credentialtype.TNX_Test_1';">Create
						Credential</li>
					<li class="selected"
						onclick="location.href='../../foundation/view/signOn.jsp?credentialType=com.webauthnplus.credentialtype.TNX_Test_1';">Sign
						On</li>
					<li
						onclick="location.href='../../foundation/displayDistributedLedgerSignatureRequest.action?credentialType=com.webauthnplus.credentialtype.TNX_Test_1';">JSON
						DLT</li>
					<li
						onclick="location.href='../../foundation/view/createCredential.jsp?credentialType=com.cwbank.credentialtype.Financial_Test_1';">Financial
						Credential</li>
				</ul>
			</div>

			<div class="col-deskTopB col-tabletB">
				<div class="mainText">

					<div id="distributedLedgerInitializedMessage" class="textDivC">
						</span> <span class="font_alert">You must first sign on before you
							can test JSON DLT.</span>
					</div>

					<h2>
						<span class="font_emphasis">Sign On ~ <span
							id="credentialDisplayNameSpan"></span></span>
					</h2>

					<div id="authentication0" class="textDivA">

						<img id="credentialImage" src="" height="40" width="60"
							style="float: left; padding-right: 8px" />
						<div>
							<div class="textDivB">
								<strong><span id="credentialProviderNameSpan"></span></strong>
							</div>
							<div class="textDivB">
								<span class="font_alert"><span id="domainNameSpan"></span></span>
							</div>
						</div>

					</div>

					<div id="authentication1" class="textDivA">

						<div class="textDivA">
							<span class="font_emphasis">Authentication Code: &nbsp;</span> <span
								class="font_alert"><span id='authenticationCode'></span></span>
						</div>
						<div class="textDivA">
							Touch the <span	class="font_emphasis">Sign On</span> button in your <span class="font_emphasis">TNX WebAuthn+</span> mobile app.
						</div>

					</div>

					<!-- We are not using Struts2 to process the form because there is a lot happening in the JavaScript for Web Bluetooth when the WebAuthn+ button is clicked. -->

					<div id="authentication2">

						<div class="textDivA">
							Please enter the <span class="font_alert">contact email
								address</span> for your <span class="font_emphasis">TNX
								WebAuthn+</span> mobile app.
						</div>
						<div class="textDivA">
							<s:textfield key="email" size="30"
								onkeypress="if (event.keyCode == 13) {initializeButtonClick();}"
								spellcheck="false" />
						</div>
						<div id="userNotFoundDisplay" class="textDivA">
							The <span class="font_alert">contact email address</span> you
							entered was not found in our system.
						</div>

						<div class="textDivA">
							<a href="javascript: initializeButtonClick()"><img
								style="width: 100px; height: 28px; border: 0;"
								src="../../foundation/images/button_webauthn+.png"></img></a>
						</div>
              
                        <div class="textDivC">Activate your <span class="font_emphasis">TNX WebAuthn+</span> mobile app.</div>
              
                        <div class="textDivC">"Session specific pairing" from the 
		                  <a href="https://drive.google.com/file/d/13Ki_eTSWhSBylI2TaBh4gmH_W-4bXIfS/view?usp=sharing" target="_blank">Nexus Chromium Browser</a> will pair this web page to your mobile device.</div>

					</div>

					<div id="authenticationMessage" class="textDivC"></div>

					<div id="authentication3">

						<div class="textDivA">
							<input type="checkbox" id="deestablishTrustedSystem">&nbsp;
							<span class="font_emphasis">This system is no longer a
								trusted system.</span>
						</div>

						<div class="textDivA">
							Please enter the <span class="font_alert">contact email
								address</span> for your <span class="font_emphasis">TNX
								WebAuthn+</span> mobile app.
						</div>
						<div class="textDivA">
							<s:textfield key="email2" size="30" spellcheck="false" />
						</div>
						<div id="userNotFoundDisplay2" class="textDivA">
							The <span class="font_alert">contact email address</span> you
							entered was not found in our system.
						</div>

						<div class="textDivA">
							Please enter the <span class="font_alert">password</span> you
							created when you established this system as a <span
								class="font_emphasis">Trusted System</span>.
						</div>
						<div class="textDivA">
							<s:password key="password" size="30"
								onkeypress="if (event.keyCode == 13) {sigOnButtonClick();}"
								spellcheck="false" />
						</div>
						<div id="signOnFailure" class="textDivA">
							The sign on process <span class="font_alert">failed</span>.
						</div>

						<div class="textDivA">
							<a href="javascript: sigOnButtonClick()"><img
								style="width: 100px; height: 28px; border: 0;"
								src="../../foundation/images/button_webauthn+.png"></img></a>
						</div>
					</div>

				</div>
			</div>

			<div class="col-deskTopA col-tabletC">
				<div class="rightPanel">
					<p>This institutional validation represents a stamp of
						approval.&nbsp; If the stamp is cryptographically valid, the
						authentication can be trusted.</p>
				</div>
			</div>
		</div>

		<div class="footer">
			<p>The code for this page and all the test options is part of
				the, "WebAuthn+ Open Source Project" (click the "Dev Guide" on the
				upper left for more details).</p>
			<p>The CSS for this "responsive design" will cause the page
				layout to change as you resize your browser.</p>
		</div>

		<div>
			<p>
				<span class="font_copyright">&copy; Copyright 2023 ~ Trust
					Nexus, Inc. <br>All technologies described here in are "Patent
					Pending".
				</span>
			</p>
		</div>
	</div>

</body>
</html>







