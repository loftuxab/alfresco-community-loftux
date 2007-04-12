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
*  You should have recieved a copy of the text describing the FLOSS exception,
*  and it is also available here:   http://www.alfresco.com/legal/licensing
*  
*  
*  Author  Jon Cox  <jcox@alfresco.com>
*  File    JndiTest.java
*----------------------------------------------------------------------------*/


package org.alfresco.jndi;
import java.io.*;
import java.util.List;
import junit.framework.TestCase;
import org.alfresco.service.cmr.avm.AVMService;
import org.alfresco.service.cmr.avm.AVMStoreDescriptor;
import org.springframework.context.support.FileSystemXmlApplicationContext;

public class JndiTest extends TestCase
{
    private FileSystemXmlApplicationContext context_;

    public JndiTest(String name)
    {
        super(name);
    }


    protected void setUp() throws Exception
    {
        context_ = 
            new FileSystemXmlApplicationContext("config/jndi-test-context.xml");

        AVMService    service     = (AVMService)context_.getBean("AVMService");
        AVMBulkLoader loader      = new AVMBulkLoader(service); 
        File          import_base = new File("test-data");

        for ( String child : import_base.list() )
        {
            File import_dir = new File(import_base, child);
            if ( ! import_dir.isDirectory()) { continue; }

            try { import_dir = import_dir.getCanonicalFile(); }
            catch (Exception e ) 
            {
                System.out.println("Cannot open: " + 
                                    import_dir.getName() + "   " + 
                                    e.getMessage() );
                throw e;
            }

            loader.importAVMdataFromDirectory( import_dir );
        }

        loader.snapshot();

        List<AVMStoreDescriptor> repositories = service.getStores();

        for (AVMStoreDescriptor rdesc : repositories  )
        {
            // e.g.:            Repo name:  main
            System.out.println("Repo name: " + rdesc.getName() );
        }
    }

    protected void tearDown() throws Exception
    {
        System.out.println("tear down logic goes here...");
        context_.close();
    }
    
    //-------------------------------------------------------------------------
    //-------------------------------------------------------------------------
    /**
    *  Nil test. 
    */
    //-------------------------------------------------------------------------
    public void testNil()
    {
        try
        {
            System.out.println("add some tests here...");
        }
        catch (Exception e)
        {
        }
    }
}


