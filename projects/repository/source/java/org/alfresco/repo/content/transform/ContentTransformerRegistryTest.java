package org.alfresco.repo.content.transform;

import java.util.ArrayList;
import java.util.List;

import org.alfresco.repo.content.ContentIOException;
import org.alfresco.repo.content.ContentReader;
import org.alfresco.repo.content.ContentWriter;
import org.alfresco.repo.content.MimetypeMap;
import org.alfresco.util.BaseSpringTest;

/**
 * @see org.alfresco.repo.content.transform.ContentTransformerRegistry
 * 
 * @author Derek Hulley
 */
public class ContentTransformerRegistryTest extends BaseSpringTest
{
    private static final String A = "A";
    private static final String B = "B";
    private static final String C = "C";
    
    private ContentTransformerRegistry registry;
    private ContentTransformerRegistry dummyRegistry;
    private MimetypeMap mimetypeMap;
    
    public void setMimetypeMap(MimetypeMap mimetypeMap)
    {
        this.mimetypeMap = mimetypeMap;
    }
    
    public void setContentTransformerRegistry(ContentTransformerRegistry registry)
    {
        this.registry = registry;
    }

    @Override
    public void onSetUpInTransaction() throws Exception
    {
        byte[] bytes = new byte[256];
        for (int i = 0; i < 256; i++)
        {
            bytes[i] = (byte)i;
        }
        String string = new String(bytes, "UTF-8");
        
        List<ContentTransformer> transformers = new ArrayList<ContentTransformer>(5);
        // create some dummy transformers - named by transformation and percentage reliability
        transformers.add(new DummyTransformer(A, B, 0.3));
        transformers.add(new DummyTransformer(A, B, 0.6));
        transformers.add(new DummyTransformer(A, C, 0.5));
        transformers.add(new DummyTransformer(A, C, 1.0));
        transformers.add(new DummyTransformer(B, C, 0.2));
        // create the dummyRegistry
        dummyRegistry = new ContentTransformerRegistry(transformers, mimetypeMap);
    }
    
    public void testNullRetrieval() throws Exception
    {
        ContentTransformer transformer = null;
        transformer = dummyRegistry.getTransformer(C, B);
        assertNull("No transformer expected", transformer);
        transformer = dummyRegistry.getTransformer(C, A);
        assertNull("No transformer expected", transformer);
        transformer = dummyRegistry.getTransformer(B, A);
        assertNull("No transformer expected", transformer);
    }
    
    public void testSimpleRetrieval() throws Exception
    {
        ContentTransformer transformer = null;
        // B -> C expect 0.2
        transformer = dummyRegistry.getTransformer(B, C);
        assertNotNull("No transformer found", transformer);
        assertEquals("Incorrect reliability", 0.2, transformer.getReliability(B, C));
        assertEquals("Incorrect reliability", 0.0, transformer.getReliability(C, B));
    }
    
    public void testScoredRetrieval() throws Exception
    {
        ContentTransformer transformer = null;
        // A -> B expect 0.6
        transformer = dummyRegistry.getTransformer(A, B);
        assertNotNull("No transformer found", transformer);
        assertEquals("Incorrect reliability", 0.6, transformer.getReliability(A, B));
        assertEquals("Incorrect reliability", 0.0, transformer.getReliability(B, A));
        // A -> C expect 1.0
        transformer = dummyRegistry.getTransformer(A, C);
        assertNotNull("No transformer found", transformer);
        assertEquals("Incorrect reliability", 1.0, transformer.getReliability(A, C));
        assertEquals("Incorrect reliability", 0.0, transformer.getReliability(C, A));
    }
    
    /**
     * Dummy transformer that does no transformation and scores exactly as it is
     * told to in the constructor.  It enables the tests to be sure of what to expect.
     */
    private static class DummyTransformer implements ContentTransformer
    {
        private String sourceMimetype;
        private String targetMimetype;
        private double reliability;
        
        public DummyTransformer(String sourceMimetype, String targetMimetype, double reliability)
        {
            this.sourceMimetype = sourceMimetype;
            this.targetMimetype = targetMimetype;
            this.reliability = reliability;
        }

        public double getReliability(String sourceMimetype, String targetMimetype)
        {
            if (this.sourceMimetype.equals(sourceMimetype)
                    && this.targetMimetype.equals(targetMimetype))
            {
                return reliability;
            }
            else
            {
                return 0.0;
            }
        }

        /**
         * We are not test transformations - just the dummyRegistry
         */
        public void transform(ContentReader reader, ContentWriter writer) throws ContentIOException
        {
            throw new UnsupportedOperationException();
        }
    }
}
