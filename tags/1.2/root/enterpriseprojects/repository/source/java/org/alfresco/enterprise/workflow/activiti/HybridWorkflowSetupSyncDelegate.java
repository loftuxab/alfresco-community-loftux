/*
 * Copyright 2005-2011 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.enterprise.workflow.activiti;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.activiti.engine.delegate.DelegateExecution;
import org.alfresco.enterprise.repo.sync.SyncAdminService;
import org.alfresco.enterprise.repo.sync.SyncSetCreationConflictException;
import org.alfresco.enterprise.repo.sync.SyncSetDefinition;
import org.alfresco.enterprise.workflow.activiti.HybridWorkflowModel.HybridWorkflowStatus;
import org.alfresco.model.ContentModel;
import org.alfresco.repo.i18n.MessageService;
import org.alfresco.repo.security.permissions.AccessDeniedException;
import org.alfresco.repo.workflow.BPMEngineRegistry;
import org.alfresco.repo.workflow.WorkflowModel;
import org.alfresco.repo.workflow.WorkflowQNameConverter;
import org.alfresco.repo.workflow.activiti.ActivitiConstants;
import org.alfresco.repo.workflow.activiti.ActivitiScriptNode;
import org.alfresco.repo.workflow.activiti.BaseJavaDelegate;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.workflow.WorkflowException;
import org.alfresco.service.namespace.QName;
import org.apache.commons.lang.StringUtils;


/**
 * Class that will setup cloud-sync for the selected files in the workflow to the selected
 * remote tenant folder/site.
 * 
 * @author Frederik Heremans
 *
 */
public class HybridWorkflowSetupSyncDelegate extends BaseJavaDelegate {

    private static final String DESTINATION_SPLIT_CHAR = "|";
    private static final String DESTINATION_SPLIT_REGEX = "\\|";
    private static final String NODEREF_SPLIT_CHAR = ",";
    
    // Error message keys
    private static final String ERROR_INVALID_OR_MISSING_DESTINATION = "hybrid.workflow.error.invalid.destination";
    private static final String ERROR_CONTENT_ALREADY_IN_HYBRID_WORKFLOW = "hybrid.workflow.error.content.already.in.hybrid.workflow";
    private static final String ERROR_MISSING_PACKAGE = "hybrid.workflow.error.missing.package";
    private static final String ERROR_MISSING_CONTENT = "hybrid.workflow.error.missing.content";
    private static final String ERROR_HYBRID_WORKFLOW_DISABLED = "hybrid.workflow.disabled";
    private static final String ERROR_HYBRID_WORKFLOW_NO_PERMISSIONS_ON_CONTENT = "hybrid.workflow.error.content.permissions";
    private static final String ERROR_ASSIGNEES_NOT_PART_OF_SITE = "hybrid.workflow.error.assignees.permissions";
    
    /**
     * Used to convert QName to actual variable-names in Activiti
     */
    private WorkflowQNameConverter workflowQNameConverter;
    
    private SyncAdminService syncAdminService;
    private HybridWorkflowHelper hybridWorkflowHelper;
    private MessageService messageService;
    private boolean enableHybridWorkflow;
    
    public void execute(DelegateExecution execution) throws Exception {
        
        if(!enableHybridWorkflow || !syncAdminService.isOnPremise()) {
            throw new WorkflowException(ERROR_HYBRID_WORKFLOW_DISABLED);
        }
        
        String destination = (String) execution.getVariable(workflowQNameConverter.mapQNameToName(HybridWorkflowModel.PROP_DESTINATION));
        
        if(destination == null || !destination.contains(DESTINATION_SPLIT_CHAR))
        {
            throw new WorkflowException(messageService.getMessage(ERROR_INVALID_OR_MISSING_DESTINATION, destination));
        }
        
        // Extract tenant and node-ref
        String[] parts = destination.split(DESTINATION_SPLIT_REGEX);
        if(parts.length != 3)
        {
            throw new WorkflowException(messageService.getMessage(ERROR_INVALID_OR_MISSING_DESTINATION, destination));
        }
        
        String tenant = parts[0];
        String folderNodeRef = parts[1];
        String siteId = parts[2];
        
        // Check if selected assignees are part of the site where the content will be synced
        checkAssigneePermissions(tenant, siteId, (String) getWorkflowProperty(HybridWorkflowModel.PROP_ASSIGNMENT, execution));
        
        List<NodeRef> contentToSync = extractContentToSync(execution);
        
        // Add aspect to the content, containing workflow-information
        addAspectToContent(contentToSync, execution);
        
        Boolean lockOnPremiseCopy = (Boolean) getWorkflowProperty(HybridWorkflowModel.PROP_LOCK_ON_PREMISE_COPY, execution);
        if(lockOnPremiseCopy == null)
        {
            lockOnPremiseCopy = false;
        }
        
        try
        {
            // Finally, create the master sync-set
            SyncSetDefinition ssd = syncAdminService.createSourceSyncSet(contentToSync, tenant, folderNodeRef, lockOnPremiseCopy.booleanValue(), true, false);
            
            // Store reference to the SSD in the process, for potential later use (eg. canceling workflow)
            execution.setVariable(workflowQNameConverter.mapQNameToName(HybridWorkflowModel.PROP_SSD_ID), ssd.getId());
        }
        catch(SyncSetCreationConflictException sscce)
        {
            throw new WorkflowException(messageService.getMessage(ERROR_CONTENT_ALREADY_IN_HYBRID_WORKFLOW));
        }
        
    }
    
    protected void checkAssigneePermissions(String network, String siteId, String assigneeString)
    {
        List<String> assigneeNodeRefs = new ArrayList<String>();
        if(assigneeString != null && !assigneeString.isEmpty())
        {
            if(assigneeString.contains(NODEREF_SPLIT_CHAR))
            {
               StringTokenizer tokeniser = new StringTokenizer(assigneeString, NODEREF_SPLIT_CHAR);
               while(tokeniser.hasMoreTokens())
               {
                   assigneeNodeRefs.add(tokeniser.nextToken());
               }
            }
            else
            {
                assigneeNodeRefs.add(assigneeString);
            }
        }
        
        List<String> invalidCloudAssignees = hybridWorkflowHelper.getInvalidCloudAssignees(assigneeNodeRefs, network, siteId);
        if(invalidCloudAssignees.size() > 0)
        {
            throw new WorkflowException(ERROR_ASSIGNEES_NOT_PART_OF_SITE, StringUtils.join(invalidCloudAssignees, ", "));
        }
    }

    @Override
    public void setServiceRegistry(ServiceRegistry serviceRegistry)
    {
        super.setServiceRegistry(serviceRegistry);
        
        // Extract namespace-service to use for converting QNames to variable-names
        workflowQNameConverter = new WorkflowQNameConverter(serviceRegistry.getNamespaceService());
    }
    
    public void setSyncAdminService(SyncAdminService syncAdminService)
    {
        this.syncAdminService = syncAdminService;
    }
    
    public void setEnableHybridWorkflow(boolean enableHybridWorkflow)
    {
        this.enableHybridWorkflow = enableHybridWorkflow;
    }
    
    public void setMessageService(MessageService messageService)
    {
        this.messageService = messageService;
    }
    
    public void setHybridWorkflowHelper(HybridWorkflowHelper hybridWorkflowHelper)
    {
        this.hybridWorkflowHelper = hybridWorkflowHelper;
    }
    
    /**
     * Extract all content in the package associated with the given execution.
     * 
     * @param execution
     * @return list of node-refs to selected content
     */
    protected List<NodeRef> extractContentToSync(DelegateExecution execution)
    {
        ActivitiScriptNode scriptNode = (ActivitiScriptNode) execution.getVariable(workflowQNameConverter.mapQNameToName(WorkflowModel.ASSOC_PACKAGE));
        if(scriptNode == null)
        {
            throw new WorkflowException(messageService.getMessage(ERROR_MISSING_PACKAGE));
        }
        
        List<NodeRef> contentToSync = getServiceRegistry().getWorkflowService().getPackageContents(scriptNode.getNodeRef());
        if(contentToSync == null || contentToSync.size() == 0) 
        {
            throw new WorkflowException(messageService.getMessage(ERROR_MISSING_CONTENT));
        }
        return contentToSync;
    }
    
    /**
     * Add hybridWorkflow aspect to all content that will be synced.
     * 
     * @param contentToSync all content to sync
     * @param execution the associated execution
     */
    protected void addAspectToContent(List<NodeRef> contentToSync, DelegateExecution execution)
    {
        // Build aspect properties
        Map<QName, Serializable> aspectProps = new HashMap<QName, Serializable>();
        aspectProps.put(HybridWorkflowModel.PROP_ON_PREMISE_WORKFLOW_ID, 
            BPMEngineRegistry.createGlobalId(ActivitiConstants.ENGINE_ID, execution.getProcessInstanceId()));
        aspectProps.put(HybridWorkflowModel.PROP_WORKFLOW_STATUS, 
            HybridWorkflowStatus.STARTED_ON_PREMISE.getPropertyValue());
        
        // Common workflow properties
        aspectProps.put(HybridWorkflowModel.PROP_WORKFLOW_DESCRIPTION, 
                    getWorkflowProperty(WorkflowModel.PROP_WORKFLOW_DESCRIPTION, execution));
        
        aspectProps.put(HybridWorkflowModel.PROP_WORKFLOW_DUE_DATE, 
                    getWorkflowProperty(WorkflowModel.PROP_WORKFLOW_DUE_DATE, execution));
        
        aspectProps.put(HybridWorkflowModel.PROP_WORKFLOW_PRIORITY, 
                    getWorkflowProperty(WorkflowModel.PROP_WORKFLOW_PRIORITY, execution));
        
        aspectProps.put(HybridWorkflowModel.PROP_ASSIGNMENT, 
                    getWorkflowProperty(HybridWorkflowModel.PROP_ASSIGNMENT, execution));
        
        aspectProps.put(HybridWorkflowModel.PROP_CLOUD_WORKFLOW_TYPE, 
                    getWorkflowProperty(HybridWorkflowModel.PROP_CLOUD_WORKFLOW_TYPE, execution));
        
        aspectProps.put(HybridWorkflowModel.PROP_RETAIN_STRATEGY, 
        		getWorkflowProperty(HybridWorkflowModel.PROP_RETAIN_STRATEGY, execution));
        
        Serializable approvalPercentage = getWorkflowProperty(HybridWorkflowModel.PROP_REQUIRED_APPROVAL_PERCENTAGE, execution);
        if(approvalPercentage != null) {
            aspectProps.put(HybridWorkflowModel.PROP_REQUIRED_APPROVAL_PERCENTAGE, approvalPercentage); 
        }
        
        // Add aspect to ALL files involved to ensure the worklfow-package 
        // can be reconstructed on the cloud
        final NodeService nodeService = getServiceRegistry().getNodeService();
        for(NodeRef nodeRef : contentToSync)
        {
            try 
            {
                nodeService.addAspect(nodeRef, HybridWorkflowModel.ASPECT_HYBRID_WORKFLOW, aspectProps);
            }
            catch(AccessDeniedException ade)
            {
                // Try extracting the name of the content to display in the exception message
                String contentName = nodeRef.toString();
                try 
                {
                    contentName = (String) nodeService.getProperty(nodeRef, ContentModel.PROP_NAME);
                }
                catch(Throwable ignore) {
                    // Possible that an exception is thrown when accessing the properties of the node, ignore to
                    // prevent swallowing original exception
                }
                throw new WorkflowException(ERROR_HYBRID_WORKFLOW_NO_PERMISSIONS_ON_CONTENT, contentName);
            }
        }
    }
    
    /**
     * Extract property value from execution.
     * 
     * @param propertyName
     * @param execution
     * @return property value
     */
    protected Serializable getWorkflowProperty(QName propertyName, DelegateExecution execution)
    {
        return (Serializable)execution.getVariable(workflowQNameConverter.mapQNameToName(propertyName));
    }
}