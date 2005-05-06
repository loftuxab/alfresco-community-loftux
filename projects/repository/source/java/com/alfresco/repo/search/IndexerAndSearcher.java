/*
 * Created on Mar 30, 2005
 * 
 * TODO Comment this class
 * 
 * 
 */
package org.alfresco.repo.search;

import org.alfresco.repo.ref.StoreRef;

/**
 * Interface for Indexer and Searcher Factories to implement
 * 
 * @author andyh
 * 
 */
public interface IndexerAndSearcher
{
    /**
     * Get an indexer for a store
     * 
     * @param storeRef
     * @return
     * @throws IndexerException
     */
    public abstract Indexer getIndexer(StoreRef storeRef) throws IndexerException;

    /**
     * Get a searcher for a store
     * 
     * @param storeRef
     * @param searchDelta -
     *            serach the in progress transaction as well as the main index
     *            (this is ignored for searches that do full text)
     * @return
     * @throws SearcherException
     */
    public abstract Searcher getSearcher(StoreRef storeRef, boolean searchDelta) throws SearcherException;
}
