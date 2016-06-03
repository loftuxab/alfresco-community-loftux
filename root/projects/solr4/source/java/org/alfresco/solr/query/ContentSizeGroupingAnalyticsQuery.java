package org.alfresco.solr.query;

import java.util.HashMap;

import org.apache.lucene.search.IndexSearcher;
import org.apache.solr.handler.component.ResponseBuilder;
import org.apache.solr.search.AnalyticsQuery;
import org.apache.solr.search.DelegatingCollector;

/**
 * @author Andy
 *
 */
public class ContentSizeGroupingAnalyticsQuery extends AnalyticsQuery
{
    private int buckets;
    private int scale;

    /**
     * @param buckets 
     * @param mappings
     */
    public ContentSizeGroupingAnalyticsQuery(int scale, int buckets)
    {
        this.scale = scale;
        this.buckets = buckets;
        
    }

    /* (non-Javadoc)
     * @see org.apache.solr.search.AnalyticsQuery#getAnalyticsCollector(org.apache.solr.handler.component.ResponseBuilder, org.apache.lucene.search.IndexSearcher)
     */
    @Override
    public DelegatingCollector getAnalyticsCollector(ResponseBuilder rb, IndexSearcher searcher)
    {
        return new ContentSizeGroupingCollector(rb, scale, buckets);
    }

}
