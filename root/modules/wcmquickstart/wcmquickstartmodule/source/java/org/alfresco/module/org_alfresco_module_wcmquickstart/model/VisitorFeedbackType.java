/*
 * Copyright (C) 2005-2010 Alfresco Software Limited.
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
package org.alfresco.module.org_alfresco_module_wcmquickstart.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.alfresco.module.org_alfresco_module_wcmquickstart.util.SiteHelper;
import org.alfresco.module.org_alfresco_module_wcmquickstart.util.contextparser.ContextParserService;
import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.BehaviourFilter;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.repo.policy.Behaviour.NotificationFrequency;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.transaction.AlfrescoTransactionSupport;
import org.alfresco.repo.workflow.StartWorkflowActionExecuter;
import org.alfresco.service.cmr.action.Action;
import org.alfresco.service.cmr.action.ActionService;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.security.PersonService;
import org.alfresco.service.namespace.QName;

/**
 * ws:visitorFeedback type behaviours.
 * 
 * @author Brian Remmington
 */
public class VisitorFeedbackType implements WebSiteModel
{
    private final static String AFFECTED_VISITOR_FEEDBACK = "AffectedVisitorFeedback";
    
    /** Feedback types */
    public final static String COMMENT_TYPE = "Comment";
    public final static String CONTACT_REQUEST_TYPE = "Contact Request";
    
	/** Policy component */
	private PolicyComponent policyComponent;
	
	/** Behaviour filter */
	private BehaviourFilter behaviourFilter;
	
	/** Node service */
	private NodeService nodeService;
	
	/** Action service */
	private ActionService actionService;
	
	/** Person service */
	private PersonService personService;
	
	/** Site helper */
	private SiteHelper siteHelper;
	
	/** Context parser service */
	private ContextParserService contextParserService;
	
	/**
	 * Set the policy component
	 * 
	 * @param policyComponent	policy component
	 */
	public void setPolicyComponent(PolicyComponent policyComponent) 
	{
		this.policyComponent = policyComponent;
	}
	
	/**
	 * Set the node service
	 * 
	 * @param nodeService	node service
	 */
	public void setNodeService(NodeService nodeService)
	{
		this.nodeService = nodeService;
	}
	
	/**
	 * Set the behaviour filter
	 * @param behaviourFilter	behaviour filter
	 */
	public void setBehaviourFilter(BehaviourFilter behaviourFilter)
    {
        this.behaviourFilter = behaviourFilter;
    }
	
	/**
	 * Set the action service
	 * @param actionService	action service
	 */
	public void setActionService(ActionService actionService)
    {
	    this.actionService = actionService;
    }
	
	/**
	 * Set the person service
	 * @param personService
	 */
	public void setPersonService(PersonService personService)
    {
	    this.personService = personService;
    }

	/**
	 * Sets the site helper
	 * @param siteHelper	site helper
	 */
	public void setSiteHelper(SiteHelper siteHelper)
    {
	    this.siteHelper = siteHelper;
    }
	
	/**
	 * Set the context parser service
	 * @param contextParserService	context parser service
	 */
	public void setContextParserService(ContextParserService contextParserService)
    {
	    this.contextParserService = contextParserService;
    }
	
    /**
	 * Init method.  Binds model behaviours to policies.
	 */
	public void init()
	{
        policyComponent.bindClassBehaviour(NodeServicePolicies.OnUpdatePropertiesPolicy.QNAME, 
                WebSiteModel.TYPE_VISITOR_FEEDBACK,
                new JavaBehaviour(this, "onUpdatePropertiesEveryEvent", NotificationFrequency.EVERY_EVENT));
        
        policyComponent.bindClassBehaviour(NodeServicePolicies.OnCreateNodePolicy.QNAME,
                WebSiteModel.TYPE_VISITOR_FEEDBACK, new JavaBehaviour(this, "onCreateNodeEveryEvent", 
                        NotificationFrequency.EVERY_EVENT));

        policyComponent.bindClassBehaviour(NodeServicePolicies.OnUpdatePropertiesPolicy.QNAME, 
                WebSiteModel.TYPE_VISITOR_FEEDBACK,
                new JavaBehaviour(this, "onUpdatePropertiesOnCommit", NotificationFrequency.TRANSACTION_COMMIT));
        
        policyComponent.bindClassBehaviour(NodeServicePolicies.OnCreateNodePolicy.QNAME,
                WebSiteModel.TYPE_VISITOR_FEEDBACK, new JavaBehaviour(this, "onCreateNodeOnCommit", 
                        NotificationFrequency.TRANSACTION_COMMIT));
	}

	/**
	 * On create node, every event.
	 * @param childAssocRef		child association reference
	 */
    public void onCreateNodeEveryEvent(ChildAssociationRef childAssocRef)
    {
        recordNode(childAssocRef.getChildRef());
    }
    
    /**
     * On update properties behaviour, every event
     * @param nodeRef	node reference
     * @param before	before property values
     * @param after		after property values
     */
    public void onUpdatePropertiesEveryEvent(
            NodeRef nodeRef,
            Map<QName, Serializable> before,
            Map<QName, Serializable> after)
    {
        recordNode(nodeRef);
    }
    
    /**
     * On create node behaviour, on commit.
     * @param childAssocRef		child association reference
     */
    public void onCreateNodeOnCommit(ChildAssociationRef childAssocRef)
    {
        processCommit(childAssocRef.getChildRef());        
    }
    
    /**
     * Get the feeback configuration for the relevant web site
     * @param feedback				feedback node reference
     * @return Map<String, String>  feedback configuration
     */
    @SuppressWarnings("unchecked")
    private Map<String, String> getFeedbackConfiguration(NodeRef feedback)
    {
    	Map<String, String> result = new TreeMap<String, String>();
        
    	NodeRef relevantArticle = (NodeRef)nodeService.getProperty(feedback, PROP_RELEVANT_ASSET);
        if (relevantArticle != null)
        {
        	NodeRef website = siteHelper.getRelevantWebSite(relevantArticle);
        	if (website != null)
        	{
        		List<String> feedbackConfig = (List<String>)nodeService.getProperty(website, PROP_FEEDBACK_CONFIG);
        		if (feedbackConfig != null)
        		{
	        		for (String configValue : feedbackConfig)
	                {
		                String[] configValues = configValue.split("=");
		                if (configValues.length == 2)
		                {
		                	result.put(configValues[0], 
		                			   contextParserService.parse(website, configValues[1]));
		                }
	                }
        		}
        	}
        }
        
        return result;
    }
        
    public void onUpdatePropertiesOnCommit(
            NodeRef nodeRef,
            Map<QName, Serializable> before,
            Map<QName, Serializable> after)
    {
        processCommit(nodeRef);
    }

    private void recordNode(NodeRef nodeRef)
    {
        @SuppressWarnings("unchecked")
        Set<NodeRef> affectedNodeRefs = (Set<NodeRef>) AlfrescoTransactionSupport
                .getResource(AFFECTED_VISITOR_FEEDBACK);
        if (affectedNodeRefs == null)
        {
            affectedNodeRefs = new HashSet<NodeRef>(5);
            AlfrescoTransactionSupport.bindResource(AFFECTED_VISITOR_FEEDBACK, affectedNodeRefs);
        }
        affectedNodeRefs.add(nodeRef);
    }

    private void processCommit(NodeRef nodeRef)
    {
        @SuppressWarnings("unchecked")
        Set<NodeRef> affectedNodeRefs = (Set<NodeRef>) AlfrescoTransactionSupport
                .getResource(AFFECTED_VISITOR_FEEDBACK);
        if (affectedNodeRefs != null && affectedNodeRefs.remove(nodeRef))
        {
            try
            {
                behaviourFilter.disableBehaviour(nodeRef, TYPE_VISITOR_FEEDBACK);
                Map<QName,Serializable> props = nodeService.getProperties(nodeRef);
                NodeRef relevantArticle = (NodeRef)props.get(PROP_RELEVANT_ASSET);
                List<AssociationRef> assocs = nodeService.getTargetAssocs(nodeRef, ASSOC_RELEVANT_ASSET);
                boolean existingAssocRemoved = false;
                if (!assocs.isEmpty() && !assocs.get(0).getTargetRef().equals(relevantArticle))
                {
                    nodeService.removeAssociation(nodeRef, relevantArticle, ASSOC_RELEVANT_ASSET);
                    existingAssocRemoved = true;
                }
                if (assocs.isEmpty() || existingAssocRemoved)
                {
                    nodeService.createAssociation(nodeRef, relevantArticle, ASSOC_RELEVANT_ASSET);
                }
                //Check that the two properties "commentFlagged" and "ratingProcessed" have a value, and 
                //force to the default of "false" if not
                if (!props.containsKey(PROP_COMMENT_FLAGGED))
                {
                    nodeService.setProperty(nodeRef, PROP_COMMENT_FLAGGED, Boolean.FALSE);
                }
                if (!props.containsKey(PROP_RATING_PROCESSED))
                {
                    nodeService.setProperty(nodeRef, PROP_RATING_PROCESSED, Boolean.FALSE);
                }
                

                // Start workflow for contact us feedback
            	String feedbackType = (String)nodeService.getProperty(nodeRef, PROP_FEEDBACK_TYPE);
            	if (feedbackType != null &&
            	    CONTACT_REQUEST_TYPE.equals(feedbackType) == true)
            	{
            		// Create the action
            		Action action = actionService.createAction(StartWorkflowActionExecuter.NAME);
                    action.setParameterValue(StartWorkflowActionExecuter.PARAM_WORKFLOW_NAME, PROCESS_READ_CONTACT);
                    
                    // Get feedback configuration
                    Map<String, String> feedbackConfig = getFeedbackConfiguration(nodeRef);
                    String workflowUser = null;
                    if (feedbackConfig != null)
                    {
                    	feedbackConfig.get(CONTACT_REQUEST_TYPE);
                    }
                    if (workflowUser == null)
                    {
                    	workflowUser = AuthenticationUtil.getAdminUserName();
                    }
                    
                    // Get the assignee
                    NodeRef workflowPerson = personService.getPerson(workflowUser);
                    action.setParameterValue("bpm:assignee", workflowPerson);
                    
                    // Start the workflow
                    actionService.executeAction(action, nodeRef);
            	}
            }
            finally
            {
                behaviourFilter.enableBehaviour(nodeRef, TYPE_VISITOR_FEEDBACK);
            }
        }
    }
}
