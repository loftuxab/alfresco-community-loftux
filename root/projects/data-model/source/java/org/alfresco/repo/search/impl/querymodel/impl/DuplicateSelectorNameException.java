package org.alfresco.repo.search.impl.querymodel.impl;

import org.alfresco.error.AlfrescoRuntimeException;

/**
 * @author andyh
 *
 */
public class DuplicateSelectorNameException extends AlfrescoRuntimeException
{

    /**
     * @param msgId String
     * @param msgParams Object[]
     * @param cause Throwable
     */
    public DuplicateSelectorNameException(String msgId, Object[] msgParams, Throwable cause)
    {
        super(msgId, msgParams, cause);
        // TODO Auto-generated constructor stub
    }

    /**
     * @param msgId String
     * @param msgParams Object[]
     */
    public DuplicateSelectorNameException(String msgId, Object[] msgParams)
    {
        super(msgId, msgParams);
        // TODO Auto-generated constructor stub
    }

    /**
     * @param msgId String
     * @param cause Throwable
     */
    public DuplicateSelectorNameException(String msgId, Throwable cause)
    {
        super(msgId, cause);
        // TODO Auto-generated constructor stub
    }

    /**
     * @param msgId String
     */
    public DuplicateSelectorNameException(String msgId)
    {
        super(msgId);
        // TODO Auto-generated constructor stub
    }

    /**
     * 
     */
    private static final long serialVersionUID = 3163974668059624874L;

}
