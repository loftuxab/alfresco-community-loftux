package com.activiti.repo.node;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.dao.DataIntegrityViolationException;

import com.activiti.repo.domain.ChildAssoc;
import com.activiti.repo.domain.ContainerNode;
import com.activiti.repo.domain.Node;
import com.activiti.repo.domain.RealNode;
import com.activiti.repo.domain.Store;
import com.activiti.repo.ref.NodeRef;
import com.activiti.repo.ref.StoreRef;
import com.activiti.repo.workspace.DbStoreService;

/**
 * Persistent-independent <b>node</b> functionality
 * 
 * @author derekh
 */
public class NodeServiceImpl implements NodeService
{
    private static final Log logger = LogFactory.getLog(NodeServiceImpl.class);
    
    private TypedNodeService typedNodeService;
    private StoreDaoService typedWorkspaceService;
    
    public void setTypedNodeService(TypedNodeService typedNodeService)
    {
        this.typedNodeService = typedNodeService;
    }

    public void setTypedWorkspaceService(StoreDaoService typedWorkspaceService)
    {
        this.typedWorkspaceService = typedWorkspaceService;
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
        Store workspace = typedWorkspaceService.findStore(storeRef.getProtocol(), storeRef.getIdentifier());
        if (workspace == null)
        {
            throw new DataIntegrityViolationException("No workspace found for parent node: " + parentRef);
        }
        // create the node instance
        RealNode node = typedNodeService.newRealNode(workspace, nodeType);
        // get the parent node
        Node uncheckedParent = typedNodeService.findNodeInWorkspace(workspace, parentRef.getGuid());
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
        ChildAssoc assoc = typedNodeService.newChildAssoc(parentNode, node, true, name);
        // done
        return node.getNodeRef();
    }

    public void deleteNode(NodeRef nodeRef)
    {
        throw new UnsupportedOperationException();
    }

}
