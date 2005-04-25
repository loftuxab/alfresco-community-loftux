/*
 * Created on 22-Apr-2005
 *
 * TODO Comment this class
 * 
 * 
 */
package com.activiti.repo.search.impl.lucene.fts;

import com.activiti.repo.ref.StoreRef;

public interface FTSIndexerAware
{

    public void indexCompleted(StoreRef storeRef, int remaining, Exception e);
}
