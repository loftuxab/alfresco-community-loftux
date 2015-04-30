/*
 * Copyright (C) 2005-2013 Alfresco Software Limited.
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
package org.alfresco.module.org_alfresco_module_cloud_share;

import org.alfresco.web.scripts.DictionaryQuery;

/**
 * CLOUD-1368: override DictionaryQuery script bean (see 'slingshot-application-context.xml')
 * 
 * @author Kevin Roast
 */
public class CloudDictionaryQuery extends DictionaryQuery
{
    /**
     * For Cloud - the Repository uses a single Share DD across all Tenants.
     */
    @Override
    protected boolean isTenant()
    {
        return false;
    }
}
