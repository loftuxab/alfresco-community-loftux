package org.alfresco.solr.query;

import java.io.IOException;

import org.alfresco.repo.search.adaptor.lucene.QueryConstants;
import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Scorer;
import org.apache.lucene.search.Weight;
import org.apache.lucene.util.Bits;
import org.apache.solr.search.SolrIndexSearcher;

/**
 * Query for docs the supplied authority is able to read.
 * 
 * @author Matt Ward
 */
public class SolrAuthorityQuery extends AbstractAuthorityQuery
{
    public SolrAuthorityQuery(String authority)
    {
        super(authority);
    }

    @Override
    public String toString()
    {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(QueryConstants.FIELD_AUTHORITY).append(':');
        stringBuilder.append(authority);
        return stringBuilder.toString();
    }

    @Override
    public Weight createWeight(IndexSearcher searcher) throws IOException
    {
        if(!(searcher instanceof SolrIndexSearcher))
        {
            throw new IllegalStateException("Must have a SolrIndexSearcher");
        }
        return new SolrAuthorityQueryWeight((SolrIndexSearcher)searcher, this, authority);
    }
    
    private class SolrAuthorityQueryWeight extends AbstractAuthorityQueryWeight
    {
        public SolrAuthorityQueryWeight(SolrIndexSearcher searcher, Query query, String authority) throws IOException
        {
            super(searcher, query, QueryConstants.FIELD_AUTHORITY, authority);
        }

        @Override
        public Scorer scorer(AtomicReaderContext context, Bits acceptDocs) throws IOException
        {
            return SolrAuthorityScorer.createAuthorityScorer(this, context, acceptDocs, searcher, authority);
        }   
    }
}
