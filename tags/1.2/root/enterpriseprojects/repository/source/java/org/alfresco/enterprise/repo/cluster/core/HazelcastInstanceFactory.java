/*
 * Copyright 2005-2012 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.enterprise.repo.cluster.core;

import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.hazelcast.config.Config;
import com.hazelcast.core.Cluster;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.MembershipListener;

/**
 * Provides a way of lazily creating HazelcastInstances for a given configuration.
 * The HazelcastInstance will not be created until {@link #getInstance()} is called.
 * <p>
 * An intermediary class such as this is required in order to avoid starting
 * Hazelcast instances when clustering is not configured/required. Otherwise
 * simply by defining a HazelcastInstance bean clustering would spring into life.
 * 
 * @author Matt Ward
 */
public class HazelcastInstanceFactory implements ApplicationContextAware
{
    private static final Log log = LogFactory.getLog(HazelcastInstanceFactory.class);
    private HazelcastConfigFactoryBean configFactory;
    private static HazelcastInstance hazelcastInstance;
    /** Guards {@link #configFactory} and {@link #hazelcastInstance} */
    private ReentrantReadWriteLock rwLock = new ReentrantReadWriteLock();
    private boolean clusteringEnabled;
    private ClusterService clusterService;
    private ApplicationContext applicationContext;
    private List<MembershipListener> membershipListeners;
    
    public HazelcastInstance getInstance()
    {
        rwLock.readLock().lock();
        try
        {
            if (hazelcastInstance != null)
            {
                return hazelcastInstance;
            }
            else
            {
                throw new IllegalStateException("Hazelcast instance has not been initialised.");
            }
        }
        finally
        {
            rwLock.readLock().unlock();
        }
    }
    
    protected void initInstance()
    {
        rwLock.writeLock().lock();
        try
        {
            // Check hazelcast hasn't already been initialised.
            if (hazelcastInstance == null)
            {
                Config config;
                try
                {
                    config = configFactory.getConfig();
                }
                catch (Exception error)
                {
                    throw new RuntimeException(error);
                }
                    
                hazelcastInstance = Hazelcast.newHazelcastInstance(config);
                addMembershipListeners();
            }
            else
            {
                log.warn("Hazelcast instance has already been initialised.");
            }
        }
        finally
        {
            rwLock.writeLock().unlock();
        }
    }
    
    protected void addMembershipListeners()
    {
        Cluster cluster = hazelcastInstance.getCluster();
        for (MembershipListener m : membershipListeners)
        {
            cluster.addMembershipListener(m);
        }
    }

    protected void destroyAllInstances()
    {
        rwLock.writeLock().lock();
        try
        {
            Hazelcast.shutdownAll();
            hazelcastInstance = null;
        }
        finally
        {
            rwLock.writeLock().unlock();
        }
    }

    public void setClusteringEnabled(boolean clusteringEnabled)
    {
        this.clusteringEnabled = clusteringEnabled;
    }

    /**
     * Checks whether clustering is enabled.
     * 
     * @return true if clustering is enabled, false otherwise.
     */
    public boolean isClusteringEnabled()
    {
        return clusteringEnabled;
    }
    
    /**
     * Retrieve the name of the cluster for the configuration used by this factory.
     * 
     * @return String - the cluster name.
     */
    public String getClusterName()
    {
        return getClusterService().getClusterName();
    }
    
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException
    {
        this.applicationContext = applicationContext;
    }

    /**
     * Sets the Hazelcast configuration that will be used by this factory when
     * creating the HazelcastInstance.
     * 
     * @param configFactory Hazelcast configuration
     */
    public void setConfigFactory(HazelcastConfigFactoryBean configFactory)
    {
        rwLock.writeLock().lock();
        try
        {
            this.configFactory = configFactory;
        }
        finally
        {
            rwLock.writeLock().unlock();
        }
    }
    
    /**
     * List of {@link MembershipListener}s that should be added to the cluster
     * immediately after creation (and before use).
     * 
     * @param membershipListeners
     */
    public void setMembershipListeners(List<MembershipListener> membershipListeners)
    {   
        this.membershipListeners = membershipListeners;
    }

    private ClusterService getClusterService()
    {
        if (clusterService == null)
        {
            clusterService = (ClusterService) applicationContext.getBean("ClusterService");
        }
        return clusterService;
    }
}
