/*
 * Copyright (C) 2005-2014 Alfresco Software Limited.
 *
 * This file is part of Alfresco
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
 */
package org.alfresco.solr.query;

import java.io.IOException;

import org.alfresco.repo.search.adaptor.lucene.QueryConstants;
import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.index.NumericDocValues;
import org.apache.lucene.search.ConstantScoreQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Scorer;
import org.apache.lucene.search.Weight;
import org.apache.solr.search.DelegatingCollector;
import org.apache.solr.search.PostFilter;
import org.apache.solr.search.SolrIndexSearcher;

/**
 * Query for a set of denied authorities.
 * 
 * @author Matt Ward
 */
public class SolrDenySetQuery extends AbstractAuthoritySetQuery implements PostFilter
{
    private int cost;
    private boolean cache;
    private boolean cacheSep;
    public SolrDenySetQuery(String authorities)
    {
        super(authorities);
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public int getCost() {
        return 200;
    }

    public boolean getCache() {
        return false;
    }

    public void setCache(boolean cache)
    {
        this.cache = cache;
    }

    public boolean getCacheSep()
    {
        return false;
    }

    public void setCacheSep(boolean cacheSep)
    {
        this.cacheSep = cacheSep;
    }
    
    @Override
    public Weight createWeight(IndexSearcher searcher) throws IOException
    {
        if(!(searcher instanceof SolrIndexSearcher))
        {
            throw new IllegalStateException("Must have a SolrIndexSearcher");
        }

        String[] auths = authorities.substring(1).split(authorities.substring(0, 1));
        BitsFilter denyFilter  = getACLFilter(auths, QueryConstants.FIELD_DENIED, (SolrIndexSearcher) searcher);
        return new ConstantScoreQuery(denyFilter).createWeight(searcher);
    }

    public DelegatingCollector getFilterCollector(IndexSearcher searcher)
    {
        String[] auths = authorities.substring(1).split(authorities.substring(0, 1));
        try
        {
            HybridBitSet denySet = getACLSet(auths, QueryConstants.FIELD_DENIED, (SolrIndexSearcher) searcher);
            return new AccessControlCollector(denySet);
        }
        catch(Exception e)
        {
            throw new RuntimeException(e);
        }
    }

        @Override
    public String toString()
    {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(QueryConstants.FIELD_DENYSET).append(':');
        stringBuilder.append(authorities);
        return stringBuilder.toString();
    }

    class AccessControlCollector extends DelegatingCollector
    {
        private HybridBitSet aclIds;
        private NumericDocValues fieldValues;

        public AccessControlCollector(HybridBitSet aclIds)
        {
            this.aclIds=aclIds;
        }

        public boolean acceptsDocsOutOfOrder() {
            return false;
        }

        public void setNextReader(AtomicReaderContext context) throws IOException {
            this.fieldValues = DocValuesCache.getNumericDocValues(QueryConstants.FIELD_ACLID, context.reader());
            delegate.setNextReader(context);
        }

        public void setScorer(Scorer scorer) throws IOException {
            delegate.setScorer(scorer);
        }

        public void collect(int doc) throws IOException{
            long aclId = this.fieldValues.get(doc);
            if(!aclIds.get(aclId))
            {
                delegate.collect(doc);
            }
        }
    }
}
