package org.alfresco.solr;

import java.io.IOException;

import org.apache.lucene.search.Query;
import org.apache.solr.search.CacheRegenerator;
import org.apache.solr.search.SolrCache;
import org.apache.solr.search.SolrIndexSearcher;

/**
 * @author Andy
 *
 */
public class FilterCacheRegenerator implements CacheRegenerator
{

    /* (non-Javadoc)
     * @see org.apache.solr.search.CacheRegenerator#regenerateItem(org.apache.solr.search.SolrIndexSearcher, org.apache.solr.search.SolrCache, org.apache.solr.search.SolrCache, java.lang.Object, java.lang.Object)
     */
    @Override
    public boolean regenerateItem(SolrIndexSearcher newSearcher, SolrCache newCache, SolrCache oldCache, Object oldKey, Object oldVal) throws IOException
    {
        if(oldKey instanceof Query)
        {
            Object cache = newSearcher.cacheLookup(AlfrescoSolrEventListener.ALFRESCO_CACHE, AlfrescoSolrEventListener.KEY_ALL_LEAF_DOCS);
            if(cache != null)
            {
                newSearcher.cacheDocSet((Query)oldKey, null, false);
            }
        }
        return true;
            
    }

}
