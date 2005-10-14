<?php

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
