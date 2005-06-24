package org.alfresco.filesys.smb.server;

import org.alfresco.filesys.server.core.*;

/**
 * Administration shared device, IPC$.
 */
final class AdminSharedDevice extends SharedDevice
{

    /**
     * Class constructor
     */
    protected AdminSharedDevice()
    {
        super("IPC$", ShareType.ADMINPIPE, null);

        // Set the device attributes

        setAttributes(SharedDevice.Admin + SharedDevice.Hidden);
    }
}