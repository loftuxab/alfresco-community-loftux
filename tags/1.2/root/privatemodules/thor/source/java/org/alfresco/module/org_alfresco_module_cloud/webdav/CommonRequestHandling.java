/*
 * Copyright (C) 2005-2012 Alfresco Software Limited.
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
package org.alfresco.module.org_alfresco_module_cloud.webdav;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.alfresco.module.org_alfresco_module_cloud.accounts.AccountClass;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.tenant.TenantService;
import org.alfresco.repo.webdav.WebDAVServerException;
import org.alfresco.service.cmr.site.SiteService;
import org.alfresco.util.Pair;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.extensions.surf.util.URLDecoder;

/**
 * Encapsulates preliminary request processing common to both Cloud WebDAV and Cloud SPP implementations.
 * 
 * @author Matt Ward
 */
public class CommonRequestHandling
{
    public static final String REQ_ATTR_SPLIT_PATH = "alfresco.webdav.split.path";
    public static final String REQ_ATTR_TENANT_DOMAIN = "alfresco.webdav.tenant.domain";
    public static final String REQ_ATTR_DOCLIB_ELEMENT_ADDED = "alfresco.documentLibrary.path.element.added";
    private static final Log logger = LogFactory.getLog(CommonRequestHandling.class);
    private CloudWebDAVHelper davHelper;
    
    
    public boolean preProcessRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {        
        // Default tenant until we know otherwise.
        request.setAttribute(REQ_ATTR_TENANT_DOMAIN, TenantService.DEFAULT_DOMAIN);
        // Empty path until we know otherwise.
        request.setAttribute(REQ_ATTR_SPLIT_PATH, Collections.EMPTY_LIST);
        
        CloudWebDAVHelper webDavHelper = getDavHelper();
        
        String path = webDavHelper.getRepositoryPath(request);
        
        Pair<String, List<String>> tenantAndSplitPath = webDavHelper.getTenantAndSplitPath(path);
        String tenantDomain = tenantAndSplitPath.getFirst();
        request.setAttribute(REQ_ATTR_TENANT_DOMAIN, tenantDomain);
        
        List<String> splitPath = tenantAndSplitPath.getSecond();
        request.setAttribute(REQ_ATTR_SPLIT_PATH, splitPath);
        
        // Add the documentLibrary path element to the request if this hasn't already been done.
        if (!tenantDomain.equals(TenantService.DEFAULT_DOMAIN) && splitPath.size() > 0)
        {
            if (splitPath.size() > 1 && splitPath.get(1).startsWith("_vti_"))
            {
                if (logger.isDebugEnabled())
                {
                    logger.debug("NOT adding documentLibrary to path, as this is a site related SOAP request: " + path);
                }
            }
            else if (docLibPathElementAdded(request))
            {
                if (logger.isDebugEnabled())
                {
                    logger.debug("Would add documentLibrary to path, but already done.");
                }
            }
            else
            {
                splitPath.add(1, SiteService.DOCUMENT_LIBRARY);
                String newPath = StringUtils.join(splitPath, "/");
                newPath = request.getServletPath() + "/" + tenantDomain + "/" + newPath;
                if (logger.isDebugEnabled())
                {
                    String oldPath = request.getServletPath() + path;
                    logger.debug("Rerouting " + oldPath + " to " + newPath);
                }
                request.setAttribute(REQ_ATTR_DOCLIB_ELEMENT_ADDED, true);
                request.getRequestDispatcher(newPath).forward(request, response);
                return false;
            }
        }
        
        return true;
    }

    /**
     * Has the document library path element been added to the request path?
     * 
     * @param request
     * @return
     */
    private boolean docLibPathElementAdded(HttpServletRequest request)
    {
        Boolean added = (Boolean) request.getAttribute(REQ_ATTR_DOCLIB_ELEMENT_ADDED);
        return added != null ? added : false;
    }

    public boolean checkPrerequisites(HttpServletRequest request, HttpServletResponse response) throws IOException
    {   
        @SuppressWarnings("unchecked")
        List<String> splitPath = (List<String>) request.getAttribute(REQ_ATTR_SPLIT_PATH);
        CloudWebDAVHelper webDavHelper = getDavHelper();
        
        if (webDavHelper.isPremiumAccountsOnly())
        {
            Long accountId = webDavHelper.getDirectoryService().getHomeAccount(AuthenticationUtil.getRunAsUser());
            if ((accountId == null) ||
                (! webDavHelper.getAccountService().getAccount(accountId).getAccountClassName().equals(AccountClass.Name.PAID_BUSINESS.toString())))
            {
                // THOR-1117: premium feature only - prevent access unless current user's home network is premium-enabled
                response.sendError(new WebDAVServerException(HttpServletResponse.SC_FORBIDDEN).getHttpStatusCode());
                return false;
            }
        }
        
        String uriPath = URLDecoder.decode(request.getRequestURI());
        if (!(uriPath.contains("_vti_bin") || uriPath.contains("_vti_history") || uriPath.startsWith("/vti_inf.html")))
        {
            String method = request.getMethod();
            if (!methodAllowedForPath(method, splitPath))
            {
                response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
                return false;
            }
        }
                
        return true;
    }

    /**
     * Check whether the request method is allowed for the given path.
     * 
     * @param method The request method, e.g. "GET"
     * @param splitPath The path elements after stripping off the network name
     * @return true if the request method is allowed, false otherwise.
     */
    private boolean methodAllowedForPath(String method, List<String> splitPath)
    {
        if (withinDocumentLibrary(splitPath))
        {
            // All methods are potentially allowed within the documentLibrary
            return true;
        }
        
        // Not within documentLibrary, so need to check whether we can perform the action.
        boolean methodIsSafe = "GET".equals(method) ||
                               "HEAD".equals(method) ||
                               "OPTIONS".equals(method) ||
                               "PROPFIND".equals(method);
        return methodIsSafe;
    }

    /**
     * Is the supplied path a reference to a resource contained within
     * (and not including) the documentLibrary of a site?
     * 
     * @param splitPath The path elements - not including the network.
     * @return true if the path indicates a resource within the documentLibrary.
     */
    private boolean withinDocumentLibrary(List<String> splitPath)
    {
        return (splitPath.size() > 2 && SiteService.DOCUMENT_LIBRARY.equals(splitPath.get(1)));
    }

    private CloudWebDAVHelper getDavHelper()
    {
        return davHelper;
    }
    
    public void setDavHelper(CloudWebDAVHelper davHelper)
    {
        this.davHelper = davHelper;
    }
    
}
