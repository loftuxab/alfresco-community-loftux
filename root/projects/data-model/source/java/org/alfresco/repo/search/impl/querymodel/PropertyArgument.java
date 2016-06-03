package org.alfresco.repo.search.impl.querymodel;


/**
 * @author andyh
 */
public interface PropertyArgument extends DynamicArgument
{
    public String getSelector();
    
    public String getPropertyName();
}
