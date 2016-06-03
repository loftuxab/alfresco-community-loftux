package org.alfresco.repo.search.impl.querymodel.impl.lucene.functions;

import java.util.Map;
import java.util.Set;

import org.alfresco.repo.search.adaptor.lucene.AnalysisMode;
import org.alfresco.repo.search.adaptor.lucene.LuceneQueryParserAdaptor;
import org.alfresco.repo.search.impl.querymodel.Argument;
import org.alfresco.repo.search.impl.querymodel.FunctionEvaluationContext;
import org.alfresco.repo.search.impl.querymodel.PropertyArgument;
import org.alfresco.repo.search.impl.querymodel.impl.functions.FTSPrefixTerm;
import org.alfresco.repo.search.impl.querymodel.impl.lucene.LuceneQueryBuilderComponent;
import org.alfresco.repo.search.impl.querymodel.impl.lucene.LuceneQueryBuilderContext;

/**
 * Perfix term
 * @author andyh
 *
 */
public class LuceneFTSPrefixTerm<Q, S, E extends Throwable> extends FTSPrefixTerm implements LuceneQueryBuilderComponent<Q, S, E>
{
    /**
     * 
     */
    public LuceneFTSPrefixTerm()
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
        // strip trailing wildcard *
        term = term.substring(0, term.length()-1);

        argument = functionArgs.get(ARG_TOKENISATION_MODE);
        AnalysisMode mode = (AnalysisMode) argument.getValue(functionContext);
        
        PropertyArgument propArg = (PropertyArgument) functionArgs.get(ARG_PROPERTY);
        Q query;
        if (propArg != null)
        {
            String prop = propArg.getPropertyName();
            query = lqpa.getPrefixQuery(functionContext.getLuceneFieldName(prop), term, mode);
        }
        else
        {
            query = lqpa.getPrefixQuery(lqpa.getField(), term, mode);
            
        }
        return query;
    }

}
