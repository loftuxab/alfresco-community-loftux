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
   require_once('alfresco/RepositoryService.php');
   require_once('alfresco/type/Store.php');
   require_once('alfresco/type/Reference.php');


   $authentication_service = new AuthenticationService();
   if ($authentication_service->isUserAuthenticated() == false)
   {
      // Redirect to the login page
      header("Location: /examples/common/login.php?redirect=/examples/userMaint/index.php");
      exit;
   }

   $auth_details = $authentication_service->getAuthenticationDetails();
   $administation_service = new AdministrationService($auth_details);
   $repository_service = new RepositoryService($auth_details);

   $action = $_REQUEST["action"];
   $do = "false";
   if (isset($_REQUEST["do"]) == true)
   {
      $do = $_REQUEST["do"];
   }

   $user_details = null;
   if ($action == "edit")
   {
      $user_details = $administation_service->getUser($_REQUEST["username"]);
   }

   if ($do == "true")
   {
      if ($user_details == null)
      {
         $user_details = new UserDetails();
      }

      // Update the user details
      $user_details->first_name = $_REQUEST['firstname'];
      $user_details->last_name = $_REQUEST['lastname'];
      $user_details->email = $_REQUEST['email'];
      $user_details->organization_id = $_REQUEST['orgid'];
      $user_details->home_folder = $_REQUEST['homefolder'];

      if ($action == "edit")
      {
          // Update the user details
          $administation_service->updateUsers(array($user_details));
      }
      else if ($action == "create")
      {
         // Set the user name and password
         $user_details->user_name = $_REQUEST['username'];
         $user_details->password = $_REQUEST['password'];
         
         // Set the home folder to the company home (need to this as you can't leave the company home blank!)
         $store = new Store('SpacesStore');
         $result = $repository_service->get(null, $store, 'PATH:"/app:company_home"');
         $user_details->home_folder = $store->scheme."://".$store->address."/".$result->reference->uuid;

         // Create the user
         $administation_service->createUsers(array($user_details));
      }

      // Redirect back to main page
      header("Location: index.php");
   }
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
   
   <table cellspacing=3 cellpadding=2 border=0 width=95% align=center>
   <tr>
   <td>

   <table cellspacing=0 cellpadding=0 border=0 width=100%>

      <tr>
          <td width=7><img src='/examples/common/images/blue_01.gif' width=7 height=7 alt=''></td><td background='/examples/common/images/blue_02.gif'><img src='/examples/common/images/blue_02.gif' width=7 height=7 alt=''></td>
          <td width=7><img src='/examples/common/images/blue_03.gif' width=7 height=7 alt=''></td></tr><tr><td background='/examples/common/images/blue_04.gif'><img src='/examples/common/images/blue_04.gif' width=7 height=7 alt=''></td>
          <td bgcolor='#D3E6FE'>
              <table border='0' cellspacing='0' cellpadding='0' width='100%'><tr><td><span class='mainSubTitle'>User Details</span></td></tr></table>
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
              
              <form id='mainform' name='mainform' action='edituser.php' method='post'>
              
                <input type='hidden' name='do' value='true'/>
                <input type='hidden' name='action'  value='<?php echo $action ?>'>
                <input type='hidden' name='homefolder' value='<?php if ($user_details != null) {echo $user_details->home_folder;} ?>'>

                <table border='0' cellspacing=3 cellpadding=2>

                   <tr>
                       <td>User name:</td>
                       <td><input type='edit' name='username' <?php if ($action == 'edit') {echo "readonly";} ?> value='<?php if ($user_details != null) {echo $user_details->user_name;} ?>' style='width: 250'></td>
                   </tr>
<?php
                   if ($action == "create")
                   {
?>
                   <tr>
                       <td>Password:</td>
                       <td><input type='password' name='password' style='width: 250'></td>
                   </tr>
<?php
                   }
?>
                   <tr>
                       <td>First name:</td>
                       <td><input type='edit' name='firstname' value='<?php if ($user_details != null) {echo $user_details->first_name;} ?>' style='width: 250'></td>
                   </tr>
                   <tr>
                       <td>Last name:</td>
                       <td><input type='edit' name='lastname' value='<?php if ($user_details != null) {echo $user_details->last_name;} ?>' style='width: 250'></td>
                   </tr>
                   <tr>
                       <td>E-Mail:</td>
                       <td><input type='edit' name='email' value='<?php if ($user_details != null) {echo $user_details->email;} ?>' style='width: 250'></td>
                   </tr>
                   <tr>
                       <td>Organization Id:</td>
                       <td><input type='edit' name='orgid' value='<?php if ($user_details != null) {echo $user_details->organization_id;} ?>' style='width: 250'></td>
                   </tr>

                </table>

              </form>
          </td>
          <td background='/examples/common/images/white_06.gif'><img src='/examples/common/images/white_06.gif' width=7 height=7 alt=''></td>
       </tr>
       <tr>
          <td width=7><img src='/examples/common/images/white_07.gif' width=7 height=7 alt=''></td>
          <td background='/examples/common/images/white_08.gif'><img src='/examples/common/images/white_08.gif' width=7 height=7 alt=''></td>
          <td width=7><img src='/examples/common/images/white_09.gif' width=7 height=7 alt=''></td>
       </tr>
   </table>

   </td>
   <td valign=top width='1%'>
   <table cellspacing=0 cellpadding=0 border=0 align=center valign=top>
      <tr>
         <td width=7><img src='/examples/common/images/blue_01.gif' width=7 height=7 alt=''></td>
         <td background='/examples/common/images/blue_02.gif'><img src='/examples/common/images/blue_02.gif' width=7 height=7 alt=''></td>
         <td width=7><img src='/examples/common/images/blue_03.gif' width=7 height=7 alt=''></td></tr>
      <tr>
        <td background='/examples/common/images/blue_04.gif'><img src='/examples/common/images/blue_04.gif' width=7 height=7 alt=''></td>
        <td bgcolor='#D3E6FE'>
          
          <table cellpadding="1" cellspacing="1" border="0">
            <tr>
              <td align="center">
                  <input name="btnOk" type="submit" value="Ok" onclick="javascript:document['mainform'].submit();"/>
              </td>
            </tr>
            <tr>
              <td align="center">
                  <input name="btnCancel" type="submit" value="Cancel" onclick="javascript:window.location.href='index.php';" />
              </td>
            </tr>
          </table>
        </td>
        <td background='/examples/common/images/blue_06.gif'><img src='/examples/common/images/blue_06.gif' width=7 height=7 alt=''></td>
      </tr>
      <tr>
         <td width=7><img src='/examples/common/images/blue_07.gif' width=7 height=7 alt=''></td>
         <td background='/examples/common/images/blue_08.gif'><img src='/examples/common/images/blue_08.gif' width=7 height=7 alt=''></td>
         <td width=7><img src='/examples/common/images/blue_09.gif' width=7 height=7 alt=''></td>
      </tr>
   </table>

   </td>
   </tr>
   </table>

   </body>

</html>
