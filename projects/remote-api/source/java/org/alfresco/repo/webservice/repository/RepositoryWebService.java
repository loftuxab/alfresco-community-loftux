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

import javax.transaction.UserTransaction;

import org.alfresco.repo.cache.SimpleCache;
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
   private SimpleCache<String, QuerySession> querySessionCache; 
   
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
    * Sets the instance of the SimpleCache to be used
    * 
    * @param querySessionCache The SimpleCache
    */
   public void setQuerySessionCache(SimpleCache<String, QuerySession> querySessionCache)
   {
      this.querySessionCache = querySessionCache;
   }

   /**
    * @see org.alfresco.repo.webservice.repository.RepositoryServiceSoapPort#getStores()
    */
   public Store[] getStores() throws RemoteException, RepositoryFault
   {
      UserTransaction tx = null;
      
      try
      {
         tx = Utils.getUserTransaction(MessageContext.getCurrentContext());
         tx.begin();

         List<StoreRef> stores = this.nodeService.getStores();
         Store[] returnStores = new Store[stores.size()];
         for (int x = 0; x < stores.size(); x++)
         {
            StoreRef storeRef = stores.get(x);
            StoreEnum storeEnum = StoreEnum.fromString(storeRef.getProtocol());
            Store store = new Store(storeEnum, storeRef.getIdentifier());
            returnStores[x] = store;
         }
         
         // commit the transaction
         tx.commit();
         
         return returnStores;
      }
      catch (Throwable e)
      {
         // rollback the transaction
         try { if (tx != null) {tx.rollback();} } catch (Exception ex) {}
         
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
      
      UserTransaction tx = null;
      MessageContext msgContext = MessageContext.getCurrentContext();
      
      try
      {
         tx = Utils.getUserTransaction(msgContext);
         tx.begin();
         
         // setup a query session and get the first batch of results
         QuerySession querySession = new ResultSetQuerySession(Utils.getBatchSize(msgContext), store, query, includeMetaData);
         QueryResult queryResult = querySession.getNextResultsBatch(this.searchService, this.nodeService);
         
         // add the session to the cache if there are more results to come
         if (queryResult.getQuerySession() != null)
         {
            //this.querySessionCache.putQuerySession(querySession);
            this.querySessionCache.put(queryResult.getQuerySession(), querySession);
         }
         
         // commit the transaction
         tx.commit();
         
         return queryResult;
      }
      catch (Throwable e)
      {
         // rollback the transaction
         try { if (tx != null) {tx.rollback();} } catch (Exception ex) {}
         
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
      UserTransaction tx = null;
      
      try
      {
         tx = Utils.getUserTransaction(MessageContext.getCurrentContext());
         tx.begin();
         
         // setup a query session and get the first batch of results
         QuerySession querySession = new ChildrenQuerySession(Utils.getBatchSize(MessageContext.getCurrentContext()), node);
         QueryResult queryResult = querySession.getNextResultsBatch(this.searchService, this.nodeService);
         
         // add the session to the cache if there are more results to come
         if (queryResult.getQuerySession() != null)
         {
            //this.querySessionCache.putQuerySession(querySession);
            this.querySessionCache.put(queryResult.getQuerySession(), querySession);
         }
         
         // commit the transaction
         tx.commit();
         
         return queryResult;
      }
      catch (Throwable e)
      {
         // rollback the transaction
         try { if (tx != null) {tx.rollback();} } catch (Exception ex) {}
         
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
      UserTransaction tx = null;
      
      try
      {
         tx = Utils.getUserTransaction(MessageContext.getCurrentContext());
         tx.begin();
         
         // setup a query session and get the first batch of results
         QuerySession querySession = new ParentsQuerySession(Utils.getBatchSize(MessageContext.getCurrentContext()), node);
         QueryResult queryResult = querySession.getNextResultsBatch(this.searchService, this.nodeService); 
         
         // add the session to the cache if there are more results to come
         if (queryResult.getQuerySession() != null)
         {
            //this.querySessionCache.putQuerySession(querySession);
            this.querySessionCache.put(queryResult.getQuerySession(), querySession);
         }
         
         // commit the transaction
         tx.commit();
         
         return queryResult;
      }
      catch (Throwable e)
      {
         // rollback the transaction
         try { if (tx != null) {tx.rollback();} } catch (Exception ex) {}
         
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
      QueryResult queryResult = null;
      
      UserTransaction tx = null;
      
      try
      {
         tx = Utils.getUserTransaction(MessageContext.getCurrentContext());
         tx.begin();
         
         // try and get the QuerySession with the given id from the cache
         QuerySession session = this.querySessionCache.get(querySession);
         
         if (session == null)
         {
            if (logger.isDebugEnabled())
               logger.debug("Invalid querySession id requested: " + querySession);
            
            throw new RepositoryFault(4, "querySession with id '" + querySession + "' is invalid");
         }
      
         // get the next batch of results
         queryResult = session.getNextResultsBatch(this.searchService, this.nodeService); 
         
         // remove the QuerySession from the cache if there are no more results to come
         if (queryResult.getQuerySession() == null)
         {
            this.querySessionCache.remove(querySession);
         }
         
         // commit the transaction
         tx.commit();
      }
      catch (Throwable e)
      {
         // rollback the transaction
         try { if (tx != null) {tx.rollback();} } catch (Exception ex) {}
         
         if (e instanceof RepositoryFault)
         {
            throw (RepositoryFault)e;
         }
         else
         {
            if (logger.isDebugEnabled())
            {
               logger.error("Unexpected error occurred", e);
            }
            
            throw new RepositoryFault(0, e.getMessage());
         }
      }
      
      return queryResult;
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
