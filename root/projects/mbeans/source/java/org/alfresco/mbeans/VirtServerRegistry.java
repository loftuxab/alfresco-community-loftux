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

    private String      virtServerJmxUrl_;
    private String      virtServerFQDN_;
    private Integer     virtServerHttpPort_;

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

    public void   setPasswordFile(String path) { passwordFile_ = path;}
    public String getPasswordFile()            { return passwordFile_;}

    public void   setAccessFile(String path)   { accessFile_   = path;}
    public String getAccessFile()              { return accessFile_; }

    public Integer getVirtServerHttpPort()     { return virtServerHttpPort_;}
    public String  getVirtServerFQDN()         { return virtServerFQDN_;    }

    /**
    *  Accepts the registration of a remote virtualization server's 
    *  JMXServiceURL, FQDN, and HTTP port.   The JMXServiceURL is 
    *  used to broadcast updates regarding the WEB-INF directory, 
    *  and start/stop events.  The FQDN and HTTP port are used
    *  to create URLs for viewing virtualized content.
    *  
    *  Note:  a JMXServiceURL tends to look something like this:
    *  service:jmx:rmi:///jndi/rmi://some-remote-hostname:50501/alfresco/jmxrmi
    */
    public synchronized 
    void registerVirtServerInfo( String  virtServerJmxUrl,
                                 String  virtServerFQDN,
                                 Integer virtServerHttpPort
                               )
    {
        if  (   virtServerJmxUrl_ != null  && 
              ! virtServerJmxUrl_.equals( virtServerJmxUrl )
            )
        {
            jmxServiceUrl_ = null;
        }
        virtServerJmxUrl_    = virtServerJmxUrl; 
        virtServerFQDN_      = virtServerFQDN;
        virtServerHttpPort_  = virtServerHttpPort;
    }


    // public synchronized void   setVirtServerJmxUrl(String virtServerJmxUrl) 
    // { 
    //     if  (   virtServerJmxUrl_ != null  && 
    //           ! virtServerJmxUrl_.equals( virtServerJmxUrl )
    //         )
    //     {
    //         jmxServiceUrl_ = null;
    //     }
    //     virtServerJmxUrl_ = virtServerJmxUrl; 
    // }

    synchronized 
    JMXServiceURL getJMXServiceURL()
    {
        if ( jmxServiceUrl_ != null)       { return jmxServiceUrl_; }
        if ( virtServerJmxUrl_  == null )  { return null; } 

        try { jmxServiceUrl_ = new JMXServiceURL( virtServerJmxUrl_ ); }
        catch (Exception e)
        {
            log.error("Could not create JMXServiceURL from: " + virtServerJmxUrl_);
            jmxServiceUrl_ = null;
        }
        return jmxServiceUrl_;
    }
    
    public String getVirtServerJmxUrl()                  { return virtServerJmxUrl_; }


    /**
    *  Notifies remote listener that a AVM-based webapp has been "updated".
    *  An "update" is any change to (or creation of) contents within
    *  WEB-INF/classes  WEB-INF/lib, WEB-INF/web.xml of a webapp.
    */
    public boolean webappUpdated(int version, String pathToWebapp)
    {
        return jmxRmiWebappNotification( "virtualWebappUpdated",
                                          version,
                                          pathToWebapp
                                       );
    }

    /**
    *  Notifies remote listener that a AVM-based webapp has been removed.
    */
    public boolean webappRemoved(int version, String pathToWebapp)
    {
        return jmxRmiWebappNotification( "virtualWebappRemoved",
                                          version,
                                          pathToWebapp
                                       );
    }

    protected boolean verifyJmxRmiConnection()
    {
        // Typically the JMXServiceURL looks something like this:
        //  "service:jmx:rmi://ignored/jndi/rmi://localhost:50501/alfresco/jmxrmi"

        if ( getVirtServerJmxUrl() == null )
        { 
            log.error("No virtualization servers have registered as listeners");
            return false ; 
        }

        if ( conn_ == null)
        {
            try 
            { 
                conn_ = JMXConnectorFactory.connect( getJMXServiceURL() , env_);
                mbsc_ = conn_.getMBeanServerConnection();
            }
            catch (Exception e)
            {
                log.error("Could not connect to virtualization server: " + 
                          getVirtServerJmxUrl() );

                return false;
            }
        }
        return true;
    }

    protected boolean 
    jmxRmiWebappNotification( String  action,
                              int     version, 
                              String  pathToWebapp 
                            )
    {
        if  ( ! verifyJmxRmiConnection() ) { return false; }

        try
        {
            Boolean result = 
                (Boolean) mbsc_.invoke( virtWebappRegistry_, 
                                        action,
                                        new Object [] { new Integer( version ),
                                                        pathToWebapp
                                                      },
                                        new String [] { "java.lang.Integer",
                                                        "java.lang.String"
                                                      }
                                      );
           
            if ( ! result.booleanValue() ) 
            { 
                log.error("Action failed: " + action + "  Version: " + version  + 
                          "  Webapp: " + pathToWebapp );

                return false; 
            }
            return true;
        }
        catch (Exception e)
        {
            log.error(
              "Could not connect to JMX Server within remote virtualization server " + 
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

