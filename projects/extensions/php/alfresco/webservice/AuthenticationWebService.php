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

require_once 'SOAP/Client.php';
require_once 'alfresco/webservice/WebServiceUtils.php';

class AuthenticationWebService extends SOAP_Client
{
    function AuthenticationWebService($path = null)
    {
        if ($path == null)
        {
           $path = getServerLocation().'/alfresco/api/AuthenticationService';
        }
        $this->SOAP_Client($path, 0);
    }
    function &startSession($username, $password)
    {
        $startSession =& new SOAP_Value('{http://www.alfresco.org/ws/service/authentication/1.0}startSession', false, $v = array('username' => $username, 'password' => $password));
        
        // startSession
        return $this->call('startSession',
                           $v = array('startSession' => $startSession),
                           array('namespace' => 'http://www.alfresco.org/ws/service/authentication/1.0',
                                 'soapaction' => 'http://www.alfresco.org/ws/service/authentication/1.0/startSession',
                                 'style' => 'document',
                                 'use' => 'literal'));
    }
    
    function &endSession()
    {
        return $this->call('endSession',
                           $v = null,
                           array('namespace' => 'http://www.alfresco.org/ws/service/authentication/1.0',
                                 'soapaction' => 'http://www.alfresco.org/ws/service/authentication/1.0/endSession',
                                 'style' => 'document',
                                 'use' => 'literal'));
    }
    
      
}

?>
