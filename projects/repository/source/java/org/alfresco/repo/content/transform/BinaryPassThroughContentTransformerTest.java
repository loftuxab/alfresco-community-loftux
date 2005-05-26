package org.alfresco.repo.content.transform;

import org.alfresco.repo.content.MimetypeMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @see org.alfresco.repo.content.transform.BinaryPassThroughContentTransformer
 * 
 * @author Derek Hulley
 */
public class BinaryPassThroughContentTransformerTest extends AbstractContentTransformerTest
{
    private static final Log logger = LogFactory.getLog(BinaryPassThroughContentTransformerTest.class);

    private ContentTransformer transformer;
    
    public void onSetUpInTransaction() throws Exception
    {
        transformer = new BinaryPassThroughContentTransformer();
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
        reliability = transformer.getReliability(MimetypeMap.MIMETYPE_TEXT_PLAIN, MimetypeMap.MIMETYPE_TEXT_PLAIN);
        assertEquals("Mimetype should not be supported", 0.0, reliability);
        reliability = transformer.getReliability(MimetypeMap.MIMETYPE_XML, MimetypeMap.MIMETYPE_XML);
        assertEquals("Mimetype should not be supported", 0.0, reliability);
        reliability = transformer.getReliability(MimetypeMap.MIMETYPE_WORD, MimetypeMap.MIMETYPE_WORD);
        assertEquals("Mimetype should be supported", 1.0, reliability);
        reliability = transformer.getReliability(MimetypeMap.MIMETYPE_EXCEL, MimetypeMap.MIMETYPE_EXCEL);
        assertEquals("Mimetype should be supported", 1.0, reliability);
    }
}
