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

import java.io.IOException;
import java.io.Serializable;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.transaction.UserTransaction;

import org.alfresco.model.ContentModel;
import org.alfresco.module.org_alfresco_module_dod5015.DOD5015Model;
import org.alfresco.module.org_alfresco_module_dod5015.DispositionAction;
import org.alfresco.module.org_alfresco_module_dod5015.RecordsManagementAdminServiceImpl;
import org.alfresco.module.org_alfresco_module_dod5015.RecordsManagementModel;
import org.alfresco.module.org_alfresco_module_dod5015.RecordsManagementService;
import org.alfresco.module.org_alfresco_module_dod5015.action.RecordsManagementActionService;
import org.alfresco.module.org_alfresco_module_dod5015.action.impl.CompleteEventAction;
import org.alfresco.module.org_alfresco_module_dod5015.action.impl.DefineCustomElementAbstractAction;
import org.alfresco.repo.content.MimetypeMap;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.web.scripts.BaseWebScriptTest;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.dictionary.AspectDefinition;
import org.alfresco.service.cmr.dictionary.DataTypeDefinition;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.ContentWriter;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.cmr.view.ImporterService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.service.namespace.RegexQNamePattern;
import org.alfresco.service.transaction.TransactionService;
import org.alfresco.util.ISO8601DateFormat;
import org.alfresco.web.scripts.TestWebScriptServer.GetRequest;
import org.alfresco.web.scripts.TestWebScriptServer.PostRequest;
import org.alfresco.web.scripts.TestWebScriptServer.Response;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;
import org.json.JSONTokener;

/**
 * This class tests the Rest API for the submission of RM actions.
 * 
 * @author Neil McErlean
 */
public class RmRestApiTest extends BaseWebScriptTest implements RecordsManagementModel
{
    protected static final String GET_TRANSFER_URL_FORMAT = "/api/node/{0}/transfers/{1}";
    protected static final String RMA_ACTIONS_URL = "/api/rma/actions/ExecutionQueue";
    protected static final String APPLICATION_JSON = "application/json";
    protected static final String RMA_CUSTOM_ASSOCS_URL = "/api/rma/admin/customassociationdefinitions";
    protected static final String RMA_CUSTOM_PROPS_URL = "/api/rma/admin/custompropertydefinitions";
    protected NamespaceService namespaceService;
    protected NodeService nodeService;
    protected ContentService contentService;
    protected DictionaryService dictionaryService;
    protected SearchService searchService;
    protected ImporterService importService;
    protected TransactionService transactionService;
    protected ServiceRegistry services;
    protected RecordsManagementService rmService;
    protected RecordsManagementActionService rmActionService;
    

    @Override
    protected void setUp() throws Exception
    {
        BaseWebScriptTest.setCustomContext("classpath:org/alfresco/module/org_alfresco_module_dod5015/test/test-context.xml");
        
        super.setUp();
        this.namespaceService = (NamespaceService) getServer().getApplicationContext().getBean("NamespaceService");
        this.nodeService = (NodeService) getServer().getApplicationContext().getBean("NodeService");
        this.contentService = (ContentService)getServer().getApplicationContext().getBean("ContentService");
        this.dictionaryService = (DictionaryService)getServer().getApplicationContext().getBean("DictionaryService");
        this.searchService = (SearchService)getServer().getApplicationContext().getBean("SearchService");
        this.importService = (ImporterService)getServer().getApplicationContext().getBean("ImporterService");
        this.transactionService = (TransactionService)getServer().getApplicationContext().getBean("TransactionService");
        this.services = (ServiceRegistry)getServer().getApplicationContext().getBean("ServiceRegistry");
        this.rmService = (RecordsManagementService)getServer().getApplicationContext().getBean("RecordsManagementService");
        this.rmActionService = (RecordsManagementActionService)getServer().getApplicationContext().getBean("RecordsManagementActionService");
        
        AuthenticationUtil.setFullyAuthenticatedUser(AuthenticationUtil.getSystemUserName());

        // Bring the filePlan into the test database.
        //
        // This is quite a slow call, so if this class grew to have many test methods,
        // there would be a real benefit in using something like @BeforeClass for the line below.
        TestUtilities.loadFilePlanData(null, this.nodeService, this.importService, this.services.getPermissionService());
    }

    /**
     * This test method ensures that a POST of an RM action to a non-existent node
     * will result in a 404 status.
     * 
     * @throws Exception
     */
    // TODO taken out for now
    public void xtestPostActionToNonExistentNode() throws Exception
    {
        NodeRef recordCategory = TestUtilities.getRecordCategory(searchService, "Reports", "AIS Audit Records");     
        assertNotNull(recordCategory);

        NodeRef nonExistentNode = new NodeRef("workspace://SpacesStore/09ca1e02-1c87-4a53-97e7-xxxxxxxxxxxx");
        
        // Construct the JSON request.
        JSONObject jsonPostData = new JSONObject();
        jsonPostData.put("nodeRef", nonExistentNode.toString());
        // Although the request specifies a 'reviewed' action, it does not matter what
        // action is specified here, as the non-existent Node should trigger a 404
        // before the action is executed.
        jsonPostData.put("name", "reviewed");
        
        // Submit the JSON request.
        String jsonPostString = jsonPostData.toString();
        
        final int expectedStatus = 404;
        sendRequest(new PostRequest(RMA_ACTIONS_URL, jsonPostString, APPLICATION_JSON), expectedStatus);
    }

    public void testPostReviewedAction() throws IOException, JSONException
    {
        // Get the recordCategory under which we will create the testNode.
        NodeRef recordCategory = TestUtilities.getRecordCategory(searchService, "Reports", "AIS Audit Records");     
        assertNotNull(recordCategory);

        NodeRef recordFolder = TestUtilities.getRecordFolder(searchService, "Reports", "AIS Audit Records", "January AIS Audit Records");
        assertNotNull(recordFolder);

        // Create a testNode/file which is to be declared as a record.
        NodeRef testRecord = this.nodeService.createNode(recordFolder, 
                ContentModel.ASSOC_CONTAINS, 
                QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, "MyRecord.txt"),
                ContentModel.TYPE_CONTENT).getChildRef();

        // Set some dummy content.
        ContentWriter writer = this.contentService.getWriter(testRecord, ContentModel.PROP_CONTENT, true);
        writer.setMimetype(MimetypeMap.MIMETYPE_TEXT_PLAIN);
        writer.setEncoding("UTF-8");
        writer.putContent("There is some content in this record");
        
        // In this test, this property has a date-value equal to the model import time.
        Serializable pristineReviewAsOf = this.nodeService.getProperty(testRecord, PROP_REVIEW_AS_OF);
        
        // Construct the JSON request for 'reviewed'.
        String jsonString = new JSONStringer().object()
            .key("name").value("reviewed")
            .key("nodeRef").value(testRecord.toString())
            // These JSON params are just to test the submission of params. They'll be ignored.
            .key("params").object()
                .key("param1").value("one")
                .key("param2").value("two")
            .endObject()
        .endObject()
        .toString();
        
        // Submit the JSON request.
        final int expectedStatus = 200;
        Response rsp = sendRequest(new PostRequest(RMA_ACTIONS_URL,
                                 jsonString, APPLICATION_JSON), expectedStatus);
        
        String rspContent = rsp.getContentAsString();
        assertTrue(rspContent.contains("Successfully queued action [reviewed]"));
        
        Serializable newReviewAsOfDate = this.nodeService.getProperty(testRecord, PROP_REVIEW_AS_OF);
        assertFalse("The reviewAsOf property should have changed. Was " + pristineReviewAsOf,
        		pristineReviewAsOf.equals(newReviewAsOfDate));
    }

    public void testPostMultiReviewedAction() throws IOException, JSONException
    {
        // Get the recordCategory under which we will create the testNode.
        NodeRef recordCategory = TestUtilities.getRecordCategory(searchService, "Reports", "AIS Audit Records");     
        assertNotNull(recordCategory);

        NodeRef recordFolder = TestUtilities.getRecordFolder(searchService, "Reports", "AIS Audit Records", "January AIS Audit Records");
        assertNotNull(recordFolder);

        // Create a testNode/file which is to be declared as a record.
        NodeRef testRecord = this.nodeService.createNode(recordFolder, 
                ContentModel.ASSOC_CONTAINS, 
                QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, "MyRecord.txt"),
                ContentModel.TYPE_CONTENT).getChildRef();

        // Set some dummy content.
        ContentWriter writer = this.contentService.getWriter(testRecord, ContentModel.PROP_CONTENT, true);
        writer.setMimetype(MimetypeMap.MIMETYPE_TEXT_PLAIN);
        writer.setEncoding("UTF-8");
        writer.putContent("There is some content in this record");
        
        NodeRef testRecord2 = this.nodeService.createNode(recordFolder, 
                ContentModel.ASSOC_CONTAINS, 
                QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, "MyRecord2.txt"),
                ContentModel.TYPE_CONTENT).getChildRef();

        // Set some dummy content.
        writer = this.contentService.getWriter(testRecord2, ContentModel.PROP_CONTENT, true);
        writer.setMimetype(MimetypeMap.MIMETYPE_TEXT_PLAIN);
        writer.setEncoding("UTF-8");
        writer.putContent("There is some content in this record");
        
        NodeRef testRecord3 = this.nodeService.createNode(recordFolder, 
                ContentModel.ASSOC_CONTAINS, 
                QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, "MyRecord3.txt"),
                ContentModel.TYPE_CONTENT).getChildRef();

        // Set some dummy content.
        writer = this.contentService.getWriter(testRecord3, ContentModel.PROP_CONTENT, true);
        writer.setMimetype(MimetypeMap.MIMETYPE_TEXT_PLAIN);
        writer.setEncoding("UTF-8");
        writer.putContent("There is some content in this record");
        
        // In this test, this property has a date-value equal to the model import time.
        Serializable pristineReviewAsOf = this.nodeService.getProperty(testRecord, PROP_REVIEW_AS_OF);
        Serializable pristineReviewAsOf2 = this.nodeService.getProperty(testRecord2, PROP_REVIEW_AS_OF);
        Serializable pristineReviewAsOf3 = this.nodeService.getProperty(testRecord3, PROP_REVIEW_AS_OF);
        
        // Construct the JSON request for 'reviewed'.
        String jsonString = new JSONStringer().object()
            .key("name").value("reviewed")
            .key("nodeRefs").array()    
                .value(testRecord.toString())
                .value(testRecord2.toString())
                .value(testRecord3.toString())
                .endArray()
            // These JSON params are just to test the submission of params. They'll be ignored.
            .key("params").object()
                .key("param1").value("one")
                .key("param2").value("two")
            .endObject()
        .endObject()
        .toString();
        
        // Submit the JSON request.
        final int expectedStatus = 200;
        Response rsp = sendRequest(new PostRequest(RMA_ACTIONS_URL,
                                 jsonString, APPLICATION_JSON), expectedStatus);
        
        String rspContent = rsp.getContentAsString();
        assertTrue(rspContent.contains("Successfully queued action [reviewed]"));
        
        Serializable newReviewAsOfDate = this.nodeService.getProperty(testRecord, PROP_REVIEW_AS_OF);
        assertFalse("The reviewAsOf property should have changed. Was " + pristineReviewAsOf,
                pristineReviewAsOf.equals(newReviewAsOfDate));
        Serializable newReviewAsOfDate2 = this.nodeService.getProperty(testRecord2, PROP_REVIEW_AS_OF);
        assertFalse("The reviewAsOf property should have changed. Was " + pristineReviewAsOf2,
                pristineReviewAsOf2.equals(newReviewAsOfDate2));
        Serializable newReviewAsOfDate3 = this.nodeService.getProperty(testRecord3, PROP_REVIEW_AS_OF);
        assertFalse("The reviewAsOf property should have changed. Was " + pristineReviewAsOf3,
                pristineReviewAsOf3.equals(newReviewAsOfDate3));
    }
    
    public void testActionParams() throws Exception
    {
     // Construct the JSON request for 'reviewed'.
        String jsonString = new JSONStringer().object()
            .key("name").value("testActionParams")
            .key("nodeRef").array()    
                .value("nothing://nothing/nothing")
                .endArray()
            // These JSON params are just to test the submission of params. They'll be ignored.
            .key("params").object()
                .key(TestActionParams.PARAM_DATE).object()
                    .key("iso8601")
                    .value(ISO8601DateFormat.format(new Date()))
                    .endObject()
            .endObject()
        .endObject()
        .toString();
        
        // Submit the JSON request.
        final int expectedStatus = 200;
        //TODO Currently failing unit test.
//        Response rsp = sendRequest(new PostRequest(RMA_ACTIONS_URL,
//                                 jsonString, APPLICATION_JSON), expectedStatus);
    }
    
    public void testPostCustomAssoc() throws IOException, JSONException
    {
        NodeRef customModelNodeRef = DefineCustomElementAbstractAction.RM_CUSTOM_MODEL_NODE_REF;
        // Construct the JSON request for 'defineCustomAssociation'.
        // 1. Child association.
        final String childAssocName = "rmc:customAssocChild" + System.currentTimeMillis();

        String jsonString = new JSONStringer().object()
            .key("name").value("defineCustomAssociation")
            .key("nodeRef").value(customModelNodeRef.toString()) // The nodeRef doesn't matter!
            .key("params").object()
                .key("name").value(childAssocName)
                .key("isChild").value(true)
                .key("title").value("Supersedes link")
                .key("description").value("Descriptive text")
                .key("sourceRoleName").value("superseding")
                .key("targetRoleName").value("superseded")
                .key("sourceMandatory").value(true)
                .key("targetMandatory").value(true)
                .key("targetMandatoryEnforced").value(true)
                .key("sourceMany").value(false)
                .key("targetMany").value(false)
                // Have left out 'protected'.
            .endObject()
        .endObject()
        .toString();
        
        // Submit the JSON request.
        final int expectedStatus = 200;
        Response rsp = sendRequest(new PostRequest(RMA_ACTIONS_URL,
                                 jsonString, APPLICATION_JSON), expectedStatus);
        
        String rspContent = rsp.getContentAsString();
        assertTrue(rspContent.contains("Successfully queued action [defineCustomAssociation]"));

        // 2. Non-child or standard association.
        final String stdAssocName = "rmc:customAssocStandard" + System.currentTimeMillis();
        
        String secondJsonString = new JSONStringer().object()
            .key("name").value("defineCustomAssociation")
            .key("nodeRef").value(customModelNodeRef.toString()) // The nodeRef doesn't matter!
            .key("params").object()
                .key("name").value(stdAssocName)
                .key("isChild").value(false)
                .key("title").value("Cross-references link")
                .key("description").value("cross")
                .key("sourceRoleName").value("cross-referencing")
                .key("targetRoleName").value("cross-referenced")
                .key("sourceMandatory").value(true)
                .key("targetMandatory").value(true)
                .key("targetMandatoryEnforced").value(true)
                .key("sourceMany").value(false)
                .key("targetMany").value(false)
                // Have left out 'protected'.
            .endObject()
        .endObject()
        .toString();
        
        // Submit the JSON request.
        rsp = sendRequest(new PostRequest(RMA_ACTIONS_URL,
                secondJsonString, APPLICATION_JSON), expectedStatus);
        
        rspContent = rsp.getContentAsString();
        assertTrue(rspContent.contains("Successfully queued action [defineCustomAssociation]"));
        
        AspectDefinition customAssocsAspect =
            dictionaryService.getAspect(QName.createQName(RecordsManagementAdminServiceImpl.RMC_CUSTOM_ASSOCS, namespaceService));
        assertNotNull("Missing customAssocs aspect", customAssocsAspect);
        QName newAssocQname = QName.createQName(childAssocName, namespaceService);
        assertTrue("New custom assoc not returned by dataDictionary.", customAssocsAspect.getAssociations().containsKey(newAssocQname));
    }

    public void testGetCustomAssociations() throws IOException, JSONException
    {
        // Ensure that there is at least one custom association in the model.
        this.testPostCustomAssoc();
        
        final int expectedStatus = 200;
        Response rsp = sendRequest(new GetRequest(RMA_CUSTOM_ASSOCS_URL), expectedStatus);

        JSONObject jsonRsp = new JSONObject(new JSONTokener(rsp.getContentAsString()));

        JSONObject dataObj = (JSONObject)jsonRsp.get("data");
        assertNotNull("JSON 'data' object was null", dataObj);
        
        JSONObject customAssocsObj = (JSONObject)dataObj.get("customAssociations");
        assertNotNull("JSON 'customAssocsObj' object was null", customAssocsObj);
        
        final int customAssocsCount = customAssocsObj.length();
        assertTrue("There should be at least one custom association. Found " + customAssocsObj, customAssocsCount > 0);
    }
    
    public void testPostCustomProperty() throws Exception
    {
        final String propertyName = "rmc:customProperty" + System.currentTimeMillis();
        
        String jsonString = new JSONStringer().object()
            .key("name").value(propertyName)
            .key("title").value("Custom test property")
            .key("description").value("Dynamically defined test property")
            .key("mandatory").value(false)
            .key("dataType").value(DataTypeDefinition.BOOLEAN)
            .key("element").value("record")
        .endObject()
        .toString();
        
        // Submit the JSON request.
        final int expectedStatus = 200;
        Response rsp = sendRequest(new PostRequest("/api/rma/admin/custompropertydefinitions?element=record",
                                 jsonString, APPLICATION_JSON), expectedStatus);
        
        String rspContent = rsp.getContentAsString();
        assertTrue(rspContent.contains("success"));
    }

    public void testGetCustomProperties() throws Exception
    {
        // Ensure that there is at least one custom property.
        this.testPostCustomProperty();

        final int expectedStatus = 200;
        Response rsp = sendRequest(new GetRequest("/api/rma/admin/custompropertydefinitions?element=record"), expectedStatus);

        JSONObject jsonRsp = new JSONObject(new JSONTokener(rsp.getContentAsString()));

        JSONObject dataObj = (JSONObject)jsonRsp.get("data");
        assertNotNull("JSON 'data' object was null", dataObj);
        
        JSONObject customPropsObj = (JSONObject)dataObj.get("customProperties");
        assertNotNull("JSON 'customProperties' object was null", customPropsObj);

        final int customPropsCount = customPropsObj.length();
        assertTrue("There should be at least one custom property. Found " + customPropsObj, customPropsCount > 0);
    }
    
    public void testExport() throws Exception
    {
        NodeRef recordFolder1 = TestUtilities.getRecordFolder(searchService, "Reports", 
                    "AIS Audit Records", "January AIS Audit Records");
        assertNotNull(recordFolder1);
        
        NodeRef recordFolder2 = TestUtilities.getRecordFolder(searchService, "Reports", 
                    "Unit Manning Documents", "1st Quarter Unit Manning Documents");
        assertNotNull(recordFolder2);
        
        String exportUrl = "/api/rma/admin/export";
        
        // define JSON POST body
        JSONObject jsonPostData = new JSONObject();
        JSONArray nodeRefs = new JSONArray();
        nodeRefs.put(recordFolder1.toString());
        nodeRefs.put(recordFolder2.toString());
        jsonPostData.put("nodeRefs", nodeRefs);
        String jsonPostString = jsonPostData.toString();
        
        // make the export request
        Response rsp = sendRequest(new PostRequest(exportUrl, jsonPostString, APPLICATION_JSON), 200);
        assertEquals("application/acp", rsp.getContentType());
    }
    
    public void testTransfer() throws Exception
    {
        // Test 404 status for non existent node
        String transferId = "yyy";
        String nonExistentNode = "workspace/SpacesStore/09ca1e02-1c87-4a53-97e7-xxxxxxxxxxxx";
        String nonExistentUrl = MessageFormat.format(GET_TRANSFER_URL_FORMAT, nonExistentNode, transferId);
        Response rsp = sendRequest(new GetRequest(nonExistentUrl), 404);
        
        // Test 400 status for node that isn't a file plan
        NodeRef series = TestUtilities.getRecordSeries(searchService, "Reports");
        assertNotNull(series);
        String seriesNodeUrl = series.toString().replace("://", "/");
        String wrongNodeUrl = MessageFormat.format(GET_TRANSFER_URL_FORMAT, seriesNodeUrl, transferId);
        rsp = sendRequest(new GetRequest(wrongNodeUrl), 400);
        
        // Test 404 status for file plan with no transfers
        NodeRef rootNode = this.rmService.getRecordsManagementRoot(series);
        String rootNodeUrl = rootNode.toString().replace("://", "/");
        String transferUrl = MessageFormat.format(GET_TRANSFER_URL_FORMAT, rootNodeUrl, transferId);
        rsp = sendRequest(new GetRequest(transferUrl), 404);
        
        // Get test in state where a transfer will be present
        NodeRef recordCategory = TestUtilities.getRecordCategory(searchService, "Civilian Files", "Foreign Employee Award Files");    
        assertNotNull(recordCategory);
        
        UserTransaction txn = transactionService.getUserTransaction(false);
        txn.begin();
        
        NodeRef newRecordFolder = this.nodeService.createNode(recordCategory, ContentModel.ASSOC_CONTAINS, 
                    QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, QName.createValidLocalName("recordFolder")), 
                    DOD5015Model.TYPE_RECORD_FOLDER).getChildRef();
        
        txn.commit();
        txn = transactionService.getUserTransaction(false);
        txn.begin();
        
        // Create the document
        NodeRef recordOne = this.nodeService.createNode(newRecordFolder, 
                                                        ContentModel.ASSOC_CONTAINS, 
                                                        QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, "record"), 
                                                        ContentModel.TYPE_CONTENT).getChildRef();
        
        // Set the content
        ContentWriter writer = this.contentService.getWriter(recordOne, ContentModel.PROP_CONTENT, true);
        writer.setMimetype(MimetypeMap.MIMETYPE_TEXT_PLAIN);
        writer.setEncoding("UTF-8");
        writer.putContent("There is some content in this record");
        txn.commit();
        
        // declare the new record
        txn = transactionService.getUserTransaction(false);
        txn.begin();
        declareRecord(recordOne);
        
        // prepare for the transfer
        Map<String, Serializable> params = new HashMap<String, Serializable>(3);
        params.put(CompleteEventAction.PARAM_EVENT_NAME, "case_complete");
        params.put(CompleteEventAction.PARAM_EVENT_COMPLETED_AT, new Date());
        params.put(CompleteEventAction.PARAM_EVENT_COMPLETED_BY, "gavinc");
        this.rmActionService.executeRecordsManagementAction(newRecordFolder, "completeEvent", params);
        this.rmActionService.executeRecordsManagementAction(newRecordFolder, "cutoff");
        
        DispositionAction da = this.rmService.getNextDispositionAction(newRecordFolder);
        assertNotNull(da);
        assertEquals("transfer", da.getName());
        txn.commit();
        
        // Clock the asOf date back to ensure eligibility
        txn = transactionService.getUserTransaction(false);
        txn.begin();
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        this.nodeService.setProperty(da.getNodeRef(), PROP_DISPOSITION_AS_OF, calendar.getTime());
        
        // Do the transfer
        this.rmActionService.executeRecordsManagementAction(newRecordFolder, "transfer", null);
        txn.commit();
        
        // check that there is a transfer object present
        List<ChildAssociationRef> assocs = this.nodeService.getChildAssocs(rootNode, ASSOC_TRANSFERS, RegexQNamePattern.MATCH_ALL);
        assertNotNull(assocs);
        assertTrue(assocs.size() > 0);
        
        // Test 404 status for file plan with transfers but not the requested one
        rootNode = this.rmService.getRecordsManagementRoot(newRecordFolder);
        rootNodeUrl = rootNode.toString().replace("://", "/");
        transferUrl = MessageFormat.format(GET_TRANSFER_URL_FORMAT, rootNodeUrl, transferId);
        rsp = sendRequest(new GetRequest(transferUrl), 404);
        
        // retrieve the id of the first transfer
        NodeRef transferNodeRef = assocs.get(0).getChildRef();
        
        // Test successful retrieval of transfer archive
        transferId = transferNodeRef.getId();
        transferUrl = MessageFormat.format(GET_TRANSFER_URL_FORMAT, rootNodeUrl, transferId);
        rsp = sendRequest(new GetRequest(transferUrl), 200);
        assertEquals("application/acp", rsp.getContentType());
    }
    
    private void declareRecord(NodeRef recordOne)
    {
        // Declare record
        Map<QName, Serializable> propValues = this.nodeService.getProperties(recordOne);        
        propValues.put(RecordsManagementModel.PROP_PUBLICATION_DATE, new Date());       
        List<String> smList = new ArrayList<String>(2);
        smList.add("FOUO");
        smList.add("NOFORN");
        propValues.put(RecordsManagementModel.PROP_SUPPLEMENTAL_MARKING_LIST, (Serializable)smList);        
        propValues.put(RecordsManagementModel.PROP_MEDIA_TYPE, "mediaTypeValue"); 
        propValues.put(RecordsManagementModel.PROP_FORMAT, "formatValue"); 
        propValues.put(RecordsManagementModel.PROP_DATE_RECEIVED, new Date());       
        propValues.put(RecordsManagementModel.PROP_ORIGINATOR, "origValue");
        propValues.put(RecordsManagementModel.PROP_ORIGINATING_ORGANIZATION, "origOrgValue");
        propValues.put(ContentModel.PROP_TITLE, "titleValue");
        this.nodeService.setProperties(recordOne, propValues);
        this.rmActionService.executeRecordsManagementAction(recordOne, "declareRecord");        
    }
}
