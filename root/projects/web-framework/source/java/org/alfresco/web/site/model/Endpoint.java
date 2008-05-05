/*
 * Copyright (C) 2005-2008 Alfresco Software Limited.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.

 * As a special exception to the terms and conditions of version 2.0 of 
 * the GPL, you may redistribute this Program in connection with Free/Libre 
 * and Open Source Software ("FLOSS") applications as described in Alfresco's 
 * FLOSS exception.  You should have recieved a copy of the text describing 
 * the FLOSS exception, and it is also available here: 
 * http://www.alfresco.com/legal/licensing"
 */
package org.alfresco.web.site.model;

import org.dom4j.Document;

/**
 * Endpoint model object
 * 
 * @author muzquiano
 */
public class Endpoint extends AbstractModelObject
{
    public static String TYPE_NAME = "endpoint";
    public static String PROP_ENDPOINT_ID = "endpoint-id";
    public static String PROP_CONNECTOR_ID = "connector-id";
    public static String PROP_AUTH_ID = "auth-id";
    public static String PROP_ENDPOINT_URL = "endpoint-url";
    public static String PROP_DEFAULT_URI = "default-uri";
    public static String PROP_IDENTITY = "identity";
    public static String PROP_USERNAME = "username";
    public static String PROP_PASSWORD = "password";
    public static String VALUE_IDENTITY_SPECIFIC_USER = "specific";
    public static String VALUE_IDENTITY_CURRENT_USER = "current";
    
    /**
     * Instantiates a new endpoint for a given XML document
     * 
     * @param document the document
     */
    public Endpoint(Document document)
    {
        super(document);
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        return "Endpoint: " + getId() + ", " + toXML();
    }

    /**
     * Gets the endpoint id.
     * 
     * @return the endpoint id
     */
    public String getEndpointId()
    {
        return getProperty(PROP_ENDPOINT_ID);
    }

    /**
     * Sets the endpoint id.
     * 
     * @param endpointId the new endpoint id
     */
    public void setEndpointId(String endpointId)
    {
        setProperty(PROP_ENDPOINT_ID, endpointId);
    }
    
    /**
     * Gets the connector id.
     * 
     * @return the connector id
     */
    public String getConnectorId()
    {
        return getProperty(PROP_CONNECTOR_ID);
    }

    /**
     * Sets the connector id.
     * 
     * @param connectorId the new connector id
     */
    public void setConnectorId(String connectorId)
    {
        setProperty(PROP_CONNECTOR_ID, connectorId);
    }

    /**
     * Gets the authenticator id.
     * 
     * @return the authenticator id
     */
    public String getAuthenticatorId()
    {
        return getProperty(PROP_AUTH_ID);
    }

    /**
     * Sets the authenticator id.
     * 
     * @param authenticatorId the new authenticator id
     */
    public void setAuthenticatorId(String authenticatorId)
    {
        setProperty(PROP_AUTH_ID, authenticatorId);
    }
    
    /**
     * Gets the endpoint url.
     * 
     * @return the endpoint url
     */
    public String getEndpointURL()
    {
        return getProperty(PROP_ENDPOINT_URL);
    }
    
    /**
     * Sets the endpoint url.
     * 
     * @param endpointUrl the new endpoint url
     */
    public void setEndpointURL(String endpointUrl)
    {
        setProperty(PROP_ENDPOINT_URL, endpointUrl);
    }
    
    /**
     * Gets the default uri.
     * 
     * @return the default uri
     */
    public String getDefaultURI()
    {
        return getProperty(PROP_DEFAULT_URI);
    }
    
    /**
     * Sets the default uri.
     * 
     * @param defaultUri the new default uri
     */
    public void setDefaultURI(String defaultUri)
    {
        setProperty(PROP_DEFAULT_URI, defaultUri);
    }
    
    /**
     * Gets the identity.
     * 
     * @return the identity
     */
    public String getIdentity()
    {
        return getProperty(PROP_IDENTITY);
    }
    
    /**
     * Sets the identity.
     * 
     * @param identity the new identity
     */
    public void setIdentity(String identity)
    {
        setProperty(PROP_IDENTITY, identity);
    }
    
    /**
     * Gets the username.
     * 
     * @return the username
     */
    public String getUsername()
    {
        return getProperty(PROP_USERNAME);
    }
    
    /**
     * Sets the username.
     * 
     * @param username the new username
     */
    public void setUsername(String username)
    {
        setProperty(PROP_USERNAME, username);
    }
    
    /**
     * Gets the password.
     * 
     * @return the password
     */
    public String getPassword()
    {
        return getProperty(PROP_PASSWORD);
    }
    
    /**
     * Sets the password.
     * 
     * @param password the new password
     */
    public void setPassword(String password)
    {
        setProperty(PROP_PASSWORD, password);
    }    
    
    /* (non-Javadoc)
     * @see org.alfresco.web.site.model.AbstractModelObject#getTypeName()
     */
    public String getTypeName() 
    {
        return TYPE_NAME;
    }
}
