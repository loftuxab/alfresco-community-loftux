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
*  File    VirtWebappRegistry.java
*----------------------------------------------------------------------------*/


package org.alfresco.mbeans;

import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.ObjectName;
import java.util.ArrayList;
import org.alfresco.catalina.host.AVMHostConfig;

public class VirtWebappRegistry implements VirtWebappRegistryMBean 
{
    private String [] virtWebapps_ = { "totally", "bogus", "example", "of", "list" };
    private AVMHostConfig deployer_;

    public VirtWebappRegistry() { }

    public Boolean 
    virtualWebappUpdated(Integer version, String pathToWebapp, Boolean isRecursive)
    {
        if ( deployer_ != null )
        {
            return deployer_.updateVirtualWebapp( version.intValue(), 
                                                  pathToWebapp,
                                                  isRecursive.booleanValue()
                                                );
        }
        return false;
    }

    public Boolean 
    virtualWebappRemoved(Integer version, String pathToWebapp, Boolean isRecursive)
    {
        if ( deployer_ != null )
        {
            return deployer_.removeVirtualWebapp( version.intValue(), 
                                                  pathToWebapp,
                                                  isRecursive.booleanValue()
                                                );
        }
        return false;
    }


    public String[] getVirtWebapps()
    {
        return virtWebapps_;
    }

    /** Sets AVMHostConfig webapp deployer 
    *  The deployer handles the actual load/reload/unload of webapps
    */
    public void setDeployer(AVMHostConfig deployer)
    {
        deployer_ = deployer;
    }
}
