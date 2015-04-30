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
package org.alfresco.module.org_alfresco_module_cloud_share;

import org.springframework.extensions.webscripts.URLModel;
import org.springframework.extensions.webscripts.URLModelFactory;
import org.springframework.extensions.webscripts.WebScriptRequest;

/**
 * <p>Factory for creating {@link TenantURLModel}s. This is used to override the default
 * {@link URLModelFactory} used by the WebScript framework. The result is that a Tenant
 * specific {@link URLModel} will be created for each WebScript rendering. It is tenant specific
 * in that the context contains the tenant as well as the application context.</p>
 * @author David Draper
 */
public class TenantURLModelFactory implements URLModelFactory
{
    @Override
    public URLModel createURLModel(WebScriptRequest request)
    {
        return new TenantURLModel(request);
    }
}
