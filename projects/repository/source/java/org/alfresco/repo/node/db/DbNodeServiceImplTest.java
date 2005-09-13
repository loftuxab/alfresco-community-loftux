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
package org.alfresco.repo.node.db;

import java.util.List;
import java.util.Map;

import javax.transaction.UserTransaction;

import org.alfresco.repo.node.BaseNodeServiceTest;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.QName;
import org.alfresco.service.transaction.TransactionService;

/**
 * @see org.alfresco.repo.node.db.DbNodeServiceImpl
 * 
 * @author Derek Hulley
 */
public class DbNodeServiceImplTest extends BaseNodeServiceTest
{
    private TransactionService txnService;
    private NodeDaoService nodeDaoService;
    
    protected NodeService getNodeService()
    {
        return (NodeService) applicationContext.getBean("dbNodeService");
    }

    @Override
    protected void onSetUpInTransaction() throws Exception
    {
        super.onSetUpInTransaction();
        txnService = (TransactionService) applicationContext.getBean("transactionComponent");
        nodeDaoService = (NodeDaoService) applicationContext.getBean("nodeDaoService");
    }

    /**
     * Deletes a child node and then iterates over the children of the parent node,
     * getting the QName.  This caused some issues after we did some optimization
     * using lazy loading of the associations.
     */
    public void testLazyLoadIssue() throws Exception
    {
        Map<QName, ChildAssociationRef> assocRefs = buildNodeGraph();
        // commit results
        setComplete();
        endTransaction();

        UserTransaction userTransaction = txnService.getUserTransaction();
        
        try
        {
            userTransaction.begin();
            
            ChildAssociationRef n6pn8Ref = assocRefs.get(QName.createQName(BaseNodeServiceTest.NAMESPACE, "n6_p_n8"));
            NodeRef n6Ref = n6pn8Ref.getParentRef();
            NodeRef n8Ref = n6pn8Ref.getChildRef();
            
            // delete n8
            nodeService.deleteNode(n8Ref);
            
            // get the parent children
            List<ChildAssociationRef> assocs = nodeService.getChildAssocs(n6Ref);
            for (ChildAssociationRef assoc : assocs)
            {
                // just checking
            }
            
            userTransaction.commit();
        }
        catch(Exception e)
        {
            userTransaction.rollback();
            throw e;
        }
    }
}
