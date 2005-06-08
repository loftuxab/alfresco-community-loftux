package org.alfresco.repo.version.lightweight;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.alfresco.repo.content.ContentService;
import org.alfresco.repo.content.ContentStore;
import org.alfresco.repo.dictionary.NamespaceService;
import org.alfresco.repo.dictionary.impl.DictionaryBootstrap;
import org.alfresco.repo.node.NodeService;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyScope;
import org.alfresco.repo.ref.ChildAssocRef;
import org.alfresco.repo.ref.NodeAssocRef;
import org.alfresco.repo.ref.NodeRef;
import org.alfresco.repo.ref.QName;
import org.alfresco.repo.ref.StoreRef;
import org.alfresco.repo.search.Searcher;
import org.alfresco.repo.version.ReservedVersionNameException;
import org.alfresco.repo.version.Version;
import org.alfresco.repo.version.VersionHistory;
import org.alfresco.repo.version.VersionService;
import org.alfresco.repo.version.VersionServiceException;
import org.alfresco.repo.version.common.AbstractVersionServiceImpl;
import org.alfresco.repo.version.common.VersionHistoryImpl;
import org.alfresco.repo.version.common.VersionImpl;
import org.alfresco.repo.version.common.VersionUtil;
import org.alfresco.repo.version.common.counter.VersionCounterDaoService;
import org.alfresco.repo.version.common.versionlabel.SerialVersionLabelPolicy;
import org.alfresco.util.AspectMissingException;

/**
 * The light weight version service implementation.
 * 
 * @author Roy Wetheral
 */
public class VersionServiceImpl extends AbstractVersionServiceImpl
								implements VersionService, VersionStoreConst
{
    /**
     * Error messages
     */
    private static final String ERR_NOT_FOUND = "The current version could not be found in the light weight store.";
    private static final String ERR_NO_BRANCHES = "The current implmentation of the light weight version store does " +
                                                    "not support the creation of branches.";
    
    /**
     * The version counter service
     */
    private VersionCounterDaoService versionCounterService ;
    
    /**
     * The db node service, used as the version store implementation
     */
    protected NodeService dbNodeService;

    /**
     * The repository searcher
     */
    private Searcher searcher;       
	
    /**
     * The generic content service
     */
    private ContentService contentService;
    
    /**
     * The version store content store
     */
    private ContentStore versionContentStore;		
    
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
     * Sets the version counter service
     * 
     * @param versionCounterService  the version counter service
     */
    public void setVersionCounterDaoService(VersionCounterDaoService versionCounterService)
    {
        this.versionCounterService = versionCounterService;
    }    
    
    /**
     * Set the generic content service
     * 
     * @param contentService  the content service
     */
    public void setContentService(ContentService contentService)
    {
        this.contentService = contentService;
    }
    
    /**
     * Sets the version content store 
     * 
     * @param versionContentStore  the version content store
     */
    public void setVersionContentStore(
            ContentStore versionContentStore)
    {
        this.versionContentStore = versionContentStore;
    }
    
	/**
	 * Initialise method
	 */
	@Override
    public void initialise()
    {
		super.initialise();
		
		//	Ensure that the version store has been created
        if (this.dbNodeService.exists(getVersionStoreReference()) == false)
        {
            this.dbNodeService.createStore(
					VersionStoreConst.STORE_PROTOCOL, 
					VersionStoreConst.STORE_ID);
        }
		
		// Regiseter the serial version label behaviour
		this.policyComponent.bindClassBehaviour(
				QName.createQName(NamespaceService.ALFRESCO_URI, "calculateVersionLabel"),
				DictionaryBootstrap.TYPE_QNAME_BASE,
				new JavaBehaviour(new SerialVersionLabelPolicy(), "calculateVersionLabel"));
	}
    
	/**
     * Gets the reference to the version store
     * 
     * @return  reference to the version store
     */
    public StoreRef getVersionStoreReference()
    {
        return new StoreRef(
				VersionStoreConst.STORE_PROTOCOL, 
				VersionStoreConst.STORE_ID);
    }
	
    /**
     * @see org.alfresco.repo.version.VersionService#createVersion(NodeRef, Map<String, Serializable>)
     */
    public Version createVersion(
            NodeRef nodeRef, 
            Map<String, Serializable> versionProperties)
            throws ReservedVersionNameException, AspectMissingException
    {
        // Get the next version number
        int versionNumber = this.versionCounterService.nextVersionNumber(getVersionStoreReference());
        
        // Create the version
        return createVersion(nodeRef, versionProperties, versionNumber);
    }        

    /**
     * The version's are created from the children upwards with the parent being created first.  This will
     * ensure that the child version references in the version node will point to the version history nodes
     * for the (possibly) newly created version histories.
     * 
     * @see org.alfresco.repo.version.VersionService#createVersion(NodeRef, Map<String, Serializable>, boolean)
     */
    public Collection<Version> createVersion(
            NodeRef nodeRef, 
            Map<String, Serializable> versionProperties,
            boolean versionChildren)
            throws ReservedVersionNameException, AspectMissingException
    {
        // Get the next version number
        int versionNumber = this.versionCounterService.nextVersionNumber(getVersionStoreReference());
        
        // Create the versions
        return createVersion(nodeRef, versionProperties, versionChildren, versionNumber);
    }
    
    /**
     * Helper method used to create the version when the versionChildren flag is provided.  This method
     * ensures that all the children (if the falg is set to true) are created with the same version 
     * number, this ensuring that the version stripe is correct.
     * 
     * @param nodeRef                           the parent node reference
     * @param versionProperties                 the version properties
     * @param versionChildren                   indicates whether to version the children of the parent
     *                                          node
     * @param versionNumber                     the version number
     
     * @return                                  a collection of the created versions
     * @throws ReservedVersionNameException     thrown if there is a reserved version property name clash
     * @throws AspectMissingException    thrown if the version aspect is missing from a node
     */
    private Collection<Version> createVersion(
            NodeRef nodeRef, 
            Map<String, Serializable> versionProperties,
            boolean versionChildren,
            int versionNumber) 
            throws ReservedVersionNameException, AspectMissingException
    {

        Collection<Version> result = new ArrayList<Version>();
        
        if (versionChildren == true)
        {
            // Get the children of the node
            Collection<ChildAssocRef> children = this.dbNodeService.getChildAssocs(nodeRef);
            for (ChildAssocRef childAssoc : children)
            {
                // Recurse into this method to version all the children with the same version number
                Collection<Version> childVersions = createVersion(
                        childAssoc.getChildRef(), 
                        versionProperties, 
                        versionChildren, 
                        versionNumber);
                result.addAll(childVersions);
            }
        }
        
        result.add(createVersion(nodeRef, versionProperties, versionNumber));
        
        return result;
    }

    /**
     * Note:  we can't control the order of the list, so if we have children and parents in the list and the
     * parents get versioned before the children and the children are not already versioned then the parents 
     * child references will be pointing to the node ref, rather than the verison history.
     * 
     * @see org.alfresco.repo.version.VersionService#createVersion(List<NodeRef>, Map<String, Serializable>)
     */
    public Collection<Version> createVersion(
            Collection<NodeRef> nodeRefs, 
            Map<String, Serializable> versionProperties)
            throws ReservedVersionNameException, AspectMissingException
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
     * @param  nodeRef              a node reference
     * @param  versionProperties    the version properties
     * @param  versionNumber        the version number
     * @return                      the newly created version
     * @throws ReservedVersionNameException
     *                              thrown if there is a name clash in the version properties  
     * @throws AspectMissingException    
     *                              thrown if the version aspect is missing from the node   
     */
    private Version createVersion(
            NodeRef nodeRef, 
            Map<String, Serializable> origVersionProperties, 
            int versionNumber)
            throws ReservedVersionNameException, AspectMissingException
    {

		// Copy the version properties (to prevent unexpected side effects to the caller)
		Map<String, Serializable> versionProperties = new HashMap<String, Serializable>();
		versionProperties.putAll(origVersionProperties);
		
        // Check for the version aspect
        checkForVersionAspect(nodeRef);
        
        // Call the policy behaviour
		invokeBeforeCreateVersion(nodeRef);		
        
        // Check that the supplied additional version properties do not clash with the reserved ones
        VersionUtil.checkVersionPropertyNames(versionProperties.keySet());
        
        // Check the repository for the version history for this node
        NodeRef versionHistoryRef = getVersionHistoryNodeRef(nodeRef); 
        NodeRef currentVersionRef = null;
        
        if (versionHistoryRef == null)
        {
            HashMap<QName, Serializable> props = new HashMap<QName, Serializable>();
            props.put(PROP_QNAME_VERSIONED_NODE_ID, nodeRef.getId());
            
            // Create a new version history node
            ChildAssocRef childAssocRef = this.dbNodeService.createNode(
                    getRootNode(), 
					CHILD_QNAME_VERSION_HISTORIES, 
                    CHILD_QNAME_VERSION_HISTORIES,
                    TYPE_QNAME_VERSION_HISTORY,
                    props);
            versionHistoryRef = childAssocRef.getChildRef();            
        }
        else
        {
            // Since we have an exisiting version history we should be able to lookup
            // the current version
            currentVersionRef = getCurrentVersionNodeRef(versionHistoryRef, nodeRef);     
            
            if (currentVersionRef == null)
            {
                throw new VersionServiceException(ERR_NOT_FOUND);
            }
            
            // Need to check that we are not about to create branch since this is not currently supported
            VersionHistory versionHistory = buildVersionHistory(versionHistoryRef, nodeRef);
            Version currentVersion = getVersion(currentVersionRef);
            if (versionHistory.getSuccessors(currentVersion).size() != 0)
            {
                throw new VersionServiceException(ERR_NO_BRANCHES);
            }
        }
        
		// Add the standard version properties
		addStandardVersionProperties(versionProperties, nodeRef, currentVersionRef, versionNumber);
		
		// Create the node details
		QName classRef = this.nodeService.getType(nodeRef);
		PolicyScope nodeDetails = new PolicyScope(classRef);
		
		// Get the node details by calling the onVersionCreate policy behaviour
		invokeOnCreateVersion(nodeRef, versionProperties, nodeDetails);
		
		// TODO What do we do about ad-hoc properties and assocs ?
		
		// Create the new version node (child of the version history)
        NodeRef newVersionRef = createNewVersion(
                nodeRef, 
                versionHistoryRef,
                versionProperties, 
                nodeDetails);
        
        if (currentVersionRef == null)
        {
            // Set the new version to be the root version in the version history
            this.dbNodeService.createAssociation(
                    versionHistoryRef, 
                    newVersionRef, 
                    VersionServiceImpl.ASSOC_ROOT_VERSION);
        }
        else
        {
            // Relate the new version to the current version as its successor
            this.dbNodeService.createAssociation(
                    currentVersionRef, 
                    newVersionRef, 
                    VersionServiceImpl.ASSOC_SUCCESSOR);
        }
        
        // Create the version data object
        Version version = getVersion(newVersionRef);
        
        // Set the new version label on the versioned node
        this.nodeService.setProperty(
                nodeRef, 
                DictionaryBootstrap.PROP_QNAME_CURRENT_VERSION_LABEL, 
                version.getVersionLabel());
        
        // Return the data object representing the newly created version
        return version;
    }

    /**
     * @see org.alfresco.repo.version.VersionService#getVersionHistory(NodeRef)
     */
    public VersionHistory getVersionHistory(NodeRef nodeRef)
        throws AspectMissingException
    {
        // Check for the version aspect
        checkForVersionAspect(nodeRef);

        // TODO could definatly do with a cache since these are read-only objects ... maybe not 
        //      since they are dependant on the workspace of the node passed
        
        VersionHistory versionHistory = null;
        
        NodeRef versionHistoryRef = getVersionHistoryNodeRef(nodeRef);
        if (versionHistoryRef != null)
        {
            versionHistory = buildVersionHistory(versionHistoryRef, nodeRef);
        }
        
        return versionHistory;
    }       
	
	/**
	 * @see VersionService#getCurrentVersion(NodeRef)
	 */
	public Version getCurrentVersion(NodeRef nodeRef)
	{
		Version version = null;
		
		if (this.nodeService.hasAspect(nodeRef, DictionaryBootstrap.ASPECT_QNAME_VERSIONABLE) == true)
		{
			VersionHistory versionHistory = getVersionHistory(nodeRef);
			if (versionHistory != null)
			{
				String versionLabel = (String)this.nodeService.getProperty(nodeRef, DictionaryBootstrap.PROP_QNAME_CURRENT_VERSION_LABEL);
				version = versionHistory.getVersion(versionLabel);
			}
		}
		
		return version;
	}
	
	private void addStandardVersionProperties(Map<String, Serializable> versionProperties, NodeRef nodeRef, NodeRef preceedingNodeRef, int versionNumber)
	{
		// Set the version number for the new version
		versionProperties.put(Version.PROP_VERSION_NUMBER, Integer.toString(versionNumber));
        
        // Set the created date
		versionProperties.put(Version.PROP_CREATED_DATE, new Date());
		
		// Set the versionable node id
		versionProperties.put(Version.PROP_FROZEN_NODE_ID, nodeRef.getId());
		
		// Set the versionable node store protocol
		versionProperties.put(Version.PROP_FROZEN_NODE_STORE_PROTOCOL, nodeRef.getStoreRef().getProtocol());
		
		// Set the versionable node store id
		versionProperties.put(Version.PROP_FROZEN_NODE_STORE_ID, nodeRef.getStoreRef().getIdentifier());
        
        // Store the current node type
        QName nodeType = this.nodeService.getType(nodeRef);
		versionProperties.put(Version.PROP_FROZEN_NODE_TYPE, nodeType);
        
        // Store the current aspects
        Set<QName> aspects = this.nodeService.getAspects(nodeRef);
		versionProperties.put(Version.PROP_FROZEN_ASPECTS, (Serializable)aspects);
        
        // Calculate the version label
		QName classRef = this.nodeService.getType(nodeRef);
		Version preceedingVersion = getVersion(preceedingNodeRef);
        String versionLabel = invokeCalculateVersionLabel(classRef, preceedingVersion, versionNumber, versionProperties);
		versionProperties.put(Version.PROP_VERSION_LABEL, versionLabel);
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
			Map<String, Serializable> versionProperties,
			PolicyScope nodeDetails)
    {
        HashMap<QName, Serializable> props = new HashMap<QName, Serializable>(15, 1.0f);        
        
		// Set the property values
        for (String key : versionProperties.keySet())
        {
            // Apply the namespace to the verison property
            QName propertyName = QName.createQName(
                    VersionServiceImpl.NAMESPACE_URI,
                    key);
            
            // Set the property value on the node
            props.put(propertyName, versionProperties.get(key));
        }
        
        // Create the new version
        ChildAssocRef childAssocRef = this.dbNodeService.createNode(
                versionHistoryRef, 
				CHILD_QNAME_VERSIONS,
                CHILD_QNAME_VERSIONS,
                TYPE_QNAME_VERSION,
                props);
        NodeRef versionNodeRef = childAssocRef.getChildRef();
		
		// Freeze the various parts of the node
		freezeProperties(versionNodeRef, nodeDetails.getProperties());
		freezeChildAssociations(versionNodeRef, nodeDetails.getChildAssociations());
		freezeAssociations(versionNodeRef, nodeDetails.getAssociations());
		freezeAspects(nodeDetails, versionNodeRef, nodeDetails.getAspects());
		
		// Return the created node reference
		return versionNodeRef;
    }
    
	/**
	 * 
	 * @param nodeDetails
	 * @param versionNodeRef
	 * @param aspects
	 */
    private void freezeAspects(PolicyScope nodeDetails, NodeRef versionNodeRef, Set<QName> aspects) 
	{
		for (QName aspect : aspects) 
		{
			// Freeze the details of the aspect
			freezeProperties(versionNodeRef, nodeDetails.getProperties(aspect));
			freezeChildAssociations(versionNodeRef, nodeDetails.getChildAssociations(aspect));
			freezeAssociations(versionNodeRef, nodeDetails.getAssociations(aspect));
		}
	}

	/**
	 * 
	 * @param versionNodeRef
	 * @param associations
	 */
	private void freezeAssociations(NodeRef versionNodeRef, Map<QName, NodeAssocRef> associations) 
	{
		for (NodeAssocRef targetAssoc : associations.values())
        {
            HashMap<QName, Serializable> properties = new HashMap<QName, Serializable>();
            
            // Set the qname of the association
            properties.put(PROP_QNAME_ASSOC_TYPE_QNAME, targetAssoc.getTypeQName());
            
            // Need to determine whether the target is versioned or not
            NodeRef versionHistoryRef = getVersionHistoryNodeRef(targetAssoc.getTargetRef());
            if (versionHistoryRef == null)
            {
                // Set the reference property to point to the child node
                properties.put(DictionaryBootstrap.PROP_QNAME_REFERENCE, targetAssoc.getTargetRef());
            }
            else
            {
                // Set the reference property to point to the version history
                properties.put(DictionaryBootstrap.PROP_QNAME_REFERENCE, versionHistoryRef);
            }
            
            // Create child version reference
            ChildAssocRef newRef = this.dbNodeService.createNode(
                    versionNodeRef,
					CHILD_QNAME_VERSIONED_ASSOCS, 
                    CHILD_QNAME_VERSIONED_ASSOCS, 
                    TYPE_QNAME_VERSIONED_ASSOC,
                    properties);
        }
	}

	/**
	 * 
	 * @param versionNodeRef
	 * @param childAssociations
	 */
	private void freezeChildAssociations(NodeRef versionNodeRef, Map<QName, ChildAssocRef> childAssociations) 
	{
		for (Map.Entry<QName, ChildAssocRef> entry : childAssociations.entrySet()) 
		{
			ChildAssocRef childAssocRef = entry.getValue();
			HashMap<QName, Serializable> properties = new HashMap<QName, Serializable>();
            
            // Set the qname, isPrimary and nthSibling properties
            properties.put(PROP_QNAME_ASSOC_QNAME, childAssocRef.getQName());
            properties.put(PROP_QNAME_ASSOC_TYPE_QNAME, childAssocRef.getTypeQName());
            properties.put(PROP_QNAME_IS_PRIMARY, Boolean.valueOf(childAssocRef.isPrimary()));
            properties.put(PROP_QNAME_NTH_SIBLING, Integer.valueOf(childAssocRef.getNthSibling()));
            
            // Need to determine whether the child is versioned or not
            NodeRef versionHistoryRef = getVersionHistoryNodeRef(childAssocRef.getChildRef());
            if (versionHistoryRef == null)
            {
                // Set the reference property to point to the child node
                properties.put(DictionaryBootstrap.PROP_QNAME_REFERENCE, childAssocRef.getChildRef());
            }
            else
            {
                // Set the reference property to point to the version history
                properties.put(DictionaryBootstrap.PROP_QNAME_REFERENCE, versionHistoryRef);
            }
            
            // Create child version reference
            ChildAssocRef newRef = this.dbNodeService.createNode(
                    versionNodeRef,
					CHILD_QNAME_VERSIONED_CHILD_ASSOCS,
                    CHILD_QNAME_VERSIONED_CHILD_ASSOCS, 
                    TYPE_QNAME_VERSIONED_CHILD_ASSOC,
                    properties);
		}
	}

	/**
	 * 
	 * @param versionNodeRef
	 * @param properties
	 */
	private void freezeProperties(NodeRef versionNodeRef, Map<QName, Serializable> properties) 
	{
		// Copy the property values from the node onto the version node
        for (Map.Entry<QName, Serializable> entry : properties.entrySet())
        {                               
            // Get the property values
            HashMap<QName, Serializable> props = new HashMap<QName, Serializable>();
			props.put(PROP_QNAME_QNAME, entry.getKey());
			props.put(PROP_QNAME_VALUE, entry.getValue());
            
            // Create the node storing the frozen attribute details
            this.dbNodeService.createNode(
                    versionNodeRef, 
					CHILD_QNAME_VERSIONED_ATTRIBUTES,
                    CHILD_QNAME_VERSIONED_ATTRIBUTES,
                    TYPE_QNAME_VERSIONED_PROPERTY,
                    props);                
        }
	}  		
	
	/**
	 * Gets the version stores root node
	 * 
	 * @return the node ref to the root node of the version store
	 */
	private NodeRef getRootNode() 
	{
		// Get the version store root node reference
        return this.dbNodeService.getRootNode(getVersionStoreReference());
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
    private VersionHistory buildVersionHistory(NodeRef versionHistoryRef, NodeRef nodeRef)
    {
        VersionHistory versionHistory = null;
        
        ArrayList<NodeRef> versionHistoryNodeRefs = new ArrayList<NodeRef>();
        NodeRef currentVersion = getCurrentVersionNodeRef(versionHistoryRef, nodeRef);
        
        while (currentVersion != null)
        {
            NodeAssocRef preceedingVersion = null;
            
            versionHistoryNodeRefs.add(0, currentVersion);
            
            List<NodeAssocRef> preceedingVersions = this.dbNodeService.getSourceAssocs(
																				currentVersion, 
																				VersionStoreConst.ASSOC_SUCCESSOR);
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
    private Version getVersion(NodeRef versionRef)
    {
        // TODO could definatly do with a cache since these are read only objects ...
        
		Version result = null;
		
		if (versionRef != null)
		{
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
	        result = new VersionImpl(versionProperties, versionRef);   
		}
		
		return result;
    }
    
    /**
     * Gets a reference to the version history node for a given 'real' node.
     * 
     * @param nodeRef  a node reference
     * @return         a reference to the version history node, null of none
     */
    private NodeRef getVersionHistoryNodeRef(NodeRef nodeRef)
    {
        NodeRef result = null;
        
        // TODO use the sercher to retrieve the version history node
        
        Collection<ChildAssocRef> versionHistories = this.dbNodeService.getChildAssocs(getRootNode());
        for (ChildAssocRef versionHistory : versionHistories)
        {
            String nodeId = (String)this.dbNodeService.getProperty(versionHistory.getChildRef(), VersionStoreConst.PROP_QNAME_VERSIONED_NODE_ID);
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
    private NodeRef getCurrentVersionNodeRef(NodeRef versionHistory, NodeRef nodeRef)
    {
        // TODO use the searcher to retrieve the version node
        
        NodeRef result = null;
        String versionLabel = (String)this.nodeService.getProperty(nodeRef, DictionaryBootstrap.PROP_QNAME_CURRENT_VERSION_LABEL);
        
        Collection<ChildAssocRef> versions = this.dbNodeService.getChildAssocs(versionHistory);
        for (ChildAssocRef version : versions)
        {
            String tempLabel = (String)this.dbNodeService.getProperty(version.getChildRef(), VersionStoreConst.PROP_QNAME_VERSION_LABEL);
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
    private void checkForVersionAspect(NodeRef nodeRef)
       throws AspectMissingException
    {
        QName aspectRef = DictionaryBootstrap.ASPECT_QNAME_VERSIONABLE;
        
        if (this.nodeService.hasAspect(nodeRef, aspectRef) == false)
        {
            // Raise exception to indicate version aspect is not present
            throw new AspectMissingException(aspectRef, nodeRef);
        }
    }
}
