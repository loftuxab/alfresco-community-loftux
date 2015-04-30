package org.alfresco.module.org_alfresco_module_cloud.rest.api.tests;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.alfresco.model.ContentModel;
import org.alfresco.module.org_alfresco_module_cloud.accounts.Account;
import org.alfresco.module.org_alfresco_module_cloud.accounts.AccountService;
import org.alfresco.module.org_alfresco_module_cloud.accounts.AccountService.AccountMembershipType;
import org.alfresco.module.org_alfresco_module_cloud.accounts.AccountType;
import org.alfresco.module.org_alfresco_module_cloud.directory.DirectoryService;
import org.alfresco.module.org_alfresco_module_cloud.registration.RegistrationService;
import org.alfresco.module.org_alfresco_module_cloud.util.CloudUtil;
import org.alfresco.query.PagingRequest;
import org.alfresco.query.PagingResults;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.tenant.Network;
import org.alfresco.repo.tenant.NetworksService;
import org.alfresco.repo.tenant.TenantService;
import org.alfresco.repo.tenant.TenantUtil;
import org.alfresco.repo.tenant.TenantUtil.TenantRunAsWork;
import org.alfresco.repo.transaction.RetryingTransactionInterceptor;
import org.alfresco.rest.api.tests.PersonInfo;
import org.alfresco.rest.api.tests.RepoService;
import org.alfresco.rest.api.tests.client.data.Company;
import org.alfresco.rest.api.tests.client.data.NetworkImpl;
import org.alfresco.rest.api.tests.client.data.Person;
import org.alfresco.rest.api.tests.client.data.PersonNetwork;
import org.alfresco.rest.api.tests.client.data.SiteRole;
import org.alfresco.service.cmr.site.SiteInfo;
import org.alfresco.service.cmr.site.SiteVisibility;
import org.alfresco.service.namespace.QName;
import org.alfresco.service.transaction.TransactionService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.transaction.PlatformTransactionManager;

public class CloudRepoService extends RepoService
{
	private static final Log logger = LogFactory.getLog(CloudRepoService.class);
	
	private DirectoryService directoryService;
    private AccountService accountService;
    private NetworksService networksService;
    private RegistrationService registrationService;

    public static CloudRepoService createCloudRepoService(ApplicationContext applicationContext) throws Exception
    {
        RetryingTransactionInterceptor txInterceptor = new RetryingTransactionInterceptor();
        txInterceptor.setBeanFactory(applicationContext);
        TransactionService transactionService = (TransactionService) applicationContext.getBean("TransactionService");
        txInterceptor.setTransactionService(transactionService);
        PlatformTransactionManager transactionManager = (PlatformTransactionManager) applicationContext.getBean("transactionManager");
        txInterceptor.setTransactionManager(transactionManager);
        Properties txAttrsProps = new Properties();
        txAttrsProps.setProperty("*", "PROPAGATION_REQUIRED");
        txInterceptor.setTransactionAttributes(txAttrsProps);
        txInterceptor.afterPropertiesSet();
        CloudRepoService target = new CloudRepoService(applicationContext);
        ProxyFactory proxyFactory = new ProxyFactory(target);
        proxyFactory.addAdvice(txInterceptor);
        return (CloudRepoService) proxyFactory.getProxy();
    }
    
    // Needed by ProxyFactory/CGLIB.
    public CloudRepoService()
    {
        super();
    }
    
    public CloudRepoService(ApplicationContext applicationContext) throws Exception
    {
		super(applicationContext);
    	this.accountService = (AccountService)applicationContext.getBean("AccountService");
    	this.directoryService = (DirectoryService)applicationContext.getBean("directoryService");
    	this.networksService = (NetworksService)applicationContext.getBean("networksService");
    	this.registrationService = (RegistrationService)applicationContext.getBean("RegistrationService");
    }

	public Account getAccount(TestNetwork testAccount)
	{
		return accountService.getAccountByDomain(testAccount.getId());
	}

	@Override
    public TestNetwork createNetworkWithAlias(String alias, boolean enabled)
    {
        String networkId = alias + "-" + System.currentTimeMillis();
    	TestNetwork network = new CloudNetwork(networkId, AccountType.ENTERPRISE_NETWORK_ACCOUNT_TYPE, enabled);
    	return network;
    }

	@Override
    public TestNetwork createNetwork(String networkId, boolean enabled)
    {
    	CloudNetwork network = new CloudNetwork(networkId, AccountType.ENTERPRISE_NETWORK_ACCOUNT_TYPE, enabled);
    	return network;
    }

    public class CloudNetwork extends TestNetwork implements Comparable<TestNetwork>
	{
		private static final long serialVersionUID = 719179749542243627L;

		private Long accountId = Long.valueOf(-1);
		private int type;
		private Set<String> networkAdmins = new HashSet<String>();

		public CloudNetwork(String domain, int type, boolean enabled)
		{
			super(domain, enabled);
			this.type = type;
		}

		@Override
		public void create()
		{
			final Account account = accountService.createAccount(getId(), getType(), getIsEnabled());
			setAccountId(account.getId());
//			cloudContext.addAccount(account);

			logger.debug("Created cloud network " + getId());
		}
		
		@Override
		public void addExternalUser(String personId)
		{
			boolean isMember = false;
            List<Account> secondaryNetworks = registrationService.getSecondaryAccounts(personId);
            for(Account secondaryNetwork : secondaryNetworks)
            {
            	String networkId = secondaryNetwork.getName();
            	if(getId().equals(networkId))
            	{
            		isMember = true;
            	}
            }
            
            if(!isMember)
            {
            	registrationService.addUser(accountService.getAccountByDomain(getId()).getId(), personId);
            }
		}

		@Override
		public TestSite homeSite(TestPerson person)
		{
			TestSite site = null;

			String email = person.getId();
			final String siteShortName = CloudUtil.generateSiteShortName(email);
			SiteInfo siteInfo = siteService.getSite(siteShortName);
			if(siteInfo != null)
			{
				site = new TestSite(person.getDefaultAccount(), siteShortName, siteInfo.getNodeRef().getId(), person.getFirstName() + " " + person.getLastName() + "'s Home",
					"", SiteVisibility.PRIVATE);
			}
			return site;
		}

		@Override
		public TestPerson createUser(final PersonInfo personInfo)
		{
			String email = publicApiContext.createUserName(personInfo.getUsername(), getId());
			final CloudTestPerson testPerson = new CloudTestPerson(personInfo.getFirstName(), personInfo.getLastName(), email, personInfo.getPassword(),
					personInfo.getCompany(), personInfo.isNetworkAdmin(), this, personInfo.getSkype(), personInfo.getLocation(), personInfo.getTel(),
					personInfo.getMob(), personInfo.getInstantmsg(), personInfo.getGoogle());
			final Map<QName, Serializable> props = testPerson.getProperties();

			registrationService.createUser(testPerson.getId(), testPerson.getFirstName(), testPerson.getLastName(), testPerson.getPassword());
			if(props != null)
			{
				TenantUtil.runAsSystemTenant(new TenantRunAsWork<Void>()
				{
					@Override
					public Void doWork() throws Exception
					{
						if(props.containsKey(ContentModel.PROP_USERNAME))
						{
							props.remove(ContentModel.PROP_USERNAME);
						}
						personService.setPersonProperties(testPerson.getId(), props);
						return null;
					}
				}, getId());
			}

			TestSite homeSite = TenantUtil.runAsUserTenant(new TenantRunAsWork<TestSite>()
			{
				@Override
				public TestSite doWork() throws Exception
				{
					TestSite homeSite = homeSite(testPerson);
					return homeSite;
				}
			}, email, getId());

			testPerson.addSiteMembership(homeSite, SiteRole.SiteManager);

			logger.debug("Created cloud person " + testPerson.getId());
			
			Account account = getAccount(this);

			if(testPerson.isNetworkAdmin())
			{
				registrationService.promoteUserToNetworkAdmin(account.getId(), testPerson.getId());
				addNetworkAdmin(testPerson.getId());
			}
			publicApiContext.addUser(testPerson.getId());
			addPerson(testPerson);
			CloudRepoService.this.addPerson(testPerson);
			
			return testPerson;
		}

		@Override
		public void inviteUser(final String user)
		{
			TenantUtil.runAsUserTenant(new TenantRunAsWork<Void>()
			{
				@Override
				public Void doWork() throws Exception
				{
					logger.debug("Adding " + user + " as secondary user in account " + getId());
					registrationService.addUser(getAccountId(), user);
					logger.debug("Added " + user + " as secondary user in account " + getId());

					return null;
				}
			}, AuthenticationUtil.getSystemUserName(), TenantService.DEFAULT_DOMAIN);
		}

		@Override
		public Collection<TestSite> getHomeSites()
		{
			Collection<TestSite> sites = new ArrayList<TestSite>();
			Collection<TestPerson> testPeople = getPeople();
			for(TestPerson testPerson : testPeople)
			{
				TestSite homeSite = homeSite(testPerson);
				sites.add(homeSite);
			}

			return sites;
		}
		
		@Override
		public Long getAccountId()
		{
			return accountId;
		}

		@Override
		public void setAccountId(Long accountId)
		{
			this.accountId = accountId;
		}

		@Override
		public int getType()
		{
			return type;
		}
		
		@Override
		public void addNetworkAdmin(String email)
		{
			networkAdmins.add(email);
		}

		@Override
		public Set<String> getNetworkAdmins()
		{
			return networkAdmins;
		}
		
		@Override
		public Set<String> getNonNetworkAdmins()
		{
			Set<String> nonNetworkAdmins = new HashSet<String>(people.keySet());
			nonNetworkAdmins.removeAll(networkAdmins);
			return nonNetworkAdmins;
		}

		@Override
		public String toString()
		{
			return "CloudNetwork [accountId=" + accountId + ", type=" + type
					+ ", networkAdmins=" + networkAdmins + ", people=" + people
					+ ", sites=" + sites + ", publicSites=" + publicSites + "]";
		}
	}
    
	public class CloudTestPerson extends TestPerson
    {
		private static final long serialVersionUID = 4038390056182705588L;

    	private boolean networkAdmin;

		public CloudTestPerson(String firstName, String lastName, String username, String password, Company company, boolean networkAdmin, TestNetwork defaultAccount, String skype, String location, String tel,
				String mob, String instantmsg, String google)
		{
			super(firstName, lastName, username, password, company, defaultAccount, skype, location, tel, mob, instantmsg, google);
			this.networkAdmin = networkAdmin;
		}

		public void addSiteMembership(TestSite site, SiteRole siteRole)
		{
			super.addSiteMembership(site, siteRole);
		}

		@Override
		public boolean isNetworkAdmin()
		{
			return networkAdmin;
		}
		
		@Override
		public List<PersonNetwork> getNetworkMemberships()
		{
			final String personId = getId();
			final String runAsNetworkId = Person.getNetworkId(personId);
			return TenantUtil.runAsUserTenant(new TenantRunAsWork<List<PersonNetwork>>()
			{
				@Override
				public List<PersonNetwork> doWork() throws Exception
				{
					List<PersonNetwork> members = new ArrayList<PersonNetwork>();
					
					PagingResults<Network> networks = networksService.getNetworks(new PagingRequest(0, Integer.MAX_VALUE));
					for(Network network : networks.getPage())
					{
			            Account account = accountService.getAccountByDomain(network.getTenantDomain());
						AccountMembershipType type = directoryService.getAccountMembershipType(personId, account.getId());
			            NetworkImpl restNetwork = new CloudNetworkImpl(account);
			            PersonNetwork personNetwork = new PersonNetwork(type == AccountMembershipType.HomeNetwork, restNetwork);
						members.add(personNetwork);
					}
					return members;
				}
			}, personId, runAsNetworkId);
		}
		
		public CloudTestPerson setNetworkAdmin(boolean networkAdmin)
		{
			this.networkAdmin = networkAdmin;
			return this;
		}
		
		@Override
		public String toString()
		{
			return "CloudTestPerson [networkAdmin=" + networkAdmin
					+ ", enabled=" + enabled + ", password=" + password
					+ ", defaultAccount=" + defaultAccount.getId()
					+ ", siteMemberships=" + siteMemberships + "]";
		}
    }
}
