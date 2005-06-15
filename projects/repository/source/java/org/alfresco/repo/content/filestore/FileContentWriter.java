package org.alfresco.repo.content.filestore;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.WritableByteChannel;
import java.util.List;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.repo.content.AbstractContentWriter;
import org.alfresco.repo.content.CallbackFileChannel;
import org.alfresco.repo.content.RandomAccessContent;
import org.alfresco.service.cmr.repository.ContentIOException;
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.ContentStreamListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Provides direct access to a local file.
 * <p>
 * This class does not provide remote access to the file.
 * 
 * @author Derek Hulley
 */
public class FileContentWriter extends AbstractContentWriter implements RandomAccessContent
{
    private static final Log logger = LogFactory.getLog(FileContentWriter.class);
    
    private File file;
    
    /**
     * @param file the file for reading and writing.  This will most likely be directly
     *      related to the content URL.
     */
    public FileContentWriter(File file)
    {
        super(FileContentStore.STORE_PROTOCOL + file.getAbsolutePath());
        
        this.file = file;
    }
    
    /**
     * @return Returns the file that this writer accesses
     */
    public File getFile()
    {
        return file;
    }

    /**
     * The URL of the write is known from the start and this method contract states
     * that no consideration needs to be taken w.r.t. the stream state.
     */
    @Override
    protected ContentReader createReader() throws ContentIOException
    {
        return new FileContentReader(this.file);
    }
    
    @Override
    protected WritableByteChannel getDirectWritableChannel() throws ContentIOException
    {
        try
        {
            // we may not write to an existing file - EVER!!
            if (file.exists() && file.length() > 0)
            {
                throw new IOException("File exists - overwriting not allowed");
            }
            // create the channel
            RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");  // will create it
            FileChannel channel = randomAccessFile.getChannel();
            // done
            if (logger.isDebugEnabled())
            {
                logger.debug("Opened channel to file: " + file);
            }
            return channel;
        }
        catch (Throwable e)
        {
            throw new ContentIOException("Failed to open file channel: " + this, e);
        }
    }

    /**
     * @param directChannel a file channel
     */
    @Override
    protected WritableByteChannel getCallbackWritableChannel(
            WritableByteChannel directChannel,
            List<ContentStreamListener> listeners) throws ContentIOException
    {
        if (!(directChannel instanceof FileChannel))
        {
            throw new AlfrescoRuntimeException("Expected read channel to be a file channel");
        }
        FileChannel fileChannel = (FileChannel) directChannel;
        // wrap it
        FileChannel callbackChannel = new CallbackFileChannel(fileChannel, listeners);
        // done
        return callbackChannel;
    }

    /**
     * @return Returns true always
     */
    public boolean canWrite()
    {
        return true;    // this is a writer
    }

    public FileChannel getChannel() throws ContentIOException
    {
        // go through the super classes to ensure that all concurrency conditions
        // and listeners are satisfied
        return (FileChannel) super.getWritableChannel();
    }
}
