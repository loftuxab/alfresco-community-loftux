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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.alfresco.repo.tenant.TenantAdminService;
import org.alfresco.repo.tenant.TenantUtil;
import org.alfresco.repo.tenant.TenantUtil.TenantRunAsWork;
import org.alfresco.repo.transaction.RetryingTransactionHelper.RetryingTransactionCallback;
import org.alfresco.service.cmr.repository.InvalidNodeRefException;
import org.alfresco.util.GUID;
import org.alfresco.util.test.junitrules.AbstractAlfrescoPersonTest;
import org.alfresco.util.test.junitrules.AlfrescoPerson;
import org.junit.BeforeClass;

/**
 * Test class for {@link AlfrescoPerson}.
 * 
 * @author Neil Mc Erlean
 * @since 4.2
 */
public class AlfrescoCloudPersonTest extends AbstractAlfrescoPersonTest
{
    private String testUserTenant;
    
    protected static TenantAdminService TENANT_ADMIN_SERVIVCE;
    
    @BeforeClass public static void initCloudStaticData() throws Exception
    {
        TENANT_ADMIN_SERVIVCE = APP_CONTEXT_INIT.getApplicationContext().getBean("TenantAdminService", TenantAdminService.class);
    }
    
    @Override protected String createTestUserName()
    {
        // In Cloud Alfresco, users all have email addresses as their user names.
        testUserTenant = GUID.generate() + ".example";
        
        return GUID.generate() + "@" + testUserTenant;
    }
    
    @Override protected void validateCmPersonNode(final String username, final boolean exists)
    {
        // In the Cloud, the cm:person node(s) for users are stored in any tenants of which they are members.
        // So for our test user, there is only one such tenant.
        
        TenantUtil.runAsTenant(new TenantRunAsWork<Void>()
        {
            @Override public Void doWork() throws Exception
            {
                TRANSACTION_HELPER.doInTransaction(new RetryingTransactionCallback<Void>()
                {
                    public Void execute() throws Throwable
                    {
                        boolean personExists = false;
                        try
                        {
                            personExists = PERSON_SERVICE.personExists(username);
                        } catch (InvalidNodeRefException ignoreIfThrown)
                        {
                            // Intentionally empty
                            
                            // There's something odd about the exists check in multi-tenancy if tenant itself doesn't exist.
                            // So we'll default to false & catch the exception:
                        }
                        assertEquals("Test person's existence was wrong", exists, personExists);
                        return null;
                    }
                });
                
                return null;
            }
        }, testUserTenant);
    }
    
    @Override protected void additionalValidations(final String username, final boolean userExists)
    {
        TRANSACTION_HELPER.doInTransaction(new RetryingTransactionCallback<Void>()
        {
            public Void execute() throws Throwable
            {
                // If the user exists, then we expect their home tenant to exist. And if not, not...
                final int indexOfAtSymbol = username.indexOf("@");
                assertTrue("Username did not contain expected '@': " + username, indexOfAtSymbol > 1);
                
                final String tenantName = username.substring(indexOfAtSymbol + 1);
                assertEquals("Test tenant's existence was wrong", userExists, TENANT_ADMIN_SERVIVCE.existsTenant(tenantName));
                return null;
            }
        });
    }
}
