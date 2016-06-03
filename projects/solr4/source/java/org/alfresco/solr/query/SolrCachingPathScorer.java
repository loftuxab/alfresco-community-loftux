package org.alfresco.solr.query;

import java.io.IOException;

import org.alfresco.solr.cache.CacheConstants;
import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.search.Weight;
import org.apache.lucene.util.Bits;
import org.apache.solr.search.DocSet;
import org.apache.solr.search.SolrIndexSearcher;
import org.apache.solr.search.WrappedQuery;

/**
 * Caching wrapper for {@link SolrPathQuery}.
 *  
 * @author Matt Ward
 */
public class SolrCachingPathScorer extends AbstractSolrCachingScorer
{
    SolrCachingPathScorer(Weight weight, DocSet in, AtomicReaderContext context, Bits acceptDocs, SolrIndexSearcher searcher)
    {
        super(weight, in, context, acceptDocs, searcher);
    }


    /**
     * Factory method used to create {@link SolrCachingPathScorer} instances.
     * @param acceptDocs 
     */
    public static SolrCachingPathScorer create(SolrCachingPathWeight weight,
                                               AtomicReaderContext context,
                                               Bits acceptDocs, SolrIndexSearcher searcher,
                                               SolrPathQuery wrappedPathQuery) throws IOException
    {
        DocSet results = (DocSet) searcher.cacheLookup(CacheConstants.ALFRESCO_PATH_CACHE, wrappedPathQuery);
        if (results == null)
        {
            // Cache miss: get path query results and cache them
            WrappedQuery wrapped = new WrappedQuery(wrappedPathQuery);
            wrapped.setCache(false);
            results = searcher.getDocSet(wrapped);
            searcher.cacheInsert(CacheConstants.ALFRESCO_PATH_CACHE, wrappedPathQuery, results);
        }
        
        return new SolrCachingPathScorer(weight, results, context, acceptDocs, searcher);
    }
}
