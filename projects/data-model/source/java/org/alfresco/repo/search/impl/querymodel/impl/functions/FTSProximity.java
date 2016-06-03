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

public class FTSProximity extends BaseFunction
{
    public final static String NAME = "FTSProximity";

    public final static String ARG_FIRST = "First";
    
    public final static String ARG_LAST = "Last";
    
    public final static String ARG_PROPERTY = "Property";
    
    public final static String ARG_SLOP = "Slop";

    public static LinkedHashMap<String, ArgumentDefinition> args;

    static
    {
        args = new LinkedHashMap<String, ArgumentDefinition>();
        args.put(ARG_FIRST, new BaseArgumentDefinition(Multiplicity.SINGLE_VALUED, ARG_FIRST, DataTypeDefinition.ANY, true));
        args.put(ARG_LAST, new BaseArgumentDefinition(Multiplicity.SINGLE_VALUED, ARG_LAST, DataTypeDefinition.ANY, true));
        args.put(ARG_PROPERTY, new BaseArgumentDefinition(Multiplicity.SINGLE_VALUED, ARG_PROPERTY, DataTypeDefinition.ANY, false));
        args.put(ARG_SLOP, new BaseArgumentDefinition(Multiplicity.SINGLE_VALUED, ARG_SLOP, DataTypeDefinition.INT, false));
    }

    public FTSProximity()
    {
        super(NAME, DataTypeDefinition.BOOLEAN, args);
    }
    public Serializable getValue(Map<String, Argument> args, FunctionEvaluationContext context)
    {
        throw new UnsupportedOperationException();
    }

}
