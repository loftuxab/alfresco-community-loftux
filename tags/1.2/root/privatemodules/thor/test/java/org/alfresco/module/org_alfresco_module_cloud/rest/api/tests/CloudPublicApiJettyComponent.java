package org.alfresco.module.org_alfresco_module_cloud.rest.api.tests;

import org.alfresco.rest.api.tests.PublicApiJettyComponent;

public class CloudPublicApiJettyComponent extends PublicApiJettyComponent
{
	public CloudPublicApiJettyComponent(int port, String contextPath, String[] configLocations, String[] classLocations)
	{
		super(port, contextPath, configLocations, classLocations);
	}

}
