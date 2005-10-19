<?php

require_once('alfresco/Common.php');
require_once('alfresco/webservice/WebServiceUtils.php');
require_once('alfresco/webservice/ContentWebService.php');

class ContentService extends BaseService
{
   public function __construct($auth_details)
   {
      parent::__construct($auth_details);
      $this->web_service = new ContentWebService();
   }
   
   public function read($reference)
   {
     // Make the web service call
      $this->addSecurityHeader();
      $result = $this->web_service->read(getReferenceSOAPValue($reference));
      $this->checkForError($result);
      
      // TODO marhsall the result into a helpful object
      
      return $result;
   }
}

?>
