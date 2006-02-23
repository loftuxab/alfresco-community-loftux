<?php

require_once 'SOAP/Client.php';
require_once 'alfresco/webservice/WebServiceUtils.php';

class AdministrationWebService extends SOAP_Client
{
    function AdministrationWebService($path = null)
    {
        if ($path == null)
        {
            $path = getServerLocation().'/alfresco/api/AdministrationService';
        }
        $this->SOAP_Client($path, 0);
    }
    function &queryUsers($filter)
    {
        $queryUsers =& new SOAP_Value('{http://www.alfresco.org/ws/service/administration/1.0}queryUsers', false, $v = array('filter' => $filter));
        return $this->call('queryUsers',
                           $v = array('queryUsers' => $queryUsers),
                           array('namespace' => 'http://www.alfresco.org/ws/service/administration/1.0',
                                 'soapaction' => 'http://www.alfresco.org/ws/service/administration/1.0/queryUsers',
                                 'style' => 'document',
                                 'use' => 'literal'));
    }
    function &fetchMoreUsers($querySession)
    {
        $fetchMoreUsers =& new SOAP_Value('{http://www.alfresco.org/ws/service/administration/1.0}fetchMoreUsers', false, $v = array('querySession' => $querySession));
        return $this->call('fetchMoreUsers',
                           $v = array('fetchMoreUsers' => $fetchMoreUsers),
                           array('namespace' => 'http://www.alfresco.org/ws/service/administration/1.0',
                                 'soapaction' => 'http://www.alfresco.org/ws/service/administration/1.0/fetchMoreUsers',
                                 'style' => 'document',
                                 'use' => 'literal'));
    }
    function &getUser($userName)
    {
        $getUser =& new SOAP_Value('{http://www.alfresco.org/ws/service/administration/1.0}getUser', false, $v = array('userName' => $userName));
        return $this->call('getUser',
                           $v = array('getUser' => $getUser),
                           array('namespace' => 'http://www.alfresco.org/ws/service/administration/1.0',
                                 'soapaction' => 'http://www.alfresco.org/ws/service/administration/1.0/getUser',
                                 'style' => 'document',
                                 'use' => 'literal'));
    }
    function &createUsers($newUsers)
    {
        $createUsers =& new SOAP_Value('{http://www.alfresco.org/ws/service/administration/1.0}createUsers', false, $v = array('newUsers' => $newUsers));
        return $this->call('createUsers',
                           $v = array('createUsers' => $createUsers),
                           array('namespace' => 'http://www.alfresco.org/ws/service/administration/1.0',
                                 'soapaction' => 'http://www.alfresco.org/ws/service/administration/1.0/createUsers',
                                 'style' => 'document',
                                 'use' => 'literal'));
    }
    function &updateUsers($users)
    {
        $updateUsers =& new SOAP_Value('{http://www.alfresco.org/ws/service/administration/1.0}updateUsers', false, $v = array('users' => $users));
        return $this->call('updateUsers',
                           $v = array('updateUsers' => $updateUsers),
                           array('namespace' => 'http://www.alfresco.org/ws/service/administration/1.0',
                                 'soapaction' => 'http://www.alfresco.org/ws/service/administration/1.0/updateUsers',
                                 'style' => 'document',
                                 'use' => 'literal'));
    }
    function &changePassword($userName, $oldPassword, $newPassword)
    {
        $changePassword =& new SOAP_Value('{http://www.alfresco.org/ws/service/administration/1.0}changePassword', false, $v = array('userName' => $userName, 'oldPassword' => $oldPassword, 'newPassword' => $newPassword));
        return $this->call('changePassword',
                           $v = array('changePassword' => $changePassword),
                           array('namespace' => 'http://www.alfresco.org/ws/service/administration/1.0',
                                 'soapaction' => 'http://www.alfresco.org/ws/service/administration/1.0/changePassword',
                                 'style' => 'document',
                                 'use' => 'literal'));
    }
    function &deleteUsers($userNames)
    {
        $deleteUsers =& new SOAP_Value('{http://www.alfresco.org/ws/service/administration/1.0}deleteUsers', false, $v = array('userNames' => $userNames));
        return $this->call('deleteUsers',
                           $v = array('deleteUsers' => $deleteUsers),
                           array('namespace' => 'http://www.alfresco.org/ws/service/administration/1.0',
                                 'soapaction' => 'http://www.alfresco.org/ws/service/administration/1.0/deleteUsers',
                                 'style' => 'document',
                                 'use' => 'literal'));
    }
}

?>