package org.alfresco.repo.transaction;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.repo.node.db.NodeDaoService;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.beans.factory.InitializingBean;

/**
 * Utility class that ensures that a <tt>NodeDaoService</tt> has been registered
 * with the current transaction.
 * <p>
 * It is designed to act as a <b>postInterceptor</b> on the <tt>NodeDaoService</tt>'s
 * {@link org.springframework.transaction.interceptor.TransactionProxyFactoryBean}. 
 * 
 * @author Derek Hulley
 */
public class NodeDaoServiceTransactionInterceptor implements MethodInterceptor, InitializingBean
{
    private NodeDaoService nodeDaoService;

    /**
     * @param nodeDaoService the <tt>NodeDaoService</tt> to register
     */
    public void setNodeDaoService(NodeDaoService nodeDaoService)
    {
        this.nodeDaoService = nodeDaoService;
    }

    /**
     * Checks that required values have been injected
     */
    public void afterPropertiesSet() throws Exception
    {
        if (nodeDaoService == null)
        {
            throw new AlfrescoRuntimeException("NodeDaoService is required: " + this);
        }
    }

    public Object invoke(MethodInvocation invocation) throws Throwable
    {
        AlfrescoTransactionSupport.bindNodeDaoService(nodeDaoService);
        // propogate the call
        return invocation.proceed();
    }
}
