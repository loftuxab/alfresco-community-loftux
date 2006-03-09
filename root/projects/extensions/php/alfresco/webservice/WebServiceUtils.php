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
      $createdDate = date("Y-m-d\TH:i:s\Z", mktime(date("H")+24, date("i"), date("s"), date("m"), date("d"), date("Y")));
      $expiresDate = date("Y-m-d\TH:i:s\Z", mktime(date("H")+25, date("i"), date("s"), date("m"), date("d"), date("Y")));

      $created =& new SOAP_Value('{http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd}Created', false, $createdDate);;
      $expires =& new SOAP_Value('{http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd}Expires', false, $expiresDate);;
      $timestamp =& new SOAP_Value('{http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd}Timestamp', false, array($created, $expires));

      $username =& new SOAP_Value('{http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd}Username', false, $user);
      $password =& new SOAP_Value('{http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd}Password', false,
                               $ticket, array('Type' => 'http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-username-token-profile-1.0#PasswordText'));
      $usernameToken =& new SOAP_Value('{http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd}UsernameToken', false,
                                  $v = array($username, $password));

      $securityHeader =& new SOAP_Header('{http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd}Security', null,
                                      array($timestamp, $usernameToken), 1);
      // remove the actor attribute that gets added by the constructor otherwise Axis gets upset!
      unset($securityHeader->attributes['SOAP-ENV:actor']);
      $client->addHeader($securityHeader);
   }

   /**
    * Create a SOAP value for a Store object
    */
   function getStoreSOAPValue($store)
   {
      // If the store is null make sure the scheme and address are also null
      $scheme = null;
      $address = null;
      if ($store != null)
      {
         $scheme = $store->scheme;
         $address = $store->address;
      }

      // Create the store soap value
      $params = array(
              new SOAP_Value('scheme', 'StoreEnum', $scheme),
              new SOAP_Value('address', false, $address)
      );
      return new SOAP_Value('store', false, $params);

   }

   function getQuerySOAPValue($statement, $language)
   {
      $params = array(
              new SOAP_Value('language', 'QueryLanguageEnum', $language),
              new SOAP_Value('statement', false, $statement)
      );
      return new SOAP_Value('query', false, $params);
   }

   function getReferenceSOAPValue($reference, $name='reference')
   {
      $params = array(
              getStoreSOAPValue($reference->store),
              new SOAP_Value('uuid', false, $reference->uuid),
              new SOAP_Value('path', false, $reference->path)
      );
      
      return new SOAP_Value($name, false, $params);
   }
   
   function getPredicateSOAPValue($references, $store, $query_statement, $query_language)
   {
       $params = array();
       if ($references != null)
       {
          foreach ($references as $reference)
          {
             $params[] = getReferenceSOAPValue($reference, 'nodes');
          }
       }

       $params[] = getStoreSOAPValue($store);
       $params[] = getQuerySOAPValue($query_statement, $query_language);

       return new SOAP_Value('items', false, $params);
   }

?>
