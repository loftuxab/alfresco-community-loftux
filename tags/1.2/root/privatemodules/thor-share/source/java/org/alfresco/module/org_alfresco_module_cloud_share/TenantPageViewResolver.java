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

import org.alfresco.web.site.SlingshotPageViewResolver;
import org.springframework.extensions.surf.support.ThreadLocalRequestContext;
import org.springframework.extensions.surf.types.Page;
import org.springframework.extensions.webscripts.URLHelper;
import org.springframework.extensions.webscripts.URLHelperFactory;
import org.springframework.web.servlet.view.AbstractUrlBasedView;

/**
 * <p>A tenant specific implementation of the {@link SlingshotPageViewResolver}. This modifies the
 * inherited behaviour to ensure that a {@link TenantPageView} is instantiated and that a  
 * {@link URLHelperFactory} is assigned to it. This is important so that login redirect URLs are 
 * set correctly and that the URL context available in the {@link URLHelper} will contain the
 * tenant name.</p>
 *   
 * @author David Draper
 */
public class TenantPageViewResolver extends SlingshotPageViewResolver
{
    @Override
    protected AbstractUrlBasedView buildView(String viewName)
    {
        TenantPageView view = null;
        Page page = ThreadLocalRequestContext.getRequestContext().getPage();
        if (page != null)
        {
            view = new TenantPageView(getWebframeworkConfigElement(), 
                                      getModelObjectService(), 
                                      getWebFrameworkResourceService(), 
                                      getWebFrameworkRenderService(),
                                      getTemplatesContainer());
            view.setUrl(viewName);
            view.setPage(page);
            view.setUriTokens(ThreadLocalRequestContext.getRequestContext().getUriTokens());
            view.setUrlHelperFactory(getUrlHelperFactory());
            view.setUserFactory(getUserFactory());
        }
        return view;
    }
    
    /**
     * <p>A {@link TenantUserFactory} is required when it is necessary to refresh an already authenticated
     * users metadata. This is primarily required for updating the list of secondary tenants that the 
     * user has access to.</p>
     */
    private TenantUserFactory userFactory;

    /**
     * @return A {@link TenantUserFactory}
     */
    public TenantUserFactory getUserFactory()
    {
        return userFactory;
    }

    /**
     * <p>Set the {@link TenantUserFactory}.</p>
     * @param userFactory
     */
    public void setUserFactory(TenantUserFactory userFactory)
    {
        this.userFactory = userFactory;
    }

    @Override
    protected Object getCacheKey(String viewName, Locale locale)
    {
        return new StringBuilder(64).append(TenantUtil.getTenantName())
                                    .append('_').append(viewName).toString();
    }
}
