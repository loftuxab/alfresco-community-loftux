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
*  File    VirtServerUtils.java
*----------------------------------------------------------------------------*/

package org.alfresco.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.alfresco.config.JNDIConstants;


public class VirtServerUtils
{
    // patterns for WEB-INF files that require virtualisation server reload

    private final static Pattern WEB_INF_PATH_PATTERN = 
        Pattern.compile(  ".*:/" + JNDIConstants.DIR_DEFAULT_WWW        + 
                          "/" + JNDIConstants.DIR_DEFAULT_APPBASE + "/" + 
                          ".*/WEB-INF/((classes/.*)|(lib/.*)|(web.xml))",
                          Pattern.CASE_INSENSITIVE
                        );


   /**
    * @param path    Path to match against
    * 
    * @return true if the path should require a virtualisation 
    *              server reload, false otherwise
    */
   public static boolean requiresUpdateNotification(String path)
   {
      if (path == null || path.length() == 0)
      {
         throw new IllegalArgumentException("Path value is mandatory.");
      }
      
      return WEB_INF_PATH_PATTERN.matcher(path).matches();
   }
}

