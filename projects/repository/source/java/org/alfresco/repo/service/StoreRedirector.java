package org.alfresco.repo.service;

import java.util.Collection;

import org.alfresco.repo.ref.StoreRef;

public interface StoreRedirector
{
    /**
     * Get the names of the protocols supported
     * @return
     */
    public Collection<String> getSupportedStoreProtocols();
    
    /**
     * Get the StoreRefs of the stores suported
     * @return
     */
    public Collection<StoreRef> getSupportedStores();
}
