
package org.alfresco.solr.query;

import java.util.List;

import org.apache.lucene.search.Filter;
import org.apache.lucene.search.DocIdSet;
import org.apache.lucene.search.BitsFilteredDocIdSet;
import org.apache.lucene.util.Bits;
import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.util.FixedBitSet;


/*
* A segment level Lucene Filter where each segment is backed by a FixedBitSet.
*/

public class BitsFilter extends Filter {

    private List<FixedBitSet> bitSets;

    public BitsFilter(List<FixedBitSet> bitSets)
    {
        this.bitSets = bitSets;
    }

    public void or(BitsFilter bitsFilter)
    {
        List<FixedBitSet> andSets = bitsFilter.bitSets;
        for(int i=0; i<bitSets.size(); i++)
        {
            FixedBitSet a = bitSets.get(i);
            FixedBitSet b = andSets.get(i);
            a.or(b);
        }
    }

    public void and(BitsFilter bitsFilter)
    {
        List<FixedBitSet> andSets = bitsFilter.bitSets;
        for(int i=0; i<bitSets.size(); i++)
        {
            FixedBitSet a = bitSets.get(i);
            FixedBitSet b = andSets.get(i);
            a.and(b);
        }
    }

    public List<FixedBitSet> getBitSets()
    {
        return this.bitSets;
    }

    public String toString(String s) {
        return s;
    }

    public DocIdSet getDocIdSet(AtomicReaderContext context, Bits bits) {
        return BitsFilteredDocIdSet.wrap(bitSets.get(context.ord), bits);
    }
}
