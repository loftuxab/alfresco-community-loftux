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

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.extensions.webscripts.connector.RemoteClient;
import org.springframework.extensions.webscripts.connector.Response;
import org.springframework.web.servlet.LocaleResolver;

/**
 * <p>A locale specific implementation of the {@link RemoteClient} that ensures that requests made to 
 * the remote endpoint include an accept-language based on the locale of the calling thread.</p> 
 */
public class LocaleTenantRemoteClient extends TenantRemoteClient
{
    private LocaleResolver localeResolver;
    
    public void setLocaleResolver(LocaleResolver service)
    {
        localeResolver = service;
    }
    
    public Response call(String uri, HttpServletRequest req, HttpServletResponse res)
    {
        Locale locale = localeResolver.resolveLocale(req);
        if (locale != null)
        {
            req = new LocaleHttpServletRequest(req, locale);
        }
        return super.call(uri, req, res);
    }

}
