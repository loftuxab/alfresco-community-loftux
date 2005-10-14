/*
 * Copyright (C) 2005 Alfresco, Inc.
 *
 * Licensed under the Alfresco Network License. You may obtain a
 * copy of the License at
 *
 *   http://www.alfrescosoftware.com/legal/
 *
 * Please view the license relevant to your network subscription.
 *
 * BY CLICKING THE "I UNDERSTAND AND ACCEPT" BOX, OR INSTALLING,  
 * READING OR USING ALFRESCO'S Network SOFTWARE (THE "SOFTWARE"),  
 * YOU ARE AGREEING ON BEHALF OF THE ENTITY LICENSING THE SOFTWARE    
 * ("COMPANY") THAT COMPANY WILL BE BOUND BY AND IS BECOMING A PARTY TO 
 * THIS ALFRESCO NETWORK AGREEMENT ("AGREEMENT") AND THAT YOU HAVE THE   
 * AUTHORITY TO BIND COMPANY. IF COMPANY DOES NOT AGREE TO ALL OF THE   
 * TERMS OF THIS AGREEMENT, DO NOT SELECT THE "I UNDERSTAND AND AGREE"   
 * BOX AND DO NOT INSTALL THE SOFTWARE OR VIEW THE SOURCE CODE. COMPANY   
 * HAS NOT BECOME A LICENSEE OF, AND IS NOT AUTHORIZED TO USE THE    
 * SOFTWARE UNLESS AND UNTIL IT HAS AGREED TO BE BOUND BY THESE LICENSE  
 * TERMS. THE "EFFECTIVE DATE" FOR THIS AGREEMENT SHALL BE THE DAY YOU  
 * CHECK THE "I UNDERSTAND AND ACCEPT" BOX.
 */
package org.alfresco.repo.node.index;

import java.util.List;

import junit.framework.TestCase;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.search.Indexer;
import org.alfresco.repo.transaction.AlfrescoTransactionSupport;
import org.alfresco.repo.transaction.TransactionUtil;
import org.alfresco.repo.transaction.TransactionUtil.TransactionWork;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.InvalidStoreRefException;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
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
        // deletes a content node from the index
        TransactionWork<String> dropNodeIndexWork = new TransactionWork<String>()
        {
            public String doWork()
            {
                // create a node in each store and drop it from the index
                List<StoreRef> storeRefs = nodeService.getStores();
                for (StoreRef storeRef : storeRefs)
                {
                    try
                    {
                        NodeRef rootNodeRef = nodeService.getRootNode(storeRef);
                        ChildAssociationRef assocRef = nodeService.createNode(
                                rootNodeRef,
                                ContentModel.ASSOC_CONTAINS,
                                QName.createQName(NamespaceService.ALFRESCO_URI, "unindexedChild" + System.currentTimeMillis()),
                                ContentModel.TYPE_BASE);
                        // this will have indexed it, so remove it from the index
                        indexer.deleteNode(assocRef);
                    }
                    catch (InvalidStoreRefException e)
                    {
                        // just ignore stores that are invalid
                    }
                }
                return AlfrescoTransactionSupport.getTransactionId();
            }
        };
        
        // create un-indexed nodes
        String txnId = TransactionUtil.executeInNonPropagatingUserTransaction(txnService, dropNodeIndexWork);
        
        // reindex
        indexRecoverer.reindex();

        // check that reindexing fails
        try
        {
            indexRecoverer.reindex();
            fail("Reindexer failed to prevent reindex from being called twice");
        }
        catch (RuntimeException e)
        {
            // expected
        }
        
        // loop for some time, giving it a chance to do its thing
        for (int i = 0; i < 60; i++)
        {
            String lastProcessedTxnId = FullIndexRecoveryComponent.getCurrentTransactionId();
            if (lastProcessedTxnId.equals(txnId))
            {
                break;
            }
            // wait for a second
            synchronized(this)
            {
                this.wait(1000L);
            }
        }
    }
}
