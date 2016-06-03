package org.alfresco.solr.query;

import java.io.IOException;
import java.util.ArrayList;

import org.alfresco.repo.search.impl.lucene.query.CachingTermPositions;
import org.alfresco.repo.search.impl.lucene.query.SelfAxisStructuredFieldPosition;
import org.alfresco.repo.search.impl.lucene.query.StructuredFieldPosition;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermPositions;
import org.apache.lucene.search.Explanation;
import org.apache.lucene.search.Scorer;
import org.apache.lucene.search.Similarity;
import org.apache.lucene.search.Weight;
import org.apache.solr.search.SolrIndexReader;

public class SolrPathScorer extends Scorer
{
    Scorer scorer;
  
    SolrPathScorer(Similarity similarity, Scorer scorer)
    {
        super(similarity);
        this.scorer = scorer;
    }
  

    public static SolrPathScorer createPathScorer(Similarity similarity, SolrPathQuery solrPathQuery, SolrIndexReader reader, Weight weight, DictionaryService dictionarySertvice, boolean repeat) throws IOException
    {
        
        StructuredFieldPosition last = null;
        if(solrPathQuery.getPathStructuredFieldPositions().size() > 0)
        {
           last = solrPathQuery.getPathStructuredFieldPositions().get(solrPathQuery.getPathStructuredFieldPositions().size() - 1);
        }
   
        
        if (solrPathQuery.getPathStructuredFieldPositions().size() == 0) 
        {
                ArrayList<StructuredFieldPosition> answer = new ArrayList<StructuredFieldPosition>(2);
                answer.add(new SelfAxisStructuredFieldPosition());
                answer.add(new SelfAxisStructuredFieldPosition());
                
                solrPathQuery.appendQuery(answer);
        }

        
        for (StructuredFieldPosition sfp : solrPathQuery.getPathStructuredFieldPositions())
        {
            if (sfp.getTermText() != null)
            {
                TermPositions p = reader.termPositions(new Term(solrPathQuery.getPathField(), sfp.getTermText()));
                if (p == null)
                    return null;
                CachingTermPositions ctp = new CachingTermPositions(p);
                sfp.setCachingTermPositions(ctp);
            }
        }

        SolrContainerScorer cs = null;

        TermPositions rootContainerPositions = null;
        if (solrPathQuery.getPathRootTerm() != null)
        {
            rootContainerPositions = reader.termPositions(solrPathQuery.getPathRootTerm());
        }
       
        if (solrPathQuery.getPathStructuredFieldPositions().size() > 0)
        {
            cs = new SolrContainerScorer(weight, rootContainerPositions, (StructuredFieldPosition[]) solrPathQuery.getPathStructuredFieldPositions().toArray(new StructuredFieldPosition[] {}),
                    similarity, reader.norms(solrPathQuery.getPathField()));
        }
       
       
        return new SolrPathScorer(similarity, cs);
    }

    @Override
    public boolean next() throws IOException
    {
        return scorer.next();
    }

    @Override
    public int doc()
    {
        return scorer.doc();
    }

    @Override
    public float score() throws IOException
    {
        return scorer.score();
    }

    @Override
    public boolean skipTo(int position) throws IOException
    {
        return scorer.skipTo(position);
    }

    @Override
    public Explanation explain(int position) throws IOException
    {
        return scorer.explain(position);
    }

}
