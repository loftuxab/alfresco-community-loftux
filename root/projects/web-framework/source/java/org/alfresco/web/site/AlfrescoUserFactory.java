/*
 * Copyright (C) 2005-2008 Alfresco Software Limited.
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
 * FLOSS exception.  You should have recieved a copy of the text describing 
 * the FLOSS exception, and it is also available here: 
 * http://www.alfresco.com/legal/licensing"
 */
package org.alfresco.web.site;

import javax.servlet.http.HttpServletRequest;

import org.alfresco.connector.remote.Connector;
import org.alfresco.connector.remote.Response;
import org.alfresco.connector.remote.WebConnector;
import org.alfresco.web.site.exception.UserFactoryException;
import org.alfresco.web.site.model.Endpoint;
import org.alfresco.web.site.remote.ConnectorFactory;
import org.dom4j.Document;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * This factory loads users from Alfresco, fetching their properties
 * and so forth.
 * 
 * @author muzquiano
 */
public class AlfrescoUserFactory extends UserFactory
{
    public static final String ALFRESCO_SYSTEM_ENDPOINT_ID = "alfresco-system";
    public static final String ALFRESCO_USER_ENDPOINT_ID = "alfresco-user";

	/* (non-Javadoc)
	 * @see org.alfresco.web.site.UserFactory#authenticate(org.alfresco.web.site.RequestContext, javax.servlet.http.HttpServletRequest, java.lang.String, java.lang.String)
	 */
	public boolean authenticate(HttpServletRequest request, String username, String password)
    {
        //String endpointId = ALFRESCO_SYSTEM_ENDPOINT_ID;

        // Load the endpoint
        //Endpoint endpoint = ModelUtil.getEndpoint(context, endpointId);

        // Webscript to use
        String webscriptUri = "/service/api/login?u=" + username + "&pw=" + password;

        // get a web connector
        // this bypasses the credential vault
        //WebConnector webConnector = ConnectorFactory.newWebConnector(endpoint.getEndpointURL());
        WebConnector webConnector = ConnectorFactory.newWebConnector("http://localhost:8080/alfresco");
        Response response = webConnector.call(webscriptUri);

        // get the response string
        String responseString = response.getResponse();
        
        // this is overkill but does the trick
        String ticket = null;
        try
        {
        	Document document = org.dom4j.DocumentHelper.parseText(responseString);
        	ticket = document.getRootElement().getText();
        }
        catch(Exception ex) 
        {
        	// an invalid ticket, thus invalid user credentials       	
        }
        
        return(ticket != null);
    }
	
    /* (non-Javadoc)
     * @see org.alfresco.web.site.UserFactory#loadUser(org.alfresco.web.site.RequestContext, javax.servlet.http.HttpServletRequest, java.lang.String)
     */
    public User loadUser(RequestContext context, HttpServletRequest request,
            String user_id) throws UserFactoryException
    {
    	User user = null;
        String endpointId = ALFRESCO_SYSTEM_ENDPOINT_ID;

        // Load the endpoint
        Endpoint endpoint = ModelUtil.getEndpoint(context, endpointId);
        if(endpoint != null)
        {
	        // Webscript to use
	        String webscriptUri = "/service/content/query?user=" + user_id;
	
	        // get a web connector
	        // this routes through the credential vault
	        Connector conn = ConnectorFactory.newInstance(context, endpoint);
	        Response response = conn.call(webscriptUri);
	        String responseString = response.getResponse();
	        
	        // Load the user from the JSON parser
	        JSONObject jsonObject = null;
	        try
	        {
	        	jsonObject = new JSONObject(responseString);
	        	
	        	JSONObject properties = jsonObject.getJSONObject("properties");
	        	
	        	user = new User(user_id);
	            user.setFirstName(properties.getString("{http://www.alfresco.org/model/content/1.0}firstName"));
	            //user.setMiddleName(value);
	            user.setLastName(properties.getString("{http://www.alfresco.org/model/content/1.0}lastName"));
	            
	            //user.setHomePhone(value);
	            //user.setMobilePhone(value);
	            //user.setWorkPhone(value);
	            
	            //user.setAddress1(value);
	            //user.setAddress2();        
	            //user.setCity(value);
	            //user.setState(value);
	            //user.setZipCode(value);
	            //user.setCountry(value);        	
	        }
	        catch(Exception ex)
	        {
	        	// unable to read back the user json object
	        	throw new UserFactoryException("Unable to retrieve user from repository", ex);
	        }
        }

        return user;
    }
}
