/*
 * Copyright 2005-2012 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.enterprise.repo.web.scripts.sync;

import org.alfresco.enterprise.repo.sync.SyncAdminService;
import org.alfresco.enterprise.repo.sync.SyncAdminServiceImpl;
import org.alfresco.enterprise.repo.web.scripts.BaseEnterpriseWebScriptTest;
import org.alfresco.repo.security.authentication.AuthenticationComponent;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.springframework.extensions.webscripts.TestWebScriptServer.GetRequest;
import org.springframework.extensions.webscripts.TestWebScriptServer.Response;

public class SyncConfigGetTest extends BaseEnterpriseWebScriptTest
{
    // Miscellaneous constants used throughout this test class.
    private static final String GET_CONFIG_URL = "/enterprise/sync/config";
   
    private SyncAdminService          syncAdminService;
    
    @Override protected void setUp() throws Exception
    {
        super.setUp();
           
        syncAdminService  = getServer().getApplicationContext().getBean("syncAdminService", SyncAdminService.class);
        //this.authenticationService = (MutableAuthenticationService)getServer().getApplicationContext().getBean("AuthenticationService");
        AuthenticationComponent authenticationComponent = (AuthenticationComponent)getServer().getApplicationContext().getBean("authenticationComponent");
        //this.personService = (PersonService)getServer().getApplicationContext().getBean("PersonService");
        //this.siteService = (SiteService)getServer().getApplicationContext().getBean("SiteService");
        //this.nodeService = (NodeService)getServer().getApplicationContext().getBean("NodeService");
        //this.authorityService = (AuthorityService)getServer().getApplicationContext().getBean("AuthorityService");
        // sets the testMode property to true via spring injection. This will prevent emails
        // from being sent from within this test case.
        authenticationComponent.setSystemUserAsCurrentUser();
        
        // Push the "developer" mode switch to stop checking licenses
        ((SyncAdminServiceImpl)getServer().getApplicationContext().getBean("syncAdminService")).setCheckLicenseForSyncMode(false);
    }
    
    @Override protected void tearDown() throws Exception
    {
    }
    
    public void testGetSyncMode() throws Exception
    {
        String url = GET_CONFIG_URL;
        
        Response resp =  sendRequest(new GetRequest(url), 200);
        
        String contentAsString = resp.getContentAsString();
        
        JSONObject jsonRsp = (JSONObject) JSONValue.parse(contentAsString);
        assertNotNull("Problem reading JSON", jsonRsp);
        
        assertNotNull("syncMode was null", jsonRsp.get("syncMode"));
        
        assertEquals("syncMode was different", syncAdminService.getMode().toString(), jsonRsp.get("syncMode"));
    }
}
