/*
 * Created on 04-May-2005
 *
 * TODO Comment this class
 * 
 * 
 */
package org.alfresco.repo.search.impl.lucene.fts;

import org.alfresco.service.cmr.repository.StoreRef;



public interface FullTextSearchIndexer {

    public abstract void requiresIndex(StoreRef storeRef);

    public abstract void indexCompleted(StoreRef storeRef, int remaining, Exception e);

    public abstract void pause() throws InterruptedException;

    public abstract void resume() throws InterruptedException;

    public abstract void index();

}