/*
 * Copyright 2014-2014 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.enterprise.repo.content.cryptodoc.jce;

import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.Security;
import java.util.LinkedList;
import java.util.List;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import org.alfresco.enterprise.repo.content.cryptodoc.KeyGenerationService;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author Brian Long
 * @author sglover
 *
 */
public class KeyGenerationServiceImpl implements KeyGenerationService
{
	private static Log logger = LogFactory.getLog(MasterKeyStoreServiceImpl.class);

	private String defaultSymmetricAlgorithm = "AES";
	private int defaultSymmetricKeySize = 256;
	private String providerName;

	private Provider provider;

	public void setDefaultSymmetricAlgorithm(String defaultSymmetricAlgorithm)
	{
		this.defaultSymmetricAlgorithm = defaultSymmetricAlgorithm;
	}

	public void setDefaultSymmetricKeySize(int defaultSymmetricKeySize)
	{
		this.defaultSymmetricKeySize = defaultSymmetricKeySize;
	}

	public void setProviderName(String providerName) 
	{
		this.providerName = StringUtils.trimToNull(providerName);
	}

	public String getDefaultSymmetricAlgorithm()
	{
		return this.defaultSymmetricAlgorithm;
	}

	@Override
	public int getDefaultSymmetricKeySize()
	{
		return this.defaultSymmetricKeySize;
	}

	private void initProvider()
	{
		if (this.providerName == null)
		{
			return;
		}
		
		this.provider = Security.getProvider(this.providerName);
		if (this.provider != null)
		{
			if (logger.isDebugEnabled())
			{
				logger.debug("Found '" + this.providerName + "' provider: " + this.provider.getClass().getName());
			}
		}
		else
		{
			logger.error("Could not find provider: " + this.providerName);
			
			List<String> providerNames = new LinkedList<String>();
			for (Provider provider : Security.getProviders())
			{
				providerNames.add(provider.getName());
			}
			if (logger.isInfoEnabled())
			{
				logger.info("Available providers: " + providerNames);
			}
			throw new IllegalArgumentException("The '" + providerName + "' provider does not exist; valid providers: " + providerNames);
		}
	}

	public void init() throws KeyStoreException
	{
		this.initProvider();
	}

	@Override
	public SecretKey generateSymmetricKey(String algorithm, int keySize) throws NoSuchAlgorithmException
	{
		KeyGenerator generator = this.provider == null
				? KeyGenerator.getInstance(algorithm)
				: KeyGenerator.getInstance(algorithm, this.provider);
		generator.init(keySize);
		return generator.generateKey();
	}

	@Override
	public SecretKey generateSymmetricKey()
	{
		try
		{
			KeyGenerator generator = this.provider == null
					? KeyGenerator.getInstance(this.defaultSymmetricAlgorithm)
					: KeyGenerator.getInstance(this.defaultSymmetricAlgorithm, this.provider);
			generator.init(this.defaultSymmetricKeySize);
			return generator.generateKey();
		}
		catch (NoSuchAlgorithmException nsae)
		{
			throw new RuntimeException("This should never happen: " + nsae.getMessage(), nsae);
		}
	}
}
