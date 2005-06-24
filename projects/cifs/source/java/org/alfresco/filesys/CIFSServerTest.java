package org.alfresco.filesys;

import java.io.*;

import org.alfresco.config.source.*;
import org.alfresco.config.xml.*;
import org.alfresco.filesys.server.config.*;
import org.apache.log4j.*;

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
