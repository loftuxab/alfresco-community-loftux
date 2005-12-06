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

require_once('alfresco/webservice/WebServiceUtils.php');
require_once('alfresco/type/AuthenticationDetails.php');

class BaseService
{
   protected $auth_details;
   protected $web_service;
   
   public function __construct($auth_details)
   {
     $this->auth_details = $auth_details;
   }

   protected function addSecurityHeader()
   {
      addSecurityHeader($this->web_service, $this->auth_details->getUserName(), $this->auth_details->getTicket());
   }
   
   protected function checkForError($result)
   {
      if ($result != null && PEAR::isError($result))
      {
         $exception_message = "An unidentified exception has occured.";
         if ($result->getMessage())
         {
            $exception_message = $result->getMessage();
         }
         else
         {
            if (isset($result->userinfo->AuthenticationFault) == true)
            {
               $exception_message = $result->userinfo->AuthenticationFault->message;
            }
            else if (isset($result->userinfo->RepositoryFault) == true)
            {
               $exception_message = $result->userinfo->RepositoryFault->message;
            }
         }
         error_log("An error was encountered when calling web service: ".$exception_message);
         throw new Exception($exception_message);
      }
   }
}

?>
