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
package org.alfresco.repo.transaction;

import java.util.Stack;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.repo.integrity.IntegrityException;
import org.alfresco.repo.integrity.IntegrityService;
import org.alfresco.repo.rule.RuleExecution;
import org.alfresco.repo.search.IndexerException;
import org.alfresco.repo.search.impl.lucene.LuceneIndexerAndSearcherFactory;
import org.alfresco.repo.search.transaction.LuceneTransactionException;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.rule.RuleService;
import org.alfresco.util.GUID;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.orm.hibernate3.HibernateTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.support.DefaultTransactionStatus;

/**
 * This is simple Transaction Manager that adds support for various end-of-transaction
 * processing.  The intention is to keep this simple and avoid JTA overhead.
 * <p>
 * This does not have full recovery support at the moment but could have. After
 * the lucene indexer prepare we can only fail on IO errors.
 * <p>
 * TODO: We should track required deletions and the delta to commit and then we
 * can retry database commits that went through followed by lucene index commits
 * that failed. These will be serialised so could be retied on recovery and
 * block any further index action. Could persist a lock in a manager or to disk.
 * The lock manager could recover on start up etc. This will be required for
 * true JTA in any case.
 * 
 * @author andyh
 * 
 */
public class AlfrescoTransactionManager extends HibernateTransactionManager
{
    /** not the usual <b>logger</b> as it clashes with a protected member in base classes */
    private static Log txnIdLogger = LogFactory.getLog(AlfrescoTransactionManager.class.getName() + ".txnid");
    
    private LuceneIndexerAndSearcherFactory luceneIndexerAndSearcherFactory;
	private ServiceRegistry serviceRegistry;
	private RuleService ruleService;
    /** used to check changes made during a transaction */
    private IntegrityService integrityService;
    /** the stacked thread local transaction IDs.  This gets cleared when the transaction terminates */
    private static ThreadLocal<Stack<String>> threadLocalTransactionIds = new ThreadLocal<Stack<String>>();

    public AlfrescoTransactionManager()
    {
        super();
    }

    /**
     * Dependency injection of Lucene indexing component
     * 
     * @param luceneIndexerAndSearcherFactory
     */
    public void setLuceneIndexerAndSearchFactory(LuceneIndexerAndSearcherFactory luceneIndexerAndSearcherFactory)
    {
        this.luceneIndexerAndSearcherFactory = luceneIndexerAndSearcherFactory;
    }

    /**
     * Dependency injection of repository access service
     *  
     * @param serviceRegistry
     */
	public void setServiceRegistry(ServiceRegistry serviceRegistry) 
	{
		this.serviceRegistry = serviceRegistry;
	}

    /**
     * Optional dependency injection of integrity service.
     * 
     * @param integrityService the service to use for integrity checking or null
     *      to disable.
     */
    public void setIntegrityService(IntegrityService integrityService)
    {
        this.integrityService = integrityService;
    }

    /**
     * Retrieves the current transaction id
     * 
     * @return Returns the transaction ID assigned to the thread, or null if a transaction was
     *      not started, or if the transaction wasn't started by this transaction manager.
     */
    public static String getTransactionId()
    {
        Stack<String> txnIdStack = AlfrescoTransactionManager.threadLocalTransactionIds.get(); 
        if (txnIdStack == null || txnIdStack.empty())
        {
            return null;
        }
        else
        {
            return txnIdStack.peek();
        }
    }
    
    /**
     * Overridden to assign a transaction ID to the thread
     */
    @Override
    protected void doBegin(Object transaction, TransactionDefinition definition)
    {
        if (txnIdLogger.isDebugEnabled())
        {
            txnIdLogger.debug("Beginning transaction");
        }
        
        super.doBegin(transaction, definition);
        
        // push a thread txn id onto the stack
        Stack<String> txnIdStack = AlfrescoTransactionManager.threadLocalTransactionIds.get();
        if (txnIdStack == null)
        {
            // nothing has been set on the thread, yet
            txnIdStack = new Stack<String>();
            AlfrescoTransactionManager.threadLocalTransactionIds.set(txnIdStack);
        }
        txnIdStack.push(GUID.generate());
        if (txnIdLogger.isDebugEnabled())
        {
            txnIdLogger.debug("TransactionID stack pushed: " + txnIdStack.peek());
        }
    }

    protected void doCommit(DefaultTransactionStatus status) throws TransactionException
    {
        if (txnIdLogger.isDebugEnabled())
        {
            txnIdLogger.debug("Committing transaction");
        }
        
        // Call any pending rules
        if (this.ruleService == null)
        {
            this.ruleService = (RuleService)this.serviceRegistry.getService(ServiceRegistry.RULE_SERVICE);
        }
        if (this.ruleService != null)
        {
            ((RuleExecution)this.ruleService).executePendingRules();
        }
        
        // check integrity if the integrity service is available
        if (integrityService != null)
        {
            try
            {
                integrityService.checkIntegrity(getTransactionId());
            }
            catch (IntegrityException e)
            {
                // integrity failure - rollback
                doRollback(status);
                throw new AlfrescoTransactionException("Integrity violation prevented transaction commit", e);
            }
        }
        
        try
        {
            // TODO: The following call should mark for recovery - it does not
            luceneIndexerAndSearcherFactory.prepare();
        }
        catch (IndexerException e)
        {
            doRollback(status);
            throw new LuceneTransactionException("Lucene index transaction failed to prepare", e);
        }
        try
        {
            super.doCommit(status);
        }
        catch (TransactionException e)
        {
            luceneIndexerAndSearcherFactory.rollback();
            throw new LuceneTransactionException("Failed transaction manager commit", e);
        }

        // If the followiung commit fails it willclean up
        luceneIndexerAndSearcherFactory.commit();
    }

    protected void doRollback(DefaultTransactionStatus status) throws TransactionException
    {
        if (txnIdLogger.isDebugEnabled())
        {
            txnIdLogger.debug("Rolling back transaction");
        }
        
        try
        {
            super.doRollback(status);
        }
        finally
        {
            luceneIndexerAndSearcherFactory.rollback();
        }
    }

    /**
     * Overridden to remove the threadlocal transaction ID
     */
    @Override
    protected void doCleanupAfterCompletion(Object transaction)
    {
        super.doCleanupAfterCompletion(transaction);
        
        // pop a thread's txn id from the stack
        Stack<String> txnIdStack = AlfrescoTransactionManager.threadLocalTransactionIds.get(); 
        if (txnIdStack == null || txnIdStack.empty())
        {
            throw new AlfrescoRuntimeException(
                    "Transaction cleanup encountered empty or null TXNID stack: " + txnIdStack);
        }
        else
        {
            // pop one off
            String txnId = txnIdStack.pop();
            if (txnIdLogger.isDebugEnabled())
            {
                txnIdLogger.debug("TransactionID stack popped: " + txnId);
            }
        }
    }
}
