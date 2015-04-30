/*
 * Copyright 2005-2011 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.enterprise.repo.management;

/**
 * CachingContentStore management interface.
 * 
 * @author Matt Ward
 */
public interface CachingContentStoreMBean
{
    String getBackingStoreType();
    String getBackingStoreDescription();
    String getBackingStoreRootLocation();
    void setCacheOnInbound(boolean cacheOnInbound);
    boolean isCacheOnInbound();
    int getMaxCacheTries();
    void setMaxCacheTries(int maxCacheTries);
}
