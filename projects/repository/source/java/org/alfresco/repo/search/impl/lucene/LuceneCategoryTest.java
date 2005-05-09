/*
 * Created on 29-Apr-2005
 *
 * TODO Comment this class
 * 
 * 
 */
package org.alfresco.repo.search.impl.lucene;

import java.util.Random;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import org.alfresco.repo.dictionary.DictionaryService;
import org.alfresco.repo.dictionary.bootstrap.DictionaryBootstrap;
import org.alfresco.repo.node.NodeService;
import org.alfresco.repo.ref.ChildAssocRef;
import org.alfresco.repo.ref.NodeRef;
import org.alfresco.repo.ref.QName;
import org.alfresco.repo.ref.StoreRef;
import org.alfresco.repo.search.ResultSet;
import org.alfresco.repo.search.Searcher;
import org.alfresco.repo.search.impl.lucene.LuceneTest.MockNameService;
import org.alfresco.repo.search.transaction.LuceneIndexLock;

import junit.framework.TestCase;

public class LuceneCategoryTest extends TestCase
{
    
    ApplicationContext ctx;
    NodeService nodeService;
    DictionaryService dictionaryService;
    LuceneIndexLock luceneIndexLock;
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
    
    private NodeRef catContainer;
    private NodeRef catRoot;
    private NodeRef catBase;
    private NodeRef catOne;
    private NodeRef catTwo;
    private NodeRef catThree;

    public LuceneCategoryTest()
    {
        super();
    }

    public LuceneCategoryTest(String arg0)
    {
        super(arg0);
    }

    public void setUp()
    {
        ctx = new ClassPathXmlApplicationContext("classpath:applicationContext.xml");
        nodeService = (NodeService)ctx.getBean("dbNodeService");
        luceneIndexLock = (LuceneIndexLock)ctx.getBean("luceneIndexLock");
        dictionaryService = (DictionaryService)ctx.getBean("dictionaryService");
        
        StoreRef storeRef = nodeService.createStore(
                StoreRef.PROTOCOL_WORKSPACE,
                "Test_" + System.currentTimeMillis());
        rootNodeRef = nodeService.getRootNode(storeRef);
        
        n1 = nodeService.createNode(rootNodeRef, null, QName.createQName("{namespace}one"), DictionaryBootstrap.TYPE_QNAME_CONTAINER).getChildRef();
        nodeService.setProperty(n1, QName.createQName("{namespace}property-1"), "value-1");
        n2 = nodeService.createNode(rootNodeRef, null, QName.createQName("{namespace}two"), DictionaryBootstrap.TYPE_QNAME_CONTAINER).getChildRef();
        nodeService.setProperty(n2, QName.createQName("{namespace}property-1"), "value-1");
        nodeService.setProperty(n2, QName.createQName("{namespace}property-2"), "value-2");
        n3 = nodeService.createNode(rootNodeRef, null, QName.createQName("{namespace}three"), DictionaryBootstrap.TYPE_QNAME_CONTAINER).getChildRef();
        n4 = nodeService.createNode(rootNodeRef, null, QName.createQName("{namespace}four"), DictionaryBootstrap.TYPE_QNAME_CONTAINER).getChildRef();
        n5 = nodeService.createNode(n1, null, QName.createQName("{namespace}five"), DictionaryBootstrap.TYPE_QNAME_CONTAINER).getChildRef();
        n6 = nodeService.createNode(n1, null, QName.createQName("{namespace}six"), DictionaryBootstrap.TYPE_QNAME_CONTAINER).getChildRef();
        n7 = nodeService.createNode(n2, null, QName.createQName("{namespace}seven"), DictionaryBootstrap.TYPE_QNAME_CONTAINER).getChildRef();
        n8 = nodeService.createNode(n2, null, QName.createQName("{namespace}eight-2"), DictionaryBootstrap.TYPE_QNAME_CONTAINER).getChildRef();
        n9 = nodeService.createNode(n5, null, QName.createQName("{namespace}nine"), DictionaryBootstrap.TYPE_QNAME_CONTAINER).getChildRef();
        n10 = nodeService.createNode(n5, null, QName.createQName("{namespace}ten"), DictionaryBootstrap.TYPE_QNAME_CONTAINER).getChildRef();
        n11 = nodeService.createNode(n5, null, QName.createQName("{namespace}eleven"), DictionaryBootstrap.TYPE_QNAME_CONTAINER).getChildRef();
        n12 = nodeService.createNode(n5, null, QName.createQName("{namespace}twelve"), DictionaryBootstrap.TYPE_QNAME_CONTAINER).getChildRef();
        n13 = nodeService.createNode(n12, null, QName.createQName("{namespace}thirteen"), DictionaryBootstrap.TYPE_QNAME_CONTAINER).getChildRef();
        n14 = nodeService.createNode(n13, null, QName.createQName("{namespace}fourteen"), DictionaryBootstrap.TYPE_QNAME_CONTAINER).getChildRef();
        
        nodeService.addChild(rootNodeRef, n8, QName.createQName("{namespace}eight-0"));
        nodeService.addChild(n1, n8, QName.createQName("{namespace}eight-1"));
        nodeService.addChild(n2, n13, QName.createQName("{namespace}link"));
        
        nodeService.addChild(n1, n14, QName.createQName("{namespace}common"));
        nodeService.addChild(n2, n14, QName.createQName("{namespace}common"));
        nodeService.addChild(n5, n14, QName.createQName("{namespace}common"));
        nodeService.addChild(n6, n14, QName.createQName("{namespace}common"));
        nodeService.addChild(n12, n14, QName.createQName("{namespace}common"));
        nodeService.addChild(n13, n14, QName.createQName("{namespace}common"));
        
        // Categories
        
        catContainer = nodeService.createNode(rootNodeRef, null, QName.createQName("{namespace}categoryContainer"), DictionaryBootstrap.TYPE_QNAME_CONTAINER).getChildRef();
        catRoot = nodeService.createNode(catContainer, null, QName.createQName("{cat}categoryRoot"), DictionaryBootstrap.TYPE_QNAME_CATEGORYROOT).getChildRef();
        catBase = nodeService.createNode(catRoot, null, QName.createQName("{cat}AssetClass"), DictionaryBootstrap.TYPE_QNAME_CATEGORY).getChildRef();
        catOne = nodeService.createNode(catBase, null, QName.createQName("{cat}Fixed"), DictionaryBootstrap.TYPE_QNAME_CATEGORY).getChildRef();
        catTwo = nodeService.createNode(catBase, null, QName.createQName("{cat}Equity"), DictionaryBootstrap.TYPE_QNAME_CATEGORY).getChildRef();
        catThree = nodeService.createNode(catTwo, null, QName.createQName("{cat}SpecialEquity"), DictionaryBootstrap.TYPE_QNAME_CATEGORY).getChildRef();
        
        nodeService.addChild(catBase, n1, QName.createQName("{catmember}member"));
        nodeService.addChild(catOne, n2, QName.createQName("{catmember}member"));
        nodeService.addChild(catOne, n3, QName.createQName("{catmember}member"));
        nodeService.addChild(catOne, n4, QName.createQName("{catmember}member"));
        nodeService.addChild(catOne, n5, QName.createQName("{catmember}member"));
        nodeService.addChild(catOne, n6, QName.createQName("{catmember}member"));
        nodeService.addChild(catTwo, n7, QName.createQName("{catmember}member"));
        nodeService.addChild(catTwo, n8, QName.createQName("{catmember}member"));
        nodeService.addChild(catTwo, n9, QName.createQName("{catmember}member"));
        nodeService.addChild(catTwo, n10, QName.createQName("{catmember}member"));
        nodeService.addChild(catTwo, n11, QName.createQName("{catmember}member"));
        
        nodeService.addChild(catOne, n12, QName.createQName("{catmember}member"));
        nodeService.addChild(catOne, n13, QName.createQName("{catmember}member"));
        nodeService.addChild(catOne, n14, QName.createQName("{catmember}member"));
        
        nodeService.addChild(catTwo, n12, QName.createQName("{catmember}member"));
        nodeService.addChild(catTwo, n13, QName.createQName("{catmember}member"));
        nodeService.addChild(catTwo, n14, QName.createQName("{catmember}member"));
        
        nodeService.addChild(catThree, n13, QName.createQName("{catmember}member"));
    }
    
    private void buildBaseIndex()
    {
        LuceneIndexerImpl indexer = LuceneIndexerImpl.getUpdateIndexer(rootNodeRef.getStoreRef(), "delta" + System.currentTimeMillis() + "_" + (new Random().nextInt()));
        indexer.setNodeService(nodeService);
        indexer.setLuceneIndexLock(luceneIndexLock);
        indexer.setDictionaryService(dictionaryService);
        indexer.clearIndex();
        indexer.createNode(new ChildAssocRef(null, null, rootNodeRef));
        indexer.createNode(new ChildAssocRef(rootNodeRef, QName.createQName("{namespace}one"), n1));
        indexer.createNode(new ChildAssocRef(rootNodeRef, QName.createQName("{namespace}two"), n2));
        indexer.createNode(new ChildAssocRef(rootNodeRef, QName.createQName("{namespace}three"), n3));
        indexer.createNode(new ChildAssocRef(rootNodeRef, QName.createQName("{namespace}four"), n4));
        indexer.createNode(new ChildAssocRef(rootNodeRef, QName.createQName("{namespace}categoryContainer"), catContainer));
        indexer.createNode(new ChildAssocRef(catContainer, QName.createQName("{cat}categoryRoot"), catRoot));
        indexer.createNode(new ChildAssocRef(catRoot, QName.createQName("{cat}AssetClass"), catBase));
        indexer.createNode(new ChildAssocRef(catBase, QName.createQName("{cat}Fixed"), catOne));
        indexer.createNode(new ChildAssocRef(catBase, QName.createQName("{cat}Equity"), catTwo));
        indexer.createNode(new ChildAssocRef(catTwo, QName.createQName("{cat}SpecialEquity"), catThree));
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

    
    public void test1()
    {
        buildBaseIndex();
        
        Searcher searcher = LuceneSearcherImpl.getSearcher(rootNodeRef.getStoreRef());
        searcher.setNameSpaceService(new MockNameService());
        ResultSet results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "PATH:\"/namespace:categoryContainer\"", null, null);
        assertEquals(1, results.length());
        results.close();
        
        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "PATH:\"/namespace:categoryContainer/cat:categoryRoot\"", null, null);
        assertEquals(1, results.length());
        results.close();
        
        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "PATH:\"/namespace:categoryContainer/cat:categoryRoot\"", null, null);
        assertEquals(1, results.length());
        results.close();
        
        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "PATH:\"/namespace:categoryContainer/cat:categoryRoot/cat:AssetClass\"", null, null);
        assertEquals(1, results.length());
        results.close();
        
        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "PATH:\"/namespace:categoryContainer/cat:categoryRoot/cat:AssetClass/cat:Fixed\"", null, null);
        assertEquals(1, results.length());
        results.close();
        
        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "PATH:\"/namespace:categoryContainer/cat:categoryRoot/cat:AssetClass/cat:Equity\"", null, null);
        assertEquals(1, results.length());
        results.close();
        
        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "PATH:\"/cat:AssetClass\"", null, null);
        assertEquals(1, results.length());
        results.close();
        
        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "PATH:\"/cat:AssetClass/cat:Fixed\"", null, null);
        assertEquals(1, results.length());
        results.close();
        
        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "PATH:\"/cat:AssetClass/cat:Equity\"", null, null);
        assertEquals(1, results.length());
        results.close();
        
        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "PATH:\"/cat:AssetClass/cat:*\"", null, null);
        assertEquals(2, results.length());
        results.close();
        
        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "PATH:\"/cat:AssetClass//cat:*\"", null, null);
        assertEquals(3, results.length());
        results.close();
        
        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "PATH:\"/cat:AssetClass/cat:Fixed/catmember:member\"", null, null);
        assertEquals(8, results.length());
        results.close();
        
        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "PATH:\"/cat:AssetClass/cat:Equity/catmember:member\"", null, null);
        assertEquals(8, results.length());
        results.close();
        
        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "PATH:\"/cat:AssetClass/cat:Equity/cat:SpecialEquity/catmember:member//.\"", null, null);
        assertEquals(3, results.length());
        results.close();
        
        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "PATH:\"/cat:AssetClass/cat:Equity/cat:SpecialEquity/catmember:member//*\"", null, null);
        assertEquals(2, results.length());
        results.close();
        
        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "PATH:\"/cat:AssetClass/cat:Equity/cat:SpecialEquity/catmember:member\"", null, null);
        assertEquals(1, results.length());
        results.close();
        
        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "PATH:\"/cat:AssetClass/cat:Equity/catmember:member\"   AND PATH:\"/cat:AssetClass/cat:Fixed/catmember:member\"", null, null);
        assertEquals(3, results.length());
        results.close();
        
        
    }
}
