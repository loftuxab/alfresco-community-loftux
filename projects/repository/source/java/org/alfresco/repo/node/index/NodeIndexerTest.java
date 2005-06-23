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
package org.alfresco.repo.node.index;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.alfresco.repo.node.BaseNodeServiceTest;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.search.ResultSet;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.namespace.DynamicNamespacePrefixResolver;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.perf.PerformanceMonitor;

/**
 * Checks that the indexing of the node hierarchy is working
 * 
 * @see org.alfresco.repo.node.index.NodeIndexer
 * 
 * @author Derek Hulley
 */
public class NodeIndexerTest extends BaseNodeServiceTest
{
    private SearchService searcher;
    private static StoreRef localStoreRef;
    private static NodeRef localRootNode;

    @Override
    protected NodeService getNodeService()
    {
        return (NodeService) applicationContext.getBean("nodeService");
    }
    
    @Override
    protected ContentService getContentService()
    {
        return (ContentService) applicationContext.getBean("contentService");
    }

    @Override
    protected void onSetUpInTransaction() throws Exception
    {
        super.onSetUpInTransaction();
        searcher = (SearchService) applicationContext.getBean("searcherComponent");

        if (localStoreRef == null)
        {
            localStoreRef = nodeService.createStore(StoreRef.PROTOCOL_WORKSPACE, "Test_Persisted" + System.currentTimeMillis());
            localRootNode = nodeService.getRootNode(localStoreRef);
        }
    }

    public void testCommitQueryData() throws Exception
    {
        rootNodeRef = localRootNode;
        Map<QName, ChildAssociationRef> assocRefs = buildNodeGraph();
        setComplete();
    }

    public void testQuery() throws Exception
    {
        rootNodeRef = localRootNode;
        ResultSet results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "PATH:\"" + BaseNodeServiceTest.TEST_PREFIX + ":root_p_n1\"", null, null);
        assertEquals(1, results.length());
    }
    
    public void testLikeAndContains() throws Exception
    {
        rootNodeRef = localRootNode;
        
        DynamicNamespacePrefixResolver namespacePrefixResolver = new DynamicNamespacePrefixResolver(null);
        namespacePrefixResolver.addDynamicNamespace(NamespaceService.ALFRESCO_PREFIX, NamespaceService.ALFRESCO_URI);
        namespacePrefixResolver.addDynamicNamespace(BaseNodeServiceTest.TEST_PREFIX, BaseNodeServiceTest.NAMESPACE);
   
        PerformanceMonitor selectNodesPerf = new PerformanceMonitor(getClass().getSimpleName(), "selectNodes");
        PerformanceMonitor selectPropertiesPerf = new PerformanceMonitor(getClass().getSimpleName(), "selectProperties");
        
        List<NodeRef> answer;
        
        selectNodesPerf.start();
        answer =  nodeService.selectNodes(rootNodeRef, "//*[like(@test:animal, '*monkey')", null, namespacePrefixResolver, false);
        assertEquals(1, answer.size());
        selectNodesPerf.stop();
        
        selectNodesPerf.start();
        answer =  nodeService.selectNodes(rootNodeRef, "//*[like(@test:animal, '%monkey')", null, namespacePrefixResolver, false);
        assertEquals(1, answer.size());
        selectNodesPerf.stop();
        
        selectNodesPerf.start();
        answer =  nodeService.selectNodes(rootNodeRef, "//*[like(@test:animal, 'monk*')", null, namespacePrefixResolver, false);
        assertEquals(1, answer.size());
        selectNodesPerf.stop();
        
        selectNodesPerf.start();
        answer =  nodeService.selectNodes(rootNodeRef, "//*[like(@test:animal, 'monk%')", null, namespacePrefixResolver, false);
        assertEquals(1, answer.size());
        selectNodesPerf.stop();
        
        selectNodesPerf.start();
        answer =  nodeService.selectNodes(rootNodeRef, "//*[like(@test:animal, 'monk\\%')", null, namespacePrefixResolver, false);
        assertEquals(0, answer.size());
        selectNodesPerf.stop();
        
        selectNodesPerf.start();
        answer =  nodeService.selectNodes(rootNodeRef, "//*[contains('monkey')", null, namespacePrefixResolver, false);
        assertEquals(1, answer.size());
        selectNodesPerf.stop();
        
        selectPropertiesPerf.start();
        List<Serializable> result =  nodeService.selectProperties(rootNodeRef, "//@*[contains('monkey')", null, namespacePrefixResolver, false);
        assertEquals(1, result.size());
        selectPropertiesPerf.stop();
        
        selectNodesPerf.start();
        answer =  nodeService.selectNodes(rootNodeRef, "//*[contains('mon?ey')", null, namespacePrefixResolver, false);
        assertEquals(1, answer.size());
        selectNodesPerf.stop();
        
        selectPropertiesPerf.start();
        result =  nodeService.selectProperties(rootNodeRef, "//@*[contains('mon?ey')", null, namespacePrefixResolver, false);
        assertEquals(1, result.size());
        selectPropertiesPerf.stop();
        
        selectNodesPerf.start();
        answer =  nodeService.selectNodes(rootNodeRef, "//*[contains('m*y')", null, namespacePrefixResolver, false);
        assertEquals(1, answer.size());
        selectNodesPerf.stop();
        
        selectPropertiesPerf.start();
        result =  nodeService.selectProperties(rootNodeRef, "//@*[contains('mon*')", null, namespacePrefixResolver, false);
        assertEquals(1, result.size());
        selectPropertiesPerf.stop();
        
        selectNodesPerf.start();
        answer =  nodeService.selectNodes(rootNodeRef, "//*[contains('*nkey')", null, namespacePrefixResolver, false);
        assertEquals(1, answer.size());
        selectNodesPerf.stop();
        
        selectPropertiesPerf.start();
        result =  nodeService.selectProperties(rootNodeRef, "//@*[contains('?onkey')", null, namespacePrefixResolver, false);
        assertEquals(1, result.size());
        selectPropertiesPerf.stop();
    }
}
