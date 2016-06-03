package org.alfresco.module.vti.handler;

import org.alfresco.error.AlfrescoRuntimeException;

/**
 * Exception thrown when a Vti call tries to find a given Object
 *  or Node, but it could not be found.
 * This is typically thrown from a Handler, and caught in an
 *  Endpoint. The Endpoint will then return the appropriate error code.
 * 
 * @author Nick Burch
 */
public class ObjectNotFoundException extends AlfrescoRuntimeException
{
    private static final long serialVersionUID = -2639705474062599344L;

    /**
     * Create exception with no message
     */
    public ObjectNotFoundException()
    {
        super("The specified object could not be found");
    }

    /**
     * Create exception with specified message
     * 
     * @param message the specified message
     */
    public ObjectNotFoundException(String message)
    {
        super(message);
    }
}
