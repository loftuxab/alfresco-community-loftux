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
package org.alfresco.filesys.server.config;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.Provider;
import java.security.Security;
import java.util.*;

import org.alfresco.config.Config;
import org.alfresco.config.ConfigElement;
import org.alfresco.config.ConfigLookupContext;
import org.alfresco.config.ConfigService;
import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.filesys.server.NetworkServer;
import org.alfresco.filesys.server.NetworkServerList;
import org.alfresco.filesys.server.auth.LocalAuthenticator;
import org.alfresco.filesys.server.auth.SrvAuthenticator;
import org.alfresco.filesys.server.auth.UserAccount;
import org.alfresco.filesys.server.auth.UserAccountList;
import org.alfresco.filesys.server.auth.acl.ACLParseException;
import org.alfresco.filesys.server.auth.acl.AccessControl;
import org.alfresco.filesys.server.auth.acl.AccessControlList;
import org.alfresco.filesys.server.auth.acl.AccessControlManager;
import org.alfresco.filesys.server.auth.acl.AccessControlParser;
import org.alfresco.filesys.server.auth.acl.DefaultAccessControlManager;
import org.alfresco.filesys.server.auth.acl.InvalidACLTypeException;
import org.alfresco.filesys.server.auth.passthru.PassthruAuthenticator;
import org.alfresco.filesys.server.core.DeviceContextException;
import org.alfresco.filesys.server.core.ShareMapper;
import org.alfresco.filesys.server.core.SharedDevice;
import org.alfresco.filesys.server.core.SharedDeviceList;
import org.alfresco.filesys.server.filesys.DefaultShareMapper;
import org.alfresco.filesys.server.filesys.DiskDeviceContext;
import org.alfresco.filesys.server.filesys.DiskInterface;
import org.alfresco.filesys.server.filesys.DiskSharedDevice;
import org.alfresco.filesys.smb.Dialect;
import org.alfresco.filesys.smb.DialectSelector;
import org.alfresco.filesys.smb.ServerType;
import org.alfresco.filesys.util.IPAddress;
import org.alfresco.service.ServiceRegistry;

/**
 * <p>
 * Provides the configuration parameters for the network file servers.
 * 
 * @author Gary K. Spencer
 */
public class ServerConfiguration
{
    // Filesystem configuration constants
    private static final String ConfigArea = "file-servers";
    private static final String ConfigCIFS = "CIFS Server";
    private static final String ConfigFilesystems = "Filesystems";
    private static final String ConfigSecurity = "Filesystem Security";

    // SMB/CIFS session debug type strings
    //
    // Must match the bit mask order.
    private static final String m_sessDbgStr[] = { "NETBIOS", "STATE", "NEGOTIATE", "TREE", "SEARCH", "INFO", "FILE",
            "FILEIO", "TRANSACT", "ECHO", "ERROR", "IPC", "LOCK", "PKTTYPE", "DCERPC", "STATECACHE", "NOTIFY",
            "STREAMS", "SOCKET" };

    // Platform types

    private enum PlatformType
    {
        Unknown, WINDOWS, LINUX
    };

    /** connection to database */
    private ServiceRegistry serviceRegistry;
    /** Configuration service used to read the configuration from */
    private ConfigService m_configService;
    /** the device to connect use */
    private DiskInterface diskInterface;

    // Runtime platform type
    private PlatformType m_platform = PlatformType.Unknown;

    // Main server enable flags, to enable SMB, FTP and/or NFS server components
    private boolean m_smbEnable = true;

    // Server name
    private String m_name;

    // Server type, used by the host announcer
    private int m_srvType = ServerType.WorkStation + ServerType.Server + ServerType.NTServer;

    // Active server list
    private NetworkServerList m_serverList;

    // Server comment
    private String m_comment;

    // Server domain
    private String m_domain;

    // Network broadcast mask string
    private String m_broadcast;

    // Announce the server to network neighborhood, announcement interval in
    // minutes
    private boolean m_announce;

    private int m_announceInterval;

    // Default SMB dialects to enable
    private DialectSelector m_dialects;

    // List of shared devices
    private SharedDeviceList m_shareList;

    // Authenticator, used to authenticate users and share connections.
    private SrvAuthenticator m_authenticator;

    // Share mapper
    private ShareMapper m_shareMapper;

    // Access control manager
    private AccessControlManager m_aclManager;

    // Global access control list, applied to all shares that do not have access
    // controls
    private AccessControlList m_globalACLs;

    // SMB server, NetBIOS name server and host announcer debug enable
    private boolean m_srvDebug = false;

    private boolean m_nbDebug = false;

    private boolean m_announceDebug = false;

    // Default session debugging setting
    private int m_sessDebug;

    // Flags to indicate if NetBIOS, native TCP/IP SMB and/or Win32 NetBIOS
    // should be enabled
    private boolean m_netBIOSEnable = true;

    private boolean m_tcpSMBEnable = false;

    private boolean m_win32NBEnable = false;

    // Address to bind the SMB server to, if null all local addresses are used
    private InetAddress m_smbBindAddress;

    // Address to bind the NetBIOS name server to, if null all addresses are
    // used
    private InetAddress m_nbBindAddress;

    // WINS servers
    private InetAddress m_winsPrimary;
    private InetAddress m_winsSecondary;

    // User account list
    private UserAccountList m_userList;

    // Enable/disable Macintosh extension SMBs
    private boolean m_macExtensions;

    // --------------------------------------------------------------------------------
    // Win32 NetBIOS configuration
    //
    // Server name to register under Win32 NetBIOS, if not set the main server
    // name is used
    private String m_win32NBName;

    // LANA to be used for Win32 NetBIOS, if not specified the first available
    // is used
    private int m_win32NBLANA = -1;

    // Send out host announcements via the Win32 NetBIOS interface
    private boolean m_win32NBAnnounce = false;
    private int m_win32NBAnnounceInterval;

    // --------------------------------------------------------------------------------
    // Global server configuration
    //
    // Timezone name and offset from UTC in minutes
    private String m_timeZone;
    private int m_tzOffset;

    // JCE provider class name
    private String m_jceProviderClass;

    /**
     * Class constructor
     * 
     * @param config ConfigService
     */
    public ServerConfiguration(ServiceRegistry serviceRegistry, ConfigService config, DiskInterface diskInterface)
    {
        this.serviceRegistry = serviceRegistry;
        this.diskInterface = diskInterface;
        // Save the configuration service
        m_configService = config;

        // Allocate the shared device list
        m_shareList = new SharedDeviceList();

        // Allocate the SMB dialect selector, and initialize using the default
        // list of dialects

        m_dialects = new DialectSelector();

        m_dialects.AddDialect(Dialect.DOSLanMan1);
        m_dialects.AddDialect(Dialect.DOSLanMan2);
        m_dialects.AddDialect(Dialect.LanMan1);
        m_dialects.AddDialect(Dialect.LanMan2);
        m_dialects.AddDialect(Dialect.LanMan2_1);
        m_dialects.AddDialect(Dialect.NT);

        // Use the local authenticator, that allows locally defined users to connect to the
        // server

        setAuthenticator(new LocalAuthenticator(), null, true);

        // Use the default share mapper

        m_shareMapper = new DefaultShareMapper();

        try
        {
            m_shareMapper.initializeMapper(this, null);
        }
        catch (InvalidConfigurationException ex)
        {
            throw new AlfrescoRuntimeException("Failed to initialise share mapper", ex);
        }

        // Set the default access control manager

        m_aclManager = new DefaultAccessControlManager();
        m_aclManager.initialize(this, null);

        // Use the default timezone

        try
        {
            setTimeZone(TimeZone.getDefault().getID());
        }
        catch (Exception ex)
        {
            throw new AlfrescoRuntimeException("Failed to set timezone", ex);
        }

        // Allocate the active server list

        m_serverList = new NetworkServerList();
    }

    /**
     * Initialize the configuration using the configuration service
     */
    public void init()
    {

        // Create the configuration context

        ConfigLookupContext configCtx = new ConfigLookupContext(ConfigArea);

        // Set the platform type

        determinePlatformType();

        // Process the security configuration, must be done first to set the
        // global ACL list

        Config config = m_configService.getConfig(ConfigSecurity, configCtx);
        processSecurityConfig(config);

        // Process the CIFS server configuration

        config = m_configService.getConfig(ConfigCIFS, configCtx);
        processCIFSServerConfig(config);

        // Process the filesystems configuration

        config = m_configService.getConfig(ConfigFilesystems, configCtx);
        processFilesystemsConfig(config);

    }

    /**
     * Determine the platform type
     */
    private final void determinePlatformType()
    {
        // Get the operating system type

        String osName = System.getProperty("os.name");

        if (osName.startsWith("Windows"))
            m_platform = PlatformType.WINDOWS;
        else if (osName.equalsIgnoreCase("Linux"))
            m_platform = PlatformType.LINUX;
    }

    /**
     * Return the platform type
     * 
     * @return PlatformType
     */
    private final PlatformType getPlatformType()
    {
        return m_platform;
    }

    /**
     * Process the CIFS server configuration
     * 
     * @param config Config
     */
    private final void processCIFSServerConfig(Config config)
    {

        // Get the host configuration

        ConfigElement elem = config.getConfigElement("host");
        if (elem == null)
            throw new AlfrescoRuntimeException("CIFS server host settings not specified");

        String hostName = elem.getAttribute("name");
        if (hostName == null || hostName.length() == 0)
            throw new AlfrescoRuntimeException("Host name not specified or invalid");
        setServerName(hostName.toUpperCase());

        // Get the domain/workgroup name

        String domain = elem.getAttribute("domain");
        if (domain != null && domain.length() > 0)
            setDomainName(domain.toUpperCase());

        // Check for a server comment

        elem = config.getConfigElement("comment");
        if (elem != null)
            setComment(elem.getValue());

        // Get the network broadcast address

        elem = config.getConfigElement("broadcast");
        if (elem != null)
        {

            // Check if the broadcast mask is a valid numeric IP address

            if (IPAddress.isNumericAddress(elem.getValue()) == false)
                throw new AlfrescoRuntimeException("Invalid broadcast mask, must be n.n.n.n format");

            // Set the network broadcast mask

            setBroadcastMask(elem.getValue());
        }

        // Check for a bind address

        elem = config.getConfigElement("bindto");
        if (elem != null)
        {

            // Validate the bind address

            String bindText = elem.getValue();

            try
            {

                // Check the bind address

                InetAddress bindAddr = InetAddress.getByName(bindText);

                // Set the bind address for the server

                setSMBBindAddress(bindAddr);
            }
            catch (UnknownHostException ex)
            {
                throw new AlfrescoRuntimeException("Invalid CIFS server bind address");
            }
        }

        // Check if the host announcer should be enabled

        elem = config.getConfigElement("hostAnnounce");
        if (elem != null)
        {

            // Check for an announcement interval

            String interval = elem.getAttribute("interval");
            if (interval != null && interval.length() > 0)
            {
                try
                {
                    setHostAnnounceInterval(Integer.parseInt(interval));
                }
                catch (NumberFormatException ex)
                {
                    throw new AlfrescoRuntimeException("Invalid host announcement interval");
                }
            }

            // Check if the domain name has been set, this is required if the
            // host announcer is enabled

            if (getDomainName() == null)
                throw new AlfrescoRuntimeException("Domain name must be specified if host announcement is enabled");

            // Enable host announcement

            setHostAnnouncer(true);
        }

        // Check if NetBIOS SMB is enabled

        elem = config.getConfigElement("netBIOSSMB");
        if (elem != null)
        {
            // Check if NetBIOS over TCP/IP is enabled for the current platform

            String platformsStr = elem.getAttribute("platforms");
            boolean platformOK = false;

            if (platformsStr != null)
            {
                // Parse the list of platforms that NetBIOS over TCP/IP is to be enabled for and
                // check if the current platform is included

                EnumSet<PlatformType> enabledPlatforms = parsePlatformString(platformsStr);
                if (enabledPlatforms.contains(getPlatformType()))
                    platformOK = true;
            }
            else
            {
                // No restriction on platforms

                platformOK = true;
            }

            // Check if the broadcast mask has been specified

            if (getBroadcastMask() == null)
                throw new AlfrescoRuntimeException("Network broadcast mask not specified");

            // Enable the NetBIOS SMB support, if enabled for this platform

            setNetBIOSSMB(platformOK);

            // Check for a bind address

            String bindto = elem.getAttribute("bindto");
            if (bindto != null && bindto.length() > 0)
            {

                // Validate the bind address

                try
                {

                    // Check the bind address

                    InetAddress bindAddr = InetAddress.getByName(bindto);

                    // Set the bind address for the NetBIOS name server

                    setNetBIOSBindAddress(bindAddr);
                }
                catch (UnknownHostException ex)
                {
                    throw new AlfrescoRuntimeException("Invalid NetBIOS bind address");
                }
            }
            else if (hasSMBBindAddress())
            {

                // Use the SMB bind address for the NetBIOS name server

                setNetBIOSBindAddress(getSMBBindAddress());
            }
        }
        else
        {

            // Disable NetBIOS SMB support

            setNetBIOSSMB(false);
        }

        // Check if TCP/IP SMB is enabled

        elem = config.getConfigElement("tcpipSMB");
        if (elem != null)
        {

            // Check if native SMB is enabled for the current platform

            String platformsStr = elem.getAttribute("platforms");
            boolean platformOK = false;

            if (platformsStr != null)
            {
                // Parse the list of platforms that native SMB is to be enabled for and
                // check if the current platform is included

                EnumSet<PlatformType> enabledPlatforms = parsePlatformString(platformsStr);
                if (enabledPlatforms.contains(getPlatformType()))
                    platformOK = true;
            }
            else
            {
                // No restriction on platforms

                platformOK = true;
            }

            // Enable the TCP/IP SMB support, if enabled for this platform

            setTcpipSMB(platformOK);
        }
        else
        {

            // Disable TCP/IP SMB support

            setTcpipSMB(false);
        }

        // Check if Win32 NetBIOS is enabled

        elem = config.getConfigElement("Win32NetBIOS");
        if (elem != null)
        {

            // Check if the Win32 NetBIOS server name has been specified

            String win32Name = elem.getAttribute("name");
            if (win32Name != null && win32Name.length() > 0)
            {

                // Validate the name

                if (win32Name.length() > 16)
                    throw new AlfrescoRuntimeException("Invalid Win32 NetBIOS name, " + win32Name);

                // Set the Win32 NetBIOS file server name

                setWin32NetBIOSName(win32Name);
            }

            // Check if the Win32 NetBIOS LANA has been specified

            String lanaStr = elem.getAttribute("lana");
            if (lanaStr != null && lanaStr.length() > 0)
            {

                // Validate the LANA number

                int lana = -1;

                try
                {
                    lana = Integer.parseInt(lanaStr);
                }
                catch (NumberFormatException ex)
                {
                    throw new AlfrescoRuntimeException("Invalid win32 NetBIOS LANA specified");
                }

                // LANA should be in the range 0-255

                if (lana < 0 || lana > 255)
                    throw new AlfrescoRuntimeException("Invlaid Win32 NetBIOS LANA number, " + lana);

                // Set the LANA number

                setWin32LANA(lana);
            }

            // Check if the current operating system is supported by the Win32
            // NetBIOS handler

            String osName = System.getProperty("os.name");
            if (osName.startsWith("Windows")
                    && (osName.endsWith("95") == false && osName.endsWith("98") == false && osName.endsWith("ME") == false))
            {

                // Enable Win32 NetBIOS

                setWin32NetBIOS(true);
            }
            else
            {

                // Win32 NetBIOS not supported on the current operating system

                setWin32NetBIOS(false);
            }
        }
        else
        {

            // Disable Win32 NetBIOS

            setWin32NetBIOS(false);
        }

        // Check if the host announcer should be enabled

        elem = config.getConfigElement("Win32Announce");
        if (elem != null)
        {

            // Check for an announcement interval

            String interval = elem.getAttribute("interval");
            if (interval != null && interval.length() > 0)
            {
                try
                {
                    setWin32HostAnnounceInterval(Integer.parseInt(interval));
                }
                catch (NumberFormatException ex)
                {
                    throw new AlfrescoRuntimeException("Invalid host announcement interval");
                }
            }

            // Check if the domain name has been set, this is required if the
            // host announcer is enabled

            if (getDomainName() == null)
                throw new AlfrescoRuntimeException("Domain name must be specified if host announcement is enabled");

            // Enable Win32 NetBIOS host announcement

            setWin32HostAnnouncer(true);
        }

        // Check if NetBIOS and/or TCP/IP SMB have been enabled

        if (hasNetBIOSSMB() == false && hasTcpipSMB() == false && hasWin32NetBIOS() == false)
            throw new AlfrescoRuntimeException("NetBIOS SMB, TCP/IP SMB or Win32 NetBIOS must be enabled");

        // Check if WINS servers are configured

        elem = config.getConfigElement("WINS");

        if (elem != null)
        {

            // Get the primary WINS server

            ConfigElement priWinsElem = elem.getChild("primary");

            if (priWinsElem == null || priWinsElem.getValue().length() == 0)
                throw new AlfrescoRuntimeException("No primary WINS server configured");

            // Validate the WINS server address

            InetAddress primaryWINS = null;

            try
            {
                primaryWINS = InetAddress.getByName(priWinsElem.getValue());
            }
            catch (UnknownHostException ex)
            {
                throw new AlfrescoRuntimeException("Invalid primary WINS server address, " + priWinsElem.getValue());
            }

            // Check if a secondary WINS server has been specified

            ConfigElement secWinsElem = elem.getChild("secondary");
            InetAddress secondaryWINS = null;

            if (secWinsElem != null)
            {

                // Validate the secondary WINS server address

                try
                {
                    secondaryWINS = InetAddress.getByName(secWinsElem.getValue());
                }
                catch (UnknownHostException ex)
                {
                    throw new AlfrescoRuntimeException("Invalid secondary WINS server address, "
                            + secWinsElem.getValue());
                }
            }

            // Set the WINS server address(es)

            setPrimaryWINSServer(primaryWINS);
            if (secondaryWINS != null)
                setSecondaryWINSServer(secondaryWINS);
        }

        // Check if session debug is enabled

        elem = config.getConfigElement("sessionDebug");
        if (elem != null)
        {

            // Check for session debug flags

            String flags = elem.getAttribute("flags");
            int sessDbg = 0;

            if (flags != null)
            {

                // Parse the flags

                flags = flags.toUpperCase();
                StringTokenizer token = new StringTokenizer(flags, ",");

                while (token.hasMoreTokens())
                {

                    // Get the current debug flag token

                    String dbg = token.nextToken().trim();

                    // Find the debug flag name

                    int idx = 0;

                    while (idx < m_sessDbgStr.length && m_sessDbgStr[idx].equalsIgnoreCase(dbg) == false)
                        idx++;

                    if (idx > m_sessDbgStr.length)
                        throw new AlfrescoRuntimeException("Invalid session debug flag, " + dbg);

                    // Set the debug flag

                    sessDbg += 1 << idx;
                }
            }

            // Set the session debug flags

            setSessionDebugFlags(sessDbg);
        }
    }

    /**
     * Process the filesystems configuration
     * 
     * @param config Config
     */
    private final void processFilesystemsConfig(Config config)
    {

        // Get the filesystem configuration elements

        List<ConfigElement> filesysElems = config.getConfigElementList("filesystem");

        if (filesysElems != null)
        {

            // Add the filesystems

            for (int i = 0; i < filesysElems.size(); i++)
            {

                // Get the current filesystem configuration

                ConfigElement elem = filesysElems.get(i);
                String filesysName = elem.getAttribute("name");

                try
                {
                    // Create a new filesystem driver instance and create a context for
                    // the new filesystem
                    DiskInterface filesysDriver = this.diskInterface;
                    DiskDeviceContext filesysContext = (DiskDeviceContext) filesysDriver.createContext(elem);

                    // Check if an access control list has been specified

                    AccessControlList acls = null;
                    ConfigElement aclElem = elem.getChild("accessControl");

                    if (aclElem != null)
                    {

                        // Parse the access control list

                        acls = processAccessControlList(aclElem);
                    }
                    else if (hasGlobalAccessControls())
                    {

                        // Use the global access control list for this disk share

                        acls = getGlobalAccessControls();
                    }

                    // Check if change notifications are disabled

                    boolean changeNotify = elem.getChild("disableChangeNotification") == null ? true : false;

                    // Create the shared filesystem

                    DiskSharedDevice filesys = new DiskSharedDevice(filesysName, filesysDriver, filesysContext);

                    // Add any access controls to the share

                    filesys.setAccessControlList(acls);

                    // Enable/disable change notification for this device

                    filesysContext.enableChangeHandler(changeNotify);

                    // Start the filesystem

                    filesysContext.startFilesystem(filesys);

                    // Create the shared device and add to the list of available
                    // shared filesystems

                    addShare(filesys);
                }
                catch (DeviceContextException ex)
                {
                    throw new AlfrescoRuntimeException("Error creating filesystem " + filesysName, ex);
                }
            }
        }
    }

    /**
     * Process the security configuration
     * 
     * @param config Config
     */
    private final void processSecurityConfig(Config config)
    {

        // Check if global access controls have been specified

        ConfigElement globalACLs = config.getConfigElement("globalAccessControl");
        if (globalACLs != null)
        {

            // Parse the access control list

            AccessControlList acls = processAccessControlList(globalACLs);
            if (acls != null)
                setGlobalAccessControls(acls);
        }

        // Check if a JCE provider class has been specified

        ConfigElement jceElem = config.getConfigElement("JCEProvider");
        if (jceElem != null)
        {

            // Set the JCE provider

            setJCEProvider(jceElem.getValue());
        }

        // Check if an authenticator has been specified

        ConfigElement authElem = config.getConfigElement("authenticator");
        if (authElem != null)
        {

            // Get the authenticator type, should be either 'local' or 'passthru'

            String authType = authElem.getAttribute("type");
            if (authType == null)
                throw new AlfrescoRuntimeException("Authenticator type not specified");

            // Set the authenticator class to use

            SrvAuthenticator auth = null;
            if (authType.equalsIgnoreCase("local"))
                auth = new LocalAuthenticator();
            else if (authType.equalsIgnoreCase("passthru"))
                auth = new PassthruAuthenticator();
            else
                throw new AlfrescoRuntimeException("Invalid authenticator type, " + authType);

            // Get the allow guest setting

            boolean allowGuest = authElem.getChild("allowGuest") != null ? true : false;

            // Initialize and set the authenticator class

            setAuthenticator(auth, authElem, allowGuest);
        }

        // Add the users

        ConfigElement usersElem = config.getConfigElement("users");
        if (usersElem != null)
        {

            // Get the list of user elements

            List<ConfigElement> userElemList = usersElem.getChildren();

            for (int i = 0; i < userElemList.size(); i++)
            {

                // Get the current user element

                ConfigElement curUserElem = userElemList.get(i);

                if (curUserElem.getName().equals("localuser"))
                {
                    processUser(curUserElem);
                }
            }
        }

    }

    /**
     * Process an access control sub-section and return the access control list
     * 
     * @param aclsElem ConfigElement
     */
    private final AccessControlList processAccessControlList(ConfigElement aclsElem)
    {

        // Check if there is an access control manager configured

        if (getAccessControlManager() == null)
            throw new AlfrescoRuntimeException("No access control manager configured");

        // Create the access control list

        AccessControlList acls = new AccessControlList();

        // Check if there is a default access level for the ACL group

        String attrib = aclsElem.getAttribute("default");

        if (attrib != null && attrib.length() > 0)
        {

            // Get the access level and validate

            try
            {

                // Parse the access level name

                int access = AccessControlParser.parseAccessTypeString(attrib);

                // Set the default access level for the access control list

                acls.setDefaultAccessLevel(access);
            }
            catch (InvalidACLTypeException ex)
            {
                throw new AlfrescoRuntimeException("Default access level error", ex);
            }
            catch (ACLParseException ex)
            {
                throw new AlfrescoRuntimeException("Default access level error", ex);
            }
        }

        // Parse each access control element

        List<ConfigElement> aclElemList = aclsElem.getChildren();

        if (aclElemList != null && aclElemList.size() > 0)
        {

            // Create the access controls

            for (int i = 0; i < aclsElem.getChildCount(); i++)
            {

                // Get the current ACL element

                ConfigElement curAclElem = aclElemList.get(i);

                try
                {
                    // Create the access control and add to the list

                    acls.addControl(getAccessControlManager().createAccessControl(curAclElem.getName(), curAclElem));
                }
                catch (InvalidACLTypeException ex)
                {
                    throw new AlfrescoRuntimeException("Invalid access control type - " + curAclElem.getName());
                }
                catch (ACLParseException ex)
                {
                    throw new AlfrescoRuntimeException("Access control parse error (" + curAclElem.getName() + ")", ex);
                }
            }
        }

        // Check if there are no access control rules but the default access level is set to 'None',
        // this is not allowed as the share would not be accessible or visible.

        if (acls.getDefaultAccessLevel() == AccessControl.NoAccess && acls.numberOfControls() == 0)
            throw new AlfrescoRuntimeException("Empty access control list and default access 'None' not allowed");

        // Return the access control list

        return acls;
    }

    /**
     * Add a user account
     * 
     * @param user ConfigElement
     */
    private final void processUser(ConfigElement user)
    {

        // Get the username

        String attr = user.getAttribute("name");
        if (attr == null || attr.length() == 0)
            throw new AlfrescoRuntimeException("User name not specified, or zero length");

        // Check if the user already exists

        String userName = attr;

        if (hasUserAccounts() && getUserAccounts().findUser(userName) != null)
            throw new AlfrescoRuntimeException("User " + userName + " already defined");

        // Get the password for the account

        ConfigElement elem = user.getChild("password");
        if (elem == null)
            throw new AlfrescoRuntimeException("No password specified for user " + userName);

        String password = elem.getValue();

        // Create the user account

        UserAccount userAcc = new UserAccount(userName, password);

        // Check if the user in an administrator

        if (user.getChild("administrator") != null)
            userAcc.setAdministrator(true);

        // Get the real user name and comment

        elem = user.getChild("realname");
        if (elem != null)
            userAcc.setRealName(elem.getValue());

        elem = user.getChild("comment");
        if (elem != null)
            userAcc.setComment(elem.getValue());

        // Add the user account

        UserAccountList accList = getUserAccounts();
        if (accList == null)
            setUserAccounts(new UserAccountList());
        getUserAccounts().addUser(userAcc);
    }

    /**
     * Parse the platforms attribute returning the set of platform ids
     * 
     * @param platformStr String
     * @return EnumSet<PlatformType>
     */
    private final EnumSet<PlatformType> parsePlatformString(String platformStr)
    {
        // Split the platform string and build up a set of platform types

        EnumSet<PlatformType> platformTypes = EnumSet.noneOf(PlatformType.class);
        if (platformStr == null || platformStr.length() == 0)
            return platformTypes;

        StringTokenizer token = new StringTokenizer(platformStr.toUpperCase(), ",");
        String typ = null;

        try
        {
            while (token.hasMoreTokens())
            {

                // Get the current platform type string and validate

                typ = token.nextToken().trim();
                PlatformType platform = PlatformType.valueOf(typ);

                if (platform != PlatformType.Unknown)
                    platformTypes.add(platform);
                else
                    throw new AlfrescoRuntimeException("Invalid platform type, " + typ);
            }
        }
        catch (IllegalArgumentException ex)
        {
            throw new AlfrescoRuntimeException("Invalid platform type, " + typ);
        }

        // Return the platform types

        return platformTypes;
    }

    /**
     * Add a shared device to the server configuration.
     * 
     * @param shr SharedDevice
     * @return boolean
     */
    public final boolean addShare(SharedDevice shr)
    {
        return m_shareList.addShare(shr);
    }

    /**
     * Add a server to the list of active servers
     * 
     * @param srv NetworkServer
     */
    public final void addServer(NetworkServer srv)
    {
        m_serverList.addServer(srv);
    }

    /**
     * Find an active server using the protocol name
     * 
     * @param proto String
     * @return NetworkServer
     */
    public final NetworkServer findServer(String proto)
    {
        return m_serverList.findServer(proto);
    }

    /**
     * Remove an active server
     * 
     * @param proto String
     * @return NetworkServer
     */
    public final NetworkServer removeServer(String proto)
    {
        return m_serverList.removeServer(proto);
    }

    /**
     * Return the number of active servers
     * 
     * @return int
     */
    public final int numberOfServers()
    {
        return m_serverList.numberOfServers();
    }

    /**
     * Return the server at the specified index
     * 
     * @param idx int
     * @return NetworkServer
     */
    public final NetworkServer getServer(int idx)
    {
        return m_serverList.getServer(idx);
    }

    /**
     * Check if there is an access control manager configured
     * 
     * @return boolean
     */
    public final boolean hasAccessControlManager()
    {
        return m_aclManager != null ? true : false;
    }

    /**
     * Get the access control manager that is used to control per share access
     * 
     * @return AccessControlManager
     */
    public final AccessControlManager getAccessControlManager()
    {
        return m_aclManager;
    }

    /**
     * Check if the global access control list is configured
     * 
     * @return boolean
     */
    public final boolean hasGlobalAccessControls()
    {
        return m_globalACLs != null ? true : false;
    }

    /**
     * Return the global access control list
     * 
     * @return AccessControlList
     */
    public final AccessControlList getGlobalAccessControls()
    {
        return m_globalACLs;
    }

    /**
     * Get the authenticator object that is used to provide user and share connection
     * authentication.
     * 
     * @return Authenticator
     */
    public final SrvAuthenticator getAuthenticator()
    {
        return m_authenticator;
    }

    /**
     * Return the local address that the SMB server should bind to.
     * 
     * @return java.net.InetAddress
     */
    public final InetAddress getSMBBindAddress()
    {
        return m_smbBindAddress;
    }

    /**
     * Return the local address that the NetBIOS name server should bind to.
     * 
     * @return java.net.InetAddress
     */
    public final InetAddress getNetBIOSBindAddress()
    {
        return m_nbBindAddress;
    }

    /**
     * Return the network broadcast mask to be used for broadcast datagrams.
     * 
     * @return java.lang.String
     */
    public final String getBroadcastMask()
    {
        return m_broadcast;
    }

    /**
     * Return the server comment.
     * 
     * @return java.lang.String
     */
    public final String getComment()
    {
        return m_comment != null ? m_comment : "";
    }

    /**
     * Return the domain name.
     * 
     * @return java.lang.String
     */
    public final String getDomainName()
    {
        return m_domain;
    }

    /**
     * Return the enabled SMB dialects that the server will use when negotiating sessions.
     * 
     * @return DialectSelector
     */
    public final DialectSelector getEnabledDialects()
    {
        return m_dialects;
    }

    /**
     * Return the server name.
     * 
     * @return java.lang.String
     */
    public final String getServerName()
    {
        return m_name;
    }

    /**
     * Return the server type flags.
     * 
     * @return int
     */
    public final int getServerType()
    {
        return m_srvType;
    }

    /**
     * Return the server debug flags.
     * 
     * @return int
     */
    public final int getSessionDebugFlags()
    {
        return m_sessDebug;
    }

    /**
     * Return the shared device list.
     * 
     * @return SharedDeviceList
     */
    public final SharedDeviceList getShares()
    {
        return m_shareList;
    }

    /**
     * Return the share mapper
     * 
     * @return ShareMapper
     */
    public final ShareMapper getShareMapper()
    {
        return m_shareMapper;
    }

    /**
     * Return the user account list.
     * 
     * @return UserAccountList
     */
    public final UserAccountList getUserAccounts()
    {
        return m_userList;
    }

    /**
     * Return the Win32 NetBIOS server name, if null the default server name will be used
     * 
     * @return String
     */
    public final String getWin32ServerName()
    {
        return m_win32NBName;
    }

    /**
     * Determine if the server should be announced via Win32 NetBIOS, so that it appears under
     * Network Neighborhood.
     * 
     * @return boolean
     */
    public final boolean hasWin32EnableAnnouncer()
    {
        return m_win32NBAnnounce;
    }

    /**
     * Return the Win32 NetBIOS host announcement interval, in minutes
     * 
     * @return int
     */
    public final int getWin32HostAnnounceInterval()
    {
        return m_win32NBAnnounceInterval;
    }

    /**
     * Return the Win3 NetBIOS LANA number to use, or -1 for the first available
     * 
     * @return int
     */
    public final int getWin32LANA()
    {
        return m_win32NBLANA;
    }

    /**
     * Return the timezone name
     * 
     * @return String
     */
    public final String getTimeZone()
    {
        return m_timeZone;
    }

    /**
     * Return the timezone offset from UTC in seconds
     * 
     * @return int
     */
    public final int getTimeZoneOffset()
    {
        return m_tzOffset;
    }

    /**
     * Determine if the primary WINS server address has been set
     * 
     * @return boolean
     */
    public final boolean hasPrimaryWINSServer()
    {
        return m_winsPrimary != null ? true : false;
    }

    /**
     * Return the primary WINS server address
     * 
     * @return InetAddress
     */
    public final InetAddress getPrimaryWINSServer()
    {
        return m_winsPrimary;
    }

    /**
     * Determine if the secondary WINS server address has been set
     * 
     * @return boolean
     */
    public final boolean hasSecondaryWINSServer()
    {
        return m_winsSecondary != null ? true : false;
    }

    /**
     * Return the secondary WINS server address
     * 
     * @return InetAddress
     */
    public final InetAddress getSecondaryWINSServer()
    {
        return m_winsSecondary;
    }

    /**
     * Determine if the SMB server should bind to a particular local address
     * 
     * @return boolean
     */
    public final boolean hasSMBBindAddress()
    {
        return m_smbBindAddress != null ? true : false;
    }

    /**
     * Determine if the NetBIOS name server should bind to a particular local address
     * 
     * @return boolean
     */
    public final boolean hasNetBIOSBindAddress()
    {
        return m_nbBindAddress != null ? true : false;
    }

    /**
     * Determine if NetBIOS name server debugging is enabled
     * 
     * @return boolean
     */
    public final boolean hasNetBIOSDebug()
    {
        return m_nbDebug;
    }

    /**
     * Determine if host announcement debugging is enabled
     * 
     * @return boolean
     */
    public final boolean hasHostAnnounceDebug()
    {
        return m_announceDebug;
    }

    /**
     * Determine if the server should be announced so that it appears under Network Neighborhood.
     * 
     * @return boolean
     */
    public final boolean hasEnableAnnouncer()
    {
        return m_announce;
    }

    /**
     * Return the host announcement interval, in minutes
     * 
     * @return int
     */
    public final int getHostAnnounceInterval()
    {
        return m_announceInterval;
    }

    /**
     * Return the JCE provider class name
     * 
     * @return String
     */
    public final String getJCEProvider()
    {
        return m_jceProviderClass;
    }

    /**
     * Determine if Macintosh extension SMBs are enabled
     * 
     * @return boolean
     */
    public final boolean hasMacintoshExtensions()
    {
        return m_macExtensions;
    }

    /**
     * Determine if there are any user accounts defined.
     * 
     * @return boolean
     */
    public final boolean hasUserAccounts()
    {
        if (m_userList != null && m_userList.numberOfUsers() > 0)
            return true;
        return false;
    }

    /**
     * Determine if NetBIOS SMB is enabled
     * 
     * @return boolean
     */
    public final boolean hasNetBIOSSMB()
    {
        return m_netBIOSEnable;
    }

    /**
     * Determine if TCP/IP SMB is enabled
     * 
     * @return boolean
     */
    public final boolean hasTcpipSMB()
    {
        return m_tcpSMBEnable;
    }

    /**
     * Determine if Win32 NetBIOS is enabled
     * 
     * @return boolean
     */
    public final boolean hasWin32NetBIOS()
    {
        return m_win32NBEnable;
    }

    /**
     * Check if the SMB server is enabled
     * 
     * @return boolean
     */
    public final boolean isSMBServerEnabled()
    {
        return m_smbEnable;
    }

    /**
     * Set the SMB server enabled state
     * 
     * @param ena boolean
     */
    public final void setSMBServerEnabled(boolean ena)
    {
        m_smbEnable = ena;
    }

    /**
     * Set the authenticator to be used to authenticate users and share connections.
     * 
     * @param auth SrvAuthenticator
     * @param params ConfigElement
     * @param allowGuest boolean
     */
    public final void setAuthenticator(SrvAuthenticator auth, ConfigElement params, boolean allowGuest)
    {

        // Set the server authenticator mode and guest access

        auth.setAccessMode(SrvAuthenticator.USER_MODE);
        auth.setAllowGuest(allowGuest);

        // Initialize the authenticator using the parameter values

        try
        {
            auth.initialize(this, params);
        }
        catch (InvalidConfigurationException ex)
        {
            throw new AlfrescoRuntimeException("Failed to initialize authenticator", ex);
        }

        // Set the server authenticator and initialization parameters

        m_authenticator = auth;
    }

    /**
     * Set the local address that the SMB server should bind to.
     * 
     * @param addr InetAddress
     */
    public final void setSMBBindAddress(InetAddress addr)
    {
        m_smbBindAddress = addr;
    }

    /**
     * Set the local address that the NetBIOS name server should bind to.
     * 
     * @param addr InetAddress
     */
    public final void setNetBIOSBindAddress(InetAddress addr)
    {
        m_nbBindAddress = addr;
    }

    /**
     * Set the broadcast mask to be used for broadcast datagrams.
     * 
     * @param mask String
     */
    public final void setBroadcastMask(String mask)
    {
        m_broadcast = mask;
    }

    /**
     * Set the server comment.
     * 
     * @param comment String
     */
    public final void setComment(String comment)
    {
        m_comment = comment;
    }

    /**
     * Set the domain that the server belongs to.
     * 
     * @param domain String
     */
    public final void setDomainName(String domain)
    {
        m_domain = domain;
    }

    /**
     * Enable/disable the host announcer.
     * 
     * @param b boolean
     */
    public final void setHostAnnouncer(boolean b)
    {
        m_announce = b;
    }

    /**
     * Set the host announcement interval, in minutes
     * 
     * @param ival int
     */
    public final void setHostAnnounceInterval(int ival)
    {
        m_announceInterval = ival;
    }

    /**
     * Set the JCE provider
     * 
     * @param providerClass String
     */
    public final void setJCEProvider(String providerClass)
    {

        // Validate the JCE provider class

        try
        {

            // Load the JCE provider class and validate

            Object jceObj = Class.forName(providerClass).newInstance();
            if (jceObj instanceof java.security.Provider)
            {

                // Inform listeners, validate the configuration change

                Provider jceProvider = (Provider) jceObj;

                // Save the JCE provider class name

                m_jceProviderClass = providerClass;

                // Add the JCE provider

                Security.addProvider(jceProvider);
            }
            else
            {
                throw new AlfrescoRuntimeException("JCE provider class is not a valid Provider class");
            }
        }
        catch (ClassNotFoundException ex)
        {
            throw new AlfrescoRuntimeException("JCE provider class " + providerClass + " not found");
        }
        catch (Exception ex)
        {
            throw new AlfrescoRuntimeException("JCE provider class error", ex);
        }
    }

    /**
     * Enable/disable NetBIOS name server debug output
     * 
     * @param ena boolean
     */
    public final void setNetBIOSDebug(boolean ena)
    {
        m_nbDebug = ena;
    }

    /**
     * Enable/disable host announcement debug output
     * 
     * @param ena boolean
     */
    public final void setHostAnnounceDebug(boolean ena)
    {
        m_announceDebug = ena;
    }

    /**
     * Set the server name.
     * 
     * @param name String
     */
    public final void setServerName(String name)
    {
        m_name = name;
    }

    /**
     * Set the debug flags to be used by the server.
     * 
     * @param flags int
     */
    public final void setSessionDebugFlags(int flags)
    {
        m_sessDebug = flags;
    }

    /**
     * Set the user account list.
     * 
     * @param users UserAccountList
     */
    public final void setUserAccounts(UserAccountList users)
    {
        m_userList = users;
    }

    /**
     * Set the global access control list
     * 
     * @param acls AccessControlList
     */
    public final void setGlobalAccessControls(AccessControlList acls)
    {
        m_globalACLs = acls;
    }

    /**
     * Enable/disable the NetBIOS SMB support
     * 
     * @param ena boolean
     */
    public final void setNetBIOSSMB(boolean ena)
    {
        m_netBIOSEnable = ena;
    }

    /**
     * Enable/disable the TCP/IP SMB support
     * 
     * @param ena boolean
     */
    public final void setTcpipSMB(boolean ena)
    {
        m_tcpSMBEnable = ena;
    }

    /**
     * Enable/disable the Win32 NetBIOS SMB support
     * 
     * @param ena boolean
     */
    public final void setWin32NetBIOS(boolean ena)
    {
        m_win32NBEnable = ena;
    }

    /**
     * Set the Win32 NetBIOS file server name
     * 
     * @param name String
     */
    public final void setWin32NetBIOSName(String name)
    {
        m_win32NBName = name;
    }

    /**
     * Enable/disable the Win32 NetBIOS host announcer.
     * 
     * @param b boolean
     */
    public final void setWin32HostAnnouncer(boolean b)
    {
        m_win32NBAnnounce = b;
    }

    /**
     * Set the Win32 LANA to be used by the Win32 NetBIOS interface
     * 
     * @param ival int
     */
    public final void setWin32LANA(int ival)
    {
        m_win32NBLANA = ival;
    }

    /**
     * Set the Win32 NetBIOS host announcement interval, in minutes
     * 
     * @param ival int
     */
    public final void setWin32HostAnnounceInterval(int ival)
    {
        m_win32NBAnnounceInterval = ival;
    }

    /**
     * Set the server timezone name
     * 
     * @param name String
     * @exception InvalidConfigurationException If the timezone is invalid
     */
    public final void setTimeZone(String name) throws InvalidConfigurationException
    {

        // Validate the timezone

        TimeZone tz = TimeZone.getTimeZone(name);
        if (tz == null)
            throw new InvalidConfigurationException("Invalid timezone, " + name);

        // Set the timezone name and offset from UTC in minutes
        //
        // Invert the result of TimeZone.getRawOffset() as SMB/CIFS requires
        // positive minutes west of UTC

        m_timeZone = name;
        m_tzOffset = -(tz.getRawOffset() / 60000);
    }

    /**
     * Set the timezone offset from UTC in seconds (+/-)
     * 
     * @param offset int
     */
    public final void setTimeZoneOffset(int offset)
    {
        m_tzOffset = offset;
    }

    /**
     * Set the primary WINS server address
     * 
     * @param addr InetAddress
     */
    public final void setPrimaryWINSServer(InetAddress addr)
    {
        m_winsPrimary = addr;
    }

    /**
     * Set the secondary WINS server address
     * 
     * @param addr InetAddress
     */
    public final void setSecondaryWINSServer(InetAddress addr)
    {
        m_winsSecondary = addr;
    }
}