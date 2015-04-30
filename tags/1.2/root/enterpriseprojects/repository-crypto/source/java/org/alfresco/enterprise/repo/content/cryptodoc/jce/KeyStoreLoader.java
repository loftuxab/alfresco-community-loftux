/*
 * Copyright 2014-2014 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.enterprise.repo.content.cryptodoc.jce;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.io.Resource;
import org.springframework.util.ResourceUtils;

/**
 * 
 * @author sglover
 *
 */
public class KeyStoreLoader implements ApplicationContextAware
{
	/**
	 * The application context might not be available, in which case the usual URL
	 * loading is used.
	 */
	private ApplicationContext applicationContext;

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException
	{
		this.applicationContext = applicationContext;
	}

	/**
	 * Helper method to switch between application context resource loading or
	 * simpler current classloader resource loading.
	 */
	private InputStream getSafeInputStream(String location)
	{
		try
		{
			final InputStream is;
			if (applicationContext != null)
			{
				Resource resource = applicationContext.getResource(location);
				if (resource.exists())
				{
					is = new BufferedInputStream(resource.getInputStream());
				}
				else
				{
					// Fall back to conventional loading
					File file = ResourceUtils.getFile(location);
					if (file.exists())
					{
						is = new BufferedInputStream(new FileInputStream(file));
					}
					else
					{
						is = null;
					}
				}
			}
			else
			{
				// Load conventionally (i.e. we are in a unit test)
				File file = ResourceUtils.getFile(location);
				if (file.exists())
				{
					is = new BufferedInputStream(new FileInputStream(file));
				}
				else
				{
					is = null;
				}
			}

			return is;
		}
		catch (IOException e) 
		{
			return null;
		}
	}

	public boolean loadKeyStore(KeyStore keyStore, String keyStoreLocation, char[] keystorePassword)
			throws NoSuchAlgorithmException, CertificateException, IOException
	{
		boolean success = false;

		if(keyStoreLocation != null)
		{
			InputStream in = getSafeInputStream(keyStoreLocation);
			if(in != null)
			{
				try
				{
					keyStore.load(in, keystorePassword);
					success = true;
				}
				finally 
				{
					if(in != null)
					{
						in.close();
					}
				}
			}
		}

		return success;
	}
}
