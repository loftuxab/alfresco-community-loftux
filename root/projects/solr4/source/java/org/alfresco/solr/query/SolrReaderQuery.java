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
 * TODO: comment me!
 * @author Matt Ward
 */
public class SolrReaderQuery extends AbstractAuthorityQuery
{
    public SolrReaderQuery(String authority)
    {
        super(authority);
    }

    @Override
    public String toString()
    {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(QueryConstants.FIELD_READER).append(':');
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
        return new SolrReaderQueryWeight((SolrIndexSearcher)searcher, this, authority);
    }

    
    private class SolrReaderQueryWeight extends AbstractAuthorityQueryWeight
    {
        public SolrReaderQueryWeight(SolrIndexSearcher searcher, Query query, String reader) throws IOException
        {
            super(searcher, query, QueryConstants.FIELD_READER, reader);
        }

        @Override
        public Scorer scorer(AtomicReaderContext context, Bits acceptDocs) throws IOException
        {
            return SolrReaderScorer.createReaderScorer(this, context, acceptDocs, searcher, authority);
        }   
    }
}
