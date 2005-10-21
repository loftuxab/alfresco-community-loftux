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
require_once('alfresco/webservice/WebServiceUtils.php');
require_once('alfresco/webservice/RepositoryWebService.php');

class RepositoryService extends BaseService
{
   public function __construct($auth_details)
   {
      parent::__construct($auth_details);
      $this->web_service = new RepositoryWebService();
   }

   public function getStores()
   {
     // Make the web service call
      $this->addSecurityHeader();
      $result = $this->web_service->getStores();
      $this->checkForError($result);
      
      $stores = array();
      $index = 0;
      foreach ($result as $value)
      {
         $store = new Store($value->address, $value->scheme);
         $stores[$index] = $store;
         $index ++;
      }

      return $stores;
   }

   public function query($store, $query, $include_meta_data = false)
   {
     // Make the web service call
      $this->addSecurityHeader();
      $store_value = getStoreSOAPValue($store);
      $query_value = getQuerySOAPValue($query);
      $result = $this->web_service->query($store_value, $query_value, $include_meta_data);
      $this->checkForError($result);

      // TODO marshal the returned object back into a helpful object
      
      return $result;
   }

   public function queryChildren($reference)
   {
      $this->addSecurityHeader();
      $result = $this->web_service->queryChildren(getReferenceSOAPValue($reference));
      $this->checkForError($result);

      // TODO marshal the returned object back into a helpful object
      
      return $result;
   }

   public function get($references)
   {

   }
}


?>
