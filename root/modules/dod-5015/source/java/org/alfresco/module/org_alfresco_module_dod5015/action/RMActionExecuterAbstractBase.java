/*
 * Copyright (C) 2005-2008 Alfresco Software Limited.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.

 * As a special exception to the terms and conditions of version 2.0 of 
 * the GPL, you may redistribute this Program in connection with Free/Libre 
 * and Open Source Software ("FLOSS") applications as described in Alfresco's 
 * FLOSS exception.  You should have recieved a copy of the text describing 
 * the FLOSS exception, and it is also available here: 
 * http://www.alfresco.com/legal/licensing"
 */
package org.alfresco.module.org_alfresco_module_dod5015.action;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.alfresco.i18n.I18NUtil;
import org.alfresco.module.org_alfresco_module_dod5015.DispositionActionDefinition;
import org.alfresco.module.org_alfresco_module_dod5015.DispositionSchedule;
import org.alfresco.module.org_alfresco_module_dod5015.RecordsManagementModel;
import org.alfresco.module.org_alfresco_module_dod5015.RecordsManagementService;
import org.alfresco.module.org_alfresco_module_dod5015.audit.RecordsManagementAuditService;
import org.alfresco.module.org_alfresco_module_dod5015.capability.impl.AbstractCapability;
import org.alfresco.module.org_alfresco_module_dod5015.event.RecordsManagementEvent;
import org.alfresco.module.org_alfresco_module_dod5015.event.RecordsManagementEventService;
import org.alfresco.module.org_alfresco_module_dod5015.event.RecordsManagementEventType;
import org.alfresco.repo.action.executer.ActionExecuterAbstractBase;
import org.alfresco.repo.audit.AuditComponent;
import org.alfresco.repo.audit.AuditSession;
import org.alfresco.repo.audit.model.AuditApplication;
import org.alfresco.repo.transaction.AlfrescoTransactionSupport;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.repo.transaction.TransactionListenerAdapter;
import org.alfresco.repo.transaction.TransactionalResourceHelper;
import org.alfresco.repo.transaction.RetryingTransactionHelper.RetryingTransactionCallback;
import org.alfresco.service.cmr.action.Action;
import org.alfresco.service.cmr.action.ActionService;
import org.alfresco.service.cmr.action.ParameterDefinition;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.Period;
import org.alfresco.service.cmr.security.OwnableService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.service.namespace.RegexQNamePattern;
import org.alfresco.service.transaction.TransactionService;
import org.alfresco.util.PropertyCheck;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.util.StringUtils;

/**
 * Records management action executer base class
 * 
 * @author Roy Wetherall
 */
public abstract class RMActionExecuterAbstractBase  extends ActionExecuterAbstractBase
                                                    implements RecordsManagementAction,
                                                               RecordsManagementModel,
                                                               BeanNameAware
{
    private static final String KEY_RM_ACTION_AUDIT_PARAMETERS = "RM.ACTION.AUDIT_PARAMETERS";
    
    /** Namespace service */
    protected NamespaceService namespaceService;
    
    /** Used to control transactional behaviour including post-commit auditing */
    protected TransactionService transactionService;
    
    /** Node service */
    protected NodeService nodeService;
    
    /** Dictionary service */
    protected DictionaryService dictionaryService;
    
    /** Content service */
    protected ContentService contentService;
    
    /** Action service */
    protected ActionService actionService;
    
    /** Audit component used for recording actions */
    protected AuditComponent auditComponent;
    
    /** Records management action service */
    protected RecordsManagementActionService recordsManagementActionService;
    
    /** Records management service */
    protected RecordsManagementService recordsManagementService;
    
    /** Records management event service */
    protected RecordsManagementEventService recordsManagementEventService;
    
    /** Ownable service **/
    protected OwnableService ownableService;
    
    protected LinkedList<AbstractCapability> capabilities = new LinkedList<AbstractCapability>();;
    
    /**
     * A <b>stateless</b> listener of transactional events.
     */
    private final RMActionExecuterTxnListener txnListener;
    
    /** Default constructor */
    public RMActionExecuterAbstractBase()
    {
        txnListener = new RMActionExecuterTxnListener();
    }
    
    /**
     * Set the namespace service
     */
    public void setTransactionService(TransactionService transactionService)
    {
        this.transactionService = transactionService;
    }
    
    /**
     * Set the namespace service
     */
    public void setNamespaceService(NamespaceService namespaceService)
    {
        this.namespaceService = namespaceService;
    }
    
    /**
     * Set node service
     */
    public void setNodeService(NodeService nodeService)
    {
        this.nodeService = nodeService;
    }
    
    /**
     * Set the dictionary service
     */
    public void setDictionaryService(DictionaryService dictionaryService)
    {
        this.dictionaryService = dictionaryService;
    }
    
    /**
     * Set the content service
     */
    public void setContentService(ContentService contentService)
    {
        this.contentService = contentService;
    }
    
    /**
     * Set action service 
     */
    public void setActionService(ActionService actionService)
    {
        this.actionService = actionService;
    }

    /**
     * Set the audit component that will be used to record actions occuring
     */
    public void setAuditComponent(AuditComponent auditComponent)
    {
        this.auditComponent = auditComponent;
    }

    /**
     * Set records management service
     */
    public void setRecordsManagementActionService(RecordsManagementActionService recordsManagementActionService)
    {
        this.recordsManagementActionService = recordsManagementActionService;
    }
    
    /**
     * Set records management service
     */
    public void setRecordsManagementService(RecordsManagementService recordsManagementService)
    {
        this.recordsManagementService = recordsManagementService;
    }    
    
    /** 
     * Set records management event service
     */
    public void setRecordsManagementEventService(RecordsManagementEventService recordsManagementEventService)
    {
        this.recordsManagementEventService = recordsManagementEventService;
    }
    
    
    /**
     * Set the ownable service
     * @param ownableSerice
     */
    public void setOwnableService(OwnableService ownableService)
    {
        this.ownableService = ownableService;
    }

    /**
     * Register with a single capability
     * @param capability
     */
    public void setCapability(AbstractCapability capability)
    {
        capabilities.add(capability);
    }
    
    /**
     * Register with several capabilities
     * @param capabilities
     */
    public void setCapabilities(Collection<AbstractCapability> capabilities)
    {
        this.capabilities.addAll(capabilities);
    }

    /**
     * Init method
     */
    @Override
    public void init()
    {
        PropertyCheck.mandatory(this, "namespaceService", namespaceService);
        PropertyCheck.mandatory(this, "transactionService", transactionService);
        PropertyCheck.mandatory(this, "nodeService", nodeService);
        PropertyCheck.mandatory(this, "dictionaryService", dictionaryService);
        PropertyCheck.mandatory(this, "contentService", contentService);
        PropertyCheck.mandatory(this, "actionService", actionService);
        PropertyCheck.mandatory(this, "auditComponent", auditComponent);
        PropertyCheck.mandatory(this, "transactionService", transactionService);
        PropertyCheck.mandatory(this, "recordsManagementActionService", recordsManagementActionService);
        PropertyCheck.mandatory(this, "recordsManagementService", recordsManagementService);
        PropertyCheck.mandatory(this, "recordsManagementEventService", recordsManagementEventService);
        for(AbstractCapability capability : capabilities)
        {
            capability.registerAction(this);
        }
    }
    
    /**
     * @see org.alfresco.repo.action.CommonResourceAbstractBase#setBeanName(java.lang.String)
     */
    @Override
    public void setBeanName(String name)
    {
        this.name = name;
    }
    
    /**
     * @see org.alfresco.module.org_alfresco_module_dod5015.RecordsManagementAction#getName()
     */
    public String getName()
    {
        return this.name;
    }
    
    /*
     * @see org.alfresco.module.org_alfresco_module_dod5015.action.RecordsManagementAction#getLabel()
     */
    public String getLabel()
    {
        String label = I18NUtil.getMessage(this.getTitleKey());
        
        if (label == null)
        {
            // default to the name of the action with first letter capitalised
            label = StringUtils.capitalize(this.name);
        }
        
        return label;
    }
    
    /*
     * @see org.alfresco.module.org_alfresco_module_dod5015.action.RecordsManagementAction#getDescription()
     */
    public String getDescription()
    {
        String desc = I18NUtil.getMessage(this.getDescriptionKey());
        
        if (desc == null)
        {
            // default to the name of the action with first letter capitalised
            desc = StringUtils.capitalize(this.name);
        }
        
        return desc;
    }

    /**
     * By default an action is not a disposition action
     * 
     * @see org.alfresco.module.org_alfresco_module_dod5015.RecordsManagementAction#isDispositionAction()
     */
    public boolean isDispositionAction()
    {
        return false;
    }
    
    /**
     * @see org.alfresco.module.org_alfresco_module_dod5015.RecordsManagementAction#execute(org.alfresco.service.cmr.repository.NodeRef, java.util.Map)
     */
    public void execute(NodeRef filePlanComponent, Map<String, Serializable> parameters)
    {
        isExecutableImpl(filePlanComponent, parameters, true);
        
        // Create the action
        Action action = this.actionService.createAction(name);
        action.setParameterValues(parameters);
        
        // Audit: Bind a txn listener to the current transaction
        AlfrescoTransactionSupport.bindListener(this.txnListener);
        // Audit: Bind the audit parameters to the current transaction
        List<RMActionExecutorAuditParameters> boundAuditParams = TransactionalResourceHelper.getList(
                KEY_RM_ACTION_AUDIT_PARAMETERS);
        RMActionExecutorAuditParameters auditParams = new RMActionExecutorAuditParameters(
                this,
                filePlanComponent,
                parameters);
        boundAuditParams.add(auditParams);
        
        // Execute the action
        this.actionService.executeAction(action, filePlanComponent);          
    }
    
    /**
     * A class to carry audit information through the transaction.
     * 
     * @author Derek Hulley
     * @since 3.2
     */
    private static class RMActionExecutorAuditParameters
    {
        /*
         * No getters because they are final and the class is only used internally
         */
        
        private final RMActionExecuterAbstractBase action;
        private final NodeRef nodeRef;
        private final Map<String, Serializable> parameters;
        
        private RMActionExecutorAuditParameters(
                RMActionExecuterAbstractBase action,
                NodeRef nodeRef,
                Map<String, Serializable> parameters)
        {
            this.action = action;
            this.nodeRef = nodeRef;
            if (this.parameters != null)
            {
                this.parameters = new HashMap<String, Serializable>(parameters);   // Deliberate copy
            }
            else
            {
                this.parameters = new HashMap<String, Serializable>(0);
            }
        }

        public RMActionExecuterAbstractBase getAction()
        {
            return action;
        }
        public NodeRef getNodeRef()
        {
            return nodeRef;
        }
        public Map<String, Serializable> getParameters()
        {
            return parameters;
        }
    }
    
    /**
     * A <b>stateless</b> transaction listener for RM actions.  Amongst other things, auditing of actions
     * is done by this component.
     * <p/>
     * This class is not static so that the instances will have access to the action's implementation.
     * 
     * @author Derek Hulley
     * @since 3.2
     */
    private class RMActionExecuterTxnListener extends TransactionListenerAdapter
    {
        private final Log logger = LogFactory.getLog(RMActionExecuterAbstractBase.class);
        
        /*
         * Equality and hashcode generation are left unimplemented; we expect to only have a single
         * instance of this class per action.
         */

        /**
         * Get the action parameters from the transaction and audit them.
         */
        @Override
        public void afterCommit()
        {
            final List<RMActionExecutorAuditParameters> boundAuditParams = TransactionalResourceHelper.getList(
                    KEY_RM_ACTION_AUDIT_PARAMETERS);
            // Shortcut if there is nothing to audit
            if (boundAuditParams.size() == 0)
            {
                if (logger.isDebugEnabled())
                {
                    logger.debug("No audit parameters bound to transaction.");
                }
                return;
            }
            
            // Start a *new* read-write transaction to audit in
            RetryingTransactionCallback<Void> auditCallback = new RetryingTransactionCallback<Void>()
            {
                public Void execute() throws Throwable
                {
                    auditInTxn(boundAuditParams);
                    return null;
                }
            };
            transactionService.getRetryingTransactionHelper().doInTransaction(auditCallback, false, true);
        }

        /**
         * Do the actual auditing, assuming the presence of a viable transaction
         * 
         * @param boundAuditParams          the parameters to audit
         */
        private void auditInTxn(List<RMActionExecutorAuditParameters> boundAuditParams) throws Throwable
        {
            // Start and audit session
            AuditSession auditSession = auditComponent.startAuditSession(
                    RecordsManagementAuditService.RM_AUDIT_APPLICATION_NAME,
                    RecordsManagementAuditService.RM_AUDIT_PATH_ROOT);
            if (auditSession == null)
            {
                if (logger.isDebugEnabled())
                {
                    logger.debug("RM Audit: No session created for application.");
                }
                // There is nothing to do, and nothing to commit
                RetryingTransactionHelper.getActiveUserTransaction().setRollbackOnly();
                return;
            }
            // Go through all the audit information and audit it
            boolean auditedSomething = false;                       // We rollback if nothing is audited
            for (RMActionExecutorAuditParameters auditParams : boundAuditParams)
            {
                Map<String, Serializable> auditMap = new HashMap<String, Serializable>(13);
                // The node
                String actionName = auditParams.getAction().getName();
                String actionPath = AuditApplication.buildPath(
                                RecordsManagementAuditService.RM_AUDIT_PATH_ACTIONS,
                                actionName);
                auditMap.put(
                        AuditApplication.buildPath(
                                actionPath,
                                RecordsManagementAuditService.RM_AUDIT_PATH_ACTIONS_NODE),
                        auditParams.getNodeRef());
                for (Map.Entry<String, Serializable> actionParam : auditParams.getParameters().entrySet())
                {
                    auditMap.put(
                            AuditApplication.buildPath(
                                    actionPath,
                                    RecordsManagementAuditService.RM_AUDIT_PATH_ACTIONS_PARAMS,
                                    actionParam.getKey()),
                            actionParam.getValue());
                }
                if (logger.isDebugEnabled())
                {
                    logger.debug("RM Audit: Auditing values: \n" + auditMap);
                }
                auditMap = auditComponent.audit(auditSession, auditMap);
                if (auditMap.isEmpty())
                {
                    if (logger.isDebugEnabled())
                    {
                        logger.debug("RM Audit: Nothing was audited.");
                    }
                }
                else
                {
                    if (logger.isDebugEnabled())
                    {
                        logger.debug("RM Audit: Audited values: \n" + auditMap);
                    }
                    // We must commit the transaction to get the values in
                    auditedSomething = true;
                }
            }
            // Check if anything was audited
            if (!auditedSomething)
            {
                // Nothing was audited, so do nothing
                RetryingTransactionHelper.getActiveUserTransaction().setRollbackOnly();
            }
        }
    }
    
    /**
     * Function to pad a string with zero '0' characters to the required length
     * 
     * @param s     String to pad with leading zero '0' characters
     * @param len   Length to pad to
     * 
     * @return padded string or the original if already at >=len characters 
     */
    protected String padString(String s, int len)
    {
       String result = s;
       for (int i=0; i<(len - s.length()); i++)
       {
           result = "0" + result;
       }
       return result;
    }    
    
    /**
     * By default there are no parameters.
     * 
     * @see org.alfresco.repo.action.ParameterizedItemAbstractBase#addParameterDefinitions(java.util.List)
     */
    @Override
    protected void addParameterDefinitions(List<ParameterDefinition> paramList)
    {
        // No parameters
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.module.org_alfresco_module_dod5015.action.RecordsManagementAction#getProtectedProperties()
     */
    public Set<QName> getProtectedProperties()
    {
       return Collections.<QName>emptySet();
    }
    

    /*
     * (non-Javadoc)
     * @see org.alfresco.module.org_alfresco_module_dod5015.action.RecordsManagementAction#getProtectedAspects()
     */
    public Set<QName> getProtectedAspects()
    {
        return Collections.<QName>emptySet();
    }

    /* (non-Javadoc)
     * @see org.alfresco.module.org_alfresco_module_dod5015.action.RecordsManagementAction#isExecutable(org.alfresco.service.cmr.repository.NodeRef, java.util.Map)
     */
    public boolean isExecutable(NodeRef filePlanComponent, Map<String, Serializable> parameters)
    {
        return isExecutableImpl(filePlanComponent, parameters, false);
    }
    
    protected abstract boolean isExecutableImpl(NodeRef filePlanComponent, Map<String, Serializable> parameters, boolean throwException);

    /**
     * By default, rmActions do not provide an implicit target nodeRef.
     */
    public NodeRef getImplicitTargetNodeRef()
    {
        return null;
    }

    /**
     * @see org.alfresco.module.org_alfresco_module_dod5015.RecordsManagementService#updateNextDispositionAction(org.alfresco.service.cmr.repository.NodeRef)
     */
    public void updateNextDispositionAction(NodeRef nodeRef)
    {
        // Get this disposition instructions for the node
        DispositionSchedule di = recordsManagementService.getDispositionSchedule(nodeRef);
        if (di != null)
        {
            // Get the current action node
            NodeRef currentDispositionAction = null;
            if (this.nodeService.hasAspect(nodeRef, ASPECT_DISPOSITION_LIFECYCLE) == true)
            {
                List<ChildAssociationRef> assocs = this.nodeService.getChildAssocs(nodeRef, ASSOC_NEXT_DISPOSITION_ACTION, RegexQNamePattern.MATCH_ALL);
                if (assocs.size() > 0)
                {
                    currentDispositionAction = assocs.get(0).getChildRef();
                }
            }
            
            if (currentDispositionAction != null)
            {
                // Move it to the history association
                this.nodeService.moveNode(currentDispositionAction, nodeRef, ASSOC_DISPOSITION_ACTION_HISTORY, ASSOC_DISPOSITION_ACTION_HISTORY);
            }
           
            List<DispositionActionDefinition> dispositionActionDefinitions = di.getDispositionActionDefinitions();
            DispositionActionDefinition currentDispositionActionDefinition = null;
            DispositionActionDefinition nextDispositionActionDefinition = null;
            
            if (currentDispositionAction == null)
            {
                if (dispositionActionDefinitions.isEmpty() == false)
                {
                    // The next disposition action is the first action
                    nextDispositionActionDefinition = dispositionActionDefinitions.get(0);
                }
            }
            else
            {
                // Get the current action
                String currentADId = (String)this.nodeService.getProperty(currentDispositionAction, PROP_DISPOSITION_ACTION_ID);
                currentDispositionActionDefinition = di.getDispositionActionDefinition(currentADId);
                
                // Get the next disposition action
                int index = currentDispositionActionDefinition.getIndex();
                index++;
                if (index < dispositionActionDefinitions.size())
                {
                    nextDispositionActionDefinition = dispositionActionDefinitions.get(index);
                }
            }
            
            if (nextDispositionActionDefinition != null)
            {
                if (this.nodeService.hasAspect(nodeRef, ASPECT_DISPOSITION_LIFECYCLE) == false)
                {
                    // Add the disposition life cycle aspect
                    this.nodeService.addAspect(nodeRef, ASPECT_DISPOSITION_LIFECYCLE, null);
                }
                
                // Create the properties
                Map<QName, Serializable> props = new HashMap<QName, Serializable>(10);
                
                // Calculate the asOf date
                Date asOfDate = null;
                Period period = nextDispositionActionDefinition.getPeriod();
                if (period != null)
                {
                    // Use NOW as the default context date
                    Date contextDate = new Date();
                    
                    // Get the period properties value
                    QName periodProperty = nextDispositionActionDefinition.getPeriodProperty();
                    if (periodProperty != null)
                    {
                        contextDate = (Date)this.nodeService.getProperty(nodeRef, periodProperty);
                        
                        if (contextDate == null)
                        {
                            // TODO For now we will use NOW to resolve MOB-1184
                            //throw new AlfrescoRuntimeException("Date used to calculate disposition action asOf date is not set for property " + periodProperty.toString());
                            contextDate = new Date();
                        }
                    }
                    
                    // Calculate the as of date
                    asOfDate = period.getNextDate(contextDate);
                }            
                
                // Set the property values
                props.put(PROP_DISPOSITION_ACTION_ID, nextDispositionActionDefinition.getId());
                props.put(PROP_DISPOSITION_ACTION, nextDispositionActionDefinition.getName());
                if (asOfDate != null)
                {
                    props.put(PROP_DISPOSITION_AS_OF, asOfDate);
                }
                
                // Create a new disposition action object
                NodeRef dispositionActionNodeRef = this.nodeService.createNode(
                        nodeRef, 
                        ASSOC_NEXT_DISPOSITION_ACTION, 
                        ASSOC_NEXT_DISPOSITION_ACTION, 
                        TYPE_DISPOSITION_ACTION,
                        props).getChildRef();     
                
                // Create the events
                List<RecordsManagementEvent> events = nextDispositionActionDefinition.getEvents();
                for (RecordsManagementEvent event : events)
                {
                    // For every event create an entry on the action
                    Map<QName, Serializable> eventProps = new HashMap<QName, Serializable>(7);
                    eventProps.put(PROP_EVENT_EXECUTION_NAME, event.getName());
                    // TODO display label
                    RecordsManagementEventType eventType = recordsManagementEventService.getEventType(event.getType());
                    eventProps.put(PROP_EVENT_EXECUTION_AUTOMATIC, eventType.isAutomaticEvent());
                    eventProps.put(PROP_EVENT_EXECUTION_COMPLETE, false);
                    
                    // Create the event execution object
                    this.nodeService.createNode(
                            dispositionActionNodeRef,
                            ASSOC_EVENT_EXECUTIONS,
                            ASSOC_EVENT_EXECUTIONS,
                            TYPE_EVENT_EXECUTION,
                            eventProps);
                }
            }
        }
    }
}
