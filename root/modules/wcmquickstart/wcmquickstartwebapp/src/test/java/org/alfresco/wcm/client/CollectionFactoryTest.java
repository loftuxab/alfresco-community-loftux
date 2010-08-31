package org.alfresco.wcm.client;

import java.util.List;

import junit.framework.TestCase;

import org.alfresco.wcm.client.impl.AssetFactoryCmisImpl;
import org.alfresco.wcm.client.impl.CollectionFactoryWebserviceImpl;
import org.alfresco.wcm.client.impl.SectionFactoryCmisImpl;
import org.alfresco.wcm.client.impl.WebSiteServiceImpl;
import org.alfresco.wcm.client.util.CmisSessionHelper;
import org.alfresco.wcm.client.util.CmisSessionPool;
import org.alfresco.wcm.client.util.impl.CmisSessionPoolImpl;
import org.alfresco.wcm.client.util.impl.GuestSessionFactoryImpl;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.commons.pool.impl.GenericObjectPool;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.extensions.webscripts.connector.ConnectorService;

public class CollectionFactoryTest extends TestCase 
{
	private final static int port = 8082;
	
	private CmisSessionPool sessionPool;
	private Session session;
	private ApplicationContext ctx;
	
	@Override
    protected void setUp() throws Exception
    {
	    super.setUp();
	    
	    // Create a CMIS session
	    GuestSessionFactoryImpl guestSessionFactory = new GuestSessionFactoryImpl("http://localhost:8080/alfresco/service/cmis","admin","admin");	
	    GenericObjectPool guestSessionPool = new GenericObjectPool(guestSessionFactory, 1, GenericObjectPool.WHEN_EXHAUSTED_GROW, 30, 1);	    
	    sessionPool = new CmisSessionPoolImpl(guestSessionPool, null);
	    session = sessionPool.getGuestSession();
		CmisSessionHelper.setSession(session);	
	    
        ctx = new ClassPathXmlApplicationContext("test-context.xml");        
    }

	@Override
    protected void tearDown() throws Exception
    {
	    super.tearDown();
		sessionPool.closeSession(session);
    }

	public void testGetCollection()
	{
		SectionFactoryCmisImpl sectionFactory = new SectionFactoryCmisImpl();
		sectionFactory.setSectionsRefreshAfter(30);
		
		AssetFactoryCmisImpl assetFactory = new AssetFactoryCmisImpl();
		
		ConnectorService connectorService = (ConnectorService)ctx.getBean("connector.service");
		
		CollectionFactoryWebserviceImpl collectionFactory = new CollectionFactoryWebserviceImpl();
		collectionFactory.setSectionFactory(sectionFactory);
		collectionFactory.setAssetFactory(assetFactory);
		collectionFactory.setConnectorService(connectorService);
		
		WebSiteServiceImpl webSiteService = new WebSiteServiceImpl();
		webSiteService.setSectionFactory(sectionFactory);
		webSiteService.setAssetFactory(assetFactory);
		webSiteService.setConnectorService(connectorService);
		webSiteService.setLogoFilename("logo.%");

		WebSite site = webSiteService.getWebSite("localhost", port);
		assertNotNull(site);
		
		Section rootSection = site.getRootSection();
		
		AssetCollection topNews = collectionFactory.getCollection(rootSection.getId(), "news.top");
		assertNotNull(topNews);
		List<Asset> assets = topNews.getAssets();
		assertTrue(assets != null && assets.size() > 0);
	}

}
