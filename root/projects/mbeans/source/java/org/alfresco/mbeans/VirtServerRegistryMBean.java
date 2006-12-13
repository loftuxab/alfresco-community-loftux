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
*  File    VirtServerRegistryMBean.java
*----------------------------------------------------------------------------*/

package org.alfresco.mbeans;

public interface VirtServerRegistryMBean
{
    public void initialize();

    // public void   setVirtServerJmxUrl(String virtServerJmxUrl);
    public String    getVirtServerJmxUrl();

    public Integer getVirtServerHttpPort();
    public String  getVirtServerFQDN();

    public void registerVirtServerInfo( String  virtServerJmxUrl,
                                        String  virtServerFQDN,
                                        Integer virtServerHttpPort
                                      );  


    /**  Sets password file used to access virt server */
    public void   setPasswordFile(String path);

    /**  Gets password file used to access virt server */
    public String getPasswordFile();
    
    /**  Sets access "role" file used by virt server */
    public void   setAccessFile(String path);

    /**  Gets  access "role" file used by virt server */
    public String getAccessFile();


    /** 
    *  Tells the virtualization server to load (or reload) 
    *  the specified webapp.  Typically, version == -1  
    *  (i.e.: HEAD), and  the pathToWebapp looks something like:
    *     some-repo-name:/appBase/avm_webapps/SomeWebappName
    *
    *  Note: if this virtual webapp has other virtual webapps 
    *  that depend on it, then the dependees are reloaded too.
    *  For example, if you reload a virtual webapp that 
    *  corresponds to the "staging" area,  every author sandbox
    *  that overlays it is a dependee (due to the classloader
    *  hierarchy).   Thus, it's good to only load/reload webapps
    *  when you absolutely must (such as when the classes or jars
    *  in WEB-INF change, or when web.xml changes).
    */
    public boolean webappUpdated(int version, String pathToWebapp);

    /**
    *  Tell shte 
    */
    public boolean webappRemoved(int version, String pathToWebapp);
}
