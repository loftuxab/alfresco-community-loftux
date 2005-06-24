package org.alfresco.filesys.server.filesys;

/**
 * <p>
 * This error is generated when a tree connection has no free file slots. The new file open request
 * will be rejected by the server.
 */
public class TooManyFilesException extends Exception
{
    private static final long serialVersionUID = 4051332218943060273L;

    /**
     * TooManyFilesException constructor.
     */
    public TooManyFilesException()
    {
        super();
    }

    /**
     * TooManyFilesException constructor.
     * 
     * @param s java.lang.String
     */
    public TooManyFilesException(String s)
    {
        super(s);
    }
}