package org.alfresco.filesys.smb.dcerpc;

/**
 * Wkssvc Operation Ids Class
 */
public class Wkssvc
{
    // Wkssvc opcodes

    public static final int NetWkstaGetInfo = 0x00;

    /**
     * Convert an opcode to a function name
     * 
     * @param opCode int
     * @return String
     */
    public final static String getOpcodeName(int opCode)
    {
        String ret = "";
        switch (opCode)
        {
        case NetWkstaGetInfo:
            ret = "NetWkstaGetInfo";
            break;
        }
        return ret;
    }
}
