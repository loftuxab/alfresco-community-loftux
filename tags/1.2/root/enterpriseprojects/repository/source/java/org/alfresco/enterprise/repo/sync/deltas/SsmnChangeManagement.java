/*
 * Copyright 2005-2013 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.enterprise.repo.sync.deltas;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.alfresco.enterprise.repo.sync.SyncAdminService;
import org.alfresco.enterprise.repo.sync.SyncChangeMonitor;
import org.alfresco.enterprise.repo.sync.SyncModel;
import org.alfresco.enterprise.repo.sync.SyncSetDefinition;
import org.alfresco.enterprise.repo.sync.audit.SyncChangeEvent;
import org.alfresco.enterprise.repo.sync.audit.SyncEventHandler.AuditEventId;
import org.alfresco.enterprise.repo.sync.transport.AuditToken;
import org.alfresco.enterprise.repo.sync.transport.CloudSyncContent;
import org.alfresco.enterprise.repo.sync.transport.CloudSyncMemberNodeTransport;
import org.alfresco.enterprise.repo.sync.transport.SyncNodeChangesInfo;
import org.alfresco.enterprise.repo.sync.transport.impl.AuditTokenImpl;
import org.alfresco.enterprise.repo.sync.transport.impl.CloudSyncContentNodeImpl;
import org.alfresco.enterprise.repo.sync.transport.impl.SyncNodeChangesInfoImpl;
import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.model.ContentModel;
import org.alfresco.repo.node.MLPropertyInterceptor;
import org.alfresco.repo.site.SiteModel;
import org.alfresco.repo.version.Version2Model;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.Path;
import org.alfresco.service.cmr.security.AccessStatus;
import org.alfresco.service.cmr.security.PermissionService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.PropertyCheck;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This class is responsible for the conversion and aggregation of audited changes to a single {@link SyncModel#ASPECT_SYNC_SET_MEMBER_NODE SSMN} into
 * the {@link SyncNodeChangesInfo} object that is required for {@link CloudSyncMemberNodeTransport transport} over the wire.
 * 
 * @author Neil Mc Erlean, janv
 * @since 4.1
 */
public class SsmnChangeManagement
{
    private static final Log log = LogFactory.getLog(SsmnChangeManagement.class);
    
    /** This exception is thrown when the outer class is asked to convert/aggregate changes that are not on an {@link SyncModel#ASPECT_SYNC_SET_MEMBER_NODE SSMN}. */
    public static class UnsupportedSyncNodeType extends AlfrescoRuntimeException
    {
        private static final long serialVersionUID   = 1L;
        public UnsupportedSyncNodeType(String msgId) { super(msgId); }
    }
    
    private ContentService    contentService;
    private NodeService       nodeService;
    private PermissionService permissionService;
    private SyncAdminService  syncAdminService;
    private SyncChangeMonitor syncChangeMonitor;
   
    
    public void setContentService(ContentService service)                 { this.contentService = service; }
    public void setNodeService(NodeService service)                       { this.nodeService = service; }
    public void setPermissionService(PermissionService permissionService) { this.permissionService = permissionService; }
    public void setSyncAdminService(SyncAdminService service)             { this.syncAdminService = service; }
    public void setSyncChangeMonitor(SyncChangeMonitor monitor)           { this.syncChangeMonitor = monitor; }
    
    public void init()
    {
        PropertyCheck.mandatory(this, "nodeService",  nodeService );
        PropertyCheck.mandatory(this, "contentService",  contentService );
        PropertyCheck.mandatory(this, "permissionService",  permissionService );
        PropertyCheck.mandatory(this, "syncAdminService",  syncAdminService );
        PropertyCheck.mandatory(this, "syncChangeMonitorService",  syncChangeMonitor );
    }
    
    public SyncNodeChangesInfoImpl convert(SyncChangeEvent ssmnEvent)
    {
        final QName nodeType = ssmnEvent.getNodeType();
        if (SyncModel.TYPE_SYNC_SET_DEFINITION.equals(nodeType))
        {
            throw new UnsupportedSyncNodeType("Illegal nodeType: " + nodeType);
        }
        
        final NodeRef localNodeRef = ssmnEvent.getNodeRef();
                
        final NodeRef remoteNodeRef = (ssmnEvent.getOtherNodeRef() != null ? ssmnEvent.getOtherNodeRef() : getRemoteNodeRef(localNodeRef));
        final String syncSetGUID = ssmnEvent.getSsdId();
        
        if (log.isTraceEnabled()) 
        { 
            log.trace("converting SyncChangeEvent: " + ssmnEvent );
            log.trace("    for localNodeRef " + localNodeRef); 
            log.trace("    in sync set with id: " + syncSetGUID); 
        }
        
        SyncNodeChangesInfoImpl result = new SyncNodeChangesInfoImpl(localNodeRef, remoteNodeRef, syncSetGUID, nodeType);
        
        final SyncSetDefinition ssd = syncAdminService.getSyncSetDefinition(syncSetGUID);
        
        result.getLocalAuditIds().add(ssmnEvent.getAuditId());
         
        AuditToken auditToken = new AuditTokenImpl();
        auditToken.record(ssmnEvent, AggregatedNodeChange.getChangeType(ssmnEvent.getEventId()));
        result.setAuditToken(auditToken);
        
        // We need to always get raw MLText properties, and never the locale-specific string
        // Inform the node service that we know what we're doing, and want the MLText version
        final boolean oldMLSetting = MLPropertyInterceptor.isMLAware();
        try
        {
            MLPropertyInterceptor.setMLAware(true);
            
            // Have the node processed
            if (nodeService.exists(localNodeRef))
            {
                Boolean directSync = (Boolean)nodeService.getProperty(localNodeRef, SyncModel.PROP_DIRECT_SYNC);
                if (directSync == null) { directSync = false; }
                
                final Date localCmModified       = (Date)nodeService.getProperty(localNodeRef, ContentModel.PROP_MODIFIED);
                final NodeRef localParentNodeRef = nodeService.getPrimaryParent(localNodeRef).getParentRef();
                
                // if this a folder sync and we have an indirect sync node then we should also be able to retrieve the other (remote) parent ref
                // in the case of a directly synced node (either file sync or top-level folder sync) this will not be available
                // note: we cannot use the SSD original target folder since it is the wrong end (and in any case, may no longer be relevant - eg. moves of file syncs)
                String remoteParentNodeRef = null;
                NodeRef localParentRef = nodeService.getPrimaryParent(localNodeRef).getParentRef();
                if (syncAdminService.isSyncSetMemberNode(localParentRef, ssd))
                {
                    // indirect sync - note: remoteParentNodeRef can be null - eg. in case of name clash on parent folder (eg. ALF-15287)
                    remoteParentNodeRef = (String)nodeService.getProperty(localParentRef, SyncModel.PROP_OTHER_NODEREF_STRING);
                }
                
                String localVersionLabel = (String) nodeService.getProperty(localNodeRef, Version2Model.PROP_QNAME_VERSION_LABEL); // FIXME Is this correct?
                
                result.setLocalModifiedAt(localCmModified);
                result.setLocalParentNodeRef(localParentNodeRef);
                result.setLocalVersionLabel(localVersionLabel);
                
                result.setDirectSync(directSync);
                
                switch (ssmnEvent.getEventId())
                {
                // The SSD_CREATED & SSD_DELETED event IDs have been excluded by this stage.
                
                case SSMN_ADDED :
                    if (log.isTraceEnabled()) { log.trace("    case: " + AuditEventId.SSMN_ADDED); }
                    if (remoteParentNodeRef == null)
                    {
                        if (directSync)
                        {
                            // direct sync - set initial destination to be parent of file / folder sync
                            remoteParentNodeRef = ssd.getTargetFolderNodeRef();
                        }
                        else
                        {
                            // indirect sync - note: remoteParentNodeRef can be null - eg. in case of name clash on parent folder (eg. ALF-15287)
                            if (log.isDebugEnabled())
                            {
                                log.debug("convert: Indirect sync but cannot find otherNodeRef (remote) for localParentRef: " + localParentRef);
                            }
                        }
                    }
                    
                    // drop through
                    
                case SSMN_UPDATE_ALL:
                    if (log.isTraceEnabled()) { log.trace("    case: " + AuditEventId.SSMN_UPDATE_ALL); }
                    // We need to send all the relevant state across the wire. All relevant aspects, properties etc. 
                    // The SyncChangeMonitor knows what is relevant for sync.
                    List<QName> trackedProperties = syncChangeMonitor.getPropertiesToTrack();
                    List<QName> trackedAspects = syncChangeMonitor.getAspectsToTrack();
                    
                    Set<QName> relevantNodeAspects = new HashSet<QName>();
                    for (QName aspect : trackedAspects)
                    {
                        // We can assume that the node exists, because if it's been deleted we do not sync it.
                        // TODO Handle missing node correctly.
                        if (nodeService.hasAspect(localNodeRef, aspect))
                        {
                            if (log.isTraceEnabled())
                            {
                                log.trace("record node ref :" + localNodeRef + ", aspect :" + aspect);
                            }
                            relevantNodeAspects.add(aspect);
                        }
                    }
                    result.setAspectsAdded(relevantNodeAspects.isEmpty() ? null : relevantNodeAspects);
                    
                    // MER Extra Debug
                    Map<QName, Serializable> props = nodeService.getProperties(localNodeRef);
                    if(log.isTraceEnabled())
                    {
                        log.trace(localNodeRef + ", properties :" + props);
                        log.trace("trackedProperties : " + trackedProperties);
                    }
                    // MER Extra Debug
                    
                    Map<QName, Serializable> relevantNodeProperties = new HashMap<QName, Serializable>();
                    for (QName property : trackedProperties)
                    {
                        final Serializable propertyValue = nodeService.getProperty(localNodeRef, property);
                        if (propertyValue != null)
                        {
                            if (log.isTraceEnabled())
                            {
                                log.trace("record node ref :" + localNodeRef + " property : " + property + " value:" + propertyValue);                                
                            }
                            relevantNodeProperties.put(property, propertyValue);
                        }
                    }
                    result.setPropertyUpdates(relevantNodeProperties.isEmpty() ? null : relevantNodeProperties);
                    
                    // And include the node's content, if any.
                	CloudSyncContent content = new CloudSyncContentNodeImpl(ContentModel.PROP_CONTENT, localNodeRef, contentService);
                	if(content.exists())
                	{
                        if (log.isTraceEnabled())
                        {
                            log.trace("record content ");                                
                        }
                    	Map<QName, CloudSyncContent> contentUpdates = new HashMap<QName, CloudSyncContent>();
                    	contentUpdates.put(ContentModel.PROP_CONTENT, content);
                    	result.setContentUpdates(contentUpdates);
                    }
            
                    break;
                
                case SSMN_DELETED :
                    if (log.isTraceEnabled()) { log.trace("    case: " + AuditEventId.SSMN_DELETED); }
                case SSMN_REMOVED :
                    if (log.isTraceEnabled()) { log.trace("    case: " + AuditEventId.SSMN_REMOVED); }
                case SSMN_MOVED :
                    if (log.isTraceEnabled()) { log.trace("    case: " + AuditEventId.SSMN_MOVED); }
                    // No need to set any extra state on the SyncNodeChangesInfo object
                    break;
                case ASPECT_ADDED :
                    if (log.isTraceEnabled()) { log.trace("    case: " + AuditEventId.ASPECT_ADDED); }
                    QName aspectAdded = (QName) ssmnEvent.getAspect();
                    Set<QName> aspectsAdded = result.getAspectsAdded();
                    if (aspectsAdded == null)
                    {
                        aspectsAdded = new HashSet<QName>();
                    }
                    
                    Set<QName> aspects = new HashSet<QName>(aspectsAdded);
                    aspects.add(aspectAdded);
                    
                    result.setAspectsAdded(aspects.isEmpty() ? null : aspects);
                    break;
                    
                case ASPECT_REMOVED :
                    if (log.isTraceEnabled()) { log.trace("    case: " + AuditEventId.ASPECT_REMOVED); }
                    QName aspectRemoved = (QName) ssmnEvent.getAspect();
                    Set<QName> aspectsRemoved = result.getAspectsRemoved();
                    if (aspectsRemoved == null)
                    {
                        aspectsRemoved = new HashSet<QName>();
                    }
                    
                    Set<QName> aspectsR = new HashSet<QName>(aspectsRemoved);
                    aspectsR.add(aspectRemoved);
                    
                    result.setAspectsRemoved(aspectsR.isEmpty() ? null : aspectsR);
                    break;
                    
                case CONTENT_CHANGED :
                    if (log.isTraceEnabled()) { log.trace("    case: " + AuditEventId.CONTENT_CHANGED); }
                	CloudSyncContent changedContent = new CloudSyncContentNodeImpl(ContentModel.PROP_CONTENT, localNodeRef, contentService);
                	
                	if(changedContent.exists())
                	{
                    	  Map<QName, CloudSyncContent> contentUpdates = new HashMap<QName, CloudSyncContent>();
                    	  contentUpdates.put(ContentModel.PROP_CONTENT, changedContent);
                    	  result.setContentUpdates(contentUpdates);
                    }
                    break;
                    
                case PROPS_CHANGED :
                    if (log.isTraceEnabled()) { log.trace("    case: " + AuditEventId.PROPS_CHANGED); }
                    Set<QName> changedPropNames = ssmnEvent.getPropertyNames();
                    
                    Map<QName, Serializable> currentProps = new HashMap<QName, Serializable>();
                    for (QName propName : changedPropNames)
                    {
                        currentProps.put(propName, nodeService.getProperty(localNodeRef, propName));
                    }
                    result.setPropertyUpdates(currentProps.isEmpty() ? null : currentProps);
                    break;
                    
                default:
                    if (log.isTraceEnabled()) { log.trace("    case: default"); }
                    throw new AlfrescoRuntimeException("Unrecognised SyncChangeEvent: " + ssmnEvent.getEventId());
                }
                
                if (remoteParentNodeRef != null)
                {
                    result.setRemoteParentNodeRef(new NodeRef(remoteParentNodeRef));
                }
                
            }
            else
            {
                if ((! ssmnEvent.getEventId().equals(AuditEventId.SSMN_REMOVED)) && (! ssmnEvent.getEventId().equals(AuditEventId.SSMN_DELETED)))
                {
                    if (log.isWarnEnabled())
                    {
                        log.warn("convert: localNodeRef does not exist "+localNodeRef + " for non remove/delete event ('"+ssmnEvent.getEventId()+")");
                    }
                }
            }
        }
        finally
        {
            // Restore the MLText setting to whatever it was before
            MLPropertyInterceptor.setMLAware(oldMLSetting);
        }
        
        // All done
        return result;
    }
    
    private NodeRef getRemoteNodeRef(NodeRef localSyncMemberNode)
    {
        NodeRef result = null;
        if (nodeService.exists(localSyncMemberNode) && syncAdminService.isSyncSetMemberNode(localSyncMemberNode))
        {
            String otherNodeRefString = (String) nodeService.getProperty(localSyncMemberNode, SyncModel.PROP_OTHER_NODEREF_STRING);
            
            if (otherNodeRefString != null)
            {
                result = new NodeRef(otherNodeRefString);
            }
        }
        return result;
    }
    
    /**
     * @see #combine(List)
     */
    public AggregatedNodeChange combine(SyncChangeEvent... deltas)
    {
        if (deltas == null || deltas.length == 0)
        {
            deltas = new SyncChangeEvent[0];
        }
        return combine(Arrays.asList(deltas));
    }
    
    /**
     * This method attempts to combine some of the {@link SyncChangeEvent change event} objects together
     * into a single delta for transport across the wire. Note that not all deltas can be combined and this method
     * will only combine as many as is possible, starting with the first element of the list and ending with the last combinable
     * delta. <p/>
     * For example it is possible to combine multiple property changes and/or or content changes into a single delta object.
     * But it is not possible to combine an unsync event with a subsequent sync event (as they would refer to different
     * {@link SyncModel#TYPE_SYNC_SET_DEFINITION SSDs}.
     * 
     * @param deltas
     * @return an aggregation of as many of the deltas as could be combined starting with the first.
     * @see AggregatedNodeChange#getAuditIds().
     */
    public AggregatedNodeChange combine(List<SyncChangeEvent> deltas)
    {
        AggregatedNodeChange result = null;
        
        // Fetch the details on the changes, and build up something to send
        if (deltas != null && !deltas.isEmpty())
        {
            Iterator<SyncChangeEvent> iter = deltas.iterator();
            
            // We know there is at least one SyncChangeEvent
            SyncChangeEvent firstChangeEvent = iter.next();
            final NodeRef firstNodeRef = firstChangeEvent.getNodeRef();
            final String firstSsdId = firstChangeEvent.getSsdId();
            
            SyncNodeChangesInfo firstSnci = convert(firstChangeEvent);
            
            result = new AggregatedNodeChange((SyncNodeChangesInfoImpl) firstSnci, firstChangeEvent.getEventId());
            
            // Now iterate through the rest, if there are any more
            while (iter.hasNext())
            {
                SyncChangeEvent nextChange = iter.next();
                
                final NodeRef nextNodeRef = nextChange.getNodeRef();
                final String nextSsdId = nextChange.getSsdId();
                
                // We cannot aggregate:
                // 1. change events on different NodeRefs (a programming error)
                // 2. change events on different SSDs (possible if one NodeRef is unsynced and synced again)
                if ( !nextNodeRef.equals(firstNodeRef))
                {
                    throw new AlfrescoRuntimeException("Cannot aggregate changes across different NodeRefs");
                }
                else if ( !nextSsdId.equals(firstSsdId))
                {
                    // This is not an error - it just means we stop aggregating at this point.
                    break;
                }
                
                
                SyncNodeChangesInfoImpl nextSnci = convert(nextChange);
                
                if (result.canAppend(nextSnci, nextChange.getEventId()))
                {
                    boolean appended = result.append(convert(nextChange), nextChange);
                    if ( !appended) { break; }
                }
                else
                {
                    break;
                }
            }
            
            // Set stuff that is not aggregated from the event list 
            // TODO this code is possibly in wrong class or method
            SyncNodeChangesInfoImpl info =  (SyncNodeChangesInfoImpl)result.getSyncNodeChangesInfo();
            if(info != null)
            {
                NodeRef localNodeRef = info.getLocalNodeRef();
                
                if(localNodeRef != null && nodeService.exists(localNodeRef))
                {
                    String path = generatePath(localNodeRef);
                    if(path != null)
                    {
                        info.setLocalPath(path);
                    }
                }
            }
        }
                
        return result;
    }
    
    /**
     * Generate the "path" for sync, suitable for adding to the sync audit messages
     * 
     * <pre>
     * If the node is a member of a document library for a Site generates
     *      {Site Display Name} / {folders from doclib} / {file name}
     * else
     *      the display path of the node
     * </pre>
     * @param nodeRef node for which to generate the path
     * 
     * @return the path or null if the specified node does not exist
     */
    public String generatePath(NodeRef localNodeRef)
    {
        StringBuffer pathBuffer =  new StringBuffer();
        
        if(localNodeRef != null && nodeService.exists(localNodeRef))
        {
            NodeRef currentNode = localNodeRef;
                                   
            // walk the tree upwards
            ChildAssociationRef  assoc = nodeService.getPrimaryParent(currentNode);
            while(assoc != null)
            {
                if(currentNode != null && nodeService.exists(currentNode))       
                {
                    if(permissionService != null)
                    {
                        AccessStatus status = permissionService.hasPermission(currentNode, PermissionService.READ);
                        if(status == null || status == AccessStatus.DENIED)
                        {
                            return pathBuffer.toString();
                        }
                    }
                    
                    if(nodeService.hasAspect(currentNode, SiteModel.ASPECT_SITE_CONTAINER))
                    {
                        // don't do anything with a site container
                        //TODO do we need to check this is a doclib?
                    }
                    else
                    {    
                        if(nodeService.getType(currentNode).equals(SiteModel.TYPE_SITE))
                        {
                            // Got the site
                            String siteTitle = (String)nodeService.getProperty(currentNode, ContentModel.PROP_TITLE);
                            String sitePath = siteTitle + "/" + pathBuffer.toString();
                            if(log.isDebugEnabled())
                            {
                                log.debug("returning path in site: "+ sitePath);
                            }
                            return sitePath;
                        }
                    
                        if(pathBuffer.length() > 0)
                        {
                            pathBuffer.insert(0, "/");
                        }
                        pathBuffer.insert(0, nodeService.getProperty(currentNode, ContentModel.PROP_NAME));              
                    }
                    
                    // now move on to the next assoc
                    assoc = nodeService.getPrimaryParent(currentNode);
                    if(assoc != null)
                    {
                        currentNode = assoc.getParentRef();
                    }
                }
                else
                {
                    // can't read node or node does not exist
                    assoc = null;
                    currentNode = null;
                }
            }
            
            
            // node is not in a site
            Path path = nodeService.getPath(localNodeRef);
            if(path != null)
            {
               String nonSitePath = path.toDisplayPath(nodeService, permissionService);
            
               if(log.isDebugEnabled())
               {
                   log.debug("returning path not in site: " + nonSitePath);
               }
               
               return nonSitePath;
            }
        }

        return null;
    }

}
