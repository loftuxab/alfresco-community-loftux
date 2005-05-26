package org.alfresco.repo.content.transform;

import org.alfresco.repo.content.MimetypeMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @see org.alfresco.repo.content.transform.UnoContentTransformer
 * 
 * @author Derek Hulley
 */
public class UnoContentTransformerTest extends AbstractContentTransformerTest
{
    private static String MIMETYPE_RUBBISH = "text/rubbish";
    
    private static final Log logger = LogFactory.getLog(UnoContentTransformerTest.class);

    private UnoContentTransformer transformer;
    
    public void onSetUpInTransaction() throws Exception
    {
        transformer = new UnoContentTransformer(mimetypeMap);
    }
    
    /**
     * @return Returns the same transformer regardless - it is allowed
     */
    protected ContentTransformer getTransformer(String sourceMimetype, String targetMimetype)
    {
        return transformer;
    }

    public void testSetUp() throws Exception
    {
        super.testSetUp();
        assertNotNull(mimetypeMap);
    }
    
    public void testReliability() throws Exception
    {
        if (!transformer.isConnected())
        {
            // no connection
            return;
        }
        double reliability = 0.0;
        reliability = transformer.getReliability(MIMETYPE_RUBBISH, MimetypeMap.MIMETYPE_TEXT_PLAIN);
        assertEquals("Mimetype should not be supported", 0.0, reliability);
        reliability = transformer.getReliability(MimetypeMap.MIMETYPE_TEXT_PLAIN, MIMETYPE_RUBBISH);
        assertEquals("Mimetype should not be supported", 0.0, reliability);
        reliability = transformer.getReliability(MimetypeMap.MIMETYPE_TEXT_PLAIN, MimetypeMap.MIMETYPE_WORD);
        assertEquals("Mimetype should be supported", 1.0, reliability);
        reliability = transformer.getReliability(MimetypeMap.MIMETYPE_WORD, MimetypeMap.MIMETYPE_TEXT_PLAIN);
        assertEquals("Mimetype should be supported", 1.0, reliability);
    }
}
