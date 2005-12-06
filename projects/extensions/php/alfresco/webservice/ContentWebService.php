<?php

require_once 'SOAP/Client.php';
require_once 'alfresco/webservice/WebServiceUtils.php';

class ContentWebService extends SOAP_Client
{
    function ContentWebService($path = null)
    {
        if ($path == null)
        {
           $path = getServerLocation().'/alfresco/api/ContentService';
        }
        $this->SOAP_Client($path, 0);
    }
    function &read($items, $property)
    {
        $read =& new SOAP_Value('{http://www.alfresco.org/ws/service/content/1.0}read', false, $v = array('items' => $items, 'property' => $property));
        return $this->call('read',
                           $v = array('read' => $read),
                           array('namespace' => 'http://www.alfresco.org/ws/service/content/1.0',
                                 'soapaction' => 'http://www.alfresco.org/ws/service/content/1.0/read',
                                 'style' => 'document',
                                 'use' => 'literal'));
    }
    function &write($node, $property, $content, $format)
    {
        $write =& new SOAP_Value('{http://www.alfresco.org/ws/service/content/1.0}write', false, $v = array('node' => $node, 'property' => $property, 'content' => $content, 'format' => $format));
        return $this->call('write',
                           $v = array('write' => $write),
                           array('namespace' => 'http://www.alfresco.org/ws/service/content/1.0',
                                 'soapaction' => 'http://www.alfresco.org/ws/service/content/1.0/write',
                                 'style' => 'document',
                                 'use' => 'literal'));
    }
    function &clear($items, $property)
    {
        $clear =& new SOAP_Value('{http://www.alfresco.org/ws/service/content/1.0}clear', false, $v = array('items' => $items, 'property' => $property));
        return $this->call('clear',
                           $v = array('clear' => $clear),
                           array('namespace' => 'http://www.alfresco.org/ws/service/content/1.0',
                                 'soapaction' => 'http://www.alfresco.org/ws/service/content/1.0/clear',
                                 'style' => 'document',
                                 'use' => 'literal'));
    }
}

?>