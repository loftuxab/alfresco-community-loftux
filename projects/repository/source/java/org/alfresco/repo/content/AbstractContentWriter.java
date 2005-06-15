package org.alfresco.repo.content;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.WritableByteChannel;
import java.util.ArrayList;
import java.util.List;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.service.cmr.repository.Content;
import org.alfresco.service.cmr.repository.ContentIOException;
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.ContentStreamListener;
import org.alfresco.service.cmr.repository.ContentWriter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.FileCopyUtils;

/**
 * Implements all the convenience methods of the interface.  The only methods
 * that need to be implemented, i.e. provide low-level content access are:
 * <ul>
 *   <li>{@link #getDirectWritableChannel()} to write content to the repository</li>
 * </ul>
 * 
 * @author Derek Hulley
 */
public abstract class AbstractContentWriter extends AbstractContent implements ContentWriter
{
    private static final Log logger = LogFactory.getLog(AbstractContentWriter.class);
    
    private List<ContentStreamListener> listeners;
    private WritableByteChannel channel;
    
    /**
     * @param contentUrl the content URL
     */
    protected AbstractContentWriter(String contentUrl)
    {
        super(contentUrl);
        
        listeners = new ArrayList<ContentStreamListener>(2);
    }
    
    /**
     * Adds the listener after checking that the output stream isn't already in
     * use.
     */
    public synchronized void addListener(ContentStreamListener listener)
    {
        if (channel != null)
        {
            throw new RuntimeException("Channel is already in use");
        }
        listeners.add(listener);
    }

    /**
     * A factory method for subclasses to implement that will ensure the proper
     * implementation of the {@link ContentWriter#getReader()} method.
     * <p>
     * Only the instance need be constructed.  The required mimetype, encoding, etc
     * will be copied across by this class.
     * <p>
     *  
     * @return Returns a reader onto the location referenced by this instance.
     *      The instance must <b>always</b> be a new instance and never null.
     * @throws ContentIOException
     */
    protected abstract ContentReader createReader() throws ContentIOException;
    
    /**
     * Performs checks and copies required reader attributes
     */
    public final ContentReader getReader() throws ContentIOException
    {
        if (!isClosed())
        {
            return null;
        }
        ContentReader reader = createReader();
        if (reader == null)
        {
            throw new AlfrescoRuntimeException("ContentReader failed to create new reader: \n" +
                    "   writer: " + this);
        }
        else if (reader.getContentUrl() == null || !reader.getContentUrl().equals(getContentUrl()))
        {
            throw new AlfrescoRuntimeException("ContentReader has different URL: \n" +
                    "   writer: " + this + "\n" +
                    "   new reader: " + reader);
        }
        // copy across common attributes
        reader.setMimetype(this.getMimetype());
        reader.setEncoding(this.getEncoding());
        // done
        if (logger.isDebugEnabled())
        {
            logger.debug("Writer spawned new reader: \n" +
                    "   writer: " + this + "\n" +
                    "   new reader: " + reader);
        }
        return reader;
    }

    /**
     * An automatically created listener sets the flag
     */
    public synchronized final boolean isClosed()
    {
        if (channel != null)
        {
            return !channel.isOpen();
        }
        else
        {
            return false;
        }
    }

    /**
     * Provides low-level access to write content to the repository.
     * <p>
     * This is the only of the content <i>writing</i> methods that needs to be implemented
     * by derived classes.  All other content access methods make use of this in their
     * underlying implementations.
     * 
     * @return Returns a channel with which to write content
     * @throws ContentIOException if the channel could not be opened
     */
    protected abstract WritableByteChannel getDirectWritableChannel() throws ContentIOException;
    
    /**
     * Optionally override to supply an alternate callback channel.
     *
     * @param directChannel the result of {@link #getDirectWritableChannel()}
     * @param listeners the listeners to call
     * @return Returns a callback channel
     * @throws ContentIOException
     * 
     * @see AbstractContentWriter.CallbackChannel
     */
    protected WritableByteChannel getCallbackWritableChannel(
            WritableByteChannel directChannel,
            List<ContentStreamListener> listeners)
            throws ContentIOException
    {
        // wrap it
        WritableByteChannel callbackChannel = new CallbackChannel(directChannel, listeners);
        // done
        return callbackChannel;
    }

    /**
     * @see #getDirectWritableChannel()
     * @see #getCallbackWritableChannel()
     */
    public synchronized final WritableByteChannel getWritableChannel() throws ContentIOException
    {
        // this is a use-once object
        if (channel != null)
        {
            throw new RuntimeException("A channel has already been opened");
        }
        WritableByteChannel directChannel = getDirectWritableChannel();
        channel = getCallbackWritableChannel(directChannel, listeners);
        // done
        if (logger.isDebugEnabled())
        {
            logger.debug("Opened channel onto content: " + this);
        }
        return channel;
    }
    
    /**
     * @see Channels#newOutputStream(java.nio.channels.WritableByteChannel)
     */
    public OutputStream getContentOutputStream() throws ContentIOException
    {
        try
        {
            WritableByteChannel channel = getWritableChannel();
            OutputStream is = Channels.newOutputStream(channel);
            // done
            return is;
        }
        catch (Throwable e)
        {
            throw new ContentIOException("Failed to open stream onto channel: \n" +
                    "   accessor: " + this,
                    e);
        }
    }

    public final void putContent(InputStream is) throws ContentIOException
    {
        try
        {
            OutputStream os = getContentOutputStream();
            FileCopyUtils.copy(is, os);     // both streams are closed
            // done
        }
        catch (IOException e)
        {
            throw new ContentIOException("Failed to copy content from input stream: \n" +
                    "   accessor: " + this,
                    e);
        }
    }
    
    public final void putContent(File file) throws ContentIOException
    {
        try
        {
            OutputStream os = getContentOutputStream();
            FileInputStream is = new FileInputStream(file);
            FileCopyUtils.copy(is, os);     // both streams are closed
            // done
        }
        catch (IOException e)
        {
            throw new ContentIOException("Failed to copy content from file: \n" +
                    "   accessor: " + this + "\n" +
                    "   file: " + file,
                    e);
        }
    }
    
    /**
     * Makes use of the encoding, if available, to convert the string to bytes.
     * 
     * @see Content#getEncoding()
     */
    public final void putContent(String content) throws ContentIOException
    {
        try
        {
            // attempt to use the correct encoding
            String encoding = getEncoding();
            byte[] bytes = (encoding == null) ? content.getBytes() : content.getBytes(encoding);
            // get the stream
            OutputStream os = getContentOutputStream();
            ByteArrayInputStream is = new ByteArrayInputStream(bytes);
            FileCopyUtils.copy(is, os);     // both streams are closed
            // done
        }
        catch (IOException e)
        {
            throw new ContentIOException("Failed to copy content from string: \n" +
                    "   accessor: " + this +
                    "   content length: " + content.length(),
                    e);
        }
    }

    /**
     * Provides callbacks to the {@link ContentStreamListener listeners}.
     * 
     * @author Derek Hulley
     */
    private static class CallbackChannel implements WritableByteChannel
    {
        /*
         * Override most methods in order to go direct to the delegate's
         * implementations.
         */
        
        private WritableByteChannel delegate;
        private List<ContentStreamListener> listeners;
        
        public CallbackChannel(WritableByteChannel delegate, List<ContentStreamListener> listeners)
        {
            this.delegate = delegate;
            this.listeners = listeners;
        }
        
        public boolean isOpen()
        {
            return delegate.isOpen();
        }
        public int write(ByteBuffer src) throws IOException
        {
            return delegate.write(src);
        }
        public void close() throws IOException
        {
            delegate.close();
            // call the listeners
            for (ContentStreamListener listener : listeners)
            {
                listener.contentStreamClosed();
            }
        }
    }
}
