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
*  File    VirtServerRegistry.java
*----------------------------------------------------------------------------*/

package org.alfresco.mbeans;

import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.ObjectName;
import java.util.ArrayList;

public class VirtServerRegistry implements VirtServerRegistryMBean 
{
    private int moo_ = 1;
    private String virtServer_;

    // The org.alfresco.mbeans.ContextListener
    // creates a VirtServerRegistry when it receives 
    // a  contextInitialized(ServletContextEvent event) 
    // call from the Alfresco webapp.
    //
    // See also: the deployment descriptor for ContextListener 
    //           within the alfresco webapp's web.xml file.
    //
    public VirtServerRegistry() 
    {
        System.out.println("--------------Creating VirtServerRegistry MBean: " + this );

        // How to register w/o Spring:
        //
        //        MBeanServer server = getServer();
        //        ObjectName name = null;
        //        try 
        //        {
        //            name = new ObjectName("Alfresco:Name=VirtServerRegistry,Type=VirtServerRegistry");
        //            server.registerMBean(this, name);
        //        } 
        //        catch (Exception e) { e.printStackTrace(); }
    }

    // interface method implementations
    public void setMoo(int moo) { moo_ = moo ; }
    public int  getMoo()        { return moo_; }

    /**
    *  Registers the JMXServiceURL of a virtualization server.
    *  This is later used to broadcast updates regarding 
    *  the WEB-INF directory, and start/stop events.
    *  
    *  Note:  a JMXServiceURL can look something like:
    *  service:jmx:rmi:///jndi/rmi://some-remote-hostname:50501/alfresco/jmxrmi
    */
    public void  setVirtServer(String virtServer)
    {
        virtServer_ = virtServer;
    }

    public String  getVirtServer()
    {
        return virtServer_;
    }



    // How to register w/o Spring (continued): 
    //
    //    private MBeanServer getServer() 
    //    {
    //        MBeanServer mbserver = null;
    //        ArrayList   mbservers = MBeanServerFactory.findMBeanServer(null);
    //
    //        if (mbservers.size() > 0) 
    //        {
    //            mbserver = (MBeanServer) mbservers.get(0);
    //        }
    //
    //        // Cnly create an MBeanServer if the system didn't already have one
    //        if ( mbserver == null )
    //        {
    //            mbserver = MBeanServerFactory.createMBeanServer();
    //        } 
    //        return mbserver;
    //    }
}

