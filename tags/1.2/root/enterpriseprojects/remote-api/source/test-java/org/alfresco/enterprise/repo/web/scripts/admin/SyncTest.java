package org.alfresco.enterprise.repo.web.scripts.admin;

import org.alfresco.enterprise.repo.web.scripts.BaseEnterpriseWebScriptTest;
import org.alfresco.repo.security.authentication.AuthenticationComponent;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.web.scripts.BaseWebScriptTest;
import org.alfresco.service.cmr.security.MutableAuthenticationService;
import org.json.JSONObject;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.TestWebScriptServer.DeleteRequest;
import org.springframework.extensions.webscripts.TestWebScriptServer.GetRequest;
import org.springframework.extensions.webscripts.TestWebScriptServer.PostRequest;
import org.springframework.extensions.webscripts.TestWebScriptServer.PutRequest;
import org.springframework.extensions.webscripts.TestWebScriptServer.Response;

/**
 * Unit test to test POST Sync REST API
 * 
 * @author Mark Rogers
 */
public class SyncTest extends BaseEnterpriseWebScriptTest
{    
    public final static String SYNC_URL = "/enterprise/admin/admin-sync";
    
    private MutableAuthenticationService authenticationService;
    private AuthenticationComponent authenticationComponent;

    @Override
    protected void setUp() throws Exception
    {
        super.setUp();
        this.authenticationService = (MutableAuthenticationService)getServer().getApplicationContext().getBean("AuthenticationService");
        this.authenticationComponent = (AuthenticationComponent)getServer().getApplicationContext().getBean("authenticationComponent");
 
        this.authenticationComponent.setCurrentUser(AuthenticationUtil.getAdminUserName());
        
               
    }
    
  
    @Override
    protected void tearDown() throws Exception
    {
        super.tearDown();
        this.authenticationComponent.setCurrentUser(AuthenticationUtil.getAdminUserName());
        
    }
    
    public void testPostSync() throws Exception
    { 
        /**
         * Call the sync web script
         */
        JSONObject syncObj = new JSONObject();
        Response response = sendRequest(new PostRequest(SYNC_URL, syncObj.toString(), "application/json"), Status.STATUS_OK); 
        JSONObject top = new JSONObject(response.getContentAsString());
    
        assertTrue("not successfull", top.getBoolean("success"));       	
    } // end testPostSync
       
}
