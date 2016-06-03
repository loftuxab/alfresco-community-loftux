package org.alfresco.solr.query;

import java.io.IOException;

import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Weight;
import org.apache.solr.search.SolrIndexSearcher;

/**
 * Decorator that executes a SolrPathQuery and returns cached results where possible.
 * 
 * @author Andy
 */
public class SolrCachingPathQuery extends Query
{
    SolrPathQuery pathQuery;

    public SolrCachingPathQuery(SolrPathQuery pathQuery)
    {
        this.pathQuery = pathQuery;
    }
    
    /*
     * @see org.apache.lucene.search.Query#createWeight(org.apache.lucene.search.Searcher)
     */
    public Weight createWeight(IndexSearcher searcher) throws IOException
    {
        if(!(searcher instanceof SolrIndexSearcher))
        {
            throw new IllegalStateException("Must have a SolrIndexSearcher");
        }
        return new SolrCachingPathWeight(this, (SolrIndexSearcher)searcher);
    }

    /*
     * @see java.lang.Object#toString()
     */
    public String toString()
    {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("CACHED -> :");
        stringBuilder.append(pathQuery.toString());
        return stringBuilder.toString();
    }

    /*
     * @see org.apache.lucene.search.Query#toString(java.lang.String)
     */
    public String toString(String field)
    {
        return toString();
    }

    
    
    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((pathQuery == null) ? 0 : pathQuery.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        SolrCachingPathQuery other = (SolrCachingPathQuery) obj;
        if (pathQuery == null)
        {
            if (other.pathQuery != null)
                return false;
        }
        else if (!pathQuery.equals(other.pathQuery))
            return false;
        return true;
    }
}
