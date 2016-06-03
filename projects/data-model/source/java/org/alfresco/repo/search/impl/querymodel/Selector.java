package org.alfresco.repo.search.impl.querymodel;

import org.alfresco.service.namespace.QName;

/**
 * @author andyh
 *
 */
public interface Selector extends Source
{
    /**
     * The qname of the type or aspect to select 
     * @return QName
     */
    public QName getType();
    
    /**
     * The alias or name for the selector
     * This must be unique across all selectors in the query
     * 
     * @return String
     */
    public String getAlias();
}
