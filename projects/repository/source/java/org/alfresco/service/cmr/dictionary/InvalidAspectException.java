package org.alfresco.service.cmr.dictionary;

import org.alfresco.service.namespace.QName;

/**
 * Thrown when a reference to an <b>aspect</b> is incorrect.
 * 
 * @author Derek Hulley
 */
public class InvalidAspectException extends InvalidClassException
{
    private static final long serialVersionUID = 3257290240330051893L;

    public InvalidAspectException(QName aspectName)
    {
        super(null, aspectName);
    }

    public InvalidAspectException(String msg, QName aspectName)
    {
        super(msg, aspectName);
    }

    /**
     * @return Returns the offending aspect name
     */
    public QName getAspectName()
    {
        return getClassName();
    }
}
