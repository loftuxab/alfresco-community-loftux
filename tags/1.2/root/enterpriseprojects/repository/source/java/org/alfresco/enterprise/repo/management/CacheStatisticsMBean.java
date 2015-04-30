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


/**
 * Management interface for cache statistics.
 * 
 * @since 5.0
 * @author Matt Ward
 */
public interface CacheStatisticsMBean
{
    /**
     * The hit ratio for the given cache, where 1.0 is the maximum possible value indicating that every single
     * request for a value has been honoured (i.e. all gets are "hits") and 0.0 represents a cache that has never
     * successfully returned a previously cached value (i.e. all gets are "misses"). 
     */
    double getHitMissRatio();

    /**
     * The number of times that the cache has had a value requested from it - this includes cache hits
     * (where the cache contains a value) and misses where the cache reports it has no value corresponding
     * to a particular key).
     */
    long getGets();
    
    /**
     * The number of hits on a cache (see gets).
     */
    long getHits();
    
    /**
     * The mean time (nanoseconds) for get operations where a value has been found in the cache.
     */
    double getHitTime();
    
    /**
     * The number of misses on a cache (see gets).
     */
    long getMisses();
    
    /**
     * The mean time (nanoseconds) for gets resulting in a miss.
     */
    double getMissTime();
    
    /**
     * The number of put operations applied to the cache.
     */
    long getPuts();
    
    /**
     * The mean time (nanoseconds) for inserting a value into the cache.
     */
    double getPutTime();
    
    /**
     * The number of removal operations applied to the cache (i.e. where a value is removed from the cache).
     */
    long getRemoves();
    
    /**
     * The mean time (nanoseconds) that remove operations have taken for the cache.
     */
    double getRemoveTime();

    /**
     * The number of times that the cache has been cleared (i.e emptied, or "dropped").
     */
    long getClears();
    
    /**
     * The mean time (nanoseconds) for clear operations on the cache (i.e emptying or
     * "dropping" the entire cache contents).
     */
    double getClearTime();
}
