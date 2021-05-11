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
    
    <title>Trust Nexus ~ Funds Notification</title> <!-- fundsNotification.jsp -->
    
    <script type="text/javascript" src="../../foundation/javascript/webAuthnPlus.js"></script>     
  </head>

  <body>

    <table id="table_banner" cellspacing="0">
      <tr>
        <td class="cell_money" colspan="2">
          <a href="http://www.xe.com/symbols.php"  title="Cool Company!  Thx!" target="_blank"  style="text-decoration: none;">
          <span class="font_money">
            &#76;&#101;&#107; &#1547; &#36; &#402; &#36; &#8380; &#36; &#36; &#66;&#114; &#66;&#90;&#36; &#36; &#36;&#98; &#75;&#77; &#80; &#1083;&#1074;
            &#82;&#36; &#36; &#6107; &#36; &#36; &#36; &#165; &#36; &#8353; &#107;&#110; &#8369; &#75;&#269; &#107;&#114; &#82;&#68;&#36; &#36; &#163; &#36;
            &#8364; &#163; &#36; &#162; &#163; &#81; &#163; &#36; &#76; &#36; &#70;&#116; &#107;&#114; &#8377; &#82;&#112; &#65020; &#163; &#8362; &#74;&#36; 
            &#165; &#163; &#1083;&#1074; &#8361; &#8361; &#1083;&#1074; &#8365; &#163; &#36; 
            
            &#1076;&#1077;&#1085; &#82;&#77; &#8360; &#36; &#8366; &#77;&#84; &#36; &#8360; &#402; &#36; &#67;&#36; &#8358; &#107;&#114; &#65020; &#8360; 
            &#66;&#47;&#46; &#71;&#115; &#83;&#47;&#46; &#8369; &#122;&#322; &#65020; &#108;&#101;&#105; &#8381; &#163; &#65020; &#1044;&#1080;&#1085;&#46; 
            &#8360; &#36; &#36; &#83; &#82; &#8360; &#107;&#114; &#67;&#72;&#70; &#36; &#163; &#78;&#84;&#36; &#3647; &#84;&#84;&#36; &#8378; &#36; &#8372; 
            &#163; &#36; &#36;&#85; &#1083;&#1074; &#66;&#115; &#8363; &#65020; &#90;&#36;
          </span>
          </a>
        </td>
      </tr>
      <tr>
        <td class="cell_banner"><span class="font_title_A">Trust Nexus </span><br><span class="font_title_B">WebAuthn+ JSON DLT ~ The Internet of Value</span></td>
        <td class="cell_banner2">
	    
		      <table cellspacing="0">
		        <tr>
              <td class="cell_menu"><a href="http://www.trustnexus.io/index.htm" style="text-decoration: none;"><span class="font_menu_label">Home</span></a></td>
              <td class="cell_menu"><a href="http://www.trustnexus.io/webauthn_plus.htm" style="text-decoration: none;"><span class="font_menu_label">WebAuthn+</span></a></td>
              <td class="cell_menu"><a href="http://www.trustnexus.io/identity.htm" style="text-decoration: none;"><span class="font_menu_label">Identity</span></a></td>
              <td class="cell_menu"><a href="http://www.trustnexus.io/distributed_ledgers_1.htm" style="text-decoration: none;"><span class="font_menu_label">Distributed Ledgers</span></a></td>
              <td class="cell_menu"><a href="http://www.trustnexus.io/finance.htm" style="text-decoration: none;"><span class="font_menu_label">Finance</span></a></td>
              <td class="cell_menu"><a href="http://www.trustnexus.io/demo01.htm" style="text-decoration: none;"><span class="font_menu_label">Demo</span></a></td>
              <td class="cell_menu"><a href="http://www.trustnexus.io/ivy.htm" style="text-decoration: none;"><span class="font_menu_label">IVY</span></a></td>
              <td class="cell_menu"><a href="https://www.webauthnplus.com/foundation/displayTestOverview.action" style="text-decoration: none;"><span class="font_menu_label">Test</span></a></td>
              <td class="cell_menu"><a href="http://www.trustnexus.io/contact.htm" style="text-decoration: none;"><span class="font_menu_label">Contact</span></a></td>
              <td class="cell_menu"><a href="http://www.trustnexus.io/license.htm" style="text-decoration: none;"><span class="font_menu_label">License</span></a></td>
		        </tr>
		      </table> <!-- table_banner -->
	    
	      </td>
	    </tr>
		  <tr>
				<td class="cell_header" colspan="2">
			
          <table id="table_header" cellspacing="0"  align="center">
            <tr>
              <td class="cell_header_text"><span class="font_header">The potential for distributed ledgers to become a 
                cryptographically secure shared source of truth is extraordinary.</span></td>
              <td><div class="fa fa-mobile fa-5x" style="font-size: 8em">&nbsp;&nbsp;</div></td>
            </tr>
          </table> 
			
				</td>
		  </tr>
	  </table>  
      
    <table id="table_text">
               
			<tr>
			  <td class="cell_text4"><img src="../../foundation/images/pixel_brownze.png" height="2" width="900"/></td>
			</tr>
			<tr>
			  <td class="cell_text2">        
			    <span class="font_section_label">&nbsp;<s:property value="message.lineOne" escapeHtml="false" /></span>
			  </td>
			</tr>
			
			<tr>
			  <td class="cell_text2">        
			    <span class="font_body_text">&nbsp;<s:property value="message.lineTwo" escapeHtml="false"/></span> 
			  </td>
			</tr>
      
      <tr>
        <td class="cell_text2">        
          <div class="font_body_text">
          &nbsp;There are three possibilities for failure:
          <ul style="list-style-type:disc">
            <li>You do not have your bank's mobile app installed on your phone.</li>
            <li>Your bank's mobile app does not support the TNX (Trust Nexus) protocol.</li>
            <li>You are using Samsung's default text messaging app which has major flaws in linking into Android apps.</li>
          </ul> 
          
          </div> 
        </td>
      </tr>
			
			<tr>
			  <td class="cell_text2">        
			    <span class="font_body_text">&nbsp;<s:property value="message.lineThree" escapeHtml="false"/></span> 
			  </td>
			</tr>
			
			<tr>
			  <td class="cell_text2">        
			    <span class="font_body_text">&nbsp;<s:property value="message.lineFour" escapeHtml="false"/></span> 
			  </td>
			</tr>
			             
			<tr>
			  <td class="cell_text4"><img src="../../foundation/images/pixel_brownze.png" height="2" width="900"/></td>
			</tr>
			<tr>
			  <td><span class="font_footer">&copy; Copyright 2021 ~ Trust Nexus, Inc.</span></td>
			</tr>
			<tr>
			  <td><span class="font_footer">All technologies described here in are "Patent Pending".</span></td>
			</tr>
	  </table>
	
    <br/><br/><br/><br/><br/><br/><br/><br/><br/><br/>   
    
  </body>
</html>







