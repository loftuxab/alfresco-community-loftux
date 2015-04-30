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
package org.alfresco.enterprise.repo.content.cryptodoc.jmx;

import java.security.KeyStoreException;
import java.util.Map;

import javax.management.openmbean.CompositeDataSupport;
import javax.management.openmbean.CompositeType;
import javax.management.openmbean.OpenDataException;
import javax.management.openmbean.TabularData;
import javax.management.openmbean.TabularDataSupport;
import javax.management.openmbean.TabularType;

import org.alfresco.encryption.MissingKeyException;
import org.alfresco.enterprise.repo.content.cryptodoc.KeyInformation;
import org.alfresco.enterprise.repo.content.cryptodoc.KeyReference;
import org.alfresco.enterprise.repo.content.cryptodoc.MasterKeystoreService;
import org.springframework.jmx.export.annotation.ManagedResource;

/**
 * MBean exposing document at rest encryption apis.
 * 
 * @since 5.0
 * @author sglover
 *
 */
@ManagedResource
public class CryptoDoc
{
	private MasterKeystoreService masterKeyStoreService;

	private CompositeType compositeType;
	private TabularType tabularType;

	public CryptoDoc(MasterKeystoreService masterKeyStoreService) throws OpenDataException
	{
		this.masterKeyStoreService = masterKeyStoreService;

		compositeType = new CompositeType("Master Keys", "Master Keys", KeyInformation.attributeKeys,
				KeyInformation.attributeDescriptions, KeyInformation.attributeTypes);
		tabularType = new TabularType("Master Keys", "Master Keys", compositeType, KeyInformation.attributeKeys);
	}

	public void revokeMasterKey(String alias)
	{
    	ClassLoader backup = Thread.currentThread().getContextClassLoader();
    	try
    	{
			// Hack: override context class loader (which seems to be set to system class loader for JMX threads,
	    	// causing RetryingTransactionHelper bean proxying to fail because it uses the context class loader)
	    	Thread.currentThread().setContextClassLoader(getClass().getClassLoader());

			KeyReference masterKeyRef = new KeyReference();
			masterKeyRef.setAlias(alias);
			masterKeyStoreService.revokeMasterKey(masterKeyRef);
    	}
    	catch(Throwable t)
    	{
    		throw new RuntimeException("Error: " + t.getMessage());
    	}
    	finally
    	{
    		Thread.currentThread().setContextClassLoader(backup);
    	}
	}

	public void cancelRevocation(String alias)
	{
    	ClassLoader backup = Thread.currentThread().getContextClassLoader();
    	try
    	{
			// Hack: override context class loader (which seems to be set to system class loader for JMX threads,
	    	// causing RetryingTransactionHelper bean proxying to fail because it uses the context class loader)
	    	Thread.currentThread().setContextClassLoader(getClass().getClassLoader());

			KeyReference masterKeyRef = new KeyReference();
			masterKeyRef.setAlias(alias);
			masterKeyStoreService.cancelRevocation(masterKeyRef);
    	}
    	catch(Throwable t)
    	{
    		throw new RuntimeException("Error: " + t.getMessage());
    	}
    	finally
    	{
    		Thread.currentThread().setContextClassLoader(backup);
    	}
	}

	public TabularData showMasterKeys() throws KeyStoreException, OpenDataException
	{
		TabularDataSupport table = new TabularDataSupport(tabularType);
		Map<String, KeyInformation> masterKeys = masterKeyStoreService.getMasterKeys();
		for(String alias : masterKeys.keySet())
		{
			KeyInformation keyInfo = masterKeys.get(alias);
			CompositeDataSupport row = new CompositeDataSupport(compositeType, keyInfo.getValues());
			table.put(row);
		}

		return table;
	}

	public void reEncryptSymmetricKeys(String alias) throws KeyStoreException, MissingKeyException
	{
    	ClassLoader backup = Thread.currentThread().getContextClassLoader();
    	try
    	{
			// Hack: override context class loader (which seems to be set to system class loader for JMX threads,
	    	// causing RetryingTransactionHelper bean proxying to fail because it uses the context class loader)
	    	Thread.currentThread().setContextClassLoader(getClass().getClassLoader());

			KeyReference masterKeyRef = new KeyReference();
			masterKeyRef.setAlias(alias);
			masterKeyStoreService.reEncryptSymmetricKeys(masterKeyRef);
    	}
    	finally
    	{
    		Thread.currentThread().setContextClassLoader(backup);
    	}
	}
}
