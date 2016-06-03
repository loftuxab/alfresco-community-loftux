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
import org.alfresco.service.cmr.repository.datatype.DefaultTypeConverter;

/**
 * @author andyh
 */
public class Upper extends BaseFunction
{
    public final static String NAME = "Upper";

    public final static String ARG_ARG = "Arg";

    public static LinkedHashMap<String, ArgumentDefinition> args;

    static
    {
        args = new LinkedHashMap<String, ArgumentDefinition>();
        args.put(ARG_ARG, new BaseArgumentDefinition(Multiplicity.SINGLE_VALUED, ARG_ARG, DataTypeDefinition.ANY, true));
    }

    public Upper()
    {
        super(NAME, DataTypeDefinition.TEXT, args);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.alfresco.repo.search.impl.querymodel.Function#getValue(java.util.Set)
     */
    public Serializable getValue(Map<String, Argument> args, FunctionEvaluationContext context)
    {
        Argument arg = args.get(ARG_ARG);
        Serializable value = arg.getValue(context);
        String stringValue = DefaultTypeConverter.INSTANCE.convert(String.class, value);
        return stringValue.toUpperCase();
    }

}
