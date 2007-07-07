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
*  You should have received a copy of the text describing the FLOSS exception,
*  and it is also available here:   http://www.alfresco.com/legal/licensing
*
*
*  Author  Jon Cox  <jcox@alfresco.com>
*  File    LinkValidationServiceImpl.java
*----------------------------------------------------------------------------*/

package org.alfresco.linkvalidation;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.SocketException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.Security;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Set;
import javax.net.ssl.SSLException;
import org.alfresco.config.JNDIConstants;
import org.alfresco.filter.CacheControlFilter;
import org.alfresco.mbeans.VirtServerRegistry;
import org.alfresco.repo.attributes.Attribute;
import org.alfresco.repo.attributes.BooleanAttribute;
import org.alfresco.repo.attributes.BooleanAttributeValue;
import org.alfresco.repo.attributes.IntAttribute;
import org.alfresco.repo.attributes.IntAttributeValue;
import org.alfresco.repo.attributes.MapAttribute;
import org.alfresco.repo.attributes.MapAttributeValue;
import org.alfresco.repo.attributes.StringAttribute;
import org.alfresco.repo.attributes.StringAttributeValue;
import org.alfresco.repo.avm.CreateVersionTxnListener;
import org.alfresco.repo.avm.PurgeStoreTxnListener;
import org.alfresco.repo.avm.PurgeVersionTxnListener;
import org.alfresco.repo.avm.util.UriSchemeNameMatcher;
import org.alfresco.repo.domain.PropertyValue;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.sandbox.SandboxConstants;
import org.alfresco.service.cmr.attributes.AttrAndQuery;
import org.alfresco.service.cmr.attributes.AttributeService;
import org.alfresco.service.cmr.attributes.AttrQueryGTE;
import org.alfresco.service.cmr.attributes.AttrQueryLTE;
import org.alfresco.service.cmr.avm.AVMException;
import org.alfresco.service.cmr.avm.AVMNodeDescriptor;
import org.alfresco.service.cmr.avm.AVMNotFoundException;
import org.alfresco.service.cmr.avmsync.AVMDifference;
import org.alfresco.service.cmr.avmsync.AVMSyncService;
import org.alfresco.service.cmr.remote.AVMRemote;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.MD5;
import org.alfresco.util.NameMatcher;
import org.alfresco.util.Pair;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

//-----------------------------------------------------------------------------
//-----------------------------------------------------------------------------
/**
*    Utility class to parse paths
*/
//-----------------------------------------------------------------------------
class ValidationPathParser
{
    static String App_Dir_ = "/" + JNDIConstants.DIR_DEFAULT_WWW   +
                             "/" + JNDIConstants.DIR_DEFAULT_APPBASE;

    String    store_        = null;
    String    app_base_     = null;
    String    webapp_name_  = null;
    String    dns_name_     = null;
    String    path_         = null;
    String    req_path_     = null;
    int       store_end_    = -2;
    int       webapp_start_ = -2;
    int       webapp_end_   = -2;
    AVMRemote avm_          = null;

    ValidationPathParser(AVMRemote avm, String path)
    throws               IllegalArgumentException
    {
        avm_ = avm;
        path_ = path;
    }

    String getStore()
    {
        if ( store_ != null ) { return store_ ; }
        store_end_ = path_.indexOf(':');

        if ( store_end_ < 0)
        {
            store_ = path_;
            return store_;
        }

        if ( ! path_.startsWith( App_Dir_, store_end_ + 1 )  )
        {
            throw new IllegalArgumentException("Invalid webapp path: " + path_);
        }
        else
        {
            store_ = path_.substring(0,store_end_);
        }

        return store_;
    }

    String getAppBase()
    {
        if (app_base_ != null) { return app_base_; }
        if (store_ == null )   { getStore(); }
        app_base_ = store_ + ":" + App_Dir_;
        return app_base_;
    }

    String getWebappName()
    {
        if ( webapp_name_ != null) { return webapp_name_; }
        if ( store_end_ < -1)    { getStore(); }
        if ( store_end_ < 0 )    { return null; }

        webapp_start_ =
                path_.indexOf('/', store_end_ + 1 + App_Dir_.length());

        if (webapp_start_ >= 0)
        {
            webapp_end_  = path_.indexOf('/', webapp_start_ + 1);
            webapp_name_ = path_.substring( webapp_start_ +1,
                                            (webapp_end_ < 1)
                                            ?  path_.length()
                                            :  webapp_end_ );

            if  ((webapp_name_ != null) &&
                 (webapp_name_.length() == 0)
                )
            {
                webapp_name_ = null;
            }
        }
        return webapp_name_;
    }

    /*-------------------------------------------------------------------------
    *  getRequestPath --
    *       Given an path of the form mysite:/www/avm_webapps/ROOT/m oo/bar.txt
    *       returns the non-encoded request path  ( "/mo o/bar.txt")
    *
    *       Given an path of the form mysite:/www/avm_webapps/cow/m oo/bar.txt
    *       returns the non-encoded request path  ( "/cow/mo o/bar.txt")
    *
    *------------------------------------------------------------------------*/
    String getRequestPath()
    {
        if (req_path_ != null ) { return req_path_;}

        String webapp_name = getWebappName();
        if (webapp_name == null ) { return null; }

        int req_start = -1;

        if ( webapp_name.equals("ROOT") )
        {
            req_start = webapp_start_ + "ROOT".length() + 1;
        }
        else
        {
            req_start = webapp_start_;
        }

        req_path_ =  path_.substring( req_start, path_.length() );

        if ( req_path_.equals("") ) { req_path_ = "/"; }

        return req_path_;
    }


    String getDnsName() throws AVMNotFoundException
    {
        if ( dns_name_ != null ) { return dns_name_; }
        if ( store_ == null )    { getStore() ; }

        dns_name_ = lookupStoreDNS( avm_, store_ );
        if ( dns_name_ == null )
        {
            throw new AVMNotFoundException(
                       "No DNS entry for AVM store: " + store_);
        }
        dns_name_ = dns_name_.toLowerCase();
        return dns_name_;
    }

    String lookupStoreDNS(AVMRemote avm,  String store )
    {
        Map<QName, PropertyValue> props =
                avm.queryStorePropertyKey(store,
                     QName.createQName(null, SandboxConstants.PROP_DNS + '%'));

        return ( props.size() != 1
                 ? null
                 : props.keySet().iterator().next().getLocalName().
                         substring(SandboxConstants.PROP_DNS.length())
               );
    }
}

//-----------------------------------------------------------------------------
//-----------------------------------------------------------------------------
/**
   Implementation of link validation service.

<pre>

 Before starting, create an empty url cache for this changeset update.
 This will allow us to skip certain redundant ops like status checks.

 For each file F, calulate a url U.

  [1]   Given a source file, what hrefs appear in it explicitly/implicitly:
            md5_source_to_md5_href[ md5( file ) ]  --> map { md5( url ) }
            .href/mysite/|mywebapp/-2/md5_source_to_md5_href/

  [2]   Given an href, in what source files does it appear
        explicitly or implicitly (via dead reconing):
            md5_href_to_md5_source[ md5( url ) ]  --> map { md5( file ) }
            .href/mysite/|mywebapp/-2/md5_href_to_source

  [3]   Given an href, what's its status?
            md5_href_to_status[   md5( url ) ]  --> 200/404, etc.
            .href/mysite/|mywebapp/-2/md5_href_to_status/

  [4]   Given a status, what hrefs have it?
            status_to_md5_href[   status  ]  --> map { md5( url ) }
            .href/mysite/|mywebapp/-2/status_to_md5_href/

  [5]   Given an md5, what's the filename?
            md5_to_file[          md5( file ) ]  --> String( file )
            .href/mysite/|mywebapp/-2/md5_to_file/

  [6]   Given an md5, what's the href?
            md5_to_href[          md5( url ) ]  --> String( url )
            .href/mysite/|mywebapp/-2/md5_to_href/

  [7]   Given an href what files does it depend on?
             hamd5_href_to_md5_fdep[ md5( url ) ]  --> map { md5( file ) }
            .href/mysite/|mywebapp/-2/md5_href_to_md5_fdep/

  [8]   Given a file, what hrefs depend on it
            md5_file_to_md5_hdep[ md5( file ) ]  --> map { md5( url ) }
            .href/mysite/|mywebapp/-2/file_to_hdep/

</pre>
*/
//-----------------------------------------------------------------------------
public class LinkValidationServiceImpl implements LinkValidationService,
                                                  Runnable
{
    private static Log log = LogFactory.getLog(LinkValidationServiceImpl.class);

    //------------------------------------------------------------------------
    //                         ***************
    //                         **  WARNING  **
    //                         ***************
    //
    //        Update the Schema_version_ any time the AttributeService
    //        schema used by LinkValidationServiceImpl creates changes.
    //
    //------------------------------------------------------------------------
    static private final int Schema_version_  = 1;


    // Shutdown flag for service
    private static AtomicBoolean Shutdown_ = new AtomicBoolean( false );

    static String HREF               = ".href";    // top level href key
    static String SCHEMA_VERSION     = "schema";   // vers # of  info schema
    static String BASE_VERSION       = "latest";   // vers # of  baseline
    static String BASE_VERSION_ALIAS = "-2";       // alias  for baseline
    static String UPDATE_VERSION     = "update";   // vers # of  update

    static String SOURCE_TO_HREF     = "source_to_href";  // key->map
    static String HREF_TO_SOURCE     = "href_to_source";  // key->map

    static String HREF_TO_STATUS     = "href_to_status";  // key->int
    static String STATUS_TO_HREF     = "status_to_href";  // key->map

    static String MD5_TO_FILE        = "md5_to_file";     // key->string
    static String MD5_TO_HREF        = "md5_to_href";     // key->string


    // All files on which a hyperlink depends
    static String HREF_TO_FDEP         = "href_to_fdep";    // key->map

    // All hyperinks dependent upon a given file
    static String FILE_TO_HDEP         = "file_to_hdep";    // key->map


    AVMRemote                    avm_;
    AttributeService             attr_;
    AVMSyncService               sync_;
    NameMatcher                  path_excluder_;
    NameMatcher                  scheme_excluder_;
    NameMatcher                  href_bearing_request_path_matcher_;
    RetryingTransactionHelper    transaction_helper_;
    CreateVersionTxnListener     create_version_txn_listener_;
    PurgeVersionTxnListener      purge_version_txn_listener_;
    PurgeStoreTxnListener        purge_store_txn_listener_;

    LinkValidationStoreCallbackHandler  store_latest_version_info_;

    int local_connect_timeout_  = 10000;
    int remote_connect_timeout_ = 10000;
    int local_read_timeout_     = 30000;
    int remote_read_timeout_    = 30000;
    int poll_interval_          = 5000;

    String jsse_trust_store_file_;
    String jsse_trust_store_password_;


    boolean purge_all_validation_data_on_bootstrap_;


    static Pattern WEB_INF_META_INF_pattern_ =
             Pattern.compile(
               "[^:]+:/www/avm_webapps/[^/]+/(:?META-INF|WEB-INF)(?:/.*|$)",
               Pattern.CASE_INSENSITIVE);


    VirtServerRegistry virtreg_;

    public LinkValidationServiceImpl() { }

    public void setAttributeService(AttributeService svc) { attr_ = svc; }
    public AttributeService getAttributeService()         { return attr_;}

    public void setAVMSyncService( AVMSyncService sync )  { sync_ = sync;}
    public AVMSyncService getAVMSyncService()             { return sync_;}

    public void setAvmRemote(AVMRemote svc)               { avm_ = svc; }
    public AVMRemote getAvmRemote()                       { return avm_;}

    public void setExcludePathMatcher(NameMatcher matcher) 
    {
        path_excluder_ = matcher;
    }

    public NameMatcher getExcludePathMatcher()
    {
        return path_excluder_;
    }

    public void setHrefBearingRequestPathMatcher(NameMatcher matcher)
    {
        href_bearing_request_path_matcher_ = matcher;
    }

    public NameMatcher getHrefBearingRequestPathMatcher()
    {
        return href_bearing_request_path_matcher_;
    }

    public void setExcludeUriMatcher( NameMatcher matcher)
    {
        scheme_excluder_ = matcher;
    }

    public void setVirtServerRegistry(VirtServerRegistry reg) {virtreg_ = reg;}
    public VirtServerRegistry getVirtServerRegistry()         {return virtreg_;}


    public void setRetryingTransactionHelper( RetryingTransactionHelper t)
    {
        transaction_helper_ = t;
    }

    public void setLocalConnectTimeout(int milliseconds)
    {
        local_connect_timeout_ = milliseconds;
    }
    public void setRemoteConnectTimeout(int milliseconds)
    {
        local_connect_timeout_ = milliseconds;
    }
    public void setLocalReadTimeout( int milliseconds )
    {
        local_read_timeout_ = milliseconds;
    }
    public void setRemoteReadTimeout( int milliseconds )
    {
        remote_read_timeout_ = milliseconds;
    }
    public void setPollInterval( int milliseconds )
    {
        poll_interval_ = milliseconds;
    }

    public void setCreateVersionTxnListener( CreateVersionTxnListener listener)
    {
        create_version_txn_listener_ = listener;
    }

    public void setPurgeVersionTxnListener ( PurgeVersionTxnListener listener)
    {
        purge_version_txn_listener_ = listener;
    }

    public void setPurgeStoreTxnListener(PurgeStoreTxnListener listener)
    {
        purge_store_txn_listener_ = listener;
    }

    public void setJsseTrustStoreFile( String file )
    {
        jsse_trust_store_file_ = file.replace('/', File.separatorChar);
    }

    public void setJsseTrustStorePassword( String password)
    {
        jsse_trust_store_password_ = password;
    }

    public void  setPurgeAllValidationDataOnBootstrap( boolean tf)
    {
        purge_all_validation_data_on_bootstrap_ = tf;
    }


    //-------------------------------------------------------------------------
    /**
    *  Called by LinkValidationServiceBootstrap at startup time to ensure
    *  that the link status in all stores is up to date.
    */
    //-------------------------------------------------------------------------
    public void onBootstrap()
    {
        Thread validation_update_thread = new Thread(this);
        Shutdown_.set( false );
        validation_update_thread.start();
    }
    //-------------------------------------------------------------------------
    /**
    *  Called by LinkValidationServiceBootstrap at shutdown time to ensure
    *  that any link status checking operation in progress is abandoned.
    */
    //-------------------------------------------------------------------------
    public void onShutdown() { Shutdown_.set( true ); }


    //-------------------------------------------------------------------------
    /**
    *   Main thread to update href validation info in staging.
    */
    //-------------------------------------------------------------------------
    public void run()
    {
        // Initiate background process to check links
        // For now, hard-code initial update
        final String   webappPath       =  null;   // all stores/webapps
        final boolean  incremental      =  true;   // use deltas & merge
        final boolean  validateExternal =  true;   // check external hrefs

        HrefValidationProgress progress_sleepy = null;

        // Register transaction callbacks to build a cache that helps to
        // avoid unnecesary calls to the real AVM getLatestSnapshotID().
        // Instead, the local version is called (which consults the cache).

        store_latest_version_info_ = new LinkValidationStoreCallbackHandler();

        create_version_txn_listener_.addCallback( store_latest_version_info_ );
        purge_version_txn_listener_.addCallback(  store_latest_version_info_ );
        purge_store_txn_listener_.addCallback(    store_latest_version_info_ );

        // JSSE HTTPS:
        //   Unless a trust store is set, HTTPS connections will fail with:
        //      java.security.InvalidAlgorithmParameterException: 
        //           the trustAnchors parameter must be non-empty

        String password =  
                ( (jsse_trust_store_password_ != null) && 
                  ! jsse_trust_store_password_.equals("")
                )
                ? jsse_trust_store_password_ 
                : "changeit";                       // That's the JDK default!
                                                    // Who really changes this?
        String trust_store_file = 
                ( (jsse_trust_store_file_ != null) && 
                  !jsse_trust_store_file_.equals("")
                ) ? jsse_trust_store_file_
                  : (System.getProperty("java.home") + 
                    "/lib/security/cacerts".replace('/', File.separatorChar));

        System.setProperty("javax.net.ssl.trustStore",trust_store_file);
        System.setProperty("javax.net.ssl.trustStorePassword",password);

        // Before Java 1.4, you needed to do a dance like the one below;
        // however, that's no longer necessary.
        //
        // See also:   http://java.sun.com/j2se/1.4.2/docs/guide/security/
        //                    jsse/JSSERefGuide.html
        //
        // Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
        // Properties properties = System.getProperties();
        // String handlers = System.getProperty("java.protocol.handler.pkgs");
        // if (handlers == null)  // expected case: no handlers
        // {
        //     properties.put("java.protocol.handler.pkgs", 
        //                    "com.sun.net.ssl.internal.www.protocol");
        // }
        // else
        // {
        //     properties.put(
        //        "java.protocol.handler.pkgs", 
        //        "com.sun.net.ssl.internal.www.protocol|".concat(handlers));
        // }
        // System.setProperties(properties);


        if ( purge_all_validation_data_on_bootstrap_ )
        {
            try 
            { 
                attr_.removeAttribute( "", HREF); 

                if ( log.isInfoEnabled() )
                    log.info( "Purged all link validation data.");
            }
            catch (Exception e) 
            { 
                if ( log.isInfoEnabled() )
                    log.info( "No link validation data to purge.");
            }
        }


        while ( ! Shutdown_.get() )
        {
            if ( log.isDebugEnabled() )
                log.debug( "LinkValidationService polling webapps...");

            final HrefValidationProgress progress = new HrefValidationProgress();
            progress_sleepy = progress;

            try
            {
                RetryingTransactionHelper.RetryingTransactionCallback<Object>
                callback = new RetryingTransactionHelper.
                               RetryingTransactionCallback<Object>()
                               {
                                   public String execute() throws Throwable
                                   {
                                           updateHrefInfo( webappPath,
                                                           incremental,
                                                           validateExternal,
                                                           progress);
                                       return null;
                                   }
                               };

                transaction_helper_.doInTransaction(callback);
            }
            catch (Exception e)
            {
                // Super-low level debug.
                //
                // if ( log.isDebugEnabled() )
                // {
                //     // After all these years, there's still no easy
                //     // method to print a stack trace into a string.
                //     // Where is the love?  Where?
                //
                //     StringWriter string_writer = new StringWriter();
                //     PrintWriter print_writer   = new PrintWriter(string_writer);
                //     e.printStackTrace(print_writer);
                //
                //     log.debug( "Exception class:  " + e.getClass().getName() +
                //                ":  " + e.getMessage() +
                //                "\n" + string_writer.toString() );
                // }


                if ( log.isInfoEnabled() )
                    log.info("Could not validate links.  Retrying");
            }
            finally
            {
                if ( log.isDebugEnabled() )
                    log.debug("About to sleep");
            }

            // Sleep regardless of whetherthe updateHrefInfo failed
            try { Thread.sleep( poll_interval_ ); }
            catch (Exception e)
            {
                /* nothing to do */
                if ( log.isDebugEnabled() )
                    log.debug("Troubled sleep(): " + e.getMessage());
            }
            if ( log.isDebugEnabled() )
                log.debug("Woke up from sleep");
        }

        if ( log.isDebugEnabled() )
            log.debug("Done with main loop of link validation thread");


        // Usually, tomcat will be dead by now, but just in case...
        if ( progress_sleepy != null)
        {
            if ( log.isDebugEnabled() )
                log.debug("Aborting...");

            progress_sleepy.abort();
        }
    }

    /*-------------------------------------------------------------------------
    *  getLatestSnapshotID --
    *       Cached version of the AVM call of the same name.
    *------------------------------------------------------------------------*/
    protected int getLatestSnapshotID( String  store )
                  throws  AVMNotFoundException
    {
        Integer latest = 
            store_latest_version_info_.getLatestSnapshotID( store );

        if ((latest != null) &&  latest >= 0) 
        { 
            return latest;                      // cache hit
        }

        // Call the real function
        latest = avm_.getLatestSnapshotID( store );

        // Update the cache
        //
        // Note:  The somewhat peculiar calling syntax is necesary to avoid
        //        a race condition. If the cache was updated between the time
        //        the call to the "real" getLatestSnapshotID() (see above)
        //        and the fetch of the data from the cache (see below),
        //        the return vaule of putLatestSnapshotID() obtains the
        //        final latest version.

        latest = store_latest_version_info_.putLatestSnapshotID(store, latest);

        return latest;
    }



    //-------------------------------------------------------------------------
    /**
    *  Purge AttributeService of href info that no longer corresponds
    *  to a webapp (e.g.: a project or webapp may have been deleted).
    */
    //-------------------------------------------------------------------------
    void remove_stale_href_info(
            Map<String, Map<QName, PropertyValue>>  store_staging_main_entries )
    {
        if ( log.isDebugEnabled() )
            log.debug( "remove_stale_href_info");

        Map<String,String> valid_webapp_keys = new HashMap<String,String>();

        for ( Map.Entry<String, Map<QName, PropertyValue>>
              store_staging_main_entry  :
              store_staging_main_entries.entrySet())
        {
            String  store  = store_staging_main_entry.getKey();
            ValidationPathParser p =
                new ValidationPathParser(avm_, store );

            String app_base  = p.getAppBase();
            String dns_name  = p.getDnsName();

            Map<String, AVMNodeDescriptor> webapp_entries = null;
            try
            {
                // It's safe to use -1 as the version here because
                // if the user has removed the HEAD version, there's
                // no reason to care about any associated href info
                // in an archive.

                webapp_entries = avm_.getDirectoryListing( -1 , app_base);
            }
            catch (AVMException e)
            {
                if ( log.isErrorEnabled() )
                {
                    log.error("Could to get directory listing of: " + app_base +
                              "     " + e.getClass().getName()  +  e.getMessage());
                }
                continue;      // don't let errors like this thwart cleanup
            }

            for ( Map.Entry<String, AVMNodeDescriptor> webapp_entry  :
                                                       webapp_entries.entrySet()
                )
            {
                String webapp_name = webapp_entry.getKey();  // my_webapp
                AVMNodeDescriptor avm_node = webapp_entry.getValue();

                if ( webapp_name.equalsIgnoreCase("META-INF")  ||
                     webapp_name.equalsIgnoreCase("WEB-INF")
                   )
                {
                    continue;
                }

                if  ( ! avm_node.isDirectory() )
                {
                    continue;
                }

                String store_attr_base  = getAttributeStemForDnsName( dns_name );

                // Examples of possible webapp_attr_base values:
                //
                //            .href/mysite/|ROOT
                //            .href/mysite/alice/preview/|mywebapp
                //
                // Note that the leafname always begins with '|'
                // which is an illegal DNS name char... therefore
                // there's no need to worry about escape/unescape.

                String webapp_attr_base = store_attr_base  +  "/|"  +  webapp_name;
                valid_webapp_keys.put( webapp_attr_base, null );
            }
        }

        // The valid_webapp_keys map now contains all and only the keys
        // that correspond to webapps that have not been deleted.
        //
        // Build a list of all the webapps in attribute service

        Map<String,String> webapp_keys = new HashMap<String,String>();
        get_webapp_keys_in_attribute_service( HREF, 0, webapp_keys );

        // Get rid of stuff that isn't needed anymore

        for (String key : webapp_keys.keySet() )
        {
            if ( ! valid_webapp_keys.containsKey( key ) )
            {
                int    last_slash = key.lastIndexOf('/');
                String stem       = key.substring(0,last_slash);
                String stale      = key.substring(last_slash + 1);

                try
                {
                    attr_.removeAttribute( stem, stale);

                    if ( log.isDebugEnabled() )
                        log.debug("Removed stale AttributeService key: " + key );

                }
                catch (Exception e )
                {
                    if ( log.isErrorEnabled() )
                        log.error(
                        "Could not remove stale AttributeService key: " + key +
                        "   " + e.getMessage());
                }
            }
        }
    }

    /*-------------------------------------------------------------------------
    *  get_webapp_keys_in_attribute_service --
    *       Collect the webapp keys in attribute service,
    *       some of which may be stale.
    *------------------------------------------------------------------------*/
    void get_webapp_keys_in_attribute_service(
            String             base,
            int                depth,
            Map<String,String> webapp_keys)
    {
        if ( depth > 128 )              // Sanity check.
        {                               // No DNS name can be this deep
            if ( log.isWarnEnabled() )
                log.warn("Impossible link validation key: " + base);

            return;
        }

        List<String> children = null;

        try
        {
            children = attr_.getKeys( base );
        }
        catch (Exception e)
        {
            return;     // no children means we're done
        }


        for (String child : children )
        {
            if  ( child.charAt(0) == '|' )
            {
                webapp_keys.put( base + "/" + child, null );
            }
            else
            {
                get_webapp_keys_in_attribute_service(
                    base + "/" + child,
                    depth + 1,
                    webapp_keys);
            }
        }
    }



    //-------------------------------------------------------------------------
    /**
    *
    * @param  webappPath
    *           Path to webapp
    *
    * @param  incremental
    *           Use deltas if true (faster); otherwise, force
    *           the href info to be updated from scratch.
    *
    * @param  validateExternal
    *           Validate external links
    *
    *
    * @param  progress
    *             While updateHrefInfo() is a synchronous function,
    *             'progress' may be polled in a separate thread to
    *             observe its progress.
    */
    //-------------------------------------------------------------------------
    public void updateHrefInfo( String                 path,
                                boolean                incremental,
                                boolean                validateExternal,
                                HrefValidationProgress progress)
                throws          AVMNotFoundException,
                                SocketException,
                                SSLException,
                                LinkValidationAbortedException
    {
        if ( log.isDebugEnabled() )
            log.debug( "updateHrefInfo path: " + path);

        if ( path == null )           // For every staging store, update href
        {                             // validity info on every webapp.
            // get staging stores
            Map<String, Map<QName, PropertyValue>> store_staging_main_entries =
                avm_.queryStoresPropertyKey(
                    SandboxConstants.PROP_SANDBOX_STAGING_MAIN );

            for ( Map.Entry<String, Map<QName, PropertyValue>>
                  store_staging_main_entry  :
                  store_staging_main_entries.entrySet())
            {
                String  store  = store_staging_main_entry.getKey();

                updateStoreHrefInfo( store,
                                     incremental,
                                     validateExternal,
                                     progress);
            }

            // Because this is a global update, this is a good place
            // to trigger a cleanup of any stale attribute info.
            // For example, a store may have been validated in the past,
            // but then deleted.  The old validation data can be purged.

            remove_stale_href_info(  store_staging_main_entries );

        }
        else
        {                                                // This must be either
            ValidationPathParser p =                     // a store path or a
                new ValidationPathParser(avm_, path );   // webapp path. If the
                                                         // wepapp_name is null
            String store           = p.getStore();       // then it's a store
            String webapp_name     = p.getWebappName();  // path;  otherwise it
                                                         // is a webapp path.

            if ( webapp_name == null )                   // A store path
            {
                updateStoreHrefInfo( store,
                                     incremental,
                                     validateExternal,
                                     progress);
            }
            else                                         // A webapp path
            {
                // Flush all modified files into an anonymous snapshot.
                // A null tag & description  are used to avoid clobbering
                // tag/description info in a non-anonymous snapshot.
                // avm_.createSnapshot(store, null, null);

                int update_to_version = getLatestSnapshotID( store );

                updateWebappHrefInfo( update_to_version,
                                      path,
                                      incremental,
                                      validateExternal,
                                      progress);
            }
        }

        // Clean up attribute info for webapps that no longer exist
    }

    /*-------------------------------------------------------------------------
    *  updateStoreHrefInfo --
    *
    *------------------------------------------------------------------------*/
    void updateStoreHrefInfo( String                 store,
                              boolean                incremental,
                              boolean                validateExternal,
                              HrefValidationProgress progress)
         throws               AVMNotFoundException,
                              SocketException,
                              SSLException,
                              LinkValidationAbortedException
    {
        if ( log.isDebugEnabled() )
            log.debug("starting updateStoreHrefInfo: " + store );

        // Determine what version of the store we're trying to update
        // the baseline version of the href validity info to.


        // Flush all modified files into an anonymous snapshot.
        // A null tag & description  are used to avoid clobbering
        // tag/description info in a non-anonymous snapshot.
        // avm_.createSnapshot(store, null, null);


        int update_to_version = getLatestSnapshotID( store );

        // Get all webapps in this store

        ValidationPathParser p = new ValidationPathParser(avm_, store );
        String app_base  = p.getAppBase();

        Map<String, AVMNodeDescriptor> webapp_entries = null;

        try
        {
            webapp_entries = avm_.getDirectoryListing( update_to_version, app_base);
        }
        catch (AVMException e)
        {
            if ( log.isErrorEnabled() )
                log.error("Could to do directory listing of: " + app_base +
                          "     " + e.getClass().getName()  +  e.getMessage());

            throw e;
        }

        for ( Map.Entry<String, AVMNodeDescriptor> webapp_entry  :
                                                   webapp_entries.entrySet()
            )
        {
            String webapp_name = webapp_entry.getKey();  // my_webapp
            AVMNodeDescriptor avm_node = webapp_entry.getValue();

            if ( webapp_name.equalsIgnoreCase("META-INF")  ||
                 webapp_name.equalsIgnoreCase("WEB-INF")
               )
            {
                continue;
            }

            if  ( ! avm_node.isDirectory() )
            {
                continue;
            }

            String webappPath = app_base + "/" + webapp_name;

            if ( log.isDebugEnabled() )
                log.debug( "About to update hrefs in: "  + webappPath);

            updateWebappHrefInfo( update_to_version,
                                  webappPath,
                                  incremental,
                                  validateExternal,
                                  progress);

            if ( log.isDebugEnabled() )
                log.debug( "Finished update of hrefs in: "  + webappPath);
        }

        if ( log.isDebugEnabled() )
            log.debug("done updateStoreHrefInfo: " + store );
    }

    /*-------------------------------------------------------------------------
    *  setAttribute_if_not_found --
    *     Creates base/key/value  if  value does not already exist at base/key.
    *     Returns true if value needed to be set.
    *------------------------------------------------------------------------*/
    boolean setAttribute_if_not_found( String base,
                                       String key,
                                       Attribute  value)
    {
        if ( ! attr_.exists( base + "/" + key ) )
        {
            attr_.setAttribute( base, key, value );
            return true;
        }
        return false;
    }

    /*-------------------------------------------------------------------------
    *  updateWebappHrefInfo --
    *
    *------------------------------------------------------------------------*/
    void updateWebappHrefInfo( int                    update_to_version,
                               String                 webappPath,
                               boolean                incremental,
                               boolean                validateExternal,
                               HrefValidationProgress progress)
         throws                AVMNotFoundException,
                               SocketException,
                               SSLException,
                               LinkValidationAbortedException
    {
        if ( log.isDebugEnabled() )
            log.debug("starting updateWebappHrefInfo:  " +
                      webappPath + " (to version: " + update_to_version + ")");


        // Ensure the proper attribute service key is set up for this webapp
        //
        // The following convention is used:
        //           version <==> (version - max - 2) %(max+2)
        //
        // The only case that ever matters for now is that:
        // -2 is an alias for the last snapshot
        //
        // Thus href attribute info for the  "last snapshot" of
        // a store with the dns name:  preview.alice.mysite is
        // stored within attribute service under the keys:
        //
        //      .href/mysite/alice/preview/|mywebapp/-2
        //
        // This allows entire projects and/or webapps to removed in 1 step


        ValidationPathParser p =
            new ValidationPathParser(avm_, webappPath );


        String store           = p.getStore();
        String webapp_name     = p.getWebappName();
        String app_base        = p.getAppBase();
        String dns_name        = p.getDnsName();


        // Example:               ".href/mysite"
        String store_attr_base  = setAttributeStemForDnsName( dns_name );

        setAttribute_if_not_found( store_attr_base,
                                   "|" + webapp_name,
                                   new MapAttributeValue());


        // Example:               ".href/mysite/|ROOT"
        String webapp_attr_base = store_attr_base  +  "/|" + webapp_name;

        // Example:               ".href/mysite/|ROOT/-2"
        String href_attr        = webapp_attr_base +  "/"  + BASE_VERSION_ALIAS;


        Attribute schema_vers_attr =
            attr_.getAttribute( webapp_attr_base + "/" + SCHEMA_VERSION);

        if ((schema_vers_attr == null)
            ||
            (schema_vers_attr.getIntValue() != Schema_version_ )
           )
        {
            // The webapp's href info is not using the same schema version
            // so toss the old data before attempting to to an update.

            attr_.setAttribute(                 // reset schema version
                webapp_attr_base,
                SCHEMA_VERSION,
                new IntAttributeValue( Schema_version_ ));

            attr_.setAttribute(                 // diff against empty store
                webapp_attr_base,
                BASE_VERSION,
                new IntAttributeValue( 0 ));
            
            try                                 // discard other schema's info
            { 
                attr_.removeAttribute( webapp_attr_base, BASE_VERSION_ALIAS);
            }
            catch (Exception e)
            {
                // Not harmful if there was nothing to throw away.
            }

            if ( log.isDebugEnabled() )
                log.debug("updateWebappHrefInfo purged data from prior schema");
        }


        int base_version = 0;
        Attribute base_vers_attr =
            attr_.getAttribute( webapp_attr_base + "/" + BASE_VERSION);

        if ( base_vers_attr != null )
        {
            base_version = base_vers_attr.getIntValue();
        }
        else
        {
            attr_.setAttribute( webapp_attr_base,
                                BASE_VERSION,
                                new IntAttributeValue( 0 ));
        }




        setAttribute_if_not_found( webapp_attr_base,
                                   BASE_VERSION_ALIAS,
                                   new MapAttributeValue());


        String [] index_list = { SOURCE_TO_HREF, HREF_TO_SOURCE,
                                 HREF_TO_STATUS, STATUS_TO_HREF,
                                 MD5_TO_FILE,    MD5_TO_HREF,
                                 HREF_TO_FDEP,   FILE_TO_HDEP };


        for (String key : index_list )
        {
            setAttribute_if_not_found( href_attr, key ,new MapAttributeValue());
        }


        int old_update_version = 0;
        Attribute old_update_vers_attr =
            attr_.getAttribute( webapp_attr_base + "/" + UPDATE_VERSION);

        if ( old_update_vers_attr != null )
        {
            old_update_version = old_update_vers_attr.getIntValue();
        }
        else
        {
             attr_.setAttribute( webapp_attr_base,
                                 UPDATE_VERSION,
                                 new IntAttributeValue( 0 ));
        }

        if  ( base_version == update_to_version )
        {
            // The latest version for which hrefs have been validated
            // equals the last snapshot for this store.  Therefore
            // there's nothing to do.

            if ( log.isDebugEnabled() )
                log.debug("No need to revalidate (skipping): " +  webappPath );

            progress.incrementWebappUpdateCount();  // for monitoring progress

            if ( log.isDebugEnabled() )
                log.debug("done updateWebappHrefInfo: " + webappPath);

            return;
        }

        if ( old_update_version > 0 )
        {
            // make JMX call to turn old virtualized webapp off
            virtreg_.removeWebapp( old_update_version, webappPath, false);
        }

        // set update_to_version attribute

        attr_.setAttribute( webapp_attr_base,
                            UPDATE_VERSION,
                            new IntAttributeValue( update_to_version ));


        // Virtualize update_to_version via JMX call

        if ( ! virtreg_.updateWebapp( update_to_version, webappPath, false))
        {
            throw new LinkValidationAbortedException(
                "Version: " + update_to_version + " of: " + webappPath +
                " cannot be virtualized");
        }

        if ( log.isDebugEnabled() )
            log.debug("updateWebappHrefInfo starting getHrefDifference:  " +
                      base_version + "->" + update_to_version + "  " +
                      webappPath);

        HrefDifference hdiff =
                 getHrefDifference( update_to_version,  // src vers
                                    webappPath,
                                    base_version,       // dst vers
                                    webappPath,
                                    progress);


        mergeHrefDiff( hdiff, progress );

        // set baseline == update_to_version

        attr_.setAttribute( webapp_attr_base,
                            BASE_VERSION,
                            new IntAttributeValue( update_to_version ));

        progress.incrementWebappUpdateCount();  // for monitoring progress

        if ( log.isDebugEnabled() )
            log.debug("done updateWebappHrefInfo: " + webappPath);
    }


    //------------------------------------------------------------------------
    /**
    *  Computes what changes would take place the link status of staging
    *  when diffs from a srcWebappPath are applied to staging.  There are
    *  four state transitions of interest:
    *
    * <pre>
    *          [1]  Broken by deleted files/dirs in src:
    *               o  Any url that both depends on a deleted file and would
    *                  still remain in a file after the set was deleted
    *                 -  Scales with # deletions
    *                 -  Computable via inference (fast)
    *
    *          [2]  Broken by added files/dirs in src:
    *               o  Any url that is broken in the changeset
    *                 - Scales with # of links in changeset
    *                 - Potentially slow
    *
    *          [3]  Fixed by deleted files/dirs in src:
    *               o  Any url that is broken and only occurs in the
    *                  members of the set being deleted
    *                  -  Scales with # deletions
    *                  -  Computable via inference (fast)
    *
    *          [4]  Fixed by added files/dirs in src:
    *               o  Any url that was broken that is now fixed
    *                  Must rescan all broken links still present
    *                  in changeset to be 100% sure.
    *                  - Scales with the # of broken links in staging
    *                  - Potentially slow
    *
    * </pre>
    */
    //------------------------------------------------------------------------
    public HrefDifference getHrefDifference(
                              String  srcWebappPath,
                              String  dstWebappPath,
                              HrefValidationProgress progress)
            throws            AVMNotFoundException,
                              SocketException,
                              SSLException,
                              LinkValidationAbortedException
    {
        if ( log.isErrorEnabled() )
            log.error("getHrefDifference: " + srcWebappPath  + "  " + dstWebappPath );


        ValidationPathParser p =
            new ValidationPathParser(avm_, dstWebappPath);

        String dns_name         = p.getDnsName();
        String webapp_name      = p.getWebappName();
        String store_attr_base  = getAttributeStemForDnsName( dns_name );
        String webapp_attr_base = store_attr_base  +  "/|"  +  webapp_name;


        Attribute base_vers_attr =
            attr_.getAttribute( webapp_attr_base + "/" + BASE_VERSION);


        int srcVersion = -1;
        int dstVersion;

        if ( base_vers_attr != null )
        {
            dstVersion = base_vers_attr.getIntValue();
        }
        else                    // If we're differencing against a non-indexed
        {                       // base, then consider every file "new"> in the
            dstVersion = 0;     // src by by diffing against the empty version.
        }


        if ( progress != null )
        {
            progress.init();
        }

        try
        {
           return  getHrefDifference( srcVersion,
                                      srcWebappPath,
                                      dstVersion,
                                      dstWebappPath,
                                      progress);
        }
        finally
        {
            if (progress != null)
            {
                progress.setDone( true );
            }
        }
    }


    /*-----------------------------------------------------------------------*/
    /**
    * Determines how hrefs have changed between two stores,
    * along with status info, and returns the result;
    * this href difference info is not merged with the
    * overall href status info for the webapp.
    *
    * Thus, you can use this function to see how the status of hyperlinks
    * would change if you were to incorporate the diff without actually
    * incorporating it.  After calling this function, the user can see
    * what's broken by invoking:
    * <ul>
    *   <li>  getHrefManifestBrokenByDeletion()
    *   <li>  getHrefManifestBrokenInNewOrMod()
    * </ul>
    */
    /*-----------------------------------------------------------------------*/
    public HrefDifference getHrefDifference(
                                  int    srcVersion,
                                  String srcWebappPath,
                                  int    dstVersion,
                                  String dstWebappPath,
                                  HrefValidationProgress progress)
                          throws  AVMNotFoundException,
                                  SocketException,
                                  SSLException,
                                  LinkValidationAbortedException
    {
        if ( log.isDebugEnabled() )
            log.debug(
             "getHrefDifference: " + srcVersion + " " + srcWebappPath  +
                              "  " + dstVersion + " " +  dstWebappPath );


        ValidationPathParser dp = new ValidationPathParser(avm_,dstWebappPath);
        String dst_dns_name     = dp.getDnsName();
        String dst_req_path     = dp.getRequestPath();
        String webapp_name      = dp.getWebappName();


        if (webapp_name == null )
        {
            throw new RuntimeException( "Not a path to a webapp: " +
                                        dstWebappPath);
        }

        List<AVMDifference> diffs = sync_.compare( srcVersion,
                                                   srcWebappPath,
                                                   dstVersion,
                                                   dstWebappPath,
                                                   path_excluder_);

        ValidationPathParser sp = new ValidationPathParser(avm_, srcWebappPath);
        String src_dns_name     = sp.getDnsName();
        String src_req_path     = sp.getRequestPath();

        String virt_domain      = virtreg_.getVirtServerFQDN();
        int    virt_port        = virtreg_.getVirtServerHttpPort();

        String src_fqdn         = src_dns_name + ".www--sandbox.version--v" +
                                  srcVersion   + "." + virt_domain;

        String dst_fqdn         = dst_dns_name + ".www--sandbox.version--v" +
                                  dstVersion   + "." + virt_domain;


        String src_webapp_url_base = null;
        String dst_webapp_url_base = null;
        try
        {
            URI u;

            u = new URI( "http",       // scheme
                         null,         // userinfo
                         src_fqdn,     // host
                         virt_port,    // port
                         src_req_path, // request path
                         null,         // query
                         null);        // frag

            src_webapp_url_base = u.toASCIIString();
            if ( src_webapp_url_base.charAt( src_webapp_url_base.length()-1 )
                 == '/'
               )
            {
                src_webapp_url_base = src_webapp_url_base.substring(
                                         0, src_webapp_url_base.length() -1 );
            }

            // Example src_webapp_url_base:
            //   http://alice.mysite.www--sandbox.
            //         version--v-1.127-0-0-1.ip.alfrescodemo.net:8180

            u = new URI( "http",       // scheme
                         null,         // userinfo
                         dst_fqdn,     // host
                         virt_port,    // port
                         dst_req_path, // request path
                         null,         // query
                         null);        // frag

            dst_webapp_url_base = u.toASCIIString();
        }
        catch (Exception e)
        {
            /* can't happen */
            if ( log.isErrorEnabled() )
                log.error("Could not create src & dst URL base");
        }

        if ( dst_webapp_url_base.charAt( dst_webapp_url_base.length()-1 )
             == '/'
           )
        {
            dst_webapp_url_base = dst_webapp_url_base.substring(
                                     0, dst_webapp_url_base.length() -1 );
        }

        // Example dst_webapp_url_base:
        //   http://mysite.www--sandbox.
        //          version--v-1.127-0-0-1.ip.alfrescodemo.net:8180/


        // Examine the diffs between src and dst
        //
        //    o  If a file or dir is non-deleted, then fetch all the
        //       relevant link from src and store the data collected
        //       in href_manifest and src_href_status_map.
        //
        //    o  If a file or dir is deleted, then update the
        //       deleted_file_md5 and the broken_hdep cache from dst.

        String store_attr_base = getAttributeStemForDnsName(dst_dns_name);

        String href_attr       = store_attr_base    +
                                 "/|" + webapp_name +
                                 "/"  + BASE_VERSION_ALIAS;

        HrefDifference href_diff = new HrefDifference(href_attr,
                                                      sp.getStore(),
                                                      dp.getStore(),
                                                      src_webapp_url_base,
                                                      dst_webapp_url_base);

        HrefStatusMap  src_href_status_map  = href_diff.getHrefStatusMap();
        HrefManifest   href_manifest        = href_diff.getHrefManifest();
        Map<String,String> deleted_file_md5 = href_diff.getDeletedFileMd5();

        HashMap<String,String> broken_hdep_cache = new HashMap<String,String>();
        MD5 md5 = new MD5();

        for (AVMDifference diff : diffs )
        {
            String src_path  = diff.getSourcePath();

            // Never look at diffs in META-INF or WEB-INF

            if ( (WEB_INF_META_INF_pattern_.matcher( src_path )).matches() )
            {
                continue;
            }

            // Look up source node, even if it's been deleted

            AVMNodeDescriptor src_desc = avm_.lookup(srcVersion,src_path,true);
            if (src_desc == null ) { continue; }

            if (src_desc.isDeleted() )
            {
                // Deal with deleted files

                String dst_path  = diff.getDestinationPath();

                if ( src_desc.isDeletedDirectory() )            // deleted dir
                {
                    update_dir_gone_broken_hdep_cache(
                        dstVersion,
                        dst_path,
                        deleted_file_md5,
                        broken_hdep_cache,
                        href_attr,
                        md5,
                        progress);

                    // stats for monitoring
                    if ( progress != null )
                    {
                        progress.incrementDirUpdateCount();
                    }
                }
                else                                            // deleted file
                {
                    update_file_gone_broken_hdep_cache(
                        dst_path,
                        deleted_file_md5,
                        broken_hdep_cache,
                        href_attr,
                        md5,
                        progress);

                    // stats for monitoring
                    if ( progress != null )
                    {
                        progress.incrementFileUpdateCount();
                    }
                }
            }
            else
            {
                // New, modified, or conflicted  files/dirs

                ValidationPathParser spath =
                        new ValidationPathParser(avm_, src_path);

                String req_path = spath.getRequestPath();
                if ( req_path.equals("/") ) { req_path = ""; }

                if (src_desc.isDirectory())
                {
                    // recurse within src for new links
                    extract_links_from_dir(
                            srcVersion,
                            src_path,
                            src_fqdn,
                            virt_port,
                            req_path,
                            href_manifest,        // sync list
                            src_href_status_map,  // sync map
                            progress,
                            0);

                    // stats for monitoring
                    if ( progress != null )
                    {
                        progress.incrementDirUpdateCount();
                    }
                }
                else
                {
                    // Get links from src_path and status of implicit
                    // (dead-reckoned) link to it, but don't pull
                    // on the parsed links yet.

                    extract_links_from_file(
                            src_path,
                            src_fqdn,
                            virt_port,
                            req_path,
                            href_manifest,        // sync list
                            src_href_status_map,  // sync map
                            progress);

                    // stats for monitoring
                    if (progress != null)
                    {
                        progress.incrementFileUpdateCount();
                    }
                }
            }
        }


        // TODO:  When extract_links_from_file is multi-threaded,
        //        put a thread barrier here, so that all the dead
        //        reckoned links have been validated before proceeding.
        //


        // Get status of links in href_manifest if not already known
        List<HrefManifestEntry> src_manifest_entry_list =
                href_manifest.getManifestEntries();

        for ( HrefManifestEntry src_manifest_entry : src_manifest_entry_list )
        {
            List<String> src_href_list = src_manifest_entry.getHrefs();
            for (String src_parsed_url : src_href_list)
            {
                if ( src_href_status_map.get( src_parsed_url ) == null )
                {
                    validate_uri( src_parsed_url,
                                   src_href_status_map,
                                   src_parsed_url.startsWith( src_webapp_url_base ),
                                   false,
                                   progress);
                }
            }
        }

        // TODO:  When validating url for cache is multi-threaded
        //        put a thread barrier here so that we've got
        //        status info for all parsed links after this point


        // Remove from the collection of "broken" hyperlinks
        // anything that is no longer referenced by any file
        //
        // Compute what's broken by the deletion

        Map<String, List<String>> broken_manifest_map =
                href_diff.getBrokenManifestMap();


        for ( String broken_href_md5 : broken_hdep_cache.keySet() )
        {
            List<String> file_md5_list =
                    attr_.getKeys( href_attr      + "/" +
                                   HREF_TO_SOURCE + "/" +
                                   broken_href_md5
                                 );

            ArrayList<String> conc_file_list = new ArrayList<String>();

            for ( String file_md5 : file_md5_list )
            {
                if  (  ! deleted_file_md5.containsKey(  file_md5 ) )
                {
                    String conc_file =
                       attr_.getAttribute( href_attr   + "/" +
                                           MD5_TO_FILE + "/" +
                                           file_md5
                                         ).getStringValue();

                    conc_file_list.add( conc_file );
                }
            }

            String rel_broken_href = attr_.getAttribute(
                                              href_attr   + "/" +
                                              MD5_TO_HREF + "/" +
                                              broken_href_md5).getStringValue();


            // This broken href is relevant because it still exists in some file
            if ( conc_file_list.size() > 0 )
            {
                for ( String broken_file : conc_file_list )
                {
                    List<String> manifest_list =
                        broken_manifest_map.get(broken_file);

                    if ( manifest_list == null )
                    {
                        manifest_list = new ArrayList<String>();
                        broken_manifest_map.put( broken_file, manifest_list);
                    }
                    manifest_list.add( rel_broken_href );
                }
            }
        }

        // Now to see what's broken in the update, the client can call:
        //
        //    getHrefManifestBrokenByDeletion()
        //    getHrefManifestBrokenInNewOrMod()

        return href_diff;
    }


    /*-------------------------------------------------------------------------
    *  getHrefManifestBrokenByDelete --
    *------------------------------------------------------------------------*/
    public HrefManifest getHrefManifestBrokenByDelete(HrefDifference href_diff)
    {
        if ( log.isDebugEnabled() )
            log.debug("getHrefManifestBrokenByDelete");


        if ( href_diff.broken_by_deletion_ != null )
        {
            return href_diff.broken_by_deletion_;
        }

        href_diff.broken_by_deletion_ = new HrefManifest();

        String src_webapp_url_base = href_diff.getSrcWebappUrlBase();
        String src_store           = href_diff.getSrcStore();
        String dst_store           = href_diff.getDstStore();
        int dst_store_length       = dst_store.length();

        Map<String, List<String>> broken_manifest_map =
                href_diff.getBrokenManifestMap();

        ArrayList<String> dst_broken_file_list =
            new ArrayList<String>( broken_manifest_map.keySet() );

        Collections.sort( dst_broken_file_list);

        // push result into href_diff

        for (String dst_broken_file : dst_broken_file_list)
        {
            List<String> rel_broken_href_list =
                broken_manifest_map.get(dst_broken_file);

            List<String> src_broken_href_list =
                new ArrayList<String>(
                        ( rel_broken_href_list != null)
                        ? rel_broken_href_list.size()
                        : 0 );

            for ( String rel_broken_href : rel_broken_href_list )
            {
                String src_broken_href;
                if ( rel_broken_href.charAt(0) == '/')
                {
                    src_broken_href = src_webapp_url_base + rel_broken_href;
                }
                else
                {
                    src_broken_href = rel_broken_href;
                }
                src_broken_href_list.add( src_broken_href );
            }

            Collections.sort( src_broken_href_list );

            String src_broken_file =
                  src_store +
                  dst_broken_file.substring( dst_store_length );

            href_diff.broken_by_deletion_.add(
                new HrefManifestEntry( src_broken_file, src_broken_href_list ));
        }
        return href_diff.broken_by_deletion_;
    }


    /*-------------------------------------------------------------------------
    *  getHrefManifestBrokenByNewOrMod --
    *
    *------------------------------------------------------------------------*/
    public HrefManifest getHrefManifestBrokenByNewOrMod(
                            HrefDifference href_diff)
    {
        if ( log.isDebugEnabled() )
            log.debug("getHrefManifestBrokenByNewOrMod");


        if ( href_diff.broken_in_newmod_ != null)   // If already calculated
        {                                           // then just return the
            return href_diff.broken_in_newmod_;     // old result
        }

        // Derived a pruned version of the href_manifest to get only those
        // new/modified files with at least one broken link.  Note that
        // href_diff.broken_in_newmod_ is a subset of href_diff.href_manifest_
        // in both files mentioned and hrefs per file.

        href_diff.broken_in_newmod_ = new HrefManifest();

        List<HrefManifestEntry> src_manifest_entry_list =
                href_diff.getHrefManifest().getManifestEntries();

        HrefStatusMap src_href_status_map  = href_diff.getHrefStatusMap();

        for ( HrefManifestEntry src_manifest_entry : src_manifest_entry_list )
        {
            ArrayList<String> src_broken_href_list = new ArrayList<String>();
            List<String>      src_href_list = src_manifest_entry.getHrefs();

            for (String src_parsed_url : src_href_list)
            {
                Pair<Integer,List<String>> tuple = 
                    src_href_status_map.get(src_parsed_url);

                int status_code;

                if ( tuple != null ) { status_code =  tuple.getFirst(); }
                else                 { status_code = 400; }

                if (status_code >= 400 )
                {
                    src_broken_href_list.add( src_parsed_url );
                }
            }

            if  ( src_broken_href_list.size() > 0 )
            {
                href_diff.broken_in_newmod_.add(
                   new HrefManifestEntry( src_manifest_entry.getFileName(),
                                          src_broken_href_list));
            }
        }
        return href_diff.broken_in_newmod_;
    }

    //-------------------------------------------------------------------------
    /**
    * Walk the list of files that are new or modified (newmod_file)
    * and update the following parameters:
    * <pre>
    *
    *   rel_newmod_conc       Contains as keys any new or modified rel_url_md5.
    *                         Its values are a map keys of dst_file_md5
    *                         (that have null values).
    *
    *   deleted_conc          Contains as keys any deleted rel_url_md5.
    *                         its values are a map of the dst_file_md5
    *                         (that have null values).  A url can appear
    *                         in this concordance because some dst_file_md5
    *                         it was once in was either deleted or modified.
    *
    *   newmod_file           Contains as keys any new or modified dst_file_md5.
    *                         Its values are the associated dst_file
    *
    *   rel_href_in_conc      Contains as keys rel_url_md5 that is present
    *                         in either rel_newmod_conc or deleted_conc.  Its
    *                         values are the associated rel_url.  When the
    *                         href appears in rel_newmod_conc but not
    *                         deleted_conc, its value in is the actual url,
    *                         not null.  This serves as a flag that allows some
    *                         calls to set md5 -> url to be skipped.
    *
    *   rel_href_broken_fdep  Contains as keys any rel_url_md5 that depended
    *                         upon a file that was deleted.  These hrefs
    *                         are are likely to make a state transition from
    *                         good to bad, and so must be rechecked.
    *
    *   rel_newmod_manifest_list
    *                         Like the src_manifest_list, only all values are
    *                         the md5sum of their translation into the rel
    *                         namespace.
    *
    * </pre>
    */
    //-------------------------------------------------------------------------
    void build_changeset_concordances(
            HrefDifference                       href_diff,
            Map<String, HashMap<String,String>>  rel_newmod_conc,
            Map<String, HashMap<String,String>>  deleted_conc,
            Map<String,String>                   newmod_file,
            Map<String,String>                   rel_href_in_conc,
            Map<String,String>                   rel_href_broken_fdep,
            List<HrefManifestEntry>              rel_newmod_manifest_list,
            String                               dst_webapp_url_base,
            String                               src_webapp_url_base,
            int                                  src_webapp_url_base_length,
            int                                  src_store_length,
            String                               dst_store,
            MD5                                  md5)
    {
        if ( log.isDebugEnabled() )
            log.debug("build_changeset_concordances");

        String                  href_attr;
        List<HrefManifestEntry> src_manifest_entry_list;

        href_attr = href_diff.getHrefAttr();
        src_manifest_entry_list =  href_diff.getHrefManifest().getManifestEntries();

        //
        // Build concordance of new or modified files
        //

        for ( HrefManifestEntry src_manifest_entry : src_manifest_entry_list )
        {
            String newmod_src_file = src_manifest_entry.getFileName();

            String newmod_dst_file =
                   dst_store + newmod_src_file.substring( src_store_length );

            String newmod_dst_file_md5 = md5.digest(newmod_dst_file.getBytes());

            newmod_file.put( newmod_dst_file_md5, newmod_dst_file );

            List<String>      src_href_list     = src_manifest_entry.getHrefs();
            ArrayList<String> rel_href_md5_list = new ArrayList<String>();

            for (String src_url : src_href_list)
            {
                // The src & dst urls are different for any given asset
                // so just keep track of the virtual host relative link
                // and take context-appropriate action later.   This makes
                // it easy to look up the link status in hashes without
                // needing to know what version it was originally saved;
                // there's no ".version--vXXX." to worry about in the hash
                // lookup and hostname case becomes a non-issue.

                String rel_url;
                if ( ! src_url.startsWith( src_webapp_url_base ) )
                {
                    rel_url = src_url;
                }
                else
                {
                    rel_url = src_url.substring( src_webapp_url_base_length );
                }

                String rel_url_md5 = md5.digest( rel_url.getBytes() );
                rel_href_md5_list.add( rel_url_md5 );

                HashMap<String,String> url_locations =
                  rel_newmod_conc.get( rel_url_md5 );

                // rel_href_in_conc has as keys any href in either
                // rel_newmod_conc or deleted_conc.  Note that because
                // the url could be new, its value is set to dst_url
                // rather than null.

                rel_href_in_conc.put( rel_url_md5, rel_url );

                if (  url_locations == null )
                {
                    url_locations = new HashMap<String,String>();
                    rel_newmod_conc.put( rel_url_md5, url_locations);
                }
                url_locations.put( newmod_dst_file_md5, null );
            }
            rel_newmod_manifest_list.add(
                new HrefManifestEntry( newmod_dst_file_md5,
                                       rel_href_md5_list));
        }


        // Add to deleted_conc any url that was once in SOURCE_TO_HREF
        // but now is not.   This allows the system to become aware that
        // a URL has been removed from a modified (but not deleted) file.

        for ( String newmod_file_md5  : newmod_file.keySet() )
        {
            List<String> old_source_href_list = null;

            try
            {
                old_source_href_list = attr_.getKeys( href_attr      + "/" +
                                                      SOURCE_TO_HREF + "/" +
                                                      newmod_file_md5);
            }
            catch (Exception e)
            {
                continue;
            }

            for ( String old_href_md5 : old_source_href_list )
            {
                HashMap<String,String> url_locations =
                                       rel_newmod_conc.get( old_href_md5 );


                if ( (url_locations == null) ||
                      ! url_locations.containsKey( newmod_file_md5 )
                   )
                {
                    // The url was once present in this file but now it isn't
                    rel_href_in_conc.put( old_href_md5, null );

                    if (url_locations == null )
                    {
                         url_locations = new HashMap<String,String>();
                         deleted_conc.put( old_href_md5, url_locations );
                    }
                    url_locations.put(newmod_file_md5, null );
                }
            }
        }


        //
        // Build concordance of deleted files
        //

        Map<String,String>  deleted_file_md5_map;
        deleted_file_md5_map = href_diff.getDeletedFileMd5();

        for (String deleted_file_md5 : deleted_file_md5_map.keySet() )
        {
            //
            // Get the hrefs that depended upon this deleted file
            //

            try
            {
                List<String> dependent_hrefs_md5_list =
                    attr_.getKeys( href_attr    + "/" +
                                   FILE_TO_HDEP + "/" +
                                   deleted_file_md5);

                for ( String href_md5 : dependent_hrefs_md5_list )
                {
                    rel_href_broken_fdep.put( href_md5, null );
                }
            }
            catch (Exception e)
            {
                // It's ok not to have any href dependencies
            }


            //
            // Get the set of hrefs that are now gone from deleted file
            //

            List<String> deleted_href_md5_list =
                            attr_.getKeys( href_attr      + "/" +
                                           SOURCE_TO_HREF + "/" +
                                           deleted_file_md5);


            for ( String deleted_href_md5 : deleted_href_md5_list )
            {
                // rel_href_in_conc contains as keys any hrefs
                // present in rel_newmod_conc or deleted_conc.
                //
                // Because the href is deleted, there's no need to worry
                // about the  md5->href mapping, so just use null
                // to allow some md5 -> href updates to be skipped.

                rel_href_in_conc.put( deleted_href_md5, null );

                HashMap<String,String> url_locations =
                        deleted_conc.get( deleted_file_md5 );

                if (url_locations == null )
                {
                    url_locations = new HashMap<String,String>();
                    deleted_conc.put( deleted_href_md5, url_locations );
                }
                url_locations.put(deleted_file_md5, null );
            }
        }
    }

    /*-------------------------------------------------------------------------
    *  merge_href_to_source_and_source_to_href --
    *       Merges HREF_TO_SOURCE and new SOURCE_TO_HREF data,
    *       and gets rid of stale hrefs.   Does not get rid
    *       of stale SOURCE_TO_HREF data (that's handled elsewhere).
    *------------------------------------------------------------------------*/
    void merge_href_to_source_and_source_to_href(
                 HrefDifference                       href_diff,
                 Map<String, HashMap<String,String>>  rel_newmod_conc,
                 Map<String, HashMap<String,String>>  deleted_conc,
                 Map<String,String>                   newmod_file,
                 Map<String,String>                   rel_href_in_conc,
                 List<HrefManifestEntry>              rel_newmod_manifest_list)
    {
        if ( log.isDebugEnabled() )
            log.debug("merge_href_to_source_and_source_to_href");


        String href_attr = href_diff.getHrefAttr();

        // Look at any href in the changeset   (ie: in a new/mod/del file)

        for (String rel_href_md5  :  rel_href_in_conc.keySet() )
        {
            // updated HREF_TO_SOURCE value for rel_href_md5
            MapAttribute new_href_to_source = new MapAttributeValue();

            Attribute old_href_to_source_attrib =
                    attr_.getAttribute( href_attr      + "/" +
                                        HREF_TO_SOURCE + "/" +
                                        rel_href_md5);

            Set<String> old_href_to_source_set =
                           ( old_href_to_source_attrib != null)
                           ? old_href_to_source_attrib.keySet()
                           : null;

            HashMap<String,String> deleted_locations =
                deleted_conc.get( rel_href_md5  );

            // Copy filtered list of locations into  new_href_to_source

            if  ( old_href_to_source_set != null )
            {
                for ( String location : old_href_to_source_set )
                {
                    if ( ( deleted_locations != null  &&
                           deleted_locations.containsKey( location )
                         ) || newmod_file.containsKey( location ))
                    {
                        continue;               // filter this location out
                    }
                    new_href_to_source.put(
                        location, new BooleanAttributeValue( true ));
                }
            }

            // Add to new_href_to_source any values in rel_newmod_conc

            HashMap<String,String> added_locations =
                rel_newmod_conc.get( rel_href_md5  );

            if  ( added_locations != null )
            {
                for (String location : added_locations.keySet() )
                {
                    new_href_to_source.put(
                        location, new BooleanAttributeValue( true ));
                }
            }

            // If new_href_to_source is empty, this is now a stale HREF
            if  ( new_href_to_source.size() == 0)
            {
                attr_.removeAttribute( href_attr  + "/" + HREF_TO_SOURCE,
                                       rel_href_md5);

                Attribute rsp =
                    attr_.getAttribute( href_attr      + "/" +
                                        HREF_TO_STATUS + "/" +
                                        rel_href_md5
                                      );

                boolean is_href_broken = true;
                if (rsp != null)
                {
                    int response_code = rsp.getIntValue();

                    if ( response_code < 400 ) { is_href_broken = false; }

                    attr_.removeAttribute( href_attr + "/" +  HREF_TO_STATUS,
                                           rel_href_md5);

                    attr_.removeAttribute( href_attr + "/" + STATUS_TO_HREF +
                                           "/" + response_code,
                                           rel_href_md5
                                         );

                    attr_.removeAttribute( href_attr   + "/" + MD5_TO_HREF,
                                           rel_href_md5);


                    Attribute old_fdep_attribute =
                        attr_.getAttribute( href_attr    + "/" +
                                            HREF_TO_FDEP + "/" +
                                            rel_href_md5);

                    try
                    {
                        attr_.removeAttribute( href_attr  + "/" + HREF_TO_FDEP,
                                               rel_href_md5);
                    }
                    catch (Exception xx)
                    {
                        // If you try to remove an attribute that does not exist,
                        // AttributeService throws an exception.  In this case,
                        // we don't care if is attribute wasn't there, so it's
                        // safe to ignore the exception.
                        //
                        // Two cases can cause this non-exceptional exception
                        // to be thrown:
                        //
                        //      [1]  An internal href that was always bad
                        //      [2]  An external href
                        //
                        // Neither are expected to have any file dependencies.
                    }

                    if ( old_fdep_attribute != null )
                    {
                        Set<String> old_fdep_set = old_fdep_attribute.keySet();
                        for (String old_fdep : old_fdep_set)
                        {
                            attr_.removeAttribute(href_attr    + "/" +
                                                  FILE_TO_HDEP + "/" + old_fdep,
                                                  rel_href_md5);
                        }
                    }
                }
            }
            else
            {
                // If possibly a new href, be sure md5 -> href mapping exists
                String rel_orig_href = rel_href_in_conc.get( rel_href_md5 );
                if  ( rel_orig_href != null )
                {
                    attr_.setAttribute( href_attr + "/" + MD5_TO_HREF,
                                        rel_href_md5,
                                        new StringAttributeValue( rel_orig_href ));
                }

                attr_.setAttribute( href_attr + "/" + HREF_TO_SOURCE,
                                    rel_href_md5,
                                    new_href_to_source );
            }
        }

        //  Update SOURCE_TO_HREF
        for ( HrefManifestEntry rel_md5_entry : rel_newmod_manifest_list)
        {
            String       dst_file_md5       = rel_md5_entry.getFileName();
            List<String> rel_href_md5_list  = rel_md5_entry.getHrefs();
            MapAttribute new_source_to_href = new MapAttributeValue();

            for ( String rel_href_md5 : rel_href_md5_list)
            {
                new_source_to_href.put( rel_href_md5,
                                        new BooleanAttributeValue( true ));
            }

            attr_.setAttribute( href_attr + "/" + SOURCE_TO_HREF,
                                dst_file_md5,
                                new_source_to_href);
        }
    }

    /*-------------------------------------------------------------------------
    *  update_href_status --
    *------------------------------------------------------------------------*/
    void update_href_status(
             String href_attr,
             HashMap<String,Pair<Integer,HashMap<String,String>>> status_map)
    {
        if ( log.isDebugEnabled() )
            log.debug("update_href_status");

        HashMap<Integer,String> status_cache = new HashMap<Integer,String>();

        for ( String rel_url_md5 : status_map.keySet() )
        {
            Pair<Integer,HashMap<String,String>>  src_status =
                status_map.get( rel_url_md5 );

            int  src_response_code  =  src_status.getFirst(); // new status
            HashMap<String,String> src_dst_fdep_md5_map =     // new fdep
                                       src_status.getSecond();


            // Get the old status of this href that's in a new/modified file:

            Attribute rsp = attr_.getAttribute( href_attr      + "/" +
                                                HREF_TO_STATUS + "/" +
                                                rel_url_md5
                                              );

            boolean status_already_correct = false;

            if (rsp != null)
            {
                // If this URL had a status value previously, but the status
                // was different from what it is now, remove the old info now.

                int  dst_response_code = rsp.getIntValue();

                if ( dst_response_code != src_response_code )
                {
                    attr_.removeAttribute( href_attr + "/" +  HREF_TO_STATUS,
                                           rel_url_md5);


                    attr_.removeAttribute( href_attr + "/" + STATUS_TO_HREF +
                                           "/" + dst_response_code,
                                           rel_url_md5
                                         );
                }
                else                                // The status of this
                {                                   // href has not changed.
                    status_already_correct = true;  // Therefore, don't bother
                }                                   // doing an update for it.
            }

            if ( ! status_already_correct )
            {
                attr_.setAttribute( href_attr + "/" + HREF_TO_STATUS,
                                    rel_url_md5,
                                    new IntAttributeValue( src_response_code ));


                if ( ! status_cache.containsKey( src_response_code ) )
                {
                    if ( ! attr_.exists(
                               href_attr      + "/" +  // do actual remote
                               STATUS_TO_HREF + "/" +  // call to see if
                               src_response_code )     // this status key
                       )                                // must be created
                    {
                        attr_.setAttribute(
                            href_attr + "/" + STATUS_TO_HREF,
                            "" + src_response_code,
                            new MapAttributeValue());
                    }
                    // never check again
                    status_cache.put( src_response_code, null );
                }

                attr_.setAttribute(
                    href_attr + "/" + STATUS_TO_HREF + "/" + src_response_code,
                    rel_url_md5,
                    new BooleanAttributeValue( true ));
            }



            // If an fdep is in fdep_already_present, there's no need to add it
            //
            HashMap<String,String> fdep_already_present =
                new HashMap<String,String>();

            Attribute old_fdep_attribute = null;   // If the href had a status,
            if (rsp != null)                       // it might have old fdep;
            {                                      // otherwise, it can't.
                old_fdep_attribute =
                    attr_.getAttribute( href_attr    + "/" +
                                        HREF_TO_FDEP + "/" +
                                        rel_url_md5);

                // If the fdep
                if ( old_fdep_attribute != null )
                {
                    // Note that List<String> src_dst_fdep_md5_map
                    // contains the (possibly null) incoming fdep list
                    // that's been translated into the dst namespace.
                    //
                    // If these lists are identical

                    ArrayList<String> stale_fdep_md5_list =
                        new ArrayList<String>();

                    Set<String> old_fdep_md5_set = old_fdep_attribute.keySet();
                    for (String old_fdep_md5 : old_fdep_md5_set)
                    {
                        if ( src_dst_fdep_md5_map.containsKey( old_fdep_md5 ) )
                        {
                            // no need to add this by hand later
                            fdep_already_present.put( old_fdep_md5, null );
                        }
                        else { stale_fdep_md5_list.add( old_fdep_md5 ); }
                    }

                    // Remove the stale file dependencies
                    for ( String stale_fdep_md5 :  stale_fdep_md5_list)
                    {
                        attr_.removeAttribute( href_attr    + "/" +
                                               HREF_TO_FDEP + "/" +
                                               rel_url_md5,
                                               stale_fdep_md5);

                        attr_.removeAttribute(href_attr    + "/" +
                                              FILE_TO_HDEP + "/" +
                                              stale_fdep_md5,
                                              rel_url_md5);
                    }
                }
            }

            // Add new file dependencies, skipping work if possible.
            for ( String src_dst_fdep_md5 : src_dst_fdep_md5_map.keySet())
            {
                if  ( fdep_already_present.containsKey( src_dst_fdep_md5 ) )
                {
                    continue;           // No need to add dependency
                }

                if ( ! attr_.exists( href_attr      + "/" +
                                     HREF_TO_FDEP   + "/" +
                                     rel_url_md5 )
                   )
                {
                    attr_.setAttribute(  href_attr + "/" + HREF_TO_FDEP,
                                         rel_url_md5,
                                         new MapAttributeValue());
                }

                if ( ! attr_.exists( href_attr      + "/" +
                                     FILE_TO_HDEP   + "/" +
                                     src_dst_fdep_md5 )
                   )
                {
                    attr_.setAttribute(  href_attr + "/" + FILE_TO_HDEP,
                                         src_dst_fdep_md5,
                                         new MapAttributeValue());
                }

                attr_.setAttribute(
                        href_attr + "/" + HREF_TO_FDEP + "/" + rel_url_md5,
                        src_dst_fdep_md5,
                        new BooleanAttributeValue( true ));

                attr_.setAttribute(
                        href_attr + "/" + FILE_TO_HDEP + "/" + src_dst_fdep_md5,
                        rel_url_md5,
                        new BooleanAttributeValue( true ));
            }
        }
    }


    //-------------------------------------------------------------------------
    /**
    * Build a version of href_status map that translates all
    * values from src into md5(dst) namespace, and uses
    * a map rather than a list of strings (this makes
    * comparisons of the old list & new map easy later on).
    */
    //-------------------------------------------------------------------------
    HashMap<String,Pair<Integer,HashMap<String,String>>> make_rel_status_map(
        HrefStatusMap href_status_map,
        String        dst_webapp_url_base,
        String        src_webapp_url_base,
        int           src_webapp_url_base_length,
        int           src_store_length,
        String        dst_store,
        MD5           md5)
    {
        if ( log.isDebugEnabled() )
            log.debug("make_rel_status_map");

        // Extract the raw map (avoids the need to use syncrhonized func)
        Map<String,Pair<Integer,List<String>>> src_status_map =
                                               href_status_map.getStatusMap();

        HashMap<String,Pair<Integer,HashMap<String,String>>>
            rel_status_md5_map =
                new HashMap<String,Pair<Integer,HashMap<String,String>>>(
                    src_status_map.size());

        for ( String src_url  :  src_status_map.keySet() )
        {
            String rel_url;
            if ( ! src_url.startsWith( src_webapp_url_base ) )
            {
                rel_url = src_url;
            }
            else
            {
                rel_url = src_url.substring( src_webapp_url_base_length );
            }

            String rel_url_md5 = md5.digest( rel_url.getBytes() );

            Pair<Integer,List<String>>  src_status =
                                        src_status_map.get( src_url );

            List<String> src_fdep_list  = src_status.getSecond();   // maybe null

            HashMap<String,String> src_dst_fdep_md5_map =
                new HashMap<String,String>();

            if (src_fdep_list != null )
            {
                for ( String src_fdep : src_fdep_list )
                {
                    String dst_fdep =
                       dst_store + src_fdep.substring( src_store_length );
                    String dest_fdep_md5 = md5.digest( dst_fdep.getBytes());
                    src_dst_fdep_md5_map.put( dest_fdep_md5, null );
                }
            }

            // Note:  src_dst_fdep_md5_map will never be null,
            //        but it might be empty

            rel_status_md5_map.put(rel_url_md5,
                                   new Pair<Integer,HashMap<String,String>>(
                                           src_status.getFirst(),
                                           src_dst_fdep_md5_map));
        }

        return rel_status_md5_map;
    }

    //-------------------------------------------------------------------------
    /**
    *  Merges href difference into destnation table (e.g.: for staging)
    */
    //-------------------------------------------------------------------------
    public void mergeHrefDiff( HrefDifference         href_diff,
                               HrefValidationProgress progress)
                throws         AVMNotFoundException,
                               SocketException,
                               SSLException,
                               LinkValidationAbortedException
    {
        if ( log.isDebugEnabled() )
            log.debug("mergeHrefDiff");


        MD5    md5                        = new MD5();
        String dst_store                  = href_diff.getDstStore();
        String src_store                  = href_diff.getSrcStore();
        int    src_store_length           = src_store.length();
        String dst_webapp_url_base        = href_diff.getDstWebappUrlBase();
        String src_webapp_url_base        = href_diff.getSrcWebappUrlBase();
        int    src_webapp_url_base_length = src_webapp_url_base.length();
        String href_attr                  = href_diff.getHrefAttr();


        // Build various concordances & lookup tables for this changeset:
        //
        Map<String,String> newmod_file          = new HashMap<String, String>();
        Map<String,String> rel_href_in_conc     = new HashMap<String, String>();
        Map<String,String> rel_href_broken_fdep = new HashMap<String, String>();
        HashMap<String, HashMap<String,String>>  rel_newmod_conc;
        HashMap<String, HashMap<String,String>>  deleted_conc;
        List<HrefManifestEntry>                  rel_newmod_manifest_list;

        rel_newmod_conc          = new HashMap<String,HashMap<String,String>>();
        deleted_conc             = new HashMap<String,HashMap<String,String>>();
        rel_newmod_manifest_list = new ArrayList<HrefManifestEntry>();

        build_changeset_concordances(
             href_diff,
             rel_newmod_conc,             // updated by this call
             deleted_conc,                // updated by this call
             newmod_file,                 // updated by this call
             rel_href_in_conc,            // updated by this call
             rel_href_broken_fdep,        // updated by this call
             rel_newmod_manifest_list,
             dst_webapp_url_base,
             src_webapp_url_base,
             src_webapp_url_base_length,
             src_store_length,
             dst_store,
             md5);


        // The old HREF_TO_SOURCE needs to be updated with the delta as follows:
        //
        //    For each URL in rel_href_in_conc,
        //       o  Get old HREF_TO_SOURCE (if null, create empty set).
        //       o  Remove from this set any file in deleted_conc or newmod_file
        //       o  Add back to this set any file in rel_newmod_conc
        //       o  Update HREF_TO_SOURCE
        //       o  If rel_href_in_conc value is non-null, update MD5_TO_HREF
        //       o  If new HREF_TO_SOURCE is empty, remove the href:
        //              - [2] HREF_TO_SOURCE
        //              - [3] HREF_TO_STATUS
        //              - [4] STATUS_TO_HREF
        //              - [6] MD5_TO_HREF
        //              - [7] HREF_TO_FDEP
        //              - [8] FILE_TO_HREF
        //
        //
        //  Update href status/dep info for eveything in the href_status_map
        //        o  Get old status via HREF_TO_STATUS
        //           If not there or different:
        //             [3]  Update status
        //             [4]  Remove prior status from STATUS_TO_HREF if necessary
        //
        //        o  Get old dependency list from HREF_TO_FDEP
        //           For every file not in href_status_map,
        //                remove it from new HREF_TO_FDEP and FILE_TO_HDEP.
        //           For every file in href_status_map not in old HREF_TO_FDEP
        //                add it to new HREF_TO_FDEP and FILE_TO_HDEP.
        //
        //
        //    For each deleted file in:  deleted_file_md5_map
        //
        //       o  Remove:
        //              - [1] SOURCE_TO_HREF
        //
        //       o  If HREF_TO_FDEP for the file is empty, then remove:
        //              - [5] MD5_TO_FILE
        //
        //    Also, ensure:
        //            any new files get an md5 -> file mapping
        //            any new hrefs get an md5 -> href mapping
        //

        merge_href_to_source_and_source_to_href(
            href_diff,
            rel_newmod_conc,
            deleted_conc,
            newmod_file,
            rel_href_in_conc,
            rel_newmod_manifest_list);


        // Ensure any new or modified file has an associated md5 -> file mapping
        for ( String file_md5 : newmod_file.keySet() )
        {
            attr_.setAttribute(
                    href_attr + "/" + MD5_TO_FILE,
                    file_md5,
                    new StringAttributeValue( newmod_file.get( file_md5 )));
        }


        //
        // Update href status info
        //


        // Get the status of the modified links.
        HrefStatusMap  href_status_map  = href_diff.getHrefStatusMap();

        HashMap<String,Pair<Integer,HashMap<String,String>>>
            src_dst_status_md5_map  =  make_rel_status_map(
                                            href_status_map,
                                            dst_webapp_url_base,
                                            src_webapp_url_base,
                                            src_webapp_url_base_length,
                                            src_store_length,
                                            dst_store,
                                            md5);


        // Reset the status of the urls, if necessary.
        //
        // Because src_dst_status_md5_map is derived from src_status_map
        // by translating values into the dst namespace and taking
        // their md5sum, all operations below can work directly
        // using md5(dst) values.


        update_href_status( href_attr, src_dst_status_md5_map);


        //
        // Clean up obsolete file info when a file has been deleted
        //

        Map<String,String>  deleted_file_md5_map;
        deleted_file_md5_map = href_diff.getDeletedFileMd5();

        for (String deleted_file_md5 : deleted_file_md5_map.keySet() )
        {
            attr_.removeAttribute(href_attr + "/" + SOURCE_TO_HREF,
                                  deleted_file_md5);

            Attribute old_hdep_attribute =
                attr_.getAttribute( href_attr    + "/" +
                                    FILE_TO_HDEP + "/" +
                                    deleted_file_md5);

            // Let's see if any hrefs depend on the deleted file
            if ( old_hdep_attribute != null )
            {
                if ( old_hdep_attribute.size() == 0 )
                {
                    attr_.removeAttribute( href_attr   + "/" + FILE_TO_HDEP,
                                           deleted_file_md5);

                    // No hrefs depend on this, so md5 -> file isn't needed
                    attr_.removeAttribute( href_attr   + "/" + MD5_TO_FILE,
                                           deleted_file_md5);
                }
            }
            else
            {
                // No hrefs depend on this, so md5 -> file isn't needed
                attr_.removeAttribute( href_attr   + "/" + MD5_TO_FILE,
                                       deleted_file_md5);
            }
        }


        recheckBrokenLinks(href_diff,
                           dst_webapp_url_base,
                           src_webapp_url_base,
                           src_webapp_url_base_length,
                           src_store_length,
                           dst_store,
                           href_attr,
                           rel_href_broken_fdep,
                           md5,
                           progress);
    }


    //-------------------------------------------------------------------------
    /**
    *   Walk the set of links believed to be broken looking for hrefs that
    *   are no longer broken (due to adding a file, fixing a server, etc.).
    */
    //-------------------------------------------------------------------------
    public void recheckBrokenLinks(
                    HrefDifference         href_diff,
                    String                 dst_webapp_url_base,
                    String                 src_webapp_url_base,
                    int                    src_webapp_url_base_length,
                    int                    src_store_length,
                    String                 dst_store,
                    String                 href_attr,
                    Map<String,String>     rel_href_broken_fdep,
                    MD5                    md5,
                    HrefValidationProgress progress)
           throws   AVMNotFoundException,
                    SocketException,
                    SSLException,
                    LinkValidationAbortedException
    {
        if ( log.isDebugEnabled() )
            log.debug("recheckBrokenLinks");

        List<Pair<String, Attribute>> links =
            attr_.query( href_attr + "/" + STATUS_TO_HREF,
                         new AttrAndQuery(new AttrQueryGTE( "" + 400 ),
                                          new AttrQueryLTE( "" + 599 )));

        if  ( links == null ) {return;}

        HrefStatusMap  href_status_map = new HrefStatusMap();

        int    dst_webapp_url_base_length = dst_webapp_url_base.length();

        for ( Pair<String, Attribute> link : links  )
        {
            String  response_code_str = link.getFirst();
            int     response_code     = Integer.parseInt( response_code_str );
            Set<String> href_md5_set  = link.getSecond().keySet();

            for ( String href_md5 : href_md5_set )
            {
                if ( rel_href_broken_fdep.containsKey( href_md5 ) )
                {
                    // This URL had broken status, and also seems to
                    // be further broken by a newly unsatisified file
                    // dependency (c.f.: the late Grigori Rasputin).
                    // Rather than double-check, skip it for now and
                    // let the rel_href_broken_fdep handle it (below).
                    //
                    continue;
                }

                Attribute rel_href_str_attr =
                   attr_.getAttribute( href_attr   + "/" +
                                       MD5_TO_HREF + "/" +
                                       href_md5);

                if (  rel_href_str_attr == null )
                {
                    if ( log.isDebugEnabled() )
                        log.debug("No MD5_TO_HREF (purged): " + href_md5);

                    continue;
                }

                String rel_href_str = rel_href_str_attr.getStringValue();


                boolean get_lookup_dependencies = (rel_href_str.charAt(0) == '/');

                String href_str;
                if  ( get_lookup_dependencies )
                {
                    // Convert it into the src namespace for validation.
                    href_str = src_webapp_url_base + rel_href_str;
                }
                else
                {
                    href_str = rel_href_str;
                }

                validate_uri(
                    href_str,                 // href to revalidate
                    href_status_map,          // new status map
                    get_lookup_dependencies,  // only when url is internal
                    false,                    // don't fetch urls in result
                    progress);
            }
        }


        //
        // Recheck hrefs that depended on a file that
        // has since been deleted
        //

        for (String href_md5 :  rel_href_broken_fdep.keySet())
        {
            Attribute rel_href_str_attr =
                attr_.getAttribute( href_attr   + "/" +
                                    MD5_TO_HREF + "/" +
                                    href_md5);


             if ( rel_href_str_attr == null )
             {
                if ( log.isDebugEnabled() )
                    log.debug("No MD5_TO_HREF (purged): " + href_md5);

                 continue;
             }

             String rel_href_str = rel_href_str_attr.getStringValue();

             String href_str = src_webapp_url_base + rel_href_str;

             validate_uri(
                 href_str,                 // href to revalidate
                 href_status_map,          // new status map
                 true,                     // always internal
                 false,                    // don't fetch urls in result
                 progress);                // don't monitor progress
        }



        HashMap<String,Pair<Integer,HashMap<String,String>>>
            src_dst_status_md5_map  =  make_rel_status_map(
                                            href_status_map,
                                            dst_webapp_url_base,
                                            src_webapp_url_base,
                                            src_webapp_url_base_length,
                                            src_store_length,
                                            dst_store,
                                            md5);

        update_href_status( href_attr, src_dst_status_md5_map);

        return;
    }


    /*-------------------------------------------------------------------------
    *  extract_links_from_dir --
    *------------------------------------------------------------------------*/
    void  extract_links_from_dir( int                    version,
                                  String                 dir,
                                  String                 fqdn,
                                  int                    port,
                                  String                 req_path,
                                  HrefManifest           href_manifest,
                                  HrefStatusMap          href_status_map,
                                  HrefValidationProgress progress,
                                  int                    depth)
          throws                  AVMNotFoundException,
                                  SocketException,
                                  SSLException,
                                  LinkValidationAbortedException
    {
        if ( log.isDebugEnabled() )
            log.debug("extract_links_from_dir");

        Map<String, AVMNodeDescriptor> entries = null;

        // Ensure that the virt server is virtualizing this version

        try
        {
            // e.g.:   42, "mysite:/www/avm_webapps/ROOT/moo"
            entries = avm_.getDirectoryListing( version, dir );
        }
        catch (Exception e)
        {
            if ( log.isErrorEnabled() )
            {
                log.error("Could not list version: " + version +
                         " of directory: " + dir + "  " + e.getMessage());
            }
            return;
        }

        for ( Map.Entry<String, AVMNodeDescriptor> entry  : entries.entrySet() )
        {
            String            entry_name = entry.getKey();
            AVMNodeDescriptor avm_node   = entry.getValue();
            String            avm_path   = dir +  "/" + entry_name;

            if ( (depth == 0) &&
                 (entry_name.equalsIgnoreCase("META-INF")  ||
                  entry_name.equalsIgnoreCase("WEB-INF")
                 )
               )
            {
                continue;
            }

            if  ( avm_node.isDirectory() )
            {
                extract_links_from_dir( version,
                                        avm_path,
                                        fqdn,
                                        port,
                                        req_path + "/" + entry_name,
                                        href_manifest,
                                        href_status_map,
                                        progress,
                                        depth + 1);

                // stats for monitoring
                if ( progress != null )
                {
                    progress.incrementDirUpdateCount();
                }
            }
            else
            {
                extract_links_from_file( avm_path,
                                         fqdn,
                                         port,
                                         req_path + "/" + entry_name,
                                         href_manifest,
                                         href_status_map,
                                         progress);

                // stats for monitoring
                if ( progress != null )
                {
                    progress.incrementFileUpdateCount();
                }
            }
        }
    }


    /*-------------------------------------------------------------------------
    *  extract_links_from_file --
    *------------------------------------------------------------------------*/
    void  extract_links_from_file( String                 src_path,
                                   String                 fqdn,
                                   int                    port,
                                   String                 req_path,
                                   HrefManifest           href_manifest,
                                   HrefStatusMap          href_status_map,
                                   HrefValidationProgress progress)
          throws                   AVMNotFoundException,
                                   SocketException,
                                   SSLException,
                                   LinkValidationAbortedException
    {
        String implicit_url = null;
        try
        {
            // You might think that URLEncoder or URI would be
            // able to encode individual segements, but URLEncoder
            // turns " " into "+" rather than "%20".   That's ok for
            // x-www-form-encoding, but not for the request path;
            // the URL class just lets " " remain " ".  Nothing in
            // class contains a cross reference to URI and this issue.

            URI u = new URI( "http",       // scheme
                             null,         // userinfo
                             fqdn,         // host
                             port,         // port
                             req_path,     // request path
                             null,         // query
                             null);        // frag

            implicit_url = u.toASCIIString();
        }
        catch (Exception e)
        {
            if ( log.isErrorEnabled() )
                log.error("Could not create URI for:  " + req_path +
                           "   " + e.getClass().getName() +
                           "   " + e.getMessage());

            return;
        }


        Set<String> urls  = validate_uri( implicit_url,
                                           href_status_map,
                                           true,            // get lookup dep
                                           true,            // get urls
                                           progress);


        boolean saw_gen_url = false;

        ArrayList<String> href_arraylist;
        if ( urls == null )
        {
            href_arraylist = new ArrayList<String>( 1 );
        }
        else
        {
            href_arraylist = new ArrayList<String>( urls.size() + 1 );

            for (String  resp_url  : urls )
            {
                if ( ! saw_gen_url  && implicit_url.equals(resp_url))
                {
                    saw_gen_url = true;
                }
                href_arraylist.add( resp_url );
            }
        }

        // Add imlicit (dead reckoned) url if not contained in file

        if ( ! saw_gen_url )
        {
            href_arraylist.add( implicit_url );
        }


        href_manifest.add( new HrefManifestEntry( src_path, href_arraylist ));
    }

    /*-------------------------------------------------------------------------
    *  validate_uri --
    *        Validate one hyperlink 
    *------------------------------------------------------------------------*/
    Set<String>   validate_uri( String                 uri_str,
                                HrefStatusMap          href_status_map,
                                boolean                get_lookup_dependencies,
                                boolean                get_urls,
                                HrefValidationProgress progress)
                  throws        SocketException,
                                SSLException,
                                LinkValidationAbortedException
    {
        HttpURLConnection conn = null;
        URL               url  = null;
        int               response_code;

        // Allow operation to be aborted before the potentially
        // long-running act of pulling on a URL.

        if ( progress != null && progress.isAborted() )
        {
            throw new LinkValidationAbortedException();
        }

        // Certain URI schema types are hard to validate,
        // so just claim they work if they've been excluded 

        if  ( scheme_excluder_.matches( uri_str ) )
        {
            // TODO: 
            //   In the future, it would be nice if there were a good way
            //   to at least validate FTP links. 
            //
            // See also:
            //   http://www.javaworld.com/javaworld/jw-04-2003/jw-0404-ftp.html
            //   http://java.sun.com/developer/onlineTraining/protocolhandlers/
            //
            // For now, let's just not claim they're broken.
            // Hack:  set status to 299.

            response_code = 299;

            progress.incrementUrlUpdateCount();

            if ( log.isDebugEnabled() )
                log.debug("Response code (simulated) for '" + 
                           uri_str + "': " + response_code);

            href_status_map.put(
                uri_str, new Pair<Integer,List<String>>(response_code,null) );

            return null;
        }


        try
        {
            // The URL class is much too strict, and
            // the URI class is much too lax.
            // Where's the 3rd Java Bear, huh?
            // Nothing is "just right". :(  

            url = new URL( uri_str );
        }
        catch (Exception e)
        {
            progress.incrementUrlUpdateCount();

            if ( log.isDebugEnabled() )
                log.debug("Bad URI:  " + uri_str + "  " +
                          e.getClass().getName() +  "  " + e.getMessage());

            return null;
        }

        try
        {
            // Oddly, url.openConnection() does not actually
            // open a connection; it merely creates a connection
            // object that is later opened via connect() within
            // the UriExtractor.
            //
            conn = (HttpURLConnection) url.openConnection();
        }
        catch (Exception e )
        {
            // You could have a bogus protocol or some other probem.
            //
            // <a href=>xxx</a>
            // <a href='bogus-moo://xh:324/s'>xxx</a>
            // Causes: java.net.MalformedURLException
            //
            // ... and so on.

            if ( log.isDebugEnabled() )
                log.debug("Cannot connect to:  " + uri_str + "  " +
                          e.getClass().getName() +  "  " + e.getMessage());

            // Rather than update the URL status just let it retain
            // whatever status it had before, and assume this is
            // an ephemeral network failure;  "ephemeral" here means
            // "on all instances of this url for this validation."

            return null;
        }

        if ( get_lookup_dependencies )
        {
            conn.addRequestProperty(
                CacheControlFilter.LOOKUP_DEPENDENCY_HEADER, "true" );
        }

        // "Infinite" timeouts that aren't really infinite
        // are a bad idea in this context.  If it takes more
        // than 15 seconds to connect or more than 60 seconds
        // to read a response, give up.
        //
        UriExtractor uri_extractor = new UriExtractor();

        if (get_lookup_dependencies)    // local file
        {
            conn.setConnectTimeout( local_connect_timeout_ );
            conn.setReadTimeout(    local_read_timeout_    );
        }
        else                            // remote file
        {
            conn.setConnectTimeout( remote_connect_timeout_ );
            conn.setReadTimeout(    remote_read_timeout_    );
        }

        conn.setUseCaches( false );                 // handle caching manually

        // The only reason to fetch the file data is if it's going to be parsed
        // for hyperlinks.   Therefore, use HEAD rather than GET when possible,
        // to save bandwidth & time.

        if ( ! get_urls ||
             (  (href_bearing_request_path_matcher_ != null ) &&
               ! href_bearing_request_path_matcher_.matches( url.getPath() )
             )
          )
        {
            try { conn.setRequestMethod( "HEAD" ); }
            catch (java.net.ProtocolException pex)
            {                                         // There would need to be
                return null;                          // something very wrong
            }                                         // with the link here!
        }


        if ( log.isDebugEnabled() )
            log.debug("About to fetch URL: " + uri_str );

        uri_extractor.setConnection( conn );

        try { response_code = conn.getResponseCode(); }
        catch ( SocketException se )
        {
            // This could be either of two major problems:
            //   java.net.SocketException
            //      java.net.ConnectException        likely: Server down
            //      java.net.NoRouteToHostException  likely: firewall/router

            // If we're trying to get lookup
            // dependencies, this is a fatal
            // error fetching virtualized

            if  ( get_lookup_dependencies )
            {
                if ( log.isErrorEnabled() )
                    log.error("Error validating internal link: " +
                              se.getClass().getName() + " " + se.getMessage() );

                throw se;
            }
            else
            {                                 
                // It's an external link, so
                // just call it a link failure.

                if ( log.isDebugEnabled() )
                    log.debug("Error validating external link: " +
                               se.getClass().getName() + " " + se.getMessage() );

                response_code = 400; 
            }
        }
        catch ( SSLException ssle )
        {
            // SSL issues
            if  ( get_lookup_dependencies )
            {
                if ( log.isErrorEnabled() )
                    log.error("Error validating internal link via ssl: " +
                              ssle.getClass().getName() +
                              "   " + ssle.getMessage() );

                throw ssle;
            }
            else
            { 
                // It's an external link, so
                // just call it a link failure.

                if ( log.isDebugEnabled() )
                    log.debug("Error validating external https link: " +
                               ssle.getClass().getName()         + " " + 
                               ssle.getMessage() );

                response_code = 400; 
            }
        }
        catch (IOException ioe)
        {
            // java.net.UnknownHostException
            // .. or other things, possibly due to a mist-typed url
            // or other bad/interrupted request.
            //
            // Even if this is a local link, let's keep going.

            if ( log.isDebugEnabled() )
                log.debug("Could not fetch response code: " + ioe.getMessage());

            response_code = 400;                // probably a bad request
        }

        if ( log.isDebugEnabled() )
            log.debug("Response code for '" + uri_str + "': " + response_code);

        // deal with resonse code

        if ( ! get_lookup_dependencies  ||
             ( response_code < 200 || response_code >= 300)
           )
        {
            // The remainder of this function deals with tracking lookup
            // dependencies.  Because  we only care about the links that
            // URL's page contains if we're tracking dependencies, do
            // an early return here.

            href_status_map.put(
                uri_str, new Pair<Integer,List<String>>(response_code,null) );

            if  ( progress != null )
            {
                progress.incrementUrlUpdateCount();
            }

            return null;
        }

        // Rather than just fetch the 1st LOOKUP_DEPENDENCY_HEADER
        // in the response, to be paranoid deal with the possiblity that
        // the information about what AVM files have been accessed is stored
        // in more than one of these headers (though it *should* be all in 1).
        //
        // Unfortunately, getHeaderFieldKey makes the name of the 0-th header
        // return null, "even though the 0-th header has a value".  Thus the
        // loop below is 1-based, not 0-based.
        //
        // "It's a madhouse! A madhouse!"
        //            -- Charton Heston playing the character "George Taylor"
        //               Planet of the Apes, 1968
        //

        ArrayList<String> dependencies = new ArrayList<String>();

        String header_key = null;
        for (int i=1; (header_key = conn.getHeaderFieldKey(i)) != null; i++)
        {
            if (!header_key.equals(CacheControlFilter.LOOKUP_DEPENDENCY_HEADER))
            {
                continue;
            }

            String header_value = null;
            try
            {
                header_value =
                    URLDecoder.decode( conn.getHeaderField(i), "UTF-8");
            }
            catch (Exception e)
            {
                if ( log.isErrorEnabled() )
                {
                    log.error("Skipping undecodable response header: " +
                              conn.getHeaderField(i));
                }
                continue;
            }

            // Each lookup dependency header consists of a comma-separated
            // list file names.
            String [] lookup_dependencies = header_value.split(", *");

            for (String dep : lookup_dependencies )
            {
                dependencies.add( dep );
            }
        }

        // files upon which uri_str URL depends.
        href_status_map.put(
           uri_str, new Pair<Integer,List<String>>(response_code,dependencies));

        if ( progress != null )
        {
            progress.incrementUrlUpdateCount();
        }

        if ( ! get_urls )
        {
            return null;
        }

        Set<String> extracted_uris = null;
        try
        {
            extracted_uris = uri_extractor.extractURIs();
        }
        catch (Exception e)
        {
            if ( log.isErrorEnabled() )
                log.error("Could not parse: " + uri_str );
        }

        return extracted_uris;
    }

    //-------------------------------------------------------------------------
    /**
    *   Fetches the attribute stem value associated with a given dns name.
    *   Unlike setAttributeStemForDnsName(), if the AttributeService
    *   attributes are not present, they will *not* be created.
    *   <p>
    *   This partitioning makes static analysis of what modifies
    *   attribute services more obvious.
    */
    //-------------------------------------------------------------------------
    String getAttributeStemForDnsName( String dns_name )
    {
        // Given a store name X has a dns name   a.b.c
        // The attribute key pattern used is:   .href/c/b/a
        //
        // This guarantees if a segment contains a ".", it's not a part
        // of the store's fqdn.  Thus, "." can be used to delimit the end
        // of the store, and the begnining of the version-specific info.
        //

        StringBuilder str  = new StringBuilder( dns_name.length() );
        str.append( HREF );

        // Create top level .href key if necessary

        String [] seg = dns_name.split("\\.");
        String pth;
        for (int i= seg.length -1 ; i>=0; i--)
        {
            str.append("/" + seg[i] );
        }
        String result = str.toString();
        if ( result == null )
        {
            throw new IllegalArgumentException("Invalid DNS name: " + dns_name);
        }
        return result;
    }


    //-----------------------------------------------------------------------
    /**
    *   Fetches the attribute stem value associated with a given dns name.
    *   Unlike getAttributeStemForDnsName(), if the AttributeService
    *   attributes are not present, they *will* also be created.
    *   <p>
    *   This partitioning makes static analysis of what modifies
    *   attribute services more obvious.
    */
    //-----------------------------------------------------------------------
    String setAttributeStemForDnsName( String  dns_name)
    {
        StringBuilder str  = new StringBuilder( dns_name.length() );
        str.append( HREF );

        // Create top level .href key if necessary
        if (  ! attr_.exists( HREF ) )
        {
            MapAttribute map = new MapAttributeValue();
            attr_.setAttribute("", HREF, map );
        }

        String [] seg = dns_name.split("\\.");
        String pth;
        for (int i= seg.length -1 ; i>=0; i--)
        {
            pth = str.toString();
            if ( ! attr_.exists( pth + "/" + seg[i] ) )
            {
                MapAttribute map = new MapAttributeValue();
                attr_.setAttribute( pth , seg[i], map );
            }

            str.append("/" + seg[i] );
        }
        String result = str.toString();
        if ( result == null )
        {
            throw new IllegalArgumentException("Invalid DNS name: " + dns_name);
        }
        return result;
    }

    /*------------------------------------------------------------------------*/
    /**
    *  Updates collection of all files in a dir that are "gone" now,
    *  and the collection of hyperlinks that depended upon them.
    *  When the total set of files that are "gone" has been
    *  established, this info is used to find broken hyperlinks
    *  that still exist within files that are not "gone".
    */
    /*------------------------------------------------------------------------*/
    void update_dir_gone_broken_hdep_cache(
                int                    dst_version,
                String                 dst_path,
                Map<String,String>     deleted_file_md5,
                Map<String,String>     broken_hdep_cache,
                String                 href_attr,
                MD5                    md5,
                HrefValidationProgress progress)
    {
        if ( path_excluder_ != null && path_excluder_.matches( dst_path ))
        {
            return;
        }

        Map<String, AVMNodeDescriptor> entries = null;

        try
        {
            entries = avm_.getDirectoryListing( dst_version, dst_path);
        }
        catch (Exception e)
        {
            if ( log.isErrorEnabled() )
                log.error("Could not list version: " + dst_version +
                         " of directory: " + dst_path + "  " + e.getMessage());

            return;
        }


        for ( Map.Entry<String, AVMNodeDescriptor> entry  : entries.entrySet() )
        {
            String            entry_name = entry.getKey();
            AVMNodeDescriptor avm_node   = entry.getValue();
            String            avm_path   = dst_path +  "/" + entry_name;

            if  ( avm_node.isDirectory() )
            {
                update_dir_gone_broken_hdep_cache( dst_version,
                                                   avm_path,
                                                   deleted_file_md5,
                                                   broken_hdep_cache,
                                                   href_attr,
                                                   md5,
                                                   progress
                                                 );
                // stats for monitoring
                if ( progress != null )
                {
                    progress.incrementDirUpdateCount();
                }
            }
            else if ( avm_node.isFile() )
            {
                update_file_gone_broken_hdep_cache( avm_path,
                                                    deleted_file_md5,
                                                    broken_hdep_cache,
                                                    href_attr,
                                                    md5,
                                                    progress
                                                  );
                // stats for monitoring
                if ( progress != null )
                {
                    progress.incrementFileUpdateCount();
                }
            }
        }
    }

    /*------------------------------------------------------------------------*/
    /**
    *  Updates collection of all files that are "gone" now,
    *  and the collection of hyperlinks that depended upon them.
    *  When the total set of files that are "gone" has been
    *  established, this info is used to find broken hyperlinks
    *  that still exist within files that are not "gone".
    */
    /*------------------------------------------------------------------------*/
    void update_file_gone_broken_hdep_cache(
                String                 dst_path,
                Map<String,String>     deleted_file_md5,
                Map<String,String>     broken_hdep_cache,
                String                 href_attr,
                MD5                    md5,
                HrefValidationProgress progress)
    {
        if ( path_excluder_ != null && path_excluder_.matches( dst_path ))
        {
            return;
        }

        String file_gone_md5 =  md5.digest( dst_path.getBytes() );

        Attribute  dependent_hrefs_md5_attrib =
                         attr_.getAttribute( href_attr    + "/" +
                                             FILE_TO_HDEP + "/" +
                                             file_gone_md5);

        if ( dependent_hrefs_md5_attrib != null )
        {
            Set<String> dependent_hrefs_md5 =
                dependent_hrefs_md5_attrib.keySet();

            for ( String href_md5 :  dependent_hrefs_md5 )
            {
                 broken_hdep_cache.put(href_md5, null);
            }
        }
        deleted_file_md5.put( file_gone_md5, null );
    }


    /*-------------------------------------------------------------------------
    *  getBrokenHrefManifestEntries --
    *
    *   File: test:/www/avm_webapps/ROOT/index.html
    *      /bad_internal.html
    *      /fixable_internal.html
    *      /purgeable_bad_internal.html
    *      http://a-site-that-is-quite-dead.com
    *
    *
    *------------------------------------------------------------------------*/
    public List<HrefManifestEntry> getBrokenHrefManifestEntries(
                                          String   storeNameOrWebappPath)
                                   throws AVMNotFoundException
    {
        return getHrefManifestEntries( storeNameOrWebappPath, 400, 599);
    }

    /*-------------------------------------------------------------------------
    *  getHrefManifestEntries --
    *
    *------------------------------------------------------------------------*/
    public List<HrefManifestEntry> getHrefManifestEntries(
                                      String storeNameOrWebappPath,
                                      int    statusGTE,
                                      int    statusLTE)
                                   throws AVMNotFoundException
    {
        ValidationPathParser p =
            new ValidationPathParser(avm_, storeNameOrWebappPath);

        String store           = p.getStore();
        String webapp_name     = p.getWebappName();
        String app_base        = p.getAppBase();
        String dns_name        = p.getDnsName();

        String status_gte = "" + statusGTE;
        String status_lte = "" + statusLTE;

        HashMap<String, ArrayList<String> > href_manifest_map =
            new HashMap<String, ArrayList<String> >();

        // Example value:  ".href/mysite"
        String store_attr_base =
               getAttributeStemForDnsName( dns_name );

        int version = getLatestSnapshotID( store );

        if  ( webapp_name != null )
        {
            getHrefManifestEntriesFromWebapp( href_manifest_map,
                                              webapp_name,
                                              store_attr_base,
                                              status_gte,
                                              status_lte,
                                              dns_name);
        }
        else
        {
            Map<String, AVMNodeDescriptor> webapp_entries = null;

            // e.g.:   42, "mysite:/www/avm_webapps"
            webapp_entries = avm_.getDirectoryListing(version, app_base );

            for ( Map.Entry<String, AVMNodeDescriptor>
                  webapp_entry  :  webapp_entries.entrySet()
                )
            {
                webapp_name = webapp_entry.getKey();  // my_webapp
                AVMNodeDescriptor avm_node    = webapp_entry.getValue();

                if ( webapp_name.equalsIgnoreCase("META-INF")  ||
                     webapp_name.equalsIgnoreCase("WEB-INF")
                   )
                {
                    continue;
                }

                if  ( avm_node.isDirectory() )
                {
                    getHrefManifestEntriesFromWebapp( href_manifest_map,
                                                      webapp_name,
                                                      store_attr_base,
                                                      status_gte,
                                                      status_lte,
                                                      dns_name );
                }
            }
        }

        // The user will always want to see the list of files sorted
        ArrayList<String> file_names =
            new ArrayList<String>( href_manifest_map.keySet() );

        Collections.sort (file_names );

        // Create sorted list from sorted keys of map

        ArrayList<HrefManifestEntry> href_manifest_list =
            new ArrayList<HrefManifestEntry>(file_names.size());

        for (String file_name : file_names )
        {
            List<String> hlist = href_manifest_map.get( file_name );
            Collections.sort( hlist );
            href_manifest_list.add( new HrefManifestEntry(file_name, hlist ) );
        }

        return href_manifest_list;
    }


    /*-------------------------------------------------------------------------
    *  getHrefManifestEntriesFromWebapp --
    *
    *------------------------------------------------------------------------*/
    void getHrefManifestEntriesFromWebapp(
            HashMap<String, ArrayList<String> > href_manifest_map,
            String webapp_name,
            String store_attr_base,
            String status_gte,
            String status_lte,
            String dns_name)
    {
        // Example value: .href/mysite/|ROOT
        String webapp_attr_base = store_attr_base  +  "/|"  +  webapp_name;

        // Example value: .href/mysite/|ROOT/-2
        String href_attr =  webapp_attr_base +
                            "/"  + BASE_VERSION_ALIAS;

        String virt_domain = virtreg_.getVirtServerFQDN();
        int    virt_port   = virtreg_.getVirtServerHttpPort();

        int base_version = 0;
        Attribute base_vers_attr =
            attr_.getAttribute( webapp_attr_base + "/" + BASE_VERSION);

        if ( base_vers_attr != null )
        {
            base_version = base_vers_attr.getIntValue();
        }

        String store_url_base = "http://"                   +
                                dns_name                    +
                                ".www--sandbox.version--v"  +
                                base_version  + "."         +
                                virt_domain   + ":"         +
                                virt_port;


        List<Pair<String, Attribute>> links = null;
        try
        {
            links = attr_.query( href_attr + "/" + STATUS_TO_HREF,
                         new AttrAndQuery(new AttrQueryGTE(status_gte),
                                          new AttrQueryLTE(status_lte)));
        }
        catch (Exception e )
        {
            if ( log.isErrorEnabled() )
            {
                log.error("No link validation information " +
                          "is available yet for webapp: " + webapp_name);
            }
        }

        if  ( links == null ) {return;}

        HashMap<String,String> md5_to_file_cache = new HashMap<String,String>();

        for ( Pair<String, Attribute> link : links  )
        {
            Set<String> href_md5_set  = link.getSecond().keySet();

            for ( String href_md5 : href_md5_set )
            {
                String href_str =
                   attr_.getAttribute( href_attr   + "/" +
                                       MD5_TO_HREF + "/" +
                                       href_md5
                                     ).getStringValue();

                Set<String> file_md5_set =
                        attr_.getAttribute(
                                            href_attr      + "/" +
                                            HREF_TO_SOURCE + "/" +
                                            href_md5
                                          ).keySet();


                for ( String file_md5 : file_md5_set )
                {
                    ArrayList<String> href_list;
                    String file_name = md5_to_file_cache.get( file_md5 );

                    if ( file_name != null )
                    {
                        href_list = href_manifest_map.get( file_name );
                    }
                    else
                    {
                        file_name = attr_.getAttribute( href_attr   + "/" +
                                                        MD5_TO_FILE + "/" +
                                                        file_md5
                                                      ).getStringValue();

                        md5_to_file_cache.put( file_md5, file_name );

                        href_list = new ArrayList<String>();
                        href_manifest_map.put( file_name, href_list );
                    }

                    // If this is an internal link, make it point
                    // to the version of staging that was checked
                    // for validity.

                    if ( href_str.charAt(0) == '/' )
                    {
                        href_str = store_url_base + href_str;
                    }

                    href_list.add( href_str );
                }
            }
        }
    }


    /*-------------------------------------------------------------------------
    *  getHrefsDependentUponFile --
    *
    *------------------------------------------------------------------------*/
    public List<String> getHrefsDependentUponFile(String path)
                        throws                    AVMNotFoundException
    {
        MD5    md5      = new MD5();
        String file_md5 =  md5.digest(path.getBytes());

        ValidationPathParser p =
            new ValidationPathParser(avm_, path);

        String store           = p.getStore();
        String webapp_name     = p.getWebappName();
        String app_base        = p.getAppBase();
        String dns_name        = p.getDnsName();

        // Example value:  ".href/mysite"
        String store_attr_base =  getAttributeStemForDnsName( dns_name );

        // Example value: .href/mysite/|ROOT/-2
        String href_attr =  store_attr_base    +
                            "/|" + webapp_name +
                            "/"  + BASE_VERSION_ALIAS;

        Set<String> dependent_hrefs_md5 =
                         attr_.getAttribute( href_attr    + "/" +
                                             FILE_TO_HDEP + "/" +
                                             file_md5
                                           ).keySet();

        List<String> dependent_hrefs =
                        new ArrayList<String>( dependent_hrefs_md5.size() );

        for (String href_md5 : dependent_hrefs_md5 )
        {
            String href_str =
                   attr_.getAttribute( href_attr   + "/" +
                                       MD5_TO_HREF + "/" +
                                       href_md5
                                     ).getStringValue();

            dependent_hrefs.add( href_str );
        }

        Collections.sort( dependent_hrefs );

        return  dependent_hrefs;
    }
}
