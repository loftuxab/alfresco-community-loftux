/*
 * Copyright 2014-2014 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.enterprise.repo.content.cryptodoc.jce;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import org.alfresco.encryption.MissingKeyException;
import org.alfresco.enterprise.repo.content.cryptodoc.CryptoEngineService;
import org.alfresco.enterprise.repo.content.cryptodoc.CryptoException;
import org.alfresco.enterprise.repo.content.cryptodoc.DecryptingContentReader;
import org.alfresco.enterprise.repo.content.cryptodoc.EncryptingContentWriter;
import org.alfresco.enterprise.repo.content.cryptodoc.KeyEncryptedKeyProcessor;
import org.alfresco.enterprise.repo.content.cryptodoc.KeyReference;
import org.alfresco.enterprise.repo.content.cryptodoc.MasterKeyPair;
import org.alfresco.enterprise.repo.content.cryptodoc.MasterKeystoreService;
import org.alfresco.enterprise.repo.content.cryptodoc.io.ByteBufferChannel;
import org.alfresco.enterprise.repo.content.cryptodoc.io.IOUtils;
import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.repo.domain.contentdata.ContentDataDAO;
import org.alfresco.repo.domain.contentdata.ContentUrlKeyEntity;
import org.alfresco.repo.domain.contentdata.EncryptedKey;
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.ContentWriter;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author Brian Long
 * @author sglover
 *
 */
public class JceService implements CryptoEngineService, KeyEncryptedKeyProcessor
{
	private static Log logger = LogFactory.getLog(JceService.class);

	private MasterKeystoreService masterKeystoreService;
	private ContentDataDAO contentDataDAO;

	public void setMasterKeystoreService(MasterKeystoreService masterKeystoreService)
	{
		this.masterKeystoreService = masterKeystoreService;
	}

	public void setContentDataDAO(ContentDataDAO contentDataDAO)
	{
		this.contentDataDAO = contentDataDAO;
	}

	@Override
	public KeyReference encrypt(ReadableByteChannel rbchannel, WritableByteChannel wbchannel, int chunkSize)
	{
		throw new UnsupportedOperationException();
	}
	
	@Override
	public void encrypt(Key key, ReadableByteChannel rbchannel, WritableByteChannel wbchannel, int chunkSize) throws InvalidKeyException, IOException
	{
		try
		{
			EncryptingByteChannel ebchannel = new EncryptingByteChannel(wbchannel, key);
			try
			{
				IOUtils.copy(rbchannel, ebchannel, chunkSize);
			}
			finally
			{
				ebchannel.close();
			}
		}
		catch (InvalidAlgorithmParameterException | NoSuchPaddingException e)
		{
			throw new SecurityException("This should never happen: " + e.getMessage(), e);
		}
		catch (NoSuchAlgorithmException nsae)
		{
			throw new InvalidKeyException("The algorithm '" + key.getAlgorithm() + "' is not supported: " + nsae.getMessage(), nsae);
		}
	}
	
	@Override
	public EncryptingContentWriter getEncryptingContentWriter(Key key, ContentWriter cwriter,
			ContentReader existingContentReader, int chunkSize)
	{
		return new JceEncryptingContentWriter(key, cwriter, existingContentReader, chunkSize, this, contentDataDAO);
	}
	
	@Override
	public void decrypt(Key key, ReadableByteChannel rbchannel, WritableByteChannel wbchannel, int chunkSize) throws InvalidKeyException, IOException
	{
		try 
		{
			DecryptingByteChannel dbchannel = new DecryptingByteChannel(rbchannel, key);
			try
			{
				IOUtils.copy(dbchannel, wbchannel, chunkSize);
			}
			finally
			{
				dbchannel.close();
			}
		}
		catch (InvalidAlgorithmParameterException | NoSuchPaddingException e)
		{
			throw new SecurityException("This should never happen: " + e.getMessage(), e);
		}
		catch (NoSuchAlgorithmException nsae)
		{
			throw new InvalidKeyException("The algorithm '" + key.getAlgorithm() + "' is not supported: " + nsae.getMessage(), nsae);
		}
	}
	
	@Override
	public DecryptingContentReader getDecryptingContentReader(EncryptedKey encryptedKey, ContentReader creader,
			long unencryptedFileSize, int chunkSize) throws IOException, MissingKeyException
	{
		Key key = decryptSymmetricKey(encryptedKey);
		return new JceDecryptingContentReader(key, creader, unencryptedFileSize, chunkSize);
	}

	public EncryptedKey encryptSymmetricKey(Key key) throws IOException
	{
		MasterKeyPair masterKey = this.masterKeystoreService.getNextEncryptionKey();
		if(masterKey == null)
		{
		    throw new AlfrescoRuntimeException("Unable to get master key, misconfigured master key store?");
		}

		return encryptSymmetricKey(masterKey, key);
	}

	private EncryptedKey encryptSymmetricKey(MasterKeyPair mkey, Key key) throws IOException
	{
		// prepare for key encryption
		ByteBuffer decryptedKeyAsByteBuffer = ByteBuffer.wrap(key.getEncoded());
		ByteBufferChannel encryptedKeyAsChannel = new ByteBufferChannel(decryptedKeyAsByteBuffer.capacity() + 1024);
		try {
			ByteBufferChannel decryptedKeyAsChannel = new ByteBufferChannel(decryptedKeyAsByteBuffer);
			try
			{
				encrypt(mkey.getEncryptionKey(), decryptedKeyAsChannel,
						encryptedKeyAsChannel, decryptedKeyAsChannel.bytesToRead());
			}
			catch (InvalidKeyException e)
			{
				throw new CryptoException("This should never happen: " + e.getMessage(), e);
			}
			finally 
			{
				decryptedKeyAsChannel.close();
			}

			ByteBuffer encryptedKeyAsByteBuffer = encryptedKeyAsChannel.getByteBuffer();
			EncryptedKey ekey = new EncryptedKey(this.masterKeystoreService.getId(), mkey.getAlias(),
					key.getAlgorithm(), encryptedKeyAsByteBuffer);
			return ekey;
		}
		finally
		{
			encryptedKeyAsChannel.close();
		}
	}

	public Key decryptSymmetricKey(final EncryptedKey ekey)
			throws IOException, MissingKeyException
	{
		String masterKeyAlias = ekey.getMasterKeyAlias();
		KeyReference mKeyRef = new KeyReference();
		mKeyRef.setAlias(masterKeyAlias);
		if(!masterKeystoreService.supportsId(ekey.getMasterKeystoreId()))
		{
			throw new MissingKeyException("Due to changed master key provider "
			        + ekey.getMasterKeystoreId()
			        + ", unable to find master key "
					+ ekey.getMasterKeyAlias());
		}
		final Key mKey = this.masterKeystoreService.getDecryptionKey(mKeyRef);
		if(mKey == null)
		{
		    throw new AlfrescoRuntimeException("Master key "
		            + mKeyRef.getAlias()
		            + " could not be found during decryption of content");
		}
		return decryptSymmetricKey(ekey, mKey);
	}

	public Key decryptSymmetricKey(final EncryptedKey ekey, final Key masterDecryptionKey)
			throws IOException, MissingKeyException
	{
		final String mkeyAlias = ekey.getMasterKeyAlias(); 
		KeyReference mKeyRef = new KeyReference();
		mKeyRef.setAlias(mkeyAlias);

		Key key = null;
		final String mKeyAlias = mKeyRef.getAlias();
		final String algorithm = ekey.getAlgorithm();

		ByteBufferChannel decryptedKeyAsChannel = new ByteBufferChannel(ekey.getByteBuffer().capacity() + 1024);
		try {
			ByteBufferChannel encryptedKeyAsChannel = new ByteBufferChannel(ekey.getByteBuffer());
			try
			{
				decrypt(masterDecryptionKey, encryptedKeyAsChannel,
						decryptedKeyAsChannel, encryptedKeyAsChannel.bytesToRead());
				ByteBuffer decryptedKeyAsByteBuffer = decryptedKeyAsChannel.getByteBuffer();
				byte[] decryptedKey = new byte[decryptedKeyAsByteBuffer.remaining()];
				decryptedKeyAsByteBuffer.get(decryptedKey);

				key = new SecretKeySpec(decryptedKey, algorithm);
			}
			catch (InvalidKeyException ike) 
			{
				throw new MissingKeyException("Unable to find a key for alias '" + mKeyAlias + "'");
			}
			finally
			{
				encryptedKeyAsChannel.close();
			}
		}
		finally
		{
			decryptedKeyAsChannel.close();
		}

		return key;
	}

    /**
     * Must be called in a transaction.
     * 
     * @param contentUrlKey
     * @throws IOException
     * @throws MissingKeyException
     * @throws DecoderException
     */
	@Override
    public boolean reencryptSymmetricKey(ContentUrlKeyEntity contentUrlKey) throws IOException, MissingKeyException, DecoderException
    {
        EncryptedKey encryptedKey = contentUrlKey.getEncryptedKey();

        // decrypt using the master key 
        Key key = decryptSymmetricKey(encryptedKey);

        // re-encrypt with a new master key (which will be chosen from the set of master keys
        // registered with the system)
        EncryptedKey newEncryptedKey = encryptSymmetricKey(key);

        // update the cache/db with the new symmetric key
        ContentUrlKeyEntity newContentUrlKey = ContentUrlKeyEntity.setEncryptedKey(contentUrlKey, newEncryptedKey);
        boolean success = contentDataDAO.updateContentUrlKey(contentUrlKey.getContentUrlId(), newContentUrlKey);
        return success;
    }
}
