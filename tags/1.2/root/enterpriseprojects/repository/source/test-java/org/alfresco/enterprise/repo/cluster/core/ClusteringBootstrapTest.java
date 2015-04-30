package org.alfresco.enterprise.repo.cluster.core;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.alfresco.enterprise.repo.cache.HibernateCacheProvider;
import org.alfresco.enterprise.repo.cluster.cache.ClusterAwareCacheFactory;
import org.alfresco.enterprise.repo.cluster.lock.ClusterAwareLockStoreFactory;
import org.alfresco.enterprise.repo.cluster.messenger.HazelcastMessengerFactory;
import org.alfresco.service.descriptor.DescriptorService;
import org.alfresco.service.license.LicenseDescriptor;
import org.apache.commons.logging.Log;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.context.ApplicationContext;

@RunWith(MockitoJUnitRunner.class)
public class ClusteringBootstrapTest
{
    /** The class under test */
    private ClusteringBootstrap bootstrap;
    
    private @Mock Log log;
    private @Mock DescriptorService descriptorService;
    private @Mock HazelcastInstanceFactory hazelcastInstanceFactory;
    private @Mock LicenseDescriptor licenseDescriptor;
    private @Mock ClusterService clusterService;
    private @Mock HibernateCacheProvider hibernateCacheProvider;
    private @Mock ClusterAwareCacheFactory<?, ?> cacheFactory;
    private @Mock ClusterAwareLockStoreFactory lockStoreFactory;
    private @Mock HazelcastMessengerFactory messengerFactory;
    private @Mock ApplicationContext applicationContext;
    public static boolean clusterObjectsUpgraded;
    
    
    @Before
    public void setUp() throws Exception
    {
        bootstrap = new ClusteringBootstrapEx();
        bootstrap.setLog(log);
        bootstrap.setDescriptorService(descriptorService);
        bootstrap.setHazelcastInstanceFactory(hazelcastInstanceFactory);        
        bootstrap.setClusterService(clusterService);
        bootstrap.setHibernateCacheProvider(hibernateCacheProvider);
        bootstrap.setCacheFactory(cacheFactory);
        bootstrap.setLockStoreFactory(lockStoreFactory);
        bootstrap.setMessengerFactory(messengerFactory);
        bootstrap.setApplicationContext(applicationContext);
        
        clusterObjectsUpgraded = false;
    }

    @Test
    public void clusteringIsDisabledWithNonClusterLicense()
    {
        when(descriptorService.getLicenseDescriptor()).thenReturn(licenseDescriptor);
        when(licenseDescriptor.isClusterEnabled()).thenReturn(false);

        // License won't be checked unless clustering is enabled to start with, but the second time
        // it will be reported as disabled, since setClustering(false) will have been invoked.
        when(hazelcastInstanceFactory.isClusteringEnabled())
            .thenReturn(true)
            .thenReturn(false);
        
        bootstrap.bootstrapWork();
        
        verify(log).warn(anyString());
        verify(hazelcastInstanceFactory).setClusteringEnabled(false);
        assertFalse("Caches etc. should NOT have been 'upgraded' but were.", clusterObjectsUpgraded);
    }
    
    @Test
    public void clusteringIsDisabledWithoutAnyLicense()
    {
        // No license
        when(descriptorService.getLicenseDescriptor()).thenReturn(null);
        
        // License won't be checked unless clustering is enabled to start with, but the second time
        // it will be reported as disabled, since setClustering(false) will have been invoked.
        when(hazelcastInstanceFactory.isClusteringEnabled())
            .thenReturn(true)
            .thenReturn(false);
        
        bootstrap.bootstrapWork();
        
        verify(log).warn(anyString());
        verify(hazelcastInstanceFactory).setClusteringEnabled(false);
        assertFalse("Caches etc. should NOT have been 'upgraded' but were.", clusterObjectsUpgraded);
    }
    
    @Test
    public void licenseCheckNotPerformedWhenClusteringIsDisabled()
    {
        when(hazelcastInstanceFactory.isClusteringEnabled()).thenReturn(false);
        
        bootstrap.bootstrapWork();
        
        verify(licenseDescriptor, never()).isClusterEnabled();
        verify(log).warn(anyString());
        assertFalse("Caches etc. should NOT have been 'upgraded' but were.", clusterObjectsUpgraded);
    }
    
    @Test
    public void clusteringIsEnabledWithLicense()
    {
        when(descriptorService.getLicenseDescriptor()).thenReturn(licenseDescriptor);
        when(licenseDescriptor.isClusterEnabled()).thenReturn(true);
        // This call should be dependent on whether clusterService.initClusterMethod was called.
        when(clusterService.isInitialised()).thenReturn(true);
        // Clustering is enabled, and will continue to be upon finding valid license.
        when(hazelcastInstanceFactory.isClusteringEnabled()).thenReturn(true);
        
        bootstrap.bootstrapWork();
        
        verify(hibernateCacheProvider).initCacheProvider();
        verify(cacheFactory).setClusterService(clusterService);
        verify(lockStoreFactory).setClusterService(clusterService);
        verify(messengerFactory).setClusterService(clusterService);
        verify(applicationContext).publishEvent(any(ClusterServiceInitialisedEvent.class));
        assertTrue("Caches etc. should have been 'upgraded' but were not.", clusterObjectsUpgraded);
    }
    
    public static class ClusteringBootstrapEx extends ClusteringBootstrap
    {
        @Override
        protected void upgradeClusterObjects()
        {
            clusterObjectsUpgraded = true;
        }
    }
}
