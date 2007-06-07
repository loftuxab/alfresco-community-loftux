/*
 * Copyright (C) 2005-2007 Alfresco Software Limited.
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
 * http://www.alfresco.com/legal/licensing"
 */
package org.alfresco.util.exec;

import java.io.File;

import junit.framework.TestCase;

import org.alfresco.util.exec.RuntimeExec.ExecutionResult;
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
    
    public void testFailureModeOfMissingCommand()
    {
        File dir = new File(DIR);
        dir.mkdir();
        assertTrue("Directory not created", dir.exists());
        ApplicationContext ctx = new ClassPathXmlApplicationContext(APP_CONTEXT_XML);
        
        RuntimeExec failureExec = (RuntimeExec) ctx.getBean("commandFailureGuaranteed");
        assertNotNull(failureExec);
        // Execute it
        ExecutionResult result = failureExec.execute();
        assertEquals("Expected first error code in list", 666, result.getExitValue());
    }
}
