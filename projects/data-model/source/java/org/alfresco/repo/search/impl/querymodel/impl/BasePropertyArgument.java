package org.alfresco.repo.search.impl.querymodel.impl;

import java.io.Serializable;

import org.alfresco.repo.search.impl.querymodel.FunctionEvaluationContext;
import org.alfresco.repo.search.impl.querymodel.PropertyArgument;
import org.alfresco.service.cmr.repository.NodeRef;

/**
 * @author andyh
 */
public class BasePropertyArgument extends BaseDynamicArgument implements PropertyArgument
{
    private String propertyName;

    private String selector;

    /**
     * @param name String
     * @param queryable boolean
     * @param orderable boolean
     * @param selector String
     * @param propertyName String
     */
    public BasePropertyArgument(String name, boolean queryable, boolean orderable, String selector, String propertyName)
    {
        super(name, queryable, orderable);
        this.selector = selector;
        this.propertyName = propertyName;

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.alfresco.repo.search.impl.querymodel.PropertyArgument#getSelector()
     */
    public String getSelector()
    {
        return selector;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.alfresco.repo.search.impl.querymodel.PropertyArgument#getPropertyName()
     */
    public String getPropertyName()
    {
        return propertyName;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.alfresco.repo.search.impl.querymodel.Argument#getValue()
     */
    public Serializable getValue(FunctionEvaluationContext context)
    {
        NodeRef nodeRef = context.getNodeRefs().get(getSelector());
        return context.getProperty(nodeRef, getPropertyName());
    }

    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("BasePropertyArgument[");
        builder.append("name=").append(getName()).append(", ");
        builder.append("selector=").append(getSelector()).append(", ");
        builder.append("propertName=").append(getPropertyName()).append(", ");
        builder.append("]");
        return builder.toString();
    }
    
}
