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

import java.util.Map;
import java.util.StringTokenizer;

import org.springframework.extensions.surf.RequestContext;
import org.springframework.extensions.webscripts.DefaultURLHelper;
import org.springframework.extensions.webscripts.URLHelper;

/**
 * <p>A tenant specific implementation of the {@link URLHelper} that ensures that the current tenant
 * name is included in context requests.</p>
 *  
 * @author David Draper
 */
public class TenantURLHelper extends DefaultURLHelper
{
    private static final long serialVersionUID = 1L;

    public TenantURLHelper(RequestContext context)
    {
        super(context);
        this.tenant = TenantUtil.getTenantName();
    }
    
    /**
     * <p>Instantiates a new {@link TenantURLHelper} ensuring that the current tenant is set.</p>
     * 
     * @param context
     * @param templateArgs
     */
    public TenantURLHelper(RequestContext context, Map<String, String> templateArgs)
    {
        super(context, templateArgs);
        this.tenant = TenantUtil.getTenantName();
    }

    /**
     * <p>This will be set to the name of the current tenant when the class is instantiated. This
     * value is used as part of the context and servlet context.</p>
     */
    private final String tenant;
    
    /**
     * <p>Overrides the default implementation to ensure that the current tenant name is 
     * appended to the application context.</p>
     */
    @Override
    public String getContext()
    {
        return super.getContext() + "/" + this.tenant;
    }

    /**
     * <p>Overrides the default implementation to ensure that the current tenant name is
     * placed between the application context and the servlet context.</p>
     */
    @Override
    public String getServletContext()
    {
        String servletContext = null;
        StringTokenizer t = new StringTokenizer(super.getServletContext(), "/");
        if (t.hasMoreTokens())
        {
            // First token is context, we need to use this with the tenant...
            servletContext = t.nextToken() + "/" + this.tenant;
        }
        if (t.hasMoreTokens())
        {
            // If there is another token then it's the servlet context, so append that...
            servletContext = servletContext + "/" + t.nextToken();
        }
        return servletContext;
    }
}
