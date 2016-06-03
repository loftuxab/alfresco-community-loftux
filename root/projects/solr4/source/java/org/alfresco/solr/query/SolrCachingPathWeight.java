package org.alfresco.solr.query;

import java.io.IOException;

import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.search.Explanation;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Scorer;
import org.apache.lucene.search.Weight;
import org.apache.lucene.util.Bits;
import org.apache.solr.search.SolrIndexSearcher;

/**
 * {@link Weight} implementation for the {@link SolrCachingPathQuery}.
 * 
 * @author Matt Ward
 */
public class SolrCachingPathWeight extends Weight
{
    private SolrCachingPathQuery cachingPathQuery;
    private SolrIndexSearcher searcher;
    
    public SolrCachingPathWeight(SolrCachingPathQuery cachingPathQuery, SolrIndexSearcher searcher) throws IOException 
    {
        this.searcher = searcher;
        this.cachingPathQuery = cachingPathQuery;
    }

    @Override
    public Explanation explain(AtomicReaderContext context, int doc) throws IOException
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public Query getQuery()
    {
        return cachingPathQuery;
    }

    @Override
    public float getValueForNormalization() throws IOException
    {
        return 1.0f;
    }

    @Override
    public void normalize(float norm, float topLevelBoost)
    {
    }

    @Override
    public Scorer scorer(AtomicReaderContext context, Bits acceptDocs) throws IOException
    {
        return SolrCachingPathScorer.create(this, context, acceptDocs, searcher, cachingPathQuery.pathQuery);
    }
}
