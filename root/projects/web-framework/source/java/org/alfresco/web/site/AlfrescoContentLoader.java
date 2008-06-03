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

import org.alfresco.connector.Connector;
import org.alfresco.connector.Response;
import org.alfresco.connector.exception.RemoteConfigException;
import org.alfresco.web.scripts.Status;
import org.alfresco.web.site.exception.ContentLoaderException;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Loads content objects from Alfresco and hands back AlfrescoContent
 * objects for use by the application.
 * 
 * @author muzquiano
 */
public class AlfrescoContentLoader extends AbstractContentLoader
{
	public AlfrescoContentLoader(String endpointId)
	{
		super(endpointId);
	}
	
	/* (non-Javadoc)
	 * @see org.alfresco.web.site.ContentLoader#canHandle(java.lang.String)
	 */
	public boolean canHandle(String objectId)
	{
		return (objectId.startsWith("workspace://"));
	}
	
	/* (non-Javadoc)
	 * @see org.alfresco.web.site.ContentLoader#load(java.lang.String)
	 */
	public Content load(RequestContext context, String objectId)
		throws ContentLoaderException
	{
		Content content = null;
		
		// grab a connector
		Connector connector = null;
		try
		{
			if(context != null && context.getUserId() != null)
			{
				// create a connector that uses these credentials
				connector = FrameworkHelper.getConnector(context, getEndpointId());
			}
			else
			{
				// create an unauthenticated connector
				connector = FrameworkHelper.getConnector(getEndpointId());
			}
		}
		catch(RemoteConfigException rce)
		{
			throw new ContentLoaderException("Unable to acquire connector to endpoint id: " + endpointId, rce);
		}

		// fetch the object
		String uri = "/webframework/content/metadata?id=" + objectId;
		Response response = connector.call(uri);
		
		// if we got back an OK code
		if(response.getStatus().getCode() == Status.STATUS_OK)
		{
			// convert to JSON and create content object				
			String responseString = response.getResponse();
			try
			{
				JSONObject jsonObject = new JSONObject(responseString);
				content = new AlfrescoContent(getEndpointId(), objectId, jsonObject);
			}
			catch(JSONException je)
			{
				// something happened while trying to parse the JSON			
				content = new UnloadedContent(getEndpointId(), objectId);
				content.setStatusMessage("Unable to parse JSON for object with id: " + objectId);
				content.setStatusException(je);
			}        		
		}
		
		// if content is null, then we had a non-ok code
		// this is likely because the object doesn't exist or because
		// we were unauthorized to access it
		if(content == null)
		{
			content = new UnloadedContent(getEndpointId(), objectId);
		}
		
		// populate status items
		content.setStatusCode(response.getStatus().getCode());
		if(content.getStatusMessage() == null)
		{
			content.setStatusMessage(response.getStatus().getMessage());
		}
		if(content.getStatusException() == null)
		{
			content.setStatusException(response.getStatus().getException());
		}
        
        return content;
	}
}
