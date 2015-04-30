/*
 * Copyright 2005-2012 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.enterprise.repo.web.scripts.sync.connector;

import java.util.HashMap;
import java.util.Map;

import org.alfresco.enterprise.repo.web.scripts.sync.AbstractCloudSyncDeclarativeWebScript;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.service.cmr.remotecredentials.BaseCredentialsInfo;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptException;
import org.springframework.extensions.webscripts.WebScriptRequest;

/**
 * This class is the SOURCE controller for the Cloud Credentials credentials.delete web script.
 * 
 * @author Nick Burch
 * @since 4.1
 */
public class CloudCredentialsDelete extends AbstractCloudSyncDeclarativeWebScript
{
    private static Log logger = LogFactory.getLog(CloudCredentialsDelete.class);
            
    @Override protected Map<String, Object> executeSyncImpl(WebScriptRequest req, Status status, Cache cache)
    {
        // Fetch their credentials, if known
        BaseCredentialsInfo credentials = cloudConnectorService.getCloudCredentials();
        
        // Try to remove them
        boolean deleted = cloudConnectorService.deleteCloudCredentials();
        
        // If there are none, report
        if (! deleted)
        {
            if (logger.isDebugEnabled())
                logger.debug("No cloud details found for " + AuthenticationUtil.getFullyAuthenticatedUser());
            
            throw new WebScriptException(Status.STATUS_BAD_REQUEST, "No Credentials found to delete");
        }

        // Report the credentials as having now gone
        Map<String, Object> model = new HashMap<String, Object>();
        model.put("username", credentials.getRemoteUsername());
        model.put("message", "Credentials Deleted");

        return model;
    }
}