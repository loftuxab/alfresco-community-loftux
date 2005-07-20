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
 *
 * Created on 13-Jul-2005
 */
package org.alfresco.service.cmr.search;

import java.util.ArrayList;

import org.alfresco.service.cmr.repository.Path;
import org.alfresco.service.cmr.repository.StoreRef;

/**
 * This class provides parameters to define a lucene search
 * 
 * 
 * @author andyh
 */
public class SearchParameters
{
    private ArrayList<StoreRef> stores = new ArrayList<StoreRef>(1);
    private String language;
    private String query;
    private ArrayList<Path> attributePaths = new ArrayList<Path>(1);
    private ArrayList<QueryParameterDefinition> queryParameterDefinitions = new ArrayList<QueryParameterDefinition>(1);
    private boolean excludeDataInTheCurrentTransaction = true;
    private ArrayList<SortDefinition> sortDefinitions = new ArrayList<SortDefinition>(1);
    
    public SearchParameters()
    {
        super();
    }

    /**
     * Set the stores to be suported - currently there can be only one 
     * 
     * @param store
     */
    public void addStore(StoreRef store)
    {
        if(stores.size() != 0)
        {
            throw new IllegalStateException("At the moment, there can only be one store set for the search");
        }
        stores.add(store);
    }
    
    /**
     * Set the query language and query string
     * 
     * @param language
     * @param query
     */
    public void setQuery(String language, String query)
    {
        this.language = language;
        this.query = query;
    }
    
    /**
     * Add paths for attributes in the result set
     * 
     * @param attributePath
     */
    public void addAttrbutePath(Path attributePath)    
    {
        attributePaths.add(attributePath);
    }
    
    /**
     * Add parameter defintions for te query - used to parameteris the query string
     * 
     * @param queryParameterDefinition
     */
    public void addQueryParameterDefinition(QueryParameterDefinition queryParameterDefinition)
    {
        queryParameterDefinitions.add(queryParameterDefinition);
    }
    
    /**
     * If true, any data in the current transaction will be ignored in the search. 
     * You will not see anything you have added in the current transaction.
     * 
     * @param excludeDataInTheCurrentTransaction
     */
    public void excludeDataInTheCurrentTransaction(boolean excludeDataInTheCurrentTransaction)
    {
        if(!excludeDataInTheCurrentTransaction)
        {
            throw new IllegalStateException("This option is not currently supported ");
        }
        this.excludeDataInTheCurrentTransaction = excludeDataInTheCurrentTransaction;
    }
    
    /**
     * Add a sort to the query (for those query languages that do not support it directly)
     * 
     * @param field - this is intially a direct attribute on a node not an attribute on the parent etc
     * TODO: It could be a relative path at some time. 
     * 
     * 
     * @param ascending
     */
    public void addSort(String field, boolean ascending)
    {
        sortDefinitions.add(new SortDefinition(field, ascending));
    }
    
    
    
    /**
     * A helper class for sort definition
     * @author andyh
     *
     * TODO To change the template for this generated type comment go to
     * Window - Preferences - Java - Code Style - Code Templates
     */
    public static class SortDefinition
    {
        String field;
        boolean ascending;
        
        SortDefinition(String field, boolean ascending)
        {
            this.field = field;
            this.ascending = ascending;
        }

        public boolean isAscending()
        {
            return ascending;
        }

        public String getField()
        {
            return field;
        }
        
        
    }



    public ArrayList<Path> getAttributePaths()
    {
        return attributePaths;
    }

    public boolean excludeDataInTheCurrentTransaction()
    {
        return excludeDataInTheCurrentTransaction;
    }

    public String getLanguage()
    {
        return language;
    }

    public String getQuery()
    {
        return query;
    }

    public ArrayList<QueryParameterDefinition> getQueryParameterDefinitions()
    {
        return queryParameterDefinitions;
    }

    public ArrayList<SortDefinition> getSortDefinitions()
    {
        return sortDefinitions;
    }

    public ArrayList<StoreRef> getStores()
    {
        return stores;
    }
}
