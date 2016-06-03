

package org.alfresco.solr.query;

public class EmptyHybridBitSet extends HybridBitSet
{
    public void set(long bit)
    {

    }

    public boolean get(long bit)
    {
        return false;
    }
}
