package org.alfresco.repo.search.impl.querymodel.impl.lucene.functions;

import java.util.Map;
import java.util.Set;

import org.alfresco.repo.search.adaptor.lucene.LuceneQueryParserAdaptor;
import org.alfresco.repo.search.impl.querymodel.Argument;
import org.alfresco.repo.search.impl.querymodel.FunctionEvaluationContext;
import org.alfresco.repo.search.impl.querymodel.PropertyArgument;
import org.alfresco.repo.search.impl.querymodel.impl.functions.FTSProximity;
import org.alfresco.repo.search.impl.querymodel.impl.lucene.LuceneQueryBuilderComponent;
import org.alfresco.repo.search.impl.querymodel.impl.lucene.LuceneQueryBuilderContext;

/**
 * Proximity
 * @author andyh
 *
 */
public class LuceneFTSProximity<Q, S, E extends Throwable> extends FTSProximity implements LuceneQueryBuilderComponent<Q, S, E>
{
    
    /**
     * 
     */
    public LuceneFTSProximity()
    {
        super();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.alfresco.repo.search.impl.querymodel.impl.lucene.LuceneQueryBuilderComponent#addComponent(org.apache.lucene.search.BooleanQuery,
     *      org.apache.lucene.search.BooleanQuery, org.alfresco.service.cmr.dictionary.DictionaryService,
     *      java.lang.String)
     */
    public Q addComponent(Set<String> selectors, Map<String, Argument> functionArgs, LuceneQueryBuilderContext<Q, S, E> luceneContext, FunctionEvaluationContext functionContext)
            throws E
    {
        LuceneQueryParserAdaptor<Q, S, E> lqpa = luceneContext.getLuceneQueryParserAdaptor();
        Argument argument = functionArgs.get(ARG_FIRST);
        String first = (String) argument.getValue(functionContext);
        argument = functionArgs.get(ARG_LAST);
        String last = (String) argument.getValue(functionContext);

        int slop = 100;
        argument = functionArgs.get(ARG_SLOP);
        if(argument != null)
        {
            String val = (String) argument.getValue(functionContext);
            try
            {
                slop = Integer.parseInt(val);
            }
            catch(NumberFormatException nfe)
            {
                // ignore rubbish
            }
        }
        
        
        PropertyArgument propArg = (PropertyArgument) functionArgs.get(ARG_PROPERTY);
        Q query;
        if (propArg != null)
        {
            String prop = propArg.getPropertyName();
            query = lqpa.getSpanQuery(functionContext.getLuceneFieldName(prop), first, last, slop, true);
        }
        else
        {
            query = lqpa.getSpanQuery(lqpa.getField(), first, last, slop, true);
            
        }
        return query;
    }
}
