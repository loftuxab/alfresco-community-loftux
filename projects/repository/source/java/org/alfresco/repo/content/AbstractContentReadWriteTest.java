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
    private ContentStreamListener listener;
    boolean isClosed;
    
    public AbstractContentReadWriteTest()
    {
        super();
    }

    @Override
    public void setUp() throws Exception
    {
        listener = new DummyListener();
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
     * @param the listener that must be called when the output stream is closed
     * @return Returns a writer to a common store item, e.g. a file.  This must be to the same
     *      resource that {@link #getReader()()} references.
     * 
     * @see #getReader()()
     */
    protected abstract ContentWriter getWriter();
    
    public void testSetUp() throws Exception
    {
        assertNotNull("Listener not created", listener);
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
    
    /**
     * The simplest test.  Write a string and read it again, checking that we receive the same values.
     * If the resource accessed by {@link #getReader()} and {@link #getWriter()} is not the same, then
     * values written and read won't be the same.
     */
    public void testWriteAndReadString() throws Exception
    {
        ContentReader reader = getReader();
        ContentWriter writer = getWriter();
        writer.addListener(listener);
        
        String content = "ABC";
        writer.putContent(content);
        assertTrue("Stream close not detected", isClosed);

        String check = reader.getContentString();
        assertTrue("Read and write may not share same resource", check.length() > 0);
        assertEquals("Write and read didn't work", content, check);
    }
    
    public void testReadAndWriteFile() throws Exception
    {
        ContentReader reader = getReader();
        ContentWriter writer = getWriter();
        writer.addListener(listener);
        
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
        assertTrue("Stream close not detected", isClosed);
        
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
        writer.addListener(listener);

        String content = "ABC";
        // put the content using a stream
        InputStream is = new ByteArrayInputStream(content.getBytes());
        writer.putContent(is);
        assertTrue("Stream close not detected", isClosed);
        
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
        writer.addListener(listener);

        String content = "ABC";
        // get the content output stream
        OutputStream os = writer.getContentOutputStream();
        os.write(content.getBytes());
        assertFalse("Stream has not been closed", isClosed);
        // close the stream and check again
        os.close();
        assertTrue("Stream close not detected", isClosed);
        
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
     * Dummy class to receive stream close notifications
     */
    private class DummyListener implements ContentStreamListener
    {
        public void contentStreamClosed() throws ContentIOException
        {
            isClosed = true;
        }
    }
}
