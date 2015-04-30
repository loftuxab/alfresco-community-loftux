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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.alfresco.module.org_alfresco_module_cloud_share.TenantUtil;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.extensions.surf.UserFactory;
import org.springframework.extensions.surf.exception.ConnectorServiceException;
import org.springframework.extensions.surf.exception.PlatformRuntimeException;
import org.springframework.extensions.surf.site.AuthenticationUtil;
import org.springframework.extensions.surf.support.AlfrescoUserFactory;
import org.springframework.extensions.surf.util.StringBuilderWriter;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.connector.Connector;
import org.springframework.extensions.webscripts.connector.ConnectorContext;
import org.springframework.extensions.webscripts.connector.ConnectorService;
import org.springframework.extensions.webscripts.connector.HttpMethod;
import org.springframework.extensions.webscripts.connector.Response;
import org.springframework.extensions.webscripts.json.JSONWriter;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

/**
 * TODO: Create a SAMLLogoutResponseController (for on premise) with overridable methods so this class can extend it
 *
 * Responds to SAML SLO Response POSTs to allow the user to logout from the web site.
 *
 * @author janv, Erik Winlof
 */
public class TenantSAMLIDPLogoutResponseController extends AbstractController
{
    public static final String ALFRESCO_NOAUTH_ENDPOINT_ID = "alfresco-noauth";
    
    protected static final String SAML_LOGOUT_RESP = "/internal/saml/slo-response";
    protected static final String MIMETYPE_APPLICATION_JSON = "application/json";

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
        if (!request.getMethod().equals("POST"))
        {
            throw new ServletException("SAML LogoutResponse MUST be submitted using method POST");
        }

        request.setCharacterEncoding("UTF-8");

        // Redirect user to the network's SAML login page after logout
        String redirectUrl = request.getContextPath() + "/" + TenantUtil.getTenantName();
        try
        {
            String currentUserId = null;
            
            HttpSession session = request.getSession(false);
            if (session != null)
            {
                currentUserId = (String)request.getSession().getAttribute(UserFactory.SESSION_ATTRIBUTE_KEY_USER_ID);
            }
            
            // Transform the SAML LogoutResponse parameters to a Map
            Map<String,String> samlProperties = new HashMap<String, String>();
            Enumeration params = request.getParameterNames();
            String param;
            while (params.hasMoreElements())
            {
                param = (String) params.nextElement();
                samlProperties.put(param, request.getParameter(param));
            }
            
            // Process the SAML LogoutResponse
            String idpUserId = samlLogoutResponse(request, samlProperties, currentUserId);
            if (currentUserId == null || !currentUserId.equals(idpUserId))
            {
                // In the very unlikely case that the usernames don't match log it (but log out the user anyway)
                logger.error("SAML - IDP tried to logout user '" + idpUserId +
                        "' but the logged in user according to Share was '" + currentUserId + "'");
            }

            if (samlProperties.containsKey("RelayState"))
            {
               // set redirectURL - relay state should contain just the message string:
               redirectUrl = request.getContextPath() + "/" + TenantUtil.getTenantName() + "/page/message?text=" + samlProperties.get("RelayState") ;
            }
            // Finally logout of the Share session and redirect user to the SAML login page
            AuthenticationUtil.logout(request, response);
        }
        catch (Throwable err)
        {
            // Display error page
            redirectUrl = request.getContextPath() + "/" + TenantUtil.getTenantName() + "/page/message?text=saml.idp-logoutresponse.error.text";
        }
        response.sendRedirect(redirectUrl);
        return null;
    }

    private String samlLogoutResponse(HttpServletRequest request, Map<String, String> logoutResponse, String currentUserId)
            throws IOException, ConnectorServiceException, PlatformRuntimeException
    {
        String idpUserId = null;
        Connector connector = null;
        
        if (currentUserId != null)
        {
            // Verify the LogoutReponse form the IDP
            HttpSession session = request.getSession(false);
            connector = connectorService.getConnector(AlfrescoUserFactory.ALFRESCO_ENDPOINT_ID, currentUserId, session);
        }
        else
        {
            connector = connectorService.getConnector(ALFRESCO_NOAUTH_ENDPOINT_ID);
        }
        
        ConnectorContext connectorContext = new ConnectorContext(HttpMethod.POST);
        connectorContext.setContentType(MIMETYPE_APPLICATION_JSON);

        if (logger.isDebugEnabled())
            logger.debug("Verifying SAML LogoutResponse");

        StringBuilderWriter buf = new StringBuilderWriter(512);
        JSONWriter writer = new JSONWriter(buf);
        String[] keys = logoutResponse.keySet().toArray(new String[0]);
        writer.startObject();
        for (int i = 0; i < keys.length; i++)
        {
            writer.writeValue(keys[i], logoutResponse.get(keys[i]));
        }
        writer.endObject();
        InputStream input = new ByteArrayInputStream(buf.toString().getBytes());

        Response response = null;
        try
        {
            response = connector.call(SAML_LOGOUT_RESP + "/" + TenantUtil.getTenantName(), connectorContext, input);
        }
        finally
        {
            input.close();
        }

        // read back the userid
        if (response.getStatus().getCode() == Status.STATUS_OK)
        {
            try
            {
                JSONObject json = new JSONObject(response.getResponse());
                idpUserId = json.getString("userId");
            }
            catch (JSONException jErr)
            {
                // the ticket that came back could not be parsed
                // this will cause the entire handshake to fail
                throw new IOException(
                        "Unable to retrieve logout userId from Alfresco", jErr);
            }
        }
        else
        {
            throw new PlatformRuntimeException("Log out failed, received response code: " + response.getStatus().getCode());
        }

        return idpUserId;
    }
}
