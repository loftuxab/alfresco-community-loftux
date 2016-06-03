package org.alfresco.solr.query;

import java.io.IOException;

import org.alfresco.repo.search.adaptor.lucene.QueryConstants;
import org.apache.lucene.index.AtomicReader;
import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Scorer;
import org.apache.lucene.search.Weight;
import org.apache.lucene.util.Bits;
import org.apache.solr.search.SolrIndexSearcher;

/**
 * @author Andy
 * @author Matt Ward
 */
public class SolrReaderSetQuery extends AbstractAuthoritySetQuery
{
    public SolrReaderSetQuery(String authorities)
    {
        super(authorities);
    }
    
    @Override
    public Weight createWeight(IndexSearcher searcher) throws IOException
    {
        if(!(searcher instanceof SolrIndexSearcher))
        {
            throw new IllegalStateException("Must have a SolrIndexSearcher");
        }
        return new SolrReaderSetQueryWeight((SolrIndexSearcher)searcher, this, authorities);
    }

    @Override
    public String toString()
    {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(QueryConstants.FIELD_READERSET).append(':');
        stringBuilder.append(authorities);
        return stringBuilder.toString();
    }

    private class SolrReaderSetQueryWeight extends AbstractAuthorityQueryWeight
    {
        public SolrReaderSetQueryWeight(SolrIndexSearcher searcher, Query query, String readers) throws IOException
        {
            super(searcher, query, QueryConstants.FIELD_READERSET, readers);
        }

        @Override
        public Scorer scorer(AtomicReaderContext context, Bits acceptDocs) throws IOException
        {
            AtomicReader reader = context.reader();
            return SolrReaderSetScorer2.createReaderSetScorer(this, context, acceptDocs, searcher, authorities, reader);
        }
    }
}
