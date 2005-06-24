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
