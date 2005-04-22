/**
 * Created on Apr 6, 2005
 */
package com.activiti.repo.version.lightweight;

import java.util.Collection;

import com.activiti.repo.dictionary.ClassRef;
import com.activiti.repo.dictionary.DictionaryService;
import com.activiti.repo.node.NodeService;
import com.activiti.repo.ref.ChildAssocRef;
import com.activiti.repo.ref.NodeRef;
import com.activiti.repo.ref.StoreRef;
import com.activiti.repo.search.Searcher;
import com.activiti.repo.version.VersionService;
import com.activiti.util.AspectMissingException;

/**
 * Helper base class providing common implementation used by the 
 * various Light Weight Version Store service implementations.
 * 
 * @author Roy Wetherall
 */
public abstract class VersionStoreBaseImpl implements VersionStoreConst
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
        if (this.nodeService.exists(getVersionStoreReference()) == false)
        {
            this.nodeService.createStore(STORE_PROTOCOL, STORE_ID);
        }        
        
        // Get the version store root node reference
        this.versionStoreRootNodeRef = this.nodeService.getRootNode(getVersionStoreReference());
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
        String versionLabel = (String)this.nodeService.getProperty(nodeRef, VersionService.PROP_QNAME_CURRENT_VERSION_LABEL);
        
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
        ClassRef aspectRef = new ClassRef(VersionService.ASPECT_QNAME_VERSION);
        
        if (this.dbNodeService.hasAspect(nodeRef, aspectRef) == false)
        {
            // Raise exception to indicate version aspect is not present
            throw new AspectMissingException(aspectRef, nodeRef);
        }
    }
}
