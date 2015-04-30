/*
 * Copyright (C) 2005-2013 Alfresco Software Limited.
 *
 * This file is part of Alfresco
 *
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */

package org.alfresco.module.org_alfresco_module_cloud.person;

import org.alfresco.repo.tenant.TenantAdminService;
import org.alfresco.repo.tenant.TenantUtil;
import org.alfresco.repo.tenant.TenantUtil.TenantRunAsWork;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.util.test.testusers.TestUserComponentImpl;

/**
 * @author Neil Mc Erlean
 * @since 4.2
 */
public class CloudTestUserComponentImpl extends TestUserComponentImpl
{
    private TenantAdminService tenantAdminService;
    
    private final static String ADMIN_RAW_PASSWORD = "password";
    
    public void setTenantAdminService(TenantAdminService service) { this.tenantAdminService = service; }
    
    @Override public NodeRef createTestUser(final String userName)
    {
        String[] nameComponents = userName.split("@");
        final String tenantDomain = nameComponents[1];
        
        // This is the same as the normal person creation, but with the necessary first step of creating a tenant...
        if ( !tenantAdminService.existsTenant(tenantDomain))
        {
            // Of course another user may already have been created with this tenant.
            tenantAdminService.createTenant(tenantDomain, ADMIN_RAW_PASSWORD.toCharArray());
        }
        
        // ...and then running the creation of the person node in that tenant.
        NodeRef person = TenantUtil.runAsSystemTenant(new TenantRunAsWork<NodeRef>()
        {
            @Override public NodeRef doWork() throws Exception
            {
                NodeRef personNode = CloudTestUserComponentImpl.super.createTestUser(userName);
                return personNode;
            }
        }, tenantDomain);
        
        return person;
    }
    
    @Override public void deleteTestUser(final String userName)
    {
        String[] nameComponents = userName.split("@");
        final String tenantDomain = nameComponents[1];
        
        // This is the same as the normal person deletion, but with the necessary first step of running in the user's
        // home tenant...
        TenantUtil.runAsSystemTenant(new TenantRunAsWork<Void>()
        {
            @Override public Void doWork() throws Exception
            {
                CloudTestUserComponentImpl.super.deleteTestUser(userName);
                return null;
            }
        }, tenantDomain);
        
        // ... and then deleting that tenant - note the implicit assumption here that no other user is in that tenant.
        // TODO Need to only delete tenant if this was the last user
        
        if (tenantAdminService.existsTenant(tenantDomain))
        {
            tenantAdminService.deleteTenant(tenantDomain);
        }
    }
}
