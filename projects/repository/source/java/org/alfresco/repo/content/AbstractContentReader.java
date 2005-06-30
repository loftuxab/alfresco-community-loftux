/*
 * Copyright (C) 2005 Alfresco, Inc.
 *
 * Licensed under the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version
 * 2.1 of the License, or (at your option) any later version.
 * You may obtain a copy of the License at
 *
 *     http://www.gnu.org/licenses/lgpl.txt
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the
 * License.
 */
package org.alfresco.repo.content;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;
import java.util.List;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.service.cmr.repository.Content;
import org.alfresco.service.cmr.repository.ContentIOException;
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.ContentStreamListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.FileCopyUtils;

/**
 * Implements all the convenience methods of the interface.  The only methods
 * that need to be implemented, i.e. provide low-level content access are:
 * <ul>
 *   <li>{@link #getDirectReadableChannel()} to read content from the repository</li>
 * </ul>
 * 
 * @author Derek Hulley
 */
public abstract class AbstractContentReader extends AbstractContent implements ContentReader
{
    private static final Log logger = LogFactory.getLog(AbstractContentReader.class);
    
    private List<ContentStreamListener> listeners;
    private ReadableByteChannel channel;
    
    /**
     * @param contentUrl the content URL - this should be relative to the root of the store
     *      and not absolute: to enable moving of the stores
     */
    protected AbstractContentReader(String contentUrl)
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
     * Provides low-level access to read content from the repository.
     * <p>
     * This is the only of the content <i>reading</i> methods that needs to be implemented
     * by derived classes.  All other content access methods make use of this in their
     * underlying implementations.
     * 
     * @return Returns a channel from which content can be read
     * @throws ContentIOException if the channel could not be opened or the underlying content
     *      has disappeared
     */
    protected abstract ReadableByteChannel getDirectReadableChannel() throws ContentIOException;

    /**
     * Optionally override to supply an alternate callback channel.
     *  
     * @param directChannel the result of {@link #getDirectReadableChannel()}
     * @param listeners the listeners to call
     * @return Returns a channel
     * @throws ContentIOException
     * 
     * @see AbstractContentReader.CallbackChannel
     */
    protected ReadableByteChannel getCallbackReadableChannel(
            ReadableByteChannel directChannel,
            List<ContentStreamListener> listeners)
            throws ContentIOException
    {
        // wrap it
        ReadableByteChannel callbackChannel = new CallbackChannel(directChannel, listeners);
        // done
        return callbackChannel;
    }

    /**
     * @see #getDirectReadableChannel()
     * @see #getCallbackReadableChannel()
     */
    public synchronized final ReadableByteChannel getReadableChannel() throws ContentIOException
    {
        // this is a use-once object
        if (channel != null)
        {
            throw new RuntimeException("A channel has already been opened");
        }
        ReadableByteChannel directChannel = getDirectReadableChannel();
        channel = getCallbackReadableChannel(directChannel, listeners);
        // done
        if (logger.isDebugEnabled())
        {
            logger.debug("Opened channel onto content: " + this);
        }
        return channel;
    }
    
    /**
     * @see Channels#newInputStream(java.nio.channels.ReadableByteChannel)
     */
    public InputStream getContentInputStream() throws ContentIOException
    {
        try
        {
            ReadableByteChannel channel = getReadableChannel();
            InputStream is = Channels.newInputStream(channel);
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
     * Provides callbacks to the {@link ContentStreamListener listeners}.
     * 
     * @author Derek Hulley
     */
    private static class CallbackChannel implements ReadableByteChannel
    {
        /*
         * Override most methods in order to go direct to the delegate's
         * implementations.
         */
        
        private ReadableByteChannel delegate;
        private List<ContentStreamListener> listeners;
        
        public CallbackChannel(ReadableByteChannel delegate, List<ContentStreamListener> listeners)
        {
            this.delegate = delegate;
            this.listeners = listeners;
        }
        
        public boolean isOpen()
        {
            return delegate.isOpen();
        }
        public int read(ByteBuffer dst) throws IOException
        {
            return delegate.read(dst);
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
