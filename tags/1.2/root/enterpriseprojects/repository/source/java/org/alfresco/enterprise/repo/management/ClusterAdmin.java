package org.alfresco.enterprise.repo.management;

import java.util.Collections;
import java.util.Set;

import javax.management.openmbean.TabularData;

import org.alfresco.enterprise.repo.cluster.core.ClusterService;
import org.alfresco.enterprise.repo.cluster.core.RegisteredServerInfoImpl;
import org.alfresco.enterprise.repo.cluster.core.ServerNotFoundException;
import org.alfresco.repo.transaction.RetryingTransactionHelper.RetryingTransactionCallback;

/**
 * {@link ClusterAdminMBean} implementation.
 * 
 * @author Matt Ward
 */
public class ClusterAdmin extends MBeanSupport implements ClusterAdminMBean
{
    private ClusterService clusterService;
    
    @Override
    public String getClusterName()
    {
        String clusterName = doWork(new RetryingTransactionCallback<String>()
        {
            @Override
            public String execute() throws Throwable
            {
                return clusterService.getClusterName();
            }
        }, true);
        
        return clusterName;
    }

    @Override
    public String getHostName()
    {
        return clusterService.getMemberHostName();
    }

    @Override
    public String getIPAddress()
    {
        return clusterService.getMemberIP();
    }

    @Override
    public boolean isClusteringEnabled()
    {
        return clusterService.isClusteringEnabled();
    }

    @Override
    public int getNumClusterMembers()
    {
        return clusterService.getNumActiveClusterMembers();
    }

    @Override
    public TabularData getClusterMembers()
    {
        Set<RegisteredServerInfoImpl> clusterMembers = doWork(new RetryingTransactionCallback<Set<RegisteredServerInfoImpl>>()
        {
            @Override
            public Set<RegisteredServerInfoImpl> execute() throws Throwable
            {
                return clusterService.getActiveMembers();
            }
        }, true);
        
        TabularData tabularData = ClusterMemberType.tabularData(clusterMembers);
        return tabularData;
    }

    @Override
    public TabularData getOfflineMembers()
    {
        Set<RegisteredServerInfoImpl> offlineMembers = doWork(new RetryingTransactionCallback<Set<RegisteredServerInfoImpl>>()
        {
            @Override
            public Set<RegisteredServerInfoImpl> execute() throws Throwable
            {
                return clusterService.getOfflineMembers();
            }
        }, true); 

        TabularData tabularData = ClusterMemberType.tabularData(offlineMembers);
        return tabularData;
    }

    @Override
    public TabularData getNonClusteredServers()
    {
        final String ipAddr = clusterService.getMemberIP();
        final Integer port = clusterService.getMemberPort();
        
        Set<RegisteredServerInfoImpl> nonMembers = doWork(new RetryingTransactionCallback<Set<RegisteredServerInfoImpl>>()
        {
            @Override
            public Set<RegisteredServerInfoImpl> execute() throws Throwable
            {
                return clusterService.getRegisteredNonMembers(ipAddr, port);
            }
        }, true);  

        TabularData tabularData = ClusterNonMemberType.tabularData(nonMembers);
        return tabularData;
    }

    @Override
    public void deregisterNonClusteredServer(final String ipAddress, final int port)
    {
        doWork(new RetryingTransactionCallback<Void>()
        {
            @Override
            public Void execute() throws Throwable
            {
                try
                {
                    clusterService.deregisterNonClusteredServer(ipAddress, port);
                }
                catch (ServerNotFoundException snfe)
                {
                    // Translate to a simple RuntimeException, since the required class file
                    // will not be on the JMX client, which would lead to the client giving
                    // a java.rmi.UnmarshalException
                    throw new RuntimeException(snfe.getMessage());
                }
                return null;
            }
        }, false);
    }
    
    public void setClusterService(ClusterService clusterService)
    {
        this.clusterService = clusterService;
    }
}
