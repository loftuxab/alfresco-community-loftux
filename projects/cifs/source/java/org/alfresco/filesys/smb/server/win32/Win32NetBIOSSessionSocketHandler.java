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
package org.alfresco.filesys.smb.server.win32;

import org.alfresco.filesys.netbios.NetBIOSName;
import org.alfresco.filesys.netbios.win32.NetBIOS;
import org.alfresco.filesys.netbios.win32.Win32NetBIOS;
import org.alfresco.filesys.server.config.ServerConfiguration;
import org.alfresco.filesys.smb.mailslot.Win32NetBIOSHostAnnouncer;
import org.alfresco.filesys.smb.server.PacketHandler;
import org.alfresco.filesys.smb.server.SMBServer;
import org.alfresco.filesys.smb.server.SMBSrvSession;
import org.alfresco.filesys.smb.server.SessionSocketHandler;
import org.apache.log4j.Logger;

/**
 * Win32 NetBIOS Session Socket Handler Class
 * <p>
 * Uses the Win32 Netbios() call to provide the low level session layer for better integration with
 * Windows.
 */
public class Win32NetBIOSSessionSocketHandler extends SessionSocketHandler
{

    // Debug logging

    private static final Logger logger = Logger.getLogger("org.alfresco.smb.protocol");

    // Constants
    //
    // Default LANA offline polling interval

    public static final long LANAPollingInterval = 5000; // 5 seconds

    // File server name

    private String m_srvName;

    // Accept connections from any clients or the named client only

    private byte[] m_acceptClient;

    // Local NetBIOS name to listen for sessions on and assigned name number

    private byte[] m_nbName;
    private int m_nameNum;

    // Workstation NetBIOS name and assigned name number

    private byte[] m_wksNbName;
    private int m_wksNameNum;

    // NetBIOS LAN adapter to use

    private int m_lana = -1;

    // Flag to indicate if the LANA is valid or the network adapter is currently
    // unplugged/offline/disabled

    private boolean m_lanaValid;

    // Polling interval in milliseconds to check if the configured LANA is back online

    private long m_lanaPoll;

    /**
     * Class constructor
     * 
     * @param srv SMBServer
     * @param debug boolean
     */
    public Win32NetBIOSSessionSocketHandler(SMBServer srv, boolean debug)
    {
        super("Win32 NetBIOS", srv, debug);

        // Get the Win32 NetBIOS file server name

        if (srv.getConfiguration().getWin32ServerName() != null)
            m_srvName = srv.getConfiguration().getWin32ServerName();
        else
            m_srvName = srv.getConfiguration().getServerName();

        // Get the accepted client string, defaults to '*' to accept any client connection

        NetBIOSName accName = new NetBIOSName("*", NetBIOSName.WorkStation, false);
        m_acceptClient = accName.getNetBIOSName();

        // Set the LANA to use, or -1 to use the first available

        m_lana = srv.getConfiguration().getWin32LANA();

        // Debug

        if (logger.isDebugEnabled() && hasDebug())
            logger.debug("[SMB] Win32 NetBIOS server " + m_srvName);

        // Set the LANA offline polling interval

        m_lanaPoll = LANAPollingInterval;
    }

    /**
     * Class constructor
     * 
     * @param srv SMBServer
     * @param lana int
     * @param debug boolean
     */
    public Win32NetBIOSSessionSocketHandler(SMBServer srv, int lana, boolean debug)
    {
        super("Win32 NetBIOS", srv, debug);

        // Get the Win32 NetBIOS file server name

        if (srv.getConfiguration().getWin32ServerName() != null)
            m_srvName = srv.getConfiguration().getWin32ServerName();
        else
            m_srvName = srv.getConfiguration().getServerName();

        // Get the accepted client string, defaults to '*' to accept any client connection

        NetBIOSName accName = new NetBIOSName("*", NetBIOSName.WorkStation, false);
        m_acceptClient = accName.getNetBIOSName();

        // Set the LANA to use, or -1 to use the first available

        m_lana = lana;

        // Debug

        if (logger.isDebugEnabled() && hasDebug())
            logger.debug("[SMB] Win32 NetBIOS server " + m_srvName);

        // Set the LANA offline polling interval

        m_lanaPoll = LANAPollingInterval;
    }

    /**
     * Return the LANA number that is being used
     * 
     * @return int
     */
    public final int getLANANumber()
    {
        return m_lana;
    }

    /**
     * Return the LANA offline polling interval to check for the LANA coming back online
     * 
     * @return long
     */
    public final long getLANAOfflinePollingInterval()
    {
        return m_lanaPoll;
    }

    /**
     * Return the assigned NetBIOS name number
     * 
     * @return int
     */
    public final int getNameNumber()
    {
        return m_nameNum;
    }

    /**
     * Return the local server name
     * 
     * @return String
     */
    public final String getServerName()
    {
        return m_srvName;
    }

    /**
     * Initialize the session socket handler.
     * 
     * @throws Exception
     */
    public void initialize() throws Exception
    {

        // Enumerate the LAN adapters, use the first available if the LANA has not been specified in
        // the
        // configuration

        int[] lanas = Win32NetBIOS.LanaEnum();
        if (lanas.length > 0)
        {

            // Check if the LANA has been specified via the configuration, if not then use the first
            // available

            if (m_lana == -1)
                m_lana = lanas[0];
            else
            {

                // Check if the required LANA is available

                boolean lanaOnline = false;
                int idx = 0;

                while (idx < lanas.length && lanaOnline == false)
                {

                    // Check if the LANA is listed

                    if (lanas[idx++] == getLANANumber())
                        lanaOnline = true;
                }

                // If the LANA is not available the main listener thread will poll the available
                // LANAs
                // until the required LANA is available

                if (lanaOnline == false)
                {

                    // Indicate that the LANA is not offline/unplugged/disabled

                    m_lanaValid = false;
                    return;
                }
            }
        }
        else
        {

            // If the LANA has not been set throw an exception as no LANAs are available

            if (m_lana == -1)
                throw new Exception("No Win32 NetBIOS LANAs available");

            // The required LANA is offline/unplugged/disabled

            m_lanaValid = false;
            return;
        }

        // Reset the LANA

        Win32NetBIOS.Reset(m_lana);

        // Create the local NetBIOS name to listen for incoming connections on

        NetBIOSName nbName = new NetBIOSName(m_srvName, NetBIOSName.FileServer, false);
        m_nbName = nbName.getNetBIOSName();

        // Add the NetBIOS name to the local name table

        m_nameNum = Win32NetBIOS.AddName(m_lana, m_nbName);
        if (m_nameNum < 0)
            throw new Exception("Win32 NetBIOS AddName failed (file server), status = 0x"
                    + Integer.toHexString(-m_nameNum) + ", " + NetBIOS.getErrorString(-m_nameNum));

        // Register a NetBIOS name for the server name with the workstation name type, some clients
        // use this name
        // to find the server

        nbName = new NetBIOSName(m_srvName, NetBIOSName.WorkStation, false);
        m_wksNbName = nbName.getNetBIOSName();

        m_wksNameNum = Win32NetBIOS.AddName(m_lana, m_wksNbName);
        if (m_wksNameNum < 0)
            throw new Exception("Win32 NetBIOS AddName failed (workstation), status = 0x"
                    + Integer.toHexString(-m_wksNameNum) + ", " + NetBIOS.getErrorString(-m_wksNameNum));

        // Indicate that the LANA is valid

        m_lanaValid = true;
    }

    /**
     * Check if the LANA is valid and accepting incoming sessions or the associated network adapter
     * is unplugged/disabled/offline.
     * 
     * @return boolean
     */
    public final boolean isLANAValid()
    {
        return m_lanaValid;
    }

    /**
     * Run the NetBIOS session socket handler
     */
    public void run()
    {

        try
        {

            // Clear the shutdown flag

            clearShutdown();

            // Wait for incoming connection requests

            byte[] callerNameBuf = new byte[NetBIOS.NCBNameSize];
            String callerName = null;

            while (hasShutdown() == false)
            {

                // Check if the LANA is valid and ready to accept incoming sessions

                if (isLANAValid())
                {

                    // Debug

                    if (logger.isDebugEnabled() && hasDebug())
                        logger.debug("[SMB] Waiting for Win32 NetBIOS session request ...");

                    // Clear the caller name

                    callerNameBuf[0] = '\0';
                    callerName = null;

                    // Wait for a new NetBIOS session

                    int lsn = Win32NetBIOS.Listen(m_lana, m_nbName, m_acceptClient, callerNameBuf);

                    // Get the caller name, if available

                    if (callerNameBuf[0] != '\0')
                        callerName = new String(callerNameBuf).trim();
                    else
                        callerName = "";

                    // Create a packet handler and thread for the new session

                    if (lsn >= 0)
                    {

                        // Create a new session thread

                        try
                        {

                            // Debug

                            if (logger.isDebugEnabled() && hasDebug())
                                logger.debug("[SMB] Win32 NetBIOS session request received, lsn=" + lsn + ", caller=["
                                        + callerName + "]");

                            // Create a packet handler for the session

                            PacketHandler pktHandler = new Win32NetBIOSPacketHandler(m_lana, lsn, callerName);

                            // Create a server session for the new request, and set the session id.

                            SMBSrvSession srvSess = new SMBSrvSession(pktHandler, getServer());
                            srvSess.setSessionId(getNextSessionId());
                            srvSess.setUniqueId(pktHandler.getShortName() + srvSess.getSessionId());
                            srvSess.setDebugPrefix("[" + pktHandler.getShortName() + srvSess.getSessionId() + "] ");

                            // Add the session to the active session list

                            getServer().addSession(srvSess);

                            // Start the new session in a seperate thread

                            Thread srvThread = new Thread(srvSess);
                            srvThread.setDaemon(true);
                            srvThread.setName("Sess_W" + srvSess.getSessionId() + "_LSN" + lsn);
                            srvThread.start();
                        }
                        catch (Exception ex)
                        {

                            // Debug

                            if (logger.isDebugEnabled() && hasDebug())
                                logger.debug("[SMB] Win32 NetBIOS Failed to create session, " + ex.toString());
                        }
                    }
                    else
                    {

                        // Check if the error indicates the network adapter is
                        // unplugged/offline/disabled

                        int sts = -lsn;

                        if (sts == NetBIOS.NRC_Bridge)
                        {

                            // Indicate that the LANA is no longer valid

                            m_lanaValid = false;

                            // DEBUG

                            if (logger.isDebugEnabled() && hasDebug())
                                logger.debug("[SMB] Win32 NetBIOS LANA offline/disabled, LANA=" + getLANANumber());
                        }
                        else if (logger.isDebugEnabled() && hasDebug())
                            logger.debug("[SMB] Win32 NetBIOS Listen error, 0x" + Integer.toHexString(-lsn) + ", "
                                    + NetBIOS.getErrorString(-lsn));
                    }
                }
                else
                {

                    // Sleep for a short while ...

                    try
                    {
                        Thread.sleep(getLANAOfflinePollingInterval());
                    }
                    catch (Exception ex)
                    {
                    }

                    // Check if the network adapter/LANA is back online, if so then re-initialize
                    // the LANA
                    // to start accepting sessions again

                    try
                    {
                        initialize();
                    }
                    catch (Exception ex)
                    {

                        // DEBUG

                        if (logger.isDebugEnabled() && hasDebug())
                        {
                            logger.debug("[SMB] Win32 NetBIOS Failed To ReInitialize LANA");
                            logger.debug("  " + ex.getMessage());
                        }
                    }

                    // DEBUG

                    if (logger.isDebugEnabled() && hasDebug() && isLANAValid())
                        logger.debug("[SMB] Win32 NetBIOS LANA " + getLANANumber() + " Back Online");
                }
            }
        }
        catch (Exception ex)
        {

            // Do not report an error if the server has shutdown, closing the server socket
            // causes an exception to be thrown.

            if (hasShutdown() == false)
            {
                logger.debug("[SMB] Win32 NetBIOS Server error : " + ex.toString());
                logger.debug(ex);
            }
        }

        // Debug

        if (logger.isDebugEnabled() && hasDebug())
            logger.debug("[SMB] Win32 NetBIOS session handler closed");
    }

    /**
     * Create the Win32 NetBIOS session socket handlers for the main SMB/CIFS server
     * 
     * @param server SMBServer
     * @param sockDbg boolean
     */
    public final static void createSessionHandlers(SMBServer server, boolean sockDbg)
    {

        // Access the server configuration

        ServerConfiguration config = server.getConfiguration();

        // DEBUG

        if (logger.isDebugEnabled() && sockDbg)
        {
            int[] lanas = Win32NetBIOS.LanaEnum();

            StringBuffer lanaStr = new StringBuffer();
            if (lanas != null && lanas.length > 0)
            {
                for (int i = 0; i < lanas.length; i++)
                {
                    lanaStr.append(Integer.toString(lanas[i]));
                    lanaStr.append(" ");
                }
            }
            logger.debug("[SMB] Win32 NetBIOS Available LANAs: " + lanaStr.toString());
        }

        // Check if the Win32 NetBIOS session handler should use a particular LANA/network adapter
        // or
        // should use all available LANAs/network adapters (that have NetBIOS enabled).

        Win32NetBIOSSessionSocketHandler sessHandler = null;

        if (config.getWin32LANA() != -1)
        {

            // Create a single Win32 NetBIOS session handler using the specified LANA

            sessHandler = new Win32NetBIOSSessionSocketHandler(server, config.getWin32LANA(), sockDbg);

            try
            {
                sessHandler.initialize();
            }
            catch (Exception ex)
            {

                // DEBUG

                if (logger.isDebugEnabled() && sockDbg)
                {
                    logger.debug("[SMB] Win32 NetBIOS failed to create session handler for LANA "
                            + config.getWin32LANA());
                    logger.debug("      " + ex.getMessage());
                }
            }

            // Add the session handler to the SMB/CIFS server

            server.addSessionHandler(sessHandler);

            // Run the NetBIOS session handler in a seperate thread

            Thread nbThread = new Thread(sessHandler);
            nbThread.setName("Win32NB_Handler_" + config.getWin32LANA());
            nbThread.start();

            // DEBUG

            if (logger.isDebugEnabled() && sockDbg)
                logger.debug("[SMB] Win32 NetBIOS created session handler on LANA " + config.getWin32LANA());

            // Check if a host announcer should be enabled

            if (config.hasWin32EnableAnnouncer())
            {

                // Create a host announcer

                Win32NetBIOSHostAnnouncer hostAnnouncer = new Win32NetBIOSHostAnnouncer(sessHandler, config
                        .getDomainName(), config.getWin32HostAnnounceInterval());
                hostAnnouncer.setDebug(sockDbg);

                // Add the host announcer to the SMB/CIFS server list

                server.addHostAnnouncer(hostAnnouncer);
                hostAnnouncer.start();

                // DEBUG

                if (logger.isDebugEnabled() && sockDbg)
                    logger.debug("[SMB] Win32 NetBIOS host announcer enabled on LANA " + config.getWin32LANA());
            }
        }
        else
        {

            // Get a list of the available LANAs

            int[] lanas = Win32NetBIOS.LanaEnum();

            if (lanas != null && lanas.length > 0)
            {

                // Create a session handler for each available LANA

                for (int i = 0; i < lanas.length; i++)
                {

                    // Get the current LANA

                    int lana = lanas[i];

                    // Create a session handler

                    sessHandler = new Win32NetBIOSSessionSocketHandler(server, lana, sockDbg);

                    try
                    {
                        sessHandler.initialize();
                    }
                    catch (Exception ex)
                    {

                        // DEBUG

                        if (logger.isDebugEnabled() && sockDbg)
                        {
                            logger.debug("[SMB] Win32 NetBIOS failed to create session handler for LANA " + lana);
                            logger.debug("      " + ex.getMessage());
                        }
                    }

                    // Add the session handler to the SMB/CIFS server

                    server.addSessionHandler(sessHandler);

                    // Run the NetBIOS session handler in a seperate thread

                    Thread nbThread = new Thread(sessHandler);
                    nbThread.setName("Win32NB_Handler_" + lana);
                    nbThread.start();

                    // DEBUG

                    if (logger.isDebugEnabled() && sockDbg)
                        logger.debug("[SMB] Win32 NetBIOS created session handler on LANA " + lana);

                    // Check if a host announcer should be enabled

                    if (config.hasWin32EnableAnnouncer())
                    {

                        // Create a host announcer

                        Win32NetBIOSHostAnnouncer hostAnnouncer = new Win32NetBIOSHostAnnouncer(sessHandler, config
                                .getDomainName(), config.getWin32HostAnnounceInterval());
                        hostAnnouncer.setDebug(sockDbg);

                        // Add the host announcer to the SMB/CIFS server list

                        server.addHostAnnouncer(hostAnnouncer);
                        hostAnnouncer.start();

                        // DEBUG

                        if (logger.isDebugEnabled() && sockDbg)
                            logger.debug("[SMB] Win32 NetBIOS host announcer enabled on LANA " + lana);
                    }
                }
            }

            // Create a LANA monitor to check for new LANAs becoming available

            new Win32NetBIOSLanaMonitor(server, lanas, LANAPollingInterval, sockDbg);
        }
    }
}
