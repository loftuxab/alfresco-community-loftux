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

import org.alfresco.encryption.AlfrescoKeyStore;
import org.alfresco.encryption.InvalidKeystoreException;
import org.alfresco.encryption.MissingKeyException;
import org.alfresco.encryption.ReEncryptor;
import org.alfresco.error.AlfrescoRuntimeException;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedOperationParameters;
import org.springframework.jmx.export.annotation.ManagedResource;

/**
 * MBean exposing repository encryption administrator functionality.
 * 
 * @since 4.0
 *
 */
@ManagedResource
public class EncryptionAdmin implements EncryptionAdminMBean
{
	private AlfrescoKeyStore keyStore;
	private ReEncryptor reEncryptor;
	
	public EncryptionAdmin(ReEncryptor reEncryptor, AlfrescoKeyStore keyStore)
	{
		this.keyStore = keyStore;
		this.reEncryptor = reEncryptor;
	}

	protected boolean isNullOrBlank(String value)
	{
		return(value == null || value.equals(""));
	}

	/**
     * {@inheritDoc}
     */
    @Override
    @ManagedOperation(description = "Reencrypt all encryptable properties")
    @ManagedOperationParameters({
    })
    public int reEncrypt() throws MissingKeyException
	{
    	ClassLoader backup = Thread.currentThread().getContextClassLoader();
    	try
    	{
			// Hack: override context class loader (which seems to be set to system class loader for JMX threads,
	    	// causing RetryingTransactionHelper bean proxying to fail because it uses the context class loader)
	    	Thread.currentThread().setContextClassLoader(getClass().getClassLoader());
	    	
			// refresh the key providers to pick up changes made
	    	try
	    	{
	    		keyStore.reload();
	    	}
	    	catch(InvalidKeystoreException e)
	    	{
	    		throw new AlfrescoRuntimeException("Unexpected exception", e);
	    	}
	    	catch(MissingKeyException e)
	    	{
	    		throw new AlfrescoRuntimeException("Unexpected exception", e);
	    	}

			int numProperties = reEncryptor.reEncrypt();
			return numProperties;
    	}
    	finally
    	{
    		Thread.currentThread().setContextClassLoader(backup);
    	}
	}
}
