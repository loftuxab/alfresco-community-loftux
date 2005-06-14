package org.alfresco.repo.node.db.hibernate;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.alfresco.repo.dictionary.impl.DictionaryBootstrap;
import org.alfresco.repo.domain.ChildAssoc;
import org.alfresco.repo.domain.Node;
import org.alfresco.repo.domain.NodeAssoc;
import org.alfresco.repo.domain.NodeKey;
import org.alfresco.repo.domain.Store;
import org.alfresco.repo.domain.StoreKey;
import org.alfresco.repo.domain.hibernate.ChildAssocImpl;
import org.alfresco.repo.domain.hibernate.NodeAssocImpl;
import org.alfresco.repo.domain.hibernate.NodeImpl;
import org.alfresco.repo.domain.hibernate.StoreImpl;
import org.alfresco.repo.node.db.NodeDaoService;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.dictionary.InvalidTypeException;
import org.alfresco.service.cmr.dictionary.TypeDefinition;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.GUID;
import org.hibernate.ObjectDeletedException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

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

    private DictionaryService dictionaryService;
    
    /**
     * @param dictionaryService the dictionary service to use
     */
    public void setDictionaryService(DictionaryService dictionaryService)
    {
        this.dictionaryService = dictionaryService;
    }

    public void evict(Node node)
    {
        getHibernateTemplate().evict(node);
    }

    public void evict(ChildAssoc assoc)
    {
        getHibernateTemplate().evict(assoc);
    }

    /**
     * Ensures that the store protocol/identifier combination is unique
     */
    public Store createStore(String protocol, String identifier)
    {
        // ensure that the name isn't in use
        Store store = getStore(protocol, identifier);
        if (store != null)
        {
            throw new RuntimeException("A store already exists: \n" +
                    "   protocol: " + protocol + "\n" +
                    "   identifier: " + identifier + "\n" +
                    "   store: " + store);
        }
        
        store = new StoreImpl();
        // set key
        store.setKey(new StoreKey(protocol, identifier));
        // persist so that it is present in the hibernate cache
        getHibernateTemplate().save(store);
        // create and assign a root node
        Node rootNode = newNode(store, DictionaryBootstrap.TYPE_QNAME_STOREROOT);
        store.setRootNode(rootNode);
        // done
        return store;
    }

    public Store getStore(String protocol, String identifier)
    {
        StoreKey storeKey = new StoreKey(protocol, identifier);
        Store store = (Store) getHibernateTemplate().get(StoreImpl.class, storeKey);
        // done
        return store;
    }

    public Node newNode(Store store, QName nodeTypeQName) throws InvalidTypeException
    {
        TypeDefinition typeDef = dictionaryService.getType(nodeTypeQName);
        if (typeDef == null)
        {
            throw new InvalidTypeException(nodeTypeQName);
        }
        boolean allowedChildren = typeDef.getChildAssociations().size() > 0;
        // build a concrete node based on a bootstrap type
        Node node = new NodeImpl();
        // set other required properties
		NodeKey key = new NodeKey(store.getKey(), GUID.generate());
		node.setKey(key);
        node.setTypeQName(nodeTypeQName);
        node.setStore(store);
        // persist the node
        getHibernateTemplate().save(node);
        // done
        return node;
    }

    public Node getNode(String protocol, String identifier, String id)
    {
        try
        {
    		NodeKey nodeKey = new NodeKey(protocol, identifier, id);
            Object obj = getHibernateTemplate().get(NodeImpl.class, nodeKey);
            // done
            return (Node) obj;
        }
        catch (DataAccessException e)
        {
            if (e.contains(ObjectDeletedException.class))
            {
                // the object no loner exists
                return null;
            }
            throw e;
        }
    }
    
    public void deleteNode(Node node)
    {
        getHibernateTemplate().delete(node);
        // done
    }
    
    public ChildAssoc newChildAssoc(
            Node parentNode,
            Node childNode,
            boolean isPrimary,
            QName assocTypeQName,
            QName qname)
    {
        ChildAssoc assoc = new ChildAssocImpl();
        assoc.setTypeQName(assocTypeQName);
        assoc.setIsPrimary(isPrimary);
        assoc.setQName(qname);
        assoc.buildAssociation(parentNode, childNode);
        // persist
        getHibernateTemplate().save(assoc);
        // done
        return assoc;
    }
    
    public void deleteChildAssoc(ChildAssoc assoc)
    {
        // maintain inverse association sets
        assoc.removeAssociation();
        // remove instance
        getHibernateTemplate().delete(assoc);
        
        // enforce the cascade
        getHibernateTemplate().flush();
    }

    public ChildAssoc getPrimaryParentAssoc(Node node)
    {
        // get the assocs pointing to the node
        Set<ChildAssoc> parentAssocs = node.getParentAssocs();
        ChildAssoc primaryAssoc = null;
        for (ChildAssoc assoc : parentAssocs)
        {
            // ignore non-primary assocs
            if (!assoc.getIsPrimary())
            {
                continue;
            }
            else if (primaryAssoc != null)
            {
                // we have more than one somehow
                throw new DataIntegrityViolationException("Multiple primary associations: \n" +
                        "   child: " + node + "\n" +
                        "   first primary assoc: " + primaryAssoc + "\n" +
                        "   second primary assoc: " + assoc);
            }
            primaryAssoc = assoc;
            // we keep looping to hunt out data integrity issues
        }
        // did we find a primary assoc?
        if (primaryAssoc == null)
        {
            // the only condition where this is allowed is if the given node is a root node
            Store store = node.getStore();
            Node rootNode = store.getRootNode();
            if (!rootNode.equals(node))
            {
                // it wasn't the root node
                throw new DataIntegrityViolationException("Non-root node has no primary parent: \n" +
                        "   child: " + node);
            }
        }
        // done
        return primaryAssoc;
    }

    public NodeAssoc newNodeAssoc(Node sourceNode, Node targetNode, QName assocTypeQName)
    {
        NodeAssoc assoc = new NodeAssocImpl();
        assoc.setTypeQName(assocTypeQName);
        assoc.buildAssociation(sourceNode, targetNode);
        // persist
        getHibernateTemplate().save(assoc);
        // done
        return assoc;
    }

    public NodeAssoc getNodeAssoc(
            final Node sourceNode,
            final Node targetNode,
            final QName assocTypeQName)
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
                     .setString("assocTypeNamespaceUri", assocTypeQName.getNamespaceURI())
                     .setString("assocTypeLocalName", assocTypeQName.getLocalName())
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
        return assoc;
    }

    public Collection<Node> getNodeAssocTargets(final Node sourceNode, final QName assocTypeQName)
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
                     .setString("assocTypeNamespaceUri", assocTypeQName.getNamespaceURI())
                     .setString("assocTypeLocalName", assocTypeQName.getLocalName());
                return query.list();
            }
        };
        List<Node> queryResults = (List) getHibernateTemplate().execute(callback);
        // done
        return queryResults;
    }

    public Collection<Node> getNodeAssocSources(final Node targetNode, final QName assocTypeQName)
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
                     .setString("assocTypeNamespaceUri", assocTypeQName.getNamespaceURI())
                     .setString("assocTypeLocalName", assocTypeQName.getLocalName());
                return query.list();
            }
        };
        List<Node> queryResults = (List) getHibernateTemplate().execute(callback);
        // done
        return queryResults;
    }

    public void deleteNodeAssoc(NodeAssoc assoc)
    {
        getHibernateTemplate().delete(assoc);
    }
}
