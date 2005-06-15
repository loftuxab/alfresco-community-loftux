package org.alfresco.repo.content.filestore;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;

import org.alfresco.repo.content.AbstractContentReadWriteTest;
import org.alfresco.repo.content.CallbackFileChannel;
import org.alfresco.repo.content.ContentStore;
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.ContentWriter;

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
        file = File.createTempFile(getName(), ".txt");
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
        assertTrue("Channel not of correct callback type", writeChannel instanceof CallbackFileChannel);
        
        // put some content
        writeChannel.write(ByteBuffer.wrap("ABC".getBytes()));
        assertFalse("Writer should not be closed", writer.isClosed());
        // close
        writeChannel.close();
        assertTrue("Writer should be closed", writer.isClosed());
        
        // get the reader
        ContentReader reader = writer.getReader();
        ReadableByteChannel readChannel = reader.getReadableChannel();
        assertTrue("Channel not of correct callback type", readChannel instanceof CallbackFileChannel);
    }
}
