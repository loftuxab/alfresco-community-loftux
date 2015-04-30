package org.alfresco.module.org_alfresco_module_cloud.metrics;

import java.util.List;

/**
 * JMX Interface for exposing the number of users who logged in with a particular credential type with a period of 
 * time.
 * 
 * @author Alex Miller
 * @since Cloud Sprint 5
 */
public interface ConcurrentUserMonitorMXBean
{
    /**
     * @return A list of {@link UserCount} objects for each tracked credential type, with the number of user who logged in 
     *           with a particular credential type, in the last 5 minutes, across the cluster. 
     */
    public List<UserCount> getConcurrentUserCounts();

    /**
     * @return A list of {@link UserCount} objects for each tracked credential type, with the number of user who logged in 
     *           with a particular credential type, in the last 5 minutes, on the current node. 
     */
    public List<UserCount> getLocalConcurrentUserCounts();
}
