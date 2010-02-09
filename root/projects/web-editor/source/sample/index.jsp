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
String logoNodeRef = "workspace/SpacesStore/d2a5186e-d5b0-4aa7-960d-c1cca3274423";
String mainTextNodeRef = "workspace://SpacesStore/301c4bb4-1e23-4651-bda5-de8a48b36131";
String subTextNodeRef = "workspace://SpacesStore/27872b54-a3b5-47ed-9ac6-eaa5340abec3";
%>

<html xmlns="http://www.w3.org/1999/xhtml">

   <head>
      <meta http-equiv="content-type" content="text/html;charset=utf-8" />
      <title>Alfresco Web Editor Demo</title>

      <awe:startTemplate />
      
      <link rel="stylesheet" type="text/css" href="customer.css" />
      
   </head>
   
   <body class="yui-skin-default">
      
      <div class="logo"><img src="http://localhost:8080/alfresco/d/d/<%=logoNodeRef%>/app-logo.png?guest=true" /></div>
      <h1>
         <awe:markContent id="<%=mainTextNodeRef%>" formId="default" />
         <customer:property nodeRef="<%=mainTextNodeRef%>" property="cm:title" />
      </h1>
      
      <h3>
         <awe:markContent id="<%=mainTextNodeRef%>" formId="default" />
         <customer:property nodeRef="<%=mainTextNodeRef%>" property="cm:description" />
      </h3>
      
      <div class="content">
         <awe:markContent id="<%=mainTextNodeRef%>" formId="default" />
         <customer:content nodeRef="<%=mainTextNodeRef%>" />
      </div>
      <div class="trailer-text">
         <awe:markContent id="<%=subTextNodeRef%>" formId="description" />         
         <customer:property nodeRef="<%=subTextNodeRef%>" property="cm:description" />
      </div>
      <div class="links">
         <awe:markContent id="<%=subTextNodeRef%>" formId="default" />         
         <customer:content nodeRef="<%=subTextNodeRef%>" />
      </div>
      
      <div class="copyright">&copy; 2010 Customer, Inc. All Rights Reserved.</div>
      
      <awe:endTemplate />
      
   </body>

</html>
