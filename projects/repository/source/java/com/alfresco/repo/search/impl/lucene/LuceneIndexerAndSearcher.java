/*
 * Created on 12-Apr-2005
 *
 * TODO Comment this class
 * 
 * 
 */
package org.alfresco.repo.search.impl.lucene;

import org.alfresco.repo.search.IndexerAndSearcher;
import org.alfresco.repo.search.IndexerException;

public interface LuceneIndexerAndSearcher extends IndexerAndSearcher
{

    public int prepare() throws IndexerException;
    public void commit() throws IndexerException;
    public void rollback();
    
}
