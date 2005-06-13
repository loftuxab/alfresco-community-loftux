/*
 * Created on Mar 30, 2005
 * 
 * TODO Comment this class
 * 
 * 
 */
package org.alfresco.repo.search;

import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.search.SearchService;

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
    public abstract SearchService getSearcher(StoreRef storeRef, boolean searchDelta) throws SearcherException;
}
