<?php
   require_once('SOAP/Client.php');

   function addSecurityHeader($client, $user, $ticket)
   {
      $username =& new SOAP_Value('{http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd}Username', false, $user);
      $password =& new SOAP_Value('{http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd}Password', false,
                               $ticket, array('Type' => 'http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-username-token-profile-1.0#PasswordText'));
      $usernameToken =& new SOAP_Value('{http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd}UsernameToken', false,
                                  $v = array($username, $password));
      $securityHeader =& new SOAP_Header('{http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd}Security', null,
                                      $usernameToken, 1);
      // remove the actor attribute that gets added by the constructor otherwise Axis gets upset!
      unset($securityHeader->attributes['SOAP-ENV:actor']);
      $client->addHeader($securityHeader);
   }

   function getStoreSOAPValue($store)
   {
      $scheme =& new SOAP_Value('{http://www.alfresco.org/ws/model/content/1.0}scheme', '{http://www.alfresco.org/ws/model/content/1.0}StoreEnum', $store->scheme);
      $address =& new SOAP_Value('{http://www.alfresco.org/ws/model/content/1.0}address', false, $store->address);
      return new SOAP_Value('{http://www.alfresco.org/ws/service/repository/1.0}store', false, $v=array($scheme, $address));
   }

   function getQuerySOAPValue($query)
   {
      $language =& new SOAP_Value('{http://www.alfresco.org/ws/model/content/1.0}language', '{http://www.alfresco.org/ws/model/content/1.0}QueryLanguageEnum', $query->language);
      $statement =& new SOAP_Value('{http://www.alfresco.org/ws/model/content/1.0}statement', false, $query->statement);
      return new SOAP_Value('{http://www.alfresco.org/ws/service/repository/1.0}query', false, $v=array($language, $statement));
   }

   function getReferenceSOAPValue($reference)
   {
      $store_value = getStoreSOAPValue($reference->store);
      $uuid =& new SOAP_Value('{http://www.alfresco.org/ws/service/repository/1.0}uuid', false, $reference->uuid);
      $path =& new SOAP_Value('{http://www.alfresco.org/ws/service/repository/1.0}path', false, $reference->path);
      return new SOAP_Value('{http://www.alfresco.org/ws/service/repository/1.0}reference', false, $v=array($store_value, $uuid, $path));
   }

?>