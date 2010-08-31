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
package org.alfresco.module.org_alfresco_module_wcmquickstart.model;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.model.ContentModel;
import org.alfresco.module.org_alfresco_module_wcmquickstart.util.contextparser.ContextParser;
import org.alfresco.module.org_alfresco_module_wcmquickstart.util.contextparser.ContextParserService;
import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.repo.policy.Behaviour.NotificationFrequency;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.search.ResultSet;
import org.alfresco.service.cmr.search.SearchParameters;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.namespace.QName;

/**
 * ws:webassetCollection behaviours
 * 
 * @author Roy Wetherall
 */
public class WebassetCollectionType implements WebSiteModel,
 											   NodeServicePolicies.OnUpdatePropertiesPolicy,
 											   NodeServicePolicies.OnCreateChildAssociationPolicy,
 											   NodeServicePolicies.OnCreateAssociationPolicy
{
	/** Policy component */
	private PolicyComponent policyComponent;
	
	/** Node service */
	private NodeService nodeService;
	
	/** Search service */
	private SearchService searchService;
	
	/** On create association behaviour */
	private JavaBehaviour onCreateAssociation;
	
	/** Context parser service */
	private ContextParserService contextParserService;
	
	/** Search store */
	private String searchStore = "workspace://SpacesStore";
		
	/**
	 * Set the policy component
	 * 
	 * @param policyComponent	policy component
	 */
	public void setPolicyComponent(PolicyComponent policyComponent) 
	{
		this.policyComponent = policyComponent;
	}
	
	/**
	 * Set the node service
	 * 
	 * @param nodeService	node service
	 */
	public void setNodeService(NodeService nodeService)
	{
		this.nodeService = nodeService;
	}
	
	/**
	 * Set the search service
	 * 
	 * @param searchService	search service
	 */
	public void setSearchService(SearchService searchService)
	{
		this.searchService = searchService;
	}
	
	/**
	 * Set the search store, must be a valid store reference string
	 * 
	 * @param searchStore	search store
	 */
	public void setSearchStore(String searchStore) 
	{
		this.searchStore = searchStore;
	}
	
	/**
	 * 
	 * @param contextParserService
	 */
	public void setContextParserService(ContextParserService contextParserService)
    {
	    this.contextParserService = contextParserService;
    }
	
	/**
	 * Init method.  Binds model behaviours to policies.
	 */
	public void init()
	{	
		policyComponent.bindClassBehaviour(
				NodeServicePolicies.OnUpdatePropertiesPolicy.QNAME, 
	            TYPE_WEBASSET_COLLECTION,
	            new JavaBehaviour(this, "onUpdateProperties", NotificationFrequency.TRANSACTION_COMMIT));
		policyComponent.bindAssociationBehaviour(
                NodeServicePolicies.OnCreateChildAssociationPolicy.QNAME, 
                TYPE_WEBASSET_COLLECTION, 
                ContentModel.ASSOC_CONTAINS,
                new JavaBehaviour(this, "onCreateChildAssociation", NotificationFrequency.FIRST_EVENT));		
		onCreateAssociation = new JavaBehaviour(this, "onCreateAssociation", NotificationFrequency.FIRST_EVENT);
		policyComponent.bindAssociationBehaviour(
				NodeServicePolicies.OnCreateAssociationPolicy.QNAME, 
				TYPE_WEBASSET_COLLECTION,
				ASSOC_WEBASSETS,
				onCreateAssociation);
	}
	
	/**
	 * @see org.alfresco.repo.node.NodeServicePolicies.OnUpdatePropertiesPolicy#onUpdateProperties(org.alfresco.service.cmr.repository.NodeRef, java.util.Map, java.util.Map)
	 */
	@Override
	public void onUpdateProperties(NodeRef nodeRef, Map<QName, Serializable> before, Map<QName, Serializable> after) 
	{
		String queryBefore = makeNull((String)before.get(PROP_QUERY));
		String queryAfter = makeNull((String)after.get(PROP_QUERY));
		
		if ((queryBefore == null && queryAfter != null) ||
			(queryBefore != null && queryAfter != null && queryBefore.equals(queryAfter) == false))
		{
			// Refresh the collection
			refreshCollection(nodeRef);
			
			// Set the dynamic flag
			nodeService.setProperty(nodeRef, PROP_IS_DYNAMIC, true);
		}
		else if (queryBefore != null && queryAfter == null)
		{
			// Clear the contents of the collection as we are resetting the query
			clearCollection(nodeRef);
			
			// Set the dynamic flag
			nodeService.setProperty(nodeRef, PROP_IS_DYNAMIC, false);
		}
	}
	
	/**
	 * Helper method to convert empty strings into null values
	 * @param value		value
	 * @return String	passed value, or null if value empty
	 */
	private String makeNull(String value)
	{
		String result = value;
		if (value != null && value.trim().length() == 0)
		{
			result = null;
		}
		return result;
	}
	
	/**
	 * Clear collection
	 * 
	 * @param collection	collection node reference
	 */
	private void clearCollection(NodeRef collection)
	{
		List<AssociationRef> assocs = nodeService.getTargetAssocs(collection, ASSOC_WEBASSETS);
		for (AssociationRef assoc : assocs) 
		{
			nodeService.removeAssociation(collection, assoc.getTargetRef(), ASSOC_WEBASSETS);
		}		
	}
	
	/**
	 * Refresh collection, clears all current members of the collection.
	 * 
	 * @param collection	collection node reference
	 */
	private void refreshCollection(NodeRef collection)
	{
		onCreateAssociation.disable();
		try
		{
			// Get the query language and max query size
			String queryLanguage = (String)nodeService.getProperty(collection, PROP_QUERY_LANGUAGE);
			int maxQuerySize = ((Integer)nodeService.getProperty(collection, PROP_QUERY_RESULTS_MAX_SIZE)).intValue();
			String query = (String)nodeService.getProperty(collection, PROP_QUERY);
			
			if (query != null && query.trim().length() != 0)
			{			
				// Clear the contents of the content collection
				clearCollection(collection);			
				
				// Parse the query string
				query = contextParserService.parse(collection, query);
				
				// Build the query parameters
				SearchParameters searchParameters = new SearchParameters();
				searchParameters.addStore(new StoreRef(searchStore));
				searchParameters.setLanguage(queryLanguage);
				searchParameters.setMaxItems(maxQuerySize);
				searchParameters.setQuery(query);
				
				try
				{				
					// Execute the query
					ResultSet resultSet = searchService.query(searchParameters);
			
					// Iterate over the results of the query
					for (NodeRef result : resultSet.getNodeRefs()) 
					{
						// Only ass associations to webassets
						if (nodeService.hasAspect(result, ASPECT_WEBASSET) == true)
						{
							nodeService.createAssociation(collection, result, ASSOC_WEBASSETS);
						}
					}
				}
				catch (AlfrescoRuntimeException e)
				{
					// Rethrow
					throw new AlfrescoRuntimeException("Invalid collection query.  Please check query for syntax errors.", e);
				}
			}
		}
		finally
		{
			onCreateAssociation.enable();
		}
	}

//	/**
//	 * Parse the collection query
//	 * @param collection
//	 * @param query
//	 * @return
//	 */
//	private String parseQueryString(NodeRef collection, String query)
//	{
//		
//		String result = query;	
//		
//		// Compile the regex. 
//		// Create the 'target' string we wish to interrogate. 
//		// Get a Matcher based on the target string. 
//		Matcher matcher = queryPattern.matcher(query); 
//
//		// Find all the matches. 
//		while (matcher.find()) 
//		{ 
//			String queryParserName = matcher.group(1);
//			QueryParser qp = queryParsers.get(queryParserName.trim());
//			if (qp != null)
//			{
//				String value = qp.execute(collection);
//				result = result.replace(matcher.group(), value);
//			}
//		}	
//		
//		return result;
//	}
	
	/**
	 * @see org.alfresco.repo.node.NodeServicePolicies.OnCreateChildAssociationPolicy#onCreateChildAssociation(org.alfresco.service.cmr.repository.ChildAssociationRef, boolean)
	 */
	@Override
	public void onCreateChildAssociation(ChildAssociationRef childAssocRef, boolean isNewNode) 
	{
		// No contains associations can be created within a collection
		throw new AlfrescoRuntimeException("Content or folders can not be created or added within a resource collection.");
	}

	/**
	 * @see org.alfresco.repo.node.NodeServicePolicies.OnCreateAssociationPolicy#onCreateAssociation(org.alfresco.service.cmr.repository.AssociationRef)
	 */
	@Override
	public void onCreateAssociation(AssociationRef nodeAssocRef) 
	{
		// Check that all items being added to the collection are assets
		if (nodeService.hasAspect(nodeAssocRef.getTargetRef(), ASPECT_WEBASSET) == false)
		{
			throw new AlfrescoRuntimeException("Can not add resource to a collection unless it is an asset.");
		}
	}
}
