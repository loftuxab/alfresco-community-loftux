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

package org.alfresco.filesys.server.auth.ntlm;

import java.util.Date;

/**
 * NTLM Logon Details Class
 * 
 * <p>Contains the details from the NTLM authentication session that was used to authenticate a user.
 * 
 * @author GKSpencer
 */
public class NTLMLogonDetails
{
    // User name, workstation name and domain
    
    private String m_user;
    private String m_workstation;
    private String m_domain;
    
    // Authentication server name/address
    
    private String m_authSrvAddr;
    
    // Date/time the user was authenticated
    
    private long m_authTime;
    
    // User logged on via guest access
    
    private boolean m_guestAccess;
    
    /**
     * Class constructor
     *
     * @param user String
     * @param wks String
     * @param domain String
     * @param guest boolean
     * @param authSrv String
     */
    public NTLMLogonDetails(String user, String wks, String domain, boolean guest, String authSrv)
    {
        m_user = user;
        m_workstation = wks;
        m_domain = domain;

        m_authSrvAddr = authSrv;
        
        m_guestAccess = guest;
        
        m_authTime = System.currentTimeMillis();
    }
    
    /**
     * Return the user name
     * 
     * @return String
     */
    public final String getUserName()
    {
        return m_user;
    }
    
    /**
     * Return the workstation name
     * 
     * @return String
     */
    public final String getWorkstation()
    {
        return m_workstation;
    }
    
    /**
     * Return the domain name
     * 
     * @return String
     */
    public final String getDomain()
    {
        return m_domain;
    }
    
    /**
     * Return the authentication server name/address
     * 
     * @return String
     */
    public final String getAuthenticationServer()
    {
        return m_authSrvAddr;
    }

    /**
     * Return the date/time the user was authenticated
     * 
     * @return long
     */
    public final long authenticatedAt()
    {
        return m_authTime;
    }
    
    /**
     * Return the NTLM logon details as a string
     * 
     * @return String
     */
    public String toString()
    {
        StringBuilder str = new StringBuilder();
        
        str.append("[");
        str.append(getUserName());
        str.append(",Wks:");
        str.append(getWorkstation());
        str.append(",Dom:");
        str.append(getDomain());
        str.append(",AuthSrv:");
        str.append(getAuthenticationServer());
        str.append(",");
        str.append(new Date(authenticatedAt()));
        str.append("]");
        
        return str.toString();
    }
}
