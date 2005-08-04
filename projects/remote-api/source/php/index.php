<!--
<wsu:Timestamp xmlns:wsu="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd">
   <wsu:Created>2005-08-03T21:04:51Z</wsu:Created>
   <wsu:Expires>2005-08-03T21:09:51Z</wsu:Expires>
</wsu:Timestamp>
-->

<html>
   <head>
      <title>Alfresco Web Services</title>

      <style>
         body {font-family:verdana;font-size:10pt;}
      </style>
   </head>

   <body>
      <?php 
      require_once('SOAP/Client.php');

      print("Authenticating...");

      // get hold of the authentication service
      $client = new SOAP_Client("http://localhost:8080/web-client/remote-api/AuthenticationService");
      $namespace = array('namespace' => 'http://www.alfresco.org/ws/1.0/authentication', 'soapaction' => '', 'style' => 'document', 'use' => 'literal');
      $client->__options = array('trace'=>1);
      		
      // authenticate with a username and password
      $params =& new SOAP_Value('{http://www.alfresco.org/ws/1.0/authentication}authenticate', false,
                                $v=array('username' => 'admin', 'password' => 'admin'));

      $authResult = $client->call('authenticate', $v = array('authenticate' => $params), $namespace);

      /*print "<xmp>";
      print $client->wire;
      print "</xmp>";*/

      if (PEAR::isError($authResult))
      {
         print("<br><br><span style='font-weight:bold;color:red'>Error occurred: ");

         if ($authResult->getMessage())
         {
            print($authResult->getMessage());
         }
         else
         {
            print($authResult->userinfo->AuthenticationFault->message);
         }
         
         print("</span><br>\n");
      }
      else
      {
         print("<br><br>Ticket = " . $authResult->ticket);
         print("<br><br>Successfully authenticated, retrieving stores...");
      }
      
      // get hold of the repository service
      $client = new SOAP_Client("http://localhost:8080/web-client/remote-api/RepositoryService");
      $namespace = array('namespace' => 'http://www.alfresco.org/ws/1.0/repository', 'soapaction' => '', 'style' => 'document', 'use' => 'literal');
      $client->__options = array('trace'=>1);

      $username =& new SOAP_Value('{http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd}Username', false,
                                  'admin');
      $password =& new SOAP_Value('{http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd}Password', false,
                                  $authResult->ticket, array('Type' => 'http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-username-token-profile-1.0#PasswordText'));
      $usernameToken =& new SOAP_Value('{http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd}UsernameToken', false,
                                     $v = array($username, $password));
      $securityHeader =& new SOAP_Header('{http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd}Security', null,
                                         $usernameToken, 1);
      // remove the actor attribute that gets added by the constructor otherwise Axis gets upset!
      unset($securityHeader->attributes['SOAP-ENV:actor']);
      $client->addHeader($securityHeader);

      $params =& new SOAP_Value('{http://www.alfresco.org/ws/1.0/repository}getStores', false);
      $stores = $client->call('getStores', $v = array('getStores' => $params), $namespace);
      
      /*print "<xmp>";
      print $client->wire;
      print "</xmp>";*/
      
      if (PEAR::isError($stores))
      {
         print("<br><br><span style='font-weight:bold;color:red'>Error occurred: ");

         if ($stores->getMessage())
         {
            print($stores->getMessage());
         }
         else
         {
            print($stores->userinfo->RepositoryFault->message);
         }
         
         print("</span><br>\n");
      }
      else
      {
         print("<br><br>There are " . count($stores) . " stores:<br>");
         foreach ($stores as $store) 
         {
            print($store->scheme . ':' . $store->address . '<br>');
         }
      }
      ?>
   </body>

</html>