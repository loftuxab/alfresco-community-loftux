/*
 * Created on 12-Apr-2005
 *
 * TODO Comment this class
 * 
 * 
 */
package com.activiti.repo.search.impl.lucene;

import com.activiti.repo.search.IndexerAndSearcher;
import com.activiti.repo.search.IndexerException;

public interface LuceneIndexerAndSearcher extends IndexerAndSearcher
{

    public int prepare() throws IndexerException;
    public void commit() throws IndexerException;
    public void rollback();
    
}
