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
*  File    AVMHost.java
*----------------------------------------------------------------------------*/

package org.alfresco.catalina.host;


import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;
import java.util.HashMap;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.management.Attribute;
import javax.management.MBeanServer;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import org.alfresco.jndi.AVMFileDirContext;
import org.alfresco.mbeans.VirtServerInfoMBean;
import org.alfresco.repo.avm.AVMRemote;
import org.apache.catalina.Container;
import org.apache.catalina.Context;
import org.apache.catalina.core.StandardEngine;
import org.apache.catalina.core.StandardPipeline;
import org.apache.catalina.Host;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.HostConfig;
import org.apache.catalina.Valve;
import org.apache.catalina.valves.ValveBase;
import org.apache.commons.modeler.Registry;
import org.springframework.context.support.FileSystemXmlApplicationContext;


/**
*  This class implements a Catalina virtual Host;  it can be 
*  specified as a 'className' within a Catalina <tt>&lt;Host&gt;</tt>
*  node in order to fetch data from Alfresco's virtual repository,
*  rather than from the file system.  For example:
*  
*  <pre>
*     &lt;Host name              = "avm.localhost"
*           className         = "org.alfresco.catalina.host.AVMHost"
*           appBase           = "avm_webapps"
*           unpackWARs        = "true"
*           autoDeploy        = "true"
*           xmlValidation     = "false"
*           xmlNamespaceAware = "false"&gt;
*           &lt;!-- ... other args ... --&gt;
*     &lt;/Host&gt;
*  </pre>
*
*  The addtional parameters it offers that aren't part of Catalina's 
*  StandardHost implementation are:
*
*  <dl>
*    <td><tt><strong>reverseProxyBinding</strong></tt>
*       <dd>A regex that binds reverse proxy names to this Host.<br>
*           <em>Default:</em>&nbsp;&nbsp;
*           "^www-([^.]+)(?:\\.v-([\\d]+))?\\.avm(?:\\..*)?$"
*       </dd>
*    </td>
*    <td><tt><br><strong>resourceBindingClassName</strong></tt>
*         <dd>Class that maps the request to a virtual repository name/version.<br>
*             <em>Default:</em>&nbsp;&nbsp;
*             "org.alfresco.catalina.host.DefaultAVMResourceBinding"
*        </dd>
*    </td> 
*  </dl>
*
*  Also, the interpretation of the following attribute is different:
*  <dl>
*    <td><tt><strong>appBase</strong></tt>
*       <dd>The folder within each AVM repository used to compose a virtual appBase<br>
*           (see {@link AVMHostConfig}).
*       </dd>
*    </td>
*  </dl>
*  &nbsp;
*  <p>
*
*  <h3>Details</h3>
*    The <tt>reverseProxyBinding</tt> regular expression allows 
*    DNS-wildcarded requests resolving to the machine that Alfresco 
*    is running on to be mapped to an AVMHost-based virtual server 
*    within Catalina that will provide the response.  This arrangement 
*    allows you to configure more than one <tt>&lt;Host&gt;</tt> in 
*    $CATALINA_HOME/conf/server.xml that sets:
*    <pre>
*         className ="org.alfresco.catalina.host.AVMHost"
*    </pre>
*    This can be useful, because each <tt>&lt;Host&gt;</tt> can have its
*    own "appBase" attribute.  Thus, you can have multiple 
*    virtual hosts that use different portions of Alfresco's 
*    repository (rather than the file system).  
*    <p>
*    For example, suppose the default Catalina virtual host is 
*    named "localhost",  and it's accessed via DNS wildcarding
*    using some other name (e.g.: "www-repo-1.avm.localhost").
*    What's needed is a way to say "every DNS wildcard name that
*    matches a regex pattern (<tt>reverseProxyBinding='...'</tt>) should 
*    act as a reverse proxy for a particular AVMHost-based virtual 
*    host (e.g.: "avm.localhost").  If you don't provide
*    a <tt>reverseProxyBinding</tt> attribute explicitly, the following 
*    pattern will be used:
*    <pre>
*           "^www-([^.]+)(?:\\.v-([\\d]+))?\\.avm(?:\\..*)?$"
*                  -----          ------
*                    ^              ^
*                    |              |
*                 group 1        group 2
*    </pre>
*    By default, the repository name will be taken from "group 1", 
*    and the optional version will be taken from "group 2"
*    (see: {@link org.alfresco.catalina.host.DefaultAVMResourceBinding}).
*    <p>
*    If you have multiple AVMHost-based virtual hosts, the 
*    <tt>reverseProxyBinding</tt> regexes will be processed in the 
*    order of their appearance. 
*    <p>
*    Here's a snippet from 
*    $CATALINA_HOME/conf/server.xml that illustrates two 
*    <tt>&lt;Host&gt;</tt> tags;  the first uses the default 
*    <tt>reverseProxyBinding</tt>
*    and associates it with the appBase "avm_webapps", while the second
*    binds avm2.localhost to non-default reverse proxy names
*    ("www2-..."), and the appBase "avm_webapps2"  (it also states 
*    explicitly that the 
*    org.alfresco.catalina.host.DefaultAVMResourceBinding should be
*    used to parse the results of the <tt>reverseProxyBinding</tt>).
* <pre>
*       &lt;Host name="avm.localhost"
*             className           ="org.alfresco.catalina.host.AVMHost"
*             appBase             ="avm_webapps"
*             unpackWARs          ="true"
*             autoDeploy          ="true"
*             xmlValidation       ="false"
*             xmlNamespaceAware   ="false"&gt;
*       &lt;/Host&gt;
*
*       &lt;Host name="avm2.localhost"
*             className                ="org.alfresco.catalina.host.AVMHost"
*             reverseProxyBinding      ="^www2-([^.]+)(?:\\.v-([\\d]+))?\\.avm\\..*"
*             resourceBindingClassName ="org.alfresco.catalina.host.DefaultAVMResourceBinding"
*             appBase                  ="avm_webapps2"
*             unpackWARs               ="true"
*             autoDeploy               ="true"
*             xmlValidation            ="false"
*             xmlNamespaceAware        ="false"&gt;
*       &lt;/Host&gt;
*
* </pre>
*
* The <tt>resourceBindingClassName</tt> attribute allows you to customize
* how a request to a reverse proxy maps to resources within the AVM repository.
* While <tt>org.alfresco.catalina.host.DefaultAVMResourceBinding</tt> merely
* takes "group 2" as a literal version number, you might want to do something
* more sophisticated, such as map dates to AVM version numbers;  this would
* let you create "permalinks" that could be "guessed" by date.  See the
* {@link AVMResourceBinding} interface for more details.
*/ 
public class AVMHost extends org.apache.catalina.core.StandardHost
{
    private static org.apache.commons.logging.Log log=
        org.apache.commons.logging.LogFactory.getLog( AVMHost.class );

    static String AVMFileDirAppBase_ = AVMFileDirContext.getAVMFileDirAppBase();
    static FileSystemXmlApplicationContext SpringContext_ = null;


    // Because this is private, not protected in the base class,
    // and it has no accessor, I was forced to cut/paste start().
    // All I *really* neede was init, but the structure of 
    // StandardHost forced me into grabbing more. 

    private boolean initialized=false;

    // More fallout from initialized being private in 
    // StandardHost (I had to drag in start(), which
    // references errorReportValve.   
    /**
     * The object name for the errorReportValve.
     */
    private ObjectName errorReportValveObjectName = null;


    /**
     * The descriptive information string for this implementation.
     */
    private static final String info =
        "org.alfresco.jndi.AVMHost/1.0";


    public AVMHost()
    {
        super();
    }



    /**
    */
    public String getReverseProxyBinding()
    { 
        return reverse_proxy_binding_;
    }
    public void setReverseProxyBinding(String binding) 
    { 
        reverse_proxy_binding_ = binding;
    }
    String reverse_proxy_binding_  = "^www-([^.]+)(?:\\.v-([\\d]+))?\\.avm(?:\\..*)?$";

    public String getResourceBindingClassName()
    {
        return resource_binding_classname_;
    }

    public void setResourceBindingClassName(String binding)
    {
        resource_binding_classname_ = binding;
    }

    String resource_binding_classname_  = 
               "org.alfresco.catalina.host.DefaultAVMResourceBinding";

    AVMResourceBinding resource_binding_;

    public AVMResourceBinding getResourceBinding()
    {
        return resource_binding_;
    }



    static class ReverseProxyBinding
    {
        AVMHost  host;
        String   regex;
        Pattern  pattern; 

        public  ReverseProxyBinding(AVMHost avmHost,
                                    String  reverseProxyBinding)
        {
            host    = avmHost;
            regex   = reverseProxyBinding;
            pattern = Pattern.compile( regex );
        }
    }

    // Non-sync to avoid hotlock.
    static ArrayList<ReverseProxyBinding> ReverseProxies_ = 
           new ArrayList<ReverseProxyBinding>( );

    static public AVMHostMatch getAVMHostMatch(String forwardProxyName )
    {
        Matcher match;
        for ( ReverseProxyBinding binding :  ReverseProxies_ )
        { 
            match = binding.pattern.matcher( forwardProxyName );
            if (match.find() )
            {
                return new AVMHostMatch( binding.host, match);
            }
        }
        return null;
    }

    /**
     * Return descriptive information about this Container implementation and
     * the corresponding version number, in the format
     * <code>&lt;description&gt;/&lt;version&gt;</code>.
     */
    public String getInfo() { return (info); }


    /**
     * Return a String representation of this component.
     */
    public String toString() 
    {
        StringBuffer sb = new StringBuffer();
        if (getParent() != null) 
        {
            sb.append(getParent().toString());
            sb.append(".");
        }
        sb.append("AVMHost[");
        sb.append(getName());
        sb.append("]");
        return (sb.toString());

    }


    /**
    * Return the (virtual) application root for this Host.
    */
    public String getAppBase() 
    { 
        return AVMFileDirAppBase_  + this.name;
    }

    /**
    *  Returns the non-virtualized appBase value specified
    *  for this host within $CATALINA_HOME/conf/server.xml.
    */
    public String getHostAppBase()
    {
        return super.getAppBase();
    }

    public void init() 
    {
        if( initialized ) return;
        initialized=true;

        try 
        {
            // By default, the resource_binding_classname_ is:
            //    "org.alfresco.catalina.host.DefaultAVMResourceBinding",
            //
            // This simple class maps reverseProxyBinding regex group(1) 
            // to the name of the respository, and and group(2) (if present) 
            // to the repo version (default == -1).
            
            resource_binding_ = (AVMResourceBinding) 
                Class.forName( resource_binding_classname_ ).newInstance();

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        org.apache.catalina.LifecycleListener[]  listen = findLifecycleListeners();

        // Register this AVMHost with the static list of all AVMHosts
        // This allows the AVMUrlValve to map forward proxy names
        // to the backend AVMHost that will service them.
        //
        ReverseProxies_.add( 
            new ReverseProxyBinding( this, reverse_proxy_binding_)
        );

        // already registered.
        try 
        {
            // Register with the Engine
            ObjectName serviceName=new ObjectName(domain + ":type=Engine");

            for (int i=0; i < listen.length; i++)
            {
                // Remove the HostConfig that got inserted by default
                if (listen[i]  instanceof  HostConfig )
                {
                    removeLifecycleListener( listen[ i ]  );
                }
            }

            AVMFileDirContext.InitAVMRemote();

            // Give AVM server the info it needs to perform callbacks
            // to this virtualization server when major event occur.
            // Examples:   WEB-INF is updated, the AVM server stops/starts.
            //             possibly when a revert occurs, etc.
            //
            // When this virt server gets a message to update a virtual webapp,
            // a recursive classloader reload is triggered for that webapp.

            registerVirtServerWithAvmServer();  




            // Use a custom deployer that knows how to access AVMRemote
            // Tell the AVMHostContext what appBase was given within 
            // the server.xml file.
             
            HostConfig deployer = new AVMHostConfig( super.getAppBase() );

            addLifecycleListener(deployer);                

            // TODO:  Determine whether to deal with registration.
            //        In the superclass, the code was as listed
            //        below, but in this context, it just throws
            //        an exception.   So far, seems non-critical.
            //
            //  if( mserver.isRegistered( serviceName )) {
            //
            //      if(log.isDebugEnabled())
            //          log.debug("Registering "+ serviceName +" with the Engine");
            //      mserver.invoke( serviceName, "addChild",
            //              new Object[] { this },
            //              new String[] { "org.apache.catalina.Container" } );
            //  }

        } 
        catch( Exception ex ) 
        {
            log.error("Host registering failed!",ex);
        }

        
        if( oname==null ) 
        {
            // not registered in JMX yet - standalone mode
            try 
            {
                StandardEngine engine=(StandardEngine)parent;
                domain=engine.getName();
                if(log.isDebugEnabled())
                {
                    log.debug( "Register host " + getName() + " with domain "+ domain );
                }

                oname=new ObjectName( domain             + 
                                      ":type=Host,host=" + 
                                      this.getName()
                                    );

                controller = oname;

                Registry.getRegistry(null, null).registerComponent(
                    this, oname, null
                );
            } 
            catch( Throwable t ) 
            {
                log.error("Host registering failed!", t );
            }
        }
    }


    /**
    *  Registers this virtualization server with an AVM server.
    *  Later, when the AVM server does something that will
    *  require this virtualization server to do a recursive
    *  classloader reload, the AVM server will send back a 
    *  message to the virtualization server, telling it
    *  which virtual webapp to reload.
    *
    *  TODO:  This should be in its own thread that re-registers
    *         the Host every 10 (or so) seconds.  This will handle 
    *         the case when the Alfrseco server dies, dropping 
    *         the list of all registered listeners along with its 
    *         associated MBeanServer
    */
    private void registerVirtServerWithAvmServer()
    {
        SpringContext_   = AVMFileDirContext.GetSpringApplicationContext();

        VirtServerInfoMBean serverInfo = 
            (VirtServerInfoMBean)  
                SpringContext_.getBean("virtServerInfo");

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

            // Create a JMXServiceURL to connect to the Alfresco JMX RMI server
            // These urls tend to look like:
            // 
            //  "service:jmx:rmi://ignored/jndi/rmi://localhost:50500/alfresco/jmxrmi"
            //
            JMXServiceURL url = 
              new JMXServiceURL("service:jmx:rmi://ignored/jndi/rmi://" +
                                 serverInfo.getAlfrescoJmxRmiHost()     +
                                 ":"                                    +
                                  serverInfo.getAlfrescoJmxRmiPort()    +
                                 "/alfresco/jmxrmi"
                               );

             Map<String,Object> env = new HashMap<String,Object>();
             String[] cred = new String[] { "controlRole", jmxrmi_password };
             env.put("jmx.remote.credentials", cred );
             JMXConnector conn = JMXConnectorFactory.connect(url, env);
             MBeanServerConnection mbsc = conn.getMBeanServerConnection();
             ObjectName virt_registry = ObjectName.getInstance(
                 "Alfresco:Name=VirtServerRegistry,Type=VirtServerRegistry");


             String virt_url = "service:jmx:rmi://ignored/jndi/rmi://" + 
                               serverInfo.getVirtServerJmxRmiHost()    + 
                               ":"                                     +
                               serverInfo.getVirtServerJmxRmiPort()    + 
                               "/alfresco/jmxrmi";
                                
             Attribute virt_server_attrib = 
                new Attribute("VirtServer", virt_url );

             mbsc.setAttribute( virt_registry, virt_server_attrib);
        }
        catch (Exception e)
        {
            log.error(
              "Could not find password file for remote Alfresco JXM Server",e);
        }

    }


    /**
     * Start this host.
     *
     * @exception LifecycleException if this component detects a fatal error
     *  that prevents it from being started
     */
    public synchronized void start() throws LifecycleException 
    {
        // new Exception("Stack trace").printStackTrace();

        if( started ) { return; }

        if( ! initialized ) { init(); }

   
        // Look for a realm - that may have been configured earlier. 
        // If the realm is added after context - it'll set itself.
        if( realm == null ) 
        {
            ObjectName realmName=null;
            try 
            {
                realmName=new ObjectName( domain + ":type=Realm,host=" + getName());
                if( mserver.isRegistered(realmName ) ) 
                {
                    mserver.invoke(
                        realmName, "init", new Object[] {}, new String[] {} );
                }
            } 
            catch( Throwable t ) 
            {
                log.debug("No realm for this host " + realmName);
            }
        }
            
        // Set error report valve
        if (( getErrorReportValveClass() != null) && 
            (!getErrorReportValveClass().equals(""))
           ) 
        {
            try 
            {
                boolean found = false;
                if(errorReportValveObjectName != null) 
                {
                    ObjectName[] names = 
                        ((StandardPipeline)pipeline).getValveObjectNames();

                    for (int i=0; !found && i<names.length; i++)
                    {
                        if(errorReportValveObjectName.equals(names[i])) 
                        {
                            found = true ;
                        }
                    }
                }

                if(!found) 
                {          	
                    Valve valve = (Valve) 
                        Class.forName(getErrorReportValveClass()).newInstance();

                    addValve(valve);
                    errorReportValveObjectName = ((ValveBase)valve).getObjectName() ;
                }
            } 
            catch (Throwable t) 
            {
                log.error(
                   sm.getString("standardHost.invalidErrorReportValveClass",
                   getErrorReportValveClass())
                );
            }
        }

        if(log.isInfoEnabled()) 
        {
            if ( getXmlValidation() )
            {
                log.info( sm.getString("standardHost.validationEnabled"));
            }
            else
            {
                log.info( sm.getString("standardHost.validationDisabled"));
            }
        }

        // Calling super.start() invokes     ContainerBase start(), 
        //                       which calls AVMHostConfig start(),
        //                       which calls AVMHostConfig deployApps()
        //
        super.start();
    }

    public synchronized void stop() throws LifecycleException 
    {

        AVMFileDirContext.ReleaseAVMRemote();

        // Calling super.stop()  invokes     ContainerBase stop(), 
        //                       which calls AVMHostConfig stop(),
        //                       which calls AVMHostConfig undeployApps()
        //

        super.stop();
    }
}
