/*
 * Copyright (C) 2005-2011 Alfresco Software Limited.
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
package org.alfresco.module.org_alfresco_module_cloud.webscripts;

import java.util.HashMap;
import java.util.Map;

import org.alfresco.enterprise.repo.sync.SyncNodeException;
import org.alfresco.enterprise.repo.sync.SyncNodeException.SyncNodeExceptionType;
import org.alfresco.module.org_alfresco_module_cloud.usage.QuotaUsage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptRequest;

/**
 * Cloud Specific override of the Remote Sync Set Post (Create) WebScript,
 *  which checks for the quota before creating 
 *  
 * @author Nick Burch
 * @since Thor.
 */
public class RemoteSyncSetDefinitionPost extends org.alfresco.enterprise.repo.web.scripts.sync.cloudonly.RemoteSyncSetDefinitionPost
{
    private static final Log logger = LogFactory.getLog(RemoteSyncSetDefinitionPost.class);
    
    private QuotaUsage quotaUsage;
    public void setQuotaUsage(QuotaUsage quotaUsage)
    {
        this.quotaUsage = quotaUsage;
    }

    /**
     * Checks the quota, the has the sync set added if ok
     */
    @Override
    protected Map<String, Object> executeSyncImpl(WebScriptRequest req, Status status, Cache cache)
    {
        if (quotaUsage.isOverQuota())
        {
            SyncNodeException sne = new SyncNodeException(SyncNodeExceptionType.QUOTA_LIMIT_VIOLATION);
            logger.info("Unable to create SSD, Tenant Domain is over Quota");
            
            Map<String,Object> model = new HashMap<String, Object>();
            model.put("exception", sne);
            model.put("message", sne.getMessage());
            model.put("messageId", sne.getMsgId());
            recordStatus(Status.STATUS_PRECONDITION_FAILED, sne.getExceptionType().getMessageId(), model, status);
            return model;
        }
        
        // Add as usual
        return super.executeSyncImpl(req, status, cache);
    }
}