package org.alfresco.repo.search.impl.querymodel.impl;

import java.util.Map;

import org.alfresco.repo.search.impl.querymodel.Argument;
import org.alfresco.repo.search.impl.querymodel.Function;
import org.alfresco.repo.search.impl.querymodel.FunctionalConstraint;

/**
 * @author andyh
 *
 */
public class BaseFunctionalConstraint extends BaseConstraint implements FunctionalConstraint
{
    private Function function;
    
    private Map<String, Argument> arguments;

    public BaseFunctionalConstraint(Function function, Map<String, Argument> arguments)
    {
        this.function = function;
        this.arguments = arguments;
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.repo.search.impl.querymodel.Constraint#evaluate()
     */
    public boolean evaluate()
    {
        throw new UnsupportedOperationException();
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
        return arguments;
    }

    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("BaseFunctionalConstraint[");
        builder.append("Function="+getFunction()).append(", ");
        builder.append("Arguments="+getFunctionArguments());
        builder.append("]");
        return builder.toString();
    }
}
