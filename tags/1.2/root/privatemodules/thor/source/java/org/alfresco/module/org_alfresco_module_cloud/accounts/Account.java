/*
 * Copyright (C) 2005-2011 Alfresco Software Limited.
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

import java.util.Date;
import java.util.List;

/**
 * Interface for an Account - to get the info (current properties) for an account.
 * 
 * @author David Gildeh
 * @author Neil Mc Erlean
 * @since Thor
 */
public interface Account
{
    /**
     * Get account Id. Account Id is internally set when the account is created
     * and cannot be set
     * 
     * @return account Id for account
     */
    long getId();
    
    /**
     * Get the account name
     * 
     * @return The name of the account
     */
    String getName();
    
    /**
     * Get the account domains
     *
     * @return The account domains
     */
    List<String> getDomains();
    
    /**
     * Get the account type
     *
     * @return The account type
     */
    AccountType getType();
    
    /**
     * Gets the date the account was created
     *
     * @return  The account creation date
     */
    Date getCreationDate();
    
    /**
     * Get the account usages/quotas
     * 
     * @return
     */
    AccountUsages getUsageQuota();
    
    /**
     * Gets whether an account is enabled or not. 
     *
     * @return true = account is enabled, false = account is disabled
     */
    boolean isEnabled();
    
    /**
     * Gets the associated tenant id
     *  
     * @return  tenant id
     */
    String getTenantId();
    
    /**
     * Gets the name of this account's {@link AccountClass}.
     * @return one of the values of {@link AccountClass.Name}.
     */
    String getAccountClassName();
    
    /**
     * Gets the display-name of this account's {@link AccountClass}.
     * @return a localised display name based on {@link #getAccountClassName()}.
     */
    String getAccountClassDisplayName();
}
