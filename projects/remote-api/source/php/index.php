






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
                        <td><input name="username" type="text" style="width:150px"/></td>
                     </tr>
                     
                     <tr>
                        <td>Password:</td>
                        <td><input name="password" type="password" style="width:150px"/></td>
                     </tr>
                     <tr>
                        <td colspan="2" align="right"><input type="submit" value="Login"/></td>
                     </tr>
                  </table>
               </td>
            </tr>
         </table>
      </form>

   </body>

</html>