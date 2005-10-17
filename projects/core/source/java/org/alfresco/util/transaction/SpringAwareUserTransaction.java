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

import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.Status;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;

/**
 * A <code>UserTransaction</code> that will allow the thread using it to participate
 * in transactions that are normally only begun and committed by the <b>SpringFramework</b>
 * transaction aware components.
 * <p>
 * This class is thread-safe in that it will detect multithreaded access and throw
 * exceptions.  Therefore </b>do not use on multiple threads</b>.  Instances should be
 * used only for the duration of the required user transaction and then discarded.
 * Any attempt to reuse an instance will result in failure.
 * <p>
 * Nested user transaction are allowed.
 * 
 * @see org.springframework.transaction.PlatformTransactionManager
 * @see org.springframework.transaction.support.DefaultTransactionDefinition
 * 
 * @author Derek Hulley
 */
public class SpringAwareUserTransaction extends TransactionSynchronizationAdapter implements UserTransaction
{
    /*
     * The transaction status is stored internally and the thread ID is recorded:
     * 
     * 1. We can detect whether a transaction has been started or not, whereas
     *    transactionManager.getTransaction will return a new transaction if
     *    one does not yet exist.
     * 2. We can ensure that begin() doesn't get called by different threads
     * 
     * The methods are synchronized to ensure that we can detect illegal use
     * by multiple threads.  These scenarios are all tested in the corresponding
     * unit tests.
     */
    
    private static final long serialVersionUID = 3762538897183224373L;
    
    private static final Log logger = LogFactory.getLog(SpringAwareUserTransaction.class);

    private PlatformTransactionManager transactionManager;
    private DefaultTransactionDefinition transactionDef;
    /** the id of the thread that started this transaction */
    private long txnThreadId;
    /** Stores the transaction status. */
    private TransactionStatus txnStatus;
    /** Stores the user transaction current status */
    private int status = Status.STATUS_NO_TRANSACTION;
    
    /**
     * Creates a user transaction that defaults to {@link TransactionDefinition#PROPAGATION_REQUIRED}.
     * 
     * @param transactionManager the transaction manager to use
     * 
     * @see #setPropagationBehviour(int)
     */
    public SpringAwareUserTransaction(PlatformTransactionManager transactionManager)
    {
        this.transactionManager = transactionManager;
        transactionDef = new DefaultTransactionDefinition();
        transactionDef.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        
        // set the creation thread ID that.  All operations must be performed with this thread.
        txnThreadId = Thread.currentThread().getId();
    }
    
    /**
     * Check that this instance has not been used by another thread.
     */
    private void checkThreadId()
    {
        if (Thread.currentThread().getId() != txnThreadId)
        {
            throw new RuntimeException("UserTransaction may not be accessed by multiple threads");
        }
    }

    /**
     * Set the propagation mode for when the transaction is started.  The constants are
     * defined by the {@link TransactionDefinition SpringFramework}. 
     * 
     * @param propagationBehaviour the type of transaction propagation required
     * 
     * @see TransactionDefinition#PROPAGATION_REQUIRED
     */
    public void setPropagationBehviour(int propagationBehaviour)
    {
        checkThreadId();
        
        transactionDef.setPropagationBehavior(propagationBehaviour);
    }

    @Override
    public void afterCompletion(int status)
    {
        checkThreadId();
        
        switch (status)
        {
            case TransactionSynchronization.STATUS_ROLLED_BACK:
                status = Status.STATUS_ROLLEDBACK;
                break;
            case TransactionSynchronization.STATUS_COMMITTED:
                status = Status.STATUS_COMMITTED;
                break;
            default:
                // no idea what has happened to the txn, but it has finished
                status = Status.STATUS_NO_TRANSACTION;
        }
    }

    public synchronized int getStatus() throws SystemException
    {
        checkThreadId();
        
        // ensure that our internal status is in synch with the Spring txn
        if (txnStatus != null && txnStatus.isRollbackOnly())
        {
            status = Status.STATUS_MARKED_ROLLBACK;
        }
        return status;
    }

    public void setTransactionTimeout(int timeout) throws SystemException
    {
        checkThreadId();
        
        if (txnStatus != null)
        {
            throw new RuntimeException("Can only set the timeout before begin");
        }
        transactionDef.setTimeout(timeout);
    }

    public synchronized void setRollbackOnly() throws IllegalStateException, SystemException
    {
        checkThreadId();
        
        if (txnStatus == null)
        {
            throw new IllegalStateException("Can only force rollback after a begin");
        }
        txnStatus.setRollbackOnly();
        status = Status.STATUS_MARKED_ROLLBACK;
        // done
        if (logger.isDebugEnabled())
        {
            logger.debug("Set transaction status to rollback only: " + this);
        }
    }
    
    /**
     * @throws NotSupportedException if an attempt is made to reuse this instance
     */
    public synchronized void begin() throws NotSupportedException, SystemException
    {
        checkThreadId();
        
        if (txnStatus != null)
        {
            throw new NotSupportedException("The UserTransaction may not be reused");
        }
        
        // begin a transaction
        txnStatus = transactionManager.getTransaction(transactionDef);
        status = Status.STATUS_ACTIVE;
        
        // register this UserTransaction as a synchronization so that we get the
        // callbacks required to know when the transaction is rolledback or committed, etc
        TransactionSynchronizationManager.registerSynchronization(this);
        
        // done
        if (logger.isDebugEnabled())
        {
            logger.debug("Began user transaction: " + this);
        }
    }
    
    /**
     * @throws IllegalStateException if a transaction was not started
     */
    public synchronized void commit()
            throws RollbackException, HeuristicMixedException, HeuristicRollbackException,
            SecurityException, IllegalStateException, SystemException
    {
        checkThreadId();
        
        // check the status
        if (txnStatus == null || status == Status.STATUS_NO_TRANSACTION)
        {
            throw new IllegalStateException("The transaction has not yet begun");
        }
        else if (status == Status.STATUS_ROLLING_BACK || status == Status.STATUS_ROLLEDBACK)
        {
            throw new RollbackException("The transaction has already been rolled back");
        }
        else if (status == Status.STATUS_MARKED_ROLLBACK)
        {
            throw new RollbackException("The transaction has already been marked for rollback");
        }
        else if (status == Status.STATUS_COMMITTING || status == Status.STATUS_COMMITTED)
        {
            throw new IllegalStateException("The transaction has already been committed");
        }
        
        // we definitely began a transaction on this thread
        try
        {
            // is this going to commit or rollback?
            boolean willCommit = true;
            if (txnStatus.isRollbackOnly())
            {
                willCommit = false;
            }
            // will commit or rollback
            transactionManager.commit(txnStatus);
            // dereference to clean resources
            txnStatus = null;
            transactionManager = null;
            transactionDef = null;
            
            if (willCommit)
            {
                status = Status.STATUS_COMMITTED;
            }
            else
            {
                status = Status.STATUS_ROLLEDBACK;
                throw new RollbackException("The transaction was rolled back");
            }
        }
        catch (RuntimeException e)
        {
            // the transaction will be rolled back automatically
            status = Status.STATUS_ROLLEDBACK;
            e.printStackTrace();
            throw new RollbackException("The transaction was not committed - " + e.getMessage());
        }
        
        // done
        if (logger.isDebugEnabled())
        {
            logger.debug("Committed user transaction: " + this);
        }
    }

    public synchronized void rollback()
            throws IllegalStateException, SecurityException, SystemException
    {
        checkThreadId();
        
        if (txnStatus == null)
        {
            throw new IllegalStateException("Can only call rollback after a begin");
        }
        
        // check the status
        if (status == Status.STATUS_ROLLING_BACK || status == Status.STATUS_ROLLEDBACK)
        {
            throw new IllegalStateException("The transaction has already been rolled back");
        }
        else if (status == Status.STATUS_COMMITTING || status == Status.STATUS_COMMITTED)
        {
            throw new IllegalStateException("The transaction has already been committed");
        }
        
        // we definitely began a transaction on this thread
        transactionManager.rollback(txnStatus);
        // dereference to clean resources
        txnStatus = null;
        transactionManager = null;
        transactionDef = null;
        
        status = Status.STATUS_ROLLEDBACK;
        
        // done
        if (logger.isDebugEnabled())
        {
            logger.debug("Rolled back user transaction: " + this);
        }
    }
}
