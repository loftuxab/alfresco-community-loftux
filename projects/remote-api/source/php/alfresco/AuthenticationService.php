<?php

require_once('alfresco/Common.php');
require_once('alfresco/webservice/AuthenticationWebService.php');

class AuthenticationService extends BaseService
{
   public function __construct()
   {
      $this->web_service = new AuthenticationWebService();
   }

   public function startSession($user, $password)
   {
      $authResult = $this->web_service->startSession($user, $password);
      $this->checkForError($authResult);
      
      // Put the user credentials into the session
      $_SESSION["authDetails"] = serialize(new AuthenticationDetails($authResult->username, $authResult->ticket));
   }
   
   public function endSession()
   {
      $this->web_service->endSession();
      unset($_SESSION["authDetails"]);
   }
   
   public function isUserAuthenticated()
   {
     // Check for the current credentials in the session
     return isset($_SESSION["authDetails"]);
   }
   
   public function getAuthenticationDetails()
   {
      return unserialize($_SESSION["authDetails"]);
   }
}

?>
