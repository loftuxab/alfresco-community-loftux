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
*  File    AVMFileDirContext.java
*
*
*  NOTE:
*       I would have preferred to derive from BaseDirContext directly, 
*       but the StandardContext only thinks that Resources are file 
*       system based (c.f.:  isFileSystemBased() ) if they derive 
*       from FileDirContext.
*
*
* CLASSPATH=$CLASSPATH:~/wcm-dev2/root/projects/3rd-party/lib/naming-resources.jar:
*     ../repository/build/dist/repository.jar
*     javac -Xlint:unchecked 
*     source/java/org/alfresco/jndi/AVMFileDirContext.java
*     source/java/org/alfresco/jndi/NamingContextBindingsEnumeration.java
*
*----------------------------------------------------------------------------*/

// A JSP request looks like this:
// 
//  ---------------------------------------------------------------
//  AVMFileDirContext:  getAttributes(): /xxx.jsp
//  AVMFileDirContext:  lookup(): /xxx.jsp
//  AVMFileDirContext:  getAttributes(): /avm.alfresco.localdomain/my_webapp/xxx.jsp
//  AVMFileDirContext:  lookup(): /WEB-INF/classes/org/apache/jasper/runtime/JspSourceDependent.class
//  AVMFileDirContext:  lookup(): /WEB-INF/classes/org/apache/jasper/runtime/HttpJspBase.class
//  AVMFileDirContext:  lookup(): /WEB-INF/classes/javax/servlet/Servlet.class
//  AVMFileDirContext:  lookup(): /WEB-INF/classes/javax/servlet/ServletRequest.class
//  AVMFileDirContext:  lookup(): /WEB-INF/classes/javax/servlet/jsp/JspFactory.class
//  AVMFileDirContext:  lookup(): /WEB-INF/classes/javax/servlet/http/HttpServletResponse.class
//  AVMFileDirContext:  lookup(): /WEB-INF/classes/javax/servlet/jsp/PageContext.class
//  AVMFileDirContext:  lookup(): /WEB-INF/classes/javax/servlet/jsp/JspWriter.class
//  ---------------------------------------------------------------
//
//
// A request for an html file looks like this:
//
//  AVMFileDirContext:  getAttributes(): /blah.html
//  AVMFileDirContext:  lookup(): /blah.html
//
//
// Oddly, Tomcat makes the "name" passed be relative in the case of WEB-INF/lib/
// so you can't just concatinate this.base + name.   For example, look at the 2nd line of the trace:
//
// AVMFileDirContext:  listBindings():  /opt/apache-tomcat-5.5.15/avm_webapps/my_webapp + /META-INF/
// AVMFileDirContext:  listBindings():  /opt/apache-tomcat-5.5.15/avm_webapps/my_webapp + WEB-INF/lib/
// AVMFileDirContext:  getAttributes(): /opt/apache-tomcat-5.5.15/avm_webapps/my_webapp + /WEB-INF/classes
//
// If you say file.getAbsolutePath(), the problem is covered up because
// the File ctor is imlemented like this:
//
//    public File(String path, String name) 
//    {
//        if (name == null) { throw new NullPointerException(); }
//        if (path != null) 
//        {
//            if (path.endsWith(separator)) { this.path = path + name; }
//            else { this.path = path + separator + name; }
//        } 
//        else { this.path = name; }
//    }
//
//  Actually, it does a bit more -- it removes duplicate consecutive '/' chars
// (but does not deal with '..').
//




package org.alfresco.jndi;
import  org.apache.naming.resources.*;

import java.io.File;
import java.util.Map;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.ResourceBundle;

import javax.naming.NameAlreadyBoundException;
import javax.naming.NamingEnumeration;
import javax.naming.Name;
import javax.naming.NamingException;
import javax.naming.OperationNotSupportedException;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.ModificationItem;
import javax.naming.directory.SearchControls;

import org.apache.naming.NamingContextEnumeration;
import org.apache.naming.NamingEntry;

import org.alfresco.repo.avm.AVMRemote;
import org.alfresco.repo.avm.AVMRemoteInputStream;
import org.alfresco.repo.avm.AVMNodeType;
import org.alfresco.service.cmr.avm.AVMNodeDescriptor;
import org.springframework.context.support.FileSystemXmlApplicationContext;


// Had to use:   new Exception("Stack trace").printStackTrace();
// and read a lot of tomcat source to figure out what was actually 
// happening.  Hopefully, I won't be guilty of the same thing! ;)

/**
 * AVM + Filesystem Directory Context implementation helper class.
 *
 * @author Jon Cox
 *
 */

public class AVMFileDirContext extends  
             org.apache.naming.resources.FileDirContext  
{
    // AVMFileDirAppBase  is used by AVMHost as a prefix
    // for the host appBase.  This makes it easy for JNDI
    // to recognize all paths (from all AVMHost-based 
    // virtual hosts) that belong to it.
    //
    // AVMHost uses the appDir:  <AVMFileDirAppBase>/<hostname>
    // For example:              /alfresco.avm/avm.alfresco.localhost
    //
    // Because of how StandardContext.getBasePath() works,
    // if the following dir isn't "absolute", then the application base
    // gets prepended (e.g.: on windows "c:/alfresco-.../virtual-tomcat")
    // Therefore, a little extra fancy footwork is in order here:
    //
    static final protected String AVMFileDirAppBase  =  
               (File.separatorChar == '/') 
               ?  "/alfresco.avm/"              // Unix
               :  "c:\\alfresco.avm\\";         // Windows


    // Force initialization order among modules
    public static final String getAVMFileDirAppBase() { return AVMFileDirAppBase; }

    // Given a call to setDocBase() with a value:
    //   /alfresco.avm/somehost/$-1$repo-1:/repo-1/alice/appBase/avm_webapps/xyz
    //
    // The avmDocBase_ == "repo-1:/repo-1/alice/appBase/avm_webapps/xyz"
    // and avmVersion_ == -1
    //
    String avmDocBase_;
    int    avmRootVersion_;


    //  Defined in super:
    private static org.apache.commons.logging.Log log =
        org.apache.commons.logging.LogFactory.getLog( AVMFileDirContext.class );

    // A single AVMRemote object is used for 
    // all queries to the AVM repository.

    static FileSystemXmlApplicationContext Context_ = null;
    static AVMRemote Service_;
    static int Service_refcount_ = 0; 


    /**
    *  Associates an AVMRemote with a storage location, 
    *  increments the reference count on the AVMRemote, and
    *  returns a completely initialized AVMRemote.
    *  This function is called by AVMHost during the init()
    *  of its lifcycle.
    *  <p>
    *  If this function is called more than once, the value
    *  of 'storage_directory' is ignored, and the previously-created
    *  AVMRemote singleton is returned instead.
    *  <p>
    *  If you just want to get a handle to the initialized 
    *  AVMRemote and *not* increment the refcount, then call
    *  call getAVMRemote() instead.   
    */
    static public synchronized 
    AVMRemote InitAVMRemote()
    { 
        if ( Service_ == null )
        {
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

            // Using the Spring framework load the application context:
            //   conf/alfresco-catalina-virtual-avm-context.xml"
            // which assocates
            //   "avmService" 
            // with
            //    org.alfresco.repo.avm.AVMRemote
            // and via its resource file:
            //   conf/alfresco-catalina-virtual-avm.properties
            // associates the property
            //   ${avm.storage}      (i.e.: the blob backing store location)
            // with the default Catalina directory:
            //   alfresco/store

            boolean done_trying = false;
            while ( ! done_trying )
            {
                try 
                {
                    Context_ = 
                        new FileSystemXmlApplicationContext(
                                     "file:"              +   // non-obvious Spring-ism!
                                     catalina_base        + 
                                     "conf/avm-remote-context.xml");

                    Service_ = (AVMRemote)Context_.getBean("avmRemote");
                    done_trying = true;
                }
                catch (org.springframework.beans.factory.BeanCreationException e)
                {
                    // When using RMI, the nested exception 
                    // is: java.rmi.ConnectException
                    // However, you might configure Spring to use
                    // some other transport besides RMI; therefore
                    // only require a java.io.IOException.

                    Throwable cause =  e.getCause();
                    
                    if ( (cause == null) ||
                         ! (cause instanceof java.io.IOException)
                       )
                    {
                        throw e;
                    }
                    log.warn("Retrying JNDI connection....");
                    try { Thread.currentThread().sleep( 5000 ); }
                    catch (Exception te) { /* ignored */ }
                }
            }
        }
        Service_refcount_ ++;

        return Service_;
    }

    /**
    *  Fetches the Spring FileSystemXmlApplicationContext associated
    *  with this virtualization server.   
    *  <p>
    *  This function assumesInitAVMRemote has been called previously.
    */
    static public FileSystemXmlApplicationContext 
    GetSpringApplicationContext()
    {
        return Context_;
    }


    /**
    *  Decrements the reference count on the AVMRemote.
    *  This method is called by an AVMHost at the end of
    *  its lifetime.  When the reference count reaches 0, 
    *  the conection to the AVMRemote is shutdown.  
    */
    static public synchronized 
    void ReleaseAVMRemote()
    {
        Service_refcount_ -- ;

        log.info("AVMFileDirContext.ReleaseAVMRemote() refcount now: " +  Service_refcount_ );

        if ( Service_refcount_ == 0 )
        {
            log.info("AVMFileDirContext.ReleaseAVMRemote() closing " +
                      "FileSystemXmlApplicationContext (refcount dropped to 0)");


            Context_.close();
            // lost in time, like tears in rain... 
            Service_ = null;
        }
    }

    /**
    *  Fetches the AVMRemote used to fetch data from the AVM repository.
    *  Requires that InitAVMRemote() has been called previously.
    */
    static public AVMRemote  getAVMRemote()  { return Service_; }

    /**
    *  When true, fetch files/dirs using AMVService to access repository;
    *  otherwise, fetch files/dirs using file system (via FileDirContext).
    */
    protected boolean use_AVMRemote_ = false;

    protected void setUseAVMRemote( boolean tf)
    { 
        use_AVMRemote_ = tf;
    }

    protected boolean getUseAVMRemote()
    { 
        return use_AVMRemote_;
    }


    /**
    *  Only AVMFileDirContext objects constructed from within the Tomcat
    *  framework need to look at the docBase to figure out whether to use
    *  the file system or AVMRemote to access resources when setDocBase()
    *  is invoked.
    */
    protected boolean infer_webresources_from_docBase_ = false;


    //  Defined in super:
    //
    //    /**
    //     * The descriptive information string for this implementation.
    //     */
    //    protected static final int BUFFER_SIZE = 2048;


    /** 
    *  Constructs an AVMFileDirContext.
    */
    public AVMFileDirContext() 
    {
        super();

        log.info("AVMFileDirContext:  AVMFileDirContext()");


        // This AVMFileDirContext corresponds to a top-level web application
        // directory; it is being constructed within the Tomcat framework,
        // and not within any method defined by AVMFileDirContext itself.
        //
        // Therefore, when setDocBase() is called on this object by Tomcat
        // later on (immediately after construction, actually), the parent 
        // dir of the docBase given will always correspond to the appDir 
        // of the owning host if this is a webapp that's fetching its contents
        // via AVMRemote.
        //
        // For example, within $CATALINA_HOME/conf/server.xml
        // the virtual host avm.alfresco.localdomain sets
        // its appBase to be avm_webapps like this:
        //
        //        <Host name              = "avm.alfresco.localdomain"
        //              className         = "org.alfresco.catalina.host.AVMHost"
        //              appBase           = "avm_webapps"
        //              unpackWARs        = "true" 
        //              autoDeploy        = "true"
        //              xmlValidation     = "false" 
        //              xmlNamespaceAware = "false">
        //        </Host>
        //
        //
        // Thus, when the AVMFileDirContext is created for "my_webapp",
        // when Tomcat calls setDocBase() the path it will provide will
        // look something like this:
        //
        //         /alfresco.avm/avm.alfresco.localhost/...
        //               ^        ^
        //               |        |   
        //               |        `--- AVM appBase also includes the hostname
        //               `---leading prefix for all AVMHost appBase dirs
        //
        // Only in cases like this do we need to infer whether to fetch files 
        // from the file system or AVMRemote by looking at the docBase path, 
        // because if a method within AVMFileDirContext creates a new 
        // AVMFileDirContext, it always calls setUseAVMRemote()
        // on the newly created subcontext explicitly.
        //
        // Thus we guarantee the invariants:
        //
        //      o  A toplevel AVMFileDirContext uses AVMRemote
        //         iff it's in an appBase named  avm_webapps;
        //         otherwise, the file system is used. 
        //
        //      o  All sub-AVMFileDirContext objects use AVMRemote
        //         iff their parent AVMFileDirContext does.
        //        
        //      o  All sub-AVMFileDirContext objects use the file system
        //         iff their parent AVMFileDirContext does.


                                                   // Quite a song and dance
                                                   // for just 1 line of code.
        infer_webresources_from_docBase_ = true;   // :)
    }


    /**
     * Builds an  AVMFileDirContext using the given environment.
     *
     * Invoked by the methods: lookup(Name name) and avm_list(...)
     * to create sub-contexts.
     */
    public AVMFileDirContext(Hashtable env) 
    {
        super(env);

        // When setDocBase() is called on AVMFileDirContext objects 
        // created using this constructor, the docBase won't be the
        // top-level webapp directory like "/opt/tomcat/webapps/my_webapp".
        //
        // Instead it will look something in a deeper directory.

        log.info("AVMFileDirContext:  AVMFileDirContext(env)");
    }


    // ----------------------------------------------------- Instance Variables


    // Defined in super
    //
    //     /**
    //      * The document base directory.
    //      */
    //     protected File base = null;
    //
    //
    //    /**
    //     * Absolute normalized filename of the base.
    //     */
    //    protected String absoluteBase = null;
    //
    //
    //    /**
    //     * Case sensitivity.
    //     */
    //    protected boolean caseSensitive = true;
    //
    //
    //    /**
    //     * Allow linking.
    //     */
    //    protected boolean allowLinking = false;
    //
    //
    // ------------------------------------------------------------- Properties


    /**
     * Set the document root.
     *
     * @param docBase The new document root
     *
     * @exception IllegalArgumentException if the specified value is not
     *  supported by this implementation
     * @exception IllegalArgumentException if this would create a
     *  malformed URL
     */
    public void setDocBase(String docBase) 
    {
        // new Exception("AVMFileDirContext setDocBase Stack trace: " + 
        //                docBase).printStackTrace();
        //
        //
        // Given  /opt/tomcat -> /opt/apache-tomcat-5.5.15/
        // Here's an example of the docBase values seen:
        //
        //      /opt/tomcat/avm_webapps/ROOT
        //      /opt/apache-tomcat-5.5.15/avm_webapps/ROOT/WEB-INF/lib
        //      /opt/tomcat/avm_webapps/jcox.alfresco
        //      /opt/apache-tomcat-5.5.15/avm_webapps/jcox.alfresco/WEB-INF/classes
        //      /opt/tomcat/avm_webapps/my_webapp
        //      /opt/apache-tomcat-5.5.15/avm_webapps/my_webapp/WEB-INF/classes
        //      /opt/tomcat/avm_webapps/tomcat-docs
        //      /opt/tomcat/avm_webapps/webdav
        //      /opt/apache-tomcat-5.5.15/server/webapps/manager
        //      /opt/apache-tomcat-5.5.15/server/webapps/host-manager

        log.info("AVMFileDirContext:  setDocBase(): " + docBase);

        // Validate the format of the proposed document root
        if (docBase == null)
        {
            throw new IllegalArgumentException(sm.getString("resources.null"));
        }

        if ( infer_webresources_from_docBase_  && 
             docBase.startsWith( AVMFileDirAppBase )
           )
        {
            log.info("AVMFileDirContext:  USING AVM for: " + docBase );

            use_AVMRemote_ = true;
        }

        if (! use_AVMRemote_ ) 
        { 
            super.setDocBase( docBase ); 
            return; 
        }

        // Because java does not let me say:  super.super
        // I cannot easily reuse code from BaseDirContext
        // therefore, I have to set this by hand here via
        // cut/paste.  Actually, this is a JVM constraint,
        // so there's no way to do it on any JVM-based
        // language (due to the way 'invokespecial' is
        // treated by the JVM spec).   Quite annoying.
        //
        // Think about this for a moment:
        //      A 3rd party product (tomcat) forces
        //      me to derive from FileDirContext, 
        //      not BaseDirContext.   I need to invoke
        //      methods of BaseDirContext from AVMFileDirContext
        //      but if I do so through the middle-level 
        //      class FileDirContext (which i cannot control)
        //      then illegal operations are performed
        //      (i.e.: file system access).   I don't have
        //      the option to refactor here because it's third
        //      party code.   Thus, the Java-oid advice 
        //      "dude...just refactor" simply isn't an option.
        //      
        //      Now because the JVM designers wanted for force
        //      a refactor down my throat, I can't re-use
        //      the super.super method, and I have a maintenence
        //      issue on top of it all.  As they say,  "bitter Java"!
        //
        //
        // Grudgingly copied from BaseDirContext: 
        //
        //    Change the document root property
        this.docBase = docBase;

        // TODO:
        //      Validate path within AVMRemote
        //
        //  using AVMRemote for:  /opt/apache-tomcat-5.5.15/avm_webapps/ROOT/WEB-INF/lib
        //  using AVMRemote for:  /opt/tomcat/avm_webapps/jcox.alfresco
        //  using AVMRemote for:  /opt/apache-tomcat-5.5.15/avm_webapps/jcox.alfresco/WEB-INF/classes
        //
        //  Example: /opt/tomcat/avm_webapps/jcox.alfresco
        //  Uses:    jcox.alfresco:/...
        // 

        log.info("AVMFileDirContext:  setDocBase() using AVMRemote for: " + docBase);

        // Given a call to setDocBase() with a value:
        //   /alfresco.avm/somehost/$-1$repo-1:/repo-1/alice/appBase/avm_webapps/xyz
        //
        // The avmDocBase_ == "repo-1:/repo-1/alice/appBase/avm_webapps/xyz"
        // and avmVersion_ == -1
        //

        int vers_head = docBase.indexOf('$',0);
        if ( vers_head < 0) 
        {
            throw new IllegalArgumentException(
                        sm.getString("fileResources.base", docBase));
        }

        int vers_tail = docBase.indexOf('$',vers_head +1);
        if ( vers_tail < 0) 
        {
            throw new IllegalArgumentException(
                        sm.getString("fileResources.base", docBase));
        }

        avmDocBase_ = docBase.substring(vers_tail +1);

        // Within the AVM, the file seperator char is '/'.
        // Therefore, on Windows, make sure that the avmDocBase_
        // is normalized to use '/'
        //
        if ( File.separatorChar != '/' )
        {
            avmDocBase_ = avmDocBase_.replace(  File.separatorChar , '/');
        }


        try 
        {
            avmRootVersion_ = Integer.parseInt( 
                docBase.substring( vers_head+1, vers_tail) 
            );
        }
        catch (Exception e ) 
        { 
            // If malformed, assume -1  (HEAD)
            // TODO:  issue a warning here?
            //
            avmRootVersion_ = -1; 
        }

        log.info("AVMFileDirContext.setDocBase avmDocBase_    : " + avmDocBase_);
        log.info("AVMFileDirContext.setDocBase avmRootVersion_: " + avmRootVersion_);


        // TODO:  verify that docBase exists, is a dir, and can be read
    }


     
    /**
    *  Allows the docBase to be set when the version and repoPath
    *  are already known  (this saves the trouble of having to 
    *  assemble a name-mangled request URI and parse the data you
    *  already have back out).
    *        
    * @param rootVersion  The version of the root repository node
    *                     mentioned in repoPath.
    *        
    * @param repoPath     A path into the AVMRemote repository of the form:
    *                     "reponame:/...path..."
    */
    void setDocBase(int rootVersion, String repoPath )
    {
        avmRootVersion_ = rootVersion;
        avmDocBase_     = repoPath;
    }

    // --------------------------------------------------------- Public Methods

    public void allocate() 
    {
        log.info("AVMFileDirContext:  allocate()");
        if ( use_AVMRemote_ ) 
        {
            // TODO: ensure we got an AVMRemote connection
        }


        super.allocate();               // a no-op for now
    }


    /**
     * Release any resources allocated for this directory context.
     */
    public void release() 
    {
        log.info("AVMFileDirContext:  release()");

        if ( use_AVMRemote_ ) 
        {
            // TODO
            // Ensure we get rid of AVMRemote connection
            // probably via synchronized refcount
        }

        // Because FileDirContext does early allocation,
        // we've got to call super.release() no matter what.
        super.release();
    }


    // -------------------------------------------------------- Context Methods


    /**
     * Retrieves the named object.
     *
     * @param name the name of the object to look up
     * @return the object bound to name
     * @exception NamingException if a naming exception is encountered
     */
    public Object lookup(String name) throws NamingException 
    {
        // Example:   /opt/apache-tomcat-5.5.15/avm_webapps/servlets-examples + /WEB-INF/classes/RequestInfoExample.class
        // where this.base was set by setDocBase()

        if (! use_AVMRemote_ ) 
        { 
            log.info("AVMFileDirContext:  lookup(): " + this.base + " + " + name);
            log.info("    AVMFileDirContext: using file system");
            return super.lookup( name ); 
        }

        String repo_path;
        if (  name.charAt(0) != '/') { repo_path = avmDocBase_ + "/" + name; }
        else                         { repo_path = avmDocBase_ + name; }

        log.info("AVMFileDirContext:  AVM lookup(): " + repo_path );

        AVMNodeDescriptor avm_node = null;

        try 
        { 
            avm_node = Service_.lookup(avmRootVersion_, repo_path); 
            if (avm_node == null)
            {
                log.info("AVMFileDirContext:  lookup() not found: " +  repo_path);
                throw new NamingException(sm.getString("resources.notFound", repo_path));
            }
        }
        catch (Exception e)
        {
            // TODO: emit message in exception e

            log.info("AVMFileDirContext:  lookup() not found: " +  repo_path);
            throw new NamingException(sm.getString("resources.notFound", repo_path));
        }

        Object result;

        if ( avm_node.isDirectory() ) 
        {
            log.info("AVMFileDirContext:  lookup creating AVMFileDirContext(env) for dir: " +  avm_node.getPath() );

            AVMFileDirContext tempContext = new AVMFileDirContext(env);
            tempContext.setUseAVMRemote( use_AVMRemote_ );
            tempContext.setDocBase( avmRootVersion_,  avm_node.getPath() );

            tempContext.setAllowLinking(getAllowLinking());
            tempContext.setCaseSensitive(isCaseSensitive());
            result = tempContext;
        } 
        else 
        {
            log.info("AVMFileDirContext:  lookup creating AVMFileResource for file: " +   avm_node.getPath() );

            // The goal here is to create the AVMFileResource 
            // using an object that will be sufficient to stream
            // the content back later.  For AVM, this should 
            // be a node descriptor, not a file.

            result = new AVMFileResource( avmRootVersion_, avm_node.getPath() );
        }

        return result;
    }


    /**
     * Unbinds the named object. Removes the terminal atomic name in name
     * from the target context--that named by all but the terminal atomic
     * part of name.
     * <p>
     * This method is idempotent. It succeeds even if the terminal atomic
     * name is not bound in the target context, but throws
     * NameNotFoundException if any of the intermediate contexts do not exist.
     *
     * @param name the name to bind; may not be empty
     * @exception NameNotFoundException if an intermediate context does not
     * exist
     * @exception NamingException if a naming exception is encountered
     */
    public void unbind(String name)
        throws NamingException 
    {
        log.info("AVMFileDirContext:  unbind(): " + name);

        if ( ! use_AVMRemote_ ) 
        { 
            log.info("    AVMFileDirContext: using file system");
            super.unbind( name ); 
            return;
        }

        String repo_path;
        if (  name.charAt(0) != '/') 
        { 
            repo_path = avmDocBase_ + "/" + name; 
        }
        else                         
        { 
            repo_path = avmDocBase_ + name; 
            name      = name.substring(1);
        }

        AVMNodeDescriptor avm_node = null;

        try 
        { 
            avm_node = Service_.lookup(avmRootVersion_, repo_path );
            if (avm_node == null)
            {
                throw new NamingException
                (sm.getString("resources.notFound", repo_path));                
            }
        }
        catch (Exception e)
        {
            // TODO:  log avm specific error 

            throw new NamingException
                (sm.getString("resources.notFound", repo_path));
        }

        if ( avmRootVersion_ != -1 )
        {
            throw new NamingException
                (sm.getString("resources.unbindFailed", repo_path));
        }

        try { Service_.removeNode( avmDocBase_ , name ); }
        catch (Exception e)
        {
            // TODO:  log avm specific error 
            throw new NamingException
                (sm.getString("resources.unbindFailed", repo_path));
        }
    }


    /**
     * Binds a new name to the object bound to an old name, and unbinds the
     * old name. Both names are relative to this context. Any attributes
     * associated with the old name become associated with the new name.
     * Intermediate contexts of the old name are not changed.
     *
     * @param oldName the name of the existing binding; may not be empty
     * @param newName the name of the new binding; may not be empty
     * @exception NameAlreadyBoundException if newName is already bound
     * @exception NamingException if a naming exception is encountered
     */
    public void rename(String oldName, String newName)
        throws NamingException 
    {

        log.info("AVMFileDirContext:  rename(): " + oldName + " " + newName);

        if ( ! use_AVMRemote_ ) 
        {
            log.info("    AVMFileDirContext: using file system");
            super.rename(oldName, newName); 
            return;
        }


        // TODO:
        //   replace all code below with something appropriate for AVMRemote
        //
        //        File file = file(oldName);
        //
        //        if (file == null)
        //            throw new NamingException
        //                (sm.getString("resources.notFound", oldName));
        //
        //        File newFile = new File(this.base, newName);
        //
        //        file.renameTo(newFile);

        throw new OperationNotSupportedException();
    }


    /**
     * Enumerates the names bound in the named context, along with the class
     * names of objects bound to them. The contents of any subcontexts are
     * not included.
     * <p>
     * If a binding is added to or removed from this context, its effect on
     * an enumeration previously returned is undefined.
     *
     * @param name the name of the context to list
     * @return an enumeration of the names and class names of the bindings in
     * this context. Each element of the enumeration is of type NameClassPair.
     * @exception NamingException if a naming exception is encountered
     */
    @SuppressWarnings("unchecked")
    public NamingEnumeration<javax.naming.NameClassPair> 
    list(String name) throws NamingException 
    {
        if ( ! use_AVMRemote_ ) 
        {
            log.info("    AVMFileDirContext: using file system for list(): " + name);

            // The following line is what makes me need to suppress "unchecked":
            return  super.list( name );
        }

        String repo_path;
        if (  name.charAt(0) != '/') { repo_path = avmDocBase_ + "/" + name; }
        else                         { repo_path = avmDocBase_ + name; }

        log.info("    AVMFileDirContext list() using AVMRemote for: " + repo_path);

        AVMNodeDescriptor avm_node = null;

        try 
        { 
            avm_node = Service_.lookup(avmRootVersion_, repo_path );
            if (avm_node == null)
            {
                throw new NamingException
                (sm.getString("resources.notFound", repo_path));
            }
        }
        catch( Exception e)
        {
            throw new NamingException
            (sm.getString("resources.notFound", repo_path));
        }

        return new NamingContextEnumeration(avm_list( avm_node, true ).iterator());
    }


    /**
     * Enumerates the names bound in the named context, along with the
     * objects bound to them. The contents of any subcontexts are not
     * included.
     * <p>
     * If a binding is added to or removed from this context, its effect on
     * an enumeration previously returned is undefined.
     *
     * @param name the name of the context to list
     * @return an enumeration of the bindings in this context.
     * Each element of the enumeration is of type Binding.
     * @exception NamingException if a naming exception is encountered
     */ 
    @SuppressWarnings("unchecked")
    public NamingEnumeration<javax.naming.Binding> 
    listBindings(String name) throws NamingException 
    {
        return listBindings(name, true);
    }


    @SuppressWarnings("unchecked")
    public NamingEnumeration<javax.naming.Binding> 
    listBindings(String name, boolean include_background) throws NamingException 
    {
        // this.base:
        //      /opt/tomcat/avm_webapps/ROOT
        //      /opt/apache-tomcat-5.5.15/avm_webapps/ROOT/WEB-INF/lib
        //      /opt/tomcat/avm_webapps/jcox.alfresco
        //      /opt/apache-tomcat-5.5.15/avm_webapps/jcox.alfresco/WEB-INF/classes
        //      /opt/tomcat/avm_webapps/my_webapp
        //      /opt/apache-tomcat-5.5.15/avm_webapps/my_webapp/WEB-INF/classes
        //      /opt/tomcat/avm_webapps/tomcat-docs
        //      /opt/tomcat/avm_webapps/webdav
        //      /opt/apache-tomcat-5.5.15/server/webapps/manager
        //      /opt/apache-tomcat-5.5.15/server/webapps/host-manager
        //
        // Irritatingly enough, name can have or lack the leading "/"
        // Hence the use by the tomcat crew of File.


        if ( ! use_AVMRemote_ ) 
        {
            log.info("AVMFileDirContext:  listBindings() file system: " + this.base + " + " + name);

            // The following line is what makes me need to suppress "unchecked":
            return super.listBindings(name);
        }

        String repo_path;
        if (  name.charAt(0) != '/') { repo_path = avmDocBase_ + "/" + name; }
        else                         { repo_path = avmDocBase_ + name; }

        AVMNodeDescriptor avm_node = null;

        log.info("AVMFileDirContext:  listBindings() AVM: " + repo_path);

        try 
        { 
            avm_node = Service_.lookup(avmRootVersion_, repo_path ); 
            if (avm_node == null)
            {
                throw new NamingException
                (sm.getString("resources.notFound", repo_path));                
            }
        }
        catch( Exception e)
        {
            throw new NamingException
                (sm.getString("resources.notFound", repo_path));
        }

        return new NamingContextBindingsEnumeration( 
                       avm_list(avm_node, include_background).iterator(),
                       this);
    }


    /**
     * Destroys the named context and removes it from the namespace. Any
     * attributes associated with the name are also removed. Intermediate
     * contexts are not destroyed.
     * <p>
     * This method is idempotent. It succeeds even if the terminal atomic
     * name is not bound in the target context, but throws
     * NameNotFoundException if any of the intermediate contexts do not exist.
     *
     * In a federated naming system, a context from one naming system may be
     * bound to a name in another. One can subsequently look up and perform
     * operations on the foreign context using a composite name. However, an
     * attempt destroy the context using this composite name will fail with
     * NotContextException, because the foreign context is not a "subcontext"
     * of the context in which it is bound. Instead, use unbind() to remove
     * the binding of the foreign context. Destroying the foreign context
     * requires that the destroySubcontext() be performed on a context from
     * the foreign context's "native" naming system.
     *
     * @param name the name of the context to be destroyed; may not be empty
     * @exception NameNotFoundException if an intermediate context does not
     * exist
     * @exception NotContextException if the name is bound but does not name
     * a context, or does not name a context of the appropriate type
     */
    public void destroySubcontext(String name) throws NamingException 
    {
        log.info("AVMFileDirContext:  destroySubcontext(): " + name );

        if ( ! use_AVMRemote_ ) 
        {
            log.info("    AVMFileDirContext: using file system");
            super.destroySubcontext(name);
            return;
        }

        unbind(name);
    }


    /**
     * Retrieves the named object, following links except for the terminal
     * atomic component of the name. If the object bound to name is not a
     * link, returns the object itself.
     *
     * @param name the name of the object to look up
     * @return the object bound to name, not following the terminal link
     * (if any).
     * @exception NamingException if a naming exception is encountered
     */
    public Object lookupLink(String name) throws NamingException 
    {
        // Note : Links are not supported
        log.info("AVMFileDirContext:  lokupLink(): " + name );

        if ( ! use_AVMRemote_ ) 
        {
            log.info("    AVMFileDirContext: using file system");
            return super.lookupLink( name );
        }

        return lookup(name);
    }


    /**
     * Retrieves the full name of this context within its own namespace.
     * <p>
     * Many naming services have a notion of a "full name" for objects in
     * their respective namespaces. For example, an LDAP entry has a
     * distinguished name, and a DNS record has a fully qualified name. This
     * method allows the client application to retrieve this name. The string
     * returned by this method is not a JNDI composite name and should not be
     * passed directly to context methods. In naming systems for which the
     * notion of full name does not make sense,
     * OperationNotSupportedException is thrown.
     *
     * @return this context's name in its own namespace; never null
     * @exception OperationNotSupportedException if the naming system does
     * not have the notion of a full name
     * @exception NamingException if a naming exception is encountered
     */
    public String getNameInNamespace() throws NamingException 
    {
        log.info("AVMFileDirContext:  getNameInNamespace()");

        if ( ! use_AVMRemote_ ) 
        {
            log.info("    AVMFileDirContext: using file system");
            return super.getNameInNamespace();
        }

        // TODO:
        //
        //   replace all code below with something appropriate for AVMRemote

        return docBase;   // NEON - fix as a part of making AVMRemote distrib.
    }


    // ----------------------------------------------------- DirContext Methods


    /**
     * Retrieves selected attributes associated with a named object.
     * See the class description regarding attribute models, attribute type
     * names, and operational attributes.
     *
     * @return the requested attributes; never null
     * @param name the name of the object from which to retrieve attributes
     * @param attrIds the identifiers of the attributes to retrieve. null
     * indicates that all attributes should be retrieved; an empty array
     * indicates that none should be retrieved
     * @exception NamingException if a naming exception is encountered
     */
    public Attributes getAttributes(String   name, 
                                    String[] attrIds
                                   ) throws  NamingException 
    {
        //  new Exception("Stack trace").printStackTrace();
        //  Example:
        //   /opt/apache-tomcat-5.5.15/avm_webapps/ROOT + /a/b/fun.html

        if ( ! use_AVMRemote_ ) 
        {
            log.info("    AVMFileDirContext.getAttributes(): using file system for: " + name);
            return super.getAttributes( name, attrIds );
        }

        // TODO:
        //
        //   replace all code below with something appropriate for AVMRemote


        String repo_path;
        if (  name.charAt(0) != '/') 
        { 
            repo_path = avmDocBase_ + "/" + name; 
        }
        else                         
        { 
            repo_path = avmDocBase_ + name; 
            name      = name.substring(1);
        }

        log.info("AVMFileDirContext: getAttributes(): " + repo_path );

        AVMNodeDescriptor avm_node = null;

        try 
        { 
            avm_node = Service_.lookup(avmRootVersion_, repo_path); 
            if (avm_node == null)
            {
                log.info("AVMFileDirContext:  lookup() not found: " +  repo_path);
                throw new NamingException(sm.getString("resources.notFound", repo_path));
            }
        }
        catch (Exception e)
        {
            // TODO: emit message in exception e

            log.info("AVMFileDirContext:  lookup() not found: " +  repo_path);
            throw new NamingException(sm.getString("resources.notFound", repo_path));
        }

        return new AVMFileResourceAttributes( avm_node, name );
    }


    /**
     * Modifies the attributes associated with a named object. The order of
     * the modifications is not specified. Where possible, the modifications
     * are performed atomically.
     *
     * @param name the name of the object whose attributes will be updated
     * @param mod_op the modification operation, one of: ADD_ATTRIBUTE,
     * REPLACE_ATTRIBUTE, REMOVE_ATTRIBUTE
     * @param attrs the attributes to be used for the modification; may not
     * be null
     * @exception AttributeModificationException if the modification cannot be
     * completed successfully
     * @exception NamingException if a naming exception is encountered
     */
    public void modifyAttributes(String name, int mod_op, Attributes attrs)
        throws NamingException {

        if ( ! use_AVMRemote_ ) 
        {
            log.info("    AVMFileDirContext: using file system");
            super.modifyAttributes( name, mod_op, attrs );
            return;
        }


        // TODO:
        //
        //   replace all code below with something appropriate for AVMRemote




    }


    /**
     * Modifies the attributes associated with a named object using an an
     * ordered list of modifications. The modifications are performed in the
     * order specified. Each modification specifies a modification operation
     * code and an attribute on which to operate. Where possible, the
     * modifications are performed atomically.
     *
     * @param name the name of the object whose attributes will be updated
     * @param mods an ordered sequence of modifications to be performed; may
     * not be null
     * @exception AttributeModificationException if the modification cannot be
     * completed successfully
     * @exception NamingException if a naming exception is encountered
     */
    public void modifyAttributes(String name, ModificationItem[] mods)
        throws NamingException {


        if ( ! use_AVMRemote_ ) 
        {
            log.info("    AVMFileDirContext: using file system");
            super.modifyAttributes( name, mods );
            return;
        }


        // TODO:
        //
        //   replace all code below with something appropriate for AVMRemote



    }


    /**
     * Binds a name to an object, along with associated attributes. If attrs
     * is null, the resulting binding will have the attributes associated
     * with obj if obj is a DirContext, and no attributes otherwise. If attrs
     * is non-null, the resulting binding will have attrs as its attributes;
     * any attributes associated with obj are ignored.
     *
     * @param name the name to bind; may not be empty
     * @param obj the object to bind; possibly null
     * @param attrs the attributes to associate with the binding
     * @exception NameAlreadyBoundException if name is already bound
     * @exception InvalidAttributesException if some "mandatory" attributes
     * of the binding are not supplied
     * @exception NamingException if a naming exception is encountered
     */
    public void bind(String name, Object obj, Attributes attrs)
        throws NamingException {

        log.info("AVMFileDirContext:  bind(): " + name );

        if ( ! use_AVMRemote_ ) 
        {
            log.info("    AVMFileDirContext: using file system");
            super.bind( name, obj, attrs );
            return;
        }

        // TODO:
        //
        //   replace all code below with something appropriate for AVMRemote


        // Note: No custom attributes allowed

        File file = new File(this.base, name);
        if (file.exists())
            throw new NameAlreadyBoundException
                (sm.getString("resources.alreadyBound", name));

        rebind(name, obj, attrs);

    }


    /**
     * Binds a name to an object, along with associated attributes,
     * overwriting any existing binding. If attrs is null and obj is a
     * DirContext, the attributes from obj are used. If attrs is null and obj
     * is not a DirContext, any existing attributes associated with the object
     * already bound in the directory remain unchanged. If attrs is non-null,
     * any existing attributes associated with the object already bound in
     * the directory are removed and attrs is associated with the named
     * object. If obj is a DirContext and attrs is non-null, the attributes
     * of obj are ignored.
     *
     * @param name the name to bind; may not be empty
     * @param obj the object to bind; possibly null
     * @param attrs the attributes to associate with the binding
     * @exception InvalidAttributesException if some "mandatory" attributes
     * of the binding are not supplied
     * @exception NamingException if a naming exception is encountered
     */
    public void rebind(String name, Object obj, Attributes attrs)
        throws NamingException {

        log.info("AVMFileDirContext:  rebind(): " + name );


        if ( ! use_AVMRemote_ ) 
        {
            log.info("    AVMFileDirContext: using file system");
            super.rebind( name, obj, attrs );
            return;
        }

        // TODO:
        //
        //   replace all code below with something appropriate for AVMRemote




        // Note: No custom attributes allowed
        // Check obj type

        File file = new File(this.base, name);

        InputStream is = null;
        if (obj instanceof Resource) {
            try {
                is = ((Resource) obj).streamContent();
            } catch (IOException e) {
            }
        } else if (obj instanceof InputStream) {
            is = (InputStream) obj;
        } else if (obj instanceof DirContext) {
            if (file.exists()) {
                if (!file.delete())
                    throw new NamingException
                        (sm.getString("resources.bindFailed", name));
            }
            if (!file.mkdir())
                throw new NamingException
                    (sm.getString("resources.bindFailed", name));
        }
        if (is == null)
            throw new NamingException
                (sm.getString("resources.bindFailed", name));

        // Open os

        try {
            FileOutputStream os = null;
            byte buffer[] = new byte[BUFFER_SIZE];
            int len = -1;
            try {
                os = new FileOutputStream(file);
                while (true) {
                    len = is.read(buffer);
                    if (len == -1)
                        break;
                    os.write(buffer, 0, len);
                }
            } finally {
                if (os != null)
                    os.close();
                is.close();
            }
        } catch (IOException e) {
            throw new NamingException
                (sm.getString("resources.bindFailed", e));
        }

    }


    /**
     * Creates and binds a new context, along with associated attributes.
     * This method creates a new subcontext with the given name, binds it in
     * the target context (that named by all but terminal atomic component of
     * the name), and associates the supplied attributes with the newly
     * created object. All intermediate and target contexts must already
     * exist. If attrs is null, this method is equivalent to
     * Context.createSubcontext().
     *
     * @param name the name of the context to create; may not be empty
     * @param attrs the attributes to associate with the newly created context
     * @return the newly created context
     * @exception NameAlreadyBoundException if the name is already bound
     * @exception InvalidAttributesException if attrs does not contain all
     * the mandatory attributes required for creation
     * @exception NamingException if a naming exception is encountered
     */
    public DirContext createSubcontext(String name, Attributes attrs)
        throws NamingException {


        log.info("AVMFileDirContext:  createSubcontext(): " + name );


        if ( ! use_AVMRemote_ ) 
        {
            log.info("    AVMFileDirContext: using file system");
            return super.createSubcontext( name, attrs );
        }


        // TODO:
        //
        //   replace all code below with something appropriate for AVMRemote







        File file = new File(this.base, name);
        if (file.exists())
            throw new NameAlreadyBoundException
                (sm.getString("resources.alreadyBound", name));
        if (!file.mkdir())
            throw new NamingException
                (sm.getString("resources.bindFailed", name));
        return (DirContext) lookup(name);

    }


    /**
     * Retrieves the schema associated with the named object. The schema
     * describes rules regarding the structure of the namespace and the
     * attributes stored within it. The schema specifies what types of
     * objects can be added to the directory and where they can be added;
     * what mandatory and optional attributes an object can have. The range
     * of support for schemas is directory-specific.
     *
     * @param name the name of the object whose schema is to be retrieved
     * @return the schema associated with the context; never null
     * @exception OperationNotSupportedException if schema not supported
     * @exception NamingException if a naming exception is encountered
     */
    public DirContext getSchema(String name)
        throws NamingException 
    {
    
        if ( ! use_AVMRemote_ ) 
        {
            log.info("    AVMFileDirContext: using file system");
            return super.getSchema( name );
        }

        // TODO:
        //
        //   replace all code below with something appropriate for AVMRemote





        throw new OperationNotSupportedException();
    }


    /**
     * Retrieves a context containing the schema objects of the named
     * object's class definitions.
     *
     * @param name the name of the object whose object class definition is to
     * be retrieved
     * @return the DirContext containing the named object's class
     * definitions; never null
     * @exception OperationNotSupportedException if schema not supported
     * @exception NamingException if a naming exception is encountered
     */
    public DirContext getSchemaClassDefinition(String name)
        throws NamingException {

        if ( ! use_AVMRemote_ ) 
        {
            log.info("    AVMFileDirContext: using file system");
            return super.getSchemaClassDefinition( name );
        }

        // TODO:
        //
        //   replace all code below with something appropriate for AVMRemote


        throw new OperationNotSupportedException();
    }


    /**
     * Searches in a single context for objects that contain a specified set
     * of attributes, and retrieves selected attributes. The search is
     * performed using the default SearchControls settings.
     *
     * @param name the name of the context to search
     * @param matchingAttributes the attributes to search for. If empty or
     * null, all objects in the target context are returned.
     * @param attributesToReturn the attributes to return. null indicates
     * that all attributes are to be returned; an empty array indicates that
     * none are to be returned.
     * @return a non-null enumeration of SearchResult objects. Each
     * SearchResult contains the attributes identified by attributesToReturn
     * and the name of the corresponding object, named relative to the
     * context named by name.
     * @exception NamingException if a naming exception is encountered
     */
    @SuppressWarnings("unchecked")
    public NamingEnumeration<javax.naming.directory.SearchResult>
    search(String     name, 
           Attributes matchingAttributes,
           String[]   attributesToReturn)
            throws    NamingException 
    {
        if ( ! use_AVMRemote_ ) 
        {
            log.info("    AVMFileDirContext: using file system");

            // The following line is what makes me need to suppress "unchecked":
            return super.search( name, matchingAttributes, attributesToReturn );
        }

        // TODO:
        //
        //   replace all code below with something appropriate for AVMRemote


        return null;
    }


    /**
     * Searches in a single context for objects that contain a specified set
     * of attributes. This method returns all the attributes of such objects.
     * It is equivalent to supplying null as the atributesToReturn parameter
     * to the method search(Name, Attributes, String[]).
     *
     * @param name the name of the context to search
     * @param matchingAttributes the attributes to search for. If empty or
     * null, all objects in the target context are returned.
     * @return a non-null enumeration of SearchResult objects. Each
     * SearchResult contains the attributes identified by attributesToReturn
     * and the name of the corresponding object, named relative to the
     * context named by name.
     * @exception NamingException if a naming exception is encountered
     */
    @SuppressWarnings("unchecked")
    public NamingEnumeration<javax.naming.directory.SearchResult>
    search(String     name, 
           Attributes matchingAttributes)
           throws     NamingException 
    {

        if ( ! use_AVMRemote_ ) 
        {
            log.info("    AVMFileDirContext: using file system");

            // The following line is what makes me need to suppress "unchecked":
            return super.search( name, matchingAttributes );
        }

        // TODO:
        //
        //   replace all code below with something appropriate for AVMRemote



        return null;
    }


    /**
     * Searches in the named context or object for entries that satisfy the
     * given search filter. Performs the search as specified by the search
     * controls.
     *
     * @param name the name of the context or object to search
     * @param filter the filter expression to use for the search; may not be
     * null
     * @param cons the search controls that control the search. If null,
     * the default search controls are used (equivalent to
     * (new SearchControls())).
     * @return an enumeration of SearchResults of the objects that satisfy
     * the filter; never null
     * @exception InvalidSearchFilterException if the search filter specified
     * is not supported or understood by the underlying directory
     * @exception InvalidSearchControlsException if the search controls
     * contain invalid settings
     * @exception NamingException if a naming exception is encountered
     */
    @SuppressWarnings("unchecked")
    public NamingEnumeration<javax.naming.directory.SearchResult>
    search(String         name, 
           String         filter,
           SearchControls cons)
           throws         NamingException 
    {
        if ( ! use_AVMRemote_ ) 
        {
            log.info("    AVMFileDirContext: using file system");

            // The following line is what makes me need to suppress "unchecked":
            return super.search( name, filter, cons );
        }

        // TODO:
        //
        //   replace all code below with something appropriate for AVMRemote




        return null;
    }


    /**
     * Searches in the named context or object for entries that satisfy the
     * given search filter. Performs the search as specified by the search
     * controls.
     *
     * @param name the name of the context or object to search
     * @param filterExpr the filter expression to use for the search.
     * The expression may contain variables of the form "{i}" where i is a
     * nonnegative integer. May not be null.
     * @param filterArgs the array of arguments to substitute for the
     * variables in filterExpr. The value of filterArgs[i] will replace each
     * occurrence of "{i}". If null, equivalent to an empty array.
     * @param cons the search controls that control the search. If null, the
     * default search controls are used (equivalent to (new SearchControls())).
     * @return an enumeration of SearchResults of the objects that satisy the
     * filter; never null
     * @exception ArrayIndexOutOfBoundsException if filterExpr contains {i}
     * expressions where i is outside the bounds of the array filterArgs
     * @exception InvalidSearchControlsException if cons contains invalid
     * settings
     * @exception InvalidSearchFilterException if filterExpr with filterArgs
     * represents an invalid search filter
     * @exception NamingException if a naming exception is encountered
     */
    @SuppressWarnings("unchecked")
    public NamingEnumeration<javax.naming.directory.SearchResult>
    search(String         name, 
           String         filterExpr,
           Object[]       filterArgs, 
           SearchControls cons)
           throws         NamingException 
    {
        if ( ! use_AVMRemote_ ) 
        {
            log.info("    AVMFileDirContext: using file system");

            // The following line is what makes me need to suppress "unchecked":
            return super.search( name, filterExpr, filterArgs, cons);
        }

        // TODO:
        //
        //   replace all code below with something appropriate for AVMRemote


        return null;
    }


    // The following override methods in BaseDirContext and do exactly
    // the same thing;  however, the proper java-generic return type
    // is used to suppress "unchecked conversion" errors.
    // Without this, the build shows a spurious warning:
    //
    //       Note: Recompile with -Xlint:unchecked for details.
    //
    // The only other alternative was to set a SuppressWarnings annotation 
    // on the entire class, which seemed wrong also.

    /**
    * Searches in the named context or object for entries that satisfy the 
    * given search filter. Performs the search as specified by the search 
    * controls.
    * 
    * @param name the name of the context or object to search
    * @param filterExpr the filter expression to use for the search. 
    * The expression may contain variables of the form "{i}" where i is a 
    * nonnegative integer. May not be null.
    * @param filterArgs the array of arguments to substitute for the 
    * variables in filterExpr. The value of filterArgs[i] will replace each 
    * occurrence of "{i}". If null, equivalent to an empty array.
    * @param cons the search controls that control the search. If null, the 
    * default search controls are used (equivalent to (new SearchControls())).
    * @return an enumeration of SearchResults of the objects that satisy the 
    * filter; never null
    * @exception ArrayIndexOutOfBoundsException if filterExpr contains {i} 
    * expressions where i is outside the bounds of the array filterArgs
    * @exception InvalidSearchControlsException if cons contains invalid 
    * settings
    * @exception InvalidSearchFilterException if filterExpr with filterArgs 
    * represents an invalid search filter
    * @exception NamingException if a naming exception is encountered
    */
    public NamingEnumeration<javax.naming.directory.SearchResult>
    search(Name           name, 
           String         filterExpr, 
           Object[]       filterArgs, 
           SearchControls cons)
           throws         NamingException 
    {
        return search(name.toString(), filterExpr, filterArgs, cons);
    }

    /**
    * Searches in the named context or object for entries that satisfy the 
    * given search filter. Performs the search as specified by the search 
    * controls.
    * 
    * @param name the name of the context or object to search
    * @param filter the filter expression to use for the search; may not be 
    * null
    * @param cons the search controls that control the search. If null, 
    * the default search controls are used (equivalent to 
    * (new SearchControls())).
    * @return an enumeration of SearchResults of the objects that satisfy 
    * the filter; never null
    * @exception InvalidSearchFilterException if the search filter specified 
    * is not supported or understood by the underlying directory
    * @exception InvalidSearchControlsException if the search controls 
    * contain invalid settings
    * @exception NamingException if a naming exception is encountered
    */
    public NamingEnumeration<javax.naming.directory.SearchResult>
    search(Name           name, 
           String         filter, 
           SearchControls cons)
           throws         NamingException 
    {
        return search(name.toString(), filter, cons);
    }

    /**
    * Searches in a single context for objects that contain a specified set 
    * of attributes. This method returns all the attributes of such objects. 
    * It is equivalent to supplying null as the atributesToReturn parameter 
    * to the method search(Name, Attributes, String[]).
    * 
    * @param name the name of the context to search
    * @param matchingAttributes the attributes to search for. If empty or 
    * null, all objects in the target context are returned.
    * @return a non-null enumeration of SearchResult objects. Each 
    * SearchResult contains the attributes identified by attributesToReturn 
    * and the name of the corresponding object, named relative to the 
    * context named by name.
    * @exception NamingException if a naming exception is encountered
    */
    public NamingEnumeration<javax.naming.directory.SearchResult>
    search(Name name, Attributes matchingAttributes) throws NamingException 
    {
        return search(name.toString(), matchingAttributes);
    }

    /**
    * Searches in a single context for objects that contain a specified set 
    * of attributes, and retrieves selected attributes. The search is 
    * performed using the default SearchControls settings.
    * 
    * @param name the name of the context to search
    * @param matchingAttributes the attributes to search for. If empty or 
    * null, all objects in the target context are returned.
    * @param attributesToReturn the attributes to return. null indicates 
    * that all attributes are to be returned; an empty array indicates that 
    * none are to be returned.
    * @return a non-null enumeration of SearchResult objects. Each 
    * SearchResult contains the attributes identified by attributesToReturn 
    * and the name of the corresponding object, named relative to the 
    * context named by name.
    * @exception NamingException if a naming exception is encountered
    */
    public NamingEnumeration<javax.naming.directory.SearchResult>
    search(Name       name, 
           Attributes matchingAttributes,
           String[]   attributesToReturn)
           throws     NamingException 
    {
        return search(name.toString(), matchingAttributes, attributesToReturn);
    }

    /**
    * Enumerates the names bound in the named context, along with the 
    * objects bound to them. The contents of any subcontexts are not 
    * included.
    * <p>
    * If a binding is added to or removed from this context, its effect on 
    * an enumeration previously returned is undefined.
    * 
    * @param name the name of the context to list
    * @return an enumeration of the bindings in this context. 
    * Each element of the enumeration is of type Binding.
    * @exception NamingException if a naming exception is encountered
    */
    public NamingEnumeration<javax.naming.Binding>
    listBindings(Name name) throws NamingException 
    {
        return listBindings(name.toString());
    }

    /**
    * Enumerates the names bound in the named context, along with the class 
    * names of objects bound to them. The contents of any subcontexts are 
    * not included.
    * <p>
    * If a binding is added to or removed from this context, its effect on 
    * an enumeration previously returned is undefined.
    * 
    * @param name the name of the context to list
    * @return an enumeration of the names and class names of the bindings in 
    * this context. Each element of the enumeration is of type NameClassPair.
    * @exception NamingException if a naming exception is encountered
    */
    public NamingEnumeration<javax.naming.NameClassPair>
    list(Name name) throws NamingException 
    {
        return list(name.toString());
    }


    /**
     * List the resources which are members of a collection
     * (not a part of BaseDirContext).
     *
     * @return Vector containg NamingEntry objects
     */
    protected ArrayList<NamingEntry> 
    avm_list( AVMNodeDescriptor avm_node,
              boolean           include_background
            )
    {
        // Called by:
        //      listBindings(String name)
        //      list(String name)

        ArrayList<NamingEntry> entries = new ArrayList<NamingEntry>();

        if (! avm_node.isDirectory()) { return entries; }

        Map<String, AVMNodeDescriptor> avm_entries = null;

        try 
        { 
            if ( include_background )
            {
                avm_entries = Service_.getDirectoryListing( avm_node ); 
            }
            else
            {
                // Only fetch directly contained (foreground) objects
                String fg_path =  avm_node.getPath();

                log.info("AVMFileDirContext getDirectoryListingDirect: " + fg_path);

                avm_entries = Service_.getDirectoryListingDirect( 
                                  avmRootVersion_, fg_path);
            }
        }
        catch (Exception e)
        {
            // TODO - emit message?
            return entries;
        }

        NamingEntry entry = null;

        for ( Map.Entry<String, AVMNodeDescriptor> avm_entry :
              avm_entries.entrySet()
            )
        {
            String            child_name = avm_entry.getKey();
            AVMNodeDescriptor child_node = avm_entry.getValue();
            Object            object     = null;

            if ( child_node.isDirectory() )
            {
                AVMFileDirContext tempContext = new AVMFileDirContext(env);
                tempContext.setDocBase( avmRootVersion_, child_node.getPath() );
                tempContext.setAllowLinking(getAllowLinking());
                tempContext.setCaseSensitive(isCaseSensitive());
                object = tempContext;
            }
            else
            {
                object = new AVMFileResource( avmRootVersion_, 
                                              child_node.getPath()
                                            );
            }

            entry = new NamingEntry( child_name, object, NamingEntry.ENTRY );
            entries.add(entry);
        }
        return entries;
    }




    /**
     * Return a File object representing the specified normalized
     * context-relative path if it exists and is readable.  Otherwise,
     * return <code>null</code>.
     *
     * @param name Normalized context-relative path (with leading '/')
     */
    protected File file(String name) {

        File file = new File(this.base, name);
        if (file.exists() && file.canRead()) 
        {
            if (allowLinking) {return file; }
        	
            // Check that this file belongs to our root path
            String canPath = null;

            try                   { canPath = file.getCanonicalPath(); }
            catch (IOException e) { }

            if (canPath == null) { return null; } 

            // Bugzilla 38154: after release() the absoluteBase is null, leading to an NPE
            if (this.absoluteBase == null) { return null; }


            // Check to see if going outside of the web application root
            if (!canPath.startsWith(this.absoluteBase)) { return null; } 

            // Case sensitivity check
            if (this.caseSensitive) 
            {
                String fileAbsPath = file.getAbsolutePath();

                if (fileAbsPath.endsWith(".")) { fileAbsPath = fileAbsPath + "/"; }

                String absPath = normalize(fileAbsPath);

                if (canPath != null) { canPath = normalize(canPath);}

                if ((this.absoluteBase.length() < absPath.length()) && 
                    (this.absoluteBase.length() < canPath.length())
                   ) 
                {
                    absPath = absPath.substring(this.absoluteBase.length() + 1);
                    if ((canPath == null) || (absPath == null)) { return null;}

                    if (absPath.equals("")) { absPath = "/";}

                    canPath = canPath.substring(this.absoluteBase.length() + 1);

                    if (canPath.equals("")) { canPath = "/"; }

                    if (!canPath.equals(absPath)) { return null;}
                }
            }

        } 
        else { return null; }

        return file;

    }


    /**
     * This specialized resource attribute implementation does some lazy
     * reading (to speed up simple checks, like checking the last modified
     * date).
     */
    protected class AVMFileResourceAttributes extends ResourceAttributes 
    {
        AVMNodeDescriptor avm_node_;
        String            name_;
        String            type_;

        public AVMFileResourceAttributes(AVMNodeDescriptor avm_node, String name)
        {
            avm_node_ = avm_node;
            name_     = name;
        }

        protected boolean accessed = false;
        protected String canonicalPath = null;

        /**
        *   Fetches all the attribute IDs.
        *   Currently, this is the list:
        *  <pre>
        *     "creationdate"
        *     "getlastmodified"
        *     "displayname"
        *     "resourcetype"
        *     "getcontentlength"
        *  </pre>
        */
        @SuppressWarnings("unchecked")
        public NamingEnumeration<java.lang.String> 
        getIDs()
        {
            log.info("AVMFileResourceAttributes.getIDs()");
            return super.getIDs();
        }

        /**
        *   Returns a NamingEnumeration of type BasicAttribute,
        *   (each BasicAttribute the tuple of an attribute listed 
        *   in getIDs(), and its corresponding value).
        */
        @SuppressWarnings("unchecked")
        public NamingEnumeration<? extends javax.naming.directory.Attribute> 
        getAll()
        {
            log.info("AVMFileResourceAttributes.getAll()");
            return super.getAll();    // erasure annoyance
        }


        /**
         * Is collection.
         */
        public boolean isCollection() 
        {
            log.info("AVMFileResourceAttributes.isCollection()");
            return ( avm_node_.isDirectory() );
        }


        /**
         * Get content length.
         *
         * @return content length value
         */
        public long getContentLength() 
        {
            log.info("AVMFileResourceAttributes.getContentLength()");
            return avm_node_.getLength();
        }


        /**
         * Get creation time.
         *
         * @return creation time value
         */
        public long getCreation() 
        {
            log.info("AVMFileResourceAttributes.getCreation()");
            this.creation = avm_node_.getCreateDate();
            return this.creation;
        }


        /**
         * Get creation date.
         *
         * @return Creation date value
         */
        public Date getCreationDate() 
        {
            log.info("AVMFileResourceAttributes.getCreationDate()");
            if ( this.creationDate == null )
            {
                this.creationDate = new Date(  avm_node_.getCreateDate() );
            }
            return this.creationDate;
        }


        /**
         * Get last modified time.
         *
         * @return lastModified time value
         */
        public long getLastModified() 
        {
            log.info("AVMFileResourceAttributes.getLastModified()");
            return avm_node_.getModDate();
        }

        /**
         * Get lastModified date.
         *
         * @return LastModified date value
         */
        public Date getLastModifiedDate() 
        {
            log.info("AVMFileResourceAttributes.getLastModifiedDate()");

            if ( this.lastModifiedDate == null )
            {
                this.lastModifiedDate = new Date( avm_node_.getModDate() );
            }
            return this.lastModifiedDate;
        }


        /**
         * Get name.
         *
         * @return Name value
         */
        public String getName() { return name_; }

        /**
         * Get resource type.
         *
         * @return String resource type
         */
        public String getResourceType() 
        {
            // TODO: I think I'm free to use any typenames at all,
            //       but that needs to be verified. 
            //

            if ( type_ == null )
            {
                switch ( avm_node_.getType() )
                {
                    case AVMNodeType.PLAIN_FILE:
                        type_ = "PLAIN_FILE";
                        break;

                    case AVMNodeType.PLAIN_DIRECTORY:
                        type_ = "PLAIN_DIRECTORY";
                        break;

                    case AVMNodeType.LAYERED_FILE:
                        type_ = "LAYERED_FILE";
                        break;

                    case AVMNodeType.LAYERED_DIRECTORY:
                        type_ = "LAYERED_DIRECTORY";
                        break;

                    default:
                        // TODO: I'm not thrilled with this way of handling 
                        //       unknown types, but it's what the base class 
                        //       implementation does.  Perhaps there's a reason
                        //       why, so until I dig deeper, just mimic it:
                        type_ = "";
                }
            }
            return type_;
        }
        
        /**
         * Get canonical path.
         * 
         * @return String the file's canonical path
         */
        public String getCanonicalPath() 
        {
            log.info("AVMFileResourceAttributes.getCannonicalPath(): " + avm_node_.getPath());

            // TODO:  should this include name mangling for version numbers, 
            //        or would that mess things up elsewhere?   

            return avm_node_.getPath();
        }
    }

    /**
    * This specialized resource implementation avoids opening the IputStream
    * to the file right away (which would put a lock on the file).
    */
    protected class AVMFileResource extends Resource 
    {
        /** File length.  */
        protected long length = -1L;

        protected int    root_version_;
        protected String resource_path_;


        public AVMFileResource( int root_version, String resource_path )
        {
            root_version_  = root_version;
            resource_path_ = resource_path;
        }


        /**
         * Content accessor.
         *
         * @return InputStream
         */
        public InputStream streamContent() throws IOException 
        {
            // TODO:
            //    During the create of AVMFileResource, you give it
            //    all the data it will ever need to be able to 
            //    streamContent() back later on.  Here, a file
            //    is being used, but for AVMRemote, it should
            //    be an node descriptor..

            return new AVMRemoteInputStream(AVMFileDirContext.Service_.getInputHandle(root_version_,
                                                                                      resource_path_),
                                            AVMFileDirContext.Service_);
            /*
            return AVMFileDirContext.Service_.getFileInputStream(
                       root_version_,  
                       resource_path_ 
                  );
            */
        }
    }
}

