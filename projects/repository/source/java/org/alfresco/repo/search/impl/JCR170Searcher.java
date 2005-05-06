/*
 * Created on Mar 24, 2005
 * 
 * TODO Comment this class
 * 
 * 
 */
package org.alfresco.repo.search.impl;

import org.alfresco.repo.dictionary.NamespaceService;
import org.alfresco.repo.ref.Path;
import org.alfresco.repo.ref.StoreRef;
import org.alfresco.repo.search.QueryParameter;
import org.alfresco.repo.search.ResultSet;
import org.alfresco.repo.search.Searcher;

/**
 * Simple searcher against another store using the JSR 170 API
 */
public class JCR170Searcher implements Searcher
{

    public ResultSet query(StoreRef store, String language, String query, Path[] queryOptions,
            QueryParameter[] queryParameters)
    {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public void setNameSpaceService(NamespaceService nameSpaceService)
    {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

}
