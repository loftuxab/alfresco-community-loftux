package org.alfresco.repo.transaction;

import javax.transaction.UserTransaction;

import org.alfresco.service.transaction.TransactionService;
import org.alfresco.util.transaction.SpringAwareUserTransaction;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;

/**
 * Default implementation of Transaction Service
 * 
 * @author David Caruana
 */
public class TransactionComponent implements TransactionService
{
    private PlatformTransactionManager transactionManager;
    
    /**
     * Construct Transaction Component
     * 
     * @param transactionManager platform transaction manager
     */
    public TransactionComponent(PlatformTransactionManager transactionManager)
    {
        this.transactionManager = transactionManager;
    }
    
    /**
     * @see org.springframework.transaction.TransactionDefinition#PROPAGATION_REQUIRED
     */
    public UserTransaction getUserTransaction()
    {
        return new SpringAwareUserTransaction(transactionManager);
    }

    /**
     * @see org.springframework.transaction.TransactionDefinition#PROPAGATION_REQUIRES_NEW
     */
    public UserTransaction getNonPropagatingUserTransaction()
    {
        SpringAwareUserTransaction txn = new SpringAwareUserTransaction(transactionManager);
        txn.setPropagationBehviour(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        return txn;
    }
}
