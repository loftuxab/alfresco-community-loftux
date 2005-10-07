<?php

   require_once('alfresco/Types.php');
   require_once('alfresco/RepositoryService.php');

   $store = new Store('SpacesStore');
   $query = new Query($_POST['statement'], 'lucene');

   $repository_service = new RepositoryService($_POST['username'], $_POST['ticket']);
   $queryResults = $repository_service->query($store, $query);

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