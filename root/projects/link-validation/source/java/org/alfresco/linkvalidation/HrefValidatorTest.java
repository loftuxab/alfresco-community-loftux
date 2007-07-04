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


import java.lang.reflect.Method;
import java.util.Map;
import org.alfresco.jndi.JndiInfoBean;
import org.alfresco.mbeans.VirtServerInfo;
import org.alfresco.repo.attributes.*;
import org.alfresco.repo.domain.PropertyValue;
import org.alfresco.repo.remote.ClientTicketHolder;
import org.alfresco.sandbox.SandboxConstants;
import org.alfresco.service.cmr.remote.AVMRemote;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.NameMatcher;
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
    private static AVMRemote              AvmSvc_;
    private static AttributeService       Attrib_;
    private static int                    TestMethodsLeft_;
    private static LinkValidationService  LinkValidation_;
    private static VirtServerRegistry     VirtServerRegistry_;

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
                System.setProperty("catalina.base", 
                                   System.getenv("VIRTUAL_TOMCAT_HOME" ) );
            }

            Context_ = new FileSystemXmlApplicationContext(
                           "config/alfresco-link-validation-context-test.xml");


            // Rather than hard-code which repositories to index,
            // the tests here will just access the AVM directly
            // and then tell the LinkValidationService to validate
            // hrefs within the first webapp it discovers.

            AvmSvc_ = (AVMRemote)Context_.getBean("avmRemote");


            // In a live system, it's the virt server that fetches its info:

            VirtServerRegistry_      = 
                (VirtServerRegistry) Context_.getBean("VirtServerRegistry");
            VirtServerInfo virt_info = 
                (VirtServerInfo) Context_.getBean("virtServerInfo");
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

            LinkValidation_ =  (LinkValidationService) 
                               Context_.getBean("LinkValidationService");

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

            Attrib_ =  (AttributeService) Context_.getBean("attributeService"); 

            // Start with a blank slate.
            try 
            {
                Attrib_.removeAttribute("", LinkValidationServiceImpl.HREF);
            }
            catch (Exception e)
            { 
                /* If HREF data wan't there, that's ok */
            }
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
        //         Map<String, Map<QName, PropertyValue>> store_staging_main_entries = 
        //             AvmSvc_.queryStoresPropertyKey( 
        //                SandboxConstants.PROP_SANDBOX_STAGING_MAIN );
        //        
        //         
        //         for ( Map.Entry<String, Map<QName, PropertyValue>> 
        //               store_staging_main_entry  :
        //               store_staging_main_entries.entrySet() 
        //             )
        //         {
        //             String  store_name  = store_staging_main_entry.getKey();
        //             // ...
        //         }

        String store_name = "test";    // NEON: hard coding for now

        for (int i = 0; i<2; i++)
        {

            // NEON -  remove asap
            HrefValidationProgress progress = new HrefValidationProgress();
            
            try 
            {
                LinkValidation_.updateHrefInfo( 
                    store_name,                 // store to update hrefs
                    true,                       // false = not incremental
                    true,                       // validate external
                    progress                    // status monitor
                );
            }
            catch (Exception e)
            {
                System.out.println(
                   "LinkValidation_.updateHrefInfo failed:  " + 
                     e.getClass().getName() + "  msg: " +  e.getMessage());

                e.printStackTrace();


                // Try another webapp
                continue;

            }


            System.out.println("Webapps updated: " + 
                               progress.getWebappUpdateCount() );

            System.out.println("Dirs updated: " + 
                               progress.getDirUpdateCount() );

            System.out.println("Files updated: " + 
                               progress.getFileUpdateCount() );

            System.out.println("Distinct URLs updated: " + 
                               progress.getUrlUpdateCount() );


            long start, end;



            String index_html = 
             "mysite:/www/avm_webapps/ROOT/products/ecm/comparison/index.html";


             // -1, "mysite:/www/avm_webapps/ROOT",
             // progress

            progress = new HrefValidationProgress();

            HrefDifference href_diff = null;
            try 
            {
                href_diff = LinkValidation_.getHrefDifference( 
                               store_name  +  "--alice:/www/avm_webapps/ROOT",
                               store_name  +  ":/www/avm_webapps/ROOT",
                               progress);
            }
            catch (LinkValidationAbortedException e)
            {
                // Won't happen here.
                System.out.println(
                        "LinkValidation_.getHrefDifference aborted: " + 
                        e.getMessage());
     
                continue;
            }
            catch (Exception e)
            {
                // AVMNotFoundException,
                // SocketException,
                // SSLException,
                // LinkValidationAbortedException
                
                System.out.println(
                        "LinkValidation_.getHrefDifference failed:  " + 
                         e.getClass().getName() + "  msg: " +  e.getMessage());

                e.printStackTrace();
                
                // Try another webapp
                continue;
            }



            // Show what is broken due to deleted files
            System.out.println("\n\n--------------------------------------\n");
            System.out.println(
               "\n\nMainfest broken by delete from WORKAREA:\n\n");

            HrefManifest broken_by_deletion    = 
               LinkValidation_.getHrefManifestBrokenByDelete(href_diff);

            for ( HrefManifestEntry manifest_entry : 
                  broken_by_deletion.getManifestEntries() 
                 )
            {
                System.out.println("\nFile:  " + manifest_entry.getFileName());
                for ( String broken_href :  manifest_entry.getHrefs() )
                {
                    System.out.println("       " +  broken_href);
                }
            }


            // Show what is broken within new mods:
            System.out.println("\n\n--------------------------------------\n");
            System.out.println(
               "\n\nMainfest broken by new/modified files in WORKAREA:\n\n");

            HrefManifest broken_in_newmod = 
               LinkValidation_.getHrefManifestBrokenByNewOrMod(href_diff);

            for ( HrefManifestEntry manifest_entry : 
                  broken_in_newmod.getManifestEntries() 
                 )
            {
                System.out.println("\nFile:  " + manifest_entry.getFileName());
                for ( String broken_href :  manifest_entry.getHrefs() )
                {
                    System.out.println("       " +  broken_href);
                }
            }


            if ( i == 0 )
            {
                System.out.println("\n\n----------------------------------");
                System.out.println("-----Sleeping for 30 sec...-------");
                System.out.println("----------------------------------\n\n");

                try { Thread.sleep( 30000 ); }
                catch (Exception te ) 
                { 
                    System.out.println("trouble sleeping.. " + te.getMessage());
                }
            }
        }
    }
}
