package org.alfresco.repo.content;

import java.util.Map;

import org.alfresco.util.BaseSpringTest;

/**
 * @see org.alfresco.repo.content.MimetypeMap
 * 
 * @author Derek Hulley
 */
public class MimetypeMapTest extends BaseSpringTest
{
    private MimetypeMap mimetypeMap;

    public void setMimetypeMap(MimetypeMap mimetypeMap)
    {
        this.mimetypeMap = mimetypeMap;
    }
    
    public void testExtensions() throws Exception
    {
        Map<String, String> extensionsByMimetype = mimetypeMap.getExtensionsByMimetype();
        Map<String, String> mimetypesByExtension = mimetypeMap.getMimetypesByExtension();
        
        // plain text
        assertEquals("txt", extensionsByMimetype.get("text/plain"));
        assertEquals("text/plain", mimetypesByExtension.get("txt"));
        assertEquals("text/plain", mimetypesByExtension.get("csv"));
        assertEquals("text/plain", mimetypesByExtension.get("java"));
        
        // JPEG
        assertEquals("jpg", extensionsByMimetype.get("image/jpeg"));
        assertEquals("image/jpeg", mimetypesByExtension.get("jpg"));
        assertEquals("image/jpeg", mimetypesByExtension.get("jpeg"));
        assertEquals("image/jpeg", mimetypesByExtension.get("jpe"));
        
        // MS Word
        assertEquals("doc", extensionsByMimetype.get("application/msword"));
        assertEquals("application/msword", mimetypesByExtension.get("doc"));
    }
}
