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
package org.alfresco.filesys.smb.server.win32;

import java.util.BitSet;

import org.alfresco.filesys.netbios.win32.Win32NetBIOS;
import org.alfresco.filesys.server.config.ServerConfiguration;
import org.alfresco.filesys.smb.mailslot.Win32NetBIOSHostAnnouncer;
import org.alfresco.filesys.smb.server.SMBServer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Win32 NetBIOS LANA Monitor Class
 * <p>
 * Monitors the available NetBIOS LANAs to check for new network interfaces coming online. A session
 * socket handler will be created for new LANAs as they appear.
 */
public class Win32NetBIOSLanaMonitor extends Thread
{

    // Debug logging

    private static final Log logger = LogFactory.getLog("org.alfresco.smb.protocol");

    // Global LANA monitor

    private static Win32NetBIOSLanaMonitor _lanaMonitor;

    // Available LANA list

    private BitSet m_lanas;

    // SMB/CIFS server to add new session handlers to

    private SMBServer m_server;

    // Wakeup interval

    private long m_wakeup;

    // Shutdown request flag

    private boolean m_shutdown;

    // Debug output enable

    private boolean m_debug;

    /**
     * Class constructor
     * 
     * @param server SMBServer
     * @param lanas int[]
     * @param wakeup long
     * @param debug boolean
     */
    Win32NetBIOSLanaMonitor(SMBServer server, int[] lanas, long wakeup, boolean debug)
    {

        // Set the SMB server and wakeup interval

        m_server = server;
        m_wakeup = wakeup;

        m_debug = debug;

        // Set the current LANAs in the available LANAs list

        m_lanas = new BitSet();
        if (lanas != null)
        {

            // Set the currently available LANAs

            for (int i = 0; i < lanas.length; i++)
                m_lanas.set(lanas[i]);
        }

        // Set the global LANA monitor, if not already set

        if (_lanaMonitor == null)
            _lanaMonitor = this;

        // Start the LANA monitor thread

        setDaemon(true);
        start();
    }

    /**
     * Return the global LANA monitor
     * 
     * @return Win32NetBIOSLanaMonitor
     */
    public static Win32NetBIOSLanaMonitor getLanaMonitor()
    {
        return _lanaMonitor;
    }

    /**
     * Thread method
     */
    public void run()
    {

        // Clear the shutdown flag

        m_shutdown = false;

        // Loop until shutdown

        ServerConfiguration config = m_server.getConfiguration();

        while (m_shutdown == false)
        {

            // Sleep for a while

            try
            {
                Thread.sleep(m_wakeup);
            }
            catch (InterruptedException ex)
            {
            }

            // Get the available LANA list

            int[] lanas = Win32NetBIOS.LanaEnum();
            if (lanas != null)
            {

                // Check if there are any new LANAs available

                Win32NetBIOSSessionSocketHandler sessHandler = null;

                for (int i = 0; i < lanas.length; i++)
                {

                    // Get the current LANA id, check if it's a known LANA

                    int lana = lanas[i];
                    if (m_lanas.get(lana) == false)
                    {

                        // DEBUG

                        if (logger.isDebugEnabled() && hasDebug())
                            logger.debug("[SMB] Win32 NetBIOS found new LANA, " + lana);

                        // Create a single Win32 NetBIOS session handler using the specified LANA

                        sessHandler = new Win32NetBIOSSessionSocketHandler(m_server, lana, hasDebug());

                        try
                        {
                            sessHandler.initialize();
                        }
                        catch (Exception ex)
                        {

                            // DEBUG

                            if (logger.isDebugEnabled() && hasDebug())
                                logger.debug("[SMB] Win32 NetBIOS failed to create session handler for LANA " + lana,
                                        ex);

                            // Clear the session handler

                            sessHandler = null;
                        }

                        // If the session handler was initialized successfully add it to the
                        // SMB/CIFS server

                        if (sessHandler != null)
                        {

                            // Add the session handler to the SMB/CIFS server

                            m_server.addSessionHandler(sessHandler);

                            // Run the NetBIOS session handler in a seperate thread

                            Thread nbThread = new Thread(sessHandler);
                            nbThread.setName("Win32NB_Handler_" + lana);
                            nbThread.start();

                            // DEBUG

                            if (logger.isDebugEnabled() && hasDebug())
                                logger.debug("[SMB] Win32 NetBIOS created session handler on LANA " + lana);

                            // Check if a host announcer should be enabled

                            if (config.hasWin32EnableAnnouncer())
                            {

                                // Create a host announcer

                                Win32NetBIOSHostAnnouncer hostAnnouncer = new Win32NetBIOSHostAnnouncer(sessHandler,
                                        config.getDomainName(), config.getWin32HostAnnounceInterval());

                                // Add the host announcer to the SMB/CIFS server list

                                m_server.addHostAnnouncer(hostAnnouncer);
                                hostAnnouncer.start();

                                // DEBUG

                                if (logger.isDebugEnabled() && hasDebug())
                                    logger.debug("[SMB] Win32 NetBIOS host announcer enabled on LANA " + lana);
                            }

                            // Set the LANA in the available LANA list

                            m_lanas.set(lana);
                        }
                    }
                }
            }
        }
    }

    /**
     * Determine if debug output is enabled
     * 
     * @return boolean
     */
    public final boolean hasDebug()
    {
        return m_debug;
    }

    /**
     * Request the LANA monitor thread to shutdown
     */
    public final void shutdownRequest()
    {
        m_shutdown = true;
    }
}
