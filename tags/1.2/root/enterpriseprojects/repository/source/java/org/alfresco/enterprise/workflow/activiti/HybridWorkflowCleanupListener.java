/*
 * Copyright 2005-2011 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.enterprise.workflow.activiti;

import java.util.List;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;
import org.activiti.engine.impl.pvm.delegate.ExecutionListenerExecution;
import org.alfresco.enterprise.repo.sync.NoSuchSyncSetDefinitionException;
import org.alfresco.enterprise.repo.sync.SyncAdminService;
import org.alfresco.enterprise.workflow.activiti.HybridWorkflowModel.HybridWorkflowStatus;
import org.alfresco.enterprise.workflow.activiti.HybridWorkflowModel.HybridWorklfowContentRetainStrategy;
import org.alfresco.repo.workflow.WorkflowModel;
import org.alfresco.repo.workflow.WorkflowQNameConverter;
import org.alfresco.repo.workflow.activiti.ActivitiScriptNode;
import org.alfresco.repo.workflow.activiti.BaseExecutionListener;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.lock.LockStatus;
import org.alfresco.service.cmr.repository.NodeRef;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * An {@link ExecutionListener} that is called when the hybrid worklfow is ended
 * or cancelled. Cleans up all aspects and 
 *
 * @author Frederik Heremans
 */
public class HybridWorkflowCleanupListener extends BaseExecutionListener
{
	private static final long serialVersionUID = 1L;

	private static final Log logger = LogFactory.getLog(HybridWorkflowCleanupListener.class);
    
    private transient WorkflowQNameConverter workflowQNameConverter;
    
    private transient SyncAdminService syncAdminService;
    
    @Override
    public void notify(DelegateExecution execution) throws Exception
    {
        setFinalHybridWorkflowStatus(execution);

        // Fetch package node
        ActivitiScriptNode node = (ActivitiScriptNode) execution.getVariable(workflowQNameConverter.mapQNameToName(WorkflowModel.ASSOC_PACKAGE));
        NodeRef packageRef = node.getNodeRef();
        
        // Extract content from package and iterate the content
        List<NodeRef> contentRefs = getServiceRegistry().getWorkflowService().getPackageContents(packageRef);
            
        unlockContent(contentRefs);
        // Check what cleanup is required after process finished
        HybridWorklfowContentRetainStrategy retainStrategy = getContentRetainStrategy(execution);
        if(retainStrategy == HybridWorklfowContentRetainStrategy.DOCUMENTS_SYNCED)
        {
            // Remove hybrid-worklfow aspects from content and leave SSD unthouched. This will cause
            // the cloud-workflow (if still active) to be cancelled
            removeAspectsFromContent(contentRefs);
        }
        else if(retainStrategy == HybridWorklfowContentRetainStrategy.DOCUMENTS_UNSYNCED || 
        		retainStrategy == HybridWorklfowContentRetainStrategy.DOCUMENTS_DELETE)
        {
            // Remove aspect locally BEFORE deleting SSD
            removeAspectsFromContent(contentRefs);
            
            // Delete SSD
            String syncSetDefintionId = (String) execution.getVariable(workflowQNameConverter.mapQNameToName(HybridWorkflowModel.PROP_SSD_ID));
            try
            {
                syncAdminService.deleteSourceSyncSet(syncSetDefintionId);
            }
            catch(NoSuchSyncSetDefinitionException nsssde)
            {
                // Sync-set has already been deleted before, this can be ignored because the
                // cloud-workflow will be cancelled when SSD was deleted. Only local cleanup remaining, which was done before this call.
                logger.warn("Syncset definition no longer exists (" + syncSetDefintionId + "), ignored when cancelling cloud workflow " + execution.getProcessInstanceId());
            }
        }
    }
    
    /**
     * Unlocks the content, if locked and current user is the lock owner.
     * @param contentRefs 
     */
    protected void unlockContent(List<NodeRef> contentRefs)
    {
        for(NodeRef content : contentRefs)
        {
            if(getServiceRegistry().getLockService().getLockStatus(content) == LockStatus.LOCK_OWNER) 
            {
                getServiceRegistry().getLockService().unlock(content);
                getServiceRegistry().getNodeService().removeAspect(content, HybridWorkflowModel.ASPECT_HYBRID_WORKFLOW);
            }
        }
    }

    /**
     * Set final workflow-status on the given execution.
     * @param execution
     */
    protected void setFinalHybridWorkflowStatus(DelegateExecution execution)
    {
        HybridWorkflowStatus finalStatus = null;
        ExecutionListenerExecution listenerExecution = (ExecutionListenerExecution) execution;
        if(listenerExecution.getDeleteReason() != null)
        {
            finalStatus = HybridWorkflowStatus.CANCELLED_ON_PREMISE;
        }
        else
        {
            finalStatus = HybridWorkflowStatus.FINISHED_ON_PREMISE;
        }
        
        // Set status variable before process ends
        execution.setVariable(workflowQNameConverter.mapQNameToName(HybridWorkflowModel.PROP_WORKFLOW_STATUS),
            finalStatus.getPropertyValue());
    }
    
    /**
     * @param execution
     * @return strategy for cloud-content retaining.
     */
    protected HybridWorklfowContentRetainStrategy getContentRetainStrategy(DelegateExecution execution)
    {
        String retainStrategyProp = (String) execution.getVariable(workflowQNameConverter.mapQNameToName(HybridWorkflowModel.PROP_RETAIN_STRATEGY));
        if(retainStrategyProp != null)
        {
            try 
            {
                return HybridWorklfowContentRetainStrategy.getStrategyFromPropertyValue(retainStrategyProp);
            }
            catch(IllegalArgumentException iae)
            {
                logger.error("Illegal value discovered for content retain strategy, using default", iae);
            }
        }
        
        // Revert to default when no value is set or illegal value is present
        return HybridWorklfowContentRetainStrategy.getDefault();
    }
    
    /**
     * Remove the hybrid-worklfow aspects from all contents in the worklfow-package.
     * @param execution
     */
    protected void removeAspectsFromContent(List<NodeRef> contentRefs)
    {
        for(NodeRef content : contentRefs)
        {
           getServiceRegistry().getNodeService().removeAspect(content, HybridWorkflowModel.ASPECT_HYBRID_WORKFLOW);
        }
    }
    
    @Override
    public void setServiceRegistry(ServiceRegistry serviceRegistry)
    {
        super.setServiceRegistry(serviceRegistry);
        
        // Initialize QName-converter
        this.workflowQNameConverter = new WorkflowQNameConverter(serviceRegistry.getNamespaceService());
    }
    
    public void setSyncAdminService(SyncAdminService syncAdminService)
    {
        this.syncAdminService = syncAdminService;
    }
}
