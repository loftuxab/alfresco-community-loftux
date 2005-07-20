/*
 * Copyright (C) 2005 Alfresco, Inc.
 *
 * Licensed under the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version
 * 2.1 of the License, or (at your option) any later version.
 * You may obtain a copy of the License at
 *
 *     http://www.gnu.org/licenses/lgpl.txt
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the
 * License.
 */
package org.alfresco.service.cmr.search;

import org.alfresco.service.cmr.repository.Path;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.namespace.QName;

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
public interface SearchService
{
    public static final String LANGUAGE_LUCENE = "lucene";
    
    /**
     * Search against a store.
     * 
     * @param store -
     *            the store against which to search
     * @param language -
     *            the query language
     * @param query -
     *            the query string - which may include parameters
     * @param attributePaths -
     *            explicit list of attributes/properties to extract for the selected nodes in xpath style syntax
     * @param queryParameterDefinition - query parameter definitions - the default value is used for the value.
     * @return Returns the query results
     */
    public ResultSet query(StoreRef store, String language, String query, Path[] attributePaths,
            QueryParameterDefinition[] queryParameterDefinitions);
    
    /**
     * Search against a store.
     * Pulls back all attributes on each node.
     * Does not allow parameterisation.
     * 
     * @param store -
     *            the store against which to search
     * @param language -
     *            the query language
     * @param query -
     *            the query string - which may include parameters
     * @return Returns the query results
     */
    public ResultSet query(StoreRef store, String language, String query);
    
    /**
     * Search against a store.
     * 
     * @param store -
     *            the store against which to search
     * @param language -
     *            the query language
     * @param query -
     *            the query string - which may include parameters
     * @param queryParameterDefinition - query parameter definitions - the default value is used for the value.
     * @return Returns the query results
     */
    public ResultSet query(StoreRef store, String language, String query, QueryParameterDefinition[] queryParameterDefintions);
    
    
    /**
     * Search against a store.
     * 
     * @param store -
     *            the store against which to search
     * @param language -
     *            the query language
     * @param query -
     *            the query string - which may include parameters
     * @param attributePaths -
     *            explicit list of attributes/properties to extract for the selected nodes in xpath style syntax
     * @return Returns the query results
     */
    public ResultSet query(StoreRef store, String language, String query, Path[] attributePaths);
    
    
    /**
     * Execute a canned query
     * 
     * @param store -
     *       the store against which to search
     * @param queryId - the query identifier
     * @param queryParameters - parameterisation for the canned query
     * @return Returns the query results
     */
    public ResultSet query(StoreRef store, QName queryId,  QueryParameter[] queryParameters);
    
    /**
     * Search using the given SearchParameters
     */
    
    public ResultSet query(SearchParameters searchParameters);
}
