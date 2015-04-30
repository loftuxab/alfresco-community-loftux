/*
 * Copyright 2005-2011 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.enterprise.workflow.activiti;

import java.io.IOException;
import java.io.StringWriter;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.alfresco.enterprise.repo.sync.connector.CloudConnectorService;
import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.repo.content.MimetypeMap;
import org.alfresco.repo.security.authentication.AuthenticationException;
import org.alfresco.service.cmr.remoteconnector.RemoteConnectorRequest;
import org.alfresco.service.cmr.remoteconnector.RemoteConnectorResponse;
import org.alfresco.service.cmr.remoteticket.RemoteSystemUnavailableException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.springframework.extensions.webscripts.Status;

/**
 * Helper class for hybrid workflow related functionality.
 *
 * @author Frederik Heremans
 */
public class HybridWorkflowHelper
{
    private static final String USER_DETAILS_URL = "api/forms/picker/items";
    private static final String SITE_MEMBERSHIP_URL = "api/sites/{0}/memberships?size=250&nf={1}&authorityType=USER";
    
    private static final String FIELD_ITEMS = "items";
    private static final String FIELD_AUTHORITY = "authority";
    private static final String FIELD_USERNAME = "userName";
    private static final String FIELD_DATA = "data";
    private static final String FIELD_ITEM_VALUE_TYPE = "itemValueType";
    private static final String FIELD_ITEM_VALUE_TYPE_NODEREF = "nodeRef";
    private static final Object FIELD_NAME = "name";

    
    private CloudConnectorService cloudConnectorService;
    
    public boolean isMemberOfSite(String userName, String network, String siteId)
    {
        // Build a request to fetch the assignee details
        String url = MessageFormat.format(SITE_MEMBERSHIP_URL, siteId, userName);
        RemoteConnectorRequest cloudRequest = cloudConnectorService.buildCloudRequest(url, network, "GET");
        
        JSONArray itemsResponse = getJsonArrayFromResponse(cloudRequest, Status.STATUS_OK);
        if(itemsResponse != null && itemsResponse.size() > 0)
        {
            JSONObject item = null;
            for(Object object : itemsResponse)
            {
                item = (JSONObject) object;
                JSONObject authority = (JSONObject) item.get(FIELD_AUTHORITY);
                if(authority != null) 
                {
                    // Check if username matches
                    if(userName.equals(authority.get(FIELD_USERNAME)))
                    {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    @SuppressWarnings("unchecked")
    public List<String> getInvalidCloudAssignees(List<String> cloudAssigneeNodeRefs, String network, String siteId)
    {
        List<String> invalidAssignees = new ArrayList<String>();
        
        // Build a request to fetch the assignee details
        RemoteConnectorRequest cloudRequest = cloudConnectorService.buildCloudRequest(USER_DETAILS_URL, network, "POST");
        
        JSONObject requestBody = new JSONObject();
        JSONArray items = new JSONArray();
        
        // Add each noderef to items to fetch info for
        for(String assignee : cloudAssigneeNodeRefs)
        {
            items.add(assignee);
        }
        
        requestBody.put(FIELD_ITEMS, items);
        requestBody.put(FIELD_ITEM_VALUE_TYPE, FIELD_ITEM_VALUE_TYPE_NODEREF);
        populateRequestBody(cloudRequest, requestBody);
        
        JSONObject itemsResponse = getJsonObjectFromResponse(cloudRequest, Status.STATUS_OK);
        if(itemsResponse == null)
        {
            throw new AlfrescoRuntimeException("Cloud-request did not result in a valid JSONObject");
        }
        
        int resultCount = 0;
        JSONObject data = (JSONObject) itemsResponse.get(FIELD_DATA);
        if(data != null)
        {
            JSONArray array = (JSONArray) data.get(FIELD_ITEMS);
            resultCount = array.size();
         
            JSONObject itemData = null;
            for(Object itemObject : array)
            {
                itemData = (JSONObject) itemObject;
                String actualUserName = extractUserName((String) itemData.get(FIELD_NAME));
                if(!isMemberOfSite(actualUserName, network, siteId))
                {
                    invalidAssignees.add(actualUserName);
                }
            }
        }
        
        if(resultCount != cloudAssigneeNodeRefs.size())
        {
            // Number of returned items mismatched the requested ones, possibly a selected
            // use does not exist anymore on the cloud or something went wrong
            throw new AlfrescoRuntimeException("Not all selected assignees exist on the cloud");
        }
        return invalidAssignees;
    }
    
    /**
     * Username from cloud-webscript is in format "Firstname Lastname (username)". THis method
     * extracts the actual username.
     * 
     * @param username
     * @return actual username
     */
    protected String extractUserName(String username)
    {
        if(username != null && !username.isEmpty()) 
        {
            int start = username.indexOf('(') + 1;
            if(start < username.length()) 
            {
                return username.substring(start, username.length() - 1);
            }
        }
        return null;
    }

    protected void populateRequestBody(RemoteConnectorRequest request, JSONObject jsonObject)
    {
        StringWriter stringWriter = new StringWriter();
        try
        {
            jsonObject.writeJSONString(stringWriter);
        } 
        catch (IOException iox)
        {
            throw new AlfrescoRuntimeException("Error creating JSON for cloud-request body", iox);
        }
        
        String jsonString = stringWriter.toString();
        
        request.setContentType(MimetypeMap.MIMETYPE_JSON);
        request.setRequestBody(jsonString);
    }
    
    protected Object getJsonFromResponse(RemoteConnectorRequest cloudRequest, int expectedStatus)
    {
        try
        {
            RemoteConnectorResponse response = cloudConnectorService.executeCloudRequest(cloudRequest);
            int httpStatus = response.getStatus();
            
            if(httpStatus != expectedStatus) 
            {
                throw new AlfrescoRuntimeException("Unexpected response received from cloud-request: " + httpStatus);
            }
            
            String contentAsString = response.getResponseBodyAsString();
            if (contentAsString == null || contentAsString.length() == 0)
            {
                throw new RemoteSystemUnavailableException("JSON response required but none received");
            }

            // Parse response as JSON
            return JSONValue.parse(contentAsString);
        }
        catch (AuthenticationException error)
        {
            throw new AlfrescoRuntimeException("Authentication error while performing cloud-request", error);
        }
        catch (IOException error)
        {
            throw new AlfrescoRuntimeException("Error while performing cloud-request", error);
        }
    }
    
    protected JSONObject getJsonObjectFromResponse(RemoteConnectorRequest cloudRequest, int expectedStatus)
    {
        Object object = getJsonFromResponse(cloudRequest, expectedStatus);
        if(object instanceof JSONObject)
        {
            return (JSONObject) object; 
        }
        return null;
    }
    
    protected JSONArray getJsonArrayFromResponse(RemoteConnectorRequest cloudRequest, int expectedStatus)
    {
        Object object = getJsonFromResponse(cloudRequest, expectedStatus);
        if(object instanceof JSONArray)
        {
            return (JSONArray) object; 
        }
        return null;
    }
    
    public void setCloudConnectorService(CloudConnectorService cloudConnectorService)
    {
        this.cloudConnectorService = cloudConnectorService;
    }
}
