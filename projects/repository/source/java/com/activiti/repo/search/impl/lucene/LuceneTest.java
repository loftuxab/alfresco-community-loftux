/*
 * Created on Mar 31, 2005
 * 
 * TODO Comment this class
 * 
 * 
 */
package com.activiti.repo.search.impl.lucene;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import junit.framework.TestCase;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.activiti.repo.domain.Node;
import com.activiti.repo.node.AssociationExistsException;
import com.activiti.repo.node.InvalidNodeRefException;
import com.activiti.repo.node.NodeService;
import com.activiti.repo.ref.ChildAssocRef;
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

      LuceneIndexer indexer = LuceneIndexer.getUpdateIndexer(NodeServiceStub.storeRef, "delta" + System.currentTimeMillis());
      indexer.setNodeService(new NodeServiceStub());

      indexer.createNode(new ChildAssocRef(NodeServiceStub.rootNode, QName.createQName("{}one"), NodeServiceStub.n1));
      indexer.createNode(new ChildAssocRef(NodeServiceStub.rootNode, QName.createQName("{}two"), NodeServiceStub.n2));
      //indexer.updateNode(NodeServiceStub.n1);
      // indexer.deleteNode(new ChildRelationshipRef(rootNode, "path",
      // newNode));

      indexer.commit();

      Searcher searcher = LuceneSearcher.getSearcher(NodeServiceStub.storeRef);

      ResultSet results = searcher.query(NodeServiceStub.storeRef, "lucene", "@\\{\\}property-1:value-1", null, null);
      assertEquals(2, results.length());
      assertEquals("1", results.getNodeRef(0).getId());
      assertEquals("2", results.getNodeRef(1).getId());
      assertEquals(1.0f, results.getScore(0));
      assertEquals(1.0f, results.getScore(1));

      QName qname = QName.createQName("", "property-1");

      for (ResultSetRow row : results)
      {
         System.out.println("Node = " + row.getNodeRef() + " score " + row.getScore());
         System.out.println("QName <" + qname + "> = " + row.getValue(qname));
         System.out.print("\t");
         Value[] values = row.getValues();
         for (Value value : values)
         {
            System.out.print("<");
            System.out.print(value);
            System.out.print(">");
         }
         System.out.println();

      }

      results = searcher.query(NodeServiceStub.storeRef, "lucene", "ID:\"1\"", null, null);
      assertEquals(1, results.length());

   }
   
   public void testStandAlonePathIndexer()
   {
      LuceneIndexer indexer = LuceneIndexer.getUpdateIndexer(NodeServiceStub.storeRef, "delta" + System.currentTimeMillis());
      indexer.setNodeService(new NodeServiceStub());

      indexer.createNode(new ChildAssocRef(NodeServiceStub.rootNode, QName.createQName("{}one"), NodeServiceStub.n1));
      indexer.createNode(new ChildAssocRef(NodeServiceStub.rootNode, QName.createQName("{}two"), NodeServiceStub.n2));
      indexer.createNode(new ChildAssocRef(NodeServiceStub.rootNode, QName.createQName("{}three"), NodeServiceStub.n3));
      indexer.createNode(new ChildAssocRef(NodeServiceStub.rootNode, QName.createQName("{}four"), NodeServiceStub.n4));
      indexer.createNode(new ChildAssocRef(NodeServiceStub.n1, QName.createQName("{}five"), NodeServiceStub.n5));
      indexer.createNode(new ChildAssocRef(NodeServiceStub.n1, QName.createQName("{}six"), NodeServiceStub.n6));
      indexer.createNode(new ChildAssocRef(NodeServiceStub.n2, QName.createQName("{}seven"), NodeServiceStub.n7));
      indexer.createNode(new ChildAssocRef(NodeServiceStub.n2, QName.createQName("{}eight"), NodeServiceStub.n8));
      indexer.createNode(new ChildAssocRef(NodeServiceStub.n5, QName.createQName("{}nine"), NodeServiceStub.n9));
      indexer.createNode(new ChildAssocRef(NodeServiceStub.n5, QName.createQName("{}ten"), NodeServiceStub.n10));
      indexer.createNode(new ChildAssocRef(NodeServiceStub.n5, QName.createQName("{}eleven"), NodeServiceStub.n11));
      indexer.createNode(new ChildAssocRef(NodeServiceStub.n5, QName.createQName("{}twelve"), NodeServiceStub.n12));
      
      // indexer.deleteNode(new ChildRelationshipRef(rootNode, "path",
      // newNode));

      indexer.commit();

      Searcher searcher = LuceneSearcher.getSearcher(NodeServiceStub.storeRef);

      ResultSet results = searcher.query(NodeServiceStub.storeRef, "lucene", "@\\{\\}property-1:value-1", null, null);
      assertEquals(2, results.length());
      assertEquals("1", results.getNodeRef(0).getId());
      assertEquals("2", results.getNodeRef(1).getId());
      assertEquals(1.0f, results.getScore(0));
      assertEquals(1.0f, results.getScore(1));

      QName qname = QName.createQName("", "property-1");

      for (ResultSetRow row : results)
      {
         System.out.println("Node = " + row.getNodeRef() + " score " + row.getScore());
         System.out.println("QName <" + qname + "> = " + row.getValue(qname));
         System.out.print("\t");
         Value[] values = row.getValues();
         for (Value value : values)
         {
            System.out.print("<");
            System.out.print(value);
            System.out.print(">");
         }
         System.out.println();

      }

      results = searcher.query(NodeServiceStub.storeRef, "lucene", "ID:\"1\"", null, null);
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

      NodeRef newNode = nodeService.createNode(rootNode, null, "path", Node.TYPE_CONTENT, testProperties);

      LuceneIndexer indexer = LuceneIndexer.getUpdateIndexer(storeRef, "delta" + System.currentTimeMillis());
      indexer.setNodeService(nodeService);

      indexer.createNode(new ChildAssocRef(rootNode, QName.createQName("{}path"), newNode));
      indexer.updateNode(newNode);
      indexer.deleteNode(new ChildAssocRef(rootNode, QName.createQName("{}path"), newNode));

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

      indexer1.createNode(new ChildAssocRef(rootNode1, QName.createQName("{}path"), newNode1));
      indexer1.updateNode(newNode1);
      indexer1.deleteNode(new ChildAssocRef(rootNode1, QName.createQName("{}path"), newNode1));

      indexer2.createNode(new ChildAssocRef(rootNode2, QName.createQName("{}path"), newNode2));
      indexer2.updateNode(newNode2);
      indexer2.deleteNode(new ChildAssocRef(rootNode2, QName.createQName("{}path"), newNode2));

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

      static StoreRef storeRef = new StoreRef(StoreRef.PROTOCOL_WORKSPACE, "ws");

      static NodeRef rootNode = new NodeRef(storeRef, "0");

      static NodeRef n1 = new NodeRef(storeRef, "1");

      static NodeRef n2 = new NodeRef(storeRef, "2");

      static NodeRef n3 = new NodeRef(storeRef, "3");

      static NodeRef n4 = new NodeRef(storeRef, "4");

      static NodeRef n5 = new NodeRef(storeRef, "5");

      static NodeRef n6 = new NodeRef(storeRef, "6");

      static NodeRef n7 = new NodeRef(storeRef, "7");

      static NodeRef n8 = new NodeRef(storeRef, "8");

      static NodeRef n9 = new NodeRef(storeRef, "9");

      static NodeRef n10 = new NodeRef(storeRef, "10");

      static NodeRef n11 = new NodeRef(storeRef, "11");

      static NodeRef n12 = new NodeRef(storeRef, "12");

      public String getType(NodeRef nodeRef) throws InvalidNodeRefException
      {
         if (nodeRef.getId().equals("0"))
         {
            return Node.TYPE_CONTAINER;
         }
         else if (nodeRef.getId().equals("1"))
         {
            return Node.TYPE_CONTAINER;
         }
         else if (nodeRef.getId().equals("2"))
         {
            return Node.TYPE_CONTAINER;
         }
         else if (nodeRef.getId().equals("3"))
         {
            return Node.TYPE_CONTENT;
         }
         else if (nodeRef.getId().equals("4"))
         {
            return Node.TYPE_CONTENT;
         }
         else if (nodeRef.getId().equals("5"))
         {
            return Node.TYPE_CONTAINER;
         }
         else if (nodeRef.getId().equals("6"))
         {
            return Node.TYPE_CONTAINER;
         }
         else if (nodeRef.getId().equals("7"))
         {
            return Node.TYPE_CONTENT;
         }
         else if (nodeRef.getId().equals("8"))
         {
            return Node.TYPE_CONTAINER;
         }
         else if (nodeRef.getId().equals("9"))
         {
            return Node.TYPE_CONTENT;
         }
         else if (nodeRef.getId().equals("10"))
         {
            return Node.TYPE_CONTENT;
         }
         else if (nodeRef.getId().equals("11"))
         {
            return Node.TYPE_CONTENT;
         }
         else if (nodeRef.getId().equals("12"))
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
         answer.put("{}createby", "andy");
         if (nodeRef.getId().equals("0"))
         {
            answer.put("{}does-a-property-on-the-root-make-sense", "no");
         }
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

         }
         else if (nodeRef.getId().equals("4"))
         {

         }
         else if (nodeRef.getId().equals("5"))
         {

         }
         else if (nodeRef.getId().equals("6"))
         {

         }
         else if (nodeRef.getId().equals("7"))
         {

         }
         else if (nodeRef.getId().equals("8"))
         {

         }
         else if (nodeRef.getId().equals("9"))
         {

         }
         else if (nodeRef.getId().equals("10"))
         {

         }
         else if (nodeRef.getId().equals("11"))
         {

         }
         else if (nodeRef.getId().equals("12"))
         {

         }
         return answer;
      }

      public Collection<NodeRef> getParents(NodeRef nodeRef) throws InvalidNodeRefException
      {
         Set<NodeRef> parents = new HashSet<NodeRef>();
         if (nodeRef.getId().equals("0"))
         {

         }
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
         else if (nodeRef.getId().equals("5"))
         {
            parents.add(n1);
         }
         else if (nodeRef.getId().equals("6"))
         {
            parents.add(n1);
         }
         else if (nodeRef.getId().equals("7"))
         {
            parents.add(n2);
         }
         else if (nodeRef.getId().equals("8"))
         {
            parents.add(rootNode);
            parents.add(n1);
            parents.add(n2);
         }
         else if (nodeRef.getId().equals("9"))
         {
            parents.add(n5);
         }
         else if (nodeRef.getId().equals("10"))
         {
            parents.add(n5);
         }
         else if (nodeRef.getId().equals("11"))
         {
            parents.add(n5);
         }
         else if (nodeRef.getId().equals("12"))
         {
            parents.add(n5);
         }
         return parents;
      }

      public NodeRef createNode(NodeRef parentRef, String namespaceUri, String name, String nodeType) throws InvalidNodeRefException
      {
         // TODO Auto-generated method stub
         throw new UnsupportedOperationException();
      }

      public NodeRef createNode(NodeRef parentRef, String namespaceUri, String name, String nodeType, Map<String, String> properties)
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

      public void addChild(NodeRef parentRef, NodeRef childRef, String namespaceUri, String name) throws InvalidNodeRefException
      {
         // TODO Auto-generated method stub
         throw new UnsupportedOperationException();
      }

      public void removeChild(NodeRef parentRef, NodeRef childRef) throws InvalidNodeRefException
      {
         // TODO Auto-generated method stub
         throw new UnsupportedOperationException();
      }

      public void removeChildren(NodeRef parentRef, String namespaceUri, String name) throws InvalidNodeRefException
      {
         // TODO Auto-generated method stub
         throw new UnsupportedOperationException();
      }

      public String getProperty(NodeRef nodeRef, String propertyName) throws InvalidNodeRefException
      {
         // TODO Auto-generated method stub
         throw new UnsupportedOperationException();
      }

      public void setProperties(NodeRef nodeRef, Map<String, String> properties) throws InvalidNodeRefException
      {
         // TODO Auto-generated method stub
         throw new UnsupportedOperationException();
      }

      public void setProperty(NodeRef nodeRef, String propertyName, String propertyValue)
            throws InvalidNodeRefException
      {
         // TODO Auto-generated method stub
         throw new UnsupportedOperationException();
      }

      public Collection<ChildAssocRef> getChildAssocs(NodeRef nodeRef) throws InvalidNodeRefException
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

      public Path getPath(NodeRef nodeRef) throws InvalidNodeRefException
      {
         // TODO Auto-generated method stub
         throw new UnsupportedOperationException();
      }

      public Collection<Path> getPaths(NodeRef nodeRef, boolean primaryOnly) throws InvalidNodeRefException
      {
         List<Path> paths = new ArrayList<Path>();
         if (nodeRef.getId().equals("1"))
         {
            Path path = new Path();
            path.append(new Path.ChildAssocElement(new ChildAssocRef(rootNode, QName.createQName("", "one"), n1)));
            paths.add(path);
         }
         else if (nodeRef.getId().equals("2"))
         {
            Path path = new Path();
            path.append(new Path.ChildAssocElement(new ChildAssocRef(rootNode, QName.createQName("", "two"), n2)));
            paths.add(path);
         }
         else if (nodeRef.getId().equals("3"))
         {
            Path path = new Path();
            path.append(new Path.ChildAssocElement(new ChildAssocRef(rootNode, QName.createQName("", "three"), n3)));
            paths.add(path);
         }
         else if (nodeRef.getId().equals("4"))
         {
            Path path = new Path();
            path.append(new Path.ChildAssocElement(new ChildAssocRef(rootNode, QName.createQName("", "four"), n4)));
            paths.add(path);
         }
         else if (nodeRef.getId().equals("5"))
         {
            Path path = new Path();
            path.append(new Path.ChildAssocElement(new ChildAssocRef(rootNode, QName.createQName("", "one"), n1)));
            path.append(new Path.ChildAssocElement(new ChildAssocRef(n1, QName.createQName("", "five"), n5)));
            paths.add(path);
         }
         else if (nodeRef.getId().equals("6"))
         {
            Path path = new Path();
            path.append(new Path.ChildAssocElement(new ChildAssocRef(rootNode, QName.createQName("", "one"), n1)));
            path.append(new Path.ChildAssocElement(new ChildAssocRef(n1, QName.createQName("", "six"), n6)));
            paths.add(path);
         }
         else if (nodeRef.getId().equals("7"))
         {
            Path path = new Path();
            path.append(new Path.ChildAssocElement(new ChildAssocRef(rootNode, QName.createQName("", "two"), n2)));
            path.append(new Path.ChildAssocElement(new ChildAssocRef(n2, QName.createQName("", "seven"), n7)));
            paths.add(path);
         }
         else if (nodeRef.getId().equals("8"))
         {
            Path path = new Path();
            path.append(new Path.ChildAssocElement(new ChildAssocRef(rootNode, QName.createQName("", "eight-0"), n8)));
            paths.add(path);
            path = new Path();
            path.append(new Path.ChildAssocElement(new ChildAssocRef(rootNode, QName.createQName("", "one"), n1)));
            path.append(new Path.ChildAssocElement(new ChildAssocRef(n1, QName.createQName("", "eight-1"), n8)));
            paths.add(path);
            path = new Path();
            path.append(new Path.ChildAssocElement(new ChildAssocRef(rootNode, QName.createQName("", "two"), n2)));
            path.append(new Path.ChildAssocElement(new ChildAssocRef(n2, QName.createQName("", "eight-2"), n8)));
            paths.add(path);
         }
         else if (nodeRef.getId().equals("9"))
         {
            Path path = new Path();
            path.append(new Path.ChildAssocElement(new ChildAssocRef(rootNode, QName.createQName("", "one"), n1)));
            path.append(new Path.ChildAssocElement(new ChildAssocRef(n1, QName.createQName("", "five"), n5)));
            path.append(new Path.ChildAssocElement(new ChildAssocRef(n5, QName.createQName("", "nine"), n9)));
            paths.add(path);
         }
         else if (nodeRef.getId().equals("10"))
         {
            Path path = new Path();
            path.append(new Path.ChildAssocElement(new ChildAssocRef(rootNode, QName.createQName("", "one"), n1)));
            path.append(new Path.ChildAssocElement(new ChildAssocRef(n1, QName.createQName("", "five"), n5)));
            path.append(new Path.ChildAssocElement(new ChildAssocRef(n5, QName.createQName("", "ten"), n10)));
            paths.add(path);
         }
         else if (nodeRef.getId().equals("11"))
         {
            Path path = new Path();
            path.append(new Path.ChildAssocElement(new ChildAssocRef(rootNode, QName.createQName("", "one"), n1)));
            path.append(new Path.ChildAssocElement(new ChildAssocRef(n1, QName.createQName("", "five"), n5)));
            path.append(new Path.ChildAssocElement(new ChildAssocRef(n5, QName.createQName("", "eleven"), n11)));
            paths.add(path);
         }
         else if (nodeRef.getId().equals("12"))
         {
            Path path = new Path();
            path.append(new Path.ChildAssocElement(new ChildAssocRef(rootNode, QName.createQName("", "one"), n1)));
            path.append(new Path.ChildAssocElement(new ChildAssocRef(n1, QName.createQName("", "five"), n5)));
            path.append(new Path.ChildAssocElement(new ChildAssocRef(n5, QName.createQName("", "twelve"), n12)));
            paths.add(path);
         }

         return paths;
      }

   }

   public static void main(String[] args)
   {
      String guid = GUID.generate();
      System.out.println("GUID is " + guid + " length is " + guid.length());
   }
}
