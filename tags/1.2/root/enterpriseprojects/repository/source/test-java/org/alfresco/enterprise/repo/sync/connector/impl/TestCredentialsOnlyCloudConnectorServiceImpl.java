/*
 * Copyright 2005-2012 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.enterprise.repo.sync.connector.impl;

import org.alfresco.enterprise.repo.sync.connector.CloudConnectorService;
import org.alfresco.repo.remotecredentials.PasswordCredentialsInfoImpl;
import org.alfresco.service.cmr.remoteconnector.RemoteConnectorRequest;
import org.alfresco.service.cmr.remoteconnector.RemoteConnectorResponse;
import org.alfresco.service.cmr.remotecredentials.BaseCredentialsInfo;
import org.apache.commons.lang.NotImplementedException;

/**
 * A test implementation of {@link CloudConnectorService} which handles a single
 *  set of credentials, but no remote calls.
 *  
 * @author Nick Burch
 * @since TODO
 */
public class TestCredentialsOnlyCloudConnectorServiceImpl implements CloudConnectorService
{
    private BaseCredentialsInfo credentials = null;
    
    public RemoteConnectorRequest buildCloudRequest(String relativeUrl, String network, String method)
    {
        throw new NotImplementedException("This test service provides credential management only");
    }
    public RemoteConnectorResponse executeCloudRequest(RemoteConnectorRequest request)
    {
        throw new NotImplementedException("This test service provides credential management only");
    }
    
    
    /**
     * Caches the credentials for later use. Note - only a single credentials set is supported
     */
    public BaseCredentialsInfo storeCloudCredentials(String username, String password)
    {
        PasswordCredentialsInfoImpl pwd = new PasswordCredentialsInfoImpl();
        pwd.setRemoteUsername(username);
        pwd.setRemotePassword(password);
        
        this.credentials = pwd;
        return pwd;
    }
    
    /**
     * Un-caches the cloud credentials, if they exist
     */
    public boolean deleteCloudCredentials()
    {
        if (credentials == null) return false;
        
        credentials = null;
        return true;
    }

    /**
     * Returns the cached credentials, if available
     */
    public BaseCredentialsInfo getCloudCredentials()
    {
        return credentials;
    }
}
