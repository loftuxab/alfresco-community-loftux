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
package org.alfresco.filesys.server.auth.acl;

import org.alfresco.filesys.server.SrvSession;
import org.alfresco.filesys.server.auth.ClientInfo;
import org.alfresco.filesys.server.core.SharedDevice;

/**
 * Domain Name Access Control Class
 * <p>
 * Allow/disallow access based on the SMB/CIFS session callers domain name.
 */
public class DomainAccessControl extends AccessControl
{

    /**
     * Class constructor
     * 
     * @param domainName String
     * @param type String
     * @param access int
     */
    protected DomainAccessControl(String domainName, String type, int access)
    {
        super(domainName, type, access);
    }

    /**
     * Check if the domain name matches the access control domain name and return the allowed
     * access.
     * 
     * @param sess SrvSession
     * @param share SharedDevice
     * @param mgr AccessControlManager
     * @return int
     */
    public int allowsAccess(SrvSession sess, SharedDevice share, AccessControlManager mgr)
    {

        // Check if the session has client information

        if (sess.hasClientInformation() == false
                || sess instanceof org.alfresco.filesys.smb.server.SMBSrvSession == false)
            return Default;

        // Check if the domain name matches the access control name

        ClientInfo cInfo = sess.getClientInformation();

        if (cInfo.getDomain() != null && cInfo.getDomain().equalsIgnoreCase(getName()))
            return getAccess();
        return Default;
    }
}
