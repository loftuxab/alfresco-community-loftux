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

// server side
import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.ObjectName;
import java.util.ArrayList;
 
// classes to talk with MBeanServer on virtualization server
import javax.management.Attribute;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import java.util.Properties;
import java.io.FileInputStream;
import java.util.Map;
import java.util.HashMap;


public class VirtServerRegistry implements VirtServerRegistryMBean
{
    private static org.apache.commons.logging.Log log=
        org.apache.commons.logging.LogFactory.getLog( VirtServerRegistry.class );

    private int         moo_ = 1;
    private String      virtServer_;
    private String      passwordFile_;
    private String      accessFile_;
    private ObjectName  virtWebappRegistry_;

    private JMXConnector           conn_ ;
    private JMXServiceURL          jmxServiceUrl_;
    private Map<String,Object>     env_;
    private MBeanServerConnection  mbsc_;


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
        log.info("--------------Creating VirtServerRegistry MBean");
    }

    
    public void initialize()
    {
        Properties passwordProps = new Properties();
        String jmxrmi_password   = null;

        try 
        {
            passwordProps.load( new FileInputStream( passwordFile_ ) );
            jmxrmi_password = passwordProps.getProperty("controlRole");
            env_ = new HashMap<String,Object>();
            String[] cred = new String[] { "controlRole", jmxrmi_password };
            env_.put("jmx.remote.credentials", cred );

            virtWebappRegistry_ = ObjectName.getInstance(
                "Alfresco:Name=VirtWebappRegistry,Type=VirtWebappRegistry");

        }
        catch (Exception e)
        {
            log.error(
              "Could not find password file for remote virtualization server",e);
        }
    }

    // interface method implementations
    public void setMoo(int moo)                { moo_ = moo ; }
    public int  getMoo()                       { return moo_; }

    public void   setPasswordFile(String path) { passwordFile_ = path;}
    public String getPasswordFile()            { return passwordFile_;}

    public void   setAccessFile(String path)   { accessFile_   = path;}
    public String getAccessFile()              { return accessFile_; }

    /**
    *  Accepts the registration of a remote virtualization server's 
    *  JMXServiceURL.  This is later used to broadcast updates regarding 
    *  the WEB-INF directory, and start/stop events.
    *  
    *  Note:  a JMXServiceURL tends to look something like this:
    *  service:jmx:rmi:///jndi/rmi://some-remote-hostname:50501/alfresco/jmxrmi
    */
    public void   setVirtServer(String virtServer) 
    { 
        synchronized( this )
        {
            if  ( virtServer_ != null  && 
                  ! virtServer_.equals( virtServer )
                )
            {
                jmxServiceUrl_ = null;
            }
            virtServer_ = virtServer; 
        }
    }

    synchronized 
    JMXServiceURL getJMXServiceURL()
    {
        if ( jmxServiceUrl_ != null) { return jmxServiceUrl_; }
        if ( virtServer_  == null )  { return null; } 
        try { jmxServiceUrl_ = new JMXServiceURL( virtServer_ ); }
        catch (Exception e)
        {
            log.error("Could not create JMXServiceURL from: " + virtServer_);
            jmxServiceUrl_ = null;
        }
        return jmxServiceUrl_;
    }
    
    public String getVirtServer()                  { return virtServer_; }

    public boolean webappUpdated(int version, String pathToWebapp)
    {
        // Typically:
        //  "service:jmx:rmi://ignored/jndi/rmi://localhost:50501/alfresco/jmxrmi"

        if ( getVirtServer() == null )
        { 
            log.error("No virtualization servers have registered as listeners");
            return false ; 
        }

        // NEON:  fix this to include version info  & multiple listeners
        log.info("webappUpdated:  version=" + version + 
                               "  path=" + pathToWebapp);

        if ( conn_ == null)
        {
            try 
            { 
                conn_ = JMXConnectorFactory.connect( getJMXServiceURL() , env_);
                mbsc_ = conn_.getMBeanServerConnection();
            }
            catch (Exception e)
            {
                log.error("Could not connect to virtualization server: " + getVirtServer() );
                return false;
            }
        }

        try
        {
            Attribute updatedWebapp = new Attribute("VirtWebapp", pathToWebapp );
            mbsc_.setAttribute( virtWebappRegistry_, updatedWebapp );
        }
        catch (Exception e)
        {
            log.error(
              "Could not connect to JXM Server within remote virtualization server " + 
              "(this may be a transient error.)");

            // NEON:
            //     Trace through all possible failure cases to figure out
            //     the right way to re-initiate the connection if the
            //     virt server shuts down, then comes back up.
            //     It's not clear if tossing conn_ w/o closing it is ok
            //     if an exception can happen for other reasons...
            //     yet close() can be very slow if the host is down,
            //     so it should be avoided if possible... or pushed into
            //     another thread... or something.   Investigate.
            conn_ = null;

            return false;
        }

        return true;
    }

    public boolean webappRemoved(int version, String pathToWebapp)
    {
        // TODO: fix this
        return true;
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

