/*
 * Copyright 2005-2012 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.enterprise.repo.web.scripts.sync;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.alfresco.enterprise.repo.sync.BaseSyncServiceImplTest.DummyRemoteConnectorResponse;
import org.alfresco.enterprise.repo.sync.SyncAdminService;
import org.alfresco.enterprise.repo.sync.SyncAdminServiceImpl;
import org.alfresco.enterprise.repo.sync.SyncAdminServiceImplTest.DevelopmentTestSsdIdMapping;
import org.alfresco.enterprise.repo.sync.audit.SyncAuditService;
import org.alfresco.enterprise.repo.sync.connector.CloudConnectorService;
import org.alfresco.enterprise.repo.web.scripts.BaseEnterpriseWebScriptTest;
import org.alfresco.model.ContentModel;
import org.alfresco.repo.mode.ServerMode;
import org.alfresco.repo.mode.ServerModeProvider;
import org.alfresco.repo.model.Repository;
import org.alfresco.repo.remoteconnector.RemoteConnectorRequestImpl;
import org.alfresco.repo.remotecredentials.PasswordCredentialsInfoImpl;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.security.authentication.AuthenticationUtil.RunAsWork;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.repo.transaction.RetryingTransactionHelper.RetryingTransactionCallback;
import org.alfresco.service.cmr.remoteconnector.RemoteConnectorRequest;
import org.alfresco.service.cmr.remotecredentials.PasswordCredentialsInfo;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.springframework.context.ApplicationContext;

/**
 * This class provides common test setUp for Sync REST API tests.
 * 
 * @author Neil Mc Erlean
 * @since 4.1
 */
public abstract class BaseSyncServiceRestApiTest_ extends BaseEnterpriseWebScriptTest
{
    protected static CloudConnectorService MOCK_CLOUD_CONNECTOR_SERVICE;
    
    /** Dummy remote credentials object for this test only. */
    private static final PasswordCredentialsInfo REMOTE_CREDENTIALS = new PasswordCredentialsInfoImpl();
    
    static
    {
        ((PasswordCredentialsInfoImpl)REMOTE_CREDENTIALS).setRemoteUsername("remote.user");
        ((PasswordCredentialsInfoImpl)REMOTE_CREDENTIALS).setRemotePassword("remote.password");
    }
    
    protected NodeService nodeService;
    protected Repository repositoryHelper;
    protected RetryingTransactionHelper transactionHelper;
    protected SyncAdminService syncAdminService;
    
    protected String srcRepoId;
    
    protected List<NodeRef> nodesToDeleteAfterTest = new ArrayList<NodeRef>();
    
    @Override protected void setUp() throws Exception
    {
        MOCK_CLOUD_CONNECTOR_SERVICE = mock(CloudConnectorService.class);
        
        // ...that always uses dummy credentials
        when(MOCK_CLOUD_CONNECTOR_SERVICE.getCloudCredentials())
            .thenReturn(REMOTE_CREDENTIALS);
        when(MOCK_CLOUD_CONNECTOR_SERVICE.storeCloudCredentials(any(String.class), any(String.class)))
            .thenReturn(REMOTE_CREDENTIALS);
        // ...and always returns a simple RemoteConnectorRequest object
        when(MOCK_CLOUD_CONNECTOR_SERVICE.buildCloudRequest(any(String.class), any(String.class), any(String.class)))
            .thenReturn(new RemoteConnectorRequestImpl("srcRepoId", "POST"));
        // ... and always returns a dummy response object
        
        try
        {
            when(MOCK_CLOUD_CONNECTOR_SERVICE.executeCloudRequest(any(RemoteConnectorRequest.class)))
                .thenReturn(new DummyRemoteConnectorResponse());
        } catch (IOException ignored)
        {
            // Intentionally empty.
        }
        
        super.setUp();
        
        // Init sprint services.
        final ApplicationContext applicationContext = getServer().getApplicationContext();
        nodeService       = applicationContext.getBean("NodeService", NodeService.class);
        repositoryHelper  = applicationContext.getBean("repositoryHelper", Repository.class);
        syncAdminService  = applicationContext.getBean("syncAdminService", SyncAdminServiceImpl.class);
        transactionHelper = applicationContext.getBean("retryingTransactionHelper", RetryingTransactionHelper.class);
        
        SyncAuditService syncAuditService = applicationContext.getBean("SyncAuditService", SyncAuditService.class);
        srcRepoId = syncAuditService.getRepoId();
        
        ServerModeProvider fakeServerModeProvider = new ServerModeProvider()
        {

			@Override
			public ServerMode getServerMode() {
				return ServerMode.PRODUCTION;
			}
        	
        };
        
        if(syncAdminService instanceof SyncAdminServiceImpl)
        {
        	SyncAdminServiceImpl syncAdminServiceImpl = (SyncAdminServiceImpl)syncAdminService;
        	syncAdminServiceImpl.setServerModeProvider(fakeServerModeProvider);
        }
        
        
        // Change the default strategy for On Premise - Cloud SSD ID mapping in order to avoid SSD IDs clashing - due to our using a single repo as source & target.
        SyncAdminServiceImpl syncAdminServiceImpl = (SyncAdminServiceImpl)syncAdminService;
        syncAdminServiceImpl.setSsdIdMappingStrategy(new DevelopmentTestSsdIdMapping());
    }
    
    @Override protected void tearDown() throws Exception
    {
        AuthenticationUtil.runAsSystem(new RunAsWork<Void>()
        {
            @Override public Void doWork() throws Exception
            {
                transactionHelper.doInTransaction(new RetryingTransactionCallback<Void>()
                {
                    public Void execute() throws Throwable
                    {
                        for (NodeRef nodeRef : nodesToDeleteAfterTest)
                        {
                            if (nodeService.exists(nodeRef))
                            {
                                nodeService.deleteNode(nodeRef);
                            }
                        }
                        return null;
                    }
                });
                return null;
            }
        });
        
        super.tearDown();
    }
    
    protected List<NodeRef> createNodes()
    {
        return transactionHelper.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<List<NodeRef>>()
        {
            public List<NodeRef> execute() throws Throwable
            {
                List<NodeRef> results = new ArrayList<NodeRef>();
                final NodeRef nodeRef1 = nodeService.createNode(repositoryHelper.getCompanyHome(),
                                                                ContentModel.ASSOC_CONTAINS, ContentModel.ASSOC_CONTAINS,
                                                                ContentModel.TYPE_CONTENT, null).getChildRef();
                final NodeRef nodeRef2 = nodeService.createNode(repositoryHelper.getCompanyHome(),
                                                                ContentModel.ASSOC_CONTAINS, ContentModel.ASSOC_CONTAINS,
                                                                ContentModel.TYPE_CONTENT, null).getChildRef();
                
                results.add(nodeRef1);
                results.add(nodeRef2);
                
                nodesToDeleteAfterTest.add(nodeRef1);
                nodesToDeleteAfterTest.add(nodeRef2);
                
                return results;
            }
        });
    }
}
