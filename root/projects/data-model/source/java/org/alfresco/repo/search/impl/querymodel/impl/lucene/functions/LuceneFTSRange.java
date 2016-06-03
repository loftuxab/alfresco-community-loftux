package org.alfresco.repo.search.impl.querymodel.impl.lucene.functions;

import java.util.Map;
import java.util.Set;

import org.alfresco.repo.search.adaptor.lucene.AnalysisMode;
import org.alfresco.repo.search.adaptor.lucene.LuceneFunction;
import org.alfresco.repo.search.adaptor.lucene.LuceneQueryParserAdaptor;
import org.alfresco.repo.search.impl.querymodel.Argument;
import org.alfresco.repo.search.impl.querymodel.FunctionEvaluationContext;
import org.alfresco.repo.search.impl.querymodel.PropertyArgument;
import org.alfresco.repo.search.impl.querymodel.impl.functions.FTSRange;
import org.alfresco.repo.search.impl.querymodel.impl.lucene.LuceneQueryBuilderComponent;
import org.alfresco.repo.search.impl.querymodel.impl.lucene.LuceneQueryBuilderContext;

/**
 * Range
 * @author andyh
 *
 */
public class LuceneFTSRange<Q, S, E extends Throwable> extends FTSRange implements LuceneQueryBuilderComponent<Q, S, E>
{
    /**
     * 
     */
    public LuceneFTSRange()
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
        Argument argument = functionArgs.get(ARG_FROM_INC);
        Boolean fromInc = (Boolean) argument.getValue(functionContext);
        argument = functionArgs.get(ARG_FROM);
        String from = (String) argument.getValue(functionContext);
        argument = functionArgs.get(ARG_TO);
        String to = (String) argument.getValue(functionContext);
        argument = functionArgs.get(ARG_TO_INC);
        Boolean toInc = (Boolean) argument.getValue(functionContext);
        
        PropertyArgument propArg = (PropertyArgument) functionArgs.get(ARG_PROPERTY);
        Q query;
        if (propArg != null)
        {
            String prop = propArg.getPropertyName();
            query = lqpa.getRangeQuery(functionContext.getLuceneFieldName(prop), from, to, fromInc, toInc, AnalysisMode.DEFAULT, LuceneFunction.FIELD);
        }
        else
        {
            query = lqpa.getRangeQuery(lqpa.getField(), from, to, fromInc, toInc, AnalysisMode.DEFAULT, LuceneFunction.FIELD);
        }
        return query;
    }
}
