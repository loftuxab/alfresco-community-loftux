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

require_once('SOAP/Client.php');
require_once('alfresco/BaseService.php');
require_once('alfresco/webservice/WebServiceUtils.php');
require_once('alfresco/webservice/AdministrationWebService.php');

class AdministrationService extends BaseService
{
   public function __construct($auth_details)
   {
      parent::__construct($auth_details);
      $this->web_service = new AdministrationWebService();
   }
   
   function queryUsers($user_name=null)
   {
      $filter = null;
      if ($user_name != null)
      {
         $params = array(new SOAP_VALUE('userName', false, $user_name));
         $filter = new SOAP_Value('filter', false, $params);
      }

      $this->addSecurityHeader();
      $result = $this->web_service->queryUsers($filter);
      $this->checkForError($result);
      
      return UserQueryResult::createUserQueryResult($result);
   }
   
   function getUser($user_name)
   {
      $this->addSecurityHeader();
      $result = $this->web_service->getUser($user_name);
      $this->checkForError($result);
      
      return UserDetails::createUserDetails($result);
   }
   
   function updateUsers($user_details)
   {
      $web_service_user_details = array();
      foreach ($user_details as $user_detail)
      {
         $values = array();

         $values[] = new SOAP_Value('properties', false, array(
                                                               new SOAP_Value('name', false, '{http://www.alfresco.org/model/content/1.0}firstName'),
                                                               new SOAP_Value('value', false, $user_detail->first_name)));
         $values[] = new SOAP_Value('properties', false, array(
                                                               new SOAP_Value('name', false, '{http://www.alfresco.org/model/content/1.0}lastName'),
                                                               new SOAP_Value('value', false, $user_detail->last_name)));
         $values[] = new SOAP_Value('properties', false, array(
                                                               new SOAP_Value('name', false, '{http://www.alfresco.org/model/content/1.0}email'),
                                                               new SOAP_Value('value', false, $user_detail->email)));
         $values[] = new SOAP_Value('properties', false, array(
                                                               new SOAP_Value('name', false, '{http://www.alfresco.org/model/content/1.0}organizationId'),
                                                               new SOAP_Value('value', false, $user_detail->organization_id)));
         $values[] = new SOAP_Value('properties', false, array(
                                                               new SOAP_Value('name', false, '{http://www.alfresco.org/model/content/1.0}homeFolder'),
                                                               new SOAP_Value('value', false, $user_detail->home_folder)));

         $values[] = new SOAP_Value('userName', false, $user_detail->user_name);
      }

      $this->addSecurityHeader();
      $result = $this->web_service->updateUsers(new SOAP_Value('users', "UserDetails", $values));
      $this->checkForError($result);
   }
}

class UserQueryResult
{
   public $query_session = null;
   public $users = array();

   public static function createUserQueryResult($web_service_result)
   {
      $user_query_result = new UserQueryresult();
      $user_query_result->query_session = $web_service_result->querySession;

      foreach($web_service_result->userDetails as $web_service_user_details)
      {
         $user_query_result->users[] = UserDetails::createUserDetails($web_service_user_details);
      }
      
      return $user_query_result;
   }
}

class UserDetails
{
   public $user_name = null;
   public $first_name = null;
   public $last_name = null;
   public $email = null;
   public $home_folder = null;
   public $organization_id = null;
   
   public static function createUserDetails($web_service_user_details)
   {
       $user_details = new UserDetails();
       $user_details->user_name = $web_service_user_details->userName;

       foreach($web_service_user_details->properties as $property)
       {
          if ($property->name == "{http://www.alfresco.org/model/content/1.0}firstName")
          {
             $user_details->first_name = $property->value;
          }
          else if ($property->name == "{http://www.alfresco.org/model/content/1.0}lastName")
          {
             $user_details->last_name = $property->value;
          }
          else if ($property->name == "{http://www.alfresco.org/model/content/1.0}email")
          {
             $user_details->email = $property->value;
          }
          else if ($property->name == "{http://www.alfresco.org/model/content/1.0}organizationId")
          {
             $user_details->organization_id = $property->value;
          }
          else if ($property->name == "{http://www.alfresco.org/model/content/1.0}homeFolder")
          {
             $user_details->home_folder = $property->value;
          }
       }
       
       return $user_details;
   }
}

?>