/*
 * Copyright (C) 2005-2014 Alfresco Software Limited.
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
package org.alfresco.enterprise.repo.management;

import org.alfresco.repo.cache.CacheStatistics;
import org.alfresco.repo.cache.TransactionStats.OpType;

/**
 * Exposes cache statistics for a particular cache for
 * administration via JMX.
 * <p>
 * An instance of this MBean will be registered per cache.
 * 
 * @see CacheStatisticsMBean
 * @since 5.0
 * @author Matt Ward
 */
public class CacheStatisticsMBeanImpl implements CacheStatisticsMBean
{
    private final CacheStatistics cacheStats;
    private final String cacheName;
    
    public CacheStatisticsMBeanImpl(CacheStatistics cacheStats, String cacheName)
    {
        this.cacheStats = cacheStats;
        this.cacheName = cacheName;
    }
    
    @Override
    public double getHitMissRatio()
    {
        return cacheStats.hitMissRatio(cacheName);
    }

    @Override
    public long getGets()
    {
        return cacheStats.numGets(cacheName);
    }

    @Override
    public long getHits()
    {
        return cacheStats.count(cacheName, OpType.GET_HIT);
    }

    @Override
    public double getHitTime()
    {
        return cacheStats.meanTime(cacheName, OpType.GET_HIT);
    }

    @Override
    public long getMisses()
    {
        return cacheStats.count(cacheName, OpType.GET_MISS);
    }

    @Override
    public double getMissTime()
    {
        return cacheStats.meanTime(cacheName, OpType.GET_MISS);
    }

    @Override
    public long getPuts()
    {
        return cacheStats.count(cacheName, OpType.PUT);
    }

    @Override
    public double getPutTime()
    {
        return cacheStats.meanTime(cacheName, OpType.PUT);
    }

    @Override
    public long getRemoves()
    {
        return cacheStats.count(cacheName, OpType.REMOVE);
    }

    @Override
    public double getRemoveTime()
    {
        return cacheStats.meanTime(cacheName, OpType.REMOVE);
    }

    @Override
    public long getClears()
    {
        return cacheStats.count(cacheName, OpType.CLEAR);
    }

    @Override
    public double getClearTime()
    {
        return cacheStats.meanTime(cacheName, OpType.CLEAR);
    }
}
