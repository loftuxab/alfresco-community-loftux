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

import org.alfresco.connector.Connector;
import org.alfresco.connector.Response;
import org.alfresco.web.scripts.WebFrameworkScriptRemote;
import org.alfresco.web.site.exception.UserFactoryException;
import org.dom4j.Document;
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
		String endpointId = ALFRESCO_SYSTEM_ENDPOINT_ID;
		
		String ticket = null;
		try
		{
			// request context
			RequestContext context = RequestUtil.getRequestContext(request);
			
			// create a connector for the current user
			WebFrameworkScriptRemote remote = new WebFrameworkScriptRemote(context);
			Connector connector = remote.connect(endpointId);
		
			// call the authentication ticket provider
			String uri = "/service/api/login?u=" + username + "&pw=" + password;
			Response response = connector.call(uri);
						
			// parse out the ticket
	        String responseString = response.getResponse();
        
        	Document document = org.dom4j.DocumentHelper.parseText(responseString);
        	ticket = document.getRootElement().getText();
        }
        catch(Exception ex) 
        {
        	// many things might have happened
        	// an invalid ticket or perhaps a connectivity issue
        	// at any rate, we cannot authenticate
        }
        
        return(ticket != null);
    }
	
    /* (non-Javadoc)
     * @see org.alfresco.web.site.UserFactory#loadUser(org.alfresco.web.site.RequestContext, javax.servlet.http.HttpServletRequest, java.lang.String)
     */
    public User loadUser(RequestContext context, HttpServletRequest request,
            String user_id) throws UserFactoryException
    {
        String endpointId = ALFRESCO_SYSTEM_ENDPOINT_ID;
        User user = null;
        
		try
		{
			// create a connector for the current user
			WebFrameworkScriptRemote remote = new WebFrameworkScriptRemote(context);
			Connector connector = remote.connect(endpointId);
		
			// call the authentication ticket provider
			//String uri = "/service/content/query?user=" + user_id;
			String uri = "/service/webframework/content/metadata?user=" + user_id;
			Response response = connector.call(uri);
			
			String responseString = response.getResponse();
						
	        // Load the user from the JSON parser
	        JSONObject jsonObject = new JSONObject(responseString);
	        	
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

        return user;
    }
}
