package org.alfresco.repo.node;

import org.alfresco.repo.dictionary.ClassRef;
import org.alfresco.repo.ref.NodeRef;

/**
 * Thrown when an operation cannot be performed because the <b>node</b> reference
 * no longer exists.
 * 
 * @author Derek Hulley
 */
public class InvalidNodeTypeException extends RuntimeException
{
    private static final long serialVersionUID = 3256722870754293558L;

    private ClassRef typeRef;
    
    public InvalidNodeTypeException(ClassRef typeRef)
    {
        this(null, typeRef);
    }

    public InvalidNodeTypeException(String msg, ClassRef typeRef)
    {
        super(msg);
        this.typeRef = typeRef;
    }

    /**
     * @return Returns the offending node reference
     */
    public NodeRef getTypeRef()
    {
        return typeRef;
    }
}
