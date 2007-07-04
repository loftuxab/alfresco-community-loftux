/*-----------------------------------------------------------------------------
*  Copyright 2007 Alfresco Inc.
*  
*  This program is free software; you can redistribute it and/or modify
*  it under the terms of the GNU General Public License as published by
*  the Free Software Foundation; either version 2 of the License, or
*  (at your option) any later version.
*  
*  This program is distributed in the hope that it will be useful, but
*  WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
*  or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
*  for more details.
*  
*  You should have received a copy of the GNU General Public License along
*  with this program; if not, write to the Free Software Foundation, Inc.,
*  51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.  As a special
*  exception to the terms and conditions of version 2.0 of the GPL, you may
*  redistribute this Program in connection with Free/Libre and Open Source
*  Software ("FLOSS") applications as described in Alfresco's FLOSS exception.
*  You should have recieved a copy of the text describing the FLOSS exception,
*  and it is also available here:   http://www.alfresco.com/legal/licensing
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
    updateVirtualWebapp(Integer version, String pathToWebapp, Boolean isRecursive)
    {
        if ( deployer_ != null )
        {
            return deployer_.updateVirtualWebapp( 
                                version.intValue(), 
                                pathToWebapp,
                                isRecursive.booleanValue());
        }
        return false;
    }


    public Boolean 
    updateAllVirtualWebapps(Integer version, String path, Boolean isRecursive)
    {
        if ( deployer_ != null )
        {
            return deployer_.updateAllVirtualWebapps( 
                                version.intValue(), 
                                path,
                                isRecursive.booleanValue());
        }
        return false;
    }

    public Boolean 
    removeVirtualWebapp(Integer version, String pathToWebapp, Boolean isRecursive)
    {
        if ( deployer_ != null )
        {
            return deployer_.removeVirtualWebapp( 
                                version.intValue(), 
                                pathToWebapp,
                                isRecursive.booleanValue());
        }
        return false;
    }


    public Boolean 
    removeAllVirtualWebapps(Integer version, String path, Boolean isRecursive)
    {
        if ( deployer_ != null )
        {
            return deployer_.removeAllVirtualWebapps( 
                                version.intValue(), 
                                path,
                                isRecursive.booleanValue());
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
