/*
 * Created on Mar 29, 2005
 * 
 * TODO Comment this class
 * 
 * 
 */
package org.alfresco.repo.search;

/**
 * Indexer related exceptions
 * 
 * @author andyh
 * 
 */
public class IndexerException extends RuntimeException
{

    /**
     * 
     */
    private static final long serialVersionUID = 3257286911646447666L;

    public IndexerException()
    {
        super();
    }

    public IndexerException(String message)
    {
        super(message);
    }

    public IndexerException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public IndexerException(Throwable cause)
    {
        super(cause);
    }

}
