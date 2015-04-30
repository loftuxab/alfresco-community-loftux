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

/**
 * A proto-service for managing accounts. This is distinct from the {@link AccountService} which is responsible
 * for doing basic CRUD on accounts. This service is for handling larger-scale changes to accounts - typically
 * changes that require significant business logic to be executed as part of the change.
 * 
 * @author Neil Mc Erlean
 * @since Thor Phase 2 Sprint 2
 */
public interface AccountAdminService
{
    /**
     * Changes the specified account to a different account type. Note that not all changes are supported.
     * @param account          the account to change.
     * @param newAccountTypeId the new account type id.
     */
    void changeAccountType(Account account, int newAccountTypeId);
    
    /**
     * Changes the specified account to a different account type. Note that not all changes are supported.
     * @param account          the account to change.
     * @param newAccountType   the new account type.
     */
    void changeAccountType(Account account, AccountType newAccountType);
}
