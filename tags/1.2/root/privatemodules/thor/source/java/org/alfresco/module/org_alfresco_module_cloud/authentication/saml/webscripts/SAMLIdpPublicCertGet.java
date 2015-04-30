/*
 * Copyright 2005-2013 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.module.org_alfresco_module_cloud.authentication.saml.webscripts;

import java.io.IOException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;
import org.alfresco.module.org_alfresco_module_cloud.authentication.saml.SAMLCertificateExpiredException;
import org.alfresco.module.org_alfresco_module_cloud.authentication.saml.SAMLConfigAdminService;
import org.alfresco.module.org_alfresco_module_cloud.authentication.saml.core.SAMLCertificateUtil;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.tenant.TenantUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptException;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;

/**
 * SAML Identity Provider (IdP) Certificate GET
 * 
 * This class is the controller for the "saml-idp-public-cert.get" web script.
 * 
 * @author jkaabimofrad, janv
 * 
 */
public class SAMLIdpPublicCertGet extends AbstractSAMLAdminWebScript
{
    private static final Log logger = LogFactory.getLog(SAMLIdpPublicCertGet.class);

    private SAMLConfigAdminService samlConfigAdminService;

    public void setSamlConfigAdminService(SAMLConfigAdminService service)
    {
        this.samlConfigAdminService = service;
    }

    @Override
    protected void unprotectedExecuteImpl(WebScriptRequest req, WebScriptResponse res) throws IOException
    {
        String tenantDomain = TenantUtil.getCurrentDomain();

        try
        {
            X509Certificate idpCert = samlConfigAdminService.getCertificate(tenantDomain);
            String encodedCert = SAMLCertificateUtil.encodeCertificate(idpCert);

            Map<String, Object> model = new HashMap<String, Object>();
            model.put("cert", encodedCert);

            if(logger.isDebugEnabled())
            {
                logger.debug("SAMLIdpPublicCertGet: [" + AuthenticationUtil.getFullyAuthenticatedUser() + "]\n"
                    + encodedCert);
            }
            else if(logger.isInfoEnabled())
            {
                logger.info("SAMLIdpPublicCertGet: [" + AuthenticationUtil.getFullyAuthenticatedUser() + "]");
            }

            setAttachment(res, true, tenantDomain + ".certificate.cer");

            String templatePath = getDescription().getId() + ".text.ftl";
            renderTemplate(templatePath, model, res.getWriter());
        }
        catch(SAMLCertificateExpiredException ce)
        {
            throw new WebScriptException(Status.STATUS_BAD_REQUEST, "[" + tenantDomain + "] certificate has expired.");
        }
        catch(Exception e)
        {
            throw new WebScriptException(Status.STATUS_NOT_FOUND, "Cannot get [" + tenantDomain + "] certificate: " + e);
        }
    }
}
