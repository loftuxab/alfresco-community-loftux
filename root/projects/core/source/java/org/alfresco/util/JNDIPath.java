/*-----------------------------------------------------------------------------
*  Copyright 2007 Alfresco Inc.
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
*  File    JNDIPath.java
*----------------------------------------------------------------------------*/

package org.alfresco.util;

/**
*  Given a JNDI directory mount point and absolute AVM+CIFS-style JNDI path,
*  this class is able to return the associated AVM absolute path and version.
*  It is assumed that mount_point is the leading substring of jndi_path.
*  <p>
*  The easiest way to get the value of mount_point is to call the static 
*  method:  org.alfresco.jndi.AVMFileDirContext.getAVMFileDirMountPoint()
*
*   <pre>
*
*   EXAMPLE:
*
*     On UNIX, if the constructor args are:
*        mount_point == /media/alfresco/cifs/v 
*        jndi_path   == /media/alfresco/cifs/v/mysite/VERSION/v-1/DATA/www/avm_webapps/ROOT
*
*     Or in Windows, if the constructor args are:
*        mount_point == v:
*        jndi_path   == v:/mysite/VERSION/v-1/DATA/www/avm_webapps/ROOT
*
*     Then:
*        getAvmPath()    ==  mysite:/www/avm_webapps/ROOT
*        getAvmVersion() ==  -1
*
*   </pre>
*  TODO: This functionality should probably be merged with 
*        org.alfresco.filesys.avm.AVMPath sometime soon.
*/
import java.io.File;

public class JNDIPath
{
    String mount_point_;
    String jndi_path_;
    String avm_path_;
    int    avm_version_;

    public JNDIPath(String mount_point, String jndi_path) throws Exception
    {
        mount_point_  = mount_point;
        jndi_path_    = jndi_path;

        int repo_head = mount_point_.length();
        if (mount_point_.charAt( repo_head -1 ) != File.separatorChar)
        {
            repo_head += 1;
        }

        int     repo_tail = jndi_path_.indexOf( File.separatorChar, repo_head);
        String  repo_name = jndi_path_.substring(repo_head,repo_tail);
        int     vers_head = repo_tail + "/VERSION/v".length();

        if  ( vers_head < 0)  
        { 
            throw new IllegalArgumentException("Bad version in JNDI path: " + jndi_path_ ); 
        }

        int vers_tail =  jndi_path_.indexOf( File.separatorChar , vers_head );

        if  ( vers_tail < 0)  
        { 
            throw new IllegalArgumentException("Bad version delimeter in JNDI path: " + jndi_path_ ); 
        }

        try 
        {
            String vers_string =  jndi_path_.substring( vers_head, vers_tail);
            avm_version_       = Integer.parseInt( vers_string);
        }
        catch (Exception e ) 
        { 
            // If malformed, assume -1  (HEAD)
            // TODO:  issue a warning here?
            //
            avm_version_ = -1; 
        }

        String repo_relpath = jndi_path_.substring( vers_tail + "/DATA".length(),
                                                    jndi_path_.length()
                                                   );


        avm_path_  = repo_name + ":" + repo_relpath;


        // Within the AVM, the file seperator char is '/'.
        // Therefore, on Windows, make sure that the avm_path_
        // is normalized to use '/'
        //
        if ( File.separatorChar != '/' )
        {
            avm_path_ = avm_path_.replace(  File.separatorChar , '/');
        }
    }

    public String getFileDirMountPoint() { return mount_point_; }
    public String getJndiPath()          { return jndi_path_; }
    public String getAvmPath()           { return avm_path_; }
    public int    getAvmVersion()        { return avm_version_; }
}
