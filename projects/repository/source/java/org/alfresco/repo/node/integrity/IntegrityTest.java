package org.alfresco.repo.node.integrity;

import java.io.InputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.transaction.UserTransaction;

import junit.framework.TestCase;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.dictionary.DictionaryDAO;
import org.alfresco.repo.dictionary.M2Model;
import org.alfresco.repo.node.BaseNodeServiceTest;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.namespace.QName;
import org.alfresco.service.transaction.TransactionService;
import org.alfresco.util.ApplicationContextHelper;
import org.springframework.context.ApplicationContext;

/**
 * Attempts to build faulty node structures in order to test integrity.
 * <p>
 * The entire application context is loaded as is, but the integrity fail-
 * mode is set to throw an exception.
 * 
 * @author Derek Hulley
 */
public class IntegrityTest extends TestCase
{
    public static final String NAMESPACE = "http://www.alfresco.org/test/IntegrityTest";
    public static final String TEST_PREFIX = "test";
    public static final QName TYPE_QNAME_TEST_FOLDER = QName.createQName(NAMESPACE, "folder");
    public static final QName ASPECT_QNAME_TEST_TITLED = QName.createQName(NAMESPACE, "titled");
    public static final QName PROP_QNAME_TEST_NAME = QName.createQName(NAMESPACE, "name");
    public static final QName PROP_QNAME_TEST_TITLE = QName.createQName(NAMESPACE, "title");
    
    private static ApplicationContext ctx;
    static
    {
        ctx = ApplicationContextHelper.getApplicationContext();
    }
    
    private IntegrityChecker integrityChecker;
    private ServiceRegistry serviceRegistry;
    private NodeService nodeService;
    private NodeRef rootNodeRef;
    private UserTransaction txn;
    
    public void setUp() throws Exception
    {
        DictionaryDAO dictionaryDao = (DictionaryDAO) ctx.getBean("dictionaryDAO");
        ClassLoader cl = BaseNodeServiceTest.class.getClassLoader();
        // load the test model
        InputStream modelStream = cl.getResourceAsStream("org/alfresco/repo/node/integrity/IntegrityTest_model.xml");
        assertNotNull(modelStream);
        M2Model model = M2Model.createModel(modelStream);
        dictionaryDao.putModel(model);

        integrityChecker = (IntegrityChecker) ctx.getBean("integrityChecker");
        integrityChecker.setEnabled(true);
        integrityChecker.setFailOnViolation(true);
        integrityChecker.setTraceOn(false);
        
        serviceRegistry = (ServiceRegistry) ctx.getBean(ServiceRegistry.SERVICE_REGISTRY);
        nodeService = serviceRegistry.getNodeService();
        StoreRef storeRef = new StoreRef(StoreRef.PROTOCOL_WORKSPACE, getName());
        if (!nodeService.exists(storeRef))
        {
            nodeService.createStore(storeRef.getProtocol(), storeRef.getIdentifier());
        }
        rootNodeRef = nodeService.getRootNode(storeRef);
        // begin a transaction
        TransactionService transactionService = serviceRegistry.getTransactionService();
        txn = transactionService.getUserTransaction();
        txn.begin();
    }
    
    public void tearDown() throws Exception
    {
        txn.rollback();
    }
    
    public void testSetUp() throws Exception
    {
        assertNotNull("Static IntegrityChecker not created", integrityChecker);
    }
    
    /**
     * Create a new node without a mandatory property
     */
    public void testMissingProperty() throws Exception
    {
        // create with a missing property
        ChildAssociationRef assocRef = nodeService.createNode(
                rootNodeRef,
                ContentModel.ASSOC_CHILDREN,
                QName.createQName(NAMESPACE, "abc"),
                TYPE_QNAME_TEST_FOLDER);
        NodeRef nodeRef = assocRef.getChildRef();
        
        // check integrity
        try
        {
            integrityChecker.checkIntegrity();
            fail("Failed to detect missing properties");
        }
        catch (IntegrityException e)
        {
            // expected - check that it has the correct errors
            assertEquals("Incorrect number of error records", 2, e.getRecords().size());
        }
        
        // repeat the process - since integrity MUST remove all the events,
        // there should be no errors
        integrityChecker.checkIntegrity();
        
        // add mandatory properties
        Map<QName, Serializable> properties = new HashMap<QName, Serializable>(5);
        properties.put(PROP_QNAME_TEST_NAME, "A name");
        properties.put(PROP_QNAME_TEST_TITLE, "A title");
        nodeService.setProperties(nodeRef, properties);
        // it should succeed
        integrityChecker.checkIntegrity();
        
        // remove one of the properties
        properties.remove(PROP_QNAME_TEST_NAME);
        nodeService.setProperties(nodeRef, properties);
        try
        {
            integrityChecker.checkIntegrity();
            fail("Failed to detect missing property");
        }
        catch (IntegrityException e)
        {
            // expected - check that it has the correct errors
            assertEquals("Incorrect number of error records", 1, e.getRecords().size());
        }
        
        // delete the node
        nodeService.deleteNode(nodeRef);
        // it should succeed this time
        integrityChecker.checkIntegrity();
    }
}
