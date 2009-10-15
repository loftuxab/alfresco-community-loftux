/*
 * Copyright (C) 2005-2009 Alfresco Software Limited.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.

 * As a special exception to the terms and conditions of version 2.0 of 
 * the GPL, you may redistribute this Program in connection with Free/Libre 
 * and Open Source Software ("FLOSS") applications as described in Alfresco's 
 * FLOSS exception.  You should have received a copy of the text describing 
 * the FLOSS exception, and it is also available here: 
 * http://www.alfresco.com/legal/licensing"
 */
package org.alfresco.module.org_alfresco_module_dod5015.test;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.transaction.UserTransaction;

import org.alfresco.model.ContentModel;
import org.alfresco.module.org_alfresco_module_dod5015.CustomisableRmElement;
import org.alfresco.module.org_alfresco_module_dod5015.DOD5015Model;
import org.alfresco.module.org_alfresco_module_dod5015.RecordsManagementAdminService;
import org.alfresco.module.org_alfresco_module_dod5015.RecordsManagementCustomModel;
import org.alfresco.module.org_alfresco_module_dod5015.RecordsManagementModel;
import org.alfresco.module.org_alfresco_module_dod5015.RecordsManagementPolicies;
import org.alfresco.module.org_alfresco_module_dod5015.RecordsManagementPolicies.BeforeCreateReference;
import org.alfresco.module.org_alfresco_module_dod5015.RecordsManagementPolicies.OnCreateReference;
import org.alfresco.module.org_alfresco_module_dod5015.action.RecordsManagementActionService;
import org.alfresco.module.org_alfresco_module_dod5015.caveat.RMListOfValuesConstraint;
import org.alfresco.module.org_alfresco_module_dod5015.script.CustomReferenceType;
import org.alfresco.repo.content.MimetypeMap;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.repo.policy.Behaviour.NotificationFrequency;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.service.cmr.dictionary.AspectDefinition;
import org.alfresco.service.cmr.dictionary.AssociationDefinition;
import org.alfresco.service.cmr.dictionary.Constraint;
import org.alfresco.service.cmr.dictionary.ConstraintDefinition;
import org.alfresco.service.cmr.dictionary.DataTypeDefinition;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.dictionary.PropertyDefinition;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.ContentWriter;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.search.ResultSet;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.service.namespace.RegexQNamePattern;
import org.alfresco.service.transaction.TransactionService;
import org.alfresco.util.BaseSpringTest;

/**
 * This test class tests the definition and use of a custom RM elements at the Java services layer.
 * 
 * @author Neil McErlean, janv
 */
public class RecordsManagementAdminServiceImplTest extends BaseSpringTest 
                                         implements DOD5015Model, RecordsManagementCustomModel,
                                                    BeforeCreateReference,
                                                    OnCreateReference
{    
    private NodeRef filePlan;
    
    private ContentService contentService;
    private DictionaryService dictionaryService;
    private NamespaceService namespaceService;
    private NodeService nodeService;
    private SearchService searchService;
    private RecordsManagementActionService rmActionService;
    private RecordsManagementAdminService rmAdminService;
    private RetryingTransactionHelper transactionHelper;
    private TransactionService transactionService;
    private PolicyComponent policyComponent;
    
    private final static long testRunID = System.currentTimeMillis();
    
    @Override
    protected void onSetUpInTransaction() throws Exception 
    {
        super.onSetUpInTransaction();
        
        this.dictionaryService = (DictionaryService)this.applicationContext.getBean("DictionaryService");
        this.contentService = (ContentService)this.applicationContext.getBean("ContentService");
        this.namespaceService = (NamespaceService)this.applicationContext.getBean("NamespaceService");
        this.nodeService = (NodeService)this.applicationContext.getBean("NodeService");
        this.rmActionService = (RecordsManagementActionService)this.applicationContext.getBean("RecordsManagementActionService");
        this.rmAdminService = (RecordsManagementAdminService)this.applicationContext.getBean("RecordsManagementAdminService");
        this.searchService = (SearchService)this.applicationContext.getBean("SearchService");
        this.transactionHelper = (RetryingTransactionHelper)this.applicationContext.getBean("retryingTransactionHelper");
        this.transactionService = (TransactionService)this.applicationContext.getBean("TransactionService");
        this.policyComponent = (PolicyComponent)this.applicationContext.getBean("policyComponent");
        
        // Set the current security context as admin
        AuthenticationUtil.setFullyAuthenticatedUser(AuthenticationUtil.getAdminUserName());

        // Get the test data
        setUpTestData();
    }
    
    private void setUpTestData()
    {
        // Don't reload the fileplan data on each test method.
        if (retrieveJanuaryAISVitalFolders().size() != 1)
        {
            filePlan = TestUtilities.loadFilePlanData(applicationContext);
        }
    }
    
    @Override
    protected void onTearDownInTransaction() throws Exception
    {
        try
        {
            UserTransaction txn = transactionService.getUserTransaction(false);
            txn.begin();
            this.nodeService.deleteNode(filePlan);
            txn.commit();
        }
        catch (Exception e)
        {
            // Nothing
            //System.out.println("DID NOT DELETE FILE PLAN!");
        }
    }
    
    public void testCreateSimpleCustomProperties() throws Exception
    {
        setComplete();
        endTransaction();
        
        startNewTransaction();
        
        UserTransaction txn1 = transactionService.getUserTransaction(true);
        txn1.begin();
        
        // Create simple custom property definition (no constraint) for each type of customisable RM element
        for (CustomisableRmElement ce : CustomisableRmElement.values())
        {
            String aspectName = ce.getCorrespondingAspect();
            
            String propLocalName = "myProp-for-"+aspectName+"-"+testRunID;
            
            QName dataType = DataTypeDefinition.TEXT;
            String propTitle = "My property title";
            String description = "My property description";
            
            rmAdminService.addCustomPropertyDefinition(null, aspectName, propLocalName, dataType, propTitle, description);
        }
        
        txn1.commit();
        
        // TODO test usages
    }
    
    public void testCreateAndUseCustomProperty() throws Exception
    {
        // Create the necessary test object in the db: a record.
        NodeRef recordFolder = retrievePreexistingRecordFolder();
        NodeRef testRecord = createRecord(recordFolder, "testRecord" + System.currentTimeMillis());
        
        setComplete();
        endTransaction();
        
        UserTransaction txn1 = transactionService.getUserTransaction(false);
        txn1.begin();

        declareRecord(testRecord);

        // Define a custom property.
        QName generatedQName = rmAdminService.addCustomPropertyDefinition(null, ASPECT_CUSTOM_RECORD_FOLDER_PROPERTIES.toPrefixString(namespaceService),
                "foo", DataTypeDefinition.BOOLEAN, "custom prop title", "custom prop description");
        
        // We need to commit the transaction to trigger behaviour that should reload the data dictionary model.
        txn1.commit();
        
        UserTransaction txn2 = transactionService.getUserTransaction(false);
        txn2.begin();
        
        // Confirm the custom property is included in the list from rmAdminService.
        Map<QName, PropertyDefinition> customPropDefinitions = rmAdminService.getCustomPropertyDefinitions(CustomisableRmElement.RECORD_FOLDER);
        PropertyDefinition propDefn = customPropDefinitions.get(generatedQName);
        assertNotNull("Custom property definition from rmAdminService was null.", propDefn);
        assertEquals(generatedQName, propDefn.getName());
        assertEquals("foo", propDefn.getTitle());

        assertEquals(DataTypeDefinition.BOOLEAN, propDefn.getDataType().getName());
        
        // Now we need to use the custom property.
        // So we apply the aspect containing it to our test record.
        Map<QName, Serializable> customPropValue = new HashMap<QName, Serializable>();
        customPropValue.put(generatedQName, true);
        nodeService.addAspect(testRecord, ASPECT_CUSTOM_RECORD_FOLDER_PROPERTIES, customPropValue);
        
        txn2.commit();
        
        // Read back the property value to make sure it was correctly applied.
        transactionService.getUserTransaction(true);
        Map<QName, Serializable> nodeProps = nodeService.getProperties(testRecord);
        Serializable testProperty = nodeProps.get(generatedQName);
        assertNotNull("The testProperty was null.", testProperty);
        
        boolean testPropertyValue = (Boolean)testProperty;
        assertEquals("The test property was not 'true'.", true, testPropertyValue);
        
        // Check that the property has appeared in the data dictionary
        final AspectDefinition customPropertiesAspect = dictionaryService.getAspect(ASPECT_CUSTOM_RECORD_FOLDER_PROPERTIES);
        assertNotNull(customPropertiesAspect);
        assertNotNull("The customProperty is not returned from the dictionaryService.",
                customPropertiesAspect.getProperties().get(generatedQName));
    }
    
    public void testCreateAndUseCustomChildReference() throws Exception
    {
    	long now = System.currentTimeMillis();
        createAndUseCustomReference(CustomReferenceType.PARENT_CHILD, null, "superseded" + now, "superseding" + now);
    }

    public void testCreateAndUseCustomNonChildReference() throws Exception
    {
    	long now = System.currentTimeMillis();
    	createAndUseCustomReference(CustomReferenceType.BIDIRECTIONAL, "supporting" + now, null, null);
    }
    
	private void createAndUseCustomReference(final CustomReferenceType refType, final String label, final String source, final String target) throws Exception
	{
		// Create the necessary test objects in the db: two records.
        final NodeRef recordFolder = retrievePreexistingRecordFolder();
        setComplete();
        endTransaction();

        final NodeRef testRecord1 = transactionHelper.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<NodeRef>()
                {
                    public NodeRef execute() throws Throwable
                    {
                        NodeRef result = createRecord(recordFolder, "testRecordA" + System.currentTimeMillis());
                        return result;
                    }          
                });        
        final NodeRef testRecord2 = transactionHelper.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<NodeRef>()
                {
                    public NodeRef execute() throws Throwable
                    {
                        NodeRef result = createRecord(recordFolder, "testRecordB" + System.currentTimeMillis());
                        return result;
                    }          
                });        

        final QName generatedQName = transactionHelper.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<QName>()
                {
                    public QName execute() throws Throwable
                    {
                        declareRecord(testRecord1);
                        declareRecord(testRecord2);

                        Map <String, Serializable> params = new HashMap<String, Serializable>();
                        params.put("referenceType", refType.toString());
                        if (label != null) params.put("label", label);
                        if (source != null) params.put("source", source);
                        if (target != null) params.put("target", target);

                        // Create the reference definition.
                        QName qNameResult;
                        if (label != null)
                        {
                            // A bidirectional reference
                            qNameResult = rmAdminService.addCustomAssocDefinition(label);
                        }
                        else
                        {
                            // A parent/child reference
                            qNameResult = rmAdminService.addCustomChildAssocDefinition(source, target);
                        }
                        System.out.println("Creating new " + refType + " reference definition: " + qNameResult);
                        System.out.println("  params- label: '" + label + "' source: '" + source + "' target: '" + target + "'");
                        
                        return qNameResult;
                    }          
                });        
        
        transactionHelper.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>()
                {
                    public Void execute() throws Throwable
                    {
                        // Confirm the custom reference is included in the list from rmAdminService.
                        Map<QName, AssociationDefinition> customRefDefinitions = rmAdminService.getCustomReferenceDefinitions();
                        AssociationDefinition retrievedRefDefn = customRefDefinitions.get(generatedQName);
                        assertNotNull("Custom reference definition from rmAdminService was null.", retrievedRefDefn);
                        assertEquals(generatedQName, retrievedRefDefn.getName());
                        assertEquals(refType.equals(CustomReferenceType.PARENT_CHILD), retrievedRefDefn.isChild());
                        
                        // Now we need to use the custom reference.
                        // So we apply the aspect containing it to our test records.
                        nodeService.addAspect(testRecord1, ASPECT_CUSTOM_ASSOCIATIONS, null);
                        
                        QName assocsAspectQName = QName.createQName("rmc:customAssocs", namespaceService);
                        nodeService.addAspect(testRecord1, assocsAspectQName, null);

                        if (CustomReferenceType.PARENT_CHILD.equals(refType))
                        {
                            nodeService.addChild(testRecord1, testRecord2, generatedQName, generatedQName);
                        }
                        else
                        {
                            nodeService.createAssociation(testRecord1, testRecord2, generatedQName);
                        }
                        return null;
                    }          
                });        
        
        transactionHelper.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>()
                {
                    public Void execute() throws Throwable
                    {
                        // Read back the reference value to make sure it was correctly applied.
                        List<ChildAssociationRef> childAssocs = nodeService.getChildAssocs(testRecord1);
                        List<AssociationRef> retrievedAssocs = nodeService.getTargetAssocs(testRecord1, RegexQNamePattern.MATCH_ALL);
                        
                        Object newlyAddedRef = null;
                        if (CustomReferenceType.PARENT_CHILD.equals(refType))
                        {
                            for (ChildAssociationRef caRef : childAssocs)
                            {
                                QName refInstanceQName = caRef.getQName();
                                if (generatedQName.equals(refInstanceQName)) newlyAddedRef = caRef;
                            }
                        }
                        else
                        {
                            for (AssociationRef aRef : retrievedAssocs)
                            {
                                QName refQName = aRef.getTypeQName();
                                if (generatedQName.equals(refQName)) newlyAddedRef = aRef;
                            }
                        }
                        assertNotNull("newlyAddedRef was null.", newlyAddedRef);
                        
                        // Check that the reference has appeared in the data dictionary
                        AspectDefinition customAssocsAspect = dictionaryService.getAspect(ASPECT_CUSTOM_ASSOCIATIONS);
                        assertNotNull(customAssocsAspect);
                        if (CustomReferenceType.PARENT_CHILD.equals(refType))
                        {
                            assertNotNull("The customReference is not returned from the dictionaryService.",
                                    customAssocsAspect.getChildAssociations().get(generatedQName));
                        }
                        else
                        {
                            assertNotNull("The customReference is not returned from the dictionaryService.",
                                    customAssocsAspect.getAssociations().get(generatedQName));
                        }
                        return null;
                    }          
                });        
	}
	
    public void testGetAllProperties()
    {
        // Just dump them out for visual inspection
        System.out.println("Available custom properties:");
        Map<QName, PropertyDefinition> props = rmAdminService.getCustomPropertyDefinitions();
        for (QName prop : props.keySet())
        {
            System.out.println("   - " + prop.toString());
            
            String propId = props.get(prop).getTitle();
            assertNotNull("null client-id for " + prop, propId);
            
            System.out.println("       " + propId);
        }     
    }
	
    public void testGetAllReferences()
    {
        // Just dump them out for visual inspection
        System.out.println("Available custom references:");
        Map<QName, AssociationDefinition> references = rmAdminService.getCustomReferenceDefinitions();
        for (QName reference : references.keySet())
        {
            System.out.println("    - " + reference.toString());
            System.out.println("      " + references.get(reference).getTitle());
        }
    }
    
    public void testGetAllConstraints()
    {
        // Just dump them out for visual inspection
        System.out.println("Available custom constraints:");
        List<ConstraintDefinition> constraints = rmAdminService.getCustomConstraintDefinitions();
        for (ConstraintDefinition constraint : constraints)
        {
            System.out.println("   - " + constraint.getName());
            System.out.println("       " + constraint.getTitle());
        }
    }
    
	private boolean beforeMarker = false;
    private boolean onMarker = false;
    private boolean inTest = false;
	
	public void testCreateReference() throws Exception
	{
	    inTest = true;
        try
        {
            // Create the necessary test objects in the db: two records.
            NodeRef recordFolder = retrievePreexistingRecordFolder();
            NodeRef testRecord1 = createRecord(recordFolder, "testRecordA" + System.currentTimeMillis());
            NodeRef testRecord2 = createRecord(recordFolder, "testRecordB" + System.currentTimeMillis());
            
            setComplete();
            endTransaction();
            
            UserTransaction txn1 = transactionService.getUserTransaction(false);
            txn1.begin();
    
            declareRecord(testRecord1);
            declareRecord(testRecord2);
            
            policyComponent.bindClassBehaviour(
                    RecordsManagementPolicies.BEFORE_CREATE_REFERENCE, 
                    this, 
                    new JavaBehaviour(this, "beforeCreateReference", NotificationFrequency.EVERY_EVENT));
            policyComponent.bindClassBehaviour(
                    RecordsManagementPolicies.ON_CREATE_REFERENCE, 
                    this, 
                    new JavaBehaviour(this, "onCreateReference", NotificationFrequency.EVERY_EVENT));
            
            assertFalse(beforeMarker);
            assertFalse(onMarker);
            
            rmAdminService.addCustomReference(testRecord1, testRecord2, CUSTOM_REF_VERSIONS);
            
            assertTrue(beforeMarker);
            assertTrue(onMarker);
            
            txn1.commit();
        }
        finally
        {
            inTest = false;
        }
	} 
	
	public void beforeCreateReference(NodeRef fromNodeRef, NodeRef toNodeRef, QName reference)
    {
        beforeMarker = true;
    }

    public void onCreateReference(NodeRef fromNodeRef, NodeRef toNodeRef, QName reference)
    {
        onMarker = true;
    }
    
    private NodeRef retrievePreexistingRecordFolder()
    {
        final List<NodeRef> resultNodeRefs = retrieveJanuaryAISVitalFolders();
        
        return resultNodeRefs.get(0);
    }

    private List<NodeRef> retrieveJanuaryAISVitalFolders()
    {
        String typeQuery = "TYPE:\"" + TYPE_RECORD_FOLDER + "\" AND @cm\\:name:\"January AIS Audit Records\"";
        ResultSet types = this.searchService.query(TestUtilities.SPACES_STORE, SearchService.LANGUAGE_LUCENE, typeQuery);
        
        final List<NodeRef> resultNodeRefs = types.getNodeRefs();
        return resultNodeRefs;
    }

	private NodeRef createRecord(NodeRef recordFolder, String name)
	{
    	// Create the document
        Map<QName, Serializable> props = new HashMap<QName, Serializable>(1);
        props.put(ContentModel.PROP_NAME, name);
        NodeRef recordOne = this.nodeService.createNode(recordFolder, 
                                                        ContentModel.ASSOC_CONTAINS, 
                                                        QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, name), 
                                                        ContentModel.TYPE_CONTENT).getChildRef();
        
        // Set the content
        ContentWriter writer = this.contentService.getWriter(recordOne, ContentModel.PROP_CONTENT, true);
        writer.setMimetype(MimetypeMap.MIMETYPE_TEXT_PLAIN);
        writer.setEncoding("UTF-8");
        writer.putContent("There is some content in this record");
        
        return recordOne;
	}   
      
    private void declareRecord(NodeRef recordOne)
    {
        // Declare record
        this.nodeService.setProperty(recordOne, RecordsManagementModel.PROP_PUBLICATION_DATE, new Date());
        this.nodeService.setProperty(recordOne, RecordsManagementModel.PROP_MEDIA_TYPE, "mediaTypeValue"); 
        this.nodeService.setProperty(recordOne, RecordsManagementModel.PROP_FORMAT, "formatValue"); 
        this.nodeService.setProperty(recordOne, RecordsManagementModel.PROP_DATE_RECEIVED, new Date());
        this.nodeService.setProperty(recordOne, RecordsManagementModel.PROP_ORIGINATOR, "origValue");
        this.nodeService.setProperty(recordOne, RecordsManagementModel.PROP_ORIGINATING_ORGANIZATION, "origOrgValue");
        this.nodeService.setProperty(recordOne, ContentModel.PROP_TITLE, "titleValue");
        this.rmActionService.executeRecordsManagementAction(recordOne, "declareRecord");
	}
    
    public void testCreateCustomConstraints() throws Exception
    {
        setComplete();
        endTransaction();
        
        startNewTransaction();
        
        UserTransaction txn1 = transactionService.getUserTransaction(true);
        txn1.begin();
        
        List<ConstraintDefinition> customConstraintDefs = rmAdminService.getCustomConstraintDefinitions();
        assertNotNull(customConstraintDefs);
        int beforeCnt = customConstraintDefs.size();
        
        txn1.commit();
        
        UserTransaction txn2 = transactionService.getUserTransaction(false);
        txn2.begin();
        
        String conLocalName = "test-"+testRunID;
        
        final QName testCon = QName.createQName(RecordsManagementCustomModel.RM_CUSTOM_URI, conLocalName);
        final String conTitle = "test title - "+testRunID;
        
        List<String> allowedValues = new ArrayList<String>(3);
        allowedValues.add("RED");
        allowedValues.add("AMBER");
        allowedValues.add("GREEN");
        
        rmAdminService.addCustomConstraintDefinition(testCon, conTitle, true, allowedValues);
        
        txn2.commit();
        
        setComplete();
        endTransaction();
        
        // Set the current security context as System - to see allowed values (unless caveat config is also updated for admin)
        AuthenticationUtil.setFullyAuthenticatedUser(AuthenticationUtil.getSystemUserName());
        
        UserTransaction txn3 = transactionService.getUserTransaction(true);
        txn3.begin();
        
        customConstraintDefs = rmAdminService.getCustomConstraintDefinitions();
        assertEquals(beforeCnt+1, customConstraintDefs.size());
        
        boolean found = false;
        for (ConstraintDefinition conDef : customConstraintDefs)
        {
            if (conDef.getName().equals(testCon))
            {
                assertEquals(conTitle, conDef.getTitle());
                
                Constraint con = conDef.getConstraint();
                assertTrue(con instanceof RMListOfValuesConstraint);
                
                assertEquals("LIST", ((RMListOfValuesConstraint)con).getType());
                assertEquals(3, ((RMListOfValuesConstraint)con).getAllowedValues().size());
                
                found = true;
                break;
            }
        }
        assertTrue(found);
        
        txn3.commit();
        
        // Set the current security context as admin
        AuthenticationUtil.setFullyAuthenticatedUser(AuthenticationUtil.getAdminUserName());
        
        UserTransaction txn4 = transactionService.getUserTransaction(false);
        txn4.begin();
        
        allowedValues = new ArrayList<String>(2);
        allowedValues.add("RED");
        allowedValues.add("YELLOW");
        
        rmAdminService.changeCustomConstraintValues(testCon, allowedValues);
        
        txn4.commit();
        
        // Set the current security context as System - to see allowed values (unless caveat config is also updated for admin)
        AuthenticationUtil.setFullyAuthenticatedUser(AuthenticationUtil.getSystemUserName());
        
        UserTransaction txn5 = transactionService.getUserTransaction(true);
        txn5.begin();
        
        customConstraintDefs = rmAdminService.getCustomConstraintDefinitions();
        assertEquals(beforeCnt+1, customConstraintDefs.size());
        
        found = false;
        for (ConstraintDefinition conDef : customConstraintDefs)
        {
            if (conDef.getName().equals(testCon))
            {
                assertEquals(conTitle, conDef.getTitle());
                
                Constraint con = conDef.getConstraint();
                assertTrue(con instanceof RMListOfValuesConstraint);
                
                assertEquals("LIST", ((RMListOfValuesConstraint)con).getType());
                assertEquals(2, ((RMListOfValuesConstraint)con).getAllowedValues().size());
                
                found = true;
                break;
            }
        }
        assertTrue(found);
        
        txn5.commit();
        
        // Set the current security context as admin
        AuthenticationUtil.setFullyAuthenticatedUser(AuthenticationUtil.getAdminUserName());
        
        UserTransaction txn6 = transactionService.getUserTransaction(false);
        txn6.begin();
        
        // Add custom property to record with test constraint
        
        String aspectName = CustomisableRmElement.RECORD.getCorrespondingAspect();
        
        String propLocalName = "myProp-"+testRunID;
        
        QName dataType = DataTypeDefinition.TEXT;
        String propTitle = "My property title";
        String description = "My property description";
        String defaultValue = null;
        boolean multiValued = false;
        boolean mandatory = false;
        boolean isProtected = false;
        
        rmAdminService.addCustomPropertyDefinition(null, aspectName, propLocalName, dataType, propTitle, description, defaultValue, multiValued, mandatory, isProtected, testCon);
        
        txn6.commit();
    }
}
