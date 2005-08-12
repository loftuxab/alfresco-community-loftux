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
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.util.BaseTest;
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
   public void xtestGetStores() throws Exception
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
    */
   public void testQueryChildren() throws Exception
   {
      // get the id of the root node so we can build a query
      //NodeService nodeService = (NodeService)this.applicationContext.getBean("nodeService");
      //String rootNodeId = nodeService.getRootNode(STORE_REF).getId();
      
      Reference node = new Reference();
      node.setStore(STORE);
      node.setUuid("c26c4a8d-058f-11da-811f-2fa895fd7caf");     // find a query to retrieve this maybe type == store_root?
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
}
