package org.alfresco.filesys.server.filesys;

import java.io.IOException;

/**
 * <p>
 * Thrown when an attempt is made to delete a directory that contains files or directories.
 */
public class DirectoryNotEmptyException extends IOException
{
    private static final long serialVersionUID = 3906083464527491128L;

    /**
     * Default constructor
     */
    public DirectoryNotEmptyException()
    {
        super();
    }

    /**
     * Class constructor.
     * 
     * @param s java.lang.String
     */
    public DirectoryNotEmptyException(String s)
    {
        super(s);
    }
}
