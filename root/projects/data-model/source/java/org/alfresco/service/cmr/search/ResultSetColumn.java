package org.alfresco.service.cmr.search;

import org.alfresco.service.namespace.QName;

/**
 * The metadata for a column in a result set.
 * All columns should have a data type, they may have a property type.
 * 
 * @author andyh
 *
 */
public interface ResultSetColumn
{
    /**
     * The column name
     * @return - the column name
     */
    public String getName();
    
    /**
     * The type of the column
     * @return - the data type for the column
     */
    public QName getDataType();
    
    /**
     * The property definition if there is one for the column 
     * @return - the property definition or null if it does not make sense for the column 
     */
    public QName getPropertyType();
}
