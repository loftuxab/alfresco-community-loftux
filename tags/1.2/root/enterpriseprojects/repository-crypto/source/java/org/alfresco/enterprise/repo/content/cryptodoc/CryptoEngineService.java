/*
 * Copyright 2014-2014 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.enterprise.repo.content.cryptodoc;

import java.io.IOException;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.security.InvalidKeyException;
import java.security.Key;

import org.alfresco.encryption.MissingKeyException;
import org.alfresco.repo.domain.contentdata.EncryptedKey;
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.ContentWriter;

/**
 * This service defines a cryptographic engine or cipher.
 * 
 * This supports two different ways to encrypt or decrypt bytes.  One method
 * supports the Java NIO methodology and the other uses Alfresco's
 * org.alfresco.service.cmr.repository.ContentReader and
 * org.alfresco.service.cmr.repository.ContentWriter.
 * 
 * @author Brian Long
 */
public interface CryptoEngineService
{
	
	/**
	 * This method encrypts the readable channel and returns a reference to the
	 * key used for encryption.  The encryption is streamed into the writable
	 * channel to limit memory usage.  The streaming only works for linear
	 * ciphers, like block ciphers.
	 * 
	 * @param rbchannel A channel of bytes to be encrypted
	 * @param wbchannel A channel where to stream encrypted bytes
	 * @param chunkSize A memory footprint size to use while encrypting/streaming
	 * @return A reference to a key
	 * @throws InvalidKeyException The specified key is invalid
	 * @throws IOException The channels could not be read or written
	 */
	KeyReference encrypt(ReadableByteChannel rbchannel, WritableByteChannel wbchannel, int chunkSize) throws InvalidKeyException, IOException;
	
	/**
	 * This method encrypts the readable channel using the specified key.  The
	 * encryption is streamed into the writable channel to limit memory usage.
	 * The streaming only works for linear ciphers, like block ciphers.
	 * 
	 * @param key A secret or public key
	 * @param rbchannel A channel of bytes to be encrypted
	 * @param wbchannel A channel where to stream encrypted bytes
	 * @param chunkSize A memory footprint size to use while encrypting/streaming
	 * @throws InvalidKeyException The specified key is invalid
	 * @throws IOException The channels could not be read or written
	 */
	void encrypt(Key key, ReadableByteChannel rbchannel, WritableByteChannel wbchannel, int chunkSize) throws InvalidKeyException, IOException;

	/**
	 * This method wraps the Alfresco content writer with a compatible
	 * encrypting content writer.  The encryption is streamed to limit memory
	 * usage.  The streaming only works for linear ciphers, like block ciphers.
	 * 
	 * @param key A secret or public key
	 * @param cwriter A channel where bytes will be written
	 * @param chunkSize A memory footprint size to use while encrypting/streaming
	 * @return A wrapping channel where bytes will be encrypted while being written
	 * @throws InvalidKeyException The specified key is invalid
	 * @throws IOException The content writer could not be written
	 */
	EncryptingContentWriter getEncryptingContentWriter(Key key, ContentWriter cwriter,
			ContentReader existingContentReader, int chunkSize) throws InvalidKeyException, IOException;

	/**
	 * This method decrypts the readable channel using the specified key.  The
	 * decryption is streamed into the writable channel to limit memory usage.
	 * The streaming only works for linear ciphers, like block ciphers.
	 * 
	 * @param key A secret or private key
	 * @param rbchannel A channel of bytes to be decrypted
	 * @param wbchannel A channel where to stream decrypted bytes
	 * @param chunkSize A memory footprint size to use while decrypting/streaming
	 * @throws InvalidKeyException The specified key is invalid
	 * @throws IOException The channels could not be read or written
	 */
	void decrypt(Key key, ReadableByteChannel rbchannel, WritableByteChannel wbchannel, int chunkSize) throws InvalidKeyException, IOException;

	/**
	 * This method wraps the Alfresco content reader with a compatible
	 * decrypting content reader.  The decryption is streamed to limit memory
	 * usage.  The streaming only works for linear ciphers, like block ciphers.
	 * 
	 * @param key A secret or private key
	 * @param creader A channel where bytes will be read
	 * @param chunkSize A memory footprint size to use while decrypting/streaming
	 * @return A wrapping channel where bytes will be decrypted while being read
	 * @throws InvalidKeyException The specified key is invalid
	 * @throws IOException The content reader could not be read
	 */
	DecryptingContentReader getDecryptingContentReader(EncryptedKey encryptedKey, ContentReader creader,
			long unencryptedFileSize, int chunkSize) throws IOException, MissingKeyException;

}
