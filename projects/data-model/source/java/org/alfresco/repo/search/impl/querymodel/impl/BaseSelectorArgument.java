package org.alfresco.repo.search.impl.querymodel.impl;

import java.io.Serializable;

import org.alfresco.repo.search.impl.querymodel.FunctionEvaluationContext;
import org.alfresco.repo.search.impl.querymodel.SelectorArgument;

/**
 * @author andyh
 *
 */
public class BaseSelectorArgument  extends BaseStaticArgument implements SelectorArgument
{

    private String selector;

    /**
     * @param name String
     * @param selector String
     */
    public BaseSelectorArgument(String name, String selector)
    {
        super(name, true, false);
        this.selector = selector;

    }
    
    /* (non-Javadoc)
     * @see org.alfresco.repo.search.impl.querymodel.SelectorArgument#getSelector()
     */
    public String getSelector()
    {
        return selector;
    }

   

    /* (non-Javadoc)
     * @see org.alfresco.repo.search.impl.querymodel.Argument#getValue()
     */
    public Serializable getValue(FunctionEvaluationContext context)
    {
        return getSelector();
    }

    
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("BaseSelectorArgument[");
        builder.append("name=").append(getName()).append(", ");
        builder.append("selector=").append(getSelector());
        builder.append("]");
        return builder.toString();
    }
}
