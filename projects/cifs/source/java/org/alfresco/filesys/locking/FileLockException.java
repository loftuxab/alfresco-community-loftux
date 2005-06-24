package org.alfresco.filesys.locking;

import java.io.IOException;

/**
 * File Lock Exception Class
 */
public class FileLockException extends IOException
{
    private static final long serialVersionUID = 3257845472092893751L;

    /**
     * Class constructor.
     */
    public FileLockException()
    {
        super();
    }

    /**
     * Class constructor.
     * 
     * @param s java.lang.String
     */
    public FileLockException(String s)
    {
        super(s);
    }
}
