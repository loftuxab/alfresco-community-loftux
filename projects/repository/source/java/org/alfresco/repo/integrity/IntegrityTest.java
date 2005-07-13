package org.alfresco.repo.integrity;

import java.io.InputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.transaction.UserTransaction;

import junit.framework.TestCase;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.dictionary.impl.DictionaryDAO;
import org.alfresco.repo.dictionary.impl.M2Model;
import org.alfresco.repo.node.BaseNodeServiceTest;
import org.alfresco.repo.transaction.AlfrescoTransactionManager;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.namespace.QName;
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

    private IntegrityService integrityService;
    private ServiceRegistry serviceRegistry;
    private NodeService nodeService;
    private NodeRef rootNodeRef;
    private UserTransaction txn;
    private String txnId;
    
    static
    {
        ctx = ApplicationContextHelper.getApplicationContext();
    }
    
    public void setUp() throws Exception
    {
        DictionaryDAO dictionaryDao = (DictionaryDAO) ctx.getBean("dictionaryDAO");
        ClassLoader cl = BaseNodeServiceTest.class.getClassLoader();
        // load the test model
        InputStream modelStream = cl.getResourceAsStream("org/alfresco/repo/integrity/IntegrityTest_model.xml");
        assertNotNull(modelStream);
        M2Model model = M2Model.createModel(modelStream);
        dictionaryDao.putModel(model);

        integrityService = (IntegrityService) ctx.getBean("integrityService");
        integrityService.setEnabled(true);
        integrityService.setFailOnViolation(true);
        integrityService.setTraceOn(true);
        
        serviceRegistry = (ServiceRegistry) ctx.getBean("serviceRegistry");
        nodeService = serviceRegistry.getNodeService();
        StoreRef storeRef = new StoreRef(StoreRef.PROTOCOL_WORKSPACE, getName());
        if (!nodeService.exists(storeRef))
        {
            nodeService.createStore(storeRef.getProtocol(), storeRef.getIdentifier());
        }
        rootNodeRef = nodeService.getRootNode(storeRef);
        // begin a transaction
        txn = serviceRegistry.getUserTransaction();
        txn.begin();
        txnId = AlfrescoTransactionManager.getTransactionId();
    }
    
    public void tearDown() throws Exception
    {
        txn.rollback();
    }
    
    public void testSetUp() throws Exception
    {
        assertNotNull("Static IntegrityService not created", integrityService);
    }
    
    /**
     * Create a node with a mandatory aspect and then remove it
     */
    public void testMissingAspect() throws Exception
    {
        // create node with mandatory aspect
        nodeService.createNode(
                rootNodeRef,
                ContentModel.ASSOC_CHILDREN,
                QName.createQName(NAMESPACE, "abc"),
                TYPE_QNAME_TEST_FOLDER);
        // check that it has the mandatory aspect
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
            integrityService.checkIntegrity(txnId);
            fail("Failed to detect missing properties");
        }
        catch (IntegrityException e)
        {
            // expected - check that it has the correct errors
            assertEquals("Incorrect number of error records", 2, e.getRecords().size());
        }
        
        // add mandatory properties
        Map<QName, Serializable> properties = new HashMap<QName, Serializable>(5);
        properties.put(PROP_QNAME_TEST_NAME, "A name");
        properties.put(PROP_QNAME_TEST_TITLE, "A title");
        nodeService.setProperties(nodeRef, properties);
        // it should succeed
        integrityService.checkIntegrity(txnId);
        
        // remove one of the properties
        properties.remove(PROP_QNAME_TEST_NAME);
        nodeService.setProperties(nodeRef, properties);
        try
        {
            integrityService.checkIntegrity(txnId);
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
        integrityService.checkIntegrity(txnId);
    }
}
