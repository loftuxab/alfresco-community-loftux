package org.alfresco.repo.search.impl.querymodel.impl.lucene;

import java.util.List;
import java.util.Set;

import org.alfresco.repo.search.impl.querymodel.FunctionEvaluationContext;
import org.alfresco.service.cmr.search.SearchParameters.SortDefinition;

/**
 * @author andyh
 * 
 * @param <Q> the query type used by the query engine implementation
 * @param <S> the sort type used by the query engine implementation
 * @param <E> the exception it throws 
 *
 */
public interface LuceneQueryBuilder <Q, S, E extends Throwable>
{
    /**
     * Build the matching lucene query
     * @param selectors Set<String>
     * @param luceneContext LuceneQueryBuilderContext<Q, S, E>
     * @param functionContext FunctionEvaluationContext
     * @return - the query
     * @throws E
     */
    public Q buildQuery(Set<String> selectors,  LuceneQueryBuilderContext<Q, S, E> luceneContext, FunctionEvaluationContext functionContext) throws E;

    /**
     * Build the matching lucene sort
     * @param selectors Set<String>
     * @param luceneContext LuceneQueryBuilderContext<Q, S, E>
     * @param functionContext FunctionEvaluationContext
     * @return - the sort spec
     * @throws E 
     */
    public S buildSort(Set<String> selectors, LuceneQueryBuilderContext<Q, S, E> luceneContext, FunctionEvaluationContext functionContext) throws E;
    
    /**
     * Build a sort definition for a sorted result set wrapper
     * @param selectors Set<String>
     * @param luceneContext LuceneQueryBuilderContext<Q, S, E>
     * @param functionContext FunctionEvaluationContext
     * @return List<SortDefinition>
     */
    public List<SortDefinition> buildSortDefinitions(Set<String> selectors, LuceneQueryBuilderContext<Q, S, E> luceneContext, FunctionEvaluationContext functionContext);

}
