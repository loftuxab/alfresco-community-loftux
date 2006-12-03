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
*  File    AVMHostConfig.java
*----------------------------------------------------------------------------*/

package org.alfresco.catalina.host;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.alfresco.catalina.context.AVMStandardContext;
import org.alfresco.catalina.loader.AVMWebappLoader;
import org.alfresco.jndi.AVMFileDirContext;
import org.alfresco.repo.avm.AVMNodeType;
import org.alfresco.repo.domain.PropertyValue;
import org.alfresco.service.cmr.avm.AVMNodeDescriptor;
import org.alfresco.service.cmr.avm.AVMStoreDescriptor;
import org.alfresco.service.cmr.avm.LayeringDescriptor;
import org.alfresco.service.cmr.remote.AVMRemote;
import org.alfresco.service.namespace.QName;
import org.apache.catalina.Container;
import org.apache.catalina.Context;
import org.apache.catalina.core.ContainerBase;
import org.apache.catalina.Lifecycle;
import org.apache.catalina.LifecycleListener;
import org.apache.catalina.startup.Constants;
import org.apache.catalina.startup.ExpandWar;
import org.apache.catalina.startup.HostConfig;

/**
*  Configures an {@link AVMHost} at startup time.<br>
*  Note: applications never use AVMHostConfig directly.
*  <p>
*  Alfresco's super repository (i.e.: container for virtual repositories)
*  is organized so that each {@link AVMHost}-based virtual host within Catalina
*  can have its own <tt>appBase</tt>.   For example, suppose we had only one 
*  {@link AVMHost}-based Catalina virtual host, and its <tt>appBase</tt> 
*  is "avm_webapps":
*
*  <pre>
*      &lt;Host name              = "avm.alfresco.localhost"
*            className         = "org.alfresco.catalina.host.AVMHost"
*            <strong>appBase           = "avm_webapps"</strong>
*            unpackWARs        = "true"
*            autoDeploy        = "true"
*            xmlValidation     = "false"
*            xmlNamespaceAware = "false"&gt;
*      &lt;/Host&gt;
*  </pre>
*
*  Suppose we had a set of virtual repositories within the 
*  AVM "super repository", and that it contained data from
*  two users, "alice" and "bob"
*  (note: the '<tt><~~~</tt>' symbol denotes a 
*  <a href='http://wiki.alfresco.com/wiki/Transparent_Layers'>
*  transparent layer</a> in the 
*  <a href='http://wiki.alfresco.com/wiki/Versioned_Directories'>
*  versioned directory</a> system supported by the AVM repository):
*
*  <pre>
*
*         The Super Repository:    A forest of DAG structures
*         ---------------------------------------------------
*
*              repo1:      repo2:          repo3:       repo4:
*                /           /               /            /
*                |           |               |            |
*            appBase <~~~ appBase         appBase <~~~ appBase
*                |                           |
*            <strong>avm_webapps                avm_webapps
*             /  |  \                    /  |  \
*      my_webapp    ROOT          my_webapp     ROOT
*           |                          |
*       moo.txt                    moo.txt</strong>
*
*  </pre>
*
*  At startup time, the AVMHostConfig will create a "virtualized"
*  version of <tt>avm_webapps</tt> by name-mangling the
*  webapps within each repository containing an <tt>avm_webapps</tt>
*  directory in the appropriate location.  
*  <p>
*  If we were to have multiple AVMHost-based virtual hosts
*  we might see repository data like this:
*
*  <pre>
*                 repo1:                              repo2:
*                    /                                   /
*                    |                                   |
*                 appBase                             appBase
*                /       \                           /       \
*        avm_webapps     avm2_webapps         avm_webapps   avm2_webapps 
*         /   |            /   |              /    |          /   |    \
*   my_webapp ROOT       xyz  ROOT       my_webapp ROOT     xyz   ROOT 
*       |                |                 |                 |            
*   moo.txt          cow.txt             moo.txt         cow.txt           
*
*  </pre>
*
*  The AVMHostConfig would only load data from the <tt>appBase</tt> of its 
*  owning AVMHost (e.g.: either the <tt>avm_webapps</tt> or <tt>avm2_webapps</tt>
*  trees of alice & bob).
*  <p>
*  This is configurable in $CATALINA_HOME/conf/server.xml.<br>
*  Note that avm.localhost  only loads <tt>avm_webapps</tt> trees,<br>
*  and  that avm2.localhost only loads <tt>avm2_webapps</tt> trees.
* 
*  <pre>
*      &lt;!-- Default AVMHost --&gt;
*      &lt;Host name                = "avm.localhost"
*            className           = "org.alfresco.catalina.host.AVMHost"
*            <strong>appBase             = "avm_webapps"</strong>
*            unpackWARs          = "true"
*            autoDeploy          = "true"
*            xmlValidation       = "false"
*            xmlNamespaceAware   = "false"&gt;
*      &lt;/Host&gt;
*    
*      &lt;!-- Another AVMHost --&gt;
*      &lt;Host name                = "avm2.localhost"
*            className           = "org.alfresco.catalina.host.AVMHost"
*            reverseProxyBinding = "^www2-([^.]+)(?:\\.v-([\\d]+))?\\.avm\\..*"
*            <strong>appBase             = "avm2_webapps"</strong>
*            unpackWARs          = "true"
*            autoDeploy          = "true"
*            xmlValidation       = "false"
*            xmlNamespaceAware   = "false"&gt;
*      &lt;/Host&gt;
*  </pre>
*
*  Because the {@link AVMHost} named <tt>avm2.localhost</tt>,
*  has an <tt>appBase</tt> of <tt>avm2_webapps</tt>,
*  AVMHostConfig creates a virtualized composite 
*  application base for it out of the directories shown in bold (see below):
*
*  <pre>
*
*                  repo1                               repo2:
*                    /                                   /
*                    |                                   |
*                 appBase                             appBase
*                /       \                           /       \
*        avm_webapps     <strong>avm2_webapps</strong>         avm_webapps   <strong>avm2_webapps</strong>
*         /   |    |       /   |              /    |          /   |    \
*   my_webapp ROOT       <strong>xyz  ROOT</strong>       my_webapp ROOT     <strong>xyz   ROOT</strong>
*       |                |                 |                 |            
*   moo.txt          <strong>cow.txt</strong>             moo.txt         <strong>cow.txt</strong>
*
*  </pre>
*
* Thus we have the following mappings from requests to repositories: 
*  <dl>
*       <td><tt>http://www-repo-1.avm.localhost:8080/my_webapp/moo.txt</tt>
*       <dd>Serviced by <tt>avm.localhost</tt> via <tt>repo-1</tt>.</dd>
*
*       <td><br><tt>http://www-repo-2.avm.localhost:8080/my_webapp/moo.txt</tt>
*       <dd>Serviced by <tt>avm.localhost</tt> via <tt>repo-2</tt>.</dd>
*
*       <td><br><tt>http://www2-repo-1.avm.localhost:8080/xyz/cow.txt</tt>
*       <dd>Serviced by <tt>avm2.localhost</tt> via <tt>repo-1</tt>.</dd>
*
*       <td><br><tt>http://www2-repo-2.avm.localhost:8080/xyz/cow.txt</tt>
*       <dd>Serviced by <tt>avm2.localhost</tt> via <tt>repo-2</tt>.</dd>
*  </dl>
*/
public class AVMHostConfig extends HostConfig
{
    protected static org.apache.commons.logging.Log log=
              org.apache.commons.logging.LogFactory.getLog( AVMHostConfig.class );

    /** 
    *  @exclude 
    *
    *  Store association between webapp and the classloader it used.
    */
    protected Hashtable<String, ClassLoader> context_classloader_registry_ =
          new Hashtable<String, ClassLoader>();


    // Because deployApps() requires that the AVMRemote has been initialized,
    // force the AVMFileDirContext class to be loaded by calling a cheap 
    // static method.  This will ensure that static init for the AVMRemote 
    // within AVMFileDirContext runs prior to the deployApps() callback.

    /**
    *  @exclude (hide from javadoc)
    */
    static protected AVMRemote AVMRemote_ = 
           AVMFileDirContext.getAVMRemote();


    //
    // Here's where the virual Host's relative appBase parameter
    // is stored for this AVMHost.

    String AVMHostRelativeAppBase_ = "avm_webapps";   // Default value

    /**
    * @exclude
    *
    * The Java class name of the Context implementation we should use.
    */
    protected String contextClass = "org.alfresco.catalina.context.AVMStandardContext";




    /**
    *  Creates an object that initializes an AVMHost  
    *        
    * @param AVMHostRelativeAppBase  When an AVMHost is created in 
    *                               $CATALINA_HOME/conf/server.xml,
    *                                the value for AVMHostRelativeAppBase
    *                                is taken from the appBase attribute
    *                                of the &lt;Host&gt; tag (e.g.: "avm_webapps").
    *                                Note: even if the value provied within server.xml
    *                                is an absolute path, it will be coerced into
    *                                a relative path.
    *  <pre>
    *
    *   &lt;Host name="avm.localhost"
    *         className         ="org.alfresco.catalina.host.AVMHost"
    *         appBase           ="avm_webapps"
    *         unpackWARs        ="true" 
    *         autoDeploy        ="true"
    *         xmlValidation     ="false" 
    *         xmlNamespaceAware ="false"&gt;
    *   &lt;/Host&gt;
    *  </pre>
    */
    //-------------------------------------------------------------------------
    public AVMHostConfig(String AVMHostRelativeAppBase )
    {
        super();

        log.debug("AVMHostConfig initial AVMHostRelativeAppBase: " + AVMHostRelativeAppBase);

        if (AVMHostRelativeAppBase == null )
        {
            AVMHostRelativeAppBase = "avm_webapps";
        }
        if ( AVMHostRelativeAppBase.startsWith("/") )
        {
            AVMHostRelativeAppBase = AVMHostRelativeAppBase.substring(1);
        }
        if ( AVMHostRelativeAppBase.equals("") )
        {
            AVMHostRelativeAppBase = "avm_webapps";
        }

        AVMHostRelativeAppBase_ = AVMHostRelativeAppBase;

        log.debug("AVMHostConfig initial AVMHostRelativeAppBase_: " + AVMHostRelativeAppBase_);
    }


    public void start()
    {

        // deployApps() is called by start() in HostConfig
        super.start();
    }

    public void stop()
    {
        // undeployApps() is called by stop() in HostConfig
        super.stop();

        context_classloader_registry_.clear();
    }



    protected void undeployApps() 
    {
        super.undeployApps();

        // RESUME HERE
        
    }

    /**
     * Deploy applications for any directories or WAR files that are found
     * in our "application root" directory.
     */
    protected void deployApps() 
    {
        // Example appBase:
        //    /opt/apache-tomcat-5.5.15/avm_webapps
        File appBase = appBase();   

        // Example configBase: 
        //    /opt/apache-tomcat-5.5.15/conf/Catalina/avm.localhost

        File configBase = configBase();

        // Deploy XML descriptors from configBase
        deployDescriptors(configBase, configBase.list());

        // Deploy WARs, and loop if additional descriptors are found
        // deployWARs(appBase, appBase.list());

        deployAllAVMwebappsInRepository();
    }


    protected void deployAllAVMwebappsInRepository()
    {
        HashMap<String, AVMWebappDescriptor> webapp_descriptors =  
                   new HashMap<String, AVMWebappDescriptor>();

        List<AVMStoreDescriptor> repositories = AVMRemote_.getAVMStores();
        LinkedList<String>     avm_webapp_paths = new LinkedList<String>();

        try 
        {
            Map<String, Map<QName, PropertyValue>> repo_dns_entries = 
                AVMRemote_.queryStoresPropertyKey(
                    QName.createQName(null,".dns.%"));

            for ( Map.Entry<String, Map<QName, PropertyValue>> repo_dns_entry  :
                  repo_dns_entries.entrySet() 
                )
            {
                String  repo_name  = repo_dns_entry.getKey();

                Map.Entry<QName, PropertyValue> dns_map = 
                    repo_dns_entry.getValue().entrySet().iterator().next();

                // String dns_name      = dns_map.getKey().getLocalName();
                String    dns_repo_path = dns_map.getValue().getStringValue();

                //  Example values:
                //    repo_name     =  "repo-2"
                //    dns_name      =  ".dns.bob"
                //    dns_repo_path =  "repo-2:/appBase/avm_webapps"

                if (  (dns_repo_path == null) || 
                     ! dns_repo_path.endsWith( AVMHostRelativeAppBase_ ) )
                {
                    log.debug("DNS mount point " + dns_repo_path + 
                               " does not end with: "      + 
                               AVMHostRelativeAppBase_     + 
                               " ...skipping on this host.");
                    continue;
                }

                Map<String, AVMNodeDescriptor> webapp_entries = null;
                try 
                {
                    // e.g.:   -1, "repo-3:/appBase/avm_webapps"
                    webapp_entries = 
                       AVMRemote_.getDirectoryListing(-1, dns_repo_path );
                }
                catch (Exception e)     // TODO: just AVMNotFoundException ?
                {
                    continue;
                }

                for ( Map.Entry<String, AVMNodeDescriptor> webapp_entry  : 
                      webapp_entries.entrySet()
                    )
                {
                    String webapp_name = webapp_entry.getKey();    //  my_webapp

                    log.debug("AVMHostConfig webapp: " + webapp_name); 

                    AVMWebappDescriptor webapp_desc =  
                        new AVMWebappDescriptor( 
                        -1,             // version
                        repo_name,      // repo-3
                        AVMRemote_.getIndirectionPath(-1, webapp_entry.getValue().getPath()),
                                        // this gets the indirection path even if, physically,
                                        // the path is not a layered directory, as long as the
                                        // path is in a layered context.
                        dns_repo_path,  // repo-3:/appBase/avm_webapps
                        webapp_name     // my_webapp
                    );

                    webapp_descriptors.put( webapp_desc.getContextPath(),
                                            webapp_desc);  
                }
            }
        }
        catch (Exception e)
        {
            // TODO:  think about what to do here
            log.error("deployAllAVMwebappsInRepository failed: " + 
                                e.getMessage() );
        }


        // Do topo sort of webapps according to layering config, and deploy
        deployAVMWebappsInDependencyOrder( webapp_descriptors );

        return;
    }



    //   There are 2 code paths that end up calling this function:
    //
    //   TODO: make certain that there are no race conditions in any 
    //         data structures, and that there are no datastructures
    //         that grow without bound or accumulate crud.
    //    
    //   (1)  Initial deployment:
    //           AVMHostConfig.deployAVMWebappsInDependencyOrder(AVMHostConfig.java:448)
    //           AVMHostConfig.deployAllAVMwebappsInRepository(AVMHostConfig.java:402)
    //           AVMHostConfig.deployApps(AVMHostConfig.java:324)
    //           HostConfig.start(HostConfig.java:1118)
    //
    //   (2)  The periodic check in another thread (if it's turned on):
    //           AVMHostConfig.deployAVMWebappsInDependencyOrder(AVMHostConfig.java:448)
    //           AVMHostConfig.deployAllAVMwebappsInRepository(AVMHostConfig.java:402)
    //           AVMHostConfig.deployApps(AVMHostConfig.java:324)
    //           HostConfig.check(HostConfig.java:1181)
    //
    protected void 
    deployAVMWebappsInDependencyOrder( HashMap<String, 
                                       AVMWebappDescriptor> webapp_descriptors)
    {
        // First, gather information regarding webapp dependency.
        // If webapp 'A' overlays webapp 'B', then 'A' depends on 'B'.
        //
        for ( AVMWebappDescriptor desc : webapp_descriptors.values() )
        {
            int    version   = desc.version_;
            String repo_path = desc.avm_appBase_ + "/" + desc.webapp_leafname_;

            LayeringDescriptor layer_info = 
                AVMRemote_.getLayeringInfo(version, repo_path );

            String native_repo = layer_info.getNativeAVMStore().getName();
         
            if ( ! native_repo.equals( desc.repo_name_ ) )
            {
                // System.out.println("AMMHostConfig background webapp: " + 
                //                    desc.webapp_leafname_ + 
                //                    " in repo: " + desc.repo_name_ + 
                //                    " has parent repo: " + native_repo );


                desc.setParentRepo( webapp_descriptors, native_repo );
            }
            else if ( desc.indirection_name_ != null )
            {
                // While this webapp dir is not itself a background
                // object, it's shadowing something in another layer.
                // If that other layer is in another repo, it's an
                // inter-webapp dependency.
                
                int index = desc.indirection_name_.indexOf(':');
                if ( index > 0 ) 
                {
                    String parent_repo = desc.indirection_name_.substring(0,index);
                    if ( ! parent_repo.equals( desc.repo_name_ ) )
                    {
                        // new Exception("debug stack trace").printStackTrace();
                        //
                        // System.out.println("AMMHostConfig overlay webapp: " + 
                        //                   desc.webapp_leafname_ + 
                        //                   " in repo: " + desc.repo_name_ + 
                        //                   " has parent repo: " + parent_repo);

                        desc.setParentRepo( webapp_descriptors, parent_repo);
                    }
                }
            }
        }

        // Now each descriptor has a (possibly empty) set of children
        // that depend upon it, and a (possibly null) getParentContextPath()
        // indicating the context path corresponding to the webapp that 
        // that it depends on.

        for ( AVMWebappDescriptor desc : webapp_descriptors.values() )
        {
            if ( desc.getParentContextPath() != null ) { continue; }
        
            // This is a webapp with no dependencies on any other.
            // For example, a baseline "staging" webapp.
            //
            // The following function will call each
            // non-dependent webapp, and deploy its set
            // of dependent webapps recursively.
            //
            // Therefore, webapps can register their classloader
            // with the Host, so that dependents can look it up
            // without requring any forward refs.

            deployAVMWebappDescriptorTree( desc );
        }
    }

    protected void 
    deployAVMWebappDescriptorTree( AVMWebappDescriptor desc)
    {
        deployAVMwebapp( desc.version_,
                         desc.repo_name_,
                         desc.avm_appBase_,
                         desc.webapp_leafname_,
                         desc.getContextPath(),
                         desc.getParentContextPath()
                       );

        for ( AVMWebappDescriptor dependent :  desc.dependents_)
        {
            deployAVMWebappDescriptorTree( dependent );
        }
    }


    /**
    * Deploy directories.
    */
    protected void deployAVMwebapp( int     version,
                                    String  repo_name,
                                    String  avm_appBase,
                                    String  webapp_leafname,
                                    String  context_path,
                                    String  parent_context_path
                                  )
    {
        // Examle params:
        //     version:         -1
        //     repo_name:       repo-3
        //     avm_appBase:     repo-3:/appBase/avm_webapps
        //     webapp_leafname  my_webapp
        //     context_path     /$-1$repo-3$my_webapp


        Map<String, AVMNodeDescriptor> webapp_entries = null;

        try 
        {
            webapp_entries = AVMRemote_.getDirectoryListing( 
                                -1, 
                                avm_appBase + "/" + webapp_leafname );
        }
        catch (Exception e)
        {
            return;
        }

        
        for ( Map.Entry<String, AVMNodeDescriptor> entry  : 
              webapp_entries.entrySet() 
            )
        {
            String entry_name = entry.getKey();   // leaf dirname in webapp

            if (entry_name.equalsIgnoreCase("META-INF")) { continue; }
            if (entry_name.equalsIgnoreCase("WEB-INF"))  { continue; }

            AVMNodeDescriptor entry_value = entry.getValue();

            if  ( entry_value.isDirectory() )
            {
                if (isServiced(context_path)) { continue; }

                deployAVMdirectory(version,
                                   avm_appBase,
                                   webapp_leafname,
                                   context_path,
                                   parent_context_path);
            }
        }
    }

    /**
     * Deploys AVM directory.  Requires that any directory 
     * that this dir depends on has already been deployed.
     */
    @SuppressWarnings("unchecked")
    protected void deployAVMdirectory(
       int    version,              // -1
       String avmAppBase,           // repo-3:/appBase/avm_webapps
       String webapp_leafname,      // my_webapp
       String contextPath,          // e.g.:   /$-1$repo-3$my_webapp
       String parent_context_path)  // possibly null
    {
        // repo-3:/appBase/avm_webapps/my_webapp
        String webapp_fullpath = avmAppBase + "/" + webapp_leafname;

        // Example params:
        //   version:         -1 
        //   webapp_fullpath: repo-3:/appBase/avm_webapps/my_webapps
        //   webapp_leafname: my_webapps
        //   contextPath      /$-1$repo-3$my_webapp
        
        // Don't deploy something that's already deployed
        if (deploymentExists(contextPath)) 
        { 
            return; 
        }

        AVMDeployedApplication deployedApp = 
                new AVMDeployedApplication(contextPath, avmAppBase);

        // Deploy the application in this directory
        if( log.isDebugEnabled() ) 
        {
            log.debug(sm.getString("hostConfig.deployDir", webapp_leafname));
        }

        try 
        {
            // The host is about to get an AVMStandardContext object 
            // (representing a directory/webapp) as a "child":
            //
            //
            //                       Container
            //                           |
            //                     ContainerBase  
            //                    /             \
            //   AVMStandardContext         StandardHost   
            //        (a webapp)                |
            //                               AVMHost  
            //                         (a webapp container)
            //
            //
            // The AVMStandardContext object "context" corresponds to a
            // <Context> in Tomcat's configuration files.  In Tomcat 5.5:
            // http://tomcat.apache.org/tomcat-5.5-doc/config/context.html
            //
            //  AVMHost contains AVMStandardContext "children" webapps
            //  (the Context element represents a web application).
            //  Thus, think of a host as a "webapp container".


            // contextClass is:  
            //         "org.alfresco.catalina.context.AVMStandardContext"
            //    (was "org.apache.catalina.core.StandardContext")
            //  Context context = (Context) Class.forName(contextClass).newInstance();
            //
            // Just instantiate directly:
            //
            AVMStandardContext context = new AVMStandardContext();


            if (context instanceof Lifecycle)           // yes, it's a Lifecycle
            {
                //  By default, getConfigClass() returns:
                //         "org.apache.catalina.startup.ContextConfig"

                Class clazz = Class.forName(host.getConfigClass());

                LifecycleListener listener = 
                        (LifecycleListener) clazz.newInstance();

                ((Lifecycle) context).addLifecycleListener(listener);
            }

            // Within a <Context> (i.e.: web application), the class
            // that accesses static resources can be set via <Resources>.
            //
            // For example, in $TOMCAT_HOME/context.xml you could 
            // (but should not) say this:
            //
            //    <Context>
            //        <WatchedResource>WEB-INF/web.xml</WatchedResource>
            //        <Resources className="org.alfresco.jndi.AVMFileDirContext"/>
            //    </Context>
            //
            // Such a configuration would force all webapps in all virtual hosts
            // to fetch resources via AVMFileDirContext (rather than the default
            // FileDirContext).  This is too invasive because even though 
            // AVMFileDirContext can act like a wrapper for FileDirContext,
            // someone might want to have different <Resources> for their 
            // own custom host types.
            //
            // Instead, just make all webapps in AVM-based virtual hosts 
            // fetch their resources from AVMFileDirContext:    
            //
            // Calling context.setResources(...) must be done prior to
            // context.start(), so this is as good a place as any: 
            //
            context.setResources( new AVMFileDirContext() );

            // The parent_cl of "host" is the "Shared" classloader:
            //
            //                Bootstrap
            //                      |
            //                   System
            //                      |
            //                   Common
            //                  /      \
            //             Catalina   Shared
            //                         /   \
            //                    Webapp1  Webapp2

            ClassLoader parent_cl = host.getParentClassLoader();  


            AVMWebappLoader webappLoader = 
                new AVMWebappLoader( parent_cl, 
                                     context_classloader_registry_,
                                     contextPath,
                                     parent_context_path
                                   );

            webappLoader.setDelegate( false );  // false == check local 1st

            // Set custom loader
            //   This ultimately calls down to StandardContext.setLoader
            //   which calls stop() on the  webappLoader if necessary,
            //   then calls  start() on it.  Within start(), webappLoader
            //   will create its class loader.
            //
            context.setLoader(webappLoader);

            context.setPath(contextPath);  // e.g.: /$-1$repo-3$my_webapp


            // Example of webapp_fullpath:
            //      "repo-3:/appBase/avm_webapps/my_webapp"

            context.setDocBase( "$" + version + "$" + webapp_fullpath );

            // Make Constants.ApplicationContextXml == "META-INF/context.xml";
            // on all platforms, because we're reaching into AVM, not native
            // file system.
            //
            // Example configFile: 
            //   "repo-3:/appBase/avm_webapps/my_webapp" + 
            //          "/META-INF/context.xml"
            //
            String configFile = webapp_fullpath + "/" + "META-INF/context.xml";

            if (deployXML) 
            {
                context.setConfigFile(configFile);
            }

            // The next line starts the webapp.
            //
            //     host.addChild(context)  calls context.start() inside 
            //     ContainerBase, the grandfather class of AVMHost.
            //
            host.addChild(context);

            AVMNodeDescriptor desc          = null;
            Long              last_modified = null;

            try 
            {
                desc = AVMRemote_.lookup( version, webapp_fullpath );
                if (desc != null)
                {
                    last_modified = new Long( desc.getModDate() );
                }
                else
                {
                    last_modified = new Long( 0L );
                }
            }
            catch (Exception e)
            {
                last_modified = new Long( 0L );
            }

            // put() forces SuppressWarnings, due to map def in base class.
            deployedApp.redeployResources.put( webapp_fullpath, last_modified );


            if (deployXML) 
            {
                try 
                {
                    desc = AVMRemote_.lookup( version, configFile );
                    if (desc != null)
                    {
                        last_modified = new Long( desc.getModDate() );
                    }
                    else
                    {
                        last_modified = new Long( 0L );
                    }
                }
                catch (Exception e)
                {
                    last_modified = new Long( 0L );
                }

                // put() forces SuppressWarnings, due to map def in base class.
                deployedApp.redeployResources.put(
                    configFile,
                    last_modified
                );
            }

            addWatchedResources(deployedApp, 
                                webapp_fullpath,     
                                context);
        } 
        catch (Throwable t) 
        {
            log.error(sm.getString("hostConfig.deployDir.error", webapp_leafname), t);
        }


        //  Prevent app from being deployed on top of itself

        this.deployed.put(contextPath, deployedApp);
    }


    @SuppressWarnings("unchecked")
    protected void addWatchedResources( AVMDeployedApplication app, 
                                        String                 webapp_fullpath,
                                        Context                context) 
    {
        // Example params:
        //    app:     contextPath,  {avm_path,timestamp},{avm_path,timestamp},...
        //    webapp_fullpath: repo-3:/appBase/avm_webapps/my_webapps

        String[] watchedResources = context.findWatchedResources();

        // A webapp might do something like this:
        //
        //  <Context reloadable="true">
        //      <WatchedResource>WEB-INF/web.xml</WatchedResource>
        //       ...
        //  </Context>



        for (int i = 0; i < watchedResources.length; i++) 
        {
            log.debug("AVMHostConfig DEBUG:  watched resource: " + watchedResources[i]);

            Long last_modified = null;

            String resource = watchedResources[i];
            if ( !resource.startsWith( webapp_fullpath ) )
            {
                log.debug("AVMHostConfig watched resource: " + resource + " does not start with: " + webapp_fullpath);

                // PORTING NOTE:
                //      The ugly hack below deals with Unix vs windows paths.
                //      There are better ways to do this.  

                if ( File.separatorChar == '/' )           // Unix
                {
                    if ( ! (resource.charAt(0) == '/') )   // Windows
                    {
                        resource = webapp_fullpath + "/" + resource;
                        log.debug("AVMHostConfig:  relative watched resource put into webapp_fullpath: " + resource);
                    }
                    else { continue; }
                }
                else
                {
                    if ( ! resource.startsWith(":\\", 1) )  // not absolute
                    {
                        // This is an AVM path.
                        // Because AVM uses '/' on all platforms,
                        // make resource absolute using '/' as separatorChar,
                        // even though this is Windows.
                        //
                        resource = webapp_fullpath + "/" + resource;

                        log.debug("AVMHostConfig:  relative watched resource put into webapp_fullpath: " + resource);
                    }
                    else { continue; }
                }
            }

            AVMNodeDescriptor resource_desc = null;
            try   
            { 
                resource_desc = AVMRemote_.lookup( -1, resource ); 
                if (resource_desc != null)
                {
                    last_modified = new Long( resource_desc.getModDate() );
                }
                else
                {
                    last_modified = new Long( 0L );
                }
            }
            catch (Exception e)  
            { 
                last_modified = new Long( 0L );
            }

            log.debug("AVMHostConfig adding watched resource: " + resource  + "  modtime:" + last_modified);

            // put() forces SuppressWarnings, due to map def in base class.
            app.reloadResources.put( resource, last_modified);
        }
    }


    @SuppressWarnings("unchecked")
    protected synchronized void checkResources(DeployedApplication app)
    {
        // TODO: NEON RESUME HERE remove after demo
        //       Just doing early return to to stop possiblity of accidental undeploy
        if ( 1 == 1 )
        {
            return;
        }

        // resource_desc
        //
        // The resources fetched within app look like this:
        //      repo-3:/appBase/avm_webapps/my_webapp
        //      repo-3:/appBase/avm_webapps/my_webapp/META-INF/context.xml
        //      ...

        new Exception("debug stack trace for checkResources").printStackTrace();

        
        String avm_appBase = "";

        if (app instanceof AVMDeployedApplication)
        {
            // 
            // AVMHostConfig checking AVMDeployedApplication: /$-1$alfreco-staging$my_webapp
            //
            log.debug("AVMHostConfig checking AVMDeployedApplication: " + 
                       ((AVMDeployedApplication)app).getName() );

            avm_appBase = ((AVMDeployedApplication)app).getAvmAppBase();
        }

        //         AVMHostConfig checkResources using appBase: alfreco-staging:/appBase/avm_webapps
        log.debug("AVMHostConfig checkResources using appBase: "  + avm_appBase);

    	// Any modification of the specified (static) resources will cause a 
    	// redeployment of the application. If any of the specified resources is
    	// removed, the application will be undeployed. Typically, this will
    	// contain resources like the context.xml file, a compressed WAR path.

        // keySet() forces SuppressWarnings, due to map def in base class.
        String[] resources = (String[]) app.redeployResources.keySet().toArray(new String[0]);

        for (int i = 0; i < resources.length; i++) 
        {
            log.debug("AVMHost config checking: " + resources[i]);

            String resource = resources[i];

            if (log.isDebugEnabled())
            {
                log.debug("Checking context[" + app.name + "] redeploy resource " + resource);
            }

            AVMNodeDescriptor resource_desc = null;

            try   
            { 
                // TODO:  This should check to see if resource is fetched from
                //        the file system or the AVM before assuming AVM;
                //        otherwise, you just keep undeploying/redeploying
                //        <VIRTUAL_TOMCAT_HOME>/conf/Catalina/avm.alfresco.localhost/host-manager.xml

                resource_desc = AVMRemote_.lookup( -1, resource ); 
            }
            catch (Exception e)  { /* nothing to do */ }


            if ( resource_desc != null )        // file or dir exists
            {

                long lastModified = 
                    ((Long) app.redeployResources.get( resource )).longValue();

                log.debug("AVMHost config check non-null resource_desc. " +
                          "Mod date: " +  resource_desc.getModDate() + 
                          "  Last mod: " + lastModified);

                if ( (!resource_desc.isDirectory()) && 
                     resource_desc.getModDate() > lastModified
                   )
                {
                   log.debug("AVMHost config check mod date > last mod, so undeploy app");


                    // Undeploy application
                    if (log.isInfoEnabled())
                    {
                        log.info(sm.getString("hostConfig.undeploy", app.name));
                    }

                    ContainerBase context = (ContainerBase) host.findChild(app.name);
                    try 
                    {
                        host.removeChild(context);
                    } 
                    catch (Throwable t) 
                    {
                        log.warn(sm.getString
                                 ("hostConfig.context.remove", app.name), t);
                    }

                    try 
                    {
                        context.destroy();
                    } 
                    catch (Throwable t) 
                    {
                        log.warn(sm.getString
                                 ("hostConfig.context.destroy", app.name), t);
                    }

                    // Delete other redeploy resources
                    for (int j = i + 1; j < resources.length; j++) 
                    {
                        //  For example:  
                        //    resources[j] == 
                        //        "repo-3:/appBase/avm_webapps/my_webapp"

                        try 
                        {
                            String current = resources[j];
                            if ((current.startsWith(  avm_appBase  )) || 
                                (current.startsWith(configBase().getAbsolutePath()))
                               ) 
                            {
                                if (log.isDebugEnabled()) { log.debug("Delete " + current); }

                                // NEON TODO:  figure out what to do here
                                // ExpandWar.delete(current);
                                log.debug("AVMHostConfig should un-deploy resource (but does not): " + current );
                            }
                        } 
                        catch (Exception e) 
                        {
                            log.warn(sm.getString
                                    ("hostConfig.canonicalizing", app.name), e);
                        }
                    }
                    deployed.remove(app.name);
                    return;
                }
            }
            else        // file or dir no longer exists!
            {
                long lastModified = 
                    ((Long) app.redeployResources.get( resource )).longValue();


                log.debug("AVMHost config check null resource_desc, so file/dir no longer exists.   Last mod: "+ lastModified);


                if (lastModified == 0L) { continue; }

                // Undeploy application
                if (log.isInfoEnabled())
                {
                    log.info(sm.getString("hostConfig.undeploy", app.name));
                }

                ContainerBase context = (ContainerBase) host.findChild(app.name);

                try 
                {
                    host.removeChild(context);
                } 
                catch (Throwable t) 
                {
                    log.warn(sm.getString
                             ("hostConfig.context.remove", app.name), t);
                }

                try 
                {
                    context.destroy();
                } 
                catch (Throwable t) 
                {
                    log.warn(sm.getString
                             ("hostConfig.context.destroy", app.name), t);
                }
                // Delete all redeploy resources
                for (int j = i + 1; j < resources.length; j++) 
                {
                    try 
                    {
                        String current = resources[j];

                        // NEON TODO: again, what do I do with configBase? 

                        if ( current.startsWith( avm_appBase  ) || 
                             current.startsWith( configBase().getAbsolutePath())
                           ) 
                        {
                            if (log.isDebugEnabled()) { log.debug("Delete " + current); }

                            // NEON TODO:  figure out what to do here
                            // ExpandWar.delete(current);
                            log.debug("AVMHostConfig should un-deploy resource (but does not): " + current );
                        }
                    } 
                    catch (Exception e) 
                    {
                        log.warn(sm.getString
                                ("hostConfig.canonicalizing", app.name), e);
                    }
                }

                // Delete reload resources as well (to remove any remaining .xml descriptor)
                // keySet() forces SuppressWarnings, due to map def in base class.
                String[] resources2 = (String[]) app.reloadResources.keySet().toArray(new String[0]);

                for (int j = 0; j < resources2.length; j++) 
                {
                    try 
                    {
                        String current = resources2[j];

                        // NEON TODO:  again, how should I handle configBase? 
                        //
                        if ( current.startsWith( avm_appBase ) 
                             || 
                             ( (current.startsWith(configBase().getAbsolutePath()) && 
                               (current.endsWith(".xml")))
                             )
                           )
                        {
                            if (log.isDebugEnabled()) { log.debug("Delete " + current); }

                            // NEON TODO:  figure out what to do here
                            // ExpandWar.delete(current);
                            log.debug("AVMHostConfig should un-deploy resource (but does not): " + current );
                        }
                    } 
                    catch (Exception e) 
                    {
                        log.warn(sm.getString
                                ("hostConfig.canonicalizing", app.name), e);
                    }
                }
                deployed.remove(app.name);
                return;
            }
        }

    	
        // Any modification of the specified (static) resources will cause 
        // a reload of the application. This will typically contain resources
        // such as the web.xml of a webapp, but can be configured to contain 
        // additional descriptors.

        // keySet() forces SuppressWarnings, due to map def in base class.
        resources = (String[]) app.reloadResources.keySet().toArray(new String[0]);

        for (int i = 0; i < resources.length; i++) 
        {
            String resource = resources[i];

            if (log.isDebugEnabled())
                log.debug("Checking context[" + app.name + "] reload resource " + resource);


            AVMNodeDescriptor resource_desc = null;
            long lastModified = 
                 ((Long) app.reloadResources.get(resource)).longValue();

            long    current_lastModified = 0L;
            boolean file_exists          = false;

            if ( ! resource.startsWith( avm_appBase ) )
            {
                // We're fetching something out of the normal file system.
                // For example:  /opt/tomcat/conf/context.xml

                File resourceFile = new File(resource);
                if ( resourceFile.exists() ) 
                {
                    current_lastModified = resourceFile.lastModified();
                    file_exists = true;
                }
            }
            else
            {
                try   
                { 
                    resource_desc        = AVMRemote_.lookup( -1, resource );
                    current_lastModified = resource_desc.getModDate();
                    file_exists          = true;
                }
                catch (Exception e)  
                { 
                    log.debug("Exception looking up: "  + resource + "  " + e.getMessage() + 
                              " ...and lastModified was: " + lastModified);
                }
            }

            log.debug("check on: " + resource                           +
                      "\n            exists:          " + file_exists   +
                      "\n            lastMod:         " + lastModified  +
                      "\n            current_lastMod: " + current_lastModified );

            if ( ( !file_exists && lastModified != 0L) || 
                 ( current_lastModified != lastModified)
               ) 
            {

                log.debug("       Reloading app: " + app.name );

                // Reload application
                if(log.isInfoEnabled())
                {
                    log.info(sm.getString("hostConfig.reload", app.name));
                }

                Container context = host.findChild(app.name);
                try 
                {
                    ((Lifecycle) context).stop();
                } 
                catch (Exception e) 
                {
                    log.warn(sm.getString
                             ("hostConfig.context.restart", app.name), e);
                }
                // If the context was not started (for example an error 
                // in web.xml) we'll still get to try to start
                try 
                {
                    ((Lifecycle) context).start();
                } 
                catch (Exception e) 
                {
                    log.warn(sm.getString
                             ("hostConfig.context.restart", app.name), e);
                }
                // Update times
                app.reloadResources.put(resource, new Long(  current_lastModified  ));
                app.timestamp = System.currentTimeMillis();
                return;
            }
        }
    }

    /**
    *  Used to do a topological sort of webapp dependency based 
    *  on transparent overlay configuration.  Sorting is necessary
    *  because you can't change a classloader's parent classloader
    *  once it has been created.
    */
    class AVMWebappDescriptor
    {                                // Example of params:
        int     version_;            // -1
        String  repo_name_;          // "repo-3"
        String  indirection_name_;   // not null iff not an overlay nor a layer
        String  avm_appBase_;        // "repo-3:/appBase/avm_webapps"
        String  webapp_leafname_;    // "my_webapp"

        String  context_path_;           // "/$-1$repo-3$my_webapp"
        String  parent_repo_name_;       // "repo-1"
        String  parent_context_path_;    // "/$-1$repo-1$my_webapp"

        // List of of webapp decriptors that are layered on top 
        List<AVMWebappDescriptor> dependents_ = 
                new LinkedList<AVMWebappDescriptor>();

        void addDependentWebappDescriptor( AVMWebappDescriptor d)
        {
            dependents_.add( d);
        }

        AVMWebappDescriptor(int     version,
                            String  repo_name,
                            String  indirection_name,
                            String  avm_appBase,
                            String  webapp_leafname)
        {
            version_          = version;
            repo_name_        = repo_name;
            indirection_name_ = indirection_name;
            avm_appBase_      = avm_appBase;
            webapp_leafname_  = webapp_leafname;
        }

        String getContextPath()
        {
            if ( context_path_ == null )
            {
                context_path_ = "/"              + 
                               "$"               +
                               version_          +
                               "$"               +
                               repo_name_        +
                               "$"               +
                               webapp_leafname_;
            }
            return context_path_;
        }

        void setParentRepo(
                HashMap<String, AVMWebappDescriptor> webapp_descriptors,
                String                               parent_repo_name)
        {
            parent_repo_name_ = parent_repo_name; 

            AVMWebappDescriptor parent_desc =
                webapp_descriptors.get( getParentContextPath() );

            parent_desc.addDependentWebappDescriptor( this );
        }

        String getParentContextPath()
        {
            if ( parent_repo_name_ == null ) { return null; }

            if ( parent_context_path_ == null )
            {
                parent_context_path_ = "/"                  + 
                                       "$"                  +
                                       version_             +
                                       "$"                  +
                                       parent_repo_name_    +
                                       "$"                  +
                                       webapp_leafname_;
            }
            return parent_context_path_;
        }
    }


    class AVMDeployedApplication extends HostConfig.DeployedApplication
    {
        String avmAppBase_;
        public AVMDeployedApplication( String name ) { super( name ); }

        public AVMDeployedApplication( String name, String avmAppBase )
        {
            super( name );
            avmAppBase_ = avmAppBase;
        }

        public String getName()                      {return this.name;}
        public String getAvmAppBase()                {return avmAppBase_;}
        public void setAvmAppBase(String avmAppBase) {avmAppBase_ = avmAppBase;}
    }
}
