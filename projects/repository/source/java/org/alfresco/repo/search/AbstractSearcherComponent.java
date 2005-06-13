/*
 * Created on 24-May-2005
 *
 * TODO Comment this class
 * 
 * 
 */
package org.alfresco.repo.search;

import org.alfresco.service.cmr.repository.Path;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.search.QueryParameterDefinition;
import org.alfresco.service.cmr.search.ResultSet;
import org.alfresco.service.cmr.search.SearchService;

public abstract class AbstractSearcherComponent implements SearchService
{

    public ResultSet query(StoreRef store, String language, String query)
    {
        return query(store, language, query, null, null);
    }

    public ResultSet query(StoreRef store, String language, String query, QueryParameterDefinition[] queryParameterDefintions)
    {
        return query(store, language, query, null, queryParameterDefintions);
    }

    public ResultSet query(StoreRef store, String language, String query, Path[] attributePaths)
    {
        return query(store, language, query, attributePaths, null);
    }

}
