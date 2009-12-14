/*
 * Copyright (C) 2005-2009 Alfresco Software Limited.
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
 * FLOSS exception.  You should have received a copy of the text describing 
 * the FLOSS exception, and it is also available here: 
 * http://www.alfresco.com/legal/licensing"
 */
package org.alfresco.web.config;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.util.HashMap;
import java.util.List;

import javax.net.ssl.KeyManagerFactory;

import org.springframework.extensions.config.ConfigElement;
import org.springframework.extensions.config.element.ConfigElementAdapter;
import org.alfresco.error.AlfrescoRuntimeException;
import org.apache.abdera.protocol.client.util.ClientAuthSSLProtocolSocketFactory;
import org.apache.commons.httpclient.protocol.Protocol;
import org.apache.commons.httpclient.protocol.ProtocolSocketFactory;
import org.dom4j.Element;

/**
 * Describes the connection, authentication and endpoint properties stored
 * within the <remote> block of the current configuration.  This block
 * provides settings for creating and working with remote services.
 * 
 * @author muzquiano
 */
public class RemoteConfigElement extends ConfigElementAdapter implements RemoteConfigProperties
{
    private static final String REMOTE_KEYSTORE = "keystore";  
    private static final String REMOTE_ENDPOINT = "endpoint";
    private static final String REMOTE_AUTHENTICATOR = "authenticator";
    private static final String REMOTE_CONNECTOR = "connector";
    private static final String CONFIG_ELEMENT_ID = "remote";

    protected KeyStoreDescriptor keyStoreDescriptor;
    protected HashMap<String, ConnectorDescriptor> connectors = null;
    protected HashMap<String, AuthenticatorDescriptor> authenticators = null;
    protected HashMap<String, EndpointDescriptor> endpoints = null;

    protected String defaultEndpointId;
    protected String defaultCredentialVaultProviderId;

    /**
     * Constructs a new Remote Config Element
     */
    public RemoteConfigElement()
    {
        super(CONFIG_ELEMENT_ID);

        connectors = new HashMap<String, ConnectorDescriptor>(10);
        authenticators = new HashMap<String, AuthenticatorDescriptor>(10);
        endpoints = new HashMap<String, EndpointDescriptor>(10);
    }

    /* (non-Javadoc)
     * @see org.springframework.extensions.config.element.GenericConfigElement#combine(org.alfresco.config.ConfigElement)
     */
    public ConfigElement combine(ConfigElement element)
    {
        RemoteConfigElement configElement = (RemoteConfigElement) element;

        // new combined element
        RemoteConfigElement combinedElement = new RemoteConfigElement();

        // copy in our things
        combinedElement.connectors.putAll(this.connectors);
        combinedElement.authenticators.putAll(this.authenticators);
        combinedElement.endpoints.putAll(this.endpoints);

        // override with things from the merging object
        combinedElement.connectors.putAll(configElement.connectors);
        combinedElement.authenticators.putAll(configElement.authenticators);
        combinedElement.endpoints.putAll(configElement.endpoints);

        // SSL KeyStore configuration
        combinedElement.keyStoreDescriptor = this.keyStoreDescriptor;
        if(configElement.keyStoreDescriptor != null)
        {
           combinedElement.keyStoreDescriptor = configElement.keyStoreDescriptor;
        }

        // default endpoint id
        combinedElement.defaultEndpointId = this.defaultEndpointId;
        if(configElement.defaultEndpointId != null)
        {
            combinedElement.defaultEndpointId = configElement.defaultEndpointId;
        }

        // default credential vault provider id
        combinedElement.defaultCredentialVaultProviderId = this.defaultCredentialVaultProviderId;
        if(configElement.defaultCredentialVaultProviderId != null)
        {
            combinedElement.defaultCredentialVaultProviderId = configElement.defaultCredentialVaultProviderId;
        }

        // return the combined element
        return combinedElement;
    }

    // remote connectors
    public String[] getConnectorIds()
    {
        return this.connectors.keySet().toArray(new String[this.connectors.size()]);
    }

    public ConnectorDescriptor getConnectorDescriptor(String id)
    {
        return (ConnectorDescriptor) this.connectors.get(id);
    }

    // remote authenticators
    public String[] getAuthenticatorIds()
    {
        return this.authenticators.keySet().toArray(new String[this.authenticators.size()]);
    }

    public AuthenticatorDescriptor getAuthenticatorDescriptor(String id)
    {
        return (AuthenticatorDescriptor) this.authenticators.get(id);
    }

    // remote endpoints
    public String[] getEndpointIds()
    {
        return this.endpoints.keySet().toArray(new String[this.endpoints.size()]);
    }

    public EndpointDescriptor getEndpointDescriptor(String id)
    {
        return (EndpointDescriptor) this.endpoints.get(id);
    }

    // defaults
    public String getDefaultEndpointId()
    {
        if(defaultEndpointId == null)
        {
            return "alfresco";
        }
        return defaultEndpointId;
    }

    public String getDefaultCredentialVaultProviderId()
    {
        if(defaultCredentialVaultProviderId == null)
        {
            return "credential.vault.provider";
        }
        return defaultCredentialVaultProviderId;
    }


    /**
     * EndPoint Descriptor class
     */
    public static class Descriptor implements Serializable
    {
        private static final String ID = "id";

        protected HashMap<String, Object> map = new HashMap<String, Object>();

        Descriptor(Element el)
        {
            List elements = el.elements();
            for(int i = 0; i < elements.size(); i++)
            {
                Element element = (Element) elements.get(i);
                put(element);
            }
        }

        public void put(Element el)
        {
            String key = el.getName();
            Object value = (Object) el.getTextTrim();
            if(value != null)
            {
                this.map.put(key, value);
            }
        }

        public Object get(String key)
        {
            return (Object) this.map.get(key);
        }	

        public String getId() 
        {
            return (String) get(ID);
        }		

        public Object getProperty(String key)
        {
            return get(key);
        }

        public String getStringProperty(String key)
        {
            return (String) get(key);
        }

        @Override
        public String toString()
        {
            // TODO Auto-generated method stub
            return map.toString();
        }
    }

    /**
     * The Class KeyStoreDescriptor.
     */
    public static class KeyStoreDescriptor extends Descriptor
    {
        private static final String PATH = "path";
        private static final String TYPE = "type";
        private static final String PASSWORD = "password";

        /**
         * Initializes SSL client certificate configuration, if appropriate
         * 
         * @param elem the element
         */
        KeyStoreDescriptor(Element el)
        {
            super(el);
            String keyStorePath = getStringProperty(PATH);
            InputStream keyStoreIn;
            if (keyStorePath != null && (keyStoreIn = getClass().getResourceAsStream("/" + keyStorePath)) != null)
            {

                try
                {
                    KeyStore keyStore = KeyStore.getInstance(getStringProperty(TYPE));
                    String password = getStringProperty(PASSWORD);
                    keyStore.load(keyStoreIn, password.toCharArray());
                    Protocol authHttps = new Protocol("https",
                            (ProtocolSocketFactory) new ClientAuthSSLProtocolSocketFactory(keyStore, password,
                                    "TLS", KeyManagerFactory.getDefaultAlgorithm(), null), 443);
                    Protocol.registerProtocol("https", authHttps);
                }
                catch (GeneralSecurityException e)
                {
                    throw new AlfrescoRuntimeException("Error loading keyStore: " + keyStorePath, e);
                }
                catch (IOException e)
                {
                    throw new AlfrescoRuntimeException("Error loading keyStore: " + keyStorePath, e);
                }
            }            
        }
    }
    

    /**
     * The Class ConnectorDescriptor.
     */
    public static class ConnectorDescriptor extends Descriptor
    {
        private static final String CLAZZ = "class";
        private static final String DESCRIPTION = "description";
        private static final String NAME = "name";
        private static final String AUTHENTICATOR_ID = "authenticator-id";
        private static final String UNAUTHENTICATED_MODE = "unauthenticated-mode";

        /**
         * Instantiates a new remote connector descriptor.
         * 
         * @param elem the elem
         */
        ConnectorDescriptor(Element el)
        {
            super(el);
        }

        public String getImplementationClass() 
        {
            return getStringProperty(CLAZZ);
        }
        
        public String getDescription() 
        {
            return getStringProperty(DESCRIPTION);
        }
        
        public String getName() 
        {
            return getStringProperty(NAME);
        } 
        
        public String getAuthenticatorId()
        {
            return getStringProperty(AUTHENTICATOR_ID);
        }	    
        
        public String getUnauthenticatedMode()
        {
            return getStringProperty(UNAUTHENTICATED_MODE);
        }
    }

    /**
     * The Class AuthenticatorDescriptor.
     */
    public static class AuthenticatorDescriptor extends Descriptor
    {
        private static final String CLAZZ = "class";
        private static final String DESCRIPTION = "description";
        private static final String NAME = "name";

        /**
         * Instantiates a new remote authenticator descriptor.
         * 
         * @param elem the elem
         */
        AuthenticatorDescriptor(Element el)
        {
            super(el);
        }

        public String getImplementationClass() 
        {
            return getStringProperty(CLAZZ);
        }
        public String getDescription() 
        {
            return getStringProperty(DESCRIPTION);
        }
        public String getName() 
        {
            return getStringProperty(NAME);
        }    		    
    }

    /**
     * The Class EndpointDescriptor.
     */
    public static class EndpointDescriptor extends Descriptor
    {    	
        private static final String PASSWORD = "password";
        private static final String USERNAME = "username";
        private static final String IDENTITY = "identity";
        private static final String ENDPOINT_URL = "endpoint-url";
        private static final String AUTH_ID = "auth-id";
        private static final String CONNECTOR_ID = "connector-id";
        private static final String DESCRIPTION = "description";
        private static final String NAME = "name";
        private static final String UNSECURE = "unsecure";
        private static final String PERSISTENT = "persistent";
        private static final String BASIC_AUTH = "basic-auth";
        private static final String EXTERNAL_AUTH = "external-auth";

        /**
         * Instantiates a new remote endpoint descriptor.
         * 
         * @param elem the elem
         */
        EndpointDescriptor(Element el)
        {
            super(el);
        }

        public String getDescription() 
        {
            return getStringProperty(DESCRIPTION);
        }

        public String getName() 
        {
            return getStringProperty(NAME);
        }    

        public String getConnectorId() 
        {
            return getStringProperty(CONNECTOR_ID);
        }

        public String getAuthId()
        {
            return getStringProperty(AUTH_ID);
        }

        public String getEndpointUrl()
        {
            return getStringProperty(ENDPOINT_URL);
        }

        public IdentityType getIdentity()
        {
            IdentityType identityType = IdentityType.NONE;
            String identity = getStringProperty(IDENTITY);
            if (identity != null)
            {
                identityType = IdentityType.valueOf(identity.toUpperCase());
            }
            return identityType;
        }

        public String getUsername()
        {
            return getStringProperty(USERNAME);
        }

        public String getPassword()
        {
            return getStringProperty(PASSWORD);
        }
        
        public boolean getUnsecure()
        {
            return Boolean.parseBoolean(getStringProperty(UNSECURE));
        }
        
        public boolean getPersistent()
        {
            return Boolean.parseBoolean(getStringProperty(PERSISTENT));
        }
        
        public boolean getBasicAuth()
        {
            return Boolean.parseBoolean(getStringProperty(BASIC_AUTH));
        }
        
        public boolean getExternalAuth()
        {
            return Boolean.parseBoolean(getStringProperty(EXTERNAL_AUTH));
        }
    }

    /**
     * New instance.
     * 
     * @param elem the elem
     * 
     * @return the remote config element
     */
    protected static RemoteConfigElement newInstance(Element elem)
    {
        RemoteConfigElement configElement = new RemoteConfigElement();

        // connectors
        List connectors = elem.elements(REMOTE_CONNECTOR);
        for(int i = 0; i < connectors.size(); i++)
        {
            Element el = (Element) connectors.get(i);
            ConnectorDescriptor descriptor = new ConnectorDescriptor(el);
            configElement.connectors.put(descriptor.getId(), descriptor);
        }

        // authenticators
        List authenticators = elem.elements(REMOTE_AUTHENTICATOR);
        for(int i = 0; i < authenticators.size(); i++)
        {
            Element el = (Element) authenticators.get(i);
            AuthenticatorDescriptor descriptor = new AuthenticatorDescriptor(el);
            configElement.authenticators.put(descriptor.getId(), descriptor);
        }

        // endpoints
        List endpoints = elem.elements(REMOTE_ENDPOINT);
        for(int i = 0; i < endpoints.size(); i++)
        {
            Element el = (Element) endpoints.get(i);
            EndpointDescriptor descriptor = new EndpointDescriptor(el);
            configElement.endpoints.put(descriptor.getId(), descriptor);
        }

        Element el = elem.element(REMOTE_KEYSTORE);
        if (el != null)
        {
            configElement.keyStoreDescriptor = new KeyStoreDescriptor(el);
        }
        
        String _defaultEndpointId = elem.elementTextTrim("default-endpoint-id");
        if(_defaultEndpointId != null && _defaultEndpointId.length() > 0)
        {
            configElement.defaultEndpointId = _defaultEndpointId;
        }

        String _defaultCredentialVaultProviderId = elem.elementTextTrim("default-credential-vault-provider-id");
        if(_defaultCredentialVaultProviderId != null && _defaultCredentialVaultProviderId.length() > 0)
        {
            configElement.defaultCredentialVaultProviderId = _defaultCredentialVaultProviderId;
        }

        return configElement;
    }
    
    
    /**
     * Enum describing the Identity Type for an Endpoint
     */
    public enum IdentityType
    {
        DECLARED, USER, NONE;
    }
}
