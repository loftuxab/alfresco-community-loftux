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
package org.alfresco.filesys.server;

import java.net.InetAddress;

import org.alfresco.filesys.server.auth.ClientInfo;

/**
 * Server Session Base Class
 * <p>
 * Base class for server session implementations for different protocols.
 */
public abstract class SrvSession
{

    // Network server this session is associated with

    private NetworkServer m_server;

    // Session id/slot number

    private int m_sessId;

    // Unique session id string

    private String m_uniqueId;

    // Process id

    private int m_processId = -1;

    // Session/user is logged on/validated

    private boolean m_loggedOn;

    // Client details

    private ClientInfo m_clientInfo;

    // Challenge key used for this session

    private byte[] m_challenge;

    // Debug flags for this session

    private int m_debug;
    private String m_dbgPrefix;

    // Session shutdown flag

    private boolean m_shutdown;

    // Protocol type

    private String m_protocol;

    // Remote client/host name

    private String m_remoteName;

    // Authentication token, used during logon
    
    private Object m_authToken;
    
    /**
     * Class constructor
     * 
     * @param sessId int
     * @param srv NetworkServer
     * @param proto String
     * @param remName String
     */
    public SrvSession(int sessId, NetworkServer srv, String proto, String remName)
    {
        m_sessId = sessId;
        m_server = srv;

        setProtocolName(proto);
        setRemoteName(remName);
    }

    /**
     * Return the authentication token
     * 
     * @return Object
     */
    public final Object getAuthenticationToken()
    {
        return m_authToken;
    }
    
    /**
     * Determine if the authentication token is set
     * 
     * @return boolean
     */
    public final boolean hasAuthenticationToken()
    {
        return m_authToken != null ? true : false;
    }
    
    /**
     * Return the session challenge key
     * 
     * @return byte[]
     */
    public final byte[] getChallengeKey()
    {
        return m_challenge;
    }

    /**
     * Determine if the challenge key has been set for this session
     * 
     * @return boolean
     */
    public final boolean hasChallengeKey()
    {
        return m_challenge != null ? true : false;
    }

    /**
     * Return the process id
     * 
     * @return int
     */
    public final int getProcessId()
    {
        return m_processId;
    }

    /**
     * Return the remote client network address
     * 
     * @return InetAddress
     */
    public abstract InetAddress getRemoteAddress();

    /**
     * Return the session id for this session.
     * 
     * @return int
     */
    public final int getSessionId()
    {
        return m_sessId;
    }

    /**
     * Return the server this session is associated with
     * 
     * @return NetworkServer
     */
    public final NetworkServer getServer()
    {
        return m_server;
    }

    /**
     * Check if the session has valid client information
     * 
     * @return boolean
     */
    public final boolean hasClientInformation()
    {
        return m_clientInfo != null ? true : false;
    }

    /**
     * Return the client information
     * 
     * @return ClientInfo
     */
    public final ClientInfo getClientInformation()
    {
        return m_clientInfo;
    }

    /**
     * Determine if the protocol type has been set
     * 
     * @return boolean
     */
    public final boolean hasProtocolName()
    {
        return m_protocol != null ? true : false;
    }

    /**
     * Return the protocol name
     * 
     * @return String
     */
    public final String getProtocolName()
    {
        return m_protocol;
    }

    /**
     * Determine if the remote client name has been set
     * 
     * @return boolean
     */
    public final boolean hasRemoteName()
    {
        return m_remoteName != null ? true : false;
    }

    /**
     * Return the remote client name
     * 
     * @return String
     */
    public final String getRemoteName()
    {
        return m_remoteName;
    }

    /**
     * Determine if the session is logged on/validated
     * 
     * @return boolean
     */
    public final boolean isLoggedOn()
    {
        return m_loggedOn;
    }

    /**
     * Determine if the session has been shut down
     * 
     * @return boolean
     */
    public final boolean isShutdown()
    {
        return m_shutdown;
    }

    /**
     * Return the unique session id
     * 
     * @return String
     */
    public final String getUniqueId()
    {
        return m_uniqueId;
    }

    /**
     * Determine if the specified debug flag is enabled.
     * 
     * @return boolean
     * @param dbg int
     */
    public final boolean hasDebug(int dbgFlag)
    {
        if ((m_debug & dbgFlag) != 0)
            return true;
        return false;
    }

    /**
     * Set the authentication token
     * 
     * @param authToken Object
     */
    public final void setAuthenticationToken(Object authToken)
    {
        m_authToken = authToken;
    }
    
    /**
     * Set the client information
     * 
     * @param client ClientInfo
     */
    public final void setClientInformation(ClientInfo client)
    {
        m_clientInfo = client;
    }

    /**
     * Set the session challenge key
     * 
     * @param key byte[]
     */
    public final void setChallengeKey(byte[] key)
    {
        m_challenge = key;
    }

    /**
     * Set the debug output interface.
     * 
     * @param flgs int
     */
    public final void setDebug(int flgs)
    {
        m_debug = flgs;
    }

    /**
     * Set the debug output prefix for this session
     * 
     * @param prefix String
     */
    public final void setDebugPrefix(String prefix)
    {
        m_dbgPrefix = prefix;
    }

    /**
     * Set the logged on/validated status for the session
     * 
     * @param loggedOn boolean
     */
    public final void setLoggedOn(boolean loggedOn)
    {
        m_loggedOn = loggedOn;
    }

    /**
     * Set the process id
     * 
     * @param id int
     */
    public final void setProcessId(int id)
    {
        m_processId = id;
    }

    /**
     * Set the protocol name
     * 
     * @param name String
     */
    public final void setProtocolName(String name)
    {
        m_protocol = name;
    }

    /**
     * Set the remote client name
     * 
     * @param name String
     */
    public final void setRemoteName(String name)
    {
        m_remoteName = name;
    }

    /**
     * Set the session id for this session.
     * 
     * @param id int
     */
    public final void setSessionId(int id)
    {
        m_sessId = id;
    }

    /**
     * Set the unique session id
     * 
     * @param unid String
     */
    public final void setUniqueId(String unid)
    {
        m_uniqueId = unid;
    }

    /**
     * Set the shutdown flag
     * 
     * @param flg boolean
     */
    protected final void setShutdown(boolean flg)
    {
        m_shutdown = flg;
    }

    /**
     * Close the network session
     */
    public void closeSession()
    {
    }
}
