package com.activiti.repo.node.db.hibernate;

import java.util.Collection;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.activiti.repo.domain.ChildAssoc;
import com.activiti.repo.domain.ContainerNode;
import com.activiti.repo.domain.Node;
import com.activiti.repo.domain.NodeAssoc;
import com.activiti.repo.domain.NodeKey;
import com.activiti.repo.domain.RealNode;
import com.activiti.repo.domain.ReferenceNode;
import com.activiti.repo.domain.Store;
import com.activiti.repo.domain.hibernate.ChildAssocImpl;
import com.activiti.repo.domain.hibernate.ContainerNodeImpl;
import com.activiti.repo.domain.hibernate.ContentNodeImpl;
import com.activiti.repo.domain.hibernate.NodeAssocImpl;
import com.activiti.repo.domain.hibernate.NodeImpl;
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
    public static final String QUERY_GET_NODE_ASSOC = "node.GetNodeAssoc";
    public static final String QUERY_GET_NODE_ASSOC_TARGETS = "node.GetNodeAssocTargets";
    public static final String QUERY_GET_NODE_ASSOC_SOURCES = "node.GetNodeAssocSources";
    
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
            String assocName)
    {
        ChildAssoc assoc = new ChildAssocImpl();
        assoc.setIsPrimary(isPrimary);
        assoc.setName(assocName);
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

    public NodeAssoc newNodeAssoc(RealNode sourceNode, Node targetNode, String assocName)
    {
        NodeAssoc assoc = new NodeAssocImpl();
        assoc.setName(assocName);
        assoc.buildAssociation(sourceNode, targetNode);
        // persist
        getHibernateTemplate().save(assoc);
        // done
        if (logger.isDebugEnabled())
        {
            logger.debug("Created node association: \n" +
                    "   assoc: " + assoc);
        }
        return assoc;
    }

    public NodeAssoc getNodeAssoc(final RealNode sourceNode,
            final Node targetNode,
            final String assocName)
    {
        final NodeKey sourceKey = sourceNode.getKey();
        final NodeKey targetKey = targetNode.getKey();
        HibernateCallback callback = new HibernateCallback()
        {
            public Object doInHibernate(Session session)
            {
                Query query = session.getNamedQuery(HibernateNodeDaoServiceImpl.QUERY_GET_NODE_ASSOC);
                query.setString("sourceKeyProtocol", sourceKey.getProtocol())
                     .setString("sourceKeyIdentifier", sourceKey.getIdentifier())
                     .setString("sourceKeyGuid", sourceKey.getGuid())
                     .setString("assocName", assocName)
                     .setString("targetKeyProtocol", targetKey.getProtocol())
                     .setString("targetKeyIdentifier", targetKey.getIdentifier())
                     .setString("targetKeyGuid", targetKey.getGuid());
                query.setMaxResults(1);
                return query.uniqueResult();
            }
        };
        Object queryResult = getHibernateTemplate().execute(callback);
        if (queryResult == null)
        {
            return null;
        }
        NodeAssoc assoc = (NodeAssoc) queryResult;
        // done
        if (logger.isDebugEnabled())
        {
            logger.debug("Get node association: \n" +
                    "   source: " + sourceNode + "\n" +
                    "   target: " + targetNode + "\n" +
                    "   assoc name: " + assocName + "\n" +
                    "   assoc: " + assoc);
        }
        return assoc;
    }

    public Collection<Node> getNodeAssocTargets(final RealNode sourceNode, final String assocName)
    {
        final NodeKey sourceKey = sourceNode.getKey();
        HibernateCallback callback = new HibernateCallback()
        {
            public Object doInHibernate(Session session)
            {
                Query query = session.getNamedQuery(HibernateNodeDaoServiceImpl.QUERY_GET_NODE_ASSOC_TARGETS);
                query.setString("sourceKeyProtocol", sourceKey.getProtocol())
                     .setString("sourceKeyIdentifier", sourceKey.getIdentifier())
                     .setString("sourceKeyGuid", sourceKey.getGuid())
                     .setString("assocName", assocName);
                return query.list();
            }
        };
        List queryResults = (List) getHibernateTemplate().execute(callback);
        // done
        if (logger.isDebugEnabled())
        {
            logger.debug("Get node association targets: \n" +
                    "   source: " + sourceNode + "\n" +
                    "   assoc name: " + assocName + "\n" +
                    "   targets: " + queryResults);
        }
        return queryResults;
    }

    public Collection<RealNode> getNodeAssocSources(final Node targetNode, final String assocName)
    {
        final NodeKey targetKey = targetNode.getKey();
        HibernateCallback callback = new HibernateCallback()
        {
            public Object doInHibernate(Session session)
            {
                Query query = session.getNamedQuery(HibernateNodeDaoServiceImpl.QUERY_GET_NODE_ASSOC_SOURCES);
                query.setString("targetKeyProtocol", targetKey.getProtocol())
                     .setString("targetKeyIdentifier", targetKey.getIdentifier())
                     .setString("targetKeyGuid", targetKey.getGuid())
                     .setString("assocName", assocName);
                return query.list();
            }
        };
        List queryResults = (List) getHibernateTemplate().execute(callback);
        // done
        if (logger.isDebugEnabled())
        {
            logger.debug("Get node association sources: \n" +
                    "   target: " + targetNode + "\n" +
                    "   assoc name: " + assocName + "\n" +
                    "   sources: " + queryResults);
        }
        return queryResults;
    }

    public void deleteNodeAssoc(NodeAssoc assoc)
    {
        getHibernateTemplate().delete(assoc);
    }
}
