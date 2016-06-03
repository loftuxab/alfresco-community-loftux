package org.alfresco.service.cmr.repository;

import org.alfresco.api.AlfrescoPublicApi;
import org.alfresco.error.AlfrescoRuntimeException;

/**
 * Thrown when a nodeRef representation is invalid.
 * 
 * @author rgauss
 *
 */
@AlfrescoPublicApi
public class MalformedNodeRefException extends AlfrescoRuntimeException
{

    private static final long serialVersionUID = 8922346977484016269L;

    public MalformedNodeRefException(String msgId)
    {
        super(msgId);
    }

    public MalformedNodeRefException(String msgId, Object[] msgParams)
    {
        super(msgId, msgParams);
    }

    public MalformedNodeRefException(String msgId, Throwable cause)
    {
        super(msgId, cause);
    }

    public MalformedNodeRefException(String msgId, Object[] msgParams, Throwable cause)
    {
        super(msgId, msgParams, cause);
    }

}
