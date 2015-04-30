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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;

import javax.crypto.NoSuchPaddingException;

import org.alfresco.enterprise.repo.content.cryptodoc.DecryptingContentReader;
import org.alfresco.enterprise.repo.content.cryptodoc.io.IOUtils;
import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.repo.content.filestore.FileContentWriter;
import org.alfresco.service.cmr.repository.ContentData;
import org.alfresco.service.cmr.repository.ContentIOException;
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.ContentStreamListener;
import org.alfresco.util.TempFileProvider;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class JceDecryptingContentReader implements DecryptingContentReader
{
	private static Log logger = LogFactory.getLog(JceDecryptingContentReader.class);
	
	private final Key key;
	private final ContentReader creader;
	private final long unencryptedLength;
	private final int chunkSize;

	public JceDecryptingContentReader(Key key, ContentReader creader, long unencryptedLength, int chunkSize)
	{
		this.key = key;
		this.creader = creader;
		this.unencryptedLength = unencryptedLength;
		this.chunkSize = chunkSize;
	}

	@Override
	public boolean isChannelOpen()
	{
		return this.creader.isChannelOpen();
	}

	@Override
	public void addListener(ContentStreamListener cslistener)
	{
		this.creader.addListener(cslistener);
	}

	@Override
	public long getSize()
	{
		return this.unencryptedLength;
	}

	@Override
	public ContentData getContentData()
	{
		ContentData contentData = this.creader.getContentData();
		return contentData;
	}

	@Override
	public String getContentUrl() 
	{
		return this.creader.getContentUrl();
	}

	@Override
	public String getMimetype()
	{
		return this.creader.getMimetype();
	}

	@Override
	public void setMimetype(String mimetype)
	{
		this.creader.setMimetype(mimetype);
	}

	@Override
	public String getEncoding()
	{
		return this.creader.getEncoding();
	}

	@Override
	public void setEncoding(String encoding)
	{
		this.creader.setEncoding(encoding);
	}

	@Override
	public Locale getLocale()
	{
		return this.creader.getLocale();
	}

	@Override
	public void setLocale(Locale locale)
	{
		this.creader.setLocale(locale);
	}

	@Override
	public ContentReader getReader() throws ContentIOException
	{
		return new JceDecryptingContentReader(this.key, this.creader.getReader(),
				this.unencryptedLength, this.chunkSize);
	}

	@Override
	public boolean exists()
	{
		return this.creader.exists();
	}

	@Override
	public long getLastModified()
	{
		return this.creader.getLastModified();
	}

	@Override
	public boolean isClosed() 
	{
		return this.creader.isClosed();
	}

	@Override
	public ReadableByteChannel getReadableChannel() throws ContentIOException
	{
		final ReadableByteChannel rbchannel = this.creader.getReadableChannel();
		try
		{
			DecryptingByteChannel dbchannel = new DecryptingByteChannel(rbchannel, this.key);
			dbchannel.addListener(new DecryptingByteChannel.ChannelCloseListener()
			{
				@Override
				public void onClose()
				{
					IOUtils.closeQuietly(rbchannel);
				}
			});
			return dbchannel;
		}
		catch (NoSuchAlgorithmException nsae)
		{
			throw new AlfrescoRuntimeException("The node decryption algorithm '" + this.key.getAlgorithm() + "' is not supported by the JVM: " + nsae.getMessage(), nsae);
		}
		catch (NoSuchPaddingException | InvalidKeyException | InvalidAlgorithmParameterException e)
		{
			throw new AlfrescoRuntimeException("The node decryption key is not valid for the algorithm: " + e.getMessage(), e);
		}
	}

	@Override
	public FileChannel getFileChannel() throws ContentIOException
	{
		final ReadableByteChannel channel = getReadableChannel();

        // No random access support is provided by the implementation.
        // Spoof it by providing a 2-stage read from a temp file
        final File tempFile = TempFileProvider.createTempFile("random_read_spoof_", ".bin");
        FileContentWriter spoofWriter = new FileContentWriter(tempFile);
        // pull the content in from the underlying channel
        FileChannel spoofWriterChannel = spoofWriter.getFileChannel(true);
        try
        {
            long spoofFileSize = this.getSize();
            spoofWriterChannel.transferFrom(channel, 0, spoofFileSize);
        }
        catch (IOException e)
        {
            throw new ContentIOException("Failed to copy from permanent channel to spoofed temporary channel: \n" +
                    "   reader: " + this + "\n" +
                    "   temp writer: " + spoofWriter,
                    e);
        }
        finally
        {
            try
            {
            	spoofWriterChannel.close();
            }
            catch (IOException e)
            {
            }
        }

        // get a reader onto the spoofed content
        final ContentReader spoofReader = spoofWriter.getReader();

        // Attach a listener
        // - ensure that the close call gets propogated to the underlying channel
        ContentStreamListener spoofListener = new ContentStreamListener()
        {
            public void contentStreamClosed() throws ContentIOException
            {
                try
                {
                    if (logger.isDebugEnabled())
                    {
                        logger.debug("JceDecryptingContentReader closing underlying channel \n" +
                                "   channel: " + channel);
                    }

                    channel.close();
                }
                catch (IOException e)
                {
                    throw new ContentIOException("Failed to close underlying channel", e);
                }
                finally
                {
                	try
                	{
                        if (logger.isDebugEnabled())
                        {
                            logger.debug("JceDecryptingContentRedaer removing temp file: " + tempFile);
                        }

                		tempFile.delete();
                	}
                    catch (SecurityException e)
                    {
                    	logger.warn("JceDecryptingContentRedaer failed to remove temp file: " + tempFile, e);
                    }
                }
            }
        };
        spoofReader.addListener(spoofListener);
        // we now have the spoofed up channel that the client can work with
        FileChannel clientFileChannel = spoofReader.getFileChannel();
        // debug
        if (logger.isDebugEnabled())
        {
            logger.debug("JceDecryptingContentRedaer provided indirect support for FileChannel: \n" +
                    "   reader: " + this + "\n" +
                    "   temp writer: " + spoofWriter);
        }

        return clientFileChannel;
	}

	@Override
	public InputStream getContentInputStream() throws ContentIOException
	{
		ReadableByteChannel rbchannel = this.getReadableChannel();
		return new BufferedInputStream(Channels.newInputStream(rbchannel));
	}

	@Override
	public void getContent(OutputStream ostream) throws ContentIOException
	{
		long startTime = System.currentTimeMillis();

		ReadableByteChannel rbchannel = this.getReadableChannel();
		try
		{
			IOUtils.copy(rbchannel, ostream, this.chunkSize);
			
			if (logger.isDebugEnabled())
			{
				logger.debug("Time to stream content to stream, including decryption: " + (System.currentTimeMillis()-startTime) + " ms");
			}
		}
		catch (IOException ie)
		{
			throw new ContentIOException("The content stream could not be decrypted: " + ie.getMessage(), ie);
		}
		finally
		{
			IOUtils.closeQuietly(rbchannel);
		}
	}

	@Override
	public void getContent(File file) throws ContentIOException 
	{
		try
		{
			FileOutputStream fostream = new FileOutputStream(file);
			try
			{
				this.getContent(fostream);
			} 
			finally
			{
				fostream.close();
			}
		}
		catch (IOException ie)
		{
			throw new ContentIOException("The file, '" + file + "', had issues: " + ie.getMessage(), ie);
		}
	}

	@Override
	public String getContentString() throws ContentIOException
	{
		long startTime = System.currentTimeMillis();

		ReadableByteChannel rbchannel = this.getReadableChannel();
		try
		{
			Reader reader = Channels.newReader(rbchannel, this.getEncoding());
			try
			{
				String value = org.apache.commons.io.IOUtils.toString(reader);
				
				if (logger.isDebugEnabled())
				{
					logger.debug("Time to stream content to string, including decryption: " + (System.currentTimeMillis()-startTime) + " ms");
				}
				
				return value;
			}
			finally 
			{
				reader.close();
			}
		}
		catch (IOException ie)
		{
			throw new ContentIOException("The content stream could not be decrypted: " + ie.getMessage(), ie);
		}
		finally
		{
			IOUtils.closeQuietly(rbchannel);
		}
	}

	@Override
	public String getContentString(int i) throws ContentIOException
	{
		char[] chars = new char[i];
		int charsActuallyRead = 0;

		ReadableByteChannel rbchannel = this.getReadableChannel();
		try
		{
			Reader reader = Channels.newReader(rbchannel, this.getEncoding());
			charsActuallyRead = reader.read(chars, 0, i);
		}
		catch (IOException ie)
		{
			throw new ContentIOException("The content stream could not be decrypted: " + ie.getMessage(), ie);
		}
		finally
		{
			IOUtils.closeQuietly(rbchannel);
		}
		
		return new String(chars, 0, charsActuallyRead);
	}
	

}
