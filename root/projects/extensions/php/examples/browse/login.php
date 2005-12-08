<?php

/*
  Copyright (C) 2005 Alfresco, Inc.

  Licensed under the Mozilla Public License version 1.1
  with a permitted attribution clause. You may obtain a
  copy of the License at

    http://www.alfresco.org/legal/license.txt

  Unless required by applicable law or agreed to in writing,
  software distributed under the License is distributed on an
  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
  either express or implied. See the License for the specific
  language governing permissions and limitations under the
  License.
*/

   // Start the session
   session_start();
   
   require_once('alfresco/AuthenticationService.php');
   require_once('alfresco/tag/TagFramework.php');
   require_once('alfresco/tag/CommonTags.php');

   $error_message = "";

   if (isset($_REQUEST["username"]) == true && isset($_REQUEST["password"]) == true)
   {
      try
      {
         $authentication_service = new AuthenticationService();
         $authentication_service->startSession($_REQUEST["username"], $_REQUEST["password"]);
      
         header("Location: index.php");
         exit;
       }
       catch (Exception $e)
       {
         $error_message = $e->getMessage();
       }
   }
   
   start_tags();
?>


<html>
   <head>
      <title>Alfresco Web Services Example</title>
      <style>
         body {font-family: verdana; font-size: 8pt;}
         td {font-family: verdana; font-size: 8pt;}
         input {font-family: verdana; font-size: 8pt;}
         .title {font-family: verdana; font-size: 8pt; font-weight: bold;}
         .loginDialog {border: 1px solid black; -moz-border-radius: 7px;}
      </style>
   </head>

   <body>

      <form id="loginForm" name="loginForm" method="post" action="login.php" enctype="application/x-www-form-urlencoded">
         <table border="0" width="98%" height="100%" align="center">
            <tr >
               <td valign="middle" align="center" width="100%">
                  <table border="0" cellspacing="4" cellpadding="4" class="loginDialog">
                     <tr>
                        <td colspan="2">
                           <img src="AlfrescoLogo200.png" width="200" height="58" alt="Alfresco" title="Alfresco">
                        </td>
                     </tr>
                     <tr>
                        <td colspan="2">
                           <span class='title'>Enter Login details:</span>
                        </td>
                     </tr>
                     <tr>
                        <td>User Name:</td>
                        <td><alftag:input name="username" type="text" style="width:150px"/></td>
                     </tr>
                     
                     <tr>
                        <td>Password:</td>
                        <td><alftag:input name="password" type="password" style="width:150px"/></td>
                     </tr>
                     <tr>
                        <td colspan="2" align="right"><input type="submit" value="Login"/></td>
                     </tr>
                     <tr>
                        <td colspan="2"><alftag:error error_message='<?php echo $error_message ?>'/></td>
                     </tr>
                  </table>
               </td>
            </tr>
         </table>
      </form>

   </body>

</html>
