package org.alfresco.module.org_alfresco_module_cloud.rest.api.tests;

import org.alfresco.rest.api.tests.AbstractTestApi;
import org.alfresco.rest.api.tests.TestFixture;

public class CloudTestApi extends AbstractTestApi
{
	@Override
	protected TestFixture getTestFixture() throws Exception
	{
		return CloudPublicApiTestFixture.getInstance();
	}
}
