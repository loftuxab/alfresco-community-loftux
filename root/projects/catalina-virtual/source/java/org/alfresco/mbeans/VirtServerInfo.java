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
    String virtDomain_;
    int    virtJmxRmiPort_;
    int    virtHttpPort_;
    int    retryInterval_;  // in milliseconds

    // Remote Alfresco server info
    String alfrescoJmxRmiHost_;
    int    alfrescoJmxRmiPort_;

    // Admin user name on Alfresco server.
    String alfrescoServerUser_;
    
    // Admin user password on Alfresco server.
    String alfrescoServerPassword_;

    public VirtServerInfo() { }

    public String   getVirtServerDomain()                { return virtDomain_; }
    public void     setVirtServerDomain(String host)     { virtDomain_ = host; }

    public int      getVirtServerHttpPort()              { return virtHttpPort_; }
    public void     setVirtServerHttpPort(int port)      { virtHttpPort_ = port; }

    public int      getVirtServerJmxRmiPort()            { return virtJmxRmiPort_; }
    public void     setVirtServerJmxRmiPort(int port)    { virtJmxRmiPort_ = port; }

    public String   getAlfrescoJmxRmiHost()              { return alfrescoJmxRmiHost_; }
    public void     setAlfrescoJmxRmiHost(String host)   { alfrescoJmxRmiHost_ = host; }

    public int      getAlfrescoJmxRmiPort()              { return alfrescoJmxRmiPort_; }
    public void     setAlfrescoJmxRmiPort(int port)      { alfrescoJmxRmiPort_ = port; }
    
    public String   getAlfrescoServerUser()              { return alfrescoServerUser_; }
    public void     setAlfrescoServerUser(String user)   { alfrescoServerUser_ = user; }

    public String   getAlfrescoServerPassword()
                    { return alfrescoServerPassword_; }

    public void     setAlfrescoServerPassword(String password)
                    { alfrescoServerPassword_ = password; }

    public int      getVirtServerConnectionRetryInterval() 
                    { return retryInterval_; }

    public void     setVirtServerConnectionRetryInterval(int milliseconds)
                    { retryInterval_ = milliseconds; }
}
