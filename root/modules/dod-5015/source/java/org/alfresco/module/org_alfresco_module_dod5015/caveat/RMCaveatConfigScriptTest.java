package org.alfresco.module.org_alfresco_module_dod5015.caveat;

import java.util.ArrayList;
import java.util.List;
import org.alfresco.model.ContentModel;
import org.alfresco.repo.security.authentication.AuthenticationComponent;
import org.alfresco.repo.web.scripts.BaseWebScriptTest;
import org.alfresco.service.cmr.security.AuthenticationService;
import org.alfresco.service.cmr.security.PersonService;
import org.alfresco.util.PropertyMap;
import org.alfresco.web.scripts.Status;
import org.alfresco.web.scripts.TestWebScriptServer.GetRequest;
import org.alfresco.web.scripts.TestWebScriptServer.Response;
import org.json.JSONArray;
import org.json.JSONObject;

public class RMCaveatConfigScriptTest extends BaseWebScriptTest
{
    private AuthenticationService authenticationService;
    private AuthenticationComponent authenticationComponent;
    private RMCaveatConfigService caveatConfigService;
    private PersonService personService;
    
    private static final String USER_ONE = "RMCaveatConfigTestOne";
    private static final String USER_TWO = "RMCaveatConfigTestTwo";
    
    protected final static String RM_LIST = "rma:smList";
    
    private static final String URL_RM_CONSTRAINTS = "/api/rma/admin/rmconstraints";
  
    @Override
    protected void setUp() throws Exception
    {
        this.caveatConfigService = (RMCaveatConfigService)getServer().getApplicationContext().getBean("CaveatConfigService");
        super.setUp();
    }
    
    private void createUser(String userName)
    {
        if (this.authenticationService.authenticationExists(userName) == false)
        {
            this.authenticationService.createAuthentication(userName, "PWD".toCharArray());
            
            PropertyMap ppOne = new PropertyMap(4);
            ppOne.put(ContentModel.PROP_USERNAME, userName);
            ppOne.put(ContentModel.PROP_FIRSTNAME, "firstName");
            ppOne.put(ContentModel.PROP_LASTNAME, "lastName");
            ppOne.put(ContentModel.PROP_EMAIL, "email@email.com");
            ppOne.put(ContentModel.PROP_JOBTITLE, "jobTitle");
            
            this.personService.createPerson(ppOne);
        }        
    }
    
    @Override
    protected void tearDown() throws Exception
    {
        super.tearDown();
        //this.authenticationComponent.setCurrentUser(AuthenticationUtil.getAdminUserName());
        
    }
    
     
    public void testGetRMConstraints() throws Exception
    {
        {
            Response response = sendRequest(new GetRequest(URL_RM_CONSTRAINTS), Status.STATUS_OK);    
        
            JSONObject top = new JSONObject(response.getContentAsString());
            System.out.println(response.getContentAsString());
            JSONArray data = top.getJSONArray("data");
        }
        
        /**
         * Add a list, then get it back
         */
        caveatConfigService.addRMConstraintList(RM_LIST);
        
        {
            Response response = sendRequest(new GetRequest(URL_RM_CONSTRAINTS), Status.STATUS_OK);    
            JSONObject top = new JSONObject(response.getContentAsString());
            System.out.println(response.getContentAsString());
            JSONArray data = top.getJSONArray("data");
            
            boolean found = false;
            assertTrue("no data returned",  data.length() > 0);
            for(int i = 0; i < data.length(); i++)
            {
                JSONObject obj = data.getJSONObject(i);
                String name = (String)obj.getString("constraintName");
                assertNotNull("constraintName is null", name);
                String url = (String)obj.getString("url");
                assertNotNull("detail url is null", name);
                if(name.equalsIgnoreCase(RM_LIST))
                {
                    found = true;
                }
                
                /**
                 * vallidate the detail URL returned
                 */
                sendRequest(new GetRequest(url), Status.STATUS_OK);  
            }
            
            assertTrue("constraintName not found", found);
        }       
        
    }
    
    /**
     * 
     * @throws Exception
     */
    public void testGetRMConstraint() throws Exception
    {
        /**
         * Delete the list to remove any junk then recreate it.
         */
        caveatConfigService.deleteRMConstraintList(RM_LIST);
        caveatConfigService.addRMConstraintList(RM_LIST);
        
        List<String> values = new ArrayList<String>();
        values.add("NOFORN");
        values.add("FGI");
        caveatConfigService.updateRMConstraintListAuthority(RM_LIST, "fbloggs", values);
        caveatConfigService.updateRMConstraintListAuthority(RM_LIST, "jrogers", values);
        caveatConfigService.updateRMConstraintListAuthority(RM_LIST, "jdoe", values);
        
        /**
         * Positive test Get the constraint 
         */
        {
            String url = URL_RM_CONSTRAINTS + "/" + "rma_smList";
            Response response = sendRequest(new GetRequest(url), Status.STATUS_OK);
            JSONObject top = new JSONObject(response.getContentAsString());
            
            JSONObject data = top.getJSONObject("data");
            System.out.println(response.getContentAsString());
            
            String constraintName = data.getString("constraintName");
            assertNotNull("constraintName is null", constraintName);
            
            JSONArray constraintDetails = data.getJSONArray("constraintDetails");
           
            assertTrue("details array does not contain 3 elements", constraintDetails.length() == 3);
            for(int i =0; i < constraintDetails.length(); i++)
            {
                JSONObject detail = constraintDetails.getJSONObject(i);
            }
        }
        
        /**
         * Negative test - Attempt to get a constraint that does exist
         */
        {
            String url = URL_RM_CONSTRAINTS + "/" + "rma_wibble";
            sendRequest(new GetRequest(url), Status.STATUS_NOT_FOUND); 
        }
        
        
        
    }
    
}

