package org.alfresco.service.cmr.repository;

import org.alfresco.service.namespace.QName;


/**
 * Thrown a <b>property</b> is not valid or not set.
 * 
 * @author Derek Hulley
 */
public class PropertyException extends RuntimeException
{
    private static final long serialVersionUID = 3976734787505631540L;

    private QName propertyRef;
    
    public PropertyException(QName propertyRef)
    {
        this(null, propertyRef);
    }

    public PropertyException(String msg, QName propertyRef)
    {
        super(msg);
        this.propertyRef = propertyRef;
    }

    /**
     * @return Returns the offending property reference
     */
    public QName getPropertyRef()
    {
        return propertyRef;
    }
}
