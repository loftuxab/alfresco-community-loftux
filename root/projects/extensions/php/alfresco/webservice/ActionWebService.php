<?php

require_once 'SOAP/Client.php';
require_once 'alfresco/webservice/WebServiceUtils.php';

class ActionWebService extends SOAP_Client
{
    function ActionWebService($path = null)
    {
        if ($path == null)
        {
            $path = getServerLocation().'/alfresco/api/ActionService';
        }
        $this->SOAP_Client($path, 0);
    }
    function &getConditionDefinitions()
    {
        return $this->call('getConditionDefinitions',
                           $v = null,
                           array('namespace' => 'http://www.alfresco.org/ws/service/action/1.0',
                                 'soapaction' => 'http://www.alfresco.org/ws/service/action/1.0/getConditionDefinitions',
                                 'style' => 'document',
                                 'use' => 'literal'));
    }
    function &getActionDefinitions()
    {
        return $this->call('getActionDefinitions',
                           $v = null,
                           array('namespace' => 'http://www.alfresco.org/ws/service/action/1.0',
                                 'soapaction' => 'http://www.alfresco.org/ws/service/action/1.0/getActionDefinitions',
                                 'style' => 'document',
                                 'use' => 'literal'));
    }
    function &getActionItemDefinition($name, $definitionType)
    {
        $getActionItemDefinition =& new SOAP_Value('{http://www.alfresco.org/ws/service/action/1.0}getActionItemDefinition', false, $v = array('name' => $name, 'definitionType' => $definitionType));
        return $this->call('getActionItemDefinition',
                           $v = array('getActionItemDefinition' => $getActionItemDefinition),
                           array('namespace' => 'http://www.alfresco.org/ws/service/action/1.0',
                                 'soapaction' => 'http://www.alfresco.org/ws/service/action/1.0/getActionItemDefinition',
                                 'style' => 'document',
                                 'use' => 'literal'));
    }
    function &getRuleTypes()
    {
        return $this->call('getRuleTypes',
                           $v = null,
                           array('namespace' => 'http://www.alfresco.org/ws/service/action/1.0',
                                 'soapaction' => 'http://www.alfresco.org/ws/service/action/1.0/getRuleTypes',
                                 'style' => 'document',
                                 'use' => 'literal'));
    }
    function &getRuleType($name)
    {
        $getRuleType =& new SOAP_Value('{http://www.alfresco.org/ws/service/action/1.0}getRuleType', false, $v = array('name' => $name));
        return $this->call('getRuleType',
                           $v = array('getRuleType' => $getRuleType),
                           array('namespace' => 'http://www.alfresco.org/ws/service/action/1.0',
                                 'soapaction' => 'http://www.alfresco.org/ws/service/action/1.0/getRuleType',
                                 'style' => 'document',
                                 'use' => 'literal'));
    }
    function &getActions($reference, $filter)
    {
        $getActions =& new SOAP_Value('{http://www.alfresco.org/ws/service/action/1.0}getActions', false, $v = array('reference' => $reference, 'filter' => $filter));
        return $this->call('getActions',
                           $v = array('getActions' => $getActions),
                           array('namespace' => 'http://www.alfresco.org/ws/service/action/1.0',
                                 'soapaction' => 'http://www.alfresco.org/ws/service/action/1.0/getActions',
                                 'style' => 'document',
                                 'use' => 'literal'));
    }
    function &saveActions($reference, $actions)
    {
        $saveActions =& new SOAP_Value('{http://www.alfresco.org/ws/service/action/1.0}saveActions', false, $v = array('reference' => $reference, 'actions' => $actions));
        return $this->call('saveActions',
                           $v = array('saveActions' => $saveActions),
                           array('namespace' => 'http://www.alfresco.org/ws/service/action/1.0',
                                 'soapaction' => 'http://www.alfresco.org/ws/service/action/1.0/saveActions',
                                 'style' => 'document',
                                 'use' => 'literal'));
    }
    function &removeActions($reference, $actions)
    {
        $removeActions =& new SOAP_Value('{http://www.alfresco.org/ws/service/action/1.0}removeActions', false, $v = array('reference' => $reference, 'actions' => $actions));
        return $this->call('removeActions',
                           $v = array('removeActions' => $removeActions),
                           array('namespace' => 'http://www.alfresco.org/ws/service/action/1.0',
                                 'soapaction' => 'http://www.alfresco.org/ws/service/action/1.0/removeActions',
                                 'style' => 'document',
                                 'use' => 'literal'));
    }
    function &executeActions($predicate, $actions)
    {
        $executeActions =& new SOAP_Value('{http://www.alfresco.org/ws/service/action/1.0}executeActions', false, $v = array('predicate' => $predicate, 'actions' => $actions));
        return $this->call('executeActions',
                           $v = array('executeActions' => $executeActions),
                           array('namespace' => 'http://www.alfresco.org/ws/service/action/1.0',
                                 'soapaction' => 'http://www.alfresco.org/ws/service/action/1.0/executeActions',
                                 'style' => 'document',
                                 'use' => 'literal'));
    }
    function &getRules($reference, $ruleFilter)
    {
        $getRules =& new SOAP_Value('{http://www.alfresco.org/ws/service/action/1.0}getRules', false, $v = array('reference' => $reference, 'ruleFilter' => $ruleFilter));
        return $this->call('getRules',
                           $v = array('getRules' => $getRules),
                           array('namespace' => 'http://www.alfresco.org/ws/service/action/1.0',
                                 'soapaction' => 'http://www.alfresco.org/ws/service/action/1.0/getRules',
                                 'style' => 'document',
                                 'use' => 'literal'));
    }
    function &saveRules($reference, $rules)
    {
        $saveRules =& new SOAP_Value('{http://www.alfresco.org/ws/service/action/1.0}saveRules', false, $v = array('reference' => $reference, 'rules' => $rules));
        return $this->call('saveRules',
                           $v = array('saveRules' => $saveRules),
                           array('namespace' => 'http://www.alfresco.org/ws/service/action/1.0',
                                 'soapaction' => 'http://www.alfresco.org/ws/service/action/1.0/saveRules',
                                 'style' => 'document',
                                 'use' => 'literal'));
    }
    function &removeRules($reference, $rules)
    {
        $removeRules =& new SOAP_Value('{http://www.alfresco.org/ws/service/action/1.0}removeRules', false, $v = array('reference' => $reference, 'rules' => $rules));
        return $this->call('removeRules',
                           $v = array('removeRules' => $removeRules),
                           array('namespace' => 'http://www.alfresco.org/ws/service/action/1.0',
                                 'soapaction' => 'http://www.alfresco.org/ws/service/action/1.0/removeRules',
                                 'style' => 'document',
                                 'use' => 'literal'));
    }
}

?>