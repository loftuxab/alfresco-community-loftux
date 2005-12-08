<?php

   // Start the session
   session_start();

   require_once('alfresco/RepositoryService.php');
   require_once('alfresco/AuthenticationService.php');
   require_once('alfresco/type/Store.php');
   require_once('alfresco/type/Reference.php');
   require_once('alfresco/tag/TagFramework.php');
   require_once('alfresco/tag/CommonTags.php');
   require_once('alfresco/tag/DataListTag.php');

   $error_message = "";
   try
   {
      // Log in
      $authentication_service = new AuthenticationService();
      $authentication_service->startSession("admin", "admin");
      $auth_details = $authentication_service->getAuthenticationDetails();

      // Get the children of the company home
      $store = new Store('SpacesStore');
      $repository_service = new RepositoryService($auth_details);
      $reference = new Reference($store, null, "/app:company_home");
      $result_set = $repository_service->queryChildren($reference);
   }
   catch (Exception $e)
   {
      // Catch and store the exception
      $error_message = $e->getMessage();
   }

   // Process that tags
   start_tags();
 ?>

<html>

   <head>
      <title>This is a very simple example</title>
   </head>

   <body>

   <b>A list of all the items in company home and their descriptions:</b>

   <br><br>

   <alftag:datalist result_set='<?php echo $result_set->id() ?>'>
      <alftag:datarow>
         <alftag:text data_source='{http://www.alfresco.org/model/content/1.0}name'/>&nbsp;&nbsp;
         <i><alftag:text data_source='{http://www.alfresco.org/model/content/1.0}description'/></i>
      </alftag:datarow>
   </alftag:datalist>

   <br><br>

   <!-- If there is an error then display it -->
   <alftag:error error_message='<?= $error_message ?>'/>

   </body>

</html>

