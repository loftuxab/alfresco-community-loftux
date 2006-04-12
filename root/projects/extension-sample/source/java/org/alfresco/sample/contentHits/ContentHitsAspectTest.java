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

import javax.transaction.UserTransaction;

import junit.framework.TestCase;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.content.MimetypeMap;
import org.alfresco.repo.security.authentication.AuthenticationComponent;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.ContentWriter;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.namespace.QName;
import org.alfresco.service.transaction.TransactionService;
import org.alfresco.util.ApplicationContextHelper;
import org.springframework.context.ApplicationContext;

/**
 * Content hits apsect sample unit test.
 * 
 * @author Roy Wetherall
 */
public class ContentHitsAspectTest extends TestCase
{
    private static ApplicationContext applicationContext = ApplicationContextHelper.getApplicationContext();
    protected NodeService nodeService;
    protected ContentService contentService;
    protected TransactionService transactionService;
    protected AuthenticationComponent authenticationComponent;

    
    @Override
    public void setUp()
    {
        nodeService = (NodeService)applicationContext.getBean("nodeService");
        contentService = (ContentService)applicationContext.getBean("contentService");
        authenticationComponent = (AuthenticationComponent)applicationContext.getBean("authenticationComponent");
        transactionService = (TransactionService)applicationContext.getBean("transactionComponent");
    
        // Authenticate as the system user
        authenticationComponent.setSystemUserAsCurrentUser();
    }

    @Override
    public void tearDown()
    {
        authenticationComponent.clearCurrentSecurityContext();
    }
    
    
    /**
     * Test the contentHits aspect behaviour
     */
    public void testContentHitsApsectBehaviour()
        throws Exception
    {
        NodeRef nodeRef = null;
        
        UserTransaction userTransaction1 = transactionService.getUserTransaction();
        try
        {
            userTransaction1.begin();
        
            // Create the store and get the root node
            StoreRef storeRef = this.nodeService.createStore(StoreRef.PROTOCOL_WORKSPACE, "Test_" + System.currentTimeMillis());
            NodeRef rootNodeRef = this.nodeService.getRootNode(storeRef);

            // Create the content node
            Map<QName, Serializable> properties = new HashMap<QName, Serializable>(1);
            properties.put(ContentModel.PROP_NAME, "contentHits.txt");
            nodeRef = nodeService.createNode(rootNodeRef, ContentModel.ASSOC_CHILDREN, QName.createQName("{contentHitsAspectTest}countedContent"),
                    ContentModel.TYPE_CONTENT, properties).getChildRef();
            
            // Apply the content hits aspect
            nodeService.addAspect(nodeRef, ContentHitsAspect.ASPECT_CONTENT_HITS, null);
            
            // Check the count hit values
            checkHitCountValues(nodeService, nodeRef, 0, 0);
            
            // Add some content to the node
            ContentWriter contentWriter = contentService.getWriter(nodeRef, ContentModel.PROP_CONTENT, true);
            contentWriter.setEncoding("UTF-8");
            contentWriter.setMimetype(MimetypeMap.MIMETYPE_TEXT_PLAIN);
            contentWriter.putContent("Putting some initial content onto the node.");
            
            // Read the content a couple of times
            contentService.getReader(nodeRef, ContentModel.PROP_CONTENT);
            contentService.getReader(nodeRef, ContentModel.PROP_CONTENT);
            
            // Update the content again
            ContentWriter contentWriter2 = contentService.getWriter(nodeRef, ContentModel.PROP_CONTENT, true);
            contentWriter2.putContent("Updating the existing content.");

            // Check the content hit values
            checkHitCountValues(nodeService, nodeRef, 0, 0);

            userTransaction1.commit();
        }
        catch(Exception e)
        {
            try { userTransaction1.rollback(); } catch (IllegalStateException ee) {}
            throw e;
        }        

        UserTransaction userTransaction2 = transactionService.getUserTransaction();
        try
        {
            userTransaction2.begin();
            checkHitCountValues(nodeService, nodeRef, 1, 1);
            userTransaction2.rollback();
        }
        catch(Exception e)
        {
            try { userTransaction2.rollback(); } catch (IllegalStateException ee) {}
            throw e;
        }        
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
