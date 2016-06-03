package org.alfresco.solr.query;

import java.io.IOException;
import java.util.ArrayList;

import org.alfresco.repo.search.impl.lucene.query.AbsoluteStructuredFieldPosition;
import org.alfresco.repo.search.impl.lucene.query.DescendantAndSelfStructuredFieldPosition;
import org.alfresco.repo.search.impl.lucene.query.RelativeStructuredFieldPosition;
import org.alfresco.repo.search.impl.lucene.query.SelfAxisStructuredFieldPosition;
import org.alfresco.repo.search.impl.lucene.query.StructuredFieldPosition;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.Explanation;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Scorer;
import org.apache.lucene.search.Searcher;
import org.apache.lucene.search.Similarity;
import org.apache.lucene.search.Weight;
import org.apache.lucene.search.Explanation.IDFExplanation;
import org.apache.solr.search.SolrIndexReader;
import org.apache.solr.search.SolrIndexSearcher;

/**
 * @author Andy
 *
 */
public class SolrCachingPathQuery extends Query
{

    SolrPathQuery pathQuery;

    public SolrCachingPathQuery(SolrPathQuery pathQuery)
    {
        this.pathQuery = pathQuery;
    }
    
    /*
     * @see org.apache.lucene.search.Query#createWeight(org.apache.lucene.search.Searcher)
     */
    public Weight createWeight(Searcher searcher) throws IOException
    {
        if(!(searcher instanceof SolrIndexSearcher))
        {
            throw new IllegalStateException("Must have a SolrIndexSearcher");
        }
        return new SolrCachingPathQueryWeight((SolrIndexSearcher)searcher);
    }

    /*
     * @see java.lang.Object#toString()
     */
    public String toString()
    {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("CACHED -> :");
        stringBuilder.append(pathQuery.toString());
        return stringBuilder.toString();
    }

    /*
     * @see org.apache.lucene.search.Query#toString(java.lang.String)
     */
    public String toString(String field)
    {
        return toString();
    }

    
    
    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((pathQuery == null) ? 0 : pathQuery.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        SolrCachingPathQuery other = (SolrCachingPathQuery) obj;
        if (pathQuery == null)
        {
            if (other.pathQuery != null)
                return false;
        }
        else if (!pathQuery.equals(other.pathQuery))
            return false;
        return true;
    }



    private class SolrCachingPathQueryWeight extends Weight
    {
        SolrIndexSearcher searcher;
        
        private Similarity similarity;
        private float value;
        private float idf;
        private float queryNorm;
        private float queryWeight;
        private IDFExplanation idfExp;

        public SolrCachingPathQueryWeight(SolrIndexSearcher searcher) throws IOException 
        {
            this.searcher = searcher;
            this.similarity = getSimilarity(searcher);
            idfExp = similarity.idfExplain(SolrCachingPathQuery.this.pathQuery.getTerms(), searcher);
            idf = idfExp.getIdf();
        }

        /*
         * @see org.apache.lucene.search.Weight#explain(org.apache.lucene.index.IndexReader, int)
         */
        public Explanation explain(IndexReader reader, int doc) throws IOException
        {
            throw new UnsupportedOperationException();
        }

        /*
         * @see org.apache.lucene.search.Weight#getQuery()
         */
        public Query getQuery()
        {
            return SolrCachingPathQuery.this;
        }

        /*
         * (non-Javadoc)
         * @see org.apache.lucene.search.Weight#getValue()
         */
        public float getValue()
        {
            return value;
        }

        /*
         * (non-Javadoc)
         * @see org.apache.lucene.search.Weight#normalize(float)
         */
        public void normalize(float queryNorm)
        {
            this.queryNorm = queryNorm;
            queryWeight *= queryNorm;                   // normalize query weight
            value = queryWeight * idf;                  // idf for document
        }

        /*
         * (non-Javadoc)
         * @see org.apache.lucene.search.Weight#sumOfSquaredWeights()
         */
        public float sumOfSquaredWeights() throws IOException
        {
            queryWeight = idf * getBoost();             // compute query weight
            return queryWeight * queryWeight;           // square it
          }

        /*
         * (non-Javadoc)
         * @see org.apache.lucene.search.Weight#scorer(org.apache.lucene.index.IndexReader, boolean, boolean)
         */
        @Override
        public Scorer scorer(IndexReader reader, boolean scoreDocsInOrder, boolean topScorer) throws IOException
        {
            if(!(reader instanceof SolrIndexReader))
            {
                throw new IllegalStateException("Must have a SolrIndexReader");
            }
            return SolrCachingPathScorer.createPathScorer(searcher, getSimilarity(searcher), SolrCachingPathQuery.this.pathQuery, (SolrIndexReader)reader);
        }
    }
}
