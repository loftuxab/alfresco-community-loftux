<?php

require_once 'SOAP/Client.php';
require_once 'alfresco/webservice/WebServiceUtils.php';

class AccessControlWebService extends SOAP_Client
{
    function AccessControlWebService($path = null)
    {
        if ($path == null)
        {
            $path = getServerLocation().'/alfresco/api/AccessControlService';
        }
        $this->SOAP_Client($path, 0);
    }
    function &getACLs($predicate, $filter)
    {
        $getACLs =& new SOAP_Value('{http://www.alfresco.org/ws/service/accesscontrol/1.0}getACLs', false, $v = array('predicate' => $predicate, 'filter' => $filter));
        return $this->call('getACLs',
                           $v = array('getACLs' => $getACLs),
                           array('namespace' => 'http://www.alfresco.org/ws/service/accesscontrol/1.0',
                                 'soapaction' => 'http://www.alfresco.org/ws/service/accesscontrol/1.0/getACLs',
                                 'style' => 'document',
                                 'use' => 'literal'));
    }
    function &addACEs($predicate, $aces)
    {
        $addACEs =& new SOAP_Value('{http://www.alfresco.org/ws/service/accesscontrol/1.0}addACEs', false, $v = array('predicate' => $predicate, 'aces' => $aces));
        return $this->call('addACEs',
                           $v = array('addACEs' => $addACEs),
                           array('namespace' => 'http://www.alfresco.org/ws/service/accesscontrol/1.0',
                                 'soapaction' => 'http://www.alfresco.org/ws/service/accesscontrol/1.0/addACEs',
                                 'style' => 'document',
                                 'use' => 'literal'));
    }
    function &removeACEs($predicate, $aces)
    {
        $removeACEs =& new SOAP_Value('{http://www.alfresco.org/ws/service/accesscontrol/1.0}removeACEs', false, $v = array('predicate' => $predicate, 'aces' => $aces));
        return $this->call('removeACEs',
                           $v = array('removeACEs' => $removeACEs),
                           array('namespace' => 'http://www.alfresco.org/ws/service/accesscontrol/1.0',
                                 'soapaction' => 'http://www.alfresco.org/ws/service/accesscontrol/1.0/removeACEs',
                                 'style' => 'document',
                                 'use' => 'literal'));
    }
    function &getPermissions($predicate)
    {
        $getPermissions =& new SOAP_Value('{http://www.alfresco.org/ws/service/accesscontrol/1.0}getPermissions', false, $v = array('predicate' => $predicate));
        return $this->call('getPermissions',
                           $v = array('getPermissions' => $getPermissions),
                           array('namespace' => 'http://www.alfresco.org/ws/service/accesscontrol/1.0',
                                 'soapaction' => 'http://www.alfresco.org/ws/service/accesscontrol/1.0/getPermissions',
                                 'style' => 'document',
                                 'use' => 'literal'));
    }
    function &getClassPermissions($classNames)
    {
        $getClassPermissions =& new SOAP_Value('{http://www.alfresco.org/ws/service/accesscontrol/1.0}getClassPermissions', false, $v = array('classNames' => $classNames));
        return $this->call('getClassPermissions',
                           $v = array('getClassPermissions' => $getClassPermissions),
                           array('namespace' => 'http://www.alfresco.org/ws/service/accesscontrol/1.0',
                                 'soapaction' => 'http://www.alfresco.org/ws/service/accesscontrol/1.0/getClassPermissions',
                                 'style' => 'document',
                                 'use' => 'literal'));
    }
    function &hasPermissions($predicate, $permissions)
    {
        $hasPermissions =& new SOAP_Value('{http://www.alfresco.org/ws/service/accesscontrol/1.0}hasPermissions', false, $v = array('predicate' => $predicate, 'permissions' => $permissions));
        return $this->call('hasPermissions',
                           $v = array('hasPermissions' => $hasPermissions),
                           array('namespace' => 'http://www.alfresco.org/ws/service/accesscontrol/1.0',
                                 'soapaction' => 'http://www.alfresco.org/ws/service/accesscontrol/1.0/hasPermissions',
                                 'style' => 'document',
                                 'use' => 'literal'));
    }
    function &setInheritPermission($predicate, $inheritPermission)
    {
        $setInheritPermission =& new SOAP_Value('{http://www.alfresco.org/ws/service/accesscontrol/1.0}setInheritPermission', false, $v = array('predicate' => $predicate, 'inheritPermission' => $inheritPermission));
        return $this->call('setInheritPermission',
                           $v = array('setInheritPermission' => $setInheritPermission),
                           array('namespace' => 'http://www.alfresco.org/ws/service/accesscontrol/1.0',
                                 'soapaction' => 'http://www.alfresco.org/ws/service/accesscontrol/1.0/setInheritPermission',
                                 'style' => 'document',
                                 'use' => 'literal'));
    }
    function &getOwners($predicate)
    {
        $getOwners =& new SOAP_Value('{http://www.alfresco.org/ws/service/accesscontrol/1.0}getOwners', false, $v = array('predicate' => $predicate));
        return $this->call('getOwners',
                           $v = array('getOwners' => $getOwners),
                           array('namespace' => 'http://www.alfresco.org/ws/service/accesscontrol/1.0',
                                 'soapaction' => 'http://www.alfresco.org/ws/service/accesscontrol/1.0/getOwners',
                                 'style' => 'document',
                                 'use' => 'literal'));
    }
    function &setOwners($predicate, $owner)
    {
        $setOwners =& new SOAP_Value('{http://www.alfresco.org/ws/service/accesscontrol/1.0}setOwners', false, $v = array('predicate' => $predicate, 'owner' => $owner));
        return $this->call('setOwners',
                           $v = array('setOwners' => $setOwners),
                           array('namespace' => 'http://www.alfresco.org/ws/service/accesscontrol/1.0',
                                 'soapaction' => 'http://www.alfresco.org/ws/service/accesscontrol/1.0/setOwners',
                                 'style' => 'document',
                                 'use' => 'literal'));
    }
}

?>