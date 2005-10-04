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
package org.alfresco.repo.node.index;

import java.util.List;

import junit.framework.TestCase;

import org.alfresco.repo.search.Indexer;
import org.alfresco.repo.transaction.TransactionUtil;
import org.alfresco.repo.transaction.TransactionUtil.TransactionWork;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.InvalidStoreRefException;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.transaction.TransactionService;
import org.alfresco.util.ApplicationContextHelper;
import org.springframework.context.ApplicationContext;

/**
 * Checks that full index recovery is possible
 * 
 * @author Derek Hulley
 */
public class FullIndexRecoveryComponentTest extends TestCase
{
    private static ApplicationContext ctx = ApplicationContextHelper.getApplicationContext();
    
    private IndexRecovery indexRecoverer;
    private NodeService nodeService;
    private TransactionService txnService;
    private Indexer indexer;
    
    public void setUp() throws Exception
    {
        indexRecoverer = (IndexRecovery) ctx.getBean("indexRecoveryComponent");
        txnService = (TransactionService) ctx.getBean("transactionComponent");
        nodeService = (NodeService) ctx.getBean("nodeService");
        indexer = (Indexer) ctx.getBean("indexerComponent");
    }
    
    public void testReindexing() throws Exception
    {
        // deletes all nodes from the index
        TransactionWork<Object> dropIndexWork = new TransactionWork<Object>()
        {
            public Object doWork()
            {
                // now drop the index for all stores
                List<StoreRef> storeRefs = nodeService.getStores();
                for (StoreRef storeRef : storeRefs)
                {
                    try
                    {
                        NodeRef rootNodeRef = nodeService.getRootNode(storeRef);
                        ChildAssociationRef assocRef = nodeService.getPrimaryParent(rootNodeRef);
                        indexer.deleteNode(assocRef);
                    }
                    catch (InvalidStoreRefException e)
                    {
                        // just ignore stores that are invalid
                    }
                }
                return null;
            }
        };
        // performs a reindex
        TransactionWork<List<String>> reindexWork = new TransactionWork<List<String>>()
        {
            public List<String> doWork()
            {
                return indexRecoverer.reindex();
            }
        };
        
        // drop the indexes
        TransactionUtil.executeInNonPropagatingUserTransaction(txnService, dropIndexWork);
        
        // reindex
        List<String> reindexedMany = TransactionUtil.executeInNonPropagatingUserTransaction(txnService, reindexWork);
        assertTrue("Nothing was reindexed", reindexedMany.size() > 0);
        
        // reindex
        List<String> reindexedNone = TransactionUtil.executeInNonPropagatingUserTransaction(txnService, reindexWork);
        assertEquals("Nothing should have been reindexed", 0, reindexedNone.size());
    }
}
