/*
 * Copyright 2005-2013 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.module.org_alfresco_module_cloud.authentication.saml.webscripts;

import java.util.HashMap;
import java.util.Map;

import org.alfresco.module.org_alfresco_module_cloud.authentication.saml.SAMLConfigAdminService;
import org.alfresco.module.org_alfresco_module_cloud.networkadmin.NetworkAdmin;
import org.alfresco.repo.tenant.TenantService;
import org.alfresco.repo.tenant.TenantUtil;
import org.alfresco.repo.tenant.TenantUtil.TenantRunAsWork;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.DeclarativeWebScript;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptException;
import org.springframework.extensions.webscripts.WebScriptRequest;

/**
 * SAML Enabled
 * 
 * This class is the controller for the "saml-enabled.get" web script.
 * 
 * NOTE: This is an unauthenticated webscript, required by Share, 
 *       hence it will only return true if the Network is SAML-enabled else return false (whether the Network or User actually exist)
 * 
 * @author janv
 * @since Cloud SAML
 */
public class SAMLEnabledGet extends DeclarativeWebScript
{
    private static final Log logger = LogFactory.getLog(SAMLEnabledGet.class);
    
    protected SAMLConfigAdminService samlConfigAdminService;
    private NetworkAdmin networkAdmin;
    private TenantService tenantService;
    
    public void setSamlConfigAdminService(SAMLConfigAdminService service)
    {
        this.samlConfigAdminService = service;
    }
    
    public void setNetworkAdmin(NetworkAdmin networkAdmin)
    {
        this.networkAdmin = networkAdmin;
    }
    
    public void setTenantService(TenantService tenantService)
    {
        this.tenantService = tenantService;
    }
    
    private static final String PARAM_NETWORK_TENANT_DOMAIN  = "tenant_domain";
    private static final String PARAM_EMAIL  = "email";
    
    
    @Override
    protected Map<String, Object> executeImpl(WebScriptRequest req, Status status, Cache cache)
    {
        Map<String, String> templateVars = req.getServiceMatch().getTemplateVars();
        
        String userName = null;
        String tenantDomain = templateVars.get(PARAM_NETWORK_TENANT_DOMAIN);
        if (tenantDomain == null || tenantDomain.length() == 0)
        {
            userName = templateVars.get(PARAM_EMAIL);
            if (userName == null || userName.length() == 0)
            {
                throw new WebScriptException(Status.STATUS_NOT_FOUND, "Network/Tenant domain (or Email userid) not specified");
            }
        }
                
        Boolean isNetAdmin = null;
        
        if (userName != null)
        {
            // get user's implied primary network (whether user exists or not)
            int idx = userName.indexOf(TenantService.SEPARATOR);
            if ((idx >= 0) && (idx < (userName.length()-1)))
            {
                tenantDomain = userName.substring(idx+1);
            }
            else
            {
                tenantDomain = userName;
            }
            
            isNetAdmin = Boolean.FALSE;
            
            if (tenantService.getTenant(tenantDomain) != null)
            {
                try
                {
                    isNetAdmin = TenantUtil.runAsUserTenant(new TenantRunAsWork<Boolean>()
                    {
                        public Boolean doWork()
                        {
                            networkAdmin.checkNetworkAdmin(); // currently throws WebScriptException if not Network Admin or Alfresco Admin
                            return Boolean.TRUE;
                        }
                    }, userName, tenantDomain);
                }
                catch (Throwable t)
                {
                    // assume user is not an (Network) Admin
                }
            }
        }
        
        // CLOUD-1379 The 'check' is commented to allow cross network invites
        // String currentTenantDomain = TenantContextHolder.getTenantDomain();
        // if ((currentTenantDomain != null) && (! currentTenantDomain.equals(tenantDomain)))
        // {
        // throw new WebScriptException(Status.STATUS_NOT_FOUND, "Network/Tenant domain mismatch: current="+currentTenantDomain+", requested="+tenantDomain);
        // }
        
        // note: if tenant/user does not exist then return enabled=false (rather than throw error)        
        Boolean isSamlEnabled = samlConfigAdminService.isEnabled(tenantDomain);
        
        Map<String, Object> model = new HashMap<String, Object>();
        
        model.put("isSamlEnabled", isSamlEnabled);
        model.put("tenantDomain", tenantDomain);
        
        if (isNetAdmin != null)
        {
            model.put("isNetAdmin", isNetAdmin);
        }
        
        if (logger.isDebugEnabled())
        {
            logger.debug("SAMLEnabledGet: "+model);
        }
        
        return model;
    }
}
