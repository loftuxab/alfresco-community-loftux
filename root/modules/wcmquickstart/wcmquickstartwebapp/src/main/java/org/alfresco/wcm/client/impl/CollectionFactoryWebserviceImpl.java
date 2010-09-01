/*
 * Copyright (C) 2005-2010 Alfresco Software Limited.
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
package org.alfresco.wcm.client.impl;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.alfresco.wcm.client.Asset;
import org.alfresco.wcm.client.AssetFactory;
import org.alfresco.wcm.client.CollectionFactory;
import org.alfresco.wcm.client.AssetCollection;
import org.alfresco.wcm.client.Query;
import org.alfresco.wcm.client.ResourceNotFoundException;
import org.alfresco.wcm.client.Section;
import org.alfresco.wcm.client.SectionFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.extensions.surf.exception.ConnectorServiceException;
import org.springframework.extensions.webscripts.connector.Connector;
import org.springframework.extensions.webscripts.connector.ConnectorService;
import org.springframework.extensions.webscripts.connector.Response;

/**
 * Get collection using a call to a web service. This is done to avoid having to perform
 * two queries - one for the collection meta data and one for the list of assets ids within
 * the collection. Open CMIS allows relationships to be retrieved with an object by setting a 
 * parameter of an operational context object passed to the query but this is only allowed if no
 * joins are used.
 * @author Chris Lack
 */
public class CollectionFactoryWebserviceImpl implements CollectionFactory
{	
	private final static Log log = LogFactory.getLog(CollectionFactoryWebserviceImpl.class);
	
    private SectionFactory sectionFactory;
	private AssetFactory assetFactory;
	private ConnectorService connectorService;
    
    /**
	 * Create a ResourceCollection from JSON
	 * @param jsonObject object representing the json response
	 * @return ResourceCollectionImpl collection object
     * @throws JSONException 
	 */
	private AssetCollectionImpl buildCollection(JSONObject jsonObject) throws JSONException 
	{
		AssetCollectionImpl collection = new AssetCollectionImpl();		
		collection.setId(jsonObject.getString("id"));
        collection.setName(jsonObject.getString("name"));
        collection.setTitle(jsonObject.getString("title"));
        collection.setDescription(jsonObject.getString("description"));              
		return collection;
	}
	
    /**
     * Build a list of related target node ids for the collection
     * @param jsonObject json object
     * @return List<String> list of related resource ids
     * @throws JSONException 
     */
    private List<String> buildRelatedAssetList(JSONObject jsonObject) throws JSONException
    {
        List<String> relatedIds = new ArrayList<String>();

        JSONArray relationships = jsonObject.getJSONArray("assets");
        for (int i = 0; i < relationships.length(); i++)
        {
        	String targetId = relationships.getString(i);
            relatedIds.add(targetId);
        }
        return relatedIds;
    }

    /**
     * @see org.alfresco.wcm.client.impl.CollectionFactoryWebserviceImpl#getCollection(String, String)
     */
    @Override
    public AssetCollection getCollection(String sectionId, String collectionName)
    {
    	return getCollection(sectionId, collectionName, 0, -1);
    }
    
    /**
     * @see org.alfresco.wcm.client.impl.CollectionFactoryWebserviceImpl#getCollection(String, String, int, int)
     */
    @Override
    public AssetCollection getCollection(String sectionId, String collectionName, int resultsToSkip, int maxResults)
    {	
		if (sectionId == null || sectionId.length() == 0)
		{
			throw new IllegalArgumentException("sectionId must be supplied");
		}
		if (collectionName == null || collectionName.length() == 0)
		{
			throw new IllegalArgumentException("collectionName must be supplied");
		}
		
		// Get the section so that we have the section's collections folder id
		Section section = sectionFactory.getSection(sectionId);
				
		try 
		{
			// Query the named collection under the collections folder
			Connector connector = connectorService.getConnector("alfresco-qs");
			
			String uri = "/api/assetcollections/"+URLEncoder.encode(collectionName,"UTF-8")+"?sectionid="+URLEncoder.encode(section.getId(),"UTF-8");
			log.debug("About to call: "+uri);
			Response response = connector.call(uri);
			if (response == null) throw new ResourceNotFoundException("No response for "+uri);
			String jsonString = response.getText();
			JSONObject data;
			try {
				JSONObject jsonObject = new JSONObject(jsonString);					
				data = (JSONObject)jsonObject.get("data");
			
			}
			catch (JSONException e) {
				log.warn("Collection "+collectionName+" not found for section "+section.getPath());
				return null;
			}
			
			AssetCollectionImpl collection = buildCollection(data);	
			
			// Get the list of ids of assets in the collection
			List<String> assetIds = buildRelatedAssetList(data);
			
			//TODO Remove dummy data code.....
			/*if (maxResults != -1) {
				List<String> list = new ArrayList<String>();
				list.addAll(assetIds);
				for (int i = 0; i < 7; i++) {
					assetIds.addAll(list);
				}
			}*/

			Query query = new Query();
			query.setSectionId(sectionId);
			query.setMaxResults(maxResults);
			query.setResultsToSkip(resultsToSkip);
	        collection.setQuery(query);
	        collection.setTotalSize(assetIds.size());			

			if (assetIds.size() > 0) {
				
				// If this is a paginated query then select the subset of ids for which the assets should be fetched.
				if (maxResults != -1) {
					int end = resultsToSkip+maxResults;
					assetIds = assetIds.subList(resultsToSkip, end > assetIds.size() ? assetIds.size() : end);					
				}
								
				// Get the actual asset objects.
				List<Asset> assets = assetFactory.getAssetsById(assetIds);		
				collection.setAssets(assets);				
			}
			return collection;
		}
		catch (ConnectorServiceException e) 
		{
			throw new RuntimeException("Connection to alfresco endpoint failed", e);
		}
		catch (JSONException e) 
		{
			throw new RuntimeException("Parsing getCollection ws JSON response failed", e);
		} 
		catch (UnsupportedEncodingException e)
        {
			throw new RuntimeException("Error encoding URL", e);
        }		
    }

    public void setSectionFactory(SectionFactory sectionFactory)
    {
        this.sectionFactory = sectionFactory;
    }
    
    public void setAssetFactory(AssetFactory assetFactory)
    {
        this.assetFactory = assetFactory;
    }    
    
    public void setConnectorService(ConnectorService connectorService) 
    {
    	this.connectorService = connectorService;
    }
}
