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
   require_once('alfresco/AdministrationService.php');


   $authentication_service = new AuthenticationService();
   if ($authentication_service->isUserAuthenticated() == false)
   {
      // Redirect to the login page
      header("Location: /examples/common/login.php?redirect=/examples/userMaint/index.php");
      exit;
   }

   $auth_details = $authentication_service->getAuthenticationDetails();

   $administation_service = new AdministrationService($auth_details);
   $user_query_results = $administation_service->queryUsers();
?>

<html>
   <head>
      <title>User Maintenance</title>
      <style>
         body {font-family: verdana; font-size: 8pt;}
         tr {font-family: verdana; font-size: 8pt;}
         td {font-family: verdana; font-size: 8pt;}
         input {font-family: verdana; font-size: 8pt;}
         .maintitle {font-family: verdana; font-size: 10pt; font-weight: bold; padding-bottom: 15px;}
         a:link, a:visited
         {
      	 font-size: 11px;
      	 color: #465F7D;
      	 text-decoration: none;
      	 font-family: Tahoma, Arial, Helvetica, sans-serif;
      	 font-weight: normal;
        }
        a:hover
        {
        	color: #4272B4;
        	text-decoration: underline;
        	font-weight: normal;
        }
      </style>
   </head>

   <body>

   <table cellspacing=0 cellpadding=2 width=95% align=center>
      <tr>
          <td width=100%>

            <table cellspacing=0 cellpadding=0 width=100%>
            <tr>
               <td style="padding-right:4px;"><img src="/examples/common/images/AlfrescoLogo32.png" border=0 alt="Alfresco" title="Alfresco" align=absmiddle></td>
               <td><img src="/examples/common/images/titlebar_begin.gif" width=10 height=30></td>
               <td width=100% style="background-image: url(/examples/common/images/titlebar_bg.gif)">
                   <b><font style='color: white'>User Maintenance</font></b>
               </td>
               <td><img src="/examples/common/images/titlebar_end.gif" width=8 height=30></td>
            </tr>
            </table>

          </td>

          <td width=8>&nbsp;</td>
          <td><nobr>
              <img src="/examples/common/images/logout.gif" border=0 alt="Logout (<?php echo $auth_details->getUserName() ?>)" title="Logout (<?php echo $auth_details->getUserName() ?>)" align=absmiddle><span style='padding-left:2px'><a href='/examples/common/login.php?logout=true&redirect=/examples/userMaint/index.php'>Logout (<?php echo $auth_details->getUserName() ?>)</a></span>
           </nobr></td>
        </tr>
   </table>
   <br>
   <table cellspacing=0 cellpadding=0 border=0 width=95% align=center>
      <tr>
          <td colspan=99 align=left><nobr>
              <img src="/examples/common/images/add_user.gif" border=0 alt="Create User" title="Create User" align=absmiddle><span style='padding-left:2px; valign: bottom'><a href='edituser.php?action=create'>Create User</a></span>
           </nobr>
          </td>
      <tr>
      <tr height=7><td></td></tr>
      <tr>
          <td width=7><img src='/examples/common/images/blue_01.gif' width=7 height=7 alt=''></td><td background='/examples/common/images/blue_02.gif'><img src='/examples/common/images/blue_02.gif' width=7 height=7 alt=''></td>
          <td width=7><img src='/examples/common/images/blue_03.gif' width=7 height=7 alt=''></td></tr><tr><td background='/examples/common/images/blue_04.gif'><img src='/examples/common/images/blue_04.gif' width=7 height=7 alt=''></td>
          <td bgcolor='#D3E6FE'>
              <table border='0' cellspacing='0' cellpadding='0' width='100%'><tr><td><span class='mainSubTitle'>Users</span></td></tr></table>
          </td>
          <td background='/examples/common/images/blue_06.gif'><img src='/examples/common/images/blue_06.gif' width=7 height=7 alt=''></td>
      </tr>
      <tr>
          <td width=7><img src='/examples/common/images/blue_white_07.gif' width=7 height=7 alt=''></td>
          <td background='/examples/common/images/blue_08.gif'><img src='/examples/common/images/blue_08.gif' width=7 height=7 alt=''></td>
          <td width=7><img src='/examples/common/images/blue_white_09.gif' width=7 height=7 alt=''></td>
      </tr>
      <tr>
          <td background='/examples/common/images/white_04.gif'><img src='/examples/common/images/white_04.gif' width=7 height=7 alt=''></td>
          <td bgcolor='white' style='padding-top:6px;'>
              <table border='0' cellspacing=3 cellpadding=2>
<?php
              foreach($user_query_results->users as $user)
              {
?>
                 <tr>
                    <td><img src='/examples/common/images/person.gif'></td>
                    <td width='150'><a href='edituser.php?action=edit&username=<?php echo $user->user_name ?>'><?php echo $user->first_name." ".$user->last_name ?></a></td>
                    <td width='80'><?php echo $user->user_name ?></td>
                    <td width='200'><?php echo $user->email ?></td>
                 </tr>
<?php
              }
?>
              </table>
          </td>
          <td background='/examples/common/images/white_06.gif'><img src='/examples/common/images/white_06.gif' width=7 height=7 alt=''></td>
       </tr>
       <tr>
          <td width=7><img src='/examples/common/images/white_07.gif' width=7 height=7 alt=''></td>
          <td background='/examples/common/images/white_08.gif'><img src='/examples/common/images/white_08.gif' width=7 height=7 alt=''></td>
          <td width=7><img src='/examples/common/images/white_09.gif' width=7 height=7 alt=''></td>
       </tr>
   </table>

   </body>

</html>
