package org.alfresco.enterprise.repo.cache;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.verify;

import java.io.Serializable;
import java.util.Properties;

import org.alfresco.repo.cache.CacheFactory;
import org.hibernate.cache.Cache;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class HibernateCacheProviderTest
{
    private HibernateCacheProvider cacheProvider;
    private Properties properties;
    private @Mock CacheFactory<Serializable, Object> clusteredCacheFactory;
    
    @Before
    public void setUp() throws Exception
    {
        cacheProvider = new HibernateCacheProvider();
        cacheProvider.setClusteredCacheFactory(clusteredCacheFactory);
        properties = new Properties();
    }

    @Test
    public void testBuildCacheNoCluster()
    {
        Cache hibernateCache = cacheProvider.buildCache("testRegion", properties);
        assertNotNull(hibernateCache);
        assertEquals("testRegion", hibernateCache.getRegionName());
    }
    
    @Test
    public void testBuildCacheForCluster()
    {
        cacheProvider.initCacheProvider();
        
        Cache hibernateCache = cacheProvider.buildCache("testRegion", properties);
        assertNotNull(hibernateCache);
        assertEquals("testRegion", hibernateCache.getRegionName());
        
        verify(clusteredCacheFactory).createCache("testRegion");
    }

}
