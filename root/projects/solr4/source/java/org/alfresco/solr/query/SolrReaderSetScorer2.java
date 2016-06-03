package org.alfresco.solr.query;

import java.io.IOException;
import java.util.HashSet;

import org.alfresco.repo.search.adaptor.lucene.QueryConstants;
import org.alfresco.solr.cache.CacheConstants;
import org.apache.lucene.index.AtomicReader;
import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.index.NumericDocValues;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.Weight;
import org.apache.lucene.util.Bits;
import org.apache.lucene.util.FixedBitSet;
import org.apache.solr.search.BitDocSet;
import org.apache.solr.search.DocIterator;
import org.apache.solr.search.DocSet;
import org.apache.solr.search.SolrIndexSearcher;
import org.apache.solr.search.WrappedQuery;

public class SolrReaderSetScorer2 extends AbstractSolrCachingScorer
{
	SolrReaderSetScorer2(Weight weight, DocSet in, AtomicReaderContext context, Bits acceptDocs, SolrIndexSearcher searcher)
    {
        super(weight, in, context, acceptDocs, searcher);
    }

    public static AbstractSolrCachingScorer createReaderSetScorer(Weight weight, AtomicReaderContext context, Bits acceptDocs, SolrIndexSearcher searcher, String authorities, AtomicReader reader) throws IOException
    {
        
        DocSet readableDocSet = (DocSet) searcher.cacheLookup(CacheConstants.ALFRESCO_READER_CACHE, authorities);

        if (readableDocSet == null)
        {

            String[] auths = authorities.substring(1).split(authorities.substring(0, 1));

            readableDocSet = new BitDocSet(new FixedBitSet(searcher.maxDoc()));

            BooleanQuery bQuery = new BooleanQuery();
            for(String current : auths)
            {
                bQuery.add(new TermQuery(new Term(QueryConstants.FIELD_READER, current)), Occur.SHOULD);
            }
            WrappedQuery wrapped = new WrappedQuery(bQuery);
            wrapped.setCache(false);

            DocSet aclDocs = searcher.getDocSet(wrapped);
            
            HashSet<Long> aclsFound = new HashSet<Long>(aclDocs.size());
            NumericDocValues aclDocValues = searcher.getAtomicReader().getNumericDocValues(QueryConstants.FIELD_ACLID);
           
            for (DocIterator it = aclDocs.iterator(); it.hasNext(); /**/)
            {
                int docID = it.nextDoc();
                // Obtain the ACL ID for this ACL doc.
                long aclID = aclDocValues.get(docID);
                aclsFound.add(getLong(aclID));
            }
         
            if(aclsFound.size() > 0)
            {
                for(AtomicReaderContext readerContext : searcher.getTopReaderContext().leaves() )
                {
                    int maxDoc = readerContext.reader().maxDoc();
                    NumericDocValues fieldValues = DocValuesCache.getNumericDocValues(QueryConstants.FIELD_ACLID, readerContext.reader());
                    if(fieldValues != null)
                    {
                        for(int i = 0; i < maxDoc ; i++)
                        {
                            long aclID = fieldValues.get(i);
                            Long key = getLong(aclID);
                            if(aclsFound.contains(key))
                            {
                                readableDocSet.add(readerContext.docBase + i);
                            }
                        }
                    }

                }
            }
            
            // Exclude the ACL docs from the results, we only want real docs that match.
            // Probably not very efficient, what we really want is remove(docID)
            readableDocSet = readableDocSet.andNot(aclDocs);
            searcher.cacheInsert(CacheConstants.ALFRESCO_READER_CACHE, authorities, readableDocSet);
        }
        
        // TODO: cache the full set? e.g. searcher.cacheInsert(CacheConstants.ALFRESCO_READERSET_CACHE, authorities, readableDocSet)
        // plus check of course, for presence in cache at start of method.
        return new SolrReaderSetScorer2(weight, readableDocSet, context, acceptDocs, searcher);
    }
}
