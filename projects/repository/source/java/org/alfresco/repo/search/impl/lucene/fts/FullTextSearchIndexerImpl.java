/*
 * Created on 18-Apr-2005
 * 
 * TODO Comment this class
 * 
 * 
 */
package org.alfresco.repo.search.impl.lucene.fts;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import org.alfresco.repo.ref.StoreRef;
import org.alfresco.repo.search.impl.lucene.LuceneIndexer;
import org.alfresco.repo.search.impl.lucene.LuceneIndexerAndSearcherFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class FullTextSearchIndexerImpl implements FTSIndexerAware, FullTextSearchIndexer
{
    private enum State {
        ACTIVE, PAUSING, PAUSED
    };

    private static Set<StoreRef> requiresIndex = new LinkedHashSet<StoreRef>();

    private static Set<StoreRef> indexing = new HashSet<StoreRef>();

    LuceneIndexerAndSearcherFactory luceneIndexerAndSearcherFactory;

    private int pauseCount = 0;

    private boolean paused = false;

    public FullTextSearchIndexerImpl()
    {
        super();
        //System.out.println("Created id is "+this);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.alfresco.repo.search.impl.lucene.fts.FullTextSearchIndexer#requiresIndex(org.alfresco.repo.ref.StoreRef)
     */
    public synchronized void requiresIndex(StoreRef storeRef)
    {
        requiresIndex.add(storeRef);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.alfresco.repo.search.impl.lucene.fts.FullTextSearchIndexer#indexCompleted(org.alfresco.repo.ref.StoreRef,
     *      int, java.lang.Exception)
     */
    public synchronized void indexCompleted(StoreRef storeRef, int remaining, Exception e)
    {
        try
        {
            indexing.remove(storeRef);
            if ((remaining > 0) || (e != null))
            {
                requiresIndex(storeRef);
            }
            if (e != null)
            {
                throw new FTSIndexerException(e);
            }
        }
        finally
        {
            //System.out.println("..Index Complete: id is "+this);
            this.notifyAll();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.alfresco.repo.search.impl.lucene.fts.FullTextSearchIndexer#pause()
     */
    public synchronized void pause() throws InterruptedException
    {
        pauseCount++;
        //System.out.println("..Waiting "+pauseCount+" id is "+this);
        while ((indexing.size() > 0))
        {
            //System.out.println("Pause: Waiting with count of "+indexing.size()+" id is "+this);
            this.wait();
        }
        pauseCount--;
        if(pauseCount == 0)
        {
            paused = true;
            this.notifyAll(); // only resumers
        }
        //System.out.println("..Remaining "+pauseCount +" paused = "+paused+" id is "+this);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.alfresco.repo.search.impl.lucene.fts.FullTextSearchIndexer#resume()
     */
    public synchronized void resume() throws InterruptedException
    {
        if(pauseCount == 0)
        {
            //System.out.println("Direct resume"+" id is "+this);
            paused = false;
        }
        else 
        {
            while(pauseCount > 0)
            {
                //System.out.println("Reusme waiting on "+pauseCount+" id is "+this);
                this.wait();
            }
            paused = false;
        }
    }

    private synchronized boolean isPaused() throws InterruptedException
    {
        if(pauseCount == 0)
        {
           return paused;
        }
        else 
        {
            while(pauseCount > 0)
            {
                this.wait();
            }
            return paused;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.alfresco.repo.search.impl.lucene.fts.FullTextSearchIndexer#index()
     */
    public void index()
    {
        // Use the calling thread to index
        // Parallel indexing via multiple Quartz thread initiating indexing

        StoreRef toIndex = getNextRef();
        if (toIndex != null)
        {
            //System.out.println("Indexing "+toIndex+" id is "+this);
            LuceneIndexer indexer = luceneIndexerAndSearcherFactory.getIndexer(toIndex);
            indexer.registerCallBack(this);
            indexer.updateFullTextSearch(1000);
        }
        else
        {
            //System.out.println("Nothing to index"+" id is "+this);
        }
    }

    private synchronized StoreRef getNextRef()
    {
        if (paused || (pauseCount > 0))
        {
            //System.out.println("Indexing suspended"+" id is "+this);
            return null;
        }

        StoreRef nextStoreRef = null;

        for (StoreRef ref : requiresIndex)
        {
            if (!indexing.contains(ref))
            {
                nextStoreRef = ref;
            }
        }

        if (nextStoreRef != null)
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
