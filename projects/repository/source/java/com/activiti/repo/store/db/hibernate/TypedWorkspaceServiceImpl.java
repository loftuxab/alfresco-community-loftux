package com.activiti.repo.store.db.hibernate;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.activiti.repo.domain.Node;
import com.activiti.repo.domain.RealNode;
import com.activiti.repo.domain.Store;
import com.activiti.repo.domain.hibernate.StoreImpl;
import com.activiti.repo.node.TypedNodeService;
import com.activiti.repo.store.db.DbStoreService;

/**
 * Hibernate-specific implementation of the entity-aware workspace service.
 * 
 * @author derekh
 */
public class TypedWorkspaceServiceImpl
    extends HibernateDaoSupport
    implements DbStoreService
{
    private static final Log logger = LogFactory.getLog(TypedWorkspaceServiceImpl.class);
    
    private TypedNodeService typedNodeService;
    
    public void setTypedNodeService(TypedNodeService typedNodeService)
    {
        this.typedNodeService = typedNodeService;
    }

    /**
     * Ensures that the workspace protocol/identifier combination is unique
     */
    public Store createWorkspace(String protocol, String identifier)
    {
        // ensure that the name isn't in use
        Store workspace = findWorkspace(protocol, identifier);
        if (workspace != null)
        {
            throw new RuntimeException("A workspace already exists: \n" +
                    "   protocol: " + protocol + "\n" +
                    "   identifier: " + identifier + "\n" +
                    "   workspace: " + workspace);
        }
        
        workspace = new StoreImpl();
        // set attributes
        workspace.setProtocol(protocol);
        workspace.setIdentifier(identifier);
        // persist so that it is present in the hibernate cache
        getHibernateTemplate().save(workspace);
        // create and assign a root node
        RealNode rootNode = typedNodeService.newRealNode(workspace, Node.TYPE_CONTAINER);
        workspace.setRootNode(rootNode);
        // done
        if (logger.isDebugEnabled())
        {
            logger.debug("Created workspace: " + workspace);
        }
        return workspace;
    }

    public Store findWorkspace(String protocol, String identifier)
    {
        List results = getHibernateTemplate().findByNamedQueryAndNamedParam(Store.QUERY_FIND_BY_PROTOCOL_AND_IDENTIFIER,
                new String[] {"protocol", "identifier"},
                new Object[] {protocol, identifier});
        Store workspace = null;
        if (results.size() > 0)
        {
            workspace = (Store) results.get(0); 
        }
        // done
        if (logger.isDebugEnabled())
        {
            logger.debug("getWorkspace results: \n" +
                    "   protocol: " + protocol + "\n" +
                    "   identifier: " + identifier + "\n" +
                    "   result: " + workspace);
        }
        return workspace;
    }
}
