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
package org.alfresco.module.org_alfresco_module_cloud;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.alfresco.model.ContentModel;
import org.alfresco.module.org_alfresco_module_cloud.accounts.Account;
import org.alfresco.module.org_alfresco_module_cloud.accounts.AccountService;
import org.alfresco.module.org_alfresco_module_cloud.accounts.domain.AccountDAO;
import org.alfresco.module.org_alfresco_module_cloud.accounts.domain.AccountEntity;
import org.alfresco.module.org_alfresco_module_cloud.emailaddress.EmailAddressService;
import org.alfresco.module.org_alfresco_module_cloud.registration.Registration;
import org.alfresco.module.org_alfresco_module_cloud.registration.RegistrationService;
import org.alfresco.module.org_alfresco_module_cloud.util.EmailHelper.EmailTestStorage;
import org.alfresco.repo.policy.BehaviourFilter;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.security.authentication.AuthenticationUtil.RunAsWork;
import org.alfresco.repo.tenant.TenantContextHolder;
import org.alfresco.repo.tenant.TenantService;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.repo.web.scripts.BaseWebScriptTest;
import org.alfresco.service.cmr.repository.InvalidNodeRefException;
import org.alfresco.service.cmr.security.MutableAuthenticationService;
import org.alfresco.util.ApplicationContextHelper;
import org.alfresco.util.Pair;
import org.springframework.context.ApplicationContext;
import org.springframework.extensions.webscripts.TestWebScriptServer;

/**
 * Helper for Cloud Tests
 */
public class CloudTestContext
{
    private ApplicationContext applicationContext;
    private BaseWebScriptTest test;
    
    private long RUNID = System.currentTimeMillis();
    private Set<String> usersToBeTidied = new HashSet<String>();
    private List<AccountEntity> accountEntitiesToBeTidied = new ArrayList<AccountEntity>();
    private Set<Long> accountIdsToBeTidied = new HashSet<Long>();
    private Set<String> accountDomainsToBeTidied = new HashSet<String>();
    private Set<String> invalidDomainsToBeTidied = new HashSet<String>();
    private List<Registration> registrationsToBeTidied = new ArrayList<Registration>();
    private Set<Pair<String, String>> registrationPairsToBeTidied = new HashSet<Pair<String, String>>(); 
    
    private MutableAuthenticationService authenticationService;
    private RetryingTransactionHelper transactionHelper;
    private BehaviourFilter behaviourFilter;
    private AccountDAO accountDao;
    private AccountService accountService;
    private EmailAddressService emailAddressService;
    private RegistrationService registrationService;
    /**
     * This bean stores any email requests that were sent during test execution, if it is configured.
     * It may be null for some tests.
     */
    private EmailTestStorage emailTestStorage;


    public CloudTestContext()
    {
        init(ApplicationContextHelper.getApplicationContext());
    }
    
    public CloudTestContext(ApplicationContext context)
    {
        init(context);
    }
    
    public CloudTestContext(BaseWebScriptTest test)
    {
        this.test = test;
        this.test.setCustomContext("cloud-test-context.xml");
        init(test.getServer().getApplicationContext());
    }
    
    private void init(ApplicationContext context)
    {
        applicationContext = context;
        transactionHelper = (RetryingTransactionHelper)applicationContext.getBean("retryingTransactionHelper");
        behaviourFilter = (BehaviourFilter) applicationContext.getBean("policyBehaviourFilter");
        authenticationService = (MutableAuthenticationService)applicationContext.getBean("authenticationService");
        accountDao = (AccountDAO)applicationContext.getBean("accountDAO");
        accountService = (AccountService)applicationContext.getBean("accountService");
        emailAddressService = (EmailAddressService) applicationContext.getBean("emailAddressService");
        registrationService = (RegistrationService)applicationContext.getBean("registrationService");
        if (context.containsBean("emailTestStorage"))
        {
            emailTestStorage = (EmailTestStorage)context.getBean("emailTestStorage");
        }
    }
    
    public ApplicationContext getApplicationContext()
    {
        return applicationContext;
    }
    
    public TestWebScriptServer getTestServer()
    {
        return test.getServer();
    }
    
    public String createTenantName(String alias)
    {
        return alias +"-" + RUNID + ".test";
    }
    
    public String createUserName(String alias, String tenant)
    {
        return alias + "@" + tenant;
    }

    public void addUser(String user)
    {
        usersToBeTidied.add(user);
    }
    
    public void addAccountEntity(AccountEntity account)
    {
        accountEntitiesToBeTidied.add(account);
    }

    public void removeAccountEntity(AccountEntity account)
    {
        accountEntitiesToBeTidied.remove(account);
    }

    public void addAccountDomain(String domain)
    {
        accountDomainsToBeTidied.add(domain);
    }
    
    public void removeAccountDomain(String domain)
    {
        accountDomainsToBeTidied.remove(domain);
    }
    
    public void addInvalidDomain(String domain)
    {
        invalidDomainsToBeTidied.add(domain);
    }
    
    public void removeInvalidDomain(String domain)
    {
        invalidDomainsToBeTidied.remove(domain);
    }

    public void addAccount(Account account)
    {
        accountIdsToBeTidied.add(account.getId());
    }
    
    public void removeAccount(Account account)
    {
        accountIdsToBeTidied.remove(account.getId());
    }

    public void addRegistration(Registration registration)
    {
        registrationsToBeTidied.add(registration);
    }
    
    public void removeRegistration(Registration registration)
    {
        registrationsToBeTidied.remove(registration);
    }

    public void addRegistration(Pair<String, String> registration)
    {
        registrationPairsToBeTidied.add(registration);
    }
    
    public void removeRegistration(Pair<String, String> registration)
    {
        registrationPairsToBeTidied.remove(registration);
    }
    
    public EmailTestStorage getEmailTestStorage()
    {
        return this.emailTestStorage;
    }

    public void cleanup()
    {
        // belt-and-braces
        TenantContextHolder.clearTenantDomain();
        AuthenticationUtil.clearCurrentSecurityContext();
        
        AuthenticationUtil.setFullyAuthenticatedUser(AuthenticationUtil.getAdminUserName());
        TenantContextHolder.setTenantDomain(TenantService.DEFAULT_DOMAIN);
        
        RunAsWork<Void> work = new RunAsWork<Void>()
        {
            @Override
            public Void doWork() throws Exception
            {
                transactionHelper.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>()
                {
                    @SuppressWarnings("synthetic-access")
                    public Void execute() throws Throwable
                    {
                        for (Registration registration : registrationsToBeTidied)
                        {
                            try
                            {
                                registrationService.cancelRegistration(registration.getId(), registration.getKey());
                            }
                            catch(Exception e)
                            {
                            }
                        }

                        for (Pair<String, String> registration : registrationPairsToBeTidied)
                        {
                            try
                            {
                                registrationService.cancelRegistration(registration.getFirst(), registration.getSecond());
                            }
                            catch(Exception e)
                            {
                            }
                        }

                        return null;
                    }
                });
                return null;
            }
        };
        AuthenticationUtil.runAs(work, AuthenticationUtil.getSystemUserName());
        
        for (final String user : this.usersToBeTidied)
        {
            transactionHelper.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>()
            {
                @Override
                public Void execute() throws Throwable
                {
                    if (authenticationService.authenticationExists(user))
                    {
                        try
                        {
                            authenticationService.deleteAuthentication(user);
                        }
                        catch (InvalidNodeRefException e)
                        {
                            throw new SQLException("Force retry on user " + user, e);
                        }
                    }
                    return null;
                }
            }, false, true);
        }
        usersToBeTidied.clear();
        
        for (final Long account : this.accountIdsToBeTidied)
        {
            transactionHelper.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>()
            {
                @Override
                public Void execute() throws Throwable
                {
                    if (accountService.getAccount(account) != null)
                    {
                        behaviourFilter.disableBehaviour(ContentModel.ASPECT_UNDELETABLE);
                        accountService.removeAccount(account);
                    }
                    return null;
                }
            });
        }
        accountIdsToBeTidied.clear();

        for (final String domain : this.accountDomainsToBeTidied)
        {
            transactionHelper.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>()
            {
                @Override
                public Void execute() throws Throwable
                {
                    Account account = accountService.getAccountByDomain(domain);
                    if (account != null)
                    {
                        behaviourFilter.disableBehaviour(ContentModel.ASPECT_UNDELETABLE);
                        accountService.removeAccount(account.getId());
                    }
                    return null;
                }
            });
        }
        accountIdsToBeTidied.clear();

        for (AccountEntity account : this.accountEntitiesToBeTidied)
        {
            accountDao.deleteAccount(account.getId());
        }
        accountEntitiesToBeTidied.clear();
        
        
        for (String domain : invalidDomainsToBeTidied)
        {
            emailAddressService.deleteInvalidDomain(domain);
        }
        
        
        if (emailTestStorage != null)
        {
            emailTestStorage.reset();
        }
        
        
        // finally clear authentication
        AuthenticationUtil.clearCurrentSecurityContext();
    }
}
