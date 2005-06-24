package org.alfresco.filesys.locking;

import java.io.IOException;

/**
 * File Unlock Exception Class
 */
public class FileUnlockException extends IOException
{
    private static final long serialVersionUID = 3257290240262484786L;

    /**
     * Class constructor.
     */
    public FileUnlockException()
    {
        super();
    }

    /**
     * Class constructor.
     * 
     * @param s java.lang.String
     */
    public FileUnlockException(String s)
    {
        super(s);
    }
}
