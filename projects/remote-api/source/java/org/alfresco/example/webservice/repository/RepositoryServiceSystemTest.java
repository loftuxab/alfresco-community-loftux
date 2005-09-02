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
package org.alfresco.example.webservice.repository;

import javax.xml.rpc.ServiceException;

import junit.framework.AssertionFailedError;

import org.alfresco.example.webservice.BaseWebServiceSystemTest;
import org.alfresco.example.webservice.types.ClassDefinition;
import org.alfresco.example.webservice.types.NamedValue;
import org.alfresco.example.webservice.types.NodeDefinition;
import org.alfresco.example.webservice.types.Predicate;
import org.alfresco.example.webservice.types.PropertyDefinition;
import org.alfresco.example.webservice.types.Query;
import org.alfresco.example.webservice.types.QueryConfiguration;
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

   private RepositoryServiceSoapBindingStub repoService;

   @Override
   protected void setUp() throws Exception
   {
      super.setUp();

      try 
      {
         EngineConfiguration config = new FileProvider(getResourcesDir(), "client-deploy.wsdd");
         this.repoService = (RepositoryServiceSoapBindingStub)new RepositoryServiceLocator(config).getRepositoryService();
      }
      catch (ServiceException jre) 
      {
         if (jre.getLinkedCause() != null)
         {
            jre.getLinkedCause().printStackTrace();
         }
         
         throw new AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
      }
      
      assertNotNull("repoService is null", this.repoService);
      
      // Time out after a minute
      this.repoService.setTimeout(60000);
   }

   /**
    * Tests the getStores method
    * 
    * @throws Exception
    */
   public void testGetStores() throws Exception
   {
      Store[] stores = this.repoService.getStores();
      assertNotNull("stores array should not be null", stores);
      logger.debug("store1 = " + stores[0].getScheme() + ":" + stores[0].getAddress());
      logger.debug("store2 = " + stores[1].getScheme() + ":" + stores[1].getAddress());
      logger.debug("store3 = " + stores[2].getScheme() + ":" + stores[2].getAddress());
      assertTrue("There should be 3 stores", stores.length == 3);
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
      
      QueryResult queryResult = this.repoService.query(STORE, query, false);
      assertNotNull("queryResult should not be null", queryResult);
      
      ResultSet resultSet = queryResult.getResultSet();
      assertNotNull("The result set should not be null", resultSet);
      logger.debug("There are " + resultSet.getTotalRowCount() + " rows:");
      
      if (resultSet.getTotalRowCount() > 0)
      {
         ResultSetRow[] rows = resultSet.getRows();
         for (int x = 0; x < rows.length; x++)
         {
            ResultSetRow row = rows[x];
            NamedValue[] columns = row.getColumns();
            for (int y = 0; y < columns.length; y++)
            {
               logger.debug("row " + x + ": " + row.getColumns(y).getName() + " = " + row.getColumns(y).getValue());
            }
         }
      }
      else
      {
         logger.debug("The query returned no results");
         fail("The query returned no results");
      }
   }
   
   /**
    * Tests the ability to retrieve the results of a query in batches
    * 
    * @throws Exception
    */
   public void testQuerySession() throws Exception
   {
      // define a query that will return a lot of hits i.e. EVERYTHING
      Query query = new Query(QueryLanguageEnum.lucene, "*");
      
      // add the query configuration header to the call
      int batchSize = 75;
      QueryConfiguration queryCfg = new QueryConfiguration();
      queryCfg.setFetchSize(batchSize);
      this.repoService.setHeader(new RepositoryServiceLocator().getServiceName().getNamespaceURI(), "QueryHeader", queryCfg);
      
      // get the first set of results back
      QueryResult queryResult = this.repoService.query(STORE, query, false);
      assertNotNull("queryResult should not be null", queryResult);
      String querySession = queryResult.getQuerySession();
      String origQuerySession = querySession;
      assertNotNull("querySession should not be null", querySession);
      
      ResultSet resultSet = queryResult.getResultSet();
      assertNotNull("The result set should not be null", resultSet);
      logger.debug("There are " + resultSet.getTotalRowCount() + " rows in total");
      logger.debug("There are " + resultSet.getRows().length + " rows in the first set");
      assertEquals("The result set size should be " + batchSize, batchSize, resultSet.getRows().length);
      
      // get the next batch of results
      queryResult = this.repoService.fetchMore(querySession);
      assertNotNull("queryResult should not be null", queryResult);
      querySession = queryResult.getQuerySession();
      assertNotNull("querySession should not be null", querySession);
      
      ResultSet resultSet2 = queryResult.getResultSet();
      assertNotNull("The second result set should not be null", resultSet2);
      logger.debug("There are " + resultSet2.getRows().length + " rows in the second set");
      assertEquals("The result set size should be " + batchSize, batchSize, resultSet2.getRows().length);
      
      // get the rest of the results to make sure it finishes properly
      while (querySession != null)
      {
         queryResult = this.repoService.fetchMore(querySession);
         assertNotNull("queryResult returned in loop should not be null", queryResult);
         querySession = queryResult.getQuerySession();
         logger.debug("There were another " + queryResult.getResultSet().getRows().length + " rows returned");
      }
      
      // try and fetch some more results and we should get an error
      try
      {
         queryResult = this.repoService.fetchMore(origQuerySession);
         fail("We should have seen an error as all the results have been returned");
      }
      catch (Exception e)
      {
         // expected
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
      node.setUuid(rootId);     // find a query to retrieve this maybe type == store_root?
      logger.debug("Retrieving children for node: " + rootId + "....");
      QueryResult rootChildren = this.repoService.queryChildren(node);
      
      assertNotNull("rootChildren should not be null", rootChildren);
      ResultSet rootChildrenResults = rootChildren.getResultSet();
      assertNotNull("rootChildrenResults should not be null", rootChildrenResults);
      assertTrue("There should be at least one child of the root node", 
            rootChildrenResults.getRows().length > 0);
      
      // get hold of the id of the first child
      ResultSetRow firstRow = rootChildrenResults.getRows(0);
      assertNotNull("getColumns() should not return null", firstRow.getColumns());
      String id = firstRow.getNode().getId();
      logger.debug("Retrieving parents for first node found: " + id + "....");
      
      node = new Reference();
      node.setStore(STORE);
      node.setUuid(id);
      QueryResult parents = this.repoService.queryParents(node);
      
      assertNotNull("parents should not be null", parents);
      ResultSet parentsResults = parents.getResultSet();
      assertNotNull("parentsResults should not be null", parentsResults);
      assertTrue("There should be at least one parent", parentsResults.getRows().length > 0);
      
      // show the results
      boolean rootFound = false;
      ResultSetRow[] rows = parentsResults.getRows();
      for (int x = 0; x < rows.length; x++)
      {
         ResultSetRow row = rows[x];
         assertNotNull("getColumns() should not return null", row.getColumns());
         ResultSetRowNode rowNode = row.getNode();
         String nodeId = rowNode.getId();
         logger.debug("parent node = " + nodeId + ", type = " + rowNode.getType());
         
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
         this.repoService.queryAssociated(null, null);
         fail("This method should have thrown a repository fault");
      }
      catch (RepositoryFault rf)
      {
         // expected to get this
      }
   }
   
   /**
    * Tests the describe service method
    * 
    * @throws Exception
    */
   public void testDescribe() throws Exception
   {
      // get hold of a node we know some info about so we can test the returned values (the Alfresco Tutorial PDF)
      Query query = new Query(QueryLanguageEnum.lucene, "( +@\\{http\\://www.alfresco.org/1.0\\}name:test*) OR  TEXT:test*");
      QueryResult queryResult = this.repoService.query(STORE, query, false);
      assertNotNull("queryResult should not be null", queryResult);
      ResultSet resultSet = queryResult.getResultSet();
      assertNotNull("The result set should not be null", resultSet);
      assertTrue("There should be at least one result", resultSet.getTotalRowCount() > 0);
      String id = resultSet.getRows(0).getNode().getId();
      assertNotNull("Id of Alfresco Tutorial PDF should not be null", id);
      
      // create a predicate object to to send to describe method
      Reference ref = new Reference();
      ref.setStore(STORE);
      ref.setUuid(id);
      Predicate predicate = new Predicate(new Reference[] {ref}, null, null);
      
      // make the service call
      NodeDefinition[] nodeDefs = this.repoService.describe(predicate);
      assertNotNull("nodeDefs should not be null", nodeDefs);
      assertTrue("There should only be one result", nodeDefs.length == 1);
      
      // get the result
      NodeDefinition nodeDef = nodeDefs[0];
      assertNotNull("The nodeDef should not be null", nodeDef);
      ClassDefinition typeDef = nodeDef.getType();
      assertNotNull("Type definition should not be null", typeDef);
      
      assertEquals("Type name is incorrect", "{http://www.alfresco.org/model/content/1.0}content", typeDef.getName());
      assertEquals("Superclass type name is incorrect", "{http://www.alfresco.org/model/content/1.0}cmobject", typeDef.getSuperClass());
      assertEquals("Type title is incorrect", "Content", typeDef.getTitle());
      assertEquals("Type description is incorrect", null, typeDef.getDescription());
      assertFalse("Type is an aspect and it shouldn't be", typeDef.isIsAspect());
      assertNull("There should not be any associations", typeDef.getAssociations());
      assertNotNull("Properties should not be null", typeDef.getProperties());
      assertEquals("There should be 5 properties", 5, typeDef.getProperties().length);
      
      // check the name and type of each of the properties
      assertEquals("Property1 name is incorrect", "{http://www.alfresco.org/model/content/1.0}encoding", 
            typeDef.getProperties(0).getName());
      assertEquals("Property1 type name is incorrect", "{http://www.alfresco.org/model/dictionary/1.0}text", 
            typeDef.getProperties(0).getDataType());
      
      assertEquals("Property2 name is incorrect", "{http://www.alfresco.org/model/content/1.0}size", 
            typeDef.getProperties(1).getName());
      assertEquals("Property2 type name is incorrect", "{http://www.alfresco.org/model/dictionary/1.0}long", 
            typeDef.getProperties(1).getDataType());
      
      assertEquals("Property3 name is incorrect", "{http://www.alfresco.org/model/content/1.0}contentUrl", 
            typeDef.getProperties(2).getName());
      assertEquals("Property3 type name is incorrect", "{http://www.alfresco.org/model/dictionary/1.0}text", 
            typeDef.getProperties(2).getDataType());
      
      assertEquals("Property4 name is incorrect", "{http://www.alfresco.org/model/content/1.0}name", 
            typeDef.getProperties(3).getName());
      assertEquals("Property4 type name is incorrect", "{http://www.alfresco.org/model/dictionary/1.0}text", 
            typeDef.getProperties(3).getDataType());
      
      assertEquals("Property5 name is incorrect", "{http://www.alfresco.org/model/content/1.0}mimetype", 
            typeDef.getProperties(4).getName());
      assertEquals("Property5 type name is incorrect", "{http://www.alfresco.org/model/dictionary/1.0}text", 
            typeDef.getProperties(4).getDataType());
      
      // check the aspects
      ClassDefinition[] aspects = nodeDef.getAspects();
      assertNotNull("aspects should not be null", aspects);
      assertEquals("There should be 2 aspects", 2, aspects.length);
      
      // check the first aspect
      ClassDefinition aspect1 = aspects[0];
      assertEquals("Aspect1 name is incorrect", "{http://www.alfresco.org/model/system/1.0}referencable", aspect1.getName());
      assertTrue("Aspect1 should be an aspect", aspect1.isIsAspect());
      assertNotNull("Aspect1 should have properties", aspect1.getProperties());
      assertEquals("Aspect1 has wrong number of properties", 3, aspect1.getProperties().length);
      
      // check the second aspect
      ClassDefinition aspect2 = aspects[1];
      assertEquals("Aspect2 name is incorrect", "{http://www.alfresco.org/model/content/1.0}auditable", aspect2.getName());
      assertTrue("Aspect2 should be an aspect", aspect2.isIsAspect());
      assertNotNull("Aspect2 should have properties", aspect2.getProperties());
      assertEquals("Aspect2 has wrong number of properties", 5, aspect2.getProperties().length);
   }
   
   /**
    * Tests passing a query in the predicate to return items to describe
    * 
    * @throws Exception
    */
   public void testPredicateQuery() throws Exception
   {
      // define a query to add to the predicate (get everything that mentions 'test')
      Query query = new Query(QueryLanguageEnum.lucene, "( +@\\{http\\://www.alfresco.org/1.0\\}name:test*) OR  TEXT:test*");
      
      Predicate predicate = new Predicate();
      predicate.setQuery(query);
      predicate.setStore(STORE);
      
      // call the service and make sure we get some details back
      NodeDefinition[] nodeDefs = this.repoService.describe(predicate);
      assertNotNull("nodeDefs should not be null", nodeDefs);
      assertTrue("There should be at least one result", nodeDefs.length > 0);
      
      NodeDefinition nodeDef = nodeDefs[0];
      assertNotNull("The nodeDef should not be null", nodeDef);
      ClassDefinition typeDef = nodeDef.getType();
      assertNotNull("Type definition should not be null", typeDef);
      
      logger.debug("type name = " + typeDef.getName());
      logger.debug("is aspect = " + typeDef.isIsAspect());
      PropertyDefinition[] propDefs = typeDef.getProperties();
      if (propDefs != null)
      {
         logger.debug("There are " + propDefs.length + " properties:");
         for (int x = 0; x < propDefs.length; x++)
         {
            PropertyDefinition propDef = propDefs[x];
            logger.debug("name = " + propDef.getName() + " type = " + propDef.getDataType());
         }
      }
   }
   
   /**
    * Tests the use of a path within a reference
    * 
    * @throws Exception
    */
   public void testPathReference() throws Exception
   {
      // setup a predicate to find the 'Company Home' folder using an xpath
      Reference ref = new Reference();
      ref.setStore(STORE);
      ref.setPath("//*[@cm:name = 'Company Home']");
      Predicate predicate = new Predicate();
      predicate.setNodes(new Reference[] {ref});

      // call the service and make sure we get some details back
      NodeDefinition[] nodeDefs = this.repoService.describe(predicate);
      assertNotNull("nodeDefs should not be null", nodeDefs);
      assertTrue("There should be at least one result", nodeDefs.length > 0);
      
      NodeDefinition nodeDef = nodeDefs[0];
      assertNotNull("The nodeDef should not be null", nodeDef);
      ClassDefinition typeDef = nodeDef.getType();
      assertNotNull("Type definition should not be null", typeDef);
      
      logger.debug("type name = " + typeDef.getName());
      assertEquals("Type is incorrect", "{http://www.alfresco.org/model/content/1.0}folder", typeDef.getName());
      logger.debug("is aspect = " + typeDef.isIsAspect());
      assertFalse("Item should not be an aspect", typeDef.isIsAspect());
      PropertyDefinition[] propDefs = typeDef.getProperties();
      if (propDefs != null)
      {
         logger.debug("There are " + propDefs.length + " properties:");
         for (int x = 0; x < propDefs.length; x++)
         {
            PropertyDefinition propDef = propDefs[x];
            logger.debug("name = " + propDef.getName() + " type = " + propDef.getDataType());
         }
      }
   }
   
   /**
    * Tests the update service method
    * 
    * @throws Exception
    */
   public void testUpdate() throws Exception
   {
      try
      {
         this.repoService.update(null);
         fail("This method should have thrown a repository fault");
      }
      catch (RepositoryFault rf)
      {
         // expected to get this
      }
   }
}
