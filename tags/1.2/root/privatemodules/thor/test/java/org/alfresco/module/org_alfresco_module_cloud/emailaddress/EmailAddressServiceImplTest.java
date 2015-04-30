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
package org.alfresco.module.org_alfresco_module_cloud.emailaddress;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.alfresco.module.org_alfresco_module_cloud.CloudTestContext;
import org.alfresco.module.org_alfresco_module_cloud.emailaddress.DomainValidityCheck.FailureReason;
import org.alfresco.query.PagingRequest;
import org.alfresco.query.PagingResults;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.util.ApplicationContextHelper;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.ApplicationContext;

/**
 * Unit tests for {@link EmailAddressServiceImpl}.
 */
public class EmailAddressServiceImplTest
{
    // Services
    private static ApplicationContext TEST_CONTEXT;
    
    private static EmailAddressService EMAIL_ADDRESS_SERVICE;
    private static RetryingTransactionHelper TRANSACTION_HELPER;
    
    private CloudTestContext cloudContext;
    
    /**
     * Initialise various services required by the test.
     */
    @BeforeClass public static void initTestsContext() throws Exception
    {
        TEST_CONTEXT = ApplicationContextHelper.getApplicationContext();
        EMAIL_ADDRESS_SERVICE = (EmailAddressService) TEST_CONTEXT.getBean("emailAddressService");
        TRANSACTION_HELPER    = (RetryingTransactionHelper)TEST_CONTEXT.getBean("retryingTransactionHelper");
    }

    @Before public void init()
    {
        cloudContext = new CloudTestContext(TEST_CONTEXT);

        // Set the current security context as admin
        AuthenticationUtil.setFullyAuthenticatedUser(AuthenticationUtil.getAdminUserName());
    }
    
    @After public void cleanup()
    {
        cloudContext.cleanup();
    }
    
    @Test public void isWellFormedEmail()
    {
        assertTrue(EMAIL_ADDRESS_SERVICE.isWellFormedAddress("dave@alfresco.example"));
        assertTrue(EMAIL_ADDRESS_SERVICE.isWellFormedAddress("dave@alfresco.com"));
        assertTrue(EMAIL_ADDRESS_SERVICE.isWellFormedAddress("Dave<dave@alfresco.com>"));
        assertFalse(EMAIL_ADDRESS_SERVICE.isWellFormedAddress("dave"));
        assertFalse(EMAIL_ADDRESS_SERVICE.isWellFormedAddress("dave@alfresco.com@fred"));
        assertFalse(EMAIL_ADDRESS_SERVICE.isWellFormedAddress("@"));
    }

    @Test public void isReachableDomain()
    {
        assertTrue(EMAIL_ADDRESS_SERVICE.isReachableDomain("alfresco.example"));
        assertTrue(EMAIL_ADDRESS_SERVICE.isReachableDomain("alfresco.test"));
        assertTrue(EMAIL_ADDRESS_SERVICE.isReachableDomain("alfresco.com"));
        assertFalse(EMAIL_ADDRESS_SERVICE.isReachableDomain("alfrescoinvaliddomainshouldnotexist.com"));
    }

    @Test public void isValidDomainName()
    {
        assertTrue(EMAIL_ADDRESS_SERVICE.isValidDomainName("aa.aa.test"));
        assertTrue(EMAIL_ADDRESS_SERVICE.isValidDomainName("aa.test"));
        assertTrue(EMAIL_ADDRESS_SERVICE.isValidDomainName("a.aa.test"));
        assertTrue(EMAIL_ADDRESS_SERVICE.isValidDomainName("a.a.aa.test"));
        assertTrue(EMAIL_ADDRESS_SERVICE.isValidDomainName("aa.a.a.aa.test"));
        assertFalse(EMAIL_ADDRESS_SERVICE.isValidDomainName("a.t"));
        assertFalse(EMAIL_ADDRESS_SERVICE.isValidDomainName("aa.t"));
        assertFalse(EMAIL_ADDRESS_SERVICE.isValidDomainName("a.test"));
    }

    @Test public void getDomain()
    {
        assertNull(EMAIL_ADDRESS_SERVICE.getDomain("dave"));
        assertNull(EMAIL_ADDRESS_SERVICE.getDomain("dave@alfresco.com@fred"));
        assertNull(EMAIL_ADDRESS_SERVICE.getDomain("@"));
        assertEquals("alfresco.com", EMAIL_ADDRESS_SERVICE.getDomain("dave@alfresco.com"));
        assertEquals("alfresco.com", EMAIL_ADDRESS_SERVICE.getDomain("Dave<dave@alfresco.com>"));
    }
    
    /**
     * This test method retrieves some well-known, blocked email domains from the {@link EmailAddressService}.
     */
    @Test public void validateDomain() throws Exception
    {
        // Note that it is not the email *addresses* which are blocked, but their domains.
        final String nullDomain = null;
        final String invalidDomain = "";
        final String spamDomain = "antispam.test";
        final String publicDomain = "public.test";
        final String blacklistedDomain = "blacklisted.test";
        final String notBlockedDomain = "alfresco.com";
        
        TRANSACTION_HELPER.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>()
            {
                @SuppressWarnings("synthetic-access")
                @Override
                public Void execute() throws Throwable
                {
                    // Invalid Format
                    DomainValidityCheck validityCheck = EMAIL_ADDRESS_SERVICE.validateDomain(nullDomain);
                    assertEquals("Wrong domain", nullDomain, validityCheck.getDomain());
                    assertEquals("Wrong reason", FailureReason.INVALID_FORMAT, validityCheck.getFailureReason());
                    validityCheck = EMAIL_ADDRESS_SERVICE.validateDomain(invalidDomain);
                    assertEquals("Wrong domain", invalidDomain, validityCheck.getDomain());
                    assertEquals("Wrong reason", FailureReason.INVALID_FORMAT, validityCheck.getFailureReason());
                    
                    // Anti-spam
                    validityCheck = EMAIL_ADDRESS_SERVICE.validateDomain(spamDomain);
                    assertEquals("Wrong domain", spamDomain, validityCheck.getDomain());
                    assertEquals("Wrong reason", FailureReason.ANTISPAM, validityCheck.getFailureReason());
                    assertEquals("Wrong notes", "a note", validityCheck.getFailureNotes());
                    
                    // public
                    validityCheck = EMAIL_ADDRESS_SERVICE.validateDomain(publicDomain);
                    assertEquals("Wrong domain", publicDomain, validityCheck.getDomain());
                    assertEquals("Wrong reason", FailureReason.PUBLIC, validityCheck.getFailureReason());
                    assertEquals("public domain for Alfresco test purposes", validityCheck.getFailureNotes());
                    
                    // blacklisted
                    validityCheck = EMAIL_ADDRESS_SERVICE.validateDomain(blacklistedDomain);
                    assertEquals("Wrong domain", blacklistedDomain, validityCheck.getDomain());
                    assertEquals("Wrong reason", FailureReason.BLACKLISTED, validityCheck.getFailureReason());
                    assertEquals("Wrong notes", "This was blacklisted at request of dfdfdf@sfsds.com", validityCheck.getFailureNotes());
                    
                    // not blocked
                    validityCheck = EMAIL_ADDRESS_SERVICE.validateDomain(notBlockedDomain);
                    assertEquals("Wrong domain", notBlockedDomain, validityCheck.getDomain());
                    assertNull(validityCheck.getFailureReason());
                    assertNull(validityCheck.getFailureNotes());
               
                    return null;
                }
            });
    }
    
    /**
     * @since Thor Phase 2 Sprint 1
     * @see InvalidDomainDAOImplTest#getPagesOfInvalidDomainData()} which does deeper testing of this feature.
     */
    @Test public void getPageOfInvalidDomains() throws Exception
    {
        TRANSACTION_HELPER.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>()
        {
            
            @SuppressWarnings("synthetic-access")
            @Override
            public Void execute() throws Throwable
            {
                // This is really tested by the DAOImplTest. We'll run through the code to make sure there are no exceptions.
                PagingRequest pagingReq = new PagingRequest(4, 7);
                PagingResults<DomainValidityCheck> validityChecks = EMAIL_ADDRESS_SERVICE.getInvalidDomains(pagingReq);
                assertTrue(validityChecks.hasMoreItems());
                assertEquals(7, validityChecks.getPage().size());
                
                return null;
            }
        });
    }
    
    /**
     * @since Thor Phase 2 Sprint 1
     */
    @Test public void createInvalidDomain() throws Exception
    {
        final String domain = this.getClass().getSimpleName() + ".test";
        final FailureReason type = FailureReason.ANTISPAM;
        final String notes = "Added as part of test code";
        
        TRANSACTION_HELPER.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>()
        {
            
            @SuppressWarnings("synthetic-access")
            @Override
            public Void execute() throws Throwable
            {
                DomainValidityCheck validityCheck = EMAIL_ADDRESS_SERVICE.validateDomain(domain);
                assertNull(validityCheck.getFailureReason());
                
                EMAIL_ADDRESS_SERVICE.createInvalidDomain(domain, type, notes);
                cloudContext.addInvalidDomain(domain);
                
                return null;
            }
        });
        TRANSACTION_HELPER.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>()
        {
            @SuppressWarnings("synthetic-access")
            @Override
            public Void execute() throws Throwable
            {
                DomainValidityCheck validityCheck = EMAIL_ADDRESS_SERVICE.validateDomain(domain);
                assertEquals(domain, validityCheck.getDomain());
                assertEquals(type, validityCheck.getFailureReason());
                assertEquals(notes, validityCheck.getFailureNotes());
                
                return null;
            }
        });
    }
    
    /**
     * @since Thor Phase 2 Sprint 1
     */
    @Test public void updateInvalidDomain() throws Exception
    {
        final String domain = this.getClass().getSimpleName() + ".test";
        final FailureReason type = FailureReason.ANTISPAM;
        final String notes = "Added as part of test code";
        
        final FailureReason updatedType = FailureReason.BLACKLISTED;
        final String updatedNotes = "Updated by test code";
        
        TRANSACTION_HELPER.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>()
        {
            
            @SuppressWarnings("synthetic-access")
            @Override
            public Void execute() throws Throwable
            {
                DomainValidityCheck validityCheck = EMAIL_ADDRESS_SERVICE.validateDomain(domain);
                assertNull(validityCheck.getFailureReason());
                
                EMAIL_ADDRESS_SERVICE.createInvalidDomain(domain, type, notes);
                cloudContext.addInvalidDomain(domain);
                
                return null;
            }
        });
        TRANSACTION_HELPER.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>()
        {
            @SuppressWarnings("synthetic-access")
            @Override
            public Void execute() throws Throwable
            {
                EMAIL_ADDRESS_SERVICE.updateInvalidDomain(domain, updatedType, updatedNotes);
                return null;
            }
        });
        TRANSACTION_HELPER.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>()
        {
            @SuppressWarnings("synthetic-access")
            @Override
            public Void execute() throws Throwable
            {
                DomainValidityCheck validityCheck = EMAIL_ADDRESS_SERVICE.validateDomain(domain);
                assertEquals(domain, validityCheck.getDomain());
                assertEquals(updatedType, validityCheck.getFailureReason());
                assertEquals(updatedNotes, validityCheck.getFailureNotes());
                
                return null;
            }
        });
    }
    
    /**
     * @since Thor Phase 2 Sprint 1
     */
    @Test public void deleteInvalidDomain() throws Exception
    {
        final String domain = this.getClass().getSimpleName() + ".test";
        
        TRANSACTION_HELPER.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>()
        {
            
            @SuppressWarnings("synthetic-access")
            @Override
            public Void execute() throws Throwable
            {
                DomainValidityCheck validityCheck = EMAIL_ADDRESS_SERVICE.validateDomain(domain);
                assertNull(validityCheck.getFailureReason());
                
                EMAIL_ADDRESS_SERVICE.createInvalidDomain(domain, FailureReason.ANTISPAM, "Added as part of test code");
                cloudContext.addInvalidDomain(domain);
                
                return null;
            }
        });
        TRANSACTION_HELPER.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>()
        {
            @SuppressWarnings("synthetic-access")
            @Override
            public Void execute() throws Throwable
            {
                EMAIL_ADDRESS_SERVICE.deleteInvalidDomain(domain);
                cloudContext.removeInvalidDomain(domain);
                return null;
            }
        });
        TRANSACTION_HELPER.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>()
        {
            @SuppressWarnings("synthetic-access")
            @Override
            public Void execute() throws Throwable
            {
                DomainValidityCheck validityCheck = EMAIL_ADDRESS_SERVICE.validateDomain(domain);
                assertEquals(domain, validityCheck.getDomain());
                assertNull(validityCheck.getFailureReason());
                
                return null;
            }
        });
    }
    
    @Test public void getAddress()
    {
    	String address = EMAIL_ADDRESS_SERVICE.getAddress("Some body <Some.Body@Example.Com>");
    	
    	assertEquals("Some.Body@Example.Com", address);
    }
}
