package com.activiti.repo.node.db;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.dao.DataIntegrityViolationException;

import com.activiti.repo.domain.ChildAssoc;
import com.activiti.repo.domain.ContainerNode;
import com.activiti.repo.domain.Node;
import com.activiti.repo.domain.NodeAssoc;
import com.activiti.repo.domain.NodeKey;
import com.activiti.repo.domain.RealNode;
import com.activiti.repo.domain.Store;
import com.activiti.repo.node.AssociationExistsException;
import com.activiti.repo.node.InvalidNodeRefException;
import com.activiti.repo.node.NodeService;
import com.activiti.repo.ref.NodeRef;
import com.activiti.repo.ref.Path;
import com.activiti.repo.ref.QName;
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
        
        // set the properties
        if (properties != null)
        {
            node.getProperties().putAll(properties);
        }
        
        // done
        return node.getNodeRef();
    }

    /**
     * Performs a null- and type-safe check before returning the <b>container</b> node
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
     * Performs a null- and type-safe check before returning the <b>real</b> node
     * @param nodeRef a reference to a real node
     * @return Returns an instance of a real node (never null)
     * @throws InvalidNodeRefException if the node referenced doesn't exist
     * @throws RuntimeException if the reference is to a node type that is incompatible with the return type
     */
    private RealNode getRealNodeNotNull(NodeRef nodeRef) throws InvalidNodeRefException
    {
        Node unchecked = getNodeNotNull(nodeRef);
        if (!(unchecked instanceof RealNode))
        {
            throw new RuntimeException("Node must be of type " + Node.TYPE_REAL + ": " + nodeRef);
        }
        return (RealNode) unchecked;
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
    
    public String getProperty(NodeRef nodeRef, String propertyName) throws InvalidNodeRefException
    {
        Node node = getNodeNotNull(nodeRef);
        Map<String, String> properties = node.getProperties();
        return properties.get(propertyName);
    }

    public void setProperties(NodeRef nodeRef, Map<String, String> properties) throws InvalidNodeRefException
    {
        if (properties == null)
        {
            throw new IllegalArgumentException("Properties may not be null");
        }
        
        Node node = getNodeNotNull(nodeRef);
        node.getProperties().clear();
        node.getProperties().putAll(properties);
        // done
        if (logger.isDebugEnabled())
        {
            logger.debug("Set node properties: \n" +
                    "   node: " + nodeRef + "\n" +
                    "   count: " + properties.size());
        }
    }

    public void setProperty(NodeRef nodeRef, String propertyName, String propertyValue) throws InvalidNodeRefException
    {
        Node node = getNodeNotNull(nodeRef);
        Map<String, String> properties = node.getProperties();
        properties.put(propertyName, propertyValue);
        // done
        if (logger.isDebugEnabled())
        {
            logger.debug("Set node property: \n" +
                    "   node: " + nodeRef + "\n" +
                    "   name: " + propertyName + "\n" +
                    "   value: " + propertyValue);
        }
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

    public Collection<NodeRef> getChildren(NodeRef nodeRef) throws InvalidNodeRefException
    {
        ContainerNode node = getContainerNodeNotNull(nodeRef);
        // get the assocs pointing from it
        Set<ChildAssoc> childAssocs = node.getChildAssocs();
        // list of results
        List<NodeRef> results = new ArrayList<NodeRef>(childAssocs.size());
        for (ChildAssoc assoc : childAssocs)
        {
            // get the child
            Node childNode = assoc.getChild();
            results.add(childNode.getNodeRef());
        }
        // done
        if (logger.isDebugEnabled())
        {
            logger.debug("Fetched node children: \n" +
                    "   node: " + nodeRef + "\n" +
                    "   children: " + results);
        }
        return results;
    }

    public NodeRef getPrimaryParent(NodeRef nodeRef) throws InvalidNodeRefException
    {
        Node node = getNodeNotNull(nodeRef);
        // get the primary parent assoc
        ChildAssoc assoc = nodeDaoService.getPrimaryParentAssoc(node);

        // done - the assoc may be null
        return (assoc == null ? null : assoc.getParent().getNodeRef());
    }

    public void createAssociation(NodeRef sourceRef, NodeRef targetRef, String assocName)
            throws InvalidNodeRefException, AssociationExistsException
    {
        RealNode sourceNode = getRealNodeNotNull(sourceRef);
        Node targetNode = getNodeNotNull(targetRef);
        // see if it exists
        NodeAssoc assoc = nodeDaoService.getNodeAssoc(sourceNode, targetNode, assocName);
        if (assoc != null)
        {
            throw new AssociationExistsException(sourceRef, targetRef, assocName);
        }
        // we are sure that the association doesn't exist - make it
        nodeDaoService.newNodeAssoc(sourceNode, targetNode, assocName);
        // done
    }

    public void removeAssociation(NodeRef sourceRef, NodeRef targetRef, String assocName)
            throws InvalidNodeRefException
    {
        RealNode sourceNode = getRealNodeNotNull(sourceRef);
        Node targetNode = getNodeNotNull(targetRef);
        // get the association
        NodeAssoc assoc = nodeDaoService.getNodeAssoc(sourceNode, targetNode, assocName);
        // delete it
        nodeDaoService.deleteNodeAssoc(assoc);
    }
    
    /**
     * Converts a collection of <code>Node</code> instances into an equivalent
     * collection of <code>NodeRef</code> instances.
     * 
     * @param nodes the <code>Node</code> instances to convert to references
     * @return Returns a <i>new</i> collection of equivalent <code>NodeRef</code> instances
     */
    private Collection<NodeRef> convertToNodeRefs(Collection<? extends Node> nodes)
    {
        // build the reference results
        Collection<NodeRef> nodeRefs = new ArrayList<NodeRef>(nodes.size());
        for (Node node : nodes)
        {
            nodeRefs.add(node.getNodeRef());
        }
        // done
        return nodeRefs;
    }

    public Collection<NodeRef> getAssociationTargets(NodeRef sourceRef, String assocName)
            throws InvalidNodeRefException
    {
        RealNode sourceNode = getRealNodeNotNull(sourceRef);
        Collection<Node> targets = nodeDaoService.getNodeAssocTargets(sourceNode, assocName);
        // build the reference results
        Collection<NodeRef> nodeRefs = convertToNodeRefs(targets);
        // done
        return nodeRefs;
    }

    public Collection<NodeRef> getAssociationSources(NodeRef targetRef, String assocName)
            throws InvalidNodeRefException
    {
        Node targetNode = getNodeNotNull(targetRef);
        Collection<RealNode> sources = nodeDaoService.getNodeAssocSources(targetNode, assocName);
        // build the reference results
        Collection<NodeRef> nodeRefs = convertToNodeRefs(sources);
        // done
        return nodeRefs;
    }

    /**
     * This method can pull many objects into the first level cache.  The
     * {@link NodeDaoService#evict(Node)} method is used to ensure that the cache size
     * doesn't grow too large.
     */
    public Path getPath(NodeRef nodeRef) throws InvalidNodeRefException
    {
        // get the starting node
        Node node = getNodeNotNull(nodeRef);
        // build the path
        Path path = new Path();         // the path we will build
        ChildAssoc assoc = null;        // the storage for each assoc as we move up the path
        Set<Node> touchedNodes = new HashSet<Node>(5);  // ensure that we don't walk up cyclic associations
        while(true)
        {
            if (!touchedNodes.add(node))
            {
                throw new RuntimeException("Cyclic child relationship detected: \n" +
                        "   touched nodes: " + touchedNodes);
            }
            assoc = nodeDaoService.getPrimaryParentAssoc(node);
            if (assoc == null)
            {
                break;      // stop once we hit the root node, i.e. a node without primary parents
            }
            // move up the relationship
            node = assoc.getParent();
            // prepend this association to the path as we are traversing upwards
            QName qname = QName.createQName(null, assoc.getName());  // TODO: get namespace from assoc
            Path.Element element = new Path.QNameElement(qname);
            path.prepend(element);
        }
        // done
        if (logger.isDebugEnabled())
        {
            logger.debug("Fetched primary node path: \n" +
                    "   node: " + nodeRef + "\n" +
                    "   path: " + path);
        }
        return path;
    }

    /**
     * @param currentNode the node to start from, i.e. the child node to work upwards from
     * @param currentPath the path from the current node to the descendent that we started from
     * @param primaryOnly true if only the primary parent association must be traversed
     * @return Returns a collection of new <code>Path</code> instances for the extended paths.  The <code>currentPath</code>
     *      instance may be included in the list
     */
    private Collection<Path> prependPaths(Node currentNode, Path currentPath, boolean primaryOnly)
    {
        throw new UnsupportedOperationException();
    }

    public Collection<Path> getPaths(NodeRef nodeRef) throws InvalidNodeRefException
    {
        // get the starting node
        Node node = getNodeNotNull(nodeRef);
        throw new UnsupportedOperationException();
    }
}
