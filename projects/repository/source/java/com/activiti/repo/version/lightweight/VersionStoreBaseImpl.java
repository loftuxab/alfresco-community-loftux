/**
 * Created on Apr 6, 2005
 */
package com.activiti.repo.version.lightweight;

import java.util.Collection;

import com.activiti.repo.node.NodeService;
import com.activiti.repo.ref.ChildAssocRef;
import com.activiti.repo.ref.NodeRef;
import com.activiti.repo.ref.QName;
import com.activiti.repo.ref.StoreRef;
import com.activiti.repo.search.ResultSet;
import com.activiti.repo.search.Searcher;
import com.activiti.repo.store.StoreService;
import com.activiti.repo.version.VersionLabelPolicy;
import com.activiti.repo.version.VersionService;
import com.activiti.repo.version.VersionServiceException;

/**
 * Helper base class providing common implementation used by the 
 * various Light Weight Version Store service implementations.
 * 
 * @author Roy Wetherall
 */
public abstract class VersionStoreBaseImpl
{
    /**
     * Namespace
     */
    public static final String LW_VERSION_STORE_NAMESPACE = "uri://com.activiti/lightWeightVersionStore";
    
    /**
     * Type names
     */
    public static final String TYPE_VERSION_HISTORY = "versionHistory";
    public static final String TYPE_VERSION = "version";
    
    /**
     * Attribute names
     */
    // The versioned node id, set on a version history node
    public static final QName ATTR_VERSIONED_NODE_ID = QName.createQName(LW_VERSION_STORE_NAMESPACE, "versionedNodeId");
	// The version label, set on a version node
    public static final QName ATTR_VERSION_LABEL = QName.createQName(LW_VERSION_STORE_NAMESPACE, "versionLabel");
    // The version number, set on a verison node
    public static final QName ATTR_VERSION_NUMBER = QName.createQName(LW_VERSION_STORE_NAMESPACE, "versionNumber");
    // The created date, set on a version node
    public static final QName ATTR_VERSION_CREATED_DATE = QName.createQName(LW_VERSION_STORE_NAMESPACE, "createdDate");
    // The origional id of the versioned node
	public static final QName ATTR_FROZEN_NODE_ID = QName.createQName(LW_VERSION_STORE_NAMESPACE, "frozenNodeId");
	// The node type of the versioned node
	public static final QName ATTR_FROZEN_NODE_TYPE = QName.createQName(LW_VERSION_STORE_NAMESPACE, "frozenNodeType");
	// The protocol of the store that the versioned node origionated from
	public static final QName ATTR_FROZEN_NODE_STORE_PROTOCOL = QName.createQName(LW_VERSION_STORE_NAMESPACE, "frozenNodeStoreProtocol");
	// The identifier of the store that the versioned node origionated from
	public static final QName ATTR_FROZEN_NODE_STORE_ID = QName.createQName(LW_VERSION_STORE_NAMESPACE, "frozenNodeStoreId");
    
    /**
     * Association names
     */
    public static final QName ASSOC_ROOT_VERSION = QName.createQName(LW_VERSION_STORE_NAMESPACE, "rootVersion");
    public static final QName ASSOC_SUCCESSOR = QName.createQName(LW_VERSION_STORE_NAMESPACE, "successor");
    
    /**
     * Child relationship names
     */
    public static final QName CHILD_VERSION_HISTORIES = QName.createQName(LW_VERSION_STORE_NAMESPACE, "versionHistory");
    public static final QName CHILD_VERSIONS = QName.createQName(LW_VERSION_STORE_NAMESPACE, "version");
    
    /**
     * The store protocol
     */
    protected static final String STORE_PROTOCOL = VersionService.VERSION_STORE_PROTOCOL;
    
    /**
     * The store id
     */
    protected static final String STORE_ID = "lightWeightVersionStore";
    
    /**
     * Error message(s)
     */
    private static final String ERR_MSG = "Error retrieving version history from light weight version store.";
    
    /**
     * The common node service
     */
    protected NodeService nodeService = null;
    
    /**
     * The db node service, used as the version store implementation
     */
    protected NodeService dbNodeService = null;

    /**
     * The store service
     */
    protected StoreService storeService = null;
    
    /**
     * The repository searcher
     */
    private Searcher searcher = null;
    
    /**
     * The version store root node reference
     */
    protected NodeRef versionStoreRootNodeRef = null;
    
    /**
     * Initialise the version store service, ensuring that a version store exists.
     */
    public void initialise()
    {
        // Ensure that the version store has been created
        if (this.storeService.exists(getVersionStoreReference()) == false)
        {
            this.storeService.createStore(STORE_PROTOCOL, STORE_ID);
        }        
        
        // Get the version store root node reference
        this.versionStoreRootNodeRef = this.storeService.getRootNode(getVersionStoreReference());
    }
    
    /**
     * Sets the general node service
     * 
     * @param nodeService   the node service
     */
    public void setNodeService(NodeService nodeService)
    {
        this.nodeService = nodeService;
    }
    
    /**
     * Sets the db node service, used as the version store implementation
     * 
     * @param nodeService  the node service
     */
    public void setDbNodeService(NodeService nodeService)
    {
        this.dbNodeService = nodeService;
    }

    /**
     * Sets the store service
     * 
     * @param storeService  the store service
     */
    public void setStoreService(StoreService storeService)
    {
        this.storeService = storeService;
    }
    
    /**
     * Sets the searcher
     * 
     * @param searcher  the searcher
     */
    public void setSearcher(Searcher searcher)
    {
        this.searcher = searcher; 
    }
    
    /**
     * Gets the reference to the version store
     * 
     * @return  reference to the version store
     */
    public StoreRef getVersionStoreReference()
    {
        return new StoreRef(STORE_PROTOCOL, STORE_ID);
    }
    
    /**
     * Gets a reference to the version history node for a given 'real' node.
     * 
     * @param nodeRef  a node reference
     * @return         a reference to the version history node, null of none
     */
    protected NodeRef getVersionHistoryNodeRef(NodeRef nodeRef)
    {
        NodeRef result = null;
        
        // TODO for now get the version history node by hand, since objects added in this transaction
        //      can not be found 
        
        //String strQueryString = 
        //    "@\\{" +
        //    LW_VERSION_STORE_NAMESPACE +
        //    "\\}" +
        //    ATTR_VERSIONED_NODE_ID.getLocalName() +
        //    ":" +
        //    nodeRef.getId();
        
        // Execute query to find the verison history object
        //ResultSet results = searcher.query(
        //        getVersionStoreReference(), 
        //        "lucene", 
        //        strQueryString, 
        //        null, 
        //        null);
        
        //if (results.length() == 1)
        //{
            // Get a reference to the version history node
        //    result = results.getNodeRef(0);
        //}
        //else if (results.length() > 1)
        //{
            // Error since there should only be one version history node per node id
        //    throw new VersionServiceException(ERR_MSG);
        //}
        
        Collection<ChildAssocRef> versionHistories = this.dbNodeService.getChildAssocs(this.versionStoreRootNodeRef);
        for (ChildAssocRef versionHistory : versionHistories)
        {
            String nodeId = (String)this.dbNodeService.getProperty(versionHistory.getChildRef(), ATTR_VERSIONED_NODE_ID);
            if (nodeId != null && nodeId.equals(nodeRef.getId()) == true)
            {
                result = versionHistory.getChildRef();
                break;
            }
        }
        
        return result;
    }
    
    /**
     * Gets a reference to the node for the current version of the passed node ref.
     * 
     * This uses the version label as a mechanism for looking up the version node in
     * the version history.
     * 
     * @param nodeRef  a node reference
     * @return         a reference to a version reference
     */
    protected NodeRef getCurrentVersionNodeRef(NodeRef versionHistory, NodeRef nodeRef)
    {
        NodeRef result = null;
        String versionLabel = (String)this.nodeService.getProperty(nodeRef, VersionService.ATTR_CURRENT_VERSION_LABEL);
        
        Collection<ChildAssocRef> versions = this.dbNodeService.getChildAssocs(versionHistory);
        for (ChildAssocRef version : versions)
        {
            String tempLabel = (String)this.dbNodeService.getProperty(version.getChildRef(), ATTR_VERSION_LABEL);
            if (tempLabel != null && tempLabel.equals(versionLabel) == true)
            {
                result = version.getChildRef(); 
                break;
            }
        }
        
        return result;
    }
}
