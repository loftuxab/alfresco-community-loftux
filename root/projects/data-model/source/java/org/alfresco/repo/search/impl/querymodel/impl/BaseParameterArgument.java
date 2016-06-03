package org.alfresco.repo.search.impl.querymodel.impl;

import java.io.Serializable;

import org.alfresco.repo.search.impl.querymodel.FunctionEvaluationContext;
import org.alfresco.repo.search.impl.querymodel.ParameterArgument;

/**
 * @author andyh
 *
 */
public class BaseParameterArgument extends BaseStaticArgument implements ParameterArgument
{
    private String parameterName;

    /**
     * @param name String
     * @param parameterName String
     */
    public BaseParameterArgument(String name, String parameterName)
    {
        super(name, true, false);
        this.parameterName = parameterName;
    }

    /* (non-Javadoc)
     * @see org.alfresco.repo.search.impl.querymodel.ParameterArgument#getParameterName()
     */
    public String getParameterName()
    {
        return parameterName;
    }

    /* (non-Javadoc)
     * @see org.alfresco.repo.search.impl.querymodel.Argument#getValue()
     */
    public Serializable getValue(FunctionEvaluationContext context)
    {
        throw new UnsupportedOperationException();
    }

    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("BaseParameterArgument[");
        builder.append("name=").append(getName()).append(", ");
        builder.append("parameterName=").append(getParameterName());
        builder.append("]");
        return builder.toString();
    }

}
