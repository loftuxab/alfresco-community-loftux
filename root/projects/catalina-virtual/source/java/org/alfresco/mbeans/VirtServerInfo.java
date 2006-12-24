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

    String   virtCifsAvmVersionTreeWin_;
    boolean  virtCifsAvmVersionTreeWinAutomount_;
    String   virtCifsAvmVersionTreeUnix_;
    boolean  virtCifsAvmVersionTreeUnixAutomount_;

    String   virtOsName_;

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


    // local CIFS automount props

    public String   getVirtServerCifsAvmVersionTreeWin()  
                    { return virtCifsAvmVersionTreeWin_;}

    public void     setVirtServerCifsAvmVersionTreeWin(String mountPoint)
                    { virtCifsAvmVersionTreeWin_ = mountPoint;}

    public boolean  getVirtServerCifsAvmVersionTreeWinAutomount()
                    { return virtCifsAvmVersionTreeWinAutomount_; }

    public void     setVirtServerCifsAvmVersionTreeWinAutomount(boolean doAutomount)
                    { virtCifsAvmVersionTreeWinAutomount_ = doAutomount; }

    public String   getVirtServerCifsAvmVersionTreeUnix()
                    { return  virtCifsAvmVersionTreeUnix_; }

    public void     setVirtServerCifsAvmVersionTreeUnix(String mountPoint)
                    {  virtCifsAvmVersionTreeUnix_ = mountPoint; }

    public boolean  getVirtServerCifsAvmVersionTreeUnixAutomount()
                    { return virtCifsAvmVersionTreeUnixAutomount_; }

    public void     setVirtServerCifsAvmVersionTreeUnixAutomount(boolean doAutomount)
                    { virtCifsAvmVersionTreeUnixAutomount_ = doAutomount; }

   
    /**   Fetches the value of os.name on the Virtualization server. */
    public String   getVirtServerOsName()                { return  virtOsName_; }

    /**   Sets virtualization server's exposed value for OS name. 
    *     Note:  this method is only called from within the virtualzation server itself,
    *     not remote JMXRMI clients. 
    */
    public void     setVirtServerOsName(String osName)   { virtOsName_ = osName; }


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
