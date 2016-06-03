package org.alfresco.repo.search.adaptor.lucene;

/**
 * @author Andy
 *
 */
public interface LuceneQueryParserExpressionAdaptor<Q, E extends Throwable>
{
    public void addRequired(Q q) throws E;
    public void addExcluded(Q q) throws E;
    public void addOptional(Q q) throws E;
    public void addRequired(Q q, float boost) throws E;
    public void addExcluded(Q q, float boost) throws E;
    public void addOptional(Q q, float boost) throws E;
    public Q getQuery() throws E;
    public Q getNegatedQuery() throws E;
}
