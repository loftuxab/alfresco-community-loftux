package org.alfresco.repo.search.impl.lucene;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;

import junit.framework.TestCase;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import org.alfresco.repo.dictionary.ClassRef;
import org.alfresco.repo.dictionary.DictionaryService;
import org.alfresco.repo.dictionary.NamespaceService;
import org.alfresco.repo.dictionary.PropertyTypeDefinition;
import org.alfresco.repo.dictionary.bootstrap.DictionaryBootstrap;
import org.alfresco.repo.dictionary.metamodel.M2Property;
import org.alfresco.repo.dictionary.metamodel.M2Type;
import org.alfresco.repo.dictionary.metamodel.MetaModelDAO;
import org.alfresco.repo.node.NodeService;
import org.alfresco.repo.ref.ChildAssocRef;
import org.alfresco.repo.ref.NamespaceException;
import org.alfresco.repo.ref.NodeRef;
import org.alfresco.repo.ref.Path;
import org.alfresco.repo.ref.QName;
import org.alfresco.repo.ref.StoreRef;
import org.alfresco.repo.search.ResultSet;
import org.alfresco.repo.search.ResultSetRow;
import org.alfresco.repo.search.Searcher;
import org.alfresco.repo.search.impl.lucene.fts.FullTextSearchIndexer;
import org.alfresco.repo.search.transaction.LuceneIndexLock;

/**
 * @author andyh
 * 
 */
public class LuceneTest extends TestCase
{

    static ApplicationContext ctx = new ClassPathXmlApplicationContext("classpath:applicationContext.xml");

    NodeService nodeService;

    DictionaryService dictionaryService;

    LuceneIndexLock luceneIndexLock; //= (LuceneIndexLock) ctx.getBean("luceneIndexLock");

    private NodeRef rootNodeRef;

    private NodeRef n1;

    private NodeRef n2;

    private NodeRef n3;

    private NodeRef n4;

    private NodeRef n6;

    private NodeRef n5;

    private NodeRef n7;

    private NodeRef n8;

    private NodeRef n9;

    private NodeRef n10;

    private NodeRef n11;

    private NodeRef n12;

    private NodeRef n13;

    private NodeRef n14;

    private MetaModelDAO metaModelDAO;

    private QName testType;

    private ClassRef testTypeRef;

    private FullTextSearchIndexer luceneFTS; // = (FullTextSearchIndexer) ctx.getBean("LuceneFullTextSearchIndexer");

    public LuceneTest()
    {
        super();
    }

    public void setUp()
    {
        
        nodeService = (NodeService) ctx.getBean("dbNodeService");
        luceneIndexLock = (LuceneIndexLock) ctx.getBean("luceneIndexLock");
        dictionaryService = (DictionaryService) ctx.getBean("dictionaryService");
        metaModelDAO = (MetaModelDAO) ctx.getBean("metaModelDAO");
        luceneFTS = (FullTextSearchIndexer) ctx.getBean("LuceneFullTextSearchIndexer");
        
        assertEquals(true, ctx.isSingleton("luceneIndexLock"));
        assertEquals(true, ctx.isSingleton("LuceneFullTextSearchIndexer"));
        

        createTestTypes();

        StoreRef storeRef = nodeService.createStore(StoreRef.PROTOCOL_WORKSPACE, "Test_" + System.currentTimeMillis());
        rootNodeRef = nodeService.getRootNode(storeRef);

        Map<QName, Serializable> testProperties = new HashMap<QName, Serializable>();
        testProperties
                .put(QName.createQName(NamespaceService.ALFRESCO_URI, "text-indexed-stored-tokenised-atomic"), "TEXT THAT IS INDEXED STORED AND TOKENISED ATOMICALLY KEYONE");
        testProperties.put(QName.createQName(NamespaceService.ALFRESCO_URI, "text-indexed-stored-tokenised-nonatomic"),
                "TEXT THAT IS INDEXED STORED AND TOKENISED BUT NOT ATOMICALLY KEYTWO");

        n1 = nodeService.createNode(rootNodeRef, QName.createQName("{namespace}one"), DictionaryBootstrap.TYPE_CONTAINER).getChildRef();
        nodeService.setProperty(n1, QName.createQName("{namespace}property-1"), "value-1");
        n2 = nodeService.createNode(rootNodeRef, QName.createQName("{namespace}two"), DictionaryBootstrap.TYPE_CONTAINER).getChildRef();
        nodeService.setProperty(n2, QName.createQName("{namespace}property-1"), "value-1");
        nodeService.setProperty(n2, QName.createQName("{namespace}property-2"), "value-2");
        n3 = nodeService.createNode(rootNodeRef, QName.createQName("{namespace}three"), DictionaryBootstrap.TYPE_CONTAINER).getChildRef();
        n4 = nodeService.createNode(rootNodeRef, QName.createQName("{namespace}four"), testTypeRef, testProperties).getChildRef();
        n5 = nodeService.createNode(n1, QName.createQName("{namespace}five"), DictionaryBootstrap.TYPE_CONTAINER).getChildRef();
        n6 = nodeService.createNode(n1, QName.createQName("{namespace}six"), DictionaryBootstrap.TYPE_CONTAINER).getChildRef();
        n7 = nodeService.createNode(n2, QName.createQName("{namespace}seven"), DictionaryBootstrap.TYPE_CONTAINER).getChildRef();
        n8 = nodeService.createNode(n2, QName.createQName("{namespace}eight-2"), DictionaryBootstrap.TYPE_CONTAINER).getChildRef();
        n9 = nodeService.createNode(n5, QName.createQName("{namespace}nine"), DictionaryBootstrap.TYPE_CONTAINER).getChildRef();
        n10 = nodeService.createNode(n5, QName.createQName("{namespace}ten"), DictionaryBootstrap.TYPE_CONTAINER).getChildRef();
        n11 = nodeService.createNode(n5, QName.createQName("{namespace}eleven"), DictionaryBootstrap.TYPE_CONTAINER).getChildRef();
        n12 = nodeService.createNode(n5, QName.createQName("{namespace}twelve"), DictionaryBootstrap.TYPE_CONTAINER).getChildRef();
        n13 = nodeService.createNode(n12, QName.createQName("{namespace}thirteen"), DictionaryBootstrap.TYPE_CONTAINER).getChildRef();
        n14 = nodeService.createNode(n13, QName.createQName("{namespace}fourteen"), DictionaryBootstrap.TYPE_CONTAINER).getChildRef();

        nodeService.addChild(rootNodeRef, n8, QName.createQName("{namespace}eight-0"));
        nodeService.addChild(n1, n8, QName.createQName("{namespace}eight-1"));
        nodeService.addChild(n2, n13, QName.createQName("{namespace}link"));

        nodeService.addChild(n1, n14, QName.createQName("{namespace}common"));
        nodeService.addChild(n2, n14, QName.createQName("{namespace}common"));
        nodeService.addChild(n5, n14, QName.createQName("{namespace}common"));
        nodeService.addChild(n6, n14, QName.createQName("{namespace}common"));
        nodeService.addChild(n12, n14, QName.createQName("{namespace}common"));
        nodeService.addChild(n13, n14, QName.createQName("{namespace}common"));
    }

    private void createTestTypes()
    {
        testType = QName.createQName(NamespaceService.ALFRESCO_URI, "testType");
        testTypeRef = new ClassRef(testType);

        M2Type testTypeType = metaModelDAO.createType(testType);
        M2Property text_indexed_stored_tokenised_atomic = testTypeType.createProperty("text-indexed-stored-tokenised-atomic");
        text_indexed_stored_tokenised_atomic.setType(metaModelDAO.getPropertyType(PropertyTypeDefinition.TEXT));
        text_indexed_stored_tokenised_atomic.setMandatory(true);
        text_indexed_stored_tokenised_atomic.setMultiValued(false);
        text_indexed_stored_tokenised_atomic.setIndexed(true);
        text_indexed_stored_tokenised_atomic.setIndexedAtomically(true);
        text_indexed_stored_tokenised_atomic.setStoredInIndex(true);
        text_indexed_stored_tokenised_atomic.setTokenisedInIndex(true);

        M2Property text_indexed_stored_tokenised_nonatomic = testTypeType.createProperty("text-indexed-stored-tokenised-nonatomic");
        text_indexed_stored_tokenised_nonatomic.setType(metaModelDAO.getPropertyType(PropertyTypeDefinition.TEXT));
        text_indexed_stored_tokenised_nonatomic.setMandatory(true);
        text_indexed_stored_tokenised_nonatomic.setMultiValued(false);
        text_indexed_stored_tokenised_nonatomic.setIndexed(true);
        text_indexed_stored_tokenised_nonatomic.setIndexedAtomically(false);
        text_indexed_stored_tokenised_nonatomic.setStoredInIndex(true);
        text_indexed_stored_tokenised_nonatomic.setTokenisedInIndex(true);
    }

    public LuceneTest(String arg0)
    {
        super(arg0);
    }

    public void test1() throws InterruptedException
    {
        luceneFTS.pause();
        buildBaseIndex();
        runBaseTests();
        luceneFTS.resume();
    }

    public void test2() throws InterruptedException
    {
        luceneFTS.pause();
        buildBaseIndex();
        runBaseTests();
        luceneFTS.resume();
    }

    public void test3() throws InterruptedException
    {
        luceneFTS.pause();
        buildBaseIndex();
        runBaseTests();
        luceneFTS.resume();
    }

    public void test4() throws InterruptedException
    {
        luceneFTS.pause();
        buildBaseIndex();

        Searcher searcher = LuceneSearcherImpl.getSearcher(rootNodeRef.getStoreRef());

        ResultSet results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "\\@\\{namespace\\}property\\-2:\"value-2\"", null, null);
        results.close();
        luceneFTS.resume();
    }

    public void test5() throws InterruptedException
    {
        luceneFTS.pause();
        buildBaseIndex();
        runBaseTests();
        luceneFTS.resume();
    }

    public void test6() throws InterruptedException
    {
        luceneFTS.pause();
        buildBaseIndex();
        runBaseTests();
        luceneFTS.resume();
    }

    public void testNoOp() throws InterruptedException
    {
        luceneFTS.pause();
        LuceneIndexerImpl indexer = LuceneIndexerImpl.getUpdateIndexer(rootNodeRef.getStoreRef(), "delta" + System.currentTimeMillis() + "_1");

        indexer.setNodeService(nodeService);
        indexer.setLuceneIndexLock(luceneIndexLock);
        indexer.setDictionaryService(dictionaryService);
        indexer.setLuceneFullTextSearchIndexer(luceneFTS);

        indexer.prepare();
        indexer.commit();
        luceneFTS.resume();
    }

    /**
     * Test basic index and search
     * @throws InterruptedException 
     * 
     */

    public void testStandAloneIndexerCommit() throws InterruptedException
    {

        luceneFTS.pause();
        LuceneIndexerImpl indexer = LuceneIndexerImpl.getUpdateIndexer(rootNodeRef.getStoreRef(), "delta" + System.currentTimeMillis() + "_1");

        indexer.setNodeService(nodeService);
        indexer.setLuceneIndexLock(luceneIndexLock);
        indexer.setDictionaryService(dictionaryService);
        indexer.setLuceneFullTextSearchIndexer(luceneFTS);

        indexer.clearIndex();

        indexer.createNode(new ChildAssocRef(null, null, rootNodeRef));
        indexer.createNode(new ChildAssocRef(rootNodeRef, QName.createQName("{namespace}one"), n1));
        indexer.createNode(new ChildAssocRef(rootNodeRef, QName.createQName("{namespace}two"), n2));
        indexer.updateNode(n1);
        // indexer.deleteNode(new ChildRelationshipRef(rootNode, "path",
        // newNode));

        indexer.prepare();
        indexer.commit();

        Searcher searcher = LuceneSearcherImpl.getSearcher(rootNodeRef.getStoreRef());

        ResultSet results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "\\@\\{namespace\\}property\\-2:\"value-2\"", null, null);

        assertEquals(1, results.length());
        assertEquals(n2.getId(), results.getNodeRef(0).getId());
        results.close();

        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "\\@\\{namespace\\}property\\-1:\"value-1\"", null, null);
        assertEquals(2, results.length());
        assertEquals(n2.getId(), results.getNodeRef(0).getId());
        assertEquals(n1.getId(), results.getNodeRef(1).getId());
        assertEquals(1.0f, results.getScore(0));
        assertEquals(1.0f, results.getScore(1));
        results.close();

        QName qname = QName.createQName("", "property-1");

        // for (ResultSetRow row : results)
        // {
        // System.out.println("Node = " + row.getNodeRef() + " score " +
        // row.getScore());
        // System.out.println("QName <" + qname + "> = " + row.getValue(qname));
        // System.out.print("\t");
        // Value[] values = row.getValues();
        // for (Value value : values)
        // {
        // System.out.print("<");
        // System.out.print(value);
        // System.out.print(">");
        // }
        // System.out.println();
        //
        // }

        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "ID:\"" + n1.getId() + "\"", null, null);

        assertEquals(2, results.length());

        results.close();
        luceneFTS.resume();

    }

    public void testStandAlonePathIndexer() throws InterruptedException
    {
        luceneFTS.pause();
        buildBaseIndex();

        Searcher searcher = LuceneSearcherImpl.getSearcher(rootNodeRef.getStoreRef());

        ResultSet results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "@\\{namespace\\}property-1:value-1", null, null);
        try
        {
            assertEquals(2, results.length());
            assertEquals(n1.getId(), results.getNodeRef(0).getId());
            assertEquals(n2.getId(), results.getNodeRef(1).getId());
            assertEquals(1.0f, results.getScore(0));
            assertEquals(1.0f, results.getScore(1));

            QName qname = QName.createQName("", "property-1");

            // for (ResultSetRow row : results)
            // {
            // System.out.println("Node = " + row.getNodeRef() + " score " +
            // row.getScore());
            // System.out.println("QName <" + qname + "> = " +
            // row.getValue(qname));
            // System.out.print("\t");
            // Value[] values = row.getValues();
            // for (Value value : values)
            // {
            // System.out.print("<");
            // System.out.print(value);
            // System.out.print(">");
            // }
            // System.out.println();
            //
            // }
        }
        finally
        {
            results.close();
        }

        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "+ID:\"" + n1.getId() + "\"", null, null);
        try
        {
            assertEquals(2, results.length());
        }
        finally
        {
            results.close();
        }

        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "ID:\"" + rootNodeRef.getId() + "\"", null, null);
        try
        {
            assertEquals(1, results.length());
        }
        finally
        {
            results.close();
        }
        luceneFTS.resume();

    }

    private void buildBaseIndex()
    {
        LuceneIndexerImpl indexer = LuceneIndexerImpl.getUpdateIndexer(rootNodeRef.getStoreRef(), "delta" + System.currentTimeMillis() + "_" + (new Random().nextInt()));
        indexer.setNodeService(nodeService);
        indexer.setLuceneIndexLock(luceneIndexLock);
        indexer.setDictionaryService(dictionaryService);
        indexer.setLuceneFullTextSearchIndexer(luceneFTS);
        indexer.clearIndex();
        indexer.createNode(new ChildAssocRef(null, null, rootNodeRef));
        indexer.createNode(new ChildAssocRef(rootNodeRef, QName.createQName("{namespace}one"), n1));
        indexer.createNode(new ChildAssocRef(rootNodeRef, QName.createQName("{namespace}two"), n2));
        indexer.createNode(new ChildAssocRef(rootNodeRef, QName.createQName("{namespace}three"), n3));
        indexer.createNode(new ChildAssocRef(rootNodeRef, QName.createQName("{namespace}four"), n4));
        indexer.createNode(new ChildAssocRef(n1, QName.createQName("{namespace}five"), n5));
        indexer.createNode(new ChildAssocRef(n1, QName.createQName("{namespace}six"), n6));
        indexer.createNode(new ChildAssocRef(n2, QName.createQName("{namespace}seven"), n7));
        indexer.createNode(new ChildAssocRef(n2, QName.createQName("{namespace}eight"), n8));
        indexer.createNode(new ChildAssocRef(n5, QName.createQName("{namespace}nine"), n9));
        indexer.createNode(new ChildAssocRef(n5, QName.createQName("{namespace}ten"), n10));
        indexer.createNode(new ChildAssocRef(n5, QName.createQName("{namespace}eleven"), n11));
        indexer.createNode(new ChildAssocRef(n5, QName.createQName("{namespace}twelve"), n12));
        indexer.createNode(new ChildAssocRef(n12, QName.createQName("{namespace}thirteen"), n13));
        indexer.createNode(new ChildAssocRef(n13, QName.createQName("{namespace}fourteen"), n14));
        indexer.prepare();
        indexer.commit();
    }

    public void testAllPathSearch() throws InterruptedException
    {
        luceneFTS.pause();
        buildBaseIndex();

        runBaseTests();
        luceneFTS.resume();

    }

    private void runBaseTests()
    {
        Searcher searcher = LuceneSearcherImpl.getSearcher(rootNodeRef.getStoreRef());
        searcher.setNameSpaceService(new MockNameService());
        ResultSet results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "PATH:\"/namespace:one\"", null, null);
        assertEquals(1, results.length());
        results.close();
        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "PATH:\"/namespace:two\"", null, null);
        assertEquals(1, results.length());
        results.close();
        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "PATH:\"/namespace:three\"", null, null);
        assertEquals(1, results.length());
        results.close();
        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "PATH:\"/namespace:four\"", null, null);
        assertEquals(1, results.length());
        results.close();
        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "PATH:\"/namespace:eight-0\"", null, null);
        assertEquals(1, results.length());
        results.close();
        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "PATH:\"/namespace:five\"", null, null);
        assertEquals(0, results.length());
        results.close();
        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "PATH:\"/namespace:one/namespace:one\"", null, null);
        assertEquals(0, results.length());
        results.close();
        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "PATH:\"/namespace:one/namespace:two\"", null, null);
        assertEquals(0, results.length());
        results.close();
        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "PATH:\"/namespace:two/namespace:one\"", null, null);
        assertEquals(0, results.length());
        results.close();
        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "PATH:\"/namespace:two/namespace:two\"", null, null);
        assertEquals(0, results.length());
        results.close();
        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "PATH:\"/namespace:one/namespace:five\"", null, null);
        assertEquals(1, results.length());
        results.close();
        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "PATH:\"/namespace:one/namespace:six\"", null, null);
        assertEquals(1, results.length());
        results.close();
        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "PATH:\"/namespace:two/namespace:seven\"", null, null);
        assertEquals(1, results.length());
        results.close();
        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "PATH:\"/namespace:one/namespace:eight-1\"", null, null);
        assertEquals(1, results.length());
        results.close();
        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "PATH:\"/namespace:two/namespace:eight-2\"", null, null);
        assertEquals(1, results.length());
        results.close();
        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "PATH:\"/namespace:one/namespace:eight-2\"", null, null);
        assertEquals(0, results.length());
        results.close();
        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "PATH:\"/namespace:two/namespace:eight-1\"", null, null);
        assertEquals(0, results.length());
        results.close();
        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "PATH:\"/namespace:two/namespace:eight-0\"", null, null);
        assertEquals(0, results.length());
        results.close();
        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "PATH:\"/namespace:one/namespace:eight-0\"", null, null);
        assertEquals(0, results.length());
        results.close();
        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "PATH:\"/namespace:one/namespace:five/namespace:nine\"", null, null);
        assertEquals(1, results.length());
        results.close();
        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "PATH:\"/namespace:one/namespace:five/namespace:ten\"", null, null);
        assertEquals(1, results.length());
        results.close();
        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "PATH:\"/namespace:one/namespace:five/namespace:eleven\"", null, null);
        assertEquals(1, results.length());
        results.close();
        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "PATH:\"/namespace:one/namespace:five/namespace:twelve\"", null, null);
        assertEquals(1, results.length());
        results.close();
        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "PATH:\"/namespace:one/namespace:five/namespace:twelve/namespace:thirteen\"", null, null);
        assertEquals(1, results.length());
        results.close();
        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "PATH:\"/namespace:one/namespace:five/namespace:twelve/namespace:thirteen/namespace:fourteen\"", null, null);
        assertEquals(1, results.length());
        results.close();
        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "PATH:\"/namespace:*\"", null, null);
        assertEquals(5, results.length());
        results.close();
        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "PATH:\"/namespace:*/namespace:*\"", null, null);
        assertEquals(8, results.length());
        results.close();
        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "PATH:\"/namespace:*/namespace:five\"", null, null);
        assertEquals(1, results.length());
        results.close();
        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "PATH:\"/namespace:*/namespace:*/namespace:*\"", null, null);
        assertEquals(8, results.length());
        results.close();
        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "PATH:\"/namespace:one/namespace:*\"", null, null);
        assertEquals(4, results.length());
        results.close();
        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "PATH:\"/namespace:*/namespace:five/namespace:*\"", null, null);
        assertEquals(5, results.length());
        results.close();
        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "PATH:\"/namespace:one/namespace:*/namespace:nine\"", null, null);
        assertEquals(1, results.length());
        results.close();
        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "PATH:\"/*\"", null, null);
        assertEquals(5, results.length());
        results.close();
        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "PATH:\"/*/*\"", null, null);
        assertEquals(8, results.length());
        results.close();
        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "PATH:\"/*/namespace:five\"", null, null);
        assertEquals(1, results.length());
        results.close();
        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "PATH:\"/*/*/*\"", null, null);
        assertEquals(8, results.length());
        results.close();
        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "PATH:\"/namespace:one/*\"", null, null);
        assertEquals(4, results.length());
        results.close();
        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "PATH:\"/*/namespace:five/*\"", null, null);
        assertEquals(5, results.length());
        results.close();
        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "PATH:\"/namespace:one/*/namespace:nine\"", null, null);
        assertEquals(1, results.length());
        results.close();
        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "PATH:\"//.\"", null, null);
        assertEquals(26, results.length());
        results.close();
        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "PATH:\"//*\"", null, null);
        assertEquals(25, results.length());
        results.close();
        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "PATH:\"//*/.\"", null, null);
        assertEquals(25, results.length());
        results.close();
        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "PATH:\"//*/./.\"", null, null);
        assertEquals(25, results.length());
        results.close();
        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "PATH:\"//./*\"", null, null);
        assertEquals(25, results.length());
        results.close();
        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "PATH:\"//././*/././.\"", null, null);
        assertEquals(25, results.length());
        results.close();
        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "PATH:\"//common\"", null, null);
        assertEquals(7, results.length());
        results.close();
        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "PATH:\"/one//common\"", null, null);
        assertEquals(5, results.length());
        results.close();
        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "PATH:\"/one/five//*\"", null, null);
        assertEquals(9, results.length());
        results.close();
        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "PATH:\"/one/five//.\"", null, null);
        assertEquals(10, results.length());
        results.close();
        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "PATH:\"/one//five/nine\"", null, null);
        assertEquals(1, results.length());
        results.close();
        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "PATH:\"/one//thirteen/fourteen\"", null, null);
        assertEquals(1, results.length());
        results.close();
    }

    public void testPathSearch() throws InterruptedException
    {
        luceneFTS.pause();
        buildBaseIndex();

        Searcher searcher = LuceneSearcherImpl.getSearcher(rootNodeRef.getStoreRef());
        searcher.setNameSpaceService(new MockNameService());

        // //*

        ResultSet

        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "PATH:\"//common\"", null, null);
        assertEquals(7, results.length());
        results.close();

        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "PATH:\"/one//common\"", null, null);
        assertEquals(5, results.length());
        results.close();

        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "PATH:\"/one/five//*\"", null, null);
        assertEquals(9, results.length());
        results.close();

        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "PATH:\"/one/five//.\"", null, null);
        assertEquals(10, results.length());
        results.close();

        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "PATH:\"/one//five/nine\"", null, null);
        assertEquals(1, results.length());
        results.close();

        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "PATH:\"/one//thirteen/fourteen\"", null, null);
        assertEquals(1, results.length());
        results.close();
        luceneFTS.resume();
    }

    void printPaths(ResultSet results)
    {
        System.out.println("\n\n");

        for (ResultSetRow row : results)
        {
            System.out.println(row.getNodeRef());
            for (Path path : nodeService.getPaths(row.getNodeRef(), false))
            {
                System.out.println("\t" + path);
            }
        }
    }

    public void testXPathSearch() throws InterruptedException
    {
        luceneFTS.pause();
        buildBaseIndex();

        Searcher searcher = LuceneSearcherImpl.getSearcher(rootNodeRef.getStoreRef());
        searcher.setNameSpaceService(new MockNameService());

        // //*

        ResultSet

        results = searcher.query(rootNodeRef.getStoreRef(), "xpath", "//./*", null, null);
        assertEquals(25, results.length());
        results.close();
        luceneFTS.resume();
    }

    public void testMissingIndex() throws InterruptedException
    {
        luceneFTS.pause();
        StoreRef storeRef = new StoreRef(StoreRef.PROTOCOL_WORKSPACE, "_missing_");
        Searcher searcher = LuceneSearcherImpl.getSearcher(storeRef);
        searcher.setNameSpaceService(new MockNameService());

        // //*

        ResultSet

        results = searcher.query(storeRef, "xpath", "//./*", null, null);
        assertEquals(0, results.length());
        luceneFTS.resume();
    }

    public void testUpdateIndex() throws InterruptedException
    {
        luceneFTS.pause();
        buildBaseIndex();

        runBaseTests();

        LuceneIndexerImpl indexer = LuceneIndexerImpl.getUpdateIndexer(rootNodeRef.getStoreRef(), "delta" + System.currentTimeMillis());
        indexer.setNodeService(nodeService);
        indexer.setLuceneIndexLock(luceneIndexLock);
        indexer.setDictionaryService(dictionaryService);
        indexer.setLuceneFullTextSearchIndexer(luceneFTS);

        indexer.updateNode(rootNodeRef);
        indexer.updateNode(n1);
        indexer.updateNode(n2);
        indexer.updateNode(n3);
        indexer.updateNode(n4);
        indexer.updateNode(n5);
        indexer.updateNode(n6);
        indexer.updateNode(n7);
        indexer.updateNode(n8);
        indexer.updateNode(n9);
        indexer.updateNode(n10);
        indexer.updateNode(n11);
        indexer.updateNode(n12);
        indexer.updateNode(n13);
        indexer.updateNode(n14);

        indexer.commit();

        runBaseTests();
        luceneFTS.resume();
    }

    public void testDeleteLeaf() throws InterruptedException
    {
        luceneFTS.pause();
        buildBaseIndex();
        runBaseTests();

        LuceneIndexerImpl indexer = LuceneIndexerImpl.getUpdateIndexer(rootNodeRef.getStoreRef(), "delta" + System.currentTimeMillis());
        indexer.setNodeService(nodeService);
        indexer.setLuceneIndexLock(luceneIndexLock);
        indexer.setDictionaryService(dictionaryService);
        indexer.setLuceneFullTextSearchIndexer(luceneFTS);

        indexer.deleteNode(new ChildAssocRef(n13, QName.createQName("{namespace}fourteen"), n14));

        indexer.commit();

        Searcher searcher = LuceneSearcherImpl.getSearcher(rootNodeRef.getStoreRef());
        searcher.setNameSpaceService(new MockNameService());
        ResultSet results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "PATH:\"/namespace:one\"", null, null);
        assertEquals(1, results.length());
        results.close();
        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "PATH:\"/namespace:two\"", null, null);
        assertEquals(1, results.length());
        results.close();
        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "PATH:\"/namespace:three\"", null, null);
        assertEquals(1, results.length());
        results.close();
        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "PATH:\"/namespace:four\"", null, null);
        assertEquals(1, results.length());
        results.close();
        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "PATH:\"/namespace:eight-0\"", null, null);
        assertEquals(1, results.length());
        results.close();
        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "PATH:\"/namespace:five\"", null, null);
        assertEquals(0, results.length());
        results.close();
        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "PATH:\"/namespace:one/namespace:one\"", null, null);
        assertEquals(0, results.length());
        results.close();
        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "PATH:\"/namespace:one/namespace:two\"", null, null);
        assertEquals(0, results.length());
        results.close();
        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "PATH:\"/namespace:two/namespace:one\"", null, null);
        assertEquals(0, results.length());
        results.close();
        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "PATH:\"/namespace:two/namespace:two\"", null, null);
        assertEquals(0, results.length());
        results.close();
        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "PATH:\"/namespace:one/namespace:five\"", null, null);
        assertEquals(1, results.length());
        results.close();
        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "PATH:\"/namespace:one/namespace:six\"", null, null);
        assertEquals(1, results.length());
        results.close();
        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "PATH:\"/namespace:two/namespace:seven\"", null, null);
        assertEquals(1, results.length());
        results.close();
        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "PATH:\"/namespace:one/namespace:eight-1\"", null, null);
        assertEquals(1, results.length());
        results.close();
        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "PATH:\"/namespace:two/namespace:eight-2\"", null, null);
        assertEquals(1, results.length());
        results.close();
        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "PATH:\"/namespace:one/namespace:eight-2\"", null, null);
        assertEquals(0, results.length());
        results.close();
        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "PATH:\"/namespace:two/namespace:eight-1\"", null, null);
        assertEquals(0, results.length());
        results.close();
        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "PATH:\"/namespace:two/namespace:eight-0\"", null, null);
        assertEquals(0, results.length());
        results.close();
        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "PATH:\"/namespace:one/namespace:eight-0\"", null, null);
        assertEquals(0, results.length());
        results.close();
        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "PATH:\"/namespace:one/namespace:five/namespace:nine\"", null, null);
        assertEquals(1, results.length());
        results.close();
        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "PATH:\"/namespace:one/namespace:five/namespace:ten\"", null, null);
        assertEquals(1, results.length());
        results.close();
        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "PATH:\"/namespace:one/namespace:five/namespace:eleven\"", null, null);
        assertEquals(1, results.length());
        results.close();
        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "PATH:\"/namespace:one/namespace:five/namespace:twelve\"", null, null);
        assertEquals(1, results.length());
        results.close();
        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "PATH:\"/namespace:one/namespace:five/namespace:twelve/namespace:thirteen\"", null, null);
        assertEquals(1, results.length());
        results.close();
        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "PATH:\"/namespace:one/namespace:five/namespace:twelve/namespace:thirteen/namespace:fourteen\"", null, null);
        assertEquals(0, results.length());
        results.close();
        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "PATH:\"/namespace:*\"", null, null);
        assertEquals(5, results.length());
        results.close();
        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "PATH:\"/namespace:*/namespace:*\"", null, null);
        assertEquals(6, results.length());
        results.close();
        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "PATH:\"/namespace:*/namespace:five\"", null, null);
        assertEquals(1, results.length());
        results.close();
        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "PATH:\"/namespace:*/namespace:*/namespace:*\"", null, null);
        assertEquals(4, results.length());
        results.close();
        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "PATH:\"/namespace:one/namespace:*\"", null, null);
        assertEquals(3, results.length());
        results.close();
        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "PATH:\"/namespace:*/namespace:five/namespace:*\"", null, null);
        assertEquals(4, results.length());
        results.close();
        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "PATH:\"/namespace:one/namespace:*/namespace:nine\"", null, null);
        assertEquals(1, results.length());
        results.close();
        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "PATH:\"/*\"", null, null);
        assertEquals(5, results.length());
        results.close();
        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "PATH:\"/*/*\"", null, null);
        assertEquals(6, results.length());
        results.close();
        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "PATH:\"/*/namespace:five\"", null, null);
        assertEquals(1, results.length());
        results.close();
        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "PATH:\"/*/*/*\"", null, null);
        assertEquals(4, results.length());
        results.close();
        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "PATH:\"/namespace:one/*\"", null, null);
        assertEquals(3, results.length());
        results.close();
        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "PATH:\"/*/namespace:five/*\"", null, null);
        assertEquals(4, results.length());
        results.close();
        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "PATH:\"/namespace:one/*/namespace:nine\"", null, null);
        assertEquals(1, results.length());
        results.close();
        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "PATH:\"//.\"", null, null);
        assertEquals(17, results.length());
        results.close();
        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "PATH:\"//*\"", null, null);
        assertEquals(16, results.length());
        results.close();
        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "PATH:\"//*/.\"", null, null);
        assertEquals(16, results.length());
        results.close();
        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "PATH:\"//*/./.\"", null, null);
        assertEquals(16, results.length());
        results.close();
        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "PATH:\"//./*\"", null, null);
        assertEquals(16, results.length());
        results.close();
        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "PATH:\"//././*/././.\"", null, null);
        assertEquals(16, results.length());
        results.close();
        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "PATH:\"//common\"", null, null);
        assertEquals(0, results.length());
        results.close();
        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "PATH:\"/one//common\"", null, null);
        assertEquals(0, results.length());
        results.close();
        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "PATH:\"/one/five//*\"", null, null);
        assertEquals(5, results.length());
        results.close();
        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "PATH:\"/one/five//.\"", null, null);
        assertEquals(6, results.length());
        results.close();
        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "PATH:\"/one//five/nine\"", null, null);
        assertEquals(1, results.length());
        results.close();
        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "PATH:\"/one//thirteen/fourteen\"", null, null);
        assertEquals(0, results.length());
        results.close();
        luceneFTS.resume();
    }

    public void testDeleteContainer() throws InterruptedException
    {
        luceneFTS.pause();
        buildBaseIndex();
        runBaseTests();

        LuceneIndexerImpl indexer = LuceneIndexerImpl.getUpdateIndexer(rootNodeRef.getStoreRef(), "delta" + System.currentTimeMillis());
        indexer.setNodeService(nodeService);
        indexer.setLuceneIndexLock(luceneIndexLock);
        indexer.setDictionaryService(dictionaryService);
        indexer.setLuceneFullTextSearchIndexer(luceneFTS);

        indexer.deleteNode(new ChildAssocRef(n12, QName.createQName("{namespace}thirteen"), n13));

        indexer.commit();

        Searcher searcher = LuceneSearcherImpl.getSearcher(rootNodeRef.getStoreRef());
        searcher.setNameSpaceService(new MockNameService());
        ResultSet results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "PATH:\"/namespace:one\"", null, null);
        assertEquals(1, results.length());
        results.close();
        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "PATH:\"/namespace:two\"", null, null);
        assertEquals(1, results.length());
        results.close();
        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "PATH:\"/namespace:three\"", null, null);
        assertEquals(1, results.length());
        results.close();
        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "PATH:\"/namespace:four\"", null, null);
        assertEquals(1, results.length());
        results.close();
        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "PATH:\"/namespace:eight-0\"", null, null);
        assertEquals(1, results.length());
        results.close();
        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "PATH:\"/namespace:five\"", null, null);
        assertEquals(0, results.length());
        results.close();
        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "PATH:\"/namespace:one/namespace:one\"", null, null);
        assertEquals(0, results.length());
        results.close();
        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "PATH:\"/namespace:one/namespace:two\"", null, null);
        assertEquals(0, results.length());
        results.close();
        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "PATH:\"/namespace:two/namespace:one\"", null, null);
        assertEquals(0, results.length());
        results.close();
        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "PATH:\"/namespace:two/namespace:two\"", null, null);
        assertEquals(0, results.length());
        results.close();
        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "PATH:\"/namespace:one/namespace:five\"", null, null);
        assertEquals(1, results.length());
        results.close();
        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "PATH:\"/namespace:one/namespace:six\"", null, null);
        assertEquals(1, results.length());
        results.close();
        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "PATH:\"/namespace:two/namespace:seven\"", null, null);
        assertEquals(1, results.length());
        results.close();
        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "PATH:\"/namespace:one/namespace:eight-1\"", null, null);
        assertEquals(1, results.length());
        results.close();
        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "PATH:\"/namespace:two/namespace:eight-2\"", null, null);
        assertEquals(1, results.length());
        results.close();
        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "PATH:\"/namespace:one/namespace:eight-2\"", null, null);
        assertEquals(0, results.length());
        results.close();
        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "PATH:\"/namespace:two/namespace:eight-1\"", null, null);
        assertEquals(0, results.length());
        results.close();
        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "PATH:\"/namespace:two/namespace:eight-0\"", null, null);
        assertEquals(0, results.length());
        results.close();
        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "PATH:\"/namespace:one/namespace:eight-0\"", null, null);
        assertEquals(0, results.length());
        results.close();
        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "PATH:\"/namespace:one/namespace:five/namespace:nine\"", null, null);
        assertEquals(1, results.length());
        results.close();
        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "PATH:\"/namespace:one/namespace:five/namespace:ten\"", null, null);
        assertEquals(1, results.length());
        results.close();
        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "PATH:\"/namespace:one/namespace:five/namespace:eleven\"", null, null);
        assertEquals(1, results.length());
        results.close();
        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "PATH:\"/namespace:one/namespace:five/namespace:twelve\"", null, null);
        assertEquals(1, results.length());
        results.close();
        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "PATH:\"/namespace:one/namespace:five/namespace:twelve/namespace:thirteen\"", null, null);
        assertEquals(0, results.length());
        results.close();
        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "PATH:\"/namespace:one/namespace:five/namespace:twelve/namespace:thirteen/namespace:fourteen\"", null, null);
        assertEquals(0, results.length());
        results.close();
        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "PATH:\"/namespace:*\"", null, null);
        assertEquals(5, results.length());
        results.close();
        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "PATH:\"/namespace:*/namespace:*\"", null, null);
        assertEquals(5, results.length());
        results.close();
        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "PATH:\"/namespace:*/namespace:five\"", null, null);
        assertEquals(1, results.length());
        results.close();
        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "PATH:\"/namespace:*/namespace:*/namespace:*\"", null, null);
        assertEquals(4, results.length());
        results.close();
        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "PATH:\"/namespace:one/namespace:*\"", null, null);
        assertEquals(3, results.length());
        results.close();
        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "PATH:\"/namespace:*/namespace:five/namespace:*\"", null, null);
        assertEquals(4, results.length());
        results.close();
        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "PATH:\"/namespace:one/namespace:*/namespace:nine\"", null, null);
        assertEquals(1, results.length());
        results.close();
        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "PATH:\"/*\"", null, null);
        assertEquals(5, results.length());
        results.close();
        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "PATH:\"/*/*\"", null, null);
        assertEquals(5, results.length());
        results.close();
        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "PATH:\"/*/namespace:five\"", null, null);
        assertEquals(1, results.length());
        results.close();
        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "PATH:\"/*/*/*\"", null, null);
        assertEquals(4, results.length());
        results.close();
        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "PATH:\"/namespace:one/*\"", null, null);
        assertEquals(3, results.length());
        results.close();
        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "PATH:\"/*/namespace:five/*\"", null, null);
        assertEquals(4, results.length());
        results.close();
        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "PATH:\"/namespace:one/*/namespace:nine\"", null, null);
        assertEquals(1, results.length());
        results.close();
        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "PATH:\"//.\"", null, null);
        assertEquals(15, results.length());
        results.close();
        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "PATH:\"//*\"", null, null);
        assertEquals(14, results.length());
        results.close();
        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "PATH:\"//*/.\"", null, null);
        assertEquals(14, results.length());
        results.close();
        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "PATH:\"//*/./.\"", null, null);
        assertEquals(14, results.length());
        results.close();
        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "PATH:\"//./*\"", null, null);
        assertEquals(14, results.length());
        results.close();
        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "PATH:\"//././*/././.\"", null, null);
        assertEquals(14, results.length());
        results.close();
        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "PATH:\"//common\"", null, null);
        assertEquals(0, results.length());
        results.close();
        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "PATH:\"/one//common\"", null, null);
        assertEquals(0, results.length());
        results.close();
        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "PATH:\"/one/five//*\"", null, null);
        assertEquals(4, results.length());
        results.close();
        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "PATH:\"/one/five//.\"", null, null);
        assertEquals(5, results.length());
        results.close();
        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "PATH:\"/one//five/nine\"", null, null);
        assertEquals(1, results.length());
        results.close();
        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "PATH:\"/one//thirteen/fourteen\"", null, null);
        assertEquals(0, results.length());
        results.close();
        luceneFTS.resume();
    }

    public void testDeleteAndAddReference() throws InterruptedException
    {
        luceneFTS.pause();
        buildBaseIndex();
        runBaseTests();

        LuceneIndexerImpl indexer = LuceneIndexerImpl.getUpdateIndexer(rootNodeRef.getStoreRef(), "delta" + System.currentTimeMillis());
        indexer.setNodeService(nodeService);
        indexer.setLuceneIndexLock(luceneIndexLock);
        indexer.setDictionaryService(dictionaryService);
        indexer.setLuceneFullTextSearchIndexer(luceneFTS);

        nodeService.removeChild(n2, n13);
        indexer.deleteChildRelationship(new ChildAssocRef(n2, QName.createQName("{namespace}link"), n13));

        indexer.commit();

        Searcher searcher = LuceneSearcherImpl.getSearcher(rootNodeRef.getStoreRef());
        searcher.setNameSpaceService(new MockNameService());
        ResultSet results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "PATH:\"/namespace:one\"", null, null);
        assertEquals(1, results.length());
        results.close();
        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "PATH:\"/namespace:two\"", null, null);
        assertEquals(1, results.length());
        results.close();
        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "PATH:\"/namespace:three\"", null, null);
        assertEquals(1, results.length());
        results.close();
        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "PATH:\"/namespace:four\"", null, null);
        assertEquals(1, results.length());
        results.close();
        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "PATH:\"/namespace:eight-0\"", null, null);
        assertEquals(1, results.length());
        results.close();
        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "PATH:\"/namespace:five\"", null, null);
        assertEquals(0, results.length());
        results.close();
        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "PATH:\"/namespace:one/namespace:one\"", null, null);
        assertEquals(0, results.length());
        results.close();
        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "PATH:\"/namespace:one/namespace:two\"", null, null);
        assertEquals(0, results.length());
        results.close();
        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "PATH:\"/namespace:two/namespace:one\"", null, null);
        assertEquals(0, results.length());
        results.close();
        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "PATH:\"/namespace:two/namespace:two\"", null, null);
        assertEquals(0, results.length());
        results.close();
        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "PATH:\"/namespace:one/namespace:five\"", null, null);
        assertEquals(1, results.length());
        results.close();
        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "PATH:\"/namespace:one/namespace:six\"", null, null);
        assertEquals(1, results.length());
        results.close();
        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "PATH:\"/namespace:two/namespace:seven\"", null, null);
        assertEquals(1, results.length());
        results.close();
        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "PATH:\"/namespace:one/namespace:eight-1\"", null, null);
        assertEquals(1, results.length());
        results.close();
        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "PATH:\"/namespace:two/namespace:eight-2\"", null, null);
        assertEquals(1, results.length());
        results.close();
        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "PATH:\"/namespace:one/namespace:eight-2\"", null, null);
        assertEquals(0, results.length());
        results.close();
        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "PATH:\"/namespace:two/namespace:eight-1\"", null, null);
        assertEquals(0, results.length());
        results.close();
        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "PATH:\"/namespace:two/namespace:eight-0\"", null, null);
        assertEquals(0, results.length());
        results.close();
        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "PATH:\"/namespace:one/namespace:eight-0\"", null, null);
        assertEquals(0, results.length());
        results.close();
        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "PATH:\"/namespace:one/namespace:five/namespace:nine\"", null, null);
        assertEquals(1, results.length());
        results.close();
        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "PATH:\"/namespace:one/namespace:five/namespace:ten\"", null, null);
        assertEquals(1, results.length());
        results.close();
        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "PATH:\"/namespace:one/namespace:five/namespace:eleven\"", null, null);
        assertEquals(1, results.length());
        results.close();
        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "PATH:\"/namespace:one/namespace:five/namespace:twelve\"", null, null);
        assertEquals(1, results.length());
        results.close();
        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "PATH:\"/namespace:one/namespace:five/namespace:twelve/namespace:thirteen\"", null, null);
        assertEquals(1, results.length());
        results.close();
        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "PATH:\"/namespace:one/namespace:five/namespace:twelve/namespace:thirteen/namespace:fourteen\"", null, null);
        assertEquals(1, results.length());
        results.close();
        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "PATH:\"/namespace:*\"", null, null);
        assertEquals(5, results.length());
        results.close();
        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "PATH:\"/namespace:*/namespace:*\"", null, null);
        assertEquals(7, results.length());
        results.close();
        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "PATH:\"/namespace:*/namespace:five\"", null, null);
        assertEquals(1, results.length());
        results.close();
        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "PATH:\"/namespace:*/namespace:*/namespace:*\"", null, null);
        assertEquals(6, results.length());
        results.close();
        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "PATH:\"/namespace:one/namespace:*\"", null, null);
        assertEquals(4, results.length());
        results.close();
        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "PATH:\"/namespace:*/namespace:five/namespace:*\"", null, null);
        assertEquals(5, results.length());
        results.close();
        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "PATH:\"/namespace:one/namespace:*/namespace:nine\"", null, null);
        assertEquals(1, results.length());
        results.close();
        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "PATH:\"/*\"", null, null);
        assertEquals(5, results.length());
        results.close();
        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "PATH:\"/*/*\"", null, null);
        assertEquals(7, results.length());
        results.close();
        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "PATH:\"/*/namespace:five\"", null, null);
        assertEquals(1, results.length());
        results.close();
        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "PATH:\"/*/*/*\"", null, null);
        assertEquals(6, results.length());
        results.close();
        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "PATH:\"/namespace:one/*\"", null, null);
        assertEquals(4, results.length());
        results.close();
        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "PATH:\"/*/namespace:five/*\"", null, null);
        assertEquals(5, results.length());
        results.close();
        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "PATH:\"/namespace:one/*/namespace:nine\"", null, null);
        assertEquals(1, results.length());
        results.close();
        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "PATH:\"//.\"", null, null);
        assertEquals(23, results.length());
        results.close();
        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "PATH:\"//*\"", null, null);
        assertEquals(22, results.length());
        results.close();
        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "PATH:\"//*/.\"", null, null);
        assertEquals(22, results.length());
        results.close();
        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "PATH:\"//*/./.\"", null, null);
        assertEquals(22, results.length());
        results.close();
        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "PATH:\"//./*\"", null, null);
        assertEquals(22, results.length());
        results.close();
        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "PATH:\"//././*/././.\"", null, null);
        assertEquals(22, results.length());
        results.close();
        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "PATH:\"//common\"", null, null);
        assertEquals(6, results.length());
        results.close();
        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "PATH:\"/one//common\"", null, null);
        assertEquals(5, results.length());
        results.close();
        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "PATH:\"/one/five//*\"", null, null);
        assertEquals(9, results.length());
        results.close();
        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "PATH:\"/one/five//.\"", null, null);
        assertEquals(10, results.length());
        results.close();
        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "PATH:\"/one//five/nine\"", null, null);
        assertEquals(1, results.length());
        results.close();
        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "PATH:\"/one//thirteen/fourteen\"", null, null);
        assertEquals(1, results.length());
        results.close();

        indexer = LuceneIndexerImpl.getUpdateIndexer(rootNodeRef.getStoreRef(), "delta" + System.currentTimeMillis());
        indexer.setNodeService(nodeService);
        indexer.setLuceneIndexLock(luceneIndexLock);
        indexer.setDictionaryService(dictionaryService);
        indexer.setLuceneFullTextSearchIndexer(luceneFTS);

        nodeService.addChild(n2, n13, QName.createQName("{namespace}link"));
        indexer.createChildRelationship(new ChildAssocRef(n2, QName.createQName("{namespace}link"), n13));

        indexer.commit();

        runBaseTests(); 
        luceneFTS.resume();
    }

    public void testRenameReference() throws InterruptedException
    {
        luceneFTS.pause();
        buildBaseIndex();
        runBaseTests();

        Searcher searcher = LuceneSearcherImpl.getSearcher(rootNodeRef.getStoreRef());
        searcher.setNameSpaceService(new MockNameService());

        ResultSet results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "PATH:\"//namespace:link//.\"", null, null);
        assertEquals(3, results.length());
        results.close();

        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "PATH:\"//namespace:renamed_link//.\"", null, null);
        assertEquals(0, results.length());
        results.close();

        LuceneIndexerImpl indexer = LuceneIndexerImpl.getUpdateIndexer(rootNodeRef.getStoreRef(), "delta" + System.currentTimeMillis());
        indexer.setNodeService(nodeService);
        indexer.setLuceneIndexLock(luceneIndexLock);
        indexer.setDictionaryService(dictionaryService);
        indexer.setLuceneFullTextSearchIndexer(luceneFTS);

        nodeService.removeChild(n2, n13);
        nodeService.addChild(n2, n13, QName.createQName("{namespace}renamed_link"));

        indexer.updateChildRelationship(new ChildAssocRef(n2, QName.createQName("namespace", "link"), n13), new ChildAssocRef(n2, QName.createQName("namespace", "renamed_link"),
                n13));

        indexer.commit();

        runBaseTests();

        searcher = LuceneSearcherImpl.getSearcher(rootNodeRef.getStoreRef());
        searcher.setNameSpaceService(new MockNameService());

        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "PATH:\"//namespace:link//.\"", null, null);
        assertEquals(0, results.length());
        results.close();

        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "PATH:\"//namespace:renamed_link//.\"", null, null);
        assertEquals(3, results.length());
        results.close();
        luceneFTS.resume();
    }

    public void testDelayIndex() throws InterruptedException
    {
        luceneFTS.pause();
        buildBaseIndex();
        runBaseTests();

        Searcher searcher = LuceneSearcherImpl.getSearcher(rootNodeRef.getStoreRef());
        searcher.setNameSpaceService(new MockNameService());

        ResultSet results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "\\@" + escapeQName(QName.createQName(NamespaceService.ALFRESCO_URI, "text-indexed-stored-tokenised-atomic"))
                + ":\"KEYONE\"", null, null);
        assertEquals(1, results.length());
        results.close();

        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "\\@" + escapeQName(QName.createQName(NamespaceService.ALFRESCO_URI, "text-indexed-stored-tokenised-nonatomic"))
                + ":\"KEYTWO\"", null, null);
        assertEquals(0, results.length());
        results.close();

        // Do index

        LuceneIndexerImpl indexer = LuceneIndexerImpl.getUpdateIndexer(rootNodeRef.getStoreRef(), "delta" + System.currentTimeMillis() + "_" + (new Random().nextInt()));
        indexer.setNodeService(nodeService);
        indexer.setLuceneIndexLock(luceneIndexLock);
        indexer.setDictionaryService(dictionaryService);
        indexer.setLuceneFullTextSearchIndexer(luceneFTS);
        indexer.updateFullTextSearch(1000);
        indexer.prepare();
        indexer.commit();

        searcher = LuceneSearcherImpl.getSearcher(rootNodeRef.getStoreRef());
        searcher.setNameSpaceService(new MockNameService());

        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "\\@" + escapeQName(QName.createQName(NamespaceService.ALFRESCO_URI, "text-indexed-stored-tokenised-atomic"))
                + ":\"keyone\"", null, null);
        assertEquals(1, results.length());
        results.close();

        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "\\@" + escapeQName(QName.createQName(NamespaceService.ALFRESCO_URI, "text-indexed-stored-tokenised-nonatomic"))
                + ":\"keytwo\"", null, null);
        assertEquals(1, results.length());
        results.close();
        
        runBaseTests();
        luceneFTS.resume();
    }
    
    
    public void testWaitForIndex() throws InterruptedException
    {
        luceneFTS.pause();
        buildBaseIndex();
        runBaseTests();

        Searcher searcher = LuceneSearcherImpl.getSearcher(rootNodeRef.getStoreRef());
        searcher.setNameSpaceService(new MockNameService());

        ResultSet results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "\\@" + escapeQName(QName.createQName(NamespaceService.ALFRESCO_URI, "text-indexed-stored-tokenised-atomic"))
                + ":\"KEYONE\"", null, null);
        assertEquals(1, results.length());
        results.close();

        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "\\@" + escapeQName(QName.createQName(NamespaceService.ALFRESCO_URI, "text-indexed-stored-tokenised-nonatomic"))
                + ":\"KEYTWO\"", null, null);
        assertEquals(0, results.length());
        results.close();

        // Do index

        searcher = LuceneSearcherImpl.getSearcher(rootNodeRef.getStoreRef());
        searcher.setNameSpaceService(new MockNameService());

        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "\\@" + escapeQName(QName.createQName(NamespaceService.ALFRESCO_URI, "text-indexed-stored-tokenised-atomic"))
                + ":\"keyone\"", null, null);
        assertEquals(1, results.length());
        results.close();

        luceneFTS.resume();
        
        Thread.sleep(20000);
        
        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "\\@" + escapeQName(QName.createQName(NamespaceService.ALFRESCO_URI, "text-indexed-stored-tokenised-nonatomic"))
                + ":\"keytwo\"", null, null);
        assertEquals(1, results.length());
        results.close();
        
        runBaseTests();
    }

    private String escapeQName(QName qName)
    {
        StringBuffer buffer = new StringBuffer();
        String string = qName.toString();
        for (int i = 0; i < string.length(); i++)
        {
            char c = string.charAt(i);
            if ((c == '{') || (c == '}') || (c == '-') || (c == ':'))
            {
                buffer.append('\\');
            }
            
            buffer.append(c);
        }
        return buffer.toString();
    }

    public void testForKev() throws InterruptedException
    {
        luceneFTS.pause();
        buildBaseIndex();
        runBaseTests();

        Searcher searcher = LuceneSearcherImpl.getSearcher(rootNodeRef.getStoreRef());
        searcher.setNameSpaceService(new MockNameService());

        ResultSet results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "PARENT:\"" + rootNodeRef.getId() + "\"", null, null);
        assertEquals(5, results.length());
        results.close();

        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "+PARENT:\"" + rootNodeRef.getId() + "\" +QNAME:\"one\"", null, null);
        assertEquals(1, results.length());
        results.close();
        luceneFTS.resume();
    }

    public void testPerf1() throws InterruptedException
    {
        luceneFTS.pause();
        System.out.println("One minute");
        runPerformanceTest(10000, true);
        luceneFTS.resume();
    }

    public void testPerf2() throws InterruptedException
    {
        luceneFTS.pause();
        System.out.println("One minute");
        runPerformanceTest(10000, false);
        luceneFTS.resume();
    }

    public void testPerf3() throws InterruptedException
    {
        luceneFTS.pause();
        System.out.println("One minute");
        runPerformanceTest(10000, false);
        luceneFTS.resume();
    }

    public void testPerf4() throws InterruptedException
    {
        luceneFTS.pause();
        System.out.println("One minute");
        runPerformanceTest(10000, false);
        luceneFTS.resume();
    }

    public void testPerf5() throws InterruptedException
    {
        luceneFTS.pause();
        System.out.println("One minute");
        runPerformanceTest(10000, false);
        luceneFTS.resume();
    }

    public void testPerf6() throws InterruptedException
    {
        luceneFTS.pause();
        System.out.println("One minute");
        runPerformanceTest(10000, false);
        luceneFTS.resume();
    }

    public void testPerf7() throws InterruptedException
    {
        luceneFTS.pause();
        System.out.println("One minute");
        runPerformanceTest(10000, false);
        luceneFTS.resume();
    }

    public void testPerf8() throws InterruptedException
    {
        luceneFTS.pause();
        System.out.println("One minute");
        runPerformanceTest(10000, false);
        luceneFTS.resume();
    }

    public void testPerf9() throws InterruptedException
    {
        luceneFTS.pause();
        System.out.println("One minute");
        runPerformanceTest(10000, false);
        luceneFTS.resume();
    }

    public void testPerf10() throws InterruptedException
    {
        luceneFTS.pause();
        System.out.println("One minute");
        runPerformanceTest(10000, false);
        luceneFTS.resume();
    }

    private void runPerformanceTest(double time, boolean clear)
    {
        LuceneIndexerImpl indexer = LuceneIndexerImpl.getUpdateIndexer(rootNodeRef.getStoreRef(), "delta" + System.currentTimeMillis() + "_" + (new Random().nextInt()));
        indexer.setNodeService(nodeService);
        indexer.setLuceneIndexLock(luceneIndexLock);
        indexer.setDictionaryService(dictionaryService);
        indexer.setLuceneFullTextSearchIndexer(luceneFTS);
        if (clear)
        {
            indexer.clearIndex();
        }
        indexer.createNode(new ChildAssocRef(null, null, rootNodeRef));

        long startTime = System.currentTimeMillis();
        int count = 0;
        for (int i = 0; i < 10000000; i++)
        {
            if (i % 10 == 0)
            {
                if (System.currentTimeMillis() - startTime > time)
                {
                    count = i;
                    break;
                }
            }

            QName qname = QName.createQName("{namespace}a_" + i);
            NodeRef ref = nodeService.createNode(rootNodeRef, qname, DictionaryBootstrap.TYPE_CONTAINER).getChildRef();
            indexer.createNode(new ChildAssocRef(rootNodeRef, qname, ref));

        }
        indexer.commit();
        float delta = ((System.currentTimeMillis() - startTime) / 1000.0f);
        System.out.println("\tCreated " + count + " in " + delta + "   = " + (count / delta));
    }

    public static class MockNameService implements NamespaceService
    {
        private static HashMap<String, String> map = new HashMap<String, String>();

        static
        {
            map.put(NamespaceService.ALFRESCO_PREFIX, NamespaceService.ALFRESCO_URI);
            map.put(NamespaceService.ALFRESCO_TEST_PREFIX, NamespaceService.ALFRESCO_TEST_URI);
            map.put(NamespaceService.DEFAULT_PREFIX, "namespace");
            map.put("namespace", "namespace");

        }

        public Collection<String> getURIs()
        {
            return map.values();
        }

        public Collection<String> getPrefixes()
        {
            return map.keySet();
        }

        public String getNamespaceURI(String prefix) throws NamespaceException
        {
            String ns = map.get(prefix);
            if (ns == null)
            {
                return prefix;
            }
            else
            {
                return ns;
            }
        }

        public Collection<String> getPrefixes(String namespaceURI) throws NamespaceException
        {
            HashSet<String> answer = new HashSet<String>();
            for (String prefix : map.keySet())
            {
                String test = map.get(prefix);
                if (test.equals(namespaceURI))
                {
                    answer.add(prefix);
                }
            }
            return answer;
        }

    }

    public static void main(String[] args) throws Exception
    {
        // String guid = GUID.generate();
        // System.out.println("GUID is " + guid + " length is " +
        // guid.length());
        LuceneTest test = new LuceneTest();
        test.setUp();
        test.testWaitForIndex();
        // test.testStandAloneIndexerCommit();
    }
}
