package org.alfresco.wcm.client;

import java.util.Date;
import java.util.List;
import java.util.Random;

import junit.framework.TestCase;

import org.alfresco.wcm.client.impl.AssetFactoryCmisImpl;
import org.alfresco.wcm.client.impl.SectionFactoryCmisImpl;
import org.alfresco.wcm.client.impl.WebSiteServiceImpl;
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
import org.springframework.extensions.webscripts.connector.ConnectorService;

public class UgcServiceTest extends TestCase 
{
	private final static int port = 8082;
	private final static Log log = LogFactory.getLog(UgcServiceTest.class);
	
	private CmisSessionPool sessionPool;
	private Session session;
	private SectionFactoryCmisImpl sectionFactory;

    private AssetFactoryCmisImpl assetFactory;
    private WebSiteServiceImpl webSiteService;
	
	@Override
    protected void setUp() throws Exception
    {
	    super.setUp();
	    
        ApplicationContext testCtx = new ClassPathXmlApplicationContext("test-context.xml");        
        ConnectorService connectorService = (ConnectorService)testCtx.getBean("connector.service");
        
        // Create a CMIS session
	    GuestSessionFactoryImpl guestSessionFactory = new GuestSessionFactoryImpl("http://localhost:8080/alfresco/service/cmis","admin","admin");	
	    GenericObjectPool guestSessionPool = new GenericObjectPool(guestSessionFactory, 1, GenericObjectPool.WHEN_EXHAUSTED_GROW, 30, 1);	    
	    sessionPool = new CmisSessionPoolImpl(guestSessionPool, null);
	    session = sessionPool.getGuestSession();
		CmisSessionHelper.setSession(session);	
        sectionFactory = new SectionFactoryCmisImpl();
        sectionFactory.setSectionsRefreshAfter(30);
        assetFactory = new AssetFactoryCmisImpl();
        assetFactory.setSectionFactory(sectionFactory);
        webSiteService = new WebSiteServiceImpl();
        webSiteService.setSectionFactory(sectionFactory);
        webSiteService.setAssetFactory(assetFactory);
        webSiteService.setConnectorService(connectorService);
    }

	@Override
    protected void tearDown() throws Exception
    {
	    super.tearDown();
		sessionPool.closeSession(session);
    }

	public void testPostFeedback()
	{
		WebSite site = webSiteService.getWebSite("localhost", port);
		assertNotNull(site);
		
		Section root = site.getRootSection();
		String rootId = root.getId();
		
		Asset indexAsset = assetFactory.getSectionAsset(root.getId(), "test-article.html");
		assertEquals("test-article.html", indexAsset.getName());
		assertEquals(rootId, indexAsset.getContainingSection().getId());
		
		log.info("Article id = " + indexAsset.getId());
		
		Date beforePost = new Date();
		String feedbackId = site.getUgcService().postFeedback(indexAsset.getId(), "Brian", "brian@theworld", 
		        "www.brian.com", UgcService.COMMENT_TYPE, null, "This is a fantastic article", new Random(System.currentTimeMillis()).nextInt(6));
        Date afterPost = new Date();
		
        long count = 0;
		VisitorFeedbackPage page = site.getUgcService().getFeedbackPage(indexAsset.getId(), 10, count);
		long totalSize = page.getTotalSize();
		boolean found = false;
		while (page.getSize() > 0)
		{
	        List<VisitorFeedback> feedbackList = page.getFeedback();
    		for (VisitorFeedback feedback : feedbackList)
    		{
    		    ++count;
    		    if (!found)
    		    {
    		        found = (feedback.getId().equals(feedbackId));
    		        if (found)
    		        {
    		            Date feedbackTime = feedback.getPostTime();
    		            assertNotNull(feedbackTime);
    		            assertTrue(feedbackTime.after(beforePost) || feedbackTime.equals(beforePost));
    		            assertTrue(feedbackTime.before(afterPost) || feedbackTime.equals(afterPost));
    		        }
    		    }
    		}
    		page = site.getUgcService().getFeedbackPage(indexAsset.getId(), 10, count);
		}
		assertEquals(totalSize, count);
		assertTrue(found);
	}
	
	
}
