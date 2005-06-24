package org.alfresco.filesys.smb;

/**
 * SMB device types class.
 * <p>
 * The class provides symbols for the remote device types that may be connected to. The values are
 * also used when returning remote share information.
 */
public class SMBDeviceType
{

    // Device type constants

    public static final int Disk = 0;
    public static final int Printer = 1;
    public static final int Comm = 2;
    public static final int Pipe = 3;
    public static final int Unknown = -1;

    /**
     * Convert the device type to a string
     * 
     * @param devtyp Device type
     * @return Device type string
     */
    public static String asString(int devtyp)
    {
        String devStr = null;

        switch (devtyp)
        {
        case Disk:
            devStr = "Disk";
            break;
        case Printer:
            devStr = "Printer";
            break;
        case Pipe:
            devStr = "Pipe";
            break;
        case Comm:
            devStr = "Comm";
            break;
        default:
            devStr = "Unknown";
            break;
        }
        return devStr;
    }
}