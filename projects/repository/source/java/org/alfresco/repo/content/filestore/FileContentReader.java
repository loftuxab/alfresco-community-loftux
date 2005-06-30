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
package org.alfresco.repo.content.filestore;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.util.List;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.repo.content.AbstractContentReader;
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
public class FileContentReader extends AbstractContentReader implements RandomAccessContent
{
    private static final Log logger = LogFactory.getLog(FileContentReader.class);
    
    private File file;
    
    /**
     * Constructor that builds a URL based on the absolute path of the file.
     * 
     * @param file the file for reading.  This will most likely be directly
     *      related to the content URL.
     */
    public FileContentReader(File file)
    {
        this(file, FileContentStore.STORE_PROTOCOL + file.getAbsolutePath());
    }
    
    /**
     * Constructor that explicitely sets the URL that the reader represents.
     * 
     * @param file the file for reading.  This will most likely be directly
     *      related to the content URL.
     * @param url the relative url that the reader represents
     */
    public FileContentReader(File file, String url)
    {
        super(url);
        
        this.file = file;
    }
    
    /**
     * @return Returns the file that this reader accesses
     */
    public File getFile()
    {
        return file;
    }

    public boolean exists()
    {
        return file.exists();
    }

    /**
     * @see File#length()
     */
    public long getLength()
    {
        if (!exists())
        {
            return 0L;
        }
        else
        {
            return file.length();
        }
    }
    
    /**
     * @see File#lastModified()
     */
    public long getLastModified()
    {
        if (!exists())
        {
            return 0L;
        }
        else
        {
            return file.lastModified();
        }
    }

    /**
     * The URL of the write is known from the start and this method contract states
     * that no consideration needs to be taken w.r.t. the stream state.
     */
    @Override
    protected ContentReader createReader() throws ContentIOException
    {
        return new FileContentReader(this.file, getContentUrl());
    }
    
    @Override
    protected ReadableByteChannel getDirectReadableChannel() throws ContentIOException
    {
        try
        {
            // the file must exist
            if (!file.exists())
            {
                throw new IOException("File does not exist");
            }
            // create the channel
            RandomAccessFile randomAccessFile = new RandomAccessFile(file, "r");  // won't create it
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
    protected ReadableByteChannel getCallbackReadableChannel(
            ReadableByteChannel directChannel,
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
     * @return Returns false as this is a reader
     */
    public boolean canWrite()
    {
        return false;   // we only allow reading
    }

    public FileChannel getChannel() throws ContentIOException
    {
        // go through the super classes to ensure that all concurrency conditions
        // and listeners are satisfied
        return (FileChannel) super.getReadableChannel();
    }
}
