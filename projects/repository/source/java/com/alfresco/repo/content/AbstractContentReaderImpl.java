package org.alfresco.repo.content;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

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
public abstract class AbstractContentReaderImpl implements ContentReader
{
    private List<ContentStreamListener> listeners;
    private InputStream inputStream;
    
    /**
     * @param nodeService the service that will be used to update the node properties
     * @param nodeRef a node - should have the <b>content</b> aspect
     * @param contentUrl the new content URL.  The node will be updated during close.
     */
    protected AbstractContentReaderImpl()
    {
        listeners = new ArrayList<ContentStreamListener>(2);
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

    public final String getContentString() throws ContentIOException
    {
        try
        {
            InputStream is = getContentInputStream();
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            FileCopyUtils.copy(is, os);  // both streams are closed
            // create the string from the byte[]
            String content = new String(os.toByteArray());
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
     * by {@link AbstractContentReaderImpl#getDirectInputStream()}.
     * <p>
     * The callback is made to {@link AbstractContentReaderImpl#listeners}.
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
            for (ContentStreamListener listener : AbstractContentReaderImpl.this.listeners)
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
