<?php

require_once 'SOAP/Client.php';

class RepositoryWebService extends SOAP_Client
{
    function RepositoryWebService($path = null)
    {
        if ($path == null)
        {
            $path = getServerLocation().'/alfresco/api/RepositoryService';
        }
        $this->SOAP_Client($path, 0);
    }
    function &getStores()
    {
        return $this->call('getStores',
                           $v = null,
                           array('namespace' => 'http://www.alfresco.org/ws/service/repository/1.0',
                                 'soapaction' => 'http://www.alfresco.org/ws/service/repository/1.0/getStores',
                                 'style' => 'document',
                                 'use' => 'literal'));
    }
    function &query($store, $query, $includeMetaData)
    {
        $query =& new SOAP_Value('{http://www.alfresco.org/ws/service/repository/1.0}query', false, $v = array('store' => $store, 'query' => $query, 'includeMetaData' => $includeMetaData));
        return $this->call('query',
                           $v = array('query' => $query),
                           array('namespace' => 'http://www.alfresco.org/ws/service/repository/1.0',
                                 'soapaction' => 'http://www.alfresco.org/ws/service/repository/1.0/query',
                                 'style' => 'document',
                                 'use' => 'literal'));
    }
    function &queryChildren($node)
    {
        $queryChildren =& new SOAP_Value('{http://www.alfresco.org/ws/service/repository/1.0}queryChildren', false, $v = array('node' => $node));
        return $this->call('queryChildren',
                           $v = array('queryChildren' => $queryChildren),
                           array('namespace' => 'http://www.alfresco.org/ws/service/repository/1.0',
                                 'soapaction' => 'http://www.alfresco.org/ws/service/repository/1.0/queryChildren',
                                 'style' => 'document',
                                 'use' => 'literal'));
    }
    function &queryParents($node)
    {
        $queryParents =& new SOAP_Value('{http://www.alfresco.org/ws/service/repository/1.0}queryParents', false, $v = array('node' => $node));
        return $this->call('queryParents',
                           $v = array('queryParents' => $queryParents),
                           array('namespace' => 'http://www.alfresco.org/ws/service/repository/1.0',
                                 'soapaction' => 'http://www.alfresco.org/ws/service/repository/1.0/queryParents',
                                 'style' => 'document',
                                 'use' => 'literal'));
    }
    function &queryAssociated($node, $association)
    {
        $queryAssociated =& new SOAP_Value('{http://www.alfresco.org/ws/service/repository/1.0}queryAssociated', false, $v = array('node' => $node, 'association' => $association));
        return $this->call('queryAssociated',
                           $v = array('queryAssociated' => $queryAssociated),
                           array('namespace' => 'http://www.alfresco.org/ws/service/repository/1.0',
                                 'soapaction' => 'http://www.alfresco.org/ws/service/repository/1.0/queryAssociated',
                                 'style' => 'document',
                                 'use' => 'literal'));
    }
    function &fetchMore($querySession)
    {
        $fetchMore =& new SOAP_Value('{http://www.alfresco.org/ws/service/repository/1.0}fetchMore', false, $v = array('querySession' => $querySession));
        return $this->call('fetchMore',
                           $v = array('fetchMore' => $fetchMore),
                           array('namespace' => 'http://www.alfresco.org/ws/service/repository/1.0',
                                 'soapaction' => 'http://www.alfresco.org/ws/service/repository/1.0/fetchMore',
                                 'style' => 'document',
                                 'use' => 'literal'));
    }
    function &update($statements)
    {
        $update =& new SOAP_Value('{http://www.alfresco.org/ws/service/repository/1.0}update', false, $v = array('statements' => $statements));
        return $this->call('update',
                           $v = array('update' => $update),
                           array('namespace' => 'http://www.alfresco.org/ws/service/repository/1.0',
                                 'soapaction' => 'http://www.alfresco.org/ws/service/repository/1.0/update',
                                 'style' => 'document',
                                 'use' => 'literal'));
    }
    function &describe($items)
    {
        $describe =& new SOAP_Value('{http://www.alfresco.org/ws/service/repository/1.0}describe', false, $v = array('items' => $items));
        return $this->call('describe',
                           $v = array('describe' => $describe),
                           array('namespace' => 'http://www.alfresco.org/ws/service/repository/1.0',
                                 'soapaction' => 'http://www.alfresco.org/ws/service/repository/1.0/describe',
                                 'style' => 'document',
                                 'use' => 'literal'));
    }
    function &get($where)
    {
        $get =& new SOAP_Value('{http://www.alfresco.org/ws/service/repository/1.0}get', false, $v = array('where' => $where));
        return $this->call('get',
                           $v = array('get' => $get),
                           array('namespace' => 'http://www.alfresco.org/ws/service/repository/1.0',
                                 'soapaction' => 'http://www.alfresco.org/ws/service/repository/1.0/get',
                                 'style' => 'document',
                                 'use' => 'literal'));
    }
}

?>
