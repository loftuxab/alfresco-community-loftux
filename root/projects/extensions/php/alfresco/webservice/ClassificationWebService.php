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

require_once 'SOAP/Client.php';
require_once 'alfresco/webservice/WebServiceUtils.php';

class ClassificationWebService extends SOAP_Client
{
    function WebService_ClassificationService_ClassificationService($path = null)
    {
        if ($path == null)
        {
            $path = getServerLocation().'/alfresco/api/ClassificationService';
        }
        $this->SOAP_Client($path, 0);
    }
    function &getClassifications($store)
    {
        $getClassifications =& new SOAP_Value('{http://www.alfresco.org/ws/service/classification/1.0}getClassifications', false, $v = array('store' => $store));
        return $this->call('getClassifications',
                           $v = array('getClassifications' => $getClassifications),
                           array('namespace' => 'http://www.alfresco.org/ws/service/classification/1.0',
                                 'soapaction' => 'http://www.alfresco.org/ws/service/classification/1.0/getClassifications',
                                 'style' => 'document',
                                 'use' => 'literal'));
    }
    function &getChildCategories($parentCategory)
    {
        $getChildCategories =& new SOAP_Value('{http://www.alfresco.org/ws/service/classification/1.0}getChildCategories', false, $v = array('parentCategory' => $parentCategory));
        return $this->call('getChildCategories',
                           $v = array('getChildCategories' => $getChildCategories),
                           array('namespace' => 'http://www.alfresco.org/ws/service/classification/1.0',
                                 'soapaction' => 'http://www.alfresco.org/ws/service/classification/1.0/getChildCategories',
                                 'style' => 'document',
                                 'use' => 'literal'));
    }
    function &getCategories($items)
    {
        $getCategories =& new SOAP_Value('{http://www.alfresco.org/ws/service/classification/1.0}getCategories', false, $v = array('items' => $items));
        return $this->call('getCategories',
                           $v = array('getCategories' => $getCategories),
                           array('namespace' => 'http://www.alfresco.org/ws/service/classification/1.0',
                                 'soapaction' => 'http://www.alfresco.org/ws/service/classification/1.0/getCategories',
                                 'style' => 'document',
                                 'use' => 'literal'));
    }
    function &setCategories($items, $categories)
    {
        $setCategories =& new SOAP_Value('{http://www.alfresco.org/ws/service/classification/1.0}setCategories', false, $v = array('items' => $items, 'categories' => $categories));
        return $this->call('setCategories',
                           $v = array('setCategories' => $setCategories),
                           array('namespace' => 'http://www.alfresco.org/ws/service/classification/1.0',
                                 'soapaction' => 'http://www.alfresco.org/ws/service/classification/1.0/setCategories',
                                 'style' => 'document',
                                 'use' => 'literal'));
    }
    function &describeClassification($classification)
    {
        $describeClassification =& new SOAP_Value('{http://www.alfresco.org/ws/service/classification/1.0}describeClassification', false, $v = array('classification' => $classification));
        return $this->call('describeClassification',
                           $v = array('describeClassification' => $describeClassification),
                           array('namespace' => 'http://www.alfresco.org/ws/service/classification/1.0',
                                 'soapaction' => 'http://www.alfresco.org/ws/service/classification/1.0/describeClassification',
                                 'style' => 'document',
                                 'use' => 'literal'));
    }
}

?>
