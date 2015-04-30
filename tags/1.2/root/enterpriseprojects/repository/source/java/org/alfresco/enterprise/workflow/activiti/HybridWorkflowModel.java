/*
 * Copyright 2005-2011 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.enterprise.workflow.activiti;

import org.alfresco.service.namespace.QName;

/**
 * Hybrid workflow model constants
 * 
 * @author Frederik Heremans
 */
public interface HybridWorkflowModel {
    
    // Model URI and prefix
    
    /** Hybrid Workflow Model Prefix */
    String HYBRID_WORKFLOW_MODEL_PREFIX = "hwf";
    
    /** Hybrid Workflow Model URI */
    String HYBRID_WORKFLOW_MODEL_URI = "http://www.alfresco.org/model/hybridworkflow/1.0";
    

    // Types
    
    /**
     * Aspect applied to all documents that are part of a hybrid workflow.
     */
    QName ASPECT_HYBRID_WORKFLOW = QName.createQName(HYBRID_WORKFLOW_MODEL_URI, "hybridWorkflow");

    
    // Properties
    
    /**
     * Destination of the files in the cloud. Contains both tenant and folder-nodeRef
     */
    QName PROP_DESTINATION = QName.createQName(HYBRID_WORKFLOW_MODEL_URI, "cloudDestination");
    
    /**
     * ID of the SSD (SyncSetDefinition) associated with this process
     */
    QName PROP_SSD_ID = QName.createQName(HYBRID_WORKFLOW_MODEL_URI, "ssdId");
    
    /**
     * ID of workflow running on-premise.
     */
    QName PROP_ON_PREMISE_WORKFLOW_ID = QName.createQName(HYBRID_WORKFLOW_MODEL_URI, "onPremiseWorkflowId");
    
    /**
     * Current status of the hybrid-workflow (used on aspect)
     * @see HybridWorkflowStatus#getPropertyValue()
     */
    QName PROP_WORKFLOW_STATUS = QName.createQName(HYBRID_WORKFLOW_MODEL_URI, "workflowStatus");
    
    /**
     * Description of the hybrid-workflow
     */
    QName PROP_WORKFLOW_DESCRIPTION = QName.createQName(HYBRID_WORKFLOW_MODEL_URI, "workflowDescription");
    
    /**
     * Duedate of the hybrid workflow
     */
    QName PROP_WORKFLOW_DUE_DATE = QName.createQName(HYBRID_WORKFLOW_MODEL_URI, "workflowDueDate");
    
    /**
     * Priority of the hybrid workflow
     */
    QName PROP_WORKFLOW_PRIORITY = QName.createQName(HYBRID_WORKFLOW_MODEL_URI, "workflowPriority");
    
    /**
     * Assignee of the hybrid workflow
     */
    QName PROP_ASSIGNMENT = QName.createQName(HYBRID_WORKFLOW_MODEL_URI, "assignment");
    
    /**
     * Result of the cloud-workflow
     */
    QName PROP_RESULT = QName.createQName(HYBRID_WORKFLOW_MODEL_URI, "result");
    
    /**
     * Comments done by cloud-assignee(s) when completing tasks
     */
    QName PROP_COMMENTS = QName.createQName(HYBRID_WORKFLOW_MODEL_URI, "comments");

    /**
     * Strategy for retaining cloud-content and syncing. 
     * @see HybridWorklfowContentRetainStrategy#getPropertyValue()
     */
    QName PROP_RETAIN_STRATEGY = QName.createQName(HYBRID_WORKFLOW_MODEL_URI, "retainStrategy");
    
    /**
     * Whether or not to lock on-premise content when sync is created.
     */
    QName PROP_LOCK_ON_PREMISE_COPY = QName.createQName(HYBRID_WORKFLOW_MODEL_URI, "lockOnPremiseCopy");
    
    /**
     * Whether or not to lock on-premise content when sync is created.
     * @see HybridWorkflowCloudType#getPropertyValue()
     */
    QName PROP_CLOUD_WORKFLOW_TYPE = QName.createQName(HYBRID_WORKFLOW_MODEL_URI, "cloudWorkflowType");
    
    /**
     * Used for review-task only, required percentage of reviewers who need to approve.
     */
    QName PROP_REQUIRED_APPROVAL_PERCENTAGE = QName.createQName(HYBRID_WORKFLOW_MODEL_URI, "requiredApprovalPercentage");
    
    /**
     * Used for review-task only, actual percentage of reviewers who approved.
     */
    QName PROP_ACTUAL_APPROVAL_PERCENTAGE = QName.createQName(HYBRID_WORKFLOW_MODEL_URI, "actualApprovalPercentage");

    
    // Helper enums for contrained property values
    
    /**
     * Status enum for easy comparison.
     * 
     * @author Frederik Heremans
     */
    public enum HybridWorkflowStatus {
        STARTED_ON_PREMISE("startedOnPremise"),
        STARTED_ON_CLOUD("startedOnCloud"),
        FINISHED_ON_PREMISE("finishedOnPremise"),
        FINISHED_ON_CLOUD("finishedOnCloud"),
        CANCELLED_ON_PREMISE("cancelledOnPremise"),
        CANCELLED_ON_CLOUD("cancelledOnCloud");
        
        private String propertyValue;
        
        private HybridWorkflowStatus(String propertyValue)
        {
            this.propertyValue = propertyValue;
        }
        
        public static HybridWorkflowStatus getStatusFromPropertyValue(String propValue)
        {
            for(HybridWorkflowStatus status : values())
            {
                if(status.getPropertyValue().equals(propValue)) 
                {
                    return status;
                }
            }
            throw new IllegalArgumentException("Value is not a HybridWorkflowStatus: " + propValue);
        }
        
        public String getPropertyValue() 
        {
            return propertyValue;
        }
    }
    
    /**
     * Strategy for retaining cloud-content syncing (enum for easy comparison).
     * 
     * @author Frederik Heremans
     */
    public enum HybridWorklfowContentRetainStrategy {
        DOCUMENTS_SYNCED("documentsSynced"),
        DOCUMENTS_UNSYNCED("documentsUnSynced"),
        DOCUMENTS_DELETE("documentsDelete");
        
        private String propertyValue;
        
        private HybridWorklfowContentRetainStrategy(String propertyValue)
        {
            this.propertyValue = propertyValue;
        }
        
        public static HybridWorklfowContentRetainStrategy getStrategyFromPropertyValue(String propValue)
        {
            for(HybridWorklfowContentRetainStrategy status : values())
            {
                if(status.getPropertyValue().equals(propValue)) 
                {
                    return status;
                }
            }
            throw new IllegalArgumentException("Value is not a HybridWorklfowRetainContentStrategy: " + propValue);
        }
        
        public static HybridWorklfowContentRetainStrategy getDefault()
        {
            return DOCUMENTS_SYNCED;
        }
        
        public String getPropertyValue() 
        {
            return propertyValue;
        }
    }
    
    /**
     * Possible cloud-workflow types.
     * 
     * @author Frederik Heremans
     */
    public enum HybridWorkflowCloudType {
        SIMPLE_TASK("task"),
        REVIEW("review");
        
        private String propertyValue;
        
        private HybridWorkflowCloudType(String propertyValue)
        {
            this.propertyValue = propertyValue;
        }
        
        public static HybridWorkflowCloudType getTypeFromPropertyValue(String propValue)
        {
            for(HybridWorkflowCloudType status : values())
            {
                if(status.getPropertyValue().equals(propValue)) 
                {
                    return status;
                }
            }
            throw new IllegalArgumentException("Value is not a HybridWorkflowCloudType: " + propValue);
        }
        
        public String getPropertyValue() 
        {
            return propertyValue;
        }
    }
}