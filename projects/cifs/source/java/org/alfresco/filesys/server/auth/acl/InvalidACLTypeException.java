package org.alfresco.filesys.server.auth.acl;

/**
 * Invalid ACL Type Exception Class
 */
public class InvalidACLTypeException extends Exception
{
    private static final long serialVersionUID = 3257844398418310708L;

    /**
     * Default constructor.
     */
    public InvalidACLTypeException()
    {
        super();
    }

    /**
     * Class constructor.
     * 
     * @param s java.lang.String
     */
    public InvalidACLTypeException(String s)
    {
        super(s);
    }
}
