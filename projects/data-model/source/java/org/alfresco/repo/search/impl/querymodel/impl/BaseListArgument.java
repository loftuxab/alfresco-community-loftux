package org.alfresco.repo.search.impl.querymodel.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.alfresco.repo.search.impl.querymodel.Argument;
import org.alfresco.repo.search.impl.querymodel.FunctionEvaluationContext;
import org.alfresco.repo.search.impl.querymodel.ListArgument;

/**
 * @author andyh
 *
 */
public class BaseListArgument extends BaseStaticArgument implements ListArgument
{
    private List<Argument> arguments;

    /**
     * @param name String
     * @param arguments List<Argument>
     */
    public BaseListArgument(String name, List<Argument> arguments)
    {
        super(name, false, false);
        this.arguments = arguments;
    }

    /* (non-Javadoc)
     * @see org.alfresco.repo.search.impl.querymodel.ListArgument#getArguments()
     */
    public List<Argument> getArguments()
    {
        return arguments;
    }

    /* (non-Javadoc)
     * @see org.alfresco.repo.search.impl.querymodel.Argument#getValue()
     */
    public Serializable getValue(FunctionEvaluationContext context)
    {
        ArrayList<Serializable> answer = new ArrayList<Serializable>(arguments.size());
        for(Argument argument : arguments)
        {
            Serializable value = argument.getValue(context);
            answer.add(value);
        }
        return answer;
        
    }

    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("BaseListArgument[");
        builder.append("name=").append(getName()).append(", ");
        builder.append("values=").append(getArguments());
        builder.append("]");
        return builder.toString();
    }
    
    public boolean isQueryable()
    {
        for(Argument arg : arguments)
        {
            if(!arg.isQueryable())
            {
                return false;
            }
        }
        return true;
    }
}
