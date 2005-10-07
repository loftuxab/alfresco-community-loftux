<?php

require_once 'SOAP/Client.php';

class ContentWebService extends SOAP_Client
{
    function ContentWebService($path = 'http://localhost:8080/alfresco/api/ContentService')
    {
        $this->SOAP_Client($path, 0);
    }
    function &describe($items)
    {
        $describe =& new SOAP_Value('{http://www.alfresco.org/ws/service/content/1.0}describe', false, $v = array('items' => $items));
        return $this->call('describe',
                           $v = array('describe' => $describe),
                           array('namespace' => 'http://www.alfresco.org/ws/service/content/1.0',
                                 'soapaction' => 'http://www.alfresco.org/ws/service/content/1.0/describe',
                                 'style' => 'document',
                                 'use' => 'literal'));
    }
    function &read($node)
    {
        $read =& new SOAP_Value('{http://www.alfresco.org/ws/service/content/1.0}read', false, $v = array('node' => $node));
        return $this->call('read',
                           $v = array('read' => $read),
                           array('namespace' => 'http://www.alfresco.org/ws/service/content/1.0',
                                 'soapaction' => 'http://www.alfresco.org/ws/service/content/1.0/read',
                                 'style' => 'document',
                                 'use' => 'literal'));
    }
    function &write($node, $content)
    {
        $write =& new SOAP_Value('{http://www.alfresco.org/ws/service/content/1.0}write', false, $v = array('node' => $node, 'content' => $content));
        return $this->call('write',
                           $v = array('write' => $write),
                           array('namespace' => 'http://www.alfresco.org/ws/service/content/1.0',
                                 'soapaction' => 'http://www.alfresco.org/ws/service/content/1.0/write',
                                 'style' => 'document',
                                 'use' => 'literal'));
    }
    function &create($parent, $name, $format, $content)
    {
        $create =& new SOAP_Value('{http://www.alfresco.org/ws/service/content/1.0}create', false, $v = array('parent' => $parent, 'name' => $name, 'format' => $format, 'content' => $content));
        return $this->call('create',
                           $v = array('create' => $create),
                           array('namespace' => 'http://www.alfresco.org/ws/service/content/1.0',
                                 'soapaction' => 'http://www.alfresco.org/ws/service/content/1.0/create',
                                 'style' => 'document',
                                 'use' => 'literal'));
    }
    function &delete($items)
    {
        $delete =& new SOAP_Value('{http://www.alfresco.org/ws/service/content/1.0}delete', false, $v = array('items' => $items));
        return $this->call('delete',
                           $v = array('delete' => $delete),
                           array('namespace' => 'http://www.alfresco.org/ws/service/content/1.0',
                                 'soapaction' => 'http://www.alfresco.org/ws/service/content/1.0/delete',
                                 'style' => 'document',
                                 'use' => 'literal'));
    }
    function &exists($items)
    {
        $exists =& new SOAP_Value('{http://www.alfresco.org/ws/service/content/1.0}exists', false, $v = array('items' => $items));
        return $this->call('exists',
                           $v = array('exists' => $exists),
                           array('namespace' => 'http://www.alfresco.org/ws/service/content/1.0',
                                 'soapaction' => 'http://www.alfresco.org/ws/service/content/1.0/exists',
                                 'style' => 'document',
                                 'use' => 'literal'));
    }
}

?>