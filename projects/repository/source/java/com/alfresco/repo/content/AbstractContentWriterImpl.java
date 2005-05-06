package org.alfresco.repo.content;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.springframework.util.FileCopyUtils;

/**
 * Implements all the convenience methods of the interface.
 * 
 * @author Derek Hulley
 */
public abstract class AbstractContentWriterImpl implements ContentWriter
{
    private List<ContentStreamListener> listeners;
    private OutputStream outputStream;
    
    /**
     * @param nodeService the service that will be used to update the node properties
     * @param nodeRef a node - should have the <b>content</b> aspect
     * @param contentUrl the new content URL.  The node will be updated during close.
     */
    protected AbstractContentWriterImpl()
    {
        listeners = new ArrayList<ContentStreamListener>(2);
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
        // wrap the output stream
        OutputStream callbackOs = new CallbackOutputStream(outputStream);
        // it will call the close method when it gets closed
        return callbackOs;
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
    
    public final void putContent(String content) throws ContentIOException
    {
        try
        {
            OutputStream os = getContentOutputStream();
            ByteArrayInputStream is = new ByteArrayInputStream(content.getBytes());
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
     * by {@link AbstractContentWriterImpl#getDirectOutputStream()}.
     * <p>
     * The callback is made to {@link AbstractContentWriterImpl#listeners}.
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
            for (ContentStreamListener listener : AbstractContentWriterImpl.this.listeners)
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
