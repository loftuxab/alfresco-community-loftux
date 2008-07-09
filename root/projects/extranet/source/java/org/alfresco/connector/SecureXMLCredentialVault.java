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
package org.alfresco.connector;

import org.alfresco.tools.StringEncrypter;
import org.alfresco.web.config.RemoteConfigElement.CredentialVaultDescriptor;

/**
 * A secure implementation of a persistent credential vault where
 * credentials are stored in XML on disk.
 * 
 * @author muzquiano
 */
public class SecureXMLCredentialVault extends XMLCredentialVault
{
    /**
     * Instantiates a new secure XML credential vault.
     * 
     * @param id the id
     * @param descriptor the descriptor
     */
    public SecureXMLCredentialVault(String id, CredentialVaultDescriptor descriptor)
    {
        super(id, descriptor);
    }
    
    /**
     * Serialize.
     * 
     * @return the string
     */
    protected String serialize()
    {
        String xml = super.serialize();
        
        // encrypt
        return getEncrypter().encrypt(xml);
    }
    
    /**
     * Deserialize.
     * 
     * @param secureXml the secure xml
     */
    protected void deserialize(String secureXml)
    {
        // decrypt
        String xml = getEncrypter().decrypt(secureXml);
        
        // deserialize
        super.deserialize(xml);
    }
    
    protected StringEncrypter getEncrypter()
    {
        return new StringEncrypter("test pass phrase");
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        return "XMLCredentialVault - " + credentialsMap.toString();
    }        
}
