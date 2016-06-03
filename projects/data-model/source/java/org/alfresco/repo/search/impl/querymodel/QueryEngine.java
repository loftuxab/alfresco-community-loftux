package org.alfresco.repo.search.impl.querymodel;

/**
 * @author andyh
 *
 */
public interface QueryEngine
{
    public QueryEngineResults executeQuery(Query query, QueryOptions options, FunctionEvaluationContext functionContext);
    
    public QueryModelFactory getQueryModelFactory();
}
