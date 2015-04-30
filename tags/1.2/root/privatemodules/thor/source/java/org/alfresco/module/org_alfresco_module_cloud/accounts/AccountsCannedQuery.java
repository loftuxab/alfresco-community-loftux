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

import java.util.List;

import org.alfresco.module.org_alfresco_module_cloud.accounts.domain.AccountEntity;
import org.alfresco.query.AbstractCannedQuery;
import org.alfresco.query.CannedQueryPageDetails;
import org.alfresco.query.CannedQueryParameters;
import org.alfresco.repo.domain.query.CannedQueryDAO;

/**
 * Canned query for listing Accounts
 * 
 * @author dcaruana
 * @author janv
 * @since Cloud
 */
public class AccountsCannedQuery extends AbstractCannedQuery<AccountEntity>
{
    private static final String QUERY_NAMESPACE = "alfresco.accounts";
    private static final String QUERY_SELECT_ACCOUNTS = "select_Accounts";
    private static final String QUERY_SELECT_ACCOUNTS_SORT_BY_TOTAL_USER_COUNT = "select_AccountsSortByTotalUserCount";
    private static final String QUERY_SELECT_ACCOUNTS_SORT_BY_TOTAL_USAGE_SIZE = "select_AccountsSortByTotalUsageSize";

    private final CannedQueryDAO cannedQueryDAO;
    
    protected AccountsCannedQuery(CannedQueryDAO cannedQueryDAO, CannedQueryParameters parameters)
    {
        super(parameters);
        this.cannedQueryDAO = cannedQueryDAO;
    }
    
    @Override
    protected List<AccountEntity> queryAndFilter(CannedQueryParameters parameters)
    {
        // get paramBean - note: this currently has both optional filter and optional sort param
        AccountCannedQueryParams paramBean = (AccountCannedQueryParams)parameters.getParameterBean();
        
        int skip = parameters.getPageDetails().getSkipResults();
        
        // NOTE: add one to allow test for more items beyond page. The additional row will be filtered
        //       out by post query paging
        int pageSize = parameters.getPageDetails().getPageSize();
        pageSize = (pageSize == CannedQueryPageDetails.DEFAULT_PAGE_SIZE) ? pageSize : pageSize +1;
        
        
        int requestTotalCountMax = getParameters().getTotalResultCountMax();
        if (requestTotalCountMax > 0)
        {
            // if total count (upto a max) has been requested then we need to adjust DB-level offset / limit
            skip = 0;
            if (requestTotalCountMax > pageSize)
            {
                pageSize = requestTotalCountMax;
            }
        }
        
        if (paramBean.getSortByTotalUserCount() != null)
        {
            return cannedQueryDAO.executeQuery(QUERY_NAMESPACE, QUERY_SELECT_ACCOUNTS_SORT_BY_TOTAL_USER_COUNT, paramBean, skip, pageSize);
        }
        else if (paramBean.getSortByTotalUsageSize() != null)
        {
            return cannedQueryDAO.executeQuery(QUERY_NAMESPACE, QUERY_SELECT_ACCOUNTS_SORT_BY_TOTAL_USAGE_SIZE, paramBean, skip, pageSize);
        }
        else
        {
            return cannedQueryDAO.executeQuery(QUERY_NAMESPACE, QUERY_SELECT_ACCOUNTS, paramBean, skip, pageSize);
        }
    }
}
