package org.alfresco.repo.content;


/**
 * Wraps a general <code>Exceptions</code> that occurred while reading or writing
 * content.
 * 
 * @see Throwable#getCause()
 * 
 * @author Derek Hulley
 */
public class ContentIOException extends RuntimeException
{
    private static final long serialVersionUID = 3258130249983276087L;
    
    public ContentIOException(String msg)
    {
        super(msg);
    }
    
    public ContentIOException(String msg, Throwable cause)
    {
        super(msg, cause);
    }
}
