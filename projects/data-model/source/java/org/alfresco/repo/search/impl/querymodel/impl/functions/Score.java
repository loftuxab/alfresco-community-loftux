package org.alfresco.repo.search.impl.querymodel.impl.functions;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

import org.alfresco.repo.search.impl.querymodel.Argument;
import org.alfresco.repo.search.impl.querymodel.ArgumentDefinition;
import org.alfresco.repo.search.impl.querymodel.FunctionEvaluationContext;
import org.alfresco.repo.search.impl.querymodel.Multiplicity;
import org.alfresco.repo.search.impl.querymodel.impl.BaseArgumentDefinition;
import org.alfresco.repo.search.impl.querymodel.impl.BaseFunction;
import org.alfresco.service.cmr.dictionary.DataTypeDefinition;

/**
 * @author andyh
 */
public class Score extends BaseFunction
{
    public final static String NAME = "Score";

    public final static String ARG_QUALIFIER = "Qualifier";

    public static LinkedHashMap<String, ArgumentDefinition> args;

    static
    {
        args = new LinkedHashMap<String, ArgumentDefinition>();
        args.put(ARG_QUALIFIER, new BaseArgumentDefinition(Multiplicity.SINGLE_VALUED, ARG_QUALIFIER, DataTypeDefinition.ANY, true));
    }

    public Score()
    {
        super(NAME, DataTypeDefinition.FLOAT, args);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.alfresco.repo.search.impl.querymodel.Function#getValue(java.util.Set)
     */
    public Serializable getValue(Map<String, Argument> args, FunctionEvaluationContext context)
    {
        Argument qualifier = args.get(ARG_QUALIFIER);
        if(qualifier != null)
        {
            return context.getScores().get(qualifier.getValue(context));
        }
        else
        {
            return context.getScore();
        }
    }

}
