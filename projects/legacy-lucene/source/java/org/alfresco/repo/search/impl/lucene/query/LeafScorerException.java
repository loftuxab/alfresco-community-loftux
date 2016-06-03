package org.alfresco.repo.search.impl.lucene.query;

import org.alfresco.error.AlfrescoRuntimeException;

/**
 * @author Andy
 *
 */
public class LeafScorerException extends AlfrescoRuntimeException
{

    /**
     * 
     */
    private static final long serialVersionUID = -2046162758632112186L;

    /**
     * @param msgId
     */
    public LeafScorerException(String msgId)
    {
        super(msgId);
        // TODO Auto-generated constructor stub
    }

    /**
     * @param msgId
     * @param msgParams
     */
    public LeafScorerException(String msgId, Object[] msgParams)
    {
        super(msgId, msgParams);
        // TODO Auto-generated constructor stub
    }

    /**
     * @param msgId
     * @param cause
     */
    public LeafScorerException(String msgId, Throwable cause)
    {
        super(msgId, cause);
        // TODO Auto-generated constructor stub
    }

    /**
     * @param msgId
     * @param msgParams
     * @param cause
     */
    public LeafScorerException(String msgId, Object[] msgParams, Throwable cause)
    {
        super(msgId, msgParams, cause);
        // TODO Auto-generated constructor stub
    }

}
