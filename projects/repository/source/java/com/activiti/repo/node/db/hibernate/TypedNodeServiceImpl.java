package com.activiti.repo.node.db.hibernate;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.activiti.repo.domain.ChildAssoc;
import com.activiti.repo.domain.ContainerNode;
import com.activiti.repo.domain.Node;
import com.activiti.repo.domain.RealNode;
import com.activiti.repo.domain.ReferenceNode;
import com.activiti.repo.domain.Store;
import com.activiti.repo.domain.hibernate.ChildAssocImpl;
import com.activiti.repo.domain.hibernate.ContainerNodeImpl;
import com.activiti.repo.domain.hibernate.ContentNodeImpl;
import com.activiti.repo.domain.hibernate.RealNodeImpl;
import com.activiti.repo.domain.hibernate.ReferenceNodeImpl;
import com.activiti.repo.node.db.NodeDaoService;
import com.activiti.util.GUID;

/**
 * Hibernate-specific implementation
 * 
 * @author derekh
 */
public class TypedNodeServiceImpl extends HibernateDaoSupport implements NodeDaoService
{
    private static final Log logger = LogFactory.getLog(TypedNodeServiceImpl.class);

    public ReferenceNode newReferenceNode(Store workspace, String referencedPath)
    {
        ReferenceNode node = new ReferenceNodeImpl();
        node.setType(Node.TYPE_REFERENCE);
        node.setGuid(GUID.generate());
        node.setWorkspace(workspace);
        node.setReferencedPath(referencedPath);
        // persist the node
        getHibernateTemplate().save(node);
        // done
        if (logger.isDebugEnabled())
        {
            logger.debug("Created new reference node: " + node);
        }
        return node;
    }

    public RealNode newRealNode(Store workspace, String type)
    {
        RealNode node = null;
        if (type.equals(Node.TYPE_CONTAINER))
        {
            node = new ContainerNodeImpl();
            node.setType(Node.TYPE_CONTAINER);
        }
        else if (type.equals(Node.TYPE_CONTENT))
        {
            node = new ContentNodeImpl();
            node.setType(Node.TYPE_CONTENT);
        }
        else
        {
            node = new RealNodeImpl();
            node.setType(Node.TYPE_REAL);
        }
        node.setGuid(GUID.generate());
        node.setWorkspace(workspace);
        // persist the node
        getHibernateTemplate().save(node);
        // done
        if (logger.isDebugEnabled())
        {
            logger.debug("Created new real node of type " + type + ": " + node);
        }
        return node;
    }
    
    public ChildAssoc newChildAssoc(ContainerNode parentNode,
            Node childNode,
            boolean isPrimary,
            String name)
    {
        ChildAssoc assoc = new ChildAssocImpl();
        assoc.setIsPrimary(isPrimary);
        assoc.setName(name);
        assoc.buildAssociation(parentNode, childNode);
        // persist
        getHibernateTemplate().save(assoc);
        // done
        if (logger.isDebugEnabled())
        {
            logger.debug("Created child association: \n" +
                    "   assoc: " + assoc);
        }
        return assoc;
    }

    public Node findNodeInStore(Store store, String id)
    {
        List results = getHibernateTemplate().findByNamedQueryAndNamedParam(Node.QUERY_FIND_NODE_IN_STORE,
                new String[] {"nodeGuid", "workspaceProtocol", "workspaceIdentifier"},
                new Object[] {id, store.getProtocol(), store.getIdentifier()});
        Node node = null;
        if (results.size() == 0)
        {
            node = null;
        }
        else if (results.size() > 1)
        {
            throw new RuntimeException("Multiple node ID matches in workspace: \n" +
                    "   workspace: " + store + "\n" +
                    "   node id: " + id + "\n" +
                    "   results: " + results);
        }
        else
        {
            node = (Node) results.get(0);
        }
        // done
        if (logger.isDebugEnabled())
        {
            logger.debug("Finding node in workspace: \n" +
                    "   workspace: " + store + "\n" +
                    "   node id: " + id + "\n" +
                    "   result: " + node);
        }
        return node;
    }
}
