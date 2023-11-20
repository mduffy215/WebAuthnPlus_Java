/* ============================================================================ 
/* (c) Copyright 2023 ~ Trust Nexus, Inc.
/* All technologies described here in are "Patent Pending". 
/* License information:  http://www.trustnexus.io/license.htm
/* 
/* AS LONG AS THIS NOTICE IS MAINTAINED THE LICENSE PERMITS REDISTRIBUTION OR RE-POSTING   
/* OF THIS SOURCE CODE TO A PUBLIC REPOSITORY (WITH OR WITHOUT MODIFICATIONS). 
/* 
/* Report License Violations:  trustnexus.io@austin.rr.com 
/*  
/* This is a beta version:  0.0.1
/* Suggestions for code improvements:  trustnexus.io@austin.rr.com     
/* ============================================================================ */  

  /*
   * If you are using Eclipse and seeing validation errors on this page, see:
   * 
   * https://stackoverflow.com/questions/51811630/validation-errors-using-async-await-in-eclipse-photon
   * https://stackoverflow.com/questions/3131878/how-do-i-remove-javascript-validation-from-my-eclipse-project
   */
         
  var cookiePath = '/foundation/';
  var timerElement;
  var secondsRemaining;
  var timerFunction;
  var credentialProviderName = "Trust Nexus";
  
  /*
   * On most WebBluetooth enabled browsers, this value is written from the Java Script context to the GATT server 
   * running on the user's mobile device.  Like all othe MFA schemes, it is NOT completely secure against a 
   * sophisticated phishing/MITM attack.
   *
   * The "Nexus Chromium" browser will ignore this value and validate the domain name from the browser application context.
   * 
   * A bad actor would need to install a malacious version of "Nexus Chromium"" on your computer in order to compromise your sign on   
   */
  var domainName = "https://www.webauthnplus.com";  
  
  /*
   * https://novelbits.io/uuid-for-custom-services-and-characteristics/
   */
  var sessionSpecificPairingPrevix = "5353503e";  // HEX for 'SSP>' 
  var serviceUuid;
  var domainNameCharacteristicUuid = "446f6d61-696e-204e-616d-65205353503e";  // "HEX to text:  Domain Name SSP>" 
  var sessionUuidCharacteristicUuid = "544e5820-5365-7373-696f-6e2055554944"; // "HEX to text:  TNX Session UUID"
                                       
  var email;
  var sessionUuid;
  var credentialType;
  var credentialImageFileName;
  
  var sjclIterationCount = 262144;  // Set to a value to establish the iteration time to bout 1 second.
  var sjclLength = 256;
  
  /* ------------------------------------------------------------------------------------------------------------- */

  function createXMLHttpRequest() {
    if (window.ActiveXObject) {
      xmlhttp = new ActiveXObject("Microsoft.XMLHTTP");
    } else if (window.XMLHttpRequest) {
      xmlhttp = new XMLHttpRequest();
    }     
  }          
    
  /* --------------------------------------------------------------------------------------------------------------- */
  /* --------------------------------------------------------------------------------------------------------------- */   
  
  function initializeCreateCredential() {
      
    credentialType = urlParameterValue.credentialType;  
    console.log("credentialType: " + credentialType); 

    var begIndex = credentialType.lastIndexOf(".") + 1;
    var credential_Display_Name = credentialType.substring(begIndex);
    var credentialDisplayName = credential_Display_Name.replace(/_/g, " ");    
    document.getElementById("credentialDisplayNameSpan").innerHTML = credentialDisplayName;
      
    credentialImageFileName = credentialType.replace(/\./g, '_');
    document.getElementById('credentialImage').src = "../../foundation/images/" + credentialImageFileName + ".png"; 

    var emailCookie = readCookie('email');    
    document.getElementsByName("email")[0].value = emailCookie;
    document.getElementsByName("email")[0].focus();   
    
    /*
     * This is a hack for testing financial credentials.  In production it will be best to create
     * financialCredential.js with the domain name associated with the actual domain.
     */
    if (credentialType == "com.cwbank.credentialtype.Financial_Test_1") {
      
      credentialProviderName = "Community World Bank";
      domainName = "www.cwb_test.com"; 
      
      var credentialA = document.getElementById("credentialA");
      credentialA.classList.remove("selected");
      
      var credentialB = document.getElementById("credentialB");
      credentialB.classList.add("selected"); 
    }

    document.getElementById("credentialProviderNameSpan").innerHTML = credentialProviderName;
    document.getElementById("domainNameSpan").innerHTML = domainName;  

    document.getElementById("createCredential1").style.display = 'none';
    document.getElementById("createCredential3").style.display = 'none';
    document.getElementById("createCredential4").style.display = 'none';
    document.getElementById("userNotFoundDisplay").style.display = 'none';
  } 

  /* --------------------------------------------------------------------------------------------------------------- */
  
  /*
   * Writing the value of the Domain Name Characteristic will eventually be executed from the browser context with a simple 
   * call from the application's JavaScript:  writeDomainNameCharacteristic(credentialService, domainNameCharacteristic).
   *
   * This will enable the mobile app to confirm the user is on the right domain and not on a phishing web page.
   * The attack vector against this security process will be that the browser application itself is modified for nefarious 
   * purposes (e.g., malware running on a publicly accessible computer).
   */
  async function createButtonClick() {
    
    /*
     * The following method call will clear the Address Bar of all text after the domain name; this will make it 
     * easier for the user to check the URL address.  For obvious security reasons it is not possible to rewrite 
     * the domain name.  If you attempt to rewrite the domain name you will get the following exception:  
     * 
     *     Uncaught DOMException: Failed to execute 'pushState' on 'History':  
     * 
     * There are lots of Stack Overflow postings on this issue:
     * 
     * https://stackoverflow.com/questions/824349/modify-the-url-without-reloading-the-page
     * https://stackoverflow.com/questions/3338642/updating-address-bar-with-new-url-without-hash-or-reloading-the-page
     * 
     * MDN article:  https://developer.mozilla.org/en-US/docs/Web/API/History_API
     */
    window.history.pushState('', '', '/'); 
    
    email = document.getElementsByName('email')[0].value;
    console.log("email: " + email);
    createCookie('email', email, 365);
    
    serviceUuid = generateSessionSpecificPairingServiceUuid();
    console.log("serviceUuid: " + serviceUuid);
    
    sessionUuid = uuid();
    console.log("sessionUuid: " + sessionUuid);
      
    createXMLHttpRequest();
    
    xmlhttp.open("POST", "../../foundation/initializeCreateCredential.action?credentialType=" + credentialType + "&email=" + email + "&serviceUuid=" + serviceUuid + "&sessionUuid=" + sessionUuid , true);
    xmlhttp.setRequestHeader( "pragma", "no-cache" );      
    xmlhttp.setRequestHeader("Cache-Control","no-cache,max-age=0"); 
    xmlhttp.send(null);

    xmlhttp.onreadystatechange=function() {
  
      if (xmlhttp.readyState==4) {   
        if (xmlhttp.status==200) {
            
          var responseText = xmlhttp.responseText;
          
          if (responseText.indexOf("authenticationCode") != -1) {

            document.getElementById("createCredential1").style.display = 'block';
            document.getElementById("createCredential2").style.display = 'none';
            document.getElementById("createCredential4").style.display = 'block';
              
            serviceUuid = parseNameValuePairs(responseText, 'serviceUuid');
              
            var authenticationCode = parseNameValuePairs(responseText, 'authenticationCode');
            document.getElementById("authenticationCode").innerHTML = authenticationCode;
            
            CreateTimer('authenticationMessage', 180);
            createCredentialCheckTimer(); 
            
          } else {                   
            
            document.getElementById("createCredential1").style.display = 'none';
            document.getElementById("createCredential2").style.display = 'none';
            document.getElementById("createCredential4").style.display = 'block';
            
            document.getElementById("authenticationMessage").innerHTML = "<span class='font_body_text'>" + responseText + "</span>";                   
          }                
        }
      }
    }    
  
    console.log('Requesting Service... ' + serviceUuid);
    let device = await navigator.bluetooth.requestDevice({filters: [{services: [serviceUuid]}]});
      
    console.log("await device.gatt.connect()");
    let server = await device.gatt.connect();
  
    console.log("await server.getPrimaryService(serviceUuid)");
    let service = await server.getPrimaryService(serviceUuid);
  
    console.log("new TextEncoder()");
    var textEncoder = new TextEncoder();  
    
    console.log('Getting Domain Name Characteristic...');
    let domainNameCharacteristic = await service.getCharacteristic(domainNameCharacteristicUuid);
    
    console.log("Writing Domain Name Characteristic: " + domainName); 
    console.log(textEncoder.encode(domainName)); 
    domainNameCharacteristic.writeValue(textEncoder.encode(domainName));
    
    console.log('Getting Webauthn+ Session UUID Characteristic...');
    let sessionUuidCharacteristic = await service.getCharacteristic(sessionUuidCharacteristicUuid);
    
    console.log("Writing Webauthn+ Session UUID Characteristic: " + sessionUuid); 
    console.log(textEncoder.encode(sessionUuid)); 
    sessionUuidCharacteristic.writeValue(textEncoder.encode(sessionUuid));        
  }
  
  /* ------------------------------------------------------------------------------------------------------------- */    
    
  function createCredentialCheckTimer() { 
    timerFunction = setInterval(createCredentialCheck, 5000)
  }
  
  /* ------------------------------------------------------------------------------------------------------------- */    
    
  function createCredentialCheck() {    
        
    createXMLHttpRequest();
      
    xmlhttp.open("POST", "../../foundation/createCredentialCheck.action?sessionUuid=" + sessionUuid, true);
    xmlhttp.setRequestHeader( "pragma", "no-cache" );      
    xmlhttp.setRequestHeader("Cache-Control","no-cache,max-age=0");
    xmlhttp.send(null);
  
    xmlhttp.onreadystatechange=function() {
    
      if (xmlhttp.readyState==4) {   
        if (xmlhttp.status==200) {
                
          var responseText = xmlhttp.responseText;
          
          var message = parseNameValuePairs(responseText, 'message');
          var screenNameResponse = parseNameValuePairs(responseText, 'screenName');
          var verificationCodeResponse = parseNameValuePairs(responseText, 'verificationCode');
          
          if (message.indexOf("true") != -1 || responseText.indexOf("CANCELED") != -1) {
          
            clearTimer();
            clearInterval(timerFunction);

            document.getElementById("createCredential0").style.display = 'none';
            document.getElementById("createCredential1").style.display = 'none';
            document.getElementById("createCredential2").style.display = 'none';
            document.getElementById("createCredential3").style.display = 'block';
            document.getElementById("createCredential4").style.display = 'block';
            
            if (responseText.indexOf("CANCELED") != -1) {
              
                document.getElementById("createCredential3").style.display = 'none';
            
                var innerHtml = "Your request has been <span class='font_alert'>cancelled</span> by the authorized user.";           
            
                document.getElementById("authenticationMessage").innerHTML = innerHtml; 
            
            } else if (responseText.indexOf("Financial") != -1) {
                        
              document.getElementById("credentialCreated").innerHTML = "<br/><span class='font_body_text'><strong>Thank you " + decodeURIComponent(screenNameResponse) + "</strong>"
                                                                     + "<br/><br/>Credential successfully created."
                                                                     + "<br/><br/><span class='font_emphasis'>Verification Code: </span><span class='font_alert'>" + decodeURIComponent(verificationCodeResponse) + "</span>"
                                                                     + "<br/><br/><br/>You can test the <span class='font_emphasis'>TNX Funds Transfer</span> process with someone in your phone contacts " 
                                                                     + "who also has the <span class='font_emphasis'>TNX WebAuthn+</span> mobile app installed.&nbsp;"
                                                                     + "Simply touch the credential icon to begin.</span>";
            } else {
                        
              document.getElementById("credentialCreated").innerHTML = "<br/><span class='font_body_text'><strong>Thank you " + decodeURIComponent(screenNameResponse) + "</strong>"
                                                                     + "<br/><br/>Credential successfully created."
                                                                     + "<br/><br/><span class='font_emphasis'>Verification Code: </span><span class='font_alert'>" + decodeURIComponent(verificationCodeResponse) + "</span>"
                                                                     + "<br/><br/><br/>You can test <span class='font_emphasis'>WebAuthn+ Sign On</span> by selesting <strong>Sign On</strong> from options on the left.</span>";
            }
            
          } else {

            if (secondsRemaining <= 0) { 
          
              clearTimer();
              clearInterval(timerFunction); 
              
              document.getElementById("createCredential1").style.display = 'none';
              document.getElementById("createCredential2").style.display = 'none';
              document.getElementById("createCredential4").style.display = 'block';
          
              var innerHtml = "<strong><span class='font_alert'>Sign On Timeout (3 minutes).</span><br/>Please retrun to the " 
                            + "<a href='../../foundation/view/createCredential.jsp?credentialType=" + credentialType 
                            + "'><strong>WebAuthn+ Create Credential</strong></a>&#8482; page.</strong>";
              document.getElementById("authenticationMessage").innerHTML = innerHtml;  
              
              createCredentialSessionTimeout();
            }
          }
        }
      }
    }
  } 
  
  /* ------------------------------------------------------------------------------------------------------------- */      
      
  function createCredentialSessionTimeout() {    
        
    createXMLHttpRequest();
      
    xmlhttp.open("POST", "../../foundation/createCredentialSessionTimeout.action?sessionUuid=" + sessionUuid, true);
    xmlhttp.setRequestHeader( "pragma", "no-cache" );      
    xmlhttp.setRequestHeader("Cache-Control","no-cache,max-age=0");
    xmlhttp.send(null);
  
    xmlhttp.onreadystatechange=function() {
    
      if (xmlhttp.readyState==4) {   
        if (xmlhttp.status==200) {                  
          var responseText = xmlhttp.responseText;
        }
      }
    }
  }
    
  /* --------------------------------------------------------------------------------------------------------------- */
  /* --------------------------------------------------------------------------------------------------------------- */   
  
  function initializeSignOn() {
      
    credentialType = urlParameterValue.credentialType;
    console.log("credentialType: " + credentialType);
      
    var distributedLedgerInitialized = urlParameterValue.distributedLedgerInitialized;
    console.log("distributedLedgerInitialized: " + distributedLedgerInitialized);
    
    if (distributedLedgerInitialized == "true") {
      document.getElementById("distributedLedgerInitializedMessage").style.display = 'block'; 
    } else { 
      document.getElementById("distributedLedgerInitializedMessage").style.display = 'none';
    }

    var begIndex = credentialType.lastIndexOf(".") + 1;
    var credential_Display_Name = credentialType.substring(begIndex);
    var credentialDisplayName = credential_Display_Name.replace(/_/g, " ");    
    document.getElementById("credentialDisplayNameSpan").innerHTML = credentialDisplayName;
      
    credentialImageFileName = credentialType.replace(/\./g, '_');
    document.getElementById('credentialImage').src = "../../foundation/images/" + credentialImageFileName + ".png"; 

    document.getElementById("credentialProviderNameSpan").innerHTML = credentialProviderName;
    document.getElementById("domainNameSpan").innerHTML = domainName;  

    document.getElementById("authentication1").style.display = 'none';
  
    var trustedSystemUuid = readCookie('trustedSystemUuid'); 
    console.log("trustedSystemUuid: " + trustedSystemUuid);
    
    if (trustedSystemUuid) {
      document.getElementById("authentication2").style.display = 'none';
      document.getElementById("userNotFoundDisplay2").style.display = 'none';
      document.getElementById("signOnFailure").style.display = 'none'; 

      var emailCookie = readCookie('email');      
      document.getElementsByName("email2")[0].value = emailCookie;
      document.getElementsByName("email2")[0].focus();   
      console.log("emailCookie: " + emailCookie);  
      
    } else {
      
      document.getElementById("authentication3").style.display = 'none';
      document.getElementById("userNotFoundDisplay").style.display = 'none';  

      var emailCookie = readCookie('email');      
      document.getElementsByName("email")[0].value = emailCookie;
      document.getElementsByName("email")[0].focus();   
      console.log("emailCookie: " + emailCookie);      
    }
  } 

  /* --------------------------------------------------------------------------------------------------------------- */
  
  /*
   * Writing the value of the Domain Name Characteristic will eventually be executed from the browser context with a simple 
   * call from the application's JavaScript:  writeDomainNameCharacteristic(credentialService, domainNameCharacteristic).
   *
   * This will enable the mobile app to confirm the user is on the right domain and not on a phishing web page.
   * The attack vector against this security process will be that the browser application itself is modified for nefarious 
   * purposes (e.g., malware running on a publicly accessible computer).
   */
  async function initializeButtonClick() {
    
    /*
     * The following method call will clear the Address Bar of all text after the domain name; this will make it 
     * easier for the user to check the URL address.  For obvious security reasons it is not possible to rewrite 
     * the domain name.  If you attempt to rewrite the domain name you will get the following exception:  
     * 
     *     Uncaught DOMException: Failed to execute 'pushState' on 'History':  
     * 
     * There are lots of Stack Overflow postings on this issue:
     * 
     * https://stackoverflow.com/questions/824349/modify-the-url-without-reloading-the-page
     * https://stackoverflow.com/questions/3338642/updating-address-bar-with-new-url-without-hash-or-reloading-the-page
     * 
     * MDN article:  https://developer.mozilla.org/en-US/docs/Web/API/History_API
     */
    window.history.pushState('', '', '/'); 
    
    document.getElementById("distributedLedgerInitializedMessage").style.display = 'none';
    
    email = document.getElementsByName('email')[0].value;
    console.log("email: " + email);
    createCookie('email', email, 365);
    
    serviceUuid = generateSessionSpecificPairingServiceUuid();
    console.log("serviceUuid: " + serviceUuid);
    
    sessionUuid = uuid();
    console.log("sessionUuid: " + sessionUuid);   
      
    createXMLHttpRequest();
    
    xmlhttp.open("POST", "../../foundation/initializeSignOn.action?credentialType=" + credentialType + "&email=" + email + "&serviceUuid=" + serviceUuid + "&sessionUuid=" + sessionUuid, true);
    xmlhttp.setRequestHeader( "pragma", "no-cache" );      
    xmlhttp.setRequestHeader("Cache-Control","no-cache,max-age=0"); 
    xmlhttp.send(null);

    xmlhttp.onreadystatechange=function() {
  
      if (xmlhttp.readyState==4) {   
        if (xmlhttp.status==200) {
            
          var responseText = xmlhttp.responseText;
          
          if (responseText.indexOf("authenticationCode") != -1) {

            document.getElementById("authentication1").style.display = 'block';
            document.getElementById("authentication2").style.display = 'none';
              
            serviceUuid = parseNameValuePairs(responseText, 'serviceUuid');
              
            var authenticationCode = parseNameValuePairs(responseText, 'authenticationCode');
            document.getElementById("authenticationCode").innerHTML = authenticationCode;
            
            CreateTimer('authenticationMessage', 180);
            signOnCheckTimer(); 
            
          } else if (responseText.indexOf("The user does not have the required credential.") != -1) {
            
            window.location = 'foundation/view/createCredential.jsp?credentialType=' + credentialType;                  
            
          } else {                   
            
            document.getElementById("authentication1").style.display = 'none';
            document.getElementById("authentication2").style.display = 'none';
            document.getElementById("authentication3").style.display = 'none';
            
            document.getElementById("authenticationMessage").innerHTML = "<span class='font_body_text'>" + responseText + "</span>";                   
          }                
        }
      }
    }    
    
    try {    
  
      console.log('Requesting Service... ' + serviceUuid);
      let device = await navigator.bluetooth.requestDevice({filters: [{services: [serviceUuid]}]});
      
      console.log("await device.gatt.connect()");
      let server = await device.gatt.connect();
  
      console.log("await server.getPrimaryService(serviceUuid)");
      let service = await server.getPrimaryService(serviceUuid);
  
      console.log("new TextEncoder()");
      var textEncoder = new TextEncoder(); 
      
      console.log('Getting Domain Name Characteristic...');
      let domainNameCharacteristic = await service.getCharacteristic(domainNameCharacteristicUuid);
      
      console.log("Writing Domain Name Characteristic: " + domainName); 
      console.log(textEncoder.encode(domainName)); 
      domainNameCharacteristic.writeValue(textEncoder.encode(domainName));
      
      console.log('Getting Webauthn+ Session UUID Characteristic...');
      let sessionUuidCharacteristic = await service.getCharacteristic(sessionUuidCharacteristicUuid);
      
      console.log("Writing Webauthn+ Session UUID Characteristic: " + sessionUuid); 
      console.log(textEncoder.encode(sessionUuid)); 
      sessionUuidCharacteristic.writeValue(textEncoder.encode(sessionUuid));    
        
    } catch (e) {
      console.error(e.message);
    }
  }
  
  /* ------------------------------------------------------------------------------------------------------------- */    
    
  function signOnCheckTimer() { 
    timerFunction = setInterval(signOnCheck, 5000)
  }
  
  /* ------------------------------------------------------------------------------------------------------------- */    
    
  function signOnCheck() {    
        
    createXMLHttpRequest();
      
    xmlhttp.open("POST", "../../foundation/signOnCheck.action?sessionUuid=" + sessionUuid, true);
    xmlhttp.setRequestHeader( "pragma", "no-cache" );      
    xmlhttp.setRequestHeader("Cache-Control","no-cache,max-age=0");
    xmlhttp.send(null);
  
    xmlhttp.onreadystatechange=function() {
    
      if (xmlhttp.readyState==4) {   
        if (xmlhttp.status==200) {
                
          var responseText = xmlhttp.responseText;
          
          if (responseText.indexOf("CANCELED") != -1) {  
              
              clearTimer();               
              clearInterval(timerFunction);                   

              document.getElementById("authentication1").style.display = 'none';
              document.getElementById("authentication2").style.display = 'none'; 
              document.getElementById("authentication3").style.display = 'none'; 
          
              var innerHtml = "Your request has been <span class='font_alert'>cancelled</span> by the authorized user.";           
          
              document.getElementById("authenticationMessage").innerHTML = innerHtml; 
          
          } else if (responseText.indexOf("signOnUuid") != -1) {   
          
            clearTimer();               
            clearInterval(timerFunction); 

            var signOnUuid = parseNameValuePairs(responseText, 'signOnUuid');
            
            window.open("../../foundation/signOnConfirmation.action?sessionUuid=" + sessionUuid + "&signOnUuid=" + signOnUuid,"_self"); 
            
          } else if (responseText.indexOf("Problem with authentication server.") != -1) {   
              
            clearTimer();               
            clearInterval(timerFunction);                   
            
            document.getElementById("authentication1").style.display = 'none';
            document.getElementById("authentication2").style.display = 'none';
            document.getElementById("authentication3").style.display = 'none'; 
            
            document.getElementById("authenticationMessage").innerHTML = "<strong><span class='font_alert'>" + responseText + "</span></strong>"; 
            
          } else {

            if (secondsRemaining <= 0) { 
          
              clearTimer();
              clearInterval(timerFunction); 
              
              document.getElementById("authentication1").style.display = 'none';
              document.getElementById("authentication2").style.display = 'none';
          
              var innerHtml = "<strong><span class='font_alert'>Sign On Timeout (3 minutes).</span><br/>Please retrun to the " 
                            + "<a href='../../foundation/view/signOn.jsp?credentialType=" + credentialType 
                            + "'><strong>WebAuthn+ Sign On</strong></a>&#8482; page.</strong>";
              document.getElementById("authenticationMessage").innerHTML = innerHtml;  
              
              signOnSessionTimeout();
            }
          }
        }
      }
    }
  } 
  
  /* ------------------------------------------------------------------------------------------------------------- */      
      
  function signOnConfirmation() {
  
    var trustedSystemUuid = readCookie('trustedSystemUuid'); 
    console.log("trustedSystemUuid: " + trustedSystemUuid);
    
    if (trustedSystemUuid) {
      document.getElementById("establishedSystem1").style.display = 'none';
      document.getElementById("establishedSystem2").style.display = 'none';
    }  
  }
  
  /* ------------------------------------------------------------------------------------------------------------- */      
      
  function signOnSessionTimeout() {    
        
    createXMLHttpRequest();
      
    xmlhttp.open("POST", "../../foundation/signOnSessionTimeout.action?sessionUuid=" + sessionUuid, true);
    xmlhttp.setRequestHeader( "pragma", "no-cache" );      
    xmlhttp.setRequestHeader("Cache-Control","no-cache,max-age=0");
    xmlhttp.send(null);
  
    xmlhttp.onreadystatechange=function() {
    
      if (xmlhttp.readyState==4) {   
        if (xmlhttp.status==200) {                  
          var responseText = xmlhttp.responseText;
        }
      }
    }
  }                
  
  /* ------------------------------------------------------------------------------------------------------------- */
  /* ------------------------------------------------------------------------------------------------------------- */
  
  function establishTrustedSystem() {
      
    sessionUuid = urlParameterValue.sessionUuid;  
    console.log("sessionUuid: " + sessionUuid); 
      
    signOnUuid = urlParameterValue.signOnUuid;  
    console.log("signOnUuid: " + signOnUuid); 

    var establishTrustedSystemChecked = document.getElementById("establishTrustedSystem").checked;    
    var password = document.getElementsByName('password')[0].value;
    var passwordConfirm = document.getElementsByName('passwordConfirm')[0].value;
    var noErrors = true;
    
    document.getElementById("checkRequired").style.display = 'none';  
    document.getElementById("passwordError").style.display = 'none';
    
    if (!establishTrustedSystemChecked) {      
      document.getElementById("checkRequired").style.display = 'block'; 
      noErrors = false;
    } 

    if (!password || !passwordConfirm || password !== passwordConfirm) {          
      document.getElementById("passwordError").style.display = 'block'; 
      noErrors = false;
    }
    
    if (noErrors) {
      console.log("noErrors: " + noErrors + " sessionUuid " + sessionUuid + " signOnUuid " + signOnUuid);

      var trustedSystemUuid = uuid();
      console.log("trustedSystemUuid: " + trustedSystemUuid);
      createCookie('trustedSystemUuid', trustedSystemUuid, 365);
      
      /*
       * Creating a hash of the password on the client insures that the password is NEVER exposed on the server side.
       * Also, splitting the CPU cycles between client and server is efficient.
       * 
       * TODO:  Update the PBKDF2 function to Argon2.  Possibly one of the following:
       * 
       * https://github.com/antelle/argon2-browser
       * https://www.npmjs.com/package/argon2
       */      
      
      var date1 = new Date();
      var time1 = date1.getTime();
      
      var sjclSalt = uuid();
      console.log("sjclSalt: " + sjclSalt);
      createCookie('sjclSalt', sjclSalt, 365);
      
      var out = sjcl.misc.pbkdf2(password, sjclSalt, sjclIterationCount, sjclLength);
      var passwordHash = sjcl.codec.hex.fromBits(out).toUpperCase();
      console.log("passwordHash: " + passwordHash);  // TODO: remove      

      var date2 = new Date();
      var time2 = date2.getTime();
      
      console.log("elapsedTime: " + (time2 - time1));
      
      /* ------------------------------------------------------------------------------------------------------------- */
      
      createXMLHttpRequest();
      
      xmlhttp.open("POST", "../../foundation/establishTrustedSystem.action?sessionUuid=" + sessionUuid
                         + "&signOnUuid=" + signOnUuid + "&trustedSystemUuid=" + trustedSystemUuid + "&passwordHash=" + passwordHash , true);
      xmlhttp.setRequestHeader( "pragma", "no-cache" );      
      xmlhttp.setRequestHeader("Cache-Control","no-cache,max-age=0"); 
      xmlhttp.send(null);

      xmlhttp.onreadystatechange=function() {
    
        if (xmlhttp.readyState==4) {   
          if (xmlhttp.status==200) {
              
            var responseText = xmlhttp.responseText;            

            document.getElementById("establishedSystem1").style.display = 'none';
            document.getElementById("establishedSystem2").style.display = 'block';
            document.getElementById("serverLogOutput").style.display = 'none';
            
            document.getElementById("establishedSystemResponse").innerHTML = responseText;            
          }
        }
      }      
    }      
  }
  
  /* --------------------------------------------------------------------------------------------------------------- */
  
  function sigOnButtonClick() {
    
    /*
     * The following method call will clear the Address Bar of all text after the domain name; this will make it 
     * easier for the user to check the URL address.  For obvious security reasons it is not possible to rewrite 
     * the domain name.  If you attempt to rewrite the domain name you will get the following exception:  
     * 
     *     Uncaught DOMException: Failed to execute 'pushState' on 'History':  
     * 
     * There are lots of Stack Overflow postings on this issue:
     * 
     * https://stackoverflow.com/questions/824349/modify-the-url-without-reloading-the-page
     * https://stackoverflow.com/questions/3338642/updating-address-bar-with-new-url-without-hash-or-reloading-the-page
     * 
     * MDN article:  https://developer.mozilla.org/en-US/docs/Web/API/History_API
     */
    window.history.pushState('', '', '/'); 
    
    document.getElementById("distributedLedgerInitializedMessage").style.display = 'none';

    var deestablishTrustedSystemChecked = document.getElementById("deestablishTrustedSystem").checked;
    
    if (deestablishTrustedSystemChecked) {
      
      eraseCookie('trustedSystemUuid');      
      window.open("../../foundation/view/signOn.jsp?credentialType=" + credentialType,"_self"); 
      
    } else {
      
      email = document.getElementsByName('email2')[0].value;
      console.log("email: " + email);
      createCookie('email', email, 365);
      
      var password = document.getElementsByName('password')[0].value;
      console.log("password: " + password);  // TODO:  remove.
      
      var trustedSystemUuid = readCookie('trustedSystemUuid'); 
      console.log("trustedSystemUuid: " + trustedSystemUuid); 
      
      var sjclSalt = readCookie('sjclSalt'); 
      console.log("sjclSalt: " + sjclSalt);
      
      var out = sjcl.misc.pbkdf2(password, sjclSalt, sjclIterationCount, sjclLength);
      var passwordHash = sjcl.codec.hex.fromBits(out).toUpperCase();
      console.log("passwordHash: " + passwordHash);  // TODO: remove 
      
      sessionUuid = uuid();
      console.log("sessionUuid: " + sessionUuid);
        
      createXMLHttpRequest();
      
      xmlhttp.open("POST", "../../foundation/passwordSignOn.action?email=" + email + "&trustedSystemUuid=" + trustedSystemUuid + "&passwordHash=" + passwordHash + "&sessionUuid=" + sessionUuid , true);
      xmlhttp.setRequestHeader( "pragma", "no-cache" );      
      xmlhttp.setRequestHeader("Cache-Control","no-cache,max-age=0"); 
      xmlhttp.send(null);

      xmlhttp.onreadystatechange=function() {
    
        if (xmlhttp.readyState==4) {   
          if (xmlhttp.status==200) {
              
            var responseText = xmlhttp.responseText;
            
            if (responseText.indexOf("signOnUuid") != -1) { 

              var signOnUuid = parseNameValuePairs(responseText, 'signOnUuid');
              
              window.open("../../foundation/signOnConfirmation.action?sessionUuid=" + sessionUuid + "&signOnUuid=" + signOnUuid,"_self"); 
              
            } else if (responseText.indexOf("Problem with authentication server.") != -1) { 
              
              document.getElementById("authentication1").style.display = 'none';
              document.getElementById("authentication2").style.display = 'none';
              document.getElementById("authentication3").style.display = 'none'; 
              
              document.getElementById("authenticationMessage").innerHTML = "<strong><span class='font_alert'>" + responseText + "</span></strong>"; 
              
            }               
          }
        }
      }
    }  
  }
  
  /* ------------------------------------------------------------------------------------------------------------- */
  /* ------------------------------------------------------------------------------------------------------------- */
    
  /*
   * This function submits the distributed ledger form to the action class.
   */
  function processDistributedLedgerSignatureRequest() {
    document.initializeDistributedLedgerSignatureRequest.submit();
  }

  /* ------------------------------------------------------------------------------------------------------------- */
    
  function initializeDistributedLedgerSignatureRequest() {

    credentialType = urlParameterValue.credentialType;
    console.log("credentialType: " + credentialType);           
    
    credentialImageFileName = credentialType.replace(/\./g, '_');
    document.getElementById('credentialImage').src = "../../foundation/images/" + credentialImageFileName + ".png";  

    credentialProviderName = decodePlus(urlParameterValue.credentialProviderName);
    console.log("credentialProviderName: " + credentialProviderName); 
    document.getElementById("credentialProviderNameSpan").innerHTML = credentialProviderName;  

    domainName = urlParameterValue.domainName;
    console.log("domainName: " + domainName);
    document.getElementById("domainNameSpan").innerHTML = domainName;
      
    authenticationCode = decodePlus(urlParameterValue.authenticationCode);
    console.log("authenticationCode: " + authenticationCode); 
    document.getElementById("authenticationCode").innerHTML = authenticationCode; 
      
    sessionUuid = urlParameterValue.sessionUuid;
    console.log("sessionUuid: " + sessionUuid);
    
    /*
     * The following method call will clear the Address Bar of all text after the domain name; this will make it 
     * easier for the user to check the URL address.  For obvious security reasons it is not possible to rewrite 
     * the domain name.  If you attempt to rewrite the domain name you will get the following exception:  
     * 
     *     Uncaught DOMException: Failed to execute 'pushState' on 'History':  
     * 
     * There are lots of Stack Overflow postings on this issue:
     * 
     * https://stackoverflow.com/questions/824349/modify-the-url-without-reloading-the-page
     * https://stackoverflow.com/questions/3338642/updating-address-bar-with-new-url-without-hash-or-reloading-the-page
     * 
     * MDN article:  https://developer.mozilla.org/en-US/docs/Web/API/History_API
     */
    window.history.pushState('', '', '/'); 

    document.getElementById("signature1").style.display = 'block';
    document.getElementById("signature2").style.display = 'none';
    
    CreateTimer('signatureMessage', 180);
    distributedLedgerSignatureCheckTimer(); 
  } 
  
  /* ------------------------------------------------------------------------------------------------------------- */    
    
  function distributedLedgerSignatureCheckTimer() { 
    timerFunction = setInterval(distributedLedgerSignatureCheck, 5000)
  }
  
  /* ------------------------------------------------------------------------------------------------------------- */    
    
  function distributedLedgerSignatureCheck() {    
        
    createXMLHttpRequest();
      
    xmlhttp.open("POST", "../../foundation/distributedLedgerSignatureCheck.action?sessionUuid=" + sessionUuid, true);
    xmlhttp.setRequestHeader( "pragma", "no-cache" );      
    xmlhttp.setRequestHeader("Cache-Control","no-cache,max-age=0");
    xmlhttp.send(null);
  
    xmlhttp.onreadystatechange=function() {
    
      if (xmlhttp.readyState==4) {   
        if (xmlhttp.status==200) {
                
          var responseText = xmlhttp.responseText;
          
          if (responseText.indexOf("signatureCompletedUuid") != -1) {   
          
            clearTimer();               
            clearInterval(timerFunction); 

            var screenNameResponse = parseNameValuePairs(responseText, 'screenName');
            var signatureCompletedUuid = parseNameValuePairs(responseText, 'signatureCompletedUuid');
            var distributedLedger = parseNameValuePairs(responseText, 'distributedLedger');
            var verificationCodeResponse = parseNameValuePairs(responseText, 'verificationCode');

            document.getElementById("signature1").style.display = 'none';
            document.getElementById("signature2").style.display = 'block';
            
            document.getElementById("jsonDistributedLedgerId").innerHTML = distributedLedger;   
            
            document.getElementById("thankyou").innerHTML = "Thank you " + screenNameResponse; 
            document.getElementById("verificationCode").innerHTML = verificationCodeResponse;               
            
          } else if (responseText.indexOf("Problem with authentication server.") != -1) {   
              
              clearTimer();               
              clearInterval(timerFunction);
              
              document.getElementById("signatureMessage").innerHTML = "<strong><span class='font_alert'>" + responseText + "</span></strong>"; 
            
          } else {

            if (secondsRemaining <= 0) { 
          
              clearTimer();
              clearInterval(timerFunction);
          
              var innerHtml = "<span class='font_body_text'><strong><span class='font_alert'>Signature Timeout (3 minutes).</strong> </span><br/>Please retrun to the " +
              "<a href='../../foundation/displayTestOverview.action'><strong>Test</strong></a> page.</span>";
              
              document.getElementById("signatureMessage").innerHTML = innerHtml;  
              
              distributedLedgerSignatureSessionTimeout();
            }
          }
        }
      }
    }
  } 
  
  /* ------------------------------------------------------------------------------------------------------------- */      
      
  function distributedLedgerSignatureSessionTimeout() {    
        
    createXMLHttpRequest();
      
    xmlhttp.open("POST", "../../foundation/distributedLedgerSignatureSessionTimeout.action?sessionUuid=" + sessionUuid, true);
    xmlhttp.setRequestHeader( "pragma", "no-cache" );      
    xmlhttp.setRequestHeader("Cache-Control","no-cache,max-age=0");
    xmlhttp.send(null);
  
    xmlhttp.onreadystatechange=function() {
    
      if (xmlhttp.readyState==4) {   
        if (xmlhttp.status==200) {                  
          var responseText = xmlhttp.responseText;
        }
      }
    }
  } 

  /* --------------------------------------------------------------------------------------------------------------- */
  /* --------------------------------------------------------------------------------------------------------------- */  
  
  function parseNameValuePairs(nameValuePairs, name) {
  
    var indexPoint = nameValuePairs.indexOf(name);
    var begPoint = nameValuePairs.indexOf("=", indexPoint) + 1;
    var endPoint = nameValuePairs.indexOf("&", begPoint);
    
    return nameValuePairs.substring(begPoint, endPoint);
  }  
  
  /* ------------------------------------------------------------------------------------------------------------- */
  
  /* http://www.quirksmode.org/js/cookies.html */
  function createCookie(name, value, days) {
  
    if (days) {
    
      var date = new Date();        
      date.setTime(date.getTime()+(days*24*60*60*1000));
      var expires = "; expires="+date.toGMTString();
      
    } else {
      var expires = "";
    }
    
    document.cookie = name+ "=" + encodeURIComponent(value) + expires + "; path=" + cookiePath;
  }
  
  /* ------------------------------------------------------------------------------------------------------------- */     
  
  /* http://www.quirksmode.org/js/cookies.html */
  function readCookie(name) {
  
    var nameEQ = name + "=";
    var ca = document.cookie.split(';');
    
    for(var i=0; i < ca.length; i++) {
    
      var c = ca[i];
      
      while (c.charAt(0)==' ') c = c.substring(1,c.length);
        if (c.indexOf(nameEQ) == 0) 
          return decodeURIComponent (c.substring(nameEQ.length,c.length));
    }
    return null;
  }  
  
  /* ------------------------------------------------------------------------------------------------------------- */      
      
  /* http://www.quirksmode.org/js/cookies.html */
  function eraseCookie(name) {
    createCookie(name, "", -1);
    var cookies = document.cookie.split(";");
    console.log(name + " erased.");
  }
  
  /* ------------------------------------------------------------------------------------------------------------- */     

  /* http://www.quirksmode.org/js/cookies.html */
  function areCookiesEnabled() {
  
    var r = false;
    createCookie("testing", "Hello", 1);
    
    if (readCookie("testing") != null) {
    
      eraseCookie("testing");
      return true;
      
    } else {
      return false;
    }
  }     
  
  /* ------------------------------------------------------------------------------------------------------------- */

  /* http://forum.codecall.net/topic/51639-how-to-create-a-countdown-timer-in-javascript/#axzz29GYLvBos */

  function CreateTimer(timerId, time) {
  
    timerElement = document.getElementById(timerId);
    secondsRemaining = time;
      
    UpdateTimer()
    window.setTimeout("Tick()", 1000);
  }
  
  /* ------------------------------------------------------------------------------------------------------------- */

  function Tick() {
  
    if (secondsRemaining <= 0) {
      return;
    }

    secondsRemaining -= 1;
    UpdateTimer()
    window.setTimeout("Tick()", 1000);
  }
  
  /* ------------------------------------------------------------------------------------------------------------- */

  function UpdateTimer() {
  
    var Seconds = secondsRemaining;
      
    var Days = Math.floor(Seconds / 86400);
    Seconds -= Days * 86400;

    var Hours = Math.floor(Seconds / 3600);
    Seconds -= Hours * (3600);

    var Minutes = Math.floor(Seconds / 60);
    Seconds -= Minutes * (60);

    var TimeStr = ((Days > 0) ? Days + " days " : "") + LeadingZero(Hours) + ":" + LeadingZero(Minutes) + ":" + LeadingZero(Seconds)

    timerElement.innerHTML = "<span class='font_body_text'>" + TimeStr + " until this page expires.</span>";
  }
  
  /* ------------------------------------------------------------------------------------------------------------- */

  function LeadingZero(Time) {
    return (Time < 10) ? "0" + Time : + Time;
  }
  
  /* ------------------------------------------------------------------------------------------------------------- */

  function clearTimer() {
  
    secondsRemaining = 0;
    timerElement.innerHTML = "";
  }
  
  /* ------------------------------------------------------------------------------------------------------------- */

  function resetTimer(time) {    
    secondsRemaining = time;
  }
  
  /* ------------------------------------------------------------------------------------------------------------- */ 
   
  /*
   * Many thanks to "broofa".
   * https://stackoverflow.com/questions/105034/create-guid-uuid-in-javascript
   */
  function uuidv4() {
    return ([1e7]+-1e3+-4e3+-8e3+-1e11).replace(/[018]/g, c =>
      (c ^ crypto.getRandomValues(new Uint8Array(1))[0] & 15 >> c / 4).toString(16)
    )
  }
  
  /* ------------------------------------------------------------------------------------------------------------- */ 
   
  function uuid() {

    var date = new Date();
    var systemTimeMillis = date.getTime();
    
    var uuidv4p = uuidv4();
    
    return systemTimeMillis + "-" + uuidv4p;
  }
  
  /* ------------------------------------------------------------------------------------------------------------- */ 
   
  function generateSessionSpecificPairingServiceUuid() {
    
    var uuidv4p = uuidv4();
	return sessionSpecificPairingPrevix + uuidv4p.substring(uuidv4p.indexOf("-"));
  }

  /* --------------------------------------------------------------------------------------------------------------- */
  
  /* 
   * Very cool!  While we do not need an array for multimple parameter values,
   * this function will do what we need it to do. 
   * http://stackoverflow.com/questions/979975/how-to-get-the-value-from-the-url-parameter 
   */    	
  	
  var urlParameterValue = function () {
    
    console.log("urlParameters: " +  window.location.search.substring(1))
    
    var query_string = {};
    var query = window.location.search.substring(1);
    var vars = query.split("&");
    
    for (var i=0;i<vars.length;i++) {
      var pair = vars[i].split("=");
      
      // If first entry with this name
      if (typeof query_string[pair[0]] === "undefined") {
        query_string[pair[0]] = decodeURIComponent(pair[1]);
        
      // If second entry with this name
      } else if (typeof query_string[pair[0]] === "string") {
        var arr = [ query_string[pair[0]], decodeURIComponent(pair[1]) ];
        query_string[pair[0]] = arr;
        // If third or later entry with this name
      } else {
        query_string[pair[0]].push(decodeURIComponent(pair[1]));
      }
    } 
    return query_string;
  }();
  
  /* ------------------------------------------------------------------------------------------------------------- */

  /*
   * https://stackoverflow.com/questions/18717557/remove-plus-sign-in-url-query-string
   * Thx Luke!
   */
  function decodePlus(encoded) {
    var decoded = encoded.replace(/\+/g, '%20');
    decoded = decodeURIComponent(decoded); 
    return decoded;
  }
  
  
  
  
  
  
  
  