package org.alfresco.filesys.smb.dcerpc.info;

import org.alfresco.filesys.smb.dcerpc.DCEBuffer;
import org.alfresco.filesys.smb.dcerpc.DCEBufferException;
import org.alfresco.filesys.smb.dcerpc.DCEList;
import org.alfresco.filesys.smb.dcerpc.DCEReadable;

/**
 * Connection Information List Class
 */
public class ConnectionInfoList extends DCEList
{

    /**
     * Default constructor
     */
    public ConnectionInfoList()
    {
        super();
    }

    /**
     * Class constructor
     * 
     * @param buf DCEBuffer
     * @exception DCEBufferException
     */
    public ConnectionInfoList(DCEBuffer buf) throws DCEBufferException
    {
        super(buf);
    }

    /**
     * Create a new connection information object
     * 
     * @return DCEReadable
     */
    protected DCEReadable getNewObject()
    {
        return new ConnectionInfo(getInformationLevel());
    }
}
