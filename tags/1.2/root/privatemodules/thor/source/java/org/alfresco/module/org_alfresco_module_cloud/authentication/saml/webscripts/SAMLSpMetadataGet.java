/*
 * Copyright 2005-2013 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.module.org_alfresco_module_cloud.authentication.saml.webscripts;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.alfresco.module.org_alfresco_module_cloud.authentication.saml.SAMLAuthenticationService;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.tenant.TenantUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptException;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;

/**
 * SAML Service Provider (SP) Metadata GET
 * 
 * This class is the controller for the "saml-sp-metadata.get" web script.
 * 
 * @author janv
 * @since Cloud SAML
 */
public class SAMLSpMetadataGet extends AbstractSAMLAdminWebScript
{
    private static final Log logger = LogFactory.getLog(SAMLSpMetadataGet.class);
    
    private SAMLAuthenticationService samlAuthenticationService;
    
    public void setSamlAuthenticationService(SAMLAuthenticationService service)
    {
        this.samlAuthenticationService = service;
    }
    
    @Override
    protected void unprotectedExecuteImpl(WebScriptRequest req, WebScriptResponse res) throws IOException
    {
        try
        {
            String tenantDomain = TenantUtil.getCurrentDomain();
            String spPublicCert = samlAuthenticationService.getSpPublicCertificate();
            
            Map<String, Object> model = new HashMap<String, Object>();
            model.put("cert", spPublicCert);
            model.put("spSsoUrl", samlAuthenticationService.getSpSsoURL(tenantDomain));
            model.put("spSloRequestUrl", samlAuthenticationService.getSpSloRequestURL(tenantDomain));
            model.put("spSloResponseUrl", samlAuthenticationService.getSpSloResponseURL(tenantDomain));
            model.put("spEntityID", samlAuthenticationService.getSpIssuerName(tenantDomain));
            
            if (logger.isDebugEnabled())
            {
                logger.debug("SAMLSpMetadataGet: ["+AuthenticationUtil.getFullyAuthenticatedUser()+"]\n"+spPublicCert);
            }
            else if (logger.isInfoEnabled())
            {
                logger.info("SAMLSpMetadataGet: ["+AuthenticationUtil.getFullyAuthenticatedUser()+"]");
            }
            
            setAttachment(res, true, "alfrescoSamlSpMetadata.xml");
            
            String templatePath = getDescription().getId() + ".xml.ftl";
            renderTemplate(templatePath, model, res.getWriter());
        }
        catch (Exception e)
        {
            throw new WebScriptException(Status.STATUS_NOT_FOUND, "Cannot get SP metadata: "+e);
        }
    }
}
