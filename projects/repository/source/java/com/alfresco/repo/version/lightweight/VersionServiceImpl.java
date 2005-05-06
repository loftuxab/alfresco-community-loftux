package org.alfresco.repo.version.lightweight;

import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.alfresco.repo.content.ContentReader;
import org.alfresco.repo.content.ContentService;
import org.alfresco.repo.content.ContentStore;
import org.alfresco.repo.content.ContentWriter;
import org.alfresco.repo.dictionary.ClassRef;
import org.alfresco.repo.dictionary.bootstrap.DictionaryBootstrap;
import org.alfresco.repo.policy.PolicyDefinitionService;
import org.alfresco.repo.policy.PolicyRuntimeService;
import org.alfresco.repo.ref.ChildAssocRef;
import org.alfresco.repo.ref.NodeAssocRef;
import org.alfresco.repo.ref.NodeRef;
import org.alfresco.repo.ref.QName;
import org.alfresco.repo.ref.qname.RegexQNamePattern;
import org.alfresco.repo.version.ReservedVersionNameException;
import org.alfresco.repo.version.Version;
import org.alfresco.repo.version.VersionHistory;
import org.alfresco.repo.version.VersionLabelPolicy;
import org.alfresco.repo.version.VersionService;
import org.alfresco.repo.version.VersionServiceException;
import org.alfresco.repo.version.common.VersionUtil;
import org.alfresco.repo.version.common.counter.VersionCounterDaoService;
import org.alfresco.repo.version.policy.OnBeforeCreateVersionPolicy;
import org.alfresco.util.AspectMissingException;

/**
 * The light weight version service implementation.
 * 
 * @author Roy Wetheral
 */
public class VersionServiceImpl extends BaseImpl implements VersionService
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
     * The version label policy
     */
    private VersionLabelPolicy versionLabelPolicy;
    
    /**
     * Policy definition service
     */
    private PolicyDefinitionService policyDefinitionService;
    
    /**
     * Policy runtime service
     */
    private PolicyRuntimeService policyRuntimeService;
    
    /**
     * The generic content service
     */
    private ContentService contentService;
    
    /**
     * The version store content store
     */
    private ContentStore versionContentStore;
    
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
     * Sets the policy defintion service
     * 
     * @param policyDefintionService  the policy definition service
     */
    public void setPolicyDefinitionService(
            PolicyDefinitionService policyDefinitionService)
    {
        this.policyDefinitionService = policyDefinitionService;
    }
    
    /**
     * Sets the policy runtime service
     * 
     * @param policyRuntimeService  the policy runtime service
     */
    public void setPolicyRuntimeService(
            PolicyRuntimeService policyRuntimeService)
    {
        this.policyRuntimeService = policyRuntimeService;
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
    
    @Override
    public void initialise()
    {
        super.initialise();
        
        // Register the policies
        this.policyDefinitionService.registerPolicy(this, OnBeforeCreateVersionPolicy.class);
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
            Map<String, Serializable> versionProperties, 
            int versionNumber)
            throws ReservedVersionNameException, AspectMissingException
    {

        // Check for the version aspect
        checkForVersionAspect(nodeRef);
        
        // Call the onBeforeCreateVersionPolicy 
        OnBeforeCreateVersionPolicy policy = this.policyRuntimeService.getClassBehaviour(
                OnBeforeCreateVersionPolicy.class, 
                this.nodeService,
                nodeRef);
        if (policy != null)
        {
            policy.OnBeforeCreateVersion(nodeRef);
        }
        
        // TODO we need some way of 'locking' the current node to ensure no modifications (or other versions) 
        //      can take place untill the versioning process is complete
        
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
                    this.versionStoreRootNodeRef, 
                    CHILD_QNAME_VERSION_HISTORIES, 
                    CLASS_REF_VERSION_HISTORY,
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
        
        // Create the new version node (child of the version history)
        NodeRef newVersionRef = createNewVersion(
                nodeRef, 
                versionHistoryRef,
                currentVersionRef, 
                versionProperties, 
                versionNumber);
        
        // 'Freeze' the current nodes state in the new version node
        freezeNodeState(nodeRef, newVersionRef);
        
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
        this.dbNodeService.setProperty(
                nodeRef, 
                VersionService.PROP_QNAME_CURRENT_VERSION_LABEL, 
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
			Map<String, Serializable> 
			versionProperties, 
			int versionNumber)
    {
        HashMap<QName, Serializable> props = new HashMap<QName, Serializable>(15, 1.0f);
        
        // Set the version number for the new version
        props.put(PROP_QNAME_VERSION_NUMBER, Integer.toString(versionNumber));
        
        // Set the created date
        props.put(PROP_QNAME_VERSION_CREATED_DATE, new Date());
		
		// Set the versionable node id
		props.put(PROP_QNAME_FROZEN_NODE_ID, versionableNodeRef.getId());
		
		// Set the versionable node store protocol
		props.put(PROP_QNAME_FROZEN_NODE_STORE_PROTOCOL, versionableNodeRef.getStoreRef().getProtocol());
		
		// Set the versionable node store id
		props.put(PROP_QNAME_FROZEN_NODE_STORE_ID, versionableNodeRef.getStoreRef().getIdentifier());
        
        // Store the current node type
        ClassRef nodeType = this.nodeService.getType(versionableNodeRef);
        props.put(PROP_QNAME_FROZEN_NODE_TYPE, nodeType);
        
        // Store the current aspects
        Set<ClassRef> aspects = this.nodeService.getAspects(versionableNodeRef);
		props.put(PROP_QNAME_FROZEN_ASPECTS, (Serializable)aspects);
        
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
        props.put(PROP_QNAME_VERSION_LABEL, versionLabel);
        
        // TODO any other calculated properties ...
        
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
                BaseImpl.CHILD_QNAME_VERSIONS,
                CLASS_REF_VERSION,
                props);
        return childAssocRef.getChildRef();
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
        // Copy the current values of the node onto the version node, thus taking a snap shot of the values
        Map<QName, Serializable> nodeProperties = this.nodeService.getProperties(nodeRef);
        if (nodeProperties != null)
        {
            // Copy the property values from the node onto the version node
            for (QName propertyName : nodeProperties.keySet())
            {                               
                // Get the property values
                HashMap<QName, Serializable> properties = new HashMap<QName, Serializable>();
                properties.put(PROP_QNAME_QNAME, propertyName);
                properties.put(PROP_QNAME_VALUE, nodeProperties.get(propertyName));
                
                // Create the node storing the frozen attribute details
                this.dbNodeService.createNode(
                        versionRef, 
                        CHILD_QNAME_VERSIONED_ATTRIBUTES,
                        CLASS_REF_VERSIONED_PROPERTY,
                        properties);                
            }
        }
        
        // Check to see if the node being frozen has the content aspect applied
        if (this.nodeService.hasAspect(nodeRef, DictionaryBootstrap.ASPECT_CONTENT) == true)
        {
            // Get the details of the content from the verionable node
            Serializable mimeType = this.nodeService.getProperty(nodeRef, DictionaryBootstrap.PROP_QNAME_MIME_TYPE);
            Serializable encoding = this.nodeService.getProperty(nodeRef, DictionaryBootstrap.PROP_QNAME_ENCODING);
            
            // Indroduce the content aspect to the version node
            Map<QName, Serializable> properties = this.dbNodeService.getProperties(versionRef);
            properties.put(DictionaryBootstrap.PROP_QNAME_MIME_TYPE, mimeType);
            properties.put(DictionaryBootstrap.PROP_QNAME_ENCODING, encoding);
            this.dbNodeService.addAspect(versionRef, DictionaryBootstrap.ASPECT_CONTENT, properties);
            
            // Get the content from the node
            ContentReader contentReader = this.contentService.getReader(nodeRef);
            if (contentReader != null)
            {
                // Get the content writer for the frozen version
                ContentWriter contentWriter = this.versionContentStore.getWriter(versionRef);
                if (contentWriter != null)
                {
                    // Copy the content to the version node
                    InputStream is = contentReader.getContentInputStream();
                    contentWriter.putContent(is);     
                    
                    // Set the content URL
                    String contentUrl = contentWriter.getContentUrl();
                    this.dbNodeService.setProperty(
                            versionRef, 
                            DictionaryBootstrap.PROP_QNAME_CONTENT_URL, 
                            contentUrl);
                }
            }            
        }            
        
        // TODO the following behaviour is default and should overrideable (ie: can choose when to ignore, version or 
        //      reference children) how do we do this?       
        
        // Get the children of the versioned node
        Collection<ChildAssocRef> childAssocRefs = this.nodeService.getChildAssocs(nodeRef);
        for (ChildAssocRef childAssocRef : childAssocRefs)
        {
            HashMap<QName, Serializable> properties = new HashMap<QName, Serializable>();
            
            // Set the qname, isPrimary and nthSibling properties
            properties.put(PROP_QNAME_ASSOC_QNAME, childAssocRef.getQName());
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
                    versionRef,
                    CHILD_QNAME_VERSIONED_CHILD_ASSOCS,
                    CLASS_REF_VERSIONED_CHILD_ASSOC, 
                    properties);
        }
        
        // Version the target assocs
        List<NodeAssocRef> targetAssocs = this.nodeService.getTargetAssocs(nodeRef, RegexQNamePattern.MATCH_ALL);
        for (NodeAssocRef targetAssoc : targetAssocs)
        {
            HashMap<QName, Serializable> properties = new HashMap<QName, Serializable>();
            
            // Set the qname of the association
            properties.put(PROP_QNAME_ASSOC_QNAME, targetAssoc.getQName());
            
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
                    versionRef,
                    CHILD_QNAME_VERSIONED_ASSOCS, 
                    CLASS_REF_VERSIONED_ASSOC, 
                    properties);
        }
    }    
}
