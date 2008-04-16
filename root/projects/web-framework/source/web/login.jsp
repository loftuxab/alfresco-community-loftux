<%--
 * Copyright (C) 2005-2007 Alfresco Software Limited.
 
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
<%@ page buffer="16kb" contentType="text/html;charset=UTF-8" %>
<%@ page isELIgnored="false" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">

<head>
<title>Login</title>
</head>

<body bgcolor="#ffffff">

   <form accept-charset="UTF-8" id="login" action="<%=request.getContextPath()%>/auth/login" method="post">
   
   <table width="100%" height="96%" align="center">
      <tr width="100%" align="center">
         <td valign="middle" align="center" width="100%">
            
            Username: <input type="text" id="username" name="username" maxlength="256" style="width:150px" />

            Password: <input type="secret" id="password" name="password" maxlength="256" style="width:150px" />
            
            <input type="hidden" id="returl" name="returl" value="<%=request.getParameter("returl")%>" />
            
            <input type="submit" value="Login" />

            <div id="no-cookies" style="display:none">
               <table cellpadding="0" cellspacing="0" border="0" style="padding-top:16px;">
                  <tr>
                     <td>
                        <table cellpadding="0" cellspacing="0" border="0">
                           <tr>
                              <td class="mainSubText">
                                 Cookies are disabled in your browser. Please enable cookies to use this application.
                              </td>
                           </tr>
                        </table>
                     </td>
                  </tr>
               </table>
            </div>
            <script>
               document.cookie="_alfTest=_alfTest"
               var cookieEnabled = (document.cookie.indexOf("_alfTest") != -1);
               if (cookieEnabled == false)
               {
                  document.getElementById("no-cookies").style.display = 'inline';
               }
            </script>
            
         </td>
      </tr>
      
   </table>
      
</body>

</html>