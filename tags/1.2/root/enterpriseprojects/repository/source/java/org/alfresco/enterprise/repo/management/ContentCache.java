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

import java.io.File;

/**
 * ContentCacheMBean implementation.
 * 
 * @author Matt Ward
 */
public class ContentCache implements ContentCacheMBean
{
    private org.alfresco.repo.content.caching.ContentCache cache;
    
    
    /**
     * @param cache
     */
    public ContentCache(org.alfresco.repo.content.caching.ContentCache cache)
    {
        this.cache = cache;
    }

    @Override
    public String getType()
    {
        return cache.getClass().getName();
    }

    @Override
    public String getCacheRoot()
    {
        File cacheRoot = cache.getCacheRoot();
        return (cacheRoot != null) ? cacheRoot.getAbsolutePath() : "<not applicable>";
    }

}
