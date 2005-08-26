/*
 * Copyright (C) 2005 Alfresco, Inc.
 *
 * Licensed under the Mozilla Public License version 1.1 
 * with a permitted attribution clause. You may obtain a
 * copy of the License at
 *
 *   http://www.alfresco.org/legal/license.txt
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the
 * License.
 */
package org.alfresco.repo.node.db.hibernate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.alfresco.model.ContentModel;
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
import org.alfresco.service.cmr.dictionary.InvalidTypeException;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.GUID;
import org.hibernate.ObjectDeletedException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.stat.SessionStatistics;
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
    public static final String QUERY_GET_ALL_STORES = "store.GetAllStores";
    public static final String QUERY_GET_CHILD_ASSOC = "node.GetChildAssoc";
    public static final String QUERY_GET_NODE_ASSOC = "node.GetNodeAssoc";
    public static final String QUERY_GET_NODE_ASSOC_TARGETS = "node.GetNodeAssocTargets";
    public static final String QUERY_GET_NODE_ASSOC_SOURCES = "node.GetNodeAssocSources";
    
    /** a uuid identifying this unique instance */
    private String uuid;
    /** maximum number of entities to keep in a session (L1 cache) */
    private int maxEntityCount;

    /**
     * 
     */
    public HibernateNodeDaoServiceImpl()
    {
        this.uuid = GUID.generate();
        this.maxEntityCount = 5000; 
    }

    /**
     * @param maxEntityCount the approximate maximum number of entities
     *      to allow in the L1 cache (session)
     */
    public void setMaxEntityCount(int maxEntityCount)
    {
        this.maxEntityCount = maxEntityCount;
    }

    /**
     * Checks equality by type and uuid
     */
    public boolean equals(Object obj)
    {
        if (obj == null)
        {
            return false;
        }
        else if (!(obj instanceof HibernateNodeDaoServiceImpl))
        {
            return false;
        }
        HibernateNodeDaoServiceImpl that = (HibernateNodeDaoServiceImpl) obj;
        return this.uuid.equals(that.uuid);
    }
    
    /**
     * @see #uuid
     */
    public int hashCode()
    {
        return uuid.hashCode();
    }

    /**
     * Flushes the Hibernate session and, depending on the size, clears the session.
     */
    public void flush()
    {
        // create a callback for the task
        HibernateCallback callback = new HibernateCallback()
        {
            public Object doInHibernate(Session session)
            {
                SessionStatistics stats = session.getStatistics();
                // have we exceeded the maximum entity count
                int entityCount = stats.getEntityCount();
                if (entityCount > maxEntityCount)
                {
                    // too many entities - flush and clear
                    session.flush();
                    session.clear();
                }
                // done
                return null;
            }
        };
        // execute the callback
        getHibernateTemplate().execute(callback);
        // done
    }

    /**
     * @see #QUERY_GET_ALL_STORES
     */
    @SuppressWarnings("unchecked")
    public List<Store> getStores()
    {
        HibernateCallback callback = new HibernateCallback()
        {
            public Object doInHibernate(Session session)
            {
                Query query = session.getNamedQuery(HibernateNodeDaoServiceImpl.QUERY_GET_ALL_STORES);
                return query.list();
            }
        };
        List<Store> queryResults = (List) getHibernateTemplate().execute(callback);
        // done
        return queryResults;
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
        Node rootNode = newNode(
                store,
                GUID.generate(),
                ContentModel.TYPE_STOREROOT);
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

    public Node newNode(Store store, String id, QName nodeTypeQName) throws InvalidTypeException
    {
        // build a concrete node based on a bootstrap type
        Node node = new NodeImpl();
        // set other required properties
		NodeKey key = new NodeKey(store.getKey(), id);
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
    
    /**
     * Manually ensures that all cascading of associations is taken care of
     */
    public void deleteNode(Node node, boolean cascade)
    {
        // delete all parent assocs
        Collection<ChildAssoc> parentAssocs = node.getParentAssocs();
        parentAssocs = new ArrayList<ChildAssoc>(parentAssocs);
        for (ChildAssoc assoc : parentAssocs)
        {
            deleteChildAssoc(assoc, false);  // we don't cascade upwards
        }
        // delete all child assocs
        Collection<ChildAssoc> childAssocs = node.getChildAssocs();
        childAssocs = new ArrayList<ChildAssoc>(childAssocs);
        for (ChildAssoc assoc : childAssocs)
        {
            deleteChildAssoc(assoc, cascade);   // potentially cascade downwards
        }
        // delete all target assocs
        Collection<NodeAssoc> targetAssocs = node.getTargetNodeAssocs();
        targetAssocs = new ArrayList<NodeAssoc>(targetAssocs);
        for (NodeAssoc assoc : targetAssocs)
        {
            deleteNodeAssoc(assoc);
        }
        // delete all source assocs
        Collection<NodeAssoc> sourceAssocs = node.getSourceNodeAssocs();
        sourceAssocs = new ArrayList<NodeAssoc>(sourceAssocs);
        for (NodeAssoc assoc : sourceAssocs)
        {
            deleteNodeAssoc(assoc);
        }
        // finally delete the node
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
    
    public ChildAssoc getChildAssoc(
            Node parentNode,
            Node childNode,
            QName assocTypeQName,
            QName qname)
    {
        ChildAssociationRef childAssocRef = new ChildAssociationRef(
                assocTypeQName,
                parentNode.getNodeRef(),
                qname,
                childNode.getNodeRef());
        // get all the parent's child associations
        Set<ChildAssoc> assocs = parentNode.getChildAssocs();
        // hunt down the desired assoc
        for (ChildAssoc assoc : assocs)
        {
            // is it a match?
            if (!assoc.getChildAssocRef().equals(childAssocRef))    // not a match
            {
                continue;
            }
            else
            {
                return assoc;
            }
        }
        // not found
        return null;
    }

    /**
     * Manually enforces cascade deletions down primary associations
     */
    public void deleteChildAssoc(ChildAssoc assoc, boolean cascade)
    {
        Node childNode = assoc.getChild();
        
        // maintain inverse association sets
        assoc.removeAssociation();
        // remove instance
        getHibernateTemplate().delete(assoc);
        
        if (cascade && assoc.getIsPrimary())   // the assoc is primary
        {
            // delete the child node
            deleteNode(childNode, cascade);
            /*
             * The child node deletion will cascade delete all assocs to
             * and from it, but we have safely removed this one, so no
             * duplicate call will be received to do this
             */
        }
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
                throw new DataIntegrityViolationException(
                        "Multiple primary associations: \n" +
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

    @SuppressWarnings("unchecked")
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

    @SuppressWarnings("unchecked")
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
        // maintain inverse association sets
        assoc.removeAssociation();
        // remove instance
        getHibernateTemplate().delete(assoc);
    }
}
