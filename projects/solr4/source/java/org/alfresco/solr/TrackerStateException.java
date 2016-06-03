package org.alfresco.solr;

import org.alfresco.error.AlfrescoRuntimeException;

/**
 * @author Andy
 *
 */
public class TrackerStateException extends AlfrescoRuntimeException
{

    /**
     * 
     */
    private static final long serialVersionUID = -3030261887767267309L;

    /**
     * @param msgId
     */
    public TrackerStateException(String msgId)
    {
        super(msgId);
    }

    /**
     * @param msgId
     * @param msgParams
     */
    public TrackerStateException(String msgId, Object[] msgParams)
    {
        super(msgId, msgParams);
    }

    /**
     * @param msgId
     * @param cause
     */
    public TrackerStateException(String msgId, Throwable cause)
    {
        super(msgId, cause);
    }

    /**
     * @param msgId
     * @param msgParams
     * @param cause
     */
    public TrackerStateException(String msgId, Object[] msgParams, Throwable cause)
    {
        super(msgId, msgParams, cause);
    }

}
