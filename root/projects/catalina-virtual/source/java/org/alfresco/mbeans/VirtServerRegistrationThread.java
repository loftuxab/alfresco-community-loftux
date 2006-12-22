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
*  File    VirtServerRegistrationThread.java
*----------------------------------------------------------------------------*/

package org.alfresco.mbeans;

import java.util.Properties;
import javax.management.Attribute;
import org.alfresco.jndi.AVMFileDirContext;
import java.io.FileInputStream;
import javax.management.MBeanServer;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import org.alfresco.mbeans.VirtServerInfoMBean;
import org.springframework.context.ApplicationContext;
import java.util.Map;
import java.util.HashMap;


/**
*  Registers virtualization server with an AVM server.
*  Later, when the AVM server does something that will
*  require this virtualization server to do a recursive
*  classloader reload, the AVM server will send back a 
*  message to the virtualization server, telling it
*  which virtual webapp to reload.
*
*  The registration is done repeatedly (every N seconds where n == 20 for now)
*  to deal with the possiblity of the alfresco server restarting.
*/

public class VirtServerRegistrationThread extends Thread
{
    private static org.apache.commons.logging.Log log=
        org.apache.commons.logging.LogFactory.getLog( VirtServerRegistrationThread.class );

    ApplicationContext springContext_;
    JMXServiceURL      url_;
    Map<String,Object> env_;
    String             virt_url_;
    String             virt_domain_;
    String             virt_fqdn_;
    int                virt_http_port_;
    int                virt_connect_retry_interval_;
    ObjectName         virt_registry_;
    Attribute          virt_server_attrib_;
    boolean            done_  = false;

    public VirtServerRegistrationThread()
    {
        springContext_   = AVMFileDirContext.GetSpringApplicationContext();

        VirtServerInfoMBean serverInfo = 
            (VirtServerInfoMBean)  
                springContext_.getBean("virtServerInfo");

        String catalina_base;
        catalina_base = System.getProperty("catalina.base");
        if ( catalina_base == null)
        {   
            catalina_base = System.getProperty("catalina.home");
        }
        if ( catalina_base != null)
        {   
            if ( ! catalina_base.endsWith("/") )
            {   
                catalina_base = catalina_base + "/";
            }
        }
        else { catalina_base = ""; }


        String password_file = catalina_base + "conf/alfresco-jmxrmi.password";
        Properties passwordProps = new Properties();
        String jmxrmi_password   = null;

        try 
        {
            passwordProps.load( new FileInputStream( password_file ) );
            jmxrmi_password = passwordProps.getProperty("controlRole");

            virt_connect_retry_interval_ = 
                serverInfo.getVirtServerConnectionRetryInterval();


            // Create a JMXServiceURL to connect to the Alfresco JMX RMI server
            // These urls tend to look like:
            // 
            //  "service:jmx:rmi://ignored/jndi/rmi://localhost:50500/alfresco/jmxrmi"

            String avm_jmx_url = "service:jmx:rmi://ignored/jndi/rmi://" +
                                  serverInfo.getAlfrescoJmxRmiHost()     +
                                  ":"                                    +
                                  serverInfo.getAlfrescoJmxRmiPort()     +
                                  "/alfresco/jmxrmi";

            url_ = new JMXServiceURL( avm_jmx_url );

            env_ = new HashMap<String,Object>();

            String[] cred = new String[] { "controlRole", jmxrmi_password };
            env_.put("jmx.remote.credentials", cred );

            virt_registry_ = ObjectName.getInstance(
                "Alfresco:Name=VirtServerRegistry,Type=VirtServerRegistry");

            virt_domain_    = serverInfo.getVirtServerDomain();
            virt_fqdn_      = virt_domain_;
            virt_http_port_ = serverInfo.getVirtServerHttpPort();


            // The FQDN of the virtualization server is always:
            //
            //           ${alfresco.virtserver.domain}
            //
            // The invariant are: 
            //
            //    [1]  The virtualization domain:   ${alfresco.virtserver.domain} 
            //         and all its subdomains:    *.${alfresco.virtserver.domain}
            //         must resolve to the same IP address.
            //
            //    [2]  The IP address resolved in [1] must be equal to the
            //         IP address that the virtualization server listens on.
            //
            // See: $VIRTUAL_TOMCAT_HOME/conf/alfresco-virtserver.properties
            //

            virt_url_ = "service:jmx:rmi://ignored/jndi/rmi://" + 
                        virt_fqdn_                              + 
                        ":"                                     +
                        serverInfo.getVirtServerJmxRmiPort()    + 
                        "/alfresco/jmxrmi";

            virt_server_attrib_ = new Attribute("VirtServerJmxUrl", virt_url_ );
        }
        catch (Exception e)
        {
            log.error(
              "Could not find password file for remote Alfresco JMX Server",e);
        }
    }

    public void run() 
    {
        while ( getDone() != true )
        {
            registerVirtServer();

            // Take a nap.  
            try { Thread.sleep( virt_connect_retry_interval_ ); } 
            catch (Exception e) 
            {
                // Not much you can do about an exception here, just ignore it.
            }
        }
    }

    private void registerVirtServer()
    {
        // System.out.println("Re-registering url: " + virt_url_ );
        try
        {
            JMXConnector conn = JMXConnectorFactory.connect(url_, env_);
            MBeanServerConnection mbsc = conn.getMBeanServerConnection();

            // mbsc.setAttribute( virt_registry_, virt_server_attrib_);

            mbsc.invoke( virt_registry_, 
                         "registerVirtServerInfo", 
                         new Object [] { virt_url_ , 
                                         virt_fqdn_,
                                         new Integer( virt_http_port_ )
                                       },
                         new String [] { "java.lang.String",
                                         "java.lang.String",      
                                         "java.lang.Integer"
                                       }
                       );

        }
        catch (Exception e)
        {
            log.error(
              "Could not connect to JMX Server within remote Alfresco webapp " + 
              "(this may be a transient error.  Retrying...)");
        }
    }

    /** 
    * Called by org.alfresco.catalina.host.AVMHost.stop()
    * so that the registration thread exists and the server
    * can shut down cleanly.
    */
    public void setDone()    { done_ = true; }

    /**
    *  Called within this thread's run() method to
    *  determine if it's time to end gracefully.
    */
    public boolean getDone() { return done_ ;}
}
