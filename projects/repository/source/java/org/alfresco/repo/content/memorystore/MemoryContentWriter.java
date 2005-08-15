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
package org.alfresco.repo.content.memorystore;

import java.nio.channels.WritableByteChannel;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.repo.content.AbstractContentWriter;
import org.alfresco.service.cmr.repository.ContentIOException;
import org.alfresco.service.cmr.repository.ContentReader;

/**
 * A writer onto a <tt>byte[]</tt> instance in memory.
 * <p>
 * There is no permament persistence of the content and this class should
 * therefore only be used for the most transient of content.
 * 
 * @author Derek Hulley
 */
public class MemoryContentWriter extends AbstractContentWriter
{
    private byte[] content;
    
    /**
     * @param contentUrl the URL by which the content can be accessed
     */
    public MemoryContentWriter(String contentUrl, ContentReader existingContentReader)
    {
        super(contentUrl, existingContentReader);
    }
    
    /**
     * Get the physical content.  This operation can only be used once the content
     * writing stream has been closed.
     * 
     * @return Returns the memory-resident content
     */
    public byte[] getContent()
    {
        if (!isClosed())
        {
            throw new AlfrescoRuntimeException("The content stream has not been closed");
        }
        return content;
    }

    /**
     * Abstract base class takes care of all considerations other than construction
     */
    @Override
    protected ContentReader createReader() throws ContentIOException
    {
        throw new UnsupportedOperationException();
    }

    protected WritableByteChannel getDirectWritableChannel() throws ContentIOException
    {
        throw new UnsupportedOperationException();
    }
}
