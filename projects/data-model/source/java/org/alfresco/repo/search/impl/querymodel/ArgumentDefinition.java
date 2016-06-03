package org.alfresco.repo.search.impl.querymodel;

import org.alfresco.service.namespace.QName;

/**
 * @author andyh
 *
 */
public interface ArgumentDefinition
{
    public String getName();
    
    public QName getType();
    
    public Multiplicity getMutiplicity();
    
    public boolean isMandatory();
}
