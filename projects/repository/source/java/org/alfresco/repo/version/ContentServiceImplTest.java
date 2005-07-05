/*
 * Copyright (C) 2005 Alfresco, Inc.
 *
 * Licensed under the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version
 * 2.1 of the License, or (at your option) any later version.
 * You may obtain a copy of the License at
 *
 *     http://www.gnu.org/licenses/lgpl.txt
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the
 * License.
 */
package org.alfresco.repo.version;

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
public class ContentServiceImplTest extends BaseVersionStoreTest
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
