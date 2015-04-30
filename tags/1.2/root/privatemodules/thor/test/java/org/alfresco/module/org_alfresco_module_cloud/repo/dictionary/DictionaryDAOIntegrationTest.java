/*
 * Copyright (C) 2005-2014 Alfresco Software Limited.
 *
 * This file is part of Alfresco
 *
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */
package org.alfresco.module.org_alfresco_module_cloud.repo.dictionary;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alfresco.model.ContentModel;
import org.alfresco.module.org_alfresco_module_cloud.CloudTestContext;
import org.alfresco.module.org_alfresco_module_cloud.accounts.Account;
import org.alfresco.module.org_alfresco_module_cloud.accounts.AccountService;
import org.alfresco.module.org_alfresco_module_cloud.accounts.AccountType;
import org.alfresco.module.org_alfresco_module_cloud.registration.RegistrationService;
import org.alfresco.opencmis.AlfrescoCmisServiceFactory;
import org.alfresco.opencmis.dictionary.CMISDictionaryService;
import org.alfresco.repo.dictionary.DictionaryDAO;
import org.alfresco.repo.dictionary.M2Model;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.security.authentication.AuthenticationUtil.RunAsWork;
import org.alfresco.repo.security.permissions.AccessDeniedException;
import org.alfresco.repo.tenant.TenantUtil;
import org.alfresco.repo.tenant.TenantUtil.TenantRunAsWork;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.repo.transaction.RetryingTransactionHelper.RetryingTransactionCallback;
import org.alfresco.service.cmr.dictionary.DictionaryException;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.model.FileFolderService;
import org.alfresco.service.cmr.model.FileInfo;
import org.alfresco.service.cmr.model.FileNotFoundException;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.ContentWriter;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.cmr.security.MutableAuthenticationService;
import org.alfresco.service.cmr.security.PersonService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.service.transaction.TransactionService;
import org.alfresco.test_category.OwnJVMTestsCategory;
import org.alfresco.util.Pair;
import org.apache.chemistry.opencmis.commons.enums.CmisVersion;
import org.apache.chemistry.opencmis.commons.exceptions.CmisObjectNotFoundException;
import org.apache.chemistry.opencmis.commons.server.CallContext;
import org.apache.chemistry.opencmis.commons.server.CmisService;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * 
 * @author sglover
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:alfresco/application-context.xml"})
@Category(OwnJVMTestsCategory.class)
public class DictionaryDAOIntegrationTest
{
    public static final String TEST_RESOURCE_MESSAGES = "alfresco/messages/dictionary-messages";

    @Autowired @Qualifier("DictionaryService")
    private DictionaryService service;

    @Autowired
    private DictionaryDAO dictionaryDAO;

    @Autowired @Qualifier("OpenCMISDictionaryService1.1")
    private CMISDictionaryService cmisDictionaryService;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
	private AlfrescoCmisServiceFactory factory;

    @Autowired @Qualifier("NodeService")
    private NodeService nodeService;
    
    @Autowired @Qualifier("FileFolderService")
    private FileFolderService fileFolderService;
    
    @Autowired @Qualifier("ContentService")
    private ContentService contentService;

    @Autowired @Qualifier("PersonService")
    private PersonService personService;

    @Autowired @Qualifier("SearchService")
    private SearchService searchService;

    @Autowired @Qualifier("AuthenticationService")
    private MutableAuthenticationService authenticationService;

    @Autowired @Qualifier("TransactionService")
    private TransactionService transactionService;

    @Autowired @Qualifier("AccountService")
    private AccountService accountService;

    @Autowired @Qualifier("NamespaceService")
    private NamespaceService namespaceService;

    @Autowired @Qualifier("RegistrationService")
    private RegistrationService registrationService;

    private CloudTestContext cloudContext;

    private Account account1;
	private String tenant1;
    private String tenant1Username1;
    private String tenant1Username2; // network admin

	private String tenant2;
    private String tenant2Username1;

    /**
     * This method creates a new account with the specified parameters.
     * There is no transaction handling in this method.
     * 
     * @param domain
     * @param accountName
     * @param accountTypeId
     * @return
     * @throws Exception
     */
    private Account createAccount(String domain, int type, boolean enabled) throws Exception
    {
        // This will create an account with the metadata values defined for the specified accountId.
        // See account-service-context.xml
        Account account = accountService.createAccount(domain, type, enabled);
        assertNotNull("Account was null", account);

        // Check some metadata values to make sure they're set.
        assertEquals("account name was wrong", domain, account.getName());
        assertEquals("account type was wrong", type, account.getType().getId());
        assertEquals("account domains were wrong", Arrays.asList(new String[]{domain}), account.getDomains());
        assertEquals("account enabled was wrong", enabled, account.isEnabled());
        assertNotNull("account creation date is not null", account.getCreationDate());

        cloudContext.addAccount(account);

        return account;
    }

    private void createUser(final String tenant, final String userName,
    		final String firstName, final String lastName, final boolean networkAdmin)
    {
        transactionService.getRetryingTransactionHelper().doInTransaction(
        		new RetryingTransactionHelper.RetryingTransactionCallback<Void>()
        {
            @Override
            public Void execute() throws Throwable
            {
            	AuthenticationUtil.runAsSystem(new RunAsWork<Void>()
            	{
					@Override
					public Void doWork() throws Exception
					{
		                assertFalse(authenticationService.authenticationExists(userName));
		                assertFalse(personService.personExists(userName));

		                registrationService.createUser(userName, userName, userName, "password");

		                if(networkAdmin)
		                {
		                	registrationService.promoteUserToNetworkAdmin(account1.getId(), userName);
		                }

		                cloudContext.addUser(userName);
		                cloudContext.addAccountDomain(tenant);

						return null;
					}
            		
            	});

                return null;
            }
        });
    }

    @Before
    public void before() throws Exception
    {
        cloudContext = new CloudTestContext(applicationContext);
        this.tenant1 = cloudContext.createTenantName("alfresco.com");
        this.tenant1Username1 = cloudContext.createUserName("user1" + System.currentTimeMillis(), tenant1);
        this.tenant1Username2 = cloudContext.createUserName("user2" + System.currentTimeMillis(), tenant1);
        this.tenant2 = cloudContext.createTenantName("acme.com");
        this.tenant2Username1 = cloudContext.createUserName("user1" + System.currentTimeMillis(), tenant2);

        this.account1 = createAccount(tenant1, AccountType.STANDARD_NETWORK_ACCOUNT_TYPE, true);

    	createUser(tenant1, tenant1Username1, tenant1Username1, tenant1Username1, true);
    	createUser(tenant1, tenant1Username2, tenant1Username2, tenant1Username2, false);
    	createUser(tenant2, tenant2Username1, tenant2Username1, tenant2Username1, false);
    }
    
    private NodeRef getModelsNodeRef()
    {
    	return transactionService.getRetryingTransactionHelper().doInTransaction(new RetryingTransactionCallback<NodeRef>()
    	{
			@Override
			public NodeRef execute() throws Throwable
			{
				return AuthenticationUtil.runAsSystem(new RunAsWork<NodeRef>()
				{
					@Override
					public NodeRef doWork() throws Exception
					{
						NodeRef rootNodeRef = nodeService.getRootNode(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE);
						String path = "app:company_home/app:dictionary/app:models";
				        List<NodeRef> nodeRefs = searchService.selectNodes(rootNodeRef, path , null, namespaceService, false);
				        assertEquals(1, nodeRefs.size());

						return nodeRefs.get(0);
					}
				});
			}
		}, false, true);
    }

    /** Delete any temporary accounts created during the previous test */
    @After public void cleanup() throws Exception
    {
//        cloudContext.cleanup();
    }

    private void addCustomModelToRepository(M2Model customModel)
    		throws UnsupportedEncodingException, FileNotFoundException
    {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		customModel.toXML(out);
		String modelContent = out.toString("UTF-8");

        FileInfo fileInfo = fileFolderService.create(
        		getModelsNodeRef(), "contentModel" + System.currentTimeMillis() + ".xml",
        		ContentModel.TYPE_DICTIONARY_MODEL);
        Map<QName, Serializable> properties = new HashMap<QName, Serializable>();
        properties.put(ContentModel.PROP_MODEL_ACTIVE, Boolean.TRUE);
        nodeService.setProperties(fileInfo.getNodeRef(), properties);
        ContentWriter writer = contentService.getWriter(fileInfo.getNodeRef(), ContentModel.PROP_CONTENT, true);
        writer.putContent(modelContent);
    }

    private M2Model getModel(String location, Map<String, Pair<String, String>> namespaces)
    {
		M2Model customModel = M2Model.createModel(
				Thread.currentThread().getContextClassLoader().
				getResourceAsStream(location));
		if(namespaces != null)
		{
			for(String uri : namespaces.keySet())
			{
				Pair<String, String> namespaceInfo = namespaces.get(uri);
				if(namespaceInfo != null)
				{
					customModel.removeNamespace(uri);
					String newUri = namespaceInfo.getFirst();
					String prefix = namespaceInfo.getSecond();
					customModel.createNamespace(newUri, prefix);
				}
			}
		}

		return customModel;
    }

    private static class SimpleCallContext implements CallContext
    {
    	private final Map<String, Object> contextMap = new HashMap<String, Object>();
    	private CmisVersion cmisVersion;

    	public SimpleCallContext(String user, String password, CmisVersion cmisVersion)
    	{
    		contextMap.put(USERNAME, user);
    		contextMap.put(PASSWORD, password);
    		this.cmisVersion = cmisVersion;
    	}

    	public String getBinding()
    	{
    		return BINDING_LOCAL;
    	}

    	public Object get(String key)
    	{
    		return contextMap.get(key);
    	}

    	public String getRepositoryId()
    	{
    		return (String) get(REPOSITORY_ID);
    	}

    	public String getUsername()
    	{
    		return (String) get(USERNAME);
    	}

    	public String getPassword()
    	{
    		return (String) get(PASSWORD);
    	}

    	public String getLocale()
    	{
    		return null;
    	}

    	public BigInteger getOffset()
    	{
    		return (BigInteger) get(OFFSET);
    	}

    	public BigInteger getLength()
    	{
    		return (BigInteger) get(LENGTH);
    	}

    	public boolean isObjectInfoRequired()
    	{
    		return false;
    	}

    	public File getTempDirectory()
    	{
    		return null;
    	}

    	public int getMemoryThreshold()
    	{
    		return 0;
    	}

        public long getMaxContentSize()
        {
            return Long.MAX_VALUE;
        }

        @Override
        public boolean encryptTempFiles()
        {
            return false;
        }

        @Override
        public CmisVersion getCmisVersion()
        {
            return cmisVersion;
        }
    }

    @Test
    public void testNoNetworkAdminPermission() throws Exception
    {
        TenantUtil.runAsUserTenant(new TenantRunAsWork<Void>()
        {
			@Override
			public Void doWork() throws Exception
			{
				try
				{
					Pair<String, String> namespaceInfo = new Pair<String, String>("http://www." + tenant1 + ".org/test/dictionarytest1/1.0",
							"dictionarytest1");
					M2Model customModel = getModel("dictionary/dictionarytest_model1.xml",
							Collections.singletonMap("http://www.alfresco.org/test/dictionarytest1/1.0", namespaceInfo));
					addCustomModelToRepository(customModel);
					fail("Non network admin user should not be able to do this");
				}
				catch(AccessDeniedException e)
				{
					// ok
				}

				return null;
			}
		}, tenant1Username2, tenant1);
    }

    /*
     * Test that a custom model with a namespace uri that does not contain the tenant name
     * cannot be added to the dictionary.
     */
    @Test
    public void testInvalidNamespace() throws Exception
    {
        TenantUtil.runAsUserTenant(new TenantRunAsWork<Void>()
        {
			@Override
			public Void doWork() throws Exception
			{
				M2Model customModel = getModel("dictionary/dictionarytest_model1.xml", null);
				
				try
				{
					addCustomModelToRepository(customModel);
					fail();
				}
				catch(DictionaryException e)
				{
					assertTrue(e.getMessage().indexOf("does not contain the tenant") != -1);
					// ok
				}
		
				return null;
			}
		}, tenant1Username1, tenant1);
    }

    @Test
    public void testAddCustomModel() throws Exception
    {
        TenantUtil.runAsUserTenant(new TenantRunAsWork<Void>()
        {
			@Override
			public Void doWork() throws Exception
			{
				Pair<String, String> namespaceInfo = new Pair<String, String>("http://www." + tenant1 + ".org/test/dictionarytest1/1.0",
						"dictionarytest1");
				M2Model customModel = getModel("dictionary/dictionarytest_model1.xml",
						Collections.singletonMap("http://www.alfresco.org/test/dictionarytest1/1.0", namespaceInfo));
				addCustomModelToRepository(customModel);

				CallContext context = new SimpleCallContext(tenant1Username1, "password", CmisVersion.CMIS_1_1);

				CmisService cmisService = factory.getService(context);
				try
				{
					assertNotNull(cmisService.getTypeDefinition(tenant1, "D:dictionarytest1:type1", null));
				}
				finally
				{
					cmisService.close();
				}

				return null;
			}
		}, tenant1Username1, tenant1);

        // it should be visible to a user in tenant1
        TenantUtil.runAsUserTenant(new TenantRunAsWork<Void>()
        {
			@Override
			public Void doWork() throws Exception
			{
				CallContext context = new SimpleCallContext(tenant1Username2, "password", CmisVersion.CMIS_1_1);

				CmisService cmisService = factory.getService(context);
				try
				{
					assertNotNull(cmisService.getTypeDefinition(tenant1, "D:dictionarytest1:type1", null));
				}
				finally
				{
					cmisService.close();
				}

				return null;
			}
		}, tenant1Username2, tenant1);

        // it should not be visible to a user in tenant2
        TenantUtil.runAsUserTenant(new TenantRunAsWork<Void>()
        {
			@Override
			public Void doWork() throws Exception
			{
				CallContext context = new SimpleCallContext(tenant2Username1, "password", CmisVersion.CMIS_1_1);

				CmisService cmisService = factory.getService(context);
				try
				{
					try
					{
						cmisService.getTypeDefinition(tenant2, "D:dictionarytest1:type1", null);
						fail();
					}
					catch(CmisObjectNotFoundException e)
					{
						// ok
					}
				}
				finally
				{
					cmisService.close();
				}

				return null;
			}
		}, tenant2Username1, tenant2);
    }

    /**
     * Test that adding a core model using DictionaryBootstrap after the dictionary has been initialised
     * (e.g. from a subsystem) triggers dictionary init "events" (and that the model is added to the
     * CMIS dictionary as a result).
     * 
     * Disabled for now until DictionaryBootstrap is enhanced to allow dictionary dependents to refresh
     * after new models are added to the dictionary e.g. call afterDictionaryInit
     *  
     * @throws Exception
     */
    @Ignore
    @Test
    public void testAddCoreModelAfterDictionaryInit() throws Exception
    {
        TenantUtil.runAsUserTenant(new TenantRunAsWork<Void>()
        {
			@Override
			public Void doWork() throws Exception
			{
				M2Model model = getModel("dictionary/dictionarytest_model2.xml", null);
				dictionaryDAO.putModel(model);

				CallContext context = new SimpleCallContext(tenant1Username1, "password", CmisVersion.CMIS_1_1);

				CmisService cmisService = factory.getService(context);
				try
				{
					assertNotNull(cmisService.getTypeDefinition(tenant1, "D:dictionarytest2:type1", null));
				}
				finally
				{
					cmisService.close();
				}

				return null;
			}
		}, tenant1Username1, tenant1);
    }
}
