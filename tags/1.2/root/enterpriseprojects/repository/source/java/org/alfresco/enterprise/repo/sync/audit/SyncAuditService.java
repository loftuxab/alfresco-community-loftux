/*
 * Copyright 2005-2012 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.enterprise.repo.sync.audit;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.alfresco.enterprise.repo.sync.SyncChangeMonitor;
import org.alfresco.enterprise.repo.sync.SyncModel;
import org.alfresco.enterprise.repo.sync.SyncSetDefinition;
import org.alfresco.enterprise.repo.sync.transport.AuditToken;
import org.alfresco.service.cmr.audit.AuditService;
import org.alfresco.service.cmr.repository.ContentData;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;

/**
 * This service is a sync-specific facade on top of the {@link AuditService}.
 * It is responsible for the persistence of sync-related content changes which are
 * provided by the {@link SyncChangeMonitor}. Changes are persisted in the audit DB tables
 * as they are received by this service.
 * <p/>
 * Separately this class offers a query API so that other software components can examine
 * what changes are still unsynced, act upon them and then remove them.
 * 
 * @author Neil Mc Erlean, janv
 * @since 4.1
 */
public interface SyncAuditService
{
    /**
     * This method stores an audit event which represents an update to content of a sync-relevant node.
     * 
     * @param nodeRef       the nodeRef which has changed (only valid in the local Alfresco audit tables).
     * @param beforeValue   the content URL before the change.
     * @param afterValue    the content URL after the change.
     */
    void recordContentPropertyUpdate(NodeRef nodeRef, ContentData beforeValue, ContentData afterValue);
    
    /**
     * This method stores an audit event which represents an update to sync-relevant, non-content properties of a sync-relevant node.
     * 
     * @param nodeRef       the nodeRef which has changed (only valid in the local Alfresco audit tables).
     * @param before        the node properties before the change.
     * @param after         the node properties after the change.
     */
    void recordNonContentPropertiesUpdate(NodeRef nodeRef, Map<QName, Serializable> before, Map<QName, Serializable> after);
    
    /**
     * This method stores an audit event which represents the deletion of a {@link SyncModel#TYPE_SYNC_SET_DEFINITION SSD} node.
     * 
     * @param nodeRef  the {@link NodeRef} of the about-to-be-deleted node (only valid in the local Alfresco audit tables).
     */
    void recordSsdDeleted(NodeRef nodeRef);
    
    /**
     * This method stores an audit event which represents the addition of a sync-relevant aspect to a sync-relevant node.
     * 
     * @param nodeRef         the nodeRef which has had the aspect added (only valid in the local Alfresco audit tables).
     * @param aspectTypeQName the QName of the aspect.
     */
    void recordAspectAdded(NodeRef nodeRef, QName aspectTypeQName);
    
    /**
     * This method stores an audit event which represents the removal of a sync-relevant aspect from a sync-relevant node.
     * 
     * @param nodeRef         the nodeRef which has had the aspect removed (only valid in the local Alfresco audit tables).
     * @param aspectTypeQName the QName of the aspect.
     */
    void recordAspectRemoved(NodeRef nodeRef, QName aspectTypeQName);
    
    /**
     * This method stores an audit event which represents the addition of a node to a Sync Set.
     * @param newMemberNode the new member node.
     */
    void recordSsmnAdded(NodeRef newMemberNode);
    
    /**
     * This method stores an audit event which represents the (full) update of a node in a Sync Set.
     * @param newMemberNode the new member node.
     */
    void recordSsmnUpdateAll(NodeRef newMemberNode);
    
    /**
     * This method stores an audit event which represents the removal of a node from a Sync Set.
     * @param ssd the SyncSetDefinition from which the node has been removed.
     * @param formerMemberNode the former member node.
     */
    void recordSsmnRemoved(SyncSetDefinition ssd, NodeRef formerMemberNode);
    
    /**
     * This method stores an audit event which represents the removal of a node from a Sync Set.
     * @param ssd the SyncSetDefinition from which the node has been removed.
     * @param formerMemberNode the former member node.
     */
    void recordSsmnDeleted(SyncSetDefinition ssd, NodeRef formerMemberNode);
    
    /**
     * This method stores an audit event which represents the move of a node *within* a Sync Set.
     * @param ssd the SyncSetDefinition in which the node has been moved.
     * @param memberNode the child member node that moved within the Sync Set
     */
    void recordSsmnMoved(SyncSetDefinition ssd, NodeRef memberNode);
    
    /**
     * This method gets a List of {@link SyncChangeEvent} data for the specified {@link NodeRef}.
     * The list will have a maximum size as specified.
     * 
     * @param nodeRef    the nodeRef whose audit events are required.
     * @param maxResults the maximum number of events to return (must be a non-zero, positive number).
     * @return           the events data, oldest first.
     */
    List<SyncChangeEvent> queryByNodeRef(NodeRef nodeRef, int maxResults);
    
    /**
     * This method gets a List of {@link SyncChangeEvent} data for the specified {@link SyncSetDefinition#getId() sync set}.
     * The list will have a maximum size as specified.
     * 
     * @param ssdId      the ID number of the {@link SyncSetDefinition} whose audit events are required.
     * @param maxResults the maximum number of events to return (must be a non-zero, positive number).
     * @return           the events data, oldest first.
     */
    List<SyncChangeEvent> queryBySsdId(String ssdId, int maxResults);
    
    /**
     * This method deletes audit entries with the specified {@link SyncChangeEvent#getAuditId() IDs}.
     * @param auditEntryIds the auditEntryIds to be deleted.
     */
    void deleteAuditEntries(long[] auditEntryIds);
    
    /**
     * This method deletes the audit entries associated with the specified list of
     *  {@link AuditToken} entries.
     */
    void deleteAuditEntries(AuditToken...tokens);
    
    /**
     * This method gets a manifest (ie. list of ssdIds) of sync changes for a given sourceRepoId - 
     * ie. list of {@link SyncSetDefinition SSD ids} that have one or more (typically SSMN) changes.
     * 
     * @param maxResults the maximum number of events to return (must be a non-zero, positive number).
     * 
     * @return a List (in chronological order) of SSD ids (for a given sourceRepoId) that have changes.
     */
    List<String> querySsdManifest(String sourceRepoId, int maxResults);
    
    /**
     * Helper method to get current repository id.
     * 
     * @return repoId
     */
    String getRepoId();
    
    /**
     * This method clears all Sync audit history from the DB. <b>Use with caution</b>.
     * @return the number of audit entries which were cleared.
     */
    int clearAudit();
}
