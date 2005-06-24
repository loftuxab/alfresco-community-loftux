package org.alfresco.filesys.smb;

/**
 * Invalid UNC path exception class
 * <p>
 * The InvalidUNCPathException indicates that a UNC path has an invalid format.
 * 
 * @see PCShare
 */
public class InvalidUNCPathException extends Exception
{
    private static final long serialVersionUID = 3257567304241066297L;

    /**
     * Default invalid UNC path exception constructor.
     */

    public InvalidUNCPathException()
    {
    }

    /**
     * Invalid UNC path exception constructor, with additional details string.
     */

    public InvalidUNCPathException(String msg)
    {
        super(msg);
    }
}