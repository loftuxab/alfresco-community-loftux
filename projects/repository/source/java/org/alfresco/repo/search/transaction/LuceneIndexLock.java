/*
 * Created on 13-Apr-2005
 *
 * TODO Comment this class
 * 
 * 
 */
package org.alfresco.repo.search.transaction;

import java.util.HashMap;
import java.util.concurrent.locks.ReentrantLock;

import org.alfresco.service.cmr.repository.StoreRef;

public class LuceneIndexLock 
{
    private HashMap<StoreRef, ReentrantLock> locks = new HashMap<StoreRef, ReentrantLock> ();

    public LuceneIndexLock()
    {
        super();
    }
    
    public void getReadLock(StoreRef ref)
    {
      return;
    }
    
    public void releaseReadLock(StoreRef ref)
    {
      return;
    }
    
    public void getWriteLock(StoreRef ref)
    {
        ReentrantLock lock;
        synchronized(locks)
        {
            lock = locks.get(ref);
            if(lock == null)
            {
                lock = new ReentrantLock(true);
                locks.put(ref, lock);
            }
        }
        lock.lock();
    }
    
    public void releaseWriteLock(StoreRef ref)
    {
        ReentrantLock lock;
        synchronized(locks)
        {
            lock = locks.get(ref); 
        }
        if(lock != null)
        {
           lock.unlock();
        }
      
    }
}
