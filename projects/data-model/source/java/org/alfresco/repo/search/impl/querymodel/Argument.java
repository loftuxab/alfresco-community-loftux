package org.alfresco.repo.search.impl.querymodel;

import java.io.Serializable;

/**
 * An argument to a function
 * 
 * @author andyh
 *
 */
public interface Argument
{
    public String getName();
    
    public Serializable getValue(FunctionEvaluationContext context);

    public boolean isOrderable();

    public boolean isQueryable();
}
