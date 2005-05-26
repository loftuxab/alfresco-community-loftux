package org.alfresco.repo.content.transform;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;

import org.alfresco.repo.content.ContentReader;
import org.alfresco.repo.content.ContentWriter;
import org.alfresco.repo.content.MimetypeMap;
import org.alfresco.repo.content.filestore.FileContentReader;
import org.alfresco.repo.content.filestore.FileContentWriter;
import org.alfresco.util.BaseSpringTest;
import org.alfresco.util.TempFileProvider;

/**
 * Provides a base set of tests for testing
 * {@link org.alfresco.repo.content.transform.ContentTransformer}
 * implementations.
 * 
 * @author Derek Hulley
 */
public abstract class AbstractContentTransformerTest extends BaseSpringTest
{
    private static String QUICK_CONTENT = "The quick brown fox jumps over the lazy dog";

    protected MimetypeMap mimetypeMap;
    protected ContentTransformer transformer;

    public final void setMimetypeMap(MimetypeMap mimetypeMap)
    {
        this.mimetypeMap = mimetypeMap;
    }
    
    /**
     * Fetches a transformer to test for a given transformation.  The transformer
     * does not <b>have</b> to be reliable for the given format - if it isn't
     * then it will be ignored.
     * 
     * @param sourceMimetype the sourceMimetype to be tested
     * @param targetMimetype the targetMimetype to be tested
     * @return Returns the <tt>ContentTranslators</tt> that will be tested by
     *      the methods implemented in this class.  A null return value is
     *      acceptable if the source and target mimetypes are not of interest.
     */
    protected abstract ContentTransformer getTransformer(String sourceMimetype, String targetMimetype);
    
    /**
     * Check that all objects are present
     */
    public void testSetUp() throws Exception
    {
        assertNotNull("MimetypeMap not present", mimetypeMap);
    }
    
    /**
     * Helper method to load one of the "The quick brown fox" files from the
     * classpath.
     * 
     * @param extension the extension of the file required
     * @return Returns a test resource loaded from the classpath or <tt>null</tt> if
     *      no resource could be found.
     * @throws IOException
     */
    public static File loadQuickTestFile(String extension) throws IOException
    {
        URL url = AbstractContentTransformerTest.class.getClassLoader().getResource("quick/quick." + extension);
        if (url == null)
        {
            return null;
        }
        File file = new File(url.getFile());
        if (!file.exists())
        {
            return null;
        }
        return file;
    }

    /**
     * Tests the full range of transformations available on the
     * {@link #getTransformer(String, String) transformer} subject to the
     * {@link org.alfresco.util.test.QuickFileTest available test files}
     * and the {@link ContentTransformer#getReliability(String, String) reliability} of
     * the {@link #getTransformer(String, String) transformer} itself. 
     */
    public void testAllConversions() throws Exception
    {
        // get all mimetypes
        List<String> mimetypes = mimetypeMap.getMimetypes();
        for (String sourceMimetype : mimetypes)
        {
            // attempt to get a source file for each mimetype
            String sourceExtension = mimetypeMap.getExtension(sourceMimetype);
            File sourceFile = AbstractContentTransformerTest.loadQuickTestFile(sourceExtension);
            if (sourceFile == null)
            {
                continue;  // no test file available for that extension
            }
            
            // attempt to convert to every other mimetype
            for (String targetMimetype : mimetypes)
            {
                // must we test the transformation?
                ContentTransformer transformer = getTransformer(sourceMimetype, targetMimetype);
                if (transformer == null)
                {
                    continue;   // test is not required
                }
                else if (transformer.getReliability(sourceMimetype, targetMimetype) <= 0.0)
                {
                    continue;   // not reliable for this transformation
                }
                
                // construct a reader onto the source file
                ContentReader sourceReader = new FileContentReader(sourceFile);
                
                // perform the transformation several times so that we get a good idea of performance
                int iterations = 5;
                ContentWriter targetWriter = null;
                for (int i = 0; i < iterations; i++)
                {
                    // make a writer for the target file
                    String targetExtension = mimetypeMap.getExtension(targetMimetype);
                    File targetFile = TempFileProvider.createTempFile(getName(), "." + targetExtension);
                    targetWriter = new FileContentWriter(targetFile);
                    
                    // do the transformation
                    sourceReader.setMimetype(sourceMimetype);
                    targetWriter.setMimetype(targetMimetype);
                    transformer.transform(sourceReader.getReader(), targetWriter);
                    
                    // if the target format is any type of text, then it must contain the 'quick' phrase
                    if (targetMimetype.startsWith(StringExtractingContentTransformer.PREFIX_TEXT))
                    {
                        ContentReader targetReader = targetWriter.getReader();
                        String checkContent = targetReader.getContentString();
                        assertTrue("Quick phrase not present in document converted to text by transformer: " + transformer,
                                checkContent.contains(QUICK_CONTENT));
                    }
                }
                
                if (logger.isDebugEnabled())
                {
                    logger.debug("Transformation performed " + iterations + " time: " +
                            sourceMimetype + " --> " + targetMimetype + "\n" +
                            "   source: " + sourceReader + "\n" +
                            "   target: " + targetWriter + "\n" +
                            "   transformer: " + transformer);
                }
            }
        }
    }
}
