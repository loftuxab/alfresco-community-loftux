/**
 * 
 */
package org.alfresco.module.org_alfresco_module_wcmquickstart.util;

import java.util.Calendar;
import java.util.List;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.module.org_alfresco_module_wcmquickstart.model.WebSiteModel;
import org.alfresco.module.org_alfresco_module_wcmquickstart.util.contextparser.ContextParserService;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.search.ResultSet;
import org.alfresco.service.cmr.search.SearchParameters;
import org.alfresco.service.cmr.search.SearchService;

/**
 * @author Roy Wetherall
 */
public class WebassetCollectionHelper implements WebSiteModel
{
    /** Node service */
    private NodeService nodeService;

    /** Search service */
    private SearchService searchService;

    /** Context parser service */
    private ContextParserService contextParserService;

    /** Search store */
    private String searchStore = "workspace://SpacesStore";

    /**
     * Set the node service
     * 
     * @param nodeService
     *            node service
     */
    public void setNodeService(NodeService nodeService)
    {
        this.nodeService = nodeService;
    }

    /**
     * Set the search service
     * 
     * @param searchService
     *            search service
     */
    public void setSearchService(SearchService searchService)
    {
        this.searchService = searchService;
    }

    /**
     * Set the search store, must be a valid store reference string
     * 
     * @param searchStore
     *            search store
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
     * Clear collection
     * 
     * @param collection
     *            collection node reference
     */
    public void clearCollection(NodeRef collection)
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
     * @param collection
     *            collection node reference
     */
    public void refreshCollection(NodeRef collection)
    {
        // Get the query language and max query size
        String queryLanguage = (String) nodeService.getProperty(collection, PROP_QUERY_LANGUAGE);
        String query = (String) nodeService.getProperty(collection, PROP_QUERY);
        Integer minsToRefresh = ((Integer) nodeService.getProperty(collection, PROP_MINS_TO_QUERY_REFRESH));
        minsToRefresh = minsToRefresh == null ? 30 : minsToRefresh;
        Integer maxQuerySize = ((Integer) nodeService.getProperty(collection, PROP_QUERY_RESULTS_MAX_SIZE));
        maxQuerySize = maxQuerySize == null ? 5 : maxQuerySize;

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
                int resultCount = 0;
                for (NodeRef result : resultSet.getNodeRefs())
                {
                    if (maxQuerySize < 1 || resultCount < maxQuerySize)
                    {
                        // Only ass associations to webassets
                        if (nodeService.hasAspect(result, ASPECT_WEBASSET) == true)
                        {
                            nodeService.createAssociation(collection, result, ASSOC_WEBASSETS);
                        }
                        resultCount++;
                    }
                    else
                    {
                        break;
                    }
                }

                // Set the refreshAt property
                Calendar now = Calendar.getInstance();
                now.add(Calendar.MINUTE, minsToRefresh);
                nodeService.setProperty(collection, PROP_REFRESH_AT, now.getTime());

            }
            catch (AlfrescoRuntimeException e)
            {
                // Rethrow
                throw new AlfrescoRuntimeException("Invalid collection query.  Please check query for syntax errors.",
                        e);
            }
        }
    }

}
