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
import org.alfresco.repo.attributes.*;
import org.alfresco.repo.remote.ClientTicketHolder;
import org.alfresco.sandbox.SandboxConstants;
import org.alfresco.service.cmr.remote.AVMRemote;
import org.alfresco.repo.domain.PropertyValue;
import java.util.Map;
import java.lang.reflect.Method;
import org.alfresco.service.namespace.QName;

import org.alfresco.config.JNDIConstants;
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

            AttribSvc_ = (AttributeService)Context_.getBean("attributeService");
            AvmSvc_    = (AVMRemote)Context_.getBean("avmRemote");

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
        // http://mysite.www--sandbox.192-168-1-5.ip.alfrescodemo.net:8180/

        HrefValidator validator = 
                new HrefValidator( AvmSvc_, 
                                   AttribSvc_,
                                   "127-0-0-1.ip.alfrescodemo.net",
                                   8180);


        Map<String, Map<QName, PropertyValue>> store_staging_main_entries = 
            AvmSvc_.queryStoresPropertyKey( SandboxConstants.PROP_SANDBOX_STAGING_MAIN );

        for ( Map.Entry<String, Map<QName, PropertyValue>> store_staging_main_entry  :
              store_staging_main_entries.entrySet() 
            )
        {
            String  store_name  = store_staging_main_entry.getKey();
            validator.revalidateAllWebappsInStore( store_name );
            break;
        }
    }

    
//     public void testBasic()
//     {
//         try
//         {
//             AttribSvc_.setAttribute("", "boolean", new BooleanAttributeValue(true));
//             AttribSvc_.setAttribute("", "byte", new ByteAttributeValue((byte)0x20));
//             AttribSvc_.setAttribute("", "short", new ShortAttributeValue((short)42));
//             AttribSvc_.setAttribute("", "int", new IntAttributeValue(43));
//             AttribSvc_.setAttribute("", "long", new LongAttributeValue(1000000000000L));
//             AttribSvc_.setAttribute("", "float", new FloatAttributeValue(1.414f));
//             AttribSvc_.setAttribute("", "double", new DoubleAttributeValue(3.1415926));
//             AttribSvc_.setAttribute("", "string", new StringAttributeValue("This is a string."));
//             AttribSvc_.setAttribute("", "serializable", new SerializableAttributeValue(new Long(1010101L)));
//             MapAttribute map = new MapAttributeValue();
//             map.put("foo", new StringAttributeValue("I walk."));
//             map.put("bar", new StringAttributeValue("I talk."));
//             map.put("baz", new StringAttributeValue("I shop."));
//             map.put("funky", new StringAttributeValue("I sneeze."));
//             map.put("monkey", 
//                     new StringAttributeValue("I'm going to be a fireman when the floods roll back."));
//             AttribSvc_.setAttribute("", "map", map);
//             assertNotNull(AttribSvc_.getAttribute("boolean"));
//             assertEquals(42, (int)AttribSvc_.getAttribute("short").getShortValue());
//             assertEquals("I sneeze.", AttribSvc_.getAttribute("map/funky").getStringValue());
// 
//             // This is 11 because of the AVMLockingService.
//             // 
//             // jcox:  it's 10! 
//             //
//             // assertEquals(11, AttribSvc_.getKeys("").size());
// 
//             System.out.println("Total keys:  " + AttribSvc_.getKeys("").size());
// 
//             assertEquals(5, AttribSvc_.getKeys("map").size());
//             List<String> keys = AttribSvc_.getKeys("");
//             for (String key : keys)
//             {
//                 System.out.println(key + " => " + AttribSvc_.getAttribute(key));
//             }
//             AttribSvc_.setAttribute("", "string", new StringAttributeValue("This is another string."));
//             assertEquals("This is another string.", AttribSvc_.getAttribute("string").getStringValue());
//         }
//         catch (Exception e)
//         {
//             e.printStackTrace();
//             fail();
//         }
//     }
//     
//     /**
//      * Test the query capability.
//      */
//     public void testQuery()
//     {
//         try
//         {
//             // Put some attributes in place.
//             MapAttribute map = new MapAttributeValue();
//             map.put("a", new StringAttributeValue("a"));
//             map.put("b", new StringAttributeValue("a"));
//             map.put("c", new StringAttributeValue("a"));
//             map.put("d", new StringAttributeValue("a"));
//             map.put("e", new StringAttributeValue("a"));
//             map.put("f", new StringAttributeValue("a"));
//             map.put("g", new StringAttributeValue("a"));
//             map.put("h", new StringAttributeValue("a"));
//             map.put("i", new StringAttributeValue("a"));
//             map.put("j", new StringAttributeValue("a"));
//             map.put("k", new StringAttributeValue("a"));
//             map.put("l", new StringAttributeValue("a"));
//             map.put("m", new StringAttributeValue("a"));
//             map.put("n", new StringAttributeValue("a"));
//             map.put("o", new StringAttributeValue("a"));
//             map.put("p", new StringAttributeValue("a"));
//             map.put("q", new StringAttributeValue("a"));
//             map.put("r", new StringAttributeValue("a"));
//             map.put("s", new StringAttributeValue("a"));
//             map.put("t", new StringAttributeValue("a"));
//             map.put("u", new StringAttributeValue("a"));
//             map.put("v", new StringAttributeValue("a"));
//             map.put("w", new StringAttributeValue("a"));
//             map.put("x", new StringAttributeValue("a"));
//             map.put("y", new StringAttributeValue("a"));
//             map.put("z", new StringAttributeValue("a"));
//             AttribSvc_.setAttribute("", "map1", map);
//             AttribSvc_.setAttribute("", "map2", map);
//             List<Pair<String, Attribute>> result =
//                 AttribSvc_.query("map1", new AttrQueryEquals("w"));
//             assertEquals(1, result.size());
//             result =
//                 AttribSvc_.query("map1", new AttrQueryLT("d"));
//             assertEquals(3, result.size());
//             result =
//                 AttribSvc_.query("map1", new AttrQueryLTE("d"));
//             assertEquals(4, result.size());
//             result = 
//                 AttribSvc_.query("map1", new AttrQueryGT("v"));
//             assertEquals(4, result.size());
//             result =
//                 AttribSvc_.query("map1", new AttrQueryGTE("v"));
//             assertEquals(5, result.size());
//             result =
//                 AttribSvc_.query("map1", new AttrQueryNE("g"));
//             assertEquals(25, result.size());
//             result =
//                 AttribSvc_.query("map1", new AttrNotQuery(new AttrQueryGT("d")));
//             assertEquals(4, result.size());
//             result =
//                 AttribSvc_.query("map1", new AttrAndQuery(new AttrQueryGT("g"),
//                                                         new AttrQueryLT("l")));
//             assertEquals(4, result.size());
//             result =
//                 AttribSvc_.query("map1", new AttrOrQuery(new AttrQueryLT("d"),
//                                                        new AttrQueryGT("w")));
//             assertEquals(6, result.size());
//             result =
//                 AttribSvc_.query("map1", new AttrQueryLike("%"));
//             assertEquals(26, result.size());
//             AttribSvc_.setAttribute("map2", "submap", map);
//             result =
//                 AttribSvc_.query("map2/submap", new AttrQueryEquals("w"));
//             assertEquals(1, result.size());
//             result =
//                 AttribSvc_.query("map2/submap", new AttrQueryLT("d"));
//             assertEquals(3, result.size());
//             result =
//                 AttribSvc_.query("map2/submap", new AttrQueryLTE("d"));
//             assertEquals(4, result.size());
//             result = 
//                 AttribSvc_.query("map2/submap", new AttrQueryGT("v"));
//             assertEquals(4, result.size());
//             result =
//                 AttribSvc_.query("map2/submap", new AttrQueryGTE("v"));
//             assertEquals(5, result.size());
//             result =
//                 AttribSvc_.query("map2/submap", new AttrQueryNE("g"));
//             assertEquals(25, result.size());
//             result =
//                 AttribSvc_.query("map2/submap", new AttrNotQuery(new AttrQueryGT("d")));
//             assertEquals(4, result.size());
//             result =
//                 AttribSvc_.query("map2/submap", new AttrAndQuery(new AttrQueryGT("g"),
//                                                         new AttrQueryLT("l")));
//             assertEquals(4, result.size());
//             result =
//                 AttribSvc_.query("map2/submap", new AttrOrQuery(new AttrQueryLT("d"),
//                                                        new AttrQueryGT("w")));
//             assertEquals(6, result.size());
//             result =
//                 AttribSvc_.query("map2/submap", new AttrQueryLike("%"));
//             assertEquals(26, result.size());
//         }
//         catch (Exception e)
//         {
//             e.printStackTrace();
//             fail();
//         }
//     }
//     
//     public void testDelete()
//     {
//         try
//         {
//             // Put some attributes in place.
//             MapAttribute map = new MapAttributeValue();
//             map.put("a", new StringAttributeValue("a"));
//             map.put("b", new StringAttributeValue("b"));
//             map.put("c", new StringAttributeValue("c"));
//             map.put("d", new StringAttributeValue("d"));
//             map.put("e", new StringAttributeValue("e"));
//             map.put("f", new StringAttributeValue("f"));
//             map.put("g", new StringAttributeValue("g"));
//             map.put("h", new StringAttributeValue("h"));
//             map.put("i", new StringAttributeValue("i"));
//             map.put("j", new StringAttributeValue("j"));
//             map.put("k", new StringAttributeValue("k"));
//             map.put("l", new StringAttributeValue("l"));
//             map.put("m", new StringAttributeValue("m"));
//             map.put("n", new StringAttributeValue("n"));
//             map.put("o", new StringAttributeValue("o"));
//             map.put("p", new StringAttributeValue("p"));
//             map.put("q", new StringAttributeValue("q"));
//             map.put("r", new StringAttributeValue("r"));
//             map.put("s", new StringAttributeValue("s"));
//             map.put("t", new StringAttributeValue("t"));
//             map.put("u", new StringAttributeValue("u"));
//             map.put("v", new StringAttributeValue("v"));
//             map.put("w", new StringAttributeValue("w"));
//             map.put("x", new StringAttributeValue("x"));
//             map.put("y", new StringAttributeValue("y"));
//             map.put("z", new StringAttributeValue("z"));
//             AttribSvc_.setAttribute("", "map", map);
//             AttribSvc_.setAttribute("map", "submap", map);
//             AttribSvc_.setAttribute("map/submap", "subsubmap", map);
//             assertEquals(27, AttribSvc_.getKeys("map").size());
//             assertEquals(27, AttribSvc_.getKeys("map/submap").size());
//             AttribSvc_.removeAttribute("map/submap/subsubmap", "b");
//             assertEquals(25, AttribSvc_.getKeys("map/submap/subsubmap").size());
//             AttribSvc_.removeAttribute("map/submap", "subsubmap");
//             assertEquals(26, AttribSvc_.getKeys("map/submap").size());
//         }
//         catch (Exception e)
//         {
//             e.printStackTrace();
//         }
//     }
//     
//     /**
//      * Test ListAttributes
//      */
//     public void testList()
//     {
//         try
//         {
//             ListAttribute list = new ListAttributeValue();
//             list.add(new IntAttributeValue(0));
//             list.add(new IntAttributeValue(1));
//             list.add(new IntAttributeValue(2));
//             list.add(new IntAttributeValue(3));
//             list.add(new IntAttributeValue(4));
//             AttribSvc_.setAttribute("", "dummy", list);
//             Attribute found = AttribSvc_.getAttribute("dummy");
//             assertNotNull(found);
//             assertEquals(5, found.size());
//             Attribute add = new IntAttributeValue(6);
//             AttribSvc_.addAttribute("dummy", add);
//             assertEquals(6, AttribSvc_.getAttribute("dummy").size());
//             AttribSvc_.removeAttribute("dummy", 2);
//             found = AttribSvc_.getAttribute("dummy");
//             assertEquals(5, found.size());
//             assertEquals(3, found.get(2).getIntValue());
//             Attribute replace = new StringAttributeValue("String");
//             AttribSvc_.setAttribute("dummy", 2, replace);
//             assertEquals("String", AttribSvc_.getAttribute("dummy/2").getStringValue());
//             MapAttribute map = new MapAttributeValue();
//             map.put("list", list);
//             MapAttribute subMap = new MapAttributeValue();
//             subMap.put("a", new StringAttributeValue("polyester"));
//             subMap.put("b", new StringAttributeValue("donuts"));
//             subMap.put("c", new StringAttributeValue("brutality"));
//             list.add(subMap);
//             AttribSvc_.setAttribute("", "map", map);
//             assertEquals("donuts", AttribSvc_.getAttribute("map/list/5/b").getStringValue());
//         }
//         catch (Exception e)
//         {
//             e.printStackTrace();
//             fail();
//         }
//     }
// 
//     public void testStores()
//     {
//         try
//         {
//             Map<String, Map<QName, PropertyValue>> store_staging_main_entries = 
//                 AvmSvc_.queryStoresPropertyKey( SandboxConstants.PROP_SANDBOX_STAGING_MAIN );
// 
//             for ( Map.Entry<String, Map<QName, PropertyValue>> store_staging_main_entry  :
//                   store_staging_main_entries.entrySet() 
//                 )
//             {
//                 String  store_name  = store_staging_main_entry.getKey();
//                 System.out.println("Store name:  " + store_name);
//             }
//         }
//         catch (Exception e)
//         {
//             System.out.println( e.getMessage());
//         }
//     }
}
