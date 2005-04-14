/*
 * Created on 13-Apr-2005
 *
 * TODO Comment this class
 * 
 * 
 */
package com.activiti.repo.search.impl.lucene;

import java.util.concurrent.locks.ReentrantLock;

import com.activiti.repo.ref.StoreRef;
import com.activiti.repo.search.transaction.LuceneIndexLock;

public interface Lockable
{
    public void setLuceneIndexLock(LuceneIndexLock luceneIndexLock);
    
    public LuceneIndexLock getLuceneIndexLock();
    
    public void getReadLock();
    
    public void releaseReadLock();
   
    public void getWriteLock();
    
    public void releaseWriteLock();
}
