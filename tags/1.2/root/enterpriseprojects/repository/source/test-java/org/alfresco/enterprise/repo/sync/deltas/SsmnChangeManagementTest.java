/*
 * Copyright 2005-2013 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.enterprise.repo.sync.deltas;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.alfresco.enterprise.repo.sync.SyncAdminService;
import org.alfresco.enterprise.repo.sync.SyncChangeMonitor;
import org.alfresco.enterprise.repo.sync.SyncModel;
import org.alfresco.enterprise.repo.sync.SyncSetDefinition;
import org.alfresco.enterprise.repo.sync.audit.SyncChangeEvent;
import org.alfresco.enterprise.repo.sync.audit.SyncChangeEventImpl;
import org.alfresco.enterprise.repo.sync.audit.SyncEventHandler;
import org.alfresco.enterprise.repo.sync.audit.SyncEventHandler.AuditEventId;
import org.alfresco.enterprise.repo.sync.deltas.AggregatedNodeChange.SsmnChangeType;
import org.alfresco.enterprise.repo.sync.deltas.SsmnChangeManagement.UnsupportedSyncNodeType;
import org.alfresco.enterprise.repo.sync.transport.SyncNodeChangesInfo;
import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.model.ContentModel;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.collections.CollectionUtils;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.extensions.webscripts.GUID;

/**
 * Unit tests for {@link SsmnChangeManagement}.
 * 
 * @author Neil Mc Erlean
 * @since CloudSync
 */
public class SsmnChangeManagementTest
{
    // Miscellaneous useful data for the tests
    private long nextEntryId = 0L;
    private final String user = "userone";
    
    private final static SsmnChangeManagement CHANGE_MGMT = new SsmnChangeManagement();
    
    private static final String SSD_ID = "foo";
    
    @BeforeClass public static void attachMockedServicesToChangeAggregator()
    {
        // Make a mocked ContentService...
        ContentService mockContentService = mock(ContentService.class);
        
        // ... that always returns a (mock) ContentReader
        final ContentReader mockContentReader = mock(ContentReader.class);
        //... that always claims that its backing file exists.
        when(mockContentReader.exists()).thenReturn(true);
        
        when(mockContentService.getReader(any(NodeRef.class), any(QName.class))).thenReturn(mockContentReader);
        
        CHANGE_MGMT.setContentService(mockContentService);
        
        
        // Make a mocked NodeService...
        NodeService mockNodeService = mock(NodeService.class);
        
        // ... that always says a node exists
        when(mockNodeService.exists(any(NodeRef.class))).thenReturn(true);
        // ... that always gives the same dummy node type
        when(mockNodeService.getType(any(NodeRef.class))).thenReturn(ContentModel.TYPE_CONTENT);
        // and the same parent
        when(mockNodeService.getPrimaryParent(eq(new NodeRef("test://ws/nodeRef"))))
            .thenReturn(new ChildAssociationRef(ContentModel.ASSOC_CONTAINS,
                                           new NodeRef("test://dummy/parentRef"),
                                           ContentModel.TYPE_CONTENT,
                                           new NodeRef("test://ws/nodeRef")));

        when(mockNodeService.getPrimaryParent(eq(new NodeRef("test://dummy/nodeRef"))))
            .thenReturn(new ChildAssociationRef(ContentModel.ASSOC_CONTAINS,
                                         new NodeRef("test://dummy/parentRef"),
                                         ContentModel.TYPE_CONTENT,
                                         new NodeRef("test://dummy/nodeRef")));
       
        when(mockNodeService.getPrimaryParent(eq(new NodeRef("test://ws/nodeRef1"))))
            .thenReturn(new ChildAssociationRef(ContentModel.ASSOC_CONTAINS,
                                     new NodeRef("test://dummy/parentRef"),
                                     ContentModel.TYPE_CONTENT,
                                     new NodeRef("test://ws/nodeRef1")));
        
        when(mockNodeService.getPrimaryParent(eq(new NodeRef("test://ws/nodeRef2"))))
            .thenReturn(new ChildAssociationRef(ContentModel.ASSOC_CONTAINS,
                                     new NodeRef("test://dummy/parentRef"),
                                     ContentModel.TYPE_CONTENT,
                                     new NodeRef("test://ws/nodeRef2")));
          
        
        CHANGE_MGMT.setNodeService(mockNodeService);
        
        
        // and a mocked SyncAdminService...
        SyncAdminService mockSyncAdminService = mock(SyncAdminService.class);
        
        // ... that always says a node is an SSMN unless it is a parent node - TODO is there a cleaner way to write this with mockito ?
        when(mockSyncAdminService.isSyncSetMemberNode(new NodeRef("test://dummy/parentRef"))).thenReturn(false);
        when(mockSyncAdminService.isSyncSetMemberNode(new NodeRef("test://dummy/nodeRef"))).thenReturn(true);
        
        // ... and gives a placeholder SSD object
        SyncSetDefinition dummySSD = new SyncSetDefinition(SSD_ID, GUID.generate());
        dummySSD.setTargetFolderNodeRef("test://dummy/targetFolder");
        when(mockSyncAdminService.getSyncSetDefinition(any(String.class))).thenReturn(dummySSD);
        
        CHANGE_MGMT.setSyncAdminService(mockSyncAdminService);
        
        // and a mocked SyncChangeMonitor
        SyncChangeMonitor mockSyncChangeMonitor = mock(SyncChangeMonitor.class);
        
        // ... that is only used to get the 'correct' lists of tracked properties & aspects.
        // We could have used the 'real' syncChangeMonitor object, but that would have required us to initialise
        // a spring context for this class, which I don't want to do.
        when(mockSyncChangeMonitor.getPropertiesToTrack()).thenReturn(Arrays.asList(new QName[] {ContentModel.PROP_NAME, ContentModel.PROP_TITLE, ContentModel.PROP_DESCRIPTION}));
        when(mockSyncChangeMonitor.getAspectsToTrack()).thenReturn(Arrays.asList(new QName[] {ContentModel.ASPECT_TITLED}));
        
        CHANGE_MGMT.setSyncChangeMonitor(mockSyncChangeMonitor);
    }
    
    // First of all, no combinations - just simple conversions.
    @Test public void convertSinglePropertyChange() throws Exception
    {
        final String nodeRef = "test://ws/nodeRef";
        
        final QName nodeType = ContentModel.TYPE_CONTENT;
        final Set<QName> changedProps = CollectionUtils.asSet(QName.class, ContentModel.PROP_TITLE);
        
        SyncChangeEvent propChange = createChangePropAuditEvent(SSD_ID, nodeRef, nodeType, (HashSet<QName>) changedProps);
        
        SyncNodeChangesInfo snci = CHANGE_MGMT.convert(propChange);
        
        assertNull(snci.getAspectsAdded());
        assertNull(snci.getAspectsRemoved());
        assertNull(snci.getContentUpdates());
        assertEquals(changedProps, snci.getPropertyUpdates().keySet());
    }
    
    @Test public void convertSingleContentChange() throws Exception
    {
        final String ssdId = "foo";
        final String nodeRef = "test://ws/nodeRef";
        
        final QName nodeType = ContentModel.TYPE_CONTENT;
        
        SyncChangeEvent contentChange = createChangeContentAuditEvent(ssdId, nodeRef, nodeType, "dummy:content:url");
        
        SyncNodeChangesInfo snci = CHANGE_MGMT.convert(contentChange);
        
        assertNull(snci.getAspectsAdded());
        assertNull(snci.getAspectsRemoved());
        assertEquals(1, snci.getContentUpdates().size());
        assertNull(snci.getPropertyUpdates());
    }
    
    @Test public void convertSingleAspectAddition() throws Exception
    {
        aspectAdditionRemovalTest(true);
    }
    
    @Test public void convertSingleAspectRemoval() throws Exception
    {
        aspectAdditionRemovalTest(false);
    }
    
    private void aspectAdditionRemovalTest(boolean addition)
    {
        final String ssdId = "foo";
        final String nodeRef = "test://ws/nodeRef";
        
        final QName nodeType = ContentModel.TYPE_CONTENT;
        
        SyncChangeEvent aspectAdd = createAspectAddOrRemoveAuditEvent(ssdId, nodeRef, nodeType, ContentModel.ASPECT_TITLED, addition);
        
        SyncNodeChangesInfo snci = CHANGE_MGMT.convert(aspectAdd);
        
        if (addition)
        {
            assertEquals(CollectionUtils.asSet(QName.class, ContentModel.ASPECT_TITLED), snci.getAspectsAdded());
            assertNull(snci.getAspectsRemoved());
        }
        else
        {
            assertNull(snci.getAspectsAdded());
            assertEquals(CollectionUtils.asSet(QName.class, ContentModel.ASPECT_TITLED), snci.getAspectsRemoved());
        }
        assertNull(snci.getContentUpdates());
        assertNull(snci.getPropertyUpdates());
    }
    
    @Test public void convertSingleMemberAddition() throws Exception
    {
        syncSetMembershipAdditionRemovalTest(true);
    }
    
    @Test public void convertSingleMemberRemoval() throws Exception
    {
        syncSetMembershipAdditionRemovalTest(false);
    }
    
    private void syncSetMembershipAdditionRemovalTest(boolean addition)
    {
        final String ssdId = "foo";
        final String nodeRef = "test://ws/nodeRef";
        
        final QName nodeType = ContentModel.TYPE_CONTENT;
        
        SyncChangeEvent event = createMembershipAddOrRemoveAuditEvent(ssdId, nodeRef, nodeType, addition);
        SyncNodeChangesInfo snci = CHANGE_MGMT.convert(event);
        
        assertNull(snci.getAspectsAdded());
        assertNull(snci.getAspectsRemoved());
        if (addition)
        {
            assertEquals(1, snci.getContentUpdates().size());
        }
        else // removal
        {
            assertNull(snci.getContentUpdates());
        }
        assertNull(snci.getPropertyUpdates());
        //TODO More assertions
    }
    
    @Test public void combineMultiplePropertyChanges() throws Exception
    {
        final String ssdId = "foo";
        final String nodeRef = "test://ws/nodeRef";
        
        final QName nodeType = ContentModel.TYPE_CONTENT;
        final Set<QName> changedProps1 = CollectionUtils.asSet(QName.class, ContentModel.PROP_TITLE);
        final Set<QName> changedProps2 = CollectionUtils.asSet(QName.class, ContentModel.PROP_DESCRIPTION);
        final Set<QName> changedProps3 = CollectionUtils.asSet(QName.class, ContentModel.PROP_TITLE, ContentModel.PROP_DESCRIPTION);
        
        SyncChangeEvent propChange1 = createChangePropAuditEvent(ssdId, nodeRef, nodeType, (HashSet<QName>) changedProps1);
        SyncChangeEvent propChange2 = createChangePropAuditEvent(ssdId, nodeRef, nodeType, (HashSet<QName>) changedProps2);
        SyncChangeEvent propChange3 = createChangePropAuditEvent(ssdId, nodeRef, nodeType, (HashSet<QName>) changedProps3);
        
        AggregatedNodeChange changesInfo_1     = CHANGE_MGMT.combine(propChange1);
        AggregatedNodeChange changesInfo_1_2   = CHANGE_MGMT.combine(propChange1, propChange2);
        AggregatedNodeChange changesInfo_1_2_3 = CHANGE_MGMT.combine(propChange1, propChange2, propChange3);
        
        assertEquals(1, changesInfo_1.getSyncNodeChangesInfo().getLocalAuditIds().size());
        assertEquals(2, changesInfo_1_2.getSyncNodeChangesInfo().getLocalAuditIds().size());
        assertEquals(3, changesInfo_1_2_3.getSyncNodeChangesInfo().getLocalAuditIds().size());
        assertEquals(AggregatedNodeChange.SsmnChangeType.UPDATE, changesInfo_1.getChangeType());
        assertEquals(AggregatedNodeChange.SsmnChangeType.UPDATE, changesInfo_1_2.getChangeType());
        assertEquals(AggregatedNodeChange.SsmnChangeType.UPDATE, changesInfo_1_2_3.getChangeType());
               
        for (AggregatedNodeChange combinedDelta : new AggregatedNodeChange[] {changesInfo_1, changesInfo_1_2, changesInfo_1_2_3} )
        {
            assertEquals(combinedDelta.getSyncNodeChangesInfo().getSyncSetGUID(), ssdId);
            assertNull(combinedDelta.getSyncNodeChangesInfo().getAspectsAdded());
            assertNull(combinedDelta.getSyncNodeChangesInfo().getAspectsRemoved());
            assertNull(combinedDelta.getSyncNodeChangesInfo().getContentUpdates());
            assertEquals(new NodeRef(nodeRef), combinedDelta.getSyncNodeChangesInfo().getLocalNodeRef());
            assertEquals(nodeType, combinedDelta.getSyncNodeChangesInfo().getType());
        }
        assertEquals(changedProps1, changesInfo_1.getSyncNodeChangesInfo().getPropertyUpdates().keySet());
        assertEquals(changedProps3, changesInfo_1_2.getSyncNodeChangesInfo().getPropertyUpdates().keySet());
        assertEquals(changedProps3, changesInfo_1_2_3.getSyncNodeChangesInfo().getPropertyUpdates().keySet());
    }
    
    
    @Test public void combineMultipleContentChanges() throws Exception
    {
        final String ssdId = "foo";
        final String nodeRef = "test://ws/nodeRef";
        final String contentUrl = "test:contentUrl";
        
        final QName nodeType = ContentModel.TYPE_CONTENT;
        
        SyncChangeEvent propChange1 = createChangeContentAuditEvent(ssdId, nodeRef, nodeType, contentUrl + "1");
        SyncChangeEvent propChange2 = createChangeContentAuditEvent(ssdId, nodeRef, nodeType, contentUrl + "2");
        SyncChangeEvent propChange3 = createChangeContentAuditEvent(ssdId, nodeRef, nodeType, contentUrl + "3");
        
        AggregatedNodeChange changesInfo_1     = CHANGE_MGMT.combine(propChange1);
        AggregatedNodeChange changesInfo_1_2   = CHANGE_MGMT.combine(propChange1, propChange2);
        AggregatedNodeChange changesInfo_1_2_3 = CHANGE_MGMT.combine(propChange1, propChange2, propChange3);
        
        assertEquals(1, changesInfo_1.getSyncNodeChangesInfo().getLocalAuditIds().size());
        assertEquals(2, changesInfo_1_2.getSyncNodeChangesInfo().getLocalAuditIds().size());
        assertEquals(3, changesInfo_1_2_3.getSyncNodeChangesInfo().getLocalAuditIds().size());
        assertEquals(AggregatedNodeChange.SsmnChangeType.UPDATE, changesInfo_1.getChangeType());
        assertEquals(AggregatedNodeChange.SsmnChangeType.UPDATE, changesInfo_1_2.getChangeType());
        assertEquals(AggregatedNodeChange.SsmnChangeType.UPDATE, changesInfo_1_2_3.getChangeType());
               
        for (AggregatedNodeChange combinedDelta : new AggregatedNodeChange[] {changesInfo_1, changesInfo_1_2, changesInfo_1_2_3} )
        {
            assertEquals(combinedDelta.getSyncNodeChangesInfo().getSyncSetGUID(), ssdId);
            assertNull(combinedDelta.getSyncNodeChangesInfo().getAspectsAdded());
            assertNull(combinedDelta.getSyncNodeChangesInfo().getAspectsRemoved());
            assertEquals(new NodeRef(nodeRef), combinedDelta.getSyncNodeChangesInfo().getLocalNodeRef());
            assertEquals(nodeType, combinedDelta.getSyncNodeChangesInfo().getType());
            assertNull(combinedDelta.getSyncNodeChangesInfo().getPropertyUpdates());
            assertEquals(1, combinedDelta.getSyncNodeChangesInfo().getContentUpdates().size());
        }
    }
    
    @Test (expected=UnsupportedSyncNodeType.class) public void cannotCombineSsdChangesWithThisClass() throws Exception
    {
        final String ssdId = "foo";
        final String nodeRef = "test://ws/nodeRef";
        
        final QName nodeType = SyncModel.TYPE_SYNC_SET_DEFINITION;
        
        SyncChangeEvent change1 = createAuditEvent(AuditEventId.SSD_TO_DELETE, ssdId, nodeRef, nodeType, null);
        SyncChangeEvent change2 = createAuditEvent(AuditEventId.SSD_TO_DELETE, ssdId, nodeRef, nodeType, null);
        
        CHANGE_MGMT.combine(change1, change2);
    }
    
    //TODO Deletions, creations, more.
    
    @Test(expected=AlfrescoRuntimeException.class) public void aggregatingChangesFromDifferentNodesIsntAllowed() throws Exception
    {
        final String ssdId = "foo";
        final String nodeRef1 = "test://ws/nodeRef1";
        final String nodeRef2 = "test://ws/nodeRef2";
        
        final QName nodeType = ContentModel.TYPE_CONTENT;
        final Set<QName> changedProps = CollectionUtils.asSet(QName.class, ContentModel.PROP_TITLE);
        
        SyncChangeEvent propChange1 = createChangePropAuditEvent(ssdId, nodeRef1, nodeType, (HashSet<QName>) changedProps);
        SyncChangeEvent propChange2 = createChangePropAuditEvent(ssdId, nodeRef2, nodeType, (HashSet<QName>) changedProps);
        
        CHANGE_MGMT.combine(propChange1, propChange2);
    }
    
    @Test public void aggregatingChangesFromDifferentSsdsIsntAllowed() throws Exception
    {
        final String ssdId1 = "foo";
        final String ssdId2 = "bar";
        final String nodeRef = "test://ws/nodeRef1";
        
        final QName nodeType = ContentModel.TYPE_CONTENT;
        final Set<QName> changedProps = CollectionUtils.asSet(QName.class, ContentModel.PROP_TITLE);
        
        SyncChangeEvent propChange1 = createChangePropAuditEvent(ssdId1, nodeRef, nodeType, (HashSet<QName>) changedProps);
        SyncChangeEvent propChange2 = createChangePropAuditEvent(ssdId2, nodeRef, nodeType, (HashSet<QName>) changedProps);
        
        AggregatedNodeChange aggregatedChange = CHANGE_MGMT.combine(propChange1, propChange2);
        assertEquals(SsmnChangeType.UPDATE, aggregatedChange.getChangeType());
        assertEquals("There should only have been 1 change.", 1, aggregatedChange.getSyncNodeChangesInfo().getLocalAuditIds().size());
    }
    
    private SyncChangeEvent createAspectAddOrRemoveAuditEvent(String ssdId, String nodeRef, QName nodeType, QName aspectName, boolean added)
    {
        Map<String, Serializable> additionalData = new HashMap<String, Serializable>();
        additionalData.put(SyncEventHandler.PATH_TO_ASPECT_KEY, aspectName);
        AuditEventId eventId = added ? AuditEventId.ASPECT_ADDED : AuditEventId.ASPECT_REMOVED;
        return this.createAuditEvent(eventId, ssdId, nodeRef, nodeType, additionalData);
    }
    
    private SyncChangeEvent createMembershipAddOrRemoveAuditEvent(String ssdId, String nodeRef, QName nodeType, boolean added)
    {
        Map<String, Serializable> additionalData = new HashMap<String, Serializable>();
        AuditEventId eventId = added ? AuditEventId.SSMN_ADDED : AuditEventId.SSMN_REMOVED;
        return this.createAuditEvent(eventId, ssdId, nodeRef, nodeType, additionalData);
    }
    
    private SyncChangeEvent createChangePropAuditEvent(String ssdId, String nodeRef, QName nodeType, HashSet<QName> changedProps)
    {
        Map<String, Serializable> additionalData = new HashMap<String, Serializable>();
        additionalData.put(SyncEventHandler.PATH_TO_PROPS_KEY, changedProps);
        return this.createAuditEvent(AuditEventId.PROPS_CHANGED, ssdId, nodeRef, nodeType, additionalData);
    }
    
    private SyncChangeEvent createChangeContentAuditEvent(String ssdId, String nodeRef, QName nodeType, String contentUrl)
    {
        Map<String, Serializable> additionalData = new HashMap<String, Serializable>();
        additionalData.put(SyncEventHandler.PATH_TO_CONTENT_KEY, contentUrl);
        return this.createAuditEvent(AuditEventId.CONTENT_CHANGED, ssdId, nodeRef, nodeType, additionalData);
    }
    
    private SyncChangeEvent createAuditEvent(AuditEventId auditEvent, String ssdId,
                                             String nodeRef, QName nodeType,
                                             Map<String, Serializable> additionalData)
    {
        Map<String, Serializable> values = new HashMap<String, Serializable>();
        values.put(SyncEventHandler.PATH_TO_EVENT_ID_KEY, auditEvent);
        values.put(SyncEventHandler.PATH_TO_SSDID_KEY, ssdId);
        values.put(SyncEventHandler.PATH_TO_NODEREF_KEY, nodeRef);
        values.put(SyncEventHandler.PATH_TO_NODETYPE_KEY, nodeType);
        if (additionalData != null)
        {
            values.putAll(additionalData);
        }
        
        SyncChangeEventImpl result = new SyncChangeEventImpl(nextEntryId++, user, System.currentTimeMillis(), values);
        
        return result;
    }
}
