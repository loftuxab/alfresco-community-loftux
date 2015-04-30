/*
 * Copyright (C) 2005-2012 Alfresco Software Limited.
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
package org.alfresco.module.org_alfresco_module_cloud.accounts;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import org.alfresco.module.org_alfresco_module_cloud.CloudTestContext;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.util.ApplicationContextHelper;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.ApplicationContext;

/**
 * This class does some very basic tests for the Alfresco Cloud Module.
 * It's basically checking if the module can be started up correctly.
 * 
 * @author Neil Mc Erlean
 * @since Thor Alfresco Cloud Module
 */
public class AccountRegistryTest
{
    private static ApplicationContext testContext;
    
    // Services
    private static AccountRegistry ACCOUNT_REGISTRY;
    
    private CloudTestContext cloudContext;
 
    /**
     * Initialise various services required by the test.
     */
    @BeforeClass public static void initTestsContext() throws Exception
    {
        testContext = ApplicationContextHelper.getApplicationContext();
        
        ACCOUNT_REGISTRY = (AccountRegistry)testContext.getBean("accountRegistry");
    }
    
    @Before public void init()
    {
        cloudContext = new CloudTestContext(testContext);
        
        // Set the current security context as admin
        AuthenticationUtil.setFullyAuthenticatedUser(AuthenticationUtil.getAdminUserName());
    }
    
    @After public void cleanup()
    {
        cloudContext.cleanup();
    }
    
    @Test public void checkAccountClassDefinitions() throws Exception
    {
        Collection<AccountClass> accountClasses = ACCOUNT_REGISTRY.getClasses();
        assertNotNull("accountClasses was null", accountClasses);
        assertEquals("Wrong number of account classes", 3, accountClasses.size());
        
        SortedMap<AccountClass.Name, AccountClass> sortedAccountClasses = new TreeMap<AccountClass.Name, AccountClass>();
        for (AccountClass accClass : accountClasses)
        {
            sortedAccountClasses.put(accClass.getName(), accClass);
        }
        
        // Do we have the correct Account Classes?
        Set<AccountClass.Name> expectedAccountClassNames = new HashSet<AccountClass.Name>();
        expectedAccountClassNames.add(AccountClass.Name.PUBLIC_EMAIL_DOMAIN);
        expectedAccountClassNames.add(AccountClass.Name.PRIVATE_EMAIL_DOMAIN);
        expectedAccountClassNames.add(AccountClass.Name.PAID_BUSINESS);
        
        assertEquals("Wrong set of account classes", expectedAccountClassNames, sortedAccountClasses.keySet());
        
        // And check metadata on one of them
        assertEquals("Wrong minCode on account class", 0, sortedAccountClasses.get(AccountClass.Name.PRIVATE_EMAIL_DOMAIN).getMinCode());
        assertEquals("Wrong maxCode on account class", 0, sortedAccountClasses.get(AccountClass.Name.PRIVATE_EMAIL_DOMAIN).getMaxCode());
        
        // And check display name
        assertEquals("Wrong display name", "Premium Business", sortedAccountClasses.get(AccountClass.Name.PAID_BUSINESS).getDisplayName());
    }
}
