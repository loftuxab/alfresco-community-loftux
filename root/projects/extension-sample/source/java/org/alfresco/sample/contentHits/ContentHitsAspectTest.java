/*
 * Copyright (C) 2005 Alfresco, Inc.
 *
 * Licensed under the Mozilla Public License version 1.1 
 * with a permitted attribution clause. You may obtain a
 * copy of the License at
 *
 *   http://www.alfresco.org/legal/license.txt
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the
 * License.
 */
package org.alfresco.sample.contentHits;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.content.MimetypeMap;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.ContentWriter;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.BaseAlfrescoSpringTest;

/**
 * Content hits apsect sample unit test.
 * 
 * @author Roy Wetherall
 */
public class ContentHitsAspectTest extends BaseAlfrescoSpringTest
{
    /**
     * Test the contentHits aspect behaviour
     */
    public void testContentHitsApsectBehaviour()
    {
        // Get the node and content services
        NodeService nodeService = (NodeService)this.applicationContext.getBean("nodeService");
        ContentService contentService = (ContentService)this.applicationContext.getBean("contentService");
        
        // Create the content node
        Map<QName, Serializable> properties = new HashMap<QName, Serializable>(1);
        properties.put(ContentModel.PROP_NAME, "contentHits.txt");
        NodeRef nodeRef = nodeService.createNode(
                this.rootNodeRef,
                ContentModel.ASSOC_CHILDREN,
                QName.createQName("{contentHitsAspectTest}countedContent"),
                ContentModel.TYPE_CONTENT,
                properties).getChildRef();
        
        // Apply the content hits aspect
        nodeService.addAspect(nodeRef, ContentHitsAspect.ASPECT_CONTENT_HITS, null);
        
        // Check the count hit values
        checkHitCountValues(nodeService, nodeRef, 0, 0);
        
        // Add some content to the node
        ContentWriter contentWriter = contentService.getWriter(nodeRef, ContentModel.PROP_CONTENT, true);
        contentWriter.setEncoding("UTF-8");
        contentWriter.setMimetype(MimetypeMap.MIMETYPE_TEXT_PLAIN);
        contentWriter.putContent("Putting some initial content onto the node.");
        
        // Check the content hit values
        checkHitCountValues(nodeService, nodeRef, 1, 0);
        
        // Read the content a couple of times
        contentService.getReader(nodeRef, ContentModel.PROP_CONTENT);
        contentService.getReader(nodeRef, ContentModel.PROP_CONTENT);
        
        // Check the content hit values
        checkHitCountValues(nodeService, nodeRef, 1, 2);
        
        // Update the content again
        ContentWriter contentWriter2 = contentService.getWriter(nodeRef, ContentModel.PROP_CONTENT, true);
        contentWriter2.putContent("Updating the existing content.");
        
        // Check the content hit values
        checkHitCountValues(nodeService, nodeRef, 2, 2);
    }
    
    /**
     * Helper method to check that contentHits apsect currently holds the expected values
     * 
     * @param nodeService               the node service
     * @param nodeRef                   the node reference
     * @param expectedUpdateCount       the expected update count value
     * @param expectedReadCount         the expected read count value
     */
    private void checkHitCountValues(NodeService nodeService, NodeRef nodeRef, int expectedUpdateCount, int expectedReadCount)
    {
        // Get the update count value
        int currentUpdateCount = ((Integer)nodeService.getProperty(nodeRef, ContentHitsAspect.PROP_UPDATE_COUNT)).intValue();
        
        // Assert that it matches the expected value
        assertEquals(expectedUpdateCount, currentUpdateCount);
        
        // Get the read count value
        int currentReadCount = ((Integer)nodeService.getProperty(nodeRef, ContentHitsAspect.PROP_READ_COUNT)).intValue();
        
        // Assert that it matches the expected value        
        assertEquals(expectedReadCount, currentReadCount);
    }
}
