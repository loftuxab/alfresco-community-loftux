/*
 * Copyright 2005-2012 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.module.org_alfresco_module_cloud.authentication.saml.core;

import org.opensaml.xml.security.trust.TrustEngine;
import org.opensaml.xml.signature.Signature;

/**
 * This provides service for getting trust engine (for validating IdP's signature).
 * 
 * @author jkaabimofrad
 * @since Cloud SAML
 */
public interface SAMLTrustEngineStore
{
    /**
     * Gets a trust engine for the specified tenant. This trust engine is used to validate the IdP's signature.
     * 
     * @param tenantDomain
     *            tenant domain
     * @return {@code TrustEngine<Signature> object}
     */
    public TrustEngine<Signature> getTrustEngine(String tenantDomain);
}