/*
 * Created on 13-Apr-2005
 *
 * TODO Comment this class
 * 
 * 
 */
package org.alfresco.repo.search.impl.lucene;

import org.alfresco.repo.search.transaction.LuceneIndexLock;

public interface Lockable
{
    public void setLuceneIndexLock(LuceneIndexLock luceneIndexLock);
    
    public LuceneIndexLock getLuceneIndexLock();
    
    public void getReadLock();
    
    public void releaseReadLock();
   
    public void getWriteLock();
    
    public void releaseWriteLock();
}
