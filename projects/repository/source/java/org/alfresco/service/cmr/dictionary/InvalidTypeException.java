package org.alfresco.service.cmr.dictionary;

import org.alfresco.service.namespace.QName;

/**
 * Thrown when an operation cannot be performed because the <b>node</b> reference
 * no longer exists.
 * 
 * @author Derek Hulley
 */
public class InvalidTypeException extends InvalidClassException
{
    private static final long serialVersionUID = 3256722870754293558L;

    public InvalidTypeException(QName typeName)
    {
        super(null, typeName);
    }

    public InvalidTypeException(String msg, QName typeName)
    {
        super(msg, typeName);
    }

    /**
     * @return Returns the offending type name
     */
    public QName getTypeName()
    {
        return getClassName();
    }
}
