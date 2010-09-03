package org.alfresco.wcm.client;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.alfresco.wcm.client.impl.AssetFactoryCmisImpl;
import org.alfresco.wcm.client.impl.SectionFactoryCmisImpl;
import org.alfresco.wcm.client.impl.WebScriptCaller;
import org.alfresco.wcm.client.impl.WebSiteServiceImpl;
import org.alfresco.wcm.client.impl.cache.SimpleCache;
import org.alfresco.wcm.client.util.CmisSessionHelper;
import org.alfresco.wcm.client.util.CmisSessionPool;
import org.alfresco.wcm.client.util.impl.CmisSessionPoolImpl;
import org.alfresco.wcm.client.util.impl.GuestSessionFactoryImpl;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.pool.impl.GenericObjectPool;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class AssetFactoryTest extends TestCase 
{
	private final static int port = 8082;
	private final static Log log = LogFactory.getLog(AssetFactoryTest.class);
	
	private CmisSessionPool sessionPool;
	private Session session;
	private SectionFactoryCmisImpl sectionFactory;

    private AssetFactoryCmisImpl assetFactory;
    private WebSiteServiceImpl webSiteService;
	
	@Override
    protected void setUp() throws Exception
    {
	    super.setUp();
	    
	    // Create a CMIS session
	    GuestSessionFactoryImpl guestSessionFactory = new GuestSessionFactoryImpl("http://localhost:8080/alfresco/service/cmis","admin","admin");	
	    GenericObjectPool guestSessionPool = new GenericObjectPool(guestSessionFactory, 1, GenericObjectPool.WHEN_EXHAUSTED_GROW, 30, 1);	    
	    sessionPool = new CmisSessionPoolImpl(guestSessionPool);
	    session = sessionPool.getGuestSession();
		CmisSessionHelper.setSession(session);	
        sectionFactory = new SectionFactoryCmisImpl();
        sectionFactory.setSectionsRefreshAfter(30);
        assetFactory = new AssetFactoryCmisImpl();
        assetFactory.setSectionFactory(sectionFactory);
        webSiteService = new WebSiteServiceImpl();
        webSiteService.setSectionFactory(sectionFactory);
        webSiteService.setAssetFactory(assetFactory);

        ApplicationContext testCtx = new ClassPathXmlApplicationContext("test-context.xml");        
        webSiteService.setWebscriptCaller((WebScriptCaller)testCtx.getBean("webscriptCaller"));
        webSiteService.setFormIdCache((SimpleCache<String, String>)testCtx.getBean("formIdCache"));
    }

	@Override
    protected void tearDown() throws Exception
    {
	    super.tearDown();
		sessionPool.closeSession(session);
    }

	public void testGetIndexAsset()
	{
		WebSite site = webSiteService.getWebSite("localhost", port);
		assertNotNull(site);
        
		Section root = site.getRootSection();
        String rootId = root.getId();		
		
		Asset indexAsset = assetFactory.getSectionAsset(rootId, "index.html");
		assertEquals("index.html", indexAsset.getName());
		assertEquals(rootId, indexAsset.getContainingSection().getId());
		
		Asset indexAsset2 = assetFactory.getAssetById(indexAsset.getId());
        assertEquals(indexAsset.getId(), indexAsset2.getId());
        assertEquals(indexAsset.getName(), indexAsset2.getName());
        assertEquals(indexAsset.getProperties(), indexAsset2.getProperties());
        
        log.info(indexAsset.getProperties());
        
        List<Section> sections = site.getRootSection().getSections();
        List<String> indexPageIds = new ArrayList<String>();
        for (Section section : sections)
        {
            indexPageIds.add(assetFactory.getSectionAsset(section.getId(), "index.html").getId());
        }
        List<Asset> assets = assetFactory.getAssetsById(indexPageIds);
        for (Asset asset : assets)
        {
            assertTrue(indexPageIds.remove(asset.getId()));
        }
        assertTrue(indexPageIds.isEmpty());
	}
	
    public void testSearch()
    {
        WebSite site = webSiteService.getWebSite("localhost", port);
        assertNotNull(site);
        
        Section rootSection = site.getRootSection();
        Query query = rootSection.createQuery();
        assertEquals(rootSection.getId(), query.getSectionId());
        
        //FIXME: bjr 20100720: Need reliable test data here
        query.setPhrase("test");
        SearchResults results = assetFactory.findByQuery(query);
        log.debug("Result count = " + results.getTotalSize());

        query.setPhrase(null);
        query.setTag("potato");
        results = assetFactory.findByQuery(query);
        log.debug("Result count = " + results.getTotalSize());
    }
    
    public void testRenditions()
    {
//        WebSite site = webSiteService.getWebSite("localhost", port);
//        assertNotNull(site);
//        
//        Section rootSection = site.getRootSection();
//        Asset pdf = rootSection.getAsset("test.pdf");
//        assertNotNull(pdf);
//        log.debug(pdf.getRenditions());
//        
    }
    
    public void testRelationships()
    {
        WebSite site = webSiteService.getWebSite("localhost", port);
        assertNotNull(site);
        
        //Section rootSection = site.getRootSection();
        //FIXME: bjr 20100720: Need reliable test data here...
//        Asset testArticle = rootSection.getAsset("test-article2.html");
//        
//        log.debug(testArticle.getProperties());
//        Asset primaryImage = testArticle.getRelatedAsset("ws:primaryImage");
//        assertEquals("Chrysanthemum.jpg", primaryImage.getName());
    }
    
	
}
