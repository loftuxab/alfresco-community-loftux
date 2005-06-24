package org.alfresco.filesys.server.filesys;

import java.io.IOException;

/**
 * <p>
 * Thrown when a disk write or file extend will exceed the available disk quota for the shared
 * filesystem.
 */
public class DiskFullException extends IOException
{
    private static final long serialVersionUID = 3256446901959472181L;

    /**
     * Default constructor
     */
    public DiskFullException()
    {
        super();
    }

    /**
     * Class constructor
     * 
     * @param msg String
     */
    public DiskFullException(String msg)
    {
        super(msg);
    }
}
