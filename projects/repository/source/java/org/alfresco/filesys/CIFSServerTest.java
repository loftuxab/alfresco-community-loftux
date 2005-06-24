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

import org.alfresco.config.source.ClassPathConfigSource;
import org.alfresco.config.xml.XMLConfigService;
import org.alfresco.filesys.server.config.ServerConfiguration;

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
     * @param args
     *            String[]
     */
    public static void main(String[] args)
    {

        PrintStream out = System.out;

        out.println("CIFS Server Test");
        out.println("----------------");

        try
        {
            // Create the configuration service in the same way that Spring
            // creates it

            ClassPathConfigSource classPathConfigSource = new ClassPathConfigSource(
                    "org/alfresco/filesys/file-servers.xml");

            XMLConfigService xmlConfigService = new XMLConfigService(classPathConfigSource);
            xmlConfigService.init();

            // Create the filesystem service configuration

            ServerConfiguration filesysConfig = new ServerConfiguration(xmlConfigService);
            filesysConfig.init();

            // Create the CIFS server and start it

            CIFSServer cifsServer = new CIFSServer(filesysConfig);
            cifsServer.startServer();

            // Wait for shutdown via the console

            out.println("Enter 'x' to shutdown ...");
            boolean shutdown = false;

            // Wait while the server runs, user may stop the server by typing a
            // key

            while (shutdown == false)
            {

                // Wait for the user to enter the shutdown key

                int ch = System.in.read();

                if (ch == 'x' || ch == 'X')
                    shutdown = true;
            }

            // Stop the server

            cifsServer.stopServer();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            System.exit(1);
        }
    }
}
