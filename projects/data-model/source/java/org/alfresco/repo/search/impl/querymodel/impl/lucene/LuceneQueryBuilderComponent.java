package org.alfresco.repo.search.impl.querymodel.impl.lucene;

import java.util.Map;
import java.util.Set;

import org.alfresco.repo.search.impl.querymodel.Argument;
import org.alfresco.repo.search.impl.querymodel.FunctionEvaluationContext;

/**
 * @author andyh
 */
public interface LuceneQueryBuilderComponent<Q, S, E extends Throwable>
{
    /**
     * Generate the lucene query from the query component
     * @param selectors Set<String>
     * @param functionArgs Map<String, Argument>
     * @param luceneContext LuceneQueryBuilderContext<Q, S, E>
     * @param functionContext FunctionEvaluationContext
     * @return - the lucene query fragment for this component
     * @throws E
     */
    public Q addComponent(Set<String> selectors, Map<String, Argument> functionArgs, LuceneQueryBuilderContext<Q, S, E> luceneContext, FunctionEvaluationContext functionContext) throws E;
}
