/*
 * Copyright 2005-2014 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.module.org_alfresco_module_cloud.sync;

import org.alfresco.enterprise.repo.sync.SyncAdminServiceException;
import org.alfresco.enterprise.repo.sync.SyncSetDefinition;
import org.alfresco.module.org_alfresco_module_cloud.CloudModel;
import org.alfresco.module.org_alfresco_module_cloud.users.CloudPersonService;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.security.AuthorityService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class SyncPermissionsChecker
{
    private static final Log logger = LogFactory.getLog(SyncPermissionsChecker.class);
    
    private CloudPersonService cloudPersonService;
    private AuthorityService  authorityService;
    private NodeService nodeService;
    
    public void setNodeService(NodeService nodeService)
    {
        this.nodeService = nodeService;
    }
    
    public void setAuthorityService(AuthorityService authorityService)
    {
        this.authorityService = authorityService;
    }
    
    public void setCloudPersonService(CloudPersonService cloudPersonService)
    {
        this.cloudPersonService = cloudPersonService;
    }
    
    public void checkSsdPermissions(SyncSetDefinition ssd)
    {
        if (ssd == null)
        {
            logger.warn("SyncSetDefinition is null");
            return;
        }
        String syncCreator = ssd.getSyncCreator();
        if (syncCreator == null && logger.isWarnEnabled())
        {
            logger.warn("No SyncCreator was set for SSD " + ssd.getId());
            return;
        }
        String userName = AuthenticationUtil.getFullyAuthenticatedUser();
        if (isNetworkAdmin(userName) || authorityService.isAdminAuthority(userName) || AuthenticationUtil.getSystemUserName().equals(userName))
        {
            return;
        }
        if (!syncCreator.equals(userName))
        {
            throw new SyncAdminServiceException("Not enough permissions to process SSD: " + ssd.getId());
        }
    }
    
    private boolean isNetworkAdmin(String userID)
    {
        NodeRef person = cloudPersonService.getPerson(userID, false);
        return (person == null) ? false : nodeService.hasAspect(person, CloudModel.ASPECT_NETWORK_ADMIN);
    }

}
