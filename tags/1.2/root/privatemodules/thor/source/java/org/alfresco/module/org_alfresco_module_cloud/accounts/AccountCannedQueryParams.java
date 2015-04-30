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

import org.alfresco.error.AlfrescoRuntimeException;


/**
 * Account CQ parameters - for query context and filtering
 *
 * @author janv
 * @since Cloud
 */
public class AccountCannedQueryParams
{
    private Integer filterByAccountTypeId = null;
    private Boolean sortByAccountTypeId = null;
    private Boolean sortByTotalUserCount = null;
    private Boolean sortByTotalUsageSize = null;
    
    // Note 
    // - currently the sorts are mutually exclusive - although could imagine sorting by account type id and then one of the counts
    // - The Booleans are tri-value - null (no sort), true (ascending type id), false (descending type id)
    
    // TODO - use sortPairs ...
    public AccountCannedQueryParams(Integer filterByAccountTypeId, Boolean sortByAccountTypeId, Boolean sortByTotalUserCount, Boolean sortByTotalUsageSize)
    {
        this.filterByAccountTypeId = filterByAccountTypeId;
        
        if (((sortByAccountTypeId  != null) && ((sortByTotalUserCount != null) || sortByTotalUsageSize != null)) ||
            ((sortByTotalUserCount != null) && ((sortByAccountTypeId  != null) || sortByTotalUsageSize != null)) ||
            ((sortByTotalUsageSize != null) && ((sortByAccountTypeId  != null) || sortByTotalUserCount != null)))
        {
            throw new AlfrescoRuntimeException("Only one sort param currently supported !");
        }
        
        this.sortByAccountTypeId = sortByAccountTypeId;
        this.sortByTotalUserCount = sortByTotalUserCount;
        this.sortByTotalUsageSize = sortByTotalUsageSize;
    }
    
    public Integer getFilterByAccountTypeId()
    {
        return filterByAccountTypeId;
    }
    
    public Boolean getSortByAccountTypeId()
    {
        return sortByAccountTypeId;
    }
    
    public Boolean getSortByTotalUserCount()
    {
        return sortByTotalUserCount;
    }
    
    public Boolean getSortByTotalUsageSize()
    {
        return sortByTotalUsageSize;
    }
    
    public boolean isAscTrue()
    {
        return true;
    }
}
