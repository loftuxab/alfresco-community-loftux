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
