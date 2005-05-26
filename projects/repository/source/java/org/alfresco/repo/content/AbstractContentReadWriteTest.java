package org.alfresco.repo.content;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import junit.framework.TestCase;

/**
 * Abstract base class that provides a set of tests for implementations
 * of the content readers and writers.
 * 
 * @see org.alfresco.repo.content.ContentReader
 * @see org.alfresco.repo.content.ContentWriter
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
}
