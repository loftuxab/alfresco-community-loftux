package org.alfresco.repo.search;

import org.alfresco.repo.dictionary.NamespaceService;
import org.alfresco.repo.ref.Path;
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

    public void setNameSpaceService(NamespaceService nameSpaceService)
    {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }
}
