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

import org.alfresco.error.AlfrescoRuntimeException;

/**
 * Data abstraction layer for {@link AccountEntity} entities.
 * 
 * @author Neil Mc Erlean
 * @since Alfresco Cloud Module 0.1 (Thor)
 */
public interface AccountDAO
{
    /**
     * Retrieves {@link AccountEntity} by unique ID.
     * 
     * @param id      the unique ID of the entity
     * @return        the AccountEntity or <tt>null</tt> if no Account for this domain.
     * @throws        AlfrescoRuntimeException if the ID provided is invalid
     */
    AccountEntity getAccount(Long id);
    
    /**
     * Retrieves {@link AccountEntity} by domain.
     * 
     * @param domain  the domain to query for
     * @return        the AccountEntity or <tt>null</tt> if no Account for this domain.
     */
    AccountEntity getAccount(String domain);
    
    /**
     * Creates an Account Entity
     * 
     * @return        the AccountInfo
     */
    AccountEntity createAccount(AccountEntity accountEntity);

    /**
     * Deletes an Account Entity
     * 
     * @param id      the unique ID of the entity
     */
    void deleteAccount(Long id);
    
    /**
     * Updates an Account Entity.
     * 
     * @param accountId the unique id of the entity.
     * @param newAccountTypeId the new value for the accountTypeId.
     */
    void updateAccountType(Long accountId, Integer newAccountTypeId);

    /**
     * Gets the number of accounts.
     * 
     * @return        the number of accounts
     */
    long getNumberOfAccounts();
}
