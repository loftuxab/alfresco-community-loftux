package org.alfresco.filesys.server.filesys;

/**
 * <p>
 * This error is generated when a request is made for an information level that is not currently
 * supported by the SMB server.
 */
public class UnsupportedInfoLevelException extends Exception
{
    private static final long serialVersionUID = 3762538905790395444L;

    /**
     * Class constructor.
     */
    public UnsupportedInfoLevelException()
    {
        super();
    }

    /**
     * Class constructor.
     * 
     * @param str java.lang.String
     */
    public UnsupportedInfoLevelException(String str)
    {
        super(str);
    }
}