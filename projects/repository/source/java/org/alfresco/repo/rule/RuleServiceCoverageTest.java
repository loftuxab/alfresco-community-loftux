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
package org.alfresco.repo.rule;

import java.io.File;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.transaction.UserTransaction;

import junit.framework.TestCase;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.action.evaluator.InCategoryEvaluator;
import org.alfresco.repo.action.evaluator.MatchTextEvaluator;
import org.alfresco.repo.action.evaluator.NoConditionEvaluator;
import org.alfresco.repo.action.executer.AddFeaturesActionExecuter;
import org.alfresco.repo.action.executer.CheckInActionExecuter;
import org.alfresco.repo.action.executer.CheckOutActionExecuter;
import org.alfresco.repo.action.executer.CopyActionExecuter;
import org.alfresco.repo.action.executer.ImageTransformActionExecuter;
import org.alfresco.repo.action.executer.LinkCategoryActionExecuter;
import org.alfresco.repo.action.executer.MailActionExecuter;
import org.alfresco.repo.action.executer.MoveActionExecuter;
import org.alfresco.repo.action.executer.SimpleWorkflowActionExecuter;
import org.alfresco.repo.action.executer.TransformActionExecuter;
import org.alfresco.repo.content.MimetypeMap;
import org.alfresco.repo.content.transform.AbstractContentTransformerTest;
import org.alfresco.repo.dictionary.impl.DictionaryDAO;
import org.alfresco.repo.dictionary.impl.M2Aspect;
import org.alfresco.repo.dictionary.impl.M2Model;
import org.alfresco.repo.dictionary.impl.M2Property;
import org.alfresco.repo.security.authentication.AuthenticationService;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.action.ActionService;
import org.alfresco.service.cmr.coci.CheckOutCheckInService;
import org.alfresco.service.cmr.dictionary.PropertyTypeDefinition;
import org.alfresco.service.cmr.lock.LockService;
import org.alfresco.service.cmr.lock.LockStatus;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.ContentWriter;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.rule.Rule;
import org.alfresco.service.cmr.rule.RuleService;
import org.alfresco.service.cmr.rule.RuleServiceException;
import org.alfresco.service.cmr.rule.RuleType;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.TestWithUserUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.util.StopWatch;

/**
 * @author Roy Wetherall
 */
public class RuleServiceCoverageTest extends TestCase
{
	/**
	 * Application context used during the test
	 */
	static ApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:alfresco/application-context.xml");
	
	/**
	 * Services used during the tests
	 */
    private RuleService ruleService;
    private NodeService nodeService;
    private StoreRef testStoreRef;
    private NodeRef rootNodeRef;
    private NodeRef nodeRef;
    private CheckOutCheckInService cociService;
    private LockService lockService;
	private ContentService contentService;
	private ServiceRegistry serviceRegistry;
    private DictionaryDAO dictionaryDAO;
    private AuthenticationService authenticationService;
    private ActionService actionService;

    /**
     * Category related values
     */
    private static final String TEST_NAMESPACE = "http://www.alfresco.org/test/rulesystemtest";
    private static final QName CAT_PROP_QNAME = QName.createQName(TEST_NAMESPACE, "region");
    private QName regionCategorisationQName;
    private NodeRef catContainer;
    private NodeRef catRoot;
    private NodeRef catRBase;
    private NodeRef catROne;
    private NodeRef catRTwo;
    private NodeRef catRThree;
    
    /**
     * Standard content text
     */
    private static final String STANDARD_TEXT_CONTENT = "standardTextContent";

    /**
     * Test user name and password
     */
    private static final String USER_NAME = "userName";
    private static final String PWD = "password";  
    
	/**
	 * Setup method
	 */
	@Override
    protected void setUp() throws Exception 
    {
        // Get the required services
		this.serviceRegistry = (ServiceRegistry)applicationContext.getBean("serviceRegistry");
		this.nodeService = (NodeService)applicationContext.getBean("nodeService");
        this.ruleService = (RuleService)applicationContext.getBean("ruleService");
        this.cociService = (CheckOutCheckInService)applicationContext.getBean("checkOutCheckInService");
        this.lockService = (LockService)applicationContext.getBean("lockService");
		this.contentService = (ContentService)applicationContext.getBean("contentService");
        this.dictionaryDAO = (DictionaryDAO)applicationContext.getBean("dictionaryDAO");
        this.authenticationService = (AuthenticationService)applicationContext.getBean("authenticationService");
        this.actionService = (ActionService)applicationContext.getBean("actionService");
        
        this.testStoreRef = this.nodeService.createStore(StoreRef.PROTOCOL_WORKSPACE, "Test_" + System.currentTimeMillis());
        this.rootNodeRef = this.nodeService.getRootNode(this.testStoreRef);
        
        // Create the node used for tests
        this.nodeRef = this.nodeService.createNode(
                this.rootNodeRef,
				QName.createQName(NamespaceService.ALFRESCO_URI, "children"),
                QName.createQName(NamespaceService.ALFRESCO_URI, "children"),
                ContentModel.TYPE_CONTAINER).getChildRef();
        
        // Create categories used in tests
        createTestCategories();
        
        // Create and authenticate the user used in the tests
        TestWithUserUtils.createUser(USER_NAME, PWD, this.rootNodeRef, this.nodeService, this.authenticationService);
        TestWithUserUtils.authenticateUser(USER_NAME, PWD, this.rootNodeRef, this.authenticationService);        
    }
    
	/**
	 * Create the categories used in the tests
	 */
    private void createTestCategories()
    {
        // Create the test model
        M2Model model = M2Model.createModel("test:rulecategory");
        model.createNamespace(TEST_NAMESPACE, "test");
        model.createImport(NamespaceService.ALFRESCO_DICTIONARY_URI, "d");
        model.createImport(NamespaceService.ALFRESCO_URI, "alf");
        
        // Create the region category
        regionCategorisationQName = QName.createQName(TEST_NAMESPACE, "Region");
        M2Aspect generalCategorisation = model.createAspect("test:" + regionCategorisationQName.getLocalName());
        generalCategorisation.setParentName("alf:" + ContentModel.ASPECT_CLASSIFIABLE.getLocalName());
        M2Property genCatProp = generalCategorisation.createProperty("test:region");
        genCatProp.setIndexed(true);
        genCatProp.setIndexedAtomically(true);
        genCatProp.setMandatory(true);
        genCatProp.setMultiValued(true);
        genCatProp.setStoredInIndex(true);
        genCatProp.setTokenisedInIndex(true);
        genCatProp.setType("d:" + PropertyTypeDefinition.CATEGORY.getLocalName());        

        // Save the mode
        dictionaryDAO.putModel(model);
        
        // Create the category value container and root
        catContainer = nodeService.createNode(rootNodeRef, ContentModel.ASSOC_CHILDREN, QName.createQName(NamespaceService.ALFRESCO_URI, "categoryContainer"), ContentModel.TYPE_CONTAINER).getChildRef();
        catRoot = nodeService.createNode(catContainer, ContentModel.ASSOC_CHILDREN, QName.createQName(NamespaceService.ALFRESCO_URI, "categoryRoot"), ContentModel.TYPE_CATEGORYROOT).getChildRef();

        // Create the category values
        catRBase = nodeService.createNode(catRoot, ContentModel.ASSOC_CATEGORIES, QName.createQName(TEST_NAMESPACE, "Region"), ContentModel.TYPE_CATEGORY).getChildRef();
        catROne = nodeService.createNode(catRBase, ContentModel.ASSOC_SUBCATEGORIES, QName.createQName(TEST_NAMESPACE, "Europe"), ContentModel.TYPE_CATEGORY).getChildRef();
        catRTwo = nodeService.createNode(catRBase, ContentModel.ASSOC_SUBCATEGORIES, QName.createQName(TEST_NAMESPACE, "RestOfWorld"), ContentModel.TYPE_CATEGORY).getChildRef();
        catRThree = nodeService.createNode(catRTwo, ContentModel.ASSOC_SUBCATEGORIES, QName.createQName(TEST_NAMESPACE, "US"), ContentModel.TYPE_CATEGORY).getChildRef();
    }

    /**
     * Test:
     *          rule type:  inbound
     *          condition:  no-condition()
     *          action:     add-features(
     *                          aspect-name = versionable)
     */
    public void testAddFeaturesAction()
    {
        this.ruleService.makeActionable(this.nodeRef);
        
        this.nodeService.addAspect(this.nodeRef, ContentModel.ASPECT_LOCKABLE, null);
        
        RuleType ruleType = this.ruleService.getRuleType("inbound");
        
        Map<String, Serializable> params = new HashMap<String, Serializable>(1);
        params.put("aspect-name", ContentModel.ASPECT_VERSIONABLE);        
        
        Rule rule = this.ruleService.createRule(ruleType);
        rule.addActionCondition(NoConditionEvaluator.NAME, null);
        rule.addAction(AddFeaturesActionExecuter.NAME, params);
        
        this.ruleService.saveRule(this.nodeRef, rule);

        NodeRef newNodeRef = this.nodeService.createNode(
                this.nodeRef,
                QName.createQName(NamespaceService.ALFRESCO_URI, "children"),                
                QName.createQName(NamespaceService.ALFRESCO_URI, "children"),
                ContentModel.TYPE_CONTENT,
                getContentProperties()).getChildRef();         
        addContentToNode(newNodeRef);
        assertTrue(this.nodeService.hasAspect(newNodeRef, ContentModel.ASPECT_VERSIONABLE));   
        
        Map<QName, Serializable> aspectProps = new HashMap<QName, Serializable>();
        aspectProps.put(ContentModel.PROP_APPROVE_STEP, "approveStep");
        aspectProps.put(ContentModel.PROP_APPROVE_MOVE, false);
        
        Map<String, Serializable> params2 = new HashMap<String, Serializable>(2);
        params2.put(AddFeaturesActionExecuter.PARAM_ASPECT_NAME, ContentModel.ASPECT_SIMPLE_WORKFLOW);
        params2.put(AddFeaturesActionExecuter.PARAM_ASPECT_PROPERTIES, (Serializable)aspectProps);
        
        // Test that rule can be updated and execute correctly
        rule.removeAllActions();
        rule.addAction(AddFeaturesActionExecuter.NAME, params2);
        this.ruleService.saveRule(this.nodeRef, rule);
        
        NodeRef newNodeRef2 = this.nodeService.createNode(
                this.nodeRef,
                QName.createQName(NamespaceService.ALFRESCO_URI, "children"),                
                QName.createQName(NamespaceService.ALFRESCO_URI, "children"),
                ContentModel.TYPE_CONTENT,
                getContentProperties()).getChildRef();           
        addContentToNode(newNodeRef2);
        assertTrue(this.nodeService.hasAspect(newNodeRef2, ContentModel.ASPECT_SIMPLE_WORKFLOW));
        assertEquals("approveStep", this.nodeService.getProperty(newNodeRef2, ContentModel.PROP_APPROVE_STEP));
        assertEquals(false, this.nodeService.getProperty(newNodeRef2, ContentModel.PROP_APPROVE_MOVE));
        
        // System.out.println(NodeStoreInspector.dumpNodeStore(this.nodeService, this.testStoreRef));        
    }   
	
	private Map<QName, Serializable> getContentProperties()
    {
        Map<QName, Serializable> properties = new HashMap<QName, Serializable>(1);
        properties.put(ContentModel.PROP_MIME_TYPE, MimetypeMap.MIMETYPE_TEXT_PLAIN);
        return properties;
    }

    /**
     * Test:
     *          rule type:  inbound
     *          condition:  no-condition
     *          action:     simple-workflow
     */
    public void testSimpleWorkflowAction()
    {
        this.ruleService.makeActionable(this.nodeRef);
        
        this.nodeService.addAspect(this.nodeRef, ContentModel.ASPECT_LOCKABLE, null);
        
        RuleType ruleType = this.ruleService.getRuleType("inbound");
        
        Map<String, Serializable> params = new HashMap<String, Serializable>(1);
        params.put(SimpleWorkflowActionExecuter.PARAM_APPROVE_STEP, "approveStep");
		params.put(SimpleWorkflowActionExecuter.PARAM_APPROVE_FOLDER, this.rootNodeRef);
		params.put(SimpleWorkflowActionExecuter.PARAM_APPROVE_MOVE, true);
		params.put(SimpleWorkflowActionExecuter.PARAM_REJECT_STEP, "rejectStep");
		params.put(SimpleWorkflowActionExecuter.PARAM_REJECT_FOLDER, this.rootNodeRef);
		params.put(SimpleWorkflowActionExecuter.PARAM_REJECT_MOVE, false);
        
        Rule rule = this.ruleService.createRule(ruleType);
        rule.addActionCondition(NoConditionEvaluator.NAME, null);
        rule.addAction(SimpleWorkflowActionExecuter.NAME, params);
        
        this.ruleService.saveRule(this.nodeRef, rule);
				
		NodeRef newNodeRef = this.nodeService.createNode(
                this.nodeRef,
                QName.createQName(NamespaceService.ALFRESCO_URI, "children"),                
                QName.createQName(NamespaceService.ALFRESCO_URI, "children"),
                ContentModel.TYPE_CONTENT,
                getContentProperties()).getChildRef();     
		addContentToNode(newNodeRef);
        
		assertTrue(this.nodeService.hasAspect(newNodeRef, ContentModel.ASPECT_SIMPLE_WORKFLOW));   
		assertEquals("approveStep", this.nodeService.getProperty(newNodeRef, ContentModel.PROP_APPROVE_STEP));
		assertEquals(this.rootNodeRef, this.nodeService.getProperty(newNodeRef, ContentModel.PROP_APPROVE_FOLDER));
		assertTrue(((Boolean)this.nodeService.getProperty(newNodeRef, ContentModel.PROP_APPROVE_MOVE)).booleanValue());
		assertTrue(this.nodeService.hasAspect(newNodeRef, ContentModel.ASPECT_SIMPLE_WORKFLOW));   
		assertEquals("rejectStep", this.nodeService.getProperty(newNodeRef, ContentModel.PROP_REJECT_STEP));
		assertEquals(this.rootNodeRef, this.nodeService.getProperty(newNodeRef, ContentModel.PROP_REJECT_FOLDER));
		assertFalse(((Boolean)this.nodeService.getProperty(newNodeRef, ContentModel.PROP_REJECT_MOVE)).booleanValue());
		
        // System.out.println(NodeStoreInspector.dumpNodeStore(this.nodeService, this.testStoreRef));
    } 
    
    /**
     * Test:
     *          rule type:  inbound
     *          condition:  in-category
     *          action:     add-feature           
     */
    public void testInCategoryCondition()
    {
        try
        {
            this.ruleService.makeActionable(this.nodeRef);
            
            RuleType ruleType = this.ruleService.getRuleType("inbound");
            
            Map<String, Serializable> params = new HashMap<String, Serializable>(1);
            params.put(InCategoryEvaluator.PARAM_CATEGORY_ASPECT, this.regionCategorisationQName);
            params.put(InCategoryEvaluator.PARAM_CATEGORY_VALUE, this.catROne);
            
            Map<String, Serializable> params2 = new HashMap<String, Serializable>(1);
            params2.put("aspect-name", ContentModel.ASPECT_VERSIONABLE); 
            
            Rule rule = this.ruleService.createRule(ruleType);
            rule.addActionCondition(InCategoryEvaluator.NAME, params);
            rule.addAction(AddFeaturesActionExecuter.NAME, params2);
            
            this.ruleService.saveRule(this.nodeRef, rule);
                    
            // Check rule does not get fired when a node without the aspect is added
            NodeRef newNodeRef2 = this.nodeService.createNode(
                    this.nodeRef,
                    QName.createQName(NamespaceService.ALFRESCO_URI, "children"),                
                    QName.createQName(NamespaceService.ALFRESCO_URI, "noAspect"),
                    ContentModel.TYPE_CONTENT,
                    getContentProperties()).getChildRef(); 
            addContentToNode(newNodeRef2);
            assertFalse(this.nodeService.hasAspect(newNodeRef2, ContentModel.ASPECT_VERSIONABLE));
            
            // Check rule gets fired when node contains category value
            UserTransaction tx = serviceRegistry.getUserTransaction();
            tx.begin();
            NodeRef newNodeRef = this.nodeService.createNode(
                    this.nodeRef,
                    QName.createQName(NamespaceService.ALFRESCO_URI, "children"),                
                    QName.createQName(NamespaceService.ALFRESCO_URI, "hasAspectAndValue"),
                    ContentModel.TYPE_CONTENT,
                    getContentProperties()).getChildRef();
            addContentToNode(newNodeRef);
            Map<QName, Serializable> catProps = new HashMap<QName, Serializable>();
            catProps.put(CAT_PROP_QNAME, this.catROne);
            this.nodeService.addAspect(newNodeRef, this.regionCategorisationQName, catProps);
            tx.commit();
            assertTrue(this.nodeService.hasAspect(newNodeRef, ContentModel.ASPECT_VERSIONABLE));  
            
            // Check rule does not get fired when the node has the incorrect category value
            UserTransaction tx3 = serviceRegistry.getUserTransaction();
            tx3.begin();
            NodeRef newNodeRef3 = this.nodeService.createNode(
                    this.nodeRef,
                    QName.createQName(NamespaceService.ALFRESCO_URI, "children"),                
                    QName.createQName(NamespaceService.ALFRESCO_URI, "hasAspectAndValue"),
                    ContentModel.TYPE_CONTENT,
                    getContentProperties()).getChildRef();  
            addContentToNode(newNodeRef3);
            Map<QName, Serializable> catProps3 = new HashMap<QName, Serializable>();
            catProps3.put(CAT_PROP_QNAME, this.catRTwo);
            this.nodeService.addAspect(newNodeRef3, this.regionCategorisationQName, catProps3);
            tx3.commit();
            assertFalse(this.nodeService.hasAspect(newNodeRef3, ContentModel.ASPECT_VERSIONABLE)); 
            
            //System.out.println(NodeStoreInspector.dumpNodeStore(this.nodeService, this.testStoreRef));
        }
        catch (Exception exception)
        {
            throw new RuntimeException(exception);
        }
    }
    
    /**
     * Test:
     *          rule type:  inbound
     *          condition:  no-condition
     *          action:     link-category  
     */
    public void testLinkCategoryAction()
    {        
        this.ruleService.makeActionable(this.nodeRef);
        
        RuleType ruleType = this.ruleService.getRuleType("inbound");
        
        Map<String, Serializable> params = new HashMap<String, Serializable>(1);
        params.put(LinkCategoryActionExecuter.PARAM_CATEGORY_ASPECT, this.regionCategorisationQName);
        params.put(LinkCategoryActionExecuter.PARAM_CATEGORY_VALUE, this.catROne); 
        
        Rule rule = this.ruleService.createRule(ruleType);
        rule.addActionCondition(NoConditionEvaluator.NAME, null);
        rule.addAction(LinkCategoryActionExecuter.NAME, params);
        
        this.ruleService.saveRule(this.nodeRef, rule);
                
        NodeRef newNodeRef2 = this.nodeService.createNode(
                this.nodeRef,
                QName.createQName(NamespaceService.ALFRESCO_URI, "children"),                
                QName.createQName(NamespaceService.ALFRESCO_URI, "noAspect"),
                ContentModel.TYPE_CONTENT,
                getContentProperties()).getChildRef();
        addContentToNode(newNodeRef2);
        
        // Check that the category value has been set
        NodeRef setValue = (NodeRef)this.nodeService.getProperty(newNodeRef2, CAT_PROP_QNAME);
        assertNotNull(setValue);
        assertEquals(this.catROne, setValue);
}
        
    
    /**
     * Test:
     *          rule type:  inbound
     *          condition:  no-condition
     *          action:     mail
     *          
     * Note: this test will be removed from the standard list since it is not currently automated           
     */
    public void xtestMailAction()
    {
        this.ruleService.makeActionable(this.nodeRef);
        
        this.nodeService.addAspect(this.nodeRef, ContentModel.ASPECT_LOCKABLE, null);
        
        RuleType ruleType = this.ruleService.getRuleType("inbound");
        
        Map<String, Serializable> params = new HashMap<String, Serializable>(1);
        params.put(MailActionExecuter.PARAM_TO, "alfresco.test@gmail.com");
        params.put(MailActionExecuter.PARAM_SUBJECT, "Unit test");
        params.put(MailActionExecuter.PARAM_TEXT, "This is a test to check that the mail action is working.");
        
        Rule rule = this.ruleService.createRule(ruleType);
        rule.addActionCondition(NoConditionEvaluator.NAME, null);
        rule.addAction(MailActionExecuter.NAME, params);
        
        this.ruleService.saveRule(this.nodeRef, rule);
                
        this.nodeService.createNode(
                this.nodeRef,
                QName.createQName(NamespaceService.ALFRESCO_URI, "children"),                
                QName.createQName(NamespaceService.ALFRESCO_URI, "children"),
                ContentModel.TYPE_CONTENT,
                getContentProperties()).getChildRef();        
        
        // An email should appear in the recipients email
        
        // System.out.println(NodeStoreInspector.dumpNodeStore(this.nodeService, this.testStoreRef));
    }
    
    /**
     * Test:
     *          rule type:  inbound
     *          condition:  no-condition()
     *          action:     copy()
     */
    public void testCopyAction()
    {
        this.ruleService.makeActionable(this.nodeRef);
        
        RuleType ruleType = this.ruleService.getRuleType("inbound");
        
        Map<String, Serializable> params = new HashMap<String, Serializable>(1);
        params.put(MoveActionExecuter.PARAM_DESTINATION_FOLDER, this.rootNodeRef);
        params.put(MoveActionExecuter.PARAM_ASSOC_TYPE_QNAME, ContentModel.ASSOC_CHILDREN);
        params.put(MoveActionExecuter.PARAM_ASSOC_QNAME, QName.createQName(NamespaceService.ALFRESCO_URI, "copy"));
        
        Rule rule = this.ruleService.createRule(ruleType);
        rule.addActionCondition(NoConditionEvaluator.NAME, null);
        rule.addAction(CopyActionExecuter.NAME, params);
        
        this.ruleService.saveRule(this.nodeRef, rule);

        NodeRef newNodeRef = this.nodeService.createNode(
                this.nodeRef,
                QName.createQName(NamespaceService.ALFRESCO_URI, "children"),                
                QName.createQName(NamespaceService.ALFRESCO_URI, "origional"),
                ContentModel.TYPE_CONTENT,
                getContentProperties()).getChildRef(); 
        addContentToNode(newNodeRef);
        
        //System.out.println(NodeStoreInspector.dumpNodeStore(this.nodeService, this.testStoreRef));
        
        // Check that the created node is still there
        List<ChildAssociationRef> origRefs = this.nodeService.getChildAssocs(
                this.nodeRef, 
                QName.createQName(NamespaceService.ALFRESCO_URI, "origional"));
        assertNotNull(origRefs);
        assertEquals(1, origRefs.size());
        NodeRef origNodeRef = origRefs.get(0).getChildRef();
        assertEquals(newNodeRef, origNodeRef);

        // Check that the created node has been copied
        List<ChildAssociationRef> copyChildAssocRefs = this.nodeService.getChildAssocs(
                                                    this.rootNodeRef, 
                                                    QName.createQName(NamespaceService.ALFRESCO_URI, "copy"));
        assertNotNull(copyChildAssocRefs);
        assertEquals(1, copyChildAssocRefs.size());
        NodeRef copyNodeRef = copyChildAssocRefs.get(0).getChildRef();
        assertTrue(this.nodeService.hasAspect(copyNodeRef, ContentModel.ASPECT_COPIEDFROM));
        NodeRef source = (NodeRef)this.nodeService.getProperty(copyNodeRef, ContentModel.PROP_COPY_REFERENCE);
        assertEquals(newNodeRef, source);
        
        // TODO test deep copy !!
    }
	
	/**
     * Test:
     *          rule type:  inbound
     *          condition:  no-condition()
     *          action:     transform()
     */
    public void testTransformAction()
    {
		try
		{
	        this.ruleService.makeActionable(this.nodeRef);
	        
	        RuleType ruleType = this.ruleService.getRuleType("inbound");
	        
	        Map<String, Serializable> params = new HashMap<String, Serializable>(1);
			params.put(TransformActionExecuter.PARAM_MIME_TYPE, MimetypeMap.MIMETYPE_TEXT_PLAIN);
	        params.put(TransformActionExecuter.PARAM_DESTINATION_FOLDER, this.rootNodeRef);
	        params.put(TransformActionExecuter.PARAM_ASSOC_TYPE_QNAME, ContentModel.ASSOC_CHILDREN);
	        params.put(TransformActionExecuter.PARAM_ASSOC_QNAME, QName.createQName(NamespaceService.ALFRESCO_URI, "transformed"));
	        
	        Rule rule = this.ruleService.createRule(ruleType);
	        rule.addActionCondition(NoConditionEvaluator.NAME, null);
	        rule.addAction(TransformActionExecuter.NAME, params);
	        
	        this.ruleService.saveRule(this.nodeRef, rule);
	
	        UserTransaction tx = serviceRegistry.getUserTransaction();
			tx.begin();
			
			Map<QName, Serializable> props =new HashMap<QName, Serializable>(1);
	        props.put(ContentModel.PROP_NAME, "test.xls");
			props.put(ContentModel.PROP_MIME_TYPE, MimetypeMap.MIMETYPE_EXCEL);
			
			// Create the node at the root
	        NodeRef newNodeRef = this.nodeService.createNode(
	                this.nodeRef,
	                QName.createQName(NamespaceService.ALFRESCO_URI, "children"),                
	                QName.createQName(NamespaceService.ALFRESCO_URI, "origional"),
	                ContentModel.TYPE_CONTENT,
	                props).getChildRef(); 
			
			// Set some content on the origional
			ContentWriter contentWriter = this.contentService.getUpdatingWriter(newNodeRef);
			File testFile = AbstractContentTransformerTest.loadQuickTestFile("xls");
			contentWriter.putContent(testFile);
			
			tx.commit();
	        
	        //System.out.println(NodeStoreInspector.dumpNodeStore(this.nodeService, this.testStoreRef));
	        
	        // Check that the created node is still there
	        List<ChildAssociationRef> origRefs = this.nodeService.getChildAssocs(
	                this.nodeRef, 
	                QName.createQName(NamespaceService.ALFRESCO_URI, "origional"));
	        assertNotNull(origRefs);
	        assertEquals(1, origRefs.size());
	        NodeRef origNodeRef = origRefs.get(0).getChildRef();
	        assertEquals(newNodeRef, origNodeRef);
	
	        // Check that the created node has been copied
	        List<ChildAssociationRef> copyChildAssocRefs = this.nodeService.getChildAssocs(
	                                                    this.rootNodeRef, 
	                                                    QName.createQName(NamespaceService.ALFRESCO_URI, "transformed"));
	        assertNotNull(copyChildAssocRefs);
	        assertEquals(1, copyChildAssocRefs.size());
	        NodeRef copyNodeRef = copyChildAssocRefs.get(0).getChildRef();
	        assertTrue(this.nodeService.hasAspect(copyNodeRef, ContentModel.ASPECT_COPIEDFROM));
	        NodeRef source = (NodeRef)this.nodeService.getProperty(copyNodeRef, ContentModel.PROP_COPY_REFERENCE);
	        assertEquals(newNodeRef, source);
	        
	        // Check the transformed content
			assertEquals(MimetypeMap.MIMETYPE_TEXT_PLAIN, this.nodeService.getProperty(copyNodeRef, ContentModel.PROP_MIME_TYPE));
			
		}
		catch (Exception exception)
		{
			throw new RuntimeException(exception);
		}
    }
    
    /**
     * Test image transformation
     *
     */
    public void testImageTransformAction()
    {
		try
		{
	        this.ruleService.makeActionable(this.nodeRef);
	        
	        RuleType ruleType = this.ruleService.getRuleType("inbound");
	        
	        Map<String, Serializable> params = new HashMap<String, Serializable>(1);
			params.put(ImageTransformActionExecuter.PARAM_DESTINATION_FOLDER, this.rootNodeRef);
	        params.put(ImageTransformActionExecuter.PARAM_ASSOC_TYPE_QNAME, ContentModel.ASSOC_CHILDREN);
	        params.put(TransformActionExecuter.PARAM_MIME_TYPE, MimetypeMap.MIMETYPE_IMAGE_JPEG);
	        params.put(ImageTransformActionExecuter.PARAM_ASSOC_QNAME, QName.createQName(NamespaceService.ALFRESCO_URI, "transformed"));
	        params.put(ImageTransformActionExecuter.PARAM_CONVERT_COMMAND, "-negate");
	        
	        Rule rule = this.ruleService.createRule(ruleType);
	        rule.addActionCondition(NoConditionEvaluator.NAME, null);
	        rule.addAction(ImageTransformActionExecuter.NAME, params);
	        
	        this.ruleService.saveRule(this.nodeRef, rule);
	
	        UserTransaction tx = serviceRegistry.getUserTransaction();
			tx.begin();
			
			Map<QName, Serializable> props =new HashMap<QName, Serializable>(1);
	        props.put(ContentModel.PROP_NAME, "test.gif");
			props.put(ContentModel.PROP_MIME_TYPE, MimetypeMap.MIMETYPE_IMAGE_GIF);
			
			// Create the node at the root
	        NodeRef newNodeRef = this.nodeService.createNode(
	                this.nodeRef,
	                QName.createQName(NamespaceService.ALFRESCO_URI, "children"),                
	                QName.createQName(NamespaceService.ALFRESCO_URI, "origional"),
	                ContentModel.TYPE_CONTENT,
	                props).getChildRef(); 
			
			// Set some content on the origional
			ContentWriter contentWriter = this.contentService.getUpdatingWriter(newNodeRef);
			File testFile = AbstractContentTransformerTest.loadQuickTestFile("gif");
			contentWriter.putContent(testFile);
			
			tx.commit();
	        
	        //System.out.println(NodeStoreInspector.dumpNodeStore(this.nodeService, this.testStoreRef));
	        
	        // Check that the created node is still there
	        List<ChildAssociationRef> origRefs = this.nodeService.getChildAssocs(
	                this.nodeRef, 
	                QName.createQName(NamespaceService.ALFRESCO_URI, "origional"));
	        assertNotNull(origRefs);
	        assertEquals(1, origRefs.size());
	        NodeRef origNodeRef = origRefs.get(0).getChildRef();
	        assertEquals(newNodeRef, origNodeRef);
	
	        // Check that the created node has been copied
	        List<ChildAssociationRef> copyChildAssocRefs = this.nodeService.getChildAssocs(
	                                                    this.rootNodeRef, 
	                                                    QName.createQName(NamespaceService.ALFRESCO_URI, "transformed"));
	        assertNotNull(copyChildAssocRefs);
	        assertEquals(1, copyChildAssocRefs.size());
	        NodeRef copyNodeRef = copyChildAssocRefs.get(0).getChildRef();
	        assertTrue(this.nodeService.hasAspect(copyNodeRef, ContentModel.ASPECT_COPIEDFROM));
	        NodeRef source = (NodeRef)this.nodeService.getProperty(copyNodeRef, ContentModel.PROP_COPY_REFERENCE);
	        assertEquals(newNodeRef, source);
		}
		catch (Exception exception)
		{
			throw new RuntimeException(exception);
		}
    }
	
    /**
     * Test:
     *          rule type:  inbound
     *          condition:  no-condition()
     *          action:     move()
     */
    public void testMoveAction()
    {
        this.ruleService.makeActionable(this.nodeRef);
        
        RuleType ruleType = this.ruleService.getRuleType("inbound");
        
        Map<String, Serializable> params = new HashMap<String, Serializable>(1);
        params.put(MoveActionExecuter.PARAM_DESTINATION_FOLDER, this.rootNodeRef);
        params.put(MoveActionExecuter.PARAM_ASSOC_TYPE_QNAME, ContentModel.ASSOC_CHILDREN);
        params.put(MoveActionExecuter.PARAM_ASSOC_QNAME, QName.createQName(NamespaceService.ALFRESCO_URI, "copy"));
        
        Rule rule = this.ruleService.createRule(ruleType);
        rule.addActionCondition(NoConditionEvaluator.NAME, null);
        rule.addAction(MoveActionExecuter.NAME, params);
        
        this.ruleService.saveRule(this.nodeRef, rule);
                
        NodeRef newNodeRef = this.nodeService.createNode(
                this.nodeRef,
                QName.createQName(NamespaceService.ALFRESCO_URI, "children"),                
                QName.createQName(NamespaceService.ALFRESCO_URI, "origional"),
                ContentModel.TYPE_CONTENT,
                getContentProperties()).getChildRef(); 
        addContentToNode(newNodeRef);
        
        //System.out.println(NodeStoreInspector.dumpNodeStore(this.nodeService, this.testStoreRef));
        
        // Check that the created node has been moved
        List<ChildAssociationRef> origRefs = this.nodeService.getChildAssocs(
                this.nodeRef, 
                QName.createQName(NamespaceService.ALFRESCO_URI, "origional"));
        assertNotNull(origRefs);
        assertEquals(0, origRefs.size());

        // Check that the created node is in the new location
        List<ChildAssociationRef> copyChildAssocRefs = this.nodeService.getChildAssocs(
                                                    this.rootNodeRef, 
                                                    QName.createQName(NamespaceService.ALFRESCO_URI, "copy"));
        assertNotNull(copyChildAssocRefs);
        assertEquals(1, copyChildAssocRefs.size());
        NodeRef movedNodeRef = copyChildAssocRefs.get(0).getChildRef();
        assertEquals(newNodeRef, movedNodeRef);
    }
    
    /**
     * Test:
     *          rule type:  inbound
     *          condition:  no-condition()
     *          action:     checkout()
     */
    public void testCheckOutAction()
    {
        this.ruleService.makeActionable(this.nodeRef);
        
        RuleType ruleType = this.ruleService.getRuleType("inbound");
        
        Rule rule = this.ruleService.createRule(ruleType);
        rule.addActionCondition(NoConditionEvaluator.NAME, null);
        rule.addAction(CheckOutActionExecuter.NAME, null);
        
        this.ruleService.saveRule(this.nodeRef, rule);
         
        NodeRef newNodeRef = null;
        UserTransaction tx = this.serviceRegistry.getUserTransaction();
        try
        {
        	tx.begin();     
        	
	        // Create a new node
	        newNodeRef = this.nodeService.createNode(
	                this.nodeRef,
	                QName.createQName(NamespaceService.ALFRESCO_URI, "children"),                
	                QName.createQName(NamespaceService.ALFRESCO_URI, "checkout"),
	                ContentModel.TYPE_CONTENT,
	                getContentProperties()).getChildRef();
	        addContentToNode(newNodeRef);
	        
	        tx.commit();
        }
        catch (Exception exception)
        {
        	throw new RuntimeException(exception);
        }
        
        //System.out.println(NodeStoreInspector.dumpNodeStore(this.nodeService, this.testStoreRef));
        
        // Check that the new node has been checked out
        List<ChildAssociationRef> children = this.nodeService.getChildAssocs(this.nodeRef);
        assertNotNull(children);
        assertEquals(3, children.size()); // includes the rule folder
        for (ChildAssociationRef child : children)
        {
            NodeRef childNodeRef = child.getChildRef();
            if (childNodeRef.equals(newNodeRef) == true)
            {
                // check that the node has been locked
                LockStatus lockStatus = this.lockService.getLockStatus(childNodeRef, TestWithUserUtils.getCurrentUserRef(this.authenticationService));
                assertEquals(LockStatus.LOCK_OWNER, lockStatus);
            }
            else if (this.nodeService.hasAspect(childNodeRef, ContentModel.ASPECT_WORKING_COPY) == true)
            {
                // assert that it is the working copy that relates to the origional node
                NodeRef copiedFromNodeRef = (NodeRef)this.nodeService.getProperty(childNodeRef, ContentModel.PROP_COPY_REFERENCE);
                assertEquals(newNodeRef, copiedFromNodeRef);
            }
        }
    }
    
    /**
     * Test:
     *          rule type:  inbound
     *          condition:  no-condition()
     *          action:     checkin()
     */
    public void testCheckInAction()
    {
        this.ruleService.makeActionable(this.nodeRef);
        
        RuleType ruleType = this.ruleService.getRuleType("inbound");
        
        Map<String, Serializable> params = new HashMap<String, Serializable>(1);
        params.put(CheckInActionExecuter.PARAM_DESCRIPTION, "The version description.");
        
        Rule rule = this.ruleService.createRule(ruleType);
        rule.addActionCondition(NoConditionEvaluator.NAME, null);
        rule.addAction(CheckInActionExecuter.NAME, params);
        
        this.ruleService.saveRule(this.nodeRef, rule);
         
        // Create a new node and check-it out
        NodeRef newNodeRef = this.nodeService.createNode(
                this.rootNodeRef,
                QName.createQName(NamespaceService.ALFRESCO_URI, "children"),                
                QName.createQName(NamespaceService.ALFRESCO_URI, "origional"),
                ContentModel.TYPE_CONTENT,
                getContentProperties()).getChildRef();
        NodeRef workingCopy = this.cociService.checkout(newNodeRef);
        
        // Move the working copy into the actionable folder
        this.nodeService.moveNode(
                workingCopy, 
                this.nodeRef, 
                QName.createQName(NamespaceService.ALFRESCO_URI, "children"),
                QName.createQName(NamespaceService.ALFRESCO_URI, "moved"));
		
		// Check that the working copy has been removed
		assertFalse(this.nodeService.exists(workingCopy));
		
		// Check that the origional is no longer locked
		assertEquals(LockStatus.NO_LOCK, this.lockService.getLockStatus(newNodeRef, TestWithUserUtils.getCurrentUserRef(this.authenticationService)));
		
		//System.out.println(NodeStoreInspector.dumpNodeStore(this.nodeService, this.testStoreRef));
    }
    
    /**
     * Check that the rules can be enabled and disabled
     */
    public void testRulesDisabled()
    {
        this.ruleService.makeActionable(this.nodeRef);
        
        RuleType ruleType = this.ruleService.getRuleType("inbound");
        
        Map<String, Serializable> actionParams = new HashMap<String, Serializable>(1);
        actionParams.put("aspect-name", ContentModel.ASPECT_VERSIONABLE);        
        
        Rule rule = this.ruleService.createRule(ruleType);
        rule.addActionCondition(NoConditionEvaluator.NAME, null);
        rule.addAction(AddFeaturesActionExecuter.NAME, actionParams);
        
        this.ruleService.saveRule(this.nodeRef, rule);        
        this.ruleService.disableRules(this.nodeRef);
        
        NodeRef newNodeRef = this.nodeService.createNode(
                this.nodeRef,
                QName.createQName(NamespaceService.ALFRESCO_URI, "children"),                
                QName.createQName(NamespaceService.ALFRESCO_URI, "children"),
                ContentModel.TYPE_CONTENT,
                getContentProperties()).getChildRef();         
        addContentToNode(newNodeRef);
        assertFalse(this.nodeService.hasAspect(newNodeRef, ContentModel.ASPECT_VERSIONABLE));      
        
        this.ruleService.enableRules(this.nodeRef);
        
        NodeRef newNodeRef2 = this.nodeService.createNode(
                this.nodeRef,
                QName.createQName(NamespaceService.ALFRESCO_URI, "children"),                
                QName.createQName(NamespaceService.ALFRESCO_URI, "children"),
                ContentModel.TYPE_CONTENT,
                getContentProperties()).getChildRef();        
        addContentToNode(newNodeRef2);
        assertTrue(this.nodeService.hasAspect(newNodeRef2, ContentModel.ASPECT_VERSIONABLE));       
    }
    
    /**
     * Adds content to a given node. 
     * <p>
     * Used to trigger rules of type of incomming.
     * 
     * @param nodeRef  the node reference
     */
    private void addContentToNode(NodeRef nodeRef)
    {
    	ContentWriter contentWriter = this.contentService.getUpdatingWriter(nodeRef);
    	assertNotNull(contentWriter);
    	contentWriter.putContent(STANDARD_TEXT_CONTENT);
    }
    
    /**
     * Test checkMandatoryProperties method
     */
    public void testCheckMandatoryProperties()
    {
        this.ruleService.makeActionable(this.nodeRef);
        
        RuleType ruleType = this.ruleService.getRuleType("inbound");
        
        Map<String, Serializable> actionParams = new HashMap<String, Serializable>(1);
        actionParams.put("aspect-name", ContentModel.ASPECT_VERSIONABLE);        
        
        Map<String, Serializable> condParams = new HashMap<String, Serializable>(1);
        // should be setting the condition parameter here
        
        Rule rule = this.ruleService.createRule(ruleType);
        rule.addActionCondition(MatchTextEvaluator.NAME, condParams);
        rule.addAction(AddFeaturesActionExecuter.NAME, actionParams);
        
        this.ruleService.saveRule(this.nodeRef, rule);
        
        try
        {
            // Try and create a node .. should fail since the rule is invalid
            Map<QName, Serializable> props2 = getContentProperties();
            props2.put(ContentModel.PROP_NAME, "bobbins.doc");
            NodeRef newNodeRef2 = this.nodeService.createNode(
                    this.nodeRef,
                    QName.createQName(NamespaceService.ALFRESCO_URI, "children"),                
                    QName.createQName(NamespaceService.ALFRESCO_URI, "children"),
                    ContentModel.TYPE_CONTENT,
                    props2).getChildRef();
            addContentToNode(newNodeRef2);
            fail("An exception should have been thrown since a mandatory parameter was missing from the condition.");
        }
        catch (Exception ruleServiceException)
        {
            // Success since we where expecting the exception
        }
    }
    
	/**
     * Test:
     *          rule type:  inbound
     *          condition:  match-text(
     *          				text = .doc,
     *          				operation = CONTAINS)
     *          action:     add-features(
     *                          aspect-name = versionable)
     */
	public void testContainsTextCondition()
	{
		this.ruleService.makeActionable(this.nodeRef);
        
        RuleType ruleType = this.ruleService.getRuleType("inbound");
        
        Map<String, Serializable> actionParams = new HashMap<String, Serializable>(1);
		actionParams.put("aspect-name", ContentModel.ASPECT_VERSIONABLE);        
        
        // ActionCondition parameter's 
		Map<String, Serializable> condParams = new HashMap<String, Serializable>(1);
		condParams.put(MatchTextEvaluator.PARAM_TEXT, ".doc");        
		
        Rule rule = this.ruleService.createRule(ruleType);
        rule.addActionCondition(MatchTextEvaluator.NAME, condParams);
        rule.addAction(AddFeaturesActionExecuter.NAME, actionParams);
        
        this.ruleService.saveRule(this.nodeRef, rule);
		
		// Test condition failure
		Map<QName, Serializable> props1 = new HashMap<QName, Serializable>();
		props1.put(ContentModel.PROP_NAME, "bobbins.txt");
        props1.put(ContentModel.PROP_MIME_TYPE, MimetypeMap.MIMETYPE_TEXT_PLAIN);
		NodeRef newNodeRef = this.nodeService.createNode(
                this.nodeRef,
                QName.createQName(NamespaceService.ALFRESCO_URI, "children"),                
                QName.createQName(NamespaceService.ALFRESCO_URI, "children"),
                ContentModel.TYPE_CONTENT,
                props1).getChildRef();   
		addContentToNode(newNodeRef);
        
        //Map<QName, Serializable> map = this.nodeService.getProperties(newNodeRef);
        //String value = (String)this.nodeService.getProperty(newNodeRef, ContentModel.PROP_NAME);
        
        assertFalse(this.nodeService.hasAspect(newNodeRef, ContentModel.ASPECT_VERSIONABLE));  
		
		// Test condition success
		Map<QName, Serializable> props2 = new HashMap<QName, Serializable>();
		props2.put(ContentModel.PROP_NAME, "bobbins.doc");
        props2.put(ContentModel.PROP_MIME_TYPE, MimetypeMap.MIMETYPE_TEXT_PLAIN);
		NodeRef newNodeRef2 = this.nodeService.createNode(
                this.nodeRef,
                QName.createQName(NamespaceService.ALFRESCO_URI, "children"),                
                QName.createQName(NamespaceService.ALFRESCO_URI, "children"),
                ContentModel.TYPE_CONTENT,
                props2).getChildRef();        
		addContentToNode(newNodeRef2);
        assertTrue(this.nodeService.hasAspect(
                newNodeRef2, 
                ContentModel.ASPECT_VERSIONABLE)); 
		
		try
		{
			// Test name not set
			NodeRef newNodeRef3 = this.nodeService.createNode(
	                this.nodeRef,
	                QName.createQName(NamespaceService.ALFRESCO_URI, "children"),                
	                QName.createQName(NamespaceService.ALFRESCO_URI, "children"),
	                ContentModel.TYPE_CONTENT,
                    getContentProperties()).getChildRef();      
			addContentToNode(newNodeRef3);
		}
		catch (RuleServiceException exception)
		{
			// Correct since text-match is a mandatory property
		}
        
        // Test begins with
        Map<String, Serializable> condParamsBegins = new HashMap<String, Serializable>(1);
        condParamsBegins.put(MatchTextEvaluator.PARAM_TEXT, "bob*");
        rule.removeAllActionConditions();
        rule.addActionCondition(MatchTextEvaluator.NAME, condParamsBegins);
        this.ruleService.saveRule(this.nodeRef, rule);
        Map<QName, Serializable> propsx = new HashMap<QName, Serializable>();
        propsx.put(ContentModel.PROP_NAME, "mybobbins.doc");
        propsx.put(ContentModel.PROP_MIME_TYPE, MimetypeMap.MIMETYPE_TEXT_PLAIN);
        NodeRef newNodeRefx = this.nodeService.createNode(
                this.nodeRef,
                QName.createQName(NamespaceService.ALFRESCO_URI, "children"),                
                QName.createQName(NamespaceService.ALFRESCO_URI, "children"),
                ContentModel.TYPE_CONTENT,
                propsx).getChildRef();   
        addContentToNode(newNodeRefx);
        assertFalse(this.nodeService.hasAspect(newNodeRefx, ContentModel.ASPECT_VERSIONABLE));  
        Map<QName, Serializable> propsy = new HashMap<QName, Serializable>();
        propsy.put(ContentModel.PROP_NAME, "bobbins.doc");
        propsy.put(ContentModel.PROP_MIME_TYPE, MimetypeMap.MIMETYPE_TEXT_PLAIN);
        NodeRef newNodeRefy = this.nodeService.createNode(
                this.nodeRef,
                QName.createQName(NamespaceService.ALFRESCO_URI, "children"),                
                QName.createQName(NamespaceService.ALFRESCO_URI, "children"),
                ContentModel.TYPE_CONTENT,
                propsy).getChildRef();   
        addContentToNode(newNodeRefy);
        assertTrue(this.nodeService.hasAspect(
                newNodeRefy, 
                ContentModel.ASPECT_VERSIONABLE)); 
        
        // Test ends with
        Map<String, Serializable> condParamsEnds = new HashMap<String, Serializable>(1);
        condParamsEnds.put(MatchTextEvaluator.PARAM_TEXT, "*s.doc");
        rule.removeAllActionConditions();
        rule.addActionCondition(MatchTextEvaluator.NAME, condParamsEnds);
        this.ruleService.saveRule(this.nodeRef, rule);
        Map<QName, Serializable> propsa = new HashMap<QName, Serializable>();
        propsa.put(ContentModel.PROP_NAME, "bobbins.document");
        propsa.put(ContentModel.PROP_MIME_TYPE, MimetypeMap.MIMETYPE_TEXT_PLAIN);
        NodeRef newNodeRefa = this.nodeService.createNode(
                this.nodeRef,
                QName.createQName(NamespaceService.ALFRESCO_URI, "children"),                
                QName.createQName(NamespaceService.ALFRESCO_URI, "children"),
                ContentModel.TYPE_CONTENT,
                propsa).getChildRef(); 
        addContentToNode(newNodeRefa);
        assertFalse(this.nodeService.hasAspect(newNodeRefa, ContentModel.ASPECT_VERSIONABLE));  
        Map<QName, Serializable> propsb = new HashMap<QName, Serializable>();
        propsb.put(ContentModel.PROP_NAME, "bobbins.doc");
        propsb.put(ContentModel.PROP_MIME_TYPE, MimetypeMap.MIMETYPE_TEXT_PLAIN);
        NodeRef newNodeRefb = this.nodeService.createNode(
                this.nodeRef,
                QName.createQName(NamespaceService.ALFRESCO_URI, "children"),                
                QName.createQName(NamespaceService.ALFRESCO_URI, "children"),
                ContentModel.TYPE_CONTENT,
                propsb).getChildRef();   
        addContentToNode(newNodeRefb);
        assertTrue(this.nodeService.hasAspect(
                newNodeRefb, 
                ContentModel.ASPECT_VERSIONABLE)); 
	}
    
    /**
     * Test:
     *          rule type:  outbound
     *          condition:  no-condition()
     *          action:     add-features(
     *                          aspect-name = versionable)
     */
	// TODO removed for technology preview release
    public void xtestOutboundRuleType()
    {
        this.ruleService.makeActionable(this.nodeRef);
        
        this.nodeService.addAspect(this.nodeRef, ContentModel.ASPECT_LOCKABLE, null);
        
        RuleType ruleType = this.ruleService.getRuleType("outbound");
        
        Map<String, Serializable> params = new HashMap<String, Serializable>(1);
        params.put("aspect-name", ContentModel.ASPECT_VERSIONABLE);        
        
        Rule rule = this.ruleService.createRule(ruleType);
        rule.addActionCondition(NoConditionEvaluator.NAME, null);
        rule.addAction(AddFeaturesActionExecuter.NAME, params);
        
        this.ruleService.saveRule(this.nodeRef, rule);
        
        // Create a node
        NodeRef newNodeRef = this.nodeService.createNode(
                this.nodeRef,
                QName.createQName(NamespaceService.ALFRESCO_URI, "children"),                
                QName.createQName(NamespaceService.ALFRESCO_URI, "children"),
                ContentModel.TYPE_CONTAINER).getChildRef();        
        assertFalse(this.nodeService.hasAspect(newNodeRef, ContentModel.ASPECT_VERSIONABLE));
        
        // Move the node out of the actionable folder
        this.nodeService.moveNode(
                newNodeRef, 
                this.rootNodeRef, 
                QName.createQName(NamespaceService.ALFRESCO_URI, "children"),                
                QName.createQName(NamespaceService.ALFRESCO_URI, "children"));
        assertTrue(this.nodeService.hasAspect(newNodeRef, ContentModel.ASPECT_VERSIONABLE));
        
        //System.out.println(NodeStoreInspector.dumpNodeStore(this.nodeService, this.testStoreRef));
    }
    
    /**
     * Performance guideline test
     *
     */
    public void xtestPerformanceOfRuleExecution()
    {
		try
		{
	        StopWatch sw = new StopWatch();
	        
	        // Create actionable nodes
	        sw.start("create nodes with no rule executed");		
			UserTransaction userTransaction1 = this.serviceRegistry.getUserTransaction();
			userTransaction1.begin();
			
			for (int i = 0; i < 100; i++)
	        {
	            this.nodeService.createNode(
	                    this.nodeRef,
	                    ContentModel.ASSOC_CONTAINS,
	                    ContentModel.ASSOC_CONTAINS,
	                    ContentModel.TYPE_CONTAINER).getChildRef(); 
	            assertFalse(this.nodeService.hasAspect(nodeRef, ContentModel.ASPECT_VERSIONABLE));
	        }
				
			userTransaction1.commit();
	        sw.stop();
	        
	        this.ruleService.makeActionable(this.nodeRef);
	        
	        RuleType ruleType = this.ruleService.getRuleType("inbound");
	        
	        Map<String, Serializable> params = new HashMap<String, Serializable>(1);
	        params.put("aspect-name", ContentModel.ASPECT_VERSIONABLE);        
	        
	        Rule rule = this.ruleService.createRule(ruleType);
	        rule.addActionCondition(NoConditionEvaluator.NAME, null);
	        rule.addAction(AddFeaturesActionExecuter.NAME, params);
	        
	        this.ruleService.saveRule(this.nodeRef, rule);
	        
	        sw.start("create nodes with one rule run (apply versionable aspect)");
			UserTransaction userTransaction2 = this.serviceRegistry.getUserTransaction();
			userTransaction2.begin();
			
			NodeRef[] nodeRefs = new NodeRef[100];
	        for (int i = 0; i < 100; i++)
	        {
	            NodeRef nodeRef = this.nodeService.createNode(
	                    this.nodeRef,
						QName.createQName(NamespaceService.ALFRESCO_URI, "children"),
						QName.createQName(NamespaceService.ALFRESCO_URI, "children"),
	                    ContentModel.TYPE_CONTAINER).getChildRef();
	            addContentToNode(nodeRef);
				nodeRefs[i] = nodeRef;
				
				// Check that the versionable aspect has not yet been applied
				assertFalse(this.nodeService.hasAspect(nodeRef, ContentModel.ASPECT_VERSIONABLE));
	        }
			
			userTransaction2.commit();
	        sw.stop();
			
			// Check that the versionable aspect has been applied to all the created nodes
			for (NodeRef ref : nodeRefs) 
			{
				assertTrue(this.nodeService.hasAspect(ref, ContentModel.ASPECT_VERSIONABLE));
			}
	        
	        System.out.println(sw.prettyPrint());
		}
		catch (Exception exception)
		{
			throw new RuntimeException(exception);
		}
    }
}
