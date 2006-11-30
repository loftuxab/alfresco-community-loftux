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
*  File    AVMResourceBinding.java
*----------------------------------------------------------------------------*/


package org.alfresco.catalina.host;
import java.util.regex.Matcher;

/**
*   Inteface for classes that use data collected from the 
*   AVMHost's reverseProxyBinding to calculate parameters
*   for resource lookup within AVMService (e.g.: the version
*   number and virtual repository name).
*/
public interface AVMResourceBinding
{
    /**
    *  Fetch the name of the virtual repository indicated by
    *  data obtained when the reverseProxyBinding 
    *  regex was matched within the AVMUrlValve.
    */
    public String getRepositoryName(Matcher match);

    /**
    *  Fetch the version of the resource indicated by
    *  data obtained when the reverseProxyBinding 
    *  regex was matched within the AVMUrlValve.
    *  <p>
    *  Note: "-1" corresponds to the HEAD version.
    */
    public String getVersion(Matcher match);
}
