/*
 * Created on 18-Apr-2005
 *
 * TODO Comment this class
 * 
 * 
 */
package com.activiti.repo.search.impl.lucene.fts;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.activiti.repo.ref.StoreRef;
import com.activiti.repo.search.impl.lucene.LuceneIndexer;
import com.activiti.repo.search.impl.lucene.LuceneIndexerAndSearcherFactory;

public class FullTextSearchIndexer implements FTSIndexerAware
{
    private static Set<StoreRef> requiresIndex = new LinkedHashSet<StoreRef>();
    private static Set<StoreRef> indexing = new HashSet<StoreRef>();
    
    LuceneIndexerAndSearcherFactory luceneIndexerAndSearcherFactory;
    
    public FullTextSearchIndexer()
    {
        super();
    }

    public synchronized void requiresIndex(StoreRef storeRef)
    {
        requiresIndex.add(storeRef);
    }
    
    public synchronized void indexCompleted(StoreRef storeRef, int remaining, Exception e) 
    {
        indexing.remove(storeRef);
        if((remaining > 0) || (e != null))
        {
            requiresIndex(storeRef);
        }
        if(e != null)
        {
            throw new FTSIndexerException(e);
        }
    }
    
    public void index()
    {
        StoreRef toIndex = getNextRef();
        if(toIndex != null)
        {
            LuceneIndexer indexer = luceneIndexerAndSearcherFactory.getIndexer(toIndex);
            indexer.registerCallBack(this);
            indexer.updateFullTextSearch(1000);
        }
    }
    
    private synchronized StoreRef getNextRef()
    {
        // start indexing those not already indexing
        
        StoreRef nextStoreRef = null;
        
        for(StoreRef ref: requiresIndex)
        {
            if(!indexing.contains(ref))
            {
                nextStoreRef = ref;
            }
        }
        
        if(nextStoreRef != null)
        {
           requiresIndex.remove(nextStoreRef);
           indexing.add(nextStoreRef);
        }
       
        return nextStoreRef;
    }
    
    
    
    public void setLuceneIndexerAndSearcherFactory(LuceneIndexerAndSearcherFactory luceneIndexerAndSearcherFactory)
    {
        this.luceneIndexerAndSearcherFactory = luceneIndexerAndSearcherFactory;
    }
    

    public static void main(String[] args) throws InterruptedException
    {
        ApplicationContext ctx = new ClassPathXmlApplicationContext("classpath:applicationContext.xml");
    }
}
