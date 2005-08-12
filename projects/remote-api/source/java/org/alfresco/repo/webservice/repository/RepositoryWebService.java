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
package org.alfresco.repo.webservice.repository;

import java.rmi.RemoteException;
import java.util.List;

import org.alfresco.repo.webservice.Utils;
import org.alfresco.repo.webservice.types.AssociationDefinition;
import org.alfresco.repo.webservice.types.CML;
import org.alfresco.repo.webservice.types.NamedValue;
import org.alfresco.repo.webservice.types.NodeDefinition;
import org.alfresco.repo.webservice.types.Predicate;
import org.alfresco.repo.webservice.types.Query;
import org.alfresco.repo.webservice.types.QueryLanguageEnum;
import org.alfresco.repo.webservice.types.Reference;
import org.alfresco.repo.webservice.types.ResultSet;
import org.alfresco.repo.webservice.types.ResultSetMetaData;
import org.alfresco.repo.webservice.types.ResultSetRow;
import org.alfresco.repo.webservice.types.Store;
import org.alfresco.repo.webservice.types.StoreEnum;
import org.alfresco.repo.webservice.types.ValueDefinition;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.util.GUID;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Web service implementation of the RepositoryService.
 * The WSDL for this service can be accessed from http://localhost:8080/alfresco/api/RepositoryService?wsdl
 * 
 * @author gavinc
 */
public class RepositoryWebService implements RepositoryServiceSoapPort
{
   private static Log logger = LogFactory.getLog(RepositoryWebService.class);
   
   private NodeService nodeService;
   private SearchService searchService;
   
   /**
    * Sets the instance of the NodeService to be used
    * 
    * @param nodeService The NodeService
    */
   public void setNodeService(NodeService nodeService)
   {
      this.nodeService = nodeService;
   }
   
   /**
    * Sets the instance of the SearchService to be used
    * 
    * @param searchService The SearchService
    */
   public void setSearchService(SearchService searchService)
   {
      this.searchService = searchService;
   }

   /**
    * @see org.alfresco.repo.webservice.repository.RepositoryServiceSoapPort#getStores()
    */
   public Store[] getStores() throws RemoteException, RepositoryFault
   {
      try
      {
         List<StoreRef> stores = this.nodeService.getStores();
         Store[] returnStores = new Store[stores.size()];
         for (int x = 0; x < stores.size(); x++)
         {
            StoreRef storeRef = stores.get(x);
            StoreEnum storeEnum = StoreEnum.fromString(storeRef.getProtocol());
            Store store = new Store(storeEnum, storeRef.getIdentifier());
            returnStores[x] = store;
         }
         
         return returnStores;
      }
      catch (Throwable e)
      {
         if (logger.isDebugEnabled())
         {
            logger.error("Unexpected error occurred", e);
         }
         
         throw new RepositoryFault(0, e.getMessage());
      }
   }

   /**
    * @see org.alfresco.repo.webservice.repository.RepositoryServiceSoapPort#query(org.alfresco.repo.webservice.types.Store, org.alfresco.repo.webservice.types.Query, boolean)
    */
   public QueryResult query(Store store, Query query, boolean includeMetaData) throws RemoteException, RepositoryFault
   {
      QueryLanguageEnum langEnum = query.getLanguage();
      
      if (langEnum.equals(QueryLanguageEnum.cql) || langEnum.equals(QueryLanguageEnum.xpath))
      {
         throw new RepositoryFault(110, "Currently only '" + QueryLanguageEnum.lucene.getValue() + "' queries are supported!");
      }
      
      // TODO: Return metadata if the includeMetaData flag is set
      
      // handle the special search string of * meaning, get everything
      String statement = query.getStatement();
      if (statement.equals("*"))
      {
         statement = " ISNODE:*";
      }
      
      org.alfresco.service.cmr.search.ResultSet results = this.searchService.query(Utils.convertToStoreRef(store), 
            langEnum.getValue(), statement, null, null);

      QueryResult queryResult = new QueryResult();
      
      // TODO: Setup a query session and only return the number of rows specified in the query config header
      
      queryResult.setQuerySession(GUID.generate());
      queryResult.setResultSet(Utils.convertToResultSet(results, includeMetaData, this.nodeService));
      
      return queryResult;
   }

   /**
    * @see org.alfresco.repo.webservice.repository.RepositoryServiceSoapPort#queryChildren(org.alfresco.repo.webservice.types.Reference)
    */
   public QueryResult queryChildren(Reference node) throws RemoteException, RepositoryFault
   {
      // create the node ref and get the children from the repository
      NodeRef nodeRef = new NodeRef(Utils.convertToStoreRef(node.getStore()), node.getUuid());
      List<ChildAssociationRef> kids = this.nodeService.getChildAssocs(nodeRef);
      
      QueryResult queryResult = new QueryResult();
      
      // TODO: Setup a query session and only return the number of rows specified in the query config header
      
      queryResult.setQuerySession(GUID.generate());
      queryResult.setResultSet(Utils.convertToResultSet(kids, this.nodeService));
      
      return queryResult;
   }

   /**
    * @see org.alfresco.repo.webservice.repository.RepositoryServiceSoapPort#queryParents(org.alfresco.repo.webservice.types.Reference)
    */
   public QueryResult queryParents(Reference node) throws RemoteException, RepositoryFault
   {
      return null;
   }
   
   /**
    * @see org.alfresco.repo.webservice.repository.RepositoryServiceSoapPort#queryAssociated(org.alfresco.repo.webservice.types.Reference, org.alfresco.repo.webservice.types.AssociationDefinition[])
    */
   public QueryResult queryAssociated(Reference node, AssociationDefinition[] association) throws RemoteException, RepositoryFault
   {
      return null;
   }

   /**
    * @see org.alfresco.repo.webservice.repository.RepositoryServiceSoapPort#fetchMore(java.lang.String)
    */
   public QueryResult fetchMore(String querySession) throws RemoteException, RepositoryFault
   {
      return null;
   }

   /**
    * @see org.alfresco.repo.webservice.repository.RepositoryServiceSoapPort#update(org.alfresco.repo.webservice.types.CML)
    */
   public UpdateResult update(CML statements) throws RemoteException, RepositoryFault
   {
      return null;
   }

   /**
    * @see org.alfresco.repo.webservice.repository.RepositoryServiceSoapPort#describe(org.alfresco.repo.webservice.types.Predicate)
    */
   public NodeDefinition[] describe(Predicate node) throws RemoteException, RepositoryFault
   {
      return null;
   }
}
