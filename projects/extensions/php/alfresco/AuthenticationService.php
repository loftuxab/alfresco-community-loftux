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
