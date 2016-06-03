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
public class MimetypeGroupingAnalyticsQuery extends AnalyticsQuery
{
    private HashMap<String, String> mappings;
    private boolean group;

    /**
     * @param mappings
     * @param group 
     */
    public MimetypeGroupingAnalyticsQuery(HashMap<String, String> mappings, boolean group)
    {
        this.mappings = mappings;
        this.group = group;
        
    }

    /* (non-Javadoc)
     * @see org.apache.solr.search.AnalyticsQuery#getAnalyticsCollector(org.apache.solr.handler.component.ResponseBuilder, org.apache.lucene.search.IndexSearcher)
     */
    @Override
    public DelegatingCollector getAnalyticsCollector(ResponseBuilder rb, IndexSearcher searcher)
    {
        return new MimetypeGroupingCollector(rb, mappings, group);
    }

}
