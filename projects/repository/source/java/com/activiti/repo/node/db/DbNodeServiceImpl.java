package com.activiti.repo.node.db;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.dao.DataIntegrityViolationException;

import com.activiti.repo.domain.ChildAssoc;
import com.activiti.repo.domain.ContainerNode;
import com.activiti.repo.domain.Node;
import com.activiti.repo.domain.RealNode;
import com.activiti.repo.domain.Store;
import com.activiti.repo.node.NodeService;
import com.activiti.repo.ref.NodeRef;
import com.activiti.repo.ref.StoreRef;
import com.activiti.repo.store.db.StoreDaoService;

/**
 * Node service using database persistence layer to fulfill functionality
 * 
 * @author derekh
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

    public NodeRef createNode(NodeRef parentRef, String name, String nodeType, Map properties)
    {
        if (logger.isDebugEnabled())
        {
            logger.debug("Creating node: \n" +
                    "   parent: " + parentRef + "\n" +
                    "   name: " + name + "\n" +
                    "   nodeType: " + nodeType);
        }
        // extract the name of the workspace that the parent belongs to
        StoreRef storeRef = parentRef.getStoreRef();
        Store store = storeDaoService.findStore(storeRef.getProtocol(), storeRef.getIdentifier());
        if (store == null)
        {
            throw new DataIntegrityViolationException("No store found for parent node: " + parentRef);
        }
        // create the node instance
        RealNode node = nodeDaoService.newRealNode(store, nodeType);
        // get the parent node
        Node uncheckedParent = nodeDaoService.findNodeInStore(store, parentRef.getGuid());
        if (uncheckedParent == null)
        {
            throw new IllegalArgumentException("Parent node must exist: " + parentRef);
        }
        else if (!(uncheckedParent instanceof ContainerNode))
        {
            throw new RuntimeException("Parent node must be of type " + Node.TYPE_CONTAINER);
        }
        ContainerNode parentNode = (ContainerNode) uncheckedParent;
        // create the association
        ChildAssoc assoc = nodeDaoService.newChildAssoc(parentNode, node, true, name);
        // done
        return node.getNodeRef();
    }

    public void deleteNode(NodeRef nodeRef)
    {
        throw new UnsupportedOperationException();
    }

}
