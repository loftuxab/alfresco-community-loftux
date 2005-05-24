/*
 * Created on 29-Apr-2005
 *
 * TODO Comment this class
 * 
 * 
 */
package org.alfresco.repo.search.impl.lucene;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;

import junit.framework.TestCase;

import org.alfresco.repo.dictionary.ClassRef;
import org.alfresco.repo.dictionary.DictionaryService;
import org.alfresco.repo.dictionary.NamespaceService;
import org.alfresco.repo.dictionary.PropertyTypeDefinition;
import org.alfresco.repo.dictionary.bootstrap.DictionaryBootstrap;
import org.alfresco.repo.dictionary.metamodel.M2Aspect;
import org.alfresco.repo.dictionary.metamodel.M2Property;
import org.alfresco.repo.dictionary.metamodel.MetaModelDAO;
import org.alfresco.repo.node.NodeService;
import org.alfresco.repo.ref.ChildAssocRef;
import org.alfresco.repo.ref.NodeRef;
import org.alfresco.repo.ref.Path;
import org.alfresco.repo.ref.QName;
import org.alfresco.repo.ref.StoreRef;
import org.alfresco.repo.search.ResultSet;
import org.alfresco.repo.search.ResultSetRow;
import org.alfresco.repo.search.impl.lucene.LuceneTest.MockNameService;
import org.alfresco.repo.search.impl.lucene.fts.FullTextSearchIndexer;
import org.alfresco.repo.search.transaction.LuceneIndexLock;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

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
    private NodeRef catACBase;
    private NodeRef catACOne;
    private NodeRef catACTwo;
    private NodeRef catACThree;
    private FullTextSearchIndexer luceneFTS;
    private MetaModelDAO metaModelDAO;
    private QName regionCategorisationQName;
    private QName assetClassCategorisationQName;
    private QName investmentRegionCategorisationQName;
    private QName marketingRegionCategorisationQName;
    private NodeRef catRBase;
    private NodeRef catROne;
    private NodeRef catRTwo;
    private NodeRef catRThree;

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
        luceneFTS = (FullTextSearchIndexer) ctx.getBean("LuceneFullTextSearchIndexer");
        metaModelDAO = (MetaModelDAO) ctx.getBean("metaModelDAO");
        
        createTestTypes();
        
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
        
        catContainer = nodeService.createNode(rootNodeRef, null, QName.createQName(NamespaceService.ALFRESCO_URI, "categoryContainer"), DictionaryBootstrap.TYPE_QNAME_CONTAINER).getChildRef();
        catRoot = nodeService.createNode(catContainer, null, QName.createQName(NamespaceService.ALFRESCO_URI, "categoryRoot"), DictionaryBootstrap.TYPE_QNAME_CATEGORYROOT).getChildRef();
       
       
        
        catRBase = nodeService.createNode(catRoot, null, QName.createQName(NamespaceService.ALFRESCO_URI, "Region"), DictionaryBootstrap.TYPE_QNAME_CATEGORY).getChildRef();
        catROne = nodeService.createNode(catRBase, null, QName.createQName(NamespaceService.ALFRESCO_URI, "Europe"), DictionaryBootstrap.TYPE_QNAME_CATEGORY).getChildRef();
        catRTwo = nodeService.createNode(catRBase, null, QName.createQName(NamespaceService.ALFRESCO_URI, "RestOfWorld"), DictionaryBootstrap.TYPE_QNAME_CATEGORY).getChildRef();
        catRThree = nodeService.createNode(catRTwo, null, QName.createQName(NamespaceService.ALFRESCO_URI, "US"), DictionaryBootstrap.TYPE_QNAME_CATEGORY).getChildRef();
        
        nodeService.addChild(catRoot, catRBase, QName.createQName(NamespaceService.ALFRESCO_URI, "InvestmentRegion"));
        nodeService.addChild(catRoot, catRBase, QName.createQName(NamespaceService.ALFRESCO_URI, "MarketingRegion"));
        
        
        catACBase = nodeService.createNode(catRoot, null, QName.createQName(NamespaceService.ALFRESCO_URI, "AssetClass"), DictionaryBootstrap.TYPE_QNAME_CATEGORY).getChildRef();
        catACOne = nodeService.createNode(catACBase, null, QName.createQName(NamespaceService.ALFRESCO_URI, "Fixed"), DictionaryBootstrap.TYPE_QNAME_CATEGORY).getChildRef();
        catACTwo = nodeService.createNode(catACBase, null, QName.createQName(NamespaceService.ALFRESCO_URI, "Equity"), DictionaryBootstrap.TYPE_QNAME_CATEGORY).getChildRef();
        catACThree = nodeService.createNode(catACTwo, null, QName.createQName(NamespaceService.ALFRESCO_URI, "SpecialEquity"), DictionaryBootstrap.TYPE_QNAME_CATEGORY).getChildRef();
        
        
       
        nodeService.addAspect(n1, new ClassRef(assetClassCategorisationQName), createMap("assetClass", catACBase));
        nodeService.addAspect(n1, new ClassRef(regionCategorisationQName), createMap("region", catRBase));
        
        nodeService.addAspect(n2, new ClassRef(assetClassCategorisationQName), createMap("assetClass", catACOne));
        nodeService.addAspect(n3, new ClassRef(assetClassCategorisationQName), createMap("assetClass", catACOne));
        nodeService.addAspect(n4, new ClassRef(assetClassCategorisationQName), createMap("assetClass", catACOne));
        nodeService.addAspect(n5, new ClassRef(assetClassCategorisationQName), createMap("assetClass", catACOne));
        nodeService.addAspect(n6, new ClassRef(assetClassCategorisationQName), createMap("assetClass", catACOne));
        
        nodeService.addAspect(n7, new ClassRef(assetClassCategorisationQName), createMap("assetClass", catACTwo));
        nodeService.addAspect(n8, new ClassRef(assetClassCategorisationQName), createMap("assetClass", catACTwo));
        nodeService.addAspect(n9, new ClassRef(assetClassCategorisationQName), createMap("assetClass", catACTwo));
        nodeService.addAspect(n10, new ClassRef(assetClassCategorisationQName), createMap("assetClass", catACTwo));
        nodeService.addAspect(n11, new ClassRef(assetClassCategorisationQName), createMap("assetClass", catACTwo));
        
        nodeService.addAspect(n12, new ClassRef(assetClassCategorisationQName), createMap("assetClass", catACOne, catACTwo));
        nodeService.addAspect(n13, new ClassRef(assetClassCategorisationQName), createMap("assetClass", catACOne, catACTwo, catACThree));
        nodeService.addAspect(n14, new ClassRef(assetClassCategorisationQName), createMap("assetClass", catACOne, catACTwo));
        
        nodeService.addAspect(n2, new ClassRef(regionCategorisationQName), createMap("region", catROne));
        nodeService.addAspect(n3, new ClassRef(regionCategorisationQName), createMap("region", catROne));
        nodeService.addAspect(n4, new ClassRef(regionCategorisationQName), createMap("region", catRTwo));
        nodeService.addAspect(n5, new ClassRef(regionCategorisationQName), createMap("region", catRTwo));
        
        nodeService.addAspect(n5, new ClassRef(investmentRegionCategorisationQName), createMap("investmentRegion", catRBase));
        nodeService.addAspect(n5, new ClassRef(marketingRegionCategorisationQName), createMap("marketingRegion", catRBase));
        nodeService.addAspect(n6, new ClassRef(investmentRegionCategorisationQName), createMap("investmentRegion", catRBase));
        nodeService.addAspect(n7, new ClassRef(investmentRegionCategorisationQName), createMap("investmentRegion", catRBase));
        nodeService.addAspect(n8, new ClassRef(investmentRegionCategorisationQName), createMap("investmentRegion", catRBase));
        nodeService.addAspect(n9, new ClassRef(investmentRegionCategorisationQName), createMap("investmentRegion", catRBase));
        nodeService.addAspect(n10, new ClassRef(marketingRegionCategorisationQName), createMap("marketingRegion", catRBase));
        nodeService.addAspect(n11, new ClassRef(marketingRegionCategorisationQName), createMap("marketingRegion", catRBase));
        nodeService.addAspect(n12, new ClassRef(marketingRegionCategorisationQName), createMap("marketingRegion", catRBase));
        nodeService.addAspect(n13, new ClassRef(marketingRegionCategorisationQName), createMap("marketingRegion", catRBase));
        nodeService.addAspect(n14, new ClassRef(marketingRegionCategorisationQName), createMap("marketingRegion", catRBase));
        
    }
    
    private HashMap<QName, Serializable> createMap(String name, NodeRef[] nodeRefs)
    {
        HashMap<QName, Serializable> map = new HashMap<QName, Serializable>();
        Serializable value = null;
        if(nodeRefs.length > 1)
        {
            value = (Serializable) Arrays.asList(nodeRefs);
        }
        else if(nodeRefs.length == 1)
        {
            value = nodeRefs[0];
        }
        map.put(QName.createQName(NamespaceService.ALFRESCO_URI, name), value);
        return map;
    }
    
    private HashMap<QName, Serializable> createMap(String name, NodeRef nodeRef)
    {
        return createMap(name, new NodeRef[]{nodeRef});
    }
    
    private HashMap<QName, Serializable> createMap(String name, NodeRef nodeRef1, NodeRef nodeRef2)
    {
        return createMap(name, new NodeRef[]{nodeRef1, nodeRef2});
    }
    
    private HashMap<QName, Serializable> createMap(String name, NodeRef nodeRef1, NodeRef nodeRef2, NodeRef nodeRef3)
    {
        return createMap(name, new NodeRef[]{nodeRef1, nodeRef2, nodeRef3});
    }
    
    private void createTestTypes()
    {
        regionCategorisationQName = QName.createQName(NamespaceService.ALFRESCO_URI, "Region");
        M2Aspect generalCategorisation = metaModelDAO.createAspect(regionCategorisationQName);
        generalCategorisation.setSuperClass(metaModelDAO.getAspect(DictionaryBootstrap.ASPECT_QNAME_CATEGORISATION));
        M2Property genCatProp = generalCategorisation.createProperty("region");
        genCatProp.setIndexed(true);
        genCatProp.setIndexedAtomically(true);
        genCatProp.setMandatory(true);
        genCatProp.setMultiValued(true);
        genCatProp.setStoredInIndex(true);
        genCatProp.setTokenisedInIndex(true);
        genCatProp.setType(metaModelDAO.getPropertyType(PropertyTypeDefinition.CATEGORY));
        
        assetClassCategorisationQName = QName.createQName(NamespaceService.ALFRESCO_URI, "AssetClass");
        M2Aspect assetClassCategorisation = metaModelDAO.createAspect(assetClassCategorisationQName);
        assetClassCategorisation.setSuperClass(metaModelDAO.getAspect(DictionaryBootstrap.ASPECT_QNAME_CATEGORISATION));
        M2Property acProp = assetClassCategorisation.createProperty("assetClass");
        acProp.setIndexed(true);
        acProp.setIndexedAtomically(true);
        acProp.setMandatory(true);
        acProp.setMultiValued(true);
        acProp.setStoredInIndex(true);
        acProp.setTokenisedInIndex(true);
        acProp.setType(metaModelDAO.getPropertyType(PropertyTypeDefinition.CATEGORY));
        
        investmentRegionCategorisationQName = QName.createQName(NamespaceService.ALFRESCO_URI, "InvestmentRegion");
        M2Aspect  investmentRegionCategorisation = metaModelDAO.createAspect(investmentRegionCategorisationQName);
        investmentRegionCategorisation.setSuperClass(metaModelDAO.getAspect(DictionaryBootstrap.ASPECT_QNAME_CATEGORISATION));
        M2Property irProp = investmentRegionCategorisation.createProperty("investmentRegion");
        irProp.setIndexed(true);
        irProp.setIndexedAtomically(true);
        irProp.setMandatory(true);
        irProp.setMultiValued(true);
        irProp.setStoredInIndex(true);
        irProp.setTokenisedInIndex(true);
        irProp.setType(metaModelDAO.getPropertyType(PropertyTypeDefinition.CATEGORY));
        
        marketingRegionCategorisationQName = QName.createQName(NamespaceService.ALFRESCO_URI, "MarketingRegion");
        M2Aspect marketingRegionCategorisation = metaModelDAO.createAspect(marketingRegionCategorisationQName);
        marketingRegionCategorisation.setSuperClass(metaModelDAO.getAspect(DictionaryBootstrap.ASPECT_QNAME_CATEGORISATION));
        M2Property mrProp =  marketingRegionCategorisation.createProperty("marketingRegion");
        mrProp.setIndexed(true);
        mrProp.setIndexedAtomically(true);
        mrProp.setMandatory(true);
        mrProp.setMultiValued(true);
        mrProp.setStoredInIndex(true);
        mrProp.setTokenisedInIndex(true);
        mrProp.setType(metaModelDAO.getPropertyType(PropertyTypeDefinition.CATEGORY));
        
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
        
        indexer.createNode(new ChildAssocRef(rootNodeRef, QName.createQName("{namespace}categoryContainer"), catContainer));
        indexer.createNode(new ChildAssocRef(catContainer, QName.createQName("{cat}categoryRoot"), catRoot));
        indexer.createNode(new ChildAssocRef(catRoot, QName.createQName("{cat}AssetClass"), catACBase));
        indexer.createNode(new ChildAssocRef(catACBase, QName.createQName("{cat}Fixed"), catACOne));
        indexer.createNode(new ChildAssocRef(catACBase, QName.createQName("{cat}Equity"), catACTwo));
        indexer.createNode(new ChildAssocRef(catACTwo, QName.createQName("{cat}SpecialEquity"), catACThree));
        
        indexer.createNode(new ChildAssocRef(catRoot, QName.createQName(NamespaceService.ALFRESCO_URI, "Region"), catRBase));
        indexer.createNode(new ChildAssocRef(catRBase, QName.createQName(NamespaceService.ALFRESCO_URI, "Europe"), catROne));
        indexer.createNode(new ChildAssocRef(catRBase, QName.createQName(NamespaceService.ALFRESCO_URI, "RestOfWorld"), catRTwo));
        indexer.createNode(new ChildAssocRef(catRTwo, QName.createQName(NamespaceService.ALFRESCO_URI, "US"), catRThree));
        
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
        
        LuceneSearcherImpl searcher = LuceneSearcherImpl.getSearcher(rootNodeRef.getStoreRef());
        
        searcher.setNodeService(nodeService);
        searcher.setDictionaryService(dictionaryService);
        searcher.setNamespaceService(new MockNameService(""));
        ResultSet results;
        
        
        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "PATH:\"/alf:MarketingRegion//member\"", null, null);
        //printPaths(results);
        assertEquals(6, results.length());
        results.close();
        
        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "PATH:\"/alf:categoryContainer\"", null, null);
        assertEquals(1, results.length());
        results.close();
        
        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "PATH:\"/alf:categoryContainer/alf:categoryRoot\"", null, null);
        assertEquals(1, results.length());
        results.close();
        
        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "PATH:\"/alf:categoryContainer/alf:categoryRoot\"", null, null);
        assertEquals(1, results.length());
        results.close();
        
        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "PATH:\"/alf:categoryContainer/alf:categoryRoot/alf:AssetClass\"", null, null);
        assertEquals(1, results.length());
        results.close();
        
        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "PATH:\"/alf:categoryContainer/alf:categoryRoot/alf:AssetClass/alf:Fixed\"", null, null);
        assertEquals(1, results.length());
        results.close();
        
        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "PATH:\"/alf:categoryContainer/alf:categoryRoot/alf:AssetClass/alf:Equity\"", null, null);
        assertEquals(1, results.length());
        results.close();
        
        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "PATH:\"/alf:AssetClass\"", null, null);
        assertEquals(1, results.length());
        results.close();
        
        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "PATH:\"/alf:AssetClass/alf:Fixed\"", null, null);
        assertEquals(1, results.length());
        results.close();
        
        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "PATH:\"/alf:AssetClass/alf:Equity\"", null, null);
        assertEquals(1, results.length());
        results.close();
        
        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "PATH:\"/alf:AssetClass/alf:*\"", null, null);
        assertEquals(2, results.length());
        results.close();
        
        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "PATH:\"/alf:AssetClass//alf:*\"", null, null);
        assertEquals(3, results.length());
        results.close();
        
        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "PATH:\"/alf:AssetClass/alf:Fixed/member\"", null, null);
        //printPaths(results);
        assertEquals(8, results.length());
        results.close();
        
        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "PATH:\"/alf:AssetClass/alf:Equity/member\"", null, null);
        //printPaths(results);
        assertEquals(8, results.length());
        results.close();
        
        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "PATH:\"/alf:AssetClass/alf:Equity/alf:SpecialEquity/member//.\"", null, null);
        assertEquals(1, results.length());
        results.close();
        
        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "PATH:\"/alf:AssetClass/alf:Equity/alf:SpecialEquity/member//*\"", null, null);
        assertEquals(0, results.length());
        results.close();
        
        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "PATH:\"/alf:AssetClass/alf:Equity/alf:SpecialEquity/member\"", null, null);
        assertEquals(1, results.length());
        results.close();
        
        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "+PATH:\"/alf:AssetClass/alf:Equity/member\" AND +PATH:\"/alf:AssetClass/alf:Fixed/member\"", null, null);
        //printPaths(results);
        assertEquals(3, results.length());
        results.close();
        
        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "PATH:\"/alf:AssetClass/alf:Equity/member\" PATH:\"/alf:AssetClass/alf:Fixed/member\"", null, null);
        //printPaths(results);
        assertEquals(13, results.length());
        results.close();
        
        // Region 
        
        assertEquals(4, nodeService.getChildAssocs(catRoot).size());
        
        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "PATH:\"/alf:Region\"", null, null);
        //printPaths(results);
        assertEquals(1, results.length());
        results.close();
        
        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "PATH:\"/alf:Region/member\"", null, null);
        //printPaths(results);
        assertEquals(1, results.length());
        results.close();
        
        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "PATH:\"/alf:Region/alf:Europe/member\"", null, null);
        //printPaths(results);
        assertEquals(2, results.length());
        results.close();
        
        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "PATH:\"/alf:Region/alf:RestOfWorld/member\"", null, null);
        //printPaths(results);
        assertEquals(2, results.length());
        results.close();
        
        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "PATH:\"/alf:Region//member\"", null, null);
        //printPaths(results);
        assertEquals(5, results.length());
        results.close();
        
        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "PATH:\"/alf:InvestmentRegion//member\"", null, null);
        //printPaths(results);
        assertEquals(5, results.length());
        results.close();
        
        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "PATH:\"/alf:MarketingRegion//member\"", null, null);
        //printPaths(results);
        assertEquals(6, results.length());
        results.close();
        
        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "+PATH:\"/alf:AssetClass/alf:Fixed/member\" AND +PATH:\"/alf:Region/alf:Europe/member\"", null, null);
        //printPaths(results);
        assertEquals(2, results.length());
        results.close();
        
        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "+PATH:\"/alf:categoryContainer/alf:categoryRoot/alf:AssetClass/alf:Fixed/member\" AND +PATH:\"/alf:categoryContainer/alf:categoryRoot/alf:Region/alf:Europe/member\"", null, null);
        //printPaths(results);
        assertEquals(2, results.length());
        results.close();
        
        results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "PATH:\"/alf:AssetClass/alf:Equity/member\" PATH:\"/alf:MarketingRegion/member\"", null, null);
        //printPaths(results);
        assertEquals(9, results.length());
        results.close();
    }
    
    void xprintPaths(ResultSet results)
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
}
