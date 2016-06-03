package org.alfresco.solr.query;

import java.io.IOException;

import org.alfresco.repo.search.adaptor.lucene.QueryConstants;
import org.alfresco.solr.cache.CacheConstants;
import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.Weight;
import org.apache.lucene.util.Bits;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.FixedBitSet;
import org.apache.solr.schema.SchemaField;
import org.apache.solr.search.BitDocSet;
import org.apache.solr.search.DocIterator;
import org.apache.solr.search.DocSet;
import org.apache.solr.search.SolrIndexSearcher;

/**
 * Find the set of docs denied to an authority.
 * 
 * @author Matt Ward
 */
public class SolrDeniedScorer extends AbstractSolrCachingScorer
{
    SolrDeniedScorer(Weight weight, DocSet in, AtomicReaderContext context, Bits acceptDocs, SolrIndexSearcher searcher)
    {
        super(weight, in, context, acceptDocs, searcher);
    }

    public static SolrDeniedScorer createDenyScorer(Weight weight, AtomicReaderContext context, Bits acceptDocs, SolrIndexSearcher searcher, String authority) throws IOException
    {     
        DocSet deniedDocs = (DocSet) searcher.cacheLookup(CacheConstants.ALFRESCO_DENIED_CACHE, authority);

        if (deniedDocs == null)
        {
            // Cache miss: query the index for ACL docs where the denial matches the authority. 
            DocSet aclDocs = searcher.getDocSet(new TermQuery(new Term(QueryConstants.FIELD_DENIED, authority)));
            
            // Allocate a bitset to store the results.
            deniedDocs = new BitDocSet(new FixedBitSet(searcher.maxDoc()));
            
            // Translate from ACL docs to real docs
            for (DocIterator it = aclDocs.iterator(); it.hasNext(); /**/)
            {
                int docID = it.nextDoc();
                // Obtain the ACL ID for this ACL doc.
                long aclID = searcher.getAtomicReader().getNumericDocValues(QueryConstants.FIELD_ACLID).get(docID);
                SchemaField schemaField = searcher.getSchema().getField(QueryConstants.FIELD_ACLID);
                Query query = schemaField.getType().getFieldQuery(null, schemaField, Long.toString(aclID));
                // Find real docs that match the ACL ID
                DocSet docsForAclId = searcher.getDocSet(query);                
                deniedDocs = deniedDocs.union(docsForAclId);
                // Exclude the ACL docs from the results, we only want real docs that match.
                // Probably not very efficient, what we really want is remove(docID)
                deniedDocs = deniedDocs.andNot(aclDocs);
            }
            
            searcher.cacheInsert(CacheConstants.ALFRESCO_DENIED_CACHE, authority, deniedDocs);
        }
        return new SolrDeniedScorer(weight, deniedDocs, context, acceptDocs, searcher);
    }
}
