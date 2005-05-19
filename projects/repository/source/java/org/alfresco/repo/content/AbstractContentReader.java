package org.alfresco.repo.content;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
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
 * Implements all the convenience methods of the interface.  The only methods
 * that need to be implemented, i.e. provide low-level content access are:
 * <ul>
 *   <li>{@link #getDirectInputStream()} to read content from the repository</li>
 * </ul>
 * 
 * @author Derek Hulley
 */
public abstract class AbstractContentReader extends AbstractContent implements ContentReader
{
    private static final Log logger = LogFactory.getLog(AbstractContentReader.class);
    
    private List<ContentStreamListener> listeners;
    private InputStream inputStream;
    private boolean streamClosed;
    
    /**
     * @param contentUrl the content URL
     */
    protected AbstractContentReader(String contentUrl)
    {
        super(contentUrl);
        
        listeners = new ArrayList<ContentStreamListener>(2);
        streamClosed = false;     // just to be explicit
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
        if (inputStream != null)
        {
            throw new RuntimeException("InputStream is already in use");
        }
        listeners.add(listener);
    }

    /**
     * A factory method for subclasses to implement that will ensure the proper
     * implementation of the {@link ContentReader#getReader()} method.
     * <p>
     * Only the instance need be constructed.  The required mimetype, encoding, etc
     * will be copied across by this class.
     *  
     * @return Returns a reader onto the location referenced by this instance.
     *      The instance must <b>always</b> be a new instance.
     * @throws ContentIOException
     */
    protected abstract ContentReader createReader() throws ContentIOException;
    
    /**
     * Performs checks and copies required reader attributes
     */
    public final ContentReader getReader() throws ContentIOException
    {
        ContentReader reader = createReader();
        if (reader == null)
        {
            throw new AlfrescoRuntimeException("ContentReader failed to create new reader: \n" +
                    "   reader: " + this);
        }
        else if (reader.getContentUrl() == null || !reader.getContentUrl().equals(getContentUrl()))
        {
            throw new AlfrescoRuntimeException("ContentReader has different URL: \n" +
                    "   reader: " + this + "\n" +
                    "   new reader: " + reader);
        }
        // copy across common attributes
        reader.setMimetype(this.getMimetype());
        reader.setEncoding(this.getEncoding());
        // done
        if (logger.isDebugEnabled())
        {
            logger.debug("Reader spawned new reader: \n" +
                    "   reader: " + this + "\n" +
                    "   new reader: " + reader);
        }
        return reader;
    }

    /**
     * Provides low-level access to read content from the repository.
     * <p>
     * This is the only of the content <i>reading</i> methods that needs to be implemented
     * by derived classes.  All other methods use the <code>InputStream</code> in
     * their implementations.
     * 
     * @return Returns a stream from which content can be read
     * @throws ContentIOException
     */
    protected abstract InputStream getDirectInputStream() throws ContentIOException;

    /**
     * Wraps the direct input stream with an output stream that provides a callback
     * when it is closed.
     * <p>
     * A check is made to ensure that the input stream is only retrieved once.
     */
    public synchronized final InputStream getContentInputStream() throws ContentIOException
    {
        // this is a use-once object
        if (inputStream != null)
        {
            throw new RuntimeException("An input stream has already been opened");
        }
        
        inputStream = getDirectInputStream();
        // wrap the stream
        InputStream callbackIs = new CallbackInputStream(inputStream);
        // it will call the close method when it gets closed
        return callbackIs;
    }
    
    /**
     * An automatically created listener sets the flag
     */
    public final boolean isClosed()
    {
        return streamClosed;
    }

    /**
     * Copies the {@link #getContentInputStream() input stream} to the given
     * <code>OutputStream</code>
     */
    public final void getContent(OutputStream os) throws ContentIOException
    {
        try
        {
            InputStream is = getContentInputStream();
            FileCopyUtils.copy(is, os);  // both streams are closed
            // done
        }
        catch (IOException e)
        {
            throw new ContentIOException("Failed to copy content to output stream: \n" +
                    "   accessor: " + this,
                    e);
        }
    }

    public final void getContent(File file) throws ContentIOException
    {
        try
        {
            InputStream is = getContentInputStream();
            FileOutputStream os = new FileOutputStream(file);
            FileCopyUtils.copy(is, os);  // both streams are closed
            // done
        }
        catch (IOException e)
        {
            throw new ContentIOException("Failed to copy content to file: \n" +
                    "   accessor: " + this + "\n" +
                    "   file: " + file,
                    e);
        }
    }

    /**
     * Makes use of the encoding, if available, to convert bytes to a string.
     * 
     * @see Content#getEncoding()
     */
    public final String getContentString() throws ContentIOException
    {
        try
        {
            // read from the stream into a byte[]
            InputStream is = getContentInputStream();
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            FileCopyUtils.copy(is, os);  // both streams are closed
            byte[] bytes = os.toByteArray();
            // get the encoding for the string
            String encoding = getEncoding();
            // create the string from the byte[] using encoding if necessary
            String content = (encoding == null) ? new String(bytes) : new String(bytes, encoding);
            // done
            return content;
        }
        catch (IOException e)
        {
            throw new ContentIOException("Failed to copy content to string: \n" +
                    "   accessor: " + this,
                    e);
        }
    }

    /**
     * Inner <code>InputStream</code> that executes the callback when the
     * input stream is closed.  It delegates to the input stream given
     * by {@link AbstractContentReader#getDirectInputStream()}.
     * <p>
     * The callback is made to {@link AbstractContentReader#listeners}.
     * 
     * @author Derek Hulley
     */
    private class CallbackInputStream extends InputStream
    {
        /*
         * Override most methods in order to go direct to the delegate's
         * implementations.
         */
        
        private InputStream delegate;
        
        public CallbackInputStream(InputStream delegate)
        {
            this.delegate = delegate;
        }
        public void close() throws IOException
        {
            delegate.close();
            // call the listeners
            for (ContentStreamListener listener : AbstractContentReader.this.listeners)
            {
                listener.contentStreamClosed();
            }
        }
        public int read() throws IOException
        {
            return delegate.read();
        }
        public int read(byte[] b, int off, int len) throws IOException
        {
            return delegate.read(b, off, len);
        }
        public int read(byte[] b) throws IOException
        {
            return delegate.read(b);
        }
    }
}
