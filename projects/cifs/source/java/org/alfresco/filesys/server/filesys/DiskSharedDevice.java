package org.alfresco.filesys.server.filesys;

import org.alfresco.filesys.server.core.DeviceInterface;
import org.alfresco.filesys.server.core.ShareType;
import org.alfresco.filesys.server.core.SharedDevice;

/**
 * <p>
 * A disk shared device has a name, a driver class and a context for the driver.
 */
public class DiskSharedDevice extends SharedDevice
{

    /**
     * Construct a disk share with the specified name and device interface.
     * 
     * @param name Disk share name.
     * @param iface Disk device interface.
     * @param ctx Context that will be passed to the device interface.
     */
    public DiskSharedDevice(String name, DeviceInterface iface, DiskDeviceContext ctx)
    {
        super(name, ShareType.DISK, ctx);
        setInterface(iface);
    }

    /**
     * Construct a disk share with the specified name and device interface.
     * 
     * @param name java.lang.String
     * @param iface DeviceInterface
     * @param ctx DeviceContext
     * @param attrib int
     */
    public DiskSharedDevice(String name, DeviceInterface iface, DiskDeviceContext ctx, int attrib)
    {
        super(name, ShareType.DISK, ctx);
        setInterface(iface);
        setAttributes(attrib);
    }

    /**
     * Return the disk device context
     * 
     * @return DiskDeviceContext
     */
    public final DiskDeviceContext getDiskContext()
    {
        return (DiskDeviceContext) getContext();
    }

    /**
     * Return the disk interface
     * 
     * @return DiskInterface
     */
    public final DiskInterface getDiskInterface()
    {
        try
        {
            if (getInterface() instanceof DiskInterface)
                return (DiskInterface) getInterface();
        }
        catch (Exception ex)
        {
        }
        return null;
    }
}