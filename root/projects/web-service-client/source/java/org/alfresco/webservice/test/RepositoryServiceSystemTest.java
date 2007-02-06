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
package org.alfresco.webservice.test;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;

import org.alfresco.webservice.content.Content;
import org.alfresco.webservice.repository.Association;
import org.alfresco.webservice.repository.QueryResult;
import org.alfresco.webservice.repository.RepositoryServiceLocator;
import org.alfresco.webservice.repository.UpdateResult;
import org.alfresco.webservice.types.CML;
import org.alfresco.webservice.types.CMLAddAspect;
import org.alfresco.webservice.types.CMLCopy;
import org.alfresco.webservice.types.CMLCreate;
import org.alfresco.webservice.types.CMLDelete;
import org.alfresco.webservice.types.CMLUpdate;
import org.alfresco.webservice.types.CMLWriteContent;
import org.alfresco.webservice.types.ClassDefinition;
import org.alfresco.webservice.types.ContentFormat;
import org.alfresco.webservice.types.NamedValue;
import org.alfresco.webservice.types.Node;
import org.alfresco.webservice.types.NodeDefinition;
import org.alfresco.webservice.types.ParentReference;
import org.alfresco.webservice.types.Predicate;
import org.alfresco.webservice.types.PropertyDefinition;
import org.alfresco.webservice.types.Query;
import org.alfresco.webservice.types.QueryConfiguration;
import org.alfresco.webservice.types.Reference;
import org.alfresco.webservice.types.ResultSet;
import org.alfresco.webservice.types.ResultSetRow;
import org.alfresco.webservice.types.ResultSetRowNode;
import org.alfresco.webservice.types.Store;
import org.alfresco.webservice.util.Constants;
import org.alfresco.webservice.util.ContentUtils;
import org.alfresco.webservice.util.ISO9075;
import org.alfresco.webservice.util.Utils;
import org.alfresco.webservice.util.WebServiceFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class RepositoryServiceSystemTest extends BaseWebServiceSystemTest
{
    private static Log logger = LogFactory
            .getLog(RepositoryServiceSystemTest.class);

    @Override
    protected void setUp() throws Exception
    {
        super.setUp();
    }

    /**
     * Tests the getStores method
     * 
     * @throws Exception
     */
    public void testGetStores() throws Exception
    {
        Store[] stores = WebServiceFactory.getRepositoryService().getStores();
        assertNotNull("Stores array should not be null", stores);
        assertTrue("There should be at least 1 store", stores.length >= 1);
    }

    /**
     * Tests the query service call
     * 
     * @throws Exception
     */
    public void testQuery() throws Exception
    {
        //Query query = new Query(QueryLanguageEnum.lucene, "*");
        Query query = new Query(Constants.QUERY_LANG_LUCENE,
                "( +@\\{http\\://www.alfresco.org/1.0\\}name:test*) OR  TEXT:test*");

        QueryResult queryResult = WebServiceFactory.getRepositoryService().query(BaseWebServiceSystemTest.store, query,
                false);
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
                    logger.debug("row " + x + ": "
                            + row.getColumns(y).getName() + " = "
                            + row.getColumns(y).getValue());
                }
            }
        } else
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
        Query query = new Query(Constants.QUERY_LANG_LUCENE, "*");

        // add the query configuration header to the call
        int batchSize = 5;
        QueryConfiguration queryCfg = new QueryConfiguration();
        queryCfg.setFetchSize(batchSize);
        WebServiceFactory.getRepositoryService().setHeader(new RepositoryServiceLocator()
                .getServiceName().getNamespaceURI(), "QueryHeader", queryCfg);

        // get the first set of results back
        QueryResult queryResult = WebServiceFactory.getRepositoryService().query(BaseWebServiceSystemTest.store, query,
                false);
        assertNotNull("queryResult should not be null", queryResult);
        String querySession = queryResult.getQuerySession();
        String origQuerySession = querySession;
        assertNotNull("querySession should not be null", querySession);

        ResultSet resultSet = queryResult.getResultSet();
        assertNotNull("The result set should not be null", resultSet);
        logger.debug("There are " + resultSet.getTotalRowCount()
                + " rows in total");
        logger.debug("There are " + resultSet.getRows().length
                + " rows in the first set");
        assertEquals("The result set size should be " + batchSize, batchSize,
                resultSet.getRows().length);

        // get the next batch of results
        queryResult = WebServiceFactory.getRepositoryService().fetchMore(querySession);
        assertNotNull("queryResult should not be null", queryResult);
        querySession = queryResult.getQuerySession();
        assertNotNull("querySession should not be null", querySession);

        ResultSet resultSet2 = queryResult.getResultSet();
        assertNotNull("The second result set should not be null", resultSet2);
        logger.debug("There are " + resultSet2.getRows().length
                + " rows in the second set");
        assertEquals("The result set size should be " + batchSize, batchSize,
                resultSet2.getRows().length);

        // get the rest of the results to make sure it finishes properly
        while (querySession != null)
        {
            queryResult = WebServiceFactory.getRepositoryService().fetchMore(querySession);
            assertNotNull("queryResult returned in loop should not be null",
                    queryResult);
            querySession = queryResult.getQuerySession();
            logger.debug("There were another "
                    + queryResult.getResultSet().getRows().length
                    + " rows returned");
        }

        // try and fetch some more results and we should get an error
        try
        {
            queryResult = WebServiceFactory.getRepositoryService().fetchMore(origQuerySession);
            fail("We should have seen an error as all the results have been returned");
        } catch (Exception e)
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
        Reference node = BaseWebServiceSystemTest.rootReference;
        String rootId = node.getUuid();

        QueryResult rootChildren = WebServiceFactory.getRepositoryService().queryChildren(node);

        assertNotNull("rootChildren should not be null", rootChildren);
        ResultSet rootChildrenResults = rootChildren.getResultSet();
        assertNotNull("rootChildrenResults should not be null",
                rootChildrenResults);
        assertTrue("There should be at least one child of the root node",
                rootChildrenResults.getRows().length > 0);

        // get hold of the id of the first child
        ResultSetRow firstRow = rootChildrenResults.getRows(0);
        assertNotNull("getColumns() should not return null", firstRow
                .getColumns());
        String id = firstRow.getNode().getId();
        logger.debug("Retrieving parents for first node found: " + id + "....");

        node = new Reference();
        node.setStore(BaseWebServiceSystemTest.store);
        node.setUuid(id);
        QueryResult parents = WebServiceFactory.getRepositoryService().queryParents(node);

        assertNotNull("parents should not be null", parents);
        ResultSet parentsResults = parents.getResultSet();
        assertNotNull("parentsResults should not be null", parentsResults);
        assertTrue("There should be at least one parent", parentsResults
                .getRows().length > 0);

        // show the results
        boolean rootFound = false;
        ResultSetRow[] rows = parentsResults.getRows();
        logger.debug("There are " + rows.length + " rows:");
        for (int x = 0; x < rows.length; x++)
        {
            ResultSetRow row = rows[x];
            assertNotNull("getColumns() should not return null", row
                    .getColumns());
            ResultSetRowNode rowNode = row.getNode();
            String nodeId = rowNode.getId();
            logger.debug("parent node = " + nodeId + ", type = "
                    + rowNode.getType());
            NamedValue[] columns = row.getColumns();
            for (int y = 0; y < columns.length; y++)
            {
                logger.debug("row " + x + ": "
                        + row.getColumns(y).getName() + " = "
                        + row.getColumns(y).getValue());
            }

            if (nodeId.equals(rootId) == true)
            {
                rootFound = true;
            }
        }

        // make sure the root node was one of the parents
        assertTrue("The root node was not found as one of the parents!!",
                rootFound);
    }

    /**
     * Tests the queryChildren service method
     * 
     * @throws Exception
     */
    public void testQueryChildren() throws Exception
    {
        // query for all the child nodes of the root
        Reference node = BaseWebServiceSystemTest.rootReference;
        QueryResult rootChildren = WebServiceFactory.getRepositoryService().queryChildren(node);

        assertNotNull("rootChildren should not be null", rootChildren);
        ResultSet rootChildrenResults = rootChildren.getResultSet();
        assertNotNull("rootChildrenResults should not be null",
                rootChildrenResults);
        assertTrue("There should be at least one child of the root node",
                rootChildrenResults.getRows().length > 0);

        // show the results
        ResultSetRow[] rows = rootChildrenResults.getRows();
        logger.debug("There are " + rows.length + " rows:");
        for (int x = 0; x < rows.length; x++)
        {
            ResultSetRow row = rows[x];
            assertNotNull("getColumns() should not return null", row
                    .getColumns());
            ResultSetRowNode rowNode = row.getNode();
            String nodeId = rowNode.getId();
            logger.debug("child node = " + nodeId + ", type = "
                    + rowNode.getType());

            NamedValue[] columns = row.getColumns();
            for (int y = 0; y < columns.length; y++)
            {
                logger.debug("row " + x + ": "
                        + row.getColumns(y).getName() + " = "
                        + row.getColumns(y).getValue());
            }            
        }
    }
            
    /*
     * Tests the queryAssociated service method
     * 
     * @throws Exception
     */
    public void testQueryAssociated() throws Exception
    {
        Association association = new Association(Constants.createQNameString(
                Constants.NAMESPACE_CONTENT_MODEL, "translations"),
                "target");
        QueryResult result = WebServiceFactory.getRepositoryService().queryAssociated(BaseWebServiceSystemTest.contentReference, association);
        assertNotNull(result);
        assertNotNull(result.getResultSet());
        assertNotNull(result.getResultSet().getRows());
        assertEquals(1, result.getResultSet().getRows().length);

        logger.debug("There is 1 result row:");

        ResultSetRow row = result.getResultSet().getRows()[0];
        NamedValue[] columns = row.getColumns();
        for (int y = 0; y < columns.length; y++)
        {
          logger.debug("row 0" + ": "
              + row.getColumns(y).getName() + " = "
              + row.getColumns(y).getValue());
        }     
    }

    /**
     * Tests the describe service method
     * 
     * @throws Exception
     */
    public void testDescribe() throws Exception
    {
        // get hold of a node we know some info about so we can test the
        // returned values (the Alfresco Tutorial PDF)
        Query query = new Query(Constants.QUERY_LANG_LUCENE,
                "( +@\\{http\\://www.alfresco.org/1.0\\}name:test*) OR  TEXT:test*");
        QueryResult queryResult = this.repositoryService.query(BaseWebServiceSystemTest.store, query,
                false);
        assertNotNull("queryResult should not be null", queryResult);
        ResultSet resultSet = queryResult.getResultSet();
        assertNotNull("The result set should not be null", resultSet);
        assertTrue("There should be at least one result", resultSet
                .getTotalRowCount() > 0);
        String id = resultSet.getRows(0).getNode().getId();
        assertNotNull("Id of Alfresco Tutorial PDF should not be null", id);

        // create a predicate object to to send to describe method
        Reference ref = new Reference();
        ref.setStore(BaseWebServiceSystemTest.store);
        ref.setUuid(id);
        Predicate predicate = new Predicate(new Reference[] { ref }, null, null);

        // make the service call
        NodeDefinition[] nodeDefs = WebServiceFactory.getRepositoryService().describe(predicate);
        assertNotNull("nodeDefs should not be null", nodeDefs);
        assertTrue("There should only be one result", nodeDefs.length == 1);

        // get the result
        NodeDefinition nodeDef = nodeDefs[0];
        assertNotNull("The nodeDef should not be null", nodeDef);
        ClassDefinition typeDef = nodeDef.getType();
        assertNotNull("Type definition should not be null", typeDef);

        assertEquals("Type name is incorrect",
                "{http://www.alfresco.org/model/content/1.0}content", typeDef
                        .getName());
        assertEquals("Superclass type name is incorrect",
                "{http://www.alfresco.org/model/content/1.0}cmobject", typeDef
                        .getSuperClass());
        assertEquals("Type title is incorrect", "Content", typeDef.getTitle());
        assertEquals("Type description is incorrect", "Base Content Object", typeDef
                .getDescription());
        assertFalse("Type is an aspect and it shouldn't be", typeDef
                .isIsAspect());
        assertNull("There should not be any associations", typeDef
                .getAssociations());
        assertNotNull("Properties should not be null", typeDef.getProperties());
        assertEquals("There should be 2 properties", 2,
                typeDef.getProperties().length);

        // check the name and type of each of the properties
        assertEquals("Property1 name is incorrect",
                "{http://www.alfresco.org/model/content/1.0}content", typeDef
                        .getProperties(0).getName());
        assertEquals("Property1 type name is incorrect",
                "{http://www.alfresco.org/model/dictionary/1.0}content", typeDef
                        .getProperties(0).getDataType());

        assertEquals("Property5 name is incorrect",
                "{http://www.alfresco.org/model/content/1.0}name", typeDef
                        .getProperties(1).getName());
        assertEquals("Property5 type name is incorrect",
                "{http://www.alfresco.org/model/dictionary/1.0}text", typeDef
                        .getProperties(1).getDataType());

        // check the aspects
        ClassDefinition[] aspects = nodeDef.getAspects();
        assertNotNull("aspects should not be null", aspects);
        assertEquals("There should be 3 aspects", 3, aspects.length);

        // check the first aspect
        ClassDefinition aspect1 = aspects[1];
        assertEquals("Aspect1 name is incorrect",
                "{http://www.alfresco.org/model/system/1.0}referenceable",
                aspect1.getName());
        assertTrue("Aspect1 should be an aspect", aspect1.isIsAspect());
        assertNotNull("Aspect1 should have properties", aspect1.getProperties());
        assertEquals("Aspect1 has wrong number of properties", 4, aspect1
                .getProperties().length);

        // check the second aspect
        ClassDefinition aspect2 = aspects[0];
        assertEquals("Aspect2 name is incorrect",
                "{http://www.alfresco.org/model/content/1.0}auditable", aspect2
                        .getName());
        assertTrue("Aspect2 should be an aspect", aspect2.isIsAspect());
        assertNotNull("Aspect2 should have properties", aspect2.getProperties());
        assertEquals("Aspect2 has wrong number of properties", 5, aspect2
                .getProperties().length);
    }

    /**
     * Tests passing a query in the predicate to return items to describe
     * 
     * @throws Exception
     */
    public void testPredicateQuery() throws Exception
    {
        // define a query to add to the predicate (get everything that mentions
        // 'test')
        Query query = new Query(Constants.QUERY_LANG_LUCENE,
                "( +@\\{http\\://www.alfresco.org/1.0\\}name:test*) OR  TEXT:test*");

        Predicate predicate = new Predicate();
        predicate.setQuery(query);
        predicate.setStore(BaseWebServiceSystemTest.store);

        // call the service and make sure we get some details back
        NodeDefinition[] nodeDefs = WebServiceFactory.getRepositoryService().describe(predicate);
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
                logger.debug("name = " + propDef.getName() + " type = "
                        + propDef.getDataType());
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
        // setup a predicate to find the test folder using an xpath
        Reference ref = new Reference();
        ref.setStore(BaseWebServiceSystemTest.store);
        ref.setPath("//*[@cm:name = '" + FOLDER_NAME + "']");
        Predicate predicate = new Predicate();
        predicate.setNodes(new Reference[] { ref });

        // call the service and make sure we get some details back
        NodeDefinition[] nodeDefs = WebServiceFactory.getRepositoryService().describe(predicate);
        assertNotNull("nodeDefs should not be null", nodeDefs);
        assertTrue("There should be at least one result", nodeDefs.length > 0);

        NodeDefinition nodeDef = nodeDefs[0];
        assertNotNull("The nodeDef should not be null", nodeDef);
        ClassDefinition typeDef = nodeDef.getType();
        assertNotNull("Type definition should not be null", typeDef);

        logger.debug("type name = " + typeDef.getName());
        assertEquals("Type is incorrect",
                "{http://www.alfresco.org/model/content/1.0}folder", typeDef
                        .getName());
        logger.debug("is aspect = " + typeDef.isIsAspect());
        assertFalse("Item should not be an aspect", typeDef.isIsAspect());
        PropertyDefinition[] propDefs = typeDef.getProperties();
        if (propDefs != null)
        {
            logger.debug("There are " + propDefs.length + " properties:");
            for (int x = 0; x < propDefs.length; x++)
            {
                PropertyDefinition propDef = propDefs[x];
                logger.debug("name = " + propDef.getName() + " type = "
                        + propDef.getDataType());
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
        CMLCreate create = new CMLCreate();
        create.setId("id1");
        create.setType(Constants.TYPE_CONTENT);

        ParentReference parentReference = new ParentReference();
        parentReference.setAssociationType(Constants.ASSOC_CHILDREN);
        parentReference.setChildName(Constants.ASSOC_CHILDREN);
        parentReference.setStore(BaseWebServiceSystemTest.store);
        parentReference.setUuid(BaseWebServiceSystemTest.rootReference.getUuid());

        create.setParent(parentReference);
        create.setProperty(new NamedValue[] {
                        new NamedValue(
                                Constants.PROP_NAME,
                                false,
                                "name.txt",
                                null)});
        
        // Create a folder used for later tests
        CMLCreate createFolder = new CMLCreate();
        createFolder.setId("folder1");
        createFolder.setType(Constants.TYPE_FOLDER);
        createFolder.setParent(parentReference);
        createFolder.setProperty(new NamedValue[] {
                new NamedValue(
                        Constants.PROP_NAME,
                        false,
                        "tempFolder",
                        null)});
        
        CMLAddAspect aspect = new CMLAddAspect();
        aspect.setAspect(Constants.ASPECT_VERSIONABLE);
        aspect.setWhere_id("id1");

        ContentFormat format = new ContentFormat(Constants.MIMETYPE_TEXT_PLAIN, "UTF-8");
        
        CMLWriteContent write = new CMLWriteContent();
        write.setProperty(Constants.PROP_CONTENT);
        write.setContent("this is a test".getBytes());
        write.setFormat(format);
        write.setWhere_id("id1");
        
        CML cml = new CML();
        cml.setCreate(new CMLCreate[]{create, createFolder});
        cml.setAddAspect(new CMLAddAspect[]{aspect});
        cml.setWriteContent(new CMLWriteContent[]{write});
        
        UpdateResult[] results = WebServiceFactory.getRepositoryService().update(cml);
        assertNotNull(results);
        assertEquals(4, results.length);
        
        // Get a reference to the create node
        Reference reference = results[0].getDestination();
        Reference folderReference = results[1].getDestination();
        
        // Check that the content has been set successfully
        Content[] content = WebServiceFactory.getContentService().read(new Predicate(new Reference[]{reference}, null, null), Constants.PROP_CONTENT);
        assertNotNull(content);
        assertEquals(1, content.length);
        assertEquals("this is a test", ContentUtils.getContentAsString(content[0]));

        // Try and copy the reference into the folder
        CMLCopy copy = new CMLCopy();
        copy.setTo(new ParentReference(folderReference.getStore(), folderReference.getUuid(), null, Constants.ASSOC_CONTAINS, "{" + Constants.NAMESPACE_CONTENT_MODEL + "}name.txt"));
        copy.setWhere(new Predicate(new Reference[]{reference}, null, null));
        CML cmlCopy = new CML();
        cmlCopy.setCopy(new CMLCopy[]{copy});
        
        UpdateResult[] results2 = WebServiceFactory.getRepositoryService().update(cmlCopy);
        assertNotNull(results2);
        assertEquals(1, results2.length);        
        Reference newCopy = results2[0].getDestination();
        assertNotNull(newCopy);
        
        // Check that the folder does indeed have the copied reference
        QueryResult result = this.repositoryService.queryChildren(folderReference);
        assertEquals(1, result.getResultSet().getTotalRowCount());
        
        // Test delete
        CMLDelete delete = new CMLDelete();
        delete.setWhere(new Predicate(new Reference[]{newCopy}, null, null));
        CML cmlDelete = new CML();
        cmlDelete.setDelete(new CMLDelete[]{delete});
        
        UpdateResult[] results3 = WebServiceFactory.getRepositoryService().update(cmlDelete);
        assertNotNull(results3);
        assertEquals(1, results3.length);
        UpdateResult updateResult3 = results3[0];
        assertNull(updateResult3.getDestination());
        assertEquals(newCopy.getUuid(), updateResult3.getSource().getUuid());
        
    }
    
    public void testGet() 
        throws Exception
    {
        Predicate predicate = new Predicate(null, BaseWebServiceSystemTest.store, null);
        Node[] nodes = WebServiceFactory.getRepositoryService().get(predicate);
        assertNotNull(nodes);
        assertEquals(1, nodes.length);
        
        Node rootNode = nodes[0];
        assertEquals(BaseWebServiceSystemTest.rootReference.getUuid(), rootNode.getReference().getUuid());
        
        logger.debug("Root node type = " + rootNode.getType());
        String aspects = "";
        for (String aspect : rootNode.getAspects())
        {
            aspects += aspect + ", ";
        }
        logger.debug("Root node aspects = " + aspects);
        for (NamedValue prop : rootNode.getProperties())
        {
            logger.debug("Root node property " + prop.getName() + " = " + prop.getValue());
        }        
    }

    /**
     * Test that the uuid and path are both returned in a Reference object
     * @throws Exception
     */
    public void testGetPath() throws Exception
    {      
      Predicate predicate = new Predicate(new Reference[]{BaseWebServiceSystemTest.folderReference}, null, null);   
      Node[] nodes = WebServiceFactory.getRepositoryService().get(predicate);
      assertNotNull(nodes);
      assertEquals(1, nodes.length);
      Node node = nodes[0];
      String path = node.getReference().getPath();
      String uuid = node.getReference().getUuid();
      
      logger.debug("Folder reference path = " + BaseWebServiceSystemTest.folderReference.getPath());
      logger.debug("Retrieved node path = " + path);
      logger.debug("Retrieved node uuid = " + uuid);
      
      assertNotNull(path);
      assertNotNull(uuid);
      assertEquals(BaseWebServiceSystemTest.folderReference.getPath(), path);
      
    }    
    
    public void testPropertySetGet() throws Exception
    {
        // Load a custom model using the cm:dictionaryModel type
        CMLCreate create = new CMLCreate();
        create.setId("id1");
        create.setType(Constants.createQNameString(Constants.NAMESPACE_CONTENT_MODEL, "dictionaryModel"));

        ParentReference parentReference = new ParentReference(new Store(Constants.WORKSPACE_STORE, "SpacesStore"), null, "/app:company_home", Constants.ASSOC_CONTAINS, Constants.ASSOC_CONTAINS);                    
        
        create.setParent(parentReference);
        create.setProperty(new NamedValue[] {
                        new NamedValue(
                                Constants.PROP_NAME,
                                false,
                                "testModel.xml",
                                null),
                        new NamedValue(
                                Constants.createQNameString(Constants.NAMESPACE_CONTENT_MODEL, "modelActive"),
                                false,
                                "true",
                                null)});
        
        CML cml = new CML();
        cml.setCreate(new CMLCreate[]{create});
        UpdateResult[] results = WebServiceFactory.getRepositoryService().update(cml);
        Reference model = results[0].getDestination();
        
        // Now add the content to the model
        InputStream viewStream = getClass().getClassLoader().getResourceAsStream("org/alfresco/webservice/test/resources/propertymodel.xml");
        byte[] bytes = ContentUtils.convertToByteArray(viewStream);
        this.contentService.write(model, Constants.PROP_CONTENT, bytes, new ContentFormat(Constants.MIMETYPE_XML, "UTF-8"));
        
        try
        {
            // Now create a node of the type specified in the model
            ParentReference parentReference2 = new ParentReference();
            parentReference2.setAssociationType(Constants.ASSOC_CHILDREN);
            parentReference2.setChildName(Constants.ASSOC_CHILDREN);
            parentReference2.setStore(BaseWebServiceSystemTest.store);
            parentReference2.setUuid(BaseWebServiceSystemTest.rootReference.getUuid());
            create = new CMLCreate();
            create.setId("id1");
            create.setType(Constants.createQNameString("http://www.alfresco.org/model/webservicetestmodel/1.0", "testproperties"));
            create.setParent(parentReference2);
            cml = new CML();
            cml.setCreate(new CMLCreate[]{create});
            UpdateResult[] results2 = WebServiceFactory.getRepositoryService().update(cml);
            Reference reference = results2[0].getDestination();
            
            Collection<String> list = new ArrayList<String>();
            list.add("Filrst sadf d");
            list.add("Seconf sdfasdf");
            System.out.println(list.toString());
            
            
            System.out.println(new String[] {"firstValue", "secondValue", "thirdValue"}.toString());
            
            // Now we can try and set all the various different types of properties
            NamedValue[] properties = new NamedValue[]{
                    Utils.createNamedValue(Constants.createQNameString("http://www.alfresco.org/model/webservicetestmodel/1.0", "textProp"), "some text"),
                    Utils.createNamedValue(Constants.createQNameString("http://www.alfresco.org/model/webservicetestmodel/1.0", "intProp"), "12"),
                    Utils.createNamedValue(Constants.createQNameString("http://www.alfresco.org/model/webservicetestmodel/1.0", "longProp"), "1234567890"),
                    Utils.createNamedValue(Constants.createQNameString("http://www.alfresco.org/model/webservicetestmodel/1.0", "floatProp"), "12.345"),
                    Utils.createNamedValue(Constants.createQNameString("http://www.alfresco.org/model/webservicetestmodel/1.0", "doubleProp"), "12.345"),
                    Utils.createNamedValue(Constants.createQNameString("http://www.alfresco.org/model/webservicetestmodel/1.0", "dateProp"), "2005-09-16T00:00:00.000+00:00"),
                    Utils.createNamedValue(Constants.createQNameString("http://www.alfresco.org/model/webservicetestmodel/1.0", "datetimeProp"), "2005-09-16T17:01:03.456+01:00"),
                    Utils.createNamedValue(Constants.createQNameString("http://www.alfresco.org/model/webservicetestmodel/1.0", "booleanProp"), "false"),
                    Utils.createNamedValue(Constants.createQNameString("http://www.alfresco.org/model/webservicetestmodel/1.0", "qnameProp"), "{http://www.alfresco.org/model/webservicetestmodel/1.0}testProperties"),
                    Utils.createNamedValue(Constants.createQNameString("http://www.alfresco.org/model/webservicetestmodel/1.0", "noderefProp"), "workspace://SpacesStore/123123123"),
                    Utils.createNamedValue(Constants.createQNameString("http://www.alfresco.org/model/webservicetestmodel/1.0", "textMultiProp"), new String[] {"firstValue", "secondValue", "thirdValue"}),                    
            };
            CMLUpdate cmlUpdate = new CMLUpdate(properties, new Predicate(new Reference[]{reference}, null, null), null);
            cml = new CML();
            cml.setUpdate(new CMLUpdate[]{cmlUpdate});
            WebServiceFactory.getRepositoryService().update(cml);
            
            // Output all the set property values for visual inspection
            Node[] nodes = WebServiceFactory.getRepositoryService().get(new Predicate(new Reference[]{reference}, null, null));
            Node node = nodes[0];            
            for(NamedValue namedValue : node.getProperties())
            {
                if (namedValue.getIsMultiValue() == null || namedValue.getIsMultiValue() == false)
                {
                    System.out.println(namedValue.getName() + " = " + namedValue.getValue());
                }
                else
                {
                    System.out.print(namedValue.getName() + " = ");
                    for (String value : namedValue.getValues())
                    {
                        System.out.print(value + " ");
                    }
                    System.out.println("");
                }
            }            
        }
        finally
        {
            // Need to delete the model from the spaces store to tidy things up
            Predicate where = new Predicate(new Reference[]{model}, null, null);
            CMLDelete cmlDelete = new CMLDelete(where);
            cml = new CML();
            cml.setDelete(new CMLDelete[]{cmlDelete});
            WebServiceFactory.getRepositoryService().update(cml);
        }
    }
    
    public void testFolderCreate()
        throws Exception
    {
        Reference newFolder = createFolder(BaseWebServiceSystemTest.rootReference, "123TestFolder");
        assertNotNull(newFolder);
        Reference newFolder2 = createFolder(BaseWebServiceSystemTest.rootReference, "2007");
        assertNotNull(newFolder2);
    }
    
    public void testPathLookup()
        throws Exception
    {
        Reference newFolder = createFolder(BaseWebServiceSystemTest.rootReference, "A Test Folder");
        queryForFolder(newFolder.getPath(), newFolder);
        queryForFolder("/cm:" + ISO9075.encode("A Test Folder"), newFolder);
    }
    
    private Reference createFolder(Reference parent, String folderName)
        throws Exception
    {
        ParentReference parentReference = new ParentReference();
        parentReference.setAssociationType(Constants.ASSOC_CHILDREN);
        parentReference.setChildName(Constants.createQNameString(Constants.NAMESPACE_CONTENT_MODEL, folderName));
        parentReference.setStore(parent.getStore());
        parentReference.setUuid(parent.getUuid());
        
        NamedValue[] properties = new NamedValue[]{Utils.createNamedValue(Constants.PROP_NAME, folderName)};
        CMLCreate create = new CMLCreate("1", parentReference, null, null, null, Constants.TYPE_FOLDER, properties);
        CML cml = new CML();
        cml.setCreate(new CMLCreate[]{create});
        
        UpdateResult[] results = WebServiceFactory.getRepositoryService().update(cml);
        
        return results[0].getDestination();
    }
    
    private void queryForFolder(String pathName, Reference expected) 
        throws Exception
    {
        Reference folderRef = new Reference(BaseWebServiceSystemTest.store, null, pathName);
        Node[] nodes = repositoryService.get(new Predicate(new Reference[]{folderRef}, null, null));
        if( nodes == null || nodes.length < 1 ) 
        {
            fail("No such folder found.");
        } 
        else if( nodes.length > 1) 
        {
            fail("Found more than one reference--should only be one.");
        } 
        else 
        {
            Reference ref = nodes[0].getReference();
            assertNotNull(ref);
            assertEquals(expected.getUuid(), ref.getUuid());
            assertEquals(expected.getPath(), ref.getPath());
        } 
    }
}
