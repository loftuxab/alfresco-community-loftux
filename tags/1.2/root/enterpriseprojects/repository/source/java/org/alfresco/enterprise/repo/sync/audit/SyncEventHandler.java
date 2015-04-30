/*
 * Copyright 2005-2012 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.enterprise.repo.sync.audit;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.alfresco.enterprise.repo.sync.SyncAdminService;
import org.alfresco.enterprise.repo.sync.SyncModel;
import org.alfresco.enterprise.repo.sync.SyncSetDefinition;
import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.repo.audit.AuditComponent;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author Neil Mc Erlean, janv
 * @since 4.1
 */
public class SyncEventHandler
{
    public enum AuditEventId
    {
        SSD_TO_DELETE,
        SSMN_ADDED, SSMN_UPDATE_ALL, SSMN_REMOVED,
        ASPECT_ADDED, ASPECT_REMOVED,
        CONTENT_CHANGED,
        PROPS_CHANGED,
        SSMN_DELETED,
        SSMN_MOVED
    }
    
    public static final String AUDIT_APPLICATION_NAME     = "Alfresco Sync Service";
    
    public static final String AUDIT_ROOT_PATH            = "/sync";
    
    public static final String AUDIT_KEY_SRC_REPO_ID      = "srcRepoId"; // note: depends on ssd (=> for source repo always the same / itself)
    public static final String AUDIT_KEY_SSD_ID           = "ssdid";
    public static final String AUDIT_KEY_NODEREF          = "noderef";
    public static final String AUDIT_KEY_NODEREF_OTHER    = "noderefOther";
    public static final String AUDIT_KEY_NODETYPE         = "nodetype";
    public static final String AUDIT_KEY_EVENT_ID         = "eventId";
    public static final String AUDIT_KEY_NODE_PROPERTIES  = "node-props";
    public static final String AUDIT_KEY_CONTENT_URL      = "content-url";
    public static final String AUDIT_KEY_ASPECT_NAME      = "aspect";
    
    // Paths (e.g. for queries)
    public static final String PATH_TO_EVENT_ID_KEY           = AUDIT_ROOT_PATH + "/" + AUDIT_KEY_EVENT_ID + "/value";
    public static final String PATH_TO_REPOID_KEY             = AUDIT_ROOT_PATH + "/" + AUDIT_KEY_SRC_REPO_ID + "/value";
    public static final String PATH_TO_SSDID_KEY              = AUDIT_ROOT_PATH + "/" + AUDIT_KEY_SSD_ID + "/value";
    public static final String PATH_TO_NODEREF_KEY            = AUDIT_ROOT_PATH + "/" + AUDIT_KEY_NODEREF + "/value";
    public static final String PATH_TO_NODEREF_OTHER_KEY      = AUDIT_ROOT_PATH + "/" + AUDIT_KEY_NODEREF_OTHER + "/value";
    public static final String PATH_TO_NODETYPE_KEY           = AUDIT_ROOT_PATH + "/" + AUDIT_KEY_NODETYPE + "/value";
    public static final String PATH_TO_ASPECT_KEY             = AUDIT_ROOT_PATH + "/" + AUDIT_KEY_ASPECT_NAME + "/value";
    public static final String PATH_TO_PROPS_KEY              = AUDIT_ROOT_PATH + "/" + AUDIT_KEY_NODE_PROPERTIES + "/value";
    public static final String PATH_TO_CONTENT_KEY            = AUDIT_ROOT_PATH + "/" + AUDIT_KEY_CONTENT_URL + "/value";
    
    /**
     * These keys are mandatory to create any SyncAudit entry in the DB.
     */
    private static final String[] MANDATORY_KEYS = new String[] {AUDIT_KEY_SRC_REPO_ID,
                                                                 AUDIT_KEY_SSD_ID, 
                                                                 AUDIT_KEY_NODEREF, 
                                                                 AUDIT_KEY_NODETYPE, 
                                                                 AUDIT_KEY_EVENT_ID};

    
    protected AuditComponent auditComponent;
    protected DictionaryService dictionaryService;
    protected NamespaceService namespaceService;
    protected NodeService nodeService;
    protected SyncAdminService syncAdminService;
    
    public void setAuditComponent(AuditComponent component)
    {
        this.auditComponent = component;
    }
    
    public void setDictionaryService(DictionaryService service)
    {
        this.dictionaryService = service;
    }
    
    public void setNamespaceService(NamespaceService service)
    {
        this.namespaceService = service;
    }
    
    public void setNodeService(NodeService service)
    {
        this.nodeService = service;
    }
    
    public void setSyncAdminService(SyncAdminService service)
    {
        this.syncAdminService = service;
    }
    
    public void persistAuditChanges(AuditEventId auditEventId, NodeRef changedNode)
    {
        SyncSetDefinition syncSetDefinition = this.syncAdminService.getSyncSetDefinition(changedNode);
        persistAuditChanges(auditEventId, changedNode, syncSetDefinition, false);
    }
    
    public void persistAuditChanges(AuditEventId auditEventId, NodeRef changedNode, SyncSetDefinition syncSetDefinition, boolean forceAudit)
    {
        if (forceAudit || isNodeRelevantToSync(changedNode))
        {
            Map<String, Serializable> auditProps = getDefaultAuditValues(auditEventId, changedNode, syncSetDefinition);
            this.recordAuditValuesImpl(auditProps);
        }
    }
    
    private Map<String, Serializable> getDefaultAuditValues(AuditEventId auditEventId, NodeRef changedNode)
    {
        SyncSetDefinition syncSetDefinition = this.syncAdminService.getSyncSetDefinition(changedNode);
        return getDefaultAuditValues(auditEventId, changedNode, syncSetDefinition);
    }
    
    private Map<String, Serializable> getDefaultAuditValues(AuditEventId auditEventId, NodeRef changedNode, SyncSetDefinition syncSetDefinition)
    {
        Map<String, Serializable> auditValues = new HashMap<String, Serializable>();
        auditValues.put(AUDIT_KEY_EVENT_ID, auditEventId);
        auditValues.put(AUDIT_KEY_SSD_ID, syncSetDefinition.getId());
        auditValues.put(AUDIT_KEY_NODEREF, changedNode.toString());
        auditValues.put(AUDIT_KEY_NODETYPE, nodeService.getType(changedNode));
        auditValues.put(AUDIT_KEY_SRC_REPO_ID, syncSetDefinition.getSourceRepoId());
        
        String otherNodeRefStr = (String)nodeService.getProperty(changedNode, SyncModel.PROP_OTHER_NODEREF_STRING);
        if (otherNodeRefStr != null)
        {
            auditValues.put(AUDIT_KEY_NODEREF_OTHER, otherNodeRefStr);
        }
        
        return auditValues;
    }
    
    public void persistAuditChangesPropertiesChanged(NodeRef nodeRef, HashSet<QName> changedPropQNames)
    {
        if (isNodeRelevantToSync(nodeRef))
        {
            Map<String, Serializable> auditProps = getDefaultAuditValues(AuditEventId.PROPS_CHANGED, nodeRef);
            auditProps.put(AUDIT_KEY_NODE_PROPERTIES, changedPropQNames);
            this.recordAuditValuesImpl(auditProps);
        }
    }
    
    public void persistAuditChangesContentChanged(NodeRef nodeRef, String contentUrl)
    {
        if (isNodeRelevantToSync(nodeRef))
        {
            Map<String, Serializable> auditProps = getDefaultAuditValues(AuditEventId.CONTENT_CHANGED, nodeRef);
            auditProps.put(AUDIT_KEY_CONTENT_URL, contentUrl);
            this.recordAuditValuesImpl(auditProps);
        }
    }
    
    public void persistAuditChangesAspectAdded(NodeRef nodeRef, QName aspectTypeQName)
    {
        if (isNodeRelevantToSync(nodeRef))
        {
            Map<String, Serializable> auditProps = getDefaultAuditValues(AuditEventId.ASPECT_ADDED, nodeRef);
            auditProps.put(AUDIT_KEY_ASPECT_NAME, aspectTypeQName);
            this.recordAuditValuesImpl(auditProps);
        }
    }
    
    public void persistAuditChangesAspectRemoved(NodeRef nodeRef, QName aspectTypeQName)
    {
        if (isNodeRelevantToSync(nodeRef))
        {
            Map<String, Serializable> auditProps = getDefaultAuditValues(AuditEventId.ASPECT_REMOVED, nodeRef);
            auditProps.put(AUDIT_KEY_ASPECT_NAME, aspectTypeQName);
            this.recordAuditValuesImpl(auditProps);
        }
    }
    
    protected void recordAuditValuesImpl(Map<String, Serializable> auditValues)
    {
        // Belts-&-braces: all sync audit entries must contain certain keys (including ssid, srcRepoId, noderef etc)
        for (String requiredKey : MANDATORY_KEYS)
        {
            if ( !auditValues.containsKey(requiredKey))
            {
                throw new AlfrescoRuntimeException("Cannot audit sync event as it was missing a required parameter: " + requiredKey);
            }
        }
        
        auditComponent.recordAuditValues(AUDIT_ROOT_PATH, auditValues);
    }
    
    protected QName getTypeQNameFromShortFormQNameString(String qnameString)
    {
        QName qname = QName.createQName(qnameString, namespaceService);
        if (dictionaryService.getType(qname) == null)
        {
            throw new AlfrescoRuntimeException("Unrecognised node type: '" + qnameString + "'");
        }
        return qname;
    }
    
    protected QName getAspectQNameFromShortFormQNameString(String qnameString)
    {
        QName qname = QName.createQName(qnameString, namespaceService);
        if (dictionaryService.getAspect(qname) == null)
        {
            throw new AlfrescoRuntimeException("Unrecognised aspect: '" + qnameString + "'");
        }
        return qname;
    }
    
    /**
     * Only nodes which are relevant to Cloud Sync should have their changes audited.
     * This method checks if the specified node is relevant, which means it is an {@link SyncModel#TYPE_SYNC_SET_DEFINITION SSD}
     * or it has an {@link SyncModel#TYPE_SYNC_SET_DEFINITION SSD} peer.
     */
    private boolean isNodeRelevantToSync(NodeRef node)
    {
    	boolean exists = nodeService.exists(node);
    	
    	if(exists)
    	{
        	QName nodeType = nodeService.getType(node);
        	boolean emptyMembers = nodeService.getSourceAssocs(node, SyncModel.ASSOC_SYNC_MEMBERS).isEmpty();
        	
        	if(nodeType.equals(SyncModel.TYPE_SYNC_SET_DEFINITION) || 
                    !emptyMembers)
        	{
            	if(log.isTraceEnabled())
            	{
            	    // EXTRA LOGGING FOR CLOUD-2079
            	    log.trace("isNodeRelevantToSync: node " + node + " is related to Sync & will be audited. exists," + exists + ", nodeType," + nodeType + ", emptyMembers, " + emptyMembers);
            	}
        		return true;
        	}
        	else
        	{
              	 if(log.isDebugEnabled())
            	 {
                    log.debug("isNodeRelevantToSync, node " + node + " is unrelated to Sync & will not be audited. exists," + exists + ", nodeType," + nodeType + ", emptyMembers, " + emptyMembers);
            	 }

        		return false;	
        	}
    	}
    	// Node does not exist
    	log.debug("isNodeRelevantToSync, node " + node + " does not exist so won't be audited");
    	
    	return false;
    	
    }
    
    private static final Log log = LogFactory.getLog(SyncEventHandler.class);
}