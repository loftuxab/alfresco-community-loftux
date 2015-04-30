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
import org.alfresco.rest.api.tests.client.HttpResponse;
import org.alfresco.rest.api.tests.client.PublicApiClient.ListResponse;
import org.alfresco.rest.api.tests.client.PublicApiClient.Paging;
import org.alfresco.rest.api.tests.client.PublicApiClient.People;
import org.alfresco.rest.api.tests.client.PublicApiException;
import org.alfresco.rest.api.tests.client.RequestContext;
import org.alfresco.rest.api.tests.client.data.PersonNetwork;
import org.alfresco.test_category.PublicApiTestsCategory;
import org.alfresco.util.GUID;
import org.apache.commons.httpclient.HttpStatus;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(PublicApiTestsCategory.class)
public class TestNetworks extends CloudTestApi
{
	/**
	 * Test http://<host>:<port>/alfresco/a i.e. tenant servlet root - should return user's networks
	 *
	 */
	@Test
	public void testIndex() throws Exception
	{
		final TestNetwork network = getTestFixture().getRandomNetwork();
    	Iterator<TestPerson> personIt = network.getPeople().iterator();
    	final TestPerson person = personIt.next();

    	RequestContext rc = new RequestContext(null, person.getId());
    	publicApiClient.setRequestContext(rc);

		HttpResponse response = publicApiClient.delete(null, null, null, null, null);
		assertEquals(404, response.getStatusCode());
		
		response = publicApiClient.put(null, null, null, null, null, null, null);
		assertEquals(404, response.getStatusCode());

		response = publicApiClient.post(null, null, null, null, null, null);
		assertEquals(404, response.getStatusCode());

		List<PersonNetwork> expectedNetworkMembers = person.getNetworkMemberships();
		int expectedTotal = expectedNetworkMembers.size();

		{
			// GET / - users networks
			Paging paging = getPaging(0, Integer.MAX_VALUE, expectedTotal, expectedTotal);
			publicApiClient.setRequestContext(new RequestContext("-default-", person.getId()));
			response = publicApiClient.index(createParams(paging, null));
			ListResponse<PersonNetwork> resp = PersonNetwork.parseNetworkMembers(response.getJsonResponse());
			assertEquals(200, response.getStatusCode());
	
			checkList(new ArrayList<PersonNetwork>(expectedNetworkMembers), paging.getExpectedPaging(), resp);
		}
	}

	@Test
	public void testPersonNetworks() throws Exception
	{
		final List<TestPerson> people = new ArrayList<TestPerson>(3);
		final List<TestNetwork> networks = new ArrayList<TestNetwork>();

		// create some networks
		for(int i = 0; i < 5; i++)
		{
			String networkId = "network" + i;
			TestNetwork network = repoService.createNetworkWithAlias(networkId, true);
			network.create();
			networks.add(network);
		}

		// do we have all the networks created?
		assertEquals(5, networks.size());

		final TestNetwork network1 = networks.get(0);
		final TestNetwork network2 = networks.get(1);

		// create a couple of users in one of the networks
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
		
		// create a user in another network
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

		// invite a user to all networks
		for(int i = 1; i < 5; i++)
		{
			final TestNetwork network = networks.get(i);
			TenantUtil.runAsSystemTenant(new TenantRunAsWork<Void>()
			{
				@Override
				public Void doWork() throws Exception
				{
					network.inviteUser(person1.getId());
					return null;
				}
			}, network.getId());
		}

		People peopleProxy = publicApiClient.people();

		// user from another network
		{
			List<PersonNetwork> networksMemberships = TenantUtil.runAsUserTenant(new TenantRunAsWork<List<PersonNetwork>>()
			{
				@Override
				public List<PersonNetwork> doWork() throws Exception
				{
					List<PersonNetwork> networksMemberships = person1.getNetworkMemberships();
					return networksMemberships;
				}
			}, person3.getId(), network2.getId());

			publicApiClient.setRequestContext(new RequestContext("-default-", person3.getId()));
			
			try
			{
				int skipCount = 0;
				int maxItems = 2;
				Paging paging = getPaging(skipCount, maxItems, networksMemberships.size(), networksMemberships.size());
				peopleProxy.getNetworkMemberships(person1.getId(), createParams(paging, null));

				fail();
			}
			catch(PublicApiException e)
			{
				assertEquals(HttpStatus.SC_NOT_FOUND, e.getHttpResponse().getStatusCode());
			}
		}
		
		// user from the same network
		{
			List<PersonNetwork> networksMemberships = TenantUtil.runAsUserTenant(new TenantRunAsWork<List<PersonNetwork>>()
			{
				@Override
				public List<PersonNetwork> doWork() throws Exception
				{
					List<PersonNetwork> networksMemberships = person1.getNetworkMemberships();
					return networksMemberships;
				}
			}, person2.getId(), network1.getId());

			publicApiClient.setRequestContext(new RequestContext("-default-", person2.getId()));

			try
			{
				int skipCount = 0;
				int maxItems = 2;
				Paging paging = getPaging(skipCount, maxItems, networksMemberships.size(), networksMemberships.size());
				peopleProxy.getNetworkMemberships(person1.getId(), createParams(paging, null));
				
				fail("");
			}
			catch(PublicApiException e)
			{
				assertEquals(HttpStatus.SC_NOT_FOUND, e.getHttpResponse().getStatusCode());
			}
		}

		List<PersonNetwork> networksMemberships = TenantUtil.runAsUserTenant(new TenantRunAsWork<List<PersonNetwork>>()
		{
			@Override
			public List<PersonNetwork> doWork() throws Exception
			{
				List<PersonNetwork> networksMemberships = person1.getNetworkMemberships();
				return networksMemberships;
			}
		}, person1.getId(), network1.getId());

		// Test Case cloud-2203
		// Test Case cloud-1498
		// test paging
		{
			publicApiClient.setRequestContext(new RequestContext("-default-", person1.getId()));
			
			int skipCount = 0;
			int maxItems = 2;
			Paging paging = getPaging(skipCount, maxItems, networksMemberships.size(), networksMemberships.size());
			ListResponse<PersonNetwork> resp = peopleProxy.getNetworkMemberships(person1.getId(), createParams(paging, null));
			checkList(networksMemberships.subList(skipCount, skipCount + paging.getExpectedPaging().getCount()), paging.getExpectedPaging(), resp);
		}
		
		{
			publicApiClient.setRequestContext(new RequestContext("-default-", person1.getId()));
			
			int skipCount = 2;
			int maxItems = Integer.MAX_VALUE;
			Paging paging = getPaging(skipCount, maxItems, networksMemberships.size(), networksMemberships.size());
			ListResponse<PersonNetwork> resp = peopleProxy.getNetworkMemberships(person1.getId(), createParams(paging, null));
			checkList(networksMemberships.subList(skipCount, skipCount + paging.getExpectedPaging().getCount()), paging.getExpectedPaging(), resp);
		}
		
		// "-me-" user
		{
			publicApiClient.setRequestContext(new RequestContext("-default-", person1.getId()));

			int skipCount = 2;
			int maxItems = Integer.MAX_VALUE;
			Paging paging = getPaging(skipCount, maxItems, networksMemberships.size(), networksMemberships.size());
			ListResponse<PersonNetwork> resp = peopleProxy.getNetworkMemberships(org.alfresco.rest.api.People.DEFAULT_USER, createParams(paging, null));
			checkList(networksMemberships.subList(skipCount, skipCount + paging.getExpectedPaging().getCount()), paging.getExpectedPaging(), resp);
		}
		
		// unknown person id
		try
		{
			List<PersonNetwork> networkMemberships = TenantUtil.runAsUserTenant(new TenantRunAsWork<List<PersonNetwork>>()
			{
				@Override
				public List<PersonNetwork> doWork() throws Exception
				{
					List<PersonNetwork> networkMemberships = person1.getNetworkMemberships();
					return networkMemberships;
				}
			}, person1.getId(), network1.getId());

			publicApiClient.setRequestContext(new RequestContext("-default-", person1.getId()));
			
			int skipCount = 0;
			int maxItems = 2;
			Paging expectedPaging = getPaging(skipCount, maxItems, networkMemberships.size(), networkMemberships.size());
			peopleProxy.getNetworkMemberships("invalidUser", createParams(expectedPaging, null));
			fail("");
		}
		catch(PublicApiException e)
		{
			assertEquals(HttpStatus.SC_NOT_FOUND, e.getHttpResponse().getStatusCode());
		}
		
		// invalid caller authentication
		try
		{
			List<PersonNetwork> networkMemberships = TenantUtil.runAsUserTenant(new TenantRunAsWork<List<PersonNetwork>>()
			{
				@Override
				public List<PersonNetwork> doWork() throws Exception
				{
					List<PersonNetwork> networkMemberships = person1.getNetworkMemberships();
					return networkMemberships;
				}
			}, person1.getId(), network1.getId());

			publicApiClient.setRequestContext(new RequestContext("-default-", person1.getId(), GUID.generate()));
			
			int skipCount = 0;
			int maxItems = 2;
			Paging expectedPaging = getPaging(skipCount, maxItems, networkMemberships.size(), networkMemberships.size());
			peopleProxy.getNetworkMemberships(person1.getId(), createParams(expectedPaging, null));
			fail("");
		}
		catch(PublicApiException e)
		{
			assertEquals(HttpStatus.SC_UNAUTHORIZED, e.getHttpResponse().getStatusCode());
		}
		
		// Test Case cloud-1499
		// unknown person id
		try
		{
			List<PersonNetwork> networkMemberships = TenantUtil.runAsUserTenant(new TenantRunAsWork<List<PersonNetwork>>()
			{
				@Override
				public List<PersonNetwork> doWork() throws Exception
				{
					List<PersonNetwork> networkMemberships = person1.getNetworkMemberships();
					return networkMemberships;
				}
			}, person1.getId(), network1.getId());
			assertTrue(networkMemberships.size() > 0);
			PersonNetwork network = networkMemberships.get(0);

			publicApiClient.setRequestContext(new RequestContext("-default-", person1.getId()));
			peopleProxy.getNetworkMembership("invalidUser", network.getId());
			fail("");
		}
		catch(PublicApiException e)
		{
			assertEquals(HttpStatus.SC_NOT_FOUND, e.getHttpResponse().getStatusCode());
		}
		
		// invalid caller authentication
		try
		{
			List<PersonNetwork> networkMemberships = TenantUtil.runAsUserTenant(new TenantRunAsWork<List<PersonNetwork>>()
			{
				@Override
				public List<PersonNetwork> doWork() throws Exception
				{
					List<PersonNetwork> networkMemberships = person1.getNetworkMemberships();
					return networkMemberships;
				}
			}, person1.getId(), network1.getId());
			assertTrue(networkMemberships.size() > 0);
			PersonNetwork network = networkMemberships.get(0);

			publicApiClient.setRequestContext(new RequestContext("-default-", person1.getId(), GUID.generate()));
			peopleProxy.getNetworkMembership(person1.getId(), network.getId());
			fail("");
		}
		catch(PublicApiException e)
		{
			assertEquals(HttpStatus.SC_UNAUTHORIZED, e.getHttpResponse().getStatusCode());
		}
		
		// incorrect network id
		try
		{
			publicApiClient.setRequestContext(new RequestContext("-default-", person1.getId()));
			peopleProxy.getNetworkMembership(person1.getId(), GUID.generate());
			fail("");
		}
		catch(PublicApiException e)
		{
			assertEquals(HttpStatus.SC_NOT_FOUND, e.getHttpResponse().getStatusCode());
		}
		
		// 1969
		// not allowed methods
		// POST, POST networkId, PUT, PUT networkId, DELETE, DELETE networkId
		try
		{
			PersonNetwork pn = new PersonNetwork(GUID.generate());

			publicApiClient.setRequestContext(new RequestContext("-default-", person1.getId()));
			peopleProxy.create("people", person1.getId(), "networks", null, pn.toJSON().toString(), "Unable to POST to person networks");
			fail("");
		}
		catch(PublicApiException e)
		{
			assertEquals(HttpStatus.SC_METHOD_NOT_ALLOWED, e.getHttpResponse().getStatusCode());
		}
		
		try
		{
			PersonNetwork pn = networksMemberships.get(0);

			publicApiClient.setRequestContext(new RequestContext("-default-", person1.getId()));
			peopleProxy.create("people", person1.getId(), "networks", pn.getId(), pn.toJSON().toString(), "Unable to POST to a person network");
			fail("");
		}
		catch(PublicApiException e)
		{
			assertEquals(HttpStatus.SC_METHOD_NOT_ALLOWED, e.getHttpResponse().getStatusCode());
		}

		try
		{
			PersonNetwork pn = new PersonNetwork(GUID.generate());

			publicApiClient.setRequestContext(new RequestContext("-default-", person1.getId()));
			peopleProxy.update("people", person1.getId(), "networks", null, pn.toJSON().toString(), "Unable to PUT person networks");
			fail("");
		}
		catch(PublicApiException e)
		{
			assertEquals(HttpStatus.SC_METHOD_NOT_ALLOWED, e.getHttpResponse().getStatusCode());
		}
		
		try
		{
			PersonNetwork pn = networksMemberships.get(0);

			publicApiClient.setRequestContext(new RequestContext("-default-", person1.getId()));
			peopleProxy.update("people", person1.getId(), "networks", pn.getId(), pn.toJSON().toString(), "Unable to PUT a person network");
			fail("");
		}
		catch(PublicApiException e)
		{
			assertEquals(HttpStatus.SC_METHOD_NOT_ALLOWED, e.getHttpResponse().getStatusCode());
		}
		
		try
		{
			publicApiClient.setRequestContext(new RequestContext("-default-", person1.getId()));
			peopleProxy.remove("people", person1.getId(), "networks", null, "Unable to DELETE person networks");
			fail("");
		}
		catch(PublicApiException e)
		{
			assertEquals(HttpStatus.SC_METHOD_NOT_ALLOWED, e.getHttpResponse().getStatusCode());
		}
		
		try
		{
			PersonNetwork pn = networksMemberships.get(0);

			publicApiClient.setRequestContext(new RequestContext("-default-", person1.getId()));
			peopleProxy.remove("people", person1.getId(), "networks", pn.getId(), "Unable to DELETE a person network");
			fail("");
		}
		catch(PublicApiException e)
		{
			assertEquals(HttpStatus.SC_METHOD_NOT_ALLOWED, e.getHttpResponse().getStatusCode());
		}

		// user not a member of the network
		try
		{
			publicApiClient.setRequestContext(new RequestContext(network1.getId(), person3.getId()));

			int skipCount = 0;
			int maxItems = 2;
			Paging expectedPaging = getPaging(skipCount, maxItems);
			peopleProxy.getNetworkMemberships(person1.getId(), createParams(expectedPaging, null));
		}
		catch(PublicApiException e)
		{
			assertEquals(HttpStatus.SC_UNAUTHORIZED, e.getHttpResponse().getStatusCode());
		}
	}

}
