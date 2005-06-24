package org.alfresco.filesys.smb;

/**
 * Protocol Class
 * <p>
 * Declares constants for the available SMB protocols (TCP/IP NetBIOS and native TCP/IP SMB)
 */
public class Protocol
{

    // Available protocol types

    public final static int TCPNetBIOS = 1;
    public final static int NativeSMB = 2;

    // Protocol control constants

    public final static int UseDefault = 0;
    public final static int None = -1;

    /**
     * Return the protocol type as a string
     * 
     * @param typ int
     * @return String
     */
    public static final String asString(int typ)
    {
        String ret = "";
        if (typ == TCPNetBIOS)
            ret = "TCP/IP NetBIOS";
        else if (typ == NativeSMB)
            ret = "Native SMB (port 445)";

        return ret;
    }
}
