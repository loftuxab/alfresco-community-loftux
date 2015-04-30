/*
 * Copyright 2005-2011 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.module.org_alfresco_module_cloud.workflow;

import java.io.Serializable;
import java.util.Map;

import org.activiti.engine.delegate.DelegateExecution;
import org.alfresco.enterprise.workflow.activiti.HybridWorkflowModel.HybridWorkflowCloudType;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;

/**
 * A handler responsible for handling the cloud-part of a hybrid
 * workflow of a certain type.
 *
 * @author Frederik Heremans
 */
public interface HybridWorkflowTypeHandler
{
    /**
     * @return type of workflow that can be handled
     */
    HybridWorkflowCloudType getType();
    
    /**
     * @return name of workflow definition that should be started for this type of workflow
     */
    String getWorkflowDefinitionName();
    
    /**
     * @param content reference to the content that holds the hybrid aspect
     * @param properties properties of the content that holds the hybrid aspect
     * 
     * @return start-properties to be used when starting a workflow for this type.
     */
    Map<QName, Serializable> getStartProperties(NodeRef content, Map<QName, Serializable> properties);

    /**
     * Validates if process is in a valid state to be finished an returns properties
     * to use on aspect, if valid.
     * 
     * @param execution execution of process that is ready for result-gathering
     * @return properties to be used on the content-aspect to indicate result
     * @throws IllegalStateException when state is invalid for finishing workflow
     */
    Map<QName, Serializable> getResultsAndValidate(DelegateExecution execution) throws IllegalStateException;
}
