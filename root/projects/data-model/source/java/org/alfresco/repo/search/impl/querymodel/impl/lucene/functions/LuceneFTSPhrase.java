package org.alfresco.repo.search.impl.querymodel.impl.lucene.functions;

import java.util.Map;
import java.util.Set;

import org.alfresco.repo.search.adaptor.lucene.AnalysisMode;
import org.alfresco.repo.search.adaptor.lucene.LuceneFunction;
import org.alfresco.repo.search.adaptor.lucene.LuceneQueryParserAdaptor;
import org.alfresco.repo.search.impl.querymodel.Argument;
import org.alfresco.repo.search.impl.querymodel.FunctionEvaluationContext;
import org.alfresco.repo.search.impl.querymodel.PropertyArgument;
import org.alfresco.repo.search.impl.querymodel.impl.functions.FTSPhrase;
import org.alfresco.repo.search.impl.querymodel.impl.lucene.LuceneQueryBuilderComponent;
import org.alfresco.repo.search.impl.querymodel.impl.lucene.LuceneQueryBuilderContext;

/**
 * @author andyh
 *
 */
public class LuceneFTSPhrase<Q, S, E extends Throwable> extends FTSPhrase implements LuceneQueryBuilderComponent<Q, S, E>
{
    /**
     * 
     */
    public LuceneFTSPhrase()
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
        Argument argument = functionArgs.get(ARG_PHRASE);
        String term = (String) argument.getValue(functionContext);

        Integer slop = Integer.valueOf(lqpa.getPhraseSlop());
        argument = functionArgs.get(ARG_SLOP);
        if(argument != null)
        { 
           slop = (Integer) argument.getValue(functionContext);
        }
        
        argument = functionArgs.get(ARG_TOKENISATION_MODE);
        AnalysisMode mode = (AnalysisMode) argument.getValue(functionContext);
        
        PropertyArgument propArg = (PropertyArgument) functionArgs.get(ARG_PROPERTY);
        Q query;
        if (propArg != null)
        {
            String prop = propArg.getPropertyName();
            query = lqpa.getFieldQuery(functionContext.getLuceneFieldName(prop), term, mode, slop, LuceneFunction.FIELD);
        }
        else
        {
            query = lqpa.getFieldQuery(lqpa.getField(), term, mode, slop, LuceneFunction.FIELD);
        }
        return query;
    }
}
