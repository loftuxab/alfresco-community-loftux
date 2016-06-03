package org.alfresco.repo.search.impl.querymodel.impl;

import java.util.LinkedHashMap;

import org.alfresco.repo.search.impl.querymodel.ArgumentDefinition;
import org.alfresco.repo.search.impl.querymodel.Function;
import org.alfresco.service.namespace.QName;

/**
 * @author andyh
 */
public abstract class BaseFunction implements Function
{

    private String name;

    private QName returnType;

    private LinkedHashMap<String, ArgumentDefinition> argumentDefinitions;

    public BaseFunction(String name, QName returnType, LinkedHashMap<String, ArgumentDefinition> argumentDefinitions)
    {
        this.name = name;
        this.returnType = returnType;
        this.argumentDefinitions = argumentDefinitions;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.alfresco.repo.search.impl.querymodel.Function#getArgumentDefinitions()
     */
    public LinkedHashMap<String, ArgumentDefinition> getArgumentDefinitions()
    {
        return argumentDefinitions;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.alfresco.repo.search.impl.querymodel.Function#getName()
     */
    public String getName()
    {
        return name;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.alfresco.repo.search.impl.querymodel.Function#getReturnType()
     */
    public QName getReturnType()
    {
        return returnType;
    }

    public ArgumentDefinition getArgumentDefinition(String name)
    {
        ArgumentDefinition definition = argumentDefinitions.get(name);
        if (definition != null)
        {
            return definition;
        }
        else
        {
            throw new IllegalArgumentException(name);
        }
    }

    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("BaseFunction[");
        builder.append("Name=" + getName()).append(", ");
        builder.append("Return type=" + getReturnType()).append(", ");
        builder.append("ArgumentDefinitions=" + getArgumentDefinitions());
        builder.append("]");
        return builder.toString();
    }
}
