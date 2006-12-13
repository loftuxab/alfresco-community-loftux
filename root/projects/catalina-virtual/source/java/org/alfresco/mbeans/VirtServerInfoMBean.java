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
*  File    VirtServerInfo.java
*----------------------------------------------------------------------------*/

package org.alfresco.mbeans;

/**
* Interface for helper bean containing information used to create 
* peer-to-peer registration of virtualization server with remote
* Alfresco server.
*/
public interface VirtServerInfoMBean
{
    // Local virtualization server info
    public String   getVirtServerDomain(); 
    public void     setVirtServerDomain(String host);
    public int      getVirtServerHttpPort();
    public void     setVirtServerHttpPort(int port);
    public int      getVirtServerJmxRmiPort();
    public void     setVirtServerJmxRmiPort(int port);


    // Remote Alfresco server info
    public String   getAlfrescoJmxRmiHost(); 
    public void     setAlfrescoJmxRmiHost(String host);
    public int      getAlfrescoJmxRmiPort();
    public void     setAlfrescoJmxRmiPort(int port);
}
