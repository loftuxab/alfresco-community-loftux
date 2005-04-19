/**
 * Created on Apr 5, 2005
 */
package com.activiti.repo.version.lightweight;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.activiti.repo.dictionary.ClassRef;
import com.activiti.repo.node.AssociationExistsException;
import com.activiti.repo.node.InvalidAspectException;
import com.activiti.repo.node.InvalidNodeRefException;
import com.activiti.repo.node.NodeService;
import com.activiti.repo.node.PropertyException;
import com.activiti.repo.ref.ChildAssocRef;
import com.activiti.repo.ref.EntityRef;
import com.activiti.repo.ref.NodeRef;
import com.activiti.repo.ref.Path;
import com.activiti.repo.ref.QName;
import com.activiti.repo.ref.StoreRef;

/**
 * THe light weight version store node service implementation.
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
     * @throws UnsupportedOperationException always
     */
    public ClassRef getType(NodeRef nodeRef) throws InvalidNodeRefException
    {
		// TODO
        throw new UnsupportedOperationException();
    }
    
    public void addAspect(NodeRef nodeRef, ClassRef aspectRef, Map<QName, Serializable> aspectProperties) throws InvalidNodeRefException, InvalidAspectException, PropertyException
    {
        throw new UnsupportedOperationException(MSG_UNSUPPORTED);
    }

    public boolean hasAspect(NodeRef nodeRef, ClassRef aspectRef) throws InvalidNodeRefException, InvalidAspectException
    {
        throw new UnsupportedOperationException(MSG_UNSUPPORTED);
    }

    public void removeAspect(NodeRef nodeRef, ClassRef aspectRef) throws InvalidNodeRefException, InvalidAspectException
    {
        throw new UnsupportedOperationException(MSG_UNSUPPORTED);
    }

    public Set<ClassRef> getAspects(NodeRef nodeRef) throws InvalidNodeRefException
    {
        throw new UnsupportedOperationException(MSG_UNSUPPORTED);
    }

    /**
     * Property translation for version store
     */
    public Map<QName, Serializable> getProperties(NodeRef nodeRef) throws InvalidNodeRefException
    {
		Map<QName, Serializable> result = new HashMap<QName, Serializable>();
		Map<QName, Serializable> versionNodeProperties = this.dbNodeService.getProperties(nodeRef);
		for (QName key : versionNodeProperties.keySet())
        {
			// Do not return the version store properties
            if (VersionStoreBaseImpl.LW_VERSION_STORE_NAMESPACE.equals(key.getNamespaceURI()) == false)
			{
				result.put(key, versionNodeProperties.get(key));
			}
        }
		return result;
    }
    
    /**
     * Property translation for version store
     */
    public Serializable getProperty(NodeRef nodeRef, QName qname) throws InvalidNodeRefException
    {
        Serializable result = null;
		
		// Ignore propreties that relate to the version store
		if (VersionStoreBaseImpl.LW_VERSION_STORE_NAMESPACE.equals(qname.getNamespaceURI()) == false)
		{
			result = this.dbNodeService.getProperty(nodeRef, qname);
		}
		
		return result;
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
    public Collection<NodeRef> getParents(NodeRef nodeRef) throws InvalidNodeRefException
    {
        // This operation is not supported for a verion store
        throw new UnsupportedOperationException(MSG_UNSUPPORTED);
    }
    
    /**
     * @throws UnsupportedOperationException always
     */
    public Collection<ChildAssocRef> getChildAssocs(NodeRef nodeRef) throws InvalidNodeRefException
    {
        // TODO
        throw new UnsupportedOperationException();
    }
    
    /**
     * @throws UnsupportedOperationException always
     */
    public NodeRef getPrimaryParent(NodeRef nodeRef) throws InvalidNodeRefException
    {
        // This operation is not supported for a verion store
        throw new UnsupportedOperationException(MSG_UNSUPPORTED);
    }
    
    /**
     * @throws UnsupportedOperationException always
     */
    public void createAssociation(NodeRef sourceRef, NodeRef targetRef, QName qname)
            throws InvalidNodeRefException, AssociationExistsException
    {
        // This operation is not supported for a verion store
        throw new UnsupportedOperationException(MSG_UNSUPPORTED);
    }
    
    /**
     * @throws UnsupportedOperationException always
     */
    public void removeAssociation(NodeRef sourceRef, NodeRef targetRef, QName qname)
            throws InvalidNodeRefException
    {
        // This operation is not supported for a verion store
        throw new UnsupportedOperationException(MSG_UNSUPPORTED);
    }
    
    /**
     * @throws UnsupportedOperationException always
     */
    public Collection<NodeRef> getAssociationTargets(NodeRef sourceRef, QName qname)
            throws InvalidNodeRefException
    {
        // TODO 
        throw new UnsupportedOperationException();
    }
    
    /**
     * @throws UnsupportedOperationException always
     */
    public Collection<NodeRef> getAssociationSources(NodeRef targetRef, QName qname)
            throws InvalidNodeRefException
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
