/*
 * Copyright (C) 2005 Alfresco, Inc.
 *
 * Licensed under the Mozilla Public License version 1.1 
 * with a permitted attribution clause. You may obtain a
 * copy of the License at
 *
 *   http://www.alfresco.org/legal/license.txt
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the
 * License.
 */
package org.alfresco.repo.content.memorystore;

import java.nio.channels.ReadableByteChannel;

import org.alfresco.repo.content.AbstractContentReader;
import org.alfresco.service.cmr.repository.ContentIOException;
import org.alfresco.service.cmr.repository.ContentReader;

/**
 * A reader onto <tt>byte[]</tt> instances in memory.
 * <p>
 * There is no permament persistence of the content and this class should
 * therefore only be used for the most transient of content.
 * 
 * @author Derek Hulley
 */
public class MemoryContentReader extends AbstractContentReader
{
    /*
     * TODO: Wrap the content in an object containing modification time, etc 
     */
    
    private byte[] content;
    
    /**
     * @param contentUrl the URL by which the content can be accessed
     * @param content the physical bytes making up the the content
     */
    public MemoryContentReader(String contentUrl, byte[] content)
    {
        super(contentUrl);
        if (content == null)
        {
            throw new IllegalArgumentException("byte[] may not be null");
        }
        this.content = content;
    }

    @Override
    protected ContentReader createReader() throws ContentIOException
    {
        return new MemoryContentReader(this.getContentUrl(), this.content);
    }
 
    /**
     * @return Return true always as memory is always available
     */
    public boolean exists()
    {
        // the content always exists
        return true;
    }
    
    /**
     * @return Returns the number of bytes held in the in-memory buffer
     */
    public long getLength()
    {
        return content.length;
    }

    public long getLastModified()
    {
        throw new UnsupportedOperationException();
    }

    protected ReadableByteChannel getDirectReadableChannel() throws ContentIOException
    {
        throw new UnsupportedOperationException();
    }
}
