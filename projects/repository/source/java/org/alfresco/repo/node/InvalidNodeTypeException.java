package org.alfresco.repo.node;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.repo.ref.QName;

/**
 * Thrown when an operation cannot be performed because the <b>node</b> reference
 * no longer exists.
 * 
 * @author Derek Hulley
 */
public class InvalidNodeTypeException extends AlfrescoRuntimeException
{
    private static final long serialVersionUID = 3256722870754293558L;

    private QName typeRef;
    
    public InvalidNodeTypeException(QName typeRef)
    {
        this(null, typeRef);
    }

    public InvalidNodeTypeException(String msg, QName typeRef)
    {
        super(msg);
        this.typeRef = typeRef;
    }

    /**
     * @return Returns the offending node reference
     */
    public QName getTypeRef()
    {
        return typeRef;
    }
}
