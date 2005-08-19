<?php
   require_once('SOAP/Client.php');

   // get hold of the repository service
   $client = new SOAP_Client("http://localhost:8080/alfresco/api/RepositoryService");
   $namespace = array('namespace' => 'http://www.alfresco.org/ws/service/repository/1.0', 'soapaction' => '', 'style' => 'document', 'use' => 'literal');
   $client->__options = array('trace'=>1);

   $username =& new SOAP_Value('{http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd}Username', false,
                               $_POST['username']);
   $password =& new SOAP_Value('{http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd}Password', false,
                               $_POST['ticket'], array('Type' => 'http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-username-token-profile-1.0#PasswordText'));
   $usernameToken =& new SOAP_Value('{http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd}UsernameToken', false,
                                  $v = array($username, $password));
   $securityHeader =& new SOAP_Header('{http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd}Security', null,
                                      $usernameToken, 1);
   // remove the actor attribute that gets added by the constructor otherwise Axis gets upset!
   unset($securityHeader->attributes['SOAP-ENV:actor']);
   $client->addHeader($securityHeader);

   $scheme =& new SOAP_Value('{http://www.alfresco.org/ws/model/content/1.0}scheme', '{http://www.alfresco.org/ws/model/content/1.0}StoreEnum', 'workspace');
   $address =& new SOAP_Value('{http://www.alfresco.org/ws/model/content/1.0}address', false, 'SpacesStore');
   $store =& new SOAP_Value('{http://www.alfresco.org/ws/service/repository/1.0}store', false, $v=array($scheme, $address));

   $language =& new SOAP_Value('{http://www.alfresco.org/ws/model/content/1.0}language', '{http://www.alfresco.org/ws/model/content/1.0}QueryLanguageEnum', 'lucene');
   $statement =& new SOAP_Value('{http://www.alfresco.org/ws/model/content/1.0}statement', false, $_POST['statement']);
   $query =& new SOAP_Value('{http://www.alfresco.org/ws/service/repository/1.0}query', false, $v=array($language, $statement));

   $params =& new SOAP_Value('{http://www.alfresco.org/ws/service/repository/1.0}query', false, $v=array($store, $query, 'includeMetaData' => false));

   $queryResults = $client->call('query', $v = array('query' => $params), $namespace);

   /*print "<xmp>";
   print $client->wire;
   print "</xmp>";*/

   /*print "<br><br><xmp>";
   var_dump($queryResults);
   print "</xmp>";*/
?>

<html>
   <head>
      <title>Query Results</title>
      <style>
         body {font-family: verdana; font-size: 8pt;}
         tr {font-family: verdana; font-size: 8pt;}
         td {font-family: verdana; font-size: 8pt;}
         input {font-family: verdana; font-size: 8pt;}
         .maintitle {font-family: verdana; font-size: 10pt; font-weight: bold; padding-bottom: 15px;}
      </style>
   </head>

   <body>
      <div class="maintitle">Query Results</div>
<?php

   if (PEAR::isError($queryResults))
   {
      print("<span style='font-weight:bold;color:red'>Error occurred: ");

      if ($queryResults->getMessage())
      {
         print($queryResults->getMessage());
      }
      else
      {
         print($queryResults->userinfo->RepositoryFault->message);
      }
      
      print("</span>\n");
   }
   else
   {
      print('Query Session  = ' . $queryResults->querySession . '<br/><br/>');

      if (isset($queryResults->resultSet->rows))
      {
         // this means there is one row of data
         $row = $queryResults->resultSet->rows;
         print('<table border="1">');
         print('<th>Id</th><th>Type</th><th>Name</th>');
         print('<tr><td>');
         print($row->node->id);
         print('</td><td>');
         print($row->node->type);
         print('</td>');
         if (isset($row->columns))
         {
            print('<td>');
            foreach($row->columns as $column)
            {
               if (strpos($column->name, '}name') == true)
               {
                  print($column->value);
               }
            }

            print('</td>');
         }
         else
         {
            print('<td>&nbsp;</td>');
         }
         print('</tr>');
         print("</tr></table>");
      }
      else
      {
         if (isset($queryResults->resultSet->size) && $queryResults->resultSet->size == 0)
         {
            print 'The query returned no results';
         }
         else
         {
            // this means there are several rows to display
            print('<table border="1">');
            print('<th>Id</th><th>Type</th><th>Name</th>');

            for ($i = 0; $i < count($queryResults->resultSet) - 1; $i++)
            {
               print('<tr><td>');
               print($queryResults->resultSet[$i]->node->id);
               print('</td><td>');
               print($queryResults->resultSet[$i]->node->type);
               print('</td>');
               if (isset($queryResults->resultSet[$i]->columns))
               {
                  print('<td>');
                  foreach($queryResults->resultSet[$i]->columns as $column)
                  {
                     if (strpos($column->name, '}name') == true)
                     {
                        print $column->value;
                     }
                  }

                  print('</td>');
               }
               else
               {
                  print '<td>&nbsp;</td>';
               }
               print('</tr>');
            }

            print("</tr></table>");
         }
      }
   }
?>

      <form id="queryForm" name="queryForm" method="get" action="query.php" style="padding-top: 15px;">
         <input type="hidden" name="username" value="<?php print $_POST['username']; ?>"/>
         <input type="hidden" name="ticket" value="<?php print $_POST['ticket']; ?>"/>
         <input type="submit" value="Back"/>
      </form>

   </body>

</html>