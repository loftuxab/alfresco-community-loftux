/*
 * Copyright 2005-2013 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.module.org_alfresco_module_cloud.authentication.saml.core;

import org.opensaml.xml.security.SecurityException;
import org.opensaml.xml.security.credential.Credential;
import org.opensaml.xml.security.credential.CredentialResolver;

/**
 * A resolver which resolves and returns instances of {@link Credential}.
 * 
 * @author jkaabimofrad
 * @since Cloud SAML
 */
public interface SAMLCredentialResolver extends CredentialResolver
{

    /**
     * Process the default criteria and return a single instance of {@link Credential} which satisfies the default criteria.
     * 
     * @return a single instance satisfying the criteria, or null
     * @throws SecurityException
     */
    public Credential resolveSingle() throws SecurityException;

}