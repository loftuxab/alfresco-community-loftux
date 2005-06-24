package org.alfresco.filesys.server.filesys;

import java.io.IOException;

/**
 * Media Offline Exception Class
 * <p>
 * This exception may be thrown by a disk interface when a file/folder is not available due to the
 * storage media being offline, repository being unavailable, database unavailable or inaccessible
 * or similar condition.
 */
public class MediaOfflineException extends IOException
{
    private static final long serialVersionUID = 3544956554064704306L;

    /**
     * Class constructor.
     */
    public MediaOfflineException()
    {
        super();
    }

    /**
     * Class constructor.
     * 
     * @param s java.lang.String
     */
    public MediaOfflineException(String s)
    {
        super(s);
    }
}
