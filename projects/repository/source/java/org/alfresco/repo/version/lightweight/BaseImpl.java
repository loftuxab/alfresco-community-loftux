/**
 * Created on Apr 6, 2005
 */
package org.alfresco.repo.version.lightweight;

import java.util.List;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.alfresco.repo.dictionary.ClassRef;
import org.alfresco.repo.dictionary.DictionaryService;
import org.alfresco.repo.dictionary.bootstrap.DictionaryBootstrap;
import org.alfresco.repo.node.NodeService;
import org.alfresco.repo.ref.ChildAssocRef;
import org.alfresco.repo.ref.NodeAssocRef;
import org.alfresco.repo.ref.NodeRef;
import org.alfresco.repo.ref.QName;
import org.alfresco.repo.ref.StoreRef;
import org.alfresco.repo.search.Searcher;
import org.alfresco.repo.version.Version;
import org.alfresco.repo.version.VersionHistory;
import org.alfresco.repo.version.VersionService;
import org.alfresco.repo.version.VersionServiceException;
import org.alfresco.repo.version.common.VersionHistoryImpl;
import org.alfresco.repo.version.common.VersionImpl;
import org.alfresco.util.AspectMissingException;

/**
 * Helper base class providing common implementation used by the 
 * various Light Weight Version Store service implementations.
 * 
 * @author Roy Wetherall
 */
public abstract class BaseImpl implements Const
{
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
     * The repository searcher
     */
    private Searcher searcher = null;
    
    /**
     * The dictionary service
     */
    protected DictionaryService dicitionaryService = null;
    
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
        if (this.dbNodeService.exists(getVersionStoreReference()) == false)
        {
            this.dbNodeService.createStore(STORE_PROTOCOL, STORE_ID);
        }        
        
        // Get the version store root node reference
        this.versionStoreRootNodeRef = this.dbNodeService.getRootNode(getVersionStoreReference());
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
     * Sets the searcher
     * 
     * @param searcher  the searcher
     */
    public void setSearcher(Searcher searcher)
    {
        this.searcher = searcher; 
    }
    
    /**
     * Sets the dictionary service
     * 
     * @param dictionaryService  the dictionary service
     */
    public void setDictionaryService(DictionaryService dictionaryService)
    {
        this.dicitionaryService = dictionaryService;
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
     * Builds a version history object from the version history reference.
     * <p>
     * The node ref is passed to enable the version history to be scoped to the
     * appropriate branch in the version history.
     * 
     * @param versionHistoryRef  the node ref for the version history
     * @param nodeRef            the node reference
     * @return                   a constructed version history object
     */
    protected VersionHistory buildVersionHistory(NodeRef versionHistoryRef, NodeRef nodeRef)
    {
        VersionHistory versionHistory = null;
        
        ArrayList<NodeRef> versionHistoryNodeRefs = new ArrayList<NodeRef>();
        NodeRef currentVersion = getCurrentVersionNodeRef(versionHistoryRef, nodeRef);
        
        while (currentVersion != null)
        {
            NodeAssocRef preceedingVersion = null;
            
            versionHistoryNodeRefs.add(0, currentVersion);
            
            List<NodeAssocRef> preceedingVersions = this.dbNodeService.getSourceAssocs(currentVersion, ASSOC_SUCCESSOR);
            if (preceedingVersions.size() == 1)
            {
                preceedingVersion = (NodeAssocRef)preceedingVersions.toArray()[0];
                currentVersion = preceedingVersion.getSourceRef();                
            }
            else if (preceedingVersions.size() > 1)
            {
                // Error since we only currently support one preceeding version
                throw new VersionServiceException("The light weight version store only supports one preceeding version.");
            }     
            else
            {
                currentVersion = null;
            }
        }
        
        // Build the version history object
        boolean isRoot = true;
        Version preceeding = null;
        for (NodeRef versionRef : versionHistoryNodeRefs)
        {
            Version version = getVersion(versionRef);
            
            if (isRoot == true)
            {
                versionHistory = new VersionHistoryImpl(version);
                isRoot = false;
            }
            else
            {
                ((VersionHistoryImpl)versionHistory).addVersion(version, preceeding);
            }
            preceeding = version;
        }
        
        return versionHistory;
    }
    
    /**
     * Constructs the a version object to contain the version information from the version node ref.
     * 
     * @param versionRef  the version reference
     * @return            object containing verison data
     */
    protected Version getVersion(NodeRef versionRef)
    {
        // TODO could definatly do with a cache since these are read only objects ...
        
        Map<String, Serializable> versionProperties = new HashMap<String, Serializable>();
        
        // Get the node properties
        Map<QName, Serializable> nodeProperties = this.dbNodeService.getProperties(versionRef);
        for (QName key : nodeProperties.keySet())
        {
            if (key.getNamespaceURI().equals(VersionServiceImpl.NAMESPACE_URI) == true)
            {                   
                Serializable value = nodeProperties.get(key);
                versionProperties.put(key.getLocalName(), value);
            }
        }
        
        // Create and return the version object
        return new VersionImpl(versionProperties, versionRef);        
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
        
        // TODO use the sercher to retrieve the version history node
        
        Collection<ChildAssocRef> versionHistories = this.dbNodeService.getChildAssocs(this.versionStoreRootNodeRef);
        for (ChildAssocRef versionHistory : versionHistories)
        {
            String nodeId = (String)this.dbNodeService.getProperty(versionHistory.getChildRef(), PROP_QNAME_VERSIONED_NODE_ID);
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
        // TODO use the searcher to retrieve the version node
        
        NodeRef result = null;
        String versionLabel = (String)this.nodeService.getProperty(nodeRef, DictionaryBootstrap.PROP_QNAME_CURRENT_VERSION_LABEL);
        
        Collection<ChildAssocRef> versions = this.dbNodeService.getChildAssocs(versionHistory);
        for (ChildAssocRef version : versions)
        {
            String tempLabel = (String)this.dbNodeService.getProperty(version.getChildRef(), PROP_QNAME_VERSION_LABEL);
            if (tempLabel != null && tempLabel.equals(versionLabel) == true)
            {
                result = version.getChildRef(); 
                break;
            }
        }
        
        return result;
    }
    
    /**
     * Checks the given node for the version aspect.  Throws an exception if it is not present.
     * 
     * @param nodeRef   the node reference
     * @throws AspectMissingException
     *                  the version aspect is not present on the node
     */
    protected void checkForVersionAspect(NodeRef nodeRef)
       throws AspectMissingException
    {
        ClassRef aspectRef = new ClassRef(DictionaryBootstrap.ASPECT_QNAME_VERSION);
        
        if (this.dbNodeService.hasAspect(nodeRef, aspectRef) == false)
        {
            // Raise exception to indicate version aspect is not present
            throw new AspectMissingException(aspectRef, nodeRef);
        }
    }
}
