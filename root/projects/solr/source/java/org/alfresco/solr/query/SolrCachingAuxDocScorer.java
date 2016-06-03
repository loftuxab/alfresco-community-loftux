package org.alfresco.solr.query;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;

import org.alfresco.solr.AlfrescoSolrEventListener;
import org.alfresco.solr.AlfrescoSolrEventListener.AclLookUp;
import org.alfresco.solr.AlfrescoSolrEventListener.CacheEntry;
import org.alfresco.solr.ContextAwareQuery;
import org.alfresco.solr.ResizeableArrayList;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.Explanation;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Scorer;
import org.apache.lucene.search.Similarity;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.util.OpenBitSet;
import org.apache.solr.search.BitDocSet;
import org.apache.solr.search.DocIterator;
import org.apache.solr.search.DocSet;
import org.apache.solr.search.SolrIndexReader;
import org.apache.solr.search.SolrIndexSearcher;

public class SolrCachingAuxDocScorer extends AbstractSolrCachingScorer
{
   
    SolrCachingAuxDocScorer(Similarity similarity, DocSet in, SolrIndexReader solrIndexReader)
    {
        super(similarity, in, solrIndexReader);
       
    }

    public static SolrCachingAuxDocScorer createAuxDocScorer(SolrIndexSearcher searcher, Similarity similarity, Query query, SolrIndexReader reader) throws IOException
    {
        // Get hold of solr top level searcher
        // Execute query with caching
        // translate reults to leaf docs
        // build ordered doc list

        DocSet auxDocSet = searcher.getDocSet(new ContextAwareQuery(query, null));

        ResizeableArrayList<CacheEntry> indexedByDocId = (ResizeableArrayList<CacheEntry>) searcher.cacheLookup(AlfrescoSolrEventListener.ALFRESCO_ARRAYLIST_CACHE, AlfrescoSolrEventListener.KEY_DBID_LEAF_PATH_BY_DOC_ID);

        // List<ScoreDoc> auxDocs = pathCollector.getDocs();
        OpenBitSet translated = new OpenBitSet(searcher.getReader().maxDoc());

        if (auxDocSet instanceof BitDocSet)
        {
            BitDocSet source = (BitDocSet) auxDocSet;
            OpenBitSet openBitSet = source.getBits();
            int current = -1;
            while ((current = openBitSet.nextSetBit(current + 1)) != -1)
            {
                CacheEntry entry = indexedByDocId.get(current);
                translated.set(entry.getLeaf());
            }
        }
        else
        {
            for (DocIterator it = auxDocSet.iterator(); it.hasNext(); /* */)
            {
                CacheEntry entry = indexedByDocId.get(it.nextDoc());
                translated.set(entry.getLeaf());
            }
        }

        return new SolrCachingAuxDocScorer(similarity, new BitDocSet(translated), reader);
        
    }
}
