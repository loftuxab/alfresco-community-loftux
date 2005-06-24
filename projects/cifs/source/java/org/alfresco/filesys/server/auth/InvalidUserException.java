package org.alfresco.filesys.server.auth;

/**
 * Invalid User Exception Class
 */
public class InvalidUserException extends Exception
{
    private static final long serialVersionUID = 3833743295984645425L;

    /**
     * Default constructor.
     */
    public InvalidUserException()
    {
        super();
    }

    /**
     * Class constructor.
     * 
     * @param s java.lang.String
     */
    public InvalidUserException(String s)
    {
        super(s);
    }
}
