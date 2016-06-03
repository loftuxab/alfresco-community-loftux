package org.alfresco.repo.search.impl.querymodel;

import org.alfresco.error.AlfrescoRuntimeException;

/**
 * @author andyh
 *
 */
public class QueryModelException extends AlfrescoRuntimeException
{

    /**
     * 
     */
    private static final long serialVersionUID = 5103880924975096422L;

    /**
     * @param msgId String
     */
    public QueryModelException(String msgId)
    {
        super(msgId);
        // TODO Auto-generated constructor stub
    }

    /**
     * @param msgId String
     * @param msgParams Object[]
     */
    public QueryModelException(String msgId, Object[] msgParams)
    {
        super(msgId, msgParams);
        // TODO Auto-generated constructor stub
    }

    /**
     * @param msgId String
     * @param cause Throwable
     */
    public QueryModelException(String msgId, Throwable cause)
    {
        super(msgId, cause);
        // TODO Auto-generated constructor stub
    }

    /**
     * @param msgId String
     * @param msgParams Object[]
     * @param cause Throwable
     */
    public QueryModelException(String msgId, Object[] msgParams, Throwable cause)
    {
        super(msgId, msgParams, cause);
        // TODO Auto-generated constructor stub
    }

}
