/*
 * Created on 13-Apr-2005
 *
 * TODO Comment this class
 * 
 * 
 */
package com.activiti.repo.search.impl.lucene;


import com.activiti.repo.dictionary.DictionaryService;
import com.activiti.repo.node.NodeService;
import com.activiti.repo.search.Indexer;

public interface LuceneIndexer extends Indexer, Lockable
{

    public void commit();
    public void rollback();
    public int prepare();
    public boolean isModified();
    public void setNodeService(NodeService nodeService);
    public void setDictionaryService(DictionaryService dictionaryService);
}
