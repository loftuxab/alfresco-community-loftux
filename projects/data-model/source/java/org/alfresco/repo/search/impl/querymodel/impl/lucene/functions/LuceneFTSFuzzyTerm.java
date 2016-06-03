package org.alfresco.repo.search.impl.querymodel.impl.lucene.functions;

import java.util.Map;
import java.util.Set;

import org.alfresco.repo.search.adaptor.lucene.LuceneQueryParserAdaptor;
import org.alfresco.repo.search.impl.querymodel.Argument;
import org.alfresco.repo.search.impl.querymodel.FunctionEvaluationContext;
import org.alfresco.repo.search.impl.querymodel.PropertyArgument;
import org.alfresco.repo.search.impl.querymodel.impl.functions.FTSFuzzyTerm;
import org.alfresco.repo.search.impl.querymodel.impl.lucene.LuceneQueryBuilderComponent;
import org.alfresco.repo.search.impl.querymodel.impl.lucene.LuceneQueryBuilderContext;

/**
 * Fuzzy matching
 * @author andyh
 *
 */
public class LuceneFTSFuzzyTerm<Q, S, E extends Throwable> extends FTSFuzzyTerm implements LuceneQueryBuilderComponent<Q, S, E>
{

    /**
     * 
     */
    public LuceneFTSFuzzyTerm()
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
        Argument argument = functionArgs.get(ARG_TERM);
        String term = (String) argument.getValue(functionContext);
        argument = functionArgs.get(ARG_MIN_SIMILARITY);
        Float minSimilarity = (Float) argument.getValue(functionContext);

        PropertyArgument propArg = (PropertyArgument) functionArgs.get(ARG_PROPERTY);
        Q query;
        if (propArg != null)
        {
            String prop = propArg.getPropertyName();
            query = lqpa.getFuzzyQuery(functionContext.getLuceneFieldName(prop), term, minSimilarity);
        }
        else
        {
            query = lqpa.getFuzzyQuery(lqpa.getField(), term, minSimilarity);
            
        }
        return query;
    }


}
