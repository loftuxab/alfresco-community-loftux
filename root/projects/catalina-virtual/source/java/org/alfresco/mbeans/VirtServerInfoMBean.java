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

    /** 
    *  Get the number of milliseconds the virtualization server will
    *  wait before retrying a failed connection to the Alfresco server.
    *  This allows the virtualization server to recover from a temporary
    *  network outage, a restart of the Alfresco webapp, the vagaries
    *  of daemon startup order, etc.
    */
    public int      getVirtServerConnectionRetryInterval();

    /** 
    *  Set the number of milliseconds the virtualization server will
    *  wait before retrying a failed connection to the Alfresco server.
    *  This allows the virtualization server to recover from a temporary
    *  network outage, a restart of the Alfresco webapp, the vagaries
    *  of daemon startup order, etc.
    */
    public void     setVirtServerConnectionRetryInterval(int milliseconds);



    /** Get the CIFS version tree automount path on Windows-based platforms. */
    public String   getVirtServerCifsAvmVersionTreeWin();

    /** Set the CIFS version tree automount path on Windows-based platforms. */
    public void     setVirtServerCifsAvmVersionTreeWin(String mountPoint);

    /** True if attempt is made to automount CIFS on Windows. */
    public boolean  getVirtServerCifsAvmVersionTreeWinAutomount();

    /** Set whether attempt is made to automount CIFS on Windows. */
    public void     setVirtServerCifsAvmVersionTreeWinAutomount(boolean doAutomount);


    /** Get the CIFS version tree automount path on UNIX-style platforms. */
    public String   getVirtServerCifsAvmVersionTreeUnix();

    /** Set the CIFS version tree automount path on UNIX-style platforms. */
    public void     setVirtServerCifsAvmVersionTreeUnix(String mountPoint);

    /** True if attempt is made to automount CIFS on UNIX-style platforms.. */
    public boolean  getVirtServerCifsAvmVersionTreeUnixAutomount();

    /** Set whether attempt is made to automount CIFS on UNIX-style platforms.. */
    public void     setVirtServerCifsAvmVersionTreeUnixAutomount(boolean doAutomount);

    /** 
    *   Fetches the value of os.name on the Virtualization server.
    *   Note:  all Windows-style platforms start with the string "Windows".
    *   In case you're curious, here's a list of common values for "os.name":
    *
    *   <pre>
    *        AIX
    *        Digital Unix
    *        FreeBSD
    *        HP-UX
    *        Irix
    *        Linux
    *        Mac OS
    *        Mac OS X
    *        MPE/iX
    *        NetWare
    *        OpenVMS
    *        OS/2
    *        OS/390
    *        OSF1
    *        Solaris
    *        SunOS
    *        Windows 2000
    *        Windows 2003
    *        Windows 95
    *        Windows 98
    *        Windows CE
    *        Windows Me
    *        Windows NT
    *        Windows XP
    *   </pre>
    */
    public String   getVirtServerOsName();

    /** Sets the OS name of the machine hosting the virtualization server */
    public void     setVirtServerOsName(String osName);


    // Remote Alfresco server info
    public String   getAlfrescoJmxRmiHost(); 
    public void     setAlfrescoJmxRmiHost(String host);
    public int      getAlfrescoJmxRmiPort();
    public void     setAlfrescoJmxRmiPort(int port);

}
