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


