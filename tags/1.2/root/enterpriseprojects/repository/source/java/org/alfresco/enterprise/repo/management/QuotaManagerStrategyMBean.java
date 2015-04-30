/*
 * Copyright 2005-2010 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.enterprise.repo.management;

/**
 * QuotaManagerStrategy management interface.
 * 
 * @author Matt Ward
 */
public interface QuotaManagerStrategyMBean
{
    long getCurrentUsageBytes();
    double getCurrentUsageMB();
    long getMaxUsageBytes();
    long getMaxUsageMB();
    void setMaxUsageMB(long maxUsageMB);
    int getMaxFileSizeMB();
    void setMaxFileSizeMB(int maxFileSizeMB);
}
