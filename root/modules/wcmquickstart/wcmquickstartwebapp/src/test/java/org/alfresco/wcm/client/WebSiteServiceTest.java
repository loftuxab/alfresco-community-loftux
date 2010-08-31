package org.alfresco.wcm.client;

import java.util.Collection;
import java.util.List;

import junit.framework.TestCase;

import org.alfresco.wcm.client.impl.AssetFactoryCmisImpl;
import org.alfresco.wcm.client.impl.AssetImpl;
import org.alfresco.wcm.client.impl.DictionaryServiceImpl;
import org.alfresco.wcm.client.impl.SectionFactoryCmisImpl;
import org.alfresco.wcm.client.impl.WebSiteServiceImpl;
import org.alfresco.wcm.client.util.CmisSessionHelper;
import org.alfresco.wcm.client.util.CmisSessionPool;
import org.alfresco.wcm.client.util.UrlUtils;
import org.alfresco.wcm.client.util.impl.CmisSessionPoolImpl;
import org.alfresco.wcm.client.util.impl.GuestSessionFactoryImpl;
import org.alfresco.wcm.client.util.impl.UrlUtilsImpl;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.commons.pool.impl.GenericObjectPool;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.extensions.webscripts.connector.ConnectorService;

public class WebSiteServiceTest extends TestCase 
{
	private final static int port = 8082;
	
	private CmisSessionPool sessionPool;
	private Session session;
	private SectionFactoryCmisImpl sectionFactory;
	private AssetFactoryCmisImpl assetFactory;
	private ConnectorService connectorService;
    private ApplicationContext ctx;
    private UrlUtils urlUtils;	
	
	@Override
    protected void setUp() throws Exception
    {
	    super.setUp();
	    
	    // Create a CMIS session
	    GuestSessionFactoryImpl guestSessionFactory = new GuestSessionFactoryImpl("http://localhost:8080/alfresco/service/cmis","admin","admin");	
	    GenericObjectPool guestSessionPool = new GenericObjectPool(guestSessionFactory, 5, GenericObjectPool.WHEN_EXHAUSTED_GROW, 30, 5);	    
	    sessionPool = new CmisSessionPoolImpl(guestSessionPool, null);
	    session = sessionPool.getGuestSession();
		CmisSessionHelper.setSession(session);
		
		assetFactory = new AssetFactoryCmisImpl();
		
		sectionFactory = new SectionFactoryCmisImpl();
		sectionFactory.setSectionsRefreshAfter(30);
		sectionFactory.setAssetFactory(assetFactory);
		
		DictionaryService dictionaryService = new DictionaryServiceImpl();
		((DictionaryServiceImpl)dictionaryService).init();
		sectionFactory.setDictionaryService(dictionaryService);
		
		assetFactory.setSectionFactory(sectionFactory);
		
        ctx = new ClassPathXmlApplicationContext("test-context.xml");      		
		connectorService = (ConnectorService)ctx.getBean("connector.service");
		urlUtils = new UrlUtilsImpl();
    }

	@Override
    protected void tearDown() throws Exception
    {
	    super.tearDown();
		sessionPool.closeSession(session);
    }

	public void testGetSections()
	{
		WebSiteServiceImpl webSiteService = new WebSiteServiceImpl();
		webSiteService.setSectionFactory(sectionFactory);
		webSiteService.setAssetFactory(assetFactory);
		webSiteService.setConnectorService(connectorService);
		webSiteService.setUrlUtils(urlUtils);
		
		Collection<WebSite> webSites = webSiteService.getWebSites();
		for (WebSite webSite : webSites) 
		{
			System.out.println(webSite.getHostName() + ":" + webSite.getHostPort());
		}
		
		final WebSite site = webSiteService.getWebSite("localhost", port);
		assertNotNull(site);
		assertEquals("localhost", site.getHostName());
		
		WebSite siteBad = webSiteService.getWebSite("localhost", 8080);
		assertNull(siteBad);
		
		WebSite siteBad2 = webSiteService.getWebSite("me.com", port);
		assertNull(siteBad2);
		
		Section rootSection = site.getRootSection();
		assertNotNull(rootSection);		
		outputSection(0, rootSection);
		
		Asset pathTest = site.getAssetByPath("/blog/blog1.html"); 
		assertNotNull(pathTest);
		assertTrue(pathTest instanceof AssetImpl);
		assertEquals("blog1.html", pathTest.getName());
				
		Asset indexFromPath = site.getAssetByPath("/blog/");
		assertNotNull(indexFromPath);
		assertTrue(indexFromPath instanceof AssetImpl);
		assertEquals("index.html", indexFromPath.getName());
		
		Asset indexFromNull = site.getAssetByPath(null);
		assertNotNull(indexFromNull);
		assertTrue(indexFromNull instanceof AssetImpl);
		assertEquals("index.html", indexFromNull.getName());	
	}
	
	// /blog should fail unless there is a file named "blog" ... /blog/ should return the index.html			
	@Test (expected=ResourceNotFoundException.class) 
	public void testError() 
	{
		WebSiteServiceImpl webSiteService = new WebSiteServiceImpl();
		webSiteService.setSectionFactory(sectionFactory);
		webSiteService.setAssetFactory(assetFactory);
        webSiteService.setConnectorService(connectorService);
        webSiteService.setUrlUtils(urlUtils);		
		
		final WebSite site = webSiteService.getWebSite("localhost", port);
		site.getAssetByPath("/blog");
    }	
	
	public void testSectionConfig()
	{
		WebSiteServiceImpl webSiteService = new WebSiteServiceImpl();
		webSiteService.setSectionFactory(sectionFactory);
		webSiteService.setAssetFactory(assetFactory);
        webSiteService.setConnectorService(connectorService);
        webSiteService.setUrlUtils(urlUtils);		
		
		WebSite site = webSiteService.getWebSite("localhost", port);
		
		Asset blog1 = site.getAssetByPath("/blog/blog1.html");
		Section blog = blog1.getContainingSection();
		
		String template = blog.getTemplate("cmis:document");
		assertNotNull(template);
		assertEquals("baseTemplate", template);
		
		template = ((Asset)blog1).getTemplate();
		assertNotNull(template);
		assertEquals("articlepage2", template);
		
		Resource index = blog.getIndexPage();
		assertNotNull(index);
		template = ((Asset)index).getTemplate();
		assertNotNull(template);
		assertEquals("sectionpage2", template);		
	}
	
	private void outputSection(int depth, Section section)
	{
		System.out.println(indentString(depth) + "/" + section.getName());
		List<Section> sections = section.getSections();
		for (Section child : sections) 
		{
			assertEquals(section.getName(), child.getContainingSection().getName());
			outputSection(depth+3, child);
		}
	}
	
	private String indentString(int size)
	{
		StringBuffer buffer = new StringBuffer(size);
		for (int i = 0; i < size; i++) 
		{
			buffer.append(" ");
		}
		return buffer.toString();
	}
}
