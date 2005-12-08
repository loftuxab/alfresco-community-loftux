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

class WebService_AuthoringService_AuthoringService extends SOAP_Client
{
    function WebService_AuthoringService_AuthoringService($path = null)
    {
        if ($path == null)
        {
            $path = getServerLocation().'/alfresco/api/AuthoringService';
        }
        $this->SOAP_Client($path, 0);
    }
    function &checkout($items, $destination)
    {
        $checkout =& new SOAP_Value('{http://www.alfresco.org/ws/service/authoring/1.0}checkout', false, $v = array('items' => $items, 'destination' => $destination));
        return $this->call('checkout',
                           $v = array('checkout' => $checkout),
                           array('namespace' => 'http://www.alfresco.org/ws/service/authoring/1.0',
                                 'soapaction' => 'http://www.alfresco.org/ws/service/authoring/1.0/checkout',
                                 'style' => 'document',
                                 'use' => 'literal'));
    }
    function &checkin($items, $comments, $keepCheckedOut)
    {
        $checkin =& new SOAP_Value('{http://www.alfresco.org/ws/service/authoring/1.0}checkin', false, $v = array('items' => $items, 'comments' => $comments, 'keepCheckedOut' => $keepCheckedOut));
        return $this->call('checkin',
                           $v = array('checkin' => $checkin),
                           array('namespace' => 'http://www.alfresco.org/ws/service/authoring/1.0',
                                 'soapaction' => 'http://www.alfresco.org/ws/service/authoring/1.0/checkin',
                                 'style' => 'document',
                                 'use' => 'literal'));
    }
    function &checkinExternal($items, $comments, $keepCheckedOut, $format, $content)
    {
        $checkinExternal =& new SOAP_Value('{http://www.alfresco.org/ws/service/authoring/1.0}checkinExternal', false, $v = array('items' => $items, 'comments' => $comments, 'keepCheckedOut' => $keepCheckedOut, 'format' => $format, 'content' => $content));
        return $this->call('checkinExternal',
                           $v = array('checkinExternal' => $checkinExternal),
                           array('namespace' => 'http://www.alfresco.org/ws/service/authoring/1.0',
                                 'soapaction' => 'http://www.alfresco.org/ws/service/authoring/1.0/checkinExternal',
                                 'style' => 'document',
                                 'use' => 'literal'));
    }
    function &cancelCheckout($items)
    {
        $cancelCheckout =& new SOAP_Value('{http://www.alfresco.org/ws/service/authoring/1.0}cancelCheckout', false, $v = array('items' => $items));
        return $this->call('cancelCheckout',
                           $v = array('cancelCheckout' => $cancelCheckout),
                           array('namespace' => 'http://www.alfresco.org/ws/service/authoring/1.0',
                                 'soapaction' => 'http://www.alfresco.org/ws/service/authoring/1.0/cancelCheckout',
                                 'style' => 'document',
                                 'use' => 'literal'));
    }
    function &lock($items, $lockChildren, $lockType)
    {
        $lock =& new SOAP_Value('{http://www.alfresco.org/ws/service/authoring/1.0}lock', false, $v = array('items' => $items, 'lockChildren' => $lockChildren, 'lockType' => $lockType));
        return $this->call('lock',
                           $v = array('lock' => $lock),
                           array('namespace' => 'http://www.alfresco.org/ws/service/authoring/1.0',
                                 'soapaction' => 'http://www.alfresco.org/ws/service/authoring/1.0/lock',
                                 'style' => 'document',
                                 'use' => 'literal'));
    }
    function &unlock($items, $unlockChildren)
    {
        $unlock =& new SOAP_Value('{http://www.alfresco.org/ws/service/authoring/1.0}unlock', false, $v = array('items' => $items, 'unlockChildren' => $unlockChildren));
        return $this->call('unlock',
                           $v = array('unlock' => $unlock),
                           array('namespace' => 'http://www.alfresco.org/ws/service/authoring/1.0',
                                 'soapaction' => 'http://www.alfresco.org/ws/service/authoring/1.0/unlock',
                                 'style' => 'document',
                                 'use' => 'literal'));
    }
    function &getLockStatus($items)
    {
        $getLockStatus =& new SOAP_Value('{http://www.alfresco.org/ws/service/authoring/1.0}getLockStatus', false, $v = array('items' => $items));
        return $this->call('getLockStatus',
                           $v = array('getLockStatus' => $getLockStatus),
                           array('namespace' => 'http://www.alfresco.org/ws/service/authoring/1.0',
                                 'soapaction' => 'http://www.alfresco.org/ws/service/authoring/1.0/getLockStatus',
                                 'style' => 'document',
                                 'use' => 'literal'));
    }
    function &createVersion($items, $comments, $versionChildren)
    {
        $createVersion =& new SOAP_Value('{http://www.alfresco.org/ws/service/authoring/1.0}createVersion', false, $v = array('items' => $items, 'comments' => $comments, 'versionChildren' => $versionChildren));
        return $this->call('createVersion',
                           $v = array('createVersion' => $createVersion),
                           array('namespace' => 'http://www.alfresco.org/ws/service/authoring/1.0',
                                 'soapaction' => 'http://www.alfresco.org/ws/service/authoring/1.0/createVersion',
                                 'style' => 'document',
                                 'use' => 'literal'));
    }
    function &getVersionHistory($node)
    {
        $getVersionHistory =& new SOAP_Value('{http://www.alfresco.org/ws/service/authoring/1.0}getVersionHistory', false, $v = array('node' => $node));
        return $this->call('getVersionHistory',
                           $v = array('getVersionHistory' => $getVersionHistory),
                           array('namespace' => 'http://www.alfresco.org/ws/service/authoring/1.0',
                                 'soapaction' => 'http://www.alfresco.org/ws/service/authoring/1.0/getVersionHistory',
                                 'style' => 'document',
                                 'use' => 'literal'));
    }
    function &revertVersion($node, $versionLabel)
    {
        $revertVersion =& new SOAP_Value('{http://www.alfresco.org/ws/service/authoring/1.0}revertVersion', false, $v = array('node' => $node, 'versionLabel' => $versionLabel));
        return $this->call('revertVersion',
                           $v = array('revertVersion' => $revertVersion),
                           array('namespace' => 'http://www.alfresco.org/ws/service/authoring/1.0',
                                 'soapaction' => 'http://www.alfresco.org/ws/service/authoring/1.0/revertVersion',
                                 'style' => 'document',
                                 'use' => 'literal'));
    }
    function &deleteAllVersions($node)
    {
        $deleteAllVersions =& new SOAP_Value('{http://www.alfresco.org/ws/service/authoring/1.0}deleteAllVersions', false, $v = array('node' => $node));
        return $this->call('deleteAllVersions',
                           $v = array('deleteAllVersions' => $deleteAllVersions),
                           array('namespace' => 'http://www.alfresco.org/ws/service/authoring/1.0',
                                 'soapaction' => 'http://www.alfresco.org/ws/service/authoring/1.0/deleteAllVersions',
                                 'style' => 'document',
                                 'use' => 'literal'));
    }
}

?>
