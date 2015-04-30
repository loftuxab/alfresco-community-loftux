package org.alfresco.module.org_alfresco_module_cloud.rest.api.tests;

import org.alfresco.repo.web.util.JettyComponent;
import org.alfresco.rest.api.tests.RepoService;

public class CloudPublicApiTestFixture extends CloudTestFixture
{
	public final static String[] CONFIG_LOCATIONS = new String[]
    {
		"classpath:alfresco/application-context.xml",
		"classpath:alfresco/web-scripts-application-context.xml",
		"classpath:alfresco/web-scripts-application-context-test.xml",
		"cloud-test-context.xml",
		"rest-api-test-context.xml"
    };

	public final static String[] CLASS_LOCATIONS = new String[] {"classpath*:/publicapi/lucene/"};

    private static CloudPublicApiTestFixture instance;

	/*
	 * Note: synchronized for multi-threaded test access
	 */
    public synchronized static CloudPublicApiTestFixture getInstance() throws Exception
    {
    	if(instance == null)
    	{
    		instance = new CloudPublicApiTestFixture();
    		instance.setup();
    	}
    	return instance;
    }

    private CloudPublicApiTestFixture()
	{
		super(CONFIG_LOCATIONS, CLASS_LOCATIONS, 8081, CONTEXT_PATH, PUBLIC_API_SERVLET_NAME, DEFAULT_NUM_MEMBERS_PER_SITE, false);
	}

	@Override
	protected JettyComponent makeJettyComponent()
	{
		JettyComponent jettyComponent = new CloudPublicApiJettyComponent(port, contextPath, configLocations, classLocations);
		return jettyComponent;
	}

	@Override
	protected RepoService makeRepoService() throws Exception
	{
		return CloudRepoService.createCloudRepoService(applicationContext);
	}
}
