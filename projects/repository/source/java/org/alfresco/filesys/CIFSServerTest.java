/*
 * Copyright (C) 2005 Alfresco, Inc.
 *
 * Licensed under the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version
 * 2.1 of the License, or (at your option) any later version.
 * You may obtain a copy of the License at
 *
 *     http://www.gnu.org/licenses/lgpl.txt
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the
 * License.
 */
package org.alfresco.filesys;

import java.io.PrintStream;

import org.alfresco.error.AlfrescoRuntimeException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * CIFS Server Test Application
 * 
 * @author GKSpencer
 */
public class CIFSServerTest
{

    /**
     * CIFS Server test application
     * 
     * @param args String[]
     */
    public static void main(String[] args)
    {

        PrintStream out = System.out;

        out.println("CIFS Server Test");
        out.println("----------------");

        try
        {
            // Create the configuration service in the same way that Spring creates it
            ApplicationContext ctx = new ClassPathXmlApplicationContext("applicationContext.xml");

            // get the CIFS server bean
            CIFSServer server = (CIFSServer) ctx.getBean("cifsServer");
            if (server == null)
            {
                throw new AlfrescoRuntimeException("Server bean 'cifsServer' not defined");
            }

            // it should have automatically started
            // Wait for shutdown via the console
            out.println("Enter 'x' to shutdown ...");
            boolean shutdown = false;

            // Wait while the server runs, user may stop the server by typing a key
            while (shutdown == false)
            {

                // Wait for the user to enter the shutdown key

                int ch = System.in.read();

                if (ch == 'x' || ch == 'X')
                    shutdown = true;

                synchronized (server)
                {
                    server.wait(20);
                }
            }

            // Stop the server
            server.stopServer();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        System.exit(1);
    }
}
