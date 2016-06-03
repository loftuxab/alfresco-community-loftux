package org.alfresco.solr.cache;

import java.io.IOException;

import org.apache.lucene.search.Query;
import org.apache.solr.search.CacheRegenerator;
import org.apache.solr.search.SolrCache;
import org.apache.solr.search.SolrIndexSearcher;

/**
 * Cache regeneration for AUTHORITY and AUTHSET queries.
 * 
 * @author Matt Ward
 */
public class AuthorityCacheRegenerator implements CacheRegenerator
{
    @SuppressWarnings({ "rawtypes" })
    @Override
    public boolean regenerateItem(SolrIndexSearcher newSearcher, SolrCache newCache,
                SolrCache oldCache, Object oldKey, Object oldVal) throws IOException
    {
        if (oldKey instanceof Query)
        {
            // The authority cache contains results keyed by SolrAuthorityQuery
            // and SolrAuthoritySetQuery.
            Query authQuery = (Query) oldKey;
            // Execute the query on the new searcher - resulting in cache population as a side-effect.
            newSearcher.getDocSet(authQuery);
        }
        return true;
    }
}
