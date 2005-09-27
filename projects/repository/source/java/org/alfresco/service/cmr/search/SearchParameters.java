/*
 * Copyright (C) 2005 Alfresco, Inc.
 *
 * Licensed under the Mozilla Public License version 1.1 
 * with a permitted attribution clause. You may obtain a
 * copy of the License at
 *
 *   http://www.alfresco.org/legal/license.txt
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the
 * License.
 */
package org.alfresco.service.cmr.search;

import java.util.ArrayList;

import org.alfresco.service.cmr.repository.Path;
import org.alfresco.service.cmr.repository.StoreRef;

/**
 * This class provides parameters to define a lucene search
 * 
 * 
 * @author Andy Hind
 */
public class SearchParameters extends SearchStatement
{
    private ArrayList<StoreRef> stores = new ArrayList<StoreRef>(1);
    private ArrayList<Path> attributePaths = new ArrayList<Path>(1);
    private ArrayList<QueryParameterDefinition> queryParameterDefinitions = new ArrayList<QueryParameterDefinition>(1);
    private boolean excludeDataInTheCurrentTransaction = false;
    private ArrayList<SortDefinition> sortDefinitions = new ArrayList<SortDefinition>(1);
    
    public SearchParameters()
    {
        super();
    }

    /**
     * Set the stores to be supported - currently there can be only one 
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
