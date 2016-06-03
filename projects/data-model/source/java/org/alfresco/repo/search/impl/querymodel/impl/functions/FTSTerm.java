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
 *
 */
public class FTSTerm extends BaseFunction
{   
    public final static String NAME = "FTSTerm";

    public final static String ARG_TERM = "Term";
    
    public final static String ARG_PROPERTY = "Property";
    
    public final static String ARG_TOKENISATION_MODE = "TokenisationMode";

    public static LinkedHashMap<String, ArgumentDefinition> args;

    static
    {
        args = new LinkedHashMap<String, ArgumentDefinition>();
        args.put(ARG_TERM, new BaseArgumentDefinition(Multiplicity.SINGLE_VALUED, ARG_TERM, DataTypeDefinition.ANY, true));
        args.put(ARG_PROPERTY, new BaseArgumentDefinition(Multiplicity.SINGLE_VALUED, ARG_PROPERTY, DataTypeDefinition.ANY, false));
        args.put(ARG_TOKENISATION_MODE, new BaseArgumentDefinition(Multiplicity.SINGLE_VALUED, ARG_TOKENISATION_MODE, DataTypeDefinition.ANY, false));
    }

    public FTSTerm()
    {
        super(NAME, DataTypeDefinition.BOOLEAN, args);
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.repo.search.impl.querymodel.Function#getValue(java.util.Set)
     */
    public Serializable getValue(Map<String, Argument> args, FunctionEvaluationContext context)
    {
        throw new UnsupportedOperationException();
    }

}
