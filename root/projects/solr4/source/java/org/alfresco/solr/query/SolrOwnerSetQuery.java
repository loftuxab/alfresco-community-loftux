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
 * @author Andy
 *
 */
public class SolrOwnerSetQuery extends AbstractAuthoritySetQuery
{
    public SolrOwnerSetQuery(String authorities)
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
        return new SolrOwnerSetQueryWeight((SolrIndexSearcher)searcher, this, authorities);
    }

    @Override
    public String toString()
    {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(QueryConstants.FIELD_OWNERSET).append(':');
        stringBuilder.append(authorities);
        return stringBuilder.toString();
    }


    private class SolrOwnerSetQueryWeight extends AbstractAuthorityQueryWeight
    {
        public SolrOwnerSetQueryWeight(SolrIndexSearcher searcher, Query query, String authorities) throws IOException
        {
            super(searcher, query, QueryConstants.FIELD_OWNERSET, authorities);
        }

        @Override
        public Scorer scorer(AtomicReaderContext context, Bits acceptDocs) throws IOException
        {
            return SolrOwnerSetScorer.createOwnerSetScorer(this, context, acceptDocs, searcher, authorities);
        }
    }
}
