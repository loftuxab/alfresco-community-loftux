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
package org.alfresco.module.org_alfresco_module_cloud.emailaddress.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.alfresco.module.org_alfresco_module_cloud.CloudTestContext;
import org.alfresco.module.org_alfresco_module_cloud.emailaddress.DomainValidityCheck;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.util.ApplicationContextHelper;
import org.alfresco.util.GUID;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.ApplicationContext;

/**
 * Test code for {@link InvalidDomainDAOImpl}.
 * 
 * @author Neil Mc Erlean
 * @since Thor Phase 2 Sprint 1
 */
public class InvalidDomainDAOImplTest
{
    private static final Log log = LogFactory.getLog(InvalidDomainDAOImplTest.class);
    
    protected static ApplicationContext TEST_CONTEXT;
    
    // Services
    protected static InvalidDomainDAO INVALID_DOMAIN_DAO;
    protected static RetryingTransactionHelper TRANSACTION_HELPER;
    
    private CloudTestContext cloudContext;
    
    /**
     * Could put this in the {@link CloudTestContext} but it's not of general interest.
     */
    private List<String> invalidDomainsToBeTidied = new ArrayList<String>();
    
    private static String INVALID_DOMAIN_NAME;
    private static String INVALID_DOMAIN_TYPE;
    private static String INVALID_DOMAIN_NOTE;
    
    /**
     * Initialise various services required by the test.
     */
    @BeforeClass public static void initTestsContext() throws Exception
    {
        TEST_CONTEXT          = ApplicationContextHelper.getApplicationContext();
        TRANSACTION_HELPER    = (RetryingTransactionHelper)TEST_CONTEXT.getBean("retryingTransactionHelper");
        INVALID_DOMAIN_DAO    = (InvalidDomainDAOImpl) TEST_CONTEXT.getBean("invalidDomainDAO");
        
        // The default invalidDomain data used in this test.
        INVALID_DOMAIN_NAME = GUID.generate() + ".test";
        INVALID_DOMAIN_TYPE = DomainValidityCheck.FailureReason.ANTISPAM.toString();
        INVALID_DOMAIN_NOTE = "This invalid domain created by " + InvalidDomainDAOImplTest.class.getSimpleName();
    }
    
    @Before public void init() throws Exception
    {
        cloudContext = new CloudTestContext(TEST_CONTEXT);
        
        // Set the current security context as admin
        AuthenticationUtil.setFullyAuthenticatedUser(AuthenticationUtil.getAdminUserName());
    }
    
    @After public void cleanup()
    {
        cloudContext.cleanup();
        
        for (String domain : invalidDomainsToBeTidied)
        {
            if (INVALID_DOMAIN_DAO.getInvalidDomain(domain) != null)
            {
                INVALID_DOMAIN_DAO.deleteInvalidDomain(domain);
                
                log.debug("cleaned up test domain " + domain);
            }
        }
    }
    
    @Test public void createInvalidDomain() throws Exception
    {
        TRANSACTION_HELPER.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>()
        {
            @Override
            public Void execute() throws Throwable
            {
                createInvalidDomainEntity(INVALID_DOMAIN_NAME, INVALID_DOMAIN_TYPE, INVALID_DOMAIN_NOTE);
                return null;
            }
        });
        TRANSACTION_HELPER.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>()
        {
            @Override
            public Void execute() throws Throwable
            {
                InvalidDomainEntity entity = INVALID_DOMAIN_DAO.getInvalidDomain(INVALID_DOMAIN_NAME);
                assertNotNull(entity);
                assertEquals(INVALID_DOMAIN_NAME, entity.getDomain());
                assertEquals(INVALID_DOMAIN_TYPE, entity.getType());
                assertEquals(INVALID_DOMAIN_NOTE, entity.getNote());
                return null;
            }
        });
    }
    
    @Test public void createInvalidDomainAndUpdateData() throws Exception
    {
        final InvalidDomainEntity newInvalidDomain = TRANSACTION_HELPER.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<InvalidDomainEntity>()
        {
            @Override
            public InvalidDomainEntity execute() throws Throwable
            {
                InvalidDomainEntity invalidDomain = createInvalidDomainEntity(INVALID_DOMAIN_NAME, INVALID_DOMAIN_TYPE, INVALID_DOMAIN_NOTE);
                return invalidDomain;
            }
        });
        
        final String newType = DomainValidityCheck.FailureReason.BLACKLISTED.toString();
        final String newNote = "This is an updated note";
        
        TRANSACTION_HELPER.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>()
        {
            @Override
            public Void execute() throws Throwable
            {
                updateInvalidDomain(newInvalidDomain.getDomain(), newType, newNote);
                return null;
            }
        });
        
        // And check
        TRANSACTION_HELPER.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>()
        {
            @Override
            public Void execute() throws Throwable
            {
                InvalidDomainEntity updatedInvalidDomain = INVALID_DOMAIN_DAO.getInvalidDomain(newInvalidDomain.getDomain());
                assertNotNull(updatedInvalidDomain);
                assertEquals("Updated invalid domain 'type' was wrong", newType, updatedInvalidDomain.getType());
                assertEquals("Updated invalid domain 'note' was wrong", newNote, updatedInvalidDomain.getNote());
                return null;
            }
        });
    }
    
    @Test public void createDuplicateInvalidDomains() throws Exception
    {
        TRANSACTION_HELPER.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>()
        {
            @Override
            public Void execute() throws Throwable
            {
                createInvalidDomainEntity(INVALID_DOMAIN_NAME, INVALID_DOMAIN_TYPE, INVALID_DOMAIN_NOTE);
                boolean expectedExceptionThrown = false;
                try
                {
                    createInvalidDomainEntity(INVALID_DOMAIN_NAME, INVALID_DOMAIN_TYPE, INVALID_DOMAIN_NOTE);
                } catch (InvalidDomainException eexpected)
                {
                    expectedExceptionThrown = true;
                }
                assertTrue("Expected exception not thrown.", expectedExceptionThrown);
                
                return null;
            }
        });
    }
    
    @Test public void getAccountByDomain() throws Exception
    {
        final InvalidDomainEntity invalidDomain = TRANSACTION_HELPER.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<InvalidDomainEntity>()
        {
            @Override
            public InvalidDomainEntity execute() throws Throwable
            {
                InvalidDomainEntity domain = createInvalidDomainEntity(INVALID_DOMAIN_NAME, INVALID_DOMAIN_TYPE, INVALID_DOMAIN_NOTE);
                return domain;
            }
        });
        
        TRANSACTION_HELPER.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>()
        {
            @Override
            public Void execute() throws Throwable
            {
                InvalidDomainEntity existingInvalidDomain = INVALID_DOMAIN_DAO.getInvalidDomain(invalidDomain.getDomain());
                assertNotNull(existingInvalidDomain);
                InvalidDomainEntity nonExistingInvalidDomain = INVALID_DOMAIN_DAO.getInvalidDomain("doesnotexist.com");
                assertNull(nonExistingInvalidDomain);
                return null;
            }
        });
    }
    
    @Test public void getCountOfInvalidDomains() throws Exception
    {
        TRANSACTION_HELPER.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>()
        {
            @Override
            public Void execute() throws Throwable
            {
                int tableSize = INVALID_DOMAIN_DAO.getInvalidDomainCount();
                
                log.debug("There were " + tableSize + " rows in the blacklist table.");
                
                // Not much to assert here. I'm just checking the code runs through without exception.
                assertTrue(tableSize > 100);
                
                return null;
            }
        });
    }
    
    @Test public void getPagesOfInvalidDomainData() throws Exception
    {
        TRANSACTION_HELPER.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>()
        {
            @Override
            public Void execute() throws Throwable
            {
                final int totalTableSize = INVALID_DOMAIN_DAO.getInvalidDomainCount();
                
                log.debug("There were " + totalTableSize + " rows in the blacklist table.");
                
                
                // Get rows 0..4
                int startIndex = 0;
                int pageSize = 5;
                
                List<InvalidDomainEntity> invalidDomains0To4 = INVALID_DOMAIN_DAO.getInvalidDomains(startIndex, pageSize);
                assertNotNull(invalidDomains0To4);
                assertEquals("result set was wrong size", pageSize, invalidDomains0To4.size());
                
                log.debug("Rows 0..4");
                for (InvalidDomainEntity ide : invalidDomains0To4)
                {
                    log.debug(ide.getDomain());
                }
                
                // Now we'll get an overlapping page of data.
                startIndex = 1;
                
                List<InvalidDomainEntity> invalidDomains1To5 = INVALID_DOMAIN_DAO.getInvalidDomains(startIndex, pageSize);
                assertNotNull(invalidDomains1To5);
                assertEquals("result set was wrong size", pageSize, invalidDomains1To5.size());
                
                log.debug("Rows 1..5");
                for (InvalidDomainEntity ide : invalidDomains1To5)
                {
                    log.debug(ide.getDomain());
                }
                
                // Some quick checks for data consistency.
                assertEquals("Inconsistent domains", invalidDomains0To4.get(3).getDomain(),
                                                     invalidDomains1To5.get(2).getDomain()); // index = 2 because pages are offset by one.
                
                // Get a page that overruns the end of the table and ensure it's truncated ok.
                startIndex = totalTableSize - 10;
                pageSize = 50;
                
                List<InvalidDomainEntity> invalidDomainsAtEndOfTable = INVALID_DOMAIN_DAO.getInvalidDomains(startIndex, pageSize);
                assertNotNull(invalidDomainsAtEndOfTable);
                assertEquals("result set was wrong size", 10, invalidDomainsAtEndOfTable.size());
                
                return null;
            }
        });
    }
    
    @Test public void deleteInvalidDomain() throws Exception
    {
        final InvalidDomainEntity invalidDomain = TRANSACTION_HELPER.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<InvalidDomainEntity>()
        {
            @Override
            public InvalidDomainEntity execute() throws Throwable
            {
                InvalidDomainEntity domain = createInvalidDomainEntity(INVALID_DOMAIN_NAME, INVALID_DOMAIN_TYPE, INVALID_DOMAIN_NOTE);
                return domain;
            }
        });
        
        TRANSACTION_HELPER.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>()
        {
            @Override
            public Void execute() throws Throwable
            {
                InvalidDomainEntity existingInvalidDomain = INVALID_DOMAIN_DAO.getInvalidDomain(invalidDomain.getDomain());
                assertNotNull(existingInvalidDomain);
                INVALID_DOMAIN_DAO.deleteInvalidDomain(invalidDomain.getDomain());
                return null;
            }
        });
        
        TRANSACTION_HELPER.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>()
        {
            @Override
            public Void execute() throws Throwable
            {
                InvalidDomainEntity existingInvalidDomain = INVALID_DOMAIN_DAO.getInvalidDomain(invalidDomain.getDomain());
                assertNull(existingInvalidDomain);
                return null;
            }
        });
    }
    
    /**
     * This method creates a new invalid domain with the specified parameters.
     * There is no transaction handling in this method.
     * 
     * @param domain the domain to create e.g. "acme.com"
     * @param type   the invalidity type e.g. "ANTISPAM"
     * @param note   the invalidity note e.g. "Banned for being a spam-generating domain"
     */
    private InvalidDomainEntity createInvalidDomainEntity(String domain, String type, String note) throws Exception
    {
        InvalidDomainEntity invalidDomain = new InvalidDomainEntity(domain, type, note);
        invalidDomain = INVALID_DOMAIN_DAO.createInvalidDomain(invalidDomain);
        
        assertNotNull("invalid domain id is null", invalidDomain.getId());
        assertEquals("invalidDomain 'domain' is wrong", domain, invalidDomain.getDomain());
        assertEquals("invalidDomain type is wrong", type, invalidDomain.getType());
        assertEquals("invalidDomain note is wrong", note, invalidDomain.getNote());
        
        invalidDomainsToBeTidied.add(domain);
        
        return invalidDomain;
    }
    
    /**
     * @param domainToUpdate domain of the existing InvalidDomain to be updated.
     * @param newType the new value for the invalidity type. A <tt>null</tt> will mean 'no update'.
     * @param newNote the new value for the invalidity note. A <tt>null</tt> will mean 'no update'.
     */
    private void updateInvalidDomain(String domainToUpdate, String newType, String newNote)
    {
        InvalidDomainEntity entity = new InvalidDomainEntity();
        // entity id shouldn't matter here
        entity.setDomain(domainToUpdate);
        entity.setType(newType);
        entity.setNote(newNote);
        INVALID_DOMAIN_DAO.updateInvalidDomain(entity);
    }
}
