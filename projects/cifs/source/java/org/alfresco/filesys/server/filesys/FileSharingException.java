package org.alfresco.filesys.server.filesys;

/**
 * File sharing exception class.
 */
public class FileSharingException extends java.io.IOException
{
    private static final long serialVersionUID = 3258130241309260085L;

    /**
     * Class constructor
     */
    public FileSharingException()
    {
        super();
    }

    /**
     * Class constructor.
     * 
     * @param s java.lang.String
     */
    public FileSharingException(String s)
    {
        super(s);
    }
}
