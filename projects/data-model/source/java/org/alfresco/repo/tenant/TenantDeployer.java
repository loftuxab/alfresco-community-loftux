package org.alfresco.repo.tenant;


/**
 * Tenant Deployer interface.
 * <p>
 * This interface allows components to be notified of tenant events.
 * Components will register with TenantAdminService.
 * Also callbacks used during bootstrap (init) and shutdown (destroy)
 *
 */

public interface TenantDeployer
{    
    public void onEnableTenant();
    
    public void onDisableTenant();
    
    // callback for bootstrap (for each tenant)
    public void init();
    
    // callback for shutdown (for each tenant)
    public void destroy();
}
