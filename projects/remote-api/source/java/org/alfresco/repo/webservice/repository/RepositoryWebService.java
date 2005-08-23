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
import org.alfresco.repo.webservice.types.CML;
import org.alfresco.repo.webservice.types.NodeDefinition;
import org.alfresco.repo.webservice.types.Predicate;
import org.alfresco.repo.webservice.types.Query;
import org.alfresco.repo.webservice.types.QueryLanguageEnum;
import org.alfresco.repo.webservice.types.Reference;
import org.alfresco.repo.webservice.types.Store;
import org.alfresco.repo.webservice.types.StoreEnum;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.search.ResultSet;
import org.alfresco.service.cmr.search.SearchService;
import org.apache.axis.MessageContext;
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
   private QuerySessionCache querySessionCache;
   
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
    * Sets the instance of the QuerySessionCache to be used
    * 
    * @param querySessionCache The QuerySessionCache
    */
   public void setQuerySessionCache(QuerySessionCache querySessionCache)
   {
      this.querySessionCache = querySessionCache;
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
         throw new RepositoryFault(110, "Only '" + QueryLanguageEnum.lucene.getValue() + "' queries are currently supported!");
      }
      
      try
      {
         // handle the special search string of * meaning, get everything
         String statement = query.getStatement();
         if (statement.equals("*"))
         {
            statement = " ISNODE:*";
         }
         
         // perform the requested search
         ResultSet searchResults = this.searchService.query(Utils.convertToStoreRef(store), langEnum.getValue(), statement, null, null);
         
         // setup a query session and get the first batch of results
         ResultSetQuerySession querySession = new ResultSetQuerySession(MessageContext.getCurrentContext(), this.nodeService,
               searchResults, includeMetaData);
         QueryResult queryResult = querySession.getNextResultsBatch();; 
         
         // add the session to the cache if there are more results to come
         if (querySession.hasMoreResults())
         {
            this.querySessionCache.putQuerySession(querySession);
         }
         else
         {
            // remove the query session id so the client doesn't request non-existent results
            queryResult.setQuerySession(null);
         }
         
         return queryResult;
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
    * @see org.alfresco.repo.webservice.repository.RepositoryServiceSoapPort#queryChildren(org.alfresco.repo.webservice.types.Reference)
    */
   public QueryResult queryChildren(Reference node) throws RemoteException, RepositoryFault
   {
      try
      {
         // create the node ref and get the children from the repository
         NodeRef nodeRef = Utils.convertToNodeRef(node);
         List<ChildAssociationRef> kids = this.nodeService.getChildAssocs(nodeRef);
         
         // setup a query session and get the first batch of results
         ChildrenQuerySession querySession = new ChildrenQuerySession(MessageContext.getCurrentContext(), this.nodeService,
               kids, false);
         QueryResult queryResult = querySession.getNextResultsBatch();; 
         
         // add the session to the cache if there are more results to come
         if (querySession.hasMoreResults())
         {
            this.querySessionCache.putQuerySession(querySession);
         }
         else
         {
            // remove the query session id so the client doesn't request non-existent results
            queryResult.setQuerySession(null);
         }
         
         return queryResult;
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
    * @see org.alfresco.repo.webservice.repository.RepositoryServiceSoapPort#queryParents(org.alfresco.repo.webservice.types.Reference)
    */
   public QueryResult queryParents(Reference node) throws RemoteException, RepositoryFault
   {
      try
      {
         // create the node ref and get the children from the repository
         NodeRef nodeRef = Utils.convertToNodeRef(node);
         List<ChildAssociationRef> parents = this.nodeService.getParentAssocs(nodeRef);
         
         // setup a query session and get the first batch of results
         ParentsQuerySession querySession = new ParentsQuerySession(MessageContext.getCurrentContext(), this.nodeService,
               parents, false);
         QueryResult queryResult = querySession.getNextResultsBatch();; 
         
         // add the session to the cache if there are more results to come
         if (querySession.hasMoreResults())
         {
            this.querySessionCache.putQuerySession(querySession);
         }
         else
         {
            // remove the query session id so the client doesn't request non-existent results
            queryResult.setQuerySession(null);
         }
         
         return queryResult;
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
    * @see org.alfresco.repo.webservice.repository.RepositoryServiceSoapPort#queryAssociated(org.alfresco.repo.webservice.types.Reference, org.alfresco.repo.webservice.repository.Association[])
    */
   public QueryResult queryAssociated(Reference node, Association[] association) throws RemoteException, RepositoryFault
   {
      throw new RepositoryFault(1, "queryAssociated() is not implemented yet!");
   }
   
   /**
    * @see org.alfresco.repo.webservice.repository.RepositoryServiceSoapPort#fetchMore(java.lang.String)
    */
   public QueryResult fetchMore(String querySession) throws RemoteException, RepositoryFault
   {
      // get the QuerySession from the cache
      QuerySession session = this.querySessionCache.getQuerySession(querySession);
      
      if (session == null)
      {
         throw new RepositoryFault(4, "querySession with id '" + querySession + "' is invalid");
      }
      
      if (session.hasMoreResults() == false)
      {
         throw new RepositoryFault(5, "querySession with id '" + querySession + "' does not have any more results to fetch!");
      }
      
      try
      {
         // get the next batch of results
         QueryResult queryResult = session.getNextResultsBatch(); 
         
         if (session.hasMoreResults() == false)
         {
            // remove the query session id so the client doesn't request non-existent results
            queryResult.setQuerySession(null);
            this.querySessionCache.removeQuerySession(querySession);
         }
         
         return queryResult;
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
    * @see org.alfresco.repo.webservice.repository.RepositoryServiceSoapPort#update(org.alfresco.repo.webservice.types.CML)
    */
   public UpdateResult update(CML statements) throws RemoteException, RepositoryFault
   {
      throw new RepositoryFault(1, "update() is not implemented yet!");
   }

   /**
    * @see org.alfresco.repo.webservice.repository.RepositoryServiceSoapPort#describe(org.alfresco.repo.webservice.types.Predicate)
    */
   public NodeDefinition[] describe(Predicate items) throws RemoteException, RepositoryFault
   {
      throw new RepositoryFault(1, "describe() is not implemented yet!");
   }
}
