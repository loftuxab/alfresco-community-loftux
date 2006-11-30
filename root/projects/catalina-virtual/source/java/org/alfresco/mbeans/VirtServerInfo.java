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

public class VirtServerInfo implements VirtServerInfoMBean
{
    // Local virtualization server info
    String virtJmxRmiHost_;
    int    virtJmxRmiPort_;

    // Remote Alfresco server info
    String alfrescoJmxRmiHost_;
    int    alfrescoJmxRmiPort_;


    public VirtServerInfo() { }

    public String   getVirtServerJmxRmiHost()            { return virtJmxRmiHost_; }
    public void     setVirtServerJmxRmiHost(String host) { virtJmxRmiHost_ = host; }

    public int      getVirtServerJmxRmiPort()            { return virtJmxRmiPort_; }
    public void     setVirtServerJmxRmiPort(int port)    { virtJmxRmiPort_ = port; }

    public String   getAlfrescoJmxRmiHost()              { return alfrescoJmxRmiHost_; }
    public void     setAlfrescoJmxRmiHost(String host)   { alfrescoJmxRmiHost_ = host; }

    public int      getAlfrescoJmxRmiPort()              { return alfrescoJmxRmiPort_; }
    public void     setAlfrescoJmxRmiPort(int port)      { alfrescoJmxRmiPort_ = port; }
}
