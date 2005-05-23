package org.alfresco.repo.content.transform;

import java.io.File;

import junit.framework.TestCase;

import org.alfresco.repo.content.ContentReader;
import org.alfresco.repo.content.ContentWriter;
import org.alfresco.repo.content.filestore.FileContentWriter;
import org.alfresco.util.TempFileProvider;

/**
 * @see org.alfresco.repo.content.transform.UnoContentTransformer
 * 
 * @author Derek Hulley
 */
public class UnoContentTransformerTest extends TestCase
{
    private static String SOME_CONTENT = "ABCDEFG";
    
    private static String MIMETYPE_WORD = "application/msword";
    private static String MIMETYPE_TEXT = "text/plain";
    private static String MIMETYPE_RUBBISH = "text/rubbish";

    private UnoContentTransformer transformer;
    
    public void setUp() throws Exception
    {
        transformer = new UnoContentTransformer();
    }
    
    public void testReliability() throws Exception
    {
        if (!transformer.isConnected())
        {
            // no connection
            return;
        }
        double reliability = 0.0;
        reliability = transformer.getReliability(MIMETYPE_RUBBISH, MIMETYPE_TEXT);
        assertEquals("Mimetype should not be supported", 0.0, reliability);
        reliability = transformer.getReliability(MIMETYPE_TEXT, MIMETYPE_RUBBISH);
        assertEquals("Mimetype should not be supported", 0.0, reliability);
        reliability = transformer.getReliability(MIMETYPE_TEXT, MIMETYPE_WORD);
        assertEquals("Mimetype should be supported", 1.0, reliability);
        reliability = transformer.getReliability(MIMETYPE_WORD, MIMETYPE_TEXT);
        assertEquals("Mimetype should be supported", 1.0, reliability);
    }
    
    public void testTextToWordConversionAndBack() throws Exception
    {
        if (!transformer.isConnected())
        {
            // no connection
            return;
        }
        File sourceFile = TempFileProvider.createTempFile(getName(), ".txt");
        ContentWriter sourceWriter = new FileContentWriter(sourceFile);
        sourceWriter.setMimetype(MIMETYPE_TEXT);
        sourceWriter.putContent(SOME_CONTENT);
        // get a reader for it
        ContentReader sourceReader = sourceWriter.getReader();
        sourceWriter = null;
        // create a writer for the output
        File targetFile = TempFileProvider.createTempFile(getName(), ".doc");
        ContentWriter targetWriter = new FileContentWriter(targetFile);
        targetWriter.setMimetype(MIMETYPE_WORD);
        
        // get the reliability
        double reliability = transformer.getReliability(
                sourceReader.getMimetype(),
                targetWriter.getMimetype());
        assertEquals("Text to Word should be supported", 1.0, reliability);
        
        // perform the conversion
        transformer.transform(sourceReader, targetWriter);
        
        // get a reader onto the target writer
        ContentReader targetReader = targetWriter.getReader();
        // check the bytes
        String content = targetReader.getContentString();
        assertNotSame(SOME_CONTENT, content);
        
        // convert back
        sourceReader = targetReader.getReader();
        targetWriter = new FileContentWriter(TempFileProvider.createTempFile(getName(), ".txt"));
        targetWriter.setMimetype(MIMETYPE_TEXT);
        transformer.transform(sourceReader, targetWriter);
        targetReader = targetWriter.getReader();
        String contentCheck = targetReader.getContentString();
        assertEquals("Conversion back failed", SOME_CONTENT, contentCheck);
    }
}
