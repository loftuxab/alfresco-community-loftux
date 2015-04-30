package org.alfresco.module.org_alfresco_module_cloud.rest.api.tests;

import org.alfresco.rest.api.tests.AbstractTestFixture;
import org.alfresco.rest.api.tests.RepoService.SiteInformation;
import org.alfresco.rest.api.tests.RepoService.TestNetwork;
import org.alfresco.service.cmr.site.SiteVisibility;

public abstract class CloudTestFixture extends AbstractTestFixture
{
	public static final String TEST_DOMAIN_PREFIX = "acme";
    
	protected CloudTestFixture(String[] configLocations, String[] classLocations, int port, String contextPath, String servletName, int numMembersPerSite, boolean cleanup)
	{
		super(configLocations, classLocations, port, contextPath, servletName, numMembersPerSite, cleanup);
	}

	public TestNetwork addAccount(String networkId)
	{
		TestNetwork account = repoService.new TestNetwork(networkId, true);
        networks.put(networkId, account);
        return account;
	}

    @Override
	protected void populateTestData()
	{
        for(int i = 1; i <= 2; i++)
        {
			TestNetwork network = repoService.createNetworkWithAlias(TEST_DOMAIN_PREFIX + "00" + i, true);
			addNetwork(network);
        }
        
        // 5 public sites
        for(int i = 0; i < 5;  i++)
        {
        	SiteInformation siteInfo = new SiteInformation("testSite" + i, "Public Test Site" + i, "Public Test Site" + i, SiteVisibility.PUBLIC);
        	addSite(siteInfo);
        }

        // 5 private sites
        for(int i = 5; i < 10;  i++)
        {
        	SiteInformation siteInfo = new SiteInformation("testSite" + i, "Private Test Site" + i, "Private Test Site" + i, SiteVisibility.PRIVATE);
        	addSite(siteInfo);
        }
        
    	addPerson(new CloudPersonInfo("David", "Smith", "david.smith", "password", true, null, "skype", "location", "telephone", "mob", "instant", "google"));
    	addPerson(new CloudPersonInfo("Bob", "Jones", "bob.jones", "password", false, null, "skype", "location", "telephone", "mob", "instant", "google"));
    	addPerson(new CloudPersonInfo("Bill", "Grainger", "bill.grainger", "password", true, null, "skype", "location", "telephone", "mob", "instant", "google"));
    	addPerson(new CloudPersonInfo("Jill", "Fry", "jill.fry", "password", false, null, "skype", "location", "telephone", "mob", "instant", "google"));
    	addPerson(new CloudPersonInfo("Elvis", "Presley", "elvis.presley", "password", false, null, "skype", "location", "telephone", "mob", "instant", "google"));
    	addPerson(new CloudPersonInfo("John", "Lennon", "john.lennon", "password", false, null, "skype", "location", "telephone", "mob", "instant", "google"));
    	addPerson(new CloudPersonInfo("George", "Harrison", "george.harrison", "password", false, null, "skype", "location", "telephone", "mob", "instant", "google"));
    	addPerson(new CloudPersonInfo("David", "Bowie", "david.bowie", "password", false, null, "skype", "location", "telephone", "mob", "instant", "google"));
    	addPerson(new CloudPersonInfo("Ford", "Prefect", "ford.prefect", "password", false, null, "skype", "location", "telephone", "mob", "instant", "google"));
	}
}
