/*
 * Copyright (C) 2005-2014 Alfresco Software Limited.
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
package org.alfresco.module.org_alfresco_module_cloud.directory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.module.org_alfresco_module_cloud.CloudModel;
import org.alfresco.module.org_alfresco_module_cloud.accounts.AccountService.AccountMembershipType;
import org.alfresco.module.org_alfresco_module_cloud.emailaddress.EmailAddressService;
import org.alfresco.repo.security.authentication.AuthenticationException;
import org.alfresco.repo.security.authentication.RepositoryAuthenticationDao;
import org.alfresco.repo.transaction.RetryingTransactionHelper.RetryingTransactionCallback;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.security.MutableAuthenticationService;
import org.alfresco.service.transaction.TransactionService;

/**
 * Implementation of User Directory
 * 
 * @since Thor
 */
public class DirectoryServiceImpl implements DirectoryService
{
    /*package*/ final static String GROUP_INTERNAL_USERS = "GROUP_INTERNAL_USERS"; 
    
    private EmailAddressService emailAddressService;
    private MutableAuthenticationService authenticationService;
    private RepositoryAuthenticationDao repositoryAuthenticationDao;
    private NodeService nodeService;
    private TransactionService transactionService;
    private boolean userNamesAreCaseSensitive = false;

    public void setTransactionService(TransactionService transactionService)
    {
        this.transactionService = transactionService;
    }
    
    public void setEmailAddressService(EmailAddressService service)
    {
        this.emailAddressService = service;
    }

    public void setMutableAuthenticationService(MutableAuthenticationService service)
    {
        this.authenticationService = service;
    }

    public void setRepositoryAuthenticationDao(RepositoryAuthenticationDao service)
    {
        this.repositoryAuthenticationDao = service;
    }

    public void setNodeService(NodeService service)
    {
        this.nodeService = service;
    }


    @Override
    public String createUser(final String email, final String firstName, final String lastName, final String password)
    {
        String userId = userNamesAreCaseSensitive ? email : email.toLowerCase();
        
        // validate email address
        if (!emailAddressService.isAcceptedAddress(userId))
        {
            throw new InvalidEmailAddressException("Email address " + email + " is not a valid address");
        }
        
        // create user (authentication) object
        if (!authenticationService.isAuthenticationCreationAllowed())
        {
            throw new AlfrescoRuntimeException("Configured Authentication service does not allow creation of users");
        }
        try
        {
            // create authentication to represent user
            authenticationService.createAuthentication(userId, password == null ? null : password.toCharArray());
            
            // apply tenants aspects
            NodeRef user = getUser(userId);
            nodeService.addAspect(user, CloudModel.ASPECT_ACCOUNTS, null);
            return userId;
        }
        catch(AuthenticationException e)
        {
            throw new DuplicateEmailAddressException("Email address + " + email + " already activated");
        }
    }
    
    @Override public void deleteUser(final String email)
    {
        String userId = userNamesAreCaseSensitive ? email : email.toLowerCase();
        
        repositoryAuthenticationDao.deleteUser(userId);
    }
    
    @Override public void deleteCaseSensativeUser(final String email)
    {
        repositoryAuthenticationDao.deleteUser(email);
    }
    
    @Override
    public boolean userExists(String email)
    {
        String userId = userNamesAreCaseSensitive ? email : email.toLowerCase();
        
        // validate email address
        if (!emailAddressService.isWellFormedAddress(userId))
        {
            throw new InvalidEmailAddressException("Email address " + email + " is not a valid address");
        }
        return authenticationService.authenticationExists(userId);
    }

    @Override
    public void setHomeAccount(String email, Long accountId)
    {
        NodeRef user = getUser(email);
        setHomeAccount(user, accountId);
    }

    private void setHomeAccount(NodeRef user, Long accountId)
    {
        nodeService.setProperty(user, CloudModel.PROP_HOME_ACCOUNT, accountId);
        removeSecondaryAccount(user, accountId);
    }

    @Override
    public Long getHomeAccount(String email)
    {
        NodeRef user = getUser(email);
        return (Long)nodeService.getProperty(user, CloudModel.PROP_HOME_ACCOUNT);
    }

    private Long getHomeAccount(NodeRef user)
    {
        return (Long)nodeService.getProperty(user, CloudModel.PROP_HOME_ACCOUNT);
    }

    @Override
    public void addSecondaryAccount(String email, Long accountId)
    {
        NodeRef user = getUser(email);
        addSecondaryAccount(user, accountId);
    }

    @SuppressWarnings("unchecked")
    private void addSecondaryAccount(NodeRef user, Long accountId)
    {
        Long homeAccount = getHomeAccount(user);
        if (accountId.equals(homeAccount))
        {
            throw new DirectoryServiceException("Cannot add account " + accountId + " as a secondary, as it is already the home account");
        }
        List<Long> secondaryAccounts = (List<Long>)nodeService.getProperty(user, CloudModel.PROP_SECONDARY_ACCOUNTS);
        if (secondaryAccounts == null)
        {
            secondaryAccounts = new ArrayList<Long>();
        }
        if (!secondaryAccounts.contains(accountId))
        {
            secondaryAccounts.add(accountId);
        }
        nodeService.setProperty(user, CloudModel.PROP_SECONDARY_ACCOUNTS, (Serializable)secondaryAccounts);
    }

    @Override
    public void removeSecondaryAccount(final String email, Long accountId)
    {
        // ACE-3645: get in transaction so that user nodeId could be put into shared cached 
        RetryingTransactionCallback<NodeRef> cb = new RetryingTransactionCallback<NodeRef>()
        {
            @Override
            public NodeRef execute() throws Throwable
            {
                return getUser(email);
            }
        };
        NodeRef user = transactionService.getRetryingTransactionHelper().doInTransaction(cb , true, true);
        
        removeSecondaryAccount(user, accountId);
        repointDefaultAccountIfNecessary(user);
    }

    @SuppressWarnings("unchecked")
    private void removeSecondaryAccount(NodeRef user, Long accountId)
    {
        List<Long> secondaryAccounts = (List<Long>)nodeService.getProperty(user, CloudModel.PROP_SECONDARY_ACCOUNTS);
        if (secondaryAccounts != null)
        {
            boolean removed = secondaryAccounts.remove(accountId);
            if (removed)
            {
                nodeService.setProperty(user, CloudModel.PROP_SECONDARY_ACCOUNTS, (Serializable)secondaryAccounts);
            }
        }
    }
    
    /**
     * This method checks if the user's default account is not equal to their home account or one of their
     * secondary accounts and if so, it selects a new default account. This can never occur during normal operation
     * except very briefly within a transaction when a user is being removed from an account.
     * @since Thor Phase 2 Sprint 1
     */
    @SuppressWarnings("unchecked")
    private void repointDefaultAccountIfNecessary(NodeRef user)
    {
        Long defaultAccount = (Long) nodeService.getProperty(user, CloudModel.PROP_DEFAULT_ACCOUNT);
        Long homeAccount = (Long) nodeService.getProperty(user, CloudModel.PROP_HOME_ACCOUNT);
        List<Long> secondaryAccounts = (List<Long>) nodeService.getProperty(user, CloudModel.PROP_SECONDARY_ACCOUNTS);
        
        if (secondaryAccounts == null)
        {
            secondaryAccounts = Collections.emptyList();
        }
        
        if (defaultAccount != null)
        {
            List<Long> allAccountsWithUser = new ArrayList<Long>();
            allAccountsWithUser.add(homeAccount);
            allAccountsWithUser.addAll(secondaryAccounts);
            
            // If the default account has a value which is not in the home or secondary accounts, then it is 'wrong'.
            if ( !allAccountsWithUser.contains(defaultAccount))
            {
                // We'll select a new secondary account to be the default.
                // In the case of there being no secondary accounts, we could select the home account but this
                // would be wrong for public domain email users, so we'll not do that.
                Long newDefaultAccount = secondaryAccounts.isEmpty() ? null : secondaryAccounts.get(0);
                nodeService.setProperty(user, CloudModel.PROP_DEFAULT_ACCOUNT, newDefaultAccount);
            }
        }
    }

    @Override
    public List<Long> getSecondaryAccounts(String email)
    {
        NodeRef user = getUser(email);
        return getSecondaryAccounts(user);
    }
    
    @Override
    public AccountMembershipType getAccountMembershipType(String personId, Long accountId)
    {
    	if(accountId == null)
    	{
    		return null;
    	}
		Long homeAccountId = getHomeAccount(personId);
		if(homeAccountId == null)
		{
			return null;
		}
		AccountMembershipType type = (accountId.equals(homeAccountId) ? AccountMembershipType.HomeNetwork : AccountMembershipType.SecondaryNetwork);
		return type;
    }
    
    @Override
    public boolean isMember(String email, Long accountId)
    {
        String userId = userNamesAreCaseSensitive ? email : email.toLowerCase();
        
    	List<Long> accounts = getAllAccounts(userId);
    	return accounts.contains(Long.valueOf(accountId));
    }
    
    @SuppressWarnings("unchecked")
    private List<Long> getSecondaryAccounts(NodeRef user)
    {
        List<Long> secondaryAccounts = (List<Long>)nodeService.getProperty(user, CloudModel.PROP_SECONDARY_ACCOUNTS);
        return secondaryAccounts == null ? Collections.EMPTY_LIST : secondaryAccounts;
    }
    
    @Override
    public List<Long> getAllAccounts(String email)
    {
        NodeRef user = getUser(email);
        return getAllAccounts(user);
    }

    private List<Long> getAllAccounts(NodeRef user)
    {
        Long homeAccount = getHomeAccount(user);
        List<Long> secondaryAccounts = getSecondaryAccounts(user);
        List<Long> allAccounts = new ArrayList<Long>(secondaryAccounts.size() + 1);
        if (homeAccount != null)
        {
            allAccounts.add(homeAccount);
        }
        allAccounts.addAll(secondaryAccounts);
        return allAccounts;
    }

    private NodeRef getUser(String email)
    {
        String userId = userNamesAreCaseSensitive ? email : email.toLowerCase();
        
        // RepositoryAuthenticationDao is always case sensitive
        NodeRef user = repositoryAuthenticationDao.getUserOrNull(userId);
        if (user == null)
        {
            throw new InvalidEmailAddressException("Email address " + email + " does not exist");
        }
        return user;
    }

    @Override
    public void setDefaultAccount(String email, Long accountId)
    {
        NodeRef user = getUser(email);
        List<Long> allAccounts = getAllAccounts(user);
        if (!allAccounts.contains(accountId))
        {
            throw new DirectoryServiceException("Account " + accountId + " does not belong to user " + email);
        }
        nodeService.setProperty(user, CloudModel.PROP_DEFAULT_ACCOUNT, accountId);
    }
    
    @Override
    public Long getDefaultAccount(String email)
    {
        NodeRef user = getUser(email);
        return (Long)nodeService.getProperty(user, CloudModel.PROP_DEFAULT_ACCOUNT);
    }

    public void setUserNamesAreCaseSensitive(boolean userNamesAreCaseSensitive)
    {
        this.userNamesAreCaseSensitive = userNamesAreCaseSensitive;
    }

    public boolean isUserNamesAreCaseSensitive()
    {
        return userNamesAreCaseSensitive;
    }
}
