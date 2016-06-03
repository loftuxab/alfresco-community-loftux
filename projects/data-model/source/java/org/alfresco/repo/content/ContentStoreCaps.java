package org.alfresco.repo.content;

import org.alfresco.repo.tenant.TenantDeployer;

/**
 * ContentStore capabilities. Allows us to avoid performing {@code instanceof} questions
 * which can become a problem when certain proxies or subsystems are in use.
 * <p>
 * See ACE-2682 (tenant creation failure) for motivation.
 * 
 * @author Matt Ward
 */
public interface ContentStoreCaps
{
    /**
     * Returns the ContentStore cast to a TenantRoutingContentStore if the underlying
     * instance is of that type. Returns null otherwise.
     * <p>
     * Note, the actual return type is a TenantDeployer (supertype of TenantRoutingContentStore)
     * since the data model has no knowledge of that subtype. This interface may
     * need to move to a different project.
     * 
     * @return TenantRoutingContentStore
     */
    TenantDeployer getTenantRoutingContentStore();
    
    /**
     * Returns the ContentStore cast to a TenantDeployer if the underlying
     * instance is of that type. Returns null otherwise.
     * 
     * @return TenantDeployer
     */
    TenantDeployer getTenantDeployer();
}
