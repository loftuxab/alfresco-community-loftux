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

import org.springframework.extensions.surf.RequestContext;
import org.springframework.extensions.surf.WebFrameworkConstants;
import org.springframework.extensions.surf.mvc.PageTypeViewResolver;
import org.springframework.extensions.surf.mvc.PageView;
import org.springframework.extensions.surf.support.ThreadLocalRequestContext;
import org.springframework.extensions.surf.types.Page;
import org.springframework.extensions.surf.types.Theme;
import org.springframework.web.servlet.view.AbstractUrlBasedView;

public class TenantPageTypeViewResolver extends PageTypeViewResolver
{
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
    
    /**
     * <p>Override the cache key behaviour to ensure cache keys contain the tenant name. This
     * guarantees that pages will be cached for all tenants and not just the first tenant they're
     * requested for.</p>
     */
    @Override
    protected Object getCacheKey(String viewName, Locale locale)
    {
        return TenantUtil.getTenantName() + "_" + viewName + "_" + locale;
    }
    
    /**
     * <p>Overrides inherited method to ensure that a {@link TenantPageView} is instantiated
     * rather than a {@link PageView}.</p>
     */
    protected AbstractUrlBasedView buildView(String viewName) throws Exception
    {
        TenantPageView view = null;

        // request context
        RequestContext context = ThreadLocalRequestContext.getRequestContext();

        String pageTypeId = processView(viewName);
        if (pageTypeId != null)
        {
            // determine which page to use based on requested type
            String pageId = null;

            // theme binding
            String themeId = (String) context.getThemeId();
            if (themeId != null)
            {
                Theme theme = getModelObjectService().getTheme(themeId);
                if (theme != null)
                {
                    pageId = theme.getPageId(pageTypeId);
                }
            }

            // system default page
            if (pageId == null)
            {
                pageId = getWebframeworkConfigElement().getDefaultPageTypeInstanceId(pageTypeId);
            }

            // use a generic page
            if (pageId == null)
            {
                pageId = getWebframeworkConfigElement().getDefaultPageTypeInstanceId(WebFrameworkConstants.GENERIC_PAGE_TYPE_DEFAULT_PAGE_ID);
            }

            // build a page view
            Page page = lookupPage(pageId);
            if (page != null)
            {
                ThreadLocalRequestContext.getRequestContext().setPage(page);
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
        }
        return view;
    }
}
