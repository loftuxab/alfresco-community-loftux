/*
 * Created on Mar 31, 2005
 * 
 * TODO Comment this class
 * 
 * 
 */
package com.activiti.repo.search.impl.lucene;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import junit.framework.TestCase;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.activiti.repo.domain.Node;
import com.activiti.repo.node.AssociationExistsException;
import com.activiti.repo.node.InvalidNodeRefException;
import com.activiti.repo.node.NodeService;
import com.activiti.repo.ref.ChildRelationshipRef;
import com.activiti.repo.ref.NodeRef;
import com.activiti.repo.ref.Path;
import com.activiti.repo.ref.QName;
import com.activiti.repo.ref.StoreRef;
import com.activiti.repo.search.IndexerAndSearcher;
import com.activiti.repo.search.ResultSet;
import com.activiti.repo.search.ResultSetRow;
import com.activiti.repo.search.Searcher;
import com.activiti.repo.search.Value;
import com.activiti.repo.store.StoreService;
import com.activiti.util.GUID;

public class LuceneTest extends TestCase
{

   public LuceneTest()
   {
      super();

   }

   public LuceneTest(String arg0)
   {
      super(arg0);
   }

   /**
    * Test basic index and search
    *
    */

   public void testStandAloneIndexerCommit()
   {

      StoreRef storeRef = new StoreRef(StoreRef.PROTOCOL_WORKSPACE, "ws");
      NodeRef rootNode = new NodeRef(storeRef, "1");
      NodeRef newNode = new NodeRef(storeRef, "2");
      

      LuceneIndexer indexer = LuceneIndexer.getUpdateIndexer(storeRef, "delta"+System.currentTimeMillis());
      indexer.setNodeService(new NodeServiceStub());

      indexer.createNode(new ChildRelationshipRef(rootNode, "path", newNode));
      indexer.updateNode(newNode);
      //indexer.deleteNode(new ChildRelationshipRef(rootNode, "path", newNode));

      indexer.commit();
      
      
      Searcher searcher = LuceneSearcher.getSearcher(storeRef);
      
      ResultSet results = searcher.query(storeRef, "lucene", "@\\{\\}property-1:value-1", null, null );
      assertEquals(2, results.length());
      assertEquals("1", results.getNodeRef(0).getId());
      assertEquals("2", results.getNodeRef(1).getId());
      assertEquals(1.0f, results.getScore(0));
      assertEquals(1.0f, results.getScore(1));
      
      QName qname = QName.createQName("", "property-1");
     
      for(ResultSetRow row: results)
      {
         System.out.println("Node = "+row.getNodeRef()+" score "+row.getScore());
         System.out.println("QName <"+qname+"> = "+row.getValue(qname));
         System.out.print("\t");
         Value[] values = row.getValues();
         for(Value value: values)
         {
            System.out.print("<");
            System.out.print(value);
            System.out.print(">");
         }
         System.out.println();
         
      }
      
      
      results = searcher.query(storeRef, "lucene", "ID:\"1\"", null, null );
      assertEquals(1, results.length());
      

   }
   
   /**
    * Test index and search agaist Hibernate
    *
    */
   public void xtestStandAloneIndexerCommitWithHibernate()
   {
      ApplicationContext factory = new ClassPathXmlApplicationContext("applicationContext.xml");
      NodeService nodeService = (NodeService) factory.getBean("nodeService");

      StoreService storeService = (StoreService) factory.getBean("storeService");

      StoreRef storeRef = storeService.createStore(StoreRef.PROTOCOL_WORKSPACE, "test-workspace"
            + System.currentTimeMillis());

      NodeRef rootNode = storeService.getRootNode(storeRef);

      Map<String, String> testProperties = new HashMap<String, String>();
      testProperties.put("property", "value");

      NodeRef newNode = nodeService.createNode(rootNode, "path", Node.TYPE_CONTENT, testProperties);

      LuceneIndexer indexer = LuceneIndexer.getUpdateIndexer(storeRef, "delta"+System.currentTimeMillis());
      indexer.setNodeService(nodeService);

      indexer.createNode(new ChildRelationshipRef(rootNode, "path", newNode));
      indexer.updateNode(newNode);
      indexer.deleteNode(new ChildRelationshipRef(rootNode, "path", newNode));

      indexer.commit();

   }
   
   public void testIOC()
   {
    
      
      ApplicationContext factory = new ClassPathXmlApplicationContext("applicationContext.xml");
      IndexerAndSearcher indexerAndSearcher = (IndexerAndSearcher) factory.getBean("indexerAndSearcherFactory");
      StoreService storeService = (StoreService) factory.getBean("storeService");

      StoreRef storeRef = storeService.createStore(StoreRef.PROTOCOL_WORKSPACE, "test-workspace"
            + System.currentTimeMillis());

   }
   
   /**
    * Test thread local transactions and indexing
    *
    */
   public void testThreadLocalTXIndexerCommit()
   {


      StoreRef storeRef1 = new StoreRef(StoreRef.PROTOCOL_WORKSPACE, "ws1");
      NodeRef newNode1 = new NodeRef(storeRef1, "1");
      NodeRef rootNode1 = new NodeRef(storeRef1, "0");
      
      StoreRef storeRef2 = new StoreRef(StoreRef.PROTOCOL_WORKSPACE, "ws2");
      NodeRef newNode2 = new NodeRef(storeRef2, "1");
      NodeRef rootNode2 = new NodeRef(storeRef2, "0");

      LuceneIndexer indexer1 = LuceneIndexerAndSearcherFactory.getInstance().getIndexer(storeRef1);
      indexer1.setNodeService(new NodeServiceStub());
      
      LuceneIndexer indexer2 = LuceneIndexerAndSearcherFactory.getInstance().getIndexer(storeRef2);
      indexer2.setNodeService(new NodeServiceStub());

      indexer1.createNode(new ChildRelationshipRef(rootNode1, "path", newNode1));
      indexer1.updateNode(newNode1);
      indexer1.deleteNode(new ChildRelationshipRef(rootNode1, "path", newNode1));
      
      indexer2.createNode(new ChildRelationshipRef(rootNode2, "path", newNode2));
      indexer2.updateNode(newNode2);
      indexer2.deleteNode(new ChildRelationshipRef(rootNode2, "path", newNode2));

      LuceneIndexerAndSearcherFactory.getInstance().commit();

   }

   /**
    * Support for DummyNodeService
    * 
    * @author andyh
    *
    */

   private static class NodeServiceStub implements NodeService
   {

      StoreRef storeRef = new StoreRef(StoreRef.PROTOCOL_WORKSPACE, "ws");

      NodeRef n1 = new NodeRef(storeRef, "1");

      NodeRef n2 = new NodeRef(storeRef, "2");

      NodeRef n3 = new NodeRef(storeRef, "3");

      NodeRef n4 = new NodeRef(storeRef, "4");

      NodeRef rootNode = new NodeRef(storeRef, "0");

      public NodeRef createNode(NodeRef parentRef, String name, String nodeType) throws InvalidNodeRefException
      {
         // TODO Auto-generated method stub
         throw new UnsupportedOperationException();
      }

      public NodeRef createNode(NodeRef parentRef, String name, String nodeType, Map<String, String> properties)
            throws InvalidNodeRefException
      {
         // TODO Auto-generated method stub
         throw new UnsupportedOperationException();
      }

      public void deleteNode(NodeRef nodeRef) throws InvalidNodeRefException
      {
         // TODO Auto-generated method stub
         throw new UnsupportedOperationException();
      }

      public void addChild(NodeRef parentRef, NodeRef childRef, String name) throws InvalidNodeRefException
      {
         // TODO Auto-generated method stub
         throw new UnsupportedOperationException();
      }

      public void removeChild(NodeRef parentRef, NodeRef childRef) throws InvalidNodeRefException
      {
         // TODO Auto-generated method stub
         throw new UnsupportedOperationException();
      }

      public void removeChildren(NodeRef parentRef, String name) throws InvalidNodeRefException
      {
         // TODO Auto-generated method stub
         throw new UnsupportedOperationException();
      }

      public String getType(NodeRef nodeRef) throws InvalidNodeRefException
      {
         if (nodeRef.getId().equals("0"))
         {
            return Node.TYPE_CONTAINER;
         }
         else if (nodeRef.getId().equals("1"))
         {
            return Node.TYPE_CONTENT;
         }
         else if (nodeRef.getId().equals("2"))
         {
            return Node.TYPE_CONTENT;
         }
         else if (nodeRef.getId().equals("3"))
         {
            return Node.TYPE_CONTENT;
         }
         else if (nodeRef.getId().equals("4"))
         {
            return Node.TYPE_CONTENT;
         }
         else
         {
            throw new InvalidNodeRefException(nodeRef);
         }
      }

      public Map<String, String> getProperties(NodeRef nodeRef) throws InvalidNodeRefException
      {
         Map<String, String> answer = new HashMap<String, String>();
         if (nodeRef.getId().equals("1"))
         {
            answer.put("{}property-1", "value-1");
         }
         else if (nodeRef.getId().equals("2"))
         {
            answer.put("{}property-1", "value-1");
            answer.put("{}property-2", "value-2");
         }
         else if (nodeRef.getId().equals("3"))
         {
            answer.put("{}property-1", "value-1");
            answer.put("{}property-2", "value-2");
            answer.put("{}property-3", "value-3");
         }
         else if (nodeRef.getId().equals("4"))
         {
            answer.put("{}property-1", "value-1");
            answer.put("{}property-2", "value-2");
            answer.put("{}property-3", "value-3");
            answer.put("{}property-4", "value-4");
         }
         return answer;
      }

      public void setProperties(NodeRef nodeRef, Map<String, String> properties) throws InvalidNodeRefException
      {
         // TODO Auto-generated method stub
         throw new UnsupportedOperationException();
      }

      public Collection<NodeRef> getParents(NodeRef nodeRef) throws InvalidNodeRefException
      {
         Set<NodeRef> parents = new HashSet<NodeRef>();
         if (nodeRef.getId().equals("1"))
         {
            parents.add(rootNode);
         }
         else if (nodeRef.getId().equals("2"))
         {
            parents.add(rootNode);
         }
         else if (nodeRef.getId().equals("3"))
         {
            parents.add(rootNode);
         }
         else if (nodeRef.getId().equals("4"))
         {
            parents.add(rootNode);
         }
         return parents;
      }

      public Collection<NodeRef> getChildren(NodeRef nodeRef) throws InvalidNodeRefException
      {
         // TODO Auto-generated method stub
         throw new UnsupportedOperationException();
      }

      public NodeRef getPrimaryParent(NodeRef nodeRef) throws InvalidNodeRefException
      {
         // TODO Auto-generated method stub
         throw new UnsupportedOperationException();
      }

      public void createAssociation(NodeRef sourceRef, NodeRef targetRef, String assocName)
            throws InvalidNodeRefException, AssociationExistsException
      {
         // TODO Auto-generated method stub
         throw new UnsupportedOperationException();
      }

      public void removeAssociation(NodeRef sourceRef, NodeRef targetRef, String assocName)
            throws InvalidNodeRefException
      {
         // TODO Auto-generated method stub
         throw new UnsupportedOperationException();
      }

      public Collection<NodeRef> getAssociationTargets(NodeRef sourceRef, String assocName)
            throws InvalidNodeRefException
      {
         // TODO Auto-generated method stub
         throw new UnsupportedOperationException();
      }

      public Collection<NodeRef> getAssociationSources(NodeRef targetRef, String assocName)
            throws InvalidNodeRefException
      {
         // TODO Auto-generated method stub
         throw new UnsupportedOperationException();
      }

     

      public String getProperty(NodeRef nodeRef, String propertyName) throws InvalidNodeRefException
      {
         // TODO Auto-generated method stub
         throw new UnsupportedOperationException();
      }

      public void setProperty(NodeRef nodeRef, String propertyName, String propertyValue) throws InvalidNodeRefException
      {
         // TODO Auto-generated method stub
         throw new UnsupportedOperationException();
      }

      public Collection<Path> getPaths(NodeRef nodeRef) throws InvalidNodeRefException
      {
         // TODO Auto-generated method stub
         throw new UnsupportedOperationException();
      }

      public Path getPath(NodeRef nodeRef) throws InvalidNodeRefException
      {
         // TODO Auto-generated method stub
         throw new UnsupportedOperationException();
      }

      public Collection<Path> getPaths(NodeRef nodeRef, boolean primaryOnly) throws InvalidNodeRefException
      {
         // TODO Auto-generated method stub
         throw new UnsupportedOperationException();
      }

   }
   
   public static void main(String[] args)
   {
      String guid = GUID.generate();
      System.out.println("GUID is "+guid+" length is "+guid.length());
   }
}
