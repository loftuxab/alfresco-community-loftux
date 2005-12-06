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

require_once('alfresco/BaseService.php');
require_once('alfresco/type/Store.php');
require_once('alfresco/type/ResultSet.php');
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

   public function query($store, $statement, $language = 'lucene', $include_meta_data = false)
   {
      // Make the web service call
      $this->addSecurityHeader();
      $store_value = getStoreSOAPValue($store);
      $query_value = getQuerySOAPValue($statement, $language);
      $result = $this->web_service->query($store_value, $query_value, $include_meta_data);
      $this->checkForError($result);

      return ResultSet::createResultSet($result);
   }

   public function queryChildren($reference)
   {
      $this->addSecurityHeader();
      $result = $this->web_service->queryChildren(getReferenceSOAPValue($reference));
      $this->checkForError($result);

      return ResultSet::createResultSet($result);
   }

   public function queryParents($reference)
   {
      $this->addSecurityHeader();
      $result = $this->web_service->queryParents(getReferenceSOAPValue($reference));
      $this->checkForError($result);

      return ResultSet::createResultSet($result);
   }

   public function queryAssociated($reference, $association_type, $direction='target')
   {
      // TODO
   }

   public function fetchMore($querySession)
   {
      // TODO
   }

   public function update($statements)
   {
      // TODO
   }

   public function describe($items)
   {
      // TODO
   }

   public function get($references)
   {
      // TODO
   }
}


?>
