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
package org.alfresco.util.transaction;

import javax.transaction.RollbackException;
import javax.transaction.Status;
import javax.transaction.UserTransaction;

import junit.framework.TestCase;

import org.springframework.transaction.NoTransactionException;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
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
        txn = getTxn();
    }
    
    private UserTransaction getTxn()
    {
        return new SpringAwareUserTransaction(
                transactionManager,
                false,
                TransactionDefinition.ISOLATION_DEFAULT,
                TransactionDefinition.PROPAGATION_REQUIRED,
                TransactionDefinition.TIMEOUT_DEFAULT);
    }
    
    public void testSetUp() throws Exception
    {
        assertNotNull(transactionManager);
        assertNotNull(txn);
    }
    
    private void checkNoStatusOnThread()
    {
        try
        {
            TransactionAspectSupport.currentTransactionStatus();
            fail("Spring transaction info is present outside of transaction boundaries");
        }
        catch (NoTransactionException e)
        {
            // expected
        }
    }
    
    public void testNoTxnStatus() throws Exception
    {
        checkNoStatusOnThread();
        assertEquals("Transaction status is not correct",
                Status.STATUS_NO_TRANSACTION,
                txn.getStatus());
        assertEquals("Transaction manager not set up correctly",
                txn.getStatus(),
                transactionManager.getStatus());
    }

    public void testSimpleTxnWithCommit() throws Throwable
    {
        testNoTxnStatus();
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
            // unexpected exception - attempt a cleanup
            try
            {
                txn.rollback();
            }
            catch (Throwable ee)
            {
                e.printStackTrace();
            }
            throw e;
        }
        checkNoStatusOnThread();
    }
    
    public void testSimpleTxnWithRollback() throws Exception
    {
        testNoTxnStatus();
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
        checkNoStatusOnThread();
    }
    
    public void testNoBeginCommit() throws Exception
    {
        testNoTxnStatus();
        try
        {
            txn.commit();
            fail("Failed to detected no begin");
        }
        catch (IllegalStateException e)
        {
            // expected
        }
        checkNoStatusOnThread();
    }
    
    public void testPostRollbackCommitDetection() throws Exception
    {
        testNoTxnStatus();

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
        checkNoStatusOnThread();
    }
    
    public void testPostSetRollbackOnlyCommitDetection() throws Exception
    {
        testNoTxnStatus();

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
        checkNoStatusOnThread();
    }
    
    public void testMismatchedBeginCommit() throws Exception
    {
        UserTransaction txn1 = getTxn();
        UserTransaction txn2 = getTxn();

        testNoTxnStatus();

        txn1.begin();
        txn2.begin();
        
        txn2.commit();
        txn1.commit();
        
        checkNoStatusOnThread();
        
        txn1 = getTxn();
        txn2 = getTxn();
        
        txn1.begin();
        txn2.begin();
        
        try
        {
            txn1.commit();
            fail("Failure to detect mismatched transaction begin/commit");
        }
        catch (RuntimeException e)
        {
            // expected
        }
        txn2.commit();
        txn1.commit();

        checkNoStatusOnThread();
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
