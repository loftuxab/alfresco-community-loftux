/*
 * #%L
 * Alfresco Solr 4
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

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.ArrayList;

import org.alfresco.solr.ContextAwareQuery;
import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Scorer;
import org.apache.solr.search.DelegatingCollector;
import org.apache.solr.search.PostFilter;

import java.io.IOException;

public class PostFilterQuery extends Query implements PostFilter
{
    private int cost;
    private Query query;

    public PostFilterQuery(int cost, Query query)
    {
        this.cost = cost;
        this.query = query;
    }

    public int hashcode()
    {
        return query.hashCode();
    }

    public boolean equals(Object o)
    {
        if(o instanceof PostFilterQuery)
        {
            PostFilterQuery p = (PostFilterQuery)o;
            return query.equals(p.query);
        }

        return false;
    }

    public int getCost()
    {
        return cost;
    }

    public void setCost(int cost)
    {
       this.cost = cost;
    }

    public boolean getCache()
    {
        return false;
    }

    public void setCache(boolean cache)
    {

    }

    public boolean getCacheSep()
    {
        return false;
    }

    public void setCacheSep(boolean cacheSep)
    {

    }

    public String toString(String s)
    {
        return s;
    }

    public DelegatingCollector getFilterCollector(IndexSearcher searcher)
    {
        List<PostFilter> postFilters = new ArrayList();
        getPostFilters(query, postFilters);

        Collections.sort(postFilters, new PostFilterComp());

        List<DelegatingCollector> delegatingCollectors = new ArrayList();
        for(PostFilter postFilter : postFilters)
        {
            DelegatingCollector delegatingCollector = postFilter.getFilterCollector(searcher);
            if(!(delegatingCollector instanceof AllAccessCollector)) {
                delegatingCollectors.add(delegatingCollector);
            }
        }

        if(delegatingCollectors.size() == 0)
        {
            return new AllAccessCollector();
        }
        else if(delegatingCollectors.size() == 1)
        {
            return delegatingCollectors.get(0);
        }
        else
        {
            return new WrapperCollector(delegatingCollectors);
        }
    }

    private static class WrapperCollector extends DelegatingCollector
    {
        private DelegatingCollector innerDelegate;
        private CollectorSink sink;

        public WrapperCollector(List<DelegatingCollector> delegatingCollectors)
        {
            for(DelegatingCollector delegatingCollector : delegatingCollectors)
            {
                if(innerDelegate == null)
                {
                    innerDelegate = delegatingCollector;
                }
                else
                {
                    innerDelegate.setLastDelegate(delegatingCollector);
                }
            }

            this.sink = new CollectorSink();
            innerDelegate.setLastDelegate(this.sink);
        }

        public boolean acceptsDocsOutOfOrder() {
            return false;
        }

        public void setScorer(Scorer scorer) throws IOException
        {
            delegate.setScorer(scorer);
        }

        public void setNextReader(AtomicReaderContext context) throws IOException
        {
            innerDelegate.setNextReader(context);
            delegate.setNextReader(context);
        }

        public void collect(int doc) throws IOException
        {
            innerDelegate.collect(doc);
            if(sink.doc == doc) {
                sink.doc = -1;
                delegate.collect(doc);
            }
        }
    }

    private static class CollectorSink extends DelegatingCollector
    {
        public int doc = -1;

        public void collect(int doc) throws IOException
        {
            this.doc = doc;
        }

        public void setNextReader(AtomicReaderContext context)
        {

        }
    }

    private void getPostFilters(Query q, List<PostFilter> postFilters)
    {
        if(q instanceof BooleanQuery)
        {
            BooleanQuery bq = (BooleanQuery) q;
            BooleanClause[] clauses = bq.getClauses();
            for (BooleanClause clause : clauses)
            {
                Query q1 = clause.getQuery();
                getPostFilters(q1, postFilters);
            }
        }
        else if(q instanceof ContextAwareQuery)
        {
            ContextAwareQuery cq = (ContextAwareQuery)q;
            getPostFilters(cq.getLuceneQuery(), postFilters);
        }
        else if(q instanceof PostFilter)
        {
            postFilters.add((PostFilter)q);
        }
    }

    private class PostFilterComp implements Comparator<PostFilter>
    {
        public int compare(PostFilter a, PostFilter b)
        {
            int costa = a.getCost();
            int costb = b.getCost();
            if(costa == costb)
            {
                return 0;
            }
            else if(costa < costb)
            {
                return -1;
            }
            else
            {
                return 1;
            }
        }
    }
}
