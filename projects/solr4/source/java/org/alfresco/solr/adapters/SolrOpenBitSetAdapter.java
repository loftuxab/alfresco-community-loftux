package org.alfresco.solr.adapters;

import org.apache.lucene.util.OpenBitSet;

/**
 * The reason we have this class is so that lucene-free dependent classes can be dependent on IOpenBitSet instead of the
 * lucene-version-specific OpenBitSet.
 * @author Ahmed Owian
 */
public class SolrOpenBitSetAdapter extends OpenBitSet implements IOpenBitSet
{

    @Override
    public void or(IOpenBitSet duplicatedTxInIndex)
    {
        super.or((OpenBitSet) duplicatedTxInIndex);
    }

}
