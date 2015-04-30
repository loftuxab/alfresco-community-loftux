package org.alfresco.module.org_alfresco_module_cloud.metrics;

import java.beans.ConstructorProperties;

/**
 * DTO, accessible via JMX, for representing the number of users who have logged in with given credential type, in a 
 * given time.
 * 
 * @see ConcurrentUserMonitor
 * 
 * @author Alex Miller
 * @since Cloud Sprint 5
 */
public class UserCount
{
    private String credentialType;
    private long count;

    @ConstructorProperties({"credentialType", "count"})
    public UserCount(String credentialType, long count)
    {
        this.credentialType = credentialType;
        this.count = count;
    }
    
    /**
     * @return The credential type this count is for
     */
    public String getCredentialType()
    {
        return credentialType;
    }
    
    /**
     * @return The count
     */
    public long getCount()
    {
        return count;
    }
}
