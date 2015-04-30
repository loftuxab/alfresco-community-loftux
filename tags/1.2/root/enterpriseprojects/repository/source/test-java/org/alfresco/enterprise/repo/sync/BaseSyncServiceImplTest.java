/*
 * Copyright 2005-2012 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.enterprise.repo.sync;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.InputStream;

import org.alfresco.enterprise.repo.sync.connector.CloudConnectorService;
import org.alfresco.repo.remoteconnector.RemoteConnectorRequestImpl;
import org.alfresco.repo.remotecredentials.PasswordCredentialsInfoImpl;
import org.alfresco.service.cmr.remoteconnector.RemoteConnectorRequest;
import org.alfresco.service.cmr.remoteconnector.RemoteConnectorResponse;
import org.alfresco.service.cmr.remotecredentials.PasswordCredentialsInfo;
import org.alfresco.util.test.junitrules.ApplicationContextInit;
import org.apache.commons.httpclient.Header;

/**
 * @author janv
 * @since CloudSync
 */
public abstract class BaseSyncServiceImplTest
{
    // Rule to initialise the default Alfresco spring configuration
    public static ApplicationContextInit APP_CONTEXT_INIT = new ApplicationContextInit();
    
    /** Dummy remote credentials objects for this test only. */
    protected static final PasswordCredentialsInfo REMOTE_CREDENTIALS = new PasswordCredentialsInfoImpl();
    
    protected static CloudConnectorService MOCK_CLOUD_CONNECTOR_SERVICE;
    
    public static class DummyRemoteConnectorResponse implements RemoteConnectorResponse
    {
        @Override public int                    getStatus()                                  { return 200; }
        @Override public Header[]               getResponseHeaders()                         { return null; }
        @Override public String                 getResponseBodyAsString() throws IOException { return null; }
        @Override public InputStream            getResponseBodyAsStream() throws IOException { return null; }
        @Override public byte[]                 getResponseBodyAsBytes() throws IOException  { return null; }
        @Override public RemoteConnectorRequest getRequest()                                 { return null; }
        @Override public String                 getRawContentType()                          { return null; }
        @Override public String                 getContentType()                             { return null; }
        @Override public String                 getCharset()                                 { return null; }
    };
    
    static {
        ((PasswordCredentialsInfoImpl)REMOTE_CREDENTIALS).setRemoteUsername("remote.user");
        ((PasswordCredentialsInfoImpl)REMOTE_CREDENTIALS).setRemotePassword("remote.password");
        
        // and a mocked CloudConnectorService...
        MOCK_CLOUD_CONNECTOR_SERVICE = mock(CloudConnectorService.class);
        // ...that always uses dummy credentials
        when(MOCK_CLOUD_CONNECTOR_SERVICE.getCloudCredentials())
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
    }
}
