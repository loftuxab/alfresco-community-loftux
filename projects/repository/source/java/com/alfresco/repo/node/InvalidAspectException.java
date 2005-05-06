package org.alfresco.repo.node;

import org.alfresco.repo.dictionary.ClassRef;

/**
 * Thrown when a reference to an <b>aspect</b> is incorrect.
 * 
 * @author Derek Hulley
 */
public class InvalidAspectException extends RuntimeException
{
    private static final long serialVersionUID = 3257290240330051893L;

    private ClassRef aspectRef;
    
    public InvalidAspectException(ClassRef aspectRef)
    {
        this(null, aspectRef);
    }

    public InvalidAspectException(String msg, ClassRef aspectRef)
    {
        super(msg);
        this.aspectRef = aspectRef;
    }

    /**
     * @return Returns the offending aspect type reference
     * 
     * @see org.alfresco.repo.dictionary.ClassDefinition#getProperties()
     */
    public ClassRef getAspectRef()
    {
        return aspectRef;
    }
}
