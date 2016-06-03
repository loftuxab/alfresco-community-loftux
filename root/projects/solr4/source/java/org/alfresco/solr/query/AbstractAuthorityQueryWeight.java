package org.alfresco.solr.query;

import java.io.IOException;

import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.index.IndexReaderContext;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermContext;
import org.apache.lucene.search.CollectionStatistics;
import org.apache.lucene.search.Explanation;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermStatistics;
import org.apache.lucene.search.Weight;
import org.apache.lucene.search.similarities.TFIDFSimilarity;
import org.apache.solr.search.SolrIndexSearcher;

/**
 * Abstract {@link Weight} implementation for authority related queries.
 * 
 * @see AbstractAuthoritySetQuery
 */
public abstract class AbstractAuthorityQueryWeight extends Weight
{
    protected Query query;
    protected SolrIndexSearcher searcher;
    protected TFIDFSimilarity similarity;
    protected float value;
    protected float idf;
    protected float queryNorm;
    protected float queryWeight;
    protected Explanation idfExp;
    
    public AbstractAuthorityQueryWeight(SolrIndexSearcher searcher, Query query, String authTermName, String authTermText) throws IOException
    {
        this.searcher = searcher;
        this.query = query;
        this.similarity = (TFIDFSimilarity) searcher.getSimilarity();
        CollectionStatistics collectionStats = searcher.collectionStatistics(authTermName);
        final IndexReaderContext context = searcher.getTopReaderContext();
        final Term term = new Term(authTermName, authTermText);
        final TermContext termContext = TermContext.build(context, term);
        TermStatistics termStats = searcher.termStatistics(term, termContext);
        idfExp = similarity.idfExplain(collectionStats, termStats);
        idf = idfExp.getValue();
    }

    @Override
    public Query getQuery()
    {
        return query;
    }
    
    @Override
    public Explanation explain(AtomicReaderContext context, int doc) throws IOException
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public float getValueForNormalization() throws IOException
    {
        return sumOfSquaredWeights();
    }

    @Override
    public void normalize(float queryNorm, float topLevelBoost)
    {
        this.queryNorm = queryNorm;
        queryWeight *= queryNorm;                   // normalize query weight
        value = queryWeight * idf;                  // idf for document
    }
    
    protected float sumOfSquaredWeights() throws IOException
    {
        queryWeight = idf * query.getBoost();       // compute query weight
        return queryWeight * queryWeight;           // square it
    }
}
