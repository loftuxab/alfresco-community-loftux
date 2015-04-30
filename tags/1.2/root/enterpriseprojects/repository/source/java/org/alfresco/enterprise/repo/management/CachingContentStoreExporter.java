/*
 * Copyright 2005-2011 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.enterprise.repo.management;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import org.alfresco.repo.content.caching.CachingContentStoreCreatedEvent;
import org.alfresco.repo.content.caching.CachingContentStoreEvent;
import org.alfresco.repo.content.caching.cleanup.CachedContentCleanerCreatedEvent;
import org.alfresco.repo.content.caching.quota.StandardQuotaStrategy;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * MBean exporter for resources relating to the CachingContentStore.
 * 
 * @author Matt Ward
 */
public class CachingContentStoreExporter extends AbstractManagedResourceExporter<CachingContentStoreEvent>
{
    private static final String MBEAN_PREFIX = "Alfresco:Name=CachingContentStores";
    private static final Log log = LogFactory.getLog(CachingContentStoreExporter.class);
    
    /**
     * Constructor. Registers the base class of events that will be handled by this exporter.
     */
    public CachingContentStoreExporter()
    {
        super(CachingContentStoreEvent.class);
    }

    
    @Override
    public Map<ObjectName, ?> getObjectsToExport(CachingContentStoreEvent e)
                throws MalformedObjectNameException
    {
        Exports exports = new Exports();
        
        
        if (e.isType(CachingContentStoreCreatedEvent.class))
        {
            CachingContentStoreCreatedEvent event = (CachingContentStoreCreatedEvent) e;    
            org.alfresco.repo.content.caching.CachingContentStore cachingContentStore = event.getCachingContentStore();
               
            String prefix = new StringBuffer(100)
                .append(MBEAN_PREFIX)
                .append(",BeanName=").append(cachingContentStore.getBeanName())
                .toString();
            
            // Add the main MBean node for the CachingContentStore instance
            exports.put(prefix + ",Component=CachingContentStore", new CachingContentStore(cachingContentStore));
            
            // Add a QuotaManager child node
            org.alfresco.repo.content.caching.quota.QuotaManagerStrategy quota = cachingContentStore.getQuota();
            if (quota instanceof org.alfresco.repo.content.caching.quota.StandardQuotaStrategy)
            {
                exports.put(prefix + ",Component=QuotaManager", new QuotaManagerStrategy((StandardQuotaStrategy) quota));
            }
            
            // Add a ContentCache child node
            exports.put(prefix + ",Component=ContentCache", new ContentCache(cachingContentStore.getCache()));
        }
        if (e.isType(CachedContentCleanerCreatedEvent.class))
        {
            CachedContentCleanerCreatedEvent event = (CachedContentCleanerCreatedEvent) e;
            org.alfresco.repo.content.caching.cleanup.CachedContentCleaner cleaner = event.getCleaner();
            
            String objName = new StringBuffer(100)
                .append(MBEAN_PREFIX)
                .append(",Type=").append(cleaner.getClass().getSimpleName())
                .append(",CacheRoot=" + safeNameForPath(cleaner.getCacheRoot())).toString();
            
            exports.put(objName, new CachedContentCleaner(cleaner));
        }
        
        return exports.asMap();
    }
    
    
    
    /**
     * Translates colon characters to pipe characters - stops windows paths from causing
     * MalformedObjectNameException errors being thrown when creating MBean names.
     * 
     * @param cacheRoot
     * @return Safe path for use in MBean name.
     */
    private String safeNameForPath(File file)
    {
        return file.getAbsolutePath().replace(':', '|');
    }



    private static class Exports
    {        
        Map<ObjectName, Object> map = new HashMap<ObjectName, Object>();

        public void put(String name, Object object)
        {
            put(objectName(name), object);
        }
        
        private void put(ObjectName name, Object object)
        {
            if (log.isDebugEnabled())
            {
                log.debug("Registering managed resource: name=" + name + ", object=" + object.getClass().getName());
            }
            map.put(name, object);
        }
        
        private static ObjectName objectName(String name)
        {
            try
            {
                return new ObjectName(name);
            }
            catch (MalformedObjectNameException error)
            {
                throw new RuntimeException("Unable to create ObjectName for [" + name + "]", error);
            }
        }
        
        public Map<ObjectName, Object> asMap()
        {
            return map;
        }
    }
}
