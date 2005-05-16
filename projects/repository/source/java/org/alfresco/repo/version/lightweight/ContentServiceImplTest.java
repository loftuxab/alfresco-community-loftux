/**
 * Created on May 5, 2005
 */
package org.alfresco.repo.version.lightweight;

import org.alfresco.repo.content.ContentReader;
import org.alfresco.repo.content.ContentService;
import org.alfresco.repo.content.ContentWriter;
import org.alfresco.repo.ref.NodeRef;
import org.alfresco.repo.version.Version;

/**
 * Unit test for ContentServiceImpl
 * 
 * @author Roy Wetherall
 */
public class ContentServiceImplTest extends BaseImplTest
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
    private ContentService versionContentService;    
    
    /**
     * Called during the transaction setup
     */
    protected void onSetUpInTransaction() throws Exception
    {
        super.onSetUpInTransaction();
        
        // Get the instance of the required content service
        this.versionContentService = (ContentService)this.applicationContext.getBean("lightWeightVersionStoreContentService");
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
        
        // Get the content reader for the frozen node
        ContentReader contentReader = this.versionContentService.getReader(version.getNodeRef());
        assertNotNull(contentReader);
        assertEquals(TEST_CONTENT, contentReader.getContentString());
        
        // Now update the content and verison again
        ContentWriter contentWriter = this.contentService.getUpdatingWriter(versionableNode);
        assertNotNull(contentWriter);
        contentWriter.putContent(UPDATED_CONTENT);        
        Version version2 = createVersion(versionableNode, this.versionProperties);
        
        // Get the content reader for the new verisoned content
        ContentReader contentReader2 = this.versionContentService.getReader(version2.getNodeRef());
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
            this.versionContentService.getUpdatingWriter(version.getNodeRef());
            fail("This operation is not supported.");
        }
        catch (UnsupportedOperationException exception)
        {
            if (exception.getMessage() != MSG_ERR)
            {
                fail("Unexpected exception raised during method excution: " + exception.getMessage());
            }
        }
    }
}
