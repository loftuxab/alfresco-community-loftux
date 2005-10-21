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

   require_once('SOAP/Client.php');

   function getServerLocation()
   {
      return get_cfg_var("alfresco.server");
   }

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
