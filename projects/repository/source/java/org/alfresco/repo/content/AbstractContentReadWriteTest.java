package org.alfresco.repo.content;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.util.List;

import org.alfresco.service.cmr.repository.ContentIOException;
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.ContentWriter;

import junit.framework.TestCase;

/**
 * Abstract base class that provides a set of tests for implementations
 * of the content readers and writers.
 * 
 * @see org.alfresco.service.cmr.repository.ContentReader
 * @see org.alfresco.service.cmr.repository.ContentWriter
 * 
 * @author Derek Hulley
 */
public abstract class AbstractContentReadWriteTest extends TestCase
{
    public AbstractContentReadWriteTest()
    {
        super();
    }

    @Override
    public void setUp() throws Exception
    {
    }
    
    /**
     * Fetch the store to be used during a test.  This method is invoked once per test - it is
     * therefore safe to use <code>setUp</code> to initialise resources.
     * 
     * @return Returns the store that creates the readers and writers.  The result may be null,
     *      in which case the tests that work directly against the store will be bypassed.
     * 
     * @see #getReader()()
     * @see #getWriter()
     */
    protected abstract ContentStore getStore();
    
    /**
     * Fetch a reader to be used during a test.  This method is invoked once per test - it is
     * therefore safe to use <code>setUp</code> to initialise resources.
     * 
     * @return Returns a reader to a common store item, e.g. a file.  This must be to the same
     *      resource that {@link #getWriter()} references.
     * 
     * @see #getWriter()
     */
    protected abstract ContentReader getReader();
    
    /**
     * Fetch a writer to be used during a test.  This method is invoked once per test - it is
     * therefore safe to use <code>setUp</code> to initialise resources.
     * 
     * @param the writeCloseListener that must be called when the output stream is closed
     * @return Returns a writer to a common store item, e.g. a file.  This must be to the same
     *      resource that {@link #getReader()()} references.
     * 
     * @see #getReader()()
     */
    protected abstract ContentWriter getWriter();
    
    public void testSetUp() throws Exception
    {
        assertNotNull("Reader not set", getReader());
        assertNotNull("Writer not set", getWriter());
    }
    
    public void testContentUrl() throws Exception
    {
        ContentReader reader = getReader();
        ContentWriter writer = getWriter();
        
        // the contract is that both the reader and writer must refer to the same
        // content -> the URL must be the same
        String readerContentUrl = reader.getContentUrl();
        String writerContentUrl = writer.getContentUrl();
        assertNotNull("Reader url is invalid", readerContentUrl);
        assertNotNull("Writer url is invalid", writerContentUrl);
        assertEquals("Reader and writer must reference same content",
                readerContentUrl,
                writerContentUrl);
    }
    
    public void testGetReader() throws Exception
    {
        ContentWriter writer = getWriter();
        
        // check that no reader is available from the writer just yet
        ContentReader nullReader = writer.getReader();
        assertNull("No reader expected", nullReader);
        
        // write some content
        writer.setMimetype("text/plain");
        writer.setEncoding("UTF-8");
        writer.putContent("ABC");
        
        // get a reader from the writer
        ContentReader readerFromWriter = writer.getReader();
        assertEquals("URL incorrect", writer.getContentUrl(), readerFromWriter.getContentUrl());
        assertEquals("Mimetype incorrect", writer.getMimetype(), readerFromWriter.getMimetype());
        assertEquals("Encoding incorrect", writer.getEncoding(), readerFromWriter.getEncoding());
        
        // get another reader from the reader
        ContentReader readerFromReader = readerFromWriter.getReader();
        assertEquals("URL incorrect", writer.getContentUrl(), readerFromReader.getContentUrl());
        assertEquals("Mimetype incorrect", writer.getMimetype(), readerFromReader.getMimetype());
        assertEquals("Encoding incorrect", writer.getEncoding(), readerFromReader.getEncoding());
    }
	
    /**
     * The simplest test.  Write a string and read it again, checking that we receive the same values.
     * If the resource accessed by {@link #getReader()} and {@link #getWriter()} is not the same, then
     * values written and read won't be the same.
     */
    public void testWriteAndReadString() throws Exception
    {
        ContentReader reader = getReader();
        ContentWriter writer = getWriter();
        
        String content = "ABC";
        writer.putContent(content);
        assertTrue("Stream close not detected", writer.isClosed());

        String check = reader.getContentString();
        assertTrue("Read and write may not share same resource", check.length() > 0);
        assertEquals("Write and read didn't work", content, check);
    }
    
    public void testClosedState() throws Exception
    {
        ContentReader reader = getReader();
        ContentWriter writer = getWriter();
        
        // check that streams are not flagged as closed
        assertFalse("Reader stream should not be closed", reader.isClosed());
        assertFalse("Writer stream should not be closed", writer.isClosed());
        
        // check that the write doesn't supply a reader
        ContentReader writerGivenReader = writer.getReader();
        assertNull("No reader should be available before a write has finished", writerGivenReader);
        
        // write some stuff
        writer.putContent("ABC");
        // check that the write has been closed
        assertTrue("Writer stream should be closed", writer.isClosed());
        
        // check that we can get a reader from the writer
        writerGivenReader = writer.getReader();
        assertNotNull("No reader given by closed writer", writerGivenReader);
        assertFalse("Readers should still be closed", reader.isClosed());
        assertFalse("Readers should still be closed", writerGivenReader.isClosed());
        
        // check that the instance is new each time
        ContentReader newReaderA = writer.getReader();
        ContentReader newReaderB = writer.getReader();
        assertFalse("Reader must always be a new instance", newReaderA == newReaderB);
        
        // check that the readers refer to the same URL
        assertEquals("Readers should refer to same URL",
                reader.getContentUrl(), writerGivenReader.getContentUrl());
        
        // read their content
        String contentCheck = reader.getContentString();
        assertEquals("Incorrect content", "ABC", contentCheck);
        contentCheck = writerGivenReader.getContentString();
        assertEquals("Incorrect content", "ABC", contentCheck);
        
        // check closed state of readers
        assertTrue("Reader should be closed", reader.isClosed());
        assertTrue("Reader should be closed", writerGivenReader.isClosed());
    }
    
    public void testMimetypeAndEncoding() throws Exception
    {
        ContentWriter writer = getWriter();
        // set mimetype and encoding
        writer.setMimetype("text/plain");
        writer.setEncoding("UTF-16");
        
        // create a UTF-16 string
        String content = "A little bit o' this and a little bit o' that";
        byte[] bytesUtf16 = content.getBytes("UTF-16");
        // write the bytes directly to the writer
        OutputStream os = writer.getContentOutputStream();
        os.write(bytesUtf16);
        os.close();
        
        // now get a reader from the writer
        ContentReader reader = writer.getReader();
        assertEquals("Writer -> Reader content URL mismatch", writer.getContentUrl(), reader.getContentUrl());
        assertEquals("Writer -> Reader mimetype mismatch", writer.getMimetype(), reader.getMimetype());
        assertEquals("Writer -> Reader encoding mismatch", writer.getEncoding(), reader.getEncoding());
        
        // now get the string directly from the reader
        String contentCheck = reader.getContentString();     // internally it should have taken care of the encoding
        assertEquals("Encoding and decoding of strings failed", content, contentCheck);
    }
    
    public void testReadAndWriteFile() throws Exception
    {
        ContentReader reader = getReader();
        ContentWriter writer = getWriter();
        
        File sourceFile = File.createTempFile(getName(), ".txt");
        sourceFile.deleteOnExit();
        // dump some content into the temp file
        String content = "ABC";
        FileOutputStream os = new FileOutputStream(sourceFile);
        os.write(content.getBytes());
        os.flush();
        os.close();
        
        // put our temp file's content
        writer.putContent(sourceFile);
        assertTrue("Stream close not detected", writer.isClosed());
        
        // create a sink temp file
        File sinkFile = File.createTempFile(getName(), ".txt");
        sinkFile.deleteOnExit();
        
        // get the content into our temp file
        reader.getContent(sinkFile);
        
        // read the sink file manually
        FileInputStream is = new FileInputStream(sinkFile);
        byte[] buffer = new byte[100];
        int count = is.read(buffer);
        assertEquals("No content read", 3, count);
        is.close();
        String check = new String(buffer, 0, count);
        
        assertEquals("Write out of and read into files failed", content, check);
    }
    
    public void testReadAndWriteStreamByPull() throws Exception
    {
        ContentReader reader = getReader();
        ContentWriter writer = getWriter();

        String content = "ABC";
        // put the content using a stream
        InputStream is = new ByteArrayInputStream(content.getBytes());
        writer.putContent(is);
        assertTrue("Stream close not detected", writer.isClosed());
        
        // get the content using a stream
        ByteArrayOutputStream os = new ByteArrayOutputStream(100);
        reader.getContent(os);
        byte[] bytes = os.toByteArray();
        String check = new String(bytes);
        
        assertEquals("Write out and read in using streams failed", content, check);
    }
    
    public void testReadAndWriteStreamByPush() throws Exception
    {
        ContentReader reader = getReader();
        ContentWriter writer = getWriter();

        String content = "ABC";
        // get the content output stream
        OutputStream os = writer.getContentOutputStream();
        os.write(content.getBytes());
        assertFalse("Stream has not been closed", writer.isClosed());
        // close the stream and check again
        os.close();
        assertTrue("Stream close not detected", writer.isClosed());
        
        // pull the content from a stream
        InputStream is = reader.getContentInputStream();
        byte[] buffer = new byte[100];
        int count = is.read(buffer);
        assertEquals("No content read", 3, count);
        is.close();
        String check = new String(buffer, 0, count);
        
        assertEquals("Write out of and read into files failed", content, check);
    }
    
    /**
     * Tests deletion of content.
     * <p>
     * Only applies when {@link #getStore()} returns a value.
     */
    public void testDelete() throws Exception
    {
        ContentStore store = getStore();
        if (store == null)
        {
            // allowed - in which case the test is useless
            return;
        }
        ContentWriter writer = getWriter();
        
        String content = "ABC";
        String contentUrl = writer.getContentUrl();

        // write some bytes, but don't close the stream
        OutputStream os = writer.getContentOutputStream();
        os.write(content.getBytes());
        os.flush();                  // make sure that the bytes get persisted
        
        // with the stream open, attempt to delete the content
        boolean deleted = store.delete(contentUrl);
        assertFalse("No exception thrown when attempting to delete content with open write stream", deleted);
        
        // close the stream
        os.close();
        
        // get a reader
        ContentReader reader = store.getReader(contentUrl);
        assertNotNull(reader);
        ContentReader readerCheck = writer.getReader();
        assertNotNull(readerCheck);
        assertEquals("Store and write provided readers onto different URLs",
                writer.getContentUrl(), reader.getContentUrl());
        
        // open the stream onto the content
        InputStream is = reader.getContentInputStream();
        
        // attempt to delete the content
        deleted = store.delete(contentUrl);
        assertFalse("Content deletion failed to detect active reader", deleted);

        // close the reader stream
        is.close();
        
        // get a fresh reader
        reader = store.getReader(contentUrl);
        assertNotNull(reader);
        assertTrue("Content should exist", reader.exists());
        // delete the content
        store.delete(contentUrl);
        
        // attempt to read from the reader
        try
        {
            is = reader.getContentInputStream();
            fail("Reader failed to detect underlying content deletion");
        }
        catch (ContentIOException e)
        {
            // expected
        }
        
        // get another fresh reader
        reader = store.getReader(contentUrl);
        assertNotNull("Reader must be returned even when underlying content is missing",
                reader);
        assertFalse("Content should not exist", reader.exists());
        try
        {
            is = reader.getContentInputStream();
            fail("Reader opened stream onto missing content");
        }
        catch (ContentIOException e)
        {
            // expected
        }
    }
    
    /**
     * Tests retrieval of all content URLs
     * <p>
     * Only applies when {@link #getStore()} returns a value.
     */
    public void testListUrls() throws Exception
    {
        ContentStore store = getStore();
        if (store == null)
        {
            return;         // test is meaningless
        }
        ContentWriter writer = getWriter();
        
        List<String> contentUrls = store.listUrls();
        String contentUrl = writer.getContentUrl();
        assertFalse("Writer URL should be unique", contentUrls.contains(contentUrl));
        
        // write some data
        writer.putContent("The quick brown fox...");
        
        contentUrls = store.listUrls();
        assertTrue("Newly written content does not appear as a URL", contentUrls.contains(contentUrl));
    }
    
    /**
     * Tests random access writing
     * <p>
     * Only executes if the writer implements {@link RandomAccessContent}.
     */
    public void testRandomAccessWrite() throws Exception
    {
        ContentWriter writer = getWriter();
        if (!(writer instanceof RandomAccessContent))
        {
            // not much to do here
            return;
        }
        RandomAccessContent randomWriter = (RandomAccessContent) writer;
        // check that we are allowed to write
        assertTrue("Expected random access writing", randomWriter.canWrite());
        
        FileChannel fileChannel = randomWriter.getChannel();
        assertNotNull("No channel given", fileChannel);
        
        // check that no other content access is allowed
        try
        {
            writer.getWritableChannel();
            fail("Second channel access allowed");
        }
        catch (RuntimeException e)
        {
            // expected
        }
        
        // write some content in a random fashion (reverse order)
        byte[] content = new byte[] {1, 2, 3};
        for (int i = content.length - 1; i >= 0; i--)
        {
            ByteBuffer buffer = ByteBuffer.wrap(content, i, 1);
            fileChannel.write(buffer, i);
        }
        
        // close the channel
        fileChannel.close();
        assertTrue("Writer not closed", writer.isClosed());
        
        // check the content
        ContentReader reader = writer.getReader();
        ReadableByteChannel channelReader = reader.getReadableChannel();
        ByteBuffer buffer = ByteBuffer.allocateDirect(3);
        int count = channelReader.read(buffer);
        assertEquals("Incorrect number of bytes read", 3, count);
        for (int i = 0; i < content.length; i++)
        {
            assertEquals("Content doesn't match", content[i], buffer.get(i));
        }
    }
    
    /**
     * Tests random access reading
     * <p>
     * Only executes if the reader implements {@link RandomAccessContent}.
     */
    public void testRandomAccessRead() throws Exception
    {
        ContentWriter writer = getWriter();
        // put some content
        String content = "ABC";
        byte[] bytes = content.getBytes();
        writer.putContent(content);
        ContentReader reader = writer.getReader();
        if (!(reader instanceof RandomAccessContent))
        {
            // not much to do here
            return;
        }
        RandomAccessContent randomReader = (RandomAccessContent) reader;
        // check that we are NOT allowed to write
        assertFalse("Expected read-only random access", randomReader.canWrite());
        
        FileChannel fileChannel = randomReader.getChannel();
        assertNotNull("No channel given", fileChannel);
        
        // check that no other content access is allowed
        try
        {
            reader.getReadableChannel();
            fail("Second channel access allowed");
        }
        catch (RuntimeException e)
        {
            // expected
        }
        
        // read the content
        ByteBuffer buffer = ByteBuffer.allocate(bytes.length);
        int count = fileChannel.read(buffer);
        assertEquals("Incorrect number of bytes read", bytes.length, count);
        // transfer back to array
        buffer.rewind();
        buffer.get(bytes);
        String checkContent = new String(bytes);
        assertEquals("Content read failure", content, checkContent);
    }
}
