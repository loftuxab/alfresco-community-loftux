package org.alfresco.service.cmr.repository;

/**
 * Thrown when an operation cannot be performed because the<b>child association</b>
 * reference no longer exists.
 * 
 * @author Derek Hulley
 */
public class InvalidChildAssociationRefException extends RuntimeException
{
    private static final long serialVersionUID = -7493054268618534572L;

    private ChildAssociationRef childAssociationRef;
    
    public InvalidChildAssociationRefException(ChildAssociationRef childAssociationRef)
    {
        this(null, childAssociationRef);
    }

    public InvalidChildAssociationRefException(String msg, ChildAssociationRef childAssociationRef)
    {
        super(msg);
        this.childAssociationRef = childAssociationRef;
    }

    /**
     * @return Returns the offending child association reference
     */
    public ChildAssociationRef getChildAssociationRef()
    {
        return childAssociationRef;
    }
}
