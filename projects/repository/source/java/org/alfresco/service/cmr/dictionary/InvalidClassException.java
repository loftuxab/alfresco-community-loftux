package org.alfresco.service.cmr.dictionary;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.service.namespace.QName;

/**
 * Thrown when an operation cannot be performed because the dictionary class 
 * reference does not exist.
 * 
 */
public class InvalidClassException extends AlfrescoRuntimeException
{
    private static final long serialVersionUID = 3256722870754293558L;

    private QName className;
    
    public InvalidClassException(QName className)
    {
        this(null, className);
    }

    public InvalidClassException(String msg, QName className)
    {
        super(msg);
        this.className = className;
    }

    /**
     * @return Returns the offending class name
     */
    public QName getClassName()
    {
        return className;
    }
}
