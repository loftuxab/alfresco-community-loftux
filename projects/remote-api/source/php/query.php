<html>
   <head>
      <title>Query</title>
      <style>
         body {font-family: verdana; font-size: 8pt;}
         td {font-family: verdana; font-size: 8pt;}
         input {font-family: verdana; font-size: 8pt;}
         .maintitle {font-family: verdana; font-size: 10pt; font-weight: bold;}
      </style>
   </head>

   <body>
      <div class="maintitle">Enter a Lucene query to execute against the repository</div>

      <form id="queryForm" name="queryForm" method="post" action="query-results.php" enctype="application/x-www-form-urlencoded">
         <input type="hidden" name="username" value="<?php print $_GET['username']; ?>"/>
         <input type="hidden" name="ticket" value="<?php print $_GET['ticket']; ?>"/>
         <table border="0" cellspacing="4" cellpadding="4" class="loginDialog">
            <tr>
               <td>Query:</td>
               <td><input name="statement" type="text" style="width:250px"/></td>
               <td><input type="submit" value="Execute"/></td>
            </tr>
         </table>
      </form>

   </body>

</html>