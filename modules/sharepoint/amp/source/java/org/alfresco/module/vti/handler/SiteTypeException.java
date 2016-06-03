package org.alfresco.module.vti.handler;

import org.alfresco.error.AlfrescoRuntimeException;

/**
 * An exception thrown when a site is not of the appropriate
 *  type for the operation being performed.
 * 
 * @author Nick Burch
 */
public class SiteTypeException extends AlfrescoRuntimeException
{
    private static final long serialVersionUID = -6073753941824982920L;

    /**
     * Constructs a <code>SiteTypeException</code>.
     * 
     * @param msgId
     *            the message id
     */
    public SiteTypeException(String msgId)
    {
        super(msgId);
    }

    /**
     * Constructs a <code>SiteTypeException</code>.
     * 
     * @param msgId
     *            the message id
     * @param msgParams
     *            the message parameters
     */
    public SiteTypeException(String msgId, Object[] msgParams)
    {
        super(msgId, msgParams);
    }

    /**
     * Constructs a <code>SiteTypeException</code>.
     * 
     * @param msgId
     *            the message id
     * @param cause
     *            the cause
     */
    public SiteTypeException(String msgId, Throwable cause)
    {
        super(msgId, cause);
    }

    /**
     * Constructs a <code>SiteTypeException</code>.
     * 
     * @param msgId
     *            the message id
     * @param msgParams
     *            the message parameters
     * @param cause
     *            the cause
     */
    public SiteTypeException(String msgId, Object[] msgParams, Throwable cause)
    {
        super(msgId, msgParams, cause);
    }
}