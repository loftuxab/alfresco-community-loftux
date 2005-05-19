package org.alfresco.repo.content;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.alfresco.error.AlfrescoRuntimeException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.FileCopyUtils;

/**
 * Implements all the convenience methods of the interface.
 * 
 * @author Derek Hulley
 */
public abstract class AbstractContentWriter extends AbstractContent implements ContentWriter
{
    private static final Log logger = LogFactory.getLog(AbstractContentWriter.class);
    
    private List<ContentStreamListener> listeners;
    private OutputStream outputStream;
    private boolean streamClosed;
    
    /**
     * @param contentUrl the content URL
     */
    protected AbstractContentWriter(String contentUrl)
    {
        super(contentUrl);
        
        listeners = new ArrayList<ContentStreamListener>(2);
        streamClosed = false;   // just to document it explicitly
        // add a stream close listener by default
        ContentStreamListener streamCloseListener = new ContentStreamListener()
            {
                public void contentStreamClosed() throws ContentIOException
                {
                    streamClosed = true;
                }
            };
        listeners.add(streamCloseListener);
    }
    
    /**
     * Adds the listener after checking that the output stream isn't already in
     * use.
     */
    public synchronized void addListener(ContentStreamListener listener)
    {
        if (outputStream != null)
        {
            throw new ContentIOException("OutputStream is already in use");
        }
        listeners.add(listener);
    }
    
    /**
     * An automatically created listener sets the flag
     */
    public final boolean isClosed()
    {
        return streamClosed;
    }

    /**
     * A factory method for subclasses to implement that will ensure the proper
     * implementation of the {@link ContentWriter#getReader()} method.
     * <p>
     * This method will only be called once the output stream for the underlying
     * writer has been closed.  <b>It will never be called during or before the
     * write operation</b>.
     * <p>
     * Only the instance need be constructed.  The required mimetype, encoding, etc
     * will be copied across by this class.
     *  
     * @return Returns a reader onto the location where the content <b>was</b> written.
     *      The instance must <b>always</b> be a new instance.
     * @throws ContentIOException
     */
    protected abstract ContentReader createReader() throws ContentIOException;
    
    /**
     * Manages the output stream to ensure that the reader is only returned once
     * the output stream has been closed.
     */
    public final ContentReader getReader() throws ContentIOException
    {
        // check the the stream has not been closed
        if (!streamClosed)
        {
            return null;    // interface mandates this behaviour
        }
        // it is safe to create the reader
        ContentReader reader = createReader();
        if (reader == null)
        {
            throw new AlfrescoRuntimeException("ContentWriter failed to create post-write reader: \n" +
                    "   writer: " + this);
        }
        else if (reader.getContentUrl() == null || !reader.getContentUrl().equals(getContentUrl()))
        {
            throw new AlfrescoRuntimeException("ContentReader has different URL: \n" +
                    "   writer: " + this + "\n" +
                    "   reader: " + reader);
        }
        // copy across common attributes
        reader.setMimetype(this.getMimetype());
        reader.setEncoding(this.getEncoding());
        // done
        if (logger.isDebugEnabled())
        {
            logger.debug("Writer spawned reader: \n" +
                    "   writer: " + this + "\n" +
                    "   reader: " + reader);
        }
        return reader;
    }

    /**
     * Provides low-level access to write content to the repository, allowing the stream
     * provided to call back when closed.
     * <p>
     * This is the only of the content <i>writing</i> methods that needs to be implemented
     * by derived classes.  All other methods use the <code>OutputStream</code> in
     * their implementations.
     * <p>
     *  
     * @return Returns a stream from which the content can be read
     * @throws ContentIOException
     */
    protected abstract OutputStream getDirectOutputStream() throws ContentIOException;
    
    /**
     * Wraps the direct output stream with an output stream that provides a callback
     * when it is closed.
     * <p>
     * A check is made to ensure that the output stream is only retrieved once.
     */
    public synchronized final OutputStream getContentOutputStream() throws ContentIOException
    {
        // this is a use-once object
        if (outputStream != null)
        {
            throw new ContentIOException("An output stream has already been opened");
        }
        
        outputStream = getDirectOutputStream();
        // wrap the output stream only if there are listeners present
        if (listeners.size() > 0)
        {
            outputStream = new CallbackOutputStream(outputStream);
            // it will call the close method when it gets closed
        }
        else
        {
            // just keep the original output stream
        }
        return outputStream;
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
     * Inner <code>OutputStream</code> that executes the callback when the
     * output stream is closed.  It delegates to the output stream given
     * by {@link AbstractContentWriter#getDirectOutputStream()}.
     * <p>
     * The callback is made to {@link AbstractContentWriter#listeners}.
     * 
     * @author Derek Hulley
     */
    private class CallbackOutputStream extends OutputStream
    {
        /*
         * Override most methods in order to go direct to the delegate's
         * implementations.
         */
        
        private OutputStream delegate;
        
        public CallbackOutputStream(OutputStream delegate)
        {
            this.delegate = delegate;
        }
        public void close() throws IOException
        {
            delegate.close();
            // call the listeners
            for (ContentStreamListener listener : AbstractContentWriter.this.listeners)
            {
                listener.contentStreamClosed();
            }
        }
        public void flush() throws IOException
        {
            delegate.flush();
        }
        public void write(int b) throws IOException
        {
            delegate.write(b);
        }
        public void write(byte[] b) throws IOException
        {
            delegate.write(b);
        }
        public void write(byte[] b, int off, int len) throws IOException
        {
            delegate.write(b, off, len);
        }
    }
}
