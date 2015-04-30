package org.alfresco.module.org_alfresco_module_cloud.rest.api.tests;

import org.alfresco.repo.web.util.JettyComponent;
import org.alfresco.rest.api.tests.AbstractTestFixture;
import org.alfresco.rest.api.tests.EnterpriseJettyComponent;
import org.alfresco.rest.api.tests.RepoService;
import org.alfresco.rest.api.tests.RepoService.TestNetwork;

public class TCKTestFixture extends AbstractTestFixture
{
    protected final static String[] CONFIG_LOCATIONS = new String[]
                                                                  {
      																"classpath:alfresco/application-context.xml",
      																"classpath:alfresco/web-scripts-application-context.xml",
      																"classpath:alfresco/web-scripts-application-context-test.xml",
                                                                  	"cloud-test-context.xml",
      																"rest-api-test-context.xml"
                                                                  };

	public final static String[] CLASS_LOCATIONS = new String[] {"classpath*:/publicapi/solr/"};

	public static final int DEFAULT_NUM_MEMBERS_PER_SITE = 4;
	public static final String TEST_DOMAIN_PREFIX = "acme";

	private static int port = 8081;
	private TestNetwork network;
    private static TCKTestFixture instance;

	/*
	 * Note: synchronized for multi-threaded test access
	 */
    public synchronized static TCKTestFixture getInstance() throws Exception
    {
    	if(instance == null)
    	{
    		instance = new TCKTestFixture();
    		instance.setup();
    	}
    	return instance;
    }

    private TCKTestFixture()
	{
		super(CONFIG_LOCATIONS, CLASS_LOCATIONS, port, CONTEXT_PATH, PUBLIC_API_SERVLET_NAME, DEFAULT_NUM_MEMBERS_PER_SITE, false);
	}
    
    public int getPort()
	{
		return port;
	}
    
	public TestNetwork getNetwork()
	{
		return network;
	}

	@Override
	protected void populateTestData()
	{
		this.network = repoService.createNetwork(TEST_DOMAIN_PREFIX + "tck", true);
		addNetwork(network);
	}

	@Override
	protected JettyComponent makeJettyComponent()
	{
		JettyComponent jettyComponent = new EnterpriseJettyComponent(port, contextPath, configLocations, classLocations);
		return jettyComponent;
//		CMISTCKJettyComponent jetty = new CMISTCKJettyComponent(AbstractTestFixture.PORT, AbstractTestFixture.CONTEXT_PATH, "cmisatom", CONFIG_LOCATIONS, CLASS_LOCATIONS);
//		return jetty;
	}

	@Override
	protected RepoService makeRepoService() throws Exception
	{
		return CloudRepoService.createCloudRepoService(applicationContext);
	}
}