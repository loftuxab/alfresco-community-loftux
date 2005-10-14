<?php

require_once('alfresco/webservice/WebServiceUtils.php');

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
      if (PEAR::isError($result))
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
         throw new Exception($exception_message);
      }
   }
}

class AuthenticationDetails
{
   private $username;
   private $ticket;

   public function __construct($username, $ticket)
   {
      $this->username = $username;
      $this->ticket = $ticket;
   }

   public function getUserName()
   {
     return $this->username;
   }

   public function getTicket()
   {
     return $this->ticket;
   }
}

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
