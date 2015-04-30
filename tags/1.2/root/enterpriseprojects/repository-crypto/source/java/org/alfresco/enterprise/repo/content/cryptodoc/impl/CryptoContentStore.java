/*
 * Copyright 2014-2014 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.enterprise.repo.content.cryptodoc.impl;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.util.Date;
import java.util.concurrent.atomic.AtomicBoolean;

import org.alfresco.encryption.MissingKeyException;
import org.alfresco.enterprise.license.InvalidLicenseEvent;
import org.alfresco.enterprise.license.ValidLicenseEvent;
import org.alfresco.enterprise.repo.content.cryptodoc.CryptoEngineService;
import org.alfresco.enterprise.repo.content.cryptodoc.CryptoException;
import org.alfresco.enterprise.repo.content.cryptodoc.DecryptingContentReader;
import org.alfresco.enterprise.repo.content.cryptodoc.EncryptingContentWriter;
import org.alfresco.enterprise.repo.content.cryptodoc.KeyEncryptedKeyProcessor;
import org.alfresco.enterprise.repo.content.cryptodoc.KeyGenerationService;
import org.alfresco.enterprise.repo.content.cryptodoc.jce.JceDecryptingContentReader;
import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.repo.content.ContentContext;
import org.alfresco.repo.content.ContentStoreCaps;
import org.alfresco.repo.domain.contentdata.ContentDataDAO;
import org.alfresco.repo.domain.contentdata.ContentUrlEntity;
import org.alfresco.repo.domain.contentdata.ContentUrlKeyEntity;
import org.alfresco.repo.domain.contentdata.EncryptedKey;
import org.alfresco.repo.tenant.TenantDeployer;
import org.alfresco.repo.tenant.TenantRoutingContentStore;
import org.alfresco.repo.tenant.TenantRoutingFileContentStore;
import org.alfresco.service.cmr.repository.ContentIOException;
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.ContentStreamListener;
import org.alfresco.service.cmr.repository.ContentWriter;
import org.alfresco.service.descriptor.DescriptorService;
import org.alfresco.util.PropertyCheck;
import org.apache.commons.codec.DecoderException;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.extensions.surf.util.I18NUtil;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Encrypted Content Store (cryptodoc)
 * <p>
 * Decorates a Content Store with an encrypted writer and encrypted reader.
 * <p>
 * A license is required to use this component.  Without a license you can still read 
 * encrypted content, but you can't write new encrypted content.
 * 
 * @author sglover
 * @author brian long
 */
public class CryptoContentStore implements org.alfresco.repo.content.ContentStore, ContentStoreCaps,
	ApplicationListener<ApplicationEvent>
{
	private static final Logger LOGGER = LogManager.getLogger(CryptoContentStore.class);
	
	private org.alfresco.repo.content.ContentStore contentStore;
	private KeyGenerationService keyGenerationService;
	private CryptoEngineService contentCryptoEngineService;
	private DescriptorService descriptorService;	
	private String storeName = "Encrypted File Content Store";
	private ContentDataDAO contentDataDAO;
	private KeyEncryptedKeyProcessor keyProcessor;

	private CryptodocLicense cryptodocLicense;

	private int streamingChunkSize = 16384;

	private AtomicBoolean suppressLicenseWarning = new AtomicBoolean(false);

	private enum CryptodocLicenseStatus
	{
		UNKNOWN,
		ENABLED,
		DISABLED
	}

	CryptodocLicenseStatus licenseStatus = CryptodocLicenseStatus.UNKNOWN;

	public void setKeyProcessor(KeyEncryptedKeyProcessor keyProcessor)
	{
		this.keyProcessor = keyProcessor;
	}

	public void setContentDataDAO(ContentDataDAO contentDataDAO)
	{
		this.contentDataDAO = contentDataDAO;
	}
	
	public void setContentStore(org.alfresco.repo.content.ContentStore contentStore)
	{
		this.contentStore = contentStore;
	}
	
	public void setKeyGenerationService(KeyGenerationService keyGenerationService)
	{
		this.keyGenerationService = keyGenerationService;
	}

	public void setContentCryptoEngineService(CryptoEngineService contentCryptoEngineService)
	{
		this.contentCryptoEngineService = contentCryptoEngineService;
	}

	public void setDescriptorService(DescriptorService descriptorService)
	{
		this.descriptorService = descriptorService;
	}
	
	public void setStreamingChunkSize(int streamingChunkSize)
	{
		this.streamingChunkSize = streamingChunkSize;
	}
	
	protected int getStreamingChunkSize()
	{
		return this.streamingChunkSize;
	}

	public void init()
	{
		this.validate();

		if (LOGGER.isDebugEnabled())
		{
			LOGGER.debug("Using streaming chunk size: " + this.streamingChunkSize);
		}

		/*
		 * Descriptor service is typically bootstrapped late in startup
		 * always after the content store which must be early.   
		 * So we need to cope with not having the descriptor service available. 
		 * This code below works nicely with subsystem restarts.   But has no effect 
		 * on initial bootstrap.
		 */
		if(descriptorService.isBootstrapped() && descriptorService.getLicenseDescriptor() != null) 
		{
			boolean isCryptodocLicenseEnabled = descriptorService.getLicenseDescriptor().isCryptodocEnabled();
		
			licenseStatus = isCryptodocLicenseEnabled ? CryptodocLicenseStatus.ENABLED : CryptodocLicenseStatus.DISABLED ;
		
			if (LOGGER.isInfoEnabled())
			{
				if(licenseStatus == CryptodocLicenseStatus.ENABLED)
				{
					Object[] params = { storeName };
					String infoMessage = I18NUtil.getMessage("cryptodoc.enabled", params);
					LOGGER.info(infoMessage);
				}
				else
				{
					Object[] params = { storeName };
					String infoMessage = I18NUtil.getMessage("cryptodoc.disabled", params);
					LOGGER.info(infoMessage);
				}
			}
		}	
	}

	private void validate()
	{
		PropertyCheck.mandatory(this, "descriptorService", this.descriptorService);
		PropertyCheck.mandatory(this, "contentStore", this.contentStore);
		PropertyCheck.mandatory(this, "keyGenerationService", this.keyGenerationService);
		PropertyCheck.mandatory(this, "contentCryptoEngineService", this.contentCryptoEngineService);
		PropertyCheck.mandatory(this, "cryptodocLicense", this.cryptodocLicense);
	}

	private boolean checkLicense()
	{
		boolean ret = true;

		/**
		 * Check the cryptodoc license here.   
		 */
		if(licenseStatus != CryptodocLicenseStatus.ENABLED && ! cryptodocLicense.isCryptodocLicenseValid())
		{
			// Cryptodoc License not enabled either through DescriptorService or bean in main context
		    if(!suppressLicenseWarning.get())
		    {
		    	Object[] params = { storeName };
		    	String warnMessage = I18NUtil.getMessage("cryptodoc.no.license.no.writer", params);
		    	LOGGER.warn(warnMessage);
			    suppressLicenseWarning.set(true);
		    }

		    ret = false;
		}
		else
		{
			if(suppressLicenseWarning.get())
			{
			    suppressLicenseWarning.set(false);
			    if(LOGGER.isInfoEnabled())
			    {
			        Object[] params = { storeName };
			        String infoMessage = I18NUtil.getMessage("cryptodoc.enabled", params);
			        LOGGER.info(infoMessage);
			    }
			}
		}

		return ret;
	}

	private ContentUrlKeyEntity getContentUrlKey(String contentUrl, boolean contentUrlMustExist)
	{
		ContentUrlKeyEntity contentUrlKey = null;

		ContentUrlEntity contentUrlEntity = contentDataDAO.getContentUrl(contentUrl);
		if(contentUrlEntity == null)
		{
			if(contentUrlMustExist)
			{
				throw new AlfrescoRuntimeException("Content url " + contentUrl + " is not known");
			}
		}
		else
		{
			contentUrlKey = contentUrlEntity.getContentUrlKey();
		}

		return contentUrlKey;
	}

	@Override
	@Transactional(propagation=Propagation.REQUIRED)
	public ContentReader getReader(final String contentUrl)
	{
		ContentReader ret = null;

		if (LOGGER.isTraceEnabled())
		{
			LOGGER.trace("getReader('" + contentUrl + "'): starting ...");
		}
		
		ContentReader creader = this.contentStore.getReader(contentUrl);

		ContentUrlKeyEntity contentUrlKey = getContentUrlKey(contentUrl, true);
		if(contentUrlKey != null)
		{
			try
			{
				long unencryptedFileSize = contentUrlKey.getUnencryptedFileSize();
	
				EncryptedKey encryptedKey = contentUrlKey.getEncryptedKey();

				final DecryptingContentReader dcreader = this.contentCryptoEngineService.getDecryptingContentReader(
						encryptedKey, creader, unencryptedFileSize, this.streamingChunkSize);
				if (LOGGER.isDebugEnabled())
				{
					dcreader.addListener(new ContentStreamListener()
					{
						@Override
						public void contentStreamClosed()
						{
							LOGGER.debug("getReader('" + contentUrl + "'): callback closed; [" + dcreader.getSize() + " bytes]");
						}
					});
				}

				if (LOGGER.isTraceEnabled())
				{
					LOGGER.trace("getReader('" + contentUrl + "'): finished");
				}

				ret = dcreader;
			}
			catch (DecoderException | MissingKeyException | IOException e)
			{
			    // we make this a warn because it results from a genuine exception. In the case of
			    // a missing key exception, the master key is missing
			    LOGGER.warn("Bypassing decryption for " + contentUrl, e);
				ret = creader;
			}
		}
		else
		{
		    // we make this a debug because there may be content that is not encrypted 
		    LOGGER.debug("Bypassing decryption for " + contentUrl + ", cannot find content symmetric key");

			ret = creader;
		}

		return ret;
	}
	
	@Override
	@Transactional(propagation=Propagation.REQUIRED)
	public ContentWriter getWriter(ContentContext context)
	{
		ContentWriter writer = null;

		if (LOGGER.isTraceEnabled())
		{
			LOGGER.trace("getWriter('" + context.getContentUrl() + "'): starting ...");
		}

		ContentWriter cwriter = this.contentStore.getWriter(context);

		if(!checkLicense())
		{
			// no license - return a writer to the unencrypted backing store
			writer = cwriter;
		}
		else
		{
			try 
			{
				final Key key = this.keyGenerationService.generateSymmetricKey();

				String contentUrl = cwriter.getContentUrl();
				ContentReader existingContentReader = null;

				ContentUrlKeyEntity contentUrlKey = getContentUrlKey(contentUrl, false);
				if(contentUrlKey != null)
				{
					// we have an existing content key, provide an decrypting content reader for the existing content
					try
					{
						Key existingContentKey = keyProcessor.decryptSymmetricKey(contentUrlKey.getEncryptedKey());
						long unencryptedLength = contentUrlKey.getUnencryptedFileSize();
						existingContentReader = new JceDecryptingContentReader(existingContentKey,
								context.getExistingContentReader(), unencryptedLength, this.streamingChunkSize);
					}
					catch (DecoderException | MissingKeyException | IOException e)
					{
						LOGGER.warn("Unable to create decrypting content reader for : " + contentUrl, e);
					}
				}

				final EncryptingContentWriter ecwriter = this.contentCryptoEngineService
						.getEncryptingContentWriter(key, cwriter, existingContentReader, this.streamingChunkSize);
	
				if (LOGGER.isTraceEnabled())
				{
					LOGGER.trace("getWriter('" + cwriter.getContentUrl() + "'): finished");
				}
				
				writer = ecwriter;
			}
			catch (InvalidKeyException ike)
			{
				throw new CryptoException("Failed to generate or store key: " + ike.getMessage(), ike);
			}
			catch (IOException ie)
			{
				throw new CryptoException("Failed to generate key or encrypt content: " + ie.getMessage(), ie);
			}
		}

		return writer;
	}
	
	@Override
	public boolean delete(String contentUrl)
	{
		return this.contentStore.delete(contentUrl);
	}
	
	@Override
	public boolean exists(String contentUrl)
	{
		return this.contentStore.exists(contentUrl);
	}
	
	@Override
	public String getRootLocation()
	{
		return this.contentStore.getRootLocation();
	}
	
	@Override
	public long getSpaceFree()
	{
		return this.contentStore.getSpaceFree();
	}
	
	@Override
	public long getSpaceTotal()
	{
		return this.contentStore.getSpaceTotal();
	}
	
    /**
     * @throws UnsupportedOperationException       always
     */
	@Override
	public void getUrls(ContentUrlHandler cuhandler) throws ContentIOException
	{
		throw new UnsupportedOperationException();
	}

	/**
	 * @throws UnsupportedOperationException       always
	 */
	@Override
	public void getUrls(Date fromDate, Date toDate, ContentUrlHandler cuhandler) throws ContentIOException
	{
        throw new UnsupportedOperationException();
	}
	
	@Override
	public boolean isContentUrlSupported(String contentUrl)
	{
		return this.contentStore.isContentUrlSupported(contentUrl);
	}
	
	@Override
	public boolean isWriteSupported()
	{
		return this.contentStore.isWriteSupported();
	}

	@Override
	public void onApplicationEvent(ApplicationEvent event) 
	{
	    if(event instanceof InvalidLicenseEvent)
        {
	    	if(licenseStatus != CryptodocLicenseStatus.DISABLED)
	        {
	    	    Object[] params = { storeName };
		        String infoMessage = I18NUtil.getMessage("cryptodoc.disabled", params);
				LOGGER.info(infoMessage);
	        }
            licenseStatus = CryptodocLicenseStatus.DISABLED;
            
			
        }
        else if(event instanceof ValidLicenseEvent)
        {
           ValidLicenseEvent vle = (ValidLicenseEvent)event;
           boolean isCryptodocLicenseEnabled = vle.getLicenseDescriptor().isCryptodocEnabled();
       	   
           if(licenseStatus != CryptodocLicenseStatus.ENABLED && isCryptodocLicenseEnabled)
           {
        	   // enabled from not being enabled
        	   Object[] params = { storeName };
			   String infoMessage = I18NUtil.getMessage("cryptodoc.enabled", params);
			   suppressLicenseWarning.set(false);
			   LOGGER.info(infoMessage);
           }
           
           licenseStatus = isCryptodocLicenseEnabled ? CryptodocLicenseStatus.ENABLED : CryptodocLicenseStatus.DISABLED ;
   
           if(LOGGER.isDebugEnabled())
           {
        	   LOGGER.debug("valid license event" + isCryptodocLicenseEnabled);
           }
        }  	
	}
	/**
	 * @return  Human readable store name
	 */
	public String getStoreName() 
	{
		return storeName;
	}

	/**
	 * The name of the store
	 * @param storeName
	 */
	public void setStoreName(String storeName) 
	{
		this.storeName = storeName;
	}

	public CryptodocLicense getCryptodocLicense() 
	{
		return cryptodocLicense;
	}

	public void setCryptodocLicense(CryptodocLicense cryptodocLicense) 
	{
		this.cryptodocLicense = cryptodocLicense;
	}

    public TenantDeployer getTenantRoutingContentStore()
    {
        if (this.contentStore instanceof TenantDeployer)
        {
            return (TenantRoutingContentStore)contentStore;
        }
        return null;
    }

    public TenantDeployer getTenantDeployer()
    {
        if (this.contentStore instanceof TenantDeployer)
        {
            return (TenantDeployer)contentStore;
        }
        return null;
    }
}
