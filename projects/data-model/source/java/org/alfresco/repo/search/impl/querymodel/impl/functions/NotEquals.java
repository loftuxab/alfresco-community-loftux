package org.alfresco.repo.search.impl.querymodel.impl.functions;

import java.io.Serializable;
import java.util.Map;

import org.alfresco.repo.search.impl.querymodel.Argument;
import org.alfresco.repo.search.impl.querymodel.FunctionEvaluationContext;
import org.alfresco.repo.search.impl.querymodel.impl.BaseComparison;
import org.alfresco.service.cmr.dictionary.DataTypeDefinition;

/**
 * @author andyh
 */
public class NotEquals extends BaseComparison
{
    public final static String NAME = "NotEquals";

    public NotEquals()
    {
        super(NAME, DataTypeDefinition.BOOLEAN, ARGS);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.alfresco.repo.search.impl.querymodel.Function#getValue(java.util.Set)
     */
    public Serializable getValue(Map<String, Argument> args, FunctionEvaluationContext context)
    {
        throw new UnsupportedOperationException();
    }

}
