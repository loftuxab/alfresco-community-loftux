<%--
 * Copyright (C) 2005-2010 Alfresco Software Limited.
 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.

 * As a special exception to the terms and conditions of version 2.0 of 
 * the GPL, you may redistribute this Program in connection with Free/Libre 
 * and Open Source Software ("FLOSS") applications as described in Alfresco's 
 * FLOSS exception.  You should have recieved a copy of the text describing 
 * the FLOSS exception, and it is also available here: 
 * http://www.alfresco.com/legal/licensing"
--%>
<%@ taglib uri="/WEB-INF/awe.tld" prefix="awe" %>
<%@ taglib uri="/WEB-INF/customer.tld" prefix="customer" %>

<%@ page buffer="16kb" contentType="text/html;charset=UTF-8" %>
<%@ page isELIgnored="false" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<%
String logoNodeRef = "workspace://SpacesStore/04e1c411-1896-45fb-9847-1c487af7f379";
String mainTextNodeRef = "workspace://SpacesStore/301a540e-7101-489c-a8a7-859102a99ea5";
String subTextNodeRef = "workspace://SpacesStore/e94797ce-c6e9-47eb-b28c-c6a4eb3ce666";
%>

<html xmlns="http://www.w3.org/1999/xhtml">

   <head>
      <meta http-equiv="content-type" content="text/html;charset=utf-8" />
      <title>Alfresco Web Editor Demo</title>
      
      <!-- Add ydn served yui files for testing sandbox loading
      <link rel="stylesheet" type="text/css" href="http://yui.yahooapis.com/combo?2.8.0r4/build/button/assets/skins/sam/button.css"> 
      <script type="text/javascript" src="http://yui.yahooapis.com/combo?2.8.0r4/build/utilities/utilities.js&2.8.0r4/build/button/button-min.js"></script> 
      -->
      
      <awe:startTemplate />
      
      <link rel="stylesheet" type="text/css" href="customer.css" />

      
   </head>
   
   <body class="yui-skin-default">
      
      <div class="logo"><img src="http://localhost:8080/alfresco/d/d/<%=logoNodeRef.replace("://", "/")%>/app-logo.png?guest=true" /></div>
      <h1>
         <customer:property nodeRef="<%=mainTextNodeRef%>" property="cm:title" />
         <awe:markContent id="<%=mainTextNodeRef%>" title="Edit Press Release" />
      </h1>
      
      <h3>
         <customer:property nodeRef="<%=mainTextNodeRef%>" property="cm:description" />
      </h3>
      
      <div class="content">
         <customer:content nodeRef="<%=mainTextNodeRef%>" />
      </div>
      
      <div class="trailer-text">
         <awe:markContent id="<%=subTextNodeRef%>" formId="description" title="Edit Trailing Text" nestedMarker="true" />
         <customer:property nodeRef="<%=subTextNodeRef%>" property="cm:description" />
      </div>
      
      <div class="links">
         <customer:content nodeRef="<%=subTextNodeRef%>" />
      </div>
      <awe:markContent id="<%=subTextNodeRef%>" title="Edit Links" />
      
      <div class="copyright">&copy; 2010 Customer, Inc. All Rights Reserved.</div>
      
      <awe:endTemplate />
   </body>

</html>
