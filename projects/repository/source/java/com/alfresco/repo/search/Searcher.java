/*
 * Created on Mar 24, 2005
 * 
 * TODO Comment this class
 * 
 * 
 */
package org.alfresco.repo.search;

import org.alfresco.repo.dictionary.NamespaceService;
import org.alfresco.repo.ref.Path;
import org.alfresco.repo.ref.StoreRef;

/**
 * Ths encapsultes the execution of search against different indexing
 * mechanisms.
 * 
 * Canned queries have been translated into the query string by this stage.
 * Handling of parameterisation is left to the implementation.
 * 
 * @author andyh
 * 
 */
public interface Searcher
{
    /**
     * Search against a store.
     * 
     * @param store -
     *            the store against which to search
     * @param language -
     *            the query language
     * @param query -
     *            the query string
     * @param queryOptions -
     *            explicit list of properties to extract for the selected nodes.
     * @param queryParameter -
     *            string name, string value pairs with an optional type hint
     * @return
     */
    public ResultSet query(StoreRef store, String language, String query, Path[] queryOptions,
            QueryParameter[] queryParameters);

    public void setNameSpaceService(NamespaceService nameSpaceService);
}
