/*-----------------------------------------------------------------------------
*  Copyright 2006 Alfresco Inc.
*  
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 * As a special exception to the terms and conditions of version 2.0 of
 * the GPL, you may redistribute this Program in connection with Free/Libre
 * and Open Source Software ("FLOSS") applications as described in Alfresco's
 * FLOSS exception.  You should have recieved a copy of the text describing
 * the FLOSS exception, and it is also available here:
 * http://www.alfresco.com/legal/licensing"*  
*  
*  Author  Jon Cox  <jcox@alfresco.com>
*  File    AVMHost.java
*----------------------------------------------------------------------------*/

package org.alfresco.catalina.host;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.management.ObjectName;
import org.alfresco.jndi.AVMFileDirContext;
import org.alfresco.mbeans.VirtServerInfoMBean;
import org.alfresco.mbeans.VirtServerRegistrationThread;
import org.alfresco.mbeans.VirtWebappRegistryMBean;
import org.alfresco.service.cmr.remote.AVMRemote;
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
import org.springframework.context.ApplicationContext;

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
*           "^(.+)\\.www--sandbox\\.(?:version--v(-?[\\d]+)\\.)?.*$"
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
*    For example, suppose the default Catalina virtual host is 
*    named "localhost",  and it's accessed via DNS wildcarding using
*    some other name (e.g.: "alice.mysite.www--sandbox.avm.localhost").
*    What's needed is a way to say "every DNS wildcard name that
*    matches a regex pattern (<tt>reverseProxyBinding='...'</tt>) should 
*    act as a reverse proxy for a particular AVMHost-based virtual 
*    host (e.g.: "avm.localhost").  If you don't provide
*    a <tt>reverseProxyBinding</tt> attribute explicitly, the following 
*    pattern will be used:
*    <pre>
*           "^(.+)\\.www--sandbox\\.(?:version--v(-?[\\d]+)\\.)?.*$"
*              ---                                --------
*               ^                                    ^
*               |                                    |
*             group 1                             group 2
*    </pre>
*    By default, the repository name will be taken from "group 1", 
*    and the optional version will be taken from "group 2"
*    (see: {@link org.alfresco.catalina.host.DefaultAVMResourceBinding}).
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

    static String AVMFileDirMountPoint_;

    // Repeatedly re-registers this Host with the Alfresco server
    // (e.g.: every 10 seconds or so).
    protected VirtServerRegistrationThread registrationThread_ = null;


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


    String reverse_proxy_binding_;


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
        // Help make JNDI paths line up with CIFS paths
        // Thus instead of something like:
        //
        //      return AVMFileDirMountPoint_  + this.name;
        //
        // Just pass back whatever AVMFileDirMountPoint_ 
        // was as the host's appBase.
        //
        // TODO:  Perhaps the CIFS mount path could be augmented
        //        augmented to include the virtual hosts's name.
        //        Entirely possible via automount; however, consider
        //        be sure to consider scenarios where both the
        //        Alfresco webapp and the virt server want to setup
        //        CIFS.  How important is sharing the mount point
        //        in terms of caching?  Investigate.
        //
        // NOTE:
        //        The 'work' dir depents on the virtual host's name,
        //        not the application base.  Thus, the work dir remains 
        //        the same no mater what you do.  For example, if the host 
        //        is "avm.alfresco.localhost" then the workdir created is:
        //
        //        $VIRTUAL_TOMCAT_HOME/work/Catalina/avm.alfresco.localhost
        //
        //        The host "avm.alfresco.localhost" is where AVMUrlValve 
        //        reverse proxies to by default.  AVMHost itself knows 
        //        nothing of the wildcard DNS shennanagins that made the
        //        client's request map to the IP address that the AVMHost's
        //        connector is listening on.   When AVMUrlValve issues its
        //        internal subrequest to avm.alfresco.localhost, all the
        //        interesting information for JNDI has been tunneled into
        //        the 1st segment of the request path.  In a very real sense,
        //        the only "actual" virtual host is avm.alfresco.localhost
        //        (hence, that's the only Alfreco-related "work" dir created).
        //
        //        As for JNDI, AVMFileDirContext.setDocBase() now just gets:
        //         <AvmFileDirAppBase>/<value-determined-by-AVMHostConfig>
        //

        return AVMFileDirMountPoint_;
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

        AVMFileDirContext.InitAVMRemote();

        // Create the registration thread early, but don't start it yet.
        // The creation of this object configures various attributes
        // in the VirtServerInfoMBean, and sets up AVMFileDirContext
        // so it can yield a meaningful AVMFileDirMountPoint.
        //
        registrationThread_ = new VirtServerRegistrationThread();

        // AVMHostConfig will need to call getAppBase() on its
        // container (i.e.: this AVMHost).  Therefore, we need
        // to prepare for that prior to the AVMHostConfig start()
        // occurs.
        //
        AVMFileDirMountPoint_  = AVMFileDirContext.getAVMFileDirMountPoint();


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


            // Use a custom deployer that knows how to access AVMRemote
            // Tell the AVMHostContext what appBase was given within 
            // the server.xml file.
             
            AVMHostConfig deployer = new AVMHostConfig( super.getAppBase() );
            ApplicationContext springContext =  
                AVMFileDirContext.GetSpringApplicationContext();


            if ( reverse_proxy_binding_ == null )
            {
                // VirtServerInfoMBean serverInfo =
                //     (VirtServerInfoMBean) springContext.getBean("virtServerInfo");
                //
                // String virt_domain = serverInfo.getVirtServerDomain();


                reverse_proxy_binding_ = "^(.+)\\." + 
                                         "www--sandbox\\." +
                                         "(?:version--v(-?[\\d]+)\\.)?" +
                                         ".*$";
            }

            System.out.println("Reverse proxy binding: " + reverse_proxy_binding_ );

            // Register this AVMHost with the static list of all AVMHosts
            // This allows the AVMUrlValve to map forward proxy names
            // to the backend AVMHost that will service them.
            //
            ReverseProxies_.add( 
                new ReverseProxyBinding( this, reverse_proxy_binding_)
            );



            // Register the AVMHostConfig deployer with the 
            // webapp registry.  This enables remote updates
            // to the VirtWebappRegistryMBean (from the Alfersco server)
            // to callback into the AVMHostconfig, which then does the
            // recusive load,reload, or unload of the appropriate webapps.

            VirtWebappRegistryMBean  virtWebappRegistry = 
                (VirtWebappRegistryMBean)  springContext.getBean("virtWebappRegistry");

            virtWebappRegistry.setDeployer( deployer );


            // Give AVM server the info it needs to perform callbacks
            // to this virtualization server when major event occur.
            // Examples:   WEB-INF is updated, the AVM server stops/starts.
            //             possibly when a revert occurs, etc.
            //
            // When this virt server gets a message to update a virtual webapp,
            // a recursive classloader reload is triggered for that webapp.

            registrationThread_.start();


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

        if  ( registrationThread_ == null )
        {
            // Re-register this virtualization host with the Alfresco server
            registrationThread_ = new VirtServerRegistrationThread();
            registrationThread_.start();
        }

   
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
        // Tell the registration thread it can exit
        if ( registrationThread_  != null )
        {
            registrationThread_.setDone();
            registrationThread_ = null;
        }

        AVMFileDirContext.ReleaseAVMRemote();

        // Calling super.stop()  invokes     ContainerBase stop(), 
        //                       which calls AVMHostConfig stop(),
        //                       which calls AVMHostConfig undeployApps()
        //

        super.stop();
    }
}
