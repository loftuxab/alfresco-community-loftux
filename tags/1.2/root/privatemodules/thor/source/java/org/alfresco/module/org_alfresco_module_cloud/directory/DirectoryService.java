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
package org.alfresco.module.org_alfresco_module_cloud.directory;

import java.util.List;

import org.alfresco.module.org_alfresco_module_cloud.accounts.AccountService.AccountMembershipType;


/**
 * Encapsulate user directory.
 * 
 * @since Thor
 */
public interface DirectoryService
{
    /**
     * Create user in directory
     *
     * @param email  user's unique id
     * @param firstName  first name of the user
     * @param lastName  last name of the user
     * @param password  user's password
     * @return the user's case sensitive userid - probably lower cased email address
     */
    public String createUser(final String email, final String firstName, final String lastName, final String password)
        throws InvalidEmailAddressException;

    /**
     * Delete user in directory.
     *
     * @param email  user's unique id
     * @since Thor Phase 2 Sprint 1
     */
    public void deleteUser(final String email);
    
    /**
     * Delete case-sensative user in directory.
     */
    public void deleteCaseSensativeUser(final String email);
    
	/**
	 * Returns the type of account membership for the user with unique id 'email' in account 'accountId'
	 * 
	 * @param email      user's unique id
	 * @param accountId  account id
	 * 
	 * @return the account membership type of the user with id email in account 'accountId'
	 */
    public AccountMembershipType getAccountMembershipType(String email, Long accountId);
    
    /**
     * Is the user a member of the account?
     * 
     * @param email      user's unique id
     * @param accountId  account id
     * 
     * @return true if the user is a member of the account
     */
    public boolean isMember(String email, Long accountId);
    
    /**
     * Does the user already exist?
     *
     * @param email  the email to check
     * @return  true => email address is already in directory
     */
    public boolean userExists(String email)
        throws InvalidEmailAddressException;
    
    /**
     * Sets the home account of the user
     * 
     * NOTE: If specified account is already a secondary, it is promoted.
     * 
     * @param email  email address of user
     * @param accountId  account id
     */
    public void setHomeAccount(String email, Long accountId);

    /**
     * Gets the home account of the user
     * 
     * @param email  email address of user
     * @return  home account (or null, if not specified)
     * @throws InvalidEmailAddressException if the email address is not a user of the system.
     */
    public Long getHomeAccount(String email);
    
    /**
     * Adds a secondary account to a user
     * 
     * NOTE: If the specified account is already the home account, an error is raised.
     * 
     * @param email
     * @param accountId
     */
    public void addSecondaryAccount(String email, Long accountId)
        throws DirectoryServiceException;
    
    /**
     * Removes a secondary account from the user
     * 
     * @param email  email address of user
     * @param accountId  secondary account id to remove
     */
    public void removeSecondaryAccount(String email, Long accountId);

    /**
     * Gets all secondary accounts for the user
     * 
     * @param email  email address of user
     * @return  the list of secondary accounts (or empty list, if none)
     */
    public List<Long> getSecondaryAccounts(String email);
    
    /**
     * Gets all accounts for the user
     * 
     * @param email the email address of the user.
     * @return  the list of all accounts (or empty list, if none)
     */
    public List<Long> getAllAccounts(String email);

    /**
     * Sets the default account of the user
     * 
     * NOTE: Default account must refer to either home or one of secondary accounts.
     * 
     * @param email  email address of user
     * @param accountId  default account id
     */
    public void setDefaultAccount(String email, Long accountId);

    /**
     * Gets the default account of the user
     * 
     * @param email  email address of user
     * @return  home account (or null, if not specified)
     */
    public Long getDefaultAccount(String email);
}
