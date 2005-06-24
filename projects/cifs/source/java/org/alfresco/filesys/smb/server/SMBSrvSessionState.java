package org.alfresco.filesys.smb.server;

/**
 * <p>
 * Contains the various states that an SMB server session will go through during the session
 * lifetime.
 */
public class SMBSrvSessionState
{

    // NetBIOS session has been closed.

    public static final int NBHANGUP = 5;

    // NetBIOS session request state.

    public static final int NBSESSREQ = 0;

    // SMB session closed down.

    public static final int SMBCLOSED = 4;

    // Negotiate SMB dialect.

    public static final int SMBNEGOTIATE = 1;

    // SMB session is initialized, ready to receive/handle standard SMB requests.

    public static final int SMBSESSION = 3;

    // SMB session setup.

    public static final int SMBSESSSETUP = 2;

    // State name strings

    private static final String _stateName[] = {
            "NBSESSREQ",
            "SMBNEGOTIATE",
            "SMBSESSSETUP",
            "SMBSESSION",
            "SMBCLOSED",
            "NBHANGUP" };

    /**
     * Return the specified SMB state as a string.
     */
    public static String getStateAsString(int state)
    {
        if (state < _stateName.length)
            return _stateName[state];
        return "[UNKNOWN]";
    }
}