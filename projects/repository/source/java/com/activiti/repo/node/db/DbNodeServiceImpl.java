package com.activiti.repo.node.db;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.dao.DataIntegrityViolationException;

import com.activiti.repo.domain.ChildAssoc;
import com.activiti.repo.domain.ContainerNode;
import com.activiti.repo.domain.Node;
import com.activiti.repo.domain.NodeKey;
import com.activiti.repo.domain.RealNode;
import com.activiti.repo.domain.Store;
import com.activiti.repo.node.AssociationExistsException;
import com.activiti.repo.node.InvalidNodeRefException;
import com.activiti.repo.node.NodeService;
import com.activiti.repo.ref.NodeRef;
import com.activiti.repo.ref.StoreRef;
import com.activiti.repo.store.db.StoreDaoService;

/**
 * Node service using database persistence layer to fulfill functionality
 * 
 * @author Derek Hulley
 */
public class DbNodeServiceImpl implements NodeService
{
    private static final Log logger = LogFactory.getLog(DbNodeServiceImpl.class);
    
    private NodeDaoService nodeDaoService;
    private StoreDaoService storeDaoService;

    public void setNodeDaoService(NodeDaoService nodeDaoService)
    {
        this.nodeDaoService = nodeDaoService;
    }

    public void setStoreDaoService(StoreDaoService storeDaoService)
    {
        this.storeDaoService = storeDaoService;
    }

    public NodeRef createNode(NodeRef parentRef, String name, String nodeType)
    {
        return this.createNode(parentRef, name, nodeType, null);
    }

    public NodeRef createNode(NodeRef parentRef, String name, String nodeType, Map<String, String> properties)
    {
        if (logger.isDebugEnabled())
        {
            logger.debug("Creating node: \n" +
                    "   parent: " + parentRef + "\n" +
                    "   name: " + name + "\n" +
                    "   nodeType: " + nodeType);
        }
        // get the store that the parent belongs to
        StoreRef storeRef = parentRef.getStoreRef();
        Store store = storeDaoService.getStore(storeRef.getProtocol(), storeRef.getIdentifier());
        if (store == null)
        {
            throw new DataIntegrityViolationException("No store found for parent node: " + parentRef);
        }
        // create the node instance
        RealNode node = nodeDaoService.newRealNode(store, nodeType);
        // get the parent node
        ContainerNode parentNode = getContainerNodeNotNull(parentRef);
        // create the association
        ChildAssoc assoc = nodeDaoService.newChildAssoc(parentNode, node, true, name);
        
        // attach the properties
        node.setProperties(properties);
        
        // done
        return node.getNodeRef();
    }

    /**
     * Performs a null- and type-safe check before returning the container node
     * @param nodeRef a reference to a container node
     * @return Returns an instance of a container node (never null)
     * @throws InvalidNodeRefException if the node referenced doesn't exist
     * @throws RuntimeException if the reference is to a node type that is incompatible with the return type
     */
    private ContainerNode getContainerNodeNotNull(NodeRef nodeRef) throws InvalidNodeRefException
    {
        Node unchecked = getNodeNotNull(nodeRef);
        if (!(unchecked instanceof ContainerNode))
        {
            throw new RuntimeException("Node must be of type " + Node.TYPE_CONTAINER + ": " + nodeRef);
        }
        return (ContainerNode) unchecked;
    }
    
    /**
     * Performs a null-safe get of the node
     * 
     * @param nodeRef the node to retrieve
     * @return Returns the node entity (never null)
     * @throws InvalidNodeRefException if the referenced node could not be found
     */
    private Node getNodeNotNull(NodeRef nodeRef) throws InvalidNodeRefException
    {
        String protocol = nodeRef.getStoreRef().getProtocol();
        String identifier = nodeRef.getStoreRef().getIdentifier();
        Node unchecked = nodeDaoService.getNode(protocol, identifier, nodeRef.getId());
        if (unchecked == null)
        {
            throw new InvalidNodeRefException("Node does not exist: " + nodeRef, nodeRef);
        }
        return unchecked;
    }

    public void deleteNode(NodeRef nodeRef)
    {
		// get the store
		StoreRef storeRef = nodeRef.getStoreRef();
        // get the node
        Node node = getNodeNotNull(nodeRef);
        // delete it
        nodeDaoService.deleteNode(node);
    }

    public void addChild(NodeRef parentRef, NodeRef childRef, String name) throws InvalidNodeRefException
    {
        // check that both nodes belong to the same store
        if (!parentRef.getStoreRef().equals(childRef.getStoreRef()))
        {
            throw new InvalidNodeRefException("Parent and child nodes must belong to the same store: \n" +
                    "   parent: " + parentRef + "\n" +
                    "   child: " + childRef,
                    childRef);
        }
        String protocol = parentRef.getStoreRef().getProtocol();
        String identifier = parentRef.getStoreRef().getIdentifier();
        // get the parent node and ensure that it is a container node
        ContainerNode parentNode = getContainerNodeNotNull(parentRef);
        // get the child node
        Node childNode = getNodeNotNull(childRef);
        // make the association
        nodeDaoService.newChildAssoc(parentNode, childNode, false, name);
        // done
    }

    public void removeChild(NodeRef parentRef, NodeRef childRef) throws InvalidNodeRefException
    {
        ContainerNode parentNode = getContainerNodeNotNull(parentRef);
        Node childNode = getNodeNotNull(childRef);
        NodeKey childNodeKey = childNode.getKey();
        // get all the child assocs
        boolean deleteChild = false;
        Set<ChildAssoc> assocs = parentNode.getChildAssocs();
        for (ChildAssoc assoc : assocs)
        {
            if (!assoc.getChild().getKey().equals(childNodeKey))
            {
                continue;  // not a matching association
            }
            // we have a match
            nodeDaoService.deleteChildAssoc(assoc);
            // is this a primary association?
            if (assoc.getIsPrimary())
            {
                deleteChild = true;
            }
        }
        // must the child be deleted?
        if (deleteChild)
        {
            nodeDaoService.deleteNode(childNode);
        }
        // done
        if (logger.isDebugEnabled())
        {
            logger.debug("Removed child associations: \n" +
                    "   parent: " + parentRef + "\n" +
                    "   child: " + childRef + "\n" +
                    "   count: " + assocs.size() + "\n" +
                    "   deleted child: " + deleteChild);
        }
    }

    public void removeChildren(NodeRef parentRef, String name) throws InvalidNodeRefException
    {
        ContainerNode parentNode = getContainerNodeNotNull(parentRef);
        // get all the child assocs
        Set<ChildAssoc> assocs = parentNode.getChildAssocs();
        int childrenDeleted = 0;
        for (ChildAssoc assoc : assocs)
        {
            if (!assoc.getName().equals(name))
            {
                continue;   // not a matching association
            }
            // we have a match
            nodeDaoService.deleteChildAssoc(assoc);
            // must we remove the child?
            if (assoc.getIsPrimary())
            {
                Node childNode = assoc.getChild();
                nodeDaoService.deleteNode(childNode);
                childrenDeleted++;
            }
        }
        // done
        if (logger.isDebugEnabled())
        {
            logger.debug("Removed child associations: \n" +
                    "   parent: " + parentRef + "\n" +
                    "   name: " + name + "\n" +
                    "   count: " + assocs.size() + "\n" +
                    "   deleted children: " + childrenDeleted);
        }
    }

    public String getType(NodeRef nodeRef) throws InvalidNodeRefException
    {
        Node node = getNodeNotNull(nodeRef);
        return node.getType();
    }

    public Map<String, String> getProperties(NodeRef nodeRef) throws InvalidNodeRefException
    {
        Node node = getNodeNotNull(nodeRef);
        return node.getProperties();
    }
    
    public void setProperties(NodeRef nodeRef, Map<String, String> properties) throws InvalidNodeRefException
    {
        Node node = getNodeNotNull(nodeRef);
        node.setProperties(properties);
    }

    public Collection<NodeRef> getParents(NodeRef nodeRef) throws InvalidNodeRefException
    {
        Node node = getNodeNotNull(nodeRef);
        // get the assocs pointing to it
        Set<ChildAssoc> parentAssocs = node.getParentAssocs();
        // list of results
        List<NodeRef> results = new ArrayList<NodeRef>(parentAssocs.size());
        for (ChildAssoc assoc : parentAssocs)
        {
            // get the parent
            Node parentNode = assoc.getParent();
            results.add(parentNode.getNodeRef());
        }
        // done
        if (logger.isDebugEnabled())
        {
            logger.debug("Fetched node parents: \n" +
                    "   node: " + nodeRef + "\n" +
                    "   parents: " + results);
        }
        return results;
    }

    public NodeRef getPrimaryParent(NodeRef nodeRef) throws InvalidNodeRefException
    {
        Node node = getNodeNotNull(nodeRef);
        // get the assocs pointing to it
        Set<ChildAssoc> parentAssocs = node.getParentAssocs();
        Node parentNode = null;
        for (ChildAssoc assoc : parentAssocs)
        {
            // ignore non-primary assocs
            if (!assoc.getIsPrimary())
            {
                continue;
            }
            else if (parentNode != null)
            {
                // we have more than one somehow
                throw new DataIntegrityViolationException("Multiple primary associations: \n" +
                        "   child: " + nodeRef + "\n" +
                        "   first primary parent: " + parentNode + "\n" +
                        "   second primary parent: " + assoc.getParent());
            }
            parentNode = assoc.getParent();
            // we keep looping to hunt out data integrity issues
        }
        // did we find a primary parent?
        if (parentNode == null)
        {
            // the only condition where this is allowed is if it is a root node
            Store store = node.getStore();
            Node rootNode = store.getRootNode();
            if (!rootNode.equals(node))
            {
                // it wasn't the root node
                throw new DataIntegrityViolationException("Non-root node has no primary parent: \n" +
                        "   child: " + nodeRef);
            }
        }
        // done
        if (logger.isDebugEnabled())
        {
            logger.debug("Retrieved node primary parent: \n" +
                    "   child: " + nodeRef + "\n" +
                    "   primary parent: " + parentNode);
        }
        return (parentNode == null ? null : parentNode.getNodeRef());
    }

    public void createAssociation(NodeRef sourceRef, NodeRef targetRef, String assocName)
            throws InvalidNodeRefException, AssociationExistsException
    {
        throw new UnsupportedOperationException();
    }

    public void removeAssociation(NodeRef sourceRef, NodeRef targetRef, String assocName)
            throws InvalidNodeRefException
    {
        throw new UnsupportedOperationException();
    }

    public Collection<NodeRef> getAssociationTargets(NodeRef sourceRef, String assocName)
            throws InvalidNodeRefException
    {
        throw new UnsupportedOperationException();
    }

    public Collection<NodeRef> getAssociationSources(NodeRef targetRef, String assocName)
            throws InvalidNodeRefException
    {
        throw new UnsupportedOperationException();
    }

    /**
     * This method can pull many objects into the first level cache.  The
     * {@link NodeDaoService#evict(Node)} method is used to ensure that the cache size
     * doesn't grow too large.
     */
    public String getPath(NodeRef nodeRef) throws InvalidNodeRefException
    {
        throw new UnsupportedOperationException();
    }
}
