/*
 * Copyright 2005-2011 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.module.org_alfresco_module_cloud.workflow;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.activiti.engine.delegate.DelegateExecution;
import org.alfresco.enterprise.workflow.activiti.HybridWorkflowModel;
import org.alfresco.enterprise.workflow.activiti.HybridWorkflowModel.HybridWorkflowStatus;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.workflow.WorkflowModel;
import org.alfresco.repo.workflow.WorkflowQNameConverter;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.workflow.WorkflowException;
import org.alfresco.service.namespace.QName;

/**
 * Base {@link HybridWorkflowTypeHandler} that handles common properties.
 *
 * @author Frederik Heremans
 */
public abstract class BaseHybridWorkflowTypeHandler implements HybridWorkflowTypeHandler
{
    private static final String NODEREF_SPLIT_CHAR = ",";
    private static final String ERROR_INVALID_PERSON_NODE = "hybrid.workflow.error.invalid.person";
    protected static final String ERROR_NO_ASSIGNEE_SELECTED = "hybrid.workflow.error.no.assignee";
    
    protected ServiceRegistry serviceRegistry;
    
    protected WorkflowQNameConverter workflowQNameConverter;
    
    @Override
    public Map<QName, Serializable> getStartProperties(NodeRef content,
                Map<QName, Serializable> properties)
    {
       // Copy all common properties from the content's aspect propeties 
       Map<QName, Serializable> startProps = new HashMap<QName, Serializable>();
       startProps.put(WorkflowModel.PROP_WORKFLOW_DESCRIPTION, properties.get(HybridWorkflowModel.PROP_WORKFLOW_DESCRIPTION));
       startProps.put(WorkflowModel.PROP_WORKFLOW_DUE_DATE, properties.get(HybridWorkflowModel.PROP_WORKFLOW_DUE_DATE));
       startProps.put(WorkflowModel.PROP_WORKFLOW_PRIORITY, properties.get(HybridWorkflowModel.PROP_WORKFLOW_PRIORITY));
       startProps.put(HybridWorkflowModel.PROP_CLOUD_WORKFLOW_TYPE, properties.get(HybridWorkflowModel.PROP_CLOUD_WORKFLOW_TYPE));
       startProps.put(HybridWorkflowModel.PROP_ON_PREMISE_WORKFLOW_ID, properties.get(HybridWorkflowModel.PROP_ON_PREMISE_WORKFLOW_ID));
       startProps.put(HybridWorkflowModel.PROP_WORKFLOW_STATUS, HybridWorkflowStatus.STARTED_ON_CLOUD.getPropertyValue());
       
       // CLOUD-2108: send emails for hybrid workflows by default
       startProps.put(WorkflowModel.PROP_SEND_EMAIL_NOTIFICATIONS, Boolean.TRUE);
        
       String assigneeString = (String) properties.get(HybridWorkflowModel.PROP_ASSIGNMENT);
       List<NodeRef> assigneeNodes = new ArrayList<NodeRef>();
       
       if(assigneeString == null || assigneeString.trim().length() == 0)
       {
           // When no assignee is set, the authenticated user is used
           assigneeNodes.add(serviceRegistry.getPersonService().getPerson(AuthenticationUtil.getFullyAuthenticatedUser(), false));
       } 
       else
       {
           if(assigneeString.contains(NODEREF_SPLIT_CHAR))
           {
               StringTokenizer tokeniser = new StringTokenizer(assigneeString, NODEREF_SPLIT_CHAR);
               while(tokeniser.hasMoreTokens())
               {
                   assigneeNodes.add(new NodeRef(tokeniser.nextToken()));
               }
           }
           else
           {
               assigneeNodes.add(new NodeRef(assigneeString));
           }
           
           // Validate if node-refs exist, ignoring null-values
           for(NodeRef person : assigneeNodes)
           {
               if(person != null)
               {
                   if(!serviceRegistry.getNodeService().exists(person))
                   {
                       throw new WorkflowException(ERROR_INVALID_PERSON_NODE, person.toString());
                   }
               }
           }
           
           handleAndValidateAssignees(assigneeNodes, startProps, content);
       }
       return startProps;
    }
    

    public void setServiceRegistry(ServiceRegistry serviceRegistry)
    {
        this.serviceRegistry = serviceRegistry;
        this.workflowQNameConverter = new WorkflowQNameConverter(serviceRegistry.getNamespaceService());
    }
    
    /**
     * Extract comments from execution and apply to properties.
     * 
     * @param execution
     * @param properties
     */
    protected void populateComments(DelegateExecution execution, Map<QName, Serializable> properties)
    {
       Object commentObject = execution.getVariable(workflowQNameConverter.mapQNameToName(HybridWorkflowModel.PROP_COMMENTS));
       if(commentObject != null)
       {
          properties.put(HybridWorkflowModel.PROP_COMMENTS, (Serializable) commentObject);
       }
    }
    
    /**
     * Called when assignee(s) should be set as start-properties, should validate
     * if assignee(s) can be used for this worklfow-type. If not valid, exception is thrown.
     * 
     * @param assigneeNodes all assignees, all existing person nodes
     * @param startProps start-properties to add assignee(s) to
     */
    protected abstract void handleAndValidateAssignees(List<NodeRef> assigneeNodes,
                Map<QName, Serializable> startProps, NodeRef content);

}
