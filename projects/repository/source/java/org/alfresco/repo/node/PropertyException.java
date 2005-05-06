package org.alfresco.repo.node;

import org.alfresco.repo.dictionary.PropertyRef;

/**
 * Thrown a <b>property</b> is not valid or not set.
 * 
 * @see org.alfresco.repo.dictionary.DictionaryService#getProperty(PropertyRef)
 * 
 * @author Derek Hulley
 */
public class PropertyException extends RuntimeException
{
    private static final long serialVersionUID = 3976734787505631540L;

    private PropertyRef propertyRef;
    
    public PropertyException(PropertyRef typeRef)
    {
        this(null, typeRef);
    }

    public PropertyException(String msg, PropertyRef propertyRef)
    {
        super(msg);
        this.propertyRef = propertyRef;
    }

    /**
     * @return Returns the offending property reference
     */
    public PropertyRef getPropertyRef()
    {
        return propertyRef;
    }
}
