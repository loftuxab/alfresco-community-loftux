package org.alfresco.solr.query;

import java.io.IOException;

import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Weight;

/**
 * Base class for queries relating to an authority.
 */
public abstract class AbstractAuthorityQuery extends Query
{
    protected String authority;
    
    /**
     * Construct with authority.
     * 
     * @param authority
     */
    public AbstractAuthorityQuery(String authority)
    {
        this.authority = authority;
    }

    /**
     * Subclasses should implement a descriptive toString method.
     */
    @Override
    public abstract String toString();
    
    @Override
    public abstract Weight createWeight(IndexSearcher searcher) throws IOException;
    
    public String toString(String field)
    {
        return toString();
    }
    
    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((authority == null) ? 0 : authority.hashCode());
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
        AbstractAuthorityQuery other = (AbstractAuthorityQuery) obj;
        if (authority == null)
        {
            if (other.authority != null)
                return false;
        }
        else if (!authority.equals(other.authority))
            return false;
        return true;
    }
}
