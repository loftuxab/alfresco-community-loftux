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
package org.alfresco.module.org_alfresco_module_cloud.webscripts;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.alfresco.module.org_alfresco_module_cloud.accounts.AccountService;
import org.alfresco.module.org_alfresco_module_cloud.directory.DirectoryService;
import org.alfresco.module.org_alfresco_module_cloud.directory.InvalidEmailAddressException;
import org.alfresco.repo.content.MimetypeMap;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.tenant.TenantContextHolder;
import org.alfresco.repo.tenant.TenantService;
import org.alfresco.repo.tenant.TenantUtil;
import org.alfresco.repo.tenant.TenantUtil.TenantRunAsWork;
import org.alfresco.repo.web.scripts.RepositoryContainer;
import org.alfresco.repo.web.scripts.TenantWebScriptServletRequest;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.extensions.webscripts.Authenticator;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.Format;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptException;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;


/**
 * Repository (server-tier) container for Web Scripts
 * 
 * @author davidc
 */
public class TenantSwitchingRepositoryContainer extends RepositoryContainer
{
    protected static final Log logger = LogFactory.getLog(TenantSwitchingRepositoryContainer.class);

    private DirectoryService directoryService;
    private AccountService accountService;
    private CloudTenantAuthentication tenantAuthentication;
    
    
    public void setDirectoryService(DirectoryService service)
    {
        this.directoryService = service;
    }
    
    public void setAccountService(AccountService service)
    {
        this.accountService = service;
    }
    
    public void setTenantAuthentication(CloudTenantAuthentication service)
    {
        this.tenantAuthentication = service;
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.RuntimeContainer#executeScript(org.alfresco.web.scripts.WebScriptRequest, org.alfresco.web.scripts.WebScriptResponse, org.alfresco.web.scripts.Authenticator)
     */
    public void executeScript(final WebScriptRequest scriptReq, final WebScriptResponse scriptRes, final Authenticator auth)
        throws IOException
    {
        if (!(scriptReq instanceof TenantWebScriptServletRequest))
        {
            try
            {
                if (logger.isDebugEnabled())
                {
                    logger.debug("executeScript (non-tenant): ["+AuthenticationUtil.getFullyAuthenticatedUser()+","+TenantContextHolder.getTenantDomain()+"] "+scriptReq.getServicePath());
                }
                
                // just behave as non- tenant switching container
                TenantSwitchingRepositoryContainer.super.executeScript(scriptReq, scriptRes, auth);
            }
            finally
            {
                if (logger.isTraceEnabled())
                {
                    String tenantDomain = TenantContextHolder.getTenantDomain();
                    if (tenantDomain != null)
                    {
                        logger.trace("Tenant context not null AFTER: " + scriptReq + " - tenant: \"" + tenantDomain + "\"");
                    }
                    
                    String user = AuthenticationUtil.getFullyAuthenticatedUser();
                    if (user != null)
                    {
                        logger.trace("Security context not null AFTER: " + scriptReq + " - user: \"" + user + "\"");
                    }
                }
                
                // clear security and tenant context (THOR-1216 / THOR-1288)
                AuthenticationUtil.clearCurrentSecurityContext();
            }
            
            return;
        }
            
        String tenant = ((TenantWebScriptServletRequest)scriptReq).getTenant();
        if (tenant != null)
        {
            // handle special tenant keys
            // -super-    => run as system tenant
            // -default-  => run as user's default tenant
            String user = null;
            if (tenant.equalsIgnoreCase(TenantUtil.DEFAULT_TENANT))
            {
                // switch from default to super tenant, if not authenticated
                user = AuthenticationUtil.getFullyAuthenticatedUser();
                if (user == null)
                {
                    tenant = TenantUtil.SYSTEM_TENANT;
                }
            }
            
            // run as super tenant
            if (tenant.equalsIgnoreCase(TenantUtil.SYSTEM_TENANT))
            {
                if (logger.isDebugEnabled())
                {
                    logger.debug("executeScript (-system-): ["+user+","+tenant+"] "+scriptReq.getServicePath());
                }
                
                TenantUtil.runAsDefaultTenant(new TenantRunAsWork<Object>()
                {
                    public Object doWork() throws Exception
                    {
                        TenantSwitchingRepositoryContainer.super.executeScript(scriptReq, scriptRes, auth);
                        return null;
                        
                    }
                });
            }
            
            else
            {
                if (tenant.equalsIgnoreCase(TenantUtil.DEFAULT_TENANT))
                {
                    // run as user's default tenant
                    tenant = getUserDefaultTenant(user);
                    
                    if (logger.isDebugEnabled())
                    {
                        logger.debug("executeScript (-default-): getUserDefaultTenant ["+user+","+tenant+"] "+scriptReq.getServicePath());
                    }
                }
                
                // check that tenant exists, if not return forbidden status
                if (!tenantAuthentication.tenantExists(tenant))
                {
                    scriptRes.reset();
                    Cache cache = new Cache();
                    cache.setNeverCache(true);
                    scriptRes.setCache(cache);
                    scriptRes.setStatus(scriptReq.forceSuccessStatus() ? HttpServletResponse.SC_OK : Status.STATUS_FORBIDDEN);
                    scriptRes.setContentType(Format.HTML.toString());
                    scriptRes.setContentEncoding("UTF-8");
                    return;
                }
                
                if (logger.isDebugEnabled())
                {
                    logger.debug("executeScript: ["+user+","+tenant+"] "+scriptReq.getServicePath());
                }
                
                // run as explicit tenant
                TenantUtil.runAsTenant(new TenantRunAsWork<Object>()
                {
                    public Object doWork() throws Exception
                    {
                        TenantSwitchingRepositoryContainer.super.executeScript(scriptReq, scriptRes, auth);
                        return null;
                    }
                }, tenant);
            }
        }
    }
    
    private String getUserDefaultTenant(String user)
    {
        try
        {
            String tenant = TenantService.DEFAULT_DOMAIN;
            Long accountId = directoryService.getDefaultAccount(user);
            if (accountId != null)
            {
                tenant = accountService.getAccountTenant(accountId);
            }
            return tenant;
        }
        catch(InvalidEmailAddressException e)
        {
            // Note: shouldn't get here - error message is purposely vague so as not to reveal clues of
            //       user identity or reason for failure
            throw new WebScriptException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal Server Error");
        }
    }
}