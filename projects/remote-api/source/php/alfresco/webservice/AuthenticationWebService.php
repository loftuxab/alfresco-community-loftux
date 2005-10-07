<?php

require_once 'SOAP/Client.php';

class AuthenticationWebService extends SOAP_Client
{
    function AuthenticationWebService($path = 'http://localhost:8080/alfresco/api/AuthenticationService')
    {
        $this->SOAP_Client($path, 0);
    }
    function &startSession($username, $password)
    {
        $startSession =& new SOAP_Value('{http://www.alfresco.org/ws/service/authentication/1.0}startSession', false, $v = array('username' => $username, 'password' => $password));
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