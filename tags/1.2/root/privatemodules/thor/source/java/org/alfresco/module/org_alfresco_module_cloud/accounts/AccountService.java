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

import org.alfresco.query.PagingRequest;
import org.alfresco.query.PagingResults;
import org.alfresco.util.Pair;


/**
 * Account Service for creating and managing accounts.
 * 
 * @author Neil Mc Erlean
 * @author janv
 * @since Cloud
 */
public interface AccountService
{
    final static String SORT_BY_ACCOUNT_TYPEID = "typeId";
    final static String SORT_BY_ACCOUNT_TOTAL_USER_COUNT = "totalUsers";
    final static String SORT_BY_ACCOUNT_TOTAL_USAGE_SIZE = "totalSize";

	public static enum AccountMembershipType
	{
		HomeNetwork, SecondaryNetwork;
	};

    /**
     * Creates a new account.
     *
     * @param domain            The domain to create the account under
     * @param type              The account type Id with default settings for the account
     * @param enabled           Is the account enabled
     * @return                  The Account object for the newly created account
     */
    Account createAccount(String domain, int type, boolean enabled);
    
    /**
     * Gets an Account object for an account using the account Id
     * 
     * @param accountId The Id of the account to get
     * @return Account object for the account. Returns null if account Id not found
     */
    Account getAccount(long id);
    
    /**
     * Get tenant for an account using the account Id
     * 
     * @param accountId The Id of the account to get
     * @return Tenant id associated with the account. Returns null if account Id not found
     */
    String getAccountTenant(long id);
    
    /**
     * Gets an Account object for an account via a domain, e.g. acme.com.
     * 
     * @param domain    The domain the account belongs to, e.g. acme.com
     * @return          The domain owner account, if one exists, else <tt>null</tt>.
     */
    Account getAccountByDomain(String domain);
    
    /**
     * Updates certain properties of an existing account, e.g. file quota
     *
     * @return                  The Account object for the updated account
     */
    Account updateAccount(Account accountToUpdate);
    
    /**
     * Removes an Account (and associated tenant)
     * 
     * @param id
     */
    void removeAccount(long id);
    
    /**
     * Get paged list of accounts - note: clients should set a reasonable page size (ie. not get all accounts)
     * 
     * @param (optional) filter by account type id
     * @param pagingReq paging request
     * @return accounts as defined by paging request
     */
    PagingResults<Account> getAccounts(Integer filterByAccountTypeId, PagingRequest pagingReq);
    
    /**
     * Get paged list of sorted accounts - note: clients should set a reasonable page size (ie. not get all accounts)
     * 
     * @param (optional) filter by account type id
     * @param (optional) sortByPair - null (no sort) => < typeId/totalUsers/totalSize , true/false (=> ascending/descending) >
     * @param pagingReq paging request
     * @return accounts as defined by paging request
     */
    PagingResults<Account> getAccounts(Integer filterByAccountTypeId, Pair<String, Boolean> sortByPair, PagingRequest pagingReq);
    
    /**
     * Gets the total number of accounts in the system
     * 
     * @param pagingReq paging request
     * @return accounts as defined by paging request
     */
    long getNumberOfAccounts();

//    /**
//     * Get paged list of accounts - note: clients should set a reasonable page size (ie. not get all accounts)
//     * 
//     * @param nameFilter    The name to filter accounts on
//     * @param accountType   The type of account, i.e. Free, Premium, Enterprise
//     * @return              a List of Account objects in Alfresco. Returns null if no accounts found
//     */
//    PagingResults<Account> getAccounts(PagingRequest pagingReq, String nameFilter, Integer accountType);
}
