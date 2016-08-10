/*
 * #%L
 * Alfresco Solr
 * %%
 * Copyright (C) 2005 - 2016 Alfresco Software Limited
 * %%
 * This file is part of the Alfresco software. 
 * If the software was purchased under a paid Alfresco license, the terms of 
 * the paid license agreement will prevail.  Otherwise, the software is 
 * provided under the following open source license terms:
 * 
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */
package org.alfresco.solr.query;

import java.io.IOException;

import org.apache.lucene.search.Explanation;
import org.apache.lucene.search.Scorer;
import org.apache.lucene.search.Similarity;
import org.apache.lucene.util.OpenBitSet;
import org.apache.solr.search.BitDocSet;
import org.apache.solr.search.DocIterator;
import org.apache.solr.search.DocSet;
import org.apache.solr.search.SolrIndexReader;

/**
 * @author Andy
 *
 */
public class AbstractSolrCachingScorer extends Scorer
{
    BitDocSet matches;

    int doc = -1;

    OpenBitSet openBitSet;

    SolrIndexReader solrIndexReader;

    AbstractSolrCachingScorer(Similarity similarity, DocSet in, SolrIndexReader solrIndexReader)
    {
        super(similarity);
        if (in instanceof BitDocSet)
        {
            matches = (BitDocSet) in;
        }
        else
        {
            this.matches = new BitDocSet(new OpenBitSet(solrIndexReader.maxDoc()));
            for (DocIterator it = in.iterator(); it.hasNext(); /* */)
            {
                matches.addUnique(it.nextDoc());
            }
        }
        openBitSet = matches.getBits();
        this.solrIndexReader = solrIndexReader;
        doc = solrIndexReader.getBase() - 1;
    }

    
    @Override
    public boolean next() throws IOException
    {
        doc = openBitSet.nextSetBit(doc+1);
        return (doc != -1)  && (doc < (solrIndexReader.getBase()  + solrIndexReader.maxDoc()));
    }

    @Override
    public int doc()
    {
        return doc - solrIndexReader.getBase();
    }

    @Override
    public float score() throws IOException
    {
        return 1.0f;
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean skipTo(int target) throws IOException
    {
        while (next())
        {
            if (doc() >= target)
            {
                return true;
            }
        }
        return false;
    }

    @Override
    public Explanation explain(int position) throws IOException
    {
        throw new UnsupportedOperationException();
    }
}
