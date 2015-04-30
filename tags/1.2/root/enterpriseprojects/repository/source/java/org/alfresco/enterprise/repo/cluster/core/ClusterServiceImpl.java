/*
 * Copyright 2005-2012 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.enterprise.repo.cluster.core;

import java.io.Serializable;
import java.net.InetSocketAddress;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

import org.alfresco.repo.admin.SysAdminParams;
import org.alfresco.repo.lock.JobLockService;
import org.alfresco.service.cmr.attributes.AttributeService;
import org.alfresco.service.cmr.attributes.AttributeService.AttributeQueryCallback;
import org.alfresco.service.descriptor.Descriptor;
import org.alfresco.service.descriptor.DescriptorService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.LogUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.hazelcast.core.Cluster;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.Member;

/**
 * Implementation of the {@link ClusterService} interface.
 * 
 * @author Matt Ward
 */
public class ClusterServiceImpl implements ClusterService
{
    private static final Log log = LogFactory.getLog(ClusterServiceImpl.class);
    private static final String KEY_CLUSTER_INFO = ".clusterInfo";
    private static final String KEY_CLUSTER_NAME = ".cluster_name";
    
    private static final String KEY_CLUSTER_MEMBERS = ".clusterMembers";
    private static final String KEY_HOST_NAME = ".host_name";
    private static final String KEY_IP_ADDRESS = ".ip_address";
    private static final String KEY_PORT = ".port";
    private static final String KEY_CLUSTERING_ENABLED = ".clustering_enabled";
    private static final String KEY_LAST_REGISTERED = ".last_registered";
    private static final String KEY_CLUSTER_NODE_TYPE = ".cluster_node_type";
    private static final QName LOCK_QNAME = QName.createQName(NamespaceService.SYSTEM_MODEL_1_0_URI, "ClusterService");
    private static final long LOCK_TIMEOUT_MS = 6000;
    private static final String MSG_IP_NOT_RECOMMENDED = "system.cluster.loopback_not_recommended";
    private static final String MSG_LOOPBACK_NOT_CONFIGURED = "system.cluster.loopback_not_in_conf";
    
    private AttributeService attributeService;
    private DescriptorService descriptorService;
    private String serverType;
    private SysAdminParams sysAdminParams;
    private String memberHostName;
    protected TimestampProvider timeStampProvider = new TimestampProvider();
    private boolean initialised;
    /** Guards {@link #initialised} */
    private ReentrantReadWriteLock initRWLock = new ReentrantReadWriteLock();
    private HazelcastInstanceFactory hazelcastInstanceFactory;
    private JobLockService jobLockService;
    private String interfaceSpec;
    private String memberIPAddress;
    private int memberPort;
    private int maxInitRetries;
    private NonMemberIPAddrPicker nonMemberAddrPicker;
    
    @Override
    public boolean isClusteringEnabled()
    {
        return hazelcastInstanceFactory.isClusteringEnabled();
    }

    @Override
    public void registerMember(String hostName, String ipAddress, int port, String nodeType)
    {
        registerServer(hostName, ipAddress, port, true, nodeType);   
    }
    
    @Override
    public void registerNonMember(String hostName, String ipAddress, int port, String nodeType)
    {
        registerServer(hostName, ipAddress, port, false, nodeType);   
    }

    @Override
    public String getServerType()
    {
        return serverType;
    }

    @Override
    public String getMemberHostName()
    {
        if (!isInitialised())
        {
            return null;
        }
        return memberHostName;
    }

    @Override
    public String getMemberIP()
    {
        if (!isInitialised())
        {
            return null;
        }
        return memberIPAddress;
    }

    @Override
    public Integer getMemberPort()
    {
        if (!isInitialised())
        {
            return null;
        }
        return memberPort;
    }

    @Override
    public Set<RegisteredServerInfoImpl> getOtherRegisteredMembers(String ipAddress, int port)
    {
        return getOtherServers(ipAddress, port, true);
    }
    
    @Override
    public Set<RegisteredServerInfoImpl> getActiveMembers()
    {
        if (!isInitialised())
        {
            // If clustering hasn't been initialised, then there are no active members.
            return Collections.emptySet();
        }

        HazelcastInstance hzInstance = hazelcastInstanceFactory.getInstance();
        Cluster cluster = hzInstance.getCluster();
        // Members of the cluster, reported by Hazelcast
        Set<Member> hzMembers = cluster.getMembers();
        // All registered servers (may or may not be currently in the cluster)
        Set<RegisteredServerInfoImpl> allServers = getAllRegisteredServers();
        // Active members - the list we are building.
        Set<RegisteredServerInfoImpl> activeMembers = new HashSet<RegisteredServerInfoImpl>(hzMembers.size());
        
        // Go through the list of cluster members and where members are registered, use the registered
        // info in the result set. Otherwise use the info as provided by Hazelcast.
        for (Member hzMember : hzMembers)
        {
            final String hzMemberIP = hzMember.getInetSocketAddress().getAddress().getHostAddress();
            final int hzMemberPort = hzMember.getInetSocketAddress().getPort();
            
            // Look for a registered member matching the hazelcast member info.
            RegisteredServerInfoImpl member = null;
            
            for (RegisteredServerInfoImpl registered : allServers)
            {
                if (registered.getIPAddress().equals(hzMemberIP) && registered.getPort() == hzMemberPort)
                {
                    member = registered;
                    break;
                }
            }
            if (member == null)
            {
                // No registered membership information was found, so create an entry to include in the returned set.
                final String hzHostName = hzMember.getInetSocketAddress().getHostName();
                member = new RegisteredServerInfoImpl(
                            hzHostName,
                            hzMemberIP,
                            hzMemberPort,
                            false,
                            null,
                            "Unknown server type");
            }
            // Add the member's server info to the set.
            activeMembers.add(member);
        }
        return activeMembers;
    }

    @Override
    public Set<RegisteredServerInfoImpl> getOfflineMembers()
    {
        if (!isInitialised())
        {
            // It's not possible to say which members are offline, as we are not part of the cluster.
            // (other members in the registered servers list may be part of a working cluster even though
            // this member is not).
            return Collections.emptySet();
        }
        Set<RegisteredServerInfoImpl> activeMembers = getActiveMembers();
        Set<RegisteredServerInfoImpl> registeredMembers = getAllRegisteredMembers();
        Set<RegisteredServerInfoImpl> offlineMembers = new HashSet<RegisteredServerInfoImpl>();
        for (RegisteredServerInfoImpl server : registeredMembers)
        {
            boolean add = true;
            for (RegisteredServerInfoImpl active : activeMembers)
            {
                if (active.getIPAddress().equals(server.getIPAddress()) &&
                            active.getPort() == server.getPort())
                {
                    // The registered member is in the list of active servers, so is NOT offline
                    add = false;
                    break;
                }
            }
            if (add)
            {
                offlineMembers.add(server);
            }
        }
        return offlineMembers;
    }

    @Override
    public Set<RegisteredServerInfoImpl> getRegisteredNonMembers(String ipAddress, Integer port)
    {
        if (!isInitialised())
        {
            return Collections.emptySet();
        }
        return getOtherServers(ipAddress, port, false);
    }

    @Override
    public Set<RegisteredServerInfoImpl> getAllRegisteredServers()
    {
        ServerInfoCallback callback = new ServerInfoCallback();
        try
        {
            attributeService.getAttributes(callback, KEY_CLUSTER_MEMBERS);
        }
        catch (Throwable e)
        {
            // TODO: remove catch block when class loader problem identified/fixed.
            log.error("Unable to get cluster member information from AttributeService.", e);
        }
        Set<RegisteredServerInfoImpl> allServers = callback.getMembers();
        return allServers;
    }

    @Override
    public void deregisterServer(String ipAddress, int port)
    {
        String socketAddress = ipAddress + ":" + port;
        Serializable[] key = new Serializable[] { KEY_CLUSTER_MEMBERS, socketAddress };
        if (!attributeService.exists(makeHostKey(ipAddress, port, KEY_IP_ADDRESS)))
        {
            throw new ServerNotFoundException(ipAddress, port);
        }
        attributeService.removeAttributes(key);
    }

    
    @Override
    public void deregisterNonClusteredServer(String ipAddress, int port)
    {
        // Pre-condition: server is not an active cluster member.
        Set<RegisteredServerInfoImpl> activeMembers = getActiveMembers();
        for (RegisteredServerInfoImpl server : activeMembers)
        {
            if (ipAddress.equals(server.getIPAddress()) && port == server.getPort())
            {
                String ipPort = ipAddress + ":" + port; 
                throw new IllegalArgumentException("Cannot deregister server " + ipPort +
                            " as it is an active cluster member.");
            }
        }
        
        deregisterServer(ipAddress, port);
    }

    @Override
    public String generateClusterName()
    {
        String clusterName = createClusterName();
        attributeService.createAttribute(clusterName, makeClusterNameKey());
        return clusterName;
    }

    @Override
    public String getClusterName()
    {
        // The first member that finds an empty cluster name must generate one and store it.
        boolean clusterNameExists = attributeService.exists(makeClusterNameKey());
        String clusterName = null;
        if (!clusterNameExists)
        {
            return null;
        }
        else
        {
            clusterName = (String) attributeService.getAttribute(makeClusterNameKey());
        }
        return clusterName;
    }
    
    @Override
    public void initClusterService()
    {
        final ReadLock readLock = initRWLock.readLock();
        try
        {
            readLock.lock();
            if (initialised)
            {
                // Already initialised
                return;
            }
        }
        finally
        {
            readLock.unlock();
        }
        
        final WriteLock writeLock = initRWLock.writeLock();
        try
        {
            writeLock.lock();
            // Ensure condition hasn't changed
            if (!initialised)
            {
                if (isClusteringEnabled())
                {
                    startClusterMember();
                }   
                initialised = true;
            }
        }
        finally
        {
            writeLock.unlock();
        }
    }

    @Override
    public void initNonMember()
    {
        String localHostName = sysAdminParams.subsituteHost(memberHostName);
        setMemberHostName(localHostName);
        String nodeType = getServerType();
        String ipAddress = nonMemberAddrPicker.pick();
        // Note the current limit of one non-member (e.g. transformation server) per IP
        // address - due to port 0. This is only for reporting though, as it isn't in the cluster.
        // TODO: add something to attribute store key, so that more non-members may be stored.
        registerNonMember(memberHostName, ipAddress, 0, nodeType);
    }

    private void startClusterMember()
    {
        long startTime = System.nanoTime();
        
        // Obtain JobLock lock
        final String lockToken = jobLockService.getLock(LOCK_QNAME, LOCK_TIMEOUT_MS, LOCK_TIMEOUT_MS/2, maxInitRetries);
                
        Timer timer = new Timer();
        try
        {
            scheduleJobLockRefresh(lockToken, timer);
            
            // Startup hazelcast and get hazelcast's chosen IP address and port
            hazelcastInstanceFactory.initInstance();
            HazelcastInstance hzInstance = hazelcastInstanceFactory.getInstance();
            InetSocketAddress sockAddr = hzInstance.getCluster().getLocalMember().getInetSocketAddress();
            
            String localHostName = sysAdminParams.subsituteHost(memberHostName);
            setMemberHostName(localHostName);
            memberIPAddress = sockAddr.getAddress().getHostAddress();
            memberPort = sockAddr.getPort();
            
            // 127.0.0.1 is only allowed if explicitly configured.
            validateIPAddress();
            
            // If this IP address was previously associated with a non-clustered server, remove it.
            try
            {
                deregisterNonClusteredServer(memberIPAddress, 0);
            }
            catch (ServerNotFoundException e)
            {
                // NOOP: we weren't necessarily expecting it to be there.
            }
            
            // Persist information about this host
            registerSelf(sockAddr);
        }
        finally
        {
            // Stop refreshing the job lock and relase it, allowing another
            // cluster member to initialise itself properly. 
            stopJobLockRefresh(timer);
            jobLockService.releaseLock(lockToken, LOCK_QNAME);
        }
        
        long endTime = System.nanoTime();
        if (log.isDebugEnabled())
        {
            double timeTakenMillis = (endTime - startTime) / 1e6;
            log.debug("startClusterMember() took " + timeTakenMillis + "ms");
        }
    }


    /**
     * Schedule repeating periodic job lock refresh.
     * 
     * @param lockToken
     * @param timer
     */
    private void scheduleJobLockRefresh(final String lockToken, Timer timer)
    {
        final long jobLockRefresh = LOCK_TIMEOUT_MS/2;
        timer.schedule(new TimerTask()
        {
            @Override
            public void run()
            {
                // Refresh the lock
                jobLockService.refreshLock(lockToken, LOCK_QNAME, LOCK_TIMEOUT_MS);
            }
        }, jobLockRefresh, jobLockRefresh);
    }
    
    /**
     * Cancel the repeating periodical job lock refreshes.
     * 
     * @param timer
     */
    private void stopJobLockRefresh(Timer timer)
    {
        // Stop periodically refreshing the job lock.
        timer.cancel();
    }

    /**
     * Check that the chosen IP address is acceptable. The address 127.0.0.1 is only
     * allowed if explicitly set using the property <code>alfresco.cluster.interface</code>.
     *
     * @throws ClusterAddressException when an unacceptable address has been chosen.
     */
    private void validateIPAddress()
    {
        final String loopbackAddress = "127.0.0.1";
        if (memberIPAddress != null && memberIPAddress.equals(loopbackAddress))
        {
            if (interfaceSpec != null && interfaceSpec.equals(loopbackAddress))
            {
                if (log.isWarnEnabled())
                {
                    LogUtil.warn(log, MSG_IP_NOT_RECOMMENDED, loopbackAddress);
                }
            }
            else
            {                
                if (log.isErrorEnabled())
                {
                    LogUtil.error(log, MSG_LOOPBACK_NOT_CONFIGURED, loopbackAddress);
                }
                throw new ClusterAddressException(memberIPAddress);
            }
        }
    }

    @Override
    public boolean isInitialised()
    {
        final ReadLock readLock = initRWLock.readLock();
        try
        {
            readLock.lock();
            return initialised;
        }
        finally
        {
            readLock.unlock();
        }
    }

    @Override
    public void shutDownClusterService()
    {
        final WriteLock writeLock = initRWLock.writeLock();
        try
        {
            writeLock.lock();
            if (initialised)
            {
                if (log.isDebugEnabled())
                {
                    log.debug("Shutting down ClusterService.");
                }
                // TODO: registerSelfLeftCluster() or similar.

                // Record that we've de-initialised the cluster.
                initialised = false;
            }
        }
        finally
        {
            writeLock.unlock();
        }
    }

    @Override
    public int getNumActiveClusterMembers()
    {
        if (isInitialised())
        {
            HazelcastInstance hzInstance = hazelcastInstanceFactory.getInstance();
            Cluster cluster = hzInstance.getCluster();
            Set<Member> members = cluster.getMembers();
            int numMembers = members.size();
            return numMembers;
        }
        // Clustering is not available, so this machine is not part of a cluster.
        return 0;
    }

    private String createClusterName()
    {
        Descriptor descriptor = descriptorService.getCurrentRepositoryDescriptor();
        String repoId = descriptor.getId();
        String repoName = descriptor.getName();
        String clusterName = String.format("%s-%s", repoName, repoId).replaceAll(" ", "");
        if (log.isDebugEnabled())
        {
            log.debug("Generated cluster name: " + clusterName);
        }
        return clusterName;
    }
    
    private Serializable[] makeClusterNameKey()
    {
        Serializable[] key = new Serializable[] { KEY_CLUSTER_INFO, KEY_CLUSTER_NAME };
        return key;
    }
    
    private Set<RegisteredServerInfoImpl> getOtherServers(String ipAddress, int port, boolean isMember)
    {
        Set<RegisteredServerInfoImpl> allMembers = getAllRegisteredServers();
        Set<RegisteredServerInfoImpl> otherMembers = new HashSet<RegisteredServerInfoImpl>(allMembers.size());
        for (RegisteredServerInfoImpl member : allMembers)
        {
            if (member.isClusteringEnabled() == isMember &&
                !(member.getIPAddress().equals(ipAddress) && member.getPort() == port))
            {
                // We only want members other than *this* one.
                otherMembers.add(member);
            }
        }
        
        return otherMembers;
    }
    
    @Override
    public Set<RegisteredServerInfoImpl> getAllRegisteredMembers()
    {
        Set<RegisteredServerInfoImpl> allServers = getAllRegisteredServers();
        Set<RegisteredServerInfoImpl> members = new HashSet<RegisteredServerInfoImpl>(allServers.size());
        for (RegisteredServerInfoImpl server : allServers)
        {
            if (server.isClusteringEnabled())
            {
                // We only want members, i.e. clustering is enabled.
                members.add(server);
            }
        }
        
        return members;
    }
    
    /**
     * Registers a host with the cluster service, so that it may be discovered
     * by other cluster members (if it is itself enabled for clustering) or
     * may be listed as a non-clustered server for reporting.
     * 
     * @param hostName
     * @param ipAddress
     * @param port
     */
    private void registerServer(String hostName, String ipAddress, int port, boolean clusteringEnabled, String nodeType)
    {
        if (log.isDebugEnabled())
        {
            String msg = "Registering server: " +
                         "host=" + hostName +
                         ", address=" + ipAddress + ":" + port +
                         ", clustering enabled=" + clusteringEnabled +
                         ", node type=" + nodeType;
            log.debug(msg);
        }
        attributeService.setAttribute(hostName, makeHostKey(ipAddress, port, KEY_HOST_NAME));
        attributeService.setAttribute(ipAddress, makeHostKey(ipAddress, port, KEY_IP_ADDRESS));
        attributeService.setAttribute(port, makeHostKey(ipAddress, port, KEY_PORT));
        attributeService.setAttribute(clusteringEnabled, makeHostKey(ipAddress, port, KEY_CLUSTERING_ENABLED));
        Date now = timeStampProvider.timeStamp();
        attributeService.setAttribute(now, makeHostKey(ipAddress, port, KEY_LAST_REGISTERED));
        attributeService.setAttribute(nodeType, makeHostKey(ipAddress, port, KEY_CLUSTER_NODE_TYPE));
    }
    
    /**
     * Provides a means for the server to register itself with the cluster membership registry.
     */
    protected void registerSelf(InetSocketAddress sockAddr)
    {
        if (log.isDebugEnabled())
        {
            log.debug("Registering self...");
        }
        String ipAddress = sockAddr.getAddress().getHostAddress();
        int port = sockAddr.getPort();
        boolean clusteringEnabled = hazelcastInstanceFactory.isClusteringEnabled();
        String nodeType = getServerType();
        registerServer(memberHostName, ipAddress, port, clusteringEnabled, nodeType);
    }
    
    /**
     * Create a set of keys suitable for storing cluster information related attributes
     * in the attribute service.
     * 
     * @param ipAddress
     * @param port
     * @param property
     * @return Array of Serializable keys.
     */
    private Serializable[] makeHostKey(String ipAddress, int port, Serializable property)
    {
        String socketAddress = ipAddress + ":" + port;
        return new Serializable[] { KEY_CLUSTER_MEMBERS, socketAddress, property };
    }

    /**
     * Provide service with an attribute storage mechanism used
     * to track cluster membership information.
     *  
     * @param attributeService
     */
    public void setAttributeService(AttributeService attributeService)
    {
        this.attributeService = attributeService;
    }
    
    /**
     * Provide the service with access to repository descriptor.
     *  
     * @param descriptorService
     */
    public void setDescriptorService(DescriptorService descriptorService)
    {
        this.descriptorService = descriptorService;
    }

    /**
     * @param serverType the serverType to set
     */
    public void setServerType(String serverType)
    {
        this.serverType = serverType;
    }

    /**
     * @param sysAdminParams the sysAdminParams to set
     */
    public void setSysAdminParams(SysAdminParams sysAdminParams)
    {
        this.sysAdminParams = sysAdminParams;
    }

    /**
     * @param memberHostName the memberHostName to set
     */
    public void setMemberHostName(String memberHostName)
    {
        this.memberHostName = memberHostName;
    }

    /**
     * Provide access to the Hazelcast cluster.
     *  
     * @param hazelcastInstanceFactory
     */
    public void setHazelcastInstanceFactory(HazelcastInstanceFactory hazelcastInstanceFactory)
    {
        this.hazelcastInstanceFactory = hazelcastInstanceFactory;
    }

    /**
     * Provide access to the JobLockService.
     * 
     * @param jobLockService
     */
    public void setJobLockService(JobLockService jobLockService)
    {
        this.jobLockService = jobLockService;
    }

    /**
     * The requested interface to use for clustering, as set in the property
     * <code>alfresco.cluster.interface</code>.
     * 
     * @param interfaceSpec
     */
    public void setInterfaceSpec(String interfaceSpec)
    {
        this.interfaceSpec = interfaceSpec;
    }

    /**
     * The maximum number of times that this cluster member will attempt to
     * acquire the cluster service initalisation lock from the {@link JobLockService}.
     * 
     * @param maxInitRetries
     */
    public void setMaxInitRetries(int maxInitRetries)
    {
        this.maxInitRetries = maxInitRetries;
    }

    /**
     * @param nonMemberAddrPicker the nonMemberAddrPicker to set
     */
    public void setNonMemberAddrPicker(NonMemberIPAddrPicker nonMemberAddrPicker)
    {
        this.nonMemberAddrPicker = nonMemberAddrPicker;
    }

    /**
     * Date factory used to create timestamps.
     */
    protected static class TimestampProvider
    {
        public Date timeStamp()
        {
            return new Date();
        }
    }
    
    protected static class ServerInfoCallback implements AttributeQueryCallback
    {
        private Map<String, RegisteredServerInfoImpl> memberMap = new HashMap<String, RegisteredServerInfoImpl>();
        
        @Override
        public boolean handleAttribute(Long id, Serializable value, Serializable[] keys)
        {
            if (keys.length >= 2)
            {
                String serverId = (String) keys[1];
                RegisteredServerInfoImpl member = memberMap.get(serverId);
                if (member == null)
                {
                    member = new RegisteredServerInfoImpl();
                    memberMap.put(serverId, member);
                }
                if (keys.length == 3)
                {
                    String propKey = (String) keys[2];
                    if (propKey.equals(KEY_HOST_NAME))
                    {
                        String hostName = (String) value;
                        member.setHostName(hostName);
                    }
                    else if (propKey.equals(KEY_IP_ADDRESS))
                    {
                        String ipAddress = (String) value;
                        member.setIPAddress(ipAddress);
                    }
                    else if (propKey.equals(KEY_PORT))
                    {
                        Integer port = (Integer) value;
                        member.setPort(port); 
                    }
                    else if (propKey.equals(KEY_CLUSTERING_ENABLED))
                    {
                        Boolean enabled = (Boolean) value;
                        member.setClusteringEnabled(enabled);
                    }
                    else if (propKey.equals(KEY_LAST_REGISTERED))
                    {
                        Date lastRegistered = (Date) value;
                        member.setLastRegistered(lastRegistered);
                    }
                    else if (propKey.equals(KEY_CLUSTER_NODE_TYPE))
                    {
                        String serverType = (String) value;
                        member.setServerType(serverType);
                    }
                }
                
                return true;
            }
            else
            {
                // Non top-level attributes (i.e. single key) are currently supported,
                // so something has gone wrong.
                throw new IllegalArgumentException("Key length too short, primary/only key is: " + keys[0]);
            }
        }

        public Set<RegisteredServerInfoImpl> getMembers()
        {
            Set<RegisteredServerInfoImpl> members = new HashSet<RegisteredServerInfoImpl>(memberMap.values());
            return members;
        }
    }
}
