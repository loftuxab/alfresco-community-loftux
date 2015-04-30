/*
 * Copyright 2005-2012 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.enterprise.repo.cluster.lock;

import org.alfresco.enterprise.repo.cluster.core.ClusterService;
import org.alfresco.enterprise.repo.cluster.core.ClusteredObjectProxyFactory;
import org.alfresco.enterprise.repo.cluster.core.ClusteringBootstrap;
import org.alfresco.enterprise.repo.cluster.core.HazelcastInstanceFactory;
import org.alfresco.repo.lock.mem.LockState;
import org.alfresco.repo.lock.mem.LockStore;
import org.alfresco.repo.lock.mem.LockStoreFactory;
import org.alfresco.repo.lock.mem.LockStoreImpl;
import org.alfresco.service.cmr.repository.NodeRef;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;


/**
 * Implementation of the {@link LockStoreFactory} interface capable of creating
 * both clustered and non-clustered LockStore objects.
 * <p>
 * The factory creates {@link LockStore}s backed by a Hazelcast distributed Map if clustering is enabled,
 * otherwise it creates a non-clustered {@link SimpleLockStore}.
 * 
 * @see LockStoreFactory
 * @see LockStoreImpl
 * @author Matt Ward
 */
public class ClusterAwareLockStoreFactory implements LockStoreFactory
{
    private static final Log log = LogFactory.getLog(ClusterAwareLockStoreFactory.class);
    private static final String HAZELCAST_MAP_NAME = "lockStore";
    private HazelcastInstanceFactory hazelcastInstanceFactory;
    private ClusterService clusterService;
    
    /**
     * This method should be used sparingly and the created {@link LockStore}s should be
     * retained (this factory does not cache instances of them).
     */
    @Override
    public synchronized LockStore createLockStore()
    {
        LockStore lockStore = null;
        if (hazelcastInstanceFactory.isClusteringEnabled())
        {
            if (clusterService != null && clusterService.isInitialised())
            {
                lockStore = createClusteredLockStore();
            }
            else
            {
                lockStore = createProxiedLockStore();
            }
        }
        else
        {
            lockStore = createNonClusteredLockStore();
        }
        return lockStore;
    }

    private LockStore createNonClusteredLockStore()
    {
        if (log.isDebugEnabled())
        {
            log.debug("Creating non-clustered LockStore.");
        }
        return new LockStoreImpl();
    }

    private LockStore createProxiedLockStore()
    {
        LockStore lockStore = createNonClusteredLockStore();
        LockStore proxy = ClusteredObjectProxyFactory.createLockStoreProxy(lockStore);
        return proxy;
    }

    private LockStore createClusteredLockStore()
    {
        if (log.isDebugEnabled())
        {
            log.debug("Creating clustered LockStore.");
        }
        HazelcastInstance instance = hazelcastInstanceFactory.getInstance();
        IMap<NodeRef, LockState> map = instance.getMap(HAZELCAST_MAP_NAME);
        return new HazelcastLockStore(map);
    }

    /**
     * @param hazelcastInstanceFactory the factory that will create a HazelcastInstance if required.
     */
    public synchronized void setHazelcastInstanceFactory(HazelcastInstanceFactory hazelcastInstanceFactory)
    {
        this.hazelcastInstanceFactory = hazelcastInstanceFactory;
    }

    /**
     * Do NOT set with Spring, see {@link ClusteringBootstrap}.
     * 
     * @param clusterService
     */
    public synchronized void setClusterService(ClusterService clusterService)
    {
        this.clusterService = clusterService;
    }
}
