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

   require_once('alfresco/RepositoryService.php');
   require_once('alfresco/ContentService.php');
   require_once('alfresco/AuthenticationService.php');
   require_once('alfresco/Store.php');
   require_once('alfresco/Reference.php');
   require_once('alfresco/tag/TagFramework.php');
   require_once('alfresco/tag/CommonTags.php');


   $authentication_service = new AuthenticationService();
   if ($authentication_service->isUserAuthenticated() == false)
   {
      // Redirect to the login page
      header("Location: login.php");
      exit;
   }

   $auth_details = $authentication_service->getAuthenticationDetails();

   $store = new Store('SpacesStore');
   $reference = null;
   $path = null;

   $repository_service = new RepositoryService($auth_details);
   $content_service = new ContentService($auth_details);

   if (isset($_REQUEST['uuid']) == false)
   {
      $reference = new Reference($store, null, "/app:company_home");
      $path = 'Company Home';

   }
   else
   {
      $reference = new Reference($store, $_REQUEST['uuid']);
      $path = $_REQUEST['path'].'|'.$_REQUEST['uuid'].'|'.$_REQUEST['name'];
   }

   $error_message = "";
   try
   {
      $queryResults = $repository_service->queryChildren($reference);
   }
   catch (Exception $e)
   {
      $error_message = $e->getMessage();
   }
   
   set_exception_handler("exception_handler");
   function exception_handler($exception)
   {
      print "Error: ".$exception->getMessage();
      print "<br>Stack trace: ".$exception->getTraceAsString();
   }

   start_tags();

   function getURL($current_id, $current_name, $path, $current_type="{http://www.alfresco.org/model/content/1.0}folder")
   {
      global $store, $content_service, $auth_details;

      $result = null;
      if ($current_type == "{http://www.alfresco.org/model/content/1.0}content")
      {
         $read_result = $content_service->read(new Reference($store, $current_id));
         $result = $read_result->url."?ticket=".$auth_details->getTicket();
      }
      else
      {
         $result = "index.php?".
                     "&uuid=".$current_id.
                     "&name=".$current_name.
                     "&path=".$path;
      }

      return $result;
   }

   function outputRow($row)
   {
      global $path;

      $name = $row->getValue('{http://www.alfresco.org/model/content/1.0}name');
      $uuid = $row->uuid();
      $type = $row->type();

      print("<tr><td><a href='");
      print(getURL($uuid, $name, $path, $type));
      print("'>");
      print($name);
      print("</a></td></tr>");
   }
   
   function outputTable($title, $query_results, $type_filter, $empty_message)
   {
      print(
          '<table border="0" width="95%" align="center">'.
          '   <tr style="{background-color: #D3E6FE}">'.
          '      <td>'.$title.'</td>'.
          '   </tr>'.
          '   <tr>'.
          '      <td>'.
          '         <table border="0" width="100%">');

      foreach ($query_results->rows() as $row)
      {
         if ($row->type() == $type_filter)
         {
            outputRow($row);
         }
      }

      print(
          '         </table>'.
          '      </td>'.
          '   </tr>'.
          '</table>');
   }
   
   function outputBreadcrumb($path)
   {
      print(
          '<table border="0" width="95%" align="center">'.
          '   <tr>'.
          '      <td>');

      $values = split("\|", $path);
      $home = $values[0];
      $path = $home;
      $id_map = array();
      for ($counter = 1; $counter < count($values); $counter += 2)
      {
         $id_map[$values[$counter]] = $values[$counter+1];
      }

       print("<a href='index.php'><b>".$home."</b></a>");
       foreach($id_map as $id=>$name)
       {
          $path .= '|'.$id.'|'.$name;
          print("&nbsp;&gt;&nbsp;<a href='".getURL($id, $name, $path)."'><b>".$name."</b></a>");
       }

       print(
        '      </td>'.
        '   </tr>'.
        '</table>');
   }

?>

<html>
   <head>
      <title>Browse Repository</title>
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
<?php

   if ($error_message != "")
   {
?>
      <alftag:error error_message='<?php echo $error_message ?>'/>
<?php
   }
   else
   {
       outputBreadcrumb($path);
       outputTable("Browse Spaces", $queryResults, "{http://www.alfresco.org/model/content/1.0}folder", "There are no spaces");
       outputTable("Content items", $queryResults, "{http://www.alfresco.org/model/content/1.0}content", "There is no content");
   }
?>

   </body>

</html>
