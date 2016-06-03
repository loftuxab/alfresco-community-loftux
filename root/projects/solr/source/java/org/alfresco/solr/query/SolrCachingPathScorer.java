package org.alfresco.solr.query;

import java.io.IOException;

import org.alfresco.solr.AlfrescoSolrEventListener;
import org.alfresco.solr.AlfrescoSolrEventListener.CacheEntry;
import org.alfresco.solr.ContextAwareQuery;
import org.alfresco.solr.ResizeableArrayList;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Similarity;
import org.apache.lucene.util.OpenBitSet;
import org.apache.solr.search.BitDocSet;
import org.apache.solr.search.DocIterator;
import org.apache.solr.search.DocSet;
import org.apache.solr.search.SolrCache;
import org.apache.solr.search.SolrIndexReader;
import org.apache.solr.search.SolrIndexSearcher;

public class SolrCachingPathScorer extends AbstractSolrCachingScorer
{

    SolrCachingPathScorer(Similarity similarity, DocSet in, SolrIndexReader solrIndexReader)
    {
        super(similarity, in, solrIndexReader);

    }

    public static SolrCachingPathScorer createPathScorer(SolrIndexSearcher searcher, Similarity similarity, SolrPathQuery solrPathQuery, SolrIndexReader reader) throws IOException
    {
        // Get hold of solr top level searcher
        // Execute query with caching
        // translate reults to leaf docs
        // build ordered doc list

        // PathCollector pathCollector = new PathCollector();
        // searcher.search(solrPathQuery, pathCollector);

        Query key = new SolrCachingPathQuery(solrPathQuery);
        
        DocSet answer = (DocSet)searcher.cacheLookup(AlfrescoSolrEventListener.ALFRESCO_PATH_CACHE, key);
        if(answer != null)
        {
            return new SolrCachingPathScorer(similarity, answer, reader);
        }
        
        DocSet docSet = searcher.getDocSet(solrPathQuery);

        ResizeableArrayList<CacheEntry> indexedByDocId = (ResizeableArrayList<CacheEntry>) searcher.cacheLookup(AlfrescoSolrEventListener.ALFRESCO_ARRAYLIST_CACHE, AlfrescoSolrEventListener.KEY_DBID_LEAF_PATH_BY_DOC_ID);

        // List<ScoreDoc> auxDocs = pathCollector.getDocs();
        OpenBitSet translated = new OpenBitSet(searcher.getReader().maxDoc());

        if (docSet instanceof BitDocSet)
        {
            BitDocSet source = (BitDocSet) docSet;
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
            for (DocIterator it = docSet.iterator(); it.hasNext(); /* */)
            {
                CacheEntry entry = indexedByDocId.get(it.nextDoc());
                translated.set(entry.getLeaf());
            }
        }

        BitDocSet toCache =  new BitDocSet(translated);
        searcher.cacheInsert(AlfrescoSolrEventListener.ALFRESCO_PATH_CACHE, key, toCache);
        return new SolrCachingPathScorer(similarity, toCache, reader);
    }
}
