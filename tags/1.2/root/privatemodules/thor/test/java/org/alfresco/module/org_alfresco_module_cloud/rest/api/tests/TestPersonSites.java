package org.alfresco.module.org_alfresco_module_cloud.rest.api.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.alfresco.repo.tenant.TenantUtil;
import org.alfresco.repo.tenant.TenantUtil.TenantRunAsWork;
import org.alfresco.rest.api.tests.RepoService.TestNetwork;
import org.alfresco.rest.api.tests.RepoService.TestPerson;
import org.alfresco.rest.api.tests.RepoService.TestSite;
import org.alfresco.rest.api.tests.client.PublicApiClient.ListResponse;
import org.alfresco.rest.api.tests.client.PublicApiClient.Paging;
import org.alfresco.rest.api.tests.client.PublicApiClient.Sites;
import org.alfresco.rest.api.tests.client.PublicApiException;
import org.alfresco.rest.api.tests.client.RequestContext;
import org.alfresco.rest.api.tests.client.data.MemberOfSite;
import org.alfresco.rest.api.tests.client.data.SiteRole;
import org.alfresco.service.cmr.site.SiteVisibility;
import org.alfresco.test_category.PublicApiTestsCategory;
import org.alfresco.util.GUID;
import org.apache.commons.httpclient.HttpStatus;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(PublicApiTestsCategory.class)
public class TestPersonSites extends CloudTestApi
{
	@Test
	public void testPersonSites() throws Exception
	{
		Iterator<TestNetwork> networksIt = getTestFixture().getNetworksIt();

		assertTrue(networksIt.hasNext());
		final TestNetwork network1 = networksIt.next();
		
		assertTrue(networksIt.hasNext());
		final TestNetwork network2 = networksIt.next();

		// create a user

		final List<TestPerson> people = new ArrayList<TestPerson>(1);

		// Create some users
		TenantUtil.runAsSystemTenant(new TenantRunAsWork<Void>()
		{
			@Override
			public Void doWork() throws Exception
			{
				TestPerson person = network1.createUser();
				people.add(person);
				person = network1.createUser();
				people.add(person);

				return null;
			}
		}, network1.getId());
		
		TenantUtil.runAsSystemTenant(new TenantRunAsWork<Void>()
		{
			@Override
			public Void doWork() throws Exception
			{
				TestPerson person = network2.createUser();
				people.add(person);

				return null;
			}
		}, network2.getId());

		final TestPerson person1 = people.get(0);
		final TestPerson person2 = people.get(1);
		final TestPerson person3 = people.get(2);
		
		// ...and some sites
		TenantUtil.runAsUserTenant(new TenantRunAsWork<Void>()
		{
			@Override
			public Void doWork() throws Exception
			{
				TestSite site = network1.createSite(SiteVisibility.PUBLIC);
				site.inviteToSite(person1.getId(), SiteRole.SiteContributor);
				
				site = network1.createSite(SiteVisibility.MODERATED);
				site.inviteToSite(person1.getId(), SiteRole.SiteContributor);
				
				site = network1.createSite(SiteVisibility.PRIVATE);
				site.inviteToSite(person1.getId(), SiteRole.SiteConsumer);
				
				site = network1.createSite(SiteVisibility.PUBLIC);
				site.inviteToSite(person1.getId(), SiteRole.SiteManager);

				site = network1.createSite(SiteVisibility.PRIVATE);
				site.inviteToSite(person1.getId(), SiteRole.SiteCollaborator);

				return null;
			}
		}, person2.getId(), network1.getId());

		List<MemberOfSite> expectedSites = network1.getSiteMemberships(person1.getId());

		Sites sitesProxy = publicApiClient.sites();
		
		// Test Case cloud-1487
		
		// unknown user
		try
		{
			publicApiClient.setRequestContext(new RequestContext(network1.getId(), person1.getId()));

			sitesProxy.getPersonSites(GUID.generate(), null);
			fail("");
		}
		catch(PublicApiException e)
		{
			assertEquals(HttpStatus.SC_NOT_FOUND, e.getHttpResponse().getStatusCode());
		}

		// Test Case cloud-2200
		// Test Case cloud-2213
		// user should be able to list their sites
		{
			int skipCount = 0;
			int maxItems = 2;
			Paging paging = getPaging(skipCount, maxItems, expectedSites.size(), null);
			publicApiClient.setRequestContext(new RequestContext(network1.getId(), person1.getId()));
			ListResponse<MemberOfSite> resp = sitesProxy.getPersonSites(person1.getId(), createParams(paging, null));
			checkList(expectedSites.subList(skipCount, skipCount + paging.getExpectedPaging().getCount()), paging.getExpectedPaging(), resp);
		}
		
		{
			int skipCount = 2;
			int maxItems = 8;
			Paging paging = getPaging(skipCount, maxItems, expectedSites.size(), null);
			publicApiClient.setRequestContext(new RequestContext(network1.getId(), person1.getId()));
			ListResponse<MemberOfSite> resp = sitesProxy.getPersonSites(person1.getId(), createParams(paging, null));
			checkList(expectedSites.subList(skipCount, skipCount + paging.getExpectedPaging().getCount()), paging.getExpectedPaging(), resp);
		}
		
		// "-me-" user
		{
			int skipCount = 0;
			int maxItems = 2;
			Paging paging = getPaging(skipCount, maxItems, expectedSites.size(), null);
			publicApiClient.setRequestContext(new RequestContext(network1.getId(), person1.getId()));
			ListResponse<MemberOfSite> resp = sitesProxy.getPersonSites(org.alfresco.rest.api.People.DEFAULT_USER, createParams(paging, null));
			checkList(expectedSites.subList(skipCount, skipCount + paging.getExpectedPaging().getCount()), paging.getExpectedPaging(), resp);
		}

		// a user in another tenant should not be able to list a user's sites
		try
		{
			int skipCount = 0;
			int maxItems = 2;
			Paging paging = getPaging(skipCount, maxItems, expectedSites.size(), null);
			publicApiClient.setRequestContext(new RequestContext(network1.getId(), person3.getId()));
			sitesProxy.getPersonSites(person1.getId(), createParams(paging, null));
			fail("");
		}
		catch(PublicApiException e)
		{
			assertEquals(HttpStatus.SC_UNAUTHORIZED, e.getHttpResponse().getStatusCode());
		}
		
		// Test case cloud-1488
		{
			MemberOfSite memberOfSite = expectedSites.get(0);

			publicApiClient.setRequestContext(new RequestContext(network1.getId(), person1.getId()));
			MemberOfSite ret = sitesProxy.getPersonSite(person1.getId(), memberOfSite.getSiteId());
			memberOfSite.expected(ret);
		}

		try
		{
			MemberOfSite memberOfSite = expectedSites.get(0);

			publicApiClient.setRequestContext(new RequestContext(network1.getId(), person1.getId()));
			sitesProxy.getPersonSite(GUID.generate(), memberOfSite.getSiteId());
			fail();
		}
		catch(PublicApiException e)
		{
			assertEquals(HttpStatus.SC_NOT_FOUND, e.getHttpResponse().getStatusCode());
		}

		try
		{
			publicApiClient.setRequestContext(new RequestContext(network1.getId(), person1.getId()));
			sitesProxy.getPersonSite(person1.getId(), GUID.generate());
			fail();
		}
		catch(PublicApiException e)
		{
			assertEquals(HttpStatus.SC_NOT_FOUND, e.getHttpResponse().getStatusCode());
		}

		// Test Case cloud-1487
		// unknown person id
		try
		{
			MemberOfSite memberOfSite = expectedSites.get(0);

			publicApiClient.setRequestContext(new RequestContext(network1.getId(), person1.getId()));
			sitesProxy.getPersonSite(GUID.generate(), memberOfSite.getSiteId());
			fail();
		}
		catch(PublicApiException e)
		{
			assertEquals(HttpStatus.SC_NOT_FOUND, e.getHttpResponse().getStatusCode());
		}
		
		try
		{
			publicApiClient.setRequestContext(new RequestContext(network1.getId(), person1.getId()));
			sitesProxy.getPersonSite(person1.getId(), GUID.generate());
			fail();
		}
		catch(PublicApiException e)
		{
			assertEquals(HttpStatus.SC_NOT_FOUND, e.getHttpResponse().getStatusCode());
		}
		
		// TODO
		// person from external network listing user sites
		
		// Test Case cloud-1966
		// Not allowed methods
		try
		{
			MemberOfSite memberOfSite = expectedSites.get(0);

			publicApiClient.setRequestContext(new RequestContext(network1.getId(), person1.getId()));
			sitesProxy.create("people", person1.getId(), "sites", memberOfSite.getSiteId(), null, "Unable to POST to a person site");
			fail();
		}
		catch(PublicApiException e)
		{
			assertEquals(HttpStatus.SC_METHOD_NOT_ALLOWED, e.getHttpResponse().getStatusCode());
		}

		try
		{
			publicApiClient.setRequestContext(new RequestContext(network1.getId(), person1.getId()));
			sitesProxy.create("people", person1.getId(), "sites", null, null, "Unable to POST to person sites");
			fail();
		}
		catch(PublicApiException e)
		{
			assertEquals(HttpStatus.SC_METHOD_NOT_ALLOWED, e.getHttpResponse().getStatusCode());
		}
		
		try
		{
			MemberOfSite memberOfSite = expectedSites.get(0);

			publicApiClient.setRequestContext(new RequestContext(network1.getId(), person1.getId()));
			sitesProxy.update("people", person1.getId(), "sites", memberOfSite.getSiteId(), null, "Unable to PUT a person site");
			fail();
		}
		catch(PublicApiException e)
		{
			assertEquals(HttpStatus.SC_METHOD_NOT_ALLOWED, e.getHttpResponse().getStatusCode());
		}

		try
		{
			publicApiClient.setRequestContext(new RequestContext(network1.getId(), person1.getId()));
			sitesProxy.update("people", person1.getId(), "sites", null, null, "Unable to PUT person sites");
			fail();
		}
		catch(PublicApiException e)
		{
			assertEquals(HttpStatus.SC_METHOD_NOT_ALLOWED, e.getHttpResponse().getStatusCode());
		}
		
//		try
//		{
//			MemberOfSite memberOfSite = expectedSites.get(0);
//
//			publicApiClient.setRequestContext(new RequestContext(network1.getId(), person1.getId()));
//			sitesProxy.remove("people", person1.getId(), "sites", memberOfSite.getSiteId(), "Unable to DELETE a person site");
//			fail();
//		}
//		catch(PublicApiException e)
//		{
//			assertEquals(HttpStatus.SC_METHOD_NOT_ALLOWED, e.getHttpResponse().getStatusCode());
//		}

		try
		{
			publicApiClient.setRequestContext(new RequestContext(network1.getId(), person1.getId()));
			sitesProxy.remove("people", person1.getId(), "sites", null, "Unable to DELETE person sites");
			fail();
		}
		catch(PublicApiException e)
		{
			assertEquals(HttpStatus.SC_METHOD_NOT_ALLOWED, e.getHttpResponse().getStatusCode());
		}
	}
}
