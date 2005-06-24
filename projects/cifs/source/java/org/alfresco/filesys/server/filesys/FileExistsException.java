package org.alfresco.filesys.server.filesys;

/**
 * <p>
 * This exception may be thrown by a disk interface when an attempt to create a new file fails
 * because the file already exists.
 */
public class FileExistsException extends java.io.IOException
{
    private static final long serialVersionUID = 3258408439242895670L;

    /**
     * FileExistsException constructor.
     */
    public FileExistsException()
    {
        super();
    }

    /**
     * FileExistsException constructor.
     * 
     * @param s java.lang.String
     */
    public FileExistsException(String s)
    {
        super(s);
    }
}