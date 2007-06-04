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
*----------------------------------------------------------------------------*/


package org.alfresco.linkvalidation;

import java.util.List;

import org.alfresco.jndi.JndiInfoBean;
import org.alfresco.mbeans.VirtServerInfo;
import org.alfresco.repo.attributes.*;
import org.alfresco.repo.remote.ClientTicketHolder;
import org.alfresco.sandbox.SandboxConstants;
import org.alfresco.service.cmr.remote.AVMRemote;
import org.alfresco.repo.domain.PropertyValue;
import java.util.Map;
import java.lang.reflect.Method;
import org.alfresco.service.namespace.QName;

import org.alfresco.config.JNDIConstants;
import org.alfresco.mbeans.VirtServerRegistry;
import org.alfresco.service.cmr.security.AuthenticationService;
import org.alfresco.service.cmr.attributes.AttrAndQuery;
import org.alfresco.service.cmr.attributes.AttributeService;
import org.alfresco.service.cmr.attributes.AttrNotQuery;
import org.alfresco.service.cmr.attributes.AttrOrQuery;
import org.alfresco.service.cmr.attributes.AttrQueryEquals;
import org.alfresco.service.cmr.attributes.AttrQueryGT;
import org.alfresco.service.cmr.attributes.AttrQueryGTE;
import org.alfresco.service.cmr.attributes.AttrQueryLike;
import org.alfresco.service.cmr.attributes.AttrQueryLT;
import org.alfresco.service.cmr.attributes.AttrQueryLTE;
import org.alfresco.service.cmr.attributes.AttrQueryNE;
import org.alfresco.util.Pair;

import org.springframework.context.support.FileSystemXmlApplicationContext;

import junit.framework.TestCase;

public class HrefValidatorTest extends TestCase
{
    private static FileSystemXmlApplicationContext Context_ = null;
    private static AVMRemote        AvmSvc_;
    private static AttributeService AttribSvc_;
    private static int TestMethodsLeft_;
    private static LinkValidationService LinkValidation_;
    private static VirtServerRegistry VirtServerRegistry_;

    // @BeforeClass
    static
    {
        // Count methods
        for (Method method : HrefValidatorTest.class.getMethods()) 
        {
            if (method.getName().startsWith("test")) { ++ TestMethodsLeft_; }
        }


        if (Context_ == null)
        {
            String catalina_base =  System.getProperty("catalina.base");

            if ( catalina_base == null || catalina_base.equals("") )
            {
                System.setProperty("catalina.base", System.getenv("VIRTUAL_TOMCAT_HOME" ) );
            }

            Context_ = new FileSystemXmlApplicationContext(
                           "config/alfresco-link-validation-context-test.xml");


            // Rather than hard-code which repositories to index,
            // the tests here will just access the AVM directly
            // and then tell the LinkValidationService to validate
            // hrefs within the first webapp it discovers.

            AvmSvc_ = (AVMRemote)Context_.getBean("avmRemote");


            // In a live system, it's the virt server that fetches its info:

            VirtServerRegistry_      = (VirtServerRegistry) Context_.getBean("VirtServerRegistry");
            VirtServerInfo virt_info = (VirtServerInfo) Context_.getBean("virtServerInfo");
            int    virt_port         = virt_info.getVirtServerHttpPort();
            String virt_domain       = virt_info.getVirtServerDomain();
            String virt_jmx_url      = "service:jmx:rmi://ignored/jndi/rmi://" +
                                       virt_domain                             +
                                       ":"                                     +
                                       virt_info.getVirtServerJmxRmiPort()     +
                                       "/alfresco/jmxrmi";

            // Fake registration of a virt server with the VirtServerRegistry
            //
            VirtServerRegistry_.registerVirtServerInfo(virt_jmx_url, 
                                                       virt_domain, 
                                                       virt_port);

            // Now a fake virt server is registered with the VirtServerRegistry,
            // fetch the link validation service.   This service has a ref to
            // the VirtServerRegistry within it;  the Spring config for the
            // LinkValidationService in the live webapp will also provide
            // a ref to the virt registry, so within the LinkValidationService,
            // the code will look the same (i.e.: it does not need to know that
            // the virt server registration was faked from within this test).

            LinkValidation_ =  (LinkValidationService) Context_.getBean("LinkValidationService");

            // Get the authentication service.
            AuthenticationService authService =
                (AuthenticationService)Context_.getBean("authenticationService");

            // Get the info bean for the user name and password.

            JndiInfoBean info = 
                (JndiInfoBean)Context_.getBean("jndiInfoBean");
            
            // Authenticate once,
            authService.authenticate(
                info.getAlfrescoServerUser(), 
                info.getAlfrescoServerPassword().toCharArray()
            );

            // set the ticket.
            ((ClientTicketHolder)
             (Context_.getBean("clientTicketHolder"))
            ).setTicket(authService.getCurrentTicket());
        }
    }

    // @AfterClass
    public static void oneTimeTearDown() 
    {
    }

    @Override
    protected void setUp() throws Exception
    {
    }

    @Override
    protected void tearDown() throws Exception
    {
        if (--TestMethodsLeft_ == 0) { oneTimeTearDown(); }
    }

    /**
    *  Fetches the first store from the repository,
    *  and walks 
    */
    public void testOnestore()
    {
         Map<String, Map<QName, PropertyValue>> store_staging_main_entries = 
             AvmSvc_.queryStoresPropertyKey( SandboxConstants.PROP_SANDBOX_STAGING_MAIN );
        
         for ( Map.Entry<String, Map<QName, PropertyValue>> store_staging_main_entry  :
               store_staging_main_entries.entrySet() 
             )
         {
             String  store_name  = store_staging_main_entry.getKey();

             // NEON -  remove asap
             UpdateHrefInfoStatus update_status = new UpdateHrefInfoStatus();
             if (true)
             {
                 LinkValidation_.updateHrefInfo( 
                     store_name,                    // store to update hrefs
                     false,                         // false = not incremental
                     10000,                         // connect timeout (ms)
                     30000,                         // read timeout (ms)
                     5,                             // thread count
                     update_status                  // status monitor
                 );
             }

             System.out.println("Webapps updated: " + 
                                update_status.getWebappUpdateCount() );

             System.out.println("Dirs updated: " + 
                                update_status.getDirUpdateCount() );

             System.out.println("Files updated: " + 
                                update_status.getFileUpdateCount() );

             System.out.println("Distinct URLs updated: " + 
                                update_status.getUrlUpdateCount() );


             List<HrefConcordanceEntry> href_conc;

             // OK  (200)
             // href_conc = LinkValidation_.getHrefConcordance( store_name, 200, 200);
             //
             // Success (2xx)
             // href_conc =  LinkValidation_.getHrefConcordance( store_name, 200, 299 );
             //
             // Error (400-500)
             // href_conc = LinkValidation_.getHrefConcordance( store_name, 400, 599 );
             //
             // Same as Error case above.

             long start, end;

             start = System.currentTimeMillis();
             href_conc = LinkValidation_.getBrokenHrefConcordance( store_name );
             end = System.currentTimeMillis();

             System.out.println(
                 "\n\n------------------- Broken Hrefs Concordance  "  +
                 "(time in ms: " +  (end - start ) + " )" );
                                

             for (HrefConcordanceEntry conc_entry : href_conc )
             {
                 System.out.println( 
                     "Status: " + conc_entry.getResponseStatus() + "  " +
                                  conc_entry.getHref());

                 String [] locations = conc_entry.getLocations();

                 for ( String location : locations )
                 {
                     System.out.println("        " + location);
                 }
             }



             start = System.currentTimeMillis();

             List<HrefManifest> file_href_manifests = 
                   LinkValidation_.getBrokenHrefManifests(  store_name );
                // LinkValidation_.getHrefManifests(        store_name,  400, 599 );

             end = System.currentTimeMillis();

             System.out.println(
                 "\n\n------------------- Broken Hrefs manifests  "  +
                 "(time in ms: " +  (end - start ) + " )" );

             for ( HrefManifest manifest : file_href_manifests )
             {
                 System.out.println("File: "  + manifest.getFileName() );
                 List<String> hrefs = manifest.getHrefs();

                 for (String href : hrefs )
                 {
                     System.out.println("      "  + href );
                 }
             }
             System.out.println("Time:  " + (end - start) );

             String index_html = "mysite:/www/avm_webapps/ROOT/products/ecm/comparison/index.html";


             System.out.println("---single file: " + index_html);
             HrefManifest file_href_manifest = LinkValidation_.getBrokenHrefManifest( index_html );
             List<String> single_file_hrefs  = file_href_manifest.getHrefs();

             for (String href : single_file_hrefs )
             {
                 System.out.println("      "  + href );
             }


             // Just test the first store... that's enough.
             break;
         }
    }
}
