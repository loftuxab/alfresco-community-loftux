package org.alfresco.solr.query;

import java.io.IOException;

import org.alfresco.repo.search.adaptor.lucene.QueryConstants;
import org.alfresco.solr.cache.CacheConstants;
import org.apache.lucene.index.AtomicReader;
import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.Weight;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.util.Bits;
import org.apache.lucene.util.FixedBitSet;
import org.apache.solr.schema.SchemaField;
import org.apache.solr.search.BitDocSet;
import org.apache.solr.search.DocIterator;
import org.apache.solr.search.DocSet;
import org.apache.solr.search.SolrIndexSearcher;

public class SolrReaderSetScorer extends AbstractSolrCachingScorer
{

    SolrReaderSetScorer(Weight weight, DocSet in, AtomicReaderContext context, Bits acceptDocs, SolrIndexSearcher searcher)
    {
        super(weight, in, context, acceptDocs, searcher);
    }

    public static SolrReaderSetScorer createReaderSetScorer(Weight weight, AtomicReaderContext context, Bits acceptDocs, SolrIndexSearcher searcher, String authorities, AtomicReader reader) throws IOException
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

            DocSet aclDocs = searcher.getDocSet(bQuery);
            
            BooleanQuery aQuery = new BooleanQuery();
            for (DocIterator it = aclDocs.iterator(); it.hasNext(); /**/)
            {
                int docID = it.nextDoc();
                // Obtain the ACL ID for this ACL doc.
                long aclID = searcher.getAtomicReader().getNumericDocValues(QueryConstants.FIELD_ACLID).get(docID);
                SchemaField schemaField = searcher.getSchema().getField(QueryConstants.FIELD_ACLID);
                Query query = schemaField.getType().getFieldQuery(null, schemaField, Long.toString(aclID));
                aQuery.add(query,  Occur.SHOULD);
                
                if((aQuery.clauses().size() > 999) || !it.hasNext())
                {
                    DocSet docsForAclId = searcher.getDocSet(aQuery);                
                    readableDocSet = readableDocSet.union(docsForAclId);
                       
                    aQuery = new BooleanQuery();
                }
            }
            // Exclude the ACL docs from the results, we only want real docs that match.
            // Probably not very efficient, what we really want is remove(docID)
            readableDocSet = readableDocSet.andNot(aclDocs);
            searcher.cacheInsert(CacheConstants.ALFRESCO_READER_CACHE, authorities, readableDocSet);
        }
        
        // TODO: cache the full set? e.g. searcher.cacheInsert(CacheConstants.ALFRESCO_READERSET_CACHE, authorities, readableDocSet)
        // plus check of course, for presence in cache at start of method.
        return new SolrReaderSetScorer(weight, readableDocSet, context, acceptDocs, searcher);
    }
}
