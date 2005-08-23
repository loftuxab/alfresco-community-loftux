package org.alfresco.repo.transaction;

import javax.transaction.UserTransaction;

import org.alfresco.service.transaction.TransactionService;
import org.alfresco.util.transaction.SpringAwareUserTransaction;
import org.springframework.transaction.PlatformTransactionManager;

/**
 * Default implementation of Transaction Service
 * 
 * @author David Caruana
 */
public class TransactionComponent
    implements TransactionService
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
    
    /* (non-Javadoc)
     * @see org.alfresco.util.transaction.TransactionService#getUserTransaction()
     */
    public UserTransaction getUserTransaction()
    {
        return new SpringAwareUserTransaction(transactionManager);
    }
}
