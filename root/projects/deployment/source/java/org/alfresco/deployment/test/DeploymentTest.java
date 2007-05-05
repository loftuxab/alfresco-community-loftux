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
 * http://www.alfresco.com/legal/licensing
 */

package org.alfresco.deployment.test;

import java.io.OutputStream;

import org.alfresco.deployment.DeploymentReceiverService;
import org.alfresco.deployment.impl.DeploymentException;
import org.alfresco.util.GUID;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import junit.framework.TestCase;

/**
 * Some test for the deployment receiver.
 * @author britt
 */
public class DeploymentTest extends TestCase
{
    private static ApplicationContext fContext = null;
    
    private static DeploymentReceiverService fService;
    
    @Override
    protected void setUp() throws Exception
    {
        if (fContext == null)
        {
            fContext = new FileSystemXmlApplicationContext("config/application-context.xml");
            fService = (DeploymentReceiverService)fContext.getBean("deploymentReceiverService");
        }
    }

    @Override
    protected void tearDown() throws Exception
    {
    }
    
    public void testSimple()
    {
        try
        {
            String ticket = fService.begin("sampleTarget", "Giles", "Watcher");
            System.out.println(fService.getListing(ticket, "/"));
            OutputStream out = fService.send(ticket, "/foo.dat", GUID.generate());
            out.write("I'm naming all the stars.\n".getBytes());
            fService.finishSend(ticket, out);
            fService.commit(ticket);
            ticket = fService.begin("sampleTarget", "Giles", "Watcher");
            fService.delete(ticket, "/build.xml");
            fService.delete(ticket, "/lib");
            out = fService.send(ticket, "/src", GUID.generate());
            out.write("I used to be a directory.\n".getBytes());
            fService.finishSend(ticket, out);
            fService.commit(ticket);
            ticket = fService.begin("sampleTarget", "Giles", "Watcher");
            out = fService.send(ticket, "/test/glory.txt", GUID.generate());
            out.write("This town has too many vampires and not enough retail outlets.\n".getBytes());
            fService.finishSend(ticket, out);
            fService.delete(ticket, "/example_run.xml");
            fService.abort(ticket);
            ticket = fService.begin("sampleTarget", "Giles", "Watcher");
            try
            {
                fService.getListing(ticket, "/foo/bar");
                fail();
            }
            catch (DeploymentException e)
            {
                // This should happen.
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail();
        }
    }
}
