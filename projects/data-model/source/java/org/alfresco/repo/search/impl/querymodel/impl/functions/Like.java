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
public class Like extends BaseFunction
{
    public final static String NAME = "Like";

    public final static String ARG_PROPERTY = "Property";

    public final static String ARG_EXP = "Exp";

    public final static String ARG_NOT = "Not";

    public static LinkedHashMap<String, ArgumentDefinition> args;

    static
    {
        args = new LinkedHashMap<String, ArgumentDefinition>();
        args.put(ARG_PROPERTY, new BaseArgumentDefinition(Multiplicity.ANY, ARG_PROPERTY, DataTypeDefinition.ANY, true));
        args.put(ARG_EXP, new BaseArgumentDefinition(Multiplicity.ANY, ARG_EXP, DataTypeDefinition.TEXT, true));
        args.put(ARG_NOT, new BaseArgumentDefinition(Multiplicity.ANY, ARG_NOT, DataTypeDefinition.BOOLEAN, false));
    }

    public Like()
    {
        super(NAME, DataTypeDefinition.BOOLEAN, args);
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
