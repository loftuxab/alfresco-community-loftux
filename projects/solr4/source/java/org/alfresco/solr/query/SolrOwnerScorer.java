package org.alfresco.solr.query;

import java.io.IOException;

import org.alfresco.repo.search.adaptor.lucene.QueryConstants;
import org.alfresco.service.cmr.security.AuthorityType;
import org.alfresco.solr.cache.CacheConstants;
import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.Weight;
import org.apache.lucene.util.Bits;
import org.apache.lucene.util.FixedBitSet;
import org.apache.solr.search.BitDocSet;
import org.apache.solr.search.DocSet;
import org.apache.solr.search.SolrIndexSearcher;

/**
 * Find the set of documents for which the specified authority is the owner.
 * If the authority is not a user then an empty set is returned.
 * 
 * @author Matt Ward
 */
public class SolrOwnerScorer extends AbstractSolrCachingScorer
{
    SolrOwnerScorer(Weight weight, DocSet in, AtomicReaderContext context, Bits acceptDocs, SolrIndexSearcher searcher)
    {
        super(weight, in, context, acceptDocs, searcher);
    }

    public static SolrOwnerScorer createOwnerScorer(Weight weight, AtomicReaderContext context, Bits acceptDocs, SolrIndexSearcher searcher, String authority) throws IOException
    {
        if (AuthorityType.getAuthorityType(authority) == AuthorityType.USER)
        {
            DocSet ownedDocs = (DocSet) searcher.cacheLookup(CacheConstants.ALFRESCO_OWNERLOOKUP_CACHE, authority);

            if (ownedDocs == null)
            {
                // Cache miss: query the index for docs where the owner matches the authority. 
                ownedDocs = searcher.getDocSet(new TermQuery(new Term(QueryConstants.FIELD_OWNER, authority)));
                searcher.cacheInsert(CacheConstants.ALFRESCO_OWNERLOOKUP_CACHE, authority, ownedDocs);
            }
            return new SolrOwnerScorer(weight, ownedDocs, context, acceptDocs, searcher);
        }
        
        // Return an empty doc set, as the authority isn't a user.
        return new SolrOwnerScorer(weight, new BitDocSet(new FixedBitSet(0)), context, acceptDocs, searcher);
    }

}
