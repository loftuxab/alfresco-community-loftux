package org.alfresco.wcm.client;

import junit.framework.TestCase;

import org.alfresco.wcm.client.impl.AssetFactoryCmisImpl;
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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.pool.impl.GenericObjectPool;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.extensions.webscripts.connector.ConnectorService;

public class SectionFactoryTest extends TestCase 
{
    private final static Log log = LogFactory.getLog(SectionFactoryTest.class);
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
	    GenericObjectPool guestSessionPool = new GenericObjectPool(guestSessionFactory, 1, GenericObjectPool.WHEN_EXHAUSTED_GROW, 30, 1);	    
	    sessionPool = new CmisSessionPoolImpl(guestSessionPool, null);
	    session = sessionPool.getGuestSession();
		CmisSessionHelper.setSession(session);	
		
		assetFactory = new AssetFactoryCmisImpl();

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
		sectionFactory = new SectionFactoryCmisImpl();
		sectionFactory.setSectionsRefreshAfter(30);
		
		DictionaryService dictionaryService = new DictionaryServiceImpl();
		((DictionaryServiceImpl)dictionaryService).init();
		sectionFactory.setDictionaryService(dictionaryService);		
		
		WebSiteServiceImpl webSiteService = new WebSiteServiceImpl();
		webSiteService.setSectionFactory(sectionFactory);
		webSiteService.setAssetFactory(assetFactory);
        webSiteService.setConnectorService(connectorService);
        webSiteService.setUrlUtils(urlUtils);
        
		WebSite site = webSiteService.getWebSite("localhost", port);
		assertNotNull(site);
		
        Section root = site.getRootSection();
        String rootId = root.getId();		
		
		Section section = sectionFactory.getSectionFromPathSegments(rootId, new String[] {"news"});
		assertNotNull(section);
		//assertNotNull(section.getCollectionFolderId());
		
		Section bad = sectionFactory.getSectionFromPathSegments(rootId, new String[] {"news", "wooble"});
		assertNull(bad);

		Section exists2 = sectionFactory.getSection(section.getId());
		assertNotNull(exists2);		
		//assertNotNull(exists2.getCollectionFolderId());
		
		log.debug(section.getProperties());
	}

}
