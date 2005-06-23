/*
 * Copyright (C) 2005 Alfresco, Inc.
 *
 * Licensed under the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version
 * 2.1 of the License, or (at your option) any later version.
 * You may obtain a copy of the License at
 *
 *     http://www.gnu.org/licenses/lgpl.txt
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
public class SpringAwareUserTransaction implements UserTransaction
{
    /*
     * The transaction status is stored in a threadlocal for two reasons:
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
    /**
     * Stores the transaction status.  This is thread local in order to detect illegal
     * use of this instance by multiple threads.
     */
    private ThreadLocal<TransactionStatus> threadLocalTxnStatus;
    /** Stores the user transaction current status */
    private int status = Status.STATUS_NO_TRANSACTION;
    
    /**
     * @param transactionManager the transaction manager to use
     */
    public SpringAwareUserTransaction(PlatformTransactionManager transactionManager)
    {
        this.transactionManager = transactionManager;
        transactionDef = new DefaultTransactionDefinition();
        transactionDef.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
    }

    public synchronized int getStatus() throws SystemException
    {
        return status;
    }

    public void setTransactionTimeout(int timeout) throws SystemException
    {
        if (threadLocalTxnStatus != null)
        {
            throw new RuntimeException("Can only set the timeout before begin");
        }
        transactionDef.setTimeout(timeout);
    }

    public synchronized void setRollbackOnly() throws IllegalStateException, SystemException
    {
        if (threadLocalTxnStatus == null)
        {
            throw new IllegalStateException("Can only force rollback after a begin");
        }
        // check that this instance has not been used by another thread
        TransactionStatus txnStatus = threadLocalTxnStatus.get();
        if (txnStatus == null)
        {
            // no need to clean up the transaction - this thread wasn't the one that
            // started it
            throw new RuntimeException("UserTransaction may not be accessed by multiple threads");
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
        if (threadLocalTxnStatus != null)
        {
            throw new NotSupportedException("The UserTransaction may not be reused");
        }
        
        // begin a transaction
        TransactionStatus txnStatus = transactionManager.getTransaction(transactionDef);
        // store the transaction status - we have successfully started/entered a transaction
        threadLocalTxnStatus = new ThreadLocal<TransactionStatus>();
        threadLocalTxnStatus.set(txnStatus);
        status = Status.STATUS_ACTIVE;
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
        if (threadLocalTxnStatus == null)
        {
            throw new IllegalStateException("Can only call commit after a begin");
        }
        // check that this instance has not been used by another thread
        TransactionStatus txnStatus = threadLocalTxnStatus.get();
        if (txnStatus == null)
        {
            // no need to clean up the transaction - this thread wasn't the one that
            // started it
            throw new RuntimeException("UserTransaction may not be accessed by multiple threads");
        }
        
        // check the status
        if (status == Status.STATUS_ROLLING_BACK || status == Status.STATUS_ROLLEDBACK)
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
            transactionManager.commit(txnStatus);
            status = Status.STATUS_COMMITTED;
        }
        catch (RuntimeException e)
        {
            // the transaction will be rolled back automatically
            status = Status.STATUS_ROLLEDBACK;
            throw e;
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
        if (threadLocalTxnStatus == null)
        {
            throw new IllegalStateException("Can only call rollback after a begin");
        }
        // check that this instance has not been used by another thread
        TransactionStatus txnStatus = threadLocalTxnStatus.get();
        if (txnStatus == null)
        {
            // no need to clean up the transaction - this thread wasn't the one that
            // started it
            throw new RuntimeException("UserTransaction may not be accessed by multiple threads");
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
        status = Status.STATUS_ROLLEDBACK;
        
        // done
        if (logger.isDebugEnabled())
        {
            logger.debug("Rolled back user transaction: " + this);
        }
    }
}
