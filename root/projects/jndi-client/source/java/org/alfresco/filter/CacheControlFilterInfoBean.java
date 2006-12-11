/*-----------------------------------------------------------------------------
*  Copyright 2006 Alfresco Inc.
*  
*  Licensed under the Mozilla Public License version 1.1
*  with a permitted attribution clause. You may obtain a
*  copy of the License at:
*  
*      http://www.alfresco.org/legal/license.txt
*  
*  Unless required by applicable law or agreed to in writing,
*  software distributed under the License is distributed on an
*  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
*  either express or implied. See the License for the specific
*  language governing permissions and limitations under the
*  License.
*  
*  
*  Author  Jon Cox  <jcox@alfresco.com>
*  File    CacheControlFilterInfoBean.java
*----------------------------------------------------------------------------*/
package org.alfresco.filter;

import java.util.*;

public class CacheControlFilterInfoBean
{
    // Spring creates a LinkedHashMap, so rules order is preserved
    private Map<String,String> cacheControlRules_; 

    public void setCacheControlRules( Map<String,String> cacheControlRules )
    {
        cacheControlRules_ = cacheControlRules;
    }

    public Set<Map.Entry<String,String>> getCacheControlRulesEntrySet() 
    { 
        return ( Set<Map.Entry<String,String>>) cacheControlRules_.entrySet();
    }
}


