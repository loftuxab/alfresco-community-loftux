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

import javax.servlet.http.HttpServletRequest;

import org.alfresco.web.site.SlingshotPageView;
import org.alfresco.web.site.servlet.MTAuthenticationFilter;
import org.springframework.extensions.config.ConfigElement;
import org.springframework.extensions.config.WebFrameworkConfigElement;
import org.springframework.extensions.surf.ModelObjectService;
import org.springframework.extensions.surf.RequestContext;
import org.springframework.extensions.surf.TemplatesContainer;
import org.springframework.extensions.surf.exception.PlatformRuntimeException;
import org.springframework.extensions.surf.exception.RequestDispatchException;
import org.springframework.extensions.surf.render.RenderService;
import org.springframework.extensions.surf.resource.ResourceService;
import org.springframework.extensions.surf.site.AuthenticationUtil;
import org.springframework.extensions.surf.types.Page;
import org.springframework.extensions.webscripts.Description.RequiredAuthentication;
import org.springframework.extensions.webscripts.connector.User;

/**
 * <p>A tenant specific implementation of the {@link SlingshotPageView} that ensures that all 
 * redirect URLs include the tenant. This is achieved by overriding the <code>buildLoginRedirectURL</code>
 * method.</p>
 *  
 * @author David Draper
 */
public class TenantPageView extends SlingshotPageView
{
    /**
     * <p>This is the request parameter that will trigger the metadata for the current {@link TenantUser}
     * to be refreshed. This should be appended to the end of sign-up completion e-mails to cover the
     * scenario where a user is authenticated with Share before being given access to a new tenant. Without
     * refreshing the User metadata the list of secondary tenants would be missing the new Tenant and
     * access would be incorrectly denied.</p>
     */
    public static final String REFRESH_USER_METADATA_REQUEST_PARAMETER = "refreshMetadata";
    
    public TenantPageView(WebFrameworkConfigElement webFrameworkConfiguration, 
                          ModelObjectService modelObjectService,
                          ResourceService resourceService, 
                          RenderService renderService, 
                          TemplatesContainer templatesContainer)
    {
        super(webFrameworkConfiguration, modelObjectService, resourceService, renderService, templatesContainer);
    }

    /**
     * <p>Builds a URL to use after login that will always include the tenant.</p>
     */
    @Override
    protected String buildLoginRedirectURL(HttpServletRequest request)
    {
        String suffix = request.getRequestURI().substring(request.getContextPath().length());
        String redirectUrl = request.getContextPath() + "/" + TenantUtil.getTenantName() + suffix + (request.getQueryString() != null ? ("?" + request.getQueryString()) : "");
        return redirectUrl;
    }

    @Override
    protected void renderView(RequestContext context) throws Exception
    {
        // Get the current user and the requested page
        User user = context.getUser();
        Page page = context.getPage();
        
        // For unauthenticated pages - output X-Robots-Tag HTTP headers
        if (page.getAuthentication() == RequiredAuthentication.none)
        {
            ConfigElement robotsConfig = context.getServiceRegistry().getConfigService().getConfig("Cloud").getConfigElement("x-robots-tag");
            if (robotsConfig != null)
            {
                String robots = robotsConfig.getChildValue("value");
                if (robots != null && robots.length() != 0)
                {
                    context.getResponse().setHeader("X-Robots-Tag", robots);
                }
            }
        }
        
        if (user == null || (AuthenticationUtil.isGuest(user.getId()) && page.getAuthentication() != RequiredAuthentication.none))
        {
            // If there is no User object in the context then no authentication has been done. We won't be able to
            // check for tenant access so unless the page requires no authentication the user will be redirected
            // to the login page - this is all handled by the super class...
            super.renderView(context);
        }
        else if (page.getAuthentication() == RequiredAuthentication.none)
        {
            // The page doesn't require authentication then just drop through to the super class...
            super.renderView(context);
        }
        else
        {
            // Sanity check - the user SHOULD be a TenantUser if we're using the TenantPageView
            if (user instanceof TenantUser)
            {
                TenantUser tUser = (TenantUser) user;

                // Get the name of the tenant that the User is attempting to access...
                String requestedTenant = TenantUtil.getTenantName();
                
                // Check to see if a request has been made to refresh the Users metadata. This
                // will typically occur when there is a chance that an authenticated Users metdata
                // might have become stale, e.g.  when invited to a new tenant after having already
                // been authenticated.
                String refreshMetadata = (String) context.getParameters().get(REFRESH_USER_METADATA_REQUEST_PARAMETER);
                if (refreshMetadata != null && Boolean.valueOf(refreshMetadata))
                {
                    TenantUserFactory userFactory = getUserFactory();
                    tUser = (TenantUser) userFactory.loadUser(context, user.getId());
                    MTAuthenticationFilter.getCurrentServletRequest().getSession().setAttribute(TenantUserFactory.SESSION_ATTRIBUTE_KEY_USER_OBJECT, tUser);
                    context.setUser(tUser);
                }
                
                // Check to see if the user is just accessing their home tenant...
                boolean tenantAuthenticated = (tUser.getHomeTenant().equals(requestedTenant) || 
                                               TenantUtil.DEFAULT_TENANT_NAME.equals(requestedTenant) ||
                                               (user.isAdmin() && requestedTenant.equals(TenantUtil.SYSTEM_TENANT_NAME)));
                
                if (!tenantAuthenticated)
                {
                    // ...if not the home tenant, check the list of other tenants they're authenticated for...
                    for (String allowedTenant: tUser.getSecondaryTenants())
                    {
                        if (allowedTenant.equals(requestedTenant))
                        {
                            // The user has access to the tenant
                            tenantAuthenticated = true;
                            break;
                        }
                    }
                }
                
                if (tenantAuthenticated)
                {
                    // If the user is authenticated to access the tenant then drop through to the super class
                    // implementation to render the page (this will perform the standard authentication checks
                    super.renderView(context);
                }
                else
                {
                    // If the user is not authenticated to access the page then display the restricted tenant page
                    renderRestrictedTenantPage(context);
                }
            }
            else
            {
                // If the user is NOT a tenant user then just show the restricted tenant page (this should not happen
                // unless the application has been badly configured. The TenantUserFactory should be configured in conjunction
                // with the TenantPageViewResolver in the Spring application context...
                super.renderView(context);
            }
        }
    }
    
    /**
     * <p>The default restricted tenant page id.</p>
     */
    public static final String RESTRICTED_TENANT_PAGE_ID = "restricted-tenant";
    
    /**
     * <p>Returns the id of the page to render when a tenant is requested that the user is not authorised
     * to visit</p>
     * 
     * @return
     */
    protected String getRestrictedTenantPageId()
    {
        return RESTRICTED_TENANT_PAGE_ID;
    }
    
    /**
     * <p>Redirects the user to the restricted tenant page</p>
     * @throws RequestDispatchException 
     */
    protected void renderRestrictedTenantPage(RequestContext context) throws RequestDispatchException
    {
        Page restrictedTenantPage = this.lookupPage(getRestrictedTenantPageId());
        if (restrictedTenantPage != null)
        {
            // Dispatch to the tenant restricted page
            context.setPage(restrictedTenantPage);
            dispatchPage(context, restrictedTenantPage.getId(), context.getFormatId());
        }
        else
        {
            throw new PlatformRuntimeException("No 'Tenant Restricted Page' page configured.");
        }
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
}
