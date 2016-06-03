package org.alfresco.repo.search.impl.querymodel.impl;

import java.io.Serializable;

import org.alfresco.repo.search.impl.querymodel.FunctionEvaluationContext;
import org.alfresco.repo.search.impl.querymodel.LiteralArgument;
import org.alfresco.service.namespace.QName;

/**
 * @author andyh
 *
 */
public class BaseLiteralArgument extends BaseStaticArgument implements LiteralArgument
{
    private QName type;
    
    private Serializable value;
    
    public BaseLiteralArgument(String name, QName type, Serializable value)
    {
        super(name, true, false);
        this.type = type;
        this.value = value;
    }
    
    
    /* (non-Javadoc)
     * @see org.alfresco.repo.search.impl.querymodel.LiteralArgument#getValue()
     */
    public Serializable getValue(FunctionEvaluationContext context)
    {
        return value;
    }

    /* (non-Javadoc)
     * @see org.alfresco.repo.search.impl.querymodel.LiteralArgument#getType()
     */
    public QName getType()
    {
        return type;
    }
    
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("BaseLiteralArgument[");
        builder.append("name=").append(getName()).append(", ");
        builder.append("type=").append(getType()).append(", ");
        builder.append("value=").append(getValue(null)).append(", ");
        builder.append("]");
        return builder.toString();
    }
}
