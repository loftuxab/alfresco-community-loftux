/*
 * Copyright 2005-2011 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.enterprise.workflow.activiti;

import org.alfresco.enterprise.repo.sync.SyncAdminService;
import org.alfresco.repo.workflow.WorkflowDeployer;

/**
 * A {@link WorkflowDeployer} which only deploys the workflows if sync-service is enabled
 * and the repo is an on-premise version. Can be disabled, regardless of the conditions above
 * by setting the property 'enableHybridWorkflow' to false.  
 *
 * @author Frederik Heremans
 */
public class HybridWorkflowDeployer extends WorkflowDeployer
{
    private boolean enableHybridWorkflow = false;
    private SyncAdminService syncAdminService;

    @Override
    public void init()
    {
        if(enableHybridWorkflow && syncAdminService.isEnabled() && syncAdminService.isOnPremise())
        {
            super.init();
        }
    }
    public void setSyncAdminService(SyncAdminService syncAdminService)
    {
        this.syncAdminService = syncAdminService;
    }

    public void setEnableHybridWorkflow(boolean enableHybridWorkflow)
    {
        this.enableHybridWorkflow = enableHybridWorkflow;
    }
}
