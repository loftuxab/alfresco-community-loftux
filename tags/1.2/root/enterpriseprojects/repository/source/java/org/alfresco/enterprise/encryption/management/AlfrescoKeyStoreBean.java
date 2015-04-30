/*
 * Copyright (C) 2005-2011 Alfresco Software Limited.
 *
 * This file is part of Alfresco
 *
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */
package org.alfresco.enterprise.encryption.management;

import java.security.Key;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.management.openmbean.CompositeData;
import javax.management.openmbean.CompositeDataSupport;
import javax.management.openmbean.CompositeType;
import javax.management.openmbean.OpenDataException;
import javax.management.openmbean.OpenType;
import javax.management.openmbean.SimpleType;

import org.alfresco.encryption.AlfrescoKeyStore;
import org.alfresco.encryption.InvalidKeystoreException;
import org.alfresco.encryption.MissingKeyException;
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedOperationParameters;
import org.springframework.jmx.export.annotation.ManagedResource;

/**
 * 
 * @since 4.0
 *
 */
@ManagedResource
public class AlfrescoKeyStoreBean implements AlfrescoKeyStoreMBean
{
	private AlfrescoKeyStore mainKeyStore;
	private AlfrescoKeyStore backupKeyStore;

	private static String[] attributeNames = new String[2];
	private static OpenType<?>[] types = new OpenType<?>[2];
	
	static
	{
		// TODO i18n
		types = new OpenType<?>[]{SimpleType.STRING, SimpleType.BOOLEAN, SimpleType.LONG};
		attributeNames = new String[] {"Alias Name", "Key Loaded", "Key Loaded Timestamp"};
	}

	public AlfrescoKeyStoreBean(AlfrescoKeyStore mainKeyStore)
	{
		super();
		this.mainKeyStore = mainKeyStore;
	}

    @ManagedAttribute(description = "The location of the main key store")
	public String getLocation()
	{
		return mainKeyStore.getKeyStoreParameters().getLocation();
	}

    @ManagedAttribute(description = "The location of the backup key store")
	public String getBackupLocation()
	{
    	return mainKeyStore.getBackupKeyStoreParameters().getLocation();
	}
    
    protected CompositeType getKeystoreConfigCompositeType() throws OpenDataException
    {
    	// TODO i18n
    	CompositeType type = new CompositeType("Keystore Configuration", "Keystore Configuration",
				attributeNames, attributeNames, types);
    	return type;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @ManagedOperation(description = "The key aliases in the key store")
    @ManagedOperationParameters({
    })
    public CompositeData getKeyAliases() throws OpenDataException
	{
    	return getKeyAliases(mainKeyStore);
	}

    /**
     * {@inheritDoc}
     */
    @Override
    @ManagedOperation(description = "The key aliases in the backup key store")
    @ManagedOperationParameters({
    })
    public CompositeData getBackupKeyAliases() throws OpenDataException
	{
    	if(backupKeyStore == null)
    	{
    		return null;
    	}
    	else
    	{
        	return getKeyAliases(backupKeyStore);
    	}
	}
    
    protected CompositeData getKeyAliases(AlfrescoKeyStore keyStore) throws OpenDataException
    {
    	CompositeType type = getKeystoreConfigCompositeType();
    	Set<String> keyAliases = keyStore.getKeyAliases();
    	Map<String, Object> values = new HashMap<String, Object>();

        for(String keyAlias : keyAliases)
        {
        	Key key = keyStore.getKey(keyAlias);
        	values.put("Alias Name", keyAlias);
        	values.put("Key Loaded", key != null);
        	values.put("Key Loaded Timestamp", keyStore.getKeyTimestamp(keyAlias));
        }

        CompositeDataSupport data = new CompositeDataSupport(type, values);
        return data;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    @ManagedOperation(description = "Reloads the keys from the keystore on persistent storage")
    @ManagedOperationParameters({
    })
    public void reload()
    {
    	try
    	{
    		mainKeyStore.reload();
    	}
    	catch(InvalidKeystoreException e)
    	{
    		// TODO message
    		throw new RuntimeException("", e);
    	}
    	catch(MissingKeyException e)
    	{
    		// TODO message
    		throw new RuntimeException("", e);
    	}
    }
}
