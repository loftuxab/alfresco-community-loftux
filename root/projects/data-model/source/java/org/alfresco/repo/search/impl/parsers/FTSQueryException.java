package org.alfresco.repo.search.impl.parsers;

import org.alfresco.error.AlfrescoRuntimeException;

public class FTSQueryException extends AlfrescoRuntimeException
{

    /**
     * 
     */
    private static final long serialVersionUID = -4554441051084471802L;

    public FTSQueryException(String msgId, Object[] msgParams, Throwable cause)
    {
        super(msgId, msgParams, cause);
        // TODO Auto-generated constructor stub
    }

    public FTSQueryException(String msgId, Object[] msgParams)
    {
        super(msgId, msgParams);
        // TODO Auto-generated constructor stub
    }

    public FTSQueryException(String msgId, Throwable cause)
    {
        super(msgId, cause);
        // TODO Auto-generated constructor stub
    }

    public FTSQueryException(String msgId)
    {
        super(msgId);
        // TODO Auto-generated constructor stub
    }

}
