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
*  File    JNDIConstants.java
*----------------------------------------------------------------------------*/

package org.alfresco.config;

/**
*  Constants to create proper JNDI names for the directories
*  that contain www content.
*  <p>
*  Ultimately, the constants in this fill will go away entirely.
*  This is a stop-gap until we have support multiple virtual AVMHost
*  instances, and a full Spring config (with associated sync to virt server).
*  
*/
public final class JNDIConstants 
{
    /**
    *  Directory used for virtualized web content.
    *  Typically, this directory is a transparent overlay 
    *  on a shared staging area.
    */
    public final static String  DIR_DEFAULT_WWW     = "www";

    /**
    *  Directory in which virtualized webapps reside (e.g.: "ROOT").
    */
    public final static String  DIR_DEFAULT_APPBASE  = "avm_webapps";
}
