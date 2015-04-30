/*
 * Copyright 2005-2013 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.enterprise.repo.sync;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.alfresco.enterprise.repo.sync.audit.SyncAuditService;
import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.model.ContentModel;
import org.alfresco.repo.coci.CheckOutCheckInServicePolicies;
import org.alfresco.repo.content.ContentServicePolicies;
import org.alfresco.repo.content.ContentServicePolicies.OnContentPropertyUpdatePolicy;
import org.alfresco.repo.copy.CopyBehaviourCallback;
import org.alfresco.repo.copy.CopyDetails;
import org.alfresco.repo.copy.CopyServicePolicies;
import org.alfresco.repo.copy.DefaultCopyBehaviourCallback;
import org.alfresco.repo.domain.qname.QNameDAO;
import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.node.NodeServicePolicies.BeforeDeleteChildAssociationPolicy;
import org.alfresco.repo.node.NodeServicePolicies.BeforeDeleteNodePolicy;
import org.alfresco.repo.node.NodeServicePolicies.BeforeMoveNodePolicy;
import org.alfresco.repo.node.NodeServicePolicies.BeforeRemoveAspectPolicy;
import org.alfresco.repo.node.NodeServicePolicies.OnAddAspectPolicy;
import org.alfresco.repo.node.NodeServicePolicies.OnCreateAssociationPolicy;
import org.alfresco.repo.node.NodeServicePolicies.OnCreateChildAssociationPolicy;
import org.alfresco.repo.node.NodeServicePolicies.OnCreateNodePolicy;
import org.alfresco.repo.node.NodeServicePolicies.OnDeleteAssociationPolicy;
import org.alfresco.repo.node.NodeServicePolicies.OnRemoveAspectPolicy;
import org.alfresco.repo.node.NodeServicePolicies.OnUpdatePropertiesPolicy;
import org.alfresco.repo.policy.Behaviour;
import org.alfresco.repo.policy.Behaviour.NotificationFrequency;
import org.alfresco.repo.policy.BehaviourFilter;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.repo.security.permissions.AccessDeniedException;
import org.alfresco.repo.tenant.TenantUtil;
import org.alfresco.repo.transaction.AlfrescoTransactionSupport;
import org.alfresco.repo.transaction.RetryingTransactionHelper.RetryingTransactionCallback;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.repo.transaction.TransactionListenerAdapter;
import org.alfresco.repo.transaction.TransactionalResourceHelper;
import org.alfresco.repo.version.VersionRevertCallback;
import org.alfresco.repo.version.VersionRevertDetails;
import org.alfresco.repo.version.VersionServicePolicies;
import org.alfresco.service.cmr.dictionary.AspectDefinition;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.dictionary.PropertyDefinition;
import org.alfresco.service.cmr.model.FileFolderService;
import org.alfresco.service.cmr.model.FileInfo;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.ContentData;
import org.alfresco.service.cmr.repository.InvalidNodeRefException;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.service.transaction.TransactionService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationEvent;
import org.springframework.extensions.surf.util.AbstractLifecycleBean;

/**
 * This class is responsible for monitoring any and all sync-relevant changes to content nodes.
 * For a change to be relevant, it must affect a node which is part of the Cloud Sync feature
 * (e.g. a {@link SyncModel#TYPE_SYNC_SET_DEFINITION} (On Premise only) or a {@link SyncModel#ASPECT_SYNC_SET_MEMBER_NODE})
 * and it must affect a property/aspect of that node that is relevant for syncing i.e. only value changes to
 * properties which are synced are relevant, not value changes to properties which are not synced.
 * <p/>
 * This class registers {@link Behaviour behaviours} on the relevant types/aspects and these behaviours
 * pass the data associated with the sync-relevant change to the {@link SyncAuditService} for persistence.
 * <p/>
 * The definition of what is relevant and what is not is provided by spring config. See sync-service-context.xml.
 * 
 * @author Neil Mc Erlean, janv
 * @since 4.1
 */
public class SyncChangeMonitor extends AbstractLifecycleBean
                               implements NodeServicePolicies.OnAddAspectPolicy,
                                          NodeServicePolicies.BeforeRemoveAspectPolicy,
                                          NodeServicePolicies.OnUpdatePropertiesPolicy,
                                          NodeServicePolicies.BeforeDeleteChildAssociationPolicy,
                                          NodeServicePolicies.OnCreateChildAssociationPolicy,
                                          NodeServicePolicies.BeforeMoveNodePolicy,
                                          ContentServicePolicies.OnContentPropertyUpdatePolicy
{
    private static final Log log = LogFactory.getLog(SyncChangeMonitor.class);
    
    /** Property & aspect changes on these content types are always tracked. */
    private static final List<QName> BINDING_CLASSES_FOR_PROP_TRACKING = Arrays.asList(new QName[] { SyncModel.ASPECT_SYNC_SET_MEMBER_NODE} );
    
    
    // Required services
    private TransactionService transactionService;
    private BehaviourFilter behaviourFilter;
    private DictionaryService dictionaryService;
    private NamespaceService namespaceService;
    private NodeService nodeService;
    private FileFolderService fileFolderService;
    private PolicyComponent policyComponent;
    private SyncAdminService syncAdminService;
    private SyncAuditService syncAuditService;
    private QNameDAO qnameDAO;
    
    /**
     * We'll only track property value changes for these properties.    This is the static set initialized at boot time.
     */
    // private List<QName> propertiesToTrack = new ArrayList<QName>();   
    private DynamicQNameSet dynamicPropertiesToTrack = new DynamicQNameSet();
    
    /**
     * We'll only track add/remove aspect for these aspects.   This is the static set initialized at boot time.
     */
    // private List<QName> aspectsToTrack = new ArrayList<QName>();   
    private DynamicQNameSet dynamicAspectsToTrack = new DynamicQNameSet();
   
    
    private static final String KEY_TXN_RES_MOVE_NODES      = "syncChangeMonitor.moveNodes";
    private static final String KEY_TXN_RES_CHECKOUT_NODES  = "syncChangeMonitor.checkOutNodes";
    private static final String KEY_TXN_RES_PENDING_NODES   = "syncChangeMonitor.pendingNodes";
    private static final String KEY_TXN_RES_DELETE_NODES = "syncChangeMonitor.deleteNodes";
    
    /** Transaction listener */
    private SyncChangeMonitorCreateTransactionListener createTransactionListener;
    
    public void setTransactionService(TransactionService transactionService)
    {
        this.transactionService = transactionService;
    }
    
    public void setBehaviourFilter(BehaviourFilter filter)
    {
        this.behaviourFilter = filter;
    }
    
    public void setDictionaryService(DictionaryService service)
    {
        this.dictionaryService = service;
    }
    
    public void setPolicyComponent(PolicyComponent pc)
    {
        this.policyComponent = pc;
    }
    
    public void setNamespaceService(NamespaceService service)
    {
        this.namespaceService = service;
    }
    
    public void setNodeService(NodeService service)
    {
        this.nodeService = service;
    }
    
    public void setFileFolderService(FileFolderService service)
    {
        this.fileFolderService = service;
    }
    
    public void setSyncAuditService(SyncAuditService service)
    {
        this.syncAuditService = service;
    }
    
    public void setSyncAdminService(SyncAdminService service)
    {
        this.syncAdminService = service;
    }
    
    public void setQnameDAO(QNameDAO dao)
    {
        this.qnameDAO = dao;
    }
  
    public void addCustomPropertyToTrack(QName qname)
    {
    	dynamicPropertiesToTrack.add(qname);
    	
    }
    public void removeCustomPropertyToTrack(QName qname)
    {
    	dynamicPropertiesToTrack.remove(qname);
    	
    }
    public void addCustomAspectToTrack(QName qname)
    {
    	dynamicAspectsToTrack.add(qname);
    	
    }
    public void removeCustomAspectToTrack(QName qname)
    {
    	dynamicAspectsToTrack.remove(qname);
    
    }
    
    /**
     * This method takes the injected property names or aspect name wildcards, validating that they are recognised property QNames.
     * @param propIdentifiers a list of property identifiers where each is either a property name such as "cm:name" or an aspect name with wildcard: "cm:titled.*"
     * @throws AlfrescoRuntimeException if any name is invalid.
     */
    public void setPropertiesToTrack(List<String> propIdentifiers)
    {
        // this.propertiesToTrack = new ArrayList<QName>();
        
        final Pattern propertyWildcardRegEx = Pattern.compile("(.+)\\.\\*");
        
        for (final String propertyIdentifier : propIdentifiers)
        {
            if (log.isDebugEnabled())
            {
                log.debug("Setting property to track: '" + propertyIdentifier + "'");
            }
            
            Matcher m = propertyWildcardRegEx.matcher(propertyIdentifier);
            if (m.matches())
            {
                // Then we must have the name of an aspect
                final String aspectName = m.group(1);
                
                final QName aspectQName = QName.createQName(aspectName, namespaceService);
                AspectDefinition aspectDef = dictionaryService.getAspect(aspectQName);
                if (aspectDef != null)
                {
                    Map<QName, PropertyDefinition> propDefs = aspectDef.getProperties();
                    for (QName qname : propDefs.keySet())
                    {
                        // Note that the above propDefs will include all inherited properties.
           //             this.propertiesToTrack.add(qname);
                        if(log.isDebugEnabled())
                        {
                            log.debug("tracking property : " + qname);
                        }
                        dynamicPropertiesToTrack.add(qname);
                    }
                }
                else
                {
                    throw new AlfrescoRuntimeException("Unrecognised aspect name: " + aspectName);
                }
            }
            else
            {
                // else we must have the name of a property.
                final QName propQName = QName.createQName(propertyIdentifier, namespaceService);
                final PropertyDefinition propertyDefn = dictionaryService.getProperty(propQName);
                if (propertyDefn == null)
                {
                    throw new AlfrescoRuntimeException("Unrecognised property name: " + propertyIdentifier);
                }
                if(log.isDebugEnabled())
                {
                    log.debug("tracking property : " + propQName);
                }
                dynamicPropertiesToTrack.add(propQName);
            }
        }
    }
    
    /**
     * This method returns an unmodifiable list of which (non-content) properties are being tracked for sync changes.
     */
    public List<QName> getPropertiesToTrack()
    {
    	return new ArrayList<QName>(dynamicPropertiesToTrack.getValues());
    }
    
    /**
     * This method takes the injected aspect names, validating that they are recognised aspect QNames.
     * @throws AlfrescoRuntimeException if any name is invalid.
     */
    public void setAspectsToTrack(List<String> qnames)
    {
        // This method basically converts the String-based names provided by spring into validated QNames.
        
        // this.aspectsToTrack = new ArrayList<QName>(qnames.size());
        for (String aspectName : qnames)
        {
            QName aspectQName = QName.createQName(aspectName, namespaceService);
            if (dictionaryService.getAspect(aspectQName) == null)
            {
                throw new AlfrescoRuntimeException("Unrecognised aspect name: " + aspectName);
            }
            if(log.isDebugEnabled())
            {
                log.debug("tracking aspect : " + aspectQName);
            }
            dynamicAspectsToTrack.add(aspectQName);
        }
    }
    
    /**
     * This method returns an unmodifiable list of which aspects are being tracked for sync changes.
     */
    public List<QName> getAspectsToTrack()
    {
    	return new ArrayList<QName>(dynamicAspectsToTrack.getValues());
    }
    
    /**
     * This method (called from Spring) binds all the behaviours associated with the sync-relevant properties & aspects.
     */
    @Override protected void onBootstrap(ApplicationEvent event)
    {
        // We need to ensure that the SyncSetDefinition QName is created in the database during system startup.
        // This does not matter for product code where we will have an On Premise Alfresco talking to a separate Cloud Alfresco,
        // but during developer testing where our On Premise Alfresco 'loops back' to itself, we would see DB exceptions during
        // the first attempt to sync a node via Share. This is caused by the creation of a local SSD node and the synchronous creation
        // of a remote SSD node in two transactions/threads which would always fail.
        RetryingTransactionHelper txnHelper = transactionService.getRetryingTransactionHelper();
        txnHelper.setForceWritable(true);           // We HAVE to write regardless of the repo read/write state
        RetryingTransactionCallback<Void> ensureQNamePresent = new RetryingTransactionCallback<Void>()
        {
            @Override
            public Void execute() throws Throwable
            {
                qnameDAO.getOrCreateQName(SyncModel.TYPE_SYNC_SET_DEFINITION);
                return null;
            }
        };
        txnHelper.doInTransaction(ensureQNamePresent, false, false);
        
        // Note that we are intentionally only registering these behaviors when the system has started up and not in a spring init-method
        
        // Note always bind behaviours, even if sync is OFF at the moment,  it may be turned on at runtime by a license 
        bindBehavioursToSsds();
        bindBehavioursToSsmns();
        bindBehavioursToFolderSync();
        bindAspectsToTrack();
        bindPropertiesToTrack();
        
        // Create the transaction listener
        this.createTransactionListener = new SyncChangeMonitorCreateTransactionListener();
        
        // We do not support syncing of any user-configurable associations and hence there are no behaviours registered against associations here.
    }
    
    private void bindPropertiesToTrack()
    {
        for (QName relevantContentClass : BINDING_CLASSES_FOR_PROP_TRACKING)
        {
            // node property tracking
            if (log.isDebugEnabled())
            {
                log.debug("Binding sync behaviours for tracking all property changes on content class: " + relevantContentClass.getPrefixString());
            }
            
            // content property updates
            this.policyComponent.bindClassBehaviour(
                    OnContentPropertyUpdatePolicy.QNAME, 
                    relevantContentClass, 
                    new JavaBehaviour(this, "onContentPropertyUpdate", Behaviour.NotificationFrequency.EVERY_EVENT));
            
            // non-content property updates
            this.policyComponent.bindClassBehaviour(
                    OnUpdatePropertiesPolicy.QNAME,
                    relevantContentClass, 
                    new JavaBehaviour(this, "onUpdateProperties", Behaviour.NotificationFrequency.EVERY_EVENT));
        }
    }
        
    private void bindAspectsToTrack()
    {        
        // aspect tracking
        // TODO We must attach the behaviours to a very broad content class in order to catch the addition/removal of interesting aspects.
        //      This will fire the behaviour for the addition of the interesting aspects for many nodes which are irrelevant to sync.
        //      Consider a more performant solution.
 
        this.policyComponent.bindClassBehaviour( OnAddAspectPolicy.QNAME,
    	  	this, 
    	    new JavaBehaviour(this, "onAddAspect", Behaviour.NotificationFrequency.EVERY_EVENT));
        
        this.policyComponent.bindClassBehaviour( OnRemoveAspectPolicy.QNAME,
    	  	this, 
    	    new JavaBehaviour(this, "beforeRemoveAspect", Behaviour.NotificationFrequency.EVERY_EVENT));       
        
    }
    
    
    @Override protected void onShutdown(ApplicationEvent event)
    {
        // Intentionally empty
    }
    
    public void disableSyncBehaviours()
    {
        behaviourFilter.disableBehaviour(SyncModel.ASPECT_SYNC_SET_MEMBER_NODE);
        behaviourFilter.disableBehaviour(SyncModel.TYPE_SYNC_SET_DEFINITION);
    }
    
    public void enableSyncBehaviours()
    {
        behaviourFilter.enableBehaviour(SyncModel.ASPECT_SYNC_SET_MEMBER_NODE);
        behaviourFilter.enableBehaviour(SyncModel.TYPE_SYNC_SET_DEFINITION);
    }
    
    /** This method binds Alfresco behaviours to {@link SyncModel#TYPE_SYNC_SET_DEFINITION SSDs}. */
    private void bindBehavioursToSsds()
    {
        if (log.isDebugEnabled())
        {
            log.debug("Binding sync behaviours for tracking SSDs");
        }
        
        // SSD creation
        this.policyComponent.bindClassBehaviour(
                OnCreateNodePolicy.QNAME, 
                SyncModel.TYPE_SYNC_SET_DEFINITION, 
                new JavaBehaviour(this, "onCreateSsdNode", Behaviour.NotificationFrequency.EVERY_EVENT));
        
        // SSD deletion
        this.policyComponent.bindClassBehaviour(
                BeforeDeleteNodePolicy.QNAME, 
                SyncModel.TYPE_SYNC_SET_DEFINITION, 
                new JavaBehaviour(this, "beforeDeleteSsdNode", Behaviour.NotificationFrequency.EVERY_EVENT));
        
    }
    
    /** This method binds Alfresco behaviours for folder sync - to enable auto addition/removal of SSMNs (initially immediate children => files only) */
    private void bindBehavioursToFolderSync()
    {
        if (log.isDebugEnabled())
        {
            log.debug("Binding sync behaviours for tracking folder sync");
        }
        
        // child create - where parent is SSMN (=> sync folder)
        this.policyComponent.bindAssociationBehaviour(
                OnCreateChildAssociationPolicy.QNAME,
                SyncModel.ASPECT_SYNC_SET_MEMBER_NODE,
                ContentModel.ASSOC_CONTAINS,
                new JavaBehaviour(this, "onCreateChildAssociation", Behaviour.NotificationFrequency.TRANSACTION_COMMIT));
        
        // child delete - where parent is SSMN (=> sync folder)
        this.policyComponent.bindAssociationBehaviour(
                BeforeDeleteChildAssociationPolicy.QNAME,
                SyncModel.ASPECT_SYNC_SET_MEMBER_NODE,
                ContentModel.ASSOC_CONTAINS,
                new JavaBehaviour(this, "beforeDeleteChildAssociation", Behaviour.NotificationFrequency.EVERY_EVENT));
        
        this.policyComponent.bindClassBehaviour(
                BeforeMoveNodePolicy.QNAME,
                SyncModel.ASPECT_SYNC_SET_MEMBER_NODE,
                new JavaBehaviour(this, "beforeMoveNode", Behaviour.NotificationFrequency.EVERY_EVENT));
        
        // eg. CLOUD-950 - GoogleDoc creation (in a synced folder)
        this.policyComponent.bindClassBehaviour(
                OnRemoveAspectPolicy.QNAME,
                ContentModel.ASPECT_TEMPORARY,
                new JavaBehaviour(this, "onRemoveTemporaryAspect", Behaviour.NotificationFrequency.EVERY_EVENT));
    }
    
    /** This method binds Alfresco behaviours to {@link SyncModel#ASPECT_SYNC_SET_MEMBER_NODE SSMNs}. */
    private void bindBehavioursToSsmns()
    {
        if (log.isDebugEnabled())
        {
            log.debug("Binding sync behaviours for tracking SSMNs");
        }
        
        // SSMN added to set
        this.policyComponent.bindAssociationBehaviour(
                OnCreateAssociationPolicy.QNAME, 
                SyncModel.TYPE_SYNC_SET_DEFINITION,
                SyncModel.ASSOC_SYNC_MEMBERS, 
                new JavaBehaviour(this, "onCreateSsmnAssociation", Behaviour.NotificationFrequency.EVERY_EVENT));
        
        // SSMN removed from set
        // 1. By breaking the peer assoc
        this.policyComponent.bindAssociationBehaviour(
                OnDeleteAssociationPolicy.QNAME, 
                SyncModel.TYPE_SYNC_SET_DEFINITION,
                SyncModel.ASSOC_SYNC_MEMBERS, 
                new JavaBehaviour(this, "onDeleteSsmnAssociation", Behaviour.NotificationFrequency.EVERY_EVENT));
        // 2. By deleting the node itself
        this.policyComponent.bindClassBehaviour(
                BeforeDeleteNodePolicy.QNAME, 
                SyncModel.ASPECT_SYNC_SET_MEMBER_NODE, 
                new JavaBehaviour(this, "beforeDeleteSsmnNode", Behaviour.NotificationFrequency.EVERY_EVENT));
        
        // SSMN copy (or checkout to a working copy)
        this.policyComponent.bindClassBehaviour(
                CopyServicePolicies.OnCopyNodePolicy.QNAME,
                SyncModel.ASPECT_SYNC_SET_MEMBER_NODE,
                new JavaBehaviour(this, "onCopySsmnNode", NotificationFrequency.EVERY_EVENT));
        
        // SSMN checkout
        this.policyComponent.bindClassBehaviour(
                CheckOutCheckInServicePolicies.BeforeCheckOut.QNAME,
                SyncModel.ASPECT_SYNC_SET_MEMBER_NODE,
                new JavaBehaviour(this, "beforeCheckOutSsmnNode", NotificationFrequency.EVERY_EVENT));
        
        // SSMN revert
        this.policyComponent.bindClassBehaviour(
                VersionServicePolicies.OnRevertVersionPolicy.QNAME,
                SyncModel.ASPECT_SYNC_SET_MEMBER_NODE,
                new JavaBehaviour(this, "getRevertVersionCallback", NotificationFrequency.EVERY_EVENT));
 
    }
    
    @SuppressWarnings("unchecked")
    private void trackMove(NodeRef nodeRef)
    {
        Set<NodeRef> moveNodes = (Set<NodeRef>)AlfrescoTransactionSupport.getResource(KEY_TXN_RES_MOVE_NODES);
        if (moveNodes == null)
        {
            moveNodes = new HashSet<NodeRef>();
            AlfrescoTransactionSupport.bindResource(KEY_TXN_RES_MOVE_NODES, moveNodes);
        }
        moveNodes.add(nodeRef);
    }
    
    @SuppressWarnings("unchecked")
    private boolean isMove(NodeRef nodeRef)
    {
        Set<NodeRef> moveNodes = (Set<NodeRef>)AlfrescoTransactionSupport.getResource(KEY_TXN_RES_MOVE_NODES);
        if (moveNodes != null)
        {
            return moveNodes.contains(nodeRef);
        }
        return false;
    }
    
    @SuppressWarnings("unchecked")
    private void trackCheckOut(NodeRef nodeRef)
    {
        Set<NodeRef> checkOutNodes = (Set<NodeRef>)AlfrescoTransactionSupport.getResource(KEY_TXN_RES_CHECKOUT_NODES);
        if (checkOutNodes == null)
        {
            checkOutNodes = new HashSet<NodeRef>();
            AlfrescoTransactionSupport.bindResource(KEY_TXN_RES_CHECKOUT_NODES, checkOutNodes);
            //AlfrescoTransactionSupport.bindListener(this.checkoutTransactionListener);
        }
        checkOutNodes.add(nodeRef);
    }
    
    @SuppressWarnings("unchecked")
    private void trackCreate(NodeRef parentNodeRef, NodeRef childNodeRef)
    {
        FileInfo fileInfo = fileFolderService.getFileInfo(childNodeRef);
        if (fileInfo != null)
        {
            SyncSetDefinition ssd = syncAdminService.getSyncSetDefinition(parentNodeRef);
            if (ssd != null)
            {
                // Note: do not use fileInfo.isHidden() as it checks if the node is hidden on the 'current' client and Hybrid Sync has no 'client'.
                if (nodeService.hasAspect(childNodeRef, ContentModel.ASPECT_HIDDEN))
                {
                    if (log.isTraceEnabled())
                    {
                        log.trace("trackCreate: ignore - hidden node (we're not including them) : "+childNodeRef);
                    }
                }
                else if (fileInfo.isFolder() && (! ssd.getIncludeSubFolders()))
                {
                    if (log.isTraceEnabled())
                    {
                        log.trace("trackCreate: ignore - sub-folder (we're not including them) : "+childNodeRef);
                    }
                }
                else
                {
                    Map<NodeRef, SyncSetDefinition> pendingCreates = (Map<NodeRef, SyncSetDefinition>)AlfrescoTransactionSupport.getResource(KEY_TXN_RES_PENDING_NODES);
                    if (pendingCreates == null)
                    {
                        pendingCreates = new HashMap<NodeRef, SyncSetDefinition>();
                        AlfrescoTransactionSupport.bindResource(KEY_TXN_RES_PENDING_NODES, pendingCreates);
                        AlfrescoTransactionSupport.bindListener(this.createTransactionListener);
                    }
                    if (log.isTraceEnabled())
                    {
                        log.trace("trackCreate: new node : " +childNodeRef);
                    }
                    pendingCreates.put(childNodeRef, ssd);
                }
            }
            else
            {
                if (log.isTraceEnabled())
                {
                    log.trace("trackCreate: ignore - parent does not belong to SSD (we're not including them) : "+childNodeRef);
                }
            }
        }
        else
        {
            if (log.isTraceEnabled())
            {
                log.trace("trackCreate: ignore - not a file/folder node (we're not including them) : "+childNodeRef);
            }
        }
    }
    
    public void beforeCheckOutSsmnNode(NodeRef nodeRef, NodeRef destinationParentNodeRef, QName destinationAssocTypeQName, QName destinationAssocQName)
    {
        trackCheckOut(nodeRef);
    }
    
    /**
     * When an sync set member node is copied,  don't copy the sync set member node.
     */
    public CopyBehaviourCallback onCopySsmnNode(QName classRef, CopyDetails copyDetails)
    {
        return SyncSetMemberNodeCopyBehaviourCallback.INSTANCE;
    }
    
    /**
     * Extends the default copy behaviour to prevent copying of sync set member node  and properties.
     *
     * @author Mark Rogers
     * @since sync
     */
    private static class SyncSetMemberNodeCopyBehaviourCallback extends DefaultCopyBehaviourCallback
    {
        private static final CopyBehaviourCallback INSTANCE = new SyncSetMemberNodeCopyBehaviourCallback();

        /**
         * @return          Returns an empty map
         */
        @Override
        public Map<QName, Serializable> getCopyProperties(
                QName classQName, CopyDetails copyDetails, Map<QName, Serializable> properties)
        {
            boolean copyProps = false;
            if (classQName.equals(SyncModel.ASPECT_SYNC_SET_MEMBER_NODE))
            {
                @SuppressWarnings("unchecked")
                Set<NodeRef> checkOutNodes = (Set<NodeRef>)AlfrescoTransactionSupport.getResource(KEY_TXN_RES_CHECKOUT_NODES);
                copyProps = ((checkOutNodes != null) && (checkOutNodes.contains(copyDetails.getSourceNodeRef())));
            }
            
            if (copyProps)
            {
                return properties;
            }
            else
            {
                return Collections.emptyMap();
            }
        }

        /**
         * Don't copy the transferred aspect.
         *
         * @return          Returns <tt>true</tt> always
         */
        @Override
        public boolean getMustCopy(QName classQName, CopyDetails copyDetails)
        {
            if (classQName.equals(SyncModel.ASPECT_SYNC_SET_MEMBER_NODE))
            {
                @SuppressWarnings("unchecked")
                Set<NodeRef> checkOutNodes = (Set<NodeRef>)AlfrescoTransactionSupport.getResource(KEY_TXN_RES_CHECKOUT_NODES);
                return ((checkOutNodes != null) && (checkOutNodes.contains(copyDetails.getSourceNodeRef())));
            }
            else
            {
                return true;
            }
        }
    }
    
    public class SyncChangeMonitorCreateTransactionListener extends TransactionListenerAdapter
    {
        /**
         * @see org.alfresco.repo.transaction.TransactionListener#beforeCommit(boolean)
         */
        @SuppressWarnings("unchecked")
        @Override
        public void beforeCommit(boolean readOnly)
        {
            Map<NodeRef, SyncSetDefinition> pendingCreates = (Map<NodeRef, SyncSetDefinition>)AlfrescoTransactionSupport.getResource(KEY_TXN_RES_PENDING_NODES);
            if (pendingCreates != null)
            {
                for (Map.Entry<NodeRef, SyncSetDefinition> child : pendingCreates.entrySet())
                {
                    NodeRef childNodeRef = child.getKey();
                    SyncSetDefinition ssd = child.getValue();
                    
                    // Ignore if the node no longer exists
                    if (! nodeService.exists(childNodeRef))
                    {
                        log.debug("node no longer exists : ignore");
                        continue;
                    }
                    
                    Set<QName> nodeAspects = nodeService.getAspects(childNodeRef);
                    if (nodeAspects.contains(ContentModel.ASPECT_TEMPORARY) ||
                        nodeAspects.contains(ContentModel.ASPECT_WORKING_COPY))
                    {
                        log.debug("node is temporary or working copy : ignore");
                        // Ignore PWC node (private working copy) or ...
                        // ... temporary node - eg. CLOUD-950 - GoogleDoc creation (in synced folder)
                        continue;
                    }
                    
                    if(log.isDebugEnabled())
                    {
                        log.debug("SyncChangeMonitorCreateTransactionListener beforeCommit - add sync set member " + childNodeRef);
                    }
                    
                    // indirect sync
                    syncAdminService.addSyncSetMembers(ssd, childNodeRef, false, ssd.getIncludeSubFolders());                    
                    
                }
            }
        }
    }
    
    @Override public void onContentPropertyUpdate(NodeRef nodeRef, QName propertyQName, ContentData beforeValue, ContentData afterValue)
    {
    	if(log.isTraceEnabled())
    	{
    		log.trace("onContentPropertyUpdate nodeRef: " + nodeRef +", tennantDomain:" + TenantUtil.getCurrentDomain());	
    	}
        // We always sync content - or at least put it in the audit for later consideration.
        syncAuditService.recordContentPropertyUpdate(nodeRef, beforeValue, afterValue);
    }
    
    @Override public void onUpdateProperties(NodeRef nodeRef, Map<QName, Serializable> before, Map<QName, Serializable> after)
    {
    	if(log.isTraceEnabled())
    	{
    		log.trace("onUpdateProperties nodeRef: " + nodeRef +", tennantDomain:" + TenantUtil.getCurrentDomain());	
    	}
        Map<QName, Serializable> filteredBeforeProps = filterIrrelevantProperties(before);
        Map<QName, Serializable> filteredAfterProps =  filterIrrelevantProperties(after);
        
        // After filtering, perhaps none of the relevant properties have changed...
        if ( !filteredBeforeProps.equals(filteredAfterProps))
        {
            if(log.isTraceEnabled())
            {
                log.trace("a tracked property has changed filteredAfterProps:" + filteredAfterProps);
            }
            syncAuditService.recordNonContentPropertiesUpdate(nodeRef, filteredBeforeProps, filteredAfterProps);
        }
    }
    
    private Map<QName, Serializable> filterIrrelevantProperties(Map<QName, Serializable> props)
    {
        Map<QName, Serializable> result = new HashMap<QName, Serializable>();
        
        for (Entry<QName, Serializable> entry : props.entrySet())
        {
            if (dynamicPropertiesToTrack.contains(entry.getKey()))
            {
                result.put(entry.getKey(), entry.getValue());
            }
        }
        
        return result;
    }
    
    private Set<QName> filterIrrelevantAspects(Set<QName> aspects)
    {
        HashSet<QName> result = new HashSet<QName>();
        for(QName aspect: aspects)
        if(dynamicAspectsToTrack.contains(aspect))
        {
            result.add(aspect);
        }
        return result;
    }
    
    public void onCreateSsdNode(ChildAssociationRef childAssocRef)
    {
        // NOOP
    }
    
    public void beforeDeleteSsdNode(NodeRef ssdNodeRef)
    {
    	if(log.isTraceEnabled())
    	{
    		log.trace("beforeDeleteSsdNode: ssdNodeRef," + ssdNodeRef + ", tennantDomain:" + TenantUtil.getCurrentDomain());	
    	}
    	
        if (nodeService.exists(ssdNodeRef))
        {
            List<AssociationRef> assocRefs = nodeService.getTargetAssocs(ssdNodeRef, SyncModel.ASSOC_SYNC_MEMBERS);
            for (AssociationRef assocRef : assocRefs)
            {
                NodeRef ssmnNodeRef = assocRef.getTargetRef();
                nodeService.removeAspect(ssmnNodeRef, SyncModel.ASPECT_SYNC_SET_MEMBER_NODE);
            }
            TransactionalResourceHelper.getSet(KEY_TXN_RES_DELETE_NODES).add(ssdNodeRef);
        }
    }
    
    // onCreate SSMN child node
    public void onCreateChildAssociation(ChildAssociationRef childAssocRef, boolean isNewNode)
    {
    	if(log.isTraceEnabled())
    	{
    		log.trace("onCreateChildAssociation: childAssocRef," + childAssocRef + ", tennantDomain:" + TenantUtil.getCurrentDomain());	
    	}
    
        if (childAssocRef.isPrimary())
        {
            NodeRef childNodeRef = childAssocRef.getChildRef();
            NodeRef parentNodeRef = childAssocRef.getParentRef();
            
            if (nodeService.exists(childNodeRef))
            {
                if (isMove(childNodeRef))
                {
                    if (log.isTraceEnabled())
                    {
                        log.trace("onCreateChildAssociation: ignore - folder rename: "+childNodeRef);
                    }
                    return;
                }
                
                if(log.isDebugEnabled())
                {
                    log.debug("track create childNodeRef: " + childNodeRef);
                }
                
                trackCreate(parentNodeRef, childNodeRef);
            }
        }
    }
    
    // beforeDelete SSMN child node
    public void beforeDeleteChildAssociation(ChildAssociationRef childAssocRef)
    {
    	if(log.isTraceEnabled())
    	{
    		log.trace("beforeDeleteChildAssociation: oldChildAssocRef," + childAssocRef + ", tennantDomain:" + TenantUtil.getCurrentDomain());	
    	}
    	
        if (childAssocRef.isPrimary())
        {
            NodeRef childNodeRef = childAssocRef.getChildRef();
            
            if (isMove(childNodeRef))
            {
                if (log.isTraceEnabled())
                {
                    log.trace("beforeDeleteChildAssociation: ignore - folder rename: "+childNodeRef);
                }
                return;
            }
            
            FileInfo fileInfo = fileFolderService.getFileInfo(childNodeRef);
            if (fileInfo != null)
            {
                SyncSetDefinition ssd = syncAdminService.getSyncSetDefinition(childNodeRef);
                if (ssd != null)
                {
                    syncAdminService.removeSyncSetMember(ssd, childNodeRef);
                }
            }
        }
    }
    
    // beforeMove SSMN child node
    public void beforeMoveNode(ChildAssociationRef oldChildAssocRef, NodeRef newParentRef)
    {
    	if(log.isTraceEnabled())
    	{
    		log.trace("beforeMoveNodee: oldChildAssocRef," + oldChildAssocRef + ", tennantDomain:" + newParentRef + " " + TenantUtil.getCurrentDomain());	
    	}
    
        NodeRef ssmnNodeRef = oldChildAssocRef.getChildRef();
        
        if (syncAdminService.isDirectSyncSetMemberNode(ssmnNodeRef))
        {
            // move of directly synced node (should maintain relationship to remote node)
            return;
        }
        
        if (newParentRef.equals(oldChildAssocRef.getParentRef()))
        {
            // same parent, hence assume this is a (folder) rename => will be handled as an update (of name)
            trackMove(ssmnNodeRef);
            return;
        }
        
        SyncSetDefinition newParentSsd = syncAdminService.getSyncSetDefinition(newParentRef);
        if (newParentSsd == null)
        {
            // move out
            final SyncSetDefinition ssd = syncAdminService.getSyncSetDefinition(ssmnNodeRef);
            if (ssd != null)
            {
                // record delete => push delete to target (note: pull delete to source will be ignored)
                syncAuditService.recordSsmnDeleted(ssd, ssmnNodeRef);
            }
        }
        else
        {
            SyncSetDefinition ssd = syncAdminService.getSyncSetDefinition(ssmnNodeRef);
            if (newParentSsd.equals(ssd))
            {
                // record move (within same SSD) => will be handled as an update (of parent)
                trackMove(ssmnNodeRef);
                syncAuditService.recordSsmnMoved(ssd, ssmnNodeRef);
            }
            else
            {
                // move between different SSDs => is handled as delete + add
                
                // We record the delete explicitly here - this will ensure that the currently synced copy will be deleted on the other end.
                // We do not record the add as that will be recorded when the SSMN association is created elsewhere in this class.
                syncAuditService.recordSsmnDeleted(ssd, ssmnNodeRef);
                
                return;
            }
        }
    }
    
    public void onCreateSsmnAssociation(AssociationRef assocRef)
    {
        final NodeRef newMemberNode = assocRef.getTargetRef();
    	if(log.isTraceEnabled())
    	{
    		log.trace("onCreateSsmnAssociation newMemberNode: " + newMemberNode +", tennantDomain:" + TenantUtil.getCurrentDomain());	
    	}
        syncAuditService.recordSsmnAdded(newMemberNode);
    }
    
    static VersionRevertCallback REVERT_INSTANCE = new VersionRevertCallback() 
    {

		@Override
		public RevertAspectAction getRevertAspectAction(QName aspectName,
				VersionRevertDetails details) {
			return RevertAspectAction.IGNORE;
		}

		@Override
		public RevertAssocAction getRevertAssocAction(QName assocName,
				VersionRevertDetails details) {
			return RevertAssocAction.IGNORE;
		}	
    };
    
    public VersionRevertCallback getRevertVersionCallback(QName classRef, VersionRevertDetails copyDetails)
    {
    	return REVERT_INSTANCE; 
    }
    
    public void onDeleteSsmnAssociation(AssociationRef assocRef)
    {
    	if(log.isTraceEnabled())
    	{
    		log.trace("onDeleteSsmnAssociation assocRef: " + assocRef +", tennantDomain:" + TenantUtil.getCurrentDomain());	
    	}
        // If this is a cascade-deleted association as a result of a node delete we're not interested (apparently)
        final Set<NodeRef> deletedNodes = TransactionalResourceHelper.getSet(KEY_TXN_RES_DELETE_NODES); 
        final NodeRef ssdNodeRef = assocRef.getSourceRef();
        if (deletedNodes.contains(ssdNodeRef))
        {
            return;
        }
        final NodeRef formerMemberNode = assocRef.getTargetRef();
        if (deletedNodes.contains(formerMemberNode))
        {
            return;
        }
        
        final SyncSetDefinition ssd = syncAdminService.getSyncSetDefinition(ssdNodeRef);
        
        if (ssd != null)
        {
            syncAuditService.recordSsmnRemoved(ssd, formerMemberNode);
            
            if (nodeService.exists(ssdNodeRef))
            {
                // Now let's work out if there are any SSMNs left.
                List<AssociationRef> assocRefs = nodeService.getTargetAssocs(ssdNodeRef, SyncModel.ASSOC_SYNC_MEMBERS);
                
                // If this SSMN was the last member ...
                if (assocRefs.isEmpty())
                {
                    // ... and not cloud (see ALF-15734) ...
                    // note: force unsync cannot delete the target SSD itself unless we have the ability to re-create (eg. folder sync is re-started from source)
                    if (syncAdminService.isOnPremise())
                    {
                        // ... then record the SSD node that needs to be deleted.
                        syncAuditService.recordSsdDeleted(ssdNodeRef);
                    }
                }
            }
        }
    }
    
    public void beforeDeleteSsmnNode(NodeRef nodeRef)
    {
    	if(log.isTraceEnabled())
    	{
    		log.trace("beforeDeleteSsmnNode nodeRef: " + nodeRef +", tennantDomain:" + TenantUtil.getCurrentDomain());	
    	}
        final SyncSetDefinition ssd = syncAdminService.getSyncSetDefinition(nodeRef);
        
        if (ssd != null)
        {
            syncAuditService.recordSsmnDeleted(ssd, nodeRef);
        }
        TransactionalResourceHelper.getSet(KEY_TXN_RES_DELETE_NODES).add(nodeRef);
    }
    
    @Override public void beforeRemoveAspect(NodeRef nodeRef, QName aspectTypeQName)
    {
    	if(log.isTraceEnabled())
    	{
    		log.trace("beforeRemoveAspect nodeRef: " + nodeRef +", tennantDomain:" + TenantUtil.getCurrentDomain());	
    	}
        if (dynamicAspectsToTrack.contains(aspectTypeQName) && isNodeRefPartOfSync(nodeRef))
        {
            syncAuditService.recordAspectRemoved(nodeRef, aspectTypeQName);
        }
    }
    
    @Override public void onAddAspect(NodeRef nodeRef, QName aspectTypeQName)
    {
    	if(log.isTraceEnabled())
    	{
    		log.trace("onAddAspect nodeRef: " + nodeRef +", aspectTypeQName, " + aspectTypeQName +", tennantDomain:" + TenantUtil.getCurrentDomain());	
    	}
    	if(dynamicAspectsToTrack.contains(aspectTypeQName) &&  isNodeRefPartOfSync(nodeRef))
        {
            syncAuditService.recordAspectAdded(nodeRef, aspectTypeQName);
        }
    }
    
    // eg. CLOUD-950 - GoogleDoc creation (in a synced folder)
    public void onRemoveTemporaryAspect(NodeRef childNodeRef, QName aspectTypeQName)
    {
    	if(log.isTraceEnabled())
    	{
    		log.trace("onRemoveTemporaryAspect nodeRef: " + childNodeRef +", aspectTypeQName, " + aspectTypeQName +", tennantDomain:" + TenantUtil.getCurrentDomain());	
    	}
    	
        try
        {
            if (aspectTypeQName.equals(ContentModel.ASPECT_TEMPORARY) && (! isNodeRefPartOfSync(childNodeRef)))
            {
                ChildAssociationRef childAssocRef = nodeService.getPrimaryParent(childNodeRef);
                if (childAssocRef != null)
                {
                    NodeRef parentNodeRef = childAssocRef.getParentRef();
                    if (parentNodeRef != null)
                    {
                        // note: this will check that parent is a synced folder
                        trackCreate(parentNodeRef, childNodeRef);
                    }
                }
            }
        }
        catch (InvalidNodeRefException inre)
        {
            // belts-and-braces - ignore if parent/child node does not exist
            log.warn("onRemoveTemporaryAspect: ignore ", inre);
        }
        catch (AccessDeniedException ade)
        {
            // belts-and-braces - ignore if user does not have permission to read parent/child node
            log.warn("onRemoveTemporaryAspect: ignore ", ade);
        }
    }
    
    /**
     * Returns <code>true</code> if the given NodeRef is a {@link SyncModel#TYPE_SYNC_SET_DEFINITION SSD}
     * or an {@link SyncModel#ASPECT_SYNC_SET_MEMBER_NODE SSMN}, else <code>false</code>.
     */
    private boolean isNodeRefPartOfSync(NodeRef nodeRef)
    {
        return syncAdminService.getSyncSetDefinition(nodeRef) != null;
    }
    
    private class DynamicQNameSet
    {
    	private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    	private final WriteLock writeLock = lock.writeLock();
    	private final ReadLock readLock = lock.readLock();
    	private HashSet<QName> qnameSet = new HashSet<QName>();
    	
    	/**
    	 * Thread safe add
    	 * @param qname
    	 * @return
    	 */
    	public boolean add(QName qname)
    	{
    		try 
    		{
    			writeLock.lock();
    			return qnameSet.add(qname);
    		}
    		finally
    		{
    			writeLock.unlock();
    		}
    		
    	}
    	
    	
    	public boolean contains(QName qname)
    	{
    		try 
    		{
    			readLock.lock();
    			return qnameSet.contains(qname);
    		}
    		finally
    		{
    			readLock.unlock();
    		}
    		
    	}
    	
    	/**
    	 * Thread safe remove
    	 * @param qname
    	 */
    	public void remove(QName qname)
    	{
    		try 
    		{
    			writeLock.lock();
    			qnameSet.remove(qname);
    		}
    		finally
    		{
    			writeLock.unlock();
    		}
    		
    	}
    	
    	public Set<QName> getValues()
    	{
    		try 
    		{
    			readLock.lock();
    			return Collections.unmodifiableSet(qnameSet);
    		}
    		finally
    		{
    			readLock.unlock();
    		}
    	}
    };
}
