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
package org.alfresco.repo.action.evaluator;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.action.ActionConditionImpl;
import org.alfresco.repo.dictionary.DictionaryDAO;
import org.alfresco.repo.dictionary.M2Model;
import org.alfresco.repo.dictionary.M2Property;
import org.alfresco.repo.dictionary.M2Type;
import org.alfresco.service.cmr.action.ActionServiceException;
import org.alfresco.service.cmr.dictionary.DataTypeDefinition;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.BaseSpringTest;
import org.alfresco.util.GUID;

/**
 * Compare property value evaluator test
 * 
 * @author Roy Wetherall
 */
public class ComparePropertyValueEvaluatorTest extends BaseSpringTest
{
    private static final String TEST_TYPE_NAMESPACE = "testNamespace";
    private static final QName TEST_TYPE_QNAME = QName.createQName(TEST_TYPE_NAMESPACE, "testType");
    private static final QName PROP_TEXT = QName.createQName(TEST_TYPE_NAMESPACE, "propText");
    private static final QName PROP_INT = QName.createQName(TEST_TYPE_NAMESPACE, "propInt");
    private static final QName PROP_DATETIME = QName.createQName(TEST_TYPE_NAMESPACE, "propDatetime");
    private static final QName PROP_NODEREF = QName.createQName(TEST_TYPE_NAMESPACE, "propNodeRef");
    
    private static final String TEXT_VALUE = "myDocument.doc";
    private static final int INT_VALUE = 100;
    
    private Date beforeDateValue;
    private Date dateValue;
    private Date afterDateValue;
    private NodeRef nodeValue;
    
    private DictionaryDAO dictionaryDAO;
    private NodeService nodeService;
    private StoreRef testStoreRef;
    private NodeRef rootNodeRef;
    private NodeRef nodeRef;
    private ComparePropertyValueEvaluator evaluator;
    
    /**
     * Sets the meta model DAO
     * 
     * @param dictionaryDAO  the meta model DAO
     */
    public void setDictionaryDAO(DictionaryDAO dictionaryDAO)
    {
        this.dictionaryDAO = dictionaryDAO;
    }
    
    /**
     * @see org.springframework.test.AbstractTransactionalSpringContextTests#onSetUpInTransaction()
     */
    @Override
    protected void onSetUpInTransaction() throws Exception
    {
        // Need to create model to contain our custom type
        createTestModel();
        
        this.nodeService = (NodeService)this.applicationContext.getBean("nodeService");
        
        // Create the store and get the root node
        this.testStoreRef = this.nodeService.createStore(
                StoreRef.PROTOCOL_WORKSPACE, "Test_"
                        + System.currentTimeMillis());
        this.rootNodeRef = this.nodeService.getRootNode(this.testStoreRef);

        this.nodeValue = new NodeRef(this.testStoreRef, "1234");
        
        this.beforeDateValue = new Date();
        Thread.sleep(2000);
        this.dateValue = new Date();
        Thread.sleep(2000);
        this.afterDateValue = new Date();
        
        Map<QName, Serializable> props = new HashMap<QName, Serializable>();
        props.put(PROP_TEXT, TEXT_VALUE);
        props.put(PROP_INT, INT_VALUE);
        props.put(PROP_DATETIME, this.dateValue);
        props.put(PROP_NODEREF, this.nodeValue);
        
        // Create the node used for tests
        this.nodeRef = this.nodeService.createNode(
                this.rootNodeRef,
                ContentModel.ASSOC_CHILDREN,
                QName.createQName("{test}testnode"),
                TEST_TYPE_QNAME,
                props).getChildRef();
        
        this.evaluator = (ComparePropertyValueEvaluator)this.applicationContext.getBean(ComparePropertyValueEvaluator.NAME);
    }
    
    /**
     * Test numeric comparisions
     */
    public void testNumericComparison()
    {
        ActionConditionImpl condition = new ActionConditionImpl(GUID.generate(), ComparePropertyValueEvaluator.NAME);
        condition.setParameterValue(ComparePropertyValueEvaluator.PARAM_PROPERTY, PROP_INT);
        
        // Test the default operation
        
        condition.setParameterValue(ComparePropertyValueEvaluator.PARAM_VALUE, INT_VALUE);
        assertTrue(this.evaluator.evaluate(condition, this.nodeRef));
        
        condition.setParameterValue(ComparePropertyValueEvaluator.PARAM_VALUE, 101);
        assertFalse(this.evaluator.evaluate(condition, this.nodeRef));
        
        // Test equals operation
        
        condition.setParameterValue(ComparePropertyValueEvaluator.PARAM_OPERATION, ComparePropertyValueOperation.EQUALS);
        
        condition.setParameterValue(ComparePropertyValueEvaluator.PARAM_VALUE, INT_VALUE);
        assertTrue(this.evaluator.evaluate(condition, this.nodeRef));
        
        condition.setParameterValue(ComparePropertyValueEvaluator.PARAM_VALUE, 101);
        assertFalse(this.evaluator.evaluate(condition, this.nodeRef));  
        
        // Test equals greater than operation
        
        condition.setParameterValue(ComparePropertyValueEvaluator.PARAM_OPERATION, ComparePropertyValueOperation.GREATER_THAN);
        
        condition.setParameterValue(ComparePropertyValueEvaluator.PARAM_VALUE, 99);
        assertTrue(this.evaluator.evaluate(condition, this.nodeRef));
        
        condition.setParameterValue(ComparePropertyValueEvaluator.PARAM_VALUE, 101);
        assertFalse(this.evaluator.evaluate(condition, this.nodeRef));  
        
        // Test equals greater than operation
        
        condition.setParameterValue(ComparePropertyValueEvaluator.PARAM_OPERATION, ComparePropertyValueOperation.GREATER_THAN_EQUAL);
        
        condition.setParameterValue(ComparePropertyValueEvaluator.PARAM_VALUE, 99);
        assertTrue(this.evaluator.evaluate(condition, this.nodeRef));
        
        condition.setParameterValue(ComparePropertyValueEvaluator.PARAM_VALUE, 100);
        assertTrue(this.evaluator.evaluate(condition, this.nodeRef));
        
        condition.setParameterValue(ComparePropertyValueEvaluator.PARAM_VALUE, 101);
        assertFalse(this.evaluator.evaluate(condition, this.nodeRef));
        
        // Test equals less than operation
        
        condition.setParameterValue(ComparePropertyValueEvaluator.PARAM_OPERATION, ComparePropertyValueOperation.LESS_THAN);
        
        condition.setParameterValue(ComparePropertyValueEvaluator.PARAM_VALUE, 101);
        assertTrue(this.evaluator.evaluate(condition, this.nodeRef));
        
        condition.setParameterValue(ComparePropertyValueEvaluator.PARAM_VALUE, 99);
        assertFalse(this.evaluator.evaluate(condition, this.nodeRef));  
        
        // Test equals less than equals operation
        
        condition.setParameterValue(ComparePropertyValueEvaluator.PARAM_OPERATION, ComparePropertyValueOperation.LESS_THAN_EQUAL);
        
        condition.setParameterValue(ComparePropertyValueEvaluator.PARAM_VALUE, 101);
        assertTrue(this.evaluator.evaluate(condition, this.nodeRef));
        
        condition.setParameterValue(ComparePropertyValueEvaluator.PARAM_VALUE, 100);
        assertTrue(this.evaluator.evaluate(condition, this.nodeRef));
        
        condition.setParameterValue(ComparePropertyValueEvaluator.PARAM_VALUE, 99);
        assertFalse(this.evaluator.evaluate(condition, this.nodeRef));
        
        // Ensure other operators are invalid
        
        condition.setParameterValue(ComparePropertyValueEvaluator.PARAM_OPERATION, ComparePropertyValueOperation.BEGINS);
        try { this.evaluator.evaluate(condition, this.nodeRef); fail("An exception should have been raised here."); } catch (ActionServiceException exception) {exception.printStackTrace();};
        condition.setParameterValue(ComparePropertyValueEvaluator.PARAM_OPERATION, ComparePropertyValueOperation.ENDS);
        try { this.evaluator.evaluate(condition, this.nodeRef); fail("An exception should have been raised here."); } catch (ActionServiceException exception) {};
        condition.setParameterValue(ComparePropertyValueEvaluator.PARAM_OPERATION, ComparePropertyValueOperation.CONTAINS);
        try { this.evaluator.evaluate(condition, this.nodeRef); fail("An exception should have been raised here."); } catch (ActionServiceException exception) {};  
    }
    
    /**
     * Test date comparison
     */
    public void testDateComparison()
    {
        ActionConditionImpl condition = new ActionConditionImpl(GUID.generate(), ComparePropertyValueEvaluator.NAME);
        condition.setParameterValue(ComparePropertyValueEvaluator.PARAM_PROPERTY, PROP_DATETIME);
        
        // Test the default operation
        
        condition.setParameterValue(ComparePropertyValueEvaluator.PARAM_VALUE, this.dateValue);
        assertTrue(this.evaluator.evaluate(condition, this.nodeRef));
        
        condition.setParameterValue(ComparePropertyValueEvaluator.PARAM_VALUE, new Date());
        assertFalse(this.evaluator.evaluate(condition, this.nodeRef));
        
        // Test the equals operation
        
        condition.setParameterValue(ComparePropertyValueEvaluator.PARAM_OPERATION, ComparePropertyValueOperation.EQUALS);
        
        condition.setParameterValue(ComparePropertyValueEvaluator.PARAM_VALUE, this.dateValue);
        assertTrue(this.evaluator.evaluate(condition, this.nodeRef));
        
        condition.setParameterValue(ComparePropertyValueEvaluator.PARAM_VALUE, new Date());
        assertFalse(this.evaluator.evaluate(condition, this.nodeRef));
        
        // Test equals greater than operation
        
        condition.setParameterValue(ComparePropertyValueEvaluator.PARAM_OPERATION, ComparePropertyValueOperation.GREATER_THAN);
        
        condition.setParameterValue(ComparePropertyValueEvaluator.PARAM_VALUE, this.beforeDateValue);
        assertTrue(this.evaluator.evaluate(condition, this.nodeRef));
        
        condition.setParameterValue(ComparePropertyValueEvaluator.PARAM_VALUE, this.afterDateValue);
        assertFalse(this.evaluator.evaluate(condition, this.nodeRef));  
        
        // Test equals greater than operation
        
        condition.setParameterValue(ComparePropertyValueEvaluator.PARAM_OPERATION, ComparePropertyValueOperation.GREATER_THAN_EQUAL);
        
        condition.setParameterValue(ComparePropertyValueEvaluator.PARAM_VALUE, this.beforeDateValue);
        assertTrue(this.evaluator.evaluate(condition, this.nodeRef));
        
        condition.setParameterValue(ComparePropertyValueEvaluator.PARAM_VALUE, this.dateValue);
        assertTrue(this.evaluator.evaluate(condition, this.nodeRef));
        
        condition.setParameterValue(ComparePropertyValueEvaluator.PARAM_VALUE, this.afterDateValue);
        assertFalse(this.evaluator.evaluate(condition, this.nodeRef));
        
        // Test equals less than operation
        
        condition.setParameterValue(ComparePropertyValueEvaluator.PARAM_OPERATION, ComparePropertyValueOperation.LESS_THAN);
        
        condition.setParameterValue(ComparePropertyValueEvaluator.PARAM_VALUE, this.afterDateValue);
        assertTrue(this.evaluator.evaluate(condition, this.nodeRef));
        
        condition.setParameterValue(ComparePropertyValueEvaluator.PARAM_VALUE, this.beforeDateValue);
        assertFalse(this.evaluator.evaluate(condition, this.nodeRef));  
        
        // Test equals less than equals operation
        
        condition.setParameterValue(ComparePropertyValueEvaluator.PARAM_OPERATION, ComparePropertyValueOperation.LESS_THAN_EQUAL);
        
        condition.setParameterValue(ComparePropertyValueEvaluator.PARAM_VALUE, this.afterDateValue);
        assertTrue(this.evaluator.evaluate(condition, this.nodeRef));
        
        condition.setParameterValue(ComparePropertyValueEvaluator.PARAM_VALUE, this.dateValue);
        assertTrue(this.evaluator.evaluate(condition, this.nodeRef));
        
        condition.setParameterValue(ComparePropertyValueEvaluator.PARAM_VALUE, this.beforeDateValue);
        assertFalse(this.evaluator.evaluate(condition, this.nodeRef));
        
        // Ensure other operators are invalid
        
        condition.setParameterValue(ComparePropertyValueEvaluator.PARAM_OPERATION, ComparePropertyValueOperation.BEGINS);
        try { this.evaluator.evaluate(condition, this.nodeRef); fail("An exception should have been raised here."); } catch (ActionServiceException exception) {exception.printStackTrace();};
        condition.setParameterValue(ComparePropertyValueEvaluator.PARAM_OPERATION, ComparePropertyValueOperation.ENDS);
        try { this.evaluator.evaluate(condition, this.nodeRef); fail("An exception should have been raised here."); } catch (ActionServiceException exception) {};
        condition.setParameterValue(ComparePropertyValueEvaluator.PARAM_OPERATION, ComparePropertyValueOperation.CONTAINS);
        try { this.evaluator.evaluate(condition, this.nodeRef); fail("An exception should have been raised here."); } catch (ActionServiceException exception) {};  
    }
    
    /**
     * Test text comparison
     */
    public void testTextComparison()
    {
        ActionConditionImpl condition = new ActionConditionImpl(GUID.generate(), ComparePropertyValueEvaluator.NAME);
        condition.setParameterValue(ComparePropertyValueEvaluator.PARAM_PROPERTY, PROP_TEXT);
        
        // Test default operations implied by presence and position of *
        
        condition.setParameterValue(ComparePropertyValueEvaluator.PARAM_VALUE, "*.doc");
        assertTrue(this.evaluator.evaluate(condition, this.nodeRef));
        
        condition.setParameterValue(ComparePropertyValueEvaluator.PARAM_VALUE, "*.xls");
        assertFalse(this.evaluator.evaluate(condition, this.nodeRef));
        
        condition.setParameterValue(ComparePropertyValueEvaluator.PARAM_VALUE, "my*");
        assertTrue(this.evaluator.evaluate(condition, this.nodeRef));
        
        condition.setParameterValue(ComparePropertyValueEvaluator.PARAM_VALUE, "bad*");
        assertFalse(this.evaluator.evaluate(condition, this.nodeRef));
        
        condition.setParameterValue(ComparePropertyValueEvaluator.PARAM_VALUE, "Document");
        assertTrue(this.evaluator.evaluate(condition, this.nodeRef));
        
        condition.setParameterValue(ComparePropertyValueEvaluator.PARAM_VALUE, "bobbins");
        assertFalse(this.evaluator.evaluate(condition, this.nodeRef));
        
        // Test equals operator
        
        condition.setParameterValue(ComparePropertyValueEvaluator.PARAM_OPERATION, ComparePropertyValueOperation.EQUALS);
        
        condition.setParameterValue(ComparePropertyValueEvaluator.PARAM_VALUE, TEXT_VALUE);
        assertTrue(this.evaluator.evaluate(condition, this.nodeRef));
        
        condition.setParameterValue(ComparePropertyValueEvaluator.PARAM_VALUE, "bobbins");
        assertFalse(this.evaluator.evaluate(condition, this.nodeRef));        
        
        // Test contains operator
        
        condition.setParameterValue(ComparePropertyValueEvaluator.PARAM_OPERATION, ComparePropertyValueOperation.CONTAINS);
        
        condition.setParameterValue(ComparePropertyValueEvaluator.PARAM_VALUE, "Document");
        assertTrue(this.evaluator.evaluate(condition, this.nodeRef));
        
        condition.setParameterValue(ComparePropertyValueEvaluator.PARAM_VALUE, "bobbins");
        assertFalse(this.evaluator.evaluate(condition, this.nodeRef)); 
        
        // Test begins operator
        
        condition.setParameterValue(ComparePropertyValueEvaluator.PARAM_OPERATION, ComparePropertyValueOperation.BEGINS);
        
        condition.setParameterValue(ComparePropertyValueEvaluator.PARAM_VALUE, "my");
        assertTrue(this.evaluator.evaluate(condition, this.nodeRef));
        
        condition.setParameterValue(ComparePropertyValueEvaluator.PARAM_VALUE, "bobbins");
        assertFalse(this.evaluator.evaluate(condition, this.nodeRef)); 
        
        // Test ends operator
        
        condition.setParameterValue(ComparePropertyValueEvaluator.PARAM_OPERATION, ComparePropertyValueOperation.ENDS);
        
        condition.setParameterValue(ComparePropertyValueEvaluator.PARAM_VALUE, "doc");
        assertTrue(this.evaluator.evaluate(condition, this.nodeRef));
        
        condition.setParameterValue(ComparePropertyValueEvaluator.PARAM_VALUE, "bobbins");
        assertFalse(this.evaluator.evaluate(condition, this.nodeRef)); 
        
        // Ensure other operators are invalid
        
        condition.setParameterValue(ComparePropertyValueEvaluator.PARAM_OPERATION, ComparePropertyValueOperation.GREATER_THAN);
        try { this.evaluator.evaluate(condition, this.nodeRef); fail("An exception should have been raised here."); } catch (ActionServiceException exception) {exception.printStackTrace();};
        condition.setParameterValue(ComparePropertyValueEvaluator.PARAM_OPERATION, ComparePropertyValueOperation.GREATER_THAN_EQUAL);
        try { this.evaluator.evaluate(condition, this.nodeRef); fail("An exception should have been raised here."); } catch (ActionServiceException exception) {};
        condition.setParameterValue(ComparePropertyValueEvaluator.PARAM_OPERATION, ComparePropertyValueOperation.LESS_THAN);
        try { this.evaluator.evaluate(condition, this.nodeRef); fail("An exception should have been raised here."); } catch (ActionServiceException exception) {};
        condition.setParameterValue(ComparePropertyValueEvaluator.PARAM_OPERATION, ComparePropertyValueOperation.LESS_THAN_EQUAL);
        try { this.evaluator.evaluate(condition, this.nodeRef); fail("An exception should have been raised here."); } catch (ActionServiceException exception) {};
    }
    
    /**
     * Test comparison of properties that do not have a registered comparitor
     */
    public void testOtherComparison()
    {
        NodeRef badNodeRef = new NodeRef(this.testStoreRef, "badId");
        
        ActionConditionImpl condition = new ActionConditionImpl(GUID.generate(), ComparePropertyValueEvaluator.NAME);
        condition.setParameterValue(ComparePropertyValueEvaluator.PARAM_PROPERTY, PROP_NODEREF);
        
        // Test default operation
        
        condition.setParameterValue(ComparePropertyValueEvaluator.PARAM_VALUE, this.nodeValue);
        assertTrue(this.evaluator.evaluate(condition, this.nodeRef));
        
        condition.setParameterValue(ComparePropertyValueEvaluator.PARAM_VALUE, badNodeRef);
        assertFalse(this.evaluator.evaluate(condition, this.nodeRef));
        
        condition.setParameterValue(ComparePropertyValueEvaluator.PARAM_VALUE, "this isn't even the correct type!");
        assertFalse(this.evaluator.evaluate(condition, this.nodeRef));
        
        // Test equals operation
        
        condition.setParameterValue(ComparePropertyValueEvaluator.PARAM_OPERATION, ComparePropertyValueOperation.EQUALS);
        
        condition.setParameterValue(ComparePropertyValueEvaluator.PARAM_VALUE, this.nodeValue);
        assertTrue(this.evaluator.evaluate(condition, this.nodeRef));
        
        condition.setParameterValue(ComparePropertyValueEvaluator.PARAM_VALUE, badNodeRef);
        assertFalse(this.evaluator.evaluate(condition, this.nodeRef));  
        
        // Ensure other operators are invalid
        
        condition.setParameterValue(ComparePropertyValueEvaluator.PARAM_OPERATION, ComparePropertyValueOperation.BEGINS);
        try { this.evaluator.evaluate(condition, this.nodeRef); fail("An exception should have been raised here."); } catch (ActionServiceException exception) { exception.printStackTrace();};
        condition.setParameterValue(ComparePropertyValueEvaluator.PARAM_OPERATION, ComparePropertyValueOperation.ENDS);
        try { this.evaluator.evaluate(condition, this.nodeRef); fail("An exception should have been raised here."); } catch (ActionServiceException exception) {};
        condition.setParameterValue(ComparePropertyValueEvaluator.PARAM_OPERATION, ComparePropertyValueOperation.CONTAINS);
        try { this.evaluator.evaluate(condition, this.nodeRef); fail("An exception should have been raised here."); } catch (ActionServiceException exception) {};
        condition.setParameterValue(ComparePropertyValueEvaluator.PARAM_OPERATION, ComparePropertyValueOperation.GREATER_THAN);
        try { this.evaluator.evaluate(condition, this.nodeRef); fail("An exception should have been raised here."); } catch (ActionServiceException exception) {};
        condition.setParameterValue(ComparePropertyValueEvaluator.PARAM_OPERATION, ComparePropertyValueOperation.GREATER_THAN_EQUAL);
        try { this.evaluator.evaluate(condition, this.nodeRef); fail("An exception should have been raised here."); } catch (ActionServiceException exception) {};
        condition.setParameterValue(ComparePropertyValueEvaluator.PARAM_OPERATION, ComparePropertyValueOperation.LESS_THAN);
        try { this.evaluator.evaluate(condition, this.nodeRef); fail("An exception should have been raised here."); } catch (ActionServiceException exception) {};
        condition.setParameterValue(ComparePropertyValueEvaluator.PARAM_OPERATION, ComparePropertyValueOperation.LESS_THAN_EQUAL);
        try { this.evaluator.evaluate(condition, this.nodeRef); fail("An exception should have been raised here."); } catch (ActionServiceException exception) {};
        
    }
    
    private void createTestModel()
    {
        M2Model model = M2Model.createModel("test:comparepropertyvalueevaluatortest");
        model.createNamespace(TEST_TYPE_NAMESPACE, "test");
        model.createImport(NamespaceService.DICTIONARY_MODEL_1_0_URI, NamespaceService.DICTIONARY_MODEL_PREFIX);
        model.createImport(NamespaceService.SYSTEM_MODEL_1_0_URI, NamespaceService.SYSTEM_MODEL_PREFIX);
        model.createImport(NamespaceService.CONTENT_MODEL_1_0_URI, NamespaceService.CONTENT_MODEL_PREFIX);

        M2Type testType = model.createType("test:" + TEST_TYPE_QNAME.getLocalName());
        testType.setParentName("cm:" + ContentModel.TYPE_CMOBJECT.getLocalName());
        
        M2Property prop1 = testType.createProperty("test:" + PROP_TEXT.getLocalName());
        prop1.setMandatory(false);
        prop1.setType("d:" + DataTypeDefinition.TEXT.getLocalName());
        prop1.setMultiValued(false);
        
        M2Property prop2 = testType.createProperty("test:" + PROP_INT.getLocalName());
        prop2.setMandatory(false);
        prop2.setType("d:" + DataTypeDefinition.INT.getLocalName());
        prop2.setMultiValued(false);
        
        M2Property prop3 = testType.createProperty("test:" + PROP_DATETIME.getLocalName());
        prop3.setMandatory(false);
        prop3.setType("d:" + DataTypeDefinition.DATETIME.getLocalName());
        prop3.setMultiValued(false);
        
        M2Property prop4 = testType.createProperty("test:" + PROP_NODEREF.getLocalName());
        prop4.setMandatory(false);
        prop4.setType("d:" + DataTypeDefinition.NODE_REF.getLocalName());
        prop4.setMultiValued(false);
        
        dictionaryDAO.putModel(model);
    }
}
