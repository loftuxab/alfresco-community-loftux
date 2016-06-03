package org.alfresco.solr;

import org.apache.solr.search.FastLRUCache;

/**
 * @author Andy
 *
 */
public class FilteringFastLRUCache extends  FastLRUCache
{

   


    /**
     * @param key Object
     * @param value Object
     * @return Object
     * @see org.apache.solr.search.SolrCache#put(java.lang.Object, java.lang.Object)
     */
    public Object put(Object key, Object value)
    {
        if(key instanceof ContextAwareQuery)
        {
             return super.put(key, value);
        }
        else
        {
            return null;
        }
    }

    /* (non-Javadoc)
     * @see org.apache.solr.search.FastLRUCache#get(java.lang.Object)
     */
    @Override
    public Object get(Object key)
    {
        if(key instanceof ContextAwareQuery)
        {
             return super.get(key);
        }
        else
        {
            return null;
        }
    }

    
}
