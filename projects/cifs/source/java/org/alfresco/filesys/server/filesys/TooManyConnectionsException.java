package org.alfresco.filesys.server.filesys;

/**
 * <p>
 * This error indicates that too many tree connections are currently open on a session. The new tree
 * connection request will be rejected by the server.
 */
public class TooManyConnectionsException extends Exception
{
    private static final long serialVersionUID = 3257845497929414961L;

    /**
     * TooManyConnectionsException constructor.
     */
    public TooManyConnectionsException()
    {
        super();
    }

    /**
     * TooManyConnectionsException constructor.
     * 
     * @param s java.lang.String
     */
    public TooManyConnectionsException(String s)
    {
        super(s);
    }
}