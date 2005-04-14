/*
 * Created on 13-Apr-2005
 *
 * TODO Comment this class
 * 
 * 
 */
package com.activiti.repo.search.impl.lucene;

import com.activiti.repo.search.Searcher;

public interface LuceneSearcher extends Searcher, Lockable
{
   public boolean indexExists();
}
