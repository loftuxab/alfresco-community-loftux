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
import org.alfresco.module.org_alfresco_module_cloud.authentication.saml.SAMLCertificateExpiredException;
import org.alfresco.module.org_alfresco_module_cloud.authentication.saml.SAMLCertificateNotYetValidException;
import org.alfresco.module.org_alfresco_module_cloud.authentication.saml.SAMLConfigSettings;
import org.alfresco.module.org_alfresco_module_cloud.authentication.saml.core.SAMLCertificateUtil;
import org.alfresco.repo.tenant.TenantUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptException;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.servlet.FormData;

/**
 * SAML Config Admin POST. This class is the controller for the "saml-config-admin-multipart.post" web scripts.
 * 
 * @author jkaabimofrad
 * @since Cloud SAML
 * 
 */
public class SAMLConfigAdminMultipartPost extends AbstractSAMLConfigAdminWebScript
{
    private static final Log logger = LogFactory.getLog(SAMLConfigAdminMultipartPost.class);

    @Override
    protected Map<String, Object> unprotectedExecuteImpl(WebScriptRequest req, Status status, Cache cache)
    {
        String tenantDomain = TenantUtil.getCurrentDomain();
        // Check if the account is Enterprise Network
        validateAccount(tenantDomain);

        boolean ssoEnabled = false;
        String idpSsoURL = null;
        String idpSloRequestURL = null;
        String idpSloResponseURL = null;
        Boolean autoProvisionEnabled = null;
        Boolean alfrescoLoginCredentialEnabled = null;
        byte[] encodedCertificate = null;
        String issuer = null;

        FormData formData = (FormData)req.parseContent();
        if(formData == null || !formData.getIsMultiPart())
        {
            throw new WebScriptException(Status.STATUS_BAD_REQUEST + "Request is not multi-part form data.");
        }

        for(FormData.FormField field : formData.getFields())
        {
            if(field.getName().equals("ssoEnabled"))
            {
                // Mandatory parameter
                ssoEnabled = Boolean.parseBoolean(field.getValue());
            }
            else if(field.getName().equals("idpSsoURL"))
            {
                idpSsoURL = field.getValue();
            }
            else if(field.getName().equals("idpSloRequestURL"))
            {
                idpSloRequestURL = field.getValue();
            }
            else if(field.getName().equals("idpSloResponseURL"))
            {
                idpSloResponseURL = field.getValue();
            }
            else if(field.getName().equals("autoProvisionEnabled"))
            {
                autoProvisionEnabled = getBooleanValue(field.getValue());
            }
            else if(field.getName().equals("alfrescoLoginCredentialEnabled"))
            {
                alfrescoLoginCredentialEnabled = getBooleanValue(field.getValue());
            }
            else if(field.getName().equals("certificate") && field.getIsFile())
            {
                encodedCertificate = SAMLCertificateUtil.loadCertificate(field.getContent().getInputStream());
            }
            else if(field.getName().equals("issuer"))
            {
                issuer = field.getValue();
            }
        }

        SAMLConfigSettings samlConfigSettings = new SAMLConfigSettings.Builder(ssoEnabled).idpSsoURL(idpSsoURL)
            .idpSloRequestURL(idpSloRequestURL).idpSloResponseURL(idpSloResponseURL).autoProvisionEnabled(autoProvisionEnabled)
            .alfrescoLoginCredentialEnabled(alfrescoLoginCredentialEnabled).encodedCertificate(encodedCertificate).issuer(issuer)
            .build();

        // set saml config settings
        setSamlConfigs(samlConfigSettings, formData);

        if(logger.isDebugEnabled())
        {
            logger.debug("SAMLConfigAdminPostPut: " + tenantDomain + " " + samlConfigSettings);
        }

        Map<String, Object> model = new HashMap<String, Object>();
        return model;
    }

    private void setSamlConfigs(final SAMLConfigSettings samlConfigSettings, FormData formData)
    {
        try
        {
            samlConfigAdminService.setSamlConfigs(samlConfigSettings);
        }
        catch(SAMLCertificateExpiredException ee)
        {
            throw new WebScriptException(Status.STATUS_BAD_REQUEST, "Certificate is expired.", ee);
        }
        catch(SAMLCertificateNotYetValidException nye)
        {
            throw new WebScriptException(Status.STATUS_BAD_REQUEST, "Certificate is not yet valid.", nye);
        }
        catch(RuntimeException ar)
        {
            throw new WebScriptException(Status.STATUS_BAD_REQUEST, "Certificate is not valid.", ar);
        }
        finally
        {
            formData.cleanup();
        }
    }

    private Boolean getBooleanValue(String value)
    {
        return value == null ? null : Boolean.parseBoolean(value);
    }
}
