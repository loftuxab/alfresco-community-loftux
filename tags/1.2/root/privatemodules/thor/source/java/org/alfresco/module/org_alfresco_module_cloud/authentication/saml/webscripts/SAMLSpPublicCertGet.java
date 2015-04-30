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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptException;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;

/**
 * SAML Service Provider (SP) Public Certificate GET
 * 
 * This class is the controller for the "saml-sp-public-cert.get" web script.
 * 
 * @author janv
 * @since Cloud SAML
 */
public class SAMLSpPublicCertGet extends AbstractSAMLAdminWebScript
{
    private static final Log logger = LogFactory.getLog(SAMLSpPublicCertGet.class);
    
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
            String spPublicCert = samlAuthenticationService.getSpPublicCertificate();
            
            Map<String, Object> model = new HashMap<String, Object>();
            model.put("cert", spPublicCert);
            
            if (logger.isDebugEnabled())
            {
                logger.debug("SAMLSpPublicCertGet: ["+AuthenticationUtil.getFullyAuthenticatedUser()+"]\n"+spPublicCert);
            }
            else if (logger.isInfoEnabled())
            {
                logger.info("SAMLSpPublicCertGet: ["+AuthenticationUtil.getFullyAuthenticatedUser()+"]");
            }
            
            setAttachment(res, true, "alfrescoSamlSpPublicCert.cer");
            
            String templatePath = getDescription().getId() + ".text.ftl";
            renderTemplate(templatePath, model, res.getWriter());
        }
        catch (Exception e)
        {
            throw new WebScriptException(Status.STATUS_NOT_FOUND, "Cannot get SP public cert: "+e);
        }
    }
}
