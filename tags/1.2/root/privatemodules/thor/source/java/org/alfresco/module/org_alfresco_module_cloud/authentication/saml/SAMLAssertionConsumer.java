/*
 * Copyright 2005-2012 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.module.org_alfresco_module_cloud.authentication.saml;

import org.opensaml.saml2.core.LogoutRequest;
import org.opensaml.saml2.core.LogoutResponse;
import org.opensaml.saml2.core.Response;
import org.springframework.extensions.surf.exception.AuthenticationException;
import org.springframework.extensions.webscripts.connector.User;

/**
 * SAML ("assertion") message consumer (for SSO and SLO)
 * 
 * @author jkaabimofrad, janv
 * @since Cloud SAML
 */
public interface SAMLAssertionConsumer
{
    /**
     * Gets the user from the SAML Authn Response.
     * 
     * @param samlResponse
     * @return User object
     * @throws AuthenticationException
     */
    public User getUserFromAuthnResponse(Response samlResponse) throws AuthenticationException;
    
    /**
     * Gets the user from the SAML Logout Response.
     * 
     * @param samlResponse
     * @return User object
     * @throws AuthenticationException
     */
    public User getUserFromLogoutResponse(LogoutResponse samlResponse) throws AuthenticationException;
    
    /**
     * Gets the user from the SAML Logout Request.
     * 
     * @param samlResponse
     * @return User object
     * @throws AuthenticationException
     */
    public User getUserFromLogoutRequest(LogoutRequest samlResponse) throws AuthenticationException;
}
