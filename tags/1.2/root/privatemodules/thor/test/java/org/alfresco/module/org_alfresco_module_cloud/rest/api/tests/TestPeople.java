package org.alfresco.module.org_alfresco_module_cloud.rest.api.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.Iterator;

import org.alfresco.repo.tenant.TenantUtil;
import org.alfresco.repo.tenant.TenantUtil.TenantRunAsWork;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.rest.api.tests.RepoService.SiteInformation;
import org.alfresco.rest.api.tests.RepoService.TestNetwork;
import org.alfresco.rest.api.tests.RepoService.TestSite;
import org.alfresco.rest.api.tests.client.PublicApiClient.People;
import org.alfresco.rest.api.tests.client.PublicApiException;
import org.alfresco.rest.api.tests.client.RequestContext;
import org.alfresco.rest.api.tests.client.data.Person;
import org.alfresco.rest.api.tests.client.data.SiteRole;
import org.alfresco.service.cmr.site.SiteVisibility;
import org.alfresco.test_category.PublicApiTestsCategory;
import org.apache.commons.httpclient.HttpStatus;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(PublicApiTestsCategory.class)
public class TestPeople extends CloudTestApi
{
	@Test
	public void testPeople() throws Exception
	{
		Iterator<TestNetwork> accountsIt = getTestFixture().getNetworksIt();
		final TestNetwork account1 = accountsIt.next();
		Iterator<String> personIt1 = account1.getPersonIds().iterator();
		final String person1 = personIt1.next();
    	final String person2 = personIt1.next();

		final TestNetwork account2 = accountsIt.next();
		Iterator<String> personIt2 = account2.getPersonIds().iterator();
    	final String person3 = personIt2.next();

    	People peopleProxy = publicApiClient.people();

    	// Test Case cloud-2192
    	// should be able to see oneself
    	{
			publicApiClient.setRequestContext(new RequestContext(account1.getId(), person1));
	    	Person resp = peopleProxy.getPerson(person1);
	    	Person person1Entity = repoService.getPerson(person1);
			check(person1Entity, resp);
    	}
    	
		// should be able to see another user in the same domain, and be able to see full profile
    	{
			publicApiClient.setRequestContext(new RequestContext(account1.getId(), person2));
			Person resp = peopleProxy.getPerson(person1);
	    	Person person1Entity = repoService.getPerson(person1);
			check(person1Entity, resp);
    	}
    	
    	// "-me-" user
    	{
			publicApiClient.setRequestContext(new RequestContext(account1.getId(), person1));
	    	Person resp = peopleProxy.getPerson(org.alfresco.rest.api.People.DEFAULT_USER);
	    	Person person1Entity = repoService.getPerson(person1);
			check(person1Entity, resp);
    	}

		// shouldn't be able to see another user in another domain
		publicApiClient.setRequestContext(new RequestContext(account1.getId(), person3));
		try
		{
			peopleProxy.getPerson(person1);
			fail("");
		}
		catch(PublicApiException e)
		{
			assertEquals(HttpStatus.SC_UNAUTHORIZED, e.getHttpResponse().getStatusCode());
		}

    	// invite user in another account to your site
        transactionHelper.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>()
        {
        	@SuppressWarnings("synthetic-access")
        	public Void execute() throws Throwable
        	{
        		TenantUtil.runAsUserTenant(new TenantRunAsWork<Void>()
				{
					@Override
					public Void doWork() throws Exception
					{
						account1.inviteUser(person3);

		        		String siteName = "site" + System.currentTimeMillis();
						SiteInformation siteInfo = new SiteInformation(siteName, "", "", SiteVisibility.PUBLIC);
						TestSite site = account1.createSite(siteInfo);
						site.inviteToSite(person3, SiteRole.SiteContributor);

						return null;
					}
				}, person1, account1.getId());

        		return null;
        	}
        }, false, true);

        // ...should be able to now, but not be able to see full profile
        {
			publicApiClient.setRequestContext(new RequestContext(account1.getId(), person3));
			Person resp = peopleProxy.getPerson(person1);
	    	Person person1Entity = repoService.getPerson(person1);
			check(person1Entity.restriced(), resp);
        }
	}
}
