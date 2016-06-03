package org.alfresco.repo.search.impl.querymodel.impl.lucene.functions;

import java.util.Map;
import java.util.Set;

import org.alfresco.repo.search.impl.querymodel.Argument;
import org.alfresco.repo.search.impl.querymodel.FunctionEvaluationContext;
import org.alfresco.repo.search.impl.querymodel.QueryModelException;
import org.alfresco.repo.search.impl.querymodel.impl.functions.Score;
import org.alfresco.repo.search.impl.querymodel.impl.lucene.LuceneQueryBuilderComponent;
import org.alfresco.repo.search.impl.querymodel.impl.lucene.LuceneQueryBuilderContext;

/**
 * @author andyh
 *
 */
public class LuceneScore<Q, S, E extends Throwable> extends Score implements LuceneQueryBuilderComponent<Q, S, E>
{

    /**
     * 
     */
    public LuceneScore()
    {
        super();
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.repo.search.impl.querymodel.impl.lucene.LuceneQueryBuilderComponent#addComponent(java.util.Set, java.util.Map, org.alfresco.repo.search.impl.querymodel.impl.lucene.LuceneQueryBuilderContext, org.alfresco.repo.search.impl.querymodel.FunctionEvaluationContext)
     */
    public Q addComponent(Set<String> selectors, Map<String, Argument> functionArgs, LuceneQueryBuilderContext<Q, S, E> luceneContext, FunctionEvaluationContext functionContext)
            throws E
    {
        throw new QueryModelException("Unsupported function in query "+getName());
    }
}
