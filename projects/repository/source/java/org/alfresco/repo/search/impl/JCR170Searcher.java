/*
 * Created on Mar 24, 2005
 * 
 * TODO Comment this class
 * 
 * 
 */
package org.alfresco.repo.search.impl;

import org.alfresco.repo.ref.Path;
import org.alfresco.repo.ref.QName;
import org.alfresco.repo.ref.StoreRef;
import org.alfresco.repo.search.AbstractSearcherComponent;
import org.alfresco.repo.search.QueryParameter;
import org.alfresco.repo.search.QueryParameterDefinition;
import org.alfresco.repo.search.ResultSet;

/**
 * Simple searcher against another store using the JSR 170 API
 */
public class JCR170Searcher extends AbstractSearcherComponent
{

    public ResultSet query(StoreRef store, String language, String query, Path[] queryOptions,
            QueryParameter[] queryParameters)
    {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public ResultSet query(StoreRef store, String language, String query, Path[] attributePaths, QueryParameterDefinition[] queryParameterDefinitions)
    {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public ResultSet query(StoreRef store, QName queryId, QueryParameter[] queryParameters)
    {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    
}
