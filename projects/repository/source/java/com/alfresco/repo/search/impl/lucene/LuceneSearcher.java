/*
 * Created on 13-Apr-2005
 *
 * TODO Comment this class
 * 
 * 
 */
package org.alfresco.repo.search.impl.lucene;

import org.alfresco.repo.search.Searcher;

public interface LuceneSearcher extends Searcher, Lockable
{
   public boolean indexExists();
}
