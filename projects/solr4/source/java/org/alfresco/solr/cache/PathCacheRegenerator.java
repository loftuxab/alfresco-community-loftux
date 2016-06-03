package org.alfresco.solr.cache;

import java.io.IOException;

import org.alfresco.solr.query.SolrCachingPathQuery;
import org.alfresco.solr.query.SolrPathQuery;
import org.apache.solr.search.CacheRegenerator;
import org.apache.solr.search.SolrCache;
import org.apache.solr.search.SolrIndexSearcher;

/**
 * {@link CacheRegenerator} for alfrescoPathCache
 * 
 * @author Matt Ward
 */
public class PathCacheRegenerator implements CacheRegenerator
{
    @SuppressWarnings({ "rawtypes" })
    @Override
    public boolean regenerateItem(SolrIndexSearcher newSearcher, SolrCache newCache,
                SolrCache oldCache, Object oldKey, Object oldVal) throws IOException
    {
        if (oldKey instanceof SolrPathQuery)
        {
            SolrPathQuery pathQuery = (SolrPathQuery) oldKey;
            // Re-execute the path query in a cache-aware context - causing new results to be cached.
            SolrCachingPathQuery cachingPathQuery = new SolrCachingPathQuery(pathQuery);
            newSearcher.getDocSet(cachingPathQuery);
        }
        return true;
    }

}
