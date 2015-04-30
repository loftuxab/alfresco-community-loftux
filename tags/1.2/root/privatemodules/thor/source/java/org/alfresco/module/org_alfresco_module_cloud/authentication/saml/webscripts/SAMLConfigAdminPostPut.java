/* 
 * Copyright 2005-2014 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.module.org_alfresco_module_cloud.authentication.saml.webscripts;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.alfresco.module.org_alfresco_module_cloud.authentication.saml.SAMLConfigSettings;
import org.alfresco.module.org_alfresco_module_cloud.authentication.saml.core.SAMLCertificateUtil;
import org.alfresco.repo.tenant.TenantUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptException;
import org.springframework.extensions.webscripts.WebScriptRequest;

/**
 * SAML Config Admin POST/PUT
 * 
 * This class is the controller for the "saml-config-admin.post/put" web scripts.
 * 
 * @author janv, jkaabimofrad
 * @since Cloud SAML
 */
public class SAMLConfigAdminPostPut extends AbstractSAMLConfigAdminWebScript
{
    private static final Log logger = LogFactory.getLog(SAMLConfigAdminPostPut.class);

    @Override
    protected Map<String, Object> unprotectedExecuteImpl(WebScriptRequest req, Status status, Cache cache)
    {
        String tenantDomain = TenantUtil.getCurrentDomain();
        // Check if the account is Enterprise Network
        validateAccount(tenantDomain);

        JSONObject json = null;
        try
        {
            json = new JSONObject(new JSONTokener(req.getContent().getContent()));

            final boolean ssoEnabled = json.optBoolean(PARAM_SSO_ENABLED);
            final String idpSsoURL = (String)json.opt(PARAM_IDP_SSO_URL);
            final String idpSloRequestURL = (String)json.opt(PARAM_IDP_SLO_REQUEST_URL);
            final String idpSloResponseURL = (String)json.opt(PARAM_IDP_SLO_RESPONSE_URL);
            final Boolean autoProvisionEnabled = (Boolean)json.opt(PARAM_AUTO_PROVISION_ENABLED);
            final Boolean alfrescoLoginCredentialEnabled = (Boolean)json.opt(PARAM_ALFRESCO_LOGIN_CREDENTIAL_ENABLED);
            final String b64EncodedCertificate = (String)json.opt(PARAM_B64_ENCODED_CERTIFICATE);
            final byte[] decodedCertificate = (StringUtils.isEmpty(b64EncodedCertificate)) ? null : SAMLCertificateUtil.decodeCertificate(b64EncodedCertificate);
            final String issuer = (String) json.opt(PARAM_ENTITY_ID);

            SAMLConfigSettings samlConfigSettings = new SAMLConfigSettings.Builder(ssoEnabled).idpSsoURL(idpSsoURL)
                .idpSloRequestURL(idpSloRequestURL).idpSloResponseURL(idpSloResponseURL).autoProvisionEnabled(autoProvisionEnabled)
                .alfrescoLoginCredentialEnabled(alfrescoLoginCredentialEnabled)
                .encodedCertificate(decodedCertificate).issuer(issuer).build();

            samlConfigAdminService.setSamlConfigs(samlConfigSettings);

            if(logger.isDebugEnabled())
            {
                logger.debug("SAMLConfigAdminPostPut: " + tenantDomain + " " + samlConfigSettings);
            }
        }
        catch(IOException e)
        {
            throw new WebScriptException(Status.STATUS_BAD_REQUEST, "Could not read content from req.", e);
        }
        catch(JSONException e)
        {
            throw new WebScriptException(Status.STATUS_BAD_REQUEST, "Could not parse JSON from req.", e);
        }
        catch(Throwable t)
        {
            throw new WebScriptException(Status.STATUS_BAD_REQUEST, "Could not set SAML configuration for [" + tenantDomain + "]", t);
        }

        Map<String, Object> model = new HashMap<String, Object>();
        return model;
    }
}
