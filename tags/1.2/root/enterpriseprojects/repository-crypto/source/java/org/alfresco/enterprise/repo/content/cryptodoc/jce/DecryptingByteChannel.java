/*
 * Copyright 2014-2014 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.enterprise.repo.content.cryptodoc.jce;

import java.io.IOException;
import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
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

public class DecryptingByteChannel implements ReadableByteChannel
{
	//private static final Logger LOGGER = LogManager.getLogger(DecryptingByteChannel.class);
	private static final String CIPHER_PADDING = "PKCS5Padding";

	private final Collection<ChannelCloseListener> listeners = new LinkedList<ChannelCloseListener>();
	private final ReadableByteChannel rbchannel;
	private final Cipher cipher;
	private ByteBuffer decryptedBuffer;
	private ByteBuffer readBuffer = ByteBuffer.allocate(4096);
	private volatile boolean isChannelOpen = true;
	
	public DecryptingByteChannel(ReadableByteChannel rbchannel, Key key)
	throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException
	{
		this.rbchannel = rbchannel;
		
		Cipher cipher = Cipher.getInstance(key.getAlgorithm());
		
		if (cipher.getBlockSize() == 0)
		{ // is not a block cipher
			cipher.init(Cipher.DECRYPT_MODE, key);
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
			cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(iv));
		}
		
		this.cipher = cipher;
	}
	
	@Override
	public void close() throws IOException
	{
		this.doClose();
	}
	
	@Override
	public boolean isOpen() 
	{
		return (rbchannel.isOpen() && isChannelOpen) || ((decryptedBuffer != null) && decryptedBuffer.hasRemaining());
	}
	
	@Override
	/**
	 * Add leftovers on the decrypted buffer side
	 */
	public int read(ByteBuffer outputBuffer) throws IOException 
	{
	    int initialPosition = outputBuffer.position();

	    if (outputBuffer.remaining() == 0)
	    {
	        throw new IOException("The destination buffer must allow at least 1 byte to be written");
	    }

	    if(!isOpen())
	    {
	        doClose();
	        return -1;
	    }
		
		try
		{
		    boolean hasMore = true;;
		    while(flushOutAndCheckIfWeCanSendMore(outputBuffer) && hasMore)
		    {
		        hasMore = fillReadyToReadAndHasMore();
		    }
		    
		    return outputBuffer.position() - initialPosition;
		}
		catch (ShortBufferException | IllegalBlockSizeException | BadPaddingException e)
		{
		    throw new IOException("Decryption failed", e);
		}
		
	}

    /**
     * @return
     * @throws IOException
     * @throws ShortBufferException
     * @throws BadPaddingException 
     * @throws IllegalBlockSizeException 
     */
    private boolean fillReadyToReadAndHasMore() throws IOException, ShortBufferException, IllegalBlockSizeException, BadPaddingException
    {
        // Bug out if we have already seen teh end
        if(!isChannelOpen)
        {
            return false;
        }
        
        // clear the read buffer then full it up until it is full or there is no more to get
        readBuffer.clear();
        int read = 0;
		while(readBuffer.hasRemaining() && (read != -1))
		{
		    read = this.rbchannel.read(readBuffer);
		}
		// get ready to write....
		readBuffer.flip();

		// We lazily grow the decrypt buffer or reuse if it will take the result
		int requiredDecryptedBufferSize = cipher.getOutputSize(readBuffer.limit());
		if((decryptedBuffer == null) || (decryptedBuffer.capacity() < requiredDecryptedBufferSize))
		{
		    decryptedBuffer = ByteBuffer.allocate(requiredDecryptedBufferSize);
		}
		else
		{
		    decryptedBuffer.clear();
		    decryptedBuffer.limit(requiredDecryptedBufferSize);
		}
		
		// Updste and get the decrypt buffer ready to go 
		if(read != -1)
		{
		    cipher.update(readBuffer, decryptedBuffer);
		    decryptedBuffer.flip();
		    // There is more
		    return true;
		}
		else
		{
		    // got the lasdt chunk from the file
		    cipher.doFinal(readBuffer, decryptedBuffer);
		    decryptedBuffer.flip();
		    
		    // close
		    // The channel is not closed - it is done via a registered listener ......
            isChannelOpen = false;
            // There is no more
		    return false;
		}
		
		
    }

    /**
     * @param outputBuffer
     */
    private boolean flushOutAndCheckIfWeCanSendMore(ByteBuffer outputBuffer)
    {
        // The decrypt buffer is created lazily 
        if((decryptedBuffer != null))
		{
            // There is more then enough to meet the reuest
		    if(outputBuffer.remaining() < decryptedBuffer.remaining())
		    {
		        int savedLimit = decryptedBuffer.limit();
		        decryptedBuffer.limit(decryptedBuffer.position() + outputBuffer.remaining());
		        outputBuffer.put(decryptedBuffer);
		        decryptedBuffer.limit(savedLimit);
		    }
		    else 
		    {
		        outputBuffer.put(decryptedBuffer);
		        decryptedBuffer.clear();
		        decryptedBuffer.limit(0);
		    }
		}
        // Could we write more?
        return outputBuffer.hasRemaining();
    }
	
	public void addListener(ChannelCloseListener listener)
	{
		synchronized (this.listeners)
		{
			this.listeners.add(listener);
		}
	}

	private void doClose() throws IOException
	{
	    synchronized (this.listeners)
	    {
	        for (ChannelCloseListener listener : this.listeners)
	        {
	            listener.onClose();
	        }
	        this.listeners.clear();
	    }
	}

	public static interface ChannelCloseListener
	{
		void onClose();
	}
}
