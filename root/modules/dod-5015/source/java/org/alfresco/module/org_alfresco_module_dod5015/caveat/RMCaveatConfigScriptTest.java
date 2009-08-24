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
import org.alfresco.web.scripts.TestWebScriptServer.PostRequest;
import org.alfresco.web.scripts.TestWebScriptServer.DeleteRequest;
import org.alfresco.web.scripts.TestWebScriptServer.PutRequest;
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
        caveatConfigService.addRMConstraint(RM_LIST, "", new String[0]);
        
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
        caveatConfigService.deleteRMConstraint(RM_LIST);
        caveatConfigService.addRMConstraint(RM_LIST, "", new String[0]);
      
        
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
            
//            JSONArray constraintDetails = data.getJSONArray("constraintDetails");
//           
//            assertTrue("details array does not contain 3 elements", constraintDetails.length() == 3);
//            for(int i =0; i < constraintDetails.length(); i++)
//            {
//                JSONObject detail = constraintDetails.getJSONObject(i);
//            }
        }
        
        /**
         * 
         * @throws Exception
         */
         
        /**
         * Negative test - Attempt to get a constraint that does exist
         */
        {
            String url = URL_RM_CONSTRAINTS + "/" + "rma_wibble";
            sendRequest(new GetRequest(url), Status.STATUS_NOT_FOUND); 
        }
        
        
        
    }
    
    /**
     * Create an RM Constraint
     * @throws Exception
     */
    public void testCreateRMConstraint() throws Exception
    {
        /**
         * Delete the list to remove any junk then recreate it.
         */
        caveatConfigService.deleteRMConstraint(RM_LIST);

        /**
         * 
         */
 
        JSONArray array = new JSONArray();
        array.put("NOFORN");
        array.put("FGI");
        
        JSONObject obj = new JSONObject();
        obj.put("allowedValues", array);
        obj.put("constraintName", RM_LIST);
        obj.put("constraintTitle", "this is the title");
        
        System.out.println(obj.toString());
        
        /**
         * Now do a post to create a new list
         */
        {
            Response response = sendRequest(new PostRequest(URL_RM_CONSTRAINTS, obj.toString(), "application/json"), Status.STATUS_CREATED); 
            // Check the response
        
        }
        
        /**
         * Now go and get the constraint 
         */
        {
            String url = URL_RM_CONSTRAINTS + "/" + "rma_smList";
            Response response = sendRequest(new GetRequest(url), Status.STATUS_OK);
            JSONObject top = new JSONObject(response.getContentAsString());
            
            JSONObject data = top.getJSONObject("data");
            System.out.println(response.getContentAsString());
            
            String constraintName = data.getString("constraintName");
            assertNotNull("constraintName is null", constraintName);
            
//            JSONArray constraintDetails = data.getJSONArray("constraintDetails");
//           
//            assertTrue("details array does not contain 3 elements", constraintDetails.length() == 3);
//            for(int i =0; i < constraintDetails.length(); i++)
//            {
//                JSONObject detail = constraintDetails.getJSONObject(i);
//            }
        }
        
        /**
         * Now a constraint with a generated name
         */
        
        /**
         * Negative tests - duplicate list
         */
           
    }
    
    /**
     * Create an RM Constraint
     * @throws Exception
     */
    public void testUpdateRMConstraint() throws Exception
    {
        /**
         * Delete the list to remove any junk then recreate it.
         */
        caveatConfigService.deleteRMConstraint(RM_LIST);
        caveatConfigService.addRMConstraint(RM_LIST, "", new String[0]);
        
        List<String> values = new ArrayList<String>();
        values.add("NOFORN");
        values.add("FGI");
        caveatConfigService.updateRMConstraintListAuthority(RM_LIST, "fbloggs", values);
        caveatConfigService.updateRMConstraintListAuthority(RM_LIST, "jrogers", values);
        caveatConfigService.updateRMConstraintListAuthority(RM_LIST, "jdoe", values);


        /**
         * 
         */
 
        JSONArray array = new JSONArray();
        array.put("NOFORN");
        array.put("NOCONTRACT");
        
        JSONObject obj = new JSONObject();
        obj.put("allowedValues", array);
        obj.put("constraintName", RM_LIST);
        obj.put("constraintTitle", "this is the new title");
        
        System.out.println(obj.toString());
        
        /**
         * Now do a post to create a new list
         */
        {
            Response response = sendRequest(new PutRequest(URL_RM_CONSTRAINTS + "/" + RM_LIST, obj.toString(), "application/json"), Status.STATUS_OK); 
            // Check the response
        
        }
        
              
    }
    
    public void testGetRMConstraintValues() throws Exception
    {
        /**
         * Delete the list to remove any junk then recreate it.
         */
        caveatConfigService.deleteRMConstraint(RM_LIST);
        caveatConfigService.addRMConstraint(RM_LIST, "", new String[0]);
      
        
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
            String url = URL_RM_CONSTRAINTS + "/" + "rma_smList/values";
            Response response = sendRequest(new GetRequest(url), Status.STATUS_OK);
            JSONObject top = new JSONObject(response.getContentAsString());
            
            JSONObject data = top.getJSONObject("data");
            System.out.println(response.getContentAsString());
            
//            String constraintName = data.getString("constraintName");
//            assertNotNull("constraintName is null", constraintName);
            
//            JSONArray constraintDetails = data.getJSONArray("constraintDetails");
//           
//            assertTrue("details array does not contain 3 elements", constraintDetails.length() == 3);
//            for(int i =0; i < constraintDetails.length(); i++)
//            {
//                JSONObject detail = constraintDetails.getJSONObject(i);
//            }
        }
    }

 
    
    /**
     * Update a value in a constraint
     * @throws Exception
     */
    public void testUpdateRMConstraintValue() throws Exception
    {
        caveatConfigService.deleteRMConstraint(RM_LIST);
        caveatConfigService.addRMConstraint(RM_LIST, "", new String[0]);
        
        /**
         * Add some data to an empty list
         */
        { 
            JSONArray values = new JSONArray();
        
            JSONArray authorities = new JSONArray();
            authorities.put("fbloggs");
            authorities.put("jdoe");
        
            JSONObject valueA = new JSONObject();
            valueA.put("value", "NOFORN");
            valueA.put("authorities", authorities); 
        
            values.put(valueA);
        
            JSONObject valueB = new JSONObject();
            valueB.put("value", "FGI");
            valueB.put("authorities", authorities); 
        
            values.put(valueB);
        
            JSONObject obj = new JSONObject();
            obj.put("values", values);
        
       
            /**
             * Do the first update - should get back
             * NOFORN - fbloggs, jdoe
             * FGI - fbloggs, jdoe
             */
            Response response = sendRequest(new PostRequest(URL_RM_CONSTRAINTS + "/" + RM_LIST + "/values" , obj.toString(), "application/json"), Status.STATUS_OK);   
            JSONObject top = new JSONObject(response.getContentAsString());
        
            JSONObject data = top.getJSONObject("data");
            System.out.println(response.getContentAsString());
            assertNotNull("data is null", data);
            
            JSONArray myValues = data.getJSONArray("values");
            assertTrue("two values not found", myValues.length() == 2); 
            for(int i = 0; i < myValues.length(); i++)
            {
                JSONObject myObj = myValues.getJSONObject(i);
            }    
        }
        
        
        
        
        /**
         * Add to a new value, NOCON, fbloggs, jrogers
         */
        { 
            JSONArray values = new JSONArray();
        
            JSONArray authorities = new JSONArray();
            authorities.put("fbloggs");
            authorities.put("jrogers");
        
            JSONObject valueA = new JSONObject();
            valueA.put("value", "NOCON");
            valueA.put("authorities", authorities); 
        
            values.put(valueA);
        
        
            JSONObject obj = new JSONObject();
            obj.put("values", values);
        
       
            /**
             * Add a new value - should get back
             * NOFORN - fbloggs, jdoe
             * FGI - fbloggs, jdoe
             * NOCON - fbloggs, jrogers
             */
            Response response = sendRequest(new PostRequest(URL_RM_CONSTRAINTS + "/" + RM_LIST + "/values" , obj.toString(), "application/json"), Status.STATUS_OK);   
            JSONObject top = new JSONObject(response.getContentAsString());
        
            JSONObject data = top.getJSONObject("data");
            System.out.println(response.getContentAsString());
            assertNotNull("data is null", data);
            
            JSONArray myValues = data.getJSONArray("values");
            assertTrue("three values not found", myValues.length() == 3); 
            for(int i = 0; i < myValues.length(); i++)
            {
                JSONObject myObj = myValues.getJSONObject(i);
            }
        }
        
        /**
         * Add to an existing value (NOFORN, jrogers)
         * should get back
         * NOFORN - fbloggs, jdoe, jrogers
         * FGI - fbloggs, jdoe
         * NOCON - fbloggs, jrogers
         */
        {
            JSONArray values = new JSONArray();
        
            JSONArray authorities = new JSONArray();
            authorities.put("fbloggs");
            authorities.put("jrogers");
            authorities.put("jdoe");
    
            JSONObject valueA = new JSONObject();
            valueA.put("value", "NOFORN");
            valueA.put("authorities", authorities); 
    
            values.put(valueA);
    
    
            JSONObject obj = new JSONObject();
            obj.put("values", values);
    
            Response response = sendRequest(new PostRequest(URL_RM_CONSTRAINTS + "/" + RM_LIST + "/values" , obj.toString(), "application/json"), Status.STATUS_OK);   
            JSONObject top = new JSONObject(response.getContentAsString());
    
            JSONObject data = top.getJSONObject("data");
            System.out.println(response.getContentAsString());
            assertNotNull("data is null", data);
        
            JSONArray myValues = data.getJSONArray("values");
            assertTrue("three values not found", myValues.length() == 3); 
            for(int i = 0; i < myValues.length(); i++)
            {
                JSONObject myObj = myValues.getJSONObject(i);
            }
        }

        
        /**
         * Remove from existing value (NOCON, fbloggs)
         */
        {
            JSONArray values = new JSONArray();
        
            JSONArray authorities = new JSONArray();
            authorities.put("jrogers");
    
            JSONObject valueA = new JSONObject();
            valueA.put("value", "NOCON");
            valueA.put("authorities", authorities); 
    
            values.put(valueA);
    
    
            JSONObject obj = new JSONObject();
            obj.put("values", values);
    
   
            /**
             * should get back
             * NOFORN - fbloggs, jdoe
             * FGI - fbloggs, jdoe
             * NOCON - jrogers
             */
            Response response = sendRequest(new PostRequest(URL_RM_CONSTRAINTS + "/" + RM_LIST + "/values" , obj.toString(), "application/json"), Status.STATUS_OK);   
            JSONObject top = new JSONObject(response.getContentAsString());
    
            JSONObject data = top.getJSONObject("data");
            System.out.println(response.getContentAsString());
            assertNotNull("data is null", data);
        
            JSONArray myValues = data.getJSONArray("values");
            assertTrue("three values not found", myValues.length() == 3); 
            boolean foundNOCON = false;
            boolean foundNOFORN = false;
            boolean foundFGI = false;
            
            for(int i = 0; i < myValues.length(); i++)
            {
                JSONObject myObj = myValues.getJSONObject(i);
                
                if(myObj.getString("valueName").equalsIgnoreCase("NOCON"))
                {
                    foundNOCON = true;
                }
                if(myObj.getString("valueName").equalsIgnoreCase("NOFORN"))
                {
                    foundNOFORN = true;
                }
                if(myObj.getString("valueName").equalsIgnoreCase("FGI"))
                {
                    foundFGI = true;
                }
            }
            
            assertTrue("not found NOCON", foundNOCON);
            assertTrue("not found NOFORN", foundNOFORN);
            assertTrue("not found FGI", foundFGI);
        }
    }
    
    
    /**
     * Delete the entire constraint
     * 
     * @throws Exception
     */
    public void testDeleteRMConstraint() throws Exception
    {
        /**
         * Delete the list to remove any junk then recreate it.
         */
        caveatConfigService.deleteRMConstraint(RM_LIST);
        caveatConfigService.addRMConstraint(RM_LIST, "", new String[0]);
        
        /**
         * Now do a delete
         */
        Response response = sendRequest(new DeleteRequest(URL_RM_CONSTRAINTS + "/" + RM_LIST), Status.STATUS_OK); 
        
        /**
         * Now delete the list that should have been deleted
         */
        {
            sendRequest(new DeleteRequest(URL_RM_CONSTRAINTS + "/" + RM_LIST), Status.STATUS_NOT_FOUND); 
        }
        
        /**
         * Negative test - delete list that does not exist
         */
        {
            sendRequest(new DeleteRequest(URL_RM_CONSTRAINTS + "/" + "rma_wibble"), Status.STATUS_NOT_FOUND);    
        }
    }
    
}

