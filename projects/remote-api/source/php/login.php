<?php
   require_once('SOAP/Client.php');

   // get hold of the authentication service
   $client = new SOAP_Client("http://localhost:8080/alfresco/api/AuthenticationService");
   $namespace = array('namespace' => 'http://www.alfresco.org/ws/service/authentication/1.0', 'soapaction' => '', 'style' => 'document', 'use' => 'literal');
   $client->__options = array('trace'=>1);
         
   // authenticate with a username and password
   $params =& new SOAP_Value('{http://www.alfresco.org/ws/service/authentication/1.0}startSession', false,
                             $v=array('username' => $_POST['username'], 'password' => $_POST['password']));

   $authResult = $client->call('authenticate', $v = array('authenticate' => $params), $namespace);

   /*print "<xmp>";
   print $client->wire;
   print "</xmp>";*/

   if (PEAR::isError($authResult) == false)
   {
      header("Location: browse.php?username=" . $_POST['username'] . "&ticket=" . $authResult->ticket);
      exit;
   }
?>

<html>
   <head>
      <title>Login</title>
      <style>
         body {font-family: verdana; font-size: 8pt;}
         td {font-family: verdana; font-size: 8pt;}
         input {font-family: verdana; font-size: 8pt;}
         .maintitle {font-family: verdana; font-size: 10pt; font-weight: bold; padding-bottom: 15px;}
         .error {color: red;}
      </style>
   </head>

   <body>
      <div class="maintitle">Authentication Failed:</div>
      <span class="error">
<?php
         if ($authResult->getMessage())
         {
            print($authResult->getMessage());
         }
         else
         {
            print($authResult->userinfo->AuthenticationFault->message);
         }
?>
      </span>
   </body>

</html>