package org.alfresco.repo.node;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.repo.ref.QName;

/**
 * Thrown when a reference to an <b>aspect</b> is incorrect.
 * 
 * @author Derek Hulley
 */
public class InvalidAspectException extends AlfrescoRuntimeException
{
    private static final long serialVersionUID = 3257290240330051893L;

    private QName aspectTypeQName;
    
    public InvalidAspectException(QName aspectTypeQName)
    {
        this(null, aspectTypeQName);
    }

    public InvalidAspectException(String msg, QName aspectTypeQName)
    {
        super(msg);
        this.aspectTypeQName = aspectTypeQName;
    }

    /**
     * @return Returns the offending aspect type reference
     * 
     * @see org.alfresco.repo.dictionary.ClassDefinition#getProperties()
     */
    public QName getAspectTypeQName()
    {
        return aspectTypeQName;
    }
}
