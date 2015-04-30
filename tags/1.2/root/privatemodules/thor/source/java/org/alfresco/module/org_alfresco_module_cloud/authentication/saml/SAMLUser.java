/*
 * Copyright 2005-2013 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.module.org_alfresco_module_cloud.authentication.saml;

import org.springframework.extensions.webscripts.connector.User;

/**
 * SAML User with Request/Response ID (for logging)
 * 
 * @author janv
 * @since Cloud SAML
 */
public final class SAMLUser
{
    private String samlID;
    private User user;
    
    public SAMLUser(String samlID, User user)
    {
        this.samlID = samlID;
        this.user = user;
    }
    
    public String getSamlID()
    {
        return samlID;
    }
    
    public User getUser()
    {
        return user;
    }
}