/*
 * Copyright 2014-2014 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.enterprise.repo.content.cryptodoc.jce;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.charset.Charset;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.crypto.NoSuchPaddingException;

import org.alfresco.enterprise.repo.content.cryptodoc.EncryptingContentWriter;
import org.alfresco.enterprise.repo.content.cryptodoc.KeyEncryptedKeyProcessor;
import org.alfresco.enterprise.repo.content.cryptodoc.io.IOUtils;
import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.repo.content.EmptyContentReader;
import org.alfresco.repo.content.encoding.ContentCharsetFinder;
import org.alfresco.repo.content.filestore.FileContentWriter;
import org.alfresco.repo.domain.contentdata.ContentDataDAO;
import org.alfresco.repo.domain.contentdata.ContentUrlKeyEntity;
import org.alfresco.repo.domain.contentdata.EncryptedKey;
import org.alfresco.service.cmr.repository.ContentData;
import org.alfresco.service.cmr.repository.ContentIOException;
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.ContentStreamListener;
import org.alfresco.service.cmr.repository.ContentWriter;
import org.alfresco.service.cmr.repository.MimetypeService;
import org.alfresco.service.cmr.repository.MimetypeServiceAware;
import org.alfresco.util.TempFileProvider;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

// TOOD this really should inherit from AbstractContentWriter to automatically pick up all the common behaviour in there
public class JceEncryptingContentWriter implements EncryptingContentWriter, MimetypeServiceAware
{
	private static final Logger logger = LogManager.getLogger(JceEncryptingContentWriter.class);
	
	private final Key key;
	private final ContentWriter cwriter;
	private final int chunkSize;
	private long totalBytesBeforeEncrypt = 0L;
	private long totalBytesAfterEncrypt = 0L;
	private MimetypeService mimetypeService;
	private DoGuessingOnCloseListener guessingOnCloseListener;
	private ContentReader existingContentReader;

	private List<ContentStreamListener> listeners;

	private KeyEncryptedKeyProcessor keyEncryptedKeyProcessor;
	private ContentDataDAO contentDataDAO;

	public JceEncryptingContentWriter(Key key, ContentWriter cwriter, ContentReader existingContentReader, int chunkSize,
			KeyEncryptedKeyProcessor keyEncryptedKeyProcessor, ContentDataDAO contentDataDAO)
	{
		this.key = key;
		this.cwriter = cwriter;
		this.chunkSize = chunkSize;
		this.keyEncryptedKeyProcessor = keyEncryptedKeyProcessor;
		this.contentDataDAO = contentDataDAO;
		this.listeners = new ArrayList<ContentStreamListener>(2);
		this.existingContentReader = existingContentReader;

		this.guessingOnCloseListener = new DoGuessingOnCloseListener();
		listeners.add(guessingOnCloseListener);
	}

	@Override
	public void setMimetypeService(MimetypeService mimetypeService)
	{
		this.mimetypeService = mimetypeService;
	}

	@Override
	public void addListener(ContentStreamListener listener)
	{
		listeners.add(listener);
	}

	@Override
	public ContentData getContentData()
	{
		ContentData property = new ContentData(getContentUrl(), getMimetype(), getSize(), getEncoding(), getLocale());
		return property;
	}

	@Override
	public String getContentUrl()
	{
		return this.cwriter.getContentUrl();
	}

	@Override
	public String getEncoding()
	{
		return this.cwriter.getEncoding();
	}

	@Override
	public Locale getLocale()
	{
		return this.cwriter.getLocale();
	}

	@Override
	public String getMimetype()
	{
		return this.cwriter.getMimetype();
	}

	@Override
	public long getSize()
	{
		return totalBytesBeforeEncrypt;
	}

	@Override
	public boolean isChannelOpen()
	{
		return this.cwriter.isChannelOpen();
	}

	@Override
	public void setEncoding(String encoding)
	{
		this.cwriter.setEncoding(encoding);
	}

	@Override
	public void setLocale(Locale locale)
	{
		this.cwriter.setLocale(locale);
	}

	@Override
	public void setMimetype(String mimetype)
	{
		this.cwriter.setMimetype(mimetype);
	}

	@Override
	public OutputStream getContentOutputStream() throws ContentIOException
	{
		WritableByteChannel writableChannel = this.getWritableChannel();
		return new BufferedOutputStream(Channels.newOutputStream(writableChannel));
	}

	@Override
	public FileChannel getFileChannel(boolean truncate) throws ContentIOException
	{
		final WritableByteChannel channel = getWritableChannel();

		// now use this channel if it can provide the random access, otherwise spoof it
		FileChannel clientFileChannel = null;

		// No random access support is provided by the implementation.
		// Spoof it by providing a 2-stage write via a temp file
		File tempFile = TempFileProvider.createTempFile("random_write_spoof_", ".bin");
		final FileContentWriter spoofWriter = new FileContentWriter(
				tempFile,                      // the file to write to
				existingContentReader);        // this ensures that the existing content is pulled in
		// Attach a listener
		// - to ensure that the content gets loaded from the temp file once writing has finished
		// - to ensure that the close call gets passed on to the underlying channel
		ContentStreamListener spoofListener = new ContentStreamListener()
		{
			public void contentStreamClosed() throws ContentIOException
			{
				// the spoofed temp channel has been closed, so get a new reader for it
				ContentReader spoofReader = spoofWriter.getReader();
				FileChannel spoofChannel = spoofReader.getFileChannel();
				// upload all the temp content to the real underlying channel
				try
				{
					long spoofFileSize = spoofChannel.size();
					spoofChannel.transferTo(0, spoofFileSize, channel);
				}
				catch (IOException e)
				{
					throw new ContentIOException("Failed to copy from spoofed temporary channel to permanent channel: \n" +
							"   writer: " + this + "\n" +
							"   temp: " + spoofReader,
							e);
				}
				finally
				{
					try { spoofChannel.close(); } catch (Throwable e) {}
					try
					{
						channel.close();
					}
					catch (IOException e)
					{
						throw new ContentIOException("Failed to close underlying channel", e);
					}
				}
			}
		};
		spoofWriter.addListener(spoofListener);
		// we now have the spoofed up channel that the client can work with
		clientFileChannel = spoofWriter.getFileChannel(truncate);
		// debug
		if (logger.isDebugEnabled())
		{
			logger.debug("Content writer provided indirect support for FileChannel: \n" +
					"   writer: " + this + "\n" +
					"   temp writer: " + spoofWriter);
		}

		// the file is now available for random access
		return clientFileChannel;
	}

	private void updateContentUrlKey()
	{
		long length = getTotalBytesBeforeEncrypt();
		
		try
		{
			EncryptedKey encryptedKey = keyEncryptedKeyProcessor.encryptSymmetricKey(key);

			ContentUrlKeyEntity contentUrlKeyEntity = new ContentUrlKeyEntity();
			contentUrlKeyEntity.setUnencryptedFileSize(length);
			contentUrlKeyEntity.setEncryptedKey(encryptedKey);

			contentDataDAO.updateContentUrlKey(getContentUrl(), contentUrlKeyEntity);
	
			if (logger.isDebugEnabled())
			{
				logger.debug("getWriter('" + getContentUrl() + "'): callback closed; [" + length + " bytes]");
			}
		}
		catch (IOException e)
		{
			throw new ContentIOException("", e);
		}
	}

	@Override
	public ContentReader getReader() throws ContentIOException
	{
	    String contentUrl = getContentUrl();
        if (!isClosed())
        {
            return new EmptyContentReader(contentUrl);
        }
        ContentReader reader = new JceDecryptingContentReader(key, cwriter.getReader(),
        		getTotalBytesBeforeEncrypt(), chunkSize);
        
        if (reader.getContentUrl() == null || !reader.getContentUrl().equals(contentUrl))
        {
            throw new AlfrescoRuntimeException("ContentReader has different URL: \n" +
                    "   writer: " + this + "\n" +
                    "   new reader: " + reader);
        }
        // copy across common attributes
        reader.setMimetype(this.getMimetype());
        reader.setEncoding(this.getEncoding());
        reader.setLocale(this.getLocale());
        // done
        return reader;
	}

	@Override
	public WritableByteChannel getWritableChannel() throws ContentIOException
	{
		final WritableByteChannel wbchannel = this.cwriter.getWritableChannel();
		try 
		{
			EncryptingByteChannel ebchannel = new EncryptingByteChannel(wbchannel, this.key);
			ebchannel.addListener(new EncryptingByteChannel.CryptoChannelCloseListener()
			{
				@Override
				public void onClose(long totalBytesBefore, long totalBytesAfter)
				{
					JceEncryptingContentWriter.this.totalBytesBeforeEncrypt += totalBytesBefore;
					JceEncryptingContentWriter.this.totalBytesAfterEncrypt += totalBytesAfter;

					// not having all EncryptingByteChannel instances close the parent channel;
					// as you might want to encrypt only part of the stream
					// but in this instance, we are not, so...
					IOUtils.closeQuietly(wbchannel);

					for (ContentStreamListener listener : listeners)
					{
						listener.contentStreamClosed();
					}

					updateContentUrlKey();
				}
			});
			return ebchannel;
		}
		catch (NoSuchAlgorithmException nsae) 
		{
			throw new AlfrescoRuntimeException("The node encryption algorithm '" + this.key.getAlgorithm() + "' is not supported by the JVM: " + nsae.getMessage(), nsae);
		}
		catch (NoSuchPaddingException | InvalidKeyException | InvalidAlgorithmParameterException e)
		{
			throw new AlfrescoRuntimeException("The node encryption key is not valid for the algorithm: " + e.getMessage(), e);
		}
	}

	@Override
	public void guessEncoding()
	{
		this.cwriter.guessEncoding();
	}

	@Override
	public void guessMimetype(String filename)
	{
        if (mimetypeService == null)
        {
            logger.warn("MimetypeService not supplied, but required for content guessing");
            return;
        }
        
        
        if(isClosed())
        {
            // Content written, can do it now
            doGuessMimetype(filename);
        }
        else
        {
            // Content not yet written, wait for the
            //  data to be written before doing so
            guessingOnCloseListener.guessMimetype = true;
            guessingOnCloseListener.filename = filename;
        }
	}

	@Override
	public boolean isClosed()
	{
		return this.cwriter.isClosed();
	}

	@Override
	public void putContent(ContentReader creader) throws ContentIOException
	{
		long startTime = System.currentTimeMillis();

		WritableByteChannel wbchannel = this.getWritableChannel();
		try {
			ReadableByteChannel rbchannel = creader.getReadableChannel();
			try
			{
				IOUtils.copy(rbchannel, wbchannel, this.chunkSize);
				
				if (logger.isDebugEnabled())
				{
					logger.debug("Time to stream content to stream, including encryption: " + (System.currentTimeMillis()-startTime) + " ms");
				}
			}
			finally
			{
				IOUtils.closeQuietly(rbchannel);
			}
		}
		catch (IOException ie)
		{
			throw new ContentIOException("The content stream could not be encrypted: " + ie.getMessage(), ie);
		}
		finally
		{
			IOUtils.closeQuietly(wbchannel);
		}
	}

	@Override
	public void putContent(InputStream istream) throws ContentIOException
	{
		long startTime = System.currentTimeMillis();
		
		WritableByteChannel wbchannel = this.getWritableChannel();
		try
		{
			IOUtils.copy(istream, wbchannel, this.chunkSize);
			
			if (logger.isDebugEnabled())
			{
				logger.debug("Time to stream content to stream, including encryption: " + (System.currentTimeMillis()-startTime) + " ms");
			}
		}
		catch (IOException ie)
		{
			throw new ContentIOException("The content stream could not be encrypted: " + ie.getMessage(), ie);
		}
		finally
		{
			IOUtils.closeQuietly(wbchannel);
		}
	}

	@Override
	public void putContent(File file) throws ContentIOException
	{
		try
		{
			FileInputStream fistream = new FileInputStream(file);
			try
			{
				this.putContent(fistream);
			}
			finally
			{
				fistream.close();
			}
		}
		catch (IOException ie)
		{
			throw new ContentIOException("The file, '" + file + "', had issues: " + ie.getMessage(), ie);
		}
	}

	@Override
	public void putContent(String content) throws ContentIOException 
	{
	    try
        {
            // attempt to use the correct encoding
            String encoding = getEncoding();
            byte[] bytes;
            if(encoding == null) 
            {
                // Use the system default, and record what that was
                bytes = content.getBytes();
                setEncoding( System.getProperty("file.encoding") );
            }
            else
            {
                // Use the encoding that they specified
                bytes = content.getBytes(encoding);
            }

            // get the stream
            ByteArrayInputStream is = new ByteArrayInputStream(bytes);
            
            // InputStream will be closed.
            putContent(is);
        }
        catch (IOException e)
        {
            throw new ContentIOException("Failed to copy content from string: \n" +
                    "   writer: " + this +
                    "   content length: " + content.length(),
                    e);
        }
	}
	
	public long getTotalBytesBeforeEncrypt()
	{
		return this.totalBytesBeforeEncrypt;
	}
	
	public long getTotalBytesAfterEncrypt()
	{
		return this.totalBytesAfterEncrypt;
	}

    private void doGuessMimetype(String filename)
    {
        String mimetype = mimetypeService.guessMimetype(
                filename, getReader()
        );
        setMimetype(mimetype);
    }

    private void doGuessEncoding()
    {
        ContentCharsetFinder charsetFinder = mimetypeService.getContentCharsetFinder();
        
        ContentReader reader = getReader();
        InputStream is = reader.getContentInputStream();
        Charset charset = charsetFinder.getCharset(is, getMimetype());
        try
        {
            is.close();
        }
        catch(IOException e)
        {}
        
        setEncoding(charset.name());
    }

    /**
     * Our own listener that is always the first on the list,
     *  which lets us perform guessing operations when the
     *  content has been written.
     */
    private class DoGuessingOnCloseListener implements ContentStreamListener
    {
        private boolean guessEncoding = false;
        private boolean guessMimetype = false;
        private String filename = null;

        @Override
        public void contentStreamClosed() throws ContentIOException
        {
            if(guessMimetype)
            {
                doGuessMimetype(filename);
            }
            if(guessEncoding)
            {
                doGuessEncoding();
            }
        }
    }
}
