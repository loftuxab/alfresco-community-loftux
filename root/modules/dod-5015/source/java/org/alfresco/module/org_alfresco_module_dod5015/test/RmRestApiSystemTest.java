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
import java.util.HashMap;
import java.util.Map;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.content.MimetypeMap;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.web.scripts.BaseWebScriptTest;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.ContentWriter;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.cmr.view.ImporterService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.web.scripts.TestWebScriptServer.PostRequest;
import org.alfresco.web.scripts.TestWebScriptServer.Response;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * This class tests the Rest API for the submission of RM actions.
 * 
 * @author Neil McErlean
 */
public class RmRestApiSystemTest extends BaseWebScriptTest
{
    private static final String REST_URL = "/api/rma/actions/ExecutionQueue";
    protected static final String APPLICATION_JSON = "application/json";
    protected NodeService nodeService;
    protected ContentService contentService;
    protected SearchService searchService;
    protected ImporterService importService;

    @Override
    protected void setUp() throws Exception
    {
        super.setUp();
        this.nodeService = (NodeService) getServer().getApplicationContext().getBean("NodeService");
        this.contentService = (ContentService)getServer().getApplicationContext().getBean("ContentService");
        this.searchService = (SearchService)getServer().getApplicationContext().getBean("SearchService");
        this.importService = (ImporterService)getServer().getApplicationContext().getBean("ImporterService");

        AuthenticationUtil.setFullyAuthenticatedUser(AuthenticationUtil.getSystemUserName());

        // Bring the filePlan into the test database.
        //
        // This is quite a slow call, so if this class grew to have many test methods,
        // there would be a real benefit in using something like @BeforeClass for the line below.
        TestUtilities.loadFilePlanData(null, this.nodeService, this.importService);
    }

    public void testPost_DeclareRecord_ToNonExistentNode() throws Exception
    {
        NodeRef recordCategory = TestUtilities.getRecordCategory(searchService, "Reports", "AIS Audit Records");     
        assertNotNull(recordCategory);

        NodeRef nonExistentNode = new NodeRef("workspace://SpacesStore/09ca1e02-1c87-4a53-97e7-xxxxxxxxxxxx");
        
        // Construct the JSON request for declareRecord.
        JSONObject jsonPostData = new JSONObject();
        jsonPostData.put("nodeRef", nonExistentNode.toString());
        jsonPostData.put("name", "declareRecord");
        
        // Submit the JSON request.
        String jsonPostString = jsonPostData.toString();
        
        final int expectedStatus = 404;
        sendRequest(new PostRequest(REST_URL, jsonPostString, APPLICATION_JSON), expectedStatus);
    }

    //TODO NEIL Switch this back on and get it passing.
    public void off_testPost_DeclareRecord() throws IOException, JSONException
    {
        // Get the recordCategory under which we will create the testNode.
        NodeRef recordCategory = TestUtilities.getRecordCategory(searchService, "Reports", "AIS Audit Records");     
        assertNotNull(recordCategory);

        // Create a testNode/file which is to be declared as a record.
        NodeRef nodeToBeDeclared = this.nodeService.createNode(recordCategory, 
                ContentModel.ASSOC_CONTAINS, 
                QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, "MyRecord.txt"), 
                ContentModel.TYPE_CONTENT).getChildRef();

        // Set some dummy content.
        ContentWriter writer = this.contentService.getWriter(nodeToBeDeclared, ContentModel.PROP_CONTENT, true);
        writer.setMimetype(MimetypeMap.MIMETYPE_TEXT_PLAIN);
        writer.setEncoding("UTF-8");
        writer.putContent("There is some content in this record");
        
        // Construct the JSON request for declareRecord.
        JSONObject jsonPostData = new JSONObject();
        jsonPostData.put("nodeRef", nodeToBeDeclared.toString());
        jsonPostData.put("name", "declareRecord");
        
        // Set parameters on the action.
        Map<String, Serializable> paramData = new HashMap<String, Serializable>();
        paramData.put("recordFolder", "???");
        paramData.put("recordProperties", "?????");
        jsonPostData.put("params", paramData);
        
        // Submit the JSON request.
        String jsonPostString = jsonPostData.toString();
        System.out.println("JSON= " + jsonPostString);
        
        final int expectedStatus = 200;
        Response rsp = sendRequest(new PostRequest(REST_URL,
                                      jsonPostString, APPLICATION_JSON), expectedStatus);
        
        System.out.println(rsp.getContentAsString());
        //TODO NEIL Assert something about the rsp contents.
    }
}
