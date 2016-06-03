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
 * 
 * @author Matt Ward
 */
public class SolrDeniedQuery extends AbstractAuthorityQuery
{
    public SolrDeniedQuery(String authority)
    {
        super(authority);
    }

    @Override
    public String toString()
    {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(QueryConstants.FIELD_DENIED).append(':');
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
        return new SolrDenyQueryWeight((SolrIndexSearcher)searcher, this, authority);
    }
    
    private class SolrDenyQueryWeight extends AbstractAuthorityQueryWeight
    {
        public SolrDenyQueryWeight(SolrIndexSearcher searcher, Query query, String reader) throws IOException
        {
            super(searcher, query, QueryConstants.FIELD_DENIED, reader);
        }

        @Override
        public Scorer scorer(AtomicReaderContext context, Bits acceptDocs) throws IOException
        {
            return SolrDeniedScorer.createDenyScorer(this, context, acceptDocs, searcher, authority);
        }   
    }
}
