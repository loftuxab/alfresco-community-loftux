package com.activiti.repo.node.db.hibernate;

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
import com.activiti.repo.domain.hibernate.NodeImpl;
import com.activiti.repo.domain.NodeKey;
import com.activiti.repo.domain.hibernate.RealNodeImpl;
import com.activiti.repo.domain.hibernate.ReferenceNodeImpl;
import com.activiti.repo.node.db.NodeDaoService;
import com.activiti.util.GUID;

/**
 * Hibernate-specific implementation of the persistence-independent <b>node</b> DAO interface
 * 
 * @author Derek Hulley
 */
public class HibernateNodeDaoServiceImpl extends HibernateDaoSupport implements NodeDaoService
{
    private static final Log logger = LogFactory.getLog(HibernateNodeDaoServiceImpl.class);

    public void evict(Node node)
    {
        getHibernateTemplate().evict(node);
    }

    public void evict(ChildAssoc assoc)
    {
        getHibernateTemplate().evict(assoc);
    }

    public ReferenceNode newReferenceNode(Store store, String referencedPath)
    {
        ReferenceNode node = new ReferenceNodeImpl();
		NodeKey key = new NodeKey(store.getKey(), GUID.generate());
		node.setKey(key);
        node.setType(Node.TYPE_REFERENCE);
        node.setStore(store);
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

    public RealNode newRealNode(Store store, String type)
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
		NodeKey key = new NodeKey(store.getKey(), GUID.generate());
		node.setKey(key);
        node.setStore(store);
        // persist the node
        getHibernateTemplate().save(node);
        // done
        if (logger.isDebugEnabled())
        {
            logger.debug("Created new real node of type " + type + ": " + node);
        }
        return node;
    }

    public Node getNode(String protocol, String identifier, String id)
    {
		NodeKey nodeKey = new NodeKey(protocol, identifier, id);
        Object obj = getHibernateTemplate().get(NodeImpl.class, nodeKey);
        // done
        if (logger.isDebugEnabled())
        {
            if (obj == null)
            {
                logger.debug("No node found: \n" +
                        "   protocol: " + protocol + "\n" +
                        "   identifier: " + identifier + "\n" +
                        "   id: " + id);
            }
            else
            {
                logger.debug("Fetched node: " + obj);
            }
        }
        return (Node) obj;
    }
    
    public void deleteNode(Node node)
    {
        getHibernateTemplate().delete(node);
        // done
        if (logger.isDebugEnabled())
        {
            logger.debug("Deleted node: " + node);
        }
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
    
    public void deleteChildAssoc(ChildAssoc assoc)
    {
        getHibernateTemplate().delete(assoc);
    }
}
