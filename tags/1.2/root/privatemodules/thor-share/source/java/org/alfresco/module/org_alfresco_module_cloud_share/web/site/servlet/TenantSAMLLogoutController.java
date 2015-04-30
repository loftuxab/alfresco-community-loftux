/**
 * Copyright (C) 2005-2013 Alfresco Software Limited.
 *
 * This file is part of the Spring Surf Extension project.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.alfresco.module.org_alfresco_module_cloud_share.web.site.servlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.alfresco.module.org_alfresco_module_cloud_share.TenantUserFactory;
import org.alfresco.module.org_alfresco_module_cloud_share.TenantUtil;
import org.alfresco.web.site.servlet.SlingshotLogoutController;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONObject;
import org.springframework.extensions.surf.UserFactory;
import org.springframework.extensions.surf.uri.UriUtils;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.connector.Connector;
import org.springframework.extensions.webscripts.connector.ConnectorService;
import org.springframework.extensions.webscripts.connector.Response;
import org.springframework.web.servlet.ModelAndView;

/**
 * Thor-Share specific override of the SpringSurf dologin controller.
 *
 * Request SAML SLO
 *
 * TODO: Create an on premise version with overridable methods so this class can extend it
 *
 * @author janv, Erik Winlof
 */
public class TenantSAMLLogoutController extends SlingshotLogoutController
{
    private static Log logger = LogFactory.getLog(TenantSAMLLogoutController.class);

    public static final String SAML_ENABLED_USER_URL = "/internal/saml/enabled/user/";
    public static final String ALFRESCO_NOAUTH_ENDPOINT_ID = "alfresco-noauth";
    public static final String SAML_LOGOUTREQUEST_PAGE = "page/saml-sp-logoutrequest";

    private ConnectorService connectorService;

    public void setConnectorService(ConnectorService connectorService)
    {
        this.connectorService = connectorService;
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.framework.mvc.AbstractController#createModelAndView(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception
    {
        try
        {
            String username = null;

            // check whether there is already a user logged in
            HttpSession session = request.getSession(false);
            if (session != null)
            {
                username = (String)request.getSession().getAttribute(UserFactory.SESSION_ATTRIBUTE_KEY_USER_ID);

                if ((username != null) &&  (! username.isEmpty()))
                {
                    // check if Network/Tenant (implied by username) is SAML-enabled ...
                    Connector connector = connectorService.getConnector(ALFRESCO_NOAUTH_ENDPOINT_ID);
                    String uri = "/" + TenantUtil.DEFAULT_TENANT_NAME + SAML_ENABLED_USER_URL + username;

                    // invoke and check for OK response
                    Response resp = connector.call(uri);
                    if (Status.STATUS_OK == resp.getStatus().getCode())
                    {
                        JSONObject json = new JSONObject(resp.getResponse());

                        Boolean isSamlEnabled = json.getBoolean("isSamlEnabled");
                        String tenantDomain = json.getString("tenantDomain");

                        if (isSamlEnabled == Boolean.TRUE && tenantDomain != null && !tenantDomain.isEmpty())
                        {
                            // If Network/Tenant is SAML-enabled redirect user to IDP with a LogoutRequest and
                            // perform the actual local/share logout when the IDP is POSTING back the LogoutResponse

                            // However first check that we have a idpSessionIndex, cause if we don't have we will just perform a local logout
                            String idpSessionIndex = (String) session.getAttribute(TenantUserFactory.SESSION_ATTRIBUTE_KEY_IDP_SESSION_INDEX);
                            if (idpSessionIndex != null)
                            {
                                String redirectPage = request.getContextPath() + "/" + tenantDomain + "/" + SAML_LOGOUTREQUEST_PAGE + "?idpSessionIndex=" + idpSessionIndex;
                                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                                response.setHeader("Location", UriUtils.relativeUri(redirectPage));
                                return null;
                            }

                            // No session index existed, lets assume it was a network admin that had logged in without
                            // using the idp, in other words perform a local logout using the standard logout functionality
                        }
                    }

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
