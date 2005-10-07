<?php

require_once('alfresco/Types.php');
require_once('alfresco/webservice/WebServiceUtils.php');
require_once('alfresco/webservice/RepositoryWebService.php');

class RepositoryService
{
   private $repository_web_service;
   private $user;
   private $ticket;

   public function __construct($user, $ticket)
   {
      // TOD this information should be stored in the session somewhere
      $this->user = $user;
      $this->ticket = $ticket;

      $this->repository_web_service = new RepositoryWebService();
   }

   public function getStores()
   {
      addSecurityHeader($this->repository_web_service, $this->user, $this->ticket);

      $stores = array();
      
      $result = $this->repository_web_service->getStores();
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
      addSecurityHeader($this->repository_web_service, $this->user, $this->ticket);

      // TODO convert the returned SOAP value into an object value
      $client = $this->repository_web_service;
      $store_value = getStoreSOAPValue($store);
      $query_value = getQuerySOAPValue($query);

      return $client->query($store_value, $query_value, $include_meta_data);
   }

   public function queryChildren($reference)
   {
      addSecurityHeader($this->repository_web_service, $this->user, $this->ticket);

      // TODO convert the returned SOAP value into an object value
      return $this->repository_web_service->queryChildren(getReferenceSOAPValue($reference));
   }

   public function get($references)
   {
      addSecurityHeader($this->repository_web_service, $this->user, $this->ticket);


   }
}


?>