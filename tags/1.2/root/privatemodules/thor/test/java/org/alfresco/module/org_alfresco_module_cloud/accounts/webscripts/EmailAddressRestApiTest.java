/*
 * Copyright (C) 2005-2013 Alfresco Software Limited.
 *
 * This file is part of Alfresco
 *
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */
package org.alfresco.module.org_alfresco_module_cloud.accounts.webscripts;

import java.io.IOException;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.alfresco.module.org_alfresco_module_cloud.CloudTestContext;
import org.alfresco.module.org_alfresco_module_cloud.emailaddress.DomainValidityCheck.FailureReason;
import org.alfresco.module.org_alfresco_module_cloud.emailaddress.EmailAddressService;
import org.alfresco.module.org_alfresco_module_cloud.emailaddress.webscripts.AbstractInvalidDomainWebscript;
import org.alfresco.module.org_alfresco_module_cloud.tenant.BaseTenantWebScriptTest;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.springframework.extensions.webscripts.GUID;
import org.springframework.extensions.webscripts.TestWebScriptServer.DeleteRequest;
import org.springframework.extensions.webscripts.TestWebScriptServer.GetRequest;
import org.springframework.extensions.webscripts.TestWebScriptServer.PostRequest;
import org.springframework.extensions.webscripts.TestWebScriptServer.PutRequest;
import org.springframework.extensions.webscripts.TestWebScriptServer.Response;

/**
 * This class tests the Remote API of the {@link EmailAddressService}.
 * 
 * @author Neil McErlean
 * @since Alfresco Cloud Module
 */
public class EmailAddressRestApiTest extends BaseTenantWebScriptTest
{
    // Miscellaneous constants
    
    // validate one email address domain (deprecated)
    private final static String POST_VALIDATE_EMAIL_DOMAIN_URL  = "/internal/cloud/emailaddresses/validationqueue";
    
    // validate one or more email address domains in bulk
    private final static String POST_VALIDATE_EMAIL_DOMAINS_URL = "/internal/cloud/emailaddresses/validatedomains"; 
    
    private final static String INVALID_DOMAINS_URL = "/internal/cloud/emailaddresses/invaliddomains";

    private CloudTestContext cloudContext;

    @Override
    protected void setUp() throws Exception
    {
        super.setUp();
        
        cloudContext = new CloudTestContext(this);
        
        AuthenticationUtil.setFullyAuthenticatedUser(AuthenticationUtil.getAdminUserName());
    }

    @Override
    public void tearDown() throws Exception
    {
        cloudContext.cleanup();
    }
    
    public void testValidateEmailAddresses() throws Exception
    {
        // Validate a single valid email address
        String jsonString = createJsonBodyForValidateSinglePostReq("test@alfresco.com");
        
        Response rsp = sendRequest(new PostRequest(POST_VALIDATE_EMAIL_DOMAIN_URL, jsonString, "application/json"), 200);
        String contentAsString = rsp.getContentAsString();
        
        JSONObject jsonObjRsp = (JSONObject)JSONValue.parse(contentAsString);
        assertTrue("email should have been valid.",  (Boolean) jsonObjRsp.get("isValid"));
        
        // CLOUD-2184. Validate a single valid email address that has an apostrophe in the email address
        jsonString = createJsonBodyForValidateSinglePostReq("test.o'reilly@alfresco.com");
        
        rsp = sendRequest(new PostRequest(POST_VALIDATE_EMAIL_DOMAIN_URL, jsonString, "application/json"), 200);
        contentAsString = rsp.getContentAsString();
        
        jsonObjRsp = (JSONObject)JSONValue.parse(contentAsString);
        assertTrue("email should have been valid.",  (Boolean) jsonObjRsp.get("isValid"));
        
        // Validate a single invalid email address
        jsonString = createJsonBodyForValidateSinglePostReq("person@blacklisted.test");
        
        rsp = sendRequest(new PostRequest(POST_VALIDATE_EMAIL_DOMAIN_URL, jsonString, "application/json"), 200);
        contentAsString = rsp.getContentAsString();
        
        jsonObjRsp = (JSONObject)JSONValue.parse(contentAsString);
        assertFalse("email should not have been valid.",  (Boolean) jsonObjRsp.get("isValid"));
        assertEquals("BLACKLISTED", jsonObjRsp.get("failureReason"));
        assertEquals("This was blacklisted at request of dfdfdf@sfsds.com", jsonObjRsp.get("failureNotes"));
        

        // Validate both addresses in bulk
        List<String> emails = new ArrayList<String>(3);
        emails.add("test@alfresco.com");
        emails.add("person@blacklisted.test");
        // CLOUD-2184
        emails.add("test.o'reilly@alfresco.com");
        jsonString = createJsonBodyForValidateMultiplePostReq(emails);
        
        rsp = sendRequest(new PostRequest(POST_VALIDATE_EMAIL_DOMAINS_URL, jsonString, "application/json"), 200);
        contentAsString = rsp.getContentAsString();
        
        jsonObjRsp = (JSONObject)JSONValue.parse(contentAsString);
        JSONArray jsonArr = (JSONArray)jsonObjRsp.get("domainsChecked");
        assertEquals(2, jsonArr.size());
        
        for(int i = 0; i < jsonArr.size(); i++)
        {
            jsonObjRsp = (JSONObject)jsonArr.get(i);
            
            if (jsonObjRsp.get("domain").equals("alfresco.com"))
            {
                assertTrue("domain should have been valid.", (Boolean)jsonObjRsp.get("isValid"));
            }
            else if (jsonObjRsp.get("domain").equals("blacklisted.test"))
            {
                assertFalse("domain should not have been valid.", (Boolean) jsonObjRsp.get("isValid"));
                assertEquals("BLACKLISTED", jsonObjRsp.get("failureReason"));
                assertEquals("This was blacklisted at request of dfdfdf@sfsds.com", jsonObjRsp.get("failureNotes"));
            }
        }
    }
    
    @SuppressWarnings("unchecked")
    private String createJsonBodyForValidateSinglePostReq(String email) throws IOException
    {
        JSONObject obj = new JSONObject();
        obj.put("email", email);
        
        StringWriter stringWriter = new StringWriter();
        obj.writeJSONString(stringWriter);
        return stringWriter.toString();
    }
    
    @SuppressWarnings("unchecked")
    private String createJsonBodyForValidateMultiplePostReq(List<String> emails) throws IOException
    {
        JSONObject obj = new JSONObject();
        obj.put("emails", emails);
        
        StringWriter stringWriter = new StringWriter();
        obj.writeJSONString(stringWriter);
        return stringWriter.toString();
    }
    
    /**
     * @since Thor Phase 2 Sprint 1
     */
    public void testCreateAndGetInvalidEmailDomain() throws Exception
    {
        createAndGetInvalidEmailDomain();
    }

    private String createAndGetInvalidEmailDomain() throws IOException, UnsupportedEncodingException
    {
        String domain = GUID.generate() + ".test";
        FailureReason type = FailureReason.BLACKLISTED;
        String notes = "added by " + this.getClass().getSimpleName();
        String jsonBody = createCreateInvalidDomainJsonBody(domain, type, notes);
        
        // POST to create the new invalid domain
        Response rsp = sendRequest(new PostRequest(INVALID_DOMAINS_URL, jsonBody, "application/json"), 200);
        // A 200 response here is probably enough feedback
        
        // GET that specific domain back again
        rsp = sendRequest(new GetRequest(INVALID_DOMAINS_URL + "/" + domain), 200);
        String contentAsString = rsp.getContentAsString();
        
        JSONObject jsonObj = (JSONObject) JSONValue.parse(contentAsString);
        String domainGet = (String) jsonObj.get(AbstractInvalidDomainWebscript.PARAM_DOMAIN);
        String typeGet   = (String) jsonObj.get(AbstractInvalidDomainWebscript.PARAM_TYPE);
        String notesGet  = (String) jsonObj.get(AbstractInvalidDomainWebscript.PARAM_NOTES);
        
        // Test the json and match the metadata
        assertEquals(domain, domainGet);
        assertEquals(type.toString(), typeGet);
        assertEquals(notes, notesGet);
        
        return domain;
    }
    
    /**
     * @since Thor Phase 2 Sprint 1
     */
    public void testCreateInvalidDomainAtExistingDomain() throws Exception
    {
        String existingDomain = "gmail.com";
        FailureReason type = FailureReason.PUBLIC;
        String notes = "this should fail. added by " + this.getClass().getSimpleName();
        String jsonBody = createCreateInvalidDomainJsonBody(existingDomain, type, notes);
        
        // POST to create the new invalid domain - which should fail.
        sendRequest(new PostRequest(INVALID_DOMAINS_URL, jsonBody, "application/json"), 403);
    }
    
    /**
     * @since Thor Phase 2 Sprint 1
     */
    public void testGetNonExistentInvalidDomain() throws Exception
    {
        sendRequest(new GetRequest(INVALID_DOMAINS_URL + "/" + "alfrescoThisDomainWillNotExist"), 404);
    }
    
    /**
     * @since Thor Phase 2 Sprint 1
     */
    public void testListInvalidEmailDomainsWithPaging() throws Exception
    {
        // GET that specific domain back again
        Response rsp = sendRequest(new GetRequest(INVALID_DOMAINS_URL + "?startIndex=0&pageSize=5"), 200);
        String contentAsString = rsp.getContentAsString();
        
        JSONObject jsonObj = (JSONObject) JSONValue.parse(contentAsString);
        // Check out the JSON.
        
//  {
//     "data":
//     {
//        "total": 690,
//        "pageSize": 5,
//        "startIndex": 0,
//        "itemCount": 5,
//        "items":
//        [
//           {
//              "domain": "antispam.test",
//              "type": "ANTISPAM",
//              "notes": "a note"
//           },
//           {
//              "domain": "blacklisted.test",
//              "type": "BLACKLISTED",
//              "notes": "This was blacklisted at request of dfdfdf@sfsds.com"
//           }
//           // etc...
//        ]
//     }
//  }
        JSONObject dataObj = (JSONObject) jsonObj.get("data");
        assertEquals(new Long(5L), (Long) dataObj.get("pageSize"));
        assertEquals(new Long(5L), (Long) dataObj.get("itemCount"));
        assertEquals(new Long(0L), (Long) dataObj.get("startIndex"));
        JSONArray itemsArray = (JSONArray) dataObj.get("items");
        JSONObject domainObjAtIndex3 = (JSONObject) itemsArray.get(3);
        
        
        // Now let's get another page that overlaps the first.
        rsp = sendRequest(new GetRequest(INVALID_DOMAINS_URL + "?startIndex=1&pageSize=5"), 200);
        contentAsString = rsp.getContentAsString();
        
        JSONObject jsonObj2 = (JSONObject) JSONValue.parse(contentAsString);
        JSONObject dataObj2 = (JSONObject) jsonObj2.get("data");
        JSONArray itemsArray2 = (JSONArray) dataObj2.get("items");
        JSONObject domainObjAtIndex3_secondPage = (JSONObject) itemsArray2.get(2); // Off by one as the page was off by one.
        
        assertEquals(domainObjAtIndex3.get("domain"), domainObjAtIndex3_secondPage.get("domain"));
        assertEquals(domainObjAtIndex3.get("type"), domainObjAtIndex3_secondPage.get("type"));
        assertEquals(domainObjAtIndex3.get("notes"), domainObjAtIndex3_secondPage.get("notes"));
    }
    
    /**
     * @since Thor Phase 2 Sprint 1
     */
    public void testUpdateInvalidEmailDomain() throws Exception
    {
        // First create a test domain.
        String domain = createAndGetInvalidEmailDomain();
        
        // Now update its type & notes
        FailureReason newtype = FailureReason.ANTISPAM;
        String newNotes = "updated by " + this.getClass().getSimpleName();
        String jsonBody = createCreateInvalidDomainJsonBody(domain, newtype, newNotes);
        
        // PUT to update the invalid domain
        Response rsp = sendRequest(new PutRequest(INVALID_DOMAINS_URL + "/" + domain, jsonBody, "application/json"), 200);
        // A 200 response here is probably enough feedback
        
        // GET that specific domain back again
        rsp = sendRequest(new GetRequest(INVALID_DOMAINS_URL + "/" + domain), 200);
        String contentAsString = rsp.getContentAsString();
        
        JSONObject jsonObj = (JSONObject) JSONValue.parse(contentAsString);
        String domainGet = (String) jsonObj.get(AbstractInvalidDomainWebscript.PARAM_DOMAIN);
        String typeGet   = (String) jsonObj.get(AbstractInvalidDomainWebscript.PARAM_TYPE);
        String notesGet  = (String) jsonObj.get(AbstractInvalidDomainWebscript.PARAM_NOTES);
        
        // Test the json and match the metadata
        assertEquals(domain, domainGet);
        assertEquals(newtype.toString(), typeGet);
        assertEquals(newNotes, notesGet);
    }
    
    /**
     * @since Thor Phase 2 Sprint 1
     */
    public void testUpdateNonExistentInvalidEmailDomain() throws Exception
    {
        String nonExistentDomain = "alfrescothisdomainwillnotexist.test";
        
        // Now update its type & notes
        FailureReason newtype = FailureReason.ANTISPAM;
        String newNotes = "updated by " + this.getClass().getSimpleName();
        String jsonBody = createCreateInvalidDomainJsonBody(nonExistentDomain, newtype, newNotes);
        
        // PUT to update the invalid domain
        sendRequest(new PutRequest(INVALID_DOMAINS_URL + "/" + nonExistentDomain, jsonBody, "application/json"), 404);
    }
    
    /**
     * @since Thor Phase 2 Sprint 1
     */
    public void testDeleteInvalidEmailDomain() throws Exception
    {
        // First create a test domain.
        String domain = createAndGetInvalidEmailDomain();
        
        // DELETE it again
        sendRequest(new DeleteRequest(INVALID_DOMAINS_URL + "/" + domain), 200);
        // A 200 response here is probably enough feedback
        
        // GET that specific domain back again
        sendRequest(new GetRequest(INVALID_DOMAINS_URL + "/" + domain), 404);
    }
    
    /**
     * @since Thor Phase 2 Sprint 1
     */
    public void testDeleteNonExistentInvalidEmailDomain() throws Exception
    {
        sendRequest(new DeleteRequest(INVALID_DOMAINS_URL + "/" + "alfrescoThisDomainWillNotExist"), 404);
    }
    
    @SuppressWarnings("unchecked")
    private String createCreateInvalidDomainJsonBody(String domain, FailureReason type, String notes) throws IOException
    {
        JSONObject obj = new JSONObject();
        obj.put(AbstractInvalidDomainWebscript.PARAM_DOMAIN, domain);
        obj.put(AbstractInvalidDomainWebscript.PARAM_TYPE, type.toString());
        obj.put(AbstractInvalidDomainWebscript.PARAM_NOTES, notes);
        
        StringWriter stringWriter = new StringWriter();
        obj.writeJSONString(stringWriter);
        return stringWriter.toString();
    }
}
