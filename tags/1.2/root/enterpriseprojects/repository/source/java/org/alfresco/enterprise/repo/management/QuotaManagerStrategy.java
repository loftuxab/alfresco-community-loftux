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
package org.alfresco.enterprise.repo.management;

import org.alfresco.repo.content.caching.quota.StandardQuotaStrategy;

/**
 * QuotaManagerStrategyMBean implementation.
 * 
 * @author Matt Ward
 */
public class QuotaManagerStrategy implements QuotaManagerStrategyMBean
{
    private StandardQuotaStrategy quotaManager;
    
    
    public QuotaManagerStrategy(StandardQuotaStrategy quotaManager)
    {
        this.quotaManager = quotaManager;
    }

    @Override
    public long getCurrentUsageBytes()
    {
        return quotaManager.getCurrentUsageBytes();
    }

    @Override
    public double getCurrentUsageMB()
    {
        return quotaManager.getCurrentUsageMB();
    }

    @Override
    public long getMaxUsageBytes()
    {
        return quotaManager.getMaxUsageBytes();
    }

    @Override
    public long getMaxUsageMB()
    {
        return quotaManager.getMaxUsageMB();
    }

    @Override
    public void setMaxUsageMB(long maxUsageMB)
    {
        quotaManager.setMaxUsageMB(maxUsageMB);
    }

    @Override
    public int getMaxFileSizeMB()
    {
        return quotaManager.getMaxFileSizeMB();
    }

    @Override
    public void setMaxFileSizeMB(int maxFileSizeMB)
    {
        quotaManager.setMaxFileSizeMB(maxFileSizeMB);
    }
}
