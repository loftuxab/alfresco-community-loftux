package org.alfresco.module.org_alfresco_module_cloud.rest.api.tests;

import org.junit.AfterClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * Cloud Public API tests.
 * 
 * @author steveglover
 *
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
	TestActivities.class,
	TestCMIS.class,
	TestFavourites.class,
	TestFavouriteSites.class,
	TestNetworks.class,
	TestNodeComments.class,
	TestNodeRatings.class,
	TestPeople.class,
	TestPersonSites.class,
	TestPublicApi128.class,
	TestSites.class,
	TestSiteContainers.class,
	TestSiteMembers.class,
	TestSiteMembershipRequests.class,
	TestTags.class,
	TestUserPreferences.class
})
public class CloudApiTestSuite
{
	@AfterClass
	public static void after() throws Exception
	{
//		CloudPublicApiTestFixture.getInstance().shutdown();
	}
}
