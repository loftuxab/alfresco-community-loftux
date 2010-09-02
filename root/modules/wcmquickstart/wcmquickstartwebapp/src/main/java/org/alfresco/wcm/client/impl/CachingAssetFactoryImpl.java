/*
 * Copyright (C) 2005-2010 Alfresco Software Limited.
 *
 * This file is part of Alfresco
 *
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */
package org.alfresco.wcm.client.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.alfresco.wcm.client.Asset;
import org.alfresco.wcm.client.AssetFactory;
import org.alfresco.wcm.client.Query;
import org.alfresco.wcm.client.Rendition;
import org.alfresco.wcm.client.SearchResults;
import org.alfresco.wcm.client.impl.cache.SimpleCache;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class CachingAssetFactoryImpl implements AssetFactory
{
    private static final Log log = LogFactory.getLog(CachingAssetFactoryImpl.class);
    
    private AssetFactory delegate;
    private SimpleCache<String, CacheEntry> newCache;

    public void setDelegate(AssetFactory delegate)
    {
        this.delegate = delegate;
    }

    public void setCache(SimpleCache<String, CacheEntry> newCache)
    {
        this.newCache = newCache;
    }

    public SearchResults findByQuery(Query query)
    {
        return delegate.findByQuery(query);
    }

    public Asset getAssetById(String id, boolean deferredLoad)
    {
        long time = System.currentTimeMillis() - 30000L;
        CacheEntry cacheEntry = newCache.get(id);
        Asset asset = null;
        if (cacheEntry != null)
        {
            asset = cacheEntry.asset;
            if (cacheEntry.cacheTime < time)
            {
                Date currentModifiedTime = delegate.getModifiedTimeOfAsset(id);
                Date cachedModifiedTime = (Date)asset.getProperty(Asset.PROPERTY_MODIFIED_TIME);
                if (currentModifiedTime.after(cachedModifiedTime))
                {
                    newCache.remove(id);
                    asset = null;
                }
                else
                {
                    long oldTime = cacheEntry.cacheTime;
                    cacheEntry.refresh();
                    long newTime = newCache.get(id).cacheTime;
                    if (newTime == oldTime)
                    {
                        log.error("!!!!!!!!!! Cache time not updated !!!!!!!!!!!");
                    }
                }
            }
        }
        if (asset == null)
        {
            asset = delegate.getAssetById(id, deferredLoad);
            newCache.put(id, new CacheEntry(asset));
        }
        return asset;
    }

    public Asset getAssetById(String id)
    {
        return getAssetById(id, false);
    }

    public List<Asset> getAssetsById(Collection<String> ids, boolean deferredLoad)
    {
        List<String> idsToLoad = new ArrayList<String>(ids.size());
        Map<String,Asset> assetsToCheck = new TreeMap<String, Asset>();
        Map<String, Asset> foundAssets = new TreeMap<String, Asset>();
        
        long time = System.currentTimeMillis() - 30000L;
        for (String id : ids)
        {
            CacheEntry cacheEntry = newCache.get(id);
            if (cacheEntry != null)
            {
                if (cacheEntry.cacheTime < time)
                {
                    assetsToCheck.put(id, cacheEntry.asset);
                }
                else
                {
                    foundAssets.put(id, cacheEntry.asset);
                }
            }
            else
            {
                idsToLoad.add(id);
            }
        }

        //Check the modified time of those assets found in the cache
        if (!assetsToCheck.isEmpty())
        {
            Map<String,Date> currentModifiedTimes = delegate.getModifiedTimesOfAssets(assetsToCheck.keySet());
            for (Map.Entry<String,Date> currentAssetModifiedTime : currentModifiedTimes.entrySet())
            {
                String assetId = currentAssetModifiedTime.getKey();
                Asset cachedAsset = assetsToCheck.get(assetId);
                Date currentModifiedTime = currentAssetModifiedTime.getValue();
                Date cachedModifiedTime = (Date)cachedAsset.getProperty(Asset.PROPERTY_MODIFIED_TIME);
                if (currentModifiedTime.after(cachedModifiedTime))
                {
                    newCache.remove(assetId);
                    idsToLoad.add(assetId);
                }
                else
                {
                    foundAssets.put(assetId, cachedAsset);
                    CacheEntry cachedEntry = newCache.get(assetId);
                    if (cachedEntry != null)
                    {
                        long oldTime = cachedEntry.cacheTime;
                        cachedEntry.refresh();
                        long newTime = newCache.get(assetId).cacheTime;
                        if (newTime == oldTime)
                        {
                            log.error("!!!!!!!!!! Cache time not updated !!!!!!!!!!!");
                        }
                    }
                }
            }
        }
        
        //Load any that we haven't found in the cache (or that have been modified since being cached)
        if (!idsToLoad.isEmpty())
        {
            List<Asset> assets = delegate.getAssetsById(idsToLoad, deferredLoad);
            for (Asset asset : assets)
            {
                foundAssets.put(asset.getId(), asset);
                newCache.put(asset.getId(), new CacheEntry(asset));
            }
        }
        
        //Try to retain the correct order where possible...
        List<Asset> finalResults = new ArrayList<Asset>(foundAssets.size());
        for (String id : ids)
        {
            Asset asset = foundAssets.get(id);
            if (asset != null)
            {
                finalResults.add(asset);
            }
        }
        
        return finalResults;
    }

    public List<Asset> getAssetsById(Collection<String> ids)
    {
        return getAssetsById(ids, false);
    }

    public Date getModifiedTimeOfAsset(String assetId)
    {
        return delegate.getModifiedTimeOfAsset(assetId);
    }

    public Map<String, Date> getModifiedTimesOfAssets(Collection<String> assetIds)
    {
        return delegate.getModifiedTimesOfAssets(assetIds);
    }

    public Map<String, Rendition> getRenditions(String assetId)
    {
        return delegate.getRenditions(assetId);
    }

    public Asset getSectionAsset(String sectionId, String assetName, boolean wildcardsAllowedInName)
    {
        return delegate.getSectionAsset(sectionId, assetName, wildcardsAllowedInName);
    }

    public Asset getSectionAsset(String sectionId, String assetName)
    {
        return delegate.getSectionAsset(sectionId, assetName);
    }

    public Map<String, List<String>> getSourceRelationships(String assetId)
    {
        return delegate.getSourceRelationships(assetId);
    }
    
    private static class CacheEntry
    {
        public long cacheTime;
        public final Asset asset;
        
        public CacheEntry(Asset asset)
        {
            this.asset = asset;
            refresh();
        }
        
        public void refresh()
        {
            this.cacheTime = System.currentTimeMillis();
        }
    }
}
