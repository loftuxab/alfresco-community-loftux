
package org.alfresco.solr.query;

import org.apache.lucene.util.FixedBitSet;

import java.util.Set;
import java.util.HashSet;

/**
*  The HybridBitSet is a random access (doesn't support iteration) BitSet implementation that uses a FixedBitSet for the first N bits
*  and a HashSet for higher bits. This is designed to provide a balance between the high performance of FixedBitSet and
*  the efficient sparse behavior of a HashSet.
**/

public class HybridBitSet
{
    private FixedBitSet bits;
    private Set<Long> set = new HashSet();
    private int maxBit;

    public HybridBitSet()
    {

    }

    HybridBitSet(int maxBit)
    {
        this.bits = new FixedBitSet(maxBit);
        this.maxBit = maxBit;
    }

    public void set(long bit)
    {
        if(bit < maxBit)
        {
            bits.set((int)bit);
        }
        else
        {
            set.add((long)bit);
        }
    }

    public boolean get(long bit)
    {
        if(bit < maxBit)
        {
            return bits.get((int)bit);
        }
        else
        {
            return set.contains((long)bit);
        }
    }
}
