/*
 * Created on 04-May-2005
 *
 * TODO Comment this class
 * 
 * 
 */
package com.activiti.repo.search.impl.lucene.fts;

import com.activiti.repo.ref.StoreRef;



public interface FullTextSearchIndexer {

    public abstract void requiresIndex(StoreRef storeRef);

    public abstract void indexCompleted(StoreRef storeRef, int remaining, Exception e);

    public abstract void pause() throws InterruptedException;

    public abstract void resume() throws InterruptedException;

    public abstract void index();

}