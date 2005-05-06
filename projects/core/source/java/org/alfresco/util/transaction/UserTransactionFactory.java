package org.alfresco.util.transaction;

import javax.transaction.UserTransaction;

import org.springframework.beans.factory.config.AbstractFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

/**
 * Factory of <code>UserTransaction</code> instances.
 * 
 * @author Derek Hulley
 */
public class UserTransactionFactory extends AbstractFactoryBean
{
    private PlatformTransactionManager transactionManager;
    
    public UserTransactionFactory(PlatformTransactionManager transactionManager)
    {
        this.transactionManager = transactionManager;
    }

    /**
     * @return Returns the class of <code>UserTransaction</code>
     */
    public Class getObjectType()
    {
        return UserTransaction.class;
    }

    protected Object createInstance() throws Exception
    {
        return new SpringAwareUserTransaction(transactionManager);
    }
}
