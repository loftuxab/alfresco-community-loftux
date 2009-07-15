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

import java.text.MessageFormat;

import org.alfresco.model.ContentModel;
import org.alfresco.module.org_alfresco_module_dod5015.DOD5015Model;
import org.alfresco.module.org_alfresco_module_dod5015.RecordsManagementModel;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.web.scripts.BaseWebScriptTest;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.cmr.security.PermissionService;
import org.alfresco.service.cmr.view.ImporterService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.web.scripts.TestWebScriptServer.DeleteRequest;
import org.alfresco.web.scripts.TestWebScriptServer.GetRequest;
import org.alfresco.web.scripts.TestWebScriptServer.PostRequest;
import org.alfresco.web.scripts.TestWebScriptServer.PutRequest;
import org.alfresco.web.scripts.TestWebScriptServer.Response;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

/**
 * This class tests the Rest API for disposition related operations
 * 
 * @author Gavin Cornwell
 */
public class DispositionRestApiTest extends BaseWebScriptTest implements RecordsManagementModel
{
    protected static StoreRef SPACES_STORE = new StoreRef(StoreRef.PROTOCOL_WORKSPACE, "SpacesStore");
    protected static final String GET_SCHEDULE_URL_FORMAT = "/api/node/{0}/dispositionschedule";
    protected static final String POST_ACTIONDEF_URL_FORMAT = "/api/node/{0}/dispositionschedule/dispositionactiondefinitions";
    protected static final String DELETE_ACTIONDEF_URL_FORMAT = "/api/node/{0}/dispositionschedule/dispositionactiondefinitions/{1}";
    protected static final String PUT_ACTIONDEF_URL_FORMAT = "/api/node/{0}/dispositionschedule/dispositionactiondefinitions/{1}";
    protected static final String APPLICATION_JSON = "application/json";
    
    protected NodeService nodeService;
    protected ContentService contentService;
    protected SearchService searchService;
    protected ImporterService importService;
    protected ServiceRegistry services;
    protected PermissionService permissionService;
    
    @Override
    protected void setUp() throws Exception
    {
        super.setUp();
        this.nodeService = (NodeService) getServer().getApplicationContext().getBean("NodeService");
        //this.contentService = (ContentService)getServer().getApplicationContext().getBean("ContentService");
        this.searchService = (SearchService)getServer().getApplicationContext().getBean("SearchService");
        this.importService = (ImporterService)getServer().getApplicationContext().getBean("ImporterService");
        //this.services = (ServiceRegistry)getServer().getApplicationContext().getBean("ServiceRegistry");
        this.permissionService = (PermissionService)getServer().getApplicationContext().getBean("PermissionService");

        AuthenticationUtil.setFullyAuthenticatedUser(AuthenticationUtil.getSystemUserName());

        // Bring the filePlan into the test database.
        //
        // This is quite a slow call, so if this class grew to have many test methods,
        // there would be a real benefit in using something like @BeforeClass for the line below.
        TestUtilities.loadFilePlanData(null, this.nodeService, this.importService, this.permissionService);
    }

    public void testGetDispositionSchedule() throws Exception
    {
        // Test 404 status for non existent node
        int expectedStatus = 404;
        String nonExistentNode = "workspace/SpacesStore/09ca1e02-1c87-4a53-97e7-xxxxxxxxxxxx";
        String nonExistentUrl = MessageFormat.format(GET_SCHEDULE_URL_FORMAT, nonExistentNode);
        Response rsp = sendRequest(new GetRequest(nonExistentUrl), expectedStatus);
        
        // Test 404 status for node that doesn't have dispostion schedule i.e. a record series
        NodeRef series = TestUtilities.getRecordSeries(searchService, "Reports");
        assertNotNull(series);
        String seriesNodeUrl = series.toString().replace("://", "/");
        String wrongNodeUrl = MessageFormat.format(GET_SCHEDULE_URL_FORMAT, seriesNodeUrl);
        rsp = sendRequest(new GetRequest(wrongNodeUrl), expectedStatus);
        
        // Test data structure returned from "AIS Audit Records"
        expectedStatus = 200;
        NodeRef recordCategory = TestUtilities.getRecordCategory(this.searchService, "Reports", "AIS Audit Records");
        assertNotNull(recordCategory);
        String categoryNodeUrl = recordCategory.toString().replace("://", "/");
        String requestUrl = MessageFormat.format(GET_SCHEDULE_URL_FORMAT, categoryNodeUrl);
        rsp = sendRequest(new GetRequest(requestUrl), expectedStatus);
        assertEquals("application/json;charset=UTF-8", rsp.getContentType());
        
        // get response as JSON
        JSONObject jsonParsedObject = new JSONObject(new JSONTokener(rsp.getContentAsString()));
        assertNotNull(jsonParsedObject);
        
        // check JSON data
        JSONObject dataObj = jsonParsedObject.getJSONObject("data");
        assertNotNull(dataObj);
        JSONObject rootDataObject = (JSONObject)dataObj;
        assertEquals(6, rootDataObject.length());
        
        // check individual data items
        String serviceUrl = "/alfresco/service" + requestUrl;
        String url = rootDataObject.getString("url");
        assertEquals(serviceUrl, url);
        
        String authority = rootDataObject.getString("authority");
        assertEquals("N1-218-00-4 item 023", authority);
        
        String instructions = rootDataObject.getString("instructions");
        assertEquals("Cut off monthly, hold 1 month, then destroy.", instructions);
        
        String actionsUrl = rootDataObject.getString("actionsUrl");
        assertEquals(serviceUrl + "/dispositionactiondefinitions", actionsUrl);
        
        boolean recordLevel = rootDataObject.getBoolean("recordLevelDisposition");
        assertFalse(recordLevel);
        
        JSONArray actions = rootDataObject.getJSONArray("actions");
        assertNotNull(actions);
        assertEquals(2, actions.length());
        JSONObject action1 = (JSONObject)actions.get(0);
        assertEquals(7, action1.length());
        assertNotNull(action1.get("id"));
        assertNotNull(action1.get("url"));
        assertEquals(0, action1.getInt("index"));
        assertEquals("cutoff", action1.getString("name"));
        assertEquals("Cutoff", action1.getString("label"));
        assertEquals("monthend|1", action1.getString("period"));
        assertTrue(action1.getBoolean("eligibleOnFirstCompleteEvent"));
        
        JSONObject action2 = (JSONObject)actions.get(1);
        assertEquals(8, action2.length());
        assertEquals("rma:cutOffDate", action2.get("periodProperty"));
        
        // Test data structure returned from "Personnel Security Program Records"
        recordCategory = TestUtilities.getRecordCategory(this.searchService, "Civilian Files", "Employee Performance File System Records");
        assertNotNull(recordCategory);
        categoryNodeUrl = recordCategory.toString().replace("://", "/");
        requestUrl = MessageFormat.format(GET_SCHEDULE_URL_FORMAT, categoryNodeUrl);
        rsp = sendRequest(new GetRequest(requestUrl), expectedStatus);
        assertEquals("application/json;charset=UTF-8", rsp.getContentType());
        
        // get response as JSON
        jsonParsedObject = new JSONObject(new JSONTokener(rsp.getContentAsString()));
        assertNotNull(jsonParsedObject);

        // check JSON data
        dataObj = jsonParsedObject.getJSONObject("data");
        assertNotNull(dataObj);
        rootDataObject = (JSONObject)dataObj;
        assertEquals(6, rootDataObject.length());
        
        // check individual data items
        serviceUrl = "/alfresco/service" + requestUrl;
        url = rootDataObject.getString("url");
        assertEquals(serviceUrl, url);
        
        authority = rootDataObject.getString("authority");
        assertEquals("GRS 1 item 23b(1)", authority);
        
        instructions = rootDataObject.getString("instructions");
        assertEquals("Cutoff when superseded.  Destroy immediately after cutoff", instructions);
        
        recordLevel = rootDataObject.getBoolean("recordLevelDisposition");
        assertTrue(recordLevel);
        
        actions = rootDataObject.getJSONArray("actions");
        assertNotNull(actions);
        assertEquals(2, actions.length());
        action1 = (JSONObject)actions.get(0);
        assertEquals(8, action1.length());
        assertNotNull(action1.get("id"));
        assertNotNull(action1.get("url"));
        assertEquals(0, action1.getInt("index"));
        assertEquals("cutoff", action1.getString("name"));
        assertEquals("Cutoff", action1.getString("label"));
        assertTrue(action1.getBoolean("eligibleOnFirstCompleteEvent"));
        JSONArray events = action1.getJSONArray("events");
        assertNotNull(events);
        assertEquals(1, events.length());
        assertEquals("superseded", events.get(0));
        
        // Test the retrieval of an empty disposition schedule
        NodeRef recordSeries = TestUtilities.getRecordSeries(this.searchService, "Civilian Files");
        assertNotNull(recordSeries);
        
        // create a new recordCategory node in the recordSeries and then get
        // the disposition schedule
        NodeRef newRecordCategory = this.nodeService.createNode(recordSeries, ContentModel.ASSOC_CONTAINS, 
                    QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, QName.createValidLocalName("recordCategory")), 
                    DOD5015Model.TYPE_RECORD_CATEGORY).getChildRef();
        
        categoryNodeUrl = newRecordCategory.toString().replace("://", "/");
        requestUrl = MessageFormat.format(GET_SCHEDULE_URL_FORMAT, categoryNodeUrl);
        rsp = sendRequest(new GetRequest(requestUrl), expectedStatus);
        
        // get response as JSON
        jsonParsedObject = new JSONObject(new JSONTokener(rsp.getContentAsString()));
        assertNotNull(jsonParsedObject);

        // check JSON data
        dataObj = jsonParsedObject.getJSONObject("data");
        assertNotNull(dataObj);
        rootDataObject = (JSONObject)dataObj;
        assertEquals(4, rootDataObject.length());
        actions = rootDataObject.getJSONArray("actions");
        assertNotNull(actions);
        assertEquals(0, actions.length());
    }
    
    public void testPostDispositionAction() throws Exception
    {
        // create a recordCategory to get a disposition schedule
        NodeRef recordSeries = TestUtilities.getRecordSeries(this.searchService, "Civilian Files");
        assertNotNull(recordSeries);
        
        // create a new recordCategory node in the recordSeries and then get
        // the disposition schedule
        NodeRef newRecordCategory = this.nodeService.createNode(recordSeries, ContentModel.ASSOC_CONTAINS, 
                    QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, QName.createValidLocalName("recordCategory")), 
                    DOD5015Model.TYPE_RECORD_CATEGORY).getChildRef();
        
        String categoryNodeUrl = newRecordCategory.toString().replace("://", "/");
        String requestUrl = MessageFormat.format(POST_ACTIONDEF_URL_FORMAT, categoryNodeUrl);
        
        // Construct the JSON request.
        String name = "destroy";
        String desc = "Destroy this record after 5 years";
        String period = "year|5";
        String periodProperty = "rma:cutOffDate";
        boolean eligibleOnFirstCompleteEvent = true;
        
        JSONObject jsonPostData = new JSONObject();
        jsonPostData.put("name", name);
        jsonPostData.put("description", desc);
        jsonPostData.put("period", period);
        jsonPostData.put("periodProperty", periodProperty);
        jsonPostData.put("eligibleOnFirstCompleteEvent", eligibleOnFirstCompleteEvent);
        JSONArray events = new JSONArray();
        events.put("superseded");
        events.put("no_longer_needed");
        jsonPostData.put("events", events);
        
        // Submit the JSON request.
        String jsonPostString = jsonPostData.toString();
        Response rsp = sendRequest(new PostRequest(requestUrl, jsonPostString, APPLICATION_JSON), 200);
        
        // check the returned data is what was expected
        JSONObject jsonResponse = new JSONObject(new JSONTokener(rsp.getContentAsString()));
        JSONObject dataObj = jsonResponse.getJSONObject("data");
        JSONObject rootDataObject = (JSONObject)dataObj;
        assertNotNull(rootDataObject.getString("id"));
        assertNotNull(rootDataObject.getString("url"));
        assertEquals(0, rootDataObject.getInt("index"));
        assertEquals(name, rootDataObject.getString("name"));
        assertEquals("Destroy", rootDataObject.getString("label"));
        assertEquals(desc, rootDataObject.getString("description"));
        assertEquals(period, rootDataObject.getString("period"));
        assertEquals(periodProperty, rootDataObject.getString("periodProperty"));
        assertTrue(rootDataObject.getBoolean("eligibleOnFirstCompleteEvent"));
        events = rootDataObject.getJSONArray("events");
        assertNotNull(events);
        assertEquals(2, events.length());
        assertEquals("superseded", events.get(0));
        assertEquals("no_longer_needed", events.get(1));
        
        // test the minimum amount of data required to create an action definition
        jsonPostData = new JSONObject();
        jsonPostData.put("name", name);
        jsonPostString = jsonPostData.toString();
        rsp = sendRequest(new PostRequest(requestUrl, jsonPostString, APPLICATION_JSON), 200);
        
        // check the returned data is what was expected
        jsonResponse = new JSONObject(new JSONTokener(rsp.getContentAsString()));
        dataObj = jsonResponse.getJSONObject("data");
        assertNotNull(rootDataObject.getString("id"));
        assertNotNull(rootDataObject.getString("url"));
        assertEquals(0, rootDataObject.getInt("index"));
        assertEquals(name, dataObj.getString("name"));
        assertEquals("none|0", dataObj.getString("period"));
        assertFalse(dataObj.has("description"));
        assertFalse(dataObj.has("periodProperty"));
        assertFalse(dataObj.has("events"));
        assertTrue(dataObj.getBoolean("eligibleOnFirstCompleteEvent"));
        
        // negative test to ensure not supplying mandatory data results in an error
        jsonPostData = new JSONObject();
        jsonPostData.put("description", desc);
        jsonPostData.put("period", period);
        jsonPostString = jsonPostData.toString();
        sendRequest(new PostRequest(requestUrl, jsonPostString, APPLICATION_JSON), 400);
    }
    
    public void testPutDispositionAction() throws Exception
    {
        // create a new recordCategory node in the recordSeries and then get
        // the disposition schedule
        NodeRef recordSeries = TestUtilities.getRecordSeries(this.searchService, "Civilian Files");
        assertNotNull(recordSeries);
        NodeRef newRecordCategory = this.nodeService.createNode(recordSeries, ContentModel.ASSOC_CONTAINS, 
                    QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, QName.createValidLocalName("recordCategory")), 
                    DOD5015Model.TYPE_RECORD_CATEGORY).getChildRef();
        
        // create an action definition to then update
        String categoryNodeUrl = newRecordCategory.toString().replace("://", "/");
        String postRequestUrl = MessageFormat.format(POST_ACTIONDEF_URL_FORMAT, categoryNodeUrl);
        JSONObject jsonPostData = new JSONObject();
        jsonPostData.put("name", "cutoff");
        String jsonPostString = jsonPostData.toString();
        sendRequest(new PostRequest(postRequestUrl, jsonPostString, APPLICATION_JSON), 200);
        
        // verify the action definition is present and retrieve it's id
        String getRequestUrl = MessageFormat.format(GET_SCHEDULE_URL_FORMAT, categoryNodeUrl);
        Response rsp = sendRequest(new GetRequest(getRequestUrl), 200);
        JSONObject json = new JSONObject(new JSONTokener(rsp.getContentAsString()));
        JSONObject actionDef = json.getJSONObject("data").getJSONArray("actions").getJSONObject(0);
        String actionDefId = actionDef.getString("id");
        assertEquals("cutoff", actionDef.getString("name"));
        assertEquals("Cutoff", actionDef.getString("label"));
        assertEquals("none|0", actionDef.getString("period"));
        assertFalse(actionDef.has("description"));
        assertFalse(actionDef.has("events"));
        
        // define body for PUT request
        String name = "destroy";
        String desc = "Destroy this record after 5 years";
        String period = "year|5";
        String periodProperty = "rma:cutOffDate";
        boolean eligibleOnFirstCompleteEvent = false;
        
        jsonPostData = new JSONObject();
        jsonPostData.put("name", name);
        jsonPostData.put("description", desc);
        jsonPostData.put("period", period);
        jsonPostData.put("periodProperty", periodProperty);
        jsonPostData.put("eligibleOnFirstCompleteEvent", eligibleOnFirstCompleteEvent);
        JSONArray events = new JSONArray();
        events.put("superseded");
        events.put("no_longer_needed");
        jsonPostData.put("events", events);
        jsonPostString = jsonPostData.toString();
        
        // try and update a non existent action definition to check for 404
        String putRequestUrl = MessageFormat.format(PUT_ACTIONDEF_URL_FORMAT, categoryNodeUrl, "xyz");
        rsp = sendRequest(new PutRequest(putRequestUrl, jsonPostString, APPLICATION_JSON), 404);
        
        // update the action definition
        putRequestUrl = MessageFormat.format(PUT_ACTIONDEF_URL_FORMAT, categoryNodeUrl, actionDefId);
        rsp = sendRequest(new PutRequest(putRequestUrl, jsonPostString, APPLICATION_JSON), 200);
        
        // check the update happened correctly
        json = new JSONObject(new JSONTokener(rsp.getContentAsString()));
        actionDef = json.getJSONObject("data");
        assertEquals(name, actionDef.getString("name"));
        assertEquals("Destroy", actionDef.getString("label"));
        assertEquals(desc, actionDef.getString("description"));
        assertEquals(period, actionDef.getString("period"));
        assertEquals(periodProperty, actionDef.getString("periodProperty"));
        assertFalse(actionDef.getBoolean("eligibleOnFirstCompleteEvent"));
        assertEquals(2, actionDef.getJSONArray("events").length());
        assertEquals("superseded", actionDef.getJSONArray("events").getString(0));
        assertEquals("no_longer_needed", actionDef.getJSONArray("events").getString(1));
    }
    
    public void testDeleteDispositionAction() throws Exception
    {
        // create a new recordCategory node in the recordSeries and then get
        // the disposition schedule
        NodeRef recordSeries = TestUtilities.getRecordSeries(this.searchService, "Civilian Files");
        assertNotNull(recordSeries);
        NodeRef newRecordCategory = this.nodeService.createNode(recordSeries, ContentModel.ASSOC_CONTAINS, 
                    QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, QName.createValidLocalName("recordCategory")), 
                    DOD5015Model.TYPE_RECORD_CATEGORY).getChildRef();
        
        // create an action definition to then delete
        String categoryNodeUrl = newRecordCategory.toString().replace("://", "/");
        String postRequestUrl = MessageFormat.format(POST_ACTIONDEF_URL_FORMAT, categoryNodeUrl);
        JSONObject jsonPostData = new JSONObject();
        jsonPostData.put("name", "cutoff");
        String jsonPostString = jsonPostData.toString();
        sendRequest(new PostRequest(postRequestUrl, jsonPostString, APPLICATION_JSON), 200);
        
        // verify the action definition is present and retrieve it's id
        String getRequestUrl = MessageFormat.format(GET_SCHEDULE_URL_FORMAT, categoryNodeUrl);
        Response rsp = sendRequest(new GetRequest(getRequestUrl), 200);
        JSONObject json = new JSONObject(new JSONTokener(rsp.getContentAsString()));
        String actionDefId = json.getJSONObject("data").getJSONArray("actions").getJSONObject(0).getString("id");
        
        // try and delete a non existent action definition to check for 404
        String deleteRequestUrl = MessageFormat.format(DELETE_ACTIONDEF_URL_FORMAT, categoryNodeUrl, "xyz");
        rsp = sendRequest(new DeleteRequest(deleteRequestUrl), 404);
        
        // now delete the action defintion created above
        deleteRequestUrl = MessageFormat.format(DELETE_ACTIONDEF_URL_FORMAT, categoryNodeUrl, actionDefId);
        rsp = sendRequest(new DeleteRequest(deleteRequestUrl), 200);
        
        // verify it got deleted
        getRequestUrl = MessageFormat.format(GET_SCHEDULE_URL_FORMAT, categoryNodeUrl);
        rsp = sendRequest(new GetRequest(getRequestUrl), 200);
        json = new JSONObject(new JSONTokener(rsp.getContentAsString()));
        JSONArray actions = json.getJSONObject("data").getJSONArray("actions");
        assertEquals(0, actions.length());
    }
}
