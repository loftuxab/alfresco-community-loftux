/*
 * Copyright (C) 2005-2010 Alfresco Software Limited.
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
package org.alfresco.module.org_alfresco_module_cloud.accounts.domain;

import static org.junit.Assert.assertEquals;

import org.alfresco.module.org_alfresco_module_cloud.CloudTestContext;
import org.alfresco.module.org_alfresco_module_cloud.InMemoryTestContext;
import org.alfresco.module.org_alfresco_module_cloud.accounts.AccountService;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * This class does some simple testing of the {@link AccountService} using an in-memory (test only) persistence back end.
 * 
 * @author Neil Mc Erlean
 * @since Alfresco Cloud Module 0.1 (Thor)
 */
public class AccountDAOMemoryImplTest extends AccountDAOImplTest
{
    private CloudTestContext cloudContext;
    
    // Services
    @BeforeClass public static void initTestsContext() throws Exception
    {
        TEST_CONTEXT = InMemoryTestContext.getContext();
        TRANSACTION_HELPER = (RetryingTransactionHelper)TEST_CONTEXT.getBean("retryingTransactionHelper");
        ACCOUNT_DAO = (AccountDAO) TEST_CONTEXT.getBean("accountDAO");
    }
    
    @Override
    @Test
    public void checkUsingCorrectDAO() throws Exception
    {
        // This check will only work for un-intercepted beans.
        assertEquals("Loaded the wrong AccountDAO impl class", AccountDAOMemoryImpl.class, ACCOUNT_DAO.getClass());
    }
}
