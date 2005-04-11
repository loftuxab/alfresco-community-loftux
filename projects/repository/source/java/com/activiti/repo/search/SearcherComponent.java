package com.activiti.repo.search;

import com.activiti.repo.ref.Path;
import com.activiti.repo.ref.StoreRef;

/**
 * Component API for searching.  Delegates to the real {@link com.activiti.repo.search.Searcher searcher}
 * from the {@link #indexerAndSearcherFactory}.
 * 
 * Transactional support is free.
 * 
 * @author andyh
 * 
 */
public class SearcherComponent implements Searcher
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
            QueryParameter[] queryParameters)
    {
        Searcher searcher = indexerAndSearcherFactory.getSearcher(store, false);
        return searcher.query(store, language, query, queryOptions, queryParameters);
    }
}
