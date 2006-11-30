/*
 * Copyright (C) 2005 Alfresco, Inc.
 *
 * Licensed under the Mozilla Public License version 1.1 
 * with a permitted attribution clause. You may obtain a
 * copy of the License at
 *
 *   http://www.alfresco.org/legal/license.txt
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the
 * License.
 */
package org.alfresco.util.exec;

import java.io.File;

import junit.framework.TestCase;

import org.springframework.context.ApplicationContext;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @see org.alfresco.util.exec.RuntimeExecBootstrapBean
 * 
 * @author Derek Hulley
 */
public class RuntimeExecBeansTest extends TestCase
{
    private static final String APP_CONTEXT_XML =
            "classpath:org/alfresco/util/exec/RuntimeExecBeansTest-context.xml";
    private static final String DIR = "dir_RuntimeExecBootstrapBeanTest";

    public void testBootstrapAndShutdown() throws Exception
    {
        File dir = new File(DIR);
        dir.mkdir();
        assertTrue("Directory not created", dir.exists());
        
        // now bring up the bootstrap
        ApplicationContext ctx = new ClassPathXmlApplicationContext(APP_CONTEXT_XML);
        
        // the folder should be gone
        assertFalse("Folder was not deleted by bootstrap", dir.exists());
        
        // now create the folder again
        dir.mkdir();
        assertTrue("Directory not created", dir.exists());
        
        // announce that the context is closing
        ctx.publishEvent(new ContextClosedEvent(ctx));
        
        // the folder should be gone
        assertFalse("Folder was not deleted by shutdown", dir.exists());
    }
}
