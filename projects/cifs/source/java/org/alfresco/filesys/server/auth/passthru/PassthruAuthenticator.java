package org.alfresco.filesys.server.auth.passthru;

import java.util.Hashtable;

import org.alfresco.config.ConfigElement;
import org.alfresco.filesys.netbios.NetBIOSName;
import org.alfresco.filesys.netbios.NetBIOSNameList;
import org.alfresco.filesys.netbios.NetBIOSSession;
import org.alfresco.filesys.server.SessionListener;
import org.alfresco.filesys.server.SrvSession;
import org.alfresco.filesys.server.auth.ClientInfo;
import org.alfresco.filesys.server.auth.SrvAuthenticator;
import org.alfresco.filesys.server.auth.UserAccount;
import org.alfresco.filesys.server.config.InvalidConfigurationException;
import org.alfresco.filesys.server.config.ServerConfiguration;
import org.alfresco.filesys.server.core.SharedDevice;
import org.alfresco.filesys.smb.PCShare;
import org.alfresco.filesys.smb.server.SMBServer;
import org.alfresco.filesys.smb.server.SMBSrvSession;
import org.alfresco.filesys.util.HexDump;
import org.apache.log4j.Logger;

/**
 * Passthru Authenticator Class
 * <p>
 * Authenticate users accessing the JLAN Server by validating the user against a domain controller
 * or other server on the network.
 */
public class PassthruAuthenticator extends SrvAuthenticator implements SessionListener
{

    // Debug logging

    private static final Logger logger = Logger.getLogger("org.alfresco.smb.protocol.auth");

    // Constants

    public final static int DefaultSessionTmo = 5000; // 5 seconds
    public final static int MinSessionTmo = 2000; // 2 seconds
    public final static int MaxSessionTmo = 15000; // 15 seconds

    // Domain/server to authenticate users

    private String m_authDom;
    private PCShare m_authSrv;

    // Session connection timeout, in milliseconds

    private int m_sessTmo = DefaultSessionTmo;

    // Debug enable flag

    private boolean m_debug;

    // SMB server

    private SMBServer m_server;

    // Sessions that are currently in the negotiate/session setup state

    private Hashtable<String, PassthruDetails> m_sessions;

    /**
     * Passthru Authenticator Constructor
     * <p>
     * Default to user mode security with encrypted password support.
     */
    public PassthruAuthenticator()
    {
        setAccessMode(SrvAuthenticator.USER_MODE);
        setEncryptedPasswords(true);

        // Allocate the session table

        m_sessions = new Hashtable<String, PassthruDetails>();
    }

    /**
     * Check if debug output is enabled
     * 
     * @return boolean
     */
    public final boolean hasDebug()
    {
        return m_debug;
    }

    /**
     * Authenticate the connection to a particular share, called when the SMB server is in share
     * security mode
     * 
     * @param client ClientInfo
     * @param share SharedDevice
     * @param sharePwd String
     * @param sess SrvSession
     * @return int
     */
    public int authenticateShareConnect(ClientInfo client, SharedDevice share, String sharePwd, SrvSession sess)
    {
        return SrvAuthenticator.Writeable;
    }

    /**
     * Authenticate a session setup by a user
     * 
     * @param client ClientInfo
     * @param sess SrvSession
     * @param alg int
     * @return int
     */
    public int authenticateUser(ClientInfo client, SrvSession sess, int alg)
    {

        // Find the active authentication session details for the server session

        int authSts = SrvAuthenticator.AUTH_DISALLOW;
        PassthruDetails passDetails = m_sessions.get(sess.getUniqueId());

        if (passDetails != null)
        {

            try
            {

                // Authenticate the user by passing the hashed password to the authentication server
                // using the session that
                // has already been setup.

                AuthenticateSession authSess = passDetails.getAuthenticateSession();
                authSess.doSessionSetup(client.getUserName(), client.getANSIPassword(), client.getPassword());

                // Check if the user has been logged on as a guest

                if (authSess.isGuest())
                {

                    // Check if the local server allows guest access

                    if (allowGuest() == true)
                    {

                        // Allow the user access as a guest

                        authSts = SrvAuthenticator.AUTH_GUEST;

                        // Debug

                        if (logger.isDebugEnabled() && hasDebug())
                            logger.debug("Passthru authenticate user=" + client.getUserName() + ", GUEST");
                    }
                }
                else
                {

                    // Allow the user full access to the server

                    authSts = SrvAuthenticator.AUTH_ALLOW;

                    // Debug

                    if (logger.isDebugEnabled() && hasDebug())
                        logger.debug("Passthru authenticate user=" + client.getUserName() + ", FULL");
                }
            }
            catch (Exception ex)
            {

                // Debug

                logger.error("Passthru authenticator", ex);
            }

            // Keep the authentication session if the user session is an SMB session, else close the
            // session now

            if ((sess instanceof SMBSrvSession) == false)
            {

                // Remove the passthru session from the active list

                m_sessions.remove(sess.getUniqueId());

                // Close the passthru authentication session

                try
                {

                    // Close the authentication session

                    AuthenticateSession authSess = passDetails.getAuthenticateSession();
                    authSess.CloseSession();

                    // DEBUG

                    if (logger.isDebugEnabled() && hasDebug())
                        logger.debug("Closed auth session, sessId=" + authSess.getSessionId());
                }
                catch (Exception ex)
                {

                    // Debug

                    logger.error("Passthru error closing session (auth user)", ex);
                }
            }
        }
        else
        {

            // DEBUG

            if (logger.isDebugEnabled() && hasDebug())
                logger.debug("  No PassthruDetails for " + sess.getUniqueId());
        }

        // Return the authentication status

        return authSts;
    }

    /**
     * Get user account details for the specified user
     * 
     * @param user String
     * @return UserAccount
     */
    public UserAccount getUserDetails(String user)
    {

        // No user details to return

        return null;
    }

    /**
     * Get a challenge key for a new session
     * 
     * @param sess SrvSession
     * @return byte[]
     */
    public byte[] getChallengeKey(SrvSession sess)
    {

        // Check for an SMB session

        byte[] chKey = null;

        if (sess instanceof SMBSrvSession)
        {

            // Check if the SMB server listener has been initialized

            if (m_server == null)
            {

                // Initialize the SMB server session listener so we receive callbacks when sessions
                // are opened/closed on the SMB server

                SMBSrvSession smbSess = (SMBSrvSession) sess;
                m_server = smbSess.getSMBServer();

                m_server.addSessionListener(this);
            }
        }

        try
        {

            // Open a connection to the authentication server

            AuthenticateSession authSess = AuthSessionFactory.OpenAuthenticateSession(m_authSrv, DefaultSessionTmo,
                    null);
            if (authSess != null)
            {

                // Create an entry in the active sessions table for the new session

                PassthruDetails passDetails = new PassthruDetails(sess, authSess);
                m_sessions.put(sess.getUniqueId(), passDetails);

                // Use the challenge key returned from the authentication server

                chKey = authSess.getEncryptionKey();

                // DEBUG

                if (logger.isDebugEnabled() && hasDebug())
                    logger.debug("Passthru sessId=" + authSess.getSessionId() + ", negotiate key=["
                            + HexDump.hexString(chKey) + "]");
            }
        }
        catch (Exception ex)
        {

            // Debug

            logger.error("Passthru error getting challenge", ex);
        }

        // Return the challenge key

        return chKey;
    }

    /**
     * Initialzie the authenticator
     * 
     * @param config ServerConfiguration
     * @param params ConfigElement
     * @exception InvalidConfigurationException
     */
    public void initialize(ServerConfiguration config, ConfigElement params) throws InvalidConfigurationException
    {

        // Call the base class

        super.initialize(config, params);

        // Check if debug output is enabled

        if (params.getChild("Debug") != null)
            m_debug = true;

        // Check if the session timeout has been specified

        ConfigElement sessTmoElem = params.getChild("Timeout");
        if (sessTmoElem != null)
        {

            try
            {

                // Validate the session timeout value

                m_sessTmo = Integer.parseInt(sessTmoElem.getValue());
            }
            catch (NumberFormatException ex)
            {
                throw new InvalidConfigurationException("Invalid timeout value specified");
            }
        }

        // Check if a server name has been specified

        ConfigElement srvNameElem = params.getChild("Server");

        if (srvNameElem != null && srvNameElem.getValue().length() > 0)
        {

            try
            {

                // Get the passthru authenticator server name

                String srvName = srvNameElem.getValue();

                // DEBUG

                if (logger.isDebugEnabled() && hasDebug())
                    logger.debug("Passthru authenticator connecting to server " + srvName + " ...");

                // Validate the server name, open a connection

                PCShare authSrv = new PCShare(srvName, "IPC$", "", "");
                AuthenticateSession authSess = AuthSessionFactory.OpenAuthenticateSession(authSrv, DefaultSessionTmo,
                        null);
                if (authSess != null)
                    authSess.CloseSession();

                // Save the authentication server details

                m_authSrv = authSrv;

                // Debug

                if (logger.isDebugEnabled() && hasDebug())
                    logger.debug("Test connection to passthru authentication server " + srvName + " successful");
            }
            catch (Exception ex)
            {
                throw new InvalidConfigurationException("Passthru authenticator error, " + ex.toString());
            }
        }
        else
        {

            // Check if a domain name has been specified

            ConfigElement domNameElem = params.getChild("Domain");

            if (domNameElem != null && m_authSrv != null)
                throw new InvalidConfigurationException(
                        "Specify Server or Domain for PassthruAuthenticator configuration");

            String domName = domNameElem.getValue();

            if (domName != null && domName.length() > 0)
            {

                try
                {

                    // DEBUG

                    if (logger.isDebugEnabled() && hasDebug())
                        logger.debug("Passthru authenticator finding domain controller for " + domName + " ...");

                    // Find the domain controller

                    NetBIOSName nbName = NetBIOSSession.FindName(domName, NetBIOSName.DomainControllers, m_sessTmo);

                    // DEBUG

                    if (logger.isDebugEnabled() && hasDebug())
                        logger.debug("  Found domain controller at " + nbName.getIPAddressString(0));

                    // Get the domain controller name

                    NetBIOSNameList nameList = NetBIOSSession.FindNamesForAddress(nbName.getIPAddressString(0));
                    NetBIOSName dcName = nameList.findName(NetBIOSName.FileServer, false);

                    if (dcName == null)
                        throw new InvalidConfigurationException("Domain controller not running server service");

                    // DEBUG

                    if (logger.isDebugEnabled() && hasDebug())
                        logger.debug("  Domain controller name is " + dcName.getName());

                    // Validate the domain controller, open a connection

                    PCShare authSrv = new PCShare(nbName.getIPAddressString(0), "IPC$", "", "");
                    AuthenticateSession authSess = AuthSessionFactory.OpenAuthenticateSession(authSrv,
                            DefaultSessionTmo, null);
                    if (authSess != null)
                        authSess.CloseSession();

                    // Save the authentication server details

                    m_authSrv = authSrv;

                    // DEBUG

                    if (logger.isDebugEnabled() && hasDebug())
                        logger.debug("Test connection to passthru authentication server " + domName + "\\"
                                + dcName.getName() + " successful");
                }
                catch (Exception ex)
                {
                    throw new InvalidConfigurationException("Passthru authenticator error, " + ex.toString());
                }
            }
        }

        // Check if we have an authentication server

        if (m_authSrv == null)
            throw new InvalidConfigurationException("No valid authentication server found");
    }

    /**
     * SMB server session closed notification
     * 
     * @param sess SrvSession
     */
    public void sessionClosed(SrvSession sess)
    {

        // Check if there is an active session to the authentication server for this local
        // session

        PassthruDetails passDetails = m_sessions.get(sess.getUniqueId());

        if (passDetails != null)
        {

            // Remove the passthru session from the active list

            m_sessions.remove(sess.getUniqueId());

            // Close the passthru authentication session

            try
            {

                // Close the authentication session

                AuthenticateSession authSess = passDetails.getAuthenticateSession();
                authSess.CloseSession();

                // DEBUG

                if (logger.isDebugEnabled() && hasDebug())
                    logger.debug("Closed auth session, sessId=" + authSess.getSessionId());
            }
            catch (Exception ex)
            {

                // Debug

                logger.error("Passthru error closing session (closed)", ex);
            }
        }
    }

    /**
     * SMB server session created notification
     * 
     * @param sess SrvSession
     */
    public void sessionCreated(SrvSession sess)
    {
    }

    /**
     * User successfully logged on notification
     * 
     * @param sess SrvSession
     */
    public void sessionLoggedOn(SrvSession sess)
    {

        // Check if the client information has an empty user name, if so then do not close the
        // authentication
        // session

        if (sess.hasClientInformation() && sess.getClientInformation().getUserName() != null
                && sess.getClientInformation().getUserName().length() > 0)
        {

            // Check if there is an active session to the authentication server for this local
            // session

            PassthruDetails passDetails = m_sessions.get(sess.getUniqueId());

            if (passDetails != null)
            {

                // Remove the passthru session from the active list

                m_sessions.remove(sess.getUniqueId());

                // Close the passthru authentication session

                try
                {

                    // Close the authentication session

                    AuthenticateSession authSess = passDetails.getAuthenticateSession();
                    authSess.CloseSession();

                    // DEBUG

                    if (logger.isDebugEnabled() && hasDebug())
                        logger.debug("Closed auth session, sessId=" + authSess.getSessionId());
                }
                catch (Exception ex)
                {

                    // Debug

                    logger.error("Passthru error closing session (logon)", ex);
                }
            }
        }
    }
}
