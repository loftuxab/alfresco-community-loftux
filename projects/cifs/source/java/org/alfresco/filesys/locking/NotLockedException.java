package org.alfresco.filesys.locking;

import java.io.IOException;

/**
 * Not Locked Exception Class
 * <p>
 * Thrown when an unlock request is received that has not active lock on a file.
 */
public class NotLockedException extends IOException
{
    private static final long serialVersionUID = 3834594296543261488L;

    /**
     * Class constructor.
     */
    public NotLockedException()
    {
        super();
    }

    /**
     * Class constructor.
     * 
     * @param s java.lang.String
     */
    public NotLockedException(String s)
    {
        super(s);
    }
}
