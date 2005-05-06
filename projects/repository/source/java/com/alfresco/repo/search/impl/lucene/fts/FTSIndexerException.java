/*
 * Created on 22-Apr-2005
 *
 * TODO Comment this class
 * 
 * 
 */
package org.alfresco.repo.search.impl.lucene.fts;

public class FTSIndexerException extends RuntimeException
{

    /**
     * 
     */
    private static final long serialVersionUID = 3258134635127912754L;

    public FTSIndexerException()
    {
        super();
    }

    public FTSIndexerException(String message)
    {
        super(message);
    }

    public FTSIndexerException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public FTSIndexerException(Throwable cause)
    {
        super(cause);
    }

}
