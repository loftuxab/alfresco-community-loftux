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

import java.util.HashMap;

import org.alfresco.config.ConfigService;
import org.alfresco.connector.exception.RemoteConfigException;
import org.alfresco.web.config.RemoteConfigElement;
import org.alfresco.web.config.RemoteConfigElement.CredentialVaultDescriptor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Allows for the construction of Credential Vaults
 * 
 * This is just provided for convenience so that developers don't have
 * to worry about the management of vaults.
 * 
 * @author muzquiano
 */
public class CredentialVaultFactory
{
	protected static Log logger = LogFactory.getLog(CredentialVaultFactory.class);
	protected ConfigService configService = null;

	protected static HashMap<String, CredentialVault> cache = null;
	
	/**
	 * New instance.
	 * 
	 * @param configService the config service
	 * 
	 * @return the credential vault factory
	 */
	public static CredentialVaultFactory newInstance(ConfigService configService)
	{
		CredentialVaultFactory factory = new CredentialVaultFactory();
		factory.setConfigService(configService);
		return factory;
	}
	
	private CredentialVaultFactory()
	{		
	}
		
	/**
	 * Sets the config service.
	 * 
	 * @param configService the new config service
	 */
	public void setConfigService(ConfigService configService)
	{
		this.configService = configService;
	}
	
	/**
	 * Gets the config service.
	 * 
	 * @return the config service
	 */
	public ConfigService getConfigService()
	{
		return this.configService;
	}
	
	/**
	 * Returns the default credential vault.  The vault will be created
	 * if it hasn't been created before.  It will also be cached so that
	 * this method can be used to always return the same default vault.
	 * 
	 * @return the credential vault
	 * 
	 * @throws RemoteConfigException the remote config exception
	 */
	public synchronized CredentialVault vault()
		throws RemoteConfigException
	{
		// get the remote configuration block
		RemoteConfigElement remoteConfig = (RemoteConfigElement) getConfigService().getConfig("Remote").getConfigElement("remote");
		if(remoteConfig == null)
		{
			throw new RemoteConfigException("The 'Remote' configuration was not found, unable to lookup the vault definition.");
		}
		
		return vault(remoteConfig.getDefaultCredentialVaultId());
	}

	/**
	 * Retrieves the Credential Vault with the given id (as identified in
	 * the Remote configuration block).  If the vault does not yet exist,
	 * it will be created.  It will also be cached so that subsequent calls
	 * will yield produce the same vault.
	 * 
	 * @param vaultId the vault id
	 * 
	 * @return the credential vault
	 * 
	 * @throws RemoteConfigException the remote config exception
	 */
	public synchronized CredentialVault vault(String vaultId)
		throws RemoteConfigException	
	{
		if(cache == null)
		{
			cache = new HashMap<String, CredentialVault>(10, 1.0f);
		}
		
		CredentialVault vault = (CredentialVault) cache.get(vaultId);
		if(vault == null)
		{
			// get the remote configuration block
			RemoteConfigElement remoteConfig = (RemoteConfigElement) getConfigService().getConfig("Remote").getConfigElement("remote");
			if(remoteConfig == null)
			{
				throw new RemoteConfigException("The 'Remote' configuration was not found, unable to lookup the vault definition.");
			}
			
			// load the vault
			CredentialVaultDescriptor descriptor = remoteConfig.getCredentialVaultDescriptor(vaultId);
			if(descriptor == null)
			{
				throw new RemoteConfigException("Unable to find credential vault definition for id: " + vaultId);
			}
			
			// build the vault
			String vaultClass = descriptor.getImplementationClass();
			vault = (CredentialVault) newObject(vaultClass);
			
			// place into cache
			if(vault != null)
			{
				cache.put(vaultId, vault);
			}
		}
		
		return vault;
	}

	// reflection helper
	protected static Object newObject(String className)
    {
        Object o = null;

        try
        {
            Class clazz = Class.forName(className);
            o = clazz.newInstance();
        }
        catch (ClassNotFoundException cnfe)
        {
        	cnfe.printStackTrace();
            logger.debug(cnfe);
        }
        catch (InstantiationException ie)
        {
        	ie.printStackTrace();
            logger.debug(ie);
        }
        catch (IllegalAccessException iae)
        {
        	iae.printStackTrace();
            logger.debug(iae);
        }
        return o;
    }
}
