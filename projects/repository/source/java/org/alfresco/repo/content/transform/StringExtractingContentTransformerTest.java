package org.alfresco.repo.content.transform;

import java.io.File;
import java.io.OutputStream;

import junit.framework.TestCase;

import org.alfresco.repo.content.ContentReader;
import org.alfresco.repo.content.ContentWriter;
import org.alfresco.repo.content.filestore.FileContentWriter;
import org.alfresco.util.TempFileProvider;

/**
 * @see org.alfresco.repo.content.transform.StringExtractingContentTransformer
 * 
 * @author Derek Hulley
 */
public class StringExtractingContentTransformerTest extends TestCase
{
    private static final String SOME_CONTENT = "azAz10!£$%^&*()\t\r\n";
    
    private ContentTransformer transformer;
    /** the final destination of transformations */
    private ContentWriter targetWriter;
    
    public void setUp() throws Exception
    {
        transformer = new StringExtractingContentTransformer();
        targetWriter = new FileContentWriter(getTempFile());
        targetWriter.setMimetype("text/plain");
        targetWriter.setEncoding("UTF-8");
    }
    
    public void testSetUp() throws Exception
    {
        assertNotNull(transformer);
    }
    
    /**
     * @return Returns a new temp file
     */
    private File getTempFile()
    {
        return TempFileProvider.createTempFile(getName(), ".txt");
    }
    
    /**
     * Writes some content using the mimetype and encoding specified.
     * 
     * @param mimetype
     * @param encoding
     * @return Returns a reader onto the newly written content
     */
    private ContentReader writeContent(String mimetype, String encoding)
    {
        ContentWriter writer = new FileContentWriter(getTempFile());
        writer.setMimetype(mimetype);
        writer.setEncoding(encoding);
        // put content
        writer.putContent(SOME_CONTENT);
        // return a reader onto the new content
        return writer.getReader();
    }
    
    public void testDirectTransform() throws Exception
    {
        ContentReader reader = writeContent("text/plain", "latin1");
        
        // check reliability
        double reliability = transformer.getReliability(reader.getMimetype(), targetWriter.getMimetype());
        assertEquals("Reliability incorrect", 1.0, reliability);
        
        // transform
        transformer.transform(reader, targetWriter);
        
        // get a reader onto the transformed content and check
        ContentReader checkReader = targetWriter.getReader();
        String checkContent = checkReader.getContentString();
        assertEquals("Content check failed", SOME_CONTENT, checkContent);
    }
    
    public void testInterTextTransform() throws Exception
    {
        ContentReader reader = writeContent("text/xml", "UTF-16");
        
        // check reliability
        double reliability = transformer.getReliability(reader.getMimetype(), targetWriter.getMimetype());
        assertEquals("Reliability incorrect", 1.0, reliability);
        
        // transform
        transformer.transform(reader, targetWriter);
        
        // get a reader onto the transformed content and check
        ContentReader checkReader = targetWriter.getReader();
        String checkContent = checkReader.getContentString();
        assertEquals("Content check failed", SOME_CONTENT, checkContent);
    }
    
    public void testBindaryToTextConversion() throws Exception
    {
        ContentWriter sourceWriter = new FileContentWriter(getTempFile());
        sourceWriter.setMimetype("image/tiff");
        sourceWriter.setEncoding(null);
        // put content directly to stream, but keep a string as a check
        byte[] bytes = new byte[] {1, 34, 120, 97, 19, -45, -68, 23};
        String content = new String(bytes, targetWriter.getEncoding());   // encoding must be correct
        OutputStream os = sourceWriter.getContentOutputStream();
        os.write(bytes);
        os.close();

        // get the reader
        ContentReader reader = sourceWriter.getReader();
        // check reliability
        double reliability = transformer.getReliability(reader.getMimetype(), targetWriter.getMimetype());
        assertEquals("Reliability incorrect", 0.1, reliability);
        
        // transform
        transformer.transform(reader, targetWriter);
        
        // get a reader onto the transformed content and check
        ContentReader checkReader = targetWriter.getReader();
        String checkContent = checkReader.getContentString();
        assertEquals("Content check failed", content, checkContent);
    }
}
