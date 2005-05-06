package org.alfresco.util.transaction;

import javax.transaction.RollbackException;
import javax.transaction.Status;
import javax.transaction.UserTransaction;

import junit.framework.TestCase;

import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.AbstractPlatformTransactionManager;
import org.springframework.transaction.support.DefaultTransactionStatus;

/**
 * @see org.alfresco.util.transaction.SpringAwareUserTransaction
 * 
 * @author Derek Hulley
 */
public class SpringAwareUserTransactionTest extends TestCase
{
    private DummyTransactionManager transactionManager;
    private UserTransaction txn;
    
    public SpringAwareUserTransactionTest()
    {
        super();
    }
    
    @Override
    protected void setUp() throws Exception
    {
        transactionManager = new DummyTransactionManager();
        txn = new SpringAwareUserTransaction(transactionManager);
    }
    
    public void testSetUp() throws Exception
    {
        assertNotNull(transactionManager);
        assertNotNull(txn);
    }
    
    public void testNoTxnStatus() throws Exception
    {
        assertEquals("Transaction status is not correct",
                Status.STATUS_NO_TRANSACTION,
                txn.getStatus());
        assertEquals("Transaction manager not set up correctly",
                txn.getStatus(),
                transactionManager.getStatus());
    }

    public void testSimpleTxnWithCommit() throws Exception
    {
        try
        {
            txn.begin();
            assertEquals("Transaction status is not correct",
                    Status.STATUS_ACTIVE,
                    txn.getStatus());
            assertEquals("Transaction manager not called correctly",
                    txn.getStatus(),
                    transactionManager.getStatus());

            txn.commit();
            assertEquals("Transaction status is not correct",
                    Status.STATUS_COMMITTED,
                    txn.getStatus());
            assertEquals("Transaction manager not called correctly",
                    txn.getStatus(),
                    transactionManager.getStatus());
        }
        catch (Throwable e)
        {
            txn.rollback();
        }
    }
    
    public void testSimpleTxnWithRollback() throws Exception
    {
        try
        {
            txn.begin();

            throw new Exception("Blah");
        }
        catch (Throwable e)
        {
            txn.rollback();
        }
        assertEquals("Transaction status is not correct",
                Status.STATUS_ROLLEDBACK,
                txn.getStatus());
        assertEquals("Transaction manager not called correctly",
                txn.getStatus(),
                transactionManager.getStatus());
    }
    
    public void testNoBeginCommit() throws Exception
    {
        try
        {
            txn.commit();
            fail("Failed to detected no begin");
        }
        catch (IllegalStateException e)
        {
            // expected
        }
    }
    
    public void testPostRollbackCommitDetection() throws Exception
    {
        txn.begin();
        txn.rollback();
        try
        {
            txn.commit();
            fail("Failed to detect rolled back txn");
        }
        catch (RollbackException e)
        {
            // expected
        }
    }
    
    public void testPostSetRollbackOnlyCommitDetection() throws Exception
    {
        txn.begin();
        txn.setRollbackOnly();
        try
        {
            txn.commit();
            fail("Failed to detect set rollback");
        }
        catch (RollbackException e)
        {
            // expected
        }
    }
    
    /**
     * Used to check that the transaction manager is being called correctly
     * 
     * @author Derek Hulley
     */
    private static class DummyTransactionManager extends AbstractPlatformTransactionManager
    {
        private int status = Status.STATUS_NO_TRANSACTION;
        private Object txn = new Object();
        
        /**
         * @return Returns one of the {@link Status Status.STATUS_XXX} constants
         */
        public int getStatus()
        {
            return status;
        }

        protected void doBegin(Object arg0, TransactionDefinition arg1)
        {
            status = Status.STATUS_ACTIVE;
        }

        protected void doCommit(DefaultTransactionStatus arg0)
        {
            status = Status.STATUS_COMMITTED;
        }

        protected Object doGetTransaction()
        {
            return txn;
        }

        protected void doRollback(DefaultTransactionStatus arg0)
        {
            status = Status.STATUS_ROLLEDBACK;
        }
    }
}
