/*
 * Created on Mar 30, 2005
 * 
 * TODO Comment this class
 * 
 * 
 */
package org.alfresco.repo.search;

import java.util.HashMap;
import java.util.Map;

import org.alfresco.repo.ref.StoreRef;

/**
 * Factory to find indexers based on store id.
 * 
 * IndexerAndSerachers are registered against the protocol or store.
 */
public class IndexerAndSearcherFactory implements IndexerAndSearcher
{
    /**
     * IndexersAndSearchers registered by protocol
     */
    private Map<String, IndexerAndSearcher> protocolFactories = new HashMap<String, IndexerAndSearcher>();

    /**
     * IndexersAndSearchers registered by store. These take precedence over
     * protocol registrations
     */
    private Map<StoreRef, IndexerAndSearcher> storeFactories = new HashMap<StoreRef, IndexerAndSearcher>();

    /**
     * Singleton
     * 
     */
    private IndexerAndSearcherFactory()
    {
        super();
    }

    /**
     * Regsiter a factory against a protcol
     * 
     * @param protocol
     * @param factory
     */
    public synchronized void registerFactory(String protocol, IndexerAndSearcher factory)
    {
        protocolFactories.put(protocol, factory);
    }

    /**
     * Register a factory against a store
     * 
     * @param storeRef
     * @param factory
     */
    public synchronized void registerFactory(StoreRef storeRef, IndexerAndSearcher factory)
    {
        storeFactories.put(storeRef, factory);
    }

    /**
     * Get the factory to use for a given store
     * 
     * @param storeRef
     * @return
     */
    private synchronized IndexerAndSearcher getFactory(StoreRef storeRef)
    {
        IndexerAndSearcher ias = storeFactories.get(storeRef);
        if (ias != null)
        {
            return ias;
        }

        ias = protocolFactories.get(storeRef.getProtocol());
        if (ias != null)
        {
            return ias;
        }

        throw new IndexerAndSearcherFactoryException("There is no IndexAndSearcherFactory registered for store " + storeRef);
    }

    /**
     * Get an indexer
     */
    public Indexer getIndexer(StoreRef storeRef) throws IndexerException
    {
        IndexerAndSearcher ias = getFactory(storeRef);
        return ias.getIndexer(storeRef);
    }

    /**
     * Get a store
     */
    public Searcher getSearcher(StoreRef storeRef, boolean searchDelta) throws SearcherException
    {
        IndexerAndSearcher ias = getFactory(storeRef);
        return ias.getSearcher(storeRef, searchDelta);
    }

    public void setProtocolFactories(Map<String, IndexerAndSearcher> protocolFactories)
    {
        this.protocolFactories = protocolFactories;
    }

}
