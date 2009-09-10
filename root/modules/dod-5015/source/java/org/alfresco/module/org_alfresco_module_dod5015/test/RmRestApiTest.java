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
import java.io.UnsupportedEncodingException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.transaction.UserTransaction;

import org.alfresco.model.ContentModel;
import org.alfresco.module.org_alfresco_module_dod5015.DOD5015Model;
import org.alfresco.module.org_alfresco_module_dod5015.DispositionAction;
import org.alfresco.module.org_alfresco_module_dod5015.RecordsManagementAdminService;
import org.alfresco.module.org_alfresco_module_dod5015.RecordsManagementAdminServiceImpl;
import org.alfresco.module.org_alfresco_module_dod5015.RecordsManagementModel;
import org.alfresco.module.org_alfresco_module_dod5015.RecordsManagementService;
import org.alfresco.module.org_alfresco_module_dod5015.action.RecordsManagementActionService;
import org.alfresco.module.org_alfresco_module_dod5015.action.impl.CompleteEventAction;
import org.alfresco.module.org_alfresco_module_dod5015.script.CustomReferenceType;
import org.alfresco.repo.content.MimetypeMap;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.web.scripts.BaseWebScriptTest;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.dictionary.AspectDefinition;
import org.alfresco.service.cmr.dictionary.AssociationDefinition;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.ContentWriter;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.search.ResultSet;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.cmr.view.ImporterService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.service.namespace.RegexQNamePattern;
import org.alfresco.service.transaction.TransactionService;
import org.alfresco.util.ISO8601DateFormat;
import org.alfresco.web.scripts.TestWebScriptServer.DeleteRequest;
import org.alfresco.web.scripts.TestWebScriptServer.GetRequest;
import org.alfresco.web.scripts.TestWebScriptServer.PostRequest;
import org.alfresco.web.scripts.TestWebScriptServer.PutRequest;
import org.alfresco.web.scripts.TestWebScriptServer.Response;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;
import org.json.JSONTokener;

/**
 * This class tests the Rest API for RM.
 * 
 * @author Neil McErlean
 */
public class RmRestApiTest extends BaseWebScriptTest implements RecordsManagementModel
{
    protected static final String GET_NODE_AUDITLOG_URL_FORMAT = "/api/node/{0}/rmauditlog";
    protected static final String GET_TRANSFER_URL_FORMAT = "/api/node/{0}/transfers/{1}";
    protected static final String REF_INSTANCES_URL_FORMAT = "/api/node/{0}/customreferences";
    protected static final String RMA_AUDITLOG_URL = "/api/rma/admin/rmauditlog";
    protected static final String RMA_ACTIONS_URL = "/api/rma/actions/ExecutionQueue";
    protected static final String APPLICATION_JSON = "application/json";
    protected static final String RMA_CUSTOM_PROPS_DEFINITIONS_URL = "/api/rma/admin/custompropertydefinitions";
    protected static final String RMA_CUSTOM_REFS_DEFINITIONS_URL = "/api/rma/admin/customreferencedefinitions";
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
    protected RecordsManagementAdminService rmAdminService;

    private static final String BI_DI = "BiDi";
    private static final String CHILD_SRC = "childSrc";
    private static final String CHILD_TGT = "childTgt";
    
    @Override
    protected void setUp() throws Exception
    {
        setCustomContext("classpath:org/alfresco/module/org_alfresco_module_dod5015/test/test-context.xml");
        
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
        this.rmAdminService = (RecordsManagementAdminService)getServer().getApplicationContext().getBean("RecordsManagementAdminService");
        
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
    
    public void testPostCustomReferences() throws IOException, JSONException
    {
        postCustomReferences();
    }

    /**
     * This method creates a child and a non-child reference and returns their generated ids.
     * 
     * 
     * @return String[] with element 0 = refId of p/c ref, 1 = refId pf bidi.
     */
	private String[] postCustomReferences() throws JSONException, IOException,
			UnsupportedEncodingException {
	    String[] result = new String[2];
		
		// 1. Child association.
        String jsonString = new JSONStringer().object()
            .key("referenceType").value(CustomReferenceType.PARENT_CHILD)
            .key("source").value(CHILD_SRC)
            .key("target").value(CHILD_TGT)
        .endObject()
        .toString();
        
        // Submit the JSON request.
        final int expectedStatus = 200;
        Response rsp = sendRequest(new PostRequest(RMA_CUSTOM_REFS_DEFINITIONS_URL,
                                 jsonString, APPLICATION_JSON), expectedStatus);
        
        String rspContent = rsp.getContentAsString();
        assertTrue(rspContent.contains("success"));

        JSONObject jsonRsp = new JSONObject(new JSONTokener(rspContent));
        String generatedChildRefId = jsonRsp.getJSONObject("data").getString("refId");
        result[0] = generatedChildRefId;

        // 2. Non-child or standard association.
        jsonString = new JSONStringer().object()
            .key("referenceType").value(CustomReferenceType.BIDIRECTIONAL)
            .key("label").value(BI_DI)
        .endObject()
        .toString();
        
        // Submit the JSON request.
        rsp = sendRequest(new PostRequest(RMA_CUSTOM_REFS_DEFINITIONS_URL,
                                 jsonString, APPLICATION_JSON), expectedStatus);
        
        rspContent = rsp.getContentAsString();
        assertTrue(rspContent.contains("success"));
        System.out.println(rspContent);

        jsonRsp = new JSONObject(new JSONTokener(rspContent));
        String generatedBidiRefId = jsonRsp.getJSONObject("data").getString("refId");
        result[1] = generatedBidiRefId;


        // Now assert that both have appeared in the data dictionary.
        AspectDefinition customAssocsAspect =
            dictionaryService.getAspect(QName.createQName(RecordsManagementAdminServiceImpl.RMC_CUSTOM_ASSOCS, namespaceService));
        assertNotNull("Missing customAssocs aspect", customAssocsAspect);
        
        QName newRefQname = rmAdminService.getQNameForClientId(generatedChildRefId);
        Map<QName, AssociationDefinition> associations = customAssocsAspect.getAssociations();
		assertTrue("Custom child assoc not returned by dataDictionary.", associations.containsKey(newRefQname));

        newRefQname = rmAdminService.getQNameForClientId(generatedBidiRefId);
        assertTrue("Custom std assoc not returned by dataDictionary.", customAssocsAspect.getAssociations().containsKey(newRefQname));
        
        return result;
	}

    public void testPutCustomPropertyDefinition() throws Exception
    {
        final String propertyLabel = "Original label åçîéøü";
        String propId = postCustomPropertyDefinition(propertyLabel, null);
        
        // PUT specifies only an updated label or a new constraint ref.
        final String updatedLabel = "Updated label πø^¨¥†®";
        final String updatedConstraint = "rmc:tlList";
        String jsonString = new JSONStringer().object()
            .key("label").value(updatedLabel)
            .key("constraintRef").value(updatedConstraint)
        .endObject()
        .toString();
    
        String propDefnUrl = "/api/rma/admin/custompropertydefinitions/" + propId;
        Response rsp = sendRequest(new PutRequest(propDefnUrl,
                                 jsonString, APPLICATION_JSON), 200);
    
        String rspContent = rsp.getContentAsString();
        System.out.println(rspContent);
    
        JSONObject jsonRsp = new JSONObject(new JSONTokener(rspContent));
        String urlOfNewPropDef = jsonRsp.getString("url");
        assertNotNull("urlOfNewPropDef was null.", urlOfNewPropDef);
    
        // GET from the URL again to ensure it's valid
        rsp = sendRequest(new GetRequest(propDefnUrl), 200);
        rspContent = rsp.getContentAsString();
        
        System.out.println(rspContent);
        
        jsonRsp = new JSONObject(new JSONTokener(rspContent));
        JSONObject dataObject = jsonRsp.getJSONObject("data");
        assertNotNull("JSON data object was null", dataObject);
        JSONObject customPropsObject = dataObject.getJSONObject("customProperties");
        assertNotNull("JSON customProperties object was null", customPropsObject);
        assertEquals("Wrong customProperties length.", 1, customPropsObject.length());
        
        Object keyToSoleProp = customPropsObject.keys().next();
        
        JSONObject newPropObject = customPropsObject.getJSONObject((String)keyToSoleProp);
        assertEquals("Wrong property label.", updatedLabel, newPropObject.getString("label"));
        JSONArray constraintRefsArray = newPropObject.getJSONArray("constraintRefs");
        assertEquals("ConstraintRefsArray wrong length.", 1, constraintRefsArray.length());
        String recoveredConstraintTitle = constraintRefsArray.getJSONObject(0).getString("title");
        assertEquals("Wrong constraint.", "Transfer Locations", recoveredConstraintTitle);
    }

    public void testGetCustomReferences() throws IOException, JSONException
    {
        // Ensure that there is at least one custom reference.
        postCustomReferences();

        // GET all custom reference definitions
        final int expectedStatus = 200;
        Response rsp = sendRequest(new GetRequest(RMA_CUSTOM_REFS_DEFINITIONS_URL), expectedStatus);

        JSONObject jsonRsp = new JSONObject(new JSONTokener(rsp.getContentAsString()));

        JSONObject dataObj = (JSONObject)jsonRsp.get("data");
        assertNotNull("JSON 'data' object was null", dataObj);
        
        JSONArray customRefsObj = (JSONArray)dataObj.get("customReferences");
        assertNotNull("JSON 'customReferences' object was null", customRefsObj);
        
        for (int i = 0; i < customRefsObj.length(); i++) {
            System.out.println(customRefsObj.getString(i));
        }

        assertTrue("There should be at least two custom references. Found " + customRefsObj, customRefsObj.length() >= 2);

        // GET a specific custom reference definition.
        // Here, we're using one of the built-in references
        // qname = rmc:versions
        rsp = sendRequest(new GetRequest(RMA_CUSTOM_REFS_DEFINITIONS_URL + "/" + "versions"), expectedStatus);

        jsonRsp = new JSONObject(new JSONTokener(rsp.getContentAsString()));

        dataObj = (JSONObject)jsonRsp.get("data");
        assertNotNull("JSON 'data' object was null", dataObj);
        
        customRefsObj = (JSONArray)dataObj.get("customReferences");
        assertNotNull("JSON 'customProperties' object was null", customRefsObj);

        assertTrue("There should be exactly 1 custom references. Found " + customRefsObj.length(), customRefsObj.length() == 1);
    }

    public void testGetAndDeleteCustomReferenceInstances() throws Exception
    {
    	// Create test records.
        NodeRef recordFolder = retrievePreexistingRecordFolder();
        NodeRef testRecord1 = createRecord(recordFolder, "testRecord1" + System.currentTimeMillis());
        NodeRef testRecord2 = createRecord(recordFolder, "testRecord2" + System.currentTimeMillis());

        String node1Url = testRecord1.toString().replace("://", "/");
        String refInstancesUrl = MessageFormat.format(REF_INSTANCES_URL_FORMAT, node1Url);

        // Create reference types.
        String[] generatedRefIds = postCustomReferences();

        // Add a standard ref
        String jsonString = new JSONStringer().object()
            .key("toNode").value(testRecord2.toString())
            .key("refId").value(generatedRefIds[1])
        .endObject()
        .toString();
    
        Response rsp = sendRequest(new PostRequest(refInstancesUrl,
	                             jsonString, APPLICATION_JSON), 200);
        System.out.println(rsp.getContentAsString());

	    // Add a child ref
	    jsonString = new JSONStringer().object()
	    .key("toNode").value(testRecord2.toString())
	    .key("refId").value(generatedRefIds[0])
	    .endObject()
	    .toString();
	    
	    rsp = sendRequest(new PostRequest(refInstancesUrl,
	    		jsonString, APPLICATION_JSON), 200);
        System.out.println(rsp.getContentAsString());
	    
	    
        // Now retrieve the applied references from the REST API
        rsp = sendRequest(new GetRequest(refInstancesUrl), 200);

        JSONObject jsonRsp = new JSONObject(new JSONTokener(rsp.getContentAsString()));
        System.out.println(jsonRsp);
        
        JSONObject dataObj = (JSONObject)jsonRsp.get("data");
        assertNotNull("JSON 'data' object was null", dataObj);
        
        JSONArray customRefsArray = (JSONArray)dataObj.get("customReferences");
        assertNotNull("JSON 'customReferences' object was null", customRefsArray);

        for (int i = 0; i < customRefsArray.length(); i++) {
            System.out.println(customRefsArray.get(i));
        }
        final int customRefsCount = customRefsArray.length();
        assertTrue("There should be at least one custom reference. Found " + customRefsArray, customRefsCount > 0);
        
        // Now to delete a reference instance of each type
        String protocol = testRecord2.getStoreRef().getProtocol();
        String identifier = testRecord2.getStoreRef().getIdentifier();
        String recId = testRecord2.getId();
        final String queryFormat = "?st={0}&si={1}&id={2}";
        String urlQueryString = MessageFormat.format(queryFormat, protocol, identifier, recId);

        rsp = sendRequest(new DeleteRequest(refInstancesUrl + "/" + generatedRefIds[1] + urlQueryString), 200);
        assertTrue(rsp.getContentAsString().contains("success"));

        rsp = sendRequest(new DeleteRequest(refInstancesUrl + "/"
        		+ generatedRefIds[0]
        		+ urlQueryString), 200);
        assertTrue(rsp.getContentAsString().contains("success"));
        
        // Get the reference instances back and confirm they've been removed.
        rsp = sendRequest(new GetRequest(refInstancesUrl), 200);

        jsonRsp = new JSONObject(new JSONTokener(rsp.getContentAsString()));
        
        dataObj = (JSONObject)jsonRsp.get("data");
        assertNotNull("JSON 'data' object was null", dataObj);
        
        customRefsArray = (JSONArray)dataObj.get("customReferences");
        assertNotNull("JSON 'customReferences' object was null", customRefsArray);
        assertTrue("customRefsArray was unexpectedly not empty.", customRefsArray.length() == 0);
    }
    
    public void testPostCustomPropertyDefinition() throws Exception
    {
        long currentTimeMillis = System.currentTimeMillis();

        // Create one with no propId - it'll get generated.
        postCustomPropertyDefinition("customProperty" + currentTimeMillis, null);

        // Create another with an explicit propId.
        postCustomPropertyDefinition("customProperty" + currentTimeMillis, "prop" + currentTimeMillis);
    }

    /**
     * Creates a new property definition using a POST call.
     * GETs the resultant property definition.
     * 
     * @param propertyLabel the label to use
     * @param propId the propId to use - null to have one generated.
     * @return the propId of the new property definition
     */
    private String postCustomPropertyDefinition(String propertyLabel, String propId) throws JSONException,
            IOException, UnsupportedEncodingException
    {
        String jsonString;
        if (propId == null)
        {
            jsonString = new JSONStringer().object()
                .key("label").value(propertyLabel)
                .key("description").value("Dynamically defined test property")
                .key("mandatory").value(false)
                .key("dataType").value("d:text")
                .key("element").value("record")
//                .key("constraintRef").value("rmc:smList")
                // Note no propId
            .endObject()
            .toString();
        }
        else
        {
            jsonString = new JSONStringer().object()
            .key("label").value(propertyLabel)
            .key("description").value("Dynamically defined test property")
            .key("mandatory").value(false)
            .key("dataType").value("d:text")
            .key("element").value("record")
//            .key("constraintRef").value("rmc:smList")
            .key("propId").value(propId)
        .endObject()
        .toString();
        }
        
        // Submit the JSON request.
        final int expectedStatus = 200;
        Response rsp = sendRequest(new PostRequest("/api/rma/admin/custompropertydefinitions?element=record",
                                 jsonString, APPLICATION_JSON), expectedStatus);
        
        String rspContent = rsp.getContentAsString();

        System.out.println(rspContent);
        
        JSONObject jsonRsp = new JSONObject(new JSONTokener(rspContent));
        String urlOfNewPropDef = jsonRsp.getString("url");
        String newPropId = jsonRsp.getString("propId");

        assertNotNull("urlOfNewPropDef was null.", urlOfNewPropDef);
        
        // GET from the URL we're given to ensure it's valid
        rsp = sendRequest(new GetRequest(urlOfNewPropDef), 200);
        rspContent = rsp.getContentAsString();
        
        System.out.println(rspContent);
        
        jsonRsp = new JSONObject(new JSONTokener(rspContent));
        JSONObject dataObject = jsonRsp.getJSONObject("data");
        assertNotNull("JSON data object was null", dataObject);
        JSONObject customPropsObject = dataObject.getJSONObject("customProperties");
        assertNotNull("JSON customProperties object was null", customPropsObject);
        assertEquals("Wrong customProperties length.", 1, customPropsObject.length());
        
        Object keyToSoleProp = customPropsObject.keys().next();
        
        System.out.println("New property defn: " + keyToSoleProp);
        
        JSONObject newPropObject = customPropsObject.getJSONObject((String)keyToSoleProp);
        assertEquals("Wrong property label.", propertyLabel, newPropObject.getString("label"));
        
        return newPropId;
    }

    public void testPutCustomReferenceDefinition() throws Exception
    {
        String[] generatedRefIds = postCustomReferences();
        final String pcRefId = generatedRefIds[0];
        final String bidiRefId = generatedRefIds[1];
        
        // GET the custom refs in order to retrieve the label/source/target
        String refDefnUrl = "/api/rma/admin/customreferencedefinitions/" + bidiRefId;
        Response rsp = sendRequest(new GetRequest(refDefnUrl), 200);

        String rspContent = rsp.getContentAsString();
        System.out.println(rspContent);
        JSONObject jsonRsp = new JSONObject(new JSONTokener(rspContent));

        refDefnUrl = "/api/rma/admin/customreferencedefinitions/" + pcRefId;
        rsp = sendRequest(new GetRequest(refDefnUrl), 200);

        rspContent = rsp.getContentAsString();
        System.out.println(rspContent);
        jsonRsp = new JSONObject(new JSONTokener(rspContent));
        
        // Update the bidirectional reference.
        final String updatedBiDiLabel = "Updated label üøéîçå";
        String jsonString = new JSONStringer().object()
            .key("label").value(updatedBiDiLabel)
        .endObject()
        .toString();
    
        refDefnUrl = "/api/rma/admin/customreferencedefinitions/" + bidiRefId;
        rsp = sendRequest(new PutRequest(refDefnUrl,
                                 jsonString, APPLICATION_JSON), 200);
        
        rspContent = rsp.getContentAsString();
        System.out.println(rspContent);
    
        jsonRsp = new JSONObject(new JSONTokener(rspContent));
        String urlOfNewRefDef = jsonRsp.getString("url");
        assertNotNull("urlOfNewRefDef was null.", urlOfNewRefDef);
    
        // GET the bidi reference to ensure it's valid
        rsp = sendRequest(new GetRequest(refDefnUrl), 200);
        rspContent = rsp.getContentAsString();
        
        System.out.println(rspContent);
        
        jsonRsp = new JSONObject(new JSONTokener(rspContent));
        JSONObject dataObject = jsonRsp.getJSONObject("data");
        assertNotNull("JSON data object was null", dataObject);
        JSONArray customRefsObject = dataObject.getJSONArray("customReferences");
        assertNotNull("JSON customReferences object was null", customRefsObject);
        assertEquals("Wrong customReferences length.", 1, customRefsObject.length());
        
        JSONObject newRefObject = customRefsObject.getJSONObject(0);
        assertEquals("Wrong property label.", updatedBiDiLabel, newRefObject.getString("label"));

        // Update the parent/child reference.
        final String updatedPcSource = "Updated source ∆Ωç√∫";
        final String updatedPcTarget = "Updated target ∆Ωç√∫";
        jsonString = new JSONStringer().object()
            .key("source").value(updatedPcSource)
            .key("target").value(updatedPcTarget)
        .endObject()
        .toString();
    
        refDefnUrl = "/api/rma/admin/customreferencedefinitions/" + pcRefId;
        rsp = sendRequest(new PutRequest(refDefnUrl,
                                 jsonString, APPLICATION_JSON), 200);
        
        rspContent = rsp.getContentAsString();
        System.out.println(rspContent);
    
        jsonRsp = new JSONObject(new JSONTokener(rspContent));
        urlOfNewRefDef = jsonRsp.getString("url");
        assertNotNull("urlOfNewRefDef was null.", urlOfNewRefDef);
    
        // GET the parent/child reference to ensure it's valid
        refDefnUrl = "/api/rma/admin/customreferencedefinitions/" + pcRefId;

        rsp = sendRequest(new GetRequest(refDefnUrl), 200);
        rspContent = rsp.getContentAsString();
        
        System.out.println(rspContent);
        
        jsonRsp = new JSONObject(new JSONTokener(rspContent));
        dataObject = jsonRsp.getJSONObject("data");
        assertNotNull("JSON data object was null", dataObject);
        customRefsObject = dataObject.getJSONArray("customReferences");
        assertNotNull("JSON customReferences object was null", customRefsObject);
        assertEquals("Wrong customReferences length.", 1, customRefsObject.length());
        
        newRefObject = customRefsObject.getJSONObject(0);
        assertEquals("Wrong reference source.", updatedPcSource, newRefObject.getString("source"));
        assertEquals("Wrong reference target.", updatedPcTarget, newRefObject.getString("target"));
    }

    @SuppressWarnings("unchecked")
    public void testGetCustomProperties() throws Exception
    {
        // Ensure that there is at least one custom property.
        this.testPostCustomPropertyDefinition();

        final int expectedStatus = 200;
        Response rsp = sendRequest(new GetRequest("/api/rma/admin/custompropertydefinitions?element=record"), expectedStatus);

        JSONObject jsonRsp = new JSONObject(new JSONTokener(rsp.getContentAsString()));

        JSONObject dataObj = (JSONObject)jsonRsp.get("data");
        assertNotNull("JSON 'data' object was null", dataObj);
        
        JSONObject customPropsObj = (JSONObject)dataObj.get("customProperties");
        assertNotNull("JSON 'customProperties' object was null", customPropsObj);
        
        for (Iterator iter = customPropsObj.keys(); iter.hasNext();) {
            System.out.println(customPropsObj.get((String)iter.next()));
        }

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
    
    public void testExportInTransferFormat() throws Exception
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
        jsonPostData.put("transferFormat", true);
        String jsonPostString = jsonPostData.toString();
        
        // make the export request
        Response rsp = sendRequest(new PostRequest(exportUrl, jsonPostString, APPLICATION_JSON), 200);
        assertEquals("application/zip", rsp.getContentType());
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
        assertEquals("application/zip", rsp.getContentType());
    }
    
    public void testAudit() throws IOException, JSONException
    {
        // get the full RM audit log 
        Response rsp = sendRequest(new GetRequest(RMA_AUDITLOG_URL), 200);
        // check response
        assertEquals("application/json", rsp.getContentType());
        JSONObject jsonRsp = new JSONObject(new JSONTokener(rsp.getContentAsString()));
        
        // export the full RM audit log 
        rsp = sendRequest(new GetRequest(RMA_AUDITLOG_URL + "?export=true"), 200);
        // check response
        assertEquals("application/json", rsp.getContentType());
        jsonRsp = new JSONObject(new JSONTokener(rsp.getContentAsString()));
        
        // get category
        NodeRef recordCategory = TestUtilities.getRecordCategory(searchService, "Civilian Files", "Foreign Employee Award Files");
        assertNotNull(recordCategory);
        
        // construct the URL
        String nodeUrl = recordCategory.toString().replace("://", "/");
        String auditUrl = MessageFormat.format(GET_NODE_AUDITLOG_URL_FORMAT, nodeUrl);
        
        // send request
        rsp = sendRequest(new GetRequest(auditUrl), 200);
        // check response
        assertEquals("application/json", rsp.getContentType());
        jsonRsp = new JSONObject(new JSONTokener(rsp.getContentAsString()));
        
        // get the audit log with all restrictions in place
        String filteredAuditUrl = auditUrl + "?user=gavinc&size=5&from=2009-01-01&to=2009-12-31";
        rsp = sendRequest(new GetRequest(filteredAuditUrl), 200);
        // check response
        assertEquals("application/json", rsp.getContentType());
        jsonRsp = new JSONObject(new JSONTokener(rsp.getContentAsString()));
        
        // attempt to get the audit log with invalid restrictions in place
        filteredAuditUrl = auditUrl + "?user=fred&size=abc&from=2009&to=2010";
        rsp = sendRequest(new GetRequest(filteredAuditUrl), 200);
        assertEquals("application/json", rsp.getContentType());
        jsonRsp = new JSONObject(new JSONTokener(rsp.getContentAsString()));
        
        // start the RM audit log
        JSONObject jsonPostData = new JSONObject();
        jsonPostData.put("enabled", true);
        String jsonPostString = jsonPostData.toString();
        rsp = sendRequest(new PutRequest(RMA_AUDITLOG_URL, jsonPostString, APPLICATION_JSON), 200);
        
        // check the response
        System.out.println(rsp.getContentAsString());
        jsonRsp = new JSONObject(new JSONTokener(rsp.getContentAsString()));
        JSONObject dataObj = (JSONObject)jsonRsp.get("data");
        assertNotNull("JSON 'data' object was null", dataObj);
        assertTrue(dataObj.getBoolean("enabled"));
        assertTrue(dataObj.has("started"));
        assertTrue(dataObj.has("stopped"));
        
        // stop the RM audit log
        jsonPostData = new JSONObject();
        jsonPostData.put("enabled", false);
        jsonPostString = jsonPostData.toString();
        rsp = sendRequest(new PutRequest(RMA_AUDITLOG_URL, jsonPostString, APPLICATION_JSON), 200);
        
        // check the response
        System.out.println(rsp.getContentAsString());
        jsonRsp = new JSONObject(new JSONTokener(rsp.getContentAsString()));
        dataObj = (JSONObject)jsonRsp.get("data");
        assertNotNull("JSON 'data' object was null", dataObj);
        assertFalse(dataObj.getBoolean("enabled"));
        
        // clear the RM audit log
        rsp = sendRequest(new DeleteRequest(RMA_AUDITLOG_URL), 200);
        System.out.println(rsp.getContentAsString());
        jsonRsp = new JSONObject(new JSONTokener(rsp.getContentAsString()));
        dataObj = (JSONObject)jsonRsp.get("data");
        assertNotNull("JSON 'data' object was null", dataObj);
        assertFalse(dataObj.getBoolean("enabled"));
    }
    
    public void testFileAuditLogAsRecord() throws Exception
    {
        // Attempt to store audit log at non existent destination, make sure we get 404
        JSONObject jsonPostData = new JSONObject();
        jsonPostData.put("destination", "workspace://SpacesStore/09ca1e02-1c87-4a53-97e7-xxxxxxxxxxxx");
        String jsonPostString = jsonPostData.toString();
        Response rsp = sendRequest(new PostRequest(RMA_AUDITLOG_URL, jsonPostString, APPLICATION_JSON), 404);
        
        // Attempt to store audit log at wrong type of destination, make sure we get 400
        NodeRef recordCategory = TestUtilities.getRecordCategory(searchService, "Civilian Files", 
                    "Foreign Employee Award Files");
        assertNotNull(recordCategory);
        jsonPostData = new JSONObject();
        jsonPostData.put("destination", recordCategory.toString());
        jsonPostString = jsonPostData.toString();
        rsp = sendRequest(new PostRequest(RMA_AUDITLOG_URL, jsonPostString, APPLICATION_JSON), 400);
        
        // get record folder to file into
        NodeRef destination = TestUtilities.getRecordFolder(searchService, "Civilian Files", 
                    "Foreign Employee Award Files", "Christian Bohr");
        assertNotNull(destination);
        
        // Store the full audit log as a record
        jsonPostData = new JSONObject();
        jsonPostData.put("destination", destination);
        jsonPostString = jsonPostData.toString();
        rsp = sendRequest(new PostRequest(RMA_AUDITLOG_URL, jsonPostString, APPLICATION_JSON), 200);
        
        // check the response
        System.out.println(rsp.getContentAsString());
        JSONObject jsonRsp = new JSONObject(new JSONTokener(rsp.getContentAsString()));
        assertTrue(jsonRsp.has("success"));
        assertTrue(jsonRsp.getBoolean("success"));
        assertTrue(jsonRsp.has("record"));
        assertNotNull(jsonRsp.get("record"));
        assertTrue(nodeService.exists(new NodeRef(jsonRsp.getString("record"))));
        assertTrue(jsonRsp.has("recordName"));
        assertNotNull(jsonRsp.get("recordName"));
        assertTrue(jsonRsp.getString("recordName").startsWith("audit_"));
        
        // Store a filtered audit log as a record
        jsonPostData = new JSONObject();
        jsonPostData.put("destination", destination);
        jsonPostData.put("size", "50");
        jsonPostData.put("user", "gavinc");
        jsonPostData.put("event", "Update Metadata");
        jsonPostString = jsonPostData.toString();
        rsp = sendRequest(new PostRequest(RMA_AUDITLOG_URL, jsonPostString, APPLICATION_JSON), 200);
        
        // check the response
        System.out.println(rsp.getContentAsString());
        jsonRsp = new JSONObject(new JSONTokener(rsp.getContentAsString()));
        assertTrue(jsonRsp.has("success"));
        assertTrue(jsonRsp.getBoolean("success"));
        assertTrue(jsonRsp.has("record"));
        assertNotNull(jsonRsp.get("record"));
        assertTrue(nodeService.exists(new NodeRef(jsonRsp.getString("record"))));
        assertTrue(jsonRsp.has("recordName"));
        assertNotNull(jsonRsp.get("recordName"));
        assertTrue(jsonRsp.getString("recordName").startsWith("audit_"));
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
}
