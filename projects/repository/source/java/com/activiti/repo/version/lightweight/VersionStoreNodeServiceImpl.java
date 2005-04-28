/**
 * Created on Apr 5, 2005
 */
package com.activiti.repo.version.lightweight;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.activiti.repo.dictionary.ClassRef;
import com.activiti.repo.dictionary.bootstrap.DictionaryBootstrap;
import com.activiti.repo.node.AssociationExistsException;
import com.activiti.repo.node.InvalidAspectException;
import com.activiti.repo.node.InvalidNodeRefException;
import com.activiti.repo.node.NodeService;
import com.activiti.repo.node.PropertyException;
import com.activiti.repo.ref.ChildAssocRef;
import com.activiti.repo.ref.EntityRef;
import com.activiti.repo.ref.NodeAssocRef;
import com.activiti.repo.ref.NodeRef;
import com.activiti.repo.ref.Path;
import com.activiti.repo.ref.QName;
import com.activiti.repo.ref.StoreRef;
import com.activiti.repo.ref.qname.QNamePattern;
import com.activiti.repo.ref.qname.RegexQNamePattern;

/**
 * The light weight version store node service implementation.
 * 
 * @author Roy Wetherall
 */
public class VersionStoreNodeServiceImpl extends VersionStoreBaseImpl implements NodeService 
{
    /**
     * Error messages
     */
    private final static String MSG_UNSUPPORTED = 
        "This operation is not supported by a version store implementation of the node service.";
	
    /**
     * Delegates to the <code>NodeService</code> used as the version store implementation
     */
    public StoreRef createStore(String protocol, String identifier)
    {
        return dbNodeService.createStore(protocol, identifier);
    }

    /**
     * Delegates to the <code>NodeService</code> used as the version store implementation
     */
    public boolean exists(StoreRef storeRef)
    {
        return dbNodeService.exists(storeRef);
    }

    /**
     * Delegates to the <code>NodeService</code> used as the version store implementation
     */
    public boolean exists(NodeRef nodeRef)
    {
        return dbNodeService.exists(nodeRef);
    }

    /**
     * Delegates to the <code>NodeService</code> used as the version store implementation
     */
    public NodeRef getRootNode(StoreRef storeRef)
    {
        return dbNodeService.getRootNode(storeRef);
    }

    /**
     * @throws UnsupportedOperationException always
     */
    public ChildAssocRef createNode(
			NodeRef parentRef,
            QName qname,
            ClassRef tyepRef) throws InvalidNodeRefException
    {
        // This operation is not supported for a verion store
        throw new UnsupportedOperationException(MSG_UNSUPPORTED);
    }
    
    /**
     * @throws UnsupportedOperationException always
     */
    public ChildAssocRef createNode(
			NodeRef parentRef,
            QName qname,
            ClassRef typeRef,
            Map<QName, Serializable> properties) throws InvalidNodeRefException
    {
        // This operation is not supported for a verion store
        throw new UnsupportedOperationException(MSG_UNSUPPORTED);
    }
    
    /**
     * @throws UnsupportedOperationException always
     */
    public void deleteNode(NodeRef nodeRef) throws InvalidNodeRefException
    {
        // This operation is not supported for a verion store
        throw new UnsupportedOperationException(MSG_UNSUPPORTED);
    }
    
    /**
     * @throws UnsupportedOperationException always
     */
    public ChildAssocRef addChild(NodeRef parentRef,
            NodeRef childRef,
            QName qname) throws InvalidNodeRefException
    {
        // This operation is not supported for a verion store
        throw new UnsupportedOperationException(MSG_UNSUPPORTED);
    }
    
    /**
     * @throws UnsupportedOperationException always
     */
    public Collection<EntityRef> removeChild(NodeRef parentRef, NodeRef childRef) throws InvalidNodeRefException
    {
        // This operation is not supported for a verion store
        throw new UnsupportedOperationException(MSG_UNSUPPORTED);
    }

    /**
     * @throws UnsupportedOperationException always
     */
    public Collection<EntityRef>  removeChildren(NodeRef parentRef, QName qname) throws InvalidNodeRefException
    {
        // This operation is not supported for a verion store
        throw new UnsupportedOperationException(MSG_UNSUPPORTED);
    }
    
    /**
     * Type translation for version store
     */
    public ClassRef getType(NodeRef nodeRef) throws InvalidNodeRefException
    {
		return (ClassRef)this.dbNodeService.getProperty(nodeRef, VersionStoreBaseImpl.PROP_QNAME_FROZEN_NODE_TYPE);
    }
    
    /**
     * @throws UnsupportedOperationException always
     */
    public void addAspect(NodeRef nodeRef, ClassRef aspectRef, Map<QName, Serializable> aspectProperties) throws InvalidNodeRefException, InvalidAspectException, PropertyException
    {
        // This operation is not supported for a verion store
        throw new UnsupportedOperationException(MSG_UNSUPPORTED);
    }

    /**
     * Translation for version store
     */
    public boolean hasAspect(NodeRef nodeRef, ClassRef aspectRef) throws InvalidNodeRefException, InvalidAspectException
    {
        Set<ClassRef> aspects = (Set<ClassRef>)this.dbNodeService.getProperty(nodeRef, PROP_QNAME_FROZEN_ASPECTS);
        return aspects.contains(aspectRef);
    }

    /**
     * @throws UnsupportedOperationException always
     */
    public void removeAspect(NodeRef nodeRef, ClassRef aspectRef) throws InvalidNodeRefException, InvalidAspectException
    {
        // This operation is not supported for a verion store
        throw new UnsupportedOperationException(MSG_UNSUPPORTED);
    }

    /**
     * Translation for version store
     */
    public Set<ClassRef> getAspects(NodeRef nodeRef) throws InvalidNodeRefException
    {
        return (Set<ClassRef>)this.dbNodeService.getProperty(nodeRef, PROP_QNAME_FROZEN_ASPECTS);
    }

    /**
     * Property translation for version store
     */
    public Map<QName, Serializable> getProperties(NodeRef nodeRef) throws InvalidNodeRefException
    {
		Map<QName, Serializable> result = new HashMap<QName, Serializable>();
		
        // TODO should be doing this using a path query ..
        
        Collection<ChildAssocRef> children = this.dbNodeService.getChildAssocs(nodeRef);
        for (ChildAssocRef child : children)
        {
            if (child.getQName().equals(CHILD_QNAME_VERSIONED_ATTRIBUTES))
            {
                NodeRef versionedAttribute = child.getChildRef();
                
                // Get the QName and the value
                QName qName = (QName)this.dbNodeService.getProperty(versionedAttribute, PROP_QNAME_QNAME);
                Serializable value = this.dbNodeService.getProperty(versionedAttribute, PROP_QNAME_VALUE);
                
                result.put(qName, value);
            }
        }        
        
		return result;
    }
    
    /**
     * Property translation for version store
     */
    public Serializable getProperty(NodeRef nodeRef, QName qname) throws InvalidNodeRefException
    {        
        // TODO should be doing this with a search ...
        
        Map<QName, Serializable> properties = getProperties(nodeRef);
        return properties.get(qname);			
    }
    
    /**
     * @throws UnsupportedOperationException always
     */
    public void setProperties(NodeRef nodeRef, Map<QName, Serializable> properties) throws InvalidNodeRefException
    {
        // This operation is not supported for a verion store
        throw new UnsupportedOperationException(MSG_UNSUPPORTED);
    }
    
    /**
     * @throws UnsupportedOperationException always
     */
    public void setProperty(NodeRef nodeRef, QName qame, Serializable value) throws InvalidNodeRefException
    {
        // This operation is not supported for a verion store
        throw new UnsupportedOperationException(MSG_UNSUPPORTED);
    }
    
    /**
     * @throws UnsupportedOperationException always
     */
    public List<ChildAssocRef> getParentAssocs(NodeRef nodeRef)
    {
        // This operation is not supported for a verion store
        throw new UnsupportedOperationException(MSG_UNSUPPORTED);
    }

    /**
     * @see NodeService#getChildAssocs(NodeRef)
     */
    public List<ChildAssocRef> getParentAssocs(NodeRef nodeRef, QNamePattern qnamePattern)
    {
        // This operation is not supported for a verion store
        throw new UnsupportedOperationException(MSG_UNSUPPORTED);
    }

    /**
     * @see RegexQNamePattern#MATCH_ALL
     * @see #getChildAssocs(NodeRef, QNamePattern)
     */
    public List<ChildAssocRef> getChildAssocs(NodeRef nodeRef) throws InvalidNodeRefException
    {
        return getChildAssocs(nodeRef, RegexQNamePattern.MATCH_ALL);
    }

    /**
     * Performs conversion from version store properties to <i>real</i> associations
     */
    public List<ChildAssocRef> getChildAssocs(NodeRef nodeRef, QNamePattern qnamePattern) throws InvalidNodeRefException
    {
        List<ChildAssocRef> result = new ArrayList<ChildAssocRef>();
        
        // Get the child assocs from the version store
        List<ChildAssocRef> childAssocRefs = this.dbNodeService.getChildAssocs(
                nodeRef,
                CHILD_QNAME_VERSIONED_CHILD_ASSOCS);
        for (ChildAssocRef childAssocRef : childAssocRefs)
        {
            // Get the child reference
            NodeRef childRef = childAssocRef.getChildRef();
            NodeRef referencedNode = (NodeRef)this.dbNodeService.getProperty(childRef, DictionaryBootstrap.PROP_QNAME_REFERENCE); 
            
            // get the qualified name of the frozen child association and filter out unwanted names
            QName qName = (QName)this.dbNodeService.getProperty(childRef, PROP_QNAME_ASSOC_QNAME);
            
            if (qnamePattern.isMatch(qName) == true)
            {
                //if (childAssocRef.getQName().equals(CHILD_QNAME_VERSIONED_CHILD_ASSOCS))
                //{
                    // Get the child reference
                    //NodeRef childRef = childAssocRef.getChildRef();
                   //NodeRef referencedNode = (NodeRef)this.dbNodeService.getProperty(childRef, DictionaryBootstrap.PROP_QNAME_REFERENCE); 
                    
                    // Check to see if the versioned node is a version history
                    ClassRef classRef = this.dbNodeService.getType(referencedNode);
                    if (CLASS_REF_VERSION_HISTORY.equals(classRef) == true)
                    {
                        // TODO if the referenced node is a version history then need to get the appropriate node ref                                                
                    }
                    
                    // Retrieve the isPrimary and nthSibling values of the forzen child association
                    //QName qName = (QName)this.dbNodeService.getProperty(childRef, PROP_QNAME_QNAME);
                    boolean isPrimary = ((Boolean)this.dbNodeService.getProperty(childRef, PROP_QNAME_IS_PRIMARY)).booleanValue();
                    int nthSibling = ((Integer)this.dbNodeService.getProperty(childRef, PROP_QNAME_NTH_SIBLING)).intValue();
                    
                    // Build a child assoc ref to add to the returned list
                    ChildAssocRef newChildAssocRef = new ChildAssocRef(
                            nodeRef, 
                            qName, 
                            referencedNode, 
                            isPrimary, 
                            nthSibling);
                    result.add(newChildAssocRef);
                }
                //continue;   // this was not a match
           // }
            // Retrieve the isPrimary and nthSibling values of the frozen child association
           // boolean isPrimary = ((Boolean)this.dbNodeService.getProperty(childRef, PROP_QNAME_IS_PRIMARY)).booleanValue();
           // int nthSibling = ((Integer)this.dbNodeService.getProperty(childRef, PROP_QNAME_NTH_SIBLING)).intValue();
            
            // Build a child assoc ref to add to the returned list
           // ChildAssocRef newChildAssocRef = new ChildAssocRef(
           //         nodeRef, 
           //         qName, 
            //        referencedNode, 
            //        isPrimary, 
            //        nthSibling);
            //result.add(newChildAssocRef);
        }
        
        return result;
    }
    
    /**
     * @throws UnsupportedOperationException always
     */
    public ChildAssocRef getPrimaryParent(NodeRef nodeRef) throws InvalidNodeRefException
    {
        // This operation is not supported for a verion store
        throw new UnsupportedOperationException(MSG_UNSUPPORTED);
    }
    
    /**
     * @throws UnsupportedOperationException always
     */
    public NodeAssocRef createAssociation(NodeRef sourceRef, NodeRef targetRef, QName qname)
            throws InvalidNodeRefException, AssociationExistsException
    {
        // This operation is not supported for a verion store
        throw new UnsupportedOperationException(MSG_UNSUPPORTED);
    }
    
    /**
     * @throws UnsupportedOperationException always
     */
    public void removeAssociation(NodeRef sourceRef, NodeRef targetRef, QName qname)
    {
        // This operation is not supported for a verion store
        throw new UnsupportedOperationException(MSG_UNSUPPORTED);
    }
    
    /**
     * @throws UnsupportedOperationException always
     */
    public List<NodeAssocRef> getTargetAssocs(NodeRef sourceRef, QNamePattern qnamePattern)
    {
        // TODO in order to do this we need to be able to get a list of the
        //      names of the target associations
        
        // TODO need to fill in the implementation here 
        throw new UnsupportedOperationException();        
    }
    
    /**
     * @throws UnsupportedOperationException always
     */
    public List<NodeAssocRef> getSourceAssocs(NodeRef sourceRef, QNamePattern qnamePattern)
    {
        // This operation is not supported for a verion store
        throw new UnsupportedOperationException(MSG_UNSUPPORTED);
    }
    
    /**
     * @throws UnsupportedOperationException always
     */
    public Path getPath(NodeRef nodeRef) throws InvalidNodeRefException
    {
        // This operation is not supported for a verion store
        throw new UnsupportedOperationException(MSG_UNSUPPORTED);
    }
    
    /**
     * @throws UnsupportedOperationException always
     */
    public Collection<Path> getPaths(NodeRef nodeRef, boolean primaryOnly) throws InvalidNodeRefException
    {
        // This operation is not supported for a verion store
        throw new UnsupportedOperationException(MSG_UNSUPPORTED);
    }
}
