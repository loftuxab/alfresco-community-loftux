package org.alfresco.repo.search;

import org.alfresco.service.cmr.repository.Path;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.search.QueryParameter;
import org.alfresco.service.cmr.search.QueryParameterDefinition;
import org.alfresco.service.cmr.search.ResultSet;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.namespace.QName;

/**
 * Component API for searching.  Delegates to the real {@link org.alfresco.service.cmr.search.SearchService searcher}
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
        SearchService searcher = indexerAndSearcherFactory.getSearcher(store, false);
        return searcher.query(store, language, query, queryOptions, queryParameterDefinitions);
    }

    public ResultSet query(StoreRef store, QName queryId, QueryParameter[] queryParameters)
    {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

   
}
