/*
 * Copyright 2005-2012 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.enterprise.repo.cluster.core;

import java.util.Set;

/**
 * Provides services for registering a prospective cluster member, obtaining
 * details of expected cluster members etc.
 * 
 * @author Matt Ward
 */
public interface ClusterService
{
    boolean isClusteringEnabled();
    
    /**
     * Register a server that has clustering enabled and is therefore expected to
     * become an active member of the cluster.
     * 
     * @param hostName
     * @param ipAddress
     * @param port
     * @param nodeType
     */
    void registerMember(String hostName, String ipAddress, int port, String nodeType);
    
    /**
     * Register a server that has clustering disabled and is therefore not expected
     * to become an active member of the cluster.
     * 
     * @param hostName
     * @param ipAddress
     * @param port
     * @param nodeType
     */
    void registerNonMember(String hostName, String ipAddress, int port, String nodeType);
    
    /**
     * Get the descriptive server type as set in the property <code>alfresco.cluster.nodetype</code>.
     * 
     * @return server type
     */
    String getServerType();
    
    /**
     * Get the hostname for this cluster member, as set in the property <code>alfresco.cluster.hostname</code>.
     * 
     * @return hostname
     */
    String getMemberHostName();
    
    /**
     * Get the IP address for this cluster member, as reported by the underlying clustering technology.
     * 
     * @return IP address
     */
    String getMemberIP();
    
    /**
     * Get the port used by this cluster member, as reported by the underlying clustering technology. The
     * value is not the necessarily same as the <code>alfresco.hazelcast.port</code> property, e.g. if
     * the port autoincrement option is being used.
     *  
     * @return clustering port
     */
    Integer getMemberPort();
    
    /**
     * Get members of the cluster excluding "this" host - only servers registered
     * using {@link #registerMember(String, String, int)} that do not match the
     * current host's IP address and port will be returned.
     * 
     * @param ipAddress
     * @param port
     * @return Set of {@link RegisteredServerInfo} objects.
     */
    Set<RegisteredServerInfoImpl> getOtherRegisteredMembers(String ipAddress, int port);

    /**
     * Get all registered servers that have clustering enabled.
     * 
     * @return Set of {@link RegisteredServerInfo} objects.
     */
    Set<RegisteredServerInfoImpl> getAllRegisteredMembers();

    /**
     * Get members of the cluster as reported by the underlying clustering technology. The set may
     * include members that have not been registered but have joined the cluster somehow - these will
     * have null <tt>lastRegistered</tt> properties.
     * <p>
     * The current server (as reported by {@link #getThisMember()} is included in the set <strong>if clustered</strong>.
     * 
     * @return Set of RegisteredServerInfo objects.
     */
    Set<RegisteredServerInfoImpl> getActiveMembers();

    /**
     * Get details of servers that have previously registered themselves for participation in the cluster,
     * but are not currently active.
     * 
     * @return Set of RegisteredServerInfo objects.
     */
    Set<RegisteredServerInfoImpl> getOfflineMembers();
    
    /**
     * Get servers registered as non-members, excluding the current host. Only
     * servers registered using {@link #registerNonMember(String, String, int)} that
     * do not match the current host's IP address and port will be returned.
     * 
     * @param ipAddress
     * @param port
     * @return Set of {@link RegisteredServerInfo} objects.
     */
    Set<RegisteredServerInfoImpl> getRegisteredNonMembers(String ipAddress, Integer port);
    
    /**
     * Retrieves the set of all servers, including members and non-members
     * and including the <strong>current</strong> server.
     * 
     * @return Set of {@link RegisteredServerInfo} objects.
     */
    Set<RegisteredServerInfoImpl> getAllRegisteredServers();
    
    /**
     * Removes a server's info from the cluster membership registry, whether
     * previously registered as a member or non-member.
     * 
     * @param ipAddress
     * @param port
     */
    void deregisterServer(String ipAddress, int port);
    
    /**
     * Same functionality as {@link #deregisterServer(String, int)} except that as
     * an extra safety check, this method checks that the member is not considered
     * an active server (i.e. is not returned in the list returned by
     * {@link #getActiveMembers()}) - if it is active, then an IllegalArgumentException
     * is thrown instead.
     * <p>
     * Note that as there is no locking involved in
     * that state it is possible for a servers details to be absent from the active
     * member list, only to be removed just after being added to the cluster.
     * <p>
     * This method is only intended to be invoked by a human that knows the server
     * details are no longer pertinent, e.g. for clearing out info from the OfflineMembers
     * attribute of the ClusterAdmin JMX tool.
     *  
     * @param ipAddress
     * @param port
     */
    void deregisterNonClusteredServer(String ipAddress, int port);
    
    /**
     * Generate and save a new cluster name.
     * 
     * @return the generated cluster name.
     */
    String generateClusterName();
    
    /**
     * Retrieve the cluster name as originally generated by the system.
     * 
     * @return cluster name : String
     */
    String getClusterName();
    
    
    /**
     * Called to initialise the service from within an onBootstrap event.
     */
    void initClusterService();

    /**
     * 
     */
    void initNonMember();
    
    /**
     * Returns true if initialisation has been completed.
     * 
     * @return boolean
     */
    boolean isInitialised();
    
    /**
     * Markt the service as uninitialised.
     */
    void shutDownClusterService();
    
    /**
     * Returns how many members are currently in this cluster - as reported
     * by the underlying clustering technology (as opposed to reporting the
     * number of members that have registered themselves with the repository.)
     * 
     * @return int
     */
    int getNumActiveClusterMembers();
}
