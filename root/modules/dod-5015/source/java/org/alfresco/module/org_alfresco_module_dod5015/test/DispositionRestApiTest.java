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
import org.alfresco.web.scripts.TestWebScriptServer.GetRequest;
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
        System.out.println("GET response: " + rsp.getContentAsString());
        assertEquals("application/json;charset=UTF-8", rsp.getContentType());
        
        // get response as JSON
        JSONObject jsonParsedObject = new JSONObject(new JSONTokener(rsp.getContentAsString()));
        assertNotNull(jsonParsedObject);
        
        // check JSON data
        Object dataObj = jsonParsedObject.get("data");
        assertEquals(JSONObject.class, dataObj.getClass());
        JSONObject rootDataObject = (JSONObject)dataObj;
        assertEquals(6, rootDataObject.length());
        
        // check individual data items
        String serviceUrl = "/alfresco/service" + requestUrl;
        String url = (String)rootDataObject.get("url");
        assertEquals(serviceUrl, url);
        
        String authority = (String)rootDataObject.get("authority");
        assertEquals("N1-218-00-4 item 023", authority);
        
        String instructions = (String)rootDataObject.get("instructions");
        assertEquals("Cut off monthly, hold 1 month, then destroy.", instructions);
        
        String actionsUrl = (String)rootDataObject.get("actionsUrl");
        assertEquals(serviceUrl + "/dispositionactiondefinitions", actionsUrl);
        
        boolean recordLevel = rootDataObject.getBoolean("recordLevelDisposition");
        assertFalse(recordLevel);
        
        JSONArray actions = rootDataObject.getJSONArray("actions");
        assertNotNull(actions);
        assertEquals(2, actions.length());
        JSONObject action1 = (JSONObject)actions.get(0);
        assertEquals(6, action1.length());
        assertNotNull(action1.get("id"));
        assertNotNull(action1.get("url"));
        assertEquals(0, action1.getInt("index"));
        assertEquals("cutoff", action1.get("name"));
        assertEquals("monthend|1", action1.get("period"));
        assertTrue(action1.getBoolean("eligibleOnFirstCompleteEvent"));
        
        JSONObject action2 = (JSONObject)actions.get(1);
        assertEquals(7, action2.length());
        assertEquals("rma:cutOffDate", action2.get("periodProperty"));
        
        // Test data structure returned from "Personnel Security Program Records"
        recordCategory = TestUtilities.getRecordCategory(this.searchService, "Civilian Files", "Employee Performance File System Records");
        assertNotNull(recordCategory);
        categoryNodeUrl = recordCategory.toString().replace("://", "/");
        requestUrl = MessageFormat.format(GET_SCHEDULE_URL_FORMAT, categoryNodeUrl);
        rsp = sendRequest(new GetRequest(requestUrl), expectedStatus);
        System.out.println("GET response: " + rsp.getContentAsString());
        assertEquals("application/json;charset=UTF-8", rsp.getContentType());
        
        // get response as JSON
        jsonParsedObject = new JSONObject(new JSONTokener(rsp.getContentAsString()));
        assertNotNull(jsonParsedObject);

        // check JSON data
        dataObj = jsonParsedObject.get("data");
        assertEquals(JSONObject.class, dataObj.getClass());
        rootDataObject = (JSONObject)dataObj;
        assertEquals(6, rootDataObject.length());
        
        // check individual data items
        serviceUrl = "/alfresco/service" + requestUrl;
        url = (String)rootDataObject.get("url");
        assertEquals(serviceUrl, url);
        
        authority = (String)rootDataObject.get("authority");
        assertEquals("GRS 1 item 23b(1)", authority);
        
        instructions = (String)rootDataObject.get("instructions");
        assertEquals("Cutoff when superseded.  Destroy immediately after cutoff", instructions);
        
        recordLevel = rootDataObject.getBoolean("recordLevelDisposition");
        assertTrue(recordLevel);
        
        actions = rootDataObject.getJSONArray("actions");
        assertNotNull(actions);
        assertEquals(2, actions.length());
        action1 = (JSONObject)actions.get(0);
        assertEquals(6, action1.length());
        assertNotNull(action1.get("id"));
        assertNotNull(action1.get("url"));
        assertEquals(0, action1.getInt("index"));
        assertEquals("cutoff", action1.get("name"));
        assertTrue(action1.getBoolean("eligibleOnFirstCompleteEvent"));
        /*JSONArray events = action1.getJSONArray("events");
        assertNotNull(events);
        assertEquals(1, events.length());
        assertEquals("superseded", events.get(0));*/
    }
}
