package org.alfresco.filesys.locking;

import java.io.IOException;

/**
 * Lock Conflict Exception Class
 * <p>
 * Thrown when a lock request overlaps with an existing lock on a file.
 */
public class LockConflictException extends IOException
{

    // Serializable version id

    private static final long serialVersionUID = 0;

    /**
     * Class constructor.
     */
    public LockConflictException()
    {
        super();
    }

    /**
     * Class constructor.
     * 
     * @param s java.lang.String
     */
    public LockConflictException(String s)
    {
        super(s);
    }
}
