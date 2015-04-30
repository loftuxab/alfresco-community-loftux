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
import java.nio.channels.ClosedChannelException;
import java.nio.channels.WritableByteChannel;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import java.util.LinkedList;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.ShortBufferException;
import javax.crypto.spec.IvParameterSpec;

public class EncryptingByteChannel implements WritableByteChannel
{
	
	//private static final Logger LOGGER = LogManager.getLogger(EncryptingByteChannel.class);
	private static final String CIPHER_PADDING = "PKCS5Padding";
	
	private final Collection<CryptoChannelCloseListener> listeners = new LinkedList<CryptoChannelCloseListener>();
	private final WritableByteChannel wbchannel;
	private Cipher cipher;
	private long totalBytesBeforeEncrypt = 0L;
	private long totalBytesAfterEncrypt = 0L;
	private final Object closeLock = new Object();
	private volatile boolean open = true;
	
	public EncryptingByteChannel(WritableByteChannel wbchannel, Key key)
	throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException
	{
		this.wbchannel = wbchannel;
		
		Cipher cipher = Cipher.getInstance(key.getAlgorithm());
		
		if (cipher.getBlockSize() == 0)
		{ // is not a block cipher
			cipher.init(Cipher.ENCRYPT_MODE, key);
		}
		else
		{
			String cipherMode = "CBC";  // good for block ciphers
			cipher = Cipher.getInstance(key.getAlgorithm() + "/" + cipherMode + "/" + CIPHER_PADDING);

			/**
			 * Since we are using a different key for each node, we don't need to have
			 * a different IV for each node.
			 * 
			 * An IV is really just another key used by an algorithm called
			 * "CBC".  We are basically wrapping a symmetric encryption around
			 * another symmetric encryption.  However, this is only applicable
			 * for block cipher algorithms.  On the off chance that the
			 * preferred algorithm isn't a block cipher, we are making IV as
			 * transparent as possible.
			 * 
			 * iv = 0x00000000000000000000000000000000
			 */
			byte[] iv = new byte[cipher.getBlockSize()];
			cipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(iv));
		}
		
		this.cipher = cipher;
	}
	
	@Override
	public void close() throws IOException
	{
	    synchronized (closeLock)
	    {
	        try
	        {
	            closeImpl();
	        }
	        finally
	        {
	            cipher = null;
	            open = false;
	        }
	    }
	}
	
	private void closeImpl() throws IOException
	{
	    if (!open)
        {
            // It's OK to call close() multiple times when all the data has been processed.
            return;
        }
        
        try
        {
            ByteBuffer closeBuffer = ByteBuffer.allocate(0);
            ByteBuffer encryptedBuffer = ByteBuffer.allocate(this.cipher.getOutputSize(0));

            this.cipher.doFinal(closeBuffer, encryptedBuffer);
            
            // write the encrypted data to the target buffer
            encryptedBuffer.flip();
            int bytesWritten = this.wbchannel.write(encryptedBuffer);
            this.totalBytesAfterEncrypt += bytesWritten;
        } catch (ShortBufferException | IllegalBlockSizeException | BadPaddingException e) {
            throw new IOException("This should never happen: " + e.getMessage(), e);
        }

        for (CryptoChannelCloseListener listener : this.listeners)
        {
            listener.onClose(this.totalBytesBeforeEncrypt, this.totalBytesAfterEncrypt);
        }
	}
	
	@Override
	public boolean isOpen() 
	{
		return open && wbchannel.isOpen();
	}
	
	@Override
	public int write(ByteBuffer writeBuffer) throws IOException
	{
	    if (!isOpen())
        {
            throw new ClosedChannelException();
        }
		int encryptedBufferCapacity = this.cipher.getOutputSize(writeBuffer.remaining());
		ByteBuffer encryptedBuffer = ByteBuffer.allocate(encryptedBufferCapacity);
		
		try
		{	
			// if there are no leftovers or there were enough for a full block, encrypt some more
			this.totalBytesBeforeEncrypt += writeBuffer.remaining();
			this.cipher.update(writeBuffer, encryptedBuffer);
		}
		catch (ShortBufferException sbe)
		{
			throw new IOException("The output buffer length is too short (" + encryptedBufferCapacity + "): " + sbe.getMessage(), sbe);
		}
	
		// write the encrypted data to the target buffer
		encryptedBuffer.flip();
		int bytesWritten = this.wbchannel.write(encryptedBuffer);
		this.totalBytesAfterEncrypt += bytesWritten;
		
		return bytesWritten;
	}
	
	public void addListener(CryptoChannelCloseListener listener)
	{
		this.listeners.add(listener);
	}
	
	public long getTotalBytesBeforeEncrypt()
	{
		return this.totalBytesBeforeEncrypt;
	}
	
	public long getTotalBytesAfterEncrypt() 
	{
		return this.totalBytesAfterEncrypt;
	}
	
	public static interface CryptoChannelCloseListener
	{
		void onClose(long totalBytesBefore, long totalBytesAfter);
	}

}
