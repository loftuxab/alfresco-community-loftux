package com.activiti.repo.store.db.hibernate;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.activiti.repo.domain.Node;
import com.activiti.repo.domain.RealNode;
import com.activiti.repo.domain.Store;
import com.activiti.repo.domain.hibernate.StoreImpl;
import com.activiti.repo.node.db.NodeDaoService;
import com.activiti.repo.store.db.StoreDaoService;

/**
 * Hibernate-specific implementation of the entity-aware store service.
 * 
 * @author derekh
 */
public class HibernateStoreDaoServiceImpl
    extends HibernateDaoSupport
    implements StoreDaoService
{
    private static final Log logger = LogFactory.getLog(HibernateStoreDaoServiceImpl.class);
    
    private NodeDaoService nodeDaoService;
    
    public void setNodeDaoService(NodeDaoService nodeDaoService)
    {
        this.nodeDaoService = nodeDaoService;
    }

    /**
     * Ensures that the store protocol/identifier combination is unique
     */
    public Store createStore(String protocol, String identifier)
    {
        // ensure that the name isn't in use
        Store store = findStore(protocol, identifier);
        if (store != null)
        {
            throw new RuntimeException("A store already exists: \n" +
                    "   protocol: " + protocol + "\n" +
                    "   identifier: " + identifier + "\n" +
                    "   store: " + store);
        }
        
        store = new StoreImpl();
        // set attributes
        store.setProtocol(protocol);
        store.setIdentifier(identifier);
        // persist so that it is present in the hibernate cache
        getHibernateTemplate().save(store);
        // create and assign a root node
        RealNode rootNode = nodeDaoService.newRealNode(store, Node.TYPE_CONTAINER);
        store.setRootNode(rootNode);
        // done
        if (logger.isDebugEnabled())
        {
            logger.debug("Created store: " + store);
        }
        return store;
    }

    public Store findStore(String protocol, String identifier)
    {
        List results = getHibernateTemplate().findByNamedQueryAndNamedParam(Store.QUERY_FIND_BY_PROTOCOL_AND_IDENTIFIER,
                new String[] {"protocol", "identifier"},
                new Object[] {protocol, identifier});
        Store store = null;
        if (results.size() > 0)
        {
            store = (Store) results.get(0); 
        }
        // done
        if (logger.isDebugEnabled())
        {
            logger.debug("getWorkspace results: \n" +
                    "   protocol: " + protocol + "\n" +
                    "   identifier: " + identifier + "\n" +
                    "   result: " + store);
        }
        return store;
    }
}
