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
package org.alfresco.repo.node;

import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.transaction.Status;
import javax.transaction.UserTransaction;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.content.MimetypeMap;
import org.alfresco.repo.dictionary.DictionaryComponent;
import org.alfresco.repo.dictionary.DictionaryDAO;
import org.alfresco.repo.dictionary.M2Model;
import org.alfresco.repo.domain.hibernate.NodeImpl;
import org.alfresco.repo.node.db.NodeDaoService;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.repo.transaction.AlfrescoTransactionSupport;
import org.alfresco.repo.transaction.TransactionUtil;
import org.alfresco.repo.transaction.TransactionUtil.TransactionWork;
import org.alfresco.service.cmr.dictionary.ClassDefinition;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.dictionary.InvalidAspectException;
import org.alfresco.service.cmr.dictionary.PropertyDefinition;
import org.alfresco.service.cmr.repository.AssociationExistsException;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.ContentData;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.ContentWriter;
import org.alfresco.service.cmr.repository.CyclicChildRelationshipException;
import org.alfresco.service.cmr.repository.InvalidNodeRefException;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.Path;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.service.namespace.RegexQNamePattern;
import org.alfresco.service.transaction.TransactionService;
import org.alfresco.util.BaseSpringTest;
import org.alfresco.util.GUID;
import org.hibernate.Session;
import org.springframework.context.ApplicationContext;

/**
 * PerformanceNodeServiceTest
 */
public class PerformanceNodeServiceTest extends BaseSpringTest
{
    public static final String NAMESPACE = "http://www.alfresco.org/test/BaseNodeServiceTest";
    public static final String TEST_PREFIX = "test";
    public static final QName  TYPE_QNAME_TEST = QName.createQName(NAMESPACE, "multiprop");
    public static final QName  PROP_QNAME_NAME = QName.createQName(NAMESPACE, "name");
    public static final QName  ASSOC_QNAME_CHILDREN = QName.createQName(NAMESPACE, "child");
    
    protected DictionaryService dictionaryService;
    protected NodeService nodeService;
    private ContentService contentService;
    private TransactionService txnService;
    
    private int nodeCount = 0;
    
    /** populated during setup */
    protected NodeRef rootNodeRef;

    protected void onSetUpInTransaction() throws Exception
    {
        DictionaryDAO dictionaryDao = (DictionaryDAO) applicationContext.getBean("dictionaryDAO");
        
        // load the system model
        ClassLoader cl = PerformanceNodeServiceTest.class.getClassLoader();
        InputStream modelStream = cl.getResourceAsStream("alfresco/model/contentModel.xml");
        assertNotNull(modelStream);
        M2Model model = M2Model.createModel(modelStream);
        dictionaryDao.putModel(model);
        
        // load the test model
        modelStream = cl.getResourceAsStream("org/alfresco/repo/node/BaseNodeServiceTest_model.xml");
        assertNotNull(modelStream);
        model = M2Model.createModel(modelStream);
        dictionaryDao.putModel(model);
        
        DictionaryComponent dictionary = new DictionaryComponent();
        dictionary.setDictionaryDAO(dictionaryDao);
        dictionaryService = loadModel(applicationContext);
        
        nodeService = getNodeService();
        txnService = (TransactionService) applicationContext.getBean("transactionComponent");
        contentService = (ContentService) applicationContext.getBean("contentService");
        
        // create a first store directly
        StoreRef storeRef = nodeService.createStore(
                StoreRef.PROTOCOL_WORKSPACE,
                "Test_" + System.nanoTime());
        rootNodeRef = nodeService.getRootNode(storeRef);
    }
    
    @Override
    protected void onTearDownInTransaction()
    {
    }

    /**
     * Loads the test model required for building the node graphs
     */
    public static DictionaryService loadModel(ApplicationContext applicationContext)
    {
        DictionaryDAO dictionaryDao = (DictionaryDAO) applicationContext.getBean("dictionaryDAO");
        
        // load the system model
        ClassLoader cl = PerformanceNodeServiceTest.class.getClassLoader();
        InputStream modelStream = cl.getResourceAsStream("alfresco/model/contentModel.xml");
        assertNotNull(modelStream);
        M2Model model = M2Model.createModel(modelStream);
        dictionaryDao.putModel(model);
        
        // load the test model
        modelStream = cl.getResourceAsStream("org/alfresco/repo/node/BaseNodeServiceTest_model.xml");
        assertNotNull(modelStream);
        model = M2Model.createModel(modelStream);
        dictionaryDao.putModel(model);
        
        DictionaryComponent dictionary = new DictionaryComponent();
        dictionary.setDictionaryDAO(dictionaryDao);
        
        return dictionary;
    }
    
    /**
     * Usually just implemented by fetching the bean directly from the bean factory,
     * for example:
     * <p>
     * <pre>
     *      return (NodeService) applicationContext.getBean("dbNodeService");
     * </pre>
     * 
     * @return Returns the implementation of <code>NodeService</code> to be
     *      used for this test
     */
    protected NodeService getNodeService()
    {
        return (NodeService) applicationContext.getBean("dbNodeService");
    }
    
    public void testSetUp() throws Exception
    {
        assertNotNull("StoreService not set", nodeService);
        assertNotNull("NodeService not set", nodeService);
        assertNotNull("rootNodeRef not created", rootNodeRef);
    }
    
    public void xtestPerformanceNodeService() throws Exception
    {
        startTime = System.currentTimeMillis();
        
        UserTransaction txn = null;
        txn = txnService.getUserTransaction();  
        try
        {
            txn.begin();
            
            buildNodeChildren(rootNodeRef, 1);
            
            txn.commit();
        }
        catch (Throwable exception)
        {
            try
            {
                // Roll back the exception
                if (txn.getStatus() == Status.STATUS_ACTIVE)
                {
                    txn.rollback();
                }
            }
            catch (Throwable rollbackException)
            {
                // just dump the exception - we are already in a failure state
                logger.error("Error rolling back transaction", rollbackException);
            }
            
            // Re-throw the exception
            if (exception instanceof RuntimeException)
            {
                throw (RuntimeException) exception;
            }
            else
            {
                throw new RuntimeException("Error during execution of transaction.", exception);
            }
        }
        
        long endTime = System.currentTimeMillis();
        
        System.out.println("Built " + nodeCount + " nodes in " + (endTime-startTime) + "ms");
    }
    
    public void buildNodeChildren(NodeRef parent, int level)
    {
        for (int i=0; i<CHILD_COUNT; i++)
        {
            ChildAssociationRef assocRef = this.nodeService.createNode(
                    parent, ASSOC_QNAME_CHILDREN, QName.createQName(NAMESPACE, "child" + i), TYPE_QNAME_TEST);
           
            nodeCount++;
            
            NodeRef childRef = assocRef.getChildRef();
             
            this.nodeService.setProperty(childRef,
                 ContentModel.PROP_NAME, "node" + level + "_" + i);
            
            for (int j = 0; j < PROPERTY_COUNT; j++)
            {
                this.nodeService.setProperty(
                      childRef, QName.createQName(NAMESPACE, "string" + j), level + "_" + i + "_" + j);
                
                ContentWriter writer = this.contentService.getWriter(
                      childRef, QName.createQName(NAMESPACE, "content" + j), true);
                
                writer.setMimetype("text/plain");
                writer.putContent( level + "_" + i + "_" + j );
            }
            
            if (nodeCount % FLUSH == 0)
            {
               System.out.println("Flushing transaction cache at nodecount: " + nodeCount); 
               System.out.println("At time index " + (System.currentTimeMillis() - startTime) + "ms");
               AlfrescoTransactionSupport.flush();
            }
            
            if (level <= CHILD_COUNT)
            {
                buildNodeChildren(childRef, level + 1);
            }
        }
    }
    private final static int CHILD_COUNT = 5;
    private final static int PROPERTY_COUNT = 10;
    private final static int FLUSH = 2000;
    
    private long startTime;
    
    private int countNodesById(NodeRef nodeRef)
    {
        String query =
                "select count(node.key.guid)" +
                " from " +
                NodeImpl.class.getName() + " node" +
                " where node.key.guid = ?";
        Session session = getSession();
        List results = session.createQuery(query)
            .setString(0, nodeRef.getId())
            .list();
        Integer count = (Integer) results.get(0);
        return count.intValue();
    }
    
    /**
     * @return Returns a reference to the created store
     */
    private StoreRef createStore() throws Exception
    {
        StoreRef storeRef = nodeService.createStore(
                StoreRef.PROTOCOL_WORKSPACE,
                getName() + "_" + System.nanoTime());
        assertNotNull("No reference returned", storeRef);
        // done
        return storeRef;
    }
}
