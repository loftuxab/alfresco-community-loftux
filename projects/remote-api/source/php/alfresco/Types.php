<?php

class Store
{
   public $address;
   public $scheme;

   public function __construct($address, $scheme = "workspace")
   {
      $this->address = $address;
      $this->scheme = $scheme;
   }
}

class Reference
{
   public $store = null;
   public $uuid = null;
   public $path = null;

   public function __construct($store, $uuid = null, $path = null)
   {
      $this->store = $store;
      $this->uuid = $uuid;
      $this->path = $path;
   }
}

class Query
{
   public $language;
   public $statement;

   public function __construct($statement, $language = 'lucene')
   {
      $this->statement = $statement;
      $this->language = $language;
   }
}

?>