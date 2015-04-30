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

import org.alfresco.module.org_alfresco_module_cloud.accounts.domain.AccountEntity;
import org.alfresco.query.AbstractCannedQueryFactory;
import org.alfresco.query.CannedQuery;
import org.alfresco.query.CannedQueryPageDetails;
import org.alfresco.query.CannedQueryParameters;
import org.alfresco.query.PagingRequest;
import org.alfresco.repo.domain.query.CannedQueryDAO;
import org.alfresco.util.Pair;

/**
 * Factory for Accounts canned query
 *
 * @author dcaruana
 * @author janv
 * @since Cloud
 */
public class AccountsCannedQueryFactory extends AbstractCannedQueryFactory<AccountEntity>
{
    private CannedQueryDAO cannedQueryDAO;
    
    public void setCannedQueryDAO(CannedQueryDAO service)
    {
        this.cannedQueryDAO = service;
    }
    
    public CannedQuery<AccountEntity> getCannedQuery(PagingRequest pagingRequest, Integer filterByAccountTypeId, Pair<String, Boolean> sortByPair)
    {
        // note: sortBy - null (no sort), true (ascending type id), false (descending type id)
        
        Boolean sortByAccountTypeId = null;
        Boolean sortByTotalUserCount = null;
        Boolean sortByTotalUsageSize = null;
        
        if (sortByPair != null)
        {
            if (AccountService.SORT_BY_ACCOUNT_TYPEID.equals(sortByPair.getFirst()))
            {
                if (sortByPair.getSecond() != null)
                {
                    sortByAccountTypeId = new Boolean(sortByPair.getSecond());
                }
            }
            else if (AccountService.SORT_BY_ACCOUNT_TOTAL_USER_COUNT.equals(sortByPair.getFirst()))
            {
                if (sortByPair.getSecond() != null)
                {
                    sortByTotalUserCount = new Boolean(sortByPair.getSecond());
                }
            }
            else if (AccountService.SORT_BY_ACCOUNT_TOTAL_USAGE_SIZE.equals(sortByPair.getFirst()))
            {
                if (sortByPair.getSecond() != null)
                {
                    sortByTotalUsageSize = new Boolean(sortByPair.getSecond());
                }
            }
        }
        
        // specific query params - eg. filter by account type id (or null if no filter should be applied) and one optional sort
        AccountCannedQueryParams paramBean = new AccountCannedQueryParams(filterByAccountTypeId, sortByAccountTypeId, sortByTotalUserCount, sortByTotalUsageSize);
        
        CannedQueryPageDetails cqpd = new CannedQueryPageDetails(pagingRequest.getSkipCount(), pagingRequest.getMaxItems());
        CannedQueryParameters parameters = new CannedQueryParameters(paramBean, cqpd, null, pagingRequest.getRequestTotalCountMax(), pagingRequest.getQueryExecutionId());
        return getCannedQuery(parameters);
    }
    
    @Override
    public CannedQuery<AccountEntity> getCannedQuery(CannedQueryParameters parameters)
    {
        final AccountsCannedQuery cq = new AccountsCannedQuery(cannedQueryDAO, parameters);
        return (CannedQuery<AccountEntity>) cq;
    }
}
