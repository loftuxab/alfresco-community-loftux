/*
 * Copyright (C) 2005-2010 Alfresco Software Limited.
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

import org.springframework.extensions.webscripts.connector.AlfrescoAuthenticator;

/**
 * <p>Extends the {@link AlfrescoAuthenticator} to ensure that a {@link TenantRemoteClient}
 * is used to perform the authentication request and that the correct URL is used to perform
 * the login.</p>
 *  
 * @author David Draper
 */
public class TenantAlfrescoAuthenticator extends AlfrescoAuthenticator
{
    @Override
    protected String getLoginURL()
    {
        return TenantUtil.LOGIN_URL;
    }
}
