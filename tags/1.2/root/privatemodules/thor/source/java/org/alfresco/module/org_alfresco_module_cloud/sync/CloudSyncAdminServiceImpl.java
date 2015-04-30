/*
 * Copyright 2005-2012 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.module.org_alfresco_module_cloud.sync;

import org.alfresco.enterprise.repo.sync.SyncAdminServiceImpl;
import org.alfresco.enterprise.repo.sync.SyncSetDefinition;
import org.alfresco.module.org_alfresco_module_cloud.accounts.AccountService;
import org.alfresco.module.org_alfresco_module_cloud.accounts.AccountType;
import org.alfresco.repo.tenant.TenantService;


/**
 * Override for Alfresco in the cloud - used to reject sync access to non-Enterprise networks.
 * 
 * @author janv
 * @since Cloud-specific (Sync 4.1)
 */
public class CloudSyncAdminServiceImpl extends SyncAdminServiceImpl
{
    private AccountService accountService;
    private SyncPermissionsChecker syncPermissionsChecker;

    private int minAllowedAccountTypeId = AccountType.STANDARD_NETWORK_ACCOUNT_TYPE;
    
    public void setSyncPermissionsChecker(SyncPermissionsChecker syncPermissionsChecker)
    {
        this.syncPermissionsChecker = syncPermissionsChecker;
    }
    
    public void setAccountService(AccountService accountService)
    {
        this.accountService = accountService;
    }
    
    public void setMinAllowedAccountTypeId(int minAllowedAccountTypeId)
    {
        this.minAllowedAccountTypeId = minAllowedAccountTypeId;
    }
    
    @Override
    public boolean isTenantEnabledForSync(String tenantDomain)
    {
        if (tenantDomain == null)
        {
            return false;
        }
        if (! super.isTenantEnabledForSync(tenantDomain))
        {
            return false;
        }
        
        return ((! tenantDomain.equals(TenantService.DEFAULT_DOMAIN)) &&
                accountService.getAccountByDomain(tenantDomain).getType().getId() >= minAllowedAccountTypeId);
    }
    
    @Override public void deleteTargetSyncSet(String ssdId)
    {
        SyncSetDefinition ssd = getSyncSetDefinition(ssdId);
        syncPermissionsChecker.checkSsdPermissions(ssd);
        super.deleteTargetSyncSet(ssdId);
    }

}
