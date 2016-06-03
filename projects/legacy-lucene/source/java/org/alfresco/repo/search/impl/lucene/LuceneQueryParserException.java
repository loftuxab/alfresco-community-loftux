package org.alfresco.repo.search.impl.lucene;

import org.alfresco.error.AlfrescoRuntimeException;

/**
 * @author Andy
 *
 */
public class LuceneQueryParserException extends AlfrescoRuntimeException
{

    /**
     * 
     */
    private static final long serialVersionUID = 4886993838297301968L;

    /**
     * @param msgId
     */
    public LuceneQueryParserException(String msgId)
    {
        super(msgId);
        // TODO Auto-generated constructor stub
    }

    /**
     * @param msgId
     * @param msgParams
     */
    public LuceneQueryParserException(String msgId, Object[] msgParams)
    {
        super(msgId, msgParams);
        // TODO Auto-generated constructor stub
    }

    /**
     * @param msgId
     * @param cause
     */
    public LuceneQueryParserException(String msgId, Throwable cause)
    {
        super(msgId, cause);
        // TODO Auto-generated constructor stub
    }

    /**
     * @param msgId
     * @param msgParams
     * @param cause
     */
    public LuceneQueryParserException(String msgId, Object[] msgParams, Throwable cause)
    {
        super(msgId, msgParams, cause);
        // TODO Auto-generated constructor stub
    }

}
