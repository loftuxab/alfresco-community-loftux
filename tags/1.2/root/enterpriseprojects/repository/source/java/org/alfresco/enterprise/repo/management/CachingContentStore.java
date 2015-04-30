/*
 * Copyright 2005-2011 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.enterprise.repo.management;

/**
 * CachingContentStoreMBean implementation.
 * 
 * @author Matt Ward
 */
public class CachingContentStore implements CachingContentStoreMBean
{
    private org.alfresco.repo.content.caching.CachingContentStore cachingContentStore;

    /**
     * @param cachingContentStore
     */
    public CachingContentStore(
                org.alfresco.repo.content.caching.CachingContentStore cachingContentStore)
    {
        this.cachingContentStore = cachingContentStore;
    }

    @Override
    public String getBackingStoreType()
    {
        return cachingContentStore.getBackingStoreType();
    }

    @Override
    public String getBackingStoreDescription()
    {
        return cachingContentStore.getBackingStoreDescription();
    }

    @Override
    public String getBackingStoreRootLocation()
    {
        return cachingContentStore.getRootLocation();
    }

    @Override
    public void setCacheOnInbound(boolean cacheOnInbound)
    {
        cachingContentStore.setCacheOnInbound(cacheOnInbound);
    }
    
    @Override
    public boolean isCacheOnInbound()
    {
        return cachingContentStore.isCacheOnInbound();
    }

    @Override
    public int getMaxCacheTries()
    {
        return this.cachingContentStore.getMaxCacheTries();
    }

    @Override
    public void setMaxCacheTries(int maxCacheTries)
    {
        this.cachingContentStore.setMaxCacheTries(maxCacheTries);
    }
}
