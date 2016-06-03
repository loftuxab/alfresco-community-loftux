package org.alfresco.solr.query;

import java.io.IOException;
import java.util.ArrayList;

import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.index.DocsAndPositionsEnum;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.Scorer;
import org.apache.lucene.search.Weight;
import org.apache.lucene.util.Bits;

public class SolrPathScorer extends Scorer
{
    Scorer scorer;
  
    SolrPathScorer(Weight weight, Scorer scorer)
    {
        super(weight);
        this.scorer = scorer;
    }
  

    public static SolrPathScorer createPathScorer(SolrPathQuery solrPathQuery, AtomicReaderContext context, Bits acceptDocs, Weight weight, DictionaryService dictionarySertvice, boolean repeat) throws IOException
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
                DocsAndPositionsEnum p = context.reader().termPositionsEnum(new Term(solrPathQuery.getPathField(), sfp.getTermText()));
                if (p == null)
                    return null;
                CachingTermPositions ctp = new CachingTermPositions(p);
                sfp.setCachingTermPositions(ctp);
            }
        }

        SolrContainerScorer cs = null;

        DocsAndPositionsEnum rootContainerPositions = null;
        if (solrPathQuery.getPathRootTerm() != null)
        {
            rootContainerPositions = context.reader().termPositionsEnum(solrPathQuery.getPathRootTerm());
        }
       
        if (solrPathQuery.getPathStructuredFieldPositions().size() > 0)
        {
            cs = new SolrContainerScorer(weight, rootContainerPositions, (StructuredFieldPosition[]) solrPathQuery.getPathStructuredFieldPositions().toArray(new StructuredFieldPosition[] {}));
        }
       
       
        return new SolrPathScorer(weight, cs);
    }

    @Override
    public float score() throws IOException
    {
       return scorer.score();
    }

    @Override
    public int freq() throws IOException
    {
      return scorer.freq();
    }

    @Override
    public int docID()
    {
       return scorer.docID();
    }

    @Override
    public int nextDoc() throws IOException
    {
       return scorer.nextDoc();
    }

    @Override
    public int advance(int target) throws IOException
    {
        return scorer.advance(target);
    }

    @Override
    public long cost()
    {
       return scorer.cost();
    }


}
