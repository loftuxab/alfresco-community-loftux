package org.alfresco.solr.query;

import java.io.IOException;

import org.apache.lucene.search.Explanation;
import org.apache.lucene.search.Scorer;
import org.apache.lucene.search.Similarity;
import org.apache.lucene.util.OpenBitSet;
import org.apache.solr.search.BitDocSet;
import org.apache.solr.search.DocIterator;
import org.apache.solr.search.DocSet;
import org.apache.solr.search.SolrIndexReader;

/**
 * @author Andy
 *
 */
public class AbstractSolrCachingScorer extends Scorer
{
    BitDocSet matches;

    int doc = -1;

    OpenBitSet openBitSet;

    SolrIndexReader solrIndexReader;

    AbstractSolrCachingScorer(Similarity similarity, DocSet in, SolrIndexReader solrIndexReader)
    {
        super(similarity);
        if (in instanceof BitDocSet)
        {
            matches = (BitDocSet) in;
        }
        else
        {
            this.matches = new BitDocSet(new OpenBitSet(solrIndexReader.maxDoc()));
            for (DocIterator it = in.iterator(); it.hasNext(); /* */)
            {
                matches.addUnique(it.nextDoc());
            }
        }
        openBitSet = matches.getBits();
        this.solrIndexReader = solrIndexReader;
        doc = solrIndexReader.getBase() - 1;
    }

    
    @Override
    public boolean next() throws IOException
    {
        doc = openBitSet.nextSetBit(doc+1);
        return (doc != -1)  && (doc < (solrIndexReader.getBase()  + solrIndexReader.maxDoc()));
    }

    @Override
    public int doc()
    {
        return doc - solrIndexReader.getBase();
    }

    @Override
    public float score() throws IOException
    {
        return 1.0f;
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean skipTo(int target) throws IOException
    {
        while (next())
        {
            if (doc() >= target)
            {
                return true;
            }
        }
        return false;
    }

    @Override
    public Explanation explain(int position) throws IOException
    {
        throw new UnsupportedOperationException();
    }
}
