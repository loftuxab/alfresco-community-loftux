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
import java.util.Date;

import org.alfresco.model.ContentModel;
import org.alfresco.module.org_alfresco_module_dod5015.RecordsManagementAdminServiceImpl;
import org.alfresco.module.org_alfresco_module_dod5015.RecordsManagementModel;
import org.alfresco.module.org_alfresco_module_dod5015.action.RecordsManagementActionService;
import org.alfresco.module.org_alfresco_module_dod5015.action.impl.DefineCustomElementAbstractAction;
import org.alfresco.repo.content.MimetypeMap;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.web.scripts.BaseWebScriptTest;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.dictionary.AspectDefinition;
import org.alfresco.service.cmr.dictionary.DataTypeDefinition;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.ContentWriter;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.cmr.view.ImporterService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.ISO8601DateFormat;
import org.alfresco.web.scripts.TestWebScriptServer.GetRequest;
import org.alfresco.web.scripts.TestWebScriptServer.PostRequest;
import org.alfresco.web.scripts.TestWebScriptServer.Response;
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
    private static final String RMA_ACTIONS_URL = "/api/rma/actions/ExecutionQueue";
    protected static final String APPLICATION_JSON = "application/json";
    protected static final String RMA_CUSTOM_ASSOCS_URL = "/api/rma/admin/customassociationdefinitions";
    protected static final String RMA_CUSTOM_PROPS_URL = "/api/rma/admin/custompropertydefinitions";
    protected NamespaceService namespaceService;
    protected NodeService nodeService;
    protected ContentService contentService;
    protected DictionaryService dictionaryService;
    protected SearchService searchService;
    protected ImporterService importService;
    protected ServiceRegistry services;    
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
        this.services = (ServiceRegistry)getServer().getApplicationContext().getBean("ServiceRegistry");        
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
        Response rsp = sendRequest(new PostRequest(RMA_ACTIONS_URL,
                                 jsonString, APPLICATION_JSON), expectedStatus);
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
        //TODO The dataDictionary isn't giving back custom props/assocs. Should reload models on commit.
//        assertTrue("New custom assoc not returned by dataDictionary.", customAssocsAspect.getAssociations().containsKey(newAssocQname));
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
        NodeRef customModelNodeRef = DefineCustomElementAbstractAction.RM_CUSTOM_MODEL_NODE_REF;
        // Construct the JSON request for 'defineCustomProperty'.
        final String propertyName = "rmc:customProperty" + System.currentTimeMillis();
        
        String jsonString = new JSONStringer().object()
            .key("name").value("defineCustomProperty")
            .key("nodeRef").value(customModelNodeRef.toString()) // The nodeRef doesn't matter!
            .key("params").object()
                .key("name").value(propertyName)
                .key("title").value("Custom test property")
                .key("description").value("Dynamically defined test property")
                .key("defaultValue").value("invalid")
                .key("mandatory").value(false)
                .key("multiValued").value(false)
                .key("type").value(DataTypeDefinition.BOOLEAN)
                // Have left out 'protected'.
            .endObject()
        .endObject()
        .toString();
        
        // Submit the JSON request.
        final int expectedStatus = 200;
        Response rsp = sendRequest(new PostRequest(RMA_ACTIONS_URL,
                                 jsonString, APPLICATION_JSON), expectedStatus);
        
        String rspContent = rsp.getContentAsString();
        assertTrue(rspContent.contains("Successfully queued action [defineCustomProperty]"));
    }

    public void testGetCustomProperties() throws Exception
    {
        // Ensure that there is at least one custom property.
        this.testPostCustomProperty();

        final int expectedStatus = 200;
        Response rsp = sendRequest(new GetRequest(RMA_CUSTOM_PROPS_URL), expectedStatus);

        JSONObject jsonRsp = new JSONObject(new JSONTokener(rsp.getContentAsString()));

        JSONObject dataObj = (JSONObject)jsonRsp.get("data");
        assertNotNull("JSON 'data' object was null", dataObj);
        
        JSONObject customPropsObj = (JSONObject)dataObj.get("customProperties");
        assertNotNull("JSON 'customProperties' object was null", customPropsObj);

        final int customPropsCount = customPropsObj.length();
        assertTrue("There should be at least one custom property. Found " + customPropsObj, customPropsCount > 0);
    }
}
