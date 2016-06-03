package org.alfresco.service.cmr.dictionary;

import org.alfresco.error.AlfrescoRuntimeException;

/**
 * Thrown when property value fails to meet a property constraint.
 * 
 * @author Derek Hulley
 */
public class ConstraintException extends AlfrescoRuntimeException
{
    private static final long serialVersionUID = -3925105163386197586L;

    public ConstraintException(String msgId, Object ... args)
    {
        super(msgId, args);
    }
}
