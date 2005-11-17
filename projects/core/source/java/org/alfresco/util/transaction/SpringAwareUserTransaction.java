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

import java.lang.reflect.Method;

import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.Status;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.transaction.NoTransactionException;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.transaction.interceptor.TransactionAttribute;
import org.springframework.transaction.interceptor.TransactionAttributeSource;

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
public class SpringAwareUserTransaction
        extends TransactionAspectSupport
        implements UserTransaction, TransactionAttributeSource, TransactionAttribute
{
    /*
     * There is some extra work in here to perform safety checks against the thread ID.
     * This is because this class doesn't operate in an environment that guarantees that the
     * thread coming into the begin() method is the same as the thread forcing commit() or
     * rollback().
     */
    
    private static final long serialVersionUID = 3762538897183224373L;

    private static final String NAME = "UserTransaction";
    
    private static final Log logger = LogFactory.getLog(SpringAwareUserTransaction.class);

    private boolean readOnly;
    private int isolationLevel;
    private int propagationBehaviour;
    private int timeout;
    
    /** Stores the user transaction current status as affected by explicit operations */
    private int internalStatus = Status.STATUS_NO_TRANSACTION;
    /** the transaction information used to check for mismatched begin/end */
    private TransactionInfo internalTxnInfo;
    /** keep the thread that the transaction was started on to perform thread safety checks */
    private long threadId = Long.MIN_VALUE;
    
    /**
     * Creates a user transaction that defaults to {@link TransactionDefinition#PROPAGATION_REQUIRED}.
     * 
     * @param transactionManager the transaction manager to use
     * @param readOnly true to force a read-only transaction
     * @param isolationLevel one of the
     *      {@link TransactionDefinition#ISOLATION_DEFAULT TransactionDefinition.ISOLATION_XXX}
     *      constants
     * @param propagationBehaviour one of the
     *      {@link TransactionDefinition#PROPAGATION_MANDATORY TransactionDefinition.PROPAGATION__XXX}
     *      constants
     * @param timeout the transaction timeout in seconds.
     * 
     * @see TransactionDefinition#getTimeout()
     */
    public SpringAwareUserTransaction(
            PlatformTransactionManager transactionManager,
            boolean readOnly,
            int isolationLevel,
            int propagationBehaviour,
            int timeout)
    {
        super();
        setTransactionManager(transactionManager);
        setTransactionAttributeSource(this);
        this.readOnly = readOnly;
        this.isolationLevel = isolationLevel;
        this.propagationBehaviour = propagationBehaviour;
        this.timeout = timeout;
    }
    
    /**
     * This class carries all the information required to fullfil requests about the transaction
     * attributes.  It acts as a source of the transaction attributes.
     * 
     * @return Return <code>this</code> instance
     */
    public TransactionAttribute getTransactionAttribute(Method method, Class targetClass)
    {
        return this;
    }
    
    /**
     * The {@link UserTransaction } must rollback regardless of the error.  The
     * {@link #rollback() rollback} behaviour is implemented by simulating a caught
     * exception.  As this method will always return <code>true</code>, the rollback
     * behaviour will be to rollback the transaction or mark it for rollback.
     * 
     * @return Returns true always
     */
    public boolean rollbackOn(Throwable ex)
    {
        return true;
    }

    /**
     * @see #NAME
     */
    public String getName()
    {
        return NAME;
    }

    public boolean isReadOnly()
    {
        return readOnly;
    }

    public int getIsolationLevel()
    {
        return isolationLevel;
    }

    public int getPropagationBehavior()
    {
        return propagationBehaviour;
    }

    public int getTimeout()
    {
        return timeout;
    }

    /**
     * Implementation required for {@link UserTransaction}.
     */
    public void setTransactionTimeout(int timeout) throws SystemException
    {
        if (internalStatus != Status.STATUS_NO_TRANSACTION)
        {
            throw new RuntimeException("Can only set the timeout before begin");
        }
        this.timeout = timeout;
    }

    /**
     * Gets the current transaction info, or null if none exists.
     * <p>
     * A check is done to ensure that the transaction info on the stack is exactly
     * the same instance used when this transaction was started.
     * The internal status is also checked against the transaction info.
     * These checks ensure that the transaction demarcation is done correctly and that
     * thread safety is adhered to.
     * 
     * @return Returns the current transaction
     */
    private TransactionInfo getTransactionInfo()
    {
        // a few quick self-checks
        if (threadId < 0 && internalStatus != Status.STATUS_NO_TRANSACTION)
        {
            throw new RuntimeException("Transaction has been started but there is no thread ID");
        }
        else if (threadId >= 0 && internalStatus == Status.STATUS_NO_TRANSACTION)
        {
            throw new RuntimeException("Transaction has not been started but a thread ID has been recorded");
        }
        
        TransactionInfo txnInfo = null;
        try
        {
            txnInfo = TransactionAspectSupport.currentTransactionInfo();
            // we are in a transaction
        }
        catch (NoTransactionException e)
        {
            // no transaction
        }
        // perform checks for active transactions
        if (internalStatus == Status.STATUS_ACTIVE)
        {
            if (Thread.currentThread().getId() != threadId)
            {
                // the internally stored transaction info (retrieved in begin()) should match the info
                // on the thread
                throw new RuntimeException("UserTransaction may not be accessed by multiple threads");
            }
            else if (txnInfo == null)
            {
                // internally we recorded a transaction starting, but there is nothing on the thread
                throw new RuntimeException("Transaction boundaries have been made to overlap in the stack");
            }
            else if (txnInfo != internalTxnInfo)
            {
                // the transaction info on the stack isn't the one we started with
                throw new RuntimeException("UserTransaction begin/commit mismatch");
            }
        }
        return txnInfo;
    }

    /**
     * This status is a combination of the internal status, as recorded during explicit operations,
     * and the status provided by the Spring support.
     * 
     * @see Status
     */
    public synchronized int getStatus() throws SystemException
    {
        TransactionInfo txnInfo = getTransactionInfo();
        
        // if the txn info is null, then we are outside a transaction
        if (txnInfo == null)
        {
            return internalStatus;      // this is checked in getTransactionInfo
        }

        // normally the internal status is correct, but we only need to double check
        // for the case where the transaction was marked for rollback, or rolledback
        // in a deeper transaction
        TransactionStatus txnStatus = txnInfo.getTransactionStatus();
        if (internalStatus == Status.STATUS_ROLLEDBACK)
        {
            // explicitly rolled back at some point
            return internalStatus;
        }
        else if (txnStatus.isRollbackOnly())
        {
            // marked for rollback at some point in the stack
            return Status.STATUS_MARKED_ROLLBACK;
        }
        else
        {
            // just rely on the internal status
            return internalStatus;
        }
    }

    public synchronized void setRollbackOnly() throws IllegalStateException, SystemException
    {
        // just a check
        TransactionInfo txnInfo = getTransactionInfo();

        int status = getStatus();
        // check the status
        if (status == Status.STATUS_MARKED_ROLLBACK)
        {
            // this is acceptable
        }
        else if (status == Status.STATUS_NO_TRANSACTION)
        {
            throw new IllegalStateException("The transaction has not been started yet");
        }
        else if (status == Status.STATUS_ROLLING_BACK || status == Status.STATUS_ROLLEDBACK)
        {
            throw new IllegalStateException("The transaction has already been rolled back");
        }
        else if (status == Status.STATUS_COMMITTING || status == Status.STATUS_COMMITTED)
        {
            throw new IllegalStateException("The transaction has already been committed");
        }
        else if (status != Status.STATUS_ACTIVE)
        {
            throw new IllegalStateException("The transaction is not active: " + status);
        }

        // mark for rollback
        txnInfo.getTransactionStatus().setRollbackOnly();
        // make sure that we record the fact that we have been marked for rollback
        internalStatus = Status.STATUS_MARKED_ROLLBACK;
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
        // make sure that the status and info align - the result may or may not be null
        TransactionInfo txnInfo = getTransactionInfo();
        if (internalStatus != Status.STATUS_NO_TRANSACTION)
        {
            throw new NotSupportedException("The UserTransaction may not be reused");
        }
        
        // begin a transaction
        internalTxnInfo = createTransactionIfNecessary(null, null);  // super class will just pass nulls back to us
        internalStatus = Status.STATUS_ACTIVE;
        threadId = Thread.currentThread().getId();
        
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
        // perform checks
        TransactionInfo txnInfo = getTransactionInfo();

        try
        {
            int status = getStatus();
            // check the status
            if (status == Status.STATUS_NO_TRANSACTION)
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
            else if (status != Status.STATUS_ACTIVE || txnInfo == null)
            {
                throw new IllegalStateException("Can only commit after a begin");
            }
            
            // the status seems correct - we can try a commit
            doCommitTransactionAfterReturning(txnInfo);
            
            // regardless of whether the transaction was finally committed or not, the status
            // as far as UserTransaction is concerned should be 'committed'
            
            // keep track that this UserTransaction was explicitly committed
            internalStatus = Status.STATUS_COMMITTED;
            
            // done
            if (logger.isDebugEnabled())
            {
                logger.debug("Committed user transaction: " + this);
            }
        }
        finally
        {
            // make sure that we clean up the stack
            doFinally(txnInfo);
        }
    }

    public synchronized void rollback()
            throws IllegalStateException, SecurityException, SystemException
    {
        // perform checks
        TransactionInfo txnInfo = getTransactionInfo();
        if (txnInfo == null)
        {
            throw new IllegalStateException("Can only rollback after a begin");
        }
        
        try
        {
            int status = getStatus();
            // check the status
            if (status == Status.STATUS_ROLLING_BACK || status == Status.STATUS_ROLLEDBACK)
            {
                throw new IllegalStateException("The transaction has already been rolled back");
            }
            else if (status == Status.STATUS_COMMITTING || status == Status.STATUS_COMMITTED)
            {
                throw new IllegalStateException("The transaction has already been committed");
            }
    
            // force a rollback by generating an exception that will trigger a rollback
            doCloseTransactionAfterThrowing(txnInfo, new Exception());
    
            // the internal status notes that we were specifically rolled back 
            internalStatus = Status.STATUS_ROLLEDBACK;
            
            // done
            if (logger.isDebugEnabled())
            {
                logger.debug("Rolled back user transaction: " + this);
            }
        }
        finally
        {
            // make sure that we clean up the stack
            doFinally(txnInfo);
        }
    }
}
