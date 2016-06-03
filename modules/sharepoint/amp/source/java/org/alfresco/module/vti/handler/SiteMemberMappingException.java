package org.alfresco.module.vti.handler;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.module.vti.metadata.dic.VtiError;

/**
 * An exception thrown by a {@link AuthenticationHandler}.
 * 
 * @author dward
 */
public class SiteMemberMappingException extends AlfrescoRuntimeException
{
    
    private static final long serialVersionUID = -7235067946629381543L;

    /**
     * Constructs a <code>SiteMemberMappingException</code>.
     * 
     * @param error The underlying error, usually {@link VtiError#DOES_NOT_EXIST}
     */
    public SiteMemberMappingException(VtiError error)
    {
       super(error.getMessage());
    }
    
    /**
     * Constructs a <code>SiteMemberMappingException</code>.
     * 
     * @param msgId
     *            the message id
     */
    public SiteMemberMappingException(String msgId)
    {
        super(msgId);
    }

    /**
     * Constructs a <code>SiteMemberMappingException</code>.
     * 
     * @param msgId
     *            the message id
     * @param msgParams
     *            the message parameters
     */
    public SiteMemberMappingException(String msgId, Object[] msgParams)
    {
        super(msgId, msgParams);
    }

    /**
     * Constructs a <code>SiteMemberMappingException</code>.
     * 
     * @param msgId
     *            the message id
     * @param cause
     *            the cause
     */
    public SiteMemberMappingException(String msgId, Throwable cause)
    {
        super(msgId, cause);
    }

    /**
     * Constructs a <code>SiteMemberMappingException</code>.
     * 
     * @param msgId
     *            the message id
     * @param msgParams
     *            the message parameters
     * @param cause
     *            the cause
     */
    public SiteMemberMappingException(String msgId, Object[] msgParams, Throwable cause)
    {
        super(msgId, msgParams, cause);
    }

}
