/*
 * Copyright 2005-2012 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.module.org_alfresco_module_cloud.authentication.saml.core;

import org.alfresco.module.org_alfresco_module_cloud.authentication.saml.SAMLConfigAdminService;
import org.alfresco.util.PropertyCheck;
import org.opensaml.xml.security.trust.TrustEngine;
import org.opensaml.xml.signature.Signature;

;

/**
 * A basic implementation of {@link SAMLTrustEngineStore}. This class delegates the work to
 * {@link SAMLConfigAdminService}.
 * 
 * @author jkaabimofrad
 * @since Cloud SAML
 */
public class SAMLTrustEngineStoreImpl implements SAMLTrustEngineStore
{

    private SAMLConfigAdminService samlConfigAdminService;

    public void setSamlConfigAdminService(SAMLConfigAdminService samlConfigAdminService)
    {
        this.samlConfigAdminService = samlConfigAdminService;
    }

    /**
     * Checks that all necessary properties and services have been provided.
     */
    public void init()
    {
        PropertyCheck.mandatory(this, "samlConfigAdminService", samlConfigAdminService);
    }

    /**
     * {@inheritDoc}
     */
    public TrustEngine<Signature> getTrustEngine(String tenantDomain)
    {
        return samlConfigAdminService.getTrustEngine(tenantDomain);
    }
}
