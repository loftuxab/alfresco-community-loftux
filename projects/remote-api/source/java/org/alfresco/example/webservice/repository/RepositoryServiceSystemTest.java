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
package org.alfresco.example.webservice.repository;

import javax.xml.rpc.ServiceException;

import junit.framework.AssertionFailedError;

import org.alfresco.example.webservice.BaseWebServiceSystemTest;
import org.alfresco.example.webservice.types.NamedValue;
import org.alfresco.example.webservice.types.Query;
import org.alfresco.example.webservice.types.QueryLanguageEnum;
import org.alfresco.example.webservice.types.Reference;
import org.alfresco.example.webservice.types.ResultSet;
import org.alfresco.example.webservice.types.ResultSetRow;
import org.alfresco.example.webservice.types.ResultSetRowNode;
import org.alfresco.example.webservice.types.Store;
import org.alfresco.example.webservice.types.StoreEnum;
import org.alfresco.service.cmr.repository.StoreRef;
import org.apache.axis.EngineConfiguration;
import org.apache.axis.configuration.FileProvider;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class RepositoryServiceSystemTest extends BaseWebServiceSystemTest
{
   private static Log logger = LogFactory.getLog(RepositoryServiceSystemTest.class);
   private static Store STORE = new Store(StoreEnum.workspace, "SpacesStore");
   private static StoreRef STORE_REF = new StoreRef(StoreRef.PROTOCOL_WORKSPACE, "SpacesStore");
   
   private RepositoryServiceSoapBindingStub repSvc;

   @Override
   protected void setUp() throws Exception
   {
      super.setUp();

      try 
      {
         EngineConfiguration config = new FileProvider(getResourcesDir(), "client-deploy.wsdd");
         this.repSvc = (RepositoryServiceSoapBindingStub)new RepositoryServiceLocator(config).getRepositoryService();
      }
      catch (ServiceException jre) 
      {
         if (jre.getLinkedCause() != null)
         {
            jre.getLinkedCause().printStackTrace();
         }
         
         throw new AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
      }
      
      assertNotNull("authSvc is null", this.repSvc);
      
      // Time out after a minute
      this.repSvc.setTimeout(60000);
   }

   /**
    * Tests the getStores method
    * 
    * @throws Exception
    */
   public void testGetStores() throws Exception
   {
      Store[] stores = this.repSvc.getStores();
      assertNotNull("stores array should not be null", stores);
      logger.info("store1 = " + stores[0].getScheme() + ":" + stores[0].getAddress());
      logger.info("store2 = " + stores[1].getScheme() + ":" + stores[1].getAddress());
   }
   
   /**
    * Tests the query service call
    * 
    * @throws Exception
    */
   public void testQuery() throws Exception
   {
      //Query query = new Query(QueryLanguageEnum.lucene, "*");
      //Query query = new Query(QueryLanguageEnum.lucene, "PATH:\"/.\"");
      //Query query = new Query(QueryLanguageEnum.lucene, "ISROOT:T");
      Query query = new Query(QueryLanguageEnum.lucene, "( +@\\{http\\://www.alfresco.org/1.0\\}name:test*) OR  TEXT:test*");
      
      //QueryConfiguration queryCfg = new QueryConfiguration();
      //queryCfg.setFetchSize(new PositiveInteger("50"));
      
      // add the query configuration header to the call
      //this.repSvc.setHeader(new RepositoryServiceLocator().getServiceName().getNamespaceURI(), "QueryHeader", queryCfg);
      
      QueryResult queryResult = this.repSvc.query(STORE, query, false);
      assertNotNull("queryResult should not be null", queryResult);
      
      ResultSet resultSet = queryResult.getResultSet();
      assertNotNull("The result set should not be null", resultSet);
      logger.info("There are " + resultSet.getSize() + " rows:");
      
      if (resultSet.getSize() > 0)
      {
         ResultSetRow[] rows = resultSet.getRows();
         for (int x = 0; x < rows.length; x++)
         {
            ResultSetRow row = rows[x];
            NamedValue[] columns = row.getColumns();
            for (int y = 0; y < columns.length; y++)
            {
               logger.info("row " + x + ": " + row.getColumns(y).getName() + " = " + row.getColumns(y).getValue());
            }
         }
      }
      else
      {
         logger.info("The query returned no results");
      }
   }
   
   /**
    * Tests the queryChildren service method
    * 
    * @throws Exception
    */
   public void testQueryChildren() throws Exception
   {
      // get the id of the root node so we can build a query
      //NodeService nodeService = (NodeService)this.applicationContext.getBean("nodeService");
      //String rootNodeId = nodeService.getRootNode(STORE_REF).getId();
      
      Reference node = new Reference();
      node.setStore(STORE);
      node.setUuid("7329c9d2-0d89-11da-90fb-fbe4cb2183e7");     // find a query to retrieve this maybe type == store_root?
      QueryResult queryResult = this.repSvc.queryChildren(node);
      
      assertNotNull("queryResult should not be null", queryResult);
      ResultSet resultSet = queryResult.getResultSet();
      assertNotNull("The result set should not be null", resultSet);
      logger.info("There are " + resultSet.getSize() + " rows:");
      
      if (resultSet.getSize() > 0)
      {
         ResultSetRow[] rows = resultSet.getRows();
         for (int x = 0; x < rows.length; x++)
         {
            ResultSetRow row = rows[x];
            ResultSetRowNode rowNode = row.getNode();
            logger.info("id = " + rowNode.getId() + ", type = " + rowNode.getType());
         }
      }
      else
      {
         logger.info("The query returned no results");
      }
   }
   
   /**
    * Tests the queryParents service method
    * 
    * @throws Exception
    */
   public void testQueryParents() throws Exception
   {
      // query for all the child nodes of the root
      Reference node = new Reference();
      node.setStore(STORE);
      String rootId = "7329c9d2-0d89-11da-90fb-fbe4cb2183e7";
      node.setUuid(rootId);     // find a query to retrieve this maybe type == store_root?
      QueryResult rootChildren = this.repSvc.queryChildren(node);
      
      assertNotNull("rootChildren should not be null", rootChildren);
      ResultSet rootChildrenResults = rootChildren.getResultSet();
      assertNotNull("rootChildrenResults should not be null", rootChildrenResults);
      assertTrue("There should be at least one child of the root node", rootChildrenResults.getSize() > 0);
      
      // get hold of the id of the first child
      ResultSetRow firstRow = rootChildrenResults.getRows(0);
      String id = firstRow.getNode().getId();
      logger.info("Retrieving parents for id: " + id + "....");
      
      node = new Reference();
      node.setStore(STORE);
      node.setUuid(id);
      QueryResult parents = this.repSvc.queryParents(node);
      
      assertNotNull("parents should not be null", parents);
      ResultSet parentsResults = parents.getResultSet();
      assertNotNull("parentsResults should not be null", parentsResults);
      assertTrue("There should be at least one parent", parentsResults.getSize() > 0);
      
      // show the results
      boolean rootFound = false;
      ResultSetRow[] rows = parentsResults.getRows();
      for (int x = 0; x < rows.length; x++)
      {
         ResultSetRow row = rows[x];
         ResultSetRowNode rowNode = row.getNode();
         String nodeId = rowNode.getId();
         logger.info("id = " + nodeId + ", type = " + rowNode.getType());
         
         if (nodeId.equals(rootId))
         {
            rootFound = true;
         }
      }
      
      // make sure the root node was one of the parents
      assertTrue("The root node was not found as one of the parents!!", rootFound);
   }
   
   /*
    * Tests the queryAssociated service method
    * 
    * @throws Exception
    */
   public void testQueryAssociated() throws Exception
   {
      try
      {
         this.repSvc.queryAssociated(null, null);
         fail("This method should have thrown a repository fault");
      }
      catch (RepositoryFault rf)
      {
         // expected to get this
      }
   }
}
