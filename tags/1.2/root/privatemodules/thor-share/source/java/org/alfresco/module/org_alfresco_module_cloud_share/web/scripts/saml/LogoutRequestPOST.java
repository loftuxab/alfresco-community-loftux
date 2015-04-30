package org.alfresco.module.org_alfresco_module_cloud_share.web.scripts.saml;

import org.alfresco.module.org_alfresco_module_cloud_share.TenantUtil;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.extensions.surf.UserFactory;
import org.springframework.extensions.surf.exception.ConnectorServiceException;
import org.springframework.extensions.surf.site.AuthenticationUtil;
import org.springframework.extensions.surf.support.AlfrescoUserFactory;
import org.springframework.extensions.surf.util.StringBuilderWriter;
import org.springframework.extensions.webscripts.DeclarativeWebScript;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.connector.*;
import org.springframework.extensions.webscripts.json.JSONWriter;
import org.springframework.extensions.webscripts.servlet.WebScriptServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

public class LogoutRequestPOST extends DeclarativeWebScript
{
    private static final String MIMETYPE_APPLICATION_JSON = "application/json";
    private static final String SAML_LOGOUT_REQ = "/internal/saml/slo-request/";
    private static final String SAML_LOGOUT_REQ_RESPONSE_SAMLRESPONSE = "SAMLResponse";
    private static final String SAML_LOGOUT_REQ_RESPONSE_ACTION = "action";
    private static final String SAML_LOGOUT_REQ_RESPONSE_USERID = "userId";
    private static final String SAML_LOGOUT_REQ_RESPONSE_RESULT = "result";

    private ConnectorService connectorService;

    public void setConnectorService(ConnectorService connectorService)
    {
        this.connectorService = connectorService;
    }

    @Override
    protected Map<String, Object> executeImpl(WebScriptRequest req, Status status, Cache cache)
    {
        Map<String, Object> model = new HashMap<String, Object>();

        // Transform to a http servlet request so we can use the ConnectorService and AuthenticationUtil classes
        HttpServletRequest request = ((WebScriptServletRequest)req).getHttpServletRequest();

        String currentUserId = null;

        HttpSession session = request.getSession(false);
        if (session != null)
        {
            currentUserId = (String) request.getSession().getAttribute(UserFactory.SESSION_ATTRIBUTE_KEY_USER_ID);
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

        try
        {
            // Ask repository to verify the url
            Map logoutResponse = samlLogoutRequest(request, samlProperties, currentUserId);
            model.put("idp", logoutResponse);

            // Perform local logout form Share
            AuthenticationUtil.logout(request, null);
        }
        catch (Throwable t)
        {
            // Set a global error msg label key to indicate an error has occurred
            model.put("error", "saml.logoutrequest.error.text");
        }
        return model;
    }

    private Map samlLogoutRequest(HttpServletRequest request, Map<String, String> logoutResponse, String currentUserId)
            throws IOException, ConnectorServiceException
    {
        Map<String, Object> samlLogoutResponse = new HashMap<String, Object>();

        // Verify the LogoutReponse form the IDP
        HttpSession session = request.getSession(false);
        Connector connector = connectorService.getConnector(AlfrescoUserFactory.ALFRESCO_ENDPOINT_ID, currentUserId, session);
        ConnectorContext connectorContext = new ConnectorContext(HttpMethod.POST);
        connectorContext.setContentType(MIMETYPE_APPLICATION_JSON);

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
            response = connector.call(SAML_LOGOUT_REQ + TenantUtil.getTenantName(), connectorContext, input);
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

                samlLogoutResponse.put(SAML_LOGOUT_REQ_RESPONSE_SAMLRESPONSE, json.getString(SAML_LOGOUT_REQ_RESPONSE_SAMLRESPONSE));
                // json.opt will return null, rather than empty string, when there is no value
                samlLogoutResponse.put("RelayState", json.opt("RelayState"));
                samlLogoutResponse.put(SAML_LOGOUT_REQ_RESPONSE_ACTION, json.getString(SAML_LOGOUT_REQ_RESPONSE_ACTION));
                samlLogoutResponse.put(SAML_LOGOUT_REQ_RESPONSE_USERID, json.getString(SAML_LOGOUT_REQ_RESPONSE_USERID));
                samlLogoutResponse.put(SAML_LOGOUT_REQ_RESPONSE_RESULT, json.getString(SAML_LOGOUT_REQ_RESPONSE_RESULT));
            }
            catch (JSONException jErr)
            {
                // the ticket that came back could not be parsed
                // this will cause the entire handshake to fail
                throw new IOException("Invalid SAML LogoutResponse response returned from Alfresco", jErr);
            }
        }
        else
        {
            throw new IOException("Unable to retrieve SAML LogoutResponse from Alfresco");
        }

        return samlLogoutResponse;
    }

}
