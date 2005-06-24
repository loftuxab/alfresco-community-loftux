package org.alfresco.filesys.server.filesys;

/**
 * Disk Size Interface
 * <p>
 * Optional interface that a DiskInterface driver can implement to provide disk sizing information.
 * The disk size information may also be specified via the configuration.
 */
public interface DiskSizeInterface
{

    /**
     * Get the disk information for this shared disk device.
     * 
     * @param cts DiskDeviceContext
     * @param diskDev SrvDiskInfo
     * @exception java.io.IOException The exception description.
     */
    public void getDiskInformation(DiskDeviceContext ctx, SrvDiskInfo diskDev) throws java.io.IOException;
}
