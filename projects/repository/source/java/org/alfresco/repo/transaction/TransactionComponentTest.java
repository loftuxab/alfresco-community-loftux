package org.alfresco.repo.transaction;

import javax.transaction.RollbackException;
import javax.transaction.Status;
import javax.transaction.UserTransaction;

import junit.framework.TestCase;

import org.alfresco.service.transaction.TransactionService;
import org.alfresco.util.ApplicationContextHelper;
import org.springframework.context.ApplicationContext;

/**
 * @see org.alfresco.repo.transaction.TransactionComponent
 * 
 * @author Derek Hulley
 */
public class TransactionComponentTest extends TestCase
{
    private static ApplicationContext ctx = ApplicationContextHelper.getApplicationContext();
    
    private TransactionService transactionService;
    
    public void setUp() throws Exception
    {
        transactionService = (TransactionService) ctx.getBean("transactionComponent");
    }
    
    public void testPropagatingTxn() throws Exception
    {
        // start a transaction
        UserTransaction txnOuter = transactionService.getUserTransaction();
        txnOuter.begin();
        String txnIdOuter = AlfrescoTransactionSupport.getTransactionId();
        
        // start a propagating txn
        UserTransaction txnInner = transactionService.getUserTransaction();
        txnInner.begin();
        String txnIdInner = AlfrescoTransactionSupport.getTransactionId();
        
        // the txn IDs should be the same
        assertEquals("Txn ID not propagated", txnIdOuter, txnIdInner);
        
        // rollback the inner
        txnInner.rollback();
        
        // check both transactions' status
        assertEquals("Inner txn not marked rolled back", Status.STATUS_MARKED_ROLLBACK, txnInner.getStatus());
        assertEquals("Outer txn not marked for rolled back", Status.STATUS_MARKED_ROLLBACK, txnInner.getStatus());
        
        try
        {
            txnOuter.commit();
            fail("Outer txn not marked for rollback");
        }
        catch (RollbackException e)
        {
            // expected
        }
    }
    
    public void testNonPropagatingTxn() throws Exception
    {
        // start a transaction
        UserTransaction txnOuter = transactionService.getUserTransaction();
        txnOuter.begin();
        String txnIdOuter = AlfrescoTransactionSupport.getTransactionId();
        
        // start a propagating txn
        UserTransaction txnInner = transactionService.getNonPropagatingUserTransaction();
        txnInner.begin();
        String txnIdInner = AlfrescoTransactionSupport.getTransactionId();
        
        // the txn IDs should be different
        assertNotSame("Txn ID not propagated", txnIdOuter, txnIdInner);
        
        // rollback the inner
        txnInner.rollback();

        // outer should commit without problems
        txnOuter.commit();
    }
}
