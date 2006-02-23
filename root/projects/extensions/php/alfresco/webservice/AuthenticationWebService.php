<?php

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
        return $this->call('startSession',
                           $v = array('startSession' => $startSession),
                           array('namespace' => 'http://www.alfresco.org/ws/service/authentication/1.0',
                                 'soapaction' => 'http://www.alfresco.org/ws/service/authentication/1.0/startSession',
                                 'style' => 'document',
                                 'use' => 'literal'));
    }
    function &endSession($ticket)
    {
        $endSession =& new SOAP_Value('{http://www.alfresco.org/ws/service/authentication/1.0}endSession', false, $v = array('ticket' => $ticket));
        return $this->call('endSession',
                           $v = array('endSession' => $endSession),
                           array('namespace' => 'http://www.alfresco.org/ws/service/authentication/1.0',
                                 'soapaction' => 'http://www.alfresco.org/ws/service/authentication/1.0/endSession',
                                 'style' => 'document',
                                 'use' => 'literal'));
    }
}

?>