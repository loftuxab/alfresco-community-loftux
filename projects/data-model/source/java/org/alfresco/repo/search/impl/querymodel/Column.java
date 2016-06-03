package org.alfresco.repo.search.impl.querymodel;



/**
 * @author andyh
 *
 */
public interface Column extends FunctionInvokation
{   
    /**
     * Get the column alias.
     * 
     * @return String
     */
    public String getAlias();
    
    public boolean isOrderable();
    
    public boolean isQueryable();
}
