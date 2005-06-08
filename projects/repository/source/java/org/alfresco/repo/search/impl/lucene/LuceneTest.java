package org.alfresco.repo.search.impl.lucene;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import junit.framework.TestCase;

import org.alfresco.repo.content.ContentService;
import org.alfresco.repo.content.ContentWriter;
import org.alfresco.repo.dictionary.DictionaryService;
import org.alfresco.repo.dictionary.NamespaceService;
import org.alfresco.repo.dictionary.PropertyTypeDefinition;
import org.alfresco.repo.dictionary.impl.DictionaryBootstrap;
import org.alfresco.repo.dictionary.impl.DictionaryDAO;
import org.alfresco.repo.dictionary.impl.M2Aspect;
import org.alfresco.repo.dictionary.impl.M2Model;
import org.alfresco.repo.dictionary.impl.M2Property;
import org.alfresco.repo.dictionary.impl.M2Type;
import org.alfresco.repo.node.NodeService;
import org.alfresco.repo.ref.ChildAssocRef;
import org.alfresco.repo.ref.DynamicNamespacePrefixResolver;
import org.alfresco.repo.ref.NamespacePrefixResolver;
import org.alfresco.repo.ref.NodeRef;
import org.alfresco.repo.ref.QName;
import org.alfresco.repo.ref.StoreRef;
import org.alfresco.repo.search.QueryParameter;
import org.alfresco.repo.search.QueryParameterDefImpl;
import org.alfresco.repo.search.QueryParameterDefinition;
import org.alfresco.repo.search.QueryRegisterComponent;
import org.alfresco.repo.search.ResultSet;
import org.alfresco.repo.search.impl.lucene.fts.FullTextSearchIndexer;
import org.alfresco.repo.search.transaction.LuceneIndexLock;
import org.alfresco.util.CachingDateFormat;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author andyh
 * 
 */
public class LuceneTest extends TestCase
{

    static ApplicationContext ctx = new ClassPathXmlApplicationContext("classpath:applicationContext.xml");

    NodeService nodeService;
    DictionaryService dictionaryService;
    LuceneIndexLock luceneIndexLock;
    private NodeRef rootNodeRef;
    private NodeRef n1;
    private NodeRef n2;
    private NodeRef n3;
    private NodeRef n4;
    private NodeRef n5;
    private NodeRef n6;
    private NodeRef n7;
    private NodeRef n8;
    private NodeRef n9;
    private NodeRef n10;
    private NodeRef n11;
    private NodeRef n12;
    private NodeRef n13;
    private NodeRef n14;
    private DictionaryDAO dictionaryDAO;
    private QName testType;
    private FullTextSearchIndexer luceneFTS;
    private String TEST_NAMESPACE = "http://www.alfresco.org/test/lucenetest";
    private QName assocTypeQName = QName.createQName(TEST_NAMESPACE, "contains");
    private QName testSuperType;
    private M2Type testTypeSuperType;
    private QName testAspect;
    private QName testSuperAspect;
    private M2Aspect testAspectSuperAspect;
    private ContentService contentService;
    private QueryRegisterComponent queryRegisterComponent;
    private NamespacePrefixResolver namespacePrefixResolver;
    private LuceneIndexerAndSearcher indexerAndSearcher;

    public LuceneTest()
    {
        super();
    }

    public void setUp() throws IOException
    {

        nodeService = (NodeService) ctx.getBean("dbNodeService");
        luceneIndexLock = (LuceneIndexLock) ctx.getBean("luceneIndexLock");
        dictionaryService = (DictionaryService) ctx.getBean("dictionaryService");
        dictionaryDAO = (DictionaryDAO) ctx.getBean("dictionaryDAO");
        luceneFTS = (FullTextSearchIndexer) ctx.getBean("LuceneFullTextSearchIndexer");
        contentService = (ContentService) ctx.getBean("contentService");
        queryRegisterComponent = (QueryRegisterComponent) ctx.getBean("queryRegisterComponent");
        namespacePrefixResolver = (NamespacePrefixResolver) ctx.getBean("namespaceService");
        indexerAndSearcher = (LuceneIndexerAndSearcher) ctx.getBean("luceneIndexerAndSearcherFactory");
        
        queryRegisterComponent.loadQueryCollection("testQueryRegister.xml");

        assertEquals(true, ctx.isSingleton("luceneIndexLock"));
        assertEquals(true, ctx.isSingleton("LuceneFullTextSearchIndexer"));

        createTestTypes();

        StoreRef storeRef = nodeService.createStore(StoreRef.PROTOCOL_WORKSPACE, "Test_" + System.currentTimeMillis());
        rootNodeRef = nodeService.getRootNode(storeRef);

        n1 = nodeService.createNode(rootNodeRef, null, QName.createQName("{namespace}one"), DictionaryBootstrap.TYPE_QNAME_CONTAINER).getChildRef();
        nodeService.setProperty(n1, QName.createQName("{namespace}property-1"), "value-1");
        n2 = nodeService.createNode(rootNodeRef, null, QName.createQName("{namespace}two"), DictionaryBootstrap.TYPE_QNAME_CONTAINER).getChildRef();
        nodeService.setProperty(n2, QName.createQName("{namespace}property-1"), "value-1");
        nodeService.setProperty(n2, QName.createQName("{namespace}property-2"), "value-2");

        n3 = nodeService.createNode(rootNodeRef, null, QName.createQName("{namespace}three"), DictionaryBootstrap.TYPE_QNAME_CONTAINER).getChildRef();

        ObjectOutputStream oos;
        try
        {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            oos = new ObjectOutputStream(baos);
            oos.writeObject(n3);
            oos.close();

            ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(baos.toByteArray()));
            Object o = ois.readObject();
            ois.close();
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (ClassNotFoundException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        Map<QName, Serializable> testProperties = new HashMap<QName, Serializable>();
        testProperties.put(QName.createQName(TEST_NAMESPACE, "text-indexed-stored-tokenised-atomic"), "TEXT THAT IS INDEXED STORED AND TOKENISED ATOMICALLY KEYONE");
        testProperties.put(QName.createQName(TEST_NAMESPACE, "text-indexed-stored-tokenised-nonatomic"),
                "TEXT THAT IS INDEXED STORED AND TOKENISED BUT NOT ATOMICALLY KEYTWO");
        testProperties.put(QName.createQName(TEST_NAMESPACE, "int-ista"), Integer.valueOf(1));
        testProperties.put(QName.createQName(TEST_NAMESPACE, "long-ista"), Long.valueOf(2));
        testProperties.put(QName.createQName(TEST_NAMESPACE, "float-ista"), Float.valueOf(3.4f));
        testProperties.put(QName.createQName(TEST_NAMESPACE, "double-ista"), Double.valueOf(5.6));
        testProperties.put(QName.createQName(TEST_NAMESPACE, "date-ista"), new Date());
        testProperties.put(QName.createQName(TEST_NAMESPACE, "datetime-ista"), new Date());
        testProperties.put(QName.createQName(TEST_NAMESPACE, "boolean-ista"), Boolean.valueOf(true));
        testProperties.put(QName.createQName(TEST_NAMESPACE, "qname-ista"), QName.createQName("{wibble}wobble"));
        testProperties.put(QName.createQName(TEST_NAMESPACE, "guid-ista"), "My-GUID");
        testProperties.put(QName.createQName(TEST_NAMESPACE, "category-ista"), "CategoryId");
        testProperties.put(QName.createQName(TEST_NAMESPACE, "noderef-ista"), n1);
        testProperties.put(QName.createQName(TEST_NAMESPACE, "path-ista"), nodeService.getPath(n3));

        n4 = nodeService.createNode(rootNodeRef, null, QName.createQName("{namespace}four"), testType, testProperties).getChildRef();

        nodeService.getProperties(n1);
        nodeService.getProperties(n2);
        nodeService.getProperties(n3);
        nodeService.getProperties(n4);

        n3 = nodeService.createNode(rootNodeRef, null, QName.createQName("{namespace}three"), DictionaryBootstrap.TYPE_QNAME_CONTAINER).getChildRef();

        n5 = nodeService.createNode(n1, null, QName.createQName("{namespace}five"), DictionaryBootstrap.TYPE_QNAME_CONTAINER).getChildRef();
        n6 = nodeService.createNode(n1, null, QName.createQName("{namespace}six"), DictionaryBootstrap.TYPE_QNAME_CONTAINER).getChildRef();
        n7 = nodeService.createNode(n2, null, QName.createQName("{namespace}seven"), DictionaryBootstrap.TYPE_QNAME_CONTAINER).getChildRef();
        n8 = nodeService.createNode(n2, null, QName.createQName("{namespace}eight-2"), DictionaryBootstrap.TYPE_QNAME_CONTAINER).getChildRef();
        n9 = nodeService.createNode(n5, null, QName.createQName("{namespace}nine"), DictionaryBootstrap.TYPE_QNAME_CONTAINER).getChildRef();
        n10 = nodeService.createNode(n5, null, QName.createQName("{namespace}ten"), DictionaryBootstrap.TYPE_QNAME_CONTAINER).getChildRef();
        n11 = nodeService.createNode(n5, null, QName.createQName("{namespace}eleven"), DictionaryBootstrap.TYPE_QNAME_CONTAINER).getChildRef();
        n12 = nodeService.createNode(n5, null, QName.createQName("{namespace}twelve"), DictionaryBootstrap.TYPE_QNAME_CONTAINER).getChildRef();
        n13 = nodeService.createNode(n12, null, QName.createQName("{namespace}thirteen"), DictionaryBootstrap.TYPE_QNAME_CONTAINER).getChildRef();
       

        Map<QName, Serializable> properties = new HashMap<QName, Serializable>();
        properties.put(DictionaryBootstrap.PROP_QNAME_MIME_TYPE, "text/plain");
        // properties.put(DictionaryBootstrap.PROP_QNAME_MIME_TYPE,
        // "application/msword");
        properties.put(DictionaryBootstrap.PROP_QNAME_ENCODING, "UTF-16");
        n14 = nodeService.createNode(n13, null, QName.createQName("{namespace}fourteen"), DictionaryBootstrap.TYPE_QNAME_CONTENT, properties).getChildRef();
        //nodeService.addAspect(n14, DictionaryBootstrap.ASPECT_QNAME_CONTENT, properties);

        ContentWriter writer = contentService.getUpdatingWriter(n14);
        // InputStream is =
        // this.getClass().getClassLoader().getResourceAsStream("test.doc");
        // writer.putContent(is);
        writer.putContent("The quick brown fox jumped over the lazy dog");

        nodeService.addChild(rootNodeRef, n8, assocTypeQName, QName.createQName("{namespace}eight-0"));
        nodeService.addChild(n1, n8, assocTypeQName, QName.createQName("{namespace}eight-1"));
        nodeService.addChild(n2, n13, assocTypeQName, QName.createQName("{namespace}link"));

        nodeService.addChild(n1, n14, assocTypeQName, QName.createQName("{namespace}common"));
        nodeService.addChild(n2, n14, assocTypeQName, QName.createQName("{namespace}common"));
        nodeService.addChild(n5, n14, assocTypeQName, QName.createQName("{namespace}common"));
        nodeService.addChild(n6, n14, assocTypeQName, QName.createQName("{namespace}common"));
        nodeService.addChild(n12, n14, assocTypeQName, QName.createQName("{namespace}common"));
        nodeService.addChild(n13, n14, assocTypeQName, QName.createQName("{namespace}common"));
    }

    private void createTestTypes()
    {
        M2Model model = M2Model.createModel("test:lucenetest");
        model.createImport(NamespaceService.ALFRESCO_DICTIONARY_URI, "d");
        model.createNamespace(TEST_NAMESPACE, "test");
        
        testType = QName.createQName(TEST_NAMESPACE, "testType");
        testSuperType = QName.createQName(TEST_NAMESPACE, "testSuperType");
        testTypeSuperType = model.createType("test:" + testSuperType.getLocalName());

        testAspect = QName.createQName(TEST_NAMESPACE, "testAspect");
        testSuperAspect = QName.createQName(TEST_NAMESPACE, "testSuperAspect");
        testAspectSuperAspect = model.createAspect("test:" + testSuperAspect.getLocalName());
        M2Aspect testAspectAspect = model.createAspect("test:" + testAspect.getLocalName());
        testAspectAspect.setParentName("test:" + testSuperAspect.getLocalName());

        M2Type testTypeType = model.createType("test:" + testType.getLocalName());
        testTypeType.setParentName("test:" + testSuperType.getLocalName());
        testTypeType.addMandatoryAspect("test:" + testAspect.getLocalName());
        M2Property text_indexed_stored_tokenised_atomic = testTypeType.createProperty("test:text-indexed-stored-tokenised-atomic");
        text_indexed_stored_tokenised_atomic.setType("d:" + PropertyTypeDefinition.TEXT.getLocalName());
        text_indexed_stored_tokenised_atomic.setMandatory(true);
        text_indexed_stored_tokenised_atomic.setMultiValued(false);
        text_indexed_stored_tokenised_atomic.setIndexed(true);
        text_indexed_stored_tokenised_atomic.setIndexedAtomically(true);
        text_indexed_stored_tokenised_atomic.setStoredInIndex(true);
        text_indexed_stored_tokenised_atomic.setTokenisedInIndex(true);

        M2Property text_indexed_stored_tokenised_nonatomic = testTypeType.createProperty("test:text-indexed-stored-tokenised-nonatomic");
        text_indexed_stored_tokenised_nonatomic.setType("d:" + PropertyTypeDefinition.TEXT.getLocalName());
        text_indexed_stored_tokenised_nonatomic.setMandatory(true);
        text_indexed_stored_tokenised_nonatomic.setMultiValued(false);
        text_indexed_stored_tokenised_nonatomic.setIndexed(true);
        text_indexed_stored_tokenised_nonatomic.setIndexedAtomically(false);
        text_indexed_stored_tokenised_nonatomic.setStoredInIndex(true);
        text_indexed_stored_tokenised_nonatomic.setTokenisedInIndex(true);

        M2Property int_ista = testTypeType.createProperty("test:int-ista");
        int_ista.setType("d:" + PropertyTypeDefinition.INT.getLocalName());
        int_ista.setMandatory(true);
        int_ista.setMultiValued(false);
        int_ista.setIndexed(true);
        int_ista.setIndexedAtomically(true);
        int_ista.setStoredInIndex(true);
        int_ista.setTokenisedInIndex(true);

        M2Property long_ista = testTypeType.createProperty("test:long-ista");        
        long_ista.setType("d:" + PropertyTypeDefinition.LONG.getLocalName());
        long_ista.setMandatory(true);
        long_ista.setMultiValued(false);
        long_ista.setIndexed(true);
        long_ista.setIndexedAtomically(true);
        long_ista.setStoredInIndex(true);
        long_ista.setTokenisedInIndex(true);

        M2Property float_ista = testTypeType.createProperty("test:float-ista");
        float_ista.setType("d:" + PropertyTypeDefinition.FLOAT.getLocalName());
        float_ista.setMandatory(true);
        float_ista.setMultiValued(false);
        float_ista.setIndexed(true);
        float_ista.setIndexedAtomically(true);
        float_ista.setStoredInIndex(true);
        float_ista.setTokenisedInIndex(true);

        M2Property double_ista = testTypeType.createProperty("test:double-ista");
        double_ista.setType("d:" + PropertyTypeDefinition.DOUBLE.getLocalName());
        double_ista.setMandatory(true);
        double_ista.setMultiValued(false);
        double_ista.setIndexed(true);
        double_ista.setIndexedAtomically(true);
        double_ista.setStoredInIndex(true);
        double_ista.setTokenisedInIndex(true);

        M2Property date_ista = testTypeType.createProperty("test:date-ista");
        date_ista.setType("d:" + PropertyTypeDefinition.DATE.getLocalName());
        date_ista.setMandatory(true);
        date_ista.setMultiValued(false);
        date_ista.setIndexed(true);
        date_ista.setIndexedAtomically(true);
        date_ista.setStoredInIndex(true);
        date_ista.setTokenisedInIndex(true);

        M2Property datetime_ista = testTypeType.createProperty("test:datetime-ista");
        datetime_ista.setType("d:" + PropertyTypeDefinition.DATETIME.getLocalName());
        datetime_ista.setMandatory(true);
        datetime_ista.setMultiValued(false);
        datetime_ista.setIndexed(true);
        datetime_ista.setIndexedAtomically(true);
        datetime_ista.setStoredInIndex(true);
        datetime_ista.setTokenisedInIndex(true);

        M2Property boolean_ista = testTypeType.createProperty("test:boolean-ista");
        boolean_ista.setType("d:" + PropertyTypeDefinition.BOOLEAN.getLocalName());
        boolean_ista.setMandatory(true);
        boolean_ista.setMultiValued(false);
        boolean_ista.setIndexed(true);
        boolean_ista.setIndexedAtomically(true);
        boolean_ista.setStoredInIndex(true);
        boolean_ista.setTokenisedInIndex(true);

        M2Property qname_ista = testTypeType.createProperty("test:qname-ista");
        qname_ista.setType("d:" + PropertyTypeDefinition.QNAME.getLocalName());
        qname_ista.setMandatory(true);
        qname_ista.setMultiValued(false);
        qname_ista.setIndexed(true);
        qname_ista.setIndexedAtomically(true);
        qname_ista.setStoredInIndex(true);
        qname_ista.setTokenisedInIndex(true);

        M2Property guid_ista = testTypeType.createProperty("test:guid-ista");
        guid_ista.setType("d:" + PropertyTypeDefinition.GUID.getLocalName());
        guid_ista.setMandatory(true);
        guid_ista.setMultiValued(false);
        guid_ista.setIndexed(true);
        guid_ista.setIndexedAtomically(true);
        guid_ista.setStoredInIndex(true);
        guid_ista.setTokenisedInIndex(true);

        M2Property category_ista = testTypeType.createProperty("test:category-ista");
        category_ista.setType("d:" + PropertyTypeDefinition.CATEGORY.getLocalName());
        category_ista.setMandatory(true);
        category_ista.setMultiValued(false);
        category_ista.setIndexed(true);
        category_ista.setIndexedAtomically(true);
        category_ista.setStoredInIndex(true);
        category_ista.setTokenisedInIndex(true);

        M2Property noderef_ista = testTypeType.createProperty("test:noderef-ista");
        noderef_ista.setType("d:" + PropertyTypeDefinition.NODE_REF.getLocalName());
        noderef_ista.setMandatory(true);
        noderef_ista.setMultiValued(false);
        noderef_ista.setIndexed(true);
        noderef_ista.setIndexedAtomically(true);
        noderef_ista.setStoredInIndex(true);
        noderef_ista.setTokenisedInIndex(true);

        // M2Property path_ista = testTypeType.createProperty("path-ista");
        // path_ista.setType(metaModelDAO.getPropertyType(PropertyTypeDefinition.PATH));
        // path_ista.setMandatory(true);
        // path_ista.setMultiValued(false);
        // path_ista.setIndexed(true);
        // path_ista.setIndexedAtomically(true);
        // path_ista.setStoredInIndex(true);
        // path_ista.setTokenisedInIndex(true);
    
        dictionaryDAO.putModel(model);
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

        LuceneSearcherImpl searcher = LuceneSearcherImpl.getSearcher(rootNodeRef.getStoreRef(), indexerAndSearcher.getIndexLocation());
        searcher.setDictionaryService(dictionaryService);

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
        LuceneIndexerImpl indexer = LuceneIndexerImpl.getUpdateIndexer(rootNodeRef.getStoreRef(), "delta" + System.currentTimeMillis() + "_1", indexerAndSearcher.getIndexLocation());

        indexer.setNodeService(nodeService);
        indexer.setLuceneIndexLock(luceneIndexLock);
        indexer.setDictionaryService(dictionaryService);
        indexer.setLuceneFullTextSearchIndexer(luceneFTS);
        indexer.setContentService(contentService);

        indexer.prepare();
        indexer.commit();
        luceneFTS.resume();
    }

    /**
     * Test basic index and search
     * 
     * @throws InterruptedException
     * 
     */

    public void testStandAloneIndexerCommit() throws InterruptedException
    {

        luceneFTS.pause();
        LuceneIndexerImpl indexer = LuceneIndexerImpl.getUpdateIndexer(rootNodeRef.getStoreRef(), "delta" + System.currentTimeMillis() + "_1", indexerAndSearcher.getIndexLocation());

        indexer.setNodeService(nodeService);
        indexer.setLuceneIndexLock(luceneIndexLock);
        indexer.setDictionaryService(dictionaryService);
        indexer.setLuceneFullTextSearchIndexer(luceneFTS);
        indexer.setContentService(contentService);

        indexer.clearIndex();

        indexer.createNode(new ChildAssocRef(null, null, null, rootNodeRef));
        indexer.createNode(new ChildAssocRef(assocTypeQName, rootNodeRef, QName.createQName("{namespace}one"), n1));
        indexer.createNode(new ChildAssocRef(assocTypeQName, rootNodeRef, QName.createQName("{namespace}two"), n2));
        indexer.updateNode(n1);
        // indexer.deleteNode(new ChildRelationshipRef(rootNode, "path",
        // newNode));

        indexer.prepare();
        indexer.commit();

        LuceneSearcherImpl searcher = LuceneSearcherImpl.getSearcher(rootNodeRef.getStoreRef(), indexerAndSearcher.getIndexLocation());
        searcher.setNodeService(nodeService);
        searcher.setDictionaryService(dictionaryService);
        searcher.setNamespacePrefixResolver(getNamespacePrefixReolsver("namespace"));

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

        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "\\@namespace\\:property\\-1:\"value-1\"", null, null);
        assertEquals(2, results.length());
        assertEquals(n2.getId(), results.getNodeRef(0).getId());
        assertEquals(n1.getId(), results.getNodeRef(1).getId());
        assertEquals(1.0f, results.getScore(0));
        assertEquals(1.0f, results.getScore(1));
        results.close();

        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "\\@property\\-1:\"value-1\"", null, null);
        assertEquals(2, results.length());
        assertEquals(n2.getId(), results.getNodeRef(0).getId());
        assertEquals(n1.getId(), results.getNodeRef(1).getId());
        assertEquals(1.0f, results.getScore(0));
        assertEquals(1.0f, results.getScore(1));
        results.close();

        QName qname = QName.createQName("", "property-1");

        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "ID:\"" + n1.getId() + "\"", null, null);

        assertEquals(2, results.length());

        results.close();
        luceneFTS.resume();

    }

    public void testStandAlonePathIndexer() throws InterruptedException
    {
        luceneFTS.pause();
        buildBaseIndex();

        LuceneSearcherImpl searcher = LuceneSearcherImpl.getSearcher(rootNodeRef.getStoreRef(), indexerAndSearcher.getIndexLocation());
        searcher.setNodeService(nodeService);
        searcher.setDictionaryService(dictionaryService);

        ResultSet results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "@\\{namespace\\}property-1:value-1", null, null);
        try
        {
            assertEquals(2, results.length());
            assertEquals(n1.getId(), results.getNodeRef(0).getId());
            assertEquals(n2.getId(), results.getNodeRef(1).getId());
            assertEquals(1.0f, results.getScore(0));
            assertEquals(1.0f, results.getScore(1));

            QName qname = QName.createQName("", "property-1");

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
        LuceneIndexerImpl indexer = LuceneIndexerImpl.getUpdateIndexer(rootNodeRef.getStoreRef(), "delta" + System.currentTimeMillis() + "_" + (new Random().nextInt()), indexerAndSearcher.getIndexLocation());
        indexer.setNodeService(nodeService);
        indexer.setLuceneIndexLock(luceneIndexLock);
        indexer.setDictionaryService(dictionaryService);
        indexer.setLuceneFullTextSearchIndexer(luceneFTS);
        indexer.setContentService(contentService);
        indexer.clearIndex();
        indexer.createNode(new ChildAssocRef(null, null, null, rootNodeRef));
        indexer.createNode(new ChildAssocRef(assocTypeQName, rootNodeRef, QName.createQName("{namespace}one"), n1));
        indexer.createNode(new ChildAssocRef(assocTypeQName, rootNodeRef, QName.createQName("{namespace}two"), n2));
        indexer.createNode(new ChildAssocRef(assocTypeQName, rootNodeRef, QName.createQName("{namespace}three"), n3));
        indexer.createNode(new ChildAssocRef(assocTypeQName, rootNodeRef, QName.createQName("{namespace}four"), n4));
        indexer.createNode(new ChildAssocRef(assocTypeQName, n1, QName.createQName("{namespace}five"), n5));
        indexer.createNode(new ChildAssocRef(assocTypeQName, n1, QName.createQName("{namespace}six"), n6));
        indexer.createNode(new ChildAssocRef(assocTypeQName, n2, QName.createQName("{namespace}seven"), n7));
        indexer.createNode(new ChildAssocRef(assocTypeQName, n2, QName.createQName("{namespace}eight"), n8));
        indexer.createNode(new ChildAssocRef(assocTypeQName, n5, QName.createQName("{namespace}nine"), n9));
        indexer.createNode(new ChildAssocRef(assocTypeQName, n5, QName.createQName("{namespace}ten"), n10));
        indexer.createNode(new ChildAssocRef(assocTypeQName, n5, QName.createQName("{namespace}eleven"), n11));
        indexer.createNode(new ChildAssocRef(assocTypeQName, n5, QName.createQName("{namespace}twelve"), n12));
        indexer.createNode(new ChildAssocRef(assocTypeQName, n12, QName.createQName("{namespace}thirteen"), n13));
        indexer.createNode(new ChildAssocRef(assocTypeQName, n13, QName.createQName("{namespace}fourteen"), n14));
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
        LuceneSearcherImpl searcher = LuceneSearcherImpl.getSearcher(rootNodeRef.getStoreRef(), indexerAndSearcher.getIndexLocation());
        searcher.setNodeService(nodeService);
        searcher.setDictionaryService(dictionaryService);
        searcher.setNamespacePrefixResolver(getNamespacePrefixReolsver("namespace"));
        searcher.setQueryRegister(queryRegisterComponent);
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
        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "PATH:\"/one//thirteen/fourteen//.\"", null, null);
        assertEquals(1, results.length());
        results.close();
        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "PATH:\"/one//thirteen/fourteen//.//.\"", null, null);
        assertEquals(1, results.length());
        results.close();

        // Type search tests

        QName qname = QName.createQName(TEST_NAMESPACE, "int-ista");
        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "\\@" + escapeQName(qname) + ":\"01\"", null, null);
        assertEquals(1, results.length());
        assertNotNull(results.getRow(0).getValue(qname));
        results.close();

        qname = QName.createQName(TEST_NAMESPACE, "long-ista");
        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "\\@" + escapeQName(qname) + ":\"2\"", null, null);
        assertEquals(1, results.length());
        assertNotNull(results.getRow(0).getValue(qname));
        results.close();

        qname = QName.createQName(TEST_NAMESPACE, "float-ista");
        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "\\@" + escapeQName(qname) + ":\"3.4\"", null, null);
        assertEquals(1, results.length());
        assertNotNull(results.getRow(0).getValue(qname));
        results.close();
        
        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "\\@"
                +escapeQName(QName.createQName(TEST_NAMESPACE, "double-ista")) + ":\"5.6\"", null, null);
        assertEquals(1, results.length());
        results.close();

        Date date = new Date();
        String sDate = CachingDateFormat.getDateFormat().format(date);
        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "\\@"
                +escapeQName(QName.createQName(TEST_NAMESPACE, "date-ista")) + ":\""+sDate+"\"", null, null);
        assertEquals(1, results.length());
        results.close();
        
        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "\\@"
                +escapeQName(QName.createQName(TEST_NAMESPACE, "datetime-ista")) + ":\""+sDate+"\"", null, null);
        assertEquals(1, results.length());
        results.close();
        
        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "\\@"
                +escapeQName(QName.createQName(TEST_NAMESPACE, "boolean-ista")) + ":\"true\"", null, null);
        assertEquals(1, results.length());
        results.close();
        
        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "\\@"
                +escapeQName(QName.createQName(TEST_NAMESPACE, "qname-ista")) + ":\"{wibble}wobble\"", null, null);
        assertEquals(1, results.length());
        results.close();
        
        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "\\@"
                +escapeQName(QName.createQName(TEST_NAMESPACE, "guid-ista")) + ":\"My-GUID\"", null, null);
        assertEquals(1, results.length());
        results.close();
        
        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "\\@"
                +escapeQName(QName.createQName(TEST_NAMESPACE, "category-ista")) + ":\"CategoryId\"", null, null);
        assertEquals(1, results.length());
        results.close();
        
        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "\\@"
                +escapeQName(QName.createQName(TEST_NAMESPACE, "noderef-ista")) + ":\""+n1+"\"", null, null);
        assertEquals(1, results.length());
        results.close();
        
        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "\\@"
                +escapeQName(QName.createQName(TEST_NAMESPACE, "path-ista")) + ":\""+nodeService.getPath(n3)+"\"", null, null);
        assertEquals(1, results.length());
        assertNotNull(results.getRow(0).getValue(QName.createQName(TEST_NAMESPACE, "path-ista")));
        results.close();

        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "TYPE:\"" + testType.toString() + "\"", null, null);
        assertEquals(1, results.length());
        results.close();

        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "TYPE:\"" + testSuperType.toString() + "\"", null, null);
        assertEquals(1, results.length());
        results.close();

        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "ASPECT:\"" + testAspect.toString() + "\"", null, null);
        assertEquals(1, results.length());
        results.close();

        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "ASPECT:\"" + testSuperAspect.toString() + "\"", null, null);
        assertEquals(1, results.length());
        results.close();

        // FTS test

        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "TEXT:\"fox\"", null, null);
        assertEquals(1, results.length());
        results.close();

        QName queryQName = QName.createQName("alf:test1", namespacePrefixResolver);
        results = searcher.query(rootNodeRef.getStoreRef(), queryQName, null);
        assertEquals(1, results.length());
        results.close();

        // Parameters

        queryQName = QName.createQName("alf:test2", namespacePrefixResolver);
        results = searcher.query(rootNodeRef.getStoreRef(), queryQName, null);
        assertEquals(1, results.length());
        results.close();

        queryQName = QName.createQName("alf:test2", namespacePrefixResolver);
        QueryParameter qp = new QueryParameter(QName.createQName("alf:banana", namespacePrefixResolver), "woof");
        results = searcher.query(rootNodeRef.getStoreRef(), queryQName, new QueryParameter[] { qp });
        assertEquals(0, results.length());
        results.close();

        queryQName = QName.createQName("alf:test3", namespacePrefixResolver);
        qp = new QueryParameter(QName.createQName("alf:banana", namespacePrefixResolver), "/one/five//*");
        results = searcher.query(rootNodeRef.getStoreRef(), queryQName, new QueryParameter[] { qp });
        assertEquals(9, results.length());
        results.close();

        // TODO: should not have a null property type definition
        QueryParameterDefImpl paramDef = new QueryParameterDefImpl(QName.createQName("alf:lemur", namespacePrefixResolver), (PropertyTypeDefinition) null, true, "fox");
        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "TEXT:\"${alf:lemur}\"", null, new QueryParameterDefinition[] { paramDef });
        assertEquals(1, results.length());
        results.close();

        paramDef = new QueryParameterDefImpl(QName.createQName("alf:intvalue", namespacePrefixResolver), (PropertyTypeDefinition) null, true, "1");
        qname = QName.createQName(TEST_NAMESPACE, "int-ista");
        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "\\@" + escapeQName(qname) + ":\"${alf:intvalue}\"", null, new QueryParameterDefinition[] { paramDef });
        assertEquals(1, results.length());
        assertNotNull(results.getRow(0).getValue(qname));
        results.close();

    }

    public void testPathSearch() throws InterruptedException
    {
        luceneFTS.pause();
        buildBaseIndex();

        LuceneSearcherImpl searcher = LuceneSearcherImpl.getSearcher(rootNodeRef.getStoreRef(), indexerAndSearcher.getIndexLocation());
        searcher.setNodeService(nodeService);
        searcher.setDictionaryService(dictionaryService);
        searcher.setNamespacePrefixResolver(getNamespacePrefixReolsver("namespace"));

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

    public void testXPathSearch() throws InterruptedException
    {
        luceneFTS.pause();
        buildBaseIndex();

        LuceneSearcherImpl searcher = LuceneSearcherImpl.getSearcher(rootNodeRef.getStoreRef(), indexerAndSearcher.getIndexLocation());
        searcher.setNodeService(nodeService);
        searcher.setDictionaryService(dictionaryService);
        searcher.setNamespacePrefixResolver(getNamespacePrefixReolsver("namespace"));

        // //*

        ResultSet

        results = searcher.query(rootNodeRef.getStoreRef(), "xpath", "//./*", null, null);
        assertEquals(25, results.length());
        results.close();
        luceneFTS.resume();
        
        QueryParameterDefinition paramDef = new QueryParameterDefImpl(QName.createQName("alf:query", namespacePrefixResolver), (PropertyTypeDefinition) null, true, "//./*");
        results = searcher.query(rootNodeRef.getStoreRef(), "xpath", "${alf:query}", null, new QueryParameterDefinition[] { paramDef });
        assertEquals(25, results.length());
        results.close();
    }

    public void testMissingIndex() throws InterruptedException
    {
        luceneFTS.pause();
        StoreRef storeRef = new StoreRef(StoreRef.PROTOCOL_WORKSPACE, "_missing_");
        LuceneSearcherImpl searcher = LuceneSearcherImpl.getSearcher(storeRef, indexerAndSearcher.getIndexLocation());
        searcher.setNodeService(nodeService);
        searcher.setDictionaryService(dictionaryService);
        searcher.setNamespacePrefixResolver(getNamespacePrefixReolsver("namespace"));

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

        LuceneIndexerImpl indexer = LuceneIndexerImpl.getUpdateIndexer(rootNodeRef.getStoreRef(), "delta" + System.currentTimeMillis(), indexerAndSearcher.getIndexLocation());
        indexer.setNodeService(nodeService);
        indexer.setLuceneIndexLock(luceneIndexLock);
        indexer.setDictionaryService(dictionaryService);
        indexer.setLuceneFullTextSearchIndexer(luceneFTS);
        indexer.setContentService(contentService);

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

        LuceneIndexerImpl indexer = LuceneIndexerImpl.getUpdateIndexer(rootNodeRef.getStoreRef(), "delta" + System.currentTimeMillis(), indexerAndSearcher.getIndexLocation());
        indexer.setNodeService(nodeService);
        indexer.setLuceneIndexLock(luceneIndexLock);
        indexer.setDictionaryService(dictionaryService);
        indexer.setLuceneFullTextSearchIndexer(luceneFTS);
        indexer.setContentService(contentService);

        indexer.deleteNode(new ChildAssocRef(assocTypeQName, n13, QName.createQName("{namespace}fourteen"), n14));

        indexer.commit();

        LuceneSearcherImpl searcher = LuceneSearcherImpl.getSearcher(rootNodeRef.getStoreRef(), indexerAndSearcher.getIndexLocation());
        searcher.setNodeService(nodeService);
        searcher.setDictionaryService(dictionaryService);
        searcher.setNamespacePrefixResolver(getNamespacePrefixReolsver("namespace"));
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

        LuceneIndexerImpl indexer = LuceneIndexerImpl.getUpdateIndexer(rootNodeRef.getStoreRef(), "delta" + System.currentTimeMillis(), indexerAndSearcher.getIndexLocation());
        indexer.setNodeService(nodeService);
        indexer.setLuceneIndexLock(luceneIndexLock);
        indexer.setDictionaryService(dictionaryService);
        indexer.setLuceneFullTextSearchIndexer(luceneFTS);
        indexer.setContentService(contentService);

        indexer.deleteNode(new ChildAssocRef(assocTypeQName, n12, QName.createQName("{namespace}thirteen"), n13));

        indexer.commit();

        LuceneSearcherImpl searcher = LuceneSearcherImpl.getSearcher(rootNodeRef.getStoreRef(), indexerAndSearcher.getIndexLocation());
        searcher.setNodeService(nodeService);
        searcher.setDictionaryService(dictionaryService);
        searcher.setNamespacePrefixResolver(getNamespacePrefixReolsver("namespace"));
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

        LuceneIndexerImpl indexer = LuceneIndexerImpl.getUpdateIndexer(rootNodeRef.getStoreRef(), "delta" + System.currentTimeMillis(), indexerAndSearcher.getIndexLocation());
        indexer.setNodeService(nodeService);
        indexer.setLuceneIndexLock(luceneIndexLock);
        indexer.setDictionaryService(dictionaryService);
        indexer.setLuceneFullTextSearchIndexer(luceneFTS);
        indexer.setContentService(contentService);

        nodeService.removeChild(n2, n13);
        indexer.deleteChildRelationship(new ChildAssocRef(assocTypeQName, n2, QName.createQName("{namespace}link"), n13));

        indexer.commit();

        LuceneSearcherImpl searcher = LuceneSearcherImpl.getSearcher(rootNodeRef.getStoreRef(), indexerAndSearcher.getIndexLocation());
        searcher.setNamespacePrefixResolver(getNamespacePrefixReolsver("namespace"));
        searcher.setNodeService(nodeService);
        searcher.setDictionaryService(dictionaryService);
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

        indexer = LuceneIndexerImpl.getUpdateIndexer(rootNodeRef.getStoreRef(), "delta" + System.currentTimeMillis(), indexerAndSearcher.getIndexLocation());
        indexer.setNodeService(nodeService);
        indexer.setLuceneIndexLock(luceneIndexLock);
        indexer.setDictionaryService(dictionaryService);
        indexer.setLuceneFullTextSearchIndexer(luceneFTS);
        indexer.setContentService(contentService);

        nodeService.addChild(n2, n13, assocTypeQName, QName.createQName("{namespace}link"));
        indexer.createChildRelationship(new ChildAssocRef(assocTypeQName, n2, QName.createQName("{namespace}link"), n13));

        indexer.commit();

        runBaseTests();
        luceneFTS.resume();
    }

    public void testRenameReference() throws InterruptedException
    {
        luceneFTS.pause();
        buildBaseIndex();
        runBaseTests();

        LuceneSearcherImpl searcher = LuceneSearcherImpl.getSearcher(rootNodeRef.getStoreRef(), indexerAndSearcher.getIndexLocation());
        searcher.setNodeService(nodeService);
        searcher.setDictionaryService(dictionaryService);
        searcher.setNamespacePrefixResolver(getNamespacePrefixReolsver("namespace"));

        ResultSet results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "PATH:\"//namespace:link//.\"", null, null);
        assertEquals(3, results.length());
        results.close();

        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "PATH:\"//namespace:renamed_link//.\"", null, null);
        assertEquals(0, results.length());
        results.close();

        LuceneIndexerImpl indexer = LuceneIndexerImpl.getUpdateIndexer(rootNodeRef.getStoreRef(), "delta" + System.currentTimeMillis(), indexerAndSearcher.getIndexLocation());
        indexer.setNodeService(nodeService);
        indexer.setLuceneIndexLock(luceneIndexLock);
        indexer.setDictionaryService(dictionaryService);
        indexer.setLuceneFullTextSearchIndexer(luceneFTS);
        indexer.setContentService(contentService);

        nodeService.removeChild(n2, n13);
        nodeService.addChild(n2, n13, assocTypeQName, QName.createQName("{namespace}renamed_link"));

        indexer.updateChildRelationship(
                new ChildAssocRef(assocTypeQName, n2, QName.createQName("namespace", "link"), n13),
                new ChildAssocRef(assocTypeQName, n2, QName.createQName("namespace", "renamed_link"),
                n13));

        indexer.commit();

        runBaseTests();

        searcher = LuceneSearcherImpl.getSearcher(rootNodeRef.getStoreRef(), indexerAndSearcher.getIndexLocation());
        searcher.setNamespacePrefixResolver(getNamespacePrefixReolsver("namespace"));
        searcher.setDictionaryService(dictionaryService);

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

        LuceneSearcherImpl searcher = LuceneSearcherImpl.getSearcher(rootNodeRef.getStoreRef(), indexerAndSearcher.getIndexLocation());
        searcher.setNamespacePrefixResolver(getNamespacePrefixReolsver("namespace"));
        searcher.setNodeService(nodeService);
        searcher.setDictionaryService(dictionaryService);

        ResultSet results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "\\@"
                + escapeQName(QName.createQName(TEST_NAMESPACE, "text-indexed-stored-tokenised-atomic")) + ":\"KEYONE\"", null, null);
        assertEquals(1, results.length());
        results.close();

        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "\\@"
                + escapeQName(QName.createQName(TEST_NAMESPACE, "text-indexed-stored-tokenised-nonatomic")) + ":\"KEYTWO\"", null, null);
        assertEquals(0, results.length());
        results.close();

        // Do index

        LuceneIndexerImpl indexer = LuceneIndexerImpl.getUpdateIndexer(rootNodeRef.getStoreRef(), "delta" + System.currentTimeMillis() + "_" + (new Random().nextInt()), indexerAndSearcher.getIndexLocation());
        indexer.setNodeService(nodeService);
        indexer.setLuceneIndexLock(luceneIndexLock);
        indexer.setDictionaryService(dictionaryService);
        indexer.setLuceneFullTextSearchIndexer(luceneFTS);
        indexer.setContentService(contentService);
        indexer.updateFullTextSearch(1000);
        indexer.prepare();
        indexer.commit();

        searcher = LuceneSearcherImpl.getSearcher(rootNodeRef.getStoreRef(), indexerAndSearcher.getIndexLocation());
        searcher.setNamespacePrefixResolver(getNamespacePrefixReolsver("namespace"));
        searcher.setDictionaryService(dictionaryService);

        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "\\@" + escapeQName(QName.createQName(TEST_NAMESPACE, "text-indexed-stored-tokenised-atomic"))
                + ":\"keyone\"", null, null);
        assertEquals(1, results.length());
        results.close();

        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "\\@"
                + escapeQName(QName.createQName(TEST_NAMESPACE, "text-indexed-stored-tokenised-nonatomic")) + ":\"keytwo\"", null, null);
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

        LuceneSearcherImpl searcher = LuceneSearcherImpl.getSearcher(rootNodeRef.getStoreRef(), indexerAndSearcher.getIndexLocation());
        searcher.setNodeService(nodeService);
        searcher.setDictionaryService(dictionaryService);
        searcher.setNamespacePrefixResolver(getNamespacePrefixReolsver("namespace"));

        ResultSet results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "\\@"
                + escapeQName(QName.createQName(TEST_NAMESPACE, "text-indexed-stored-tokenised-atomic")) + ":\"KEYONE\"", null, null);
        assertEquals(1, results.length());
        results.close();

        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "\\@"
                + escapeQName(QName.createQName(TEST_NAMESPACE, "text-indexed-stored-tokenised-nonatomic")) + ":\"KEYTWO\"", null, null);
        assertEquals(0, results.length());
        results.close();

        // Do index

        searcher = LuceneSearcherImpl.getSearcher(rootNodeRef.getStoreRef(), indexerAndSearcher.getIndexLocation());
        searcher.setNodeService(nodeService);
        searcher.setDictionaryService(dictionaryService);
        searcher.setNamespacePrefixResolver(getNamespacePrefixReolsver("namespace"));

        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "\\@" + escapeQName(QName.createQName(TEST_NAMESPACE, "text-indexed-stored-tokenised-atomic"))
                + ":\"keyone\"", null, null);
        assertEquals(1, results.length());
        results.close();

        luceneFTS.resume();

        Thread.sleep(20000);

        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "\\@"
                + escapeQName(QName.createQName(TEST_NAMESPACE, "text-indexed-stored-tokenised-nonatomic")) + ":\"keytwo\"", null, null);
        assertEquals(1, results.length());
        results.close();

        runBaseTests();
    }

    private String escapeQName(QName qname)
    {
        return LuceneQueryParser.escape(qname.toString());
    }

    public void testForKev() throws InterruptedException
    {
        luceneFTS.pause();
        buildBaseIndex();
        runBaseTests();

        LuceneSearcherImpl searcher = LuceneSearcherImpl.getSearcher(rootNodeRef.getStoreRef(), indexerAndSearcher.getIndexLocation());
        searcher.setNamespacePrefixResolver(getNamespacePrefixReolsver("namespace"));
        searcher.setNodeService(nodeService);
        searcher.setDictionaryService(dictionaryService);

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
        runPerformanceTest(10000, true);
        luceneFTS.resume();
    }

    public void testPerf2() throws InterruptedException
    {
        luceneFTS.pause();
        runPerformanceTest(10000, false);
        luceneFTS.resume();
    }

    public void testPerf3() throws InterruptedException
    {
        luceneFTS.pause();
        runPerformanceTest(10000, false);
        luceneFTS.resume();
    }

    public void testPerf4() throws InterruptedException
    {
        luceneFTS.pause();
        runPerformanceTest(10000, false);
        luceneFTS.resume();
    }

    public void testPerf5() throws InterruptedException
    {
        luceneFTS.pause();
        runPerformanceTest(10000, false);
        luceneFTS.resume();
    }

    public void testPerf6() throws InterruptedException
    {
        luceneFTS.pause();
        runPerformanceTest(10000, false);
        luceneFTS.resume();
    }

    public void testPerf7() throws InterruptedException
    {
        luceneFTS.pause();
        runPerformanceTest(10000, false);
        luceneFTS.resume();
    }

    public void testPerf8() throws InterruptedException
    {
        luceneFTS.pause();
        runPerformanceTest(10000, false);
        luceneFTS.resume();
    }

    public void testPerf9() throws InterruptedException
    {
        luceneFTS.pause();
        runPerformanceTest(10000, false);
        luceneFTS.resume();
    }

    public void testPerf10() throws InterruptedException
    {
        luceneFTS.pause();
        runPerformanceTest(10000, false);
        luceneFTS.resume();
    }

    private void runPerformanceTest(double time, boolean clear)
    {
        LuceneIndexerImpl indexer = LuceneIndexerImpl.getUpdateIndexer(rootNodeRef.getStoreRef(), "delta" + System.currentTimeMillis() + "_" + (new Random().nextInt()), indexerAndSearcher.getIndexLocation());
        indexer.setNodeService(nodeService);
        indexer.setLuceneIndexLock(luceneIndexLock);
        indexer.setDictionaryService(dictionaryService);
        indexer.setLuceneFullTextSearchIndexer(luceneFTS);
        indexer.setContentService(contentService);
        if (clear)
        {
            indexer.clearIndex();
        }
        indexer.createNode(new ChildAssocRef(null, null, null, rootNodeRef));

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
            NodeRef ref = nodeService.createNode(rootNodeRef, assocTypeQName, qname, DictionaryBootstrap.TYPE_QNAME_CONTAINER).getChildRef();
            indexer.createNode(new ChildAssocRef(assocTypeQName, rootNodeRef, qname, ref));

        }
        indexer.commit();
        float delta = ((System.currentTimeMillis() - startTime) / 1000.0f);
        //System.out.println("\tCreated " + count + " in " + delta + "   = " + (count / delta));
    }

    private NamespacePrefixResolver getNamespacePrefixReolsver(String defaultURI)
    {
        DynamicNamespacePrefixResolver nspr = new DynamicNamespacePrefixResolver(null);
        nspr.addDynamicNamespace(NamespaceService.ALFRESCO_PREFIX, NamespaceService.ALFRESCO_URI);
        nspr.addDynamicNamespace(NamespaceService.ALFRESCO_TEST_PREFIX, NamespaceService.ALFRESCO_TEST_URI);
        nspr.addDynamicNamespace("namespace", "namespace");
        nspr.addDynamicNamespace("test", TEST_NAMESPACE);
        nspr.addDynamicNamespace(NamespaceService.DEFAULT_PREFIX, defaultURI);
        return nspr;
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
