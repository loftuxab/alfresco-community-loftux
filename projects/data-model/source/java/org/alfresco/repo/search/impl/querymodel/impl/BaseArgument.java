package org.alfresco.repo.search.impl.querymodel.impl;

import org.alfresco.repo.search.impl.querymodel.Argument;

/**
 * @author andyh
 *
 */
public abstract class BaseArgument implements Argument
{
    private String name;
    
    private boolean queryable;
    
    private boolean orderable;
    
    
    public BaseArgument(String name, boolean queryable, boolean orderable)
    {
        this.name = name;
        this.queryable = queryable;
        this.orderable = orderable;
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.repo.search.impl.querymodel.Argument#getName()
     */
    public String getName()
    {
        return name;
    }

    public boolean isOrderable()
    {
        return orderable;
    }

    public boolean isQueryable()
    {
       return queryable;
    }
}
