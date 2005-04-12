package com.activiti.repo.version.lightweight;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.activiti.repo.domain.Node;
import com.activiti.repo.ref.ChildAssocRef;
import com.activiti.repo.ref.NodeRef;
import com.activiti.repo.ref.QName;
import com.activiti.repo.version.Version;
import com.activiti.repo.version.VersionHistory;
import com.activiti.repo.version.VersionLabelPolicy;
import com.activiti.repo.version.VersionService;
import com.activiti.repo.version.VersionServiceException;
import com.activiti.repo.version.common.VersionHistoryImpl;
import com.activiti.repo.version.common.VersionImpl;
import com.activiti.repo.version.common.counter.VersionCounterDaoService;

/**
 * The light weight version service implementation.
 * 
 * Uses a workspace store to contain the version histories and the related versions.
 * 
 * @author Roy Wetheral
 */
public class VersionStoreVersionServiceImpl extends VersionStoreBaseImpl implements VersionService
{
    /**
     * The version counter service
     */
    private VersionCounterDaoService versionCounterService = null;
    
    /**
     * The version label policy
     */
    private VersionLabelPolicy versionLabelPolicy = null;
    
    /**
     * Sets the version counter service
     * 
     * @param versionCounterService  the version counter service
     */
    public void setVersionCounterDaoService(VersionCounterDaoService versionCounterService)
    {
        this.versionCounterService = versionCounterService;
    }
    
    /**
     * Sets the version label policy
     * 
     * @param versionLabelPolicy  the version label policy
     */
    public void setVersionLabelPolicy(VersionLabelPolicy versionLabelPolicy)
    {
        this.versionLabelPolicy = versionLabelPolicy;
    }
    
    /**
     * Creates a new version based on the referenced node.
     * <p>
     * If the node has not previously been versioned then a version history and
     * initial version will be created.
     * <p>
     * If the node referenced does not or can not have the version aspect
     * applied to it then an exception will be raised.
     * <p>
     * The version properties are sotred as version meta-data against the newly
     * created version.
     * 
     * @param nodeRef            a node reference
     * @param versionProperties  the version properties that are stored with the newly created
     *                           version
     * @return                   the created version object
     */
    public Version createVersion(NodeRef nodeRef, Map<String, String> versionProperties)
    {
        // Get the next version number
        int versionNumber = this.versionCounterService.nextVersionNumber(getVersionStoreReference());
        
        // Create the version
        return createVersion(nodeRef, versionProperties, versionNumber);
    }        

    /**
     * Creates a new version based on the referenced node.
     * <p>
     * If the node has not previously been versioned then a version history and
     * initial version will be created.
     * <p>
     * If the node referenced does not or can not have the version aspect
     * applied to it then an exception will be raised.
     * <p>
     * The version properties are sotred as version meta-data against the newly
     * created version.
     * 
     * @param nodeRef            a node reference
     * @param versionProperties  the version properties that are stored with the newly created
     *                           version
     * @param versionChildren    if true then the children of the referenced node are also
     *                           versioned, false otherwise
     * @return                   the created version object(s)
     */
    public Collection<Version> createVersion(NodeRef nodeRef, Map<String, String> versionProperties,
            boolean versionChildren)
    {
        Collection<Version> result = new ArrayList<Version>();
        
        // Get the next version number
        int versionNumber = this.versionCounterService.nextVersionNumber(getVersionStoreReference());
        
        result.add(createVersion(nodeRef, versionProperties, versionNumber));
        
        if (versionChildren == true)
        {
            // Get the children of the node
            Collection<ChildAssocRef> children = this.dbNodeService.getChildAssocs(nodeRef);
            for (ChildAssocRef childAssoc : children)
            {
                // Recurse into this method to version all the children
                Collection<Version> childVersions = createVersion(childAssoc.getChildRef(), versionProperties, versionChildren);
                result.addAll(childVersions);
            }
        }
        
        return result;
    }

    /**
     * Creates new versions based on the list of node references provided.
     * 
     * @param nodeRefs           a list of node references
     * @param versionProperties  version property values
     * @return                   a collection of newly created versions
     */
    public Collection<Version> createVersion(List<NodeRef> nodeRefs, Map<String, String> versionProperties)
    {
        Collection<Version> result = new ArrayList<Version>(nodeRefs.size());
        
        // Get the next version number
        int versionNumber = this.versionCounterService.nextVersionNumber(getVersionStoreReference());
        
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
     * @param nodeRef            a node reference
     * @param versionProperties  the version properties
     * @param versionNumber      the version number
     * @return                   the newly created version     
     */
    private Version createVersion(NodeRef nodeRef, Map<String, String> versionProperties, int versionNumber)
    {
        // TODO we need some way of 'locking' the current node to ensure no modifications (or other versions) 
        //      can take place untill the versioning process is complete
        
        // Check the repository for the version history for this node
        NodeRef versionHistoryRef = getVersionHistoryNodeRef(nodeRef); 
        NodeRef currentVersionRef = null;
        
        if (versionHistoryRef == null)
        {
            // Create a new version history node
            ChildAssocRef childAssocRef = this.dbNodeService.createNode(
                    this.versionStoreRootNodeRef, 
                    VersionStoreBaseImpl.CHILD_VERSION_HISTORIES, 
                    Node.TYPE_CONTAINER);
            versionHistoryRef = childAssocRef.getChildRef();
            
            // Store the id of the origional node on the version history node 
            this.dbNodeService.setProperty(
                    versionHistoryRef, 
					VersionStoreBaseImpl.ATTR_VERSIONED_NODE_ID, 
                    nodeRef.getId());
        }
        else
        {
            // Since we have an exisiting version history we should be able to lookup
            // the current version
            currentVersionRef = getCurrentVersionNodeRef(versionHistoryRef, nodeRef);
        }
        
        // Create the new version node (child of the version history)
        NodeRef newVersionRef = createNewVersion(nodeRef, versionHistoryRef, currentVersionRef, versionProperties, versionNumber);
        
        // 'Freeze' the current nodes state in the new version node
        freezeNodeState(nodeRef, newVersionRef);
        
        if (currentVersionRef == null)
        {
            // Set the new version to be the root version in the version history
            this.dbNodeService.createAssociation(
                    versionHistoryRef, 
                    newVersionRef, 
                    VersionStoreVersionServiceImpl.ASSOC_ROOT_VERSION);
        }
        else
        {
            // Relate the new version to the current version as its successor
            this.dbNodeService.createAssociation(
                    currentVersionRef, 
                    newVersionRef, 
                    VersionStoreVersionServiceImpl.ASSOC_SUCCESSOR);
            
            // TODO what do we do about branches (if anything) are we going to support them to begin with ??
        }
        
        // Create the version data object
        Version version = getVersion(newVersionRef);
        
        // Set the new version label on the versioned node
        this.dbNodeService.setProperty(
                nodeRef, 
                VersionService.ATTR_CURRENT_VERSION_LABEL, 
                version.getVersionLabel());
        
        // Return the data object representing the newly created version
        return version;
    }

    /**
     * TODO need to check performance
     */
    public VersionHistory getVersionHistory(NodeRef nodeRef)
    {
        // TODO could definatly do with a cache since these are read-only objects ... maybe not 
        //      since they are dependant on the workspace of the node passed
        
        // TODO need to scope version history by work space ....
        
        VersionHistory versionHistory = null;
        
        NodeRef versionHistoryRef = getVersionHistoryNodeRef(nodeRef);
        if (versionHistoryRef != null)
        {
            NodeRef rootVersion = null;
            Collection<NodeRef> rootNodes = this.dbNodeService.getAssociationTargets(versionHistoryRef, ASSOC_ROOT_VERSION);
            if (rootNodes.size() == 1)
            {
                // Get the root version
                rootVersion = (NodeRef)rootNodes.toArray()[0];
            }
            else
            {
                // Error since there should be one and only one root nodes
                throw new VersionServiceException("There should only be one root node in a version history tree.");
            }
            
            versionHistory = new VersionHistoryImpl(getVersion(rootVersion));
            buildVersionHistory(versionHistory, rootVersion);
        }
        
        return versionHistory;
    }
    
    /**
     * 
     * 
     * @param versionHistory
     * @param nodeRef
     */
    private void buildVersionHistory(VersionHistory versionHistory, NodeRef nodeRef)
    {
        Collection<NodeRef> successors = this.dbNodeService.getAssociationTargets(nodeRef, ASSOC_SUCCESSOR);
        for (NodeRef successor : successors)
        {
            ((VersionHistoryImpl)versionHistory).addVersion(getVersion(successor), getVersion(nodeRef));
            buildVersionHistory(versionHistory, successor);
        }
    }
    
    /**
     * Constructs the a version object to contain the version information from the version node ref.
     * 
     * @param versionRef  the version reference
     * @return            object containing verison data
     */
    private Version getVersion(NodeRef versionRef)
    {
        // TODO could definatly do with a cache since these are read only objects ...
        // TODO this needs a little sorting out ....
        // TODO store the date and label in the versionProperies map
        
        String versionLabel = null;
        Date createdDate = null;
        Map<String, String> versionProperties = new HashMap<String, String>();
        
        // Get the node properties
        Map<QName, Serializable> nodeProperties = this.dbNodeService.getProperties(versionRef);
        for (QName key : nodeProperties.keySet())
        {
            if (key.getNamespaceURI().equals(VersionStoreVersionServiceImpl.LW_VERSION_STORE_NAMESPACE) == true)
            {   
                String strLocalName = key.getLocalName();
                String value = (String)nodeProperties.get(key);
                
                if (strLocalName.equals(VersionStoreBaseImpl.ATTR_VERSION_LABEL.getLocalName()) == true)
                {
                    // Get the version label property
                    versionLabel = value;
                }
                else if (strLocalName.equals(VersionStoreBaseImpl.ATTR_VERSION_CREATED_DATE.getLocalName()))
                {
                    // Get the created date property
                    createdDate = new Date(Long.parseLong(value));
                }
				// TODO need to find a better way to sort this out ...
                else if (strLocalName.equals(VersionStoreBaseImpl.ATTR_VERSION_NUMBER.getLocalName()) == false &&
						 strLocalName.equals(VersionStoreBaseImpl.ATTR_FROZEN_NODE_ID.getLocalName()) == false &&
						 strLocalName.equals(VersionStoreBaseImpl.ATTR_FROZEN_NODE_STORE_ID.getLocalName()) == false &&
						 strLocalName.equals(VersionStoreBaseImpl.ATTR_FROZEN_NODE_STORE_PROTOCOL.getLocalName()) == false)
                {
                    versionProperties.put(strLocalName, value);
                }
            }
        }
        
        // Create and return the version object
        return new VersionImpl(versionLabel, createdDate, versionProperties, versionRef);        
    }    
    
    /**
     * Creates a new version node, setting the properties both calculated and specified.
     * 
     * @param versionableNodeRef  the reference to the node being versioned
     * @param versionHistoryRef   version history node reference
     * @param preceedingNodeRef   the version node preceeding this in the version history
     * 							  , null if none
     * @param versionProperties   version properties
     * @param versionNumber		  the version number
     * @return                    the version node reference
     */
    private NodeRef createNewVersion(
			NodeRef versionableNodeRef, 
			NodeRef versionHistoryRef, 
			NodeRef preceedingNodeRef, 
			Map<String, String> 
			versionProperties, 
			int versionNumber)
    {
        // Create the new version
        ChildAssocRef childAssocRef = this.dbNodeService.createNode(
                versionHistoryRef, 
				VersionStoreBaseImpl.CHILD_VERSIONS, 
                Node.TYPE_CONTAINER);
        NodeRef newVersion = childAssocRef.getChildRef();
        
        // Set the version number for the new version
        this.dbNodeService.setProperty(
                newVersion, 
				VersionStoreBaseImpl.ATTR_VERSION_NUMBER, 
                Integer.toString(versionNumber));
        
        // Set the created date
        Date createdDate = new Date();
        this.dbNodeService.setProperty(
                newVersion, 
				VersionStoreBaseImpl.ATTR_VERSION_CREATED_DATE, 
                Long.toString(createdDate.getTime()));
		
		// Set the versionable node id
		this.dbNodeService.setProperty(
				newVersion,
				VersionStoreBaseImpl.ATTR_FROZEN_NODE_ID,
				versionableNodeRef.getId());
		
		// Set the versionable node store protocol
		this.dbNodeService.setProperty(
				newVersion,
				VersionStoreBaseImpl.ATTR_FROZEN_NODE_STORE_PROTOCOL,
				versionableNodeRef.getStoreRef().getProtocol());
		
		// Set the versionable node store id
		this.dbNodeService.setProperty(
				newVersion,
				VersionStoreBaseImpl.ATTR_FROZEN_NODE_STORE_ID,
				versionableNodeRef.getStoreRef().getIdentifier());
		
        // Calculate the version label
        String versionLabel = null;
        if (this.versionLabelPolicy != null)
        {
            // Use the policy to create the version label
            Version preceedingVersion = getVersion(preceedingNodeRef);
            versionLabel = this.versionLabelPolicy.getVersionLabelValue(preceedingVersion, versionNumber, versionProperties);
        }
        else
        {
            // The default version label policy is to set it equal to the verion number
            versionLabel = Integer.toString(versionNumber);
        }
        this.dbNodeService.setProperty(
                newVersion, 
                VersionStoreVersionServiceImpl.ATTR_VERSION_LABEL, 
                versionLabel);
        
        // TODO any other calculated properties ...
        
        // Set the property values
        for (String key : versionProperties.keySet())
        {
            // Apply the namespace to the verison property
            QName propertyName = QName.createQName(
                    VersionStoreVersionServiceImpl.LW_VERSION_STORE_NAMESPACE,
                    key);
            
            // Set the property value on the node
            this.dbNodeService.setProperty(
                    newVersion,
                    propertyName,
                    versionProperties.get(key));
        }
        
        return newVersion;
    }
    
    /**
     * Takes the current state of the node and 'freezes' it on the version node.
     * <p>
     * TODO describe how children are frozen and how this behaviour can be overridden.
     * 
     * @param nodeRef     the node reference
     * @param versionRef  the version node reference
     */
    private void freezeNodeState(NodeRef nodeRef, NodeRef versionRef)
    {
        // TODO this will chage when we integrate with the data dictionary
        // Store the current node type
        String nodeType = this.nodeService.getType(nodeRef);
        this.dbNodeService.setProperty(versionRef, ATTR_FROZEN_NODE_TYPE, nodeType);
        
        // Copy the current values of the node onto the version node, thus taking a snap shot of the values
        Map<QName, Serializable> nodeProperties = this.nodeService.getProperties(nodeRef);
        if (nodeProperties != null)
        {
            // Copy the property values from the node onto the version node
            for (QName propertyName : nodeProperties.keySet())
            {
                // TODO there will be certain properties that will be common (id, type ...) these
                //      need to be stored specifically
                
                // Set the property value's
                this.dbNodeService.setProperty(versionRef, propertyName, nodeProperties.get(propertyName));                
            }
        }
        
        // TODO here we need to deal with any content that might be on the node
        
        // TODO the following behaviour is default and should overrideable (ie: can choose when to ignore, version or reference children
        
        // Get the children of the versioned node
        Collection<ChildAssocRef> childAssocRefs = this.nodeService.getChildAssocs(nodeRef);
        for (ChildAssocRef childAssocRef : childAssocRefs)
        {
            // Need to determine whether the child is versioned or not
            NodeRef versionHistoryRef = getVersionHistoryNodeRef(childAssocRef.getChildRef());
            if (versionHistoryRef == null)
            {
                // TODO this should be a node of type reference ...
                
                // The child node is not versioned so we associate to a node reference
                NodeRef referenceRef = this.dbNodeService.createNode(versionRef, childAssocRef.getName(), Node.TYPE_REAL).getChildRef();
                
                // Set the reference string
                // TODO this needs to be inline with the reference type
                this.dbNodeService.setProperty(referenceRef, QName.createQName("{referenceNode}referenceString"), nodeRef.toString());                
            }
            else
            {
                // Associate the version with the version history object of hte child
                this.dbNodeService.addChild(versionRef, versionHistoryRef, childAssocRef.getName());
            }
        }
        
        // TODO What do we do about the associations???
        
        // TODO how do we override the above default behaviour ??
    }    
}
