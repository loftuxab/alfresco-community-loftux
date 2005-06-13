/**
 * Created on May 5, 2005
 */
package org.alfresco.repo.version.lightweight;

import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.ContentWriter;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.version.Version;

/**
 * Tests for retrieving frozen content from a verioned node
 * 
 * @author Roy Wetherall
 */
public class ContentServiceImplTest extends VersionStoreBaseTest
{
    /**
     * Error message
     */
    private final static String MSG_ERR = 
        "This operation is not supported by a version store implementation of the content service.";
    
    /**
     * Test content data
     */
    private final static String UPDATED_CONTENT = "This content has been updated with a new value.";
    
    /**
     * The version content store
     */
    private ContentService contentService;    
    
    /**
     * Called during the transaction setup
     */
    protected void onSetUpInTransaction() throws Exception
    {
        super.onSetUpInTransaction();
        
        // Get the instance of the required content service
        this.contentService = (ContentService)this.applicationContext.getBean("contentService");
    }
    
    /**
     * Test getReader
     */
    public void testGetReader()
    {
        // Create a new versionable node
        NodeRef versionableNode = createNewVersionableNode();
        
        // Create a new version
        Version version = createVersion(versionableNode, this.versionProperties);
        NodeRef versionNodeRef = version.getNodeRef();
		
        // Get the content reader for the frozen node
        ContentReader contentReader = this.contentService.getReader(versionNodeRef);
        assertNotNull(contentReader);
        assertEquals(TEST_CONTENT, contentReader.getContentString());
        
        // Now update the content and verison again
        ContentWriter contentWriter = this.contentService.getUpdatingWriter(versionableNode);
        assertNotNull(contentWriter);
        contentWriter.putContent(UPDATED_CONTENT);        
        Version version2 = createVersion(versionableNode, this.versionProperties);
        NodeRef version2NodeRef = version2.getNodeRef();
		
        // Get the content reader for the new verisoned content
        ContentReader contentReader2 = this.contentService.getReader(version2NodeRef);
        assertNotNull(contentReader2);
        assertEquals(UPDATED_CONTENT, contentReader2.getContentString());
    }
    
    /**
     * Test getWriter
     */
    public void testGetWriter()
    {
        // Create a new versionable node
        NodeRef versionableNode = createNewVersionableNode();
        
        // Create a new version
        Version version = createVersion(versionableNode, this.versionProperties);
        
        // Get writer is not supported by the version content service
        try
        {
            ContentWriter contentWriter = this.contentService.getUpdatingWriter(version.getNodeRef());
            contentWriter.putContent("bobbins");
            fail("This operation is not supported.");
        }
        catch (Exception exception)
        {
            // An exception should be raised
        }
    }
}
