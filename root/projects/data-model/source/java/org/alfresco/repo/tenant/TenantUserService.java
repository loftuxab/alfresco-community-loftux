package org.alfresco.repo.tenant;

import org.alfresco.api.AlfrescoPublicApi;

/**
 * Interface for Tenant User-Domain functionality.
 * 
 * @author janv
 * @author Derek Hulley
 * @since 3.0
 */
@AlfrescoPublicApi
public interface TenantUserService
{
    /**
     * @return          the username <b>with</b> the tenant-specific ID attached
     */
    public String getDomainUser(String baseUsername, String tenantDomain);
    
    /**
     * @return          the username <b>without</b> the tenant-specific ID attached
     */
    public String getBaseNameUser(String name);
    
    /**
     * @return          the tenant-specific ID for current user
     */
    public String getCurrentUserDomain();
    
    /**
     * @return          the tenant-specific ID for specified username
     */
    public String getUserDomain(String username);
    
    /**
     * @return          true if the system is configured to be MT-enabled
     */
    public boolean isEnabled();
}
