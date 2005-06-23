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

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.util.List;

import org.alfresco.service.cmr.repository.ContentStreamListener;

/**
 * Wraps a <code>FileChannel</code> to provide callbacks to listeners when the
 * channel is {@link java.nio.channels.Channel#close() closed}.
 * 
 * @author Derek Hulley
 */
public class CallbackFileChannel extends FileChannel
{
    /** the channel to route all calls to */
    private FileChannel delegate;
    /** listeners waiting for the stream close */
    private List<ContentStreamListener> listeners;

    /**
     * @param delegate the channel that will perform the work
     * @param listeners listeners for events coming from this channel
     */
    public CallbackFileChannel(FileChannel delegate, List<ContentStreamListener> listeners)
    {
        if (delegate == null)
        {
            throw new IllegalArgumentException("FileChannel delegate is required");
        }
        if (delegate instanceof CallbackFileChannel)
        {
            throw new IllegalArgumentException("FileChannel delegate may not be a CallbackFileChannel");
        }
        
        this.delegate = delegate;
        this.listeners = listeners;
    }
    
    /**
     * Closes the channel and makes the callbacks to the listeners
     */
    @Override
    protected void implCloseChannel() throws IOException
    {
        delegate.close();
        for (ContentStreamListener listener : listeners)
        {
            listener.contentStreamClosed();
        }
    }

    @Override
    public void force(boolean metaData) throws IOException
    {
        delegate.force(metaData);
    }

    @Override
    public FileLock lock(long position, long size, boolean shared) throws IOException
    {
        return delegate.lock(position, size, shared);
    }

    @Override
    public MappedByteBuffer map(MapMode mode, long position, long size) throws IOException
    {
        return delegate.map(mode, position, size);
    }

    @Override
    public long position() throws IOException
    {
        return delegate.position();
    }

    @Override
    public FileChannel position(long newPosition) throws IOException
    {
        return delegate.position(newPosition);
    }

    @Override
    public int read(ByteBuffer dst) throws IOException
    {
        return delegate.read(dst);
    }

    @Override
    public int read(ByteBuffer dst, long position) throws IOException
    {
        return delegate.read(dst, position);
    }

    @Override
    public long read(ByteBuffer[] dsts, int offset, int length) throws IOException
    {
        return delegate.read(dsts, offset, length);
    }

    @Override
    public long size() throws IOException
    {
        return delegate.size();
    }

    @Override
    public long transferFrom(ReadableByteChannel src, long position, long count) throws IOException
    {
        return delegate.transferFrom(src, position, count);
    }

    @Override
    public long transferTo(long position, long count, WritableByteChannel target) throws IOException
    {
        return delegate.transferTo(position, count, target);
    }

    @Override
    public FileChannel truncate(long size) throws IOException
    {
        return delegate.truncate(size);
    }

    @Override
    public FileLock tryLock(long position, long size, boolean shared) throws IOException
    {
        return delegate.tryLock(position, size, shared);
    }

    @Override
    public int write(ByteBuffer src) throws IOException
    {
        return delegate.write(src);
    }

    @Override
    public int write(ByteBuffer src, long position) throws IOException
    {
        return delegate.write(src, position);
    }

    @Override
    public long write(ByteBuffer[] srcs, int offset, int length) throws IOException
    {
        return delegate.write(srcs, offset, length);
    }
}
