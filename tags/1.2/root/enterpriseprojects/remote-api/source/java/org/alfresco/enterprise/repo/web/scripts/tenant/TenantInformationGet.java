/*
 * Copyright 2005-2012 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.enterprise.repo.web.scripts.tenant;

import java.io.IOException;
import java.io.Writer;

import org.alfresco.repo.content.MimetypeMap;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.tenant.TenantService;
import org.json.simple.JSONObject;
import org.springframework.extensions.webscripts.AbstractWebScript;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;

/**
 * This class returns details on the current user's Multi-Tenancy setup
 * 
 * @author Nick Burch
 * @since 4.1
 */
public class TenantInformationGet extends AbstractWebScript
{
    protected static final String MULTI_TENANCY_ENABLED = "multiTenancyEnabled";
    protected static final String TENANT_USER = "tenantUser";
    protected static final String TENANT = "tenant";
    
    protected TenantService tenantService;
    
    public void setTenantService(TenantService tenantService)
    {
        this.tenantService = tenantService;
    }
    
    @Override
    public void execute(WebScriptRequest req, WebScriptResponse res) throws IOException
    {
        // Have the basic JSON Build
        JSONObject json = new JSONObject();
        buildCoreDetails(json);
        
        // Have any additional override information added
        buildAdditionalDetails(json);
        
        // Return it
        res.setContentType(MimetypeMap.MIMETYPE_JSON);
        res.setContentEncoding("UTF-8");
        Writer writer = res.getWriter();
        writer.write(json.toString());
        writer.close();
    }

    @SuppressWarnings("unchecked")
    protected void buildCoreDetails(JSONObject json)
    {
        // Is there any multi-tenancy?
        boolean isMTEnabled = tenantService.isEnabled();
        json.put(MULTI_TENANCY_ENABLED, isMTEnabled);
        
        if (isMTEnabled)
        {
            // Is the current user in a tenant?
            String username = AuthenticationUtil.getFullyAuthenticatedUser();
            boolean tenantUser = tenantService.isTenantUser();
            json.put(TENANT_USER, tenantUser);
            
            if (tenantUser)
            {
                json.put(TENANT, tenantService.getUserDomain(username));
            }
        }
    }

    /**
     * Extension / Override point, to allow for addition tenancy
     *  details to be returned to the JSON 
     */
    protected void buildAdditionalDetails(JSONObject json)
    {
        // No extra details needed
    }
}