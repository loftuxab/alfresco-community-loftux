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

import java.util.Collections;
import java.util.Map;

import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import org.alfresco.repo.cache.CacheStatistics;
import org.alfresco.repo.cache.CacheStatisticsCreated;

/**
 * MBean exporter for CacheStatisticsMBean instances.
 * 
 * @since 5.0
 * @author Matt Ward
 */
public class CacheStatisticsExporter extends AbstractManagedResourceExporter<CacheStatisticsCreated>
{
    public CacheStatisticsExporter()
    {
        super(CacheStatisticsCreated.class);
    }

    @Override
    public Map<ObjectName, ?> getObjectsToExport(CacheStatisticsCreated event)
                throws MalformedObjectNameException
    {
        String cacheName = event.getCacheName();
        CacheStatistics cacheStats = event.getCacheStats();
        CacheStatisticsMBeanImpl cacheStatsMBean = new CacheStatisticsMBeanImpl(cacheStats, cacheName);
        ObjectName objectName = new ObjectName("Alfresco:Name=CacheStatistics,CacheName="+cacheName);
        return Collections.singletonMap(objectName, cacheStatsMBean);
    }
}
