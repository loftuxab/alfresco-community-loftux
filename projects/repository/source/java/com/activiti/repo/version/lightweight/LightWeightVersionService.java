package com.activiti.repo.version.lightweight;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.activiti.repo.domain.Node;
import com.activiti.repo.node.NodeService;
import com.activiti.repo.ref.NodeRef;
import com.activiti.repo.ref.StoreRef;
import com.activiti.repo.store.StoreService;
import com.activiti.repo.version.Version;
import com.activiti.repo.version.VersionHistory;
import com.activiti.repo.version.VersionService;
import com.activiti.repo.version.common.VersionImpl;
import com.activiti.repo.version.common.counter.VersionCounterDaoService;

/**
 * The light weight version service implementation.
 * 
 * Uses a workspace store to contain the version histories and the related versions.
 * 
 * @author Roy Wetheral
 */
public class LightWeightVersionService implements VersionService
{
    /**
     * The store protocol
     */
    private static final String STORE_PROTOCOL = StoreRef.PROTOCOL_WORKSPACE;
    
    /**
     * The store id
     */
    private static final String STORE_ID = "lightWeightVersionStore";
    
    /**
     * Attribute names
     * TODO need to be namespaced
     */
    private static final String ATTR_VERSION_LABEL = "{version}label";
    private static final String ATTR_VERSION_NUMBER = "{version}number";
    private static final String ATTR_VERSION_CREATED_DATE = "{version}createddate";
    
    /**
     * Association names
     * TODO need to namespaced
     */
    private static final String ASSOC_ROOT_VERSION = "rootVersion";
    private static final String ASSOC_SUCCESSOR = "successor";
    
    /**
     * Child relationship names
     */
    private static final String CHILD_VERSION_HISTORIES = "versionHistories";
    private static final String CHILD_VERSIONS = "versions";
    
    /**
     * The node service
     */
    private NodeService nodeService = null;

    /**
     * The store service
     */
    private StoreService storeService = null;    
    
    /**
     * The version store root node reference
     */
    private NodeRef versionStoreRootNodeRef = null;  
    
    /**
     * The version counter service
     */
    private VersionCounterDaoService versionCounterService = null;
    
    /**
     * Initialise the version store service, ensuring that a version store exists.
     */
    public void initialise()
    {
        // Ensure that the version store has been created
        if (this.storeService.exists(getStoreRef()) == false)
        {
            this.storeService.createStore(LightWeightVersionService.STORE_PROTOCOL, LightWeightVersionService.STORE_ID);
        }        
        
        // Get the version store root node reference
        this.versionStoreRootNodeRef = this.storeService.getRootNode(getStoreRef());
    }
    
    /**
     * Sets the node service
     * 
     * @param nodeService the node service
     */
    public void setNodeService(NodeService nodeService)
    {
        this.nodeService = nodeService;
    }

    /**
     * Sets the store service
     * 
     * @param storeService the store service
     */
    public void setStoreService(StoreService storeService)
    {
        this.storeService = storeService;
    }
    
    /**
     * Sets the version counter service
     * 
     * @param versionCounterService the version counter service
     */
    public void setVersionCounterDaoService(VersionCounterDaoService versionCounterService)
    {
        this.versionCounterService = versionCounterService;
    }
    
    /**
     * Creates a new version of the passed node assigning the version properties 
     * accordingly.
     * 
     * @param nodeRef a node reference
     * @param versionProperties the version properties
     * 
     * TODO should this pass the new version back ???
     */
    public Version createVersion(NodeRef nodeRef, Map<String, String> versionProperties)
    {
        // Get the next version number
        int versionNumber = this.versionCounterService.nextVersionNumber(getStoreRef());
        
        // Create the version
        return createVersion(nodeRef, versionProperties, versionNumber);
    }        

    /**
     * 
     */
    public Collection<Version> createVersion(NodeRef nodeRef, Map<String, String> versionProperties,
            boolean versionChildren)
    {
        throw new UnsupportedOperationException();
    }

    /**
     * 
     */
    public Collection<Version> createVersion(List<NodeRef> nodeRefs, Map<String, String> versionProperties)
    {
        Collection<Version> result = new ArrayList<Version>(nodeRefs.size());
        
        // Get the next version number
        int versionNumber = this.versionCounterService.nextVersionNumber(getStoreRef());
        
        // Version each node in the list
        for (NodeRef nodeRef : nodeRefs)
        {
            result.add(createVersion(nodeRef, versionProperties, versionNumber));
        }
        
        return result;
    }
    
    /**
     * Creates a new version of the passed node assigning the version properties 
     * accordingly.
     * 
     * @param nodeRef a node reference
     * @param versionProperties the version properties
     * @param versionNumber the version number
     * 
     * TODO should this pass the new version back ???
     */
    private Version createVersion(NodeRef nodeRef, Map<String, String> versionProperties, int versionNumber)
    {
        // TODO we need some way of 'locking' the current node to ensure no modifications (or other versions) 
        //      can take place untill the versioning process is complete
        
        // Check the repository for the version history for this node
        NodeRef versionHistoryRef = null; 
        NodeRef currentVersionRef = null;
        
        if (versionHistoryRef == null)
        {
            // Create a new version history node
            versionHistoryRef = this.nodeService.createNode(
                    this.versionStoreRootNodeRef, 
                    LightWeightVersionService.CHILD_VERSION_HISTORIES, 
                    Node.TYPE_CONTAINER);
            
            // TODO store the id of the node that relates to this version history ??
        }
        else
        {
            // Since we have an exisiting version history we should be able to lookup
            // the current version
            currentVersionRef = getCurrentVersionRef(versionHistoryRef, nodeRef);
        }
        
        // Create the new version node (child of the version history)
        NodeRef newVersionRef = createNewVersion(versionHistoryRef, versionProperties, versionNumber);
        
        // 'Freeze' the current nodes state in the new version node
        freezeNodeState(nodeRef, newVersionRef);
        
        if (currentVersionRef == null)
        {
            // Set the new version to be the root version in the version history
            this.nodeService.createAssociation(versionHistoryRef, newVersionRef, LightWeightVersionService.ASSOC_ROOT_VERSION);
        }
        else
        {
            // Relate the new version to the current version as its successor
            this.nodeService.createAssociation(currentVersionRef, newVersionRef, LightWeightVersionService.ASSOC_SUCCESSOR);
            
            // TODO what do we do about branches (if anything) are we going to support them to begin with ??
        }
        
        // Set the new version label on the versioned node
        // TODO
        
        // TODO return data object representing the version
        return getVersion(newVersionRef);
    }

    /**
     * 
     */
    public VersionHistory getVersionHistory(NodeRef nodeRef)
    {
        throw new UnsupportedOperationException();
    }
    
    /**
     * Constructs the a version object to contain the version information from the version node ref.
     * 
     * @param versionRef the version ref
     * @return Version object containing verison data
     */
    private Version getVersion(NodeRef versionRef)
    {
        // Get the version label
        String versionLabel = this.nodeService.getProperty(versionRef, LightWeightVersionService.ATTR_VERSION_LABEL);
        
        // Get the created date
        String longDate = this.nodeService.getProperty(versionRef, LightWeightVersionService.ATTR_VERSION_CREATED_DATE);
        Date createdDate = new Date(Long.parseLong(longDate));
        
        // TODO need to sort out how we get the other version properties off the version node
        Map<String, String> versionProperties = new HashMap<String, String>();
        
        // TODO need to figure out how we buld the version node ref from the wk version node ref
        NodeRef frozenStateRef = versionRef;
        
        return new VersionImpl(versionLabel, createdDate, versionProperties, frozenStateRef);        
    }
    
    /**
     * Gets the reference to the light weight veresion store
     * 
     * @return a store reference
     */
    private StoreRef getStoreRef()
    {
        return new StoreRef(LightWeightVersionService.STORE_PROTOCOL, LightWeightVersionService.STORE_ID);
    }
    
    /**
     * Creates a new version node, setting the properties both calculated and specified.
     * 
     * @param versionHistoryRef version history node reference
     * @param versionProperties version properties
     * @return the version node reference
     */
    private NodeRef createNewVersion(NodeRef versionHistoryRef, Map<String, String> versionProperties, int versionNumber)
    {
        NodeRef newVersion = this.nodeService.createNode(
                versionHistoryRef, 
                LightWeightVersionService.CHILD_VERSIONS, 
                Node.TYPE_REAL);
        
        // Set the version number for the new version
        this.nodeService.setProperty(newVersion, LightWeightVersionService.ATTR_VERSION_NUMBER, Integer.toString(versionNumber));
        
        // Set the created date
        Date createdDate = new Date();
        this.nodeService.setProperty(newVersion, LightWeightVersionService.ATTR_VERSION_CREATED_DATE, Long.toString(createdDate.getTime()));
        
        // Calculate the version label
        // TODO this should be a callback so that a verison label policy can be specified ...
        this.nodeService.setProperty(newVersion, LightWeightVersionService.ATTR_VERSION_LABEL, Integer.toString(versionNumber));
        
        // TODO any other calculated properties ...
        
        // Set the property values
        // TODO these need to be namespaced in order to avoid conflicts with frozen state
        //this.nodeService.setProperties(newVersion, versionProperties);
        
        return newVersion;
    }
    
    /**
     * 
     * @param nodeRef
     * @param versionRef
     */
    private void freezeNodeState(NodeRef nodeRef, NodeRef versionRef)
    {
        // Copy the current values of the node onto the version node, thus taking a snap shot of the values
        Map<String, String> nodeProperties = this.nodeService.getProperties(nodeRef);
        if (nodeProperties != null)
        {
            for (String propertyName : nodeProperties.keySet())
            {
                // TODO Copy the values onto the version node ... 
                
                // TODO Ignore the common properties (id, type ...).  Are these identifies by a namespace ??
            }
        }
        
        // TODO here we need to deal with any content that might be on the node
        
        // TODO here we need to deal with the children of the node
            // if not versionable the copy as reference to the version history
            // if not versionable then copy
            // can also ignore
        
        // TODO how do we override the above default behaviour ??
    }
    
    /**
     * Gets a reference to the version history node for a given 'real' node.
     * 
     * @param nodeRef a node reference
     * @return a reference to the version history node, null of none
     */
    private NodeRef getVersionHistoryRef(NodeRef nodeRef)
    {
        // TODO
        return null;
    }
    
    /**
     * Gets a reference to the node for the current version of the passed node ref.
     * 
     * This uses the version label as a mechanism for looking up the version node in
     * the version history.
     * 
     * @param nodeRef a node reference
     * @return a reference to a version reference
     */
    private NodeRef getCurrentVersionRef(NodeRef versionHistory, NodeRef nodeRef)
    {
        // TOOD
        return null;
    }
}
