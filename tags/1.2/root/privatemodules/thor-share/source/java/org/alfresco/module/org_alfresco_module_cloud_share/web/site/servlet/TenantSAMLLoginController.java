/*
 * Copyright (C) 2005-2013 Alfresco Software Limited.
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
package org.alfresco.module.org_alfresco_module_cloud_share.web.site.servlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.alfresco.module.org_alfresco_module_cloud_share.CloudLoginController;
import org.alfresco.module.org_alfresco_module_cloud_share.TenantUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONObject;
import org.springframework.extensions.surf.uri.UriUtils;
import org.springframework.extensions.surf.util.URLEncoder;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.connector.Connector;
import org.springframework.extensions.webscripts.connector.ConnectorService;
import org.springframework.extensions.webscripts.connector.Response;
import org.springframework.web.servlet.ModelAndView;

/**
 * Thor-Share specific override of the SpringSurf dologin controller.
 * <p>
 * The implementation allows SAML-enabled Networks to redirect to the Network's configured (remote) IdP.
 * 
 * @author janv, ewinlof
 */
public class TenantSAMLLoginController extends CloudLoginController
{
    private static Log logger = LogFactory.getLog(TenantSAMLLoginController.class);

    private ConnectorService connectorService;
    
    public void setConnectorService(ConnectorService connectorService)
    {
        this.connectorService = connectorService;
    }
    
    public static final String SAML_ENABLED_USER_URL = "/internal/saml/enabled/user/";
    public static final String ALFRESCO_NOAUTH_ENDPOINT_ID = "alfresco-noauth";
    
    @Override
    public ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response)
            throws Exception
    {
        String username = request.getParameter("username");

        try
        {
            // check if Network/Tenant (implied by username) is SAML-enabled ...
            Connector connector = connectorService.getConnector(ALFRESCO_NOAUTH_ENDPOINT_ID);
            String uri = "/" + TenantUtil.DEFAULT_TENANT_NAME + SAML_ENABLED_USER_URL + URLEncoder.encode(username);
            
            // invoke and check for OK response
            Response resp = connector.call(uri);
            if (Status.STATUS_OK == resp.getStatus().getCode())
            {
                JSONObject json = new JSONObject(resp.getResponse());
                
                Boolean isSamlEnabled = json.getBoolean("isSamlEnabled");
                String tenantDomain = json.getString("tenantDomain");
                Boolean isNetAdmin = json.getBoolean("isNetAdmin"); // network admin and/or alfresco admin
                
                // Note: Network Admin can use Alfresco auth (to allow them to get SAML config settings and avoid lock out due to mis-config
                if ((isNetAdmin == Boolean.FALSE) && 
                    (isSamlEnabled == Boolean.TRUE) && 
                    (tenantDomain != null) && (! tenantDomain.isEmpty()))
                {
                    // If Network/Tenant is SAML-enabled ... and this user is NOT  a (Network) Admin ... then redirect to SAML entry point
                    String redirectPage = request.getContextPath() + "/" + tenantDomain;
                    String successPage = request.getParameter("success");

                    if (successPage != null)
                    {
                        // Remove context page from success redirect if it's there:
                        String prefixToRemove = redirectPage + "/page/";
                        if (successPage.startsWith(prefixToRemove))
                        {
                           successPage = successPage.replace(prefixToRemove, "");
                        }
                        redirectPage += "?page=" + URLEncoder.encode(successPage);
                    }
                    response.sendRedirect(UriUtils.relativeUri(redirectPage));

                    return null;
                }
            }
        }
        catch (Exception e)
        {
            logger.error(e);
        }

        // drop through
        
        return super.handleRequestInternal(request, response);
    }
}