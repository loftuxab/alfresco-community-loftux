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
package org.alfresco.repo.content.filestore;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;

import org.alfresco.repo.content.AbstractContentReadWriteTest;
import org.alfresco.repo.content.ContentStore;
import org.alfresco.repo.content.RandomAccessContent;
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.ContentWriter;
import org.alfresco.util.TempFileProvider;

/**
 * Tests that the file reader and writer work
 * 
 * @see org.alfresco.repo.content.filestore.FileContentReader
 * @see org.alfresco.repo.content.filestore.FileContentWriter
 * 
 * @author Derek Hulley
 */
public class FileContentReadWriteTest extends AbstractContentReadWriteTest
{
    private File file;
    
    public FileContentReadWriteTest()
    {
        super();
    }

    @Override
    public void setUp() throws Exception
    {
        super.setUp();
        file = TempFileProvider.createTempFile(getName(), ".txt");
        file.deleteOnExit();
    }

    @Override
    protected ContentStore getStore()
    {
        return null;
    }
    
    @Override
    protected ContentReader getReader()
    {
        return new FileContentReader(file);
    }

    @Override
    protected ContentWriter getWriter()
    {
        return new FileContentWriter(file);
    }
    
    /**
     * Checks that the channel is a wrapper for file channel
     */
    public void testCallbackClass() throws Exception
    {
        ContentWriter writer = getWriter();
        WritableByteChannel writeChannel = writer.getWritableChannel();
        assertTrue("Channel not of correct callback type", writeChannel instanceof FileChannel);
        
        // put some content
        writeChannel.write(ByteBuffer.wrap("ABC".getBytes()));
        assertFalse("Writer should not be closed", writer.isClosed());
        // close
        writeChannel.close();
        assertTrue("Writer should be closed", writer.isClosed());
        
        // get the reader
        ContentReader reader = writer.getReader();
        ReadableByteChannel readChannel = reader.getReadableChannel();
        assertTrue("Channel not of correct callback type", readChannel instanceof FileChannel);
    }
    
    /**
     * Ensures that the random writing of content takes pre-existing content
     * into account, i.e. that the pre-existing content is copied.
     */
    public void testRandomAccessWriteCopy() throws Exception
    {
        String content = "ABC";
        byte[] bytes = content.getBytes();
        
        ContentWriter seedWriter = getWriter();
        seedWriter.putContent(content);
        // create a new writer that uses the seed writer
        File file = TempFileProvider.createTempFile(getName(), ".txt");
        ContentWriter writer = new FileContentWriter(file, seedWriter.getReader());
        // go the random access route
        RandomAccessContent randAccessContent = (RandomAccessContent) writer;
        FileChannel channel = randAccessContent.getChannel();
        
        // check that the previous contents are present in the file
        ByteBuffer buffer = ByteBuffer.allocate(bytes.length);
        int read = channel.read(buffer);
        assertEquals("Not enough bytes read", bytes.length, read);
        byte[] checkBytes = new byte[bytes.length];
        buffer.rewind();
        buffer.get(checkBytes);
        String checkContent = new String(checkBytes);
        assertEquals("Content is not exactly the same", content, checkContent);
        
        // close channel
        channel.close();
    }
}
