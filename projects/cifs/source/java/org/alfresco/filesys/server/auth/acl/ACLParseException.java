package org.alfresco.filesys.server.auth.acl;

/**
 * Access Control Parse Exception Class
 */
public class ACLParseException extends Exception
{
    private static final long serialVersionUID = 3978983284405776688L;

    /**
     * Default constructor.
     */
    public ACLParseException()
    {
        super();
    }

    /**
     * Class constructor.
     * 
     * @param s java.lang.String
     */
    public ACLParseException(String s)
    {
        super(s);
    }
}
