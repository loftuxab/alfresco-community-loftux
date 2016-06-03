package org.alfresco.repo.search.impl.querymodel.impl.lucene.functions;

import java.util.Map;
import java.util.Set;

import org.alfresco.repo.search.adaptor.lucene.LuceneQueryParserAdaptor;
import org.alfresco.repo.search.impl.querymodel.Argument;
import org.alfresco.repo.search.impl.querymodel.FunctionEvaluationContext;
import org.alfresco.repo.search.impl.querymodel.PredicateMode;
import org.alfresco.repo.search.impl.querymodel.QueryModelException;
import org.alfresco.repo.search.impl.querymodel.impl.functions.GreaterThanOrEquals;
import org.alfresco.repo.search.impl.querymodel.impl.lucene.LuceneQueryBuilderComponent;
import org.alfresco.repo.search.impl.querymodel.impl.lucene.LuceneQueryBuilderContext;

/**
 * @author andyh
 *
 */
public class LuceneGreaterThanOrEquals<Q, S, E extends Throwable> extends GreaterThanOrEquals implements LuceneQueryBuilderComponent<Q, S, E>
{

    /**
     * 
     */
    public LuceneGreaterThanOrEquals()
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
        setPropertyAndStaticArguments(functionArgs);
       
        Q query = functionContext.buildLuceneGreaterThanOrEquals(lqpa, getPropertyName(), getStaticArgument().getValue(functionContext), PredicateMode.ANY, functionContext.getLuceneFunction(getFunctionArgument()));
        
        if(query == null)
        {
            throw new QueryModelException("No query time mapping for property  "+getPropertyArgument().getPropertyName()+", it should not be allowed in predicates");
        }
        
        return query;
    }


}
