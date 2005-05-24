package org.alfresco.repo.search;

import org.alfresco.repo.ref.Path;
import org.alfresco.repo.ref.QName;
import org.alfresco.repo.ref.StoreRef;

/**
 * Component API for searching.  Delegates to the real {@link org.alfresco.repo.search.Searcher searcher}
 * from the {@link #indexerAndSearcherFactory}.
 * 
 * Transactional support is free.
 * 
 * @author andyh
 * 
 */
public class SearcherComponent extends AbstractSearcherComponent
{
    private IndexerAndSearcher indexerAndSearcherFactory;
    
    public void setIndexerAndSearcherFactory(IndexerAndSearcher indexerAndSearcherFactory)
    {
        this.indexerAndSearcherFactory = indexerAndSearcherFactory;
    }

    public ResultSet query(StoreRef store,
            String language,
            String query,
            Path[] queryOptions,
            QueryParameterDefinition[] queryParameterDefinitions)
    {
        Searcher searcher = indexerAndSearcherFactory.getSearcher(store, false);
        return searcher.query(store, language, query, queryOptions, queryParameterDefinitions);
    }

    public ResultSet query(StoreRef store, QName queryId, QueryParameter[] queryParameters)
    {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

   
}
