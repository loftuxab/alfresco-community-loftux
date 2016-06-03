package org.alfresco.repo.search.impl.querymodel.impl;

import java.util.Map;

import org.alfresco.repo.search.impl.querymodel.Argument;
import org.alfresco.repo.search.impl.querymodel.Column;
import org.alfresco.repo.search.impl.querymodel.Function;

/**
 * @author andyh
 *
 */
public class BaseColumn implements Column
{
    private String alias;
    
    private Function function;
    
    private Map<String, Argument> functionArguments;
    
    public BaseColumn(Function function, Map<String, Argument> functionArguments, String alias)
    {
        this.function = function;
        this.functionArguments = functionArguments;
        this.alias = alias;
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.repo.search.impl.querymodel.Column#getAlias()
     */
    public String getAlias()
    {
       return alias;
    }

    /* (non-Javadoc)
     * @see org.alfresco.repo.search.impl.querymodel.FunctionInvokation#getFunction()
     */
    public Function getFunction()
    {
        return function;
    }

    /* (non-Javadoc)
     * @see org.alfresco.repo.search.impl.querymodel.FunctionInvokation#getFunctionArguments()
     */
    public Map<String, Argument> getFunctionArguments()
    {
        return functionArguments;
    }

    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("BaseColumn[");
        builder.append("Alias=").append(getAlias()).append(", ");
        builder.append("Function=").append(getFunction()).append(", ");
        builder.append("FunctionArguments=").append(getFunctionArguments());
        builder.append("]");
        return builder.toString();
    }

    public boolean isOrderable()
    {
        for(Argument arg : functionArguments.values())
        {
            if(!arg.isOrderable())
            {
                return false;
            }
        }
       return true;
    }

    public boolean isQueryable()
    {
        for(Argument arg : functionArguments.values())
        {
            if(!arg.isQueryable())
            {
                return false;
            }
        }
        return true;
    }
    
}
