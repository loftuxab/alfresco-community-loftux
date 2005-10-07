<?php

require_once('alfresco/Types.php');
require_once('alfresco/webservice/WebServiceUtils.php');
require_once('alfresco/webservice/ContentWebService.php');

class ContentService
{
   private $content_web_service;
   private $user;
   private $ticket;

   public function __construct($user, $ticket)
   {
      // TOD this information should be stored in the session somewhere
      $this->user = $user;
      $this->ticket = $ticket;

      $this->content_web_service = new ContentWebService();
   }
   
   public function read($reference)
   {
      addSecurityHeader($this->content_web_service, $this->user, $this->ticket);

      // TODO convert this into a more convenient object form
      return $this->content_web_service->read(getReferenceSOAPValue($reference));
   }
}

?>