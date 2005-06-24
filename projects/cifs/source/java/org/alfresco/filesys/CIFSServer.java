package org.alfresco.filesys;

import java.io.*;
import java.net.*;

import org.alfresco.filesys.netbios.server.*;
import org.alfresco.filesys.netbios.win32.*;
import org.alfresco.filesys.server.*;
import org.alfresco.filesys.server.config.*;
import org.alfresco.filesys.smb.server.*;
import org.apache.log4j.*;

/**
 * CIFS Server Class
 * 
 * <p>
 * Create and start the various server components required to run the CIFS
 * server.
 * 
 * @author GKSpencer
 */
public class CIFSServer
{

    // Debug logging

    private static final Logger logger = Logger.getLogger("org.alfresco.smb.server");

    // Filesystem configuration

    private ServerConfiguration m_config;

    /**
     * Class constructor
     * 
     * @param config
     *            ServerConfiguration
     */
    public CIFSServer(ServerConfiguration config)
    {
        m_config = config;
    }

    /**
     * Start the CIFS server components
     * 
     * @exception SocketException
     *                If a network error occurs
     * @exception IOException
     *                If an I/O error occurs
     */
    public final void startServer() throws SocketException, IOException
    {

        // Load the Win32 NetBIOS library
        //
        // For some strange reason the native code loadLibrary() call hangs if
        // done later by the SMBServer.
        // Forcing the Win32NetBIOS class to load here and run the static
        // initializer fixes the problem.

        if (m_config.hasWin32NetBIOS())
            Win32NetBIOS.LanaEnum();

        // Create the SMB server and NetBIOS name server, if enabled

        if (m_config.isSMBServerEnabled())
        {

            // Create the NetBIOS name server if NetBIOS SMB is enabled

            if (m_config.hasNetBIOSSMB())
                m_config.addServer(new NetBIOSNameServer(m_config));

            // Create the SMB server

            m_config.addServer(new SMBServer(m_config));
        }

        // Start the configured servers

        for (int i = 0; i < m_config.numberOfServers(); i++)
        {

            // Get the current server

            NetworkServer server = m_config.getServer(i);

            // DEBUG

            if (logger.isInfoEnabled())
                logger.info("Starting server " + server.getProtocolName() + " ...");

            // Start the server

            m_config.getServer(i).startServer();
        }
    }

    /**
     * Stop the CIFS server components
     */
    public final void stopServer()
    {

        // Shutdown the servers

        for (int i = 0; i < m_config.numberOfServers(); i++)
        {

            // Get the current server

            NetworkServer server = m_config.getServer(i);

            // DEBUG

            if (logger.isInfoEnabled())
                logger.info("Shutting server " + server.getProtocolName() + " ...");

            // Start the server

            m_config.getServer(i).shutdownServer(false);
        }

    }
}
