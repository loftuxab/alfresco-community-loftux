package org.alfresco.repo.content.transform;

import org.alfresco.repo.content.MimetypeMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @see org.alfresco.repo.content.transform.PdfBoxContentTransformer
 * 
 * @author Derek Hulley
 */
public class PdfBoxContentTransformerTest extends AbstractContentTransformerTest
{
    private static final Log logger = LogFactory.getLog(PdfBoxContentTransformerTest.class);

    private ContentTransformer transformer;
    
    public void onSetUpInTransaction() throws Exception
    {
        transformer = new PdfBoxContentTransformer();
    }
    
    /**
     * @return Returns the same transformer regardless - it is allowed
     */
    protected ContentTransformer getTransformer(String sourceMimetype, String targetMimetype)
    {
        return transformer;
    }
    
    public void testReliability() throws Exception
    {
        double reliability = 0.0;
        reliability = transformer.getReliability(MimetypeMap.MIMETYPE_TEXT_PLAIN, MimetypeMap.MIMETYPE_PDF);
        assertEquals("Mimetype should not be supported", 0.0, reliability);
        reliability = transformer.getReliability(MimetypeMap.MIMETYPE_PDF, MimetypeMap.MIMETYPE_TEXT_PLAIN);
        assertEquals("Mimetype should be supported", 1.0, reliability);
    }
}
