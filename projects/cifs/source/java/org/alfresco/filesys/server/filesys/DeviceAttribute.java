package org.alfresco.filesys.server.filesys;

/**
 * Device Attribute Constants Class
 * <p>
 * Specifies the constants that can be used to set the DiskDeviceContext device attributes.
 */
public final class DeviceAttribute
{
    // Device attributes

    public static final int Removable = 0x0001;
    public static final int ReadOnly = 0x0002;
    public static final int FloppyDisk = 0x0004;
    public static final int WriteOnce = 0x0008;
    public static final int Remote = 0x0010;
    public static final int Mounted = 0x0020;
    public static final int Virtual = 0x0040;
}
