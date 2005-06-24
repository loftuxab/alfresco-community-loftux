package org.alfresco.filesys.server.filesys;

import java.io.IOException;

/**
 * <p>
 * This exception may be thrown by a disk interface when the file data is not available due to the
 * file being archived or the repository being unavailable.
 */
public class FileOfflineException extends IOException
{
    private static final long serialVersionUID = 3257006574835807795L;

    /**
     * Class constructor.
     */
    public FileOfflineException()
    {
        super();
    }

    /**
     * Class constructor.
     * 
     * @param s java.lang.String
     */
    public FileOfflineException(String s)
    {
        super(s);
    }
}
