/*
 * Copyright 2005-2012 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.enterprise.repo.cluster.core;

import java.util.Set;

import org.alfresco.enterprise.repo.cache.HibernateCacheProvider;
import org.alfresco.enterprise.repo.cluster.cache.ClusterAwareCacheFactory;
import org.alfresco.enterprise.repo.cluster.lock.ClusterAwareLockStoreFactory;
import org.alfresco.enterprise.repo.cluster.messenger.HazelcastMessengerFactory;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.security.authentication.AuthenticationUtil.RunAsWork;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.repo.transaction.RetryingTransactionHelper.RetryingTransactionCallback;
import org.alfresco.service.descriptor.DescriptorService;
import org.alfresco.service.license.LicenseDescriptor;
import org.alfresco.service.transaction.TransactionService;
import org.alfresco.util.LogUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationEvent;
import org.springframework.extensions.surf.util.AbstractLifecycleBean;
import org.springframework.extensions.surf.util.I18NUtil;

/**
 * Initialises the clustering service.
 *  
 * @author Matt Ward
 */
public class ClusteringBootstrap extends AbstractLifecycleBean
{
    private static final String MSG_CLUSTER_STARTED = "system.cluster.started";
    private static final String MSG_CURRENT_MEMBERS = "system.cluster.curr_members";
    private static final String MSG_MEMBER = "system.cluster.member";
    private static final String MSG_CLUSTER_SHUTDOWN = "system.cluster.shutdown";
    private Log log = LogFactory.getLog(ClusteringBootstrap.class);
    private ClusterService clusterService;
    private ClusterAwareCacheFactory<?, ?> cacheFactory;
    private ClusterAwareLockStoreFactory lockStoreFactory;
    private HazelcastMessengerFactory messengerFactory;
    private HibernateCacheProvider hibernateCacheProvider;
    private TransactionService transactionService;
    private HazelcastInstanceFactory hazelcastInstanceFactory;
    private DescriptorService descriptorService;
    
    @Override
    protected void onBootstrap(ApplicationEvent event)
    {
        // Important to perform this initialization with read/write transaction.
        // In the case of an invalid license, the transaction is made read-only, so
        // we must counteract that to allow clustering to be brought up correctly.
        final RetryingTransactionHelper txnHelper = transactionService.getRetryingTransactionHelper();
        txnHelper.setForceWritable(true);

        final RetryingTransactionCallback<Void> txCallback = new RetryingTransactionCallback<Void>()
        {
            @Override
            public Void execute() throws Throwable
            {
                bootstrapWork();
                return null;
            }
        };

        AuthenticationUtil.runAs(new RunAsWork<Void>()
        {
            public Void doWork() throws Exception
            {
                return txnHelper.doInTransaction(txCallback, false, true);
            }
        }, AuthenticationUtil.getSystemUserName());
        
        
        // Startup summary logging
        if (log.isInfoEnabled() && clusterService.isInitialised())
        {
            LogUtil.info(log, MSG_CLUSTER_STARTED, clusterService.getClusterName());
            Set<RegisteredServerInfoImpl> members = clusterService.getActiveMembers();
            StringBuilder sb = new StringBuilder(I18NUtil.getMessage(MSG_CURRENT_MEMBERS));
            sb.append("\n");
            for (RegisteredServerInfoImpl m : members)
            {
                String ipAndPort = m.getIPAddress() + ":" + m.getPort();
                sb.append("  ").
                   append(I18NUtil.getMessage(MSG_MEMBER, ipAndPort, m.getHostName())).
                   append("\n");
            }
            log.info(sb.toString());
        }
    }
    
    protected void bootstrapWork()
    {
        LicenseDescriptor license = descriptorService.getLicenseDescriptor(); 
        if (!hazelcastInstanceFactory.isClusteringEnabled())
        {
            // Clustering is disabled, no need to check license.
            log.warn(I18NUtil.getMessage("system.cluster.disabled"));
            // Record that this is set to be a non-member.
            clusterService.initNonMember();
        }
        else if (license == null || !license.isClusterEnabled())
        {
            // Clustering is enabled but license does not include clustering.
            log.warn(I18NUtil.getMessage("system.cluster.license.not_enabled"));
            hazelcastInstanceFactory.setClusteringEnabled(false);
        }
        
        
        if (hazelcastInstanceFactory.isClusteringEnabled())
        {
            clusterService.initClusterService();
            hibernateCacheProvider.initCacheProvider();
            // Upgrade the caches to Hazelcast clustered caches.
            cacheFactory.setClusterService(clusterService);
            lockStoreFactory.setClusterService(clusterService);
            messengerFactory.setClusterService(clusterService);
            upgradeClusterObjects();                
        }
        
        // Notify interested parties that the cluster bootstrap process has finished.
        getApplicationContext().publishEvent(new ClusterServiceInitialisedEvent(clusterService));
    }

    protected void upgradeClusterObjects()
    {
        ClusteredObjectProxyFactory.upgradeCaches(cacheFactory, lockStoreFactory, messengerFactory);
    }

    @Override
    protected void onShutdown(ApplicationEvent event)
    {
        final RetryingTransactionHelper txnHelper = transactionService.getRetryingTransactionHelper();
        txnHelper.doInTransaction(new RetryingTransactionCallback<Void>()
        {
            @Override
            public Void execute() throws Throwable
            {
                // Reset cluster service, hazelcast and cluster proxies.
                clusterService.shutDownClusterService();
                hazelcastInstanceFactory.destroyAllInstances();
                ClusteredObjectProxyFactory.clear();
                return null;
            }
        }, true);
        
        if (log.isInfoEnabled() && !clusterService.isInitialised())
        {
            LogUtil.info(log, MSG_CLUSTER_SHUTDOWN);
        }
    }

    public void setClusterService(ClusterService clusterService)
    {
        this.clusterService = clusterService;
    }

    public void setCacheFactory(ClusterAwareCacheFactory<?, ?> cacheFactory)
    {
        this.cacheFactory = cacheFactory;
    }

    public void setLockStoreFactory(ClusterAwareLockStoreFactory lockStoreFactory)
    {
        this.lockStoreFactory = lockStoreFactory;
    }

    public void setMessengerFactory(HazelcastMessengerFactory messengerFactory)
    {
        this.messengerFactory = messengerFactory;
    }

    public void setHibernateCacheProvider(HibernateCacheProvider hibernateCacheProvider)
    {
        this.hibernateCacheProvider = hibernateCacheProvider;
    }

    public void setTransactionService(TransactionService transactionService)
    {
        this.transactionService = transactionService;
    }

    public void setHazelcastInstanceFactory(HazelcastInstanceFactory hazelcastInstanceFactory)
    {
        this.hazelcastInstanceFactory = hazelcastInstanceFactory;
    }

    public void setDescriptorService(DescriptorService descriptorService)
    {
        this.descriptorService = descriptorService;
    }
    
    /**
     * Inject a logger. Intended to allow injecting a mock during testing.
     * 
     * @param log
     */
    protected void setLog(Log log)
    {
        this.log = log;
    }
}
