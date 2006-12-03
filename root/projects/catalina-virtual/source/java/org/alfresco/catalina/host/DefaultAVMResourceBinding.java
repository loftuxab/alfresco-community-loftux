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
*  File    DefaultAVMResourceBinding.java
*----------------------------------------------------------------------------*/
package org.alfresco.catalina.host;
import java.util.Map;
import java.util.regex.Matcher;
import org.alfresco.jndi.AVMFileDirContext;
import org.alfresco.repo.domain.PropertyValue;
import org.alfresco.service.cmr.remote.AVMRemote;
import org.alfresco.service.namespace.QName;

public class      DefaultAVMResourceBinding 
       implements        AVMResourceBinding
{

    /**
    *  @exclude (hide from javadoc)
    */
    static protected AVMRemote AVMRemote_;

    /**
    *  Fetch the name of the virtual repository indicated by
    *  data obtained when the reverseProxyBinding 
    *  regex was matched within the AVMUrlValve.
    */
    public String getRepositoryName(Matcher match)
    {
        String host_info = match.group(1);    // www-(...)
        if (host_info == null) { host_info = ""; }

        String repo = getRepoNameFromHostInfo( host_info );

        // System.out.println("Mapped host: " + host_info + " to repo: " + repo);

        return repo;
    }

    /**
    *  Fetch the version of the resource indicated by
    *  data obtained when the reverseProxyBinding 
    *  regex was matched within the AVMUrlValve.
    *  <p>
    *  Note: "-1" corresponds to the HEAD version.
    */
    public String getVersion(Matcher match)
    {
        String version       = match.group(2);    //   v-(...)
        if (version == null) { version = "-1"; }  // Default:  -1 == HEAD
        return version;
    }


    /**
    *  @exclude (hide from javadoc)
    */
    static String getRepoNameFromHostInfo( String host_info ) 
    {
        // Lazy init is necessary here due to library loading order
        if ( AVMRemote_ == null ) { AVMRemote_ = AVMFileDirContext.getAVMRemote(); }

        // Fetch the repo corresponding to the   ".dns.<host_info>" key 
        //
        String repo_name;
        try
        {
            Map<String, Map<QName, PropertyValue>> repo_dns_entries = 
               AVMRemote_.queryStoresPropertyKey(
               QName.createQName(null,".dns." + host_info));
               repo_name = repo_dns_entries.keySet().iterator().next(); 
        }
        catch (Exception e) { repo_name = ""; }

        return repo_name;
    }
}
